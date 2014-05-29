// Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package com.clarkparsia.pellet.rules.model;

import org.mindswap.pellet.utils.URIUtils;

import aterm.ATermAppl;

/**
 * <p>
 * Title: Individual Property Atom
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
public class IndividualPropertyAtom extends BinaryAtom<ATermAppl, AtomIObject, AtomIObject> {

	public IndividualPropertyAtom(ATermAppl predicate, AtomIObject subject, AtomIObject object) {
		super(predicate, subject, object);
	}

	@Override
	public void accept(RuleAtomVisitor visitor) {
		visitor.visit( this );
	}
	
	public String toString() {
		return URIUtils.getLocalName(getPredicate().getName())+"("+getArgument1()+","+getArgument2()+")";
	}
}
