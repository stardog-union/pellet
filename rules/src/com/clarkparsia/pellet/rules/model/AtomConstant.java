// Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package com.clarkparsia.pellet.rules.model;

import aterm.ATermAppl;

/**
 * <p>
 * Title: Atom Constant
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
public abstract class AtomConstant implements AtomObject {

	private ATermAppl value;

	public AtomConstant(ATermAppl value) {
		this.value = value;
	}

	public boolean equals(Object other) {
		if( this == other )
			return true;
		if( !( other instanceof AtomConstant ) )
			return false;
		Object otherValue = ( ( AtomConstant ) other ).value;
		return value == otherValue || ( value != null && value.equals( otherValue ) );
	}

	/**
	 * Returns the aterm value this constant was initialized with.
	 */
	public ATermAppl getValue() {
		return value;
	}

	public int hashCode() {
		return value.hashCode();
	}

}
