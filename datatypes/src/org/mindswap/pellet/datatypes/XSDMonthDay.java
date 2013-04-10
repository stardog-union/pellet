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
import com.sun.msv.datatype.xsd.datetime.BigTimeDurationValueType;
import com.sun.msv.datatype.xsd.datetime.IDateTimeValueType;
import com.sun.msv.datatype.xsd.datetime.ITimeDurationValueType;

/**
 * @author kolovski
 */
public class XSDMonthDay extends BaseXSDAtomicType implements AtomicDatatype, XSDAtomicType {
    private static XSDatatype dt = null;
    private static IDateTimeValueType min = null;
    private static IDateTimeValueType max = null;

    static {
        try {
            dt = DatatypeFactory.getTypeByName( "gMonthDay" );
            min = (IDateTimeValueType) dt.createValue( "--01-01", null );
            max = (IDateTimeValueType) dt.createValue( "--12-31", null );
        }
        catch( DatatypeException e ) {
            e.printStackTrace();
        }
    }

    private static final ValueSpace MONTH_DAY_VALUE_SPACE = new MonthDayValueSpace();

    private static class MonthDayValueSpace extends AbstractDateTimeValueSpace implements ValueSpace {

		public MonthDayValueSpace() {
            super( min, max, dt );
        }

        //return the difference in MonthDays
        public int count( Object start, Object end ) {
            long calendarStart = ((IDateTimeValueType) start).toCalendar().getTimeInMillis();
            long calendarEnd = ((IDateTimeValueType) end).toCalendar().getTimeInMillis();
            long diff = calendarEnd - calendarStart;
            int numberOfDays = (int) (diff / (100 * 60 * 60 * 24));

            return numberOfDays;
        }

        public Object succ( Object value, int n ) {
            BigInteger bigN = new BigInteger( String.valueOf( n ) );
            ITimeDurationValueType nDays =
                new BigTimeDurationValueType(
                    NumberUtils.INTEGER_ZERO, NumberUtils.INTEGER_ZERO, bigN, 
                    NumberUtils.INTEGER_ZERO, NumberUtils.INTEGER_ZERO, NumberUtils.DECIMAL_ZERO );
            IDateTimeValueType s = ((IDateTimeValueType)value).add( nDays );
            
            return s;
        }
    }

    public static XSDMonthDay instance = new XSDMonthDay( ATermUtils.makeTermAppl( Namespaces.XSD
        + "gMonthDay" ) );

    protected XSDMonthDay( ATermAppl name ) {
        super( name, MONTH_DAY_VALUE_SPACE );
    }

    public BaseXSDAtomicType create( GenericIntervalList intervals ) {
        XSDMonthDay type = new XSDMonthDay( null );
        type.values = intervals;

        return type;
    }

    public AtomicDatatype getPrimitiveType() {
        return instance;
    }
}