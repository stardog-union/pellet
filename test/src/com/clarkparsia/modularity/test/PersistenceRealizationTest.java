// Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package com.clarkparsia.modularity.test;

import static com.clarkparsia.modularity.test.TestUtils.assertInstancesEquals;
import static com.clarkparsia.modularity.test.TestUtils.assertTypesEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import org.junit.Test;
import org.mindswap.pellet.test.PelletTestSuite;
import org.semanticweb.owlapi.model.OWLOntology;

import com.clarkparsia.modularity.AxiomBasedModuleExtractor;
import com.clarkparsia.modularity.IncrementalClassifier;
import com.clarkparsia.modularity.ModuleExtractor;
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
public class PersistenceRealizationTest {
	public static final String	base	= PelletTestSuite.base + "modularity/";
	
	private static final String TEST_FILE = "test-persistence-realization.zip";
		
	public ModuleExtractor createModuleExtractor() {
		return new AxiomBasedModuleExtractor();
	}
	
	public void testFile(String fileName) throws IOException {
		String common = "file:"+ base + fileName;
		testRealization( common + ".owl");		
	}
	
	public void testRealization(String inputOnt) throws IOException {
		File testFile = new File( TEST_FILE );
		OWLOntology ontology = OntologyUtils.loadOntology( inputOnt );
		
		try {
			PelletReasoner unified = PelletReasonerFactory.getInstance().createReasoner( ontology );
			ModuleExtractor moduleExtractor = createModuleExtractor();

			IncrementalClassifier modular = new IncrementalClassifier( unified, moduleExtractor );
			modular.classify();

			// first we only persist classified-but-not-realized classifier
			assertFalse( modular.isRealized() );

			FileOutputStream fos = new FileOutputStream( testFile );

			IncrementalClassifierPersistence.save( modular, fos );

			fos.close();

			FileInputStream fis = new FileInputStream( testFile );

			IncrementalClassifier modular2 = IncrementalClassifierPersistence.load( fis );

			fis.close();
			assertTrue( testFile.delete() );

			// the classifier read from file should NOT be realized at this point
			assertFalse( modular.isRealized() );
			
			assertInstancesEquals( unified, modular2 );
			assertTypesEquals( unified, modular2 );
			
			// the previous tests should have triggered realization
			assertTrue( modular2.isRealized() );

			// save the classifier again and read it back
			fos = new FileOutputStream( testFile );

			IncrementalClassifierPersistence.save( modular2, fos );

			fos.close();

			fis = new FileInputStream( testFile );

			IncrementalClassifier modular3 = IncrementalClassifierPersistence.load( fis );

			fis.close();
			assertTrue( testFile.delete() );

			// the classifier read from file should be realized at this point
			assertTrue( modular3.isRealized() );
			
			assertInstancesEquals( unified, modular3 );
			assertTypesEquals( unified, modular3 );
			
			unified.dispose();
			modular.dispose();
			modular2.dispose();
			modular3.dispose();
		} 
		finally {
			OWL.manager.removeOntology( ontology );
		}
	}	
	
	@Test
	public void koalaPersistenceRealizationTest() throws IOException {
		testFile( "koala" );
	}

	@Test
	public void sumoPersistenceRealizationTest() throws IOException {
		testFile( "SUMO" );
	}

	@Test
	public void sweetPersistenceRealizationTest() throws IOException {
		testFile( "SWEET" );
	}
}
