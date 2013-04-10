package com.clarkparsia.pellet.datatypes.types.floating;

import java.math.BigInteger;

/**
 * <p>
 * Title: Floating Point Number Utilities
 * </p>
 * <p>
 * Description: Implementation bits to support the XSD floating point datatypes
 * </p>
 * <p>
 * Copyright: Copyright (c) 2009
 * </p>
 * <p>
 * Company: Clark & Parsia, LLC. <http://www.clarkparsia.com>
 * </p>
 * 
 * @author Mike Smith
 */
public class FloatingPointUtils {

	private static final long	DOUBLE_MAGNITUDE_MASK		= 0x7fffffffffffffffL;
	private static final long	DOUBLE_NEGATIVE_ZERO_BITS	= 0x8000000000000000L;
	private static final long	DOUBLE_POSITIVE_ZERO_BITS	= 0x0000000000000000L;
	private static final long	DOUBLE_SIGN_MASK			= 0x8000000000000000L;
	private static final int	FLOAT_MAGNITUDE_MASK		= 0x7fffffff;
	private static final int	FLOAT_NEGATIVE_ZERO_BITS	= 0x80000000;
	private static final int	FLOAT_POSITIVE_ZERO_BITS	= 0x00000000;
	private static final int	FLOAT_SIGN_MASK				= 0x80000000;

	public static Double decrement(Double n) {
		if( n.isNaN() || n.isInfinite() )
			return n;

		final long i = Double.doubleToRawLongBits( n );

		/*
		 * 0.0 decrements to -0.0
		 */
		if( i == DOUBLE_POSITIVE_ZERO_BITS )
			return Double.longBitsToDouble( DOUBLE_NEGATIVE_ZERO_BITS );
		/*
		 * Positive values decrement the bit pattern by one
		 */
		else if( (i & DOUBLE_SIGN_MASK) == 0 )
			return Double.longBitsToDouble( i - 1L );
		/*
		 * Negative values increment the bit pattern by one
		 */
		else
			return Double.longBitsToDouble( i + 1L );
	}

	public static Float decrement(Float n) {
		if( n.isNaN() || n.isInfinite() )
			return n;

		final int i = Float.floatToRawIntBits( n );

		/*
		 * 0.0 decrements to -0.0
		 */
		if( i == FLOAT_POSITIVE_ZERO_BITS )
			return Float.intBitsToFloat( FLOAT_NEGATIVE_ZERO_BITS );
		/*
		 * Positive values decrement the bit pattern by one
		 */
		else if( (i & FLOAT_SIGN_MASK) == 0 )
			return Float.intBitsToFloat( i - 1 );
		/*
		 * Negative values increment the bit pattern by one
		 */
		else
			return Float.intBitsToFloat( i + 1 );
	}

	public static Double increment(Double n) {
		if( n.isNaN() || n.isInfinite() )
			return n;

		final long i = Double.doubleToRawLongBits( n );

		/*
		 * -0.0 increments to 0.0
		 */
		if( i == DOUBLE_NEGATIVE_ZERO_BITS )
			return Double.longBitsToDouble( DOUBLE_POSITIVE_ZERO_BITS );
		/*
		 * Positive values increment the bit pattern by one
		 */
		else if( (i & DOUBLE_SIGN_MASK) == 0 )
			return Double.longBitsToDouble( i + 1L );
		/*
		 * Negative values decrement the bit pattern by one
		 */
		else
			return Double.longBitsToDouble( i - 1L );
	}

	public static Float increment(Float n) {
		if( n.isNaN() || n.isInfinite() )
			return n;

		final int i = Float.floatToRawIntBits( n );

		/*
		 * -0.0 increments to 0.0
		 */
		if( i == FLOAT_NEGATIVE_ZERO_BITS )
			return Float.intBitsToFloat( FLOAT_POSITIVE_ZERO_BITS );
		/*
		 * Positive values increment the bit pattern by one
		 */
		else if( (i & FLOAT_SIGN_MASK) == 0 )
			return Float.intBitsToFloat( i + 1 );
		/*
		 * Negative values decrement the bit pattern by one
		 */
		else
			return Float.intBitsToFloat( i - 1 );
	}

	public static BigInteger intervalSize(Double lower, Double upper) {
		if( lower.isNaN() )
			throw new IllegalArgumentException();
		if( upper.isNaN() )
			throw new IllegalArgumentException();

		final long lowerBits = Double.doubleToRawLongBits( lower );
		final long upperBits = Double.doubleToRawLongBits( upper );

		if( (DOUBLE_SIGN_MASK & lowerBits) == DOUBLE_SIGN_MASK ) {
			/*
			 * Both are negative, size is the difference
			 */
			if( (DOUBLE_SIGN_MASK & upperBits) == DOUBLE_SIGN_MASK )
				return BigInteger.valueOf( (DOUBLE_MAGNITUDE_MASK & lowerBits)
						- (DOUBLE_MAGNITUDE_MASK & upperBits) + 1L );
			/*
			 * Lower is negative, upper is positive, size is sum of patterns
			 * (plus 2 for zeros). This can possibly overflow a signed integer
			 * so all arguments are cast to longs
			 */
			else
				return BigInteger.valueOf( DOUBLE_MAGNITUDE_MASK & lowerBits ).add(
						BigInteger.valueOf( DOUBLE_MAGNITUDE_MASK & upperBits ) ).add(
						BigInteger.valueOf( 2L ) );
		}
		/*
		 * Both are positive, size is the difference
		 */
		else
			return BigInteger.valueOf( (DOUBLE_MAGNITUDE_MASK & upperBits)
					- (DOUBLE_MAGNITUDE_MASK & lowerBits) + 1L );
	}

	public static long intervalSize(Float lower, Float upper) {
		if( lower.isNaN() )
			throw new IllegalArgumentException();
		if( upper.isNaN() )
			throw new IllegalArgumentException();

		final int lowerBits = Float.floatToRawIntBits( lower );
		final int upperBits = Float.floatToRawIntBits( upper );

		if( (FLOAT_SIGN_MASK & lowerBits) == FLOAT_SIGN_MASK ) {
			/*
			 * Both are negative, size is the difference
			 */
			if( (FLOAT_SIGN_MASK & upperBits) == FLOAT_SIGN_MASK )
				return (FLOAT_MAGNITUDE_MASK & lowerBits) - (FLOAT_MAGNITUDE_MASK & upperBits) + 1;
			/*
			 * Lower is negative, upper is positive, size is sum of patterns
			 * (plus 2 for zeros). This can possibly overflow a signed integer
			 * so all arguments are cast to longs
			 */
			else
				return (long) (FLOAT_MAGNITUDE_MASK & lowerBits)
						+ (long) (FLOAT_MAGNITUDE_MASK & upperBits) + 2l;
		}
		/*
		 * Both are positive, size is the difference
		 */
		else
			return (FLOAT_MAGNITUDE_MASK & upperBits) - (FLOAT_MAGNITUDE_MASK & lowerBits) + 1;
	}

}
