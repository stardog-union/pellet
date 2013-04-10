// Portions Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// Clark & Parsia, LLC parts of this source code are available under the terms of the Affero General Public License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package org.mindswap.pellet.datatypes;

import java.math.BigInteger;

import org.mindswap.pellet.utils.ATermUtils;
import org.mindswap.pellet.utils.GenericIntervalList;
import org.mindswap.pellet.utils.Namespaces;
import org.mindswap.pellet.utils.NumberUtils;
import org.relaxng.datatype.DatatypeException;

import aterm.ATermAppl;

import com.sun.msv.datatype.xsd.DatatypeFactory;
import com.sun.msv.datatype.xsd.XSDatatype;
import com.sun.msv.datatype.xsd.datetime.BigDateTimeValueType;
import com.sun.msv.datatype.xsd.datetime.BigTimeDurationValueType;
import com.sun.msv.datatype.xsd.datetime.IDateTimeValueType;
import com.sun.msv.datatype.xsd.datetime.ITimeDurationValueType;

/**
 * @author kolovski
 */
public class XSDMonth extends BaseXSDAtomicType implements AtomicDatatype, XSDAtomicType {    
    private static XSDatatype dt = null;
    private static IDateTimeValueType min = null;
    private static IDateTimeValueType max = null;

    static {
        try {
            dt = DatatypeFactory.getTypeByName( "gMonth" );
            min = (IDateTimeValueType) dt.createValue( "--01--", null );
            max = (IDateTimeValueType) dt.createValue( "--12--", null );
        }
        catch( DatatypeException e ) {
            e.printStackTrace();
        }
    }
    
    private static final ValueSpace MONTH_VALUE_SPACE = new MonthValueSpace();
    
    private static class MonthValueSpace extends AbstractDateTimeValueSpace implements ValueSpace {

		public MonthValueSpace() {
            super( min, max, dt );
        }
        
        public Object getValue( String value ) {
        	// xsdlib wrongly expects the gMonth value to be in the format --MM--
        	// whereas the correct format is --MM. This small hack appends the
        	// string "--" at the end of valid gMonth values to turn them into 
        	// the format required by xsdlib
        	value = value + "--";
            return dt.createValue( value, null );
        } 
        
        //return the difference in Months
        public int count(Object start, Object end) {            
            BigDateTimeValueType calendarStart = ((IDateTimeValueType) start).getBigValue();          
            BigDateTimeValueType calendarEnd = ((IDateTimeValueType) end).getBigValue();      
            
            return calendarEnd.getMonth().intValue() - calendarStart.getMonth().intValue() + 1;           
        }       

        public Object succ( Object value, int n ) {
            BigInteger bigN = new BigInteger( String.valueOf( n ) );
            ITimeDurationValueType nMonths =
                new BigTimeDurationValueType(
                    NumberUtils.INTEGER_ZERO, bigN, NumberUtils.INTEGER_ZERO,
                    NumberUtils.INTEGER_ZERO, NumberUtils.INTEGER_ZERO, NumberUtils.DECIMAL_ZERO );
            IDateTimeValueType s = ((IDateTimeValueType)value).add( nMonths );
            
            return s;
        }
    }
    
    public static XSDMonth instance = new XSDMonth(ATermUtils.makeTermAppl(Namespaces.XSD + "gMonth"));

    protected XSDMonth(ATermAppl name) {
        super( name, MONTH_VALUE_SPACE );
    }

    public BaseXSDAtomicType create( GenericIntervalList intervals ) {
        XSDMonth type = new XSDMonth( null );
        type.values = intervals;

        return type;
    }

    public AtomicDatatype getPrimitiveType() {
        return instance;
    }   
    
    public ATermAppl getValue( int i ) {
		Object value = values.get( i );
		String lexical = valueSpace.getLexicalForm( value );
    	// xsdlib wrongly expects the gMonth value to be in the format --MM--
    	// whereas the correct format is --MM. This small hack removes the
    	// string "--" from the end of xsdlib generated lexical representation 
		// to create a valid gMonth value
		assert lexical.endsWith( "--" );
		assert lexical.length() == 6;
		lexical = lexical.substring( 0, 4 );
		return ATermUtils.makeTypedLiteral( lexical, getPrimitiveType().getURI() );		
    }
}