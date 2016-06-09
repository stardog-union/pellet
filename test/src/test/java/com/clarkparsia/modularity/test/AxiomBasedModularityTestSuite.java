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
		// ,AxiomBasedRandomizedIncrementalClassifierTest.class // FIXME #4
		// ,AxiomBasedRandomizedModularityTest.class// FIXME #4
})
public class AxiomBasedModularityTestSuite
{
	// Nothing to do.
}
