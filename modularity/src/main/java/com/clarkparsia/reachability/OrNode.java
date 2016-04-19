// Copyright (c) 2006 - 2015, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package com.clarkparsia.reachability;

/**
 * @author Evren Sirin
 */
public class OrNode extends Node
{

	private final int id;

	private boolean active = false;

	public OrNode(final int id)
	{
		this.id = id;
	}

	@Override
	public boolean inputActivated()
	{
		return active ? false : (active = true);
	}

	@Override
	public boolean isActive()
	{
		return active;
	}

	@Override
	public boolean isRedundant()
	{
		if (inputs.size() <= 1)
			return true;

		for (final Node output : outputs)
			if (output instanceof AndNode)
				return false;

		return true;
	}

	@Override
	public void reset()
	{
		active = false;
	}

	@Override
	public String toString()
	{
		return "Or(" + id + ")[" + (active ? "1" : "0") + "]";
	}
}
