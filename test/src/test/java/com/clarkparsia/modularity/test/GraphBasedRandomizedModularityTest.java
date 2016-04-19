package com.clarkparsia.modularity.test;

import com.clarkparsia.modularity.GraphBasedModuleExtractor;
import com.clarkparsia.modularity.ModuleExtractor;

/**
 * @author Mike Smith
 */
public class GraphBasedRandomizedModularityTest extends RandomizedModularityTest
{
	public GraphBasedRandomizedModularityTest()
	{
		super("test/data/modularity/");
	}

	@Override
	public ModuleExtractor createModuleExtractor()
	{
		return new GraphBasedModuleExtractor();
	}

}
