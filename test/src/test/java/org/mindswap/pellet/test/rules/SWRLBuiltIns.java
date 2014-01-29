// Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public
// License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of
// proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package org.mindswap.pellet.test.rules;

import junit.framework.JUnit4TestAdapter;

import org.junit.BeforeClass;
import org.junit.Test;
import org.mindswap.pellet.test.PelletTestSuite;

/**
 * <p>
 * Title: SWRLBuiltIns
 * </p>
 * <p>
 * Description: Perform tests for (implemented) SWRL built-ins written in
 * RDF/XML for both Jena and OWL API
 * </p>
 * <p>
 * Copyright: Copyright (c) 2008
 * </p>
 * <p>
 * Company: Clark & Parsia, LLC. <http://www.clarkparsia.com>
 * </p>
 * 
 * @author Markus Stocker
 */
public class SWRLBuiltIns extends SWRLAbstract {

	public static junit.framework.Test suite() {
		return new JUnit4TestAdapter( SWRLBuiltIns.class );
	}

	@BeforeClass
	public static void setUp() {
		base = PelletTestSuite.base + "swrl-builtIns/";
	}

	@Test
	public void equal() throws Exception {
		test( "equal" );
	}

	@Test
	public void notEqual() throws Exception {
		test( "notEqual" );
	}

	@Test
	public void lessThan() throws Exception {
		test( "lessThan" );
	}

	@Test
	public void lessThanOrEqual() throws Exception {
		test( "lessThanOrEqual" );
	}

	@Test
	public void greaterThan() throws Exception {
		test( "greaterThan" );
	}

	@Test
	public void greaterThanOrEqual() throws Exception {
		test( "greaterThanOrEqual" );
	}

	@Test
	public void add() throws Exception {
		test( "add" );
	}

	@Test
	public void subtract() throws Exception {
		test( "subtract" );
	}

	@Test
	public void multiply() throws Exception {
		test( "multiply" );
	}

	@Test
	public void divide() throws Exception {
		test( "divide" );
	}

	@Test
	public void integerDivide() throws Exception {
		test( "integerDivide" );
	}

	@Test
	public void mod() throws Exception {
		test( "mod" );
	}

	@Test
	public void pow() throws Exception {
		test( "pow" );
	}

	@Test
	public void unaryPlus() throws Exception {
		test( "unaryPlus" );
	}

	@Test
	public void unaryMinus() throws Exception {
		test( "unaryMinus" );
	}

	@Test
	public void abs() throws Exception {
		test( "abs" );
	}

	@Test
	public void ceiling() throws Exception {
		test( "ceiling" );
	}

	@Test
	public void floor() throws Exception {
		test( "floor" );
	}

	@Test
	public void round() throws Exception {
		test( "round" );
	}

	@Test
	public void roundHalfToEven() throws Exception {
		test( "roundHalfToEven" );
	}

	@Test
	public void sin() throws Exception {
		test( "sin" );
	}

	@Test
	public void cos() throws Exception {
		test( "cos" );
	}

	@Test
	public void tan() throws Exception {
		test( "tan" );
	}

	@Test
	public void booleanNot() throws Exception {
		test( "booleanNot" );
	}

	@Test
	public void stringEqualIgnoreCase() throws Exception {
		test( "stringEqualIgnoreCase" );
	}

	@Test
	public void stringConcat() throws Exception {
		test( "stringConcat" );
	}

	@Test
	public void substring() throws Exception {
		test( "substring" );
	}

	@Test
	public void stringLength() throws Exception {
		test( "stringLength" );
	}

	@Test
	public void normalizeSpace() throws Exception {
		test( "normalizeSpace" );
	}

	@Test
	public void upperCase() throws Exception {
		test( "upperCase" );
	}

	@Test
	public void lowerCase() throws Exception {
		test( "lowerCase" );
	}

	@Test
	public void translate() throws Exception {
		test( "translate" );
	}

	@Test
	public void contains() throws Exception {
		test( "contains" );
	}

	@Test
	public void containsIgnoreCase() throws Exception {
		test( "containsIgnoreCase" );
	}

	@Test
	public void startsWith() throws Exception {
		test( "startsWith" );
	}

	@Test
	public void endsWith() throws Exception {
		test( "endsWith" );
	}

	@Test
	public void substringBefore() throws Exception {
		test( "substringBefore" );
	}

	@Test
	public void substringAfter() throws Exception {
		test( "substringAfter" );
	}

	@Test
	public void matches() throws Exception {
		test( "matches" );
	}

	@Test
	public void replace() throws Exception {
		test( "replace" );
	}

	@Test
	public void tokenize() throws Exception {
		test( "tokenize" );
	}

	@Test
	public void yearMonthDuration() throws Exception {
		test( "yearMonthDuration" );
	}

	@Test
	public void dayTimeDuration() throws Exception {
		test( "dayTimeDuration" );
	}

	@Test
	public void dateTime() throws Exception {
		test( "dateTime" );
	}

	@Test
	public void date() throws Exception {
		test( "date" );
	}

	@Test
	public void time() throws Exception {
		test( "time" );
	}

	// TODO
	//addYearMonthDurations
	//subtractYearMonthDurations
	//multiplyYearMonthDuration
	//divideYearMonthDuration
	//addDayTimeDurations
	//subtractDayTimeDurations
	//multiplyDayTimeDuration
	//divideDayTimeDuration
	//subtractDates
	//subtractTimes
	//addYearMonthDurationToDateTime
	//addDayTimeDurationToDateTime
	//subtractYearMonthDurationFromDateTime
	//subtractDayTimeDurationFromDateTime
	//addYearMonthDurationToDate
	//addDayTimeDurationToDate
	//subtractYearMonthDurationFromDate
	//subtractDayTimeDurationFromDate
	//addDayTimeDurationToTime
	//subtractDayTimeDurationFromTime
	//subtractDateTimesYieldingYearMonthDuration
	//subtractDateTimesYieldingDayTimeDuration

	@Test
	public void resolveURI() throws Exception {
		test( "resolveURI" );
	}

	@Test
	public void anyURI() throws Exception {
		test( "anyURI" );
	}
}
