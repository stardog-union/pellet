package org.mindswap.pellet.test;

import static com.clarkparsia.pellet.utils.TermFactory.term;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Collections;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

import junit.framework.JUnit4TestAdapter;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mindswap.pellet.KnowledgeBase;
import org.mindswap.pellet.PelletOptions;
import org.mindswap.pellet.jena.PelletInfGraph;
import org.mindswap.pellet.jena.PelletReasonerFactory;
import org.mindswap.pellet.utils.ATermUtils;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;

import aterm.ATermAppl;

import com.clarkparsia.pellet.owlapiv3.OWLAPILoader;
import com.clarkparsia.pellet.sparqldl.jena.SparqlDLExecutionFactory;
import com.clarkparsia.pellet.utils.PropertiesBuilder;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.ResourceFactory;
import com.hp.hpl.jena.vocabulary.RDFS;

/**
 * <p>
 * Title: TestAnnotationsKnowledgeBase
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
public class TestAnnotations {

	private static final String	DATA1_RDF	= "file:test/data/annotations/data1.rdf";
	private static final String	DATA1_TTL	= "file:test/data/annotations/data1.ttl";
	private static final String	QUERY1_RQ	= "file:test/data/annotations/query1.rq";

	public static junit.framework.Test suite() {
		return new JUnit4TestAdapter( TestAnnotations.class );
	}

	private Properties savedOptions;

	@Before
	public void setUp() {
		Properties newOptions = PropertiesBuilder.singleton( "USE_ANNOTATION_SUPPORT", "true" );
		savedOptions = PelletOptions.setOptions( newOptions );
	}

	@After
	public void tearDown() {
		PelletOptions.setOptions( savedOptions );
	}
	
	@Test
	public void addAnnotation1() {	
		KnowledgeBase kb = new KnowledgeBase();

		ATermAppl s = ATermUtils.makeTermAppl( "i" );
		ATermAppl p = ATermUtils.makeTermAppl( "p" );
		ATermAppl o = ATermUtils.makePlainLiteral( "o" );

		kb.addIndividual( s );
		kb.addAnnotationProperty( p );

		assertTrue( kb.addAnnotation( s, p, o ) );
		assertTrue( kb.isIndividual( s ) );
		assertTrue( kb.isAnnotationProperty( p ) );
		assertFalse( kb.isIndividual( o ) );
	}

	@Test
	public void addAnnotation2() {
		KnowledgeBase kb = new KnowledgeBase();

		ATermAppl s = ATermUtils.makeTermAppl( "i" );
		ATermAppl p = ATermUtils.makeTermAppl( "p" );
		ATermAppl o = ATermUtils.makeTermAppl( "j" );

		kb.addIndividual( s );
		kb.addIndividual( o );
		kb.addAnnotationProperty( p );

		assertTrue( kb.addAnnotation( s, p, o ) );
		assertTrue( kb.isIndividual( s ) );
		assertTrue( kb.isAnnotationProperty( p ) );
		assertTrue( kb.isIndividual( o ) );
	}

	@Test
	public void addAnnotation3() {		
		KnowledgeBase kb = new KnowledgeBase();

		ATermAppl s = ATermUtils.makeTermAppl( "i" );
		ATermAppl p = ATermUtils.makeTermAppl( "p" );
		ATermAppl o = ATermUtils.makeBnode( "b" );

		kb.addIndividual( s );
		kb.addAnnotationProperty( p );

		assertTrue( kb.addAnnotation( s, p, o ) );
		assertTrue( kb.isIndividual( s ) );
		assertTrue( kb.isAnnotationProperty( p ) );
		assertFalse( kb.isIndividual( o ) );
	}

	@Test
	public void addAnnotations() {	
		KnowledgeBase kb = new KnowledgeBase();

		ATermAppl s = ATermUtils.makeTermAppl( "i" );
		ATermAppl p = ATermUtils.makeTermAppl( "p" );
		ATermAppl o1 = ATermUtils.makePlainLiteral( "o1" );
		ATermAppl o2 = ATermUtils.makePlainLiteral( "o2" );

		kb.addIndividual( s );
		kb.addAnnotationProperty( p );

		assertTrue( kb.addAnnotation( s, p, o1 ) );
		assertTrue( kb.addAnnotation( s, p, o2 ) );
		assertTrue( kb.isIndividual( s ) );
		assertTrue( kb.isAnnotationProperty( p ) );
		assertFalse( kb.isIndividual( o1 ) );
		assertFalse( kb.isIndividual( o2 ) );
	}

	@Test
	public void getAnnotations1() {		
		KnowledgeBase kb = new KnowledgeBase();

		ATermAppl s = ATermUtils.makeTermAppl( "i" );
		ATermAppl p = ATermUtils.makeTermAppl( "p" );
		ATermAppl o = ATermUtils.makeTermAppl( "j" );

		kb.addIndividual( s );
		kb.addAnnotationProperty( p );

		assertTrue( kb.addAnnotation( s, p, o ) );

		Set<ATermAppl> actual = kb.getAnnotations( s, p );
		Set<ATermAppl> expected = new HashSet<ATermAppl>();
		expected.add( o );
		assertEquals( expected, actual );

		actual = kb.getAnnotations( null, p );
		assertTrue( actual.isEmpty() );

		actual = kb.getAnnotations( s, null );
		assertTrue( actual.isEmpty() );
	}

	@Test
	public void getAnnotations2() {		
		KnowledgeBase kb = new KnowledgeBase();

		ATermAppl s = ATermUtils.makeTermAppl( "i" );
		ATermAppl p = ATermUtils.makeTermAppl( "p" );
		ATermAppl o1 = ATermUtils.makeTermAppl( "j1" );
		ATermAppl o2 = ATermUtils.makeTermAppl( "j2" );

		kb.addIndividual( s );
		kb.addAnnotationProperty( p );

		assertTrue( kb.addAnnotation( s, p, o1 ) );
		assertTrue( kb.addAnnotation( s, p, o2 ) );

		Set<ATermAppl> actual = kb.getAnnotations( s, p );
		Set<ATermAppl> expected = new HashSet<ATermAppl>();
		expected.add( o1 );
		expected.add( o2 );
		assertEquals( expected, actual );

		actual = kb.getAnnotations( null, p );
		assertTrue( actual.isEmpty() );

		actual = kb.getAnnotations( s, null );
		assertTrue( actual.isEmpty() );
	}

	@Test
	public void getAnnotations3() {	
		KnowledgeBase kb = new KnowledgeBase();

		ATermAppl s1 = ATermUtils.makeTermAppl( "s1" );
		ATermAppl p1 = ATermUtils.makeTermAppl( "p1" );
		ATermAppl o1 = ATermUtils.makeTermAppl( "o1" );

		ATermAppl s2 = ATermUtils.makeTermAppl( "s2" );
		ATermAppl p2 = ATermUtils.makeTermAppl( "p2" );
		ATermAppl o2 = ATermUtils.makeTermAppl( "o2" );

		ATermAppl o3 = ATermUtils.makeTermAppl( "o3" );

		kb.addIndividual( s1 );
		kb.addIndividual( s2 );
		kb.addAnnotationProperty( p1 );
		kb.addAnnotationProperty( p2 );

		assertTrue( kb.addAnnotation( s1, p1, o1 ) );
		assertTrue( kb.addAnnotation( s1, p2, o2 ) );
		assertTrue( kb.addAnnotation( s2, p2, o3 ) );

		Set<ATermAppl> actual = kb.getAnnotations( s1, p1 );
		Set<ATermAppl> expected = new HashSet<ATermAppl>();
		expected.add( o1 );
		assertEquals( expected, actual );

		actual = kb.getAnnotations( null, p2 );
		assertTrue( actual.isEmpty() );

		actual = kb.getAnnotations( s1, null );
		assertTrue( actual.isEmpty() );
	}

	@Test
	public void getAnnotations4() {
		// Test kb.getAnnotationProperties()		
		KnowledgeBase kb = new KnowledgeBase();

		ATermAppl s = ATermUtils.makeTermAppl( "s" );
		ATermAppl p = ATermUtils.makeTermAppl( "p" );
		ATermAppl o = ATermUtils.makeTermAppl( "o" );

		kb.addIndividual( s );
		kb.addAnnotationProperty( p );

		assertTrue( kb.addAnnotation( s, p, o ) );

		Set<ATermAppl> actual = kb.getAnnotationProperties();
		Set<ATermAppl> expected = new HashSet<ATermAppl>();
		expected.add( p );

		assertEquals( expected, actual );
	}

	@Test
	public void getAnnotations5() {
		// Test kb.getProperties()		
		KnowledgeBase kb = new KnowledgeBase();

		ATermAppl s = ATermUtils.makeTermAppl( "s" );
		ATermAppl p = ATermUtils.makeTermAppl( "p" );
		ATermAppl o = ATermUtils.makeTermAppl( "o" );

		kb.addIndividual( s );
		kb.addAnnotationProperty( p );

		assertTrue( kb.addAnnotation( s, p, o ) );

		Set<ATermAppl> actual = kb.getProperties();
		Set<ATermAppl> expected = new HashSet<ATermAppl>();
		expected.add( p );
		expected.add( ATermUtils.TOP_OBJECT_PROPERTY );
		expected.add( ATermUtils.BOTTOM_OBJECT_PROPERTY );
		expected.add( ATermUtils.TOP_DATA_PROPERTY );
		expected.add( ATermUtils.BOTTOM_DATA_PROPERTY );

		assertEquals( expected, actual );
	}

	@Test
	public void getAnnotations6() {		
		KnowledgeBase kb = new KnowledgeBase();

		ATermAppl s = ATermUtils.makeTermAppl( "s" );
		ATermAppl p = ATermUtils.makeTermAppl( "p" );
		ATermAppl o = ATermUtils.makeTermAppl( "o" );

		kb.addIndividual( s );
		kb.addAnnotationProperty( p );

		assertTrue( kb.addAnnotation( s, p, o ) );

		Set<ATermAppl> actual = kb.getIndividualsWithAnnotation( p, o );
		Set<ATermAppl> expected = new HashSet<ATermAppl>();
		expected.add( s );

		assertEquals( expected, actual );
	}

	@Test
	public void testJenaLoader1() {		
		OntModel model = ModelFactory.createOntologyModel( PelletReasonerFactory.THE_SPEC );

		Resource s = ResourceFactory.createResource( "i" );
		Property p = RDFS.label;
		Literal o = ResourceFactory.createPlainLiteral( "o" );

		model.add( s, p, o );
		model.prepare();

		ATermAppl st = ATermUtils.makeTermAppl( "i" );
		ATermAppl pt = ATermUtils.makeTermAppl( RDFS.label.getURI() );
		ATermAppl ot = ATermUtils.makePlainLiteral( "o" );

		KnowledgeBase kb = ((PelletInfGraph) model.getGraph()).getKB();
		Set<ATermAppl> actual = kb.getAnnotations( st, pt );

		Set<ATermAppl> expected = new HashSet<ATermAppl>();
		expected.add( ot );

		assertEquals( expected, actual );
	}

	@Test
	public void testJenaLoader2() {		
		OntModel model = ModelFactory.createOntologyModel( PelletReasonerFactory.THE_SPEC );

		Resource s1 = ResourceFactory.createResource( "i" );
		Property p1 = RDFS.label;
		Literal o1 = ResourceFactory.createPlainLiteral( "o1" );

		Property p2 = RDFS.comment;
		Literal o2 = ResourceFactory.createPlainLiteral( "o2" );

		model.add( s1, p1, o1 );
		model.add( s1, p2, o2 );
		model.prepare();

		ATermAppl st = ATermUtils.makeTermAppl( "i" );

		KnowledgeBase kb = ((PelletInfGraph) model.getGraph()).getKB();
		Set<ATermAppl> actual = kb.getAnnotations( st, null );

		assertTrue( actual.isEmpty() );
	}

	@Test
	public void testJenaLoader3() {		
		OntModel model = ModelFactory.createOntologyModel( PelletReasonerFactory.THE_SPEC );
		model.read( DATA1_TTL, "N3" );
		model.prepare();

		ATermAppl i = ATermUtils.makeTermAppl( "http://example.org#i" );
		ATermAppl label = ATermUtils.makeTermAppl( RDFS.label.getURI() );
		ATermAppl o1 = ATermUtils.makePlainLiteral( "o1" );

		KnowledgeBase kb = ((PelletInfGraph) model.getGraph()).getKB();
		Set<ATermAppl> actual = kb.getAnnotations( i, label );

		Set<ATermAppl> expected = new HashSet<ATermAppl>();
		expected.add( o1 );

		assertEquals( expected, actual );
	}

	@Test
	public void testOWLAPILoader() throws OWLOntologyCreationException {
		KnowledgeBase kb = new OWLAPILoader().createKB( DATA1_RDF );

		ATermAppl i = ATermUtils.makeTermAppl( "http://example.org#i" );
		ATermAppl label = ATermUtils.makeTermAppl( RDFS.label.getURI() );
		ATermAppl o1 = ATermUtils.makePlainLiteral( "o1" );

		Set<ATermAppl> actual = kb.getAnnotations( i, label );

		Set<ATermAppl> expected = new HashSet<ATermAppl>();
		expected.add( o1 );

		assertEquals( expected, actual );
	}

	@Test
	public void testCombinedQueryEngine() {
		// This tests annotations using the SPARQL-DL combined query engine
		OntModel model = ModelFactory.createOntologyModel( PelletReasonerFactory.THE_SPEC );
		model.read( DATA1_RDF );

		Query query = QueryFactory.read( QUERY1_RQ );
		QueryExecution qe = SparqlDLExecutionFactory.create( query, model );

		ResultSet rs = qe.execSelect();

		while( rs.hasNext() ) {
			QuerySolution qs = rs.nextSolution();
			Resource s = qs.getResource( "s" );
			Literal o = qs.getLiteral( "o" );

			assertEquals( "http://example.org#i", s.getURI() );
			assertEquals( "o2", o.getLexicalForm() );
		}
	}
	

	
	@Test
	public void test412() {
		KnowledgeBase kb = new KnowledgeBase();
		
		ATermAppl p = term("p");
		ATermAppl q = term("q");
		ATermAppl r = term("r");
		ATermAppl s = term("s");
		
		kb.addAnnotationProperty(p);
		kb.addAnnotationProperty(q);
		kb.addAnnotationProperty(r);
		kb.addAnnotationProperty(s);
		
		kb.addSubProperty(p, q);
		kb.addSubProperty(q, r);
		kb.addSubProperty(r, s);
		
		// The set of sub/super roles at this point are correct for each role
		assertEquals(singletonSets(p, r, q), kb.getSubProperties(s));
	}

	@Test
	public void getAnnotationsCopy() {		
		KnowledgeBase kb = new KnowledgeBase();

		ATermAppl s = ATermUtils.makeTermAppl( "s" );
		ATermAppl p = ATermUtils.makeTermAppl( "p" );
		ATermAppl o = ATermUtils.makeTermAppl( "o" );

		kb.addIndividual( s );
		kb.addAnnotationProperty( p );

		assertTrue( kb.addAnnotation( s, p, o ) );

		assertEquals( Collections.singleton( o ), kb.getAnnotations( s, p ) );
		
		assertEquals( Collections.singleton( o ), kb.copy().getAnnotations( s, p ) );
	}

	public static <T> Set<Set<T>> singletonSets(T... es) {
		Set<Set<T>> set = new HashSet<Set<T>>();
		for( T e : es ) {
			set.add( Collections.singleton( e ) );
		}
		return set;
	}
}
