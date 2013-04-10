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
public class XSDTime extends BaseXSDAtomicType implements AtomicDatatype, XSDAtomicType {
    private static XSDatatype dt = null;
    private static IDateTimeValueType min = null;
    private static IDateTimeValueType max = null;

    static {
        try {
            dt = DatatypeFactory.getTypeByName( "time" );
            min = (IDateTimeValueType) dt.createValue( "00:00:00.000Z", null );
            max = (IDateTimeValueType) dt.createValue( "23:59:59.999Z", null );
        }
        catch( DatatypeException e ) {
            e.printStackTrace();
        }
    }

    private static final ValueSpace TIME_VALUE_SPACE = new TimeValueSpace();

    private static class TimeValueSpace extends AbstractDateTimeValueSpace implements ValueSpace {

		public TimeValueSpace() {
            super( min, max, dt );
        }

        //return the difference in Times
        public int count( Object start, Object end ) {
            long calendarStart = ((IDateTimeValueType) start).toCalendar().getTimeInMillis();
            long calendarEnd = ((IDateTimeValueType) end).toCalendar().getTimeInMillis();
            long diff = calendarStart - calendarEnd;

            if( calendarStart >= calendarEnd )
             	return 0;
            else if( diff > Integer.MAX_VALUE || diff < 0 ) // check for overflow
                return Integer.MAX_VALUE;
            else
            	return (int) diff;
        }

        public Object succ( Object value, int n ) {
            BigDecimal bigN = new BigDecimal( String.valueOf( n ) );
            ITimeDurationValueType nSeconds =
                new BigTimeDurationValueType(
                    BigInteger.ZERO, BigInteger.ZERO, BigInteger.ZERO,
                    BigInteger.ZERO, BigInteger.ZERO, bigN );
            IDateTimeValueType s = ((IDateTimeValueType)value).add( nSeconds );
            
            return s;
        }
    }

    public static XSDTime instance = new XSDTime( ATermUtils.makeTermAppl( Namespaces.XSD + "time" ) );

    protected XSDTime( ATermAppl name ) {
        super( name, TIME_VALUE_SPACE );
    }

    public BaseXSDAtomicType create( GenericIntervalList intervals ) {
        XSDTime type = new XSDTime( null );
        type.values = intervals;

        return type;
    }

    public AtomicDatatype getPrimitiveType() {
        return instance;
    }
}