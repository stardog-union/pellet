// Copyright (c) 2006 - 2010, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package com.clarkparsia.pellet.rules.rete;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.mindswap.pellet.Node;
import org.mindswap.pellet.utils.iterator.IteratorUtils;

/**
 * @author Evren Sirin
 */
public abstract class BetaMemoryIndex
{
	public abstract void add(Token token);

	public abstract Iterator<Token> getTokens(WME wme);

	public abstract Iterator<WME> getWMEs(Token token, AlphaNode alpha);

	public abstract void restore(int branch);

	public abstract void clear();

	public abstract boolean isJoined();

	public static BetaMemoryIndex withoutJoin()
	{
		return new Unindexed();
	}

	public static BetaMemoryIndex withJoin(final JoinCondition condition)
	{
		return condition == null ? new Unindexed() : new JoinIndexed(condition);
	}

	private static class Unindexed extends BetaMemoryIndex
	{
		private Token[] index = new Token[10];
		private int size = 0;

		@Override
		public boolean isJoined()
		{
			return false;
		}

		@Override
		public void add(final Token token)
		{
			if (size == index.length)
			{
				final int newSize = (size * 3) / 2 + 1;
				index = Arrays.copyOf(index, newSize);
			}

			index[size++] = token;
		}

		@Override
		public Iterator<Token> getTokens(final WME wme)
		{
			return IteratorUtils.iterator(size, index);
		}

		@Override
		public Iterator<WME> getWMEs(final Token token, final AlphaNode alpha)
		{
			return alpha.getMatches();
		}

		@Override
		public void restore(final int branch)
		{
			int i = 0;
			int removed = 0;
			for (; i < size; i++)
			{
				final Token token = index[i];
				if (token.dependsOn(branch))
					removed++;
				else
					if (removed > 0)
					{
						System.arraycopy(index, i, index, i - removed, size - i);
						size -= removed;
					}
			}

			if (removed > 0)
			{
				System.arraycopy(index, i, index, i - removed, size - i);
				size -= removed;
			}
		}

		@Override
		public void clear()
		{
			size = 0;
		}

		@Override
		public String toString()
		{
			if (size == 0)
				return "[]";
			final StringBuilder sb = new StringBuilder("[");
			for (int i = 0; i < size; i++)
			{
				sb.append(index[i]);
				sb.append(", ");
			}
			final int length = sb.length();
			sb.setCharAt(length - 2, ']');
			sb.setLength(length - 1);
			return sb.toString();
		}

	}

	@SuppressWarnings("unused")
	private static class JoinUnindexed extends BetaMemoryIndex
	{
		private final List<Token> _memory = new ArrayList<>();

		private final JoinCondition _joinCondition;

		private JoinUnindexed(final JoinCondition joinCondition)
		{
			this._joinCondition = joinCondition;
		}

		@Override
		public boolean isJoined()
		{
			return true;
		}

		@Override
		public void add(final Token token)
		{
			_memory.add(token);
		}

		@Override
		public Iterator<Token> getTokens(final WME wme)
		{
			return new ListIterator<>(_memory);
		}

		@Override
		public Iterator<WME> getWMEs(final Token token, final AlphaNode alpha)
		{
			final Node tokenArg = _joinCondition.getToken().getNode(null, token);
			return alpha.getMatches(_joinCondition.getWME().getIndexArg(), tokenArg);
		}

		@Override
		public void restore(final int branch)
		{
			for (final Iterator<Token> i = _memory.iterator(); i.hasNext();)
			{
				final Token token = i.next();
				if (token.dependsOn(branch))
					i.remove();
			}
		}

		@Override
		public void clear()
		{
			_memory.clear();
		}

		@Override
		public String toString()
		{
			return _memory.toString();
		}

	}

	private static class JoinIndexed extends BetaMemoryIndex
	{
		private final Map<Node, List<Token>> _index = new HashMap<>();

		private final JoinCondition _joinCondition;

		private JoinIndexed(final JoinCondition joinCondition)
		{
			this._joinCondition = joinCondition;
		}

		@Override
		public boolean isJoined()
		{
			return true;
		}

		@Override
		public void add(final Token token)
		{
			final Node tokenArg = _joinCondition.getToken().getNode(null, token);

			List<Token> tokens = _index.get(tokenArg);
			if (tokens == null)
			{
				tokens = new ArrayList<>();
				_index.put(tokenArg, tokens);
			}
			tokens.add(token);
		}

		@Override
		public Iterator<Token> getTokens(final WME wme)
		{
			final Node wmeArg = _joinCondition.getWME().getNode(wme, null);

			final List<Token> tokens = _index.get(wmeArg);

			return tokens == null ? IteratorUtils.<Token> emptyIterator() : new ListIterator<>(tokens);
		}

		@Override
		public Iterator<WME> getWMEs(final Token token, final AlphaNode alpha)
		{
			final Node tokenArg = _joinCondition.getToken().getNode(null, token);
			return alpha.getMatches(_joinCondition.getWME().getIndexArg(), tokenArg);
		}

		@Override
		public void restore(final int branch)
		{
			for (final Iterator<List<Token>> i = _index.values().iterator(); i.hasNext();)
			{
				final List<Token> tokens = i.next();
				for (final Iterator<Token> j = tokens.iterator(); j.hasNext();)
				{
					final Token token = j.next();
					if (token.dependsOn(branch))
						j.remove();
				}
				if (tokens.isEmpty())
					i.remove();
			}
		}

		@Override
		public void clear()
		{
			_index.clear();
		}

		@Override
		public String toString()
		{
			return _index.values().toString();
		}
	}

	private static class ListIterator<T> implements Iterator<T>
	{
		private final List<T> _list;
		private final int _size;
		private int _index = 0;

		private ListIterator(final List<T> list)
		{
			this._list = list;
			this._size = list.size();
		}

		@Override
		public boolean hasNext()
		{
			return _index < _size;
		}

		@Override
		public T next()
		{
			return _list.get(_index++);
		}

		@Override
		public void remove()
		{
			throw new UnsupportedOperationException();
		}
	}
}
