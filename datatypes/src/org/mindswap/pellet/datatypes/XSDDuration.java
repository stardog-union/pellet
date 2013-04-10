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

import com.sun.msv.datatype.xsd.DatatypeFactory;
import com.sun.msv.datatype.xsd.XSDatatype;
import com.sun.msv.datatype.xsd.datetime.BigDateTimeValueType;
import com.sun.msv.datatype.xsd.datetime.IDateTimeValueType;
import com.sun.msv.datatype.xsd.datetime.ITimeDurationValueType;
import com.sun.msv.datatype.xsd.datetime.TimeDurationFactory;
import com.sun.msv.datatype.xsd.datetime.TimeZone;

/**
 * @author Evren Sirin
 */
public class XSDDuration extends BaseXSDAtomicType implements AtomicDatatype {
	private static XSDatatype					dt						= null;
	private static final Object					NEG_INF					= "-Inf";
	private static final Object					POS_INF					= "+Inf";
	private static final ITimeDurationValueType	DURATION_ZERO			= TimeDurationFactory
																				.create( null,
																						null, null,
																						null, null,
																						null );
	private static final IDateTimeValueType		TIME_ZERO				= new BigDateTimeValueType(
																				new BigInteger(
																						"1970" ),
																				0,
																				0,
																				0,
																				0,
																				new BigDecimal( 0 ),
																				TimeZone.GMT );

	static {
		try {
			dt = DatatypeFactory.getTypeByName( "duration" );
		} catch( DatatypeException e ) {
			e.printStackTrace();
		}
	}

	public static final ValueSpace				DURATION_VALUE_SPACE	= new DurationValueSpace();

	public static class DurationValueSpace extends AbstractValueSpace implements ValueSpace {

		public DurationValueSpace() {
			super( NEG_INF, DURATION_ZERO, POS_INF, true );
		}

		public boolean isValid(Object value) {
			return (value instanceof ITimeDurationValueType);
		}

		public Object getValue(String value) {
			return (ITimeDurationValueType) dt.createValue( value, null );
		}

		public int compare(Object a, Object b) {
			if( a == b ) {
				return EQ;
			}
			else if( a == POS_INF || b == NEG_INF ) {
				return GT;
			}
			else if( b == POS_INF || a == NEG_INF ) {
				return LT;
			}
			else
				return ((ITimeDurationValueType) a).compare( (ITimeDurationValueType) b );
		}

		public int count(Object start, Object end) {
			if( start == end ) {
				return SIZE_ONE;
			}
			else if( start == POS_INF || end == NEG_INF ) {
				return SIZE_INF;
			}
			else if( end == POS_INF || start == NEG_INF ) {
				return SIZE_INF;
			}

			IDateTimeValueType t1 = TIME_ZERO.add( (ITimeDurationValueType) start );
			IDateTimeValueType t2 = TIME_ZERO.add( (ITimeDurationValueType) end );

			return XSDDateTime.instance.getValueSpace().count( t1, t2 );
		}

		public Object succ(Object start, int n) {
			if( isInfinite( start ) )
				throw new IllegalArgumentException( "Cannot handle infinite values" );

			IDateTimeValueType duration = TIME_ZERO.add( (ITimeDurationValueType) start );

			BigDecimal millis = new BigDecimal( duration.toCalendar().getTimeInMillis() );
			BigDecimal succ = millis.add( new BigDecimal( n ) );

			return TimeDurationFactory.create( null, null, null, null, null, succ );
		}

		public String getLexicalForm(Object value) {
			String str = value.toString();
			
			String abs = str.replaceAll( "-", "" );
			
			boolean negative = str.length() != abs.length();
			
			return negative ? "-" + abs : str;
		}
	}

	public static XSDDuration	instance	= new XSDDuration();

	XSDDuration() {
		super( ATermUtils.makeTermAppl( Namespaces.XSD + "duration" ), DURATION_VALUE_SPACE );
	}

	public BaseXSDAtomicType create(GenericIntervalList intervals) {
		XSDDuration type = new XSDDuration();
		type.values = intervals;

		return type;
	}

	public AtomicDatatype getPrimitiveType() {
		return instance;
	}

}
