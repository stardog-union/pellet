package com.clarkparsia.modularity.test;

import com.clarkparsia.modularity.ModuleExtractor;
import com.clarkparsia.modularity.GraphBasedModuleExtractor;


/**
 * 
 * @author Evren Sirin
 */
public class GraphBasedRandomizedIncrementalClassifierTest extends RandomizedIncrementalClassifierTest {
	public GraphBasedRandomizedIncrementalClassifierTest() {
		super( "test/data/modularity/" );
	}

	@Override
	public ModuleExtractor createModuleExtractor() {
		return new GraphBasedModuleExtractor();
	}
}