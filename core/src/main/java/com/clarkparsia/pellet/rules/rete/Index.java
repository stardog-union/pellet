// Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package com.clarkparsia.pellet.rules.rete;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.mindswap.pellet.utils.Pair;

/**
 * <p>
 * Title: Index
 * </p>
 * <p>
 * Description: An indexing structure that associates an object with a list of objects as the key.
 * </p>
 * <p>
 * Copyright: Copyright (c) 2007
 * </p>
 * <p>
 * Company: Clark & Parsia, LLC. <http://www.clarkparsia.com>
 * </p>
 *
 * @author Ron Alford
 */
public class Index<S, T> implements Iterable<T>
{

	private static class IndexNode<I, J>
	{
		private final Map<I, IndexNode<I, J>> children;
		private final Collection<J> leaves;

		public IndexNode()
		{
			children = new HashMap<>();
			leaves = new HashSet<>();
		}

		public boolean add(final List<I> key, final J obj)
		{
			if (key.size() == 0)
				return leaves.add(obj);
			else
			{
				final I pivot = key.get(0);
				IndexNode<I, J> child = children.get(pivot);
				if (child == null)
				{
					child = new IndexNode<>();
					children.put(pivot, child);
				}
				return child.add(key.subList(1, key.size()), obj);
			}
		}

		private Collection<J> getAllLeaves()
		{
			final Collection<J> results = new ArrayList<>();
			getAllLeaves(results);
			return results;
		}

		private void getAllLeaves(final Collection<J> result)
		{
			result.addAll(leaves);
			for (final IndexNode<I, J> child : children.values())
				child.getAllLeaves(result);
		}

		public <V> void join(final IndexNode<I, V> node, final int shared, final Collection<Pair<J, V>> results)
		{
			if (shared > 0)
			{
				for (final Map.Entry<I, IndexNode<I, J>> entry : children.entrySet())
					if (entry.getKey() != null)
					{
						final IndexNode<I, V> nodeChild = node.children.get(entry.getKey());
						if (nodeChild != null)
							entry.getValue().join(nodeChild, shared - 1, results);

						final IndexNode<I, V> nullNodeChild = node.children.get(null);
						if (nullNodeChild != null)
							entry.getValue().join(nullNodeChild, shared - 1, results);
					}
					else
						for (final IndexNode<I, V> child : node.children.values())
							entry.getValue().join(child, shared - 1, results);
			}
			else
				for (final J leaf : getAllLeaves())
					for (final V joinLeaf : node.getAllLeaves())
						results.add(new Pair<>(leaf, joinLeaf));
		}

		public void match(final List<I> key, final Collection<J> results)
		{
			if (key.size() == 0)
				results.addAll(leaves);
			else
			{
				final List<I> subKey = key.subList(1, key.size());

				final IndexNode<I, J> pivotChild = children.get(key.get(0));
				if (pivotChild != null)
					pivotChild.match(subKey, results);

				final IndexNode<I, J> nullChild = children.get(null);
				if (nullChild != null)
					nullChild.match(subKey, results);
			}
		}

		public void print(final StringBuilder buffer, String prefix)
		{
			if (leaves.size() > 0)
				buffer.append(leaves.toString());
			buffer.append(":\n");
			prefix = prefix + " ";
			for (final Map.Entry<I, IndexNode<I, J>> entry : children.entrySet())
			{
				buffer.append(prefix).append(entry.getKey()).append(" ");
				entry.getValue().print(buffer, prefix);
			}
		}

		public boolean remove(final List<I> key, final J obj)
		{
			if (key.size() == 0)
				return leaves.remove(obj);
			else
			{
				final I pivot = key.get(0);
				final IndexNode<I, J> child = children.get(pivot);
				if (child == null)
					return false;
				final boolean result = child.remove(key.subList(1, key.size()), obj);
				if (result && child.leaves.isEmpty())
					children.remove(child);
				return result;
			}
		}

		@Override
		public String toString()
		{
			final StringBuilder result = new StringBuilder("Index Node ");
			print(result, "");
			return result.toString();
		}

	}

	private int size;
	private IndexNode<S, T> root;

	public Index()
	{
		clear();
	}

	/**
	 * Add an object to the _index.
	 * 
	 * @param key null key positions are counted as wild-cards.
	 * @param obj
	 * @return
	 */
	public boolean add(final List<S> key, final T obj)
	{
		if (root.add(key, obj))
		{
			size++;
			return true;
		}
		return false;
	}

	/**
	 * Remove all _nodes from the _index.
	 */
	public void clear()
	{
		root = new IndexNode<>();
		size = 0;
	}

	@Override
	public Iterator<T> iterator()
	{
		return root.getAllLeaves().iterator();
	}

	/**
	 * Return a join of this _index to the given _index, joining on the first <code>openllet.shared.hash</code> variables.
	 */
	public <U> Collection<Pair<T, U>> join(final Index<S, U> index, final int shared)
	{
		final Collection<Pair<T, U>> results = new ArrayList<>();
		root.join(index.root, shared, results);
		return results;
	}

	/**
	 * Return all matches to the key. There may be no null values in the key. The returned objects will be stored under keys whose elements are either equal to
	 * the corresponding element of the given key or are null.
	 */
	public Collection<T> match(final List<S> key)
	{
		final Collection<T> results = new ArrayList<>();
		root.match(key, results);
		return results;
	}

	/**
	 * Remove the element of the _index stored under the key 'key'. Return true if the element exists and was removed. Otherwise, remove false.
	 */
	public boolean remove(final List<S> key, final T obj)
	{
		if (root.remove(key, obj))
		{
			size--;
			return true;
		}
		return false;
	}

	/**
	 * Return the number of objects added to the _index.
	 */
	public int size()
	{
		return size;
	}

	@Override
	public String toString()
	{
		final StringBuilder buffer = new StringBuilder("Index ");
		root.print(buffer, "");

		return buffer.toString();
	}

}
