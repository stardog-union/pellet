// Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public
// License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of
// proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package com.clarkparsia.explanation.test;

import static java.util.Collections.singletonList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assume.assumeTrue;

import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;
import java.util.logging.Logger;
import org.apache.jena.graph.Graph;
import org.apache.jena.graph.Triple;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.reasoner.Reasoner;
import org.apache.jena.shared.PrefixMapping;
import org.apache.jena.vocabulary.OWL;
import org.apache.jena.vocabulary.RDF;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.mindswap.pellet.KnowledgeBase;
import org.mindswap.pellet.jena.PelletInfGraph;
import org.mindswap.pellet.jena.PelletReasonerFactory;
import org.mindswap.pellet.utils.Namespaces;
import org.semanticweb.owlapi.formats.TurtleDocumentFormat;
import org.semanticweb.owlapi.io.StringDocumentSource;
import org.semanticweb.owlapi.io.StringDocumentTarget;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLOntology;

/**
 * <p>
 * Title:
 * </p>
 * <p>
 * Description:
 * </p>
 * <p>
 * Copyright: Copyright (c) 2008
 * </p>
 * <p>
 * Company: Clark & Parsia, LLC. <http://www.clarkparsia.com>
 * </p>
 *
 * @author Evren Sirin
 */
@RunWith(Parameterized.class)
public class JenaExplanationTest extends AbstractExplanationTest
{
	private static final Logger log = Logger.getLogger(JenaExplanationTest.class.getName());

	@Parameters
	public static Collection<Object[]> getParameters()
	{
		final Collection<Object[]> parameters = new ArrayList<>();
		parameters.add(new Object[] { false });
		parameters.add(new Object[] { true });
		return parameters;
	}

	private PelletInfGraph pellet;

	public JenaExplanationTest(final boolean classify)
	{
		super(classify);
	}

	private Graph convertOntology(final Collection<OWLAxiom> axioms, final boolean filterTypes) throws Exception
	{
		return convertOntology(com.clarkparsia.owlapi.OWL.Ontology(axioms), filterTypes);
	}

	private Graph convertOntology(final OWLOntology ontology, final boolean filterTypes) throws Exception
	{
		final StringDocumentTarget output = new StringDocumentTarget();

		manager.saveOntology(ontology, new TurtleDocumentFormat(), output);

		final Model model = ModelFactory.createDefaultModel();
		model.read(new StringReader(output.toString()), ontologyURI.toString(), "TTL");

		if (filterTypes)
		{
			final Resource[] builtinTypes = { OWL.Ontology, OWL.Class, OWL.ObjectProperty, OWL.DatatypeProperty, OWL.Thing, RDF.List };
			for (final Resource builtinType : builtinTypes)
				model.removeAll(null, RDF.type, builtinType);
		}

		return model.getGraph();
	}

	@Override
	public void setupGenerators(final Collection<OWLAxiom> ontologyAxioms) throws Exception
	{
		final OWLOntology ontology = com.clarkparsia.owlapi.OWL.Ontology(ontologyAxioms);

		final Graph data = convertOntology(ontology, false);

		final Reasoner reasoner = PelletReasonerFactory.theInstance().create();
		pellet = (PelletInfGraph) reasoner.bind(data);

		final KnowledgeBase kb = pellet.getKB();

		if (classify)
		{
			kb.setDoExplanation(true);
			pellet.prepare();
			kb.setDoExplanation(false);

			kb.realize();
		}
	}

	@Override
	public void testInconsistencyExplanations(final int max, final OWLAxiom[]... explanations) throws Exception
	{
		// do nothing
	}

	@Override
	public void testExplanations(final OWLAxiom axiom, final int max, final Set<Set<OWLAxiom>> expectedExplanations) throws Exception
	{
		Triple triple = null;
		if (axiom != null)
		{
			final Graph graph = convertOntology(singletonList(axiom), true);

			// We can only explain single triple inference through Jena. Instead of making
			// this a failure we just tune the tests where single triples are explained.
			// OWLAPI implementation should run all tests.
			// assertTrue( "Multiple triples not supported", graph.size() == 1 );
			assumeTrue(graph.size() == 1);

			triple = graph.find(Triple.ANY).next();
		}

		final Graph actual = triple == null ? pellet.explainInconsistency().getGraph() : pellet.explain(triple);

		assertNotNull("Triple " + triple + "cannot be explained", actual);

		final boolean success = testExplanationWithJena(triple, actual, expectedExplanations);

		assertTrue("Error in explanation, see the _log file for details", success);
	}

	private boolean testExplanationWithJena(final Triple triple, final Graph actual, final Set<Set<OWLAxiom>> expectedExplanations) throws Exception
	{
		boolean success = false;

		for (final Set<OWLAxiom> expectedExplanation : expectedExplanations)
		{
			final Graph expected = convertOntology(expectedExplanation, true);

			if (expected.isIsomorphicWith(actual))
			{
				success = true;
				break;
			}
		}

		if (!success)
		{
			final StringWriter sw = new StringWriter();

			sw.getBuffer().append("\nTriple: " + triple);
			sw.getBuffer().append("\nExpected (" + expectedExplanations.size() + "):\n");
			for (final Set<OWLAxiom> expectedExplanation : expectedExplanations)
			{
				final Graph expected = convertOntology(expectedExplanation, true);
				ModelFactory.createModelForGraph(expected).write(sw, "TTL");
				sw.getBuffer().append("\n=====================");
			}
			sw.getBuffer().append("\nActual:\n");
			final Model m = ModelFactory.createModelForGraph(actual);
			m.setNsPrefixes(PrefixMapping.Extended);
			m.setNsPrefix("swrl", Namespaces.SWRL);
			m.write(sw, "TTL");

			log.severe("Error in explanation: " + sw);
		}

		return success;
	}

	@SuppressWarnings("unused")
	private boolean testExplanationWithOWLAPI(final Triple triple, final Graph actual, final Set<Set<OWLAxiom>> expectedExplanations) throws Exception
	{
		boolean success = true;

		assertEquals("Multiple explanations cannot be tested", 1, expectedExplanations.size());

		final Set<OWLAxiom> expectedExplanation = expectedExplanations.iterator().next();
		final StringWriter sw = new StringWriter();
		ModelFactory.createModelForGraph(actual).write(System.out, "TTL");
		ModelFactory.createModelForGraph(actual).write(sw, "RDF/XML");
		final OWLOntology ont = manager.loadOntologyFromOntologyDocument(new StringDocumentSource(sw.toString()));
		final Set<? extends OWLAxiom> actualExplanation = ont.getLogicalAxioms();

		System.out.println(actualExplanation);

		final StringBuilder sb = new StringBuilder();
		sb.append("\nTriple: " + triple);
		sb.append("\nUnexpected :\n");
		for (final OWLAxiom actualAxiom : actualExplanation)
			if (!expectedExplanation.remove(actualAxiom))
			{
				success = false;
				sb.append(actualAxiom);
			}

		sb.append("\nNot found:\n");
		for (final OWLAxiom expectedAxiom : expectedExplanation)
		{
			success = false;
			sb.append(expectedAxiom);
		}

		if (!success)
			log.severe("Error in explanation: " + sb);

		return success;
	}

	public static void main(final String[] args) throws Exception
	{
		final JenaExplanationTest test = new JenaExplanationTest(true);
		test.createEntities();
		test.anonymousIndividualPropertyAssertion();
	}

}
