package com.clarkparsia.pellet.datatypes.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;

import org.junit.Test;

import com.clarkparsia.pellet.datatypes.IntervalRelations;
import com.clarkparsia.pellet.datatypes.OWLRealInterval;
import com.clarkparsia.pellet.datatypes.OWLRealInterval.LineType;

/**
 * <p>
 * Title: <code>owl:real</code> Interval Tests
 * </p>
 * <p>
 * Description: Unit tests for {@link OWLRealInterval}.
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
public class OWLRealIntervalTests {

	public static OWLRealInterval interval(Double l, Double u, boolean il, boolean iu,
			OWLRealInterval.LineType t) {
		return new OWLRealInterval( l == null
			? null
			: BigDecimal.valueOf( l ), u == null
			? null
			: BigDecimal.valueOf( u ), il, iu, t );
	}

	public static OWLRealInterval interval(Integer l, Integer u, boolean il, boolean iu,
			OWLRealInterval.LineType t) {
		return new OWLRealInterval( l, u, il, iu, t );
	}

	/**
	 * Verify that exclusive endpoints are not contained within the interval.
	 * For continuous intervals.
	 */
	@Test
	public void exclusiveEndpointsCon() {
		final OWLRealInterval interval = interval( -1.3, 2.5, false, false, LineType.CONTINUOUS );
		assertFalse( interval.contains( BigDecimal.valueOf( -1.3d ) ) );
		assertFalse( interval.contains( BigDecimal.valueOf( 2.5d ) ) );
		assertTrue( interval.contains( 0 ) );
	}

	/**
	 * Verify that exclusive endpoints are not contained within the interval.
	 * For no integer intervals with non-integer bounds.
	 */
	@Test
	public void exclusiveEndpointsNoI1() {
		final OWLRealInterval interval = interval( -1.3, 2.5, false, false,
				LineType.INTEGER_EXCLUDED );
		assertFalse( interval.contains( BigDecimal.valueOf( -1.3d ) ) );
		assertFalse( interval.contains( BigDecimal.valueOf( 2.5d ) ) );
		assertFalse( interval.contains( 0 ) );
		assertTrue( interval.contains( BigDecimal.valueOf( 0.1d ) ) );
	}

	/**
	 * Verify that exclusive endpoints are not contained within the interval.
	 * For no integer intervals with integer bounds.
	 */
	@Test
	public void exclusiveEndpointsNoI2() {
		final OWLRealInterval interval = interval( -1, 3, false, false, LineType.INTEGER_EXCLUDED );
		assertFalse( interval.contains( -1 ) );
		assertFalse( interval.contains( 3 ) );
		assertFalse( interval.contains( 0 ) );
		assertTrue( interval.contains( BigDecimal.valueOf( 0.1d ) ) );
	}

	/**
	 * Verify that exclusive endpoints are not contained within the interval.
	 * For integer only intervals with integer bounds.
	 */
	@Test
	public void exclusiveEndpointsOnly() {
		final OWLRealInterval interval = interval( -1, 3, false, false, LineType.INTEGER_ONLY );
		assertFalse( interval.contains( -1 ) );
		assertFalse( interval.contains( 3 ) );
		assertTrue( interval.contains( 0 ) );
	}

	/**
	 * Test getting a sub-interval greater than an integer on an integer line.
	 */
	@Test
	public void greaterInt1() {
		final OWLRealInterval i = interval( 1, 5, true, true, LineType.INTEGER_ONLY );
		assertEquals( interval( 3, 5, true, true, LineType.INTEGER_ONLY ), i.greater( 2 ) );
	}

	/**
	 * Test getting a sub-interval greater than the lower endpoint of an integer
	 * line.
	 */
	@Test
	public void greaterInt2() {
		final OWLRealInterval i = interval( 1, 5, true, true, LineType.INTEGER_ONLY );
		assertEquals( interval( 2, 5, true, true, LineType.INTEGER_ONLY ), i.greater( 1 ) );
	}

	/**
	 * Verify that inclusive endpoints are contained within the interval. For
	 * continuous intervals.
	 */
	@Test
	public void inclusiveEndpointsCon() {
		final OWLRealInterval interval = interval( -1.3, 2.5, true, true, LineType.CONTINUOUS );
		assertTrue( interval.contains( BigDecimal.valueOf( -1.3d ) ) );
		assertTrue( interval.contains( BigDecimal.valueOf( 2.5d ) ) );
		assertTrue( interval.contains( 0 ) );
	}

	/**
	 * Verify that inclusive endpoints are contained within the interval. For no
	 * integer intervals with non-integer bounds.
	 */
	@Test
	public void inclusiveEndpointsNoI1() {
		final OWLRealInterval interval = interval( -1.3, 2.5, true, true, LineType.INTEGER_EXCLUDED );
		assertTrue( interval.contains( BigDecimal.valueOf( -1.3d ) ) );
		assertTrue( interval.contains( BigDecimal.valueOf( 2.5d ) ) );
		assertFalse( interval.contains( 0 ) );
		assertTrue( interval.contains( BigDecimal.valueOf( 0.1d ) ) );
	}

	/**
	 * Verify that inclusive endpoints are not contained within the interval for
	 * no integer intervals with integer bounds.
	 */
	@Test
	public void inclusiveEndpointsNoI2() {
		final OWLRealInterval interval = interval( -1, 3, true, true, LineType.INTEGER_EXCLUDED );
		assertFalse( interval.contains( -1 ) );
		assertFalse( interval.contains( 3 ) );
		assertFalse( interval.contains( 0 ) );
		assertTrue( interval.contains( BigDecimal.valueOf( 0.1d ) ) );
	}

	/**
	 * Verify that inclusive endpoints are contained within the interval. For
	 * integer only intervals with integer bounds.
	 */
	@Test
	public void inclusiveEndpointsOnly() {
		final OWLRealInterval interval = interval( -1, 3, true, true, LineType.INTEGER_ONLY );
		assertTrue( interval.contains( -1 ) );
		assertTrue( interval.contains( 3 ) );
		assertTrue( interval.contains( 0 ) );
	}

	/**
	 * Verify that for all interval types, if no overlap exists between the
	 * bounds an empty intersection is identified
	 */
	@Test
	public void intersectionEmpty() {
		for( LineType t : LineType.values() ) {
			final OWLRealInterval a = interval( null, 0, false, true, t );
			final OWLRealInterval b = interval( 1, null, true, false, t );

			assertNull( a.intersection( b ) );
			assertNull( b.intersection( a ) );

			assertEquals( IntervalRelations.PRECEDES, a.compare( b ) );
			assertEquals( IntervalRelations.PRECEDED_BY, b.compare( a ) );
		}
	}

	/**
	 * Verify that if two continuous intervals meet (i.e., one's upper is the
	 * other's lower and the (in|ex)clusiveness differs), then they do not
	 * intersect.
	 */
	@Test
	public void intersectionMeetsCon() {
		final OWLRealInterval a = interval( null, 2.1, false, false, LineType.CONTINUOUS );
		final OWLRealInterval b = interval( 2.1, null, true, false, LineType.CONTINUOUS );

		assertNull( a.intersection( b ) );
		assertNull( b.intersection( a ) );

		assertEquals( IntervalRelations.MEETS, a.compare( b ) );
		assertEquals( IntervalRelations.MET_BY, b.compare( a ) );
	}

	/**
	 * Verify that if two no-integer intervals meet (i.e., one's upper is the
	 * other's lower and the (in|ex)clusiveness differs), then they do not
	 * intersect.
	 */
	@Test
	public void intersectionMeetsNoI() {
		final OWLRealInterval a = interval( null, 2.1, false, false, LineType.INTEGER_EXCLUDED );
		final OWLRealInterval b = interval( 2.1, null, true, false, LineType.INTEGER_EXCLUDED );

		assertNull( a.intersection( b ) );
		assertNull( b.intersection( a ) );

		assertEquals( IntervalRelations.MEETS, a.compare( b ) );
		assertEquals( IntervalRelations.MET_BY, b.compare( a ) );
	}

	/**
	 * Verify that the overlap of two continuous intervals with exclusive bounds
	 * intersect correctly.
	 */
	@Test
	public void intersectionOverlapConCon1() {
		final OWLRealInterval a = interval( null, 0.51, false, false, LineType.CONTINUOUS );
		final OWLRealInterval b = interval( 0.49, null, false, false, LineType.CONTINUOUS );

		final OWLRealInterval expected = interval( 0.49, 0.51, false, false, LineType.CONTINUOUS );
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
	 * Verify that the overlap of two continuous intervals with inclusive bounds
	 * intersect correctly.
	 */
	@Test
	public void intersectionOverlapConCon2() {
		final OWLRealInterval a = interval( null, 0.51, false, true, LineType.CONTINUOUS );
		final OWLRealInterval b = interval( 0.49, null, true, false, LineType.CONTINUOUS );

		final OWLRealInterval expected = interval( 0.49, 0.51, true, true, LineType.CONTINUOUS );
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
	 * Verify that two continuous intervals overlapping just on an inclusive
	 * bound intersect to a point.
	 */
	@Test
	public void intersectionPointCon() {
		final OWLRealInterval a = interval( null, 2.1, false, true, LineType.CONTINUOUS );
		final OWLRealInterval b = interval( 2.1, null, true, false, LineType.CONTINUOUS );

		final OWLRealInterval expected = new OWLRealInterval( BigDecimal.valueOf( 2.1d ) );

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
	 * Verify that two integer intervals overlapping just on an inclusive bound
	 * intersect to a point.
	 */
	@Test
	public void intersectionPointOnly() {
		final OWLRealInterval a = interval( null, 2, false, true, LineType.INTEGER_ONLY );
		final OWLRealInterval b = interval( 2, null, true, false, LineType.INTEGER_ONLY );

		final OWLRealInterval expected = new OWLRealInterval( Integer.valueOf( 2 ) );

		assertEquals( expected, a.intersection( b ) );
		assertEquals( expected, b.intersection( a ) );

		assertTrue( a.intersection( b ).isPoint() );
		assertTrue( b.intersection( a ).isPoint() );

		assertTrue( a.intersection( b ).contains( Integer.valueOf( 2 ) ) );
		assertTrue( b.intersection( a ).contains( Integer.valueOf( 2 ) ) );

		assertEquals( IntervalRelations.OVERLAPS, a.compare( b ) );
		assertEquals( IntervalRelations.OVERLAPPED_BY, b.compare( a ) );
	}

	/**
	 * Verify that the intersection of a continuous interval with a continuous
	 * interval which it starts and which share an inclusive endpoint is
	 * correct.
	 */
	@Test
	public void intersectionStartsConCon1() {
		final OWLRealInterval a = interval( 2.1, 3.1, true, true, LineType.CONTINUOUS );
		final OWLRealInterval b = interval( 2.1, null, true, false, LineType.CONTINUOUS );

		assertEquals( a, a.intersection( b ) );
		assertEquals( a, b.intersection( a ) );

		assertEquals( IntervalRelations.STARTS, a.compare( b ) );
		assertEquals( IntervalRelations.STARTED_BY, b.compare( a ) );
	}

	/**
	 * Verify that the intersection of a continuous interval with a continuous
	 * interval which it starts and which share an exclusive endpoint is
	 * correct.
	 */
	@Test
	public void intersectionStartsConCon2() {
		final OWLRealInterval a = interval( 2.1, 3.1, false, true, LineType.CONTINUOUS );
		final OWLRealInterval b = interval( 2.1, null, false, false, LineType.CONTINUOUS );

		assertEquals( a, a.intersection( b ) );
		assertEquals( a, b.intersection( a ) );

		assertEquals( IntervalRelations.STARTS, a.compare( b ) );
		assertEquals( IntervalRelations.STARTED_BY, b.compare( a ) );
	}

	/**
	 * Verify that the intersection of a continuous interval with a continuous
	 * interval which it starts but for which the endpoint type is different it
	 * correct (i.e., it isn't a start relation).
	 */
	@Test
	public void intersectionStartsConCon3() {
		final OWLRealInterval a = interval( 2.1, 3.1, false, true, LineType.CONTINUOUS );
		final OWLRealInterval b = interval( 2.1, null, true, false, LineType.CONTINUOUS );

		assertEquals( a, a.intersection( b ) );
		assertEquals( a, b.intersection( a ) );

		assertEquals( IntervalRelations.DURING, a.compare( b ) );
		assertEquals( IntervalRelations.CONTAINS, b.compare( a ) );
	}

	/**
	 * Verify that a continuous interval with unequal bounds is not a point.
	 */
	@Test
	public void isPointFalseCon() {
		final OWLRealInterval a = interval( 0.1, 0.2, true, true, LineType.CONTINUOUS );

		assertFalse( a.isPoint() );
	}

	/**
	 * Verify that an integer only interval with unequal bounds is not a point.
	 */
	@Test
	public void isPointFalseOnly() {
		final OWLRealInterval a = interval( 1, 2, true, true, LineType.INTEGER_ONLY );

		assertFalse( a.isPoint() );
	}

	/**
	 * Verify that a continuous interval with equal bounds is a point.
	 */
	@Test
	public void isPointTrueCon() {
		final OWLRealInterval a = new OWLRealInterval( BigDecimal.valueOf( 0.1d ) );

		assertTrue( a.isPoint() );

		final OWLRealInterval b = interval( 0.1, 0.1, true, true, LineType.CONTINUOUS );

		assertTrue( b.isPoint() );

		assertEquals( a, b );
	}

	/**
	 * Test getting a sub-interval less than an integer on an integer line.
	 */
	@Test
	public void lessInt1() {
		final OWLRealInterval i = interval( 1, 5, true, true, LineType.INTEGER_ONLY );
		assertEquals( interval( 1, 3, true, true, LineType.INTEGER_ONLY ), i.less( 4 ) );
	}

	/**
	 * Test getting a sub-interval less than the upper endpoint of an integer
	 * interval
	 */
	@Test
	public void lessInt2() {
		final OWLRealInterval i = interval( 1, 5, true, true, LineType.INTEGER_ONLY );
		assertEquals( interval( 1, 4, true, true, LineType.INTEGER_ONLY ), i.less( 5 ) );
	}

	@Test
	public void unboundContainsAll() {
		final OWLRealInterval interval = new OWLRealInterval( null, null, false, false,
				LineType.CONTINUOUS );

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
