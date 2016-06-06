// Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package org.mindswap.pellet.test.rules;

import static org.junit.Assert.assertEquals;

import com.clarkparsia.pellet.rules.builtins.NumericPromotion;
import com.clarkparsia.pellet.rules.builtins.NumericVisitor;
import java.math.BigDecimal;
import java.math.BigInteger;
import junit.framework.JUnit4TestAdapter;
import org.junit.Before;
import org.junit.Test;

/**
 * <p>
 * Title: Numeric Promotion Tests
 * </p>
 * <p>
 * Description:
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
public class NumericPromotionTests
{

	private NumericPromotion _promoter;

	public static junit.framework.Test suite()
	{
		return new JUnit4TestAdapter(NumericPromotionTests.class);
	}

	private static class EqualityAssertion implements NumericVisitor
	{

		private final Number[] _charge;

		public EqualityAssertion(final Number[] charge)
		{
			this._charge = charge;
		}

		private void test(final Number[] args)
		{
			assertEquals("Promoted results have wrong number of arguments", _charge.length, args.length);

			for (int i = 0; i < _charge.length; i++)
			{
				assertEquals("Promoted results have wrong class", _charge[i].getClass(), args[i].getClass());
				assertEquals("Promoted results differ in position " + i, _charge[i], args[i]);
			}
		}

		@Override
		public void visit(final BigDecimal[] args)
		{
			test(args);
		}

		@Override
		public void visit(final BigInteger[] args)
		{
			test(args);
		}

		@Override
		public void visit(final Double[] args)
		{
			test(args);
		}

		@Override
		public void visit(final Float[] args)
		{
			test(args);
		}

	}

	private void promotionTester(final NumericPromotion promoter, final Number... results)
	{
		promoter.accept(new EqualityAssertion(results));
	}

	@Before
	public void setUp()
	{
		_promoter = new NumericPromotion();
	}

	@Test
	public void byteAndShort()
	{
		final byte b = 4;
		final short s = 1000;
		final BigInteger bb = new BigInteger("" + b);
		final BigInteger bs = new BigInteger("" + s);

		_promoter.promote(b, s);
		promotionTester(_promoter, bb, bs);

		_promoter.promote(s, b);
		promotionTester(_promoter, bs, bb);
	}

	@Test
	public void longAndBigInt()
	{
		final long l = 40000;
		final BigInteger big = new BigInteger("99999999999999999999999999999999999");
		final BigInteger bigl = new BigInteger((new Long(l)).toString());

		_promoter.promote(l, big);
		promotionTester(_promoter, bigl, big);

		_promoter.promote(big, l);
		promotionTester(_promoter, big, bigl);
	}

	@Test
	public void decimalAndDouble()
	{
		final double pi = Math.PI;
		final BigDecimal pidec = new BigDecimal(pi).multiply(new BigDecimal(pi));

		_promoter.promote(pidec, pi);
		promotionTester(_promoter, pidec.doubleValue(), pi);

		_promoter.promote(pi, pidec);
		promotionTester(_promoter, pi, pidec.doubleValue());
	}

	@Test
	public void bigIntAndFloat()
	{
		final BigInteger big = new BigInteger("9999999999999999999999999999999999999999999999");
		final float fl = 9876543210.0123456789f;
		final float bigf = big.floatValue();

		_promoter.promote(big, fl);
		promotionTester(_promoter, bigf, fl);

		_promoter.promote(fl, big);
		promotionTester(_promoter, fl, bigf);
	}

}
