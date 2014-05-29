// Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public
// License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of
// proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package org.mindswap.pellet.test.inctest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assume.assumeTrue;
import static org.mindswap.pellet.test.PelletTestCase.assertIteratorValues;
import static org.mindswap.pellet.test.PelletTestCase.assertPropertyValues;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Properties;

import junit.framework.JUnit4TestAdapter;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.mindswap.pellet.PelletOptions;
import org.mindswap.pellet.jena.PelletInfGraph;
import org.mindswap.pellet.jena.PelletReasonerFactory;

import com.clarkparsia.jena.test.AbstractJenaTests;
import com.clarkparsia.pellet.utils.PropertiesBuilder;
import com.hp.hpl.jena.datatypes.TypeMapper;
import com.hp.hpl.jena.ontology.DatatypeProperty;
import com.hp.hpl.jena.ontology.Individual;
import com.hp.hpl.jena.ontology.IntersectionClass;
import com.hp.hpl.jena.ontology.ObjectProperty;
import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.UnionClass;
import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.RDFList;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.vocabulary.OWL;
import com.hp.hpl.jena.vocabulary.RDF;
import com.hp.hpl.jena.vocabulary.XSD;

/**
 * Unit tests for incremental consistency checking using Jena API.
 * 
 * @author Christian Halaschek-Wiener
 * @author Evren Sirin
 */

@RunWith(Parameterized.class)
public class IncJenaConsistencyTests extends AbstractJenaTests {
	@Parameterized.Parameters
	public static Collection<Object[]> getTestCases() {
		ArrayList<Object[]> cases = new ArrayList<Object[]>();
		cases.add( new Object[] { false, false, false } );
		cases.add( new Object[] { true, false, false } );
		cases.add( new Object[] { true, true, false } );
		cases.add( new Object[] { true, true, true } );
		return cases;
	}
	
	private Properties newOptions, oldOptions;

	public static junit.framework.Test suite() {
		return new JUnit4TestAdapter( IncJenaConsistencyTests.class );
	}

	public IncJenaConsistencyTests(boolean ucq, boolean uic, boolean uid) {
		PropertiesBuilder pb = new PropertiesBuilder();
		pb.set( "USE_COMPLETION_QUEUE", String.valueOf( ucq ) );
		pb.set( "USE_INCREMENTAL_CONSISTENCY", String.valueOf( uic ) );
		pb.set( "USE_INCREMENTAL_DELETION", String.valueOf( uid ) );
		
		newOptions = pb.build();
	}

	@Override
    @Before
	public void before() {
		oldOptions = PelletOptions.setOptions( newOptions );
		
		super.before();		
	}

	@Override
    @After
	public void after() throws Exception {
		PelletOptions.setOptions( oldOptions );
		
		super.after();
	}

	@Test
	public void testTBoxChange() {
		String ns = "http://www.example.org/test#";

		OntModel model = ModelFactory.createOntologyModel( PelletReasonerFactory.THE_SPEC );
		model.setStrictMode( false );

		DatatypeProperty p = model.createDatatypeProperty( ns + "p" );
		p.addRDFType( OWL.InverseFunctionalProperty );
		p.addRange( XSD.xboolean );

		OntClass C = model.createClass( ns + "C" );
		C.addSuperClass( model.createCardinalityRestriction( null, p, 1 ) );

		Individual i1 = model.createIndividual( ns + "i1", C );
		Individual i2 = model.createIndividual( ns + "i2", C );
		Individual i3 = model.createIndividual( ns + "i3", C );

		// check consistency
		model.prepare();

		OntClass D = model.createClass( ns + "D" );
		OntClass E = model.createClass( ns + "E" );
		D.addDisjointWith( E );

		// add individual
		Individual i4 = model.createIndividual( ns + "i4", D );

		PelletInfGraph graph = (PelletInfGraph) model.getGraph();

		model.prepare();

		// check that the update occurred and that the incremental consistency
		// was used
		assertTrue( graph.getKB().timers.getTimer( "isIncConsistent" ) == null );
		assertIteratorValues( model.listIndividuals(), new Resource[] { i1, i2, i3, i4 } );

		i4.addRDFType( C );
		model.prepare();
		assertTrue( !PelletOptions.USE_INCREMENTAL_CONSISTENCY || graph.getKB().timers.getTimer( "isIncConsistent" ).getCount() == 1 );
	}

	@Ignore("This test is know to fail when the processing order of disjoint axiom changes.")
	@Test
	public void testTypeAssertions() {
		String ns = "http://www.example.org/test#";

		OntModel model = ModelFactory.createOntologyModel( PelletReasonerFactory.THE_SPEC );
		model.setStrictMode( false );

		DatatypeProperty p = model.createDatatypeProperty( ns + "p" );
		p.addRDFType( OWL.InverseFunctionalProperty );
		p.addRange( XSD.xboolean );

		OntClass C = model.createClass( ns + "C" );
		C.addSuperClass( model.createCardinalityRestriction( null, p, 1 ) );

		OntClass D = model.createClass( ns + "D" );
		OntClass E = model.createClass( ns + "E" );
		D.addDisjointWith( E );

		RDFList conj = model.createList( new RDFNode[] { D, C } );
		OntClass CONJ = model.createIntersectionClass( null, conj );

		Individual i1 = model.createIndividual( ns + "i1", C );
		i1.addRDFType( D );
		Individual i2 = model.createIndividual( ns + "i2", C );
		i2.addRDFType( D );
		Individual i3 = model.createIndividual( ns + "i3", C );
		i3.addRDFType( E );

		// check consistency
		model.prepare();

		// add individual
		Individual i4 = model.createIndividual( ns + "i4", D );

		PelletInfGraph graph = (PelletInfGraph) model.getGraph();
		model.prepare();

		// check that the update occurred and that the incremental consistency
		// was used
		assertTrue( graph.getKB().timers.getTimer( "isIncConsistent" ).getCount() > 0 );
		assertIteratorValues( model.listIndividuals(), new Resource[] { i1, i2, i3, i4 } );

		i4.addRDFType( model.createCardinalityRestriction( null, p, 1 ) );
		graph.getKB().timers.getTimer( "isIncConsistent" ).reset();

		model.prepare();

		// check that incremental consistency was not used
		assertTrue( graph.getKB().timers.getTimer( "isIncConsistent" ).getCount() == 0 );

		i4.addRDFType( E );

		// check that the kb is now inconsistent and that incremental
		// consistency was used
		assertFalse( ((PelletInfGraph) model.getGraph()).isConsistent() );
		assertTrue( graph.getKB().timers.getTimer( "isIncConsistent" ).getCount() > 0 );

		i4.removeRDFType( E );

		graph.getKB().timers.getTimer( "isIncConsistent" ).reset();

		model.prepare();

		// check that the kb is now inconsistent and that incremental
		// consistency was used
		assertTrue( ((PelletInfGraph) model.getGraph()).isConsistent() );
		assertTrue( graph.getKB().timers.getTimer( "isIncConsistent" ).getCount() == 0 );

		ObjectProperty op = model.createObjectProperty( ns + "op" );
		i2.addProperty( op, i4 );

		model.prepare();

		// check that the kb is now inconsistent and that incremental
		// consistency was used
		assertTrue( ((PelletInfGraph) model.getGraph()).isConsistent() );
		assertTrue( graph.getKB().timers.getTimer( "isIncConsistent" ).getCount() == 0 );

		i2.addRDFType( CONJ );

		model.prepare();

		// check that the kb is now inconsistent and that incremental
		// consistency was used
		assertTrue( ((PelletInfGraph) model.getGraph()).isConsistent() );
		assertTrue( graph.getKB().timers.getTimer( "isIncConsistent" ).getCount() == 0 );
	}

	@Test
	public void testPropertyAssertions() {
		String ns = "http://www.example.org/test#";

		OntModel model = ModelFactory.createOntologyModel( PelletReasonerFactory.THE_SPEC );
		model.setStrictMode( false );

		DatatypeProperty dp = model.createDatatypeProperty( ns + "dp" );

		ObjectProperty op = model.createObjectProperty( ns + "op" );

		OntClass C = model.createClass( ns + "C" );
		Individual a = model.createIndividual( ns + "a", C );
		Individual b = model.createIndividual( ns + "b", C );

		Literal one = model.createTypedLiteral( "1", TypeMapper.getInstance().getTypeByName(
				XSD.positiveInteger.getURI() ) );
		a.addProperty( dp, one );

		model.prepare();

		PelletInfGraph graph = (PelletInfGraph) model.getGraph();

		assertTrue( graph.getKB().timers.getTimer( "isIncConsistent" ) == null );
		assertIteratorValues( a.listPropertyValues( dp ), new Literal[] { one } );

		a.addProperty( op, b );

		// check consistency
		model.prepare();

		assertIteratorValues( a.listPropertyValues( op ), new Resource[] { b } );
		// check that the update occurred and that the incremental consistency
		// was used
		assertTrue( !PelletOptions.USE_INCREMENTAL_CONSISTENCY || graph.getKB().timers.getTimer( "isIncConsistent" ).getCount() == 1 );

		Literal two = model.createTypedLiteral( "2", TypeMapper.getInstance().getTypeByName(
				XSD.positiveInteger.getURI() ) );
		b.addProperty( dp, two );

		graph.getKB().isConsistent();

		assertIteratorValues( b.listPropertyValues( dp ), new Literal[] { two } );
		// check that the update occurred and that the incremental consistency
		// was used
		assertTrue( !PelletOptions.USE_INCREMENTAL_CONSISTENCY || graph.getKB().timers.getTimer( "isIncConsistent" ).getCount() == 2 );
	}

	@Test
	public void testBnodeUpdates() {
		String ns = "http://www.example.org/test#";

		OntModel model = ModelFactory.createOntologyModel( PelletReasonerFactory.THE_SPEC );
		model.setStrictMode( false );

		DatatypeProperty dp = model.createDatatypeProperty( ns + "dp" );

		ObjectProperty op = model.createObjectProperty( ns + "op" );

		OntClass C = model.createClass( ns + "C" );
		Individual anon1 = model.createIndividual( C );
		Individual a = model.createIndividual( ns + "a", C );

		Literal one = model.createTypedLiteral( "1", TypeMapper.getInstance().getTypeByName(
				XSD.positiveInteger.getURI() ) );
		a.addProperty( dp, one );

		model.prepare();

		PelletInfGraph graph = (PelletInfGraph) model.getGraph();

		assertTrue( graph.getKB().timers.getTimer( "isIncConsistent" ) == null );
		assertIteratorValues( a.listPropertyValues( dp ), new Literal[] { one } );

		a.addProperty( op, anon1 );

		// check consistency
		model.prepare();

		assertIteratorValues( a.listPropertyValues( op ), new Resource[] { anon1 } );
		// check that the update occurred and that the incremental consistency
		// was used
		assertTrue( !PelletOptions.USE_INCREMENTAL_CONSISTENCY || graph.getKB().timers.getTimer( "isIncConsistent" ).getCount() > 0 );

		Individual anon2 = model.createIndividual( C );
		anon2.addProperty( op, a );

		// check consistency
		model.prepare();

		assertIteratorValues( anon2.listPropertyValues( op ), new Resource[] { a } );
		// check that the update occurred and that the incremental consistency
		// was used
		assertTrue( !PelletOptions.USE_INCREMENTAL_CONSISTENCY || graph.getKB().timers.getTimer( "isIncConsistent" ).getCount() == 2 );
	}

	@Test
	public void testAnonClasses() {
		assumeTrue( PelletOptions.USE_INCREMENTAL_CONSISTENCY );

		OntModel ontmodel = ModelFactory.createOntologyModel( PelletReasonerFactory.THE_SPEC );

		String nc = "urn:test:";

		OntClass class1 = ontmodel.createClass( nc + "C1" );
		OntClass class2 = ontmodel.createClass( nc + "C2" );

		Individual[] inds = new Individual[6];
		for( int j = 0; j < 6; j++ ) {
			inds[j] = ontmodel.createIndividual( nc + "Ind" + j, OWL.Thing );
		}

		inds[0].addRDFType( class1 );
		inds[1].addRDFType( class1 );
		inds[2].addRDFType( class1 );
		inds[3].addRDFType( class1 );

		inds[2].addRDFType( class2 );
		inds[3].addRDFType( class2 );
		inds[4].addRDFType( class2 );
		inds[5].addRDFType( class2 );

		ontmodel.prepare();

		assertIteratorValues( class1.listInstances(), new Resource[] {
				inds[0], inds[1], inds[2], inds[3] } );

		assertIteratorValues( class2.listInstances(), new Resource[] {
				inds[2], inds[3], inds[4], inds[5] } );

		PelletInfGraph graph = (PelletInfGraph) ontmodel.getGraph();

		//assertTrue( graph.getKB().timers.getTimer( "isIncConsistent" ) == null );
		long prevCount = graph.getKB().timers.getTimer( "isIncConsistent" ) == null ?
			0 : graph.getKB().timers.getTimer( "isIncConsistent" ).getCount();
		
		inds[4].addRDFType( class1 );
		inds[5].addRDFType( class1 );

		ontmodel.prepare();

		assertIteratorValues( class1.listInstances(), new Resource[] {
				inds[0], inds[1], inds[2], inds[3], inds[4], inds[5] } );

		assertTrue( prevCount < graph.getKB().timers.getTimer( "isIncConsistent" ).getCount() );

		graph.getKB().timers.getTimer( "isIncConsistent" ).reset();

		RDFList list = ontmodel.createList( new RDFNode[] { class1, class2 } );

		IntersectionClass class3 = ontmodel.createIntersectionClass( null, list );

		UnionClass class4 = ontmodel.createUnionClass( null, list );

		graph.getKB().timers.getTimer( "isIncConsistent" ).reset();
		
		ontmodel.prepare();

		assertIteratorValues( class3.listInstances(), new Resource[] {
				inds[2], inds[3], inds[4], inds[5] } );

		assertIteratorValues( class4.listInstances(), new Resource[] {
				inds[0], inds[1], inds[2], inds[3], inds[4], inds[5] } );

		assertEquals( 0, graph.getKB().timers.getTimer( "isIncConsistent" ).getCount() );

		Individual newind = ontmodel.createIndividual( nc + "Ind7", class4 );

		ontmodel.prepare();

		assertIteratorValues( class4.listInstances(), new Resource[] {
				inds[0], inds[1], inds[2], inds[3], inds[4], inds[5], newind } );

	}

	@Test
	public void testSimpleTypeAssertion() {
		String ns = "urn:test:";

		OntModel model = ModelFactory.createOntologyModel( PelletReasonerFactory.THE_SPEC );

		// add one instance relation
		OntClass cls = model.createClass( ns + "C" );
		Individual a = model.createIndividual( ns + "a", cls );

		// load everything and check consistency
		assertTrue( model.validate().isValid() );

		// add a type relation for an existing individual
		a.addRDFType( cls );

		// verify instance relation
		assertTrue( model.contains( a, RDF.type, cls ) );
		assertIteratorValues( cls.listInstances( false ), new Resource[] { a } );
		// check for direct types to make sure we don't get results from base
		// graph
		assertIteratorValues( cls.listInstances( true ), new Resource[] { a } );

		// add a new instance relation to a new individual
		Individual b = model.createIndividual( ns + "b", cls );

		// verify inference
		assertTrue( model.contains( b, RDF.type, cls ) );
		assertIteratorValues( cls.listInstances( false ), new Resource[] { a, b } );
		// check for direct types to make sure we don't get results from base
		// graph
		assertIteratorValues( cls.listInstances( true ), new Resource[] { a, b } );
	}

	@Test
	public void testSimplePropertyAssertion() {	
		String ns = "urn:test:";

		OntModel model = ModelFactory.createOntologyModel( PelletReasonerFactory.THE_SPEC );

		ObjectProperty p = model.createObjectProperty( ns + "p" );
		ObjectProperty q = model.createObjectProperty( ns + "q" );
		Individual a = model.createIndividual( ns + "a", OWL.Thing );
		Individual b = model.createIndividual( ns + "b", OWL.Thing );

		// use a subproperty to make sure we get inferred results and not just
		// results from raw graph
		p.addSubProperty( q );

		// no property assertion to infer yet
		Model inferences = ModelFactory.createDefaultModel();
		assertPropertyValues( model, q, inferences );

		// add a new property assertion between two existing individuals
		model.add( a, q, b );

		// verify inference using super property
		inferences = ModelFactory.createDefaultModel();
		inferences.add( a, p, b );
		assertPropertyValues( model, p, inferences );

		// add a new property assertion using a new individual
		Individual c = model.createIndividual( ns + "c", OWL.Thing );
		model.add( a, q, c );

		// verify inference using super property
		inferences = ModelFactory.createDefaultModel();
		inferences.add( a, p, b );
		inferences.add( a, p, c );
		assertPropertyValues( model, p, inferences );
	}
	
	@Test
	public void addTypeToMergedNode() {
		classes( A, B, C );
		individuals( a, b, c );

		// a is either b or c
		model.add( a, RDF.type, oneOf( b, c ) );
		model.add( a, RDF.type, A );
		model.add( b, RDF.type, B );
		model.add( c, RDF.type, C );

		assertConsistent();

		assertTrue( model.contains( a, RDF.type, A ) );
		// we don't know which equality holds
		assertFalse( model.contains( a, RDF.type, B ) );
		assertFalse( model.contains( a, RDF.type, C ) );
		assertFalse( model.contains( a, RDF.type, D ) );		
		
		model.add( a, RDF.type, D );

		assertConsistent();

		assertTrue( model.contains( a, RDF.type, A ) );
		assertFalse( model.contains( a, RDF.type, B ) );
		assertFalse( model.contains( a, RDF.type, C ) );
		assertTrue( model.contains( a, RDF.type, D ) );	
	}
	
	@Test
	public void removeTypeFromMergedNode() {
		classes( A, B, C, D );
		individuals( a, b, c );

		// a is either b or c
		model.add( a, RDF.type, oneOf( b, c ) );
		model.add( a, RDF.type, A );
		model.add( b, RDF.type, B );
		model.add( c, RDF.type, C );
		model.add( a, RDF.type, D );

		assertConsistent();

		assertTrue( model.contains( a, RDF.type, A ) );
		assertFalse( model.contains( a, RDF.type, B ) );
		assertFalse( model.contains( a, RDF.type, C ) );
		assertTrue( model.contains( a, RDF.type, D ) );	
		
		model.remove( a, RDF.type, D );
		
		assertConsistent();

		assertTrue( model.contains( a, RDF.type, A ) );
		assertFalse( model.contains( a, RDF.type, B ) );
		assertFalse( model.contains( a, RDF.type, C ) );
		assertFalse( model.contains( a, RDF.type, D ) );	
	}	
		
	public static void main(String[] args) {
		IncJenaConsistencyTests test = new IncJenaConsistencyTests(true,true,false);
		test.before();
		test.testTBoxChange();
		
	}
}
