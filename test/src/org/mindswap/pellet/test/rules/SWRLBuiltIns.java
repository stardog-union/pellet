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
		base = "file:" + PelletTestSuite.base + "swrl-builtIns/";
	}

	@Test
	public void equal() {
		test( "equal" );
	}

	@Test
	public void notEqual() {
		test( "notEqual" );
	}

	@Test
	public void lessThan() {
		test( "lessThan" );
	}

	@Test
	public void lessThanOrEqual() {
		test( "lessThanOrEqual" );
	}

	@Test
	public void greaterThan() {
		test( "greaterThan" );
	}

	@Test
	public void greaterThanOrEqual() {
		test( "greaterThanOrEqual" );
	}

	@Test
	public void add() {
		test( "add" );
	}

	@Test
	public void subtract() {
		test( "subtract" );
	}

	@Test
	public void multiply() {
		test( "multiply" );
	}

	@Test
	public void divide() {
		test( "divide" );
	}

	@Test
	public void integerDivide() {
		test( "integerDivide" );
	}

	@Test
	public void mod() {
		test( "mod" );
	}

	@Test
	public void pow() {
		test( "pow" );
	}

	@Test
	public void unaryPlus() {
		test( "unaryPlus" );
	}

	@Test
	public void unaryMinus() {
		test( "unaryMinus" );
	}

	@Test
	public void abs() {
		test( "abs" );
	}

	@Test
	public void ceiling() {
		test( "ceiling" );
	}

	@Test
	public void floor() {
		test( "floor" );
	}

	@Test
	public void round() {
		test( "round" );
	}

	@Test
	public void roundHalfToEven() {
		test( "roundHalfToEven" );
	}

	@Test
	public void sin() {
		test( "sin" );
	}

	@Test
	public void cos() {
		test( "cos" );
	}

	@Test
	public void tan() {
		test( "tan" );
	}

	@Test
	public void booleanNot() {
		test( "booleanNot" );
	}

	@Test
	public void stringEqualIgnoreCase() {
		test( "stringEqualIgnoreCase" );
	}

	@Test
	public void stringConcat() {
		test( "stringConcat" );
	}

	@Test
	public void substring() {
		test( "substring" );
	}

	@Test
	public void stringLength() {
		test( "stringLength" );
	}

	@Test
	public void normalizeSpace() {
		test( "normalizeSpace" );
	}

	@Test
	public void upperCase() {
		test( "upperCase" );
	}

	@Test
	public void lowerCase() {
		test( "lowerCase" );
	}

	@Test
	public void translate() {
		test( "translate" );
	}

	@Test
	public void contains() {
		test( "contains" );
	}

	@Test
	public void containsIgnoreCase() {
		test( "containsIgnoreCase" );
	}

	@Test
	public void startsWith() {
		test( "startsWith" );
	}

	@Test
	public void endsWith() {
		test( "endsWith" );
	}

	@Test
	public void substringBefore() {
		test( "substringBefore" );
	}

	@Test
	public void substringAfter() {
		test( "substringAfter" );
	}

	@Test
	public void matches() {
		test( "matches" );
	}

	@Test
	public void replace() {
		test( "replace" );
	}

	@Test
	public void tokenize() {
		test( "tokenize" );
	}

	@Test
	public void yearMonthDuration() {
		test( "yearMonthDuration" );
	}

	@Test
	public void dayTimeDuration() {
		test( "dayTimeDuration" );
	}

	@Test
	public void dateTime() {
		test( "dateTime" );
	}

	@Test
	public void date() {
		test( "date" );
	}

	@Test
	public void time() {
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
	public void resolveURI() {
		test( "resolveURI" );
	}

	@Test
	public void anyURI() {
		test( "anyURI" );
	}
}
