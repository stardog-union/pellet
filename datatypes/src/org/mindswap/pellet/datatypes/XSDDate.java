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
 * @author Evren Sirin
 */
public class XSDDate extends BaseXSDAtomicType implements AtomicDatatype, XSDAtomicType {
    private static XSDatatype dt = null;
    private static IDateTimeValueType min = null;
    private static IDateTimeValueType max = null;

    static {
        try {
            dt = DatatypeFactory.getTypeByName( "date" );
            min = (IDateTimeValueType) dt.createValue( "-9999-01-01", null );
            max = (IDateTimeValueType) dt.createValue( "9999-12-31", null );
        }
        catch( DatatypeException e ) {
            e.printStackTrace();
        }
    }

    private static final ValueSpace DATE_VALUE_SPACE = new DateValueSpace();

    private static class DateValueSpace extends AbstractDateTimeValueSpace implements ValueSpace {

		public DateValueSpace() {
            super( min, max, dt );
        }

        //return the difference in Dates
        public int count( Object start, Object end ) {
            double milliElapsed = 
                ((IDateTimeValueType) end).toCalendar().getTimeInMillis() - 
                ((IDateTimeValueType) start).toCalendar().getTimeInMillis();
            double daysElapsed = (milliElapsed / 24F / 3600F / 1000F);
            
            return Math.round(Math.round(daysElapsed * 100F) / 100F);
        }

        public Object succ( Object value, int n ) {
            BigInteger bigN = new BigInteger( String.valueOf( n ) );
            ITimeDurationValueType nMonths = new BigTimeDurationValueType(
                NumberUtils.INTEGER_ZERO, NumberUtils.INTEGER_ZERO, bigN, 
                NumberUtils.INTEGER_ZERO, NumberUtils.INTEGER_ZERO, NumberUtils.DECIMAL_ZERO );
            IDateTimeValueType s = ((IDateTimeValueType)value).add( nMonths );
            
            return s;
        }
    }

    public static XSDDate instance = new XSDDate( ATermUtils.makeTermAppl( Namespaces.XSD + "date" ) );

    protected XSDDate( ATermAppl name ) {
        super( name, DATE_VALUE_SPACE );
    }

    public BaseXSDAtomicType create( GenericIntervalList intervals ) {
        XSDDate type = new XSDDate( null );
        type.values = intervals;

        return type;
    }

    public AtomicDatatype getPrimitiveType() {
        return instance;
    }
}
