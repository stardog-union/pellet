// Portions Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// Clark & Parsia, LLC parts of this source code are available under the terms of the Affero General Public License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package org.mindswap.pellet.test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.apache.jena.graph.Graph;
import org.apache.jena.graph.Triple;
import org.apache.jena.ontology.OntDocumentManager;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.ontology.OntModelSpec;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.util.FileManager;
import org.apache.jena.util.LocationMapper;
import org.apache.jena.util.LocatorFile;
import org.apache.jena.util.iterator.ExtendedIterator;
import org.mindswap.pellet.jena.PelletInfGraph;
import org.mindswap.pellet.jena.PelletReasonerFactory;
import org.mindswap.pellet.jena.graph.loader.GraphLoader;

public class JenaWebOntTester implements WebOntTester
{
	private OntModel _model;
	private final LocationMapper _mapper;

	public JenaWebOntTester()
	{
		_mapper = new LocationMapper();
		final FileManager manager = OntDocumentManager.getInstance().getFileManager();
		manager.setLocationMapper(_mapper);
		manager.addLocator(new LocatorFile(null));
	}

	@Override
	public void classify()
	{
		((PelletInfGraph) _model.getGraph()).getKB().realize();
	}

	@Override
	public boolean isConsistent()
	{
		return ((PelletInfGraph) _model.getGraph()).getKB().isConsistent();
	}

	@Override
	public void testEntailment(final String entailmentFileURI, final boolean positiveEntailment)
	{
		final Model entailments = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM);
		entailments.read(entailmentFileURI, entailmentFileURI, fileType(entailmentFileURI));

		final Graph entailmentsGraph = entailments.getGraph();
		final PelletInfGraph pellet = (PelletInfGraph) _model.getGraph();

		final GraphLoader savedLoader = pellet.attachTemporaryGraph(entailmentsGraph);

		final ExtendedIterator<Triple> i = entailmentsGraph.find(Triple.ANY);

		while (i.hasNext())
		{
			final Triple triple = i.next();
			if (!pellet.entails(triple))
			{
				assertFalse("Entailment failed for " + triple, positiveEntailment);
				return;
			}
		}

		pellet.detachTemporaryGraph(entailmentsGraph, savedLoader);

		assertTrue("All axioms entailed in negative entailment test", positiveEntailment);
	}

	@Override
	public void setInputOntology(final String inputFileURI)
	{
		_model = ModelFactory.createOntologyModel(PelletReasonerFactory.THE_SPEC);
		_model.read(inputFileURI, inputFileURI, fileType(inputFileURI));
		_model.prepare();
	}

	@Override
	public void setTimeout(final long timeout)
	{
		((PelletInfGraph) _model.getGraph()).getKB().setTimeout(timeout);
	}

	@Override
	public void registerURIMapping(final String fromURI, final String toURI)
	{
		_mapper.addAltEntry(fromURI, toURI);
	}

	private String fileType(final String fileURI)
	{
		if (fileURI.endsWith(".n3"))
			return "N3";
		else
			return "RDF/XML";
	}
}
