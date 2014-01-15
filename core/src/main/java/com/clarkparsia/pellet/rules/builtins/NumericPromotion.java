// Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package com.clarkparsia.pellet.rules.builtins;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;

import org.mindswap.pellet.exceptions.InternalReasonerException;

/**
 * <p>
 * Title: Numeric Promotion
 * </p>
 * <p>
 * Description: Utility to promote two numerics to comparable types.
 * </p>
 * <p>
 * Copyright: Copyright (c) 2007
 * </p>
 * <p>
 * Company: Clark & Parsia, LLC. <http://www.clarkparsia.com>
 * </p>
 * 
 * @author Ron Alford
 */ 
public class NumericPromotion {
	private enum Type {
		BIGDECIMAL(5),
		BIGINTEGER(4),
		BYTE(0),
		DOUBLE(7),
		FLOAT(6),
		INTEGER(2),
		LONG(3),
		SHORT(1);
		
		private final int rank;
		
		Type( int rank ) {
			this.rank = rank;
		}
		
		/**
		 * Return true if given type is not null and has a rank strictly greater than this.
		 * @param t
		 * @return
		 */
		public boolean isLessThan( Type t ) {
			if ( t == null )
				return false;
			return rank < t.rank;
		}
	}
	
	
	private BigInteger[] bigIntArgs;
	private BigDecimal[] decimalArgs;
	private Double[] doubleArgs;
	private Float[] floatArgs;
	
	private Type type;
	
	public void accept( NumericVisitor visitor ) {
		switch( type ) {
		case BIGDECIMAL:
			visitor.visit( decimalArgs );
			break;
		case BIGINTEGER:
			visitor.visit( bigIntArgs );
			break;
		case DOUBLE:
			visitor.visit( doubleArgs );
			break;
		case FLOAT:
			visitor.visit( floatArgs );
			break;
		default:
			throw new InternalReasonerException( "Cannot visit type " + type );
		}
	}	
	
	/**
	 * Return the highest type seen in an array of Numbers.
	 */
	private Type findHighestType( Number[] nums) {
		Type largest = Type.BYTE;
	
		for ( Number num : nums ) {
			Type type = findType( num );
			if ( largest.isLessThan( type ) ) {
				largest = type;
			}
		}
		
		return largest;
	}
	/**
	 * Return the type of the given number.
	 * Throw an InternalReasonerException if the number is not an expected type.
	 */
	private Type findType( Number num ) {
		if ( num instanceof Byte )
			return Type.BYTE;
		else if ( num instanceof Short )
			return Type.SHORT;
		else if ( num instanceof Integer )
			return Type.INTEGER;
		else if ( num instanceof Long )
			return Type.LONG;
		else if ( num instanceof BigInteger ) 
			return Type.BIGINTEGER;
		else if ( num instanceof BigDecimal )
			return Type.BIGDECIMAL;
		else if ( num instanceof Float )
			return Type.FLOAT;
		else if ( num instanceof Double )
			return Type.DOUBLE;
		else
			throw new InternalReasonerException( "Unexpected numeric type '" + num.getClass() +"': " + num);
	}
	
	/**
	 * Creates the array of the given type, and nulls the arrays for the rest of the types.
	 */
	private void prepArray( Type type, int length ) {
			
		bigIntArgs = null;
		decimalArgs = null;
		doubleArgs = null;
		floatArgs = null;
		switch ( type ) {
		case BIGDECIMAL:
			decimalArgs = new BigDecimal[ length ];
			break;
		case BIGINTEGER:
			bigIntArgs = new BigInteger[ length ];
			break;
		case DOUBLE:
			doubleArgs = new Double[ length ];
			break;
		case FLOAT:
			floatArgs = new Float[ length ];
			break;
		default:
			throw new InternalReasonerException( "Faulty switch: Don't know how to handle '" + type + "'." );
		}
	}
	
	public void promote( Number... nums ) {
		promote( Type.BIGINTEGER, nums );
	}
	
	/**
	 * Takes a Number, its position, and its desired (higher) type.
	 * Converts the number to that type, and assigns it to the given position in the
	 * array associated with that type.
	 */
	private void promote( Number arg, int position, Type type2 ) {
		if ( type2.isLessThan( Type.BIGINTEGER ) ) {
			throw new InternalReasonerException( "Cannot promote to anything less than BigInteger" );
		}
		Type type1 = findType( arg );
		
		if ( type2 == Type.DOUBLE ) {
			doubleArgs[position] = arg.doubleValue();
		}
		else if ( type2 == Type.FLOAT ) {
			floatArgs[position] = arg.floatValue();
		}
		else if ( type2 == Type.BIGDECIMAL ) {
			if ( type1 == Type.BIGDECIMAL ) {
				decimalArgs[position] = (BigDecimal) arg;
			} 
			else if ( type1 == Type.BIGINTEGER ) {
				decimalArgs[position] = new BigDecimal( (BigInteger) arg, 0, MathContext.DECIMAL128 );
			} 
			else if ( type1.isLessThan( Type.BIGINTEGER ) ) {
				decimalArgs[position] = new BigDecimal( arg.longValue(), MathContext.DECIMAL128 );
			}
			else {
				throw new InternalReasonerException( "Do not know how to convert " + type1 + " to BigDecimal.");
			}
		}
		else if ( type2 == Type.BIGINTEGER ) {
			if ( type1 == Type.BIGINTEGER ) {
				bigIntArgs[position] = (BigInteger) arg;
			}
			else if ( type1.isLessThan( Type.BIGINTEGER ) ) {
				bigIntArgs[position] = new BigDecimal( arg.longValue(), MathContext.DECIMAL128 ).toBigInteger();
			} else {
				throw new InternalReasonerException( "Do not know how to convert " + type1 + " to BigInteger.");
			}
		} else {
			throw new InternalReasonerException( "Do not know how to promote numbers to type " + type2 );
		}
	}
	
	/**
	 * Takes an array of numeric values and converts all of them into the same type, with
	 * 'minType' defining the minimum common type.
	 * @return The common type everything was converted to.
	 */
	private void promote( Type minType, Number... nums ) {
		Type largest = findHighestType( nums );
		if ( largest.isLessThan( minType ) )
			largest = minType;
				
		prepArray( largest, nums.length );
		for ( int i = 0; i < nums.length; i++ ) {
			promote( nums[i], i, largest );
		}
		
		type = largest;
	}
	
}
