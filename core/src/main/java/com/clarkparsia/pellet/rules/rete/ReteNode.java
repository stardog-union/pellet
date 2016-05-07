// Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package com.clarkparsia.pellet.rules.rete;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.logging.Logger;

/**
 * @author Evren Sirin
 */
public abstract class ReteNode
{
	public final static Logger log = Logger.getLogger(ReteNode.class.getName());

	private final List<BetaNode> children = new ArrayList<>();

	private boolean marked = false;

	public ReteNode()
	{
	}

	/**
	 * Add a directly dependent _node.
	 */
	public void addChild(final BetaNode beta)
	{
		children.add(beta);
	}

	/**
	 * Return any directly dependent _nodes.
	 */
	public Collection<BetaNode> getBetas()
	{
		return children;
	}

	/**
	 * Reset any dependent _nodes
	 */
	public void reset()
	{
		for (final BetaNode child : children)
			child.reset();
	}

	public void restore(final int branch)
	{
		//		if (!marked) {
		for (final BetaNode child : children)
			child.restore(branch);
	}

	public void mark()
	{
		setMark(true);
	}

	public void unmark()
	{
		setMark(false);
	}

	private void setMark(final boolean value)
	{
		marked = value;
		for (final ReteNode child : children)
			child.setMark(value);
	}

	public void print(final String indent)
	{
		System.out.print(indent);
		System.out.println(this);
	}
}
