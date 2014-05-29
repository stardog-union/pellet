// Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package com.clarkparsia.modularity.test;

import static com.clarkparsia.owlapiv3.OWL.all;
import static com.clarkparsia.owlapiv3.OWL.and;
import static com.clarkparsia.owlapiv3.OWL.equivalentClasses;
import static com.clarkparsia.owlapiv3.OWL.or;
import static com.clarkparsia.owlapiv3.OWL.some;

import org.junit.Test;
import org.mindswap.pellet.utils.MultiValueMap;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLException;

import com.clarkparsia.modularity.ModuleExtractor;

/**
 * <p>
 * Title: Tests modularity results for simple hand-made ontologies.
 * </p>
 * <p>
 * Description:
 * </p>
 * <p>
 * Copyright: Copyright (c) 2007
 * </p>
 * <p>
 * Company: Clark & Parsia, LLC. <http://www.clarkparsia.com>
 * </p>
 * 
 * @author Evren Sirin
 */
public abstract class SimpleModularityTest extends AbstractModularityTest {
	private MultiValueMap<OWLEntity, OWLEntity>	modules;

	public SimpleModularityTest() {
	}
	
	public abstract ModuleExtractor createModuleExtractor();

	/**
	 * Creates an ontology from the given axioms and extracts the modules for
	 * each class in the ontology.
	 * 
	 * @param axioms that will be used to construct the ontology
	 * @throws OWLException if ontology cannot be created
	 */
	private void extractModules(OWLAxiom[] axioms) throws OWLException {
		createOntology( axioms );

		modExtractor.addOntology( ontology );

		modules = modExtractor.extractModules();
	}

	/**
	 * Tests if the computed module of the given entity is same as the 
	 * expected module.
	 *  
	 * @param entity for which the module is being tested
	 * @param expectedModule expected elements in the module
	 */
	private void testModule(OWLEntity entity, OWLEntity... expectedModule) {
		OWLEntity[] computedModule = modules.get( entity ).toArray(new OWLEntity[0]);

		String msg = "Extractor " + modExtractor.getClass().getSimpleName() + " failed for " + entity;
		TestUtils.assertToStringEquals( msg, expectedModule, computedModule );
	}

	@Test
	public void intersectionTest() throws OWLException {
		OWLAxiom[] axioms = { equivalentClasses( A, and( B, C ) ) };

		extractModules( axioms );

		testModule( A, A, B, C );
		testModule( B, B );
		testModule( C, C );
	}
	
	@Test
	public void unionTest() throws OWLException {
		OWLAxiom[] axioms = { equivalentClasses( A, or( B, C ) ) };

		extractModules( axioms );

		testModule( A, A, B, C );
		testModule( B, A, B, C );
		testModule( C, A, B, C );
	}
	
	
	@Test
	public void nestedUnionTest() throws OWLException {
		OWLAxiom[] axioms = { 
			equivalentClasses( A, and( B, or( C, D ) ) ),
			equivalentClasses( E, and( B, C ) )};

		extractModules( axioms );

		testModule( A, A, B, C, D, E );
		testModule( B, B );
		testModule( C, C );
		testModule( D, D );
		testModule( E, A, B, C, D, E );
	}
	
	@Test
	public void someValuesTest() throws OWLException {
		OWLAxiom[] axioms = { equivalentClasses( A, some( p, B ) ) };

		extractModules( axioms );

		testModule( A, A, p, B );
		testModule( B, B );
	}
	
	@Test
	public void allValuesTest() throws OWLException {
		OWLAxiom[] axioms = { equivalentClasses( A, all( p, B ) ) };

		extractModules( axioms );

		testModule( A, A, p, B );
		testModule( B, A, p, B );
	}
}