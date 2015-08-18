package com.clarkparsia.modularity.test;

import com.clarkparsia.modularity.ModuleExtractor;
import com.clarkparsia.modularity.GraphBasedModuleExtractor;

/**
 * @author Evren Sirin
 */
public class GraphBasedModularityUpdateTest extends ModularityUpdateTest {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ModuleExtractor createModuleExtractor() {
		return new GraphBasedModuleExtractor();
	}

}
