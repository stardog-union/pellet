// Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package com.clarkparsia.modularity.test;

import com.clarkparsia.modularity.AxiomBasedModuleExtractor;
import com.clarkparsia.modularity.ModuleExtractor;


/**
 * 
 * @author Evren Sirin
 */
public class AxiomBasedRandomizedModularityTest extends RandomizedModularityTest {
	/**
	 * @param path
	 */
	public AxiomBasedRandomizedModularityTest() {
		super( "test/data/modularity/" );
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ModuleExtractor createModuleExtractor() {
		return new AxiomBasedModuleExtractor();
	}
}