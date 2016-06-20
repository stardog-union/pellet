// Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package com.clarkparsia.pellet.rules.model;

import openllet.aterm.ATermAppl;
import org.mindswap.pellet.utils.URIUtils;

/**
 * <p>
 * Title: Datavalued Property Atom
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
public class DatavaluedPropertyAtom extends BinaryAtom<ATermAppl, AtomIObject, AtomDObject>
{

	public DatavaluedPropertyAtom(final ATermAppl predicate, final AtomIObject subject, final AtomDObject object)
	{
		super(predicate, subject, object);
	}

	@Override
	public void accept(final RuleAtomVisitor visitor)
	{
		visitor.visit(this);
	}

	@Override
	public String toString()
	{
		return URIUtils.getLocalName(getPredicate().getName()) + "(" + getArgument1() + "," + getArgument2() + ")";
	}

}
