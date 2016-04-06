// Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package com.clarkparsia.modularity.test;

import static org.junit.Assert.assertTrue;

import com.clarkparsia.owlapi.OWL;
import com.clarkparsia.owlapi.OntologyUtils;

import com.clarkparsia.modularity.AxiomBasedModuleExtractor;
import com.clarkparsia.modularity.IncrementalClassifier;
import com.clarkparsia.modularity.ModuleExtractor;
import com.clarkparsia.modularity.PelletIncremantalReasonerFactory;
import com.clarkparsia.modularity.io.IncrementalClassifierPersistence;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import org.junit.Assert;
import org.junit.Test;
import org.mindswap.pellet.utils.MultiValueMap;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLOntology;

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
public class PersistenceModularityTest extends AbstractModularityTest
{

	private static final String TEST_FILE = "test-persistence.zip";

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ModuleExtractor createModuleExtractor()
	{
		return new AxiomBasedModuleExtractor();
	}

	private void testPersistence(OWLOntology ontology) throws IOException
	{
		final File testFile = new File(TEST_FILE);
		final ModuleExtractor moduleExtractor = createModuleExtractor();

		IncrementalClassifier modular = PelletIncremantalReasonerFactory.getInstance().createReasoner(ontology, moduleExtractor);
		modular.classify();

		final MultiValueMap<OWLEntity, OWLEntity> expectedModules = modular.getModules();

		try (FileOutputStream fos = new FileOutputStream(testFile))
		{
			IncrementalClassifierPersistence.save(modular, fos);
		}

		modular.dispose();

		try (final FileInputStream fis = new FileInputStream(testFile))
		{
			modular = IncrementalClassifierPersistence.load(fis);
		}

		modular.dispose();

		final MultiValueMap<OWLEntity, OWLEntity> actualModules = modular.getModules();

		Assert.assertEquals(expectedModules, actualModules);

		assertTrue(testFile.delete());
	}

	private void testPersistence(String file) throws IOException
	{
		final OWLOntology ontology = OntologyUtils.loadOntology("file:" + file, false);
		try
		{
			testPersistence(ontology);
		}
		finally
		{
			OWL.manager.removeOntology(ontology);
		}
	}

	@Test
	public void testGalen() throws IOException
	{
		testPersistence("test/data/modularity/galen.owl");
	}

	public static void main(String[] args) throws IOException
	{
		new PersistenceModularityTest().testPersistence("test/data/modularity/galen.owl");
	}
}
