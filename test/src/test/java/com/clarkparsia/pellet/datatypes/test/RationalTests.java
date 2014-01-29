package com.clarkparsia.pellet.datatypes.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;

import com.clarkparsia.pellet.datatypes.OWLRealUtils;
import com.clarkparsia.pellet.datatypes.types.real.Rational;

public class RationalTests {

	private static Rational rational(String s) {
		return Rational.valueOf( s );
	}

	/**
	 * Test that the denominator cannot be a decimal value
	 */
	@Test(expected = NumberFormatException.class)
	public void invalidParseDecimalDenominator() {
		Rational.valueOf( " 1 / 3.3" );
	}

	/**
	 * Test that the numerator cannot be a decimal value
	 */
	@Test(expected = NumberFormatException.class)
	public void invalidParseDecimalNumerator() {
		Rational.valueOf( " 1.1 / 3" );
	}

	/**
	 * Test that the denominator cannot be negative
	 */
	@Test(expected = NumberFormatException.class)
	public void invalidParseNegativeDenominator() {
		Rational.valueOf( " 3 / -3" );
	}

	/**
	 * Test that the denominator cannot be zero
	 */
	@Test(expected = NumberFormatException.class)
	public void invalidParseZeroDenominator() {
		Rational.valueOf( " 3 / 0" );
	}

	/**
	 * Test the compareTo method
	 */
	@Test
	public void numericCompare() {

		assertEquals( 0, rational( "1/3" ).compareTo( rational( "17/51" ) ) );
		assertEquals( -1, rational( "1/3" ).compareTo( rational( "18/51" ) ) );
		assertEquals( 1, rational( "1/3" ).compareTo( rational( "16/51" ) ) );

		assertEquals( 0, rational( "-1/3" ).compareTo( rational( "-17/51" ) ) );
		assertEquals( 1, rational( "-1/3" ).compareTo( rational( "-18/51" ) ) );
		assertEquals( -1, rational( "-1/3" ).compareTo( rational( "-16/51" ) ) );

		assertEquals( 0, new Rational( 1, 3 ).compareTo( new Rational( 1l, 3l ) ) );
	}

	/**
	 * Test that equality is dependent on numerator and denominator, not numeric
	 * value.
	 */
	@Test
	public void objectEquals() {

		assertEquals( rational( "1/3" ), rational( "1/3" ) );

		assertFalse( rational( "1/3" ).equals( rational( "1/4" ) ) );
		assertFalse( rational( "1/3" ).equals( rational( "10/30" ) ) );
		assertFalse( rational( "1/3" ).equals( rational( "-1/3" ) ) );

		/*
		 * The following holds because !Integer(1).equals(Long(1))
		 */
		assertFalse( new Rational( 1, 3 ).equals( new Rational( 1l, 3l ) ) );
	}

	/**
	 * Test that toString matches the canonical lexical form
	 */
	@Test
	public void lexicalForm() {
		assertEquals( "1 / 3", rational( "1/3" ).toString() );
		assertEquals( "10 / 5", rational( "10/5" ).toString() );
		assertEquals( "-1 / 3", rational( "-1/3" ).toString() );
		assertEquals( "-10 / 5", rational( "-10/5" ).toString() );
	}

	/**
	 * Test the simplify method
	 */
	@Test
	public void simplification() {
		assertEquals( rational( "1/3" ), Rational.simplify( rational( "17/51" ) ) );
		assertEquals( rational( "5/1" ), Rational.simplify( rational( "65/13" ) ) );
	}

	/**
	 * Test parsing of valid values
	 */
	@Test
	public void validParse() {
		Rational r;

		r = Rational.valueOf( "1 / 3" );
		assertNotNull( r );
		assertEquals( new Rational( OWLRealUtils.getCanonicalObject( 1 ), OWLRealUtils
				.getCanonicalObject( 3 ) ), r );

		r = Rational.valueOf( "12 / 36" );
		assertNotNull( r );
		assertEquals( new Rational( OWLRealUtils.getCanonicalObject( 12 ), OWLRealUtils
				.getCanonicalObject( 36 ) ), r );

		r = Rational.valueOf( "-1 / 3" );
		assertNotNull( r );
		assertEquals( new Rational( OWLRealUtils.getCanonicalObject( -1 ), OWLRealUtils
				.getCanonicalObject( 3 ) ), r );
	}
}
