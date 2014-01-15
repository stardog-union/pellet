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
 * Title: Atom Variable
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
public abstract class AtomVariable implements AtomObject {
	
	private String name;
	
	public AtomVariable( String name ) {
		this.name = name;
	}
	
	public int compareTo( ATermAppl arg0 ) {
		return getName().compareTo( arg0.getName() );
	}

    /**
     * Checks if this variable is equal to some other variable.
 	 */
    public boolean equals(Object other) {
		if( this == other )
			return true;
		if( !( other instanceof AtomVariable ) )
			return false;
		return getName().equals( ( ( AtomVariable ) other ).getName() );
	}
    
    public String getName() {
		return name;
	}    
    
    public int hashCode() {
        return name.hashCode();
    }
    
	public String toString() {
        return "?" + URIUtils.getLocalName( name );
    }
}
