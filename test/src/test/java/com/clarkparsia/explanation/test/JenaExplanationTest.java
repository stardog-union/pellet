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

import org.coode.owlapi.turtle.TurtleOntologyFormat;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.mindswap.pellet.KnowledgeBase;
import org.mindswap.pellet.jena.PelletInfGraph;
import org.mindswap.pellet.jena.PelletReasonerFactory;
import org.mindswap.pellet.utils.Namespaces;
import org.semanticweb.owlapi.io.StringDocumentSource;
import org.semanticweb.owlapi.io.StringDocumentTarget;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLOntology;

import com.hp.hpl.jena.graph.Graph;
import com.hp.hpl.jena.graph.Triple;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.reasoner.Reasoner;
import com.hp.hpl.jena.shared.PrefixMapping;
import com.hp.hpl.jena.vocabulary.OWL;
import com.hp.hpl.jena.vocabulary.OWL2;
import com.hp.hpl.jena.vocabulary.RDF;
import com.hp.hpl.jena.vocabulary.RDFS;

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
public class JenaExplanationTest extends AbstractExplanationTest {
	private static final Logger	log	= Logger.getLogger( JenaExplanationTest.class.getName() );


	@Parameters
	public static Collection<Object[]> getParameters() {
		Collection<Object[]> parameters = new ArrayList<Object[]>();
		parameters.add( new Object[] { false } );
		parameters.add( new Object[] { true } );
		return parameters;
	}
	
	private PelletInfGraph		pellet;

	public JenaExplanationTest(boolean classify) {
		super( classify );
	}

	private Graph convertOntology(Collection<OWLAxiom> axioms, boolean filterTypes) throws Exception {
		return convertOntology( com.clarkparsia.owlapiv3.OWL.Ontology( axioms ), filterTypes );
	}
	
	private Graph convertOntology(OWLOntology ontology, boolean filterTypes) throws Exception {
		StringDocumentTarget output = new StringDocumentTarget();

		manager.saveOntology( ontology, new TurtleOntologyFormat(),  output );

		Model model = ModelFactory.createDefaultModel();
		model.read( new StringReader( output.toString() ), ontologyURI.toString(), "TTL" );

		if( filterTypes ) {
			Resource[] builtinTypes = {
					OWL.Ontology, OWL.Class, OWL.ObjectProperty, OWL.DatatypeProperty, OWL.Thing,
					RDF.List };
			for( Resource builtinType : builtinTypes ) {
				model.removeAll( null, RDF.type, builtinType );
			}
		}

		return model.getGraph();
	}

	@Override
	public void setupGenerators(Collection<OWLAxiom> ontologyAxioms) throws Exception {
		OWLOntology ontology = com.clarkparsia.owlapiv3.OWL.Ontology( ontologyAxioms );			
		 
		Graph data = convertOntology( ontology, false );

		Reasoner reasoner = PelletReasonerFactory.theInstance().create();
		pellet = (PelletInfGraph) reasoner.bind( data );
		
		KnowledgeBase kb = pellet.getKB();
		
		if( classify ) {
			kb.setDoExplanation( true );
			pellet.prepare();
			kb.setDoExplanation( false );
			
			kb.realize();
		}
	}	

	@Override
	public void testInconsistencyExplanations(int max, OWLAxiom[]... explanations) throws Exception {
		// do nothing
	}

	@Override
	public void testExplanations(OWLAxiom axiom, int max, Set<Set<OWLAxiom>> expectedExplanations)
			throws Exception {
		Triple triple = null;
		if (axiom != null) {
			Graph graph = convertOntology( singletonList( axiom ), true );
	
			// We can only explain single triple inference through Jena. Instead of making 
			// this a failure we just tune the tests where single triples are explained.
			// OWLAPI implementation should run all tests.
			// assertTrue( "Multiple triples not supported", graph.size() == 1 );
			assumeTrue( graph.size() == 1 );
	
			triple = graph.find( Triple.ANY ).next();
		}

		Graph actual = triple == null ? pellet.explainInconsistency().getGraph() : pellet.explain( triple );

		assertNotNull( "Triple " + triple + "cannot be explained", actual );

		boolean success = testExplanationWithJena( triple, actual, expectedExplanations );

		assertTrue( "Error in explanation, see the log file for details", success );
	}

	private boolean testExplanationWithJena(Triple triple, Graph actual,
			Set<Set<OWLAxiom>> expectedExplanations) throws Exception {
		boolean success = false;

		for( Set<OWLAxiom> expectedExplanation : expectedExplanations ) {
			Graph expected = convertOntology( expectedExplanation, true );

			if( expected.isIsomorphicWith( actual ) ) {
				success = true;
				break;
			}
		}

		if( !success ) {
			StringWriter sw = new StringWriter();

			sw.getBuffer().append( "\nTriple: " + triple );
			sw.getBuffer().append( "\nExpected (" + expectedExplanations.size() + "):\n" );
			for( Set<OWLAxiom> expectedExplanation : expectedExplanations ) {
				Graph expected = convertOntology( expectedExplanation, true );
				ModelFactory.createModelForGraph( expected ).write( sw, "TTL" );
				sw.getBuffer().append( "\n=====================" );
			}
			sw.getBuffer().append( "\nActual:\n" );
			Model m = ModelFactory.createModelForGraph( actual );
			m.setNsPrefixes( PrefixMapping.Extended );
			m.setNsPrefix( "swrl", Namespaces.SWRL );
			m.write( sw, "TTL" );

			log.severe( "Error in explanation: " + sw );
		}

		return success;
	}

	@SuppressWarnings("unused")
	private boolean testExplanationWithOWLAPI(Triple triple, Graph actual,
			Set<Set<OWLAxiom>> expectedExplanations) throws Exception {
		boolean success = true;

		assertEquals( "Multiple explanations cannot be tested", 1, expectedExplanations.size() );

		Set<OWLAxiom> expectedExplanation = expectedExplanations.iterator().next();
		StringWriter sw = new StringWriter();
		ModelFactory.createModelForGraph( actual ).write( System.out, "TTL" );
		ModelFactory.createModelForGraph( actual ).write( sw, "RDF/XML" );
		OWLOntology ont = manager.loadOntologyFromOntologyDocument( new StringDocumentSource( sw.toString() ) );
		Set<? extends OWLAxiom> actualExplanation = ont.getLogicalAxioms();

		System.out.println( actualExplanation );

		StringBuilder sb = new StringBuilder();
		sb.append( "\nTriple: " + triple );
		sb.append( "\nUnexpected :\n" );
		for( OWLAxiom actualAxiom : actualExplanation ) {
			if( !expectedExplanation.remove( actualAxiom ) ) {
				success = false;
				sb.append( actualAxiom );
			}
		}

		sb.append( "\nNot found:\n" );
		for( OWLAxiom expectedAxiom : expectedExplanation ) {
			success = false;
			sb.append( expectedAxiom );
		}

		if( !success ) {
			log.severe( "Error in explanation: " + sb );
		}

		return success;
	}

	public static void main(String[] args) throws Exception {
		JenaExplanationTest test = new JenaExplanationTest(true);
		test.createEntities();
		test.anonymousIndividualPropertyAssertion();
	}

}