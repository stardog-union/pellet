package com.clarkparsia.pellet.test.annotations;

import static org.junit.Assert.assertEquals;

import java.util.HashSet;
import java.util.Set;

import junit.framework.JUnit4TestAdapter;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mindswap.pellet.KnowledgeBase;
import org.mindswap.pellet.PelletOptions;
import org.mindswap.pellet.jena.PelletInfGraph;
import org.mindswap.pellet.utils.ATermUtils;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAnnotationProperty;
import org.semanticweb.owlapi.model.OWLAnnotationValue;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyChangeException;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;

import aterm.ATermAppl;

import com.clarkparsia.pellet.owlapiv3.PelletReasoner;
import com.clarkparsia.pellet.owlapiv3.PelletReasonerFactory;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.ResourceFactory;
import com.hp.hpl.jena.vocabulary.OWL;
import com.hp.hpl.jena.vocabulary.RDF;
import com.hp.hpl.jena.vocabulary.RDFS;


public class TestReasoningWithAnnotationAxioms {

	KnowledgeBase kb = new KnowledgeBase();
	
	ATermAppl i = ATermUtils.makeTermAppl( "i" );
	ATermAppl p1 = ATermUtils.makeTermAppl( "p1" );
	ATermAppl o1 = ATermUtils.makePlainLiteral( "o1" );
	ATermAppl o2 = ATermUtils.makePlainLiteral( "o2" );
	ATermAppl p2 = ATermUtils.makeTermAppl( "p2" );
	ATermAppl p3 = ATermUtils.makeTermAppl( "p3" );
	
	OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
	OWLNamedIndividual oi = manager.getOWLDataFactory().getOWLNamedIndividual(IRI.create("i"));
	OWLAnnotationProperty op1 = manager.getOWLDataFactory().getOWLAnnotationProperty(IRI.create("p1"));
	OWLAnnotationProperty op2 = manager.getOWLDataFactory().getOWLAnnotationProperty(IRI.create("p2"));
	OWLAnnotationProperty op3 = manager.getOWLDataFactory().getOWLAnnotationProperty(IRI.create("p3"));
	OWLAnnotationValue oo1 = manager.getOWLDataFactory().getOWLStringLiteral("o1");
	
	OntModel model = ModelFactory.createOntologyModel( org.mindswap.pellet.jena.PelletReasonerFactory.THE_SPEC );
	Resource ji = ResourceFactory.createResource( "http://example.org#i" );
	Property jp1 = ResourceFactory.createProperty("http://example.org#p1");
	Property jp2 = ResourceFactory.createProperty("http://example.org#p2");
	Literal jo1 = ResourceFactory.createPlainLiteral( "o1" );
	
	private boolean USE_ANNOTATION_SUPPORT_DEFAULT_VALUE = PelletOptions.USE_ANNOTATION_SUPPORT;
	
	public static junit.framework.Test suite() {
		return new JUnit4TestAdapter( TestReasoningWithAnnotationAxioms.class );
	}
	
	@Before
	public void setUp() {
		PelletOptions.USE_ANNOTATION_SUPPORT = true;	
	}

	@After
	public void cleanUp() {
		PelletOptions.USE_ANNOTATION_SUPPORT = USE_ANNOTATION_SUPPORT_DEFAULT_VALUE;
	}
	
	@Test
	/**
	 * Tests if the value of a given annotation property is propagated to its superproperty
	 */
	public void testSubAnnotationPropertyOfAxiom1() {
		kb.addIndividual( i );
		kb.addAnnotationProperty( p1 );
		kb.addAnnotationProperty( p2 );
		kb.addSubProperty( p1, p2 );
		kb.addAnnotation(i, p1, o1);
		
		assertEquals(kb.getPropertyValues( p1 ), kb.getPropertyValues( p2 ));
	}
	
	@Test
	/**
	 * Tests if the value of a given annotation property is propagated to its superproperties
	 */
	public void testSubAnnotationPropertyOfAxiom2() {
		kb.addIndividual( i );
		kb.addAnnotationProperty( p1 );
		kb.addAnnotationProperty( p2 );
		kb.addAnnotationProperty( p3 );
		kb.addSubProperty( p1, p2 );
		kb.addSubProperty( p2, p3 );
		kb.addAnnotation(i, p1, o1);
		
		assertEquals(kb.getPropertyValues( p1 ), kb.getPropertyValues( p3 ));
	}
	
	@Test
	/**
	 * Tests if the value of a given annotation property is propagated to its superproperties
	 */
	public void testSubAnnotationPropertyOfAxiom3() {
		
		kb.addIndividual( i );
		kb.addAnnotationProperty( p1 );
		kb.addAnnotationProperty( p2 );
		kb.addAnnotationProperty( p3 );
		kb.addSubProperty( p1, p2 );
		kb.addSubProperty( p2, p3 );
		kb.addAnnotation(i, p1, o1);
		kb.addAnnotation(i, p2, o2);
		
		assertEquals(kb.getPropertyValues( p2 ), kb.getPropertyValues( p3 ));
	}
	
	@Test
	public void testOWLAPILoader1() throws OWLOntologyCreationException, OWLOntologyChangeException {
		Set<OWLAxiom> axioms = new HashSet<OWLAxiom>();
		axioms.add(manager.getOWLDataFactory().getOWLSubAnnotationPropertyOfAxiom(op1, op2));
		axioms.add(manager.getOWLDataFactory().getOWLAnnotationAssertionAxiom( op1 , oi.getIRI(), oo1 ));
		
		OWLOntology ontology = manager.createOntology(axioms);
		PelletReasoner reasoner = PelletReasonerFactory.getInstance().createReasoner(ontology);
		
		assertEquals(reasoner.getAnnotationPropertyValues(oi, op1), reasoner.getAnnotationPropertyValues(oi, op2));
	}
	
	@Test
	public void testOWLAPILoader2() throws OWLOntologyCreationException, OWLOntologyChangeException {		
				
		Set<OWLAxiom> axioms = new HashSet<OWLAxiom>();
		axioms.add(manager.getOWLDataFactory().getOWLSubAnnotationPropertyOfAxiom(op1, op2));
		axioms.add(manager.getOWLDataFactory().getOWLSubAnnotationPropertyOfAxiom(op2, op3));
		axioms.add(manager.getOWLDataFactory().getOWLAnnotationAssertionAxiom( op1 , oi.getIRI(), oo1 ));
				
		OWLOntology ontology = manager.createOntology(axioms);
		PelletReasoner reasoner = PelletReasonerFactory.getInstance().createReasoner(ontology);
		
		assertEquals(reasoner.getAnnotationPropertyValues(oi, op1), reasoner.getAnnotationPropertyValues(oi, op3));
	}
	
	@Test
	public void testJenaLoader1() {

		model.add( jp1, RDF.type, OWL.AnnotationProperty);
		model.add( ji, jp1, jo1 );
		model.add( jp1, RDFS.subPropertyOf, RDFS.label);
		model.prepare();

		kb = ((PelletInfGraph) model.getGraph()).getKB();
		
		ATermAppl st = ATermUtils.makeTermAppl( "http://example.org#i" );
		ATermAppl pt = ATermUtils.makeTermAppl( RDFS.label.getURI() );
		ATermAppl ot = ATermUtils.makePlainLiteral( "o1" );
		
		Set<ATermAppl> actual = kb.getAnnotations( st, pt );
		
		Set<ATermAppl> expected = new HashSet<ATermAppl>();
		expected.add( ot );

		assertEquals( expected, actual );
	}
	
	@Test
	public void testJenaLoader2() {
		
		model.add( jp1, RDF.type, OWL.AnnotationProperty);
		model.add( ji, jp1, jo1 );
		model.add( jp2, RDF.type, OWL.AnnotationProperty);
		model.add( jp1, RDFS.subPropertyOf, jp2);
		model.prepare();

		ATermAppl st = ATermUtils.makeTermAppl( "http://example.org#i" );
		ATermAppl pt = ATermUtils.makeTermAppl( jp2.getURI() );
		ATermAppl ot = ATermUtils.makePlainLiteral( "o1" );

		kb = ((PelletInfGraph) model.getGraph()).getKB();
		Set<ATermAppl> actual = kb.getAnnotations( st, pt );
		
		Set<ATermAppl> expected = new HashSet<ATermAppl>();
		expected.add( ot );
		
		assertEquals( expected, actual );
	}
}