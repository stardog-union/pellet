// Portions Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// Clark & Parsia, LLC parts of this source code are available under the terms of the Affero General Public License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package org.mindswap.pellet.datatypes;

import org.mindswap.pellet.utils.ATermUtils;
import org.mindswap.pellet.utils.GenericIntervalList;
import org.mindswap.pellet.utils.Namespaces;
import org.mindswap.pellet.utils.NumberUtils;
import org.relaxng.datatype.DatatypeException;

import aterm.ATermAppl;

import com.sun.msv.datatype.xsd.DatatypeFactory;
import com.sun.msv.datatype.xsd.XSDatatype;



/**
 * @author Evren Sirin
 */
public class XSDFloat extends BaseXSDAtomicType implements AtomicDatatype {
    private static XSDatatype dt = null;

    static {
        try {
            dt = DatatypeFactory.getTypeByName( "float" );
        }
        catch( DatatypeException e ) {
            e.printStackTrace();
        }
    }
    
    private static final Object min  = Float.NEGATIVE_INFINITY;
    private static final Object zero = Float.valueOf( 0.0f );
    private static final Object max  = Float.POSITIVE_INFINITY;

    private  static final ValueSpace FLOAT_VALUE_SPACE = new FloatValueSpace();

    private  static class FloatValueSpace extends AbstractValueSpace implements ValueSpace {

		public FloatValueSpace() {
            super( min, zero, max, true );
        }
        
        public boolean isValid( Object value ) {
            return (value instanceof Float);
        }
                
        public Object getValue( String literal ) {
            return dt.createValue( literal, null );
        }

        public String getLexicalForm( Object value ) {
            return dt.convertToLexicalValue( value, null );
        }
        
        public int compare( Object a, Object b ) {
            Integer cmp = compareInternal( a, b );
            if( cmp != null )
                return cmp.intValue();

            return NumberUtils.compare( (Number) a, (Number) b );
        }

        public int count( Object start, Object end ) {
            Integer cmp = countInternal( start, end );
            if( cmp != null )
                return cmp.intValue();

            final Float endFloat = ((Float) end).floatValue();
            final Float startFloat = ((Float) start).floatValue();
            
            if ( endFloat < startFloat ) return 0;

            long count;
            int endBits = Float.floatToRawIntBits( endFloat );
            int startBits = Float.floatToRawIntBits( startFloat );

            if (startFloat < 0) {
            	if (endFloat < 0) {
            		count = (startBits & 0x7fffffff) - (endBits & 0x7fffffff) + 1; 
            	} else {
            		count = (startBits & 0x7fffffff) + (endBits & 0x7fffffff) + 2;
            	}
            } else {
            	count = (endBits & 0x7fffffff) - (startBits & 0x7fffffff) + 1; 
            }

            return count > Integer.MAX_VALUE ? INFINITE : (int) count;
        }

        public Object succ( Object start, int n ) {
            if( isInfinite( start ) )
                throw new IllegalArgumentException( "Cannot handle infinite values" );

            return NumberUtils.add( (Number) start, n );
        }
    }

    public static XSDFloat instance = new XSDFloat( ATermUtils.makeTermAppl( Namespaces.XSD + "float" ) );

    protected XSDFloat( ATermAppl name ) {
        super( name, FLOAT_VALUE_SPACE );
    }

    public BaseXSDAtomicType create( GenericIntervalList intervals ) {
        XSDFloat type = new XSDFloat( null );
        type.values = intervals;

        return type;
    }

    public AtomicDatatype getPrimitiveType() {
        return instance;
    }
}
