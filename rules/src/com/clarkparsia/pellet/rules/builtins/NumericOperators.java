// Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package com.clarkparsia.pellet.rules.builtins;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;

/**
 * <p>
 * Title: Numeric Operators
 * </p>
 * <p>
 * Description: Implementations for each of the SWRL numeric operators.
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
public class NumericOperators {

	private static class Abs implements NumericFunction {

		public BigDecimal apply(BigDecimal... args) {
			if ( args.length != 1 )
				return null;
			return args[0].abs();
		}

		public BigInteger apply(BigInteger... args) {
			if ( args.length != 1 )
				return null;
			return args[0].abs();
		}

		public Double apply(Double... args) {
			if ( args.length != 1 )
				return null;
			return Math.abs( args[0] );
		}

		public Float apply(Float... args) {
			if ( args.length != 1 )
				return null;
			return Math.abs( args[0] );
		}	
	}
	
	private static class Add implements NumericFunction {

		public BigDecimal apply(BigDecimal... args) {
			if ( args.length < 1 )
				return null;
		
			BigDecimal sum = args[0];
			for ( int i = 1; i < args.length; i++ ) {
				sum = sum.add( args[i] );
			}
			
			return sum;
		}

		public BigInteger apply(BigInteger... args) {
			if ( args.length < 1 )
				return null;
		
			BigInteger sum = args[0];
			for ( int i = 1; i < args.length; i++ ) {
				sum = sum.add( args[i] );
			}
			
			return sum;
		}

		public Double apply(Double... args) {
			if ( args.length < 1 )
				return null;
		
			double sum = args[0];
			for ( int i = 1; i < args.length; i++ ) {
				sum = sum + args[i];
			}
			
			return sum;
		}

		public Float apply(Float... args) {
			if ( args.length < 1 )
				return null;
		
			float sum = args[0];
			for ( int i = 1; i < args.length; i++ ) {
				sum = sum + args[i];
			}
			
			return sum;
		}	
	}
	
	private static class Ceiling implements NumericFunction {		
		public Ceiling() {
		}
		
		public BigDecimal apply(BigDecimal... args) {
			if ( args.length != 1 )
				return null;
			
			return args[0].setScale( 0, BigDecimal.ROUND_CEILING ).stripTrailingZeros();
		}

		public BigInteger apply(BigInteger... args) {
			if ( args.length != 1 )
				return null;
			
			return args[0];
		}

		public Double apply(Double... args) {
			if ( args.length != 1 )
				return null;
			
			return Math.ceil( args[0] );
		}

		public Float apply(Float... args) {
			if ( args.length != 1 )
				return null;   
			
			return (float) Math.ceil( args[0] );
		}	
	}
	
	private static class Cos implements NumericFunction {

		public Double apply(BigDecimal... args) {
			if ( args.length != 1 )
				return null;
			
			return apply( args[0].doubleValue() );
		}

		public Double apply(BigInteger... args) {
			if ( args.length != 1 )
				return null;
			
			return apply( args[0].doubleValue() );
		}

		public Double apply(Double... args) {
			if ( args.length != 1 )
				return null;
			
			return Math.cos( args[0] );
		}

		public Double apply(Float... args) {
			if ( args.length != 1 )
				return null;
			
			return apply( args[0].doubleValue() );
		}	
	}
	
	private static class Divide implements NumericFunction {

		public BigDecimal apply(BigDecimal... args) {
			if ( args.length != 2 )
				return null;
			if ( args[1].equals( BigDecimal.ZERO ) )
				return null;
		
			BigDecimal result = args[0].divide( args[1], MathContext.DECIMAL128 );
			
			return result;
		}

		public BigDecimal apply(BigInteger... args) {
			if ( args.length != 2 )
				return null;
			if ( args[1].equals( BigInteger.ZERO ) )
				return null;
		
			BigDecimal first = new BigDecimal( args[0] );
			BigDecimal second = new BigDecimal( args[1] );
			
			return first.divide(second, MathContext.DECIMAL128);
		}

		public Double apply(Double... args) {
			if ( args.length != 2 )
				return null;
			
			return args[0] / args[1];
		}

		public Float apply(Float... args) {
			if ( args.length != 2 )
				return null;
			
			return args[0] / args[1];
		}	
	}
	
	private static class Floor implements NumericFunction {		
		public Floor() {
		}
		
		public BigDecimal apply(BigDecimal... args) {
			if ( args.length != 1 )
				return null;
			
			return args[0].setScale( 0, BigDecimal.ROUND_FLOOR ).stripTrailingZeros();
		}

		public BigInteger apply(BigInteger... args) {
			if ( args.length != 1 )
				return null;
			
			return args[0];
		}

		public Double apply(Double... args) {
			if ( args.length != 1 )
				return null;
			
			return Math.floor( args[0] );
		}

		public Float apply(Float... args) {
			if ( args.length != 1 )
				return null;   
			
			return (float) Math.floor( args[0] );
		}	
	}
	
	private static class IntegerDivide implements NumericFunction {

		public BigInteger apply(BigDecimal... args) {
			if ( args.length != 2 )
				return null;
			if ( args[1].equals( BigDecimal.ZERO ) )
				return null;
			
			return args[0].divideToIntegralValue( args[1], MathContext.DECIMAL128 ).toBigIntegerExact();
		}

		public BigInteger apply(BigInteger... args) {
			if ( args.length != 2 )
				return null;
			if ( args[1].equals( BigInteger.ZERO ) )
				return null;
			
			return args[0].divide( args[1] );
		}

		public BigInteger apply(Double... args) {
			if ( args.length != 2 )
				return null;
			
			double result = args[0]/args[1];
			if ( Double.isInfinite( result ) || Double.isNaN( result ) )
				return null;
			
			return new BigDecimal( result ).toBigInteger();
		}

		public BigInteger apply(Float... args) {
			if ( args.length != 2 )
				return null;
			
			float result = args[0]/args[1];
			if ( Float.isInfinite( result ) || Float.isNaN( result ) )
				return null;
			
			return new BigDecimal( result ).toBigInteger();
		}
		
	}

	private static class Mod implements NumericFunction {

		public BigDecimal apply(BigDecimal... args) {
			if ( args.length != 2 )
				return null;
			if ( args[1].equals( BigDecimal.ZERO ) )
				return null;
			
			return args[0].remainder( args[1], MathContext.DECIMAL128 );
		}

		public BigInteger apply(BigInteger... args) {
			if ( args.length != 2 )
				return null;
			if ( args[1].equals( BigInteger.ZERO ) )
				return null;
			
			return args[0].remainder( args[1] );
		}

		public Double apply(Double... args) {
			if ( args.length != 2 )
				return null;
			
			return args[0] % args[1];
		}

		public Float apply(Float... args) {
			if ( args.length != 2 )
				return null;
			
			return args[0] % args[1];
		}
		
	}
	
	private static class Multiply implements NumericFunction {

		public BigDecimal apply(BigDecimal... args) {
			if ( args.length < 1 )
				return null;
		
			BigDecimal result = args[0];
			for ( int i = 1; i < args.length; i++ ) {
				result = result.multiply( args[i] );
			}
			
			return result;
		}

		public BigInteger apply(BigInteger... args) {
			if ( args.length < 1 )
				return null;
		
			BigInteger result = args[0];
			for ( int i = 1; i < args.length; i++ ) {
				result = result.multiply( args[i] );
			}
			
			return result;
		}

		public Double apply(Double... args) {
			if ( args.length < 1 )
				return null;
		
			double result = args[0];
			for ( int i = 1; i < args.length; i++ ) {
				result = result * args[i];
			}
			
			return result;
		}

		public Float apply(Float... args) {
			if ( args.length < 1 )
				return null;
		
			float result = args[0];
			for ( int i = 1; i < args.length; i++ ) {
				result = result * args[i];
			}
			
			return result;
		}	
	}
	
	private static class Pow implements NumericFunction {

		public BigDecimal apply(BigDecimal... args) {
			if ( args.length != 2 )
				return null;
		
			try {
				BigDecimal result = args[0].pow( args[1].intValueExact(), MathContext.DECIMAL128 );
				return result;
			} catch (ArithmeticException e) {
				// TODO If we found (or made) an implementation of pow for decimals, we could return a result.
				return null;
			}
			
		}

		public BigInteger apply(BigInteger... args) {
			if ( args.length != 2 )
				return null;
			
			if ( args[1].compareTo( BIGINTMAX ) > 0 || args[1].compareTo( BigInteger.ZERO ) < 0 )
				return null;
			
			return args[0].pow( args[1].intValue() );
		}

		public Double apply(Double... args) {
			if ( args.length != 2 )
				return null;
			
			return Math.pow( args[0], args[1] );
		}

		public Float apply(Float... args) {
			if ( args.length != 2 )
				return null;
			
			return (float) Math.pow( args[0], args[1] );
		}	
	}
	
	private static class Round implements NumericFunction {
		BigDecimal HALF = new BigDecimal(  "0.5" );
		
		public Round() {
		}
		
		public BigDecimal apply(BigDecimal... args) {
			if ( args.length != 1 )
				return null;
			
			// unfortunately none of the RoundingMode's in BigDecimal are compatible with XQuery
			// definition (or Math.round(double)). BigDecimal.ROUND_UP rounds -2.5 to -3 (rounds
			// in scales without considering sign)
			return args[0].add( HALF ).setScale( 0, BigDecimal.ROUND_FLOOR ).stripTrailingZeros();
		}

		public BigInteger apply(BigInteger... args) {
			if ( args.length != 1 )
				return null;
			
			return args[0];
		}

		public Double  apply(Double... args) {
			if ( args.length != 1 )
				return null;
			
			return Double.valueOf( Math.round( args[0] ) );
		}

		public Float apply(Float... args) {
			if ( args.length != 1 )
				return null;
			
			return Float.valueOf( Math.round( args[0] ) );
		}	
	}
	
	private static class RoundHalfToEven implements NumericFunction {
		
		public RoundHalfToEven() {
		}
		
		public BigDecimal apply(BigDecimal... args) {
			if ( args.length < 1 || args.length > 2 )
				return null;
			
			int scale = 0;
			if ( args.length == 2 ) {
				if ( args[1].stripTrailingZeros().scale() > 0 )
					return null;
				BigInteger bigScale = args[1].toBigInteger();
				if ( bigScale.compareTo( BIGINTMAX ) > 0 || bigScale.compareTo( BIGINTMIN ) < 0 )
					return null;
				scale = bigScale.intValue();
			}
				
			return args[0].setScale( scale, BigDecimal.ROUND_HALF_EVEN ).stripTrailingZeros();
		}

		public BigInteger apply(BigInteger... args) {
			if ( args.length < 1 || args.length > 2 )
				return null;
			return args[0];
		}

		public Double  apply(Double... args) {
			if ( args.length < 1 || args.length > 2 )
				return null;

			BigDecimal[] decs = new BigDecimal[args.length];
			for( int i = 0; i < args.length; i++ ) {				
				decs[i] = new BigDecimal( args[i].toString() );
			}
			return apply( decs ).doubleValue();
		}

		public Float apply(Float... args) {
			if ( args.length < 1 || args.length > 2 )
				return null;
			
			BigDecimal[] decs = new BigDecimal[args.length];
			for( int i = 0; i < args.length; i++ ) {				
				decs[i] = new BigDecimal( args[i].toString() );
			}
			return apply( decs ).floatValue();
		}	
	}
	
	private static class Sin implements NumericFunction {

		public Double apply(BigDecimal... args) {
			if ( args.length != 1 )
				return null;
			
			return apply( args[0].doubleValue() );
		}

		public Double apply(BigInteger... args) {
			if ( args.length != 1 )
				return null;
			
			return apply( args[0].doubleValue() );
		}

		public Double apply(Double... args) {
			if ( args.length != 1 )
				return null;
			
			return Math.sin( args[0] );
		}

		public Double apply(Float... args) {
			if ( args.length != 1 )
				return null;
			
			return apply( args[0].doubleValue() );
		}	
	}
	
	private static class Subtract implements NumericFunction {

		public BigDecimal apply(BigDecimal... args) {
			if ( args.length != 2 )
				return null;
		
			BigDecimal result = args[0].subtract( args[1] );
			
			return result;
		}

		public BigInteger apply(BigInteger... args) {
			if ( args.length != 2 )
				return null;
		
			BigInteger result = args[0].subtract( args[1] );
			
			return result;
		}

		public Double apply(Double... args) {
			if ( args.length != 2 )
				return null;
		
			return args[0] - args[1];
		}

		public Float apply(Float... args) {
			if ( args.length != 2 )
				return null;
		
			return args[0] - args[1];
		}	
	}
	
	private static class Tan implements NumericFunction {

		public Double apply(BigDecimal... args) {
			if ( args.length != 1 )
				return null;
			
			return apply( args[0].doubleValue() );
		}

		public Double apply(BigInteger... args) {
			if ( args.length != 1 )
				return null;
			
			return apply( args[0].doubleValue() );
		}

		public Double apply(Double... args) {
			if ( args.length != 1 )
				return null;
			
			return Math.tan( args[0] );
		}

		public Double apply(Float... args) {
			if ( args.length != 1 )
				return null;
			
			return apply( args[0].doubleValue() );
		}	
	}
	
	private static class UnaryMinus implements NumericFunction {

		public BigDecimal apply(BigDecimal... args) {
			if ( args.length != 1 )
				return null;
			return args[0].negate();
		}

		public BigInteger apply(BigInteger... args) {
			if ( args.length != 1 )
				return null;
			return args[0].negate();
		}

		public Double apply(Double... args) {
			if ( args.length != 1 )
				return null;
			return 0.0 - args[0];
		}

		public Float apply(Float... args) {
			if ( args.length != 1 )
				return null;
			return 0.0f - args[0];
		}	
	}
	
	private static class UnaryPlus implements NumericFunction {

		public BigDecimal apply(BigDecimal... args) {
			if ( args.length != 1 )
				return null;
			return args[0];
		}

		public BigInteger apply(BigInteger... args) {
			if ( args.length != 1 )
				return null;
			return args[0];
		}

		public Double apply(Double... args) {
			if ( args.length != 1 )
				return null;
			return args[0];
		}

		public Float apply(Float... args) {
			if ( args.length != 1 )
				return null;
			return args[0];
		}
	}
	
	public final static NumericFunction abs = new Abs();
	public final static NumericFunction add = new Add();
	public final static NumericFunction ceiling = new Ceiling();
	public final static NumericFunction cos = new Cos();
	public final static NumericFunction divide = new Divide();
	public final static NumericFunction floor = new Floor();
	public final static NumericFunction integerDivide = new IntegerDivide();
	public final static NumericFunction mod = new Mod();
	public final static NumericFunction multiply = new Multiply();
	public final static NumericFunction pow = new Pow();
	public final static NumericFunction round = new Round();
	public final static NumericFunction roundHalfToEven = new RoundHalfToEven();
	public final static NumericFunction sin = new Sin();
	public final static NumericFunction subtract = new Subtract();
	public final static NumericFunction tan = new Tan();
	public final static NumericFunction unaryMinus = new UnaryMinus();
	public final static NumericFunction unaryPlus = new UnaryPlus();
	
	private final static BigInteger BIGINTMAX = new BigInteger( "" + Integer.MAX_VALUE );
	private final static BigInteger BIGINTMIN = new BigInteger( "" + Integer.MIN_VALUE );
}
