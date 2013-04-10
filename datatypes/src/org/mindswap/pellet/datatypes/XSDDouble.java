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
public class XSDDouble extends BaseXSDAtomicType implements AtomicDatatype {
    private static XSDatatype dt = null;

    static {
        try {
            dt = DatatypeFactory.getTypeByName( "double" );
        }
        catch( DatatypeException e ) {
            e.printStackTrace();
        }
    }
    
    private static final Object min  = Double.NEGATIVE_INFINITY;
    private static final Object zero = Double.valueOf( 0.0 );
    private static final Object max  = Double.POSITIVE_INFINITY;

    private static final ValueSpace DOUBLE_VALUE_SPACE = new DoubleValueSpace();

    private static class DoubleValueSpace extends AbstractValueSpace implements ValueSpace {

		public DoubleValueSpace() {
            super( min, zero, max, true );
        }
                
        public boolean isValid( Object value ) {
            return (value instanceof Double);
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

            // FIXME
            long count = (long) ((((Double) end).doubleValue() - ((Double) start).doubleValue()) / Double.MIN_VALUE);

            return count > Integer.MAX_VALUE ? INFINITE : (int) count;
        }

        public Object succ( Object start, int n ) {
            if( isInfinite( start ) )
                throw new IllegalArgumentException( "Cannot handle infinite values" );

            return NumberUtils.add( (Number) start, n );
        }
    }

    public final static XSDDouble instance = new XSDDouble( ATermUtils.makeTermAppl( Namespaces.XSD + "double" ) );

    protected XSDDouble( ATermAppl name ) {
        super( name, DOUBLE_VALUE_SPACE );
        
//        Double NaN = new Double( Double.NaN ); 
//        values.addInterval( NaN, NaN );
    }

    public BaseXSDAtomicType create( GenericIntervalList intervals ) {
        XSDDouble type = new XSDDouble( null );
        type.values = intervals;

        return type;
    }

    public AtomicDatatype getPrimitiveType() {
        return instance;
    }
}
