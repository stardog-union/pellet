package com.clarkparsia.pellet.datatypes.test;

import static com.clarkparsia.pellet.datatypes.Facet.XSD.MAX_EXCLUSIVE;
import static com.clarkparsia.pellet.datatypes.Facet.XSD.MAX_INCLUSIVE;
import static com.clarkparsia.pellet.datatypes.Facet.XSD.MIN_EXCLUSIVE;
import static com.clarkparsia.pellet.datatypes.Facet.XSD.MIN_INCLUSIVE;
import static java.util.Collections.singleton;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.Iterator;

import org.junit.Test;

import aterm.ATermAppl;

import com.clarkparsia.pellet.datatypes.Datatype;
import com.clarkparsia.pellet.datatypes.OWLRealUtils;
import com.clarkparsia.pellet.datatypes.RestrictedDatatype;
import com.clarkparsia.pellet.datatypes.exceptions.InvalidConstrainingFacetException;
import com.clarkparsia.pellet.datatypes.types.real.ContinuousRealInterval;
import com.clarkparsia.pellet.datatypes.types.real.IntegerInterval;
import com.clarkparsia.pellet.datatypes.types.real.Rational;
import com.clarkparsia.pellet.datatypes.types.real.RestrictedRealDatatype;

/**
 * <p>
 * Title: Restricted Real Datatype Tests
 * </p>
 * <p>
 * Description: Unit tests for {@link RestrictedRealDatatype}
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
public class RestrictedRealDatatypeTests {

	private final static Datatype<Number>	dt;

	static {
		dt = new Datatype<Number>() {

			public RestrictedDatatype<Number> asDataRange() {
				throw new UnsupportedOperationException();
			}

			public ATermAppl getCanonicalRepresentation(ATermAppl literal) {
				throw new UnsupportedOperationException();
			}

			public ATermAppl getLiteral(Object value) {
				throw new UnsupportedOperationException();
			}

			public ATermAppl getName() {
				throw new UnsupportedOperationException();
			}

			public Datatype<?> getPrimitiveDatatype() {
				throw new UnsupportedOperationException();
			}

			public Number getValue(ATermAppl literal) {
				throw new UnsupportedOperationException();
			}

			public boolean isPrimitive() {
				throw new UnsupportedOperationException();
			}

			@Override
			public String toString() {
				return "StubDt";
			}

		};
	}

	private static BigDecimal decimal(String s) {
		return new BigDecimal( s );
	}

	private static Rational rational(Number n, Number d) {
		return new Rational( n, d );
	}

	/**
	 * Verifies the implementation of the contains method for the full real
	 * number line. It contains all integer types and BigDecimal, but not the
	 * discrete floating point types.
	 */
	@Test
	public void containsCon() {
		RestrictedRealDatatype dr = new RestrictedRealDatatype( dt, IntegerInterval.allIntegers(),
				ContinuousRealInterval.allReals(), ContinuousRealInterval.allReals() );

		assertTrue( dr.contains( (byte) 0 ) );
		assertTrue( dr.contains( Byte.MIN_VALUE ) );
		assertTrue( dr.contains( Byte.MAX_VALUE ) );

		assertTrue( dr.contains( (short) 0 ) );
		assertTrue( dr.contains( Short.MIN_VALUE ) );
		assertTrue( dr.contains( Short.MAX_VALUE ) );

		assertTrue( dr.contains( 0 ) );
		assertTrue( dr.contains( Integer.MIN_VALUE ) );
		assertTrue( dr.contains( Integer.MAX_VALUE ) );

		assertTrue( dr.contains( 0l ) );
		assertTrue( dr.contains( Long.MIN_VALUE ) );
		assertTrue( dr.contains( Long.MAX_VALUE ) );

		assertTrue( dr.contains( BigInteger.ZERO ) );
		assertTrue( dr.contains( BigInteger.ONE ) );
		assertTrue( dr.contains( BigInteger.valueOf( Long.MAX_VALUE ).add(
				BigInteger.valueOf( Long.MAX_VALUE ) ) ) );
		assertTrue( dr.contains( BigInteger.ZERO.subtract( BigInteger.valueOf( Long.MAX_VALUE )
				.add( BigInteger.valueOf( Long.MAX_VALUE ) ) ) ) );

		assertTrue( dr.contains( BigDecimal.ZERO ) );
		assertTrue( dr.contains( BigDecimal.ONE ) );
		assertTrue( dr.contains( BigDecimal.TEN ) );
		assertTrue( dr.contains( BigDecimal.valueOf( Long.MAX_VALUE )
				.add( BigDecimal.valueOf( 1.1 ) ) ) );
		assertTrue( dr.contains( BigDecimal.valueOf( Long.MIN_VALUE ).subtract(
				BigDecimal.valueOf( 0.1 ) ) ) );

		assertFalse( dr.contains( 0f ) );
		assertFalse( dr.contains( Float.MAX_VALUE ) );
		assertFalse( dr.contains( Float.MIN_VALUE ) );

		assertFalse( dr.contains( 0d ) );
		assertFalse( dr.contains( Double.MAX_VALUE ) );
		assertFalse( dr.contains( Double.MIN_VALUE ) );

		assertFalse( dr.contains( new Object() ) );
	}

	/**
	 * Verifies the implementation of the contains method for the full integer
	 * number line. It contains all integer types and BigDecimals that are
	 * integers, but not the discrete floating point types or BigDecimals that
	 * aren't integers.
	 */
	@Test
	public void containsInt() {
		RestrictedRealDatatype dr = new RestrictedRealDatatype( dt, IntegerInterval.allIntegers(),
				null, null );

		assertTrue( dr.contains( (byte) 0 ) );
		assertTrue( dr.contains( Byte.MIN_VALUE ) );
		assertTrue( dr.contains( Byte.MAX_VALUE ) );

		assertTrue( dr.contains( (short) 0 ) );
		assertTrue( dr.contains( Short.MIN_VALUE ) );
		assertTrue( dr.contains( Short.MAX_VALUE ) );

		assertTrue( dr.contains( 0 ) );
		assertTrue( dr.contains( Integer.MIN_VALUE ) );
		assertTrue( dr.contains( Integer.MAX_VALUE ) );

		assertTrue( dr.contains( 0l ) );
		assertTrue( dr.contains( Long.MIN_VALUE ) );
		assertTrue( dr.contains( Long.MAX_VALUE ) );

		assertTrue( dr.contains( BigInteger.ZERO ) );
		assertTrue( dr.contains( BigInteger.ONE ) );
		assertTrue( dr.contains( BigInteger.valueOf( Long.MAX_VALUE ).add(
				BigInteger.valueOf( Long.MAX_VALUE ) ) ) );
		assertTrue( dr.contains( BigInteger.ZERO.subtract( BigInteger.valueOf( Long.MAX_VALUE )
				.add( BigInteger.valueOf( Long.MAX_VALUE ) ) ) ) );

		assertTrue( dr.contains( BigDecimal.ZERO ) );
		assertTrue( dr.contains( BigDecimal.ONE ) );
		assertTrue( dr.contains( BigDecimal.TEN ) );
		assertTrue( dr
				.contains( BigDecimal.valueOf( Long.MAX_VALUE ).add( BigDecimal.valueOf( 1 ) ) ) );
		assertTrue( dr.contains( BigDecimal.valueOf( Long.MIN_VALUE ).subtract(
				BigDecimal.valueOf( 2 ) ) ) );
		assertFalse( dr.contains( BigDecimal.valueOf( Long.MAX_VALUE ).add(
				BigDecimal.valueOf( 1.1 ) ) ) );
		assertFalse( dr.contains( BigDecimal.valueOf( Long.MIN_VALUE ).subtract(
				BigDecimal.valueOf( 0.1 ) ) ) );

		assertFalse( dr.contains( 0f ) );
		assertFalse( dr.contains( Float.MAX_VALUE ) );
		assertFalse( dr.contains( Float.MIN_VALUE ) );

		assertFalse( dr.contains( 0d ) );
		assertFalse( dr.contains( Double.MAX_VALUE ) );
		assertFalse( dr.contains( Double.MIN_VALUE ) );

		assertFalse( dr.contains( new Object() ) );
	}

	/**
	 * Verifies the implementation of the contains method for the full integer
	 * number line. It contains all integer types and BigDecimals that are
	 * integers, but not the discrete floating point types or BigDecimals that
	 * aren't integers.
	 */
	@Test
	public void containsNoI() {
		RestrictedDatatype<Number> dr = new RestrictedRealDatatype( dt, IntegerInterval
				.allIntegers(), null, null );
		dr = dr.intersect( new RestrictedRealDatatype( dt, IntegerInterval.allIntegers(), null,
				null ), true );

		assertFalse( dr.contains( (byte) 0 ) );
		assertFalse( dr.contains( Byte.MIN_VALUE ) );
		assertFalse( dr.contains( Byte.MAX_VALUE ) );

		assertFalse( dr.contains( (short) 0 ) );
		assertFalse( dr.contains( Short.MIN_VALUE ) );
		assertFalse( dr.contains( Short.MAX_VALUE ) );

		assertFalse( dr.contains( 0 ) );
		assertFalse( dr.contains( Integer.MIN_VALUE ) );
		assertFalse( dr.contains( Integer.MAX_VALUE ) );

		assertFalse( dr.contains( 0l ) );
		assertFalse( dr.contains( Long.MIN_VALUE ) );
		assertFalse( dr.contains( Long.MAX_VALUE ) );

		assertFalse( dr.contains( BigInteger.ZERO ) );
		assertFalse( dr.contains( BigInteger.ONE ) );
		assertFalse( dr.contains( BigInteger.valueOf( Long.MAX_VALUE ).add(
				BigInteger.valueOf( Long.MAX_VALUE ) ) ) );
		assertFalse( dr.contains( BigInteger.ZERO.subtract( BigInteger.valueOf( Long.MAX_VALUE )
				.add( BigInteger.valueOf( Long.MAX_VALUE ) ) ) ) );

		assertFalse( dr.contains( BigDecimal.ZERO ) );
		assertFalse( dr.contains( BigDecimal.ONE ) );
		assertFalse( dr.contains( BigDecimal.TEN ) );
		assertFalse( dr.contains( BigDecimal.valueOf( Long.MAX_VALUE )
				.add( BigDecimal.valueOf( 1 ) ) ) );
		assertFalse( dr.contains( BigDecimal.valueOf( Long.MIN_VALUE ).subtract(
				BigDecimal.valueOf( 2 ) ) ) );
		assertFalse( dr.contains( BigDecimal.valueOf( Long.MAX_VALUE ).add(
				BigDecimal.valueOf( 1.1 ) ) ) );
		assertFalse( dr.contains( BigDecimal.valueOf( Long.MIN_VALUE ).subtract(
				BigDecimal.valueOf( 0.1 ) ) ) );

		assertFalse( dr.contains( 0f ) );
		assertFalse( dr.contains( Float.MAX_VALUE ) );
		assertFalse( dr.contains( Float.MIN_VALUE ) );

		assertFalse( dr.contains( 0d ) );
		assertFalse( dr.contains( Double.MAX_VALUE ) );
		assertFalse( dr.contains( Double.MIN_VALUE ) );

		assertFalse( dr.contains( new Object() ) );
	}

	@Test
	public void emptyIntersectionConCon1() throws InvalidConstrainingFacetException {
		RestrictedDatatype<Number> dr1 = new RestrictedRealDatatype( dt, IntegerInterval
				.allIntegers(), ContinuousRealInterval.allReals(), ContinuousRealInterval
				.allReals() );
		dr1 = dr1.applyConstrainingFacet( MIN_INCLUSIVE.getName(), decimal( "2.5" ) );
		dr1 = dr1.applyConstrainingFacet( MAX_INCLUSIVE.getName(), decimal( "3.5" ) );

		RestrictedDatatype<Number> dr2 = new RestrictedRealDatatype( dt, IntegerInterval
				.allIntegers(), ContinuousRealInterval.allReals(), ContinuousRealInterval
				.allReals() );
		dr2 = dr2.applyConstrainingFacet( MIN_INCLUSIVE.getName(), decimal( "4.5" ) );
		dr2 = dr2.applyConstrainingFacet( MAX_INCLUSIVE.getName(), decimal( "5.5" ) );

		RestrictedDatatype<Number> dr = dr1.intersect( dr2, false );

		assertTrue( dr.isEmpty() );
		assertTrue( dr.isFinite() );
		assertTrue( dr.isEnumerable() );
		assertTrue( dr.containsAtLeast( 0 ) );
		assertFalse( dr.containsAtLeast( 1 ) );
	}

	/**
	 * Test that a continuous interval is empty if upper bound is below lower
	 * bound
	 * 
	 * @throws InvalidConstrainingFacetException
	 */
	@Test
	public void emptyRangeCon1() throws InvalidConstrainingFacetException {
		RestrictedDatatype<Number> dr = new RestrictedRealDatatype( dt, IntegerInterval
				.allIntegers(), ContinuousRealInterval.allReals(), ContinuousRealInterval
				.allReals() );
		dr = dr.applyConstrainingFacet( MAX_INCLUSIVE.getName(), decimal( "2.5" ) );
		dr = dr.applyConstrainingFacet( MIN_INCLUSIVE.getName(), decimal( "3.5" ) );

		assertFalse( dr.contains( 2 ) );
		assertFalse( dr.contains( decimal( "2.4999" ) ) );
		assertFalse( dr.contains( decimal( "2.5" ) ) );
		assertFalse( dr.contains( decimal( "2.5001" ) ) );
		assertFalse( dr.contains( 3 ) );
		assertFalse( dr.contains( decimal( "3.4999" ) ) );
		assertFalse( dr.contains( decimal( "3.5" ) ) );
		assertFalse( dr.contains( decimal( "3.5001" ) ) );
		assertFalse( dr.contains( 4 ) );

		assertTrue( dr.isEmpty() );
		assertTrue( dr.isFinite() );
		assertTrue( dr.isEnumerable() );
		assertTrue( dr.containsAtLeast( 0 ) );
		assertFalse( dr.containsAtLeast( 1 ) );
	}

	/**
	 * Test that an integer is correctly excluded from an infinite continuous
	 * interval
	 */
	@Test
	public void excludeConInt1() {
		RestrictedDatatype<Number> dr = new RestrictedRealDatatype( dt, IntegerInterval
				.allIntegers(), ContinuousRealInterval.allReals(), ContinuousRealInterval
				.allReals() );
		dr = dr.exclude( singleton( 1 ) );

		assertFalse( dr.contains( 1 ) );
		assertTrue( dr.contains( 0 ) );
		assertTrue( dr.contains( 2 ) );
		assertTrue( dr.contains( decimal( "0.99999" ) ) );
		assertTrue( dr.contains( decimal( "1.00001" ) ) );

		assertFalse( dr.isEmpty() );
		assertFalse( dr.isEnumerable() );
		assertFalse( dr.isFinite() );
	}

	/**
	 * Test that a collection of integers is correctly excluded from an infinite
	 * continuous interval
	 */
	@Test
	public void excludeConInt2() {
		RestrictedDatatype<Number> dr = new RestrictedRealDatatype( dt, IntegerInterval
				.allIntegers(), ContinuousRealInterval.allReals(), ContinuousRealInterval
				.allReals() );
		dr = dr.exclude( Arrays.asList( 1, 2, 3 ) );

		assertFalse( dr.contains( 1 ) );
		assertFalse( dr.contains( 2 ) );
		assertFalse( dr.contains( 3 ) );
		assertTrue( dr.contains( 0 ) );
		assertTrue( dr.contains( 4 ) );
		assertTrue( dr.contains( decimal( "0.5" ) ) );
		assertTrue( dr.contains( decimal( "1.5" ) ) );
		assertTrue( dr.contains( decimal( "2.5" ) ) );
		assertTrue( dr.contains( decimal( "3.5" ) ) );

		assertFalse( dr.isEmpty() );
		assertFalse( dr.isEnumerable() );
		assertFalse( dr.isFinite() );
	}

	/**
	 * Test that a non-integer is correctly excluded from an infinte continuous
	 * interval
	 */
	@Test
	public void excludeConNoI1() {
		RestrictedDatatype<Number> dr = new RestrictedRealDatatype( dt, IntegerInterval
				.allIntegers(), ContinuousRealInterval.allReals(), ContinuousRealInterval
				.allReals() );
		dr = dr.exclude( singleton( decimal( "1.1" ) ) );

		assertFalse( dr.contains( decimal( "1.1" ) ) );
		assertTrue( dr.contains( 1 ) );
		assertTrue( dr.contains( 2 ) );
		assertTrue( dr.contains( decimal( "1.09999" ) ) );
		assertTrue( dr.contains( decimal( "1.10001" ) ) );

		assertFalse( dr.isEmpty() );
		assertFalse( dr.isEnumerable() );
		assertFalse( dr.isFinite() );
	}

	/**
	 * Test that a set of non-integers is correctly excluded from an infinte
	 * continuous interval
	 */
	@Test
	public void excludeConNoI2() {
		RestrictedDatatype<Number> dr = new RestrictedRealDatatype( dt, IntegerInterval
				.allIntegers(), ContinuousRealInterval.allReals(), ContinuousRealInterval
				.allReals() );
		dr = dr.exclude( Arrays.asList( decimal( "1.1" ), decimal( "2.2" ), decimal( "3.3" ) ) );

		assertFalse( dr.contains( decimal( "1.1" ) ) );
		assertFalse( dr.contains( decimal( "2.2" ) ) );
		assertFalse( dr.contains( decimal( "3.3" ) ) );
		assertTrue( dr.contains( 2 ) );
		assertTrue( dr.contains( 3 ) );
		assertTrue( dr.contains( decimal( "1.09999" ) ) );
		assertTrue( dr.contains( decimal( "1.10001" ) ) );
		assertTrue( dr.contains( decimal( "2.19999" ) ) );
		assertTrue( dr.contains( decimal( "2.20001" ) ) );
		assertTrue( dr.contains( decimal( "3.29999" ) ) );
		assertTrue( dr.contains( decimal( "3.30001" ) ) );

		assertFalse( dr.isEmpty() );
		assertFalse( dr.isEnumerable() );
		assertFalse( dr.isFinite() );
	}

	/**
	 * Test that an integer is correctly excluded from an infinite integer
	 * interval
	 */
	@Test
	public void excludeIntInt1() {
		RestrictedDatatype<Number> dr = new RestrictedRealDatatype( dt, IntegerInterval
				.allIntegers(), null, null );
		dr = dr.exclude( singleton( 1 ) );

		assertFalse( dr.contains( 1 ) );
		assertTrue( dr.contains( 0 ) );
		assertTrue( dr.contains( 2 ) );
		assertFalse( dr.contains( decimal( "0.99999" ) ) );
		assertFalse( dr.contains( decimal( "1.00001" ) ) );

		assertFalse( dr.isEmpty() );
		assertTrue( dr.isEnumerable() );
		assertFalse( dr.isFinite() );
	}

	/**
	 * Test that a collection of integers is correctly excluded from an infinite
	 * integer interval
	 */
	@Test
	public void excludeIntInt2() {
		RestrictedDatatype<Number> dr = new RestrictedRealDatatype( dt, IntegerInterval
				.allIntegers(), null, null );
		dr = dr.exclude( Arrays.asList( 1, 2, 3 ) );

		assertFalse( dr.contains( 1 ) );
		assertFalse( dr.contains( 2 ) );
		assertFalse( dr.contains( 3 ) );
		assertTrue( dr.contains( 0 ) );
		assertTrue( dr.contains( 4 ) );
		assertFalse( dr.contains( decimal( "0.5" ) ) );
		assertFalse( dr.contains( decimal( "1.5" ) ) );
		assertFalse( dr.contains( decimal( "2.5" ) ) );
		assertFalse( dr.contains( decimal( "3.5" ) ) );

		assertFalse( dr.isEmpty() );
		assertTrue( dr.isEnumerable() );
		assertFalse( dr.isFinite() );
	}

	/**
	 * Test that an irrelevant value can be excluded
	 */
	@Test
	public void excludeIrrelevant1() {
		final String value = "A String, not a Number";
		RestrictedDatatype<Number> dr = new RestrictedRealDatatype( dt, IntegerInterval
				.allIntegers(), ContinuousRealInterval.allReals(), ContinuousRealInterval
				.allReals() );
		dr = dr.exclude( singleton( value ) );

		assertFalse( dr.contains( value ) );
		assertFalse( dr.isEmpty() );
		assertFalse( dr.isEnumerable() );
		assertFalse( dr.isFinite() );
	}

	/**
	 * Test ascending integer value enumeration for a bound interval
	 * 
	 * @throws InvalidConstrainingFacetException
	 */
	@Test
	public void integerEnumeration1() throws InvalidConstrainingFacetException {
		RestrictedDatatype<Number> dr = new RestrictedRealDatatype( dt, IntegerInterval
				.allIntegers(), null, null );
		dr = dr.applyConstrainingFacet( MIN_INCLUSIVE.getName(), -250 );
		dr = dr.applyConstrainingFacet( MAX_INCLUSIVE.getName(), 350 );

		Iterator<Number> it = dr.valueIterator();
		for( int i = -250; i <= 350; i++ ) {
			assertEquals( 0, OWLRealUtils.compare( i, it.next() ) );
		}
	}

	/**
	 * Test ascending integer value enumeration for two discontinuous intervals
	 * 
	 * @throws InvalidConstrainingFacetException
	 */
	@Test
	public void integerEnumeration2a() throws InvalidConstrainingFacetException {
		RestrictedDatatype<Number> dr = new RestrictedRealDatatype( dt, IntegerInterval
				.allIntegers(), null, null );
		dr = dr.applyConstrainingFacet( MIN_INCLUSIVE.getName(), -250 );
		dr = dr.applyConstrainingFacet( MAX_INCLUSIVE.getName(), 350 );

		RestrictedDatatype<Number> neg = new RestrictedRealDatatype( dt, IntegerInterval
				.allIntegers(), null, null );
		neg = neg.applyConstrainingFacet( MIN_INCLUSIVE.getName(), -5 );
		neg = neg.applyConstrainingFacet( MAX_INCLUSIVE.getName(), -2 );

		dr = dr.intersect( neg, true );

		Iterator<Number> it = dr.valueIterator();
		for( int i = -250; i <= 350; i++ ) {
			if( (i >= -5) && (i <= -2) )
				continue;

			assertEquals( 0, OWLRealUtils.compare( i, it.next() ) );
		}
	}

	/**
	 * Same test as {@link #integerEnumeration2a()}, but with range constructed
	 * using union, not intersection
	 * 
	 * @throws InvalidConstrainingFacetException
	 */
	@Test
	public void integerEnumeration2b() throws InvalidConstrainingFacetException {
		RestrictedDatatype<Number> dr = new RestrictedRealDatatype( dt, IntegerInterval
				.allIntegers(), null, null );
		dr = dr.applyConstrainingFacet( MIN_INCLUSIVE.getName(), -250 );
		dr = dr.applyConstrainingFacet( MAX_EXCLUSIVE.getName(), -5 );

		RestrictedDatatype<Number> b = new RestrictedRealDatatype( dt, IntegerInterval
				.allIntegers(), null, null );
		b = b.applyConstrainingFacet( MIN_EXCLUSIVE.getName(), -2 );
		b = b.applyConstrainingFacet( MAX_INCLUSIVE.getName(), 350 );

		dr = dr.union( b );

		Iterator<Number> it = dr.valueIterator();
		for( int i = -250; i <= 350; i++ ) {
			if( (i >= -5) && (i <= -2) )
				continue;

			assertEquals( 0, OWLRealUtils.compare( i, it.next() ) );
		}
	}

	/**
	 * Test that intersecting a full number line with the negation of one that
	 * only permits decimals, leaves only rationals
	 */
	@Test
	public void intersectToRationalOnly() {
		RestrictedDatatype<Number> dr = new RestrictedRealDatatype( dt, IntegerInterval
				.allIntegers(), ContinuousRealInterval.allReals(), ContinuousRealInterval
				.allReals() );

		assertTrue( dr.contains( 0 ) );
		assertTrue( dr.contains( decimal( "0.33" ) ) );
		assertTrue( dr.contains( rational( 1, 3 ) ) );

		dr = dr.intersect( new RestrictedRealDatatype( dt, IntegerInterval.allIntegers(),
				ContinuousRealInterval.allReals(), null ), true );

		assertFalse( dr.contains( 0 ) );
		assertFalse( dr.contains( decimal( "0.33" ) ) );
		assertTrue( dr.contains( rational( 1, 3 ) ) );
	}

	@Test
	public void maxExclusiveFacetCon1() throws InvalidConstrainingFacetException {
		RestrictedDatatype<Number> dr = new RestrictedRealDatatype( dt, IntegerInterval
				.allIntegers(), ContinuousRealInterval.allReals(), ContinuousRealInterval
				.allReals() );
		dr = dr.applyConstrainingFacet( MAX_EXCLUSIVE.getName(), decimal( "3.5" ) );

		assertTrue( dr.contains( BigDecimal.valueOf( Long.MIN_VALUE ).subtract(
				BigDecimal.valueOf( 0.1 ) ) ) );
		assertTrue( dr.contains( Long.MIN_VALUE ) );
		assertTrue( dr.contains( 0 ) );
		assertTrue( dr.contains( decimal( "3.4999" ) ) );
		assertFalse( dr.contains( decimal( "3.5" ) ) );
		assertFalse( dr.contains( decimal( "3.5001" ) ) );
		assertFalse( dr.contains( Long.MAX_VALUE ) );
		assertFalse( dr.contains( BigDecimal.valueOf( Long.MAX_VALUE ).add(
				BigDecimal.valueOf( 0.1 ) ) ) );
	}

	@Test
	public void maxExclusiveFacetInt1() throws InvalidConstrainingFacetException {
		RestrictedDatatype<Number> dr = new RestrictedRealDatatype( dt, IntegerInterval
				.allIntegers(), null, null );
		dr = dr.applyConstrainingFacet( MAX_EXCLUSIVE.getName(), 3 );

		assertFalse( dr.contains( BigDecimal.valueOf( Long.MIN_VALUE ).subtract(
				BigDecimal.valueOf( 0.1 ) ) ) );
		assertTrue( dr.contains( Long.MIN_VALUE ) );
		assertTrue( dr.contains( 0 ) );
		assertTrue( dr.contains( 2 ) );
		assertFalse( dr.contains( decimal( "2.9999" ) ) );
		assertFalse( dr.contains( 3 ) );
		assertFalse( dr.contains( decimal( "3.0001" ) ) );
		assertFalse( dr.contains( 4 ) );
		assertFalse( dr.contains( Long.MAX_VALUE ) );
		assertFalse( dr.contains( BigDecimal.valueOf( Long.MAX_VALUE ).add(
				BigDecimal.valueOf( 0.1 ) ) ) );
	}

	@Test
	public void maxExclusiveFacetNoI1() throws InvalidConstrainingFacetException {
		RestrictedDatatype<Number> dr = new RestrictedRealDatatype( dt, IntegerInterval
				.allIntegers(), ContinuousRealInterval.allReals(), ContinuousRealInterval
				.allReals() );
		dr = dr.intersect( new RestrictedRealDatatype( dt, IntegerInterval.allIntegers(), null,
				null ), true );
		dr = dr.applyConstrainingFacet( MAX_EXCLUSIVE.getName(), decimal( "3.5" ) );

		assertTrue( dr.contains( BigDecimal.valueOf( Long.MIN_VALUE ).subtract(
				BigDecimal.valueOf( 0.1 ) ) ) );
		assertFalse( dr.contains( Long.MIN_VALUE ) );
		assertFalse( dr.contains( 0 ) );
		assertFalse( dr.contains( 2 ) );
		assertTrue( dr.contains( decimal( "3.4999" ) ) );
		assertFalse( dr.contains( decimal( "3.5" ) ) );
		assertFalse( dr.contains( decimal( "3.5001" ) ) );
		assertFalse( dr.contains( Long.MAX_VALUE ) );
		assertFalse( dr.contains( BigDecimal.valueOf( Long.MAX_VALUE ).add(
				BigDecimal.valueOf( 0.1 ) ) ) );
	}

	@Test
	public void maxInclusiveFacetCon1() throws InvalidConstrainingFacetException {
		RestrictedDatatype<Number> dr = new RestrictedRealDatatype( dt, IntegerInterval
				.allIntegers(), ContinuousRealInterval.allReals(), ContinuousRealInterval
				.allReals() );
		dr = dr.applyConstrainingFacet( MAX_INCLUSIVE.getName(), decimal( "3.5" ) );

		assertTrue( dr.contains( BigDecimal.valueOf( Long.MIN_VALUE ).subtract(
				BigDecimal.valueOf( 0.1 ) ) ) );
		assertTrue( dr.contains( Long.MIN_VALUE ) );
		assertTrue( dr.contains( 0 ) );
		assertTrue( dr.contains( decimal( "3.4999" ) ) );
		assertTrue( dr.contains( decimal( "3.5" ) ) );
		assertFalse( dr.contains( decimal( "3.5001" ) ) );
		assertFalse( dr.contains( Long.MAX_VALUE ) );
		assertFalse( dr.contains( BigDecimal.valueOf( Long.MAX_VALUE ).add(
				BigDecimal.valueOf( 0.1 ) ) ) );
	}

	@Test
	public void maxInclusiveFacetInt1() throws InvalidConstrainingFacetException {
		RestrictedDatatype<Number> dr = new RestrictedRealDatatype( dt, IntegerInterval
				.allIntegers(), null, null );
		dr = dr.applyConstrainingFacet( MAX_INCLUSIVE.getName(), 3 );

		assertFalse( dr.contains( BigDecimal.valueOf( Long.MIN_VALUE ).subtract(
				BigDecimal.valueOf( 0.1 ) ) ) );
		assertTrue( dr.contains( Long.MIN_VALUE ) );
		assertTrue( dr.contains( 0 ) );
		assertTrue( dr.contains( 2 ) );
		assertFalse( dr.contains( decimal( "2.9999" ) ) );
		assertTrue( dr.contains( 3 ) );
		assertFalse( dr.contains( decimal( "3.0001" ) ) );
		assertFalse( dr.contains( 4 ) );
		assertFalse( dr.contains( Long.MAX_VALUE ) );
		assertFalse( dr.contains( BigDecimal.valueOf( Long.MAX_VALUE ).add(
				BigDecimal.valueOf( 0.1 ) ) ) );
	}

	@Test
	public void maxInclusiveFacetNoI1() throws InvalidConstrainingFacetException {
		RestrictedDatatype<Number> dr = new RestrictedRealDatatype( dt, IntegerInterval
				.allIntegers(), ContinuousRealInterval.allReals(), ContinuousRealInterval
				.allReals() );
		dr = dr.intersect( new RestrictedRealDatatype( dt, IntegerInterval.allIntegers(), null,
				null ), true );
		dr = dr.applyConstrainingFacet( MAX_INCLUSIVE.getName(), decimal( "3.5" ) );

		assertTrue( dr.contains( BigDecimal.valueOf( Long.MIN_VALUE ).subtract(
				BigDecimal.valueOf( 0.1 ) ) ) );
		assertFalse( dr.contains( Long.MIN_VALUE ) );
		assertFalse( dr.contains( 0 ) );
		assertFalse( dr.contains( 2 ) );
		assertTrue( dr.contains( decimal( "3.4999" ) ) );
		assertTrue( dr.contains( decimal( "3.5" ) ) );
		assertFalse( dr.contains( decimal( "3.5001" ) ) );
		assertFalse( dr.contains( Long.MAX_VALUE ) );
		assertFalse( dr.contains( BigDecimal.valueOf( Long.MAX_VALUE ).add(
				BigDecimal.valueOf( 0.1 ) ) ) );

	}

	@Test
	public void minExclusiveFacetCon1() throws InvalidConstrainingFacetException {
		RestrictedDatatype<Number> dr = new RestrictedRealDatatype( dt, IntegerInterval
				.allIntegers(), ContinuousRealInterval.allReals(), ContinuousRealInterval
				.allReals() );
		dr = dr.applyConstrainingFacet( MIN_EXCLUSIVE.getName(), decimal( "3.5" ) );

		assertFalse( dr.contains( BigDecimal.valueOf( Long.MIN_VALUE ).subtract(
				BigDecimal.valueOf( 0.1 ) ) ) );
		assertFalse( dr.contains( Long.MIN_VALUE ) );
		assertFalse( dr.contains( 0 ) );
		assertFalse( dr.contains( decimal( "3.4999" ) ) );
		assertFalse( dr.contains( decimal( "3.5" ) ) );
		assertTrue( dr.contains( decimal( "3.5001" ) ) );
		assertTrue( dr.contains( Long.MAX_VALUE ) );
		assertTrue( dr.contains( BigDecimal.valueOf( Long.MAX_VALUE )
				.add( BigDecimal.valueOf( 0.1 ) ) ) );
	}

	@Test
	public void minExclusiveFacetInt1() throws InvalidConstrainingFacetException {
		RestrictedDatatype<Number> dr = new RestrictedRealDatatype( dt, IntegerInterval
				.allIntegers(), null, null );
		dr = dr.applyConstrainingFacet( MIN_EXCLUSIVE.getName(), 3 );

		assertFalse( dr.contains( BigDecimal.valueOf( Long.MIN_VALUE ).subtract(
				BigDecimal.valueOf( 0.1 ) ) ) );
		assertFalse( dr.contains( Long.MIN_VALUE ) );
		assertFalse( dr.contains( 0 ) );
		assertFalse( dr.contains( 2 ) );
		assertFalse( dr.contains( decimal( "2.9999" ) ) );
		assertFalse( dr.contains( 3 ) );
		assertFalse( dr.contains( decimal( "3.0001" ) ) );
		assertTrue( dr.contains( 4 ) );
		assertTrue( dr.contains( Long.MAX_VALUE ) );
		assertFalse( dr.contains( BigDecimal.valueOf( Long.MAX_VALUE ).add(
				BigDecimal.valueOf( 0.1 ) ) ) );
	}

	@Test
	public void minExclusiveFacetNoI1() throws InvalidConstrainingFacetException {
		RestrictedDatatype<Number> dr = new RestrictedRealDatatype( dt, IntegerInterval
				.allIntegers(), ContinuousRealInterval.allReals(), ContinuousRealInterval
				.allReals() );
		dr = dr.intersect( new RestrictedRealDatatype( dt, IntegerInterval.allIntegers(), null,
				null ), true );
		dr = dr.applyConstrainingFacet( MIN_EXCLUSIVE.getName(), decimal( "3.5" ) );

		assertFalse( dr.contains( BigDecimal.valueOf( Long.MIN_VALUE ).subtract(
				BigDecimal.valueOf( 0.1 ) ) ) );
		assertFalse( dr.contains( Long.MIN_VALUE ) );
		assertFalse( dr.contains( 0 ) );
		assertFalse( dr.contains( 2 ) );
		assertFalse( dr.contains( decimal( "3.4999" ) ) );
		assertFalse( dr.contains( decimal( "3.5" ) ) );
		assertTrue( dr.contains( decimal( "3.5001" ) ) );
		assertFalse( dr.contains( Long.MAX_VALUE ) );
		assertTrue( dr.contains( BigDecimal.valueOf( Long.MAX_VALUE )
				.add( BigDecimal.valueOf( 0.1 ) ) ) );

	}

	@Test
	public void minInclusiveFacetCon1() throws InvalidConstrainingFacetException {
		RestrictedDatatype<Number> dr = new RestrictedRealDatatype( dt, IntegerInterval
				.allIntegers(), ContinuousRealInterval.allReals(), ContinuousRealInterval
				.allReals() );
		dr = dr.applyConstrainingFacet( MIN_INCLUSIVE.getName(), decimal( "3.5" ) );

		assertFalse( dr.contains( BigDecimal.valueOf( Long.MIN_VALUE ).subtract(
				BigDecimal.valueOf( 0.1 ) ) ) );
		assertFalse( dr.contains( Long.MIN_VALUE ) );
		assertFalse( dr.contains( 0 ) );
		assertFalse( dr.contains( decimal( "3.4999" ) ) );
		assertTrue( dr.contains( decimal( "3.5" ) ) );
		assertTrue( dr.contains( decimal( "3.5001" ) ) );
		assertTrue( dr.contains( Long.MAX_VALUE ) );
		assertTrue( dr.contains( BigDecimal.valueOf( Long.MAX_VALUE )
				.add( BigDecimal.valueOf( 0.1 ) ) ) );
	}

	@Test
	public void minInclusiveFacetInt1() throws InvalidConstrainingFacetException {
		RestrictedDatatype<Number> dr = new RestrictedRealDatatype( dt, IntegerInterval
				.allIntegers(), null, null );
		dr = dr.applyConstrainingFacet( MIN_INCLUSIVE.getName(), 3 );

		assertFalse( dr.contains( BigDecimal.valueOf( Long.MIN_VALUE ).subtract(
				BigDecimal.valueOf( 0.1 ) ) ) );
		assertFalse( dr.contains( Long.MIN_VALUE ) );
		assertFalse( dr.contains( 0 ) );
		assertFalse( dr.contains( 2 ) );
		assertFalse( dr.contains( decimal( "2.9999" ) ) );
		assertTrue( dr.contains( 3 ) );
		assertFalse( dr.contains( decimal( "3.0001" ) ) );
		assertTrue( dr.contains( 4 ) );
		assertTrue( dr.contains( Long.MAX_VALUE ) );
		assertFalse( dr.contains( BigDecimal.valueOf( Long.MAX_VALUE ).add(
				BigDecimal.valueOf( 0.1 ) ) ) );
	}

	@Test
	public void minInclusiveFacetNoI1() throws InvalidConstrainingFacetException {
		RestrictedDatatype<Number> dr = new RestrictedRealDatatype( dt, IntegerInterval
				.allIntegers(), ContinuousRealInterval.allReals(), ContinuousRealInterval
				.allReals() );
		dr = dr.intersect( new RestrictedRealDatatype( dt, IntegerInterval.allIntegers(), null,
				null ), true );
		dr = dr.applyConstrainingFacet( MIN_INCLUSIVE.getName(), decimal( "3.5" ) );

		assertFalse( dr.contains( BigDecimal.valueOf( Long.MIN_VALUE ).subtract(
				BigDecimal.valueOf( 0.1 ) ) ) );
		assertFalse( dr.contains( Long.MIN_VALUE ) );
		assertFalse( dr.contains( 0 ) );
		assertFalse( dr.contains( 2 ) );
		assertFalse( dr.contains( decimal( "3.4999" ) ) );
		assertTrue( dr.contains( decimal( "3.5" ) ) );
		assertTrue( dr.contains( decimal( "3.5001" ) ) );
		assertFalse( dr.contains( Long.MAX_VALUE ) );
		assertTrue( dr.contains( BigDecimal.valueOf( Long.MAX_VALUE )
				.add( BigDecimal.valueOf( 0.1 ) ) ) );

	}

	@Test
	public void minMaxIncIncCon1() throws InvalidConstrainingFacetException {
		RestrictedDatatype<Number> dr = new RestrictedRealDatatype( dt, IntegerInterval
				.allIntegers(), ContinuousRealInterval.allReals(), ContinuousRealInterval
				.allReals() );
		dr = dr.applyConstrainingFacet( MIN_INCLUSIVE.getName(), decimal( "2.5" ) );
		dr = dr.applyConstrainingFacet( MAX_INCLUSIVE.getName(), decimal( "3.5" ) );

		assertFalse( dr.contains( 2 ) );
		assertFalse( dr.contains( decimal( "2.4999" ) ) );
		assertTrue( dr.contains( decimal( "2.5" ) ) );
		assertTrue( dr.contains( decimal( "2.5001" ) ) );
		assertTrue( dr.contains( 3 ) );
		assertTrue( dr.contains( decimal( "3.4999" ) ) );
		assertTrue( dr.contains( decimal( "3.5" ) ) );
		assertFalse( dr.contains( decimal( "3.5001" ) ) );
		assertFalse( dr.contains( 4 ) );
	}

	@Test
	public void minMaxIncIncInt1() throws InvalidConstrainingFacetException {
		RestrictedDatatype<Number> dr = new RestrictedRealDatatype( dt, IntegerInterval
				.allIntegers(), null, null );
		dr = dr.applyConstrainingFacet( MIN_INCLUSIVE.getName(), 25 );
		dr = dr.applyConstrainingFacet( MAX_INCLUSIVE.getName(), 35 );

		assertFalse( dr.contains( 24 ) );
		assertFalse( dr.contains( decimal( "24.999" ) ) );
		assertTrue( dr.contains( 25 ) );
		assertFalse( dr.contains( decimal( "25.001" ) ) );
		assertTrue( dr.contains( 30 ) );
		assertFalse( dr.contains( decimal( "34.999" ) ) );
		assertTrue( dr.contains( 35 ) );
		assertFalse( dr.contains( decimal( "3.5001" ) ) );
		assertFalse( dr.contains( 36 ) );
	}

	@Test
	public void minMaxIncIncNoI1() throws InvalidConstrainingFacetException {
		RestrictedDatatype<Number> dr = new RestrictedRealDatatype( dt, IntegerInterval
				.allIntegers(), ContinuousRealInterval.allReals(), ContinuousRealInterval
				.allReals() );
		dr = dr.intersect( new RestrictedRealDatatype( dt, IntegerInterval.allIntegers(), null,
				null ), true );
		dr = dr.applyConstrainingFacet( MIN_INCLUSIVE.getName(), decimal( "2.5" ) );
		dr = dr.applyConstrainingFacet( MAX_INCLUSIVE.getName(), decimal( "3.5" ) );

		assertFalse( dr.contains( 2 ) );
		assertFalse( dr.contains( decimal( "2.4999" ) ) );
		assertTrue( dr.contains( decimal( "2.5" ) ) );
		assertTrue( dr.contains( decimal( "2.5001" ) ) );
		assertFalse( dr.contains( 3 ) );
		assertTrue( dr.contains( decimal( "3.4999" ) ) );
		assertTrue( dr.contains( decimal( "3.5" ) ) );
		assertFalse( dr.contains( decimal( "3.5001" ) ) );
		assertFalse( dr.contains( 4 ) );
	}

	/**
	 * Test union of two non-overlapping integer data ranges
	 */
	@Test
	public void unionIntInt1() throws InvalidConstrainingFacetException {
		RestrictedDatatype<Number> dr1 = new RestrictedRealDatatype( dt, IntegerInterval
				.allIntegers(), null, null );
		dr1 = dr1.applyConstrainingFacet( MIN_INCLUSIVE.getName(), -10 );
		dr1 = dr1.applyConstrainingFacet( MAX_INCLUSIVE.getName(), -2 );

		RestrictedDatatype<Number> dr2 = new RestrictedRealDatatype( dt, IntegerInterval
				.allIntegers(), null, null );
		dr2 = dr2.applyConstrainingFacet( MIN_INCLUSIVE.getName(), 12 );
		dr2 = dr2.applyConstrainingFacet( MAX_INCLUSIVE.getName(), 13 );

		RestrictedDatatype<Number> dr = dr1.union( dr2 );

		assertFalse( dr.isEmpty() );
		assertTrue( dr.isFinite() );
		assertTrue( dr.isEnumerable() );

		Iterator<Number> it = dr.valueIterator();
		for( int i = -10; i <= 13; i++ ) {
			if( (i > -2) && (i < 12) )
				continue;

			assertEquals( 0, OWLRealUtils.compare( i, it.next() ) );
		}

		assertFalse( dr.contains( decimal( "-10.1" ) ) );
		assertFalse( dr.contains( decimal( "-2.1" ) ) );
		assertFalse( dr.contains( decimal( "0.1" ) ) );
		assertFalse( dr.contains( decimal( "12.5" ) ) );
		assertFalse( dr.contains( decimal( "13.1" ) ) );
	}
}
