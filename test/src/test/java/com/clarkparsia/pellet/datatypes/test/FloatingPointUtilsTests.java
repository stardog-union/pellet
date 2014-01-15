package com.clarkparsia.pellet.datatypes.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.clarkparsia.pellet.datatypes.types.floating.FloatingPointUtils;

/**
 * <p>
 * Title: Floating Point Utilities Tests
 * </p>
 * <p>
 * Description: Unit tests for {@link FloatingPointUtils}
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
public class FloatingPointUtilsTests {

	@Test
	public void decrementNeg() {
		assertEquals( (Float) Float.intBitsToFloat( 0x80000008 ), FloatingPointUtils
				.decrement( Float.intBitsToFloat( 0x80000007 ) ) );

		assertEquals( (Double) Double.longBitsToDouble( 0x8000000000000008L ), FloatingPointUtils
				.decrement( Double.longBitsToDouble( 0x8000000000000007L ) ) );
	}

	@Test
	public void decrementNegInf() {
		assertTrue( FloatingPointUtils.decrement( Float.NEGATIVE_INFINITY ).isInfinite() );
		assertEquals( (Float) Float.NEGATIVE_INFINITY, FloatingPointUtils
				.decrement( Float.NEGATIVE_INFINITY ) );

		assertTrue( FloatingPointUtils.decrement( Double.NEGATIVE_INFINITY ).isInfinite() );
		assertEquals( (Double) Double.NEGATIVE_INFINITY, FloatingPointUtils
				.decrement( Double.NEGATIVE_INFINITY ) );
	}

	@Test
	public void decrementPos() {
		assertEquals( (Float) Float.intBitsToFloat( 0x00000006 ), FloatingPointUtils
				.decrement( Float.intBitsToFloat( 0x00000007 ) ) );

		assertEquals( (Double) Double.longBitsToDouble( 0x0000000000000006L ), FloatingPointUtils
				.decrement( Double.longBitsToDouble( 0x0000000000000007L ) ) );
	}

	@Test
	public void decrementToNegInf() {
		{
			final Float oneMore = Float.intBitsToFloat( 0xff7fffff );
			assertFalse( oneMore.isInfinite() );
			assertTrue( FloatingPointUtils.decrement( oneMore ).isInfinite() );
			assertEquals( (Float) Float.NEGATIVE_INFINITY, FloatingPointUtils.decrement( oneMore ) );
		}
		{
			final Double oneMore = Double.longBitsToDouble( 0xffefffffffffffffL );
			assertFalse( oneMore.isInfinite() );
			assertTrue( FloatingPointUtils.decrement( oneMore ).isInfinite() );
			assertEquals( (Double) Double.NEGATIVE_INFINITY, FloatingPointUtils.decrement( oneMore ) );
		}
	}

	@Test
	public void incrementNeg() {
		assertEquals( (Float) Float.intBitsToFloat( 0x80000006 ), FloatingPointUtils
				.increment( Float.intBitsToFloat( 0x80000007 ) ) );

		assertEquals( (Double) Double.longBitsToDouble( 0x8000000000000006L ), FloatingPointUtils
				.increment( Double.longBitsToDouble( 0x8000000000000007L ) ) );
	}

	@Test
	public void incrementNegZero() {
		assertNotSame( Float.valueOf( "0.0" ), Float.valueOf( "-0.0" ) );
		assertEquals( Float.valueOf( "0.0" ), FloatingPointUtils
				.increment( Float.valueOf( "-0.0" ) ) );

		assertNotSame( Double.valueOf( "0.0" ), Double.valueOf( "-0.0" ) );
		assertEquals( Double.valueOf( "0.0" ), FloatingPointUtils.increment( Double
				.valueOf( "-0.0" ) ) );
	}

	@Test
	public void incrementPos() {
		assertEquals( (Float) Float.intBitsToFloat( 0x00000008 ), FloatingPointUtils
				.increment( Float.intBitsToFloat( 0x00000007 ) ) );

		assertEquals( (Double) Double.longBitsToDouble( 0x0000000000000008L ), FloatingPointUtils
				.increment( Double.longBitsToDouble( 0x0000000000000007L ) ) );
	}

	@Test
	public void incrementPosInf() {
		assertTrue( FloatingPointUtils.increment( Float.POSITIVE_INFINITY ).isInfinite() );
		assertEquals( (Float) Float.POSITIVE_INFINITY, FloatingPointUtils
				.increment( Float.POSITIVE_INFINITY ) );

		assertTrue( FloatingPointUtils.increment( Double.POSITIVE_INFINITY ).isInfinite() );
		assertEquals( (Double) Double.POSITIVE_INFINITY, FloatingPointUtils
				.increment( Double.POSITIVE_INFINITY ) );
	}

	@Test
	public void incrementToPosInf() {
		{
			final Float oneLess = Float.intBitsToFloat( 0x7f7fffff );
			assertFalse( oneLess.isInfinite() );
			assertTrue( FloatingPointUtils.increment( oneLess ).isInfinite() );
			assertEquals( (Float) Float.POSITIVE_INFINITY, FloatingPointUtils.increment( oneLess ) );
		}
		{
			final Double oneLess = Double.longBitsToDouble( 0x7fefffffffffffffL );
			assertFalse( oneLess.isInfinite() );
			assertTrue( FloatingPointUtils.increment( oneLess ).isInfinite() );
			assertEquals( (Double) Double.POSITIVE_INFINITY, FloatingPointUtils.increment( oneLess ) );
		}
	}
}
