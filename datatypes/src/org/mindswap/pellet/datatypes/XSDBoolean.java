// Portions Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// Clark & Parsia, LLC parts of this source code are available under the terms of the Affero General Public License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package org.mindswap.pellet.datatypes;

import org.mindswap.pellet.exceptions.InternalReasonerException;
import org.mindswap.pellet.utils.ATermUtils;
import org.mindswap.pellet.utils.Namespaces;
import org.relaxng.datatype.DatatypeException;

import aterm.ATermAppl;

import com.sun.msv.datatype.xsd.DatatypeFactory;
import com.sun.msv.datatype.xsd.XSDatatype;

/**
 * @author Evren Sirin
 */
public class XSDBoolean extends BaseAtomicDatatype implements AtomicDatatype {
	public static final XSDBoolean instance = new XSDBoolean();
	
    private static XSDatatype dt = null;

    static {
        try {
            dt = DatatypeFactory.getTypeByName( "boolean" );
        }
        catch( DatatypeException e ) {
            e.printStackTrace();
        }
    }
    
    final static int NO_VALUES   = 0;
    final static int ONLY_TRUE   = 1;
    final static int ONLY_FALSE  = 2;
	final static int BOTH_VALUES = 3;
	
	protected int status = BOTH_VALUES;
	
	public static class XSDDerivedBooleanType extends XSDBoolean {
		protected XSDDerivedBooleanType(int status) {	
			this.status = status;
		}

		public boolean isDerived() {
			return true;
		}
	}

	XSDBoolean() {
		super(ATermUtils.makeTermAppl(Namespaces.XSD + "boolean"));
	}


	public AtomicDatatype not() {
		return new XSDDerivedBooleanType(BOTH_VALUES - status);
	}
	
	/* (non-Javadoc)
	 * @see org.mindswap.pellet.datatypes.AtomicDatatype#intersection(org.mindswap.pellet.datatypes.AtomicDatatype)
	 */
	public AtomicDatatype intersection(AtomicDatatype dt) {
		if(this == dt) return this;

		int result = NO_VALUES;
		if(dt instanceof XSDBoolean) {
			XSDBoolean other = (XSDBoolean) dt;
			result = status & other.status;						
		}
		
		return new XSDDerivedBooleanType(result);
	}

	/* (non-Javadoc)
	 * @see org.mindswap.pellet.datatypes.AtomicDatatype#union(org.mindswap.pellet.datatypes.AtomicDatatype)
	 */
	public AtomicDatatype union(AtomicDatatype dt) {
		if(this == dt) return this;

		int result = NO_VALUES;
		if(dt instanceof XSDBoolean) {
			XSDBoolean other = (XSDBoolean) dt;
			result = status | other.status;						
		}
		
		return new XSDDerivedBooleanType(result);
	}

	/* (non-Javadoc)
	 * @see org.mindswap.pellet.datatypes.AtomicDatatype#difference(org.mindswap.pellet.datatypes.AtomicDatatype)
	 */
	public AtomicDatatype difference(AtomicDatatype dt) {
		return intersection(dt.not());
	}

	/* (non-Javadoc)
	 * @see org.mindswap.pellet.datatypes.Datatype#size()
	 */
	public int size() {
		if(status == NO_VALUES)
			return 0;
		if(status == BOTH_VALUES)
			return 2;
		
		return 1;
	}

	/* (non-Javadoc)
	 * @see org.mindswap.pellet.datatypes.Datatype#contains(java.lang.Object)
	 */
	public boolean contains(Object value) {
		if(value instanceof Boolean) {
			Boolean bool = (Boolean) value;
			return (status == BOTH_VALUES)
				|| (status == ONLY_TRUE && bool.booleanValue())
				|| (status == ONLY_FALSE && !bool.booleanValue());
		}	
		
		return false;	
	}
	
	/* (non-Javadoc)
	 * @see org.mindswap.pellet.datatypes.Datatype#enumeration(java.util.Set)
	 */
	public Datatype singleton(Object value) {
		if(value instanceof Boolean) {
			boolean bool = ((Boolean) value).booleanValue();	
			return new XSDDerivedBooleanType(bool ? ONLY_TRUE : ONLY_FALSE);
		}	
			
		return null;			
	}

	/* (non-Javadoc)
	 * @see org.mindswap.pellet.datatypes.Datatype#getValue(java.lang.String)
	 */
	public Object getValue(String value, String datatypeURI) {
        return dt.createValue( value, null );
	}

	public AtomicDatatype getPrimitiveType() {
		return instance;
	}
    
    public ATermAppl getValue( int i ) {
        switch( status ) {
            case NO_VALUES:  
                throw new InternalReasonerException( "This datatype is empty!" );
            case ONLY_TRUE: 
                if( i == 0 )
                    return ATermUtils.makeTypedLiteral( "true", name.getName());
                else
                    throw new InternalReasonerException( "No more values!" );
            case ONLY_FALSE: 
                if( i == 0 )
                    return ATermUtils.makeTypedLiteral( "false", name.getName());
                else
                    throw new InternalReasonerException( "No more values!" );
            case BOTH_VALUES:
                if( i == 0 )
                    return ATermUtils.makeTypedLiteral( "true", name.getName());
                else if( i == 1 )
                    return ATermUtils.makeTypedLiteral( "false", name.getName());
                else
                    throw new InternalReasonerException( "No more values!" );
        }
        
        throw new InternalReasonerException( "Invalid status!" );
    }
}
