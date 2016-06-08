// Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package com.clarkparsia.modularity.test;

import com.clarkparsia.modularity.ModularityUtils;
import com.clarkparsia.owlapi.OWL;
import com.clarkparsia.owlapi.OntologyUtils;
import java.io.File;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.stream.Collectors;
import org.junit.Test;
import org.semanticweb.owlapi.model.AxiomType;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLDeclarationAxiom;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.modularity.OntologySegmenter;
import uk.ac.manchester.cs.owlapi.modularity.ModuleType;
import uk.ac.manchester.cs.owlapi.modularity.SyntacticLocalityModuleExtractor;

/**
 * @author Evren Sirin
 */
public abstract class RandomizedModularityTest extends AbstractModularityTest
{

	private String _path;

	public RandomizedModularityTest(final String path)
	{
		_path = path;

		if (!new File(path).exists())
		{
			_path = "src/test/resources/" + path;

			if (!new File(_path).exists())//
				throw new RuntimeException("Path to _data files is not correct: " + path);
		}
	}

	private void modularityTest(final String file)
	{
		final OWLOntology ontology = OntologyUtils.loadOntology("file:" + file, false);

		final Set<OWLEntity> signature = new HashSet<>();
		signature.addAll(TestUtils.selectRandomElements(ontology.classesInSignature().collect(Collectors.toList()), 5));
		modularityTest(ontology, signature);

		OWL._manager.removeOntology(ontology);
	}

	private void modularityTest(final OWLOntology ontology, final Set<OWLEntity> signature)
	{
		modularityTest(ontology, signature, ModuleType.BOT);
		modularityTest(ontology, signature, ModuleType.TOP);
		//		modularityTest( ontology, signature, ModuleType.BOT_OF_TOP );
		//		modularityTest( ontology, signature, ModuleType.TOP_OF_BOT );
	}

	private void modularityTest(final OWLOntology ontology, final Set<OWLEntity> signature, final ModuleType moduleType)
	{
		final Set<OWLAxiom> computed = ModularityUtils.extractModule(ontology, signature, moduleType);

		final OntologySegmenter segmenter = new SyntacticLocalityModuleExtractor(OWL._manager, ontology, moduleType);
		final Set<OWLAxiom> expected = segmenter.extract(signature);

		// prune declarations to avoid mismatches related to declarations
		for (final OWLEntity entity : signature)
		{
			final OWLDeclarationAxiom declaration = OWL.declaration(entity);
			computed.remove(declaration);
			computed.remove(declaration);
		}

		for (final Iterator<OWLAxiom> i = expected.iterator(); i.hasNext();)
		{
			final OWLAxiom axiom = i.next();
			if (axiom.getAxiomType() == AxiomType.SAME_INDIVIDUAL || axiom.getAxiomType() == AxiomType.DIFFERENT_INDIVIDUALS)
				i.remove();
		}

		TestUtils.assertToStringEquals("Modules diff for " + signature, expected.toArray(new OWLAxiom[0]), computed.toArray(new OWLAxiom[0]));
	}

	@Test
	public void galenModularityTest()
	{
		modularityTest(_path + "galen.owl");
	}

	@Test
	public void koalaModularityTest()
	{
		modularityTest(_path + "koala.owl");
	}

	@Test
	public void sumoModularityTest()
	{
		modularityTest(_path + "SUMO.owl");
	}

	@Test
	public void sweetModularityTest()
	{
		modularityTest(_path + "SWEET.owl");
	}

	@Test
	public void wineModularityTest()
	{
		modularityTest(_path + "wine.owl");
	}
}
