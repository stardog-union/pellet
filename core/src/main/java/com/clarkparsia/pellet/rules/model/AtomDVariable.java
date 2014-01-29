// Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package com.clarkparsia.pellet.rules.model;

/**
 * <p>
 * Title: Atom Data Variable
 * </p>
 * <p>
 * Description: A variable that ranges over all data values
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
public class AtomDVariable extends AtomVariable implements AtomDObject {

	public AtomDVariable(String name) {
		super(name);
	}

	public void accept(AtomObjectVisitor visitor) {
		visitor.visit( this );
	}

	public boolean equals( Object other ) {
		if ( other instanceof AtomDVariable ) {
			return super.equals( other );
		} 
		return false;
	}
}
