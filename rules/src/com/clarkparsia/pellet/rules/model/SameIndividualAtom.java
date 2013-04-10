// Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package com.clarkparsia.pellet.rules.model;



/**
 * <p>
 * Title: Same Individual Atom
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
public class SameIndividualAtom extends BinaryAtom<String, AtomIObject, AtomIObject> {

	public SameIndividualAtom( AtomIObject argument1, AtomIObject argument2 ) {
		super( "SAME", argument1, argument2 );
	}

	public void accept( RuleAtomVisitor visitor ) {
		visitor.visit( this );
	}
	
	public String toString() {
		return getArgument1()+" = "+getArgument2();
	}

}
