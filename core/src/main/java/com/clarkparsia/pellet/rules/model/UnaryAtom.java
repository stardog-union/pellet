// Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package com.clarkparsia.pellet.rules.model;

import java.util.Collections;
import java.util.List;
import openllet.aterm.ATermAppl;
import org.mindswap.pellet.utils.ATermUtils;

/**
 * <p>
 * Title: Unary Atom
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
public abstract class UnaryAtom<A extends AtomObject> extends RuleAtomImpl<ATermAppl>
{

	private final A _argument;

	public UnaryAtom(final ATermAppl predicate, final A argument)
	{
		super(predicate);
		this._argument = argument;
	}

	@Override
	public List<A> getAllArguments()
	{
		return Collections.singletonList(getArgument());
	}

	public A getArgument()
	{
		return _argument;
	}

	@Override
	public String toString()
	{
		return ATermUtils.toString(getPredicate()) + "(" + getArgument() + ")";
	}

}
