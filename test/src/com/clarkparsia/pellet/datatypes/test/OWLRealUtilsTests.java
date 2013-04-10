package com.clarkparsia.pellet.datatypes.test;

import static com.clarkparsia.pellet.datatypes.OWLRealUtils.compare;
import static com.clarkparsia.pellet.datatypes.OWLRealUtils.getCanonicalObject;
import static com.clarkparsia.pellet.datatypes.OWLRealUtils.isInteger;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;

import org.junit.Test;

import com.clarkparsia.pellet.datatypes.OWLRealUtils;
import com.clarkparsia.pellet.datatypes.types.real.Rational;

/**
 * <p>
 * Title: owl:real Utilities Tests
 * </p>
 * <p>
 * Description: Unit tests for {@link OWLRealUtils
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
public class OWLRealUtilsTests {

	private static Number decimal(String s) {
		return new BigDecimal(s);
	}

	private static Rational rational(Number a, Number b) {
		return new Rational(a, b);
	}

	@Test
	public void compareWithRational() {

		assertTrue(compare(decimal("0.333"), rational(1, 3)) < 0);
		assertTrue(compare(decimal("0.334"), rational(1, 3)) > 0);

		assertTrue(compare(decimal("0.25"), rational(1, 4)) == 0);

		assertTrue(compare(2, rational(42, 14)) < 0);
		assertTrue(compare(3, rational(42, 14)) == 0);
		assertTrue(compare(4, rational(42, 14)) > 0);
	}

	@Test
	public void canonicalBigDecimal() {
		assertEquals(Byte.valueOf((byte) 1), getCanonicalObject(BigDecimal.valueOf(10, 1)));
		assertEquals(Byte.valueOf((byte) 1), getCanonicalObject(BigDecimal.valueOf(1, 0)));
		assertEquals(Byte.valueOf((byte) 0), getCanonicalObject(BigDecimal.valueOf(0, 0)));
		assertEquals(Byte.valueOf((byte) 0), getCanonicalObject(BigDecimal.valueOf(0, 1)));		
	}
	
	@Test
	public void isDecimalValueInteger() {
		assertTrue(isInteger(new BigDecimal("0")));
		assertTrue(isInteger(new BigDecimal("1")));
		assertTrue(isInteger(new BigDecimal("10")));
		assertTrue(isInteger(new BigDecimal("11")));
		assertTrue(isInteger(new BigDecimal("0.0")));
		assertTrue(isInteger(new BigDecimal("1.0")));
		assertTrue(isInteger(new BigDecimal("1.00")));
		assertTrue(isInteger(new BigDecimal("10.0")));
		assertTrue(isInteger(new BigDecimal("11.0")));
		assertFalse(isInteger(new BigDecimal("10.01")));
	}
}
