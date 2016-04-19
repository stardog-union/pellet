// Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package org.mindswap.pellet.test.rules;

import org.apache.jena.ontology.OntModel;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;
import org.apache.jena.vocabulary.OWL;
import org.junit.Test;
import org.mindswap.pellet.jena.PelletInfGraph;
import org.mindswap.pellet.jena.PelletReasonerFactory;
import org.mindswap.pellet.test.PelletTestSuite;

public class SWRLPerformanceTests
{
	public static void main(final String[] args)
	{
		org.junit.runner.JUnitCore.main("org.mindswap.pellet.test.rules.SWRLPerformanceTests");
	}

	private final static String base = "file:" + PelletTestSuite.base + "swrl-test/misc/";

	@Test
	public void testBasicFamily()
	{
		final String ns = "http://www.csc.liv.ac.uk/~luigi/ontologies/basicFamily#";

		final OntModel ontModel = ModelFactory.createOntologyModel(PelletReasonerFactory.THE_SPEC, null);
		ontModel.read(base + "basicFamilyReference.owl");
		ontModel.read(base + "basicFamilyRules.owl");
		ontModel.prepare();

		final Property uncle = ontModel.getProperty(ns + "hasUncle");
		int i = 0;
		for (final Resource ind : ontModel.listIndividuals(OWL.Thing).toList())
		{
			i++;
			System.out.println(ind.toString() + ": " + ontModel.getProperty(ind, uncle));
		}

		((PelletInfGraph) ontModel.getGraph()).getKB().timers.print();

		ontModel.close();
	}

	@Test
	public void testDayCare()
	{
		final String ns = "https://mywebspace.wisc.edu/jpthielman/web/daycareontology#";

		final OntModel ontModel = ModelFactory.createOntologyModel(PelletReasonerFactory.THE_SPEC, null);
		ontModel.read(base + "daycare.swrl.owl");
		ontModel.prepare();

		final Property exposedTo = ontModel.getProperty(ns + "is_exposed_to");

		int i = 0;
		for (final Resource ind : ontModel.listIndividuals(OWL.Thing).toList())
		{
			i++;
			System.out.println(ind.toString() + ": " + ontModel.getProperty(ind, exposedTo));
		}

		((PelletInfGraph) ontModel.getGraph()).getKB().timers.print();

		ontModel.close();
	}

	@Test
	public void testProtegeFamily() throws Exception
	{
		final String ns = "http://a.com/ontology#";

		final OntModel ontModel = ModelFactory.createOntologyModel(PelletReasonerFactory.THE_SPEC, null);
		ontModel.read(base + "family.swrl.owl");
		ontModel.prepare();

		final Property hasSibling = ontModel.getProperty(ns + "hasSibling");
		final StmtIterator iter = ontModel.listStatements((Resource) null, hasSibling, (RDFNode) null);
		while (iter.hasNext())
		{
			final Statement statement = iter.nextStatement();
			System.out.println(statement);
		}

		((PelletInfGraph) ontModel.getGraph()).getKB().timers.print();

		ontModel.close();
	}
}
