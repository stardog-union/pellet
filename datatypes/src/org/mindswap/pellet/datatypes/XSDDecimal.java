// Portions Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// Clark & Parsia, LLC parts of this source code are available under the terms of the Affero General Public License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package org.mindswap.pellet.datatypes;

import java.math.BigDecimal;
import java.math.BigInteger;

import org.mindswap.pellet.utils.ATermUtils;
import org.mindswap.pellet.utils.GenericIntervalList;
import org.mindswap.pellet.utils.Namespaces;
import org.mindswap.pellet.utils.NumberUtils;

import aterm.ATermAppl;

/**
 * @author Evren Sirin
 */
public class XSDDecimal extends BaseXSDAtomicType implements AtomicDatatype, XSDAtomicType {
    private static final Object min = "-Inf";
    private static final Object zero = Byte.valueOf( (byte) 0 );
    private static final Object max = "+Inf";

    private static BigInteger MAX_INT = new BigInteger( String.valueOf( Integer.MAX_VALUE ) );
    
    public static final ValueSpace DECIMAL_VALUE_SPACE = new DecimalValueSpace( true );
    public static final ValueSpace INTEGER_VALUE_SPACE = new DecimalValueSpace( false );

    public static class DecimalValueSpace extends AbstractValueSpace implements ValueSpace {

    	private boolean fractionDigits;
        
        public DecimalValueSpace( boolean fractionDigits ) {
            super( min, zero, max, true );
            
            this.fractionDigits = fractionDigits;
        }
                
        public boolean isValid( Object value ) {
            return (value instanceof Number) && !(value instanceof Double) && !(value instanceof Float);
        }
        
        public Object getValue( String literal ) {
            return fractionDigits ? NumberUtils.parseDecimal( literal ) : NumberUtils.parseInteger( literal );
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
            
            int type1 = NumberUtils.getType( (Number) start );
            int type2 = NumberUtils.getType( (Number) end );
            
            if( fractionDigits ) {
				BigDecimal endValue = (end instanceof BigDecimal)
					? (BigDecimal) end
					: new BigDecimal( end.toString() );
				BigDecimal startValue = (start instanceof BigDecimal)
					? (BigDecimal) start
					: new BigDecimal( start.toString() );
					
				if( endValue.compareTo( startValue ) > 0 ) {
					return INFINITE;
				}
				else {
					return 0;
				}
            }
            
            if( type1 > NumberUtils.LONG || type2 > NumberUtils.LONG ) {
                BigInteger endValue = (end instanceof BigInteger) 
                    ? (BigInteger) end 
                    : new BigInteger( end.toString() ); 
                BigInteger startValue = (start instanceof BigInteger) 
                    ? (BigInteger) start
                    : new BigInteger( start.toString() ); 
                
                BigInteger count = endValue.subtract( startValue );
                count = count.add( BigInteger.ONE );
                
                if( count.compareTo( MAX_INT ) > 0 )
                    return INFINITE;
                
                return count.intValue();
            }
            else {
                long endValue = ((Number) end).longValue();
                long startValue = ((Number) start).longValue();
                            
                long count = endValue - startValue + 1;

                // TODO verify this condition catches all overflow conditions
                if( startValue < 0 && 0 <= endValue && count < endValue )
                    return INFINITE;

                return count > Integer.MAX_VALUE ? INFINITE : (int) count;
            }

        }
        
        public int count_( Object start, Object end ) {
            Integer cmp = countInternal( start, end );
            if( cmp != null )
                return cmp.intValue();

            long endValue = ((Number) end).longValue();
            long startValue = ((Number) start).longValue();
                        
            long count = endValue - startValue + 1;

            // TODO verify this condition catches all overflow conditions
            if( startValue < 0 && 0 < endValue && count < endValue )
                return INFINITE;

            return count > Integer.MAX_VALUE ? INFINITE : (int) count;
        }

        public Object succ( Object start, int n ) {
            if( isInfinite( start ) )
                throw new IllegalArgumentException( "Cannot handle infinite values" );

            return NumberUtils.add( (Number) start, n );
        }

        public String getLexicalForm( Object value ) {
            return value.toString();
        }
    }

    public static XSDDecimal instance = new XSDDecimal( ATermUtils.makeTermAppl( Namespaces.XSD + "decimal" ), true );

    protected XSDDecimal( ATermAppl name, boolean fractionDigits ) {
        super( name, fractionDigits ? DECIMAL_VALUE_SPACE : INTEGER_VALUE_SPACE );
    }

    public BaseXSDAtomicType create( GenericIntervalList intervals ) {
        XSDDecimal type = new XSDDecimal( null, ((DecimalValueSpace) valueSpace).fractionDigits );
        type.values = intervals;

        return type;
    }

    public AtomicDatatype getPrimitiveType() {
        return instance;
    }
    
    public AtomicDatatype intersection( AtomicDatatype dt ) {
    	// if we are intersecting a Decimal type with an Integer type the result
    	// should be an integer type, e.g. it should not contain fractionDigits
    	// as valid values in the value space.
    	return dt instanceof XSDInteger && !(this instanceof XSDInteger) 
    		? dt.intersection( this )
    		: super.intersection( dt );
    }
}
