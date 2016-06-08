// Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package com.clarkparsia.modularity.test;

import static com.clarkparsia.modularity.test.TestUtils.assertClassificationEquals;
import static org.junit.Assert.assertTrue;

import com.clarkparsia.modularity.AxiomBasedModuleExtractor;
import com.clarkparsia.modularity.IncrementalClassifier;
import com.clarkparsia.modularity.ModuleExtractor;
import com.clarkparsia.modularity.io.IncrementalClassifierPersistence;
import com.clarkparsia.owlapi.OWL;
import com.clarkparsia.owlapi.OntologyUtils;
import com.clarkparsia.pellet.owlapi.PelletReasoner;
import com.clarkparsia.pellet.owlapi.PelletReasonerFactory;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import org.junit.Test;
import org.mindswap.pellet.test.PelletTestSuite;
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
public class PersistenceClassificationTest
{
	public static final String base = PelletTestSuite.base + "modularity/";

	private static final String TEST_FILE = "test-persistence-classification.zip";

	public ModuleExtractor createModuleExtractor()
	{
		return new AxiomBasedModuleExtractor();
	}

	public void testFile(final String fileName) throws IOException
	{
		final String common = "file:" + base + fileName;
		testClassification(common + ".owl");
	}

	public void testClassification(final String inputOnt) throws IOException
	{
		final File testFile = new File(TEST_FILE);
		final OWLOntology ontology = OntologyUtils.loadOntology(inputOnt);

		try
		{
			final PelletReasoner unified = PelletReasonerFactory.getInstance().createReasoner(ontology);
			final ModuleExtractor moduleExtractor = createModuleExtractor();

			final IncrementalClassifier modular = new IncrementalClassifier(unified, moduleExtractor);

			modular.classify();

			try (FileOutputStream fos = new FileOutputStream(testFile))
			{
				IncrementalClassifierPersistence.save(modular, fos);
			}

			final IncrementalClassifier modular2;
			try (final FileInputStream fis = new FileInputStream(testFile))
			{
				modular2 = IncrementalClassifierPersistence.load(fis);
			}
			assertClassificationEquals(unified, modular2);
			assertTrue(testFile.delete());

			unified.dispose();
			modular.dispose();
			modular2.dispose();
		}
		finally
		{
			OWL._manager.removeOntology(ontology);
		}
	}

	@Test
	public void koalaPersistenceClassifyTest() throws IOException
	{
		testFile("koala");
	}

	@Test
	public void miniTambisPersistenceClassifyTest() throws IOException
	{
		testFile("miniTambis");
	}

	@Test
	public void sumoPersistenceClassifyTest() throws IOException
	{
		testFile("SUMO");
	}

	@Test
	public void sweetPersistenceClassifyTest() throws IOException
	{
		testFile("SWEET");
	}

	@Test
	public void galenPersistenceClassifyTest() throws IOException
	{
		testFile("galen");
	}

	@Test
	public void winePersistenceClassifyTest() throws IOException
	{
		testFile("wine");
	}
}
