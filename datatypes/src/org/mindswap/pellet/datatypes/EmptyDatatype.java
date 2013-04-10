// Portions Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// Clark & Parsia, LLC parts of this source code are available under the terms of the Affero General Public License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package org.mindswap.pellet.datatypes;

import java.util.Set;

import org.mindswap.pellet.exceptions.InternalReasonerException;

import aterm.ATermAppl;


/**
 * Represents an unsatisfiable datatype, that is, a datatype that does not accept any value. 
 * 
 * @author Evren Sirin
 */
public class EmptyDatatype implements AtomicDatatype {
	public static final EmptyDatatype instance = new EmptyDatatype();

	/* (non-Javadoc)
	 * @see org.mindswap.pellet.datatypes.Datatype#size()
	 */
	public int size() {
		return 0;
	}

	/* (non-Javadoc)
	 * @see org.mindswap.pellet.datatypes.Datatype#contains(java.lang.Object)
	 */
	public boolean contains(Object value) {
		return false;
	}
	
	public boolean contains(Object value, AtomicDatatype datatype) {
		return false;
	}


	/* (non-Javadoc)
	 * @see org.mindswap.pellet.datatypes.AtomicDatatype#not()
	 */
	public AtomicDatatype not() {		
		return null;
	}

	/* (non-Javadoc)
	 * @see org.mindswap.pellet.datatypes.AtomicDatatype#intersection(org.mindswap.pellet.datatypes.AtomicDatatype)
	 */
	public AtomicDatatype intersection(AtomicDatatype dt) {		
		return null;
	}

	/* (non-Javadoc)
	 * @see org.mindswap.pellet.datatypes.AtomicDatatype#union(org.mindswap.pellet.datatypes.AtomicDatatype)
	 */
	public AtomicDatatype union(AtomicDatatype dt) {
		
		return null;
	}

	/* (non-Javadoc)
	 * @see org.mindswap.pellet.datatypes.AtomicDatatype#difference(org.mindswap.pellet.datatypes.AtomicDatatype)
	 */
	public AtomicDatatype difference(AtomicDatatype dt) {
		
		return null;
	}

	/* (non-Javadoc)
	 * @see org.mindswap.pellet.datatypes.Datatype#getValue(java.lang.String)
	 */
	public Object getValue(String value, String datatypeURI) {
		
		return null;
	}

	/* (non-Javadoc)
	 * @see org.mindswap.pellet.datatypes.Datatype#singleton(java.lang.Object)
	 */
	public Datatype singleton(Object value) {
		
		return null;
	}

	/* (non-Javadoc)
	 * @see org.mindswap.pellet.datatypes.Datatype#isEmpty()
	 */
	public boolean isEmpty() {		
		return true;
	}

	/* (non-Javadoc)
	 * @see org.mindswap.pellet.datatypes.Datatype#enumeration(java.util.Set)
	 */
	public AtomicDatatype enumeration(Set values) {
		
		return null;
	}

	/* (non-Javadoc)
	 * @see org.mindswap.pellet.datatypes.AtomicDatatype#getPrimitiveType()
	 */
	public AtomicDatatype getPrimitiveType() {
		
		return null;
	}

	/* (non-Javadoc)
	 * @see org.mindswap.pellet.datatypes.AtomicDatatype#isDerived()
	 */
	public boolean isDerived() {
		
		return false;
	}

	/* (non-Javadoc)
	 * @see org.mindswap.pellet.datatypes.Datatype#getName()
	 */
	public ATermAppl getName() {
		return null;
	}

    /* (non-Javadoc)
     * @see org.mindswap.pellet.datatypes.Datatype#getURI()
     */
    public String getURI() {
         return null;
    }

    public ATermAppl getValue( int i ) {
        throw new InternalReasonerException( "No values for this datatype" );
    }	
        
    public String toString() {
        return "EmptyDatatype";
    }
}
