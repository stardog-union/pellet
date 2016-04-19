// Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package com.clarkparsia.modularity.test;

import com.clarkparsia.owlapi.OWL;
import com.clarkparsia.owlapi.OntologyUtils;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import org.junit.Test;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLOntology;

/**
 * <p>
 * Title:
 * </p>
 * <p>
 * Description: Test modular classification for correctness against unified classification
 * <p>
 * Copyright: Copyright (c) 2007
 * </p>
 * <p>
 * Company: Clark & Parsia, LLC. <http://www.clarkparsia.com>
 * </p>
 *
 * @author Mike Smith
 */
public abstract class RandomizedIncrementalClassifierTest extends AbstractModularityTest
{
	private String _path;

	public RandomizedIncrementalClassifierTest(final String path)
	{
		_path = path;

		if (!new File(path).exists())
		{
			_path = "src/test/resources/" + path;

			if (!new File(_path).exists())//
				throw new RuntimeException("Path to data files is not correct: " + path);
		}
	}

	private void classifyCorrectnessTest(final String file)
	{
		final int n = 5;
		final OWLOntology loadedOntology = OntologyUtils.loadOntology("file:" + file, false);

		final List<OWLAxiom> axioms = new ArrayList<>(TestUtils.selectRandomAxioms(loadedOntology, n * 2));
		final int size = axioms.size();

		// Delete 5 axioms before the test
		OntologyUtils.removeAxioms(loadedOntology, axioms.subList(0, n));

		// Update test will add n axioms and remove n axioms
		final List<OWLAxiom> additions = axioms.subList(0, n);
		final List<OWLAxiom> deletions = axioms.subList(n, n * 2);
		try
		{
			TestUtils.runUpdateTest(loadedOntology, modExtractor, additions, deletions);
		}
		catch (final AssertionError ex)
		{
			System.err.println("Additions: " + additions);
			System.err.println("Deletions: " + deletions);
			System.err.println("#axioms:" + size);
			throw ex;
		}
		catch (final RuntimeException ex)
		{
			System.err.println("Additions: " + additions);
			System.err.println("Deletions: " + deletions);
			System.err.println("#axioms:" + size);
			throw ex;
		}
		finally
		{
			OWL.manager.removeOntology(loadedOntology);
		}
	}

	@Test
	public void galenRandomizedIncrementalClassifyTest()
	{
		classifyCorrectnessTest(_path + "galen.owl");
	}

	@Test
	public void koalaRandomizedIncrementalClassifyTest()
	{
		classifyCorrectnessTest(_path + "koala.owl");
	}

	@Test
	public void sumoRandomizedIncrementalClassifyTest()
	{
		classifyCorrectnessTest(_path + "SUMO.owl");
	}

	@Test
	public void sweetRandomizedIncrementalClassifyTest()
	{
		classifyCorrectnessTest(_path + "SWEET.owl");
	}
}
