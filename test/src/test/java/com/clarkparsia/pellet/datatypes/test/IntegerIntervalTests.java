package com.clarkparsia.pellet.datatypes.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.math.BigInteger;

import org.junit.Test;

import com.clarkparsia.pellet.datatypes.types.real.IntegerInterval;

/**
 * <p>
 * Title: Integer Interval Tests
 * </p>
 * <p>
 * Description: Unit tests for {@link IntegerInterval}
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
public class IntegerIntervalTests {

	public static IntegerInterval interval(Integer l, Integer u) {
		return new IntegerInterval( l, u );
	}

	/**
	 * Test getting a sub-interval greater than an integer on an integer line.
	 */
	@Test
	public void greater1() {
		final IntegerInterval i = interval( 1, 5 );
		assertEquals( interval( 3, 5 ), i.greater( 2 ) );
	}

	/**
	 * Test getting a sub-interval greater than the lower endpoint of an integer
	 * line.
	 */
	@Test
	public void greater2() {
		final IntegerInterval i = interval( 1, 5 );
		assertEquals( interval( 2, 5 ), i.greater( 1 ) );
	}

	/**
	 * Verify that endpoints are contained within the interval.
	 */
	@Test
	public void inclusiveEndpoints() {
		final IntegerInterval interval = interval( -1, 3 );
		assertTrue( interval.contains( -1 ) );
		assertTrue( interval.contains( 3 ) );
		assertTrue( interval.contains( 0 ) );
	}

	/**
	 * Verify that if no overlap exists between the bounds an empty intersection
	 * is identified
	 */
	@Test
	public void intersectionEmpty() {
		final IntegerInterval a = interval( null, 0 );
		final IntegerInterval b = interval( 1, null );

		assertNull( a.intersection( b ) );
		assertNull( b.intersection( a ) );
	}

	/**
	 * Verify that intervals overlapping just on an inclusive bound intersect to
	 * a point.
	 */
	@Test
	public void intersectionPoint() {
		final IntegerInterval a = interval( null, 2 );
		final IntegerInterval b = interval( 2, null );

		final IntegerInterval expected = new IntegerInterval( Integer.valueOf( 2 ) );

		assertEquals( expected, a.intersection( b ) );
		assertEquals( expected, b.intersection( a ) );

		assertTrue( a.intersection( b ).contains( Integer.valueOf( 2 ) ) );
		assertTrue( b.intersection( a ).contains( Integer.valueOf( 2 ) ) );
	}

	/**
	 * Test getting a sub-interval less than an integer.
	 */
	@Test
	public void less1() {
		final IntegerInterval i = interval( 1, 5 );
		assertEquals( interval( 1, 3 ), i.less( 4 ) );
	}

	/**
	 * Test getting a sub-interval less than the upper endpoint of an interval
	 */
	@Test
	public void less2() {
		final IntegerInterval i = interval( 1, 5 );
		assertEquals( interval( 1, 4 ), i.less( 5 ) );
	}

	@Test
	public void unboundContainsAll() {
		final IntegerInterval interval = new IntegerInterval( null, null );

		assertTrue( interval.contains( -1 ) );
		assertTrue( interval.contains( 0 ) );
		assertTrue( interval.contains( 1 ) );
		assertTrue( interval.contains( Long.MAX_VALUE ) );
		assertTrue( interval.contains( BigInteger.valueOf( Long.MAX_VALUE ).multiply(
				BigInteger.TEN ) ) );
	}
}
