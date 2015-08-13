package com.clarkparsia.modularity.test;

import com.clarkparsia.modularity.ModuleExtractor;
import com.clarkparsia.modularity.GraphBasedModuleExtractor;

/**
 * @author Evren Sirin
 */
public class GraphBasedModularityTest extends SimpleModularityTest {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ModuleExtractor createModuleExtractor() {
		return new GraphBasedModuleExtractor();
	}

}
