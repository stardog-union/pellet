// Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package com.clarkparsia.modularity.test;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

import org.junit.Test;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLDeclarationAxiom;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLException;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.modularity.OntologySegmenter;

import uk.ac.manchester.cs.owlapi.modularity.ModuleType;
import uk.ac.manchester.cs.owlapi.modularity.SyntacticLocalityModuleExtractor;

import com.clarkparsia.modularity.ModularityUtils;
import com.clarkparsia.owlapiv3.OWL;
import com.clarkparsia.owlapiv3.OntologyUtils;

/**
 * 
 * @author Evren Sirin
 */
public abstract class RandomizedModularityTest extends AbstractModularityTest {

	private String	path;
	
	public RandomizedModularityTest(String path) {
		this.path = path;
		
		if( !new File( path ).exists() ) {
	        throw new RuntimeException( "Path to data files is not correct: " + path );
        }
	}

	private void modularityTest(String file) throws OWLException {
		OWLOntology ontology = OntologyUtils.loadOntology( "file:" + file, false );
		
		Set<OWLEntity> signature = new HashSet<OWLEntity>(); 
		signature.addAll( TestUtils.selectRandomElements( ontology.getClassesInSignature(), 5 ) );		
		modularityTest( ontology, signature );
		
		OWL.manager.removeOntology( ontology );
	}
	
	private void modularityTest(OWLOntology ontology, Set<OWLEntity> signature) throws OWLException {
		modularityTest( ontology, signature, ModuleType.BOT );
		modularityTest( ontology, signature, ModuleType.TOP );
//		modularityTest( ontology, signature, ModuleType.BOT_OF_TOP );
//		modularityTest( ontology, signature, ModuleType.TOP_OF_BOT );
	}
	
	private void modularityTest(OWLOntology ontology, Set<OWLEntity> signature, ModuleType moduleType) throws OWLException {
		Set<OWLAxiom> computed = ModularityUtils.extractModule( ontology, signature, moduleType );
		
		OntologySegmenter segmenter = 
			new SyntacticLocalityModuleExtractor( manager, ontology, moduleType );
		Set<OWLAxiom> expected = segmenter.extract( signature );
			
		// prune declarations to avoid mismatches related to declarations
		for( OWLEntity entity : signature ) {
			OWLDeclarationAxiom declaration = OWL.declaration( entity );
			computed.remove( declaration );
			computed.remove( declaration );
		}
		
		TestUtils.assertToStringEquals( "Modules diff for " + signature, expected.toArray( new OWLAxiom[0] ), computed.toArray( new OWLAxiom[0] ) );
	}
	
	@Test
	public void galenModularityTest() throws OWLException {
		modularityTest( path + "galen.owl" );
	}

	@Test
	public void koalaModularityTest() throws OWLException {
		modularityTest( path + "koala.owl" );
	}

	@Test
	public void sumoModularityTest() throws OWLException {
		modularityTest( path + "SUMO.owl" );
	}

	@Test
	public void sweetModularityTest() throws OWLException {
		modularityTest( path + "SWEET.owl" );
	}

	@Test
	public void wineModularityTest() throws OWLException {
		modularityTest( path + "wine.owl" );
	}
}