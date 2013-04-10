// Portions Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// Clark & Parsia, LLC parts of this source code are available under the terms of the Affero General Public License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package org.mindswap.pellet.datatypes;

import java.util.Iterator;
import java.util.Set;

import org.mindswap.pellet.utils.ATermUtils;
import org.mindswap.pellet.utils.GenericIntervalList;
import org.mindswap.pellet.utils.QNameProvider;

import aterm.ATermAppl;

/**
 * @author Evren Sirin
 */
public abstract class BaseXSDAtomicType extends BaseDatatype implements XSDAtomicType {
    protected GenericIntervalList values;

    protected ValueSpace valueSpace;

    protected BaseXSDAtomicType( ATermAppl name, ValueSpace valueSpace ) {
        super( name );

        this.valueSpace = valueSpace;
        this.values = new GenericIntervalList( valueSpace.getMinValue(), valueSpace.getMaxValue(), valueSpace );
    }

    public abstract BaseXSDAtomicType create( GenericIntervalList intervals );
    
    public ValueSpace getValueSpace() {
        return valueSpace;
    }
    
    public Object getValue( String value, String datatypeURI ) {
        try {
            Object obj = valueSpace.getValue( value.trim() );
            return obj != null && values.contains( obj )
            	? obj
            	: null;            	
        }
        catch( NumberFormatException e ) {
            return null;
        }
    }

    public AtomicDatatype not() {
        return getPrimitiveType().difference( this );
    }

    public AtomicDatatype intersection( AtomicDatatype dt ) {
        if( this == dt )
            return this;

        GenericIntervalList result = new GenericIntervalList( valueSpace );
        if( dt instanceof BaseXSDAtomicType ) {
            BaseXSDAtomicType other = (BaseXSDAtomicType) dt;

            GenericIntervalList original = new GenericIntervalList( values );
            Iterator it = other.values.iterator();
            while( it.hasNext() ) {
                GenericIntervalList.Interval interval = (GenericIntervalList.Interval) it.next();
                GenericIntervalList o = new GenericIntervalList( original );
                o.restrictToInterval( interval );
                result.addIntervalList( o );
            }
        }

        return create( result );
    }

    public AtomicDatatype union( AtomicDatatype dt ) {
        if( this == dt )
            return this;

        GenericIntervalList result = new GenericIntervalList( valueSpace );
        if( dt instanceof BaseXSDAtomicType ) {
            BaseXSDAtomicType other = (BaseXSDAtomicType) dt;

            result.addIntervalList( values );
            result.addIntervalList( other.values );
        }

        return create( result );
    }

    public AtomicDatatype difference( AtomicDatatype dt ) {
        if( this == dt )
            return EmptyDatatype.instance;

        GenericIntervalList result = new GenericIntervalList( valueSpace );
        if( dt instanceof BaseXSDAtomicType ) {
            BaseXSDAtomicType other = (BaseXSDAtomicType) dt;

            result.addIntervalList( values );
            result.removeIntervalList( other.values );
        }

        return create( result );
    }

    public AtomicDatatype enumeration( Set enum_ ) {
        GenericIntervalList result = new GenericIntervalList( valueSpace );
        for( Iterator i = enum_.iterator(); i.hasNext(); ) {
            Number number = (Number) i.next();
            result.addInterval( number, number );
        }

        return create( result );
    }

    public Datatype singleton( Object value ) {
        GenericIntervalList result = new GenericIntervalList( valueSpace );
        result.addInterval( value, value );

        return create( result );
    }

    public int size() {
        return values.count();
    }

    public ATermAppl getValue( int i ) {        
        Object value = values.get( i );
//        System.out.println(this + " " + i + " " + value);
        String lexical = valueSpace.getLexicalForm( value );
        return ATermUtils.makeTypedLiteral( lexical, getPrimitiveType().getURI() );
    }

    public boolean contains( Object value ) {
        if( valueSpace.isValid( value ) )
            return values.contains( value );

        return false;
    }
        
    public XSDAtomicType deriveByRestriction( DatatypeFacet facet, Object value ) throws UnsupportedOperationException {
        try {
            Object start = valueSpace.getMinValue(), end = valueSpace.getMaxValue();
            boolean incStart = true, incEnd = true;
            if( facet == DatatypeFacet.minInclusive ) {
                start = value;
            }
            else if( facet == DatatypeFacet.minExclusive ) {
                start = value;
                incStart = false;           
            }
            else if( facet == DatatypeFacet.maxInclusive ) {
                end = value;
            }
            else if( facet == DatatypeFacet.maxExclusive )  {
                end = value;
                incEnd = false;           
            }
            else 
                return this;
            
            // create an interval from the min max values
            GenericIntervalList intervalList = new GenericIntervalList( values );
            // intersect the current interval list with this restriction
            intervalList.restrictToInterval( start, incStart, end, incEnd );
            // derive the new type 
            return create( intervalList );
        } catch (Exception e) {
            e.printStackTrace();
            throw new UnsupportedOperationException("Value " + value + " is not valid for the facet " + facet);
        }       
    }
    
    public XSDAtomicType restrictMin( boolean inclusive, Object value ) {      
        return inclusive
            ? deriveByRestriction( DatatypeFacet.minInclusive, value )
            : deriveByRestriction( DatatypeFacet.minExclusive, value );
    }

    public XSDAtomicType restrictMinInclusive( Object value ) {
        return deriveByRestriction( DatatypeFacet.minInclusive, value );
    }

    public XSDAtomicType restrictMinExclusive( Object value ) {
        return deriveByRestriction( DatatypeFacet.minExclusive, value );
    }

    public XSDAtomicType restrictMax( boolean inclusive, Object value ) {
        return inclusive
            ? deriveByRestriction( DatatypeFacet.maxInclusive, value )
            : deriveByRestriction( DatatypeFacet.maxExclusive, value );        
    }

    public XSDAtomicType restrictMaxInclusive( Object value ) {
        return deriveByRestriction( DatatypeFacet.maxInclusive, value );
    }

    public XSDAtomicType restrictMaxExclusive( Object value ) {
        return deriveByRestriction( DatatypeFacet.maxExclusive, value );
    }

    public XSDAtomicType restrictPattern( String pattern ) {
        return deriveByRestriction( DatatypeFacet.pattern, pattern );        
    }
   
    public XSDAtomicType restrictLength( int length ) {
    	return deriveByRestriction( DatatypeFacet.length, length );
    }

    public XSDAtomicType restrictMinLength(int length) {
		return deriveByRestriction( DatatypeFacet.minLength, length );
	}

	public XSDAtomicType restrictMaxLength(int length) {
		return deriveByRestriction( DatatypeFacet.maxLength, length );
	}
    
    public String toString() {
        QNameProvider qnames = new QNameProvider();
        String str;
        if( name == null ) 
            str = qnames.shortForm( getPrimitiveType().getName().toString() ) + " " + values;        
        else
            str = qnames.shortForm( name.toString() );
            
        
        return str;
    }
}
