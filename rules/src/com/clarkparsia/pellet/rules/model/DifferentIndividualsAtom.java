// Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package com.clarkparsia.pellet.rules.model;



/**
 * <p>
 * Title: Different Individuals Atom
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
public class DifferentIndividualsAtom extends BinaryAtom<String, AtomIObject, AtomIObject> {

	public DifferentIndividualsAtom(AtomIObject argument1, AtomIObject argument2) {
		super("DIFFERENT", argument1, argument2);
	}

	@Override
	public void accept(RuleAtomVisitor visitor) {
		visitor.visit( this );
	}
	
	public String toString() {
		return getArgument1()+" != "+getArgument2();
	}

}
