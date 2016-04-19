// Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package com.clarkparsia.pellet.rules.model;

/**
 * <p>
 * Title: Atom Instance Variable
 * </p>
 * <p>
 * Description: A variable that ranges over all named individuals
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
public class AtomIVariable extends AtomVariable implements AtomIObject
{

	public AtomIVariable(final String name)
	{
		super(name);
	}

	@Override
	public void accept(final AtomObjectVisitor visitor)
	{
		visitor.visit(this);
	}

	@Override
	public boolean equals(final Object other)
	{
		if (other instanceof AtomIVariable)
			return super.equals(other);
		return false;
	}

}
