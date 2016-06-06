package com.clarkparsia.modularity.test;

import com.clarkparsia.modularity.GraphBasedModuleExtractor;
import com.clarkparsia.modularity.ModuleExtractor;

/**
 * @author Evren Sirin
 */
public class GraphBasedModularityTest extends SimpleModularityTest
{
	/**
	 * {@inheritDoc}
	 */
	@Override
	public ModuleExtractor createModuleExtractor()
	{
		return new GraphBasedModuleExtractor();
	}
}
