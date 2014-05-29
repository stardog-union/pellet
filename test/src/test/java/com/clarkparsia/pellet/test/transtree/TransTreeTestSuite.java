package com.clarkparsia.pellet.test.transtree;

import junit.framework.JUnit4TestAdapter;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;


/**
 * @author Blazej Bulka
 */
@RunWith(Suite.class)
@SuiteClasses( {
	TransTreeTest.class
})
public class TransTreeTestSuite {
	public static junit.framework.Test suite() {
		return new JUnit4TestAdapter( TransTreeTestSuite.class );
	}
}
