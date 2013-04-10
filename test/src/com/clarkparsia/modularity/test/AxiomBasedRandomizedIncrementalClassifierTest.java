// Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package com.clarkparsia.modularity.test;

import org.junit.After;
import org.junit.Before;

import com.clarkparsia.modularity.AxiomBasedModuleExtractor;
import com.clarkparsia.modularity.ModuleExtractor;


/**
 * <p>
 * Title:
 * </p>
 * <p>
 * Description: Test modular classification for correctness against unified
 * classification
 * <p>
 * Copyright: Copyright (c) 2007
 * </p>
 * <p>
 * Company: Clark & Parsia, LLC. <http://www.clarkparsia.com>
 * </p>
 * 
 * @author Mike Smith
 */
public class AxiomBasedRandomizedIncrementalClassifierTest extends RandomizedIncrementalClassifierTest {
	/**
	 * @param path
	 */
	public AxiomBasedRandomizedIncrementalClassifierTest() {
		super( "test/data/modularity/" );
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ModuleExtractor createModuleExtractor() {
		return new AxiomBasedModuleExtractor();
	}
	
	@Before
	public void before() {
		// create an empty module extractor
		modExtractor = createModuleExtractor();
	}

	@After
	public void after() {
		modExtractor = null;
		if( ontology != null )
			manager.removeOntology( ontology );
	}
}