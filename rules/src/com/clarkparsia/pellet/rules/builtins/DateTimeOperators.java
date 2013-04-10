// Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package com.clarkparsia.pellet.rules.builtins;

import static org.mindswap.pellet.utils.Namespaces.XSD;

import java.math.BigDecimal;

import javax.xml.datatype.DatatypeConstants;
import javax.xml.datatype.XMLGregorianCalendar;

import org.mindswap.pellet.ABox;
import org.mindswap.pellet.Literal;
import org.mindswap.pellet.utils.ATermUtils;
import org.relaxng.datatype.DatatypeException;

import aterm.ATermAppl;

import com.sun.msv.datatype.xsd.DatatypeFactory;
import com.sun.msv.datatype.xsd.XSDatatype;

/**
 * <p>
 * Title: Date Time Operators
 * </p>
 * <p>
 * Description: Implementations for each of the SWRL date-time operators.
 * </p>
 * <p>
 * Copyright: Copyright (c) 2008
 * </p>
 * <p>
 * Company: Clark & Parsia, LLC. <http://www.clarkparsia.com>
 * </p>
 * 
 * @author Ron Alford
 */ 
public class DateTimeOperators {
	
	private static class Date implements GeneralFunction, StringToStringFunction {
		
		StringFunctionAdapter allBound = new StringFunctionAdapter( this, XSD + "date" );
		
		public boolean apply(ABox abox, Literal[] args) {
			// Assume applicability check means we have the right number of arguments.
			if ( args[0] != null && args[0].getValue() instanceof XMLGregorianCalendar ) {
				XMLGregorianCalendar value = (XMLGregorianCalendar) args[0].getValue();
				Literal[] results = new Literal[ 5 ];
				results[0] = args[0];
				results[1] = createInteger( abox, value.getYear() );
				results[2] = createInteger( abox, value.getMonth() );
				results[3] = createInteger( abox, value.getDay() );
				if ( value.getTimezone() != DatatypeConstants.FIELD_UNDEFINED ) {
					results[4] = abox.addLiteral( ATermUtils.makePlainLiteral( toTZ( value.getTimezone() ) ) );
				} else {
					results[4] = abox.addLiteral( ATermUtils.makePlainLiteral( "" ) );
				}
				
				if ( mergeResults( args, results ) )
					return true;
			} else if ( restBound( args ) ) {
				Literal result = allBound.apply( abox, args[0], argList( args ) );
				if ( result != null ) {
					if ( args[0] == null )
						args[0] = result;
					return true;
				}
			}
			
			return false;
		}

		public String apply(String... args) {
			if ( args.length < 3 || args.length > 4 ) {
				return null;
			}
			
			String tz = "";
			if ( args.length == 4 )
				tz = args[3];
			
			String dateString = toDate( args[0], args[1], args[2] ) + tz;
			
			if ( !checkValue( dateString, "date" ) )
				return null;
			
			return dateString;
		}

		public boolean isApplicable(boolean[] boundPositions) {
			return applicability( 4, 5, boundPositions );
		}
		
	}
	
	private static class DateTime implements GeneralFunction, StringToStringFunction {

		StringFunctionAdapter allBound = new StringFunctionAdapter( this, XSD + "dateTime" );
		
		public boolean apply(ABox abox, Literal[] args) {
			// Assume applicability check means we have the right number of arguments.
			if ( restBound( args ) ) {
				Literal result = allBound.apply( abox, args[0], argList( args ) );
				if ( result != null ) {
					if ( args[0] == null )
						args[0] = result;
					return true;
				}
				return false;
			}
			
			if ( args[0].getValue() instanceof XMLGregorianCalendar ) {
				XMLGregorianCalendar value = (XMLGregorianCalendar) args[0].getValue();
				Literal[] results = new Literal[ 8 ];
				results[0] = args[0];
				results[1] = createInteger( abox, value.getYear());
				results[2] = createInteger( abox, value.getMonth() );
				results[3] = createInteger( abox, value.getDay() );
				results[4] = createInteger( abox, value.getHour() );
				results[5] = createInteger( abox, value.getMinute() );
				final BigDecimal fractionalSeconds = value.getFractionalSecond();
				results[6] = createDecimal( abox, (fractionalSeconds == null)
					? value.getSecond()
					: fractionalSeconds.add( BigDecimal.valueOf( value.getSecond() ) ) );
				if ( value.getTimezone() != DatatypeConstants.FIELD_UNDEFINED ) {
					results[7] = abox.addLiteral( ATermUtils.makePlainLiteral( toTZ( value.getTimezone() ) ) );
				}
				
				if ( mergeResults( args, results ) )
					return true;
			}
			
			return false;
		}
		
		public String apply(String... args) {
			if ( args.length < 6 || args.length > 7 ) {
				return null;
			}
			
			String tz = "";
			if ( args.length == 7 )
				tz = args[6];
			
			String dateTimeString = toDate( args[0], args[1], args[2] ) + "T" + toTime( args[3], args[4], args[5] ) + tz;
			
			if ( !checkValue( dateTimeString, "dateTime" ) )
				return null;
			
			return dateTimeString;
		}
		

		public boolean isApplicable(boolean[] boundPositions) {
			return applicability( 7, 8, boundPositions );
		}
	}
		
	/**
	 * YearMonthDuration  and DayTimeDuration creation are combined into this single class.
	 * The swrl spec seems a bit restrictive in not allowing the creation of negative time durations,
	 * so if the first argument is prefixed with a minus sign, we create a negative duration.
	 * 
	 */
	private static class Duration implements GeneralFunction, StringToStringFunction {
		
		public static enum DURATIONTYPE {
			FULL( 0, 5 ),
			YEARMONTH( 0, 2 ),
			DAYTIME( 2, 5 );
			
			private int start;
			private int end;
			
			DURATIONTYPE( int start, int end ) {
				this.start = start;
				this.end = end;
			}
			
		}
		
		private static final char[] SEP = { 'Y', 'M', 'D', 'H', 'M', 'S' };
		
		private StringFunctionAdapter allBound;
		private DURATIONTYPE granularity;
		
		public Duration( DURATIONTYPE dur ) {
			this.granularity = dur;
			allBound = new StringFunctionAdapter( this, XSD + "duration" );
		}
		
		public boolean apply(ABox abox, Literal[] args) {
			if ( restBound( args ) ) {
				Literal result = allBound.apply( abox, args[0], argList( args ) );
				if ( result != null ) {
					if ( args[0] == null )
						args[0] = result;
					return true;
				}
				return false;
			}
			
			// Can't do anything with duration values until XSDLib is upgraded.
			
			return false;
			
		}
		
		public String apply(String... args) {
			if ( args.length > SEP.length + granularity.start )
				return null;
			
			StringBuffer result;
			if ( args[ 0 ].charAt( 0 ) != '-' ) {
				result = new StringBuffer( "P" );
			} else {
				result = new StringBuffer( "-P" );
				args[ 0 ] = args[ 0 ].substring( 1 );
			}
			
			boolean seenTime = false;
			int i = granularity.start;
			for ( String arg : args ) {
				if( arg.length() > 0 ) {
					if( i > 2 && seenTime == false ) {
						seenTime = true;
						result.append( 'T' );
					}
					
					result.append( arg );
					result.append( SEP[i] );
				}
				i++;
				
			}
					
			return result.toString();
		}
		
		public boolean isApplicable(boolean[] boundPositions) {
			return applicability( 4, 5, boundPositions );
		}
		
	}
	
	private static class Time implements GeneralFunction, StringToStringFunction {

			StringFunctionAdapter allBound = new StringFunctionAdapter( this, XSD + "time" );
			
			public boolean apply(ABox abox, Literal[] args) {
				if ( restBound( args ) ) {
					Literal result = allBound.apply( abox, args[0], argList( args ) );
					if ( result != null ) {
						if ( args[0] == null )
							args[0] = result;
						return true;
					}
					return false;
				}
				
				if ( args[0].getValue() instanceof XMLGregorianCalendar ) {
					XMLGregorianCalendar value = (XMLGregorianCalendar) args[0].getValue();
					Literal[] results = new Literal[ 5 ];
					results[0] = args[0];
					results[1] = createInteger( abox, value.getHour() );
					results[2] = createInteger( abox, value.getMinute() );
					final BigDecimal fractionalSeconds = value.getFractionalSecond();
					results[3] = createDecimal( abox, (fractionalSeconds == null)
						? value.getSecond()
						: fractionalSeconds.add( BigDecimal.valueOf( value.getSecond() ) ) );
					if ( value.getTimezone() != DatatypeConstants.FIELD_UNDEFINED ) {
						results[4] = abox.addLiteral( ATermUtils.makePlainLiteral( toTZ( value.getTimezone() ) ) );
					}
					
					if ( mergeResults( args, results ) )
						return true;
				}
				
				return false;
				
			}
			
			public String apply(String... args) {
				if ( args.length < 3 || args.length > 4 ) {
					return null;
				}
				
				String tz = "";
				if ( args.length == 4 )
					tz = args[3];
				
				String timeString = toTime( args[0], args[1], args[2] ) + tz;
				
				if ( !checkValue( timeString, "time" ) )
					return null;
				
				return timeString;
			}

			public boolean isApplicable(boolean[] boundPositions) {
				return applicability( 4, 5, boundPositions );
			}
		
	}	
	
	public final static GeneralFunction date = new Date();
	
	public final static GeneralFunction dateTime = new DateTime();
	
	public final static Function dayTimeDuration = new StringFunctionAdapter( new Duration( Duration.DURATIONTYPE.DAYTIME ), XSD + "duration" );
	
	public final static GeneralFunction time = new Time();
	
	public final static Function yearMonthDuration = new StringFunctionAdapter( new Duration( Duration.DURATIONTYPE.YEARMONTH), XSD + "duration" );
	
	
	private static boolean applicability( int minargs, int maxargs, boolean[] boundPositions ) {
		if ( boundPositions.length < minargs || boundPositions.length > maxargs ) 
			return false;
		
		if ( boundPositions[0] )
			return true;
		
		for ( int i = 1; i < boundPositions.length; i++ ) {
			if ( !boundPositions[i] )
				return false;
		}
		return true;
		
	}
	
	private static Literal[] argList( Literal[] literals ) {
		Literal[] args = new Literal[ literals.length - 1 ];
		for ( int i = 1; i < literals.length; i++ ) {
			args[ i-1 ] = literals[ i ];
		}
		return args;
	}
	
	private static boolean checkValue( String val, String name ) {
		try {
			XSDatatype dt = DatatypeFactory.getTypeByName( name );
			dt.checkValid( val, null );
		} catch( DatatypeException e ) {
			ABox.log.info( "Failed to create " + name + ":" + e );
			return false;
		}
		return true;
	}
	
	private static Literal createDecimal( ABox abox, Number val ) {
		ATermAppl term = ATermUtils.makeTypedLiteral( val.toString(), XSD + "decimal" );
		return abox.addLiteral( term );
	}
	
	private static Literal createInteger( ABox abox, Number val ) {
		ATermAppl term = ATermUtils.makeTypedLiteral( val.toString(), XSD + "integer" );
		return abox.addLiteral( term );
	}
	
	private static boolean mergeResults( Literal[] args1, Literal[] args2 ) {
		for ( int i = 0; i < args1.length; i++ ) {
			if ( args1[i] == null ) {
				if ( args2[i] == null )
					return false;
				args1[i] = args2[i];
			} else {
				if ( args2[i] != null ) {
					if ( !ComparisonTesters.equal.test( new Literal[]{ args1[i], args2[i] } ) )
						return false;
				}
			}
		}
		return true;
	}
	
	private static String pad( int p, String s ) {
		while ( s.length() < p ) {
			s = "0" + s;
		}
		return s;
	}
	
	private static boolean restBound( Literal[] args ) {
		
		for ( int i = 1; i < args.length; i++ ) {
			if ( args[i] == null )
				return false;
		}
		return true;
	}
	
	private static String toDate( String year, String month, String day ) {
		return pad( 4, year) + "-" + pad( 2, month) + "-" + pad( 2, day );
	}
	
	private static String toTime( String hour, String minute, String second ) {
		String millis = "";
		int point = second.indexOf( '.' );
		if ( point >= 0 ) {
			millis = second.substring( point );
			second = second.substring( 0, point );
		}
		
		return pad( 2, hour ) + ":" + pad( 2, minute ) + ":" + pad( 2, second ) + millis; 
	}
	
	private static String toTZ( int tz ) {
		if ( tz == 0 )
			return "Z";
		else
			return String.format( "%+03d:02d", tz / 60 , tz % 60 );
	}
	
}
