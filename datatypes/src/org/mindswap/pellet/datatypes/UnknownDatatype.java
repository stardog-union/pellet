// Portions Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// Clark & Parsia, LLC parts of this source code are available under the terms of the Affero General Public License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package org.mindswap.pellet.datatypes;

import org.mindswap.pellet.utils.ATermUtils;


/**
 * @author Evren Sirin
 */
public class UnknownDatatype extends BaseAtomicDatatype implements AtomicDatatype {
	public static final UnknownDatatype instance = new UnknownDatatype();

	public UnknownDatatype() {
		super(ATermUtils.makeTermAppl("UnknownDatatype"));
	}
	
	public static UnknownDatatype create( String name ) {
		UnknownDatatype unknown = (UnknownDatatype) instance.derive( instance.values, false );
		unknown.name = ATermUtils.makeTermAppl( name );
		return unknown;
	}

	public AtomicDatatype getPrimitiveType() {
		return instance;
	}

	public Object getValue(String value, String datatypeURI) {
		return value;
	}
    
    public String toString() {
        return "UnknownDatatype";
    }
}
