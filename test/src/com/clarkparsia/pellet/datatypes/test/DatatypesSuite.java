package com.clarkparsia.pellet.datatypes.test;

import junit.framework.JUnit4TestAdapter;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

/**
 * <p>
 * Title: Datatypes Test Suite
 * </p>
 * <p>
 * Description: Collection of datatype test suite
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
@RunWith(Suite.class)
@SuiteClasses( { DatatypeReasonerTests.class, FloatingPointUtilsTests.class, ContinuousRealIntervalTests.class,
                IntegerIntervalTests.class, RationalTests.class, RestrictedRealDatatypeTests.class,
                RestrictedTimelineDatatypeTests.class, DatatypeRestrictionTests.class })
public class DatatypesSuite {
	public static junit.framework.Test suite() {
		return new JUnit4TestAdapter(DatatypesSuite.class);
	}
}
