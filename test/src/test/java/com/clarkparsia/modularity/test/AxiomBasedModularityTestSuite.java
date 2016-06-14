package com.clarkparsia.modularity.test;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

/**
 * @author Mike Smith
 */
@RunWith(Suite.class)
@SuiteClasses({ //
		AxiomBasedIncrementalClassifierTest.class //
		, AxiomBasedModularityTest.class //
		, AxiomBasedModularityUpdateTest.class //
		, AxiomBasedRandomizedIncrementalClassifierTest.class // 
		, AxiomBasedRandomizedModularityTest.class// FIXME : this test produce an enormous output at compile time.
})
public class AxiomBasedModularityTestSuite
{
	// Nothing to do.
}
