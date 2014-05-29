package com.clarkparsia.pellet.datatypes.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;

import org.junit.Test;

import com.clarkparsia.pellet.datatypes.IntervalRelations;
import com.clarkparsia.pellet.datatypes.types.real.ContinuousRealInterval;

/**
 * <p>
 * Title: Continuous <code>owl:real</code> Interval Tests
 * </p>
 * <p>
 * Description: Unit tests for {@link ContinuousRealIntervalTests}
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
public class ContinuousRealIntervalTests {

	public static ContinuousRealInterval interval(Double l, Double u, boolean il, boolean iu) {
		return new ContinuousRealInterval( l == null
			? null
			: BigDecimal.valueOf( l ), u == null
			? null
			: BigDecimal.valueOf( u ), il, iu );
	}

	/**
	 * Verify that exclusive endpoints are not contained within the interval.
	 */
	@Test
	public void exclusiveEndpoints() {
		final ContinuousRealInterval interval = interval( -1.3, 2.5, false, false );
		assertFalse( interval.contains( BigDecimal.valueOf( -1.3d ) ) );
		assertFalse( interval.contains( BigDecimal.valueOf( 2.5d ) ) );
		assertTrue( interval.contains( 0 ) );
	}

	/**
	 * Verify that inclusive endpoints are contained within the interval.
	 */
	@Test
	public void inclusiveEndpoints() {
		final ContinuousRealInterval interval = interval( -1.3, 2.5, true, true );
		assertTrue( interval.contains( BigDecimal.valueOf( -1.3d ) ) );
		assertTrue( interval.contains( BigDecimal.valueOf( 2.5d ) ) );
		assertTrue( interval.contains( 0 ) );
	}

	/**
	 * Verify that if no overlap exists between the bounds an empty intersection
	 * is identified
	 */
	@Test
	public void intersectionEmpty() {
		final ContinuousRealInterval a = interval( null, 0d, false, true );
		final ContinuousRealInterval b = interval( 1d, null, true, false );

		assertNull( a.intersection( b ) );
		assertNull( b.intersection( a ) );

		assertEquals( IntervalRelations.PRECEDES, a.compare( b ) );
		assertEquals( IntervalRelations.PRECEDED_BY, b.compare( a ) );
	}

	/**
	 * Verify that if two intervals meet (i.e., one's upper is the other's lower
	 * and the (in|ex)clusiveness differs), then they do not intersect.
	 */
	@Test
	public void intersectionMeets() {
		final ContinuousRealInterval a = interval( null, 2.1, false, false );
		final ContinuousRealInterval b = interval( 2.1, null, true, false );

		assertNull( a.intersection( b ) );
		assertNull( b.intersection( a ) );

		assertEquals( IntervalRelations.MEETS, a.compare( b ) );
		assertEquals( IntervalRelations.MET_BY, b.compare( a ) );
	}

	/**
	 * Verify that the overlap of two intervals with exclusive bounds intersect
	 * correctly.
	 */
	@Test
	public void intersectionOverlap1() {
		final ContinuousRealInterval a = interval( null, 0.51, false, false );
		final ContinuousRealInterval b = interval( 0.49, null, false, false );

		final ContinuousRealInterval expected = interval( 0.49, 0.51, false, false );
		assertEquals( expected, a.intersection( b ) );
		assertEquals( expected, b.intersection( a ) );

		assertTrue( a.intersection( b ).contains( BigDecimal.valueOf( 0.50 ) ) );
		assertTrue( b.intersection( a ).contains( BigDecimal.valueOf( 0.50 ) ) );

		assertFalse( a.intersection( b ).contains( BigDecimal.valueOf( 0.49 ) ) );
		assertFalse( b.intersection( a ).contains( BigDecimal.valueOf( 0.49 ) ) );

		assertFalse( a.intersection( b ).contains( BigDecimal.valueOf( 0.51 ) ) );
		assertFalse( b.intersection( a ).contains( BigDecimal.valueOf( 0.51 ) ) );

		assertEquals( IntervalRelations.OVERLAPS, a.compare( b ) );
		assertEquals( IntervalRelations.OVERLAPPED_BY, b.compare( a ) );
	}

	/**
	 * Verify that the overlap of two intervals with inclusive bounds intersect
	 * correctly.
	 */
	@Test
	public void intersectionOverlap2() {
		final ContinuousRealInterval a = interval( null, 0.51, false, true );
		final ContinuousRealInterval b = interval( 0.49, null, true, false );

		final ContinuousRealInterval expected = interval( 0.49, 0.51, true, true );
		assertEquals( expected, a.intersection( b ) );
		assertEquals( expected, b.intersection( a ) );

		assertTrue( a.intersection( b ).contains( BigDecimal.valueOf( 0.50 ) ) );
		assertTrue( b.intersection( a ).contains( BigDecimal.valueOf( 0.50 ) ) );

		assertTrue( a.intersection( b ).contains( BigDecimal.valueOf( 0.49 ) ) );
		assertTrue( b.intersection( a ).contains( BigDecimal.valueOf( 0.49 ) ) );

		assertTrue( a.intersection( b ).contains( BigDecimal.valueOf( 0.51 ) ) );
		assertTrue( b.intersection( a ).contains( BigDecimal.valueOf( 0.51 ) ) );

		assertEquals( IntervalRelations.OVERLAPS, a.compare( b ) );
		assertEquals( IntervalRelations.OVERLAPPED_BY, b.compare( a ) );
	}

	/**
	 * Verify that two intervals overlapping just on an inclusive bound
	 * intersect to a point.
	 */
	@Test
	public void intersectionPoint() {
		final ContinuousRealInterval a = interval( null, 2.1, false, true );
		final ContinuousRealInterval b = interval( 2.1, null, true, false );

		final ContinuousRealInterval expected = new ContinuousRealInterval( BigDecimal
				.valueOf( 2.1d ) );

		assertEquals( expected, a.intersection( b ) );
		assertEquals( expected, b.intersection( a ) );

		assertTrue( a.intersection( b ).isPoint() );
		assertTrue( b.intersection( a ).isPoint() );

		assertTrue( a.intersection( b ).contains( BigDecimal.valueOf( 2.1d ) ) );
		assertTrue( b.intersection( a ).contains( BigDecimal.valueOf( 2.1d ) ) );

		assertEquals( IntervalRelations.OVERLAPS, a.compare( b ) );
		assertEquals( IntervalRelations.OVERLAPPED_BY, b.compare( a ) );
	}

	/**
	 * Verify that the intersection of a interval with a interval which it
	 * starts and which share an inclusive endpoint is correct.
	 */
	@Test
	public void intersectionStarts1() {
		final ContinuousRealInterval a = interval( 2.1, 3.1, true, true );
		final ContinuousRealInterval b = interval( 2.1, null, true, false );

		assertEquals( a, a.intersection( b ) );
		assertEquals( a, b.intersection( a ) );

		assertEquals( IntervalRelations.STARTS, a.compare( b ) );
		assertEquals( IntervalRelations.STARTED_BY, b.compare( a ) );
	}

	/**
	 * Verify that the intersection of a interval with a interval which it
	 * starts and which share an exclusive endpoint is correct.
	 */
	@Test
	public void intersectionStarts2() {
		final ContinuousRealInterval a = interval( 2.1, 3.1, false, true );
		final ContinuousRealInterval b = interval( 2.1, null, false, false );

		assertEquals( a, a.intersection( b ) );
		assertEquals( a, b.intersection( a ) );

		assertEquals( IntervalRelations.STARTS, a.compare( b ) );
		assertEquals( IntervalRelations.STARTED_BY, b.compare( a ) );
	}

	/**
	 * Verify that the intersection of a interval with a interval which it
	 * starts but for which the endpoint type is different it correct (i.e., it
	 * isn't a start relation).
	 */
	@Test
	public void intersectionStarts3() {
		final ContinuousRealInterval a = interval( 2.1, 3.1, false, true );
		final ContinuousRealInterval b = interval( 2.1, null, true, false );

		assertEquals( a, a.intersection( b ) );
		assertEquals( a, b.intersection( a ) );

		assertEquals( IntervalRelations.DURING, a.compare( b ) );
		assertEquals( IntervalRelations.CONTAINS, b.compare( a ) );
	}

	/**
	 * Verify that a interval with unequal bounds is not a point.
	 */
	@Test
	public void isPointFalse() {
		final ContinuousRealInterval a = interval( 0.1, 0.2, true, true );

		assertFalse( a.isPoint() );
	}

	/**
	 * Verify that a interval with equal bounds is a point.
	 */
	@Test
	public void isPointTrue() {
		final ContinuousRealInterval a = new ContinuousRealInterval( BigDecimal.valueOf( 0.1d ) );

		assertTrue( a.isPoint() );

		final ContinuousRealInterval b = interval( 0.1, 0.1, true, true );

		assertTrue( b.isPoint() );

		assertEquals( a, b );
	}

	@Test
	public void unboundContainsAll() {
		final ContinuousRealInterval interval = new ContinuousRealInterval( null, null, false,
				false );

		assertFalse( interval.boundLower() );
		assertFalse( interval.boundUpper() );

		assertFalse( interval.isPoint() );

		assertTrue( interval.contains( -1 ) );
		assertTrue( interval.contains( 0 ) );
		assertTrue( interval.contains( 1 ) );
		assertTrue( interval.contains( BigDecimal.valueOf( -0.31d ) ) );
		assertTrue( interval.contains( BigDecimal.valueOf( 0.13d ) ) );
		assertTrue( interval.contains( BigDecimal.valueOf( Long.MAX_VALUE ).add(
				BigDecimal.valueOf( Long.MAX_VALUE ) ).add( BigDecimal.valueOf( 0.123d ) ) ) );
	}

}
