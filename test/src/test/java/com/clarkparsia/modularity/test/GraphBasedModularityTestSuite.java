package com.clarkparsia.modularity.test;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

/**
 * @author Mike Smith
 */
@RunWith(Suite.class)
@SuiteClasses({ GraphBasedIncrementalClassifierTest.class, GraphBasedModularityTest.class, GraphBasedModularityUpdateTest.class, GraphBasedRandomizedIncrementalClassifierTest.class, GraphBasedRandomizedModularityTest.class, GraphSimplifyTests.class, SCCTests.class })
public class GraphBasedModularityTestSuite
{

}
