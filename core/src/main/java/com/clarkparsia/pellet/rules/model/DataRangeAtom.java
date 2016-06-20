// Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package com.clarkparsia.pellet.rules.model;

import openllet.aterm.ATermAppl;

/**
 * <p>
 * Title: Data Range Atom
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
public class DataRangeAtom extends UnaryAtom<AtomDObject>
{

	public DataRangeAtom(final ATermAppl predicate, final AtomDObject argument)
	{
		super(predicate, argument);
	}

	@Override
	public void accept(final RuleAtomVisitor visitor)
	{
		visitor.visit(this);
	}

}
