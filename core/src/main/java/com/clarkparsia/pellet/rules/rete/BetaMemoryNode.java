// Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package com.clarkparsia.pellet.rules.rete;

import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;

/**
 */
public class BetaMemoryNode extends BetaNode
{
	private final BetaMemoryIndex _memory;

	private final AlphaNode _alpha;

	private final List<FilterCondition> _conditions;

	public BetaMemoryNode(final AlphaNode alpha, final List<FilterCondition> conditions)
	{
		if (conditions == null)
			throw new NullPointerException();
		this._alpha = alpha;
		this._conditions = conditions;
		this._memory = createIndex(conditions);
	}

	private static BetaMemoryIndex createIndex(final List<FilterCondition> conditions)
	{
		if (!conditions.isEmpty() && (conditions.get(0) instanceof JoinCondition))
			return BetaMemoryIndex.withJoin((JoinCondition) conditions.get(0));

		return BetaMemoryIndex.withoutJoin();
	}

	public AlphaNode getAlphaNode()
	{
		return _alpha;
	}

	public List<FilterCondition> getConditions()
	{
		return _conditions;
	}

	@Override
	public void activate(final WME wme)
	{
		if (_log.isLoggable(Level.FINE))
			_log.fine("Activate beta " + wme);

		final Iterator<Token> wmeTokens = _memory.getTokens(wme);

		while (wmeTokens.hasNext())
		{
			final Token token = wmeTokens.next();
			if (testConditions(wme, token, 0))
				activateChildren(wme, token);
		}
	}

	@Override
	public void activate(final Token token)
	{
		if (_log.isLoggable(Level.FINE))
			_log.fine("Activate beta " + token);

		_memory.add(token);

		final Iterator<WME> matches = _memory.getWMEs(token, _alpha);
		while (matches.hasNext())
		{
			final WME wme = matches.next();
			if (testConditions(wme, token, _memory.isJoined() ? 1 : 0))
				activateChildren(wme, token);
		}
	}

	private boolean testConditions(final WME wme, final Token token, final int start)
	{
		for (int i = start, n = _conditions.size(); i < n; i++)
		{
			final FilterCondition condition = _conditions.get(i);
			if (!condition.test(wme, token))
				return false;
		}
		return true;
	}

	@Override
	public void reset()
	{
		super.reset();

		_memory.clear();
	}

	@Override
	public void restore(final int branch)
	{
		super.restore(branch);
		_memory.restore(branch);
	}

	@Override
	public void print(String indent)
	{
		System.out.print(indent);
		System.out.println(_alpha);
		indent += "  ";
		System.out.print(indent);
		System.out.print(this);
		System.out.print(" ");
		System.out.println(_memory);
		for (final BetaNode node : getBetas())
			node.print(indent);
	}

	@Override
	public String toString()
	{
		return isTop() ? "Top" : "Beta" + _conditions;
	}
}
