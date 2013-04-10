// Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package org.mindswap.pellet.test.rules;

import static org.junit.Assert.assertEquals;

import java.math.BigDecimal;
import java.math.BigInteger;

import junit.framework.JUnit4TestAdapter;

import org.junit.Before;
import org.junit.Test;
import com.clarkparsia.pellet.rules.builtins.NumericPromotion;
import com.clarkparsia.pellet.rules.builtins.NumericVisitor;

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
public class NumericPromotionTests {
	
	private NumericPromotion promoter;
	
	public static junit.framework.Test suite() {
		return new JUnit4TestAdapter( NumericPromotionTests.class );
	}
	
	private static class EqualityAssertion implements NumericVisitor {

		private Number[] charge;
		
		public EqualityAssertion( Number[] charge ) {
			this.charge = charge;
		}
		
		private void test( Number[] args ) {
			assertEquals( "Promoted results have wrong number of arguments" , charge.length, args.length );
			
			for ( int i = 0; i < charge.length; i++ ) {
				assertEquals( "Promoted results have wrong class", charge[i].getClass(), args[i].getClass() );
				assertEquals( "Promoted results differ in position " + i, charge[i], args[i] );
			}
		}
		
		public void visit(BigDecimal[] args) {
			test(args);
		}

		public void visit(BigInteger[] args) {
			test(args);
		}

		public void visit(Double[] args) {
			test(args);
		}

		public void visit(Float[] args) {
			test(args);
		}
		
	}
	
	private void promotionTester( NumericPromotion promoter, Number... results ) {
		promoter.accept( new EqualityAssertion( results ) );
	}
	
	@Before
	public void setUp() {
		promoter = new NumericPromotion();
		
	}
	
	@Test public void byteAndShort() {
		byte b = 4;
		short s = 1000;
		BigInteger bb = new BigInteger( "" + b );
		BigInteger bs = new BigInteger( "" + s );
		
		promoter.promote( b, s );
		promotionTester( promoter, bb, bs );
		
		promoter.promote( s, b );
		promotionTester( promoter, bs, bb );
	}
	
	@Test public void longAndBigInt() {
		long l = 40000;
		BigInteger big = new BigInteger("99999999999999999999999999999999999");
		BigInteger bigl = new BigInteger( ( new Long( l ) ).toString() );
		
		promoter.promote( l, big);
		promotionTester( promoter, bigl, big );
		
		promoter.promote( big, l);
		promotionTester( promoter, big, bigl );
	}
	
	@Test public void decimalAndDouble() {
		double pi = Math.PI;
		BigDecimal pidec = new BigDecimal( pi ).multiply( new BigDecimal( pi ) );
		
		promoter.promote( pidec, pi);
		promotionTester( promoter, pidec.doubleValue(), pi );
		
		promoter.promote( pi, pidec);
		promotionTester( promoter, pi, pidec.doubleValue() );
	}
	
	@Test public void bigIntAndFloat() {
		BigInteger big = new BigInteger("9999999999999999999999999999999999999999999999");
		float fl = 9876543210.0123456789f;
		float bigf = big.floatValue();
		
		promoter.promote( big, fl );
		promotionTester( promoter, bigf, fl );
		
		promoter.promote( fl, big);
		promotionTester( promoter, fl, bigf );
	}
	
}
