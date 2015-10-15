// Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package com.clarkparsia.modularity.test;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.clarkparsia.modularity.ModuleExtractor;
import com.google.common.base.Supplier;
import org.junit.Test;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLException;
import org.semanticweb.owlapi.model.OWLOntology;

import com.clarkparsia.owlapiv3.OWL;
import com.clarkparsia.owlapiv3.OntologyUtils;

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
public class RandomizedIncrementalClassifierTest extends AbstractModularityTest {
public RandomizedIncrementalClassifierTest(final Supplier<ModuleExtractor> theModExtractorSupplier) {
		super(theModExtractorSupplier);
	}

	private void classifyCorrectnessTest(String file) throws OWLException {
		OWLOntology ontology = OntologyUtils.loadOntology( "file:" + base + file, false );

		List<OWLAxiom> axioms = new ArrayList<OWLAxiom>( TestUtils.selectRandomAxioms( ontology, 10 ) );

		// Delete 5 axioms before the test
		OntologyUtils.removeAxioms( ontology, axioms.subList( 0, 5 ) );		
		
		// Update test will add 5 axioms and remove 5 axioms
		List<OWLAxiom> additions = axioms.subList( 0, 5 );		
		List<OWLAxiom> deletions = axioms.subList( 5, 10 );	
		try {
			TestUtils.runUpdateTest( ontology, modExtractor, additions, deletions );
		} catch( AssertionError e ) {
			System.err.println( "Additions: " + additions );
			System.err.println( "Deletions: " + deletions );
			throw e;
		} catch( RuntimeException e ) {
			System.err.println( "Additions: " + additions );
			System.err.println( "Deletions: " + deletions );
			throw e;
		}
		finally {
			OWL.manager.removeOntology( ontology );
		}
	}

	@Test
	public void galenRandomizedIncrementalClassifyTest() throws OWLException {
		classifyCorrectnessTest( "galen.owl" );
	}

	@Test
	public void koalaRandomizedIncrementalClassifyTest() throws OWLException {
		classifyCorrectnessTest( "koala.owl" );
	}

	@Test
	public void sumoRandomizedIncrementalClassifyTest() throws OWLException {
		classifyCorrectnessTest( "SUMO.owl" );
	}

	@Test
	public void sweetRandomizedIncrementalClassifyTest() throws OWLException {
		classifyCorrectnessTest( "SWEET.owl" );
	}
}