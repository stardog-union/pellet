// Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package com.clarkparsia.pellet.rules.model;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * Title: Property Atom
 * </p>
 * <p>
 * Description:
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
public abstract class BinaryAtom<P, A extends AtomObject, B extends AtomObject> extends RuleAtomImpl<P>
{

	private final A _argument1;
	private final B _argument2;

	public BinaryAtom(final P predicate, final A argument1, final B argument2)
	{
		super(predicate);
		this._argument1 = argument1;
		this._argument2 = argument2;
	}

	@Override
	public List<AtomObject> getAllArguments()
	{
		final List<AtomObject> list = new ArrayList<>(2);
		list.add(getArgument1());
		list.add(getArgument2());
		return list;
	}

	public A getArgument1()
	{
		return _argument1;
	}

	public B getArgument2()
	{
		return _argument2;
	}
}
