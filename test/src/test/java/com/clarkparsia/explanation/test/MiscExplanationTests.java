// Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public
// License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of
// proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package com.clarkparsia.explanation.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mindswap.pellet.PelletOptions;
import org.mindswap.pellet.jena.PelletInfGraph;
import org.mindswap.pellet.jena.vocabulary.OWL2;
import org.mindswap.pellet.utils.SetUtils;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLOntology;

import com.clarkparsia.owlapi.explanation.PelletExplanation;
import com.clarkparsia.owlapiv3.OWL;
import com.clarkparsia.pellet.owlapiv3.PelletReasoner;
import com.clarkparsia.pellet.owlapiv3.PelletReasonerFactory;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.ResourceFactory;
import com.hp.hpl.jena.vocabulary.RDF;
import com.hp.hpl.jena.vocabulary.RDFS;

/**
 * <p>
 * Title: ExplainationInconsistent
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
 * @author Markus Stocker
 */
public class MiscExplanationTests {
	private static Properties savedOptions;
	
	@BeforeClass
	public static void saveOptions() {
		Properties newOptions = new Properties();
		newOptions.setProperty( "USE_TRACING", "true" );
		
		savedOptions = PelletOptions.setOptions( newOptions );
	}
		
	@AfterClass
	public static void restoreOptions() {
		 PelletOptions.setOptions( savedOptions );
	}
	
	@Test
	public void testOWLAPI() throws Exception {
		OWLClass A = OWL.Class( "A" );
		OWLClass B = OWL.Class( "B" );
		OWLClass C = OWL.Class( "C" );
		OWLIndividual i = OWL.Individual( "i" );
		OWLIndividual j = OWL.Individual( "j" );

		Set<OWLAxiom> axioms = new HashSet<OWLAxiom>();
		axioms.add( OWL.disjointClasses( A, B ) );
		axioms.add( OWL.equivalentClasses( C, OWL.Nothing ) );
		axioms.add( OWL.classAssertion( i, A ) );
		axioms.add( OWL.classAssertion( i, B ) );
		axioms.add( OWL.classAssertion( j, C ) );

		OWLOntology ontology = OWL.Ontology( axioms );
		PelletReasoner reasoner = PelletReasonerFactory.getInstance().createReasoner( ontology );
		PelletExplanation explain = new PelletExplanation( reasoner );

		Set<Set<OWLAxiom>> actual = explain.getInconsistencyExplanations();

		Set<OWLAxiom> f = new HashSet<OWLAxiom>();
		f.add( OWL.classAssertion( i, B ) );
		f.add( OWL.classAssertion( i, A ) );
		f.add( OWL.disjointClasses( A, B ) );

		Set<OWLAxiom> s = new HashSet<OWLAxiom>();
		s.add( OWL.equivalentClasses( C, OWL.Nothing ) );
		s.add( OWL.classAssertion( j, C ) );

		Set<Set<OWLAxiom>> expected = new HashSet<Set<OWLAxiom>>();
		expected.add( f );
		expected.add( s );

		assertEquals( expected, actual );
	}
	
	@Test
	public void testPunning1() throws Exception {
		OWLClass A = OWL.Class("A");
		OWLClass B = OWL.Class("B");
		OWLIndividual i = OWL.Individual("A");

		Set<OWLAxiom> axioms = new HashSet<OWLAxiom>();
		axioms.add(OWL.disjointClasses(A, B));
		axioms.add(OWL.classAssertion(i, A));
		axioms.add(OWL.classAssertion(i, B));

		OWLOntology ontology = OWL.Ontology(axioms);
		PelletReasoner reasoner = PelletReasonerFactory.getInstance().createReasoner(ontology);
		PelletExplanation explain = new PelletExplanation(reasoner);

		assertFalse(explain.getInconsistencyExplanations().isEmpty());
	}

	@Test
	public void testPunning2() throws Exception {
		OWLObjectProperty P = OWL.ObjectProperty("P");
		OWLObjectProperty S = OWL.ObjectProperty("S");
		OWLIndividual i = OWL.Individual("P");

		Set<OWLAxiom> axioms = new HashSet<OWLAxiom>();
		axioms.add(OWL.disjointProperties(P, S));
		axioms.add(OWL.propertyAssertion(i, P, i));
		axioms.add(OWL.propertyAssertion(i, S, i));

		OWLOntology ontology = OWL.Ontology(axioms);
		PelletReasoner reasoner = PelletReasonerFactory.getInstance().createReasoner(ontology);
		PelletExplanation explain = new PelletExplanation(reasoner);

		assertFalse(explain.getInconsistencyExplanations().isEmpty());
	}

	@Test
	public void testPunning3() throws Exception {
		OWLClass A = OWL.Class("A");
		OWLIndividual i = OWL.Individual("A");

		OWLClass B = OWL.Class("B");
		OWLIndividual j = OWL.Individual("B");

		Set<OWLAxiom> axioms = new HashSet<OWLAxiom>();
		axioms.add(OWL.disjointClasses(A, B));
		axioms.add(OWL.classAssertion(i, A));
		axioms.add(OWL.classAssertion(j, B));
		axioms.add(OWL.sameAs(i, j));

		OWLOntology ontology = OWL.Ontology(axioms);
		PelletReasoner reasoner = PelletReasonerFactory.getInstance().createReasoner(ontology);
		PelletExplanation explain = new PelletExplanation(reasoner);

		assertFalse(explain.getInconsistencyExplanations().isEmpty());
	}

	@Test
	public void testPunningOneOf() throws Exception {
		OWLClass A = OWL.Class("A");
		OWLIndividual a = OWL.Individual("A");
		OWLIndividual b = OWL.Individual("b");
		
		Set<OWLAxiom> axioms = new HashSet<OWLAxiom>();
		axioms.add(OWL.equivalentClasses(A, OWL.oneOf(a, b)));

		OWLOntology ontology = OWL.Ontology(axioms);
		PelletReasoner reasoner = PelletReasonerFactory.getInstance().createReasoner(ontology);
		PelletExplanation explain = new PelletExplanation(reasoner);

		assertEquals( axioms, explain.getEntailmentExplanation(OWL.classAssertion(a, A)) );
	}

	@Test
	public void testPunningSingletonOneOf() throws Exception {
		OWLClass A = OWL.Class("A");
		OWLIndividual a = OWL.Individual("A");
		
		Set<OWLAxiom> axioms = new HashSet<OWLAxiom>();
		axioms.add(OWL.equivalentClasses(A, OWL.oneOf(a)));

		OWLOntology ontology = OWL.Ontology(axioms);
		PelletReasoner reasoner = PelletReasonerFactory.getInstance().createReasoner(ontology);
		PelletExplanation explain = new PelletExplanation(reasoner);

		assertEquals( axioms, explain.getEntailmentExplanation(OWL.classAssertion(a, A)) );
	}

	@Test
	public void testJena() throws Exception {
		Resource A = ResourceFactory.createResource( "A" );
		Resource B = ResourceFactory.createResource( "B" );
		Resource C = ResourceFactory.createResource( "C" );
		Resource i = ResourceFactory.createResource( "i" );
		
		Model expected = ModelFactory.createDefaultModel(); 
		expected.add( A, OWL2.disjointWith, B );
		expected.add( i, RDF.type, A );
		expected.add( i, RDF.type, B );
		
		OntModel model = ModelFactory.createOntologyModel( org.mindswap.pellet.jena.PelletReasonerFactory.THE_SPEC );
		model.add( expected );
		model.add( i, RDF.type, C );

		model.prepare();
				
		Model actual = ((PelletInfGraph) model.getGraph()).explainInconsistency();
		
		assertEquals( expected.listStatements().toSet(), actual.listStatements().toSet() );
	}
	
	/**
	 * Tests explanation of the unsatisfiability pattern reported in bug 453.
	 */
	@Test
	public void testUnsatisfiable453() {
		// the names of concepts were observed to be important (other names
		// probably change the ordering in which concepts are processed)

		OWLClass VolcanicMountain = OWL.Class("http://test#a_VOLCANICMOUNTAIN");
		OWLClass Mountain = OWL.Class("http://test#a_MOUNTAIN");
		OWLClass Volcano = OWL.Class("http://test#a_VOLCANO");
		OWLClass UplandArea = OWL.Class("http://test#a_UPLANDAREA");

		OWLAxiom[] axioms = { OWL.subClassOf(VolcanicMountain, Mountain), OWL.subClassOf(VolcanicMountain, Volcano),
		                OWL.subClassOf(Mountain, UplandArea), OWL.subClassOf(UplandArea, OWL.not(Volcano)),
		                OWL.disjointClasses(UplandArea, Volcano) };

		OWLOntology ontology = OWL.Ontology(axioms);
		PelletReasoner reasoner = PelletReasonerFactory.getInstance().createReasoner(ontology);
		PelletExplanation explain = new PelletExplanation(reasoner);

		// bug 453 manifested by throwing an OWLRuntimeException from the following statement
		// (number of explanations is important -- there are two explanations in this case, and the problem
		// only occurs if both of them are produced)
		Set<Set<OWLAxiom>> actual = explain.getUnsatisfiableExplanations(VolcanicMountain, 0);

		Set<OWLAxiom> f = SetUtils.create(axioms[0], axioms[1], axioms[2], axioms[3]);
		Set<OWLAxiom> s = SetUtils.create(axioms[0], axioms[1], axioms[2], axioms[4]);
		@SuppressWarnings("unchecked")
		Set<Set<OWLAxiom>> expected = SetUtils.create(f, s);

		assertEquals(expected, actual);
	}
	
	/**
	 * Test for ticket  #478
	 */
	@Test
	public void testJenaUpdates() throws Exception {
		Resource A = ResourceFactory.createResource( "A" );
		Resource B = ResourceFactory.createResource( "B" );
		Resource C = ResourceFactory.createResource( "C" );
		Resource i = ResourceFactory.createResource( "i" );
		
		OntModel model = ModelFactory.createOntologyModel( org.mindswap.pellet.jena.PelletReasonerFactory.THE_SPEC );
		model.add( i, RDF.type, A );
		model.add( A, RDFS.subClassOf, B );
		
		model.prepare();
		Model actual = ((PelletInfGraph) model.getGraph()).explain(i, RDF.type, B );
		Model expected = model.getRawModel();
		
		assertEquals( expected.listStatements().toSet(), actual.listStatements().toSet() );

		model.add( B, RDFS.subClassOf, C );
		
		model.prepare();
		
		actual = ((PelletInfGraph) model.getGraph()).explain(i, RDF.type, C );
		expected = model.getRawModel();
		
		assertEquals( expected.listStatements().toSet(), actual.listStatements().toSet() );
	}
}
