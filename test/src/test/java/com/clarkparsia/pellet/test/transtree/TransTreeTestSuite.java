package com.clarkparsia.pellet.test.transtree;

import junit.framework.JUnit4TestAdapter;

import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import com.clarkparsia.StableTests;


/**
 * @author Blazej Bulka
 */
@RunWith(Suite.class)
@SuiteClasses( {
	TransTreeTest.class
})
@Category(StableTests.class)
public class TransTreeTestSuite {
	public static junit.framework.Test suite() {
		return new JUnit4TestAdapter( TransTreeTestSuite.class );
	}
}
