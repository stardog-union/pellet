// Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package com.clarkparsia.modularity.test;

import java.io.File;
import java.io.IOException;
import java.util.Set;

import com.clarkparsia.modularity.IncrementalReasoner;
import com.clarkparsia.modularity.ModuleExtractor;
import com.clarkparsia.owlapiv3.OWL;
import com.clarkparsia.owlapiv3.OntologyUtils;
import com.google.common.base.Supplier;
import org.junit.Assert;
import org.junit.Test;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLOntology;

import static org.junit.Assert.assertTrue;

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
public class PersistenceModularityTest extends AbstractModularityTest {

	private static final String TEST_FILE = "test-persistence.zip";

	public PersistenceModularityTest(final Supplier<ModuleExtractor> theModExtractorSupplier) {
		super(theModExtractorSupplier);
	}
	
	private void testPersistence( OWLOntology ontology ) throws IOException {
		File testFile = new File( TEST_FILE );
		ModuleExtractor moduleExtractor = createModuleExtractor();

		IncrementalReasoner modular = IncrementalReasoner.config().extractor(moduleExtractor).createIncrementalReasoner(ontology);
		IncrementalReasoner restored;

		try {
			modular.classify();

			modular.save(testFile);

			restored = IncrementalReasoner.config().file(testFile).manager(OWLManager.createOWLOntologyManager()).createIncrementalReasoner();

			for (OWLClass cls : ontology.getClassesInSignature()) {
				Set<OWLEntity> expectedModules = modular.getModuleExtractor().getModuleEntities(cls);
				Set<OWLEntity> actualModules = restored.getModuleExtractor().getModuleEntities(cls);

				Assert.assertEquals(cls.toString(), expectedModules, actualModules );
			}
		}
		finally {
			modular.dispose();
			assertTrue(testFile.delete());
		}

	}
	
	private void testPersistence( String file ) throws IOException {
		OWLOntology ontology = OntologyUtils.loadOntology( "file:" + file, false );
		try {
			testPersistence( ontology );
		}
		finally {		
			OWL.manager.removeOntology( ontology );
		}
	}
	
	@Test
	public void testGalen() throws IOException {
		testPersistence("test/data/modularity/galen.owl");
	}
}
