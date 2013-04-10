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
public class XSDYearMonth extends BaseXSDAtomicType implements AtomicDatatype, XSDAtomicType {    
    private static XSDatatype dt = null;
    private static IDateTimeValueType min = null;
    private static IDateTimeValueType max = null;

    static {
        try {
            dt = DatatypeFactory.getTypeByName( "gYearMonth" );
            min = (IDateTimeValueType) dt.createValue( "0001-01", null );
            max = (IDateTimeValueType) dt.createValue( "9999-12", null );
        }
        catch( Exception e ) {
            e.printStackTrace();
        }
    }
    
    private static final ValueSpace YEAR_MONTH_VALUE_SPACE = new YearMonthValueSpace();
    
    private static class YearMonthValueSpace extends AbstractDateTimeValueSpace implements ValueSpace {

		public YearMonthValueSpace() {
            super( min, max, dt );
        }
        
        //return the difference in YearMonths
        public int count(Object start, Object end) {   
            BigDateTimeValueType calendarStart = ((IDateTimeValueType)start).getBigValue();          
            BigDateTimeValueType calendarEnd = ((IDateTimeValueType)end).getBigValue();      
            
            // possible overflow error
            int numYears = calendarEnd.getYear().intValue() - calendarStart.getYear().intValue() + 1;
            int numMonths = calendarEnd.getMonth().intValue() - calendarStart.getMonth().intValue() + 1;
            
            return 12 * numYears + numMonths;            
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
    
    public static XSDYearMonth instance = new XSDYearMonth(ATermUtils.makeTermAppl(Namespaces.XSD + "gYearMonth"));

    protected XSDYearMonth(ATermAppl name) {
        super( name, YEAR_MONTH_VALUE_SPACE );
    }

    public BaseXSDAtomicType create( GenericIntervalList intervals ) {
        XSDYearMonth type = new XSDYearMonth( null );
        type.values = intervals;

        return type;
    }

    public AtomicDatatype getPrimitiveType() {
        return instance;
    }   
}   
		