// Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package com.clarkparsia.modularity.test;

import static com.clarkparsia.modularity.test.TestUtils.assertClassificationEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.mindswap.pellet.test.PelletTestSuite;
import org.semanticweb.owlapi.model.AddAxiom;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.RemoveAxiom;

import com.clarkparsia.modularity.AxiomBasedModuleExtractor;
import com.clarkparsia.modularity.IncrementalClassifier;
import com.clarkparsia.modularity.ModuleExtractor;
import com.clarkparsia.modularity.PelletIncremantalReasonerFactory;
import com.clarkparsia.modularity.io.IncrementalClassifierPersistence;
import com.clarkparsia.owlapiv3.OWL;
import com.clarkparsia.owlapiv3.OntologyUtils;
import com.clarkparsia.pellet.owlapiv3.PelletReasoner;
import com.clarkparsia.pellet.owlapiv3.PelletReasonerFactory;

/**
 * <p>
 * Title:
 * </p>
 * <p>
 * Description:
 * </p>
 * <p>
 * Copyright: Copyright (c) 2009
 * </p>
 * <p>
 * Company: Clark & Parsia, LLC. <http://www.clarkparsia.com>
 * </p>
 *
 * @author Blazej Bulka
 */
public class PersistenceUpdatesTest {
	public static final String	base	= PelletTestSuite.base + "modularity/";
	
	private static final String TEST_FILE = "test-persistence-classification.zip";
		
	public ModuleExtractor createModuleExtractor() {
		return new AxiomBasedModuleExtractor();
	}
	
	public void performPersistenceRemoves(String fileName) throws IOException {
		String common = "file:"+ base + fileName;
		testPersistenceRemoves( common + ".owl");
	}

	public void performPersistenceAdds(String fileName) throws IOException {
		String common = "file:"+ base + fileName;
		testPersistenceAdds( common + ".owl");
	}

	public void performPersistenceAllowedUpdates(String fileName) throws IOException {
		String common = "file:"+ base + fileName;
		testAllowedUpdates( common + ".owl" );
	}

	public void performUpdatesAfterPersistence(String fileName) throws IOException {
		String common = "file:"+ base + fileName;
		testUpdatesAfterPersistence( common + ".owl" );
	}
	
	public void performUpdatesAfterPersistence2(String fileName) throws IOException {
		String common = "file:"+ base + fileName;
		testUpdatesAfterPersistence2( common + ".owl" );
	}
	
	public void performUpdatesWhenPersisted(String fileName) throws IOException {
		String common = "file:"+ base + fileName;
		testUpdatesWhenPersisted( common + ".owl" );
	}

	public void testPersistenceRemoves(String inputOnt) throws IOException {
		File testFile = new File( TEST_FILE );
		OWLOntology ontology = OntologyUtils.loadOntology( inputOnt );
		
		try {		
			ModuleExtractor moduleExtractor = createModuleExtractor();

			IncrementalClassifier modular = PelletIncremantalReasonerFactory.getInstance().createReasoner( ontology, moduleExtractor );
			modular.classify();

			List<OWLAxiom> axiomsToRemove = new ArrayList<OWLAxiom>( TestUtils.selectRandomAxioms( ontology, 1 ) );

			for( OWLAxiom axiomToRemove : axiomsToRemove ) {
				OWL.manager.applyChange( new RemoveAxiom(ontology, axiomToRemove ) );
			}

			// at this point there should be a change to the ontology that is not applied yet to the classifier
			// this should cause the save operation to fail

			try {
				FileOutputStream fos = new FileOutputStream( testFile );

				IncrementalClassifierPersistence.save( modular, fos );
				fail( "The incremental classifer must not allow itself to be persisted if there are any unapplied changes to the ontology" );

				fos.close();
			} catch( IllegalStateException e ) {
				assertTrue( testFile.delete() );
				// correct behavior
			}

		} finally {
			if( ontology != null ) {
				OWL.manager.removeOntology( ontology );
			}
		}
	}
	
	public void testPersistenceAdds(String inputOnt) throws IOException {
		File testFile = new File( TEST_FILE );
		OWLOntology ontology = OntologyUtils.loadOntology( inputOnt );
		
		try {		
			ModuleExtractor moduleExtractor = createModuleExtractor();

			IncrementalClassifier modular = PelletIncremantalReasonerFactory.getInstance().createReasoner( ontology, moduleExtractor );

			// first remove a random axiom
			List<OWLAxiom> axiomsToRemove = new ArrayList<OWLAxiom>( TestUtils.selectRandomAxioms( ontology, 1 ) );

			for( OWLAxiom axiomToRemove : axiomsToRemove ) {
				OWL.manager.applyChange( new RemoveAxiom(ontology, axiomToRemove ) );
			}

			// classify (i.e., update)
			modular.classify();		

			// add the axiom back but do not classify (do not cause an update)

			for( OWLAxiom axiomToAdd : axiomsToRemove ) {
				OWL.manager.applyChange( new AddAxiom(ontology, axiomToAdd ) );
			}

			// at this point there should be a change to the ontology that is not applied yet to the classifier
			// this should cause the save operation to fail

			try {
				FileOutputStream fos = new FileOutputStream( testFile );

				IncrementalClassifierPersistence.save( modular, fos );
				fail( "The incremental classifer must not allow itself to be persisted if there are any unapplied changes to the ontology" );

				fos.close();
			} catch( IllegalStateException e ) {
				assertTrue( testFile.delete() );
				// correct behavior
			} 		
			
			modular.dispose();
		} finally {
			if( ontology != null ) {
				OWL.manager.removeOntology( ontology );
			}
		}
	}
	
	public void testAllowedUpdates(String inputOnt) throws IOException {
		File testFile = new File( TEST_FILE );
		OWLOntology ontology = OntologyUtils.loadOntology( inputOnt );
		
		try {
			ModuleExtractor moduleExtractor = createModuleExtractor();

			IncrementalClassifier modular = PelletIncremantalReasonerFactory.getInstance().createReasoner( ontology, moduleExtractor );
			modular.classify();

			// first remove a random axiom
			List<OWLAxiom> axiomsToRemove = new ArrayList<OWLAxiom>( TestUtils.selectRandomAxioms( ontology, 1 ) );

			for( OWLAxiom axiomToRemove : axiomsToRemove ) {
				OWL.manager.applyChange( new RemoveAxiom(ontology, axiomToRemove ) );
			}

			// add the axiom back but do not classify		
			for( OWLAxiom axiomToAdd : axiomsToRemove ) {
				OWL.manager.applyChange( new AddAxiom(ontology, axiomToAdd ) );
			}

			// remove another random axiom
			List<OWLAxiom> axiomsToRemove2 = new ArrayList<OWLAxiom>( TestUtils.selectRandomAxioms( ontology, 1 ) );

			for( OWLAxiom axiomToRemove : axiomsToRemove2 ) {
				OWL.manager.applyChange( new RemoveAxiom(ontology, axiomToRemove ) );
			}

			// classify (i.e., update)
			modular.classify();

			// at this point, the ontology should be updated (despite the changes), and the save should succeed.		
			FileOutputStream fos = new FileOutputStream( testFile );

			IncrementalClassifierPersistence.save( modular, fos );

			fos.close();

			assertTrue( testFile.delete() );		
		} finally {
			OWL.manager.removeOntology( ontology );	
		}	
	}
	
	/**
	 * Tests whether the restored classifier can be updated.
	 * 
	 * The test creates one original classifier (modular), persists it, reads it back as another classifier (modular2).
	 * Then it performs the same modifications of the ontology on them, and checks whether their behavior is identical.
	 * 
	 * @param inputOnt
	 * @throws IOException 
	 */
	public void testUpdatesAfterPersistence(String inputOnt) throws IOException {
		File testFile = new File( TEST_FILE );
		OWLOntology ontology = OntologyUtils.loadOntology( inputOnt );
		
		try {		
			ModuleExtractor moduleExtractor = createModuleExtractor();

			IncrementalClassifier modular = PelletIncremantalReasonerFactory.getInstance().createReasoner( ontology, moduleExtractor );
			modular.classify();

			FileOutputStream fos = new FileOutputStream( testFile );

			IncrementalClassifierPersistence.save( modular, fos );

			fos.close();


			FileInputStream fis = new FileInputStream( testFile );

			modular = IncrementalClassifierPersistence.load( fis );

			fis.close();

			// first remove a random axiom
			List<OWLAxiom> axiomsToRemove = new ArrayList<OWLAxiom>( TestUtils.selectRandomAxioms( ontology, 1 ) );

			for( OWLAxiom axiomToRemove : axiomsToRemove ) {
				OWL.manager.applyChange( new RemoveAxiom( modular.getRootOntology(), axiomToRemove) );
			}

			modular.classify();

			PelletReasoner expected = PelletReasonerFactory.getInstance().createReasoner( modular.getRootOntology() );		

			assertClassificationEquals( expected, modular );
		} finally {
			OWL.manager.removeOntology( ontology );
		}
	}
	
	/**
	 * Tests whether the restored classifier can be updated.
	 * 
	 * The test creates one original classifier (modular), persists it, reads it back as another classifier (modular2).
	 * Then it performs the same modifications of the ontology on them, and checks whether their behavior is identical.
	 * 
	 * @param inputOnt
	 * @throws IOException
	 */
	public void testUpdatesAfterPersistence2(String inputOnt) throws IOException {
		File testFile = new File( TEST_FILE );
		OWLOntology ontology = OntologyUtils.loadOntology( inputOnt );
		
		try {
			IncrementalClassifier modular = PelletIncremantalReasonerFactory.getInstance().createReasoner( ontology, createModuleExtractor() );		
			modular.classify();

			FileOutputStream fos = new FileOutputStream( testFile );

			IncrementalClassifierPersistence.save( modular, fos );

			fos.close();

			FileInputStream fis = new FileInputStream( testFile );

			modular = IncrementalClassifierPersistence.load( fis, ontology );

			fis.close();

			// first remove a random axiom
			List<OWLAxiom> axiomsToRemove = new ArrayList<OWLAxiom>( TestUtils.selectRandomAxioms( ontology, 1 ) );

			for( OWLAxiom axiomToRemove : axiomsToRemove ) {
				OWL.manager.applyChange( new RemoveAxiom(ontology, axiomToRemove ) );
			}

			modular.classify();

			PelletReasoner expected = PelletReasonerFactory.getInstance().createReasoner( ontology );

			assertClassificationEquals( expected, modular );
		} finally {
			OWL.manager.removeOntology( ontology );
		}
	}
	
	public void testUpdatesWhenPersisted(String inputOnt) throws IOException {
		File testFile = new File( TEST_FILE );
		OWLOntology ontology = OntologyUtils.loadOntology( inputOnt );
		
		try {
			IncrementalClassifier modular = PelletIncremantalReasonerFactory.getInstance().createReasoner( ontology, createModuleExtractor() );		
			modular.classify();

			FileOutputStream fos = new FileOutputStream( testFile );

			IncrementalClassifierPersistence.save( modular, fos );

			fos.close();

			// perform changes while the classifier is stored on disk
			// first remove a random axiom
			List<OWLAxiom> axiomsToRemove = new ArrayList<OWLAxiom>( TestUtils.selectRandomAxioms( ontology, 1 ) );

			for( OWLAxiom axiomToRemove : axiomsToRemove ) {
				OWL.manager.applyChange( new RemoveAxiom(ontology, axiomToRemove ) );
			}

			FileInputStream fis = new FileInputStream( testFile );

			modular = IncrementalClassifierPersistence.load( fis, ontology );

			fis.close();

			PelletReasoner expected = PelletReasonerFactory.getInstance().createReasoner( ontology );

			assertClassificationEquals( expected, modular );
		} finally {
			OWL.manager.removeOntology( ontology );
		}
	}

	@Test
	public void miniTambisPersistenceAddsTest() throws IOException {
		performPersistenceAdds( "miniTambis" );
	}
	
	@Test
	public void miniTambisPersistenceRemovesTest() throws IOException {
		performPersistenceRemoves( "miniTambis" );
	}
	
	@Test
	public void miniTambisPersistenceAllowedUpdatesTest() throws IOException {
		performPersistenceAllowedUpdates( "miniTambis" );
	}

	@Test
	public void miniTambisUpdatesAfterPersistenceTest() throws IOException {
		performUpdatesAfterPersistence( "miniTambis" );
	}
	
	@Test
	public void miniTambisUpdatesAfterPersistence2Test() throws IOException {
		performUpdatesAfterPersistence2( "miniTambis" );
	}

	@Test
	public void miniTambisUpdatesWhenPersistedTest() throws IOException {
		performUpdatesWhenPersisted( "miniTambis" );
	}

}
