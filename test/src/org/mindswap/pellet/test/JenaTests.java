// Portions Copyright (c) 2006 - 2008, Clark & Parsia, LLC.
// <http://www.clarkparsia.com>
// Clark & Parsia, LLC parts of this source code are available under the terms
// of the Affero General Public License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of
// proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package org.mindswap.pellet.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mindswap.pellet.test.PelletTestCase.addStatements;
import static org.mindswap.pellet.test.PelletTestCase.assertIteratorContains;
import static org.mindswap.pellet.test.PelletTestCase.assertIteratorValues;
import static org.mindswap.pellet.test.PelletTestCase.assertPropertyValues;

import java.io.StringReader;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import junit.framework.JUnit4TestAdapter;

import org.junit.Ignore;
import org.junit.Test;
import org.mindswap.pellet.KnowledgeBase;
import org.mindswap.pellet.PelletOptions;
import org.mindswap.pellet.jena.ModelExtractor;
import org.mindswap.pellet.jena.PelletInfGraph;
import org.mindswap.pellet.jena.PelletReasoner;
import org.mindswap.pellet.jena.PelletReasonerFactory;
import org.mindswap.pellet.jena.vocabulary.OWL2;
import org.mindswap.pellet.utils.ATermUtils;
import org.semanticweb.owl.model.OWLException;

import aterm.ATermAppl;

import com.clarkparsia.pellet.datatypes.DatatypeReasoner;
import com.clarkparsia.pellet.datatypes.DatatypeReasonerImpl;
import com.clarkparsia.pellet.utils.PropertiesBuilder;
import com.hp.hpl.jena.datatypes.TypeMapper;
import com.hp.hpl.jena.graph.compose.Union;
import com.hp.hpl.jena.ontology.AnnotationProperty;
import com.hp.hpl.jena.ontology.DatatypeProperty;
import com.hp.hpl.jena.ontology.Individual;
import com.hp.hpl.jena.ontology.IntersectionClass;
import com.hp.hpl.jena.ontology.ObjectProperty;
import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.ontology.OntProperty;
import com.hp.hpl.jena.ontology.Restriction;
import com.hp.hpl.jena.ontology.UnionClass;
import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFList;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.ResourceFactory;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.rdf.model.impl.StatementImpl;
import com.hp.hpl.jena.reasoner.Reasoner;
import com.hp.hpl.jena.vocabulary.OWL;
import com.hp.hpl.jena.vocabulary.RDF;
import com.hp.hpl.jena.vocabulary.RDFS;
import com.hp.hpl.jena.vocabulary.ReasonerVocabulary;
import com.hp.hpl.jena.vocabulary.XSD;

public class JenaTests {
	public static String	base	= "file:" + PelletTestSuite.base + "misc/";

	public static void main(String args[]) {
		junit.textui.TestRunner.run( JenaTests.suite() );
	}

	public static junit.framework.Test suite() {
		return new JUnit4TestAdapter( JenaTests.class );
	}

	@Test
	public void testIncrementalABoxAddition() throws MalformedURLException {
		OntModel model = ModelFactory.createOntologyModel( PelletReasonerFactory.THE_SPEC, null );

		ObjectProperty p = model.createObjectProperty( "p" );
		ObjectProperty t = model.createObjectProperty( "t" );
		DatatypeProperty q = model.createDatatypeProperty( "q" );
		AnnotationProperty r = model.createAnnotationProperty( "r" );

		Individual a = model.createIndividual( "a", OWL.Thing );
		Individual b = model.createIndividual( "b", OWL.Thing );

		model.add( t, RDF.type, OWL.TransitiveProperty );

		model.prepare();

		Resource bnode1 = model.createResource();
		Resource bnode2 = model.createResource();
		Resource c = ResourceFactory.createResource();
		Resource d = ResourceFactory.createResource();

		model.add( a, p, b );
		model.add( d, p, b );
		model.add( a, p, c );
		model.add( a, q, model.createLiteral( "l" ) );

		model.add( a, t, bnode1 );
		model.add( bnode1, t, bnode2 );

		assertTrue( model.contains( a, t, bnode2 ) );

		model.prepare();

		model.add( a, r, model.createLiteral( "l" ) );
		model.add( a, r, model.createResource() );

		assertTrue( model.validate().isValid() );

		model.prepare();

		model.add( p, RDF.type, OWL.FunctionalProperty );
		model.add( b, OWL.differentFrom, c );

		model.prepare();

		assertFalse( model.validate().isValid() );

		model.remove( b, OWL.differentFrom, c );

		assertTrue( model.validate().isValid() );

		model.prepare();

		model.add( p, RDF.type, OWL.InverseFunctionalProperty );
		model.add( a, OWL.differentFrom, d );

		assertFalse( model.validate().isValid() );
	}

	@Test
	public void testInverse() {
		String ns = "http://www.example.org/test#";

		OntModel ont = ModelFactory.createOntologyModel( PelletReasonerFactory.THE_SPEC, null );

		ObjectProperty p1 = ont.createObjectProperty( ns + "p1" );
		ObjectProperty p2 = ont.createObjectProperty( ns + "p2" );
		ObjectProperty p3 = ont.createObjectProperty( ns + "p3" );

		p2.addSubProperty( p1 );
		p2.addInverseOf( p3 );

		Individual s1 = ont.createIndividual( ns + "s1", OWL.Thing );
		Individual o1 = ont.createIndividual( ns + "o1", OWL.Thing );
		Individual s2 = ont.createIndividual( ns + "s2", OWL.Thing );
		Individual o2 = ont.createIndividual( ns + "o2", OWL.Thing );
		Individual s3 = ont.createIndividual( ns + "s3", OWL.Thing );
		Individual o3 = ont.createIndividual( ns + "o3", OWL.Thing );

		s1.addProperty( p1, o1 );
		s2.addProperty( p2, o2 );
		s3.addProperty( p3, o3 );

		Statement stmt = ont.createStatement( o1, p3, s1 );
		assertIteratorContains( o1.listPropertyValues( p3 ), s1 );
		assertTrue( ont.contains( stmt ) );
		assertIteratorContains( ont.listStatements( null, p3, (RDFNode) null ), stmt );
		assertIteratorContains( ont.listStatements( o1, null, (RDFNode) null ), stmt );
		assertIteratorContains( ont.listStatements(), stmt );
		assertFalse( ont.contains( o3, p1, s3 ) );

		assertTrue( p2.isInverseOf( p3 ) );
		assertIteratorValues( p2.listInverseOf(), new Property[] { p3 } );
		assertTrue( p3.isInverseOf( p2 ) );
		assertIteratorValues( p3.listInverseOf(), new Property[] { p2 } );
	}

	@Test
	public void testOWL2() {
		String ns = "http://www.example.org/test#";

		OntModel factory = ModelFactory.createOntologyModel( OntModelSpec.OWL_MEM );
		OntModel model = ModelFactory.createOntologyModel( PelletReasonerFactory.THE_SPEC );
		model.read( base + "owl2.owl" );

		OntClass C = model.getOntClass( ns + "C" );
		OntClass D = model.getOntClass( ns + "D" );
		OntClass D1 = model.getOntClass( ns + "D1" );
		OntClass D2 = model.getOntClass( ns + "D2" );
		OntClass D3 = model.getOntClass( ns + "D3" );
		OntClass test1 = model.getOntClass( ns + "test1" );
		OntClass test2 = model.getOntClass( ns + "test2" );
		OntClass test3 = model.getOntClass( ns + "test3" );
		OntClass OlderThan10 = model.getOntClass( ns + "OlderThan10" );
		OntClass YoungerThan20 = model.getOntClass( ns + "youngerThan20" );
		OntClass Teenager = model.getOntClass( ns + "Teenager" );
		OntClass Teen = model.getOntClass( ns + "Teen" );

		Individual ind1 = model.getIndividual( ns + "ind1" );
		Individual ind3 = model.getIndividual( ns + "ind3" );
		Individual ind4 = model.getIndividual( ns + "ind4" );
		Individual ind5 = model.getIndividual( ns + "ind5" );
		Individual ind6 = model.getIndividual( ns + "ind6" );
		Individual ind7 = model.getIndividual( ns + "ind7" );
		Individual ind8 = model.getIndividual( ns + "ind8" );

		DatatypeProperty dp = model.getDatatypeProperty( ns + "dp" );
		ObjectProperty p = model.getObjectProperty( ns + "p" );
		ObjectProperty r = model.getObjectProperty( ns + "r" );
		ObjectProperty invR = model.getObjectProperty( ns + "invR" );
		ObjectProperty ir = model.getObjectProperty( ns + "ir" );
		ObjectProperty as = model.getObjectProperty( ns + "as" );
		ObjectProperty d1 = model.getObjectProperty( ns + "d1" );
		ObjectProperty d2 = model.getObjectProperty( ns + "d2" );

		model.prepare();

		assertTrue( r.hasRDFType( OWL2.ReflexiveProperty ) );
		assertTrue( invR.hasRDFType( OWL2.ReflexiveProperty ) );
		assertTrue( ir.hasRDFType( OWL2.IrreflexiveProperty ) );
		assertTrue( as.hasRDFType( OWL2.AsymmetricProperty ) );

		OntClass union = factory.createUnionClass( null, factory.createList( new RDFNode[] {
				D1, D2, D3 } ) );
		assertTrue( model.listStatements( D, OWL.equivalentClass, union, factory ).hasNext() );
		assertTrue( model.contains( D, OWL.equivalentClass, test1 ) );
		assertTrue( D1.isDisjointWith( D2 ) );
		assertTrue( D1.isDisjointWith( D3 ) );
		assertTrue( D2.isDisjointWith( D3 ) );

		assertTrue( model.contains( d1, OWL2.propertyDisjointWith, d2 ) );
		assertTrue( model.contains( d2, OWL2.propertyDisjointWith, d1 ) );

		assertTrue( model.contains( ind1, r, ind1 ) );
		assertTrue( model.contains( ind1, invR, ind1 ) );
		assertTrue( model.contains( ind1, OWL.differentFrom, ind3 ) );
		assertTrue( model.contains( ind1, OWL.differentFrom, ind4 ) );
		assertTrue( model.contains( ind5, OWL.differentFrom, ind6 ) );
		assertTrue( model.contains( ind1, p, ind1 ) );
		assertTrue( model.contains( ind1, RDF.type, test2 ) );
		assertTrue( model.contains( ind1, RDF.type, test3 ) );
		assertTrue( model.contains( ind7, OWL.differentFrom, ind8 ) );
		assertTrue( model.contains( ind8, OWL.differentFrom, ind7 ) );
		assertTrue( model.contains( ind1, dp, model.createTypedLiteral( false ) ) );
		assertIteratorValues( ind1.listRDFTypes( false ),
				new Object[] { OWL.Thing, C, test2, test3 } );

		assertTrue( model.contains( Teenager, RDFS.subClassOf, OlderThan10 ) );
		assertTrue( model.contains( Teenager, RDFS.subClassOf, YoungerThan20 ) );
		assertTrue( model.contains( Teenager, OWL.equivalentClass, Teen ) );
	}

	@Test
	public void testUncle() {
		String ns = "http://www.example.org/test#";

		OntModel model = ModelFactory.createOntologyModel( PelletReasonerFactory.THE_SPEC );
		model.read( base + "uncle.owl" );

		Individual Bob = model.getIndividual( ns + "Bob" );
		Individual Sam = model.getIndividual( ns + "Sam" );

		Property uncleOf = model.getProperty( ns + "uncleOf" );

		Model uncleValues = ModelFactory.createDefaultModel();
		addStatements( uncleValues, Bob, uncleOf, Sam );
		assertPropertyValues( model, uncleOf, uncleValues );
	}

	@Test
	public void testQualifiedCardinality1() {
		String ns = "http://www.example.org/test#";

		OntModel model = ModelFactory.createOntologyModel( PelletReasonerFactory.THE_SPEC );
		model.read( base + "qcr.owl" );

		OntClass sub = model.getOntClass( ns + "sub" );
		OntClass sup = model.getOntClass( ns + "sup" );

		assertTrue( sub.hasSuperClass( sup ) );
		assertIteratorContains( sub.listSuperClasses(), sup );
		assertIteratorContains( sup.listSubClasses(), sub );
	}

	@Test
	public void testInvalidTransitivity() {
		OntModel ont = ModelFactory.createOntologyModel( OntModelSpec.OWL_MEM );

		OntClass C = ont.createClass( "C" );

		ObjectProperty p1 = ont.createObjectProperty( "p1" );
		p1.addRDFType( OWL.TransitiveProperty );

		ObjectProperty p2 = ont.createObjectProperty( "p2" );

		Individual x = ont.createIndividual( OWL.Thing );
		Individual y = ont.createIndividual( OWL.Thing );
		Individual z = ont.createIndividual( OWL.Thing );

		x.addRDFType( ont.createAllValuesFromRestriction( null, p1, C ) );
		x.addProperty( p1, y );
		y.addProperty( p1, z );

		ont.prepare();

		OntModel pellet = ModelFactory.createOntologyModel( PelletReasonerFactory.THE_SPEC, ont );
		assertTrue( pellet.contains( y, RDF.type, C ) );
		assertTrue( pellet.contains( z, RDF.type, C ) );

		Statement[] statements = new Statement[] {
				ont.createStatement( p1, RDF.type, OWL.FunctionalProperty ),
				ont.createStatement( p1, RDF.type, OWL.InverseFunctionalProperty ),
				ont.createStatement( p1, RDF.type, OWL2.IrreflexiveProperty ),
				ont.createStatement( p1, RDF.type, OWL2.AsymmetricProperty ),
				ont.createStatement( p1, OWL2.propertyDisjointWith, p2 ),
				ont.createStatement( C, RDFS.subClassOf, ont.createMinCardinalityRestriction( null,
						p1, 2 ) ),
				ont
						.createStatement( x, RDF.type, ont.createMaxCardinalityRestriction( null,
								p1, 3 ) ),
				ont.createStatement( C, OWL.disjointWith, ont.createCardinalityRestriction( null,
						p1, 2 ) ), };

		for( int i = 0; i < statements.length; i++ ) {
			ont.add( statements[i] );

			pellet = ModelFactory.createOntologyModel( PelletReasonerFactory.THE_SPEC, ont );
			assertTrue( statements[i].toString(), pellet.contains( y, RDF.type, C ) );
			assertFalse( statements[i].toString(), pellet.contains( z, RDF.type, C ) );

			ont.remove( statements[i] );
		}
	}

	@Test
	public void testInvalidComplexSubRole() {
		OntModel ont = ModelFactory.createOntologyModel( OntModelSpec.OWL_MEM );

		OntClass C = ont.createClass( "C" );

		ObjectProperty p1 = ont.createObjectProperty( "p1" );
		ObjectProperty p2 = ont.createObjectProperty( "p2" );

		RDFList pChain = ont.createList( new RDFNode[] { p1, p2 } );
		ont.add( p1, OWL2.propertyChainAxiom, pChain );

		Individual x = ont.createIndividual( OWL.Thing );
		Individual y = ont.createIndividual( OWL.Thing );
		Individual z = ont.createIndividual( OWL.Thing );

		x.addProperty( p1, y );
		y.addProperty( p2, z );

		ont.prepare();

		OntModel pellet = ModelFactory.createOntologyModel( PelletReasonerFactory.THE_SPEC, ont );
		assertTrue( pellet.contains( x, p1, y ) );
		assertTrue( pellet.contains( x, p1, z ) );

		Restriction min = ont.createMinCardinalityRestriction( null, p1, 2 );
		Restriction max = ont.createMaxCardinalityRestriction( null, p1, 3 );
		Restriction card = ont.createCardinalityRestriction( null, p1, 2 );
		Statement[] statements = new Statement[] {
				ont.createStatement( p1, RDF.type, OWL.FunctionalProperty ),
				ont.createStatement( p1, RDF.type, OWL.InverseFunctionalProperty ),
				ont.createStatement( p1, RDF.type, OWL2.IrreflexiveProperty ),
				ont.createStatement( p1, RDF.type, OWL2.AsymmetricProperty ),
				ont.createStatement( p1, OWL2.propertyDisjointWith, p2 ),
				ont.createStatement( C, RDFS.subClassOf, min ),
				ont.createStatement( x, RDF.type, max ),
				ont.createStatement( C, OWL.disjointWith, card ), };

		for( int i = 0; i < statements.length; i++ ) {
			ont.add( statements[i] );

			pellet = ModelFactory.createOntologyModel( PelletReasonerFactory.THE_SPEC, ont );
			assertTrue( statements[i].toString(), pellet.contains( x, p1, y ) );
			assertFalse( statements[i].toString(), pellet.contains( x, p1, z ) );

			ont.remove( statements[i] );
		}
	}

	@Test
	public void testReflexive2() {
		String ns = "http://www.example.org/test#";
		String foaf = "http://xmlns.com/foaf/0.1/";

		OntModel model = ModelFactory.createOntologyModel( PelletReasonerFactory.THE_SPEC );
		model.read( base + "reflexive.owl" );

		ObjectProperty[] knows = {
				model.getObjectProperty( foaf + "knows" ),
				model.getObjectProperty( ns + "knows2" ), model.getObjectProperty( ns + "knows3" ) };

		Individual[] people = new Individual[5];
		for( int j = 0; j < knows.length; j++ ) {
			Model knowsRelations = ModelFactory.createDefaultModel();

			for( int i = 0; i < people.length; i++ ) {
				people[i] = model.getIndividual( ns + "P" + (i + 1) );

				knowsRelations.add( people[i], knows[j], people[i] );
			}
			assertPropertyValues( model, knows[j], knowsRelations );
		}
	}

	@Test
	public void testEscher1() {
		String ns = "foo://bla/names#";
		String source = "@prefix owl: <http://www.w3.org/2002/07/owl#>.\r\n"
				+ "@prefix owl11: <http://www.w3.org/2006/12/owl11#>.\r\n"
				+ "@prefix rdfs: <http://www.w3.org/2000/01/rdf-schema#>.\r\n"
				+ "@prefix : <foo://bla/names#>.\r\n" + "\r\n"
				+ ":Corner owl:oneOf (:a :b :c);\r\n" + "  rdfs:subClassOf\r\n"
				+ "  [a owl:Restriction; owl:onProperty :higher; owl:cardinality 1].\r\n"
				+ "owl:AllDifferent owl:distinctMembers (:a :b :c).\r\n"
				+ ":higher rdfs:domain :Corner; rdfs:range :Corner.\r\n"
				+ ":higher a owl:FunctionalProperty. ## redundant, note cardinality 1\r\n"
				+ ":higher a owl:AsymmetricProperty.\r\n"
				+ ":higher a owl11:IrreflexiveProperty.\r\n" + ":a :higher :b.\r\n";

		OntModel model = ModelFactory.createOntologyModel( PelletReasonerFactory.THE_SPEC );
		model.read( new StringReader( source ), "", "N3" );
		model.prepare();

		Resource a = model.getResource( ns + "a" );
		Resource b = model.getResource( ns + "b" );
		Resource c = model.getResource( ns + "c" );

		OntProperty higher = model.getOntProperty( ns + "higher" );

		assertIteratorValues( model.listStatements( null, higher, (RDFNode) null ),
				new Statement[] {
						model.createStatement( a, higher, b ),
						model.createStatement( b, higher, c ),
						model.createStatement( c, higher, a ), } );
	}

	@Test
	public void testEscher2() {
		String ns = "foo://bla/names#";
		String source = "@prefix owl: <http://www.w3.org/2002/07/owl#>.\r\n"
				+ "@prefix owl11: <http://www.w3.org/2006/12/owl11#>.\r\n"
				+ "@prefix rdfs: <http://www.w3.org/2000/01/rdf-schema#>.\r\n"
				+ "@prefix : <foo://bla/names#>.\r\n" + "\r\n"
				+ ":Corner owl:oneOf (:a :b :c);\r\n" + "  rdfs:subClassOf\r\n"
				+ "  [a owl:Restriction; owl:onProperty :higher; owl:cardinality 1].\r\n"
				+ "owl:AllDifferent owl:distinctMembers (:a :b :c).\r\n"
				+ ":higher rdfs:domain :Corner; rdfs:range :Corner.\r\n"
				+ ":higher a owl:FunctionalProperty. ## redundant, note cardinality 1\r\n"
				+ ":higher a owl:AsymmetricProperty.\r\n"
				+ ":higher a owl11:IrreflexiveProperty.\r\n" + ":a :higher :b.\r\n"
				+ ":b :higher :d. :d a :Corner.\r\n" + ":c a owl:Thing.\r\n";

		OntModel model = ModelFactory.createOntologyModel( PelletReasonerFactory.THE_SPEC );
		model.read( new StringReader( source ), "", "N3" );
		model.prepare();

		Individual a = model.getIndividual( ns + "a" );
		Individual b = model.getIndividual( ns + "b" );
		Individual c = model.getIndividual( ns + "c" );
		Individual d = model.getIndividual( ns + "d" );

		OntProperty higher = model.getOntProperty( ns + "higher" );

		assertIteratorValues( model.listStatements( null, higher, (RDFNode) null ),
				new Statement[] {
						model.createStatement( a, higher, b ),
						model.createStatement( b, higher, c ),
						model.createStatement( b, higher, d ),
						model.createStatement( c, higher, a ),
						model.createStatement( d, higher, a ), } );

		assertTrue( c.isSameAs( d ) );
		assertTrue( d.isSameAs( c ) );
	}

	@Test
	public void testDatatypeCardinality() {
		String ns = "http://www.example.org/test#";

		OntModel model = ModelFactory.createOntologyModel( PelletReasonerFactory.THE_SPEC );

		OntClass C1 = model.createClass( ns + "C1" );
		OntClass C2 = model.createClass( ns + "C2" );

		DatatypeProperty p = model.createDatatypeProperty( ns + "p" );
		p.addRange( XSD.xboolean );

		C1.addSuperClass( model.createMinCardinalityRestriction( null, p, 2 ) );
		C2.addSuperClass( model.createMinCardinalityRestriction( null, p, 3 ) );

		model.prepare();

		assertTrue( ((PelletInfGraph) model.getGraph()).getKB().isConsistent() );

		assertTrue( !model.contains( C1, RDFS.subClassOf, OWL.Nothing ) );
		assertTrue( model.contains( C2, RDFS.subClassOf, OWL.Nothing ) );
	}

	@Test
	public void testIFDP1() {
		String ns = "http://www.example.org/test#";

		OntModel model = ModelFactory.createOntologyModel( PelletReasonerFactory.THE_SPEC );

		Individual a = model.createIndividual( ns + "a", OWL.Thing );
		Individual b = model.createIndividual( ns + "b", OWL.Thing );
		Individual c = model.createIndividual( ns + "c", OWL.Thing );

		ObjectProperty op = model.createObjectProperty( ns + "op" );
		DatatypeProperty dp = model.createDatatypeProperty( ns + "dp" );
		dp.convertToInverseFunctionalProperty();

		a.addProperty( op, c );

		Literal one = model.createTypedLiteral( Integer.valueOf( 1 ) );
		a.addProperty( dp, one );
		b.addProperty( dp, one );

		model.prepare();

		assertTrue( a.isSameAs( b ) );
		assertIteratorValues( a.listSameAs(), new Resource[] { a, b } );

		assertTrue( b.hasProperty( op, c ) );
		assertIteratorValues( b.listPropertyValues( op ), new Resource[] { c } );
	}

	@Ignore("Inverse functional datatype properties are only supported with asserted literals")
	@Test
	public void testIFDP2() {
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

		Individual i1 = model.createIndividual( ns + "i1", C );
		i1.addRDFType( D );
		Individual i2 = model.createIndividual( ns + "i2", C );
		i2.addRDFType( D );
		Individual i3 = model.createIndividual( ns + "i3", C );
		i3.addRDFType( E );

		model.prepare();

		assertTrue( i1.isSameAs( i2 ) );
		assertIteratorValues( i1.listSameAs(), new Resource[] { i1, i2 } );

		assertTrue( !i1.isSameAs( i3 ) );

		assertFalse( i1.listProperties( p ).hasNext() );
		assertFalse( i2.listProperties( p ).hasNext() );
		assertFalse( i3.listProperties( p ).hasNext() );
	}

	@Test
	public void testIFDP3() {
		String ns = "http://www.example.org/test#";

		OntModel model = ModelFactory.createOntologyModel( PelletReasonerFactory.THE_SPEC );

		DatatypeProperty dp = model.createDatatypeProperty( ns + "dp" );
		dp.addRange( XSD.nonNegativeInteger );
		dp.convertToInverseFunctionalProperty();

		OntClass C = model.createClass( ns + "C" );
		C.addSuperClass( model.createMinCardinalityRestriction( null, dp, 1 ) );

		Individual a = model.createIndividual( ns + "a", C );
		Individual b = model.createIndividual( ns + "b", C );
		Individual c = model.createIndividual( ns + "c", C );

		Literal zero = model.createTypedLiteral( Integer.valueOf( 0 ) );
		a.addProperty( dp, zero );

		b.addRDFType( model.createAllValuesFromRestriction( null, dp, XSD.nonPositiveInteger ) );

		Literal one = model.createTypedLiteral( Integer.valueOf( 1 ) );
		c.addProperty( dp, one );

		model.prepare();

		assertTrue( a.isSameAs( b ) );
		assertTrue( b.isSameAs( a ) );
		assertIteratorValues( a.listSameAs(), new Resource[] { a, b } );
		assertIteratorValues( b.listSameAs(), new Resource[] { a, b } );

		assertTrue( !c.isSameAs( a ) );
		assertTrue( !c.isSameAs( b ) );
	}

	@Test
	public void testDuplicateLiterals() {
		String ns = "http://www.example.org/test#";

		OntModel model = ModelFactory.createOntologyModel( PelletReasonerFactory.THE_SPEC );

		DatatypeProperty dp = model.createDatatypeProperty( ns + "dp" );

		OntClass C = model.createClass( ns + "C" );
		Individual a = model.createIndividual( ns + "a", C );

		Literal one = model.createTypedLiteral( "1", TypeMapper.getInstance().getTypeByName(
				XSD.positiveInteger.getURI() ) );
		a.addProperty( dp, one );

		model.prepare();

		Literal oneDecimal = model.createTypedLiteral( "1", TypeMapper.getInstance().getTypeByName(
				XSD.decimal.getURI() ) );
		
		assertIteratorValues( a.listPropertyValues( dp ), new Literal[] { one } );
		assertTrue( a.hasProperty( dp, oneDecimal ) );
	}

	@Test
	public void testClosedUniverse() {
		String ns = "http://www.example.org/test#";

		OntModel model = ModelFactory.createOntologyModel( PelletReasonerFactory.THE_SPEC );

		model.read( base + "ClosedUniverse.owl" );

		model.prepare();

		// OntClass Sat = model.getOntClass( ns + "Sat" );
		OntClass Unsat = model.getOntClass( ns + "Unsat" );

		// assertTrue( !Sat.hasSuperClass( OWL.Nothing ) );
		assertTrue( Unsat.hasSuperClass( OWL.Nothing ) );
	}

	/**
	 * Verifies that we can parse the OWL 1.1 self restriction RDF syntax
	 */
	@Test
	public void deprecatedSelfRestrictionSyntax() {
		String ns = "http://www.example.org/test#";

		OntModel model = ModelFactory.createOntologyModel( PelletReasonerFactory.THE_SPEC );
		model.read( base + "/deprecatedSelf.owl" );
		model.prepare();
		assertTrue( ((PelletInfGraph) model.getGraph()).getKB().isConsistent() );

		Property knows = model.getProperty( ns + "knows" );
		Individual p1 = model.getIndividual( ns + "P1" );
		Individual p2 = model.getIndividual( ns + "P2" );

		assertTrue( model.contains( p1, knows, p1 ) );
		assertFalse( model.contains( p2, knows, p2 ) );
	}

	/**
	 * Verifies that OWL 2 entity declarations are parsed from RDF/XML and
	 * handled correctly.
	 */
	@Test
	public void entityDeclarations() {
		String ns = "http://www.example.org/test#";

		OntModel model = ModelFactory.createOntologyModel( PelletReasonerFactory.THE_SPEC );
		model.read( base + "/entityDeclarations.owl" );
		model.prepare();

		KnowledgeBase kb = ((PelletInfGraph) model.getGraph()).getKB();
		assertTrue( kb.isConsistent() );

		assertTrue( kb.isIndividual( ATermUtils.makeTermAppl( ns + "a" ) ) );
		assertEquals( 1, kb.getIndividuals().size() );

		assertTrue( kb.isClass( ATermUtils.makeTermAppl( ns + "C" ) ) );
		assertEquals( 1, kb.getClasses().size() );
		assertFalse( kb.isDatatype( ATermUtils.makeTermAppl( ns + "C" ) ) );

		assertFalse( kb.isClass( ATermUtils.makeTermAppl( ns + "D" ) ) );
		assertTrue( kb.isDatatype( ATermUtils.makeTermAppl( ns + "D" ) ) );

		assertTrue( kb.isAnnotationProperty( ATermUtils.makeTermAppl( ns + "p" ) ) );
		/* Test below is 9 because Pellet returns 8 built-in properties */
		assertEquals( 8 + 1, kb.getAnnotationProperties().size() );
		assertFalse( kb.isDatatypeProperty( ATermUtils.makeTermAppl( ns + "p" ) ) );
		assertFalse( kb.isObjectProperty( ATermUtils.makeTermAppl( ns + "p" ) ) );

		assertTrue( kb.isObjectProperty( ATermUtils.makeTermAppl( ns + "q" ) ) );
		// Two built-in object properties.
		assertEquals( 2 + 1, kb.getObjectProperties().size() );
		assertFalse( kb.isAnnotationProperty( ATermUtils.makeTermAppl( ns + "q" ) ) );
		assertFalse( kb.isDatatypeProperty( ATermUtils.makeTermAppl( ns + "q" ) ) );

		assertTrue( kb.isDatatypeProperty( ATermUtils.makeTermAppl( ns + "r" ) ) );
		assertEquals( 2 + 1, kb.getDataProperties().size() );
		assertFalse( kb.isAnnotationProperty( ATermUtils.makeTermAppl( ns + "r" ) ) );
		assertFalse( kb.isObjectProperty( ATermUtils.makeTermAppl( ns + "r" ) ) );
	}

	@Test
	public void test3Sat() {
		String ns = "http://www.example.org/test#";

		OntModel model = ModelFactory.createOntologyModel( PelletReasonerFactory.THE_SPEC );

		model.read( base + "3Sat.owl" );

		String solution = "101";
		int n = solution.length();

		Individual T = model.getIndividual( ns + "T" );
		Individual F = model.getIndividual( ns + "F" );

		model.prepare();

		assertTrue( ((PelletInfGraph) model.getGraph()).getKB().isConsistent() );

		Individual[] positives = new Individual[n + 1];
		Individual[] negatives = new Individual[n + 1];

		positives[0] = T;
		negatives[0] = F;

		for( int i = 1; i <= n; i++ ) {
			boolean t = solution.charAt( i - 1 ) == '1';

			if( t ) {
				positives[i] = model.getIndividual( ns + "plus" + i );
				negatives[i] = model.getIndividual( ns + "minus" + i );
			}
			else {
				positives[i] = model.getIndividual( ns + "minus" + i );
				negatives[i] = model.getIndividual( ns + "plus" + i );
			}

			assertTrue( T + " = " + positives[i], T.isSameAs( positives[i] ) );
			assertTrue( F + " = " + negatives[i], F.isSameAs( negatives[i] ) );
		}

		// System.out.println(
		// ((org.mindswap.pellet.Individual)((PelletInfGraph)model.getGraph()).getKB().getABox().pseudoModel.
		// getIndividual(ATermUtils.makeTermAppl(ns+"T")).getSame()).getTypes(Node.NOM));
		//        
		// System.out.println(
		// ((org.mindswap.pellet.Individual)((PelletInfGraph)model.getGraph()).getKB().getABox().pseudoModel.
		// getIndividual(ATermUtils.makeTermAppl(ns+"F")).getSame()).getTypes(Node.NOM));

		assertIteratorValues( T.listSameAs(), positives );
		assertIteratorValues( F.listSameAs(), negatives );
	}

	@Test
	public void testPropertyRestrictionsInSuperclasses() {
		String ns = "urn:test:";
		OntModelSpec spec = new OntModelSpec( OntModelSpec.OWL_DL_MEM );
		spec.setReasoner( new PelletReasoner() );

		OntModel model = ModelFactory.createOntologyModel( spec, null );

		OntClass X = model.createClass( ns + "X" );
		ObjectProperty hasX = model.createObjectProperty( ns + "hasX" );
		OntClass AllX = model.createAllValuesFromRestriction( null, hasX, X );
		OntClass Y = model.createIntersectionClass( ns + "Y", model.createList( new RDFNode[] {
				X, AllX } ) );

		assertTrue( "AllX is not a superclass of Y", Y.hasSuperClass( AllX ) );
	}

	@Test
	public void testListStatementsDifferentFrom() {
		String ns = "http://www.example.org/test#";

		OntModel ont = ModelFactory.createOntologyModel( PelletReasonerFactory.THE_SPEC, null );

		ObjectProperty p = ont.createInverseFunctionalProperty( ns + "p1", /*
																			 * functional
																			 * =
																			 */true );

		Individual s1 = ont.createIndividual( ns + "s1", OWL.Thing );
		Individual s2 = ont.createIndividual( ns + "s2", OWL.Thing );
		s1.addDifferentFrom( s2 );
		Individual o1 = ont.createIndividual( ns + "o1", OWL.Thing );
		Individual o2 = ont.createIndividual( ns + "o2", OWL.Thing );
		s1.addProperty( p, o1 );
		s2.addProperty( p, o2 );

		Model values = ModelFactory.createDefaultModel();
		addStatements( values, s1, OWL.differentFrom, s2 );
		addStatements( values, s2, OWL.differentFrom, s1 );
		addStatements( values, o1, OWL.differentFrom, o2 );
		addStatements( values, o2, OWL.differentFrom, o1 );
		assertPropertyValues( ont, OWL.differentFrom, values );
	}

	@Test
	public void testListStatementsSameAs() {
		String ns = "http://www.example.org/test#";

		OntModel ont = ModelFactory.createOntologyModel( PelletReasonerFactory.THE_SPEC, null );

		ObjectProperty p = ont.createObjectProperty( ns + "p", /* functional = */true );

		Individual s = ont.createIndividual( ns + "s", OWL.Thing );
		Individual o1 = ont.createIndividual( ns + "o1", OWL.Thing );
		Individual o2 = ont.createIndividual( ns + "o2", OWL.Thing );
		s.addProperty( p, o1 );
		s.addProperty( p, o2 );

		ont.prepare();

		Model values = ModelFactory.createDefaultModel();
		addStatements( values, s, OWL.sameAs, s );
		addStatements( values, o1, OWL.sameAs, o1, o2 );
		addStatements( values, o2, OWL.sameAs, o1, o2 );
		assertPropertyValues( ont, OWL.sameAs, values );
	}

	@Test
	public void testAnonTypes() {
		String ns = "urn:test:";
		OntModel model = ModelFactory.createOntologyModel( PelletReasonerFactory.THE_SPEC );
		OntClass c = model.createClass( ns + "C" );

		Individual anon = model.createIndividual( c );
		Individual x = model.createIndividual( ns + "x", c );

		model.contains( OWL.Nothing, RDF.type, OWL.Class );
		
		assertIteratorValues( model.listStatements( x, RDF.type, (Resource) null ), new Object[] {
			model.createStatement( x, RDF.type, OWL.Thing ),
			model.createStatement( x, RDF.type, c )
			
		});
		
		assertIteratorValues( model.listStatements( anon, RDF.type, (Resource) null ), new Object[] {
			model.createStatement( anon, RDF.type, OWL.Thing ),
			model.createStatement( anon, RDF.type, c )
			
		});
		
		assertIteratorValues( model.listStatements( null, RDF.type, OWL.Thing ), new Object[] {
			model.createStatement( anon, RDF.type, OWL.Thing ),
			model.createStatement( x, RDF.type, OWL.Thing )
			
		});
		
		assertIteratorValues( model.listStatements( null, RDF.type, c ), new Object[] {
			model.createStatement( anon, RDF.type, c ),
			model.createStatement( x, RDF.type, c )
			
		});

	}

	@Test
	public void testAnonInverse() throws OWLException {
		String ns = "http://www.example.org/test#";

		OntModel model = ModelFactory.createOntologyModel( PelletReasonerFactory.THE_SPEC );

		model.read( base + "anon_inverse.owl" );

		model.prepare();

		OntClass C = model.getOntClass( ns + "C" );
		OntClass D = model.getOntClass( ns + "D" );
		ObjectProperty r = model.getObjectProperty( ns + "r" );

		OntModel posit = ModelFactory.createOntologyModel( OntModelSpec.OWL_MEM );

		Resource invR = posit.createResource();
		invR.addProperty( OWL.inverseOf, r );

		Resource restr = posit.createResource();
		restr.addProperty( OWL.onProperty, invR );
		restr.addProperty( OWL.someValuesFrom, D );

		assertTrue( model.listStatements( C, RDFS.subClassOf, restr, posit ).hasNext() );

		assertTrue( model.contains( model.getProperty( ns + "functionalP" ), RDF.type,
				OWL.InverseFunctionalProperty ) );

		assertTrue( model.contains( model.getProperty( ns + "inverseFunctionalP" ), RDF.type,
				OWL.FunctionalProperty ) );

		assertTrue( model.contains( model.getProperty( ns + "transitiveP" ), RDF.type,
				OWL.TransitiveProperty ) );

		assertTrue( model.contains( model.getProperty( ns + "symmetricP" ), RDF.type,
				OWL.SymmetricProperty ) );

		assertTrue( model.contains( model.getProperty( ns + "reflexiveP" ), RDF.type,
				OWL2.ReflexiveProperty ) );

		assertTrue( model.contains( model.getProperty( ns + "irreflexiveP" ), RDF.type,
				OWL2.IrreflexiveProperty ) );

		assertTrue( model.contains( model.getProperty( ns + "asymmetricP" ), RDF.type,
				OWL2.AsymmetricProperty ) );

		ObjectProperty p1 = model.getObjectProperty( ns + "p1" );
		ObjectProperty p2 = model.getObjectProperty( ns + "p2" );
		ObjectProperty p3 = model.getObjectProperty( ns + "p3" );

		assertTrue( model.contains( p1, OWL.equivalentProperty, p2 ) );
		assertTrue( model.contains( p1, OWL.equivalentProperty, p3 ) );
		assertTrue( model.contains( p2, OWL.equivalentProperty, p3 ) );
	}

	@Test
	public void testAnnotationLookup() {
		final boolean oldUA = PelletOptions.USE_ANNOTATION_SUPPORT;
		try {
			PelletOptions.USE_ANNOTATION_SUPPORT = true;

			String ns = "http://www.example.org#";
			OntModel model = ModelFactory.createOntologyModel( PelletReasonerFactory.THE_SPEC );

			Resource x = model.createResource( ns + "x" );
			Resource y = model.createResource( ns + "y" );

			model.add( x, RDFS.seeAlso, y );

			assertIteratorValues( model.listSubjectsWithProperty( RDFS.seeAlso, y ),
					new Resource[] { x } );
		} finally {
			PelletOptions.USE_ANNOTATION_SUPPORT = oldUA;
		}
	}

	@Test
	public void testAnonClasses() {
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

		assertIteratorValues( class1.listInstances(), new Resource[] {
				inds[0], inds[1], inds[2], inds[3] } );

		assertIteratorValues( class2.listInstances(), new Resource[] {
				inds[2], inds[3], inds[4], inds[5] } );

		RDFList list = ontmodel.createList( new RDFNode[] { class1, class2 } );

		IntersectionClass class3 = ontmodel.createIntersectionClass( null, list );

		UnionClass class4 = ontmodel.createUnionClass( null, list );

		assertIteratorValues( class3.listInstances(), new Resource[] { inds[2], inds[3] } );

		assertIteratorValues( class4.listInstances(), new Resource[] {
				inds[0], inds[1], inds[2], inds[3], inds[4], inds[5] } );

	}

	@Test
	public void testDelete() {
		String ns = "urn:test:";

		OntModel model = ModelFactory.createOntologyModel();

		OntClass A = model.createClass( ns + "A" );
		ObjectProperty P = model.createObjectProperty( ns + "P" );
		P.addDomain( A );
		Individual x = model.createIndividual( ns + "x", OWL.Thing );
		Individual y = model.createIndividual( ns + "y", OWL.Thing );
		x.addProperty( P, y );

		assertTrue( x.hasRDFType( A ) );

		x.removeRDFType( A );

		assertTrue( x.hasRDFType( A ) );
	}

	@Test
	public void testDeclaredProperties() {
		String ns = "urn:test:";

		Reasoner r = PelletReasonerFactory.theInstance().create();
		// ReasonerRegistry.getOWLMicroReasoner();

		OntModelSpec spec = new OntModelSpec( OntModelSpec.OWL_DL_MEM );
		spec.setReasoner( r );
		OntModel model = ModelFactory.createOntologyModel( spec, null );

		OntClass A = model.createClass( ns + "A" );
		OntClass B = model.createClass( ns + "B" );
		ObjectProperty P = model.createObjectProperty( ns + "P" );
		P.addDomain( model.createUnionClass( null, model.createList( new RDFNode[] { A, B } ) ) );

		OntClass oc = model.getOntClass( ns + "B" );

		assertIteratorValues( oc.listDeclaredProperties(), new Resource[] { P } );
	}

	@Test
	public void testDifferentFrom1() {
		String ns = "urn:test:";

		OntModel model = ModelFactory.createOntologyModel( PelletReasonerFactory.THE_SPEC );

		OntClass C = model.createClass( ns + "C" );
		OntClass D = model.createClass( ns + "D" );

		Individual a = model.createIndividual( ns + "a", C );
		Individual b = model.createIndividual( ns + "b", C );
		Individual c = model.createIndividual( ns + "c", D );
		Individual d = model.createIndividual( ns + "d", OWL.Thing );

		ObjectProperty p = model.createObjectProperty( ns + "p" );

		C.addDisjointWith( D );

		a.addProperty( p, b );

		d.addRDFType( model.createAllValuesFromRestriction( null, p, OWL.Nothing ) );

		model.prepare();

		assertIteratorValues( a.listDifferentFrom(), new Resource[] { c, d } );

		assertIteratorValues( model.listSubjectsWithProperty( OWL.differentFrom, a ),
				new Resource[] { c, d } );

		assertIteratorValues( b.listDifferentFrom(), new Resource[] { c } );

		assertIteratorValues( model.listSubjectsWithProperty( OWL.differentFrom, b ),
				new Resource[] { c } );
	}

	@Test
	public void testSameAs1() {
		String ns = "urn:test:";

		OntModel model = ModelFactory.createOntologyModel( PelletReasonerFactory.THE_SPEC );

		Individual a = model.createIndividual( ns + "a", OWL.Thing );
		Individual b = model.createIndividual( ns + "b", OWL.Thing );
		Individual c = model.createIndividual( ns + "c", OWL.Thing );

		ObjectProperty p = model.createObjectProperty( ns + "p" );
		ObjectProperty q = model.createObjectProperty( ns + "q" );

		a.addProperty( p, c );

		b.addProperty( p, b );
		c.addProperty( q, a );

		c.addSameAs( b );

		a.addProperty( q, c );

		model.prepare();

		assertIteratorValues( a.listPropertyValues( p ), new Resource[] { b, c } );

		assertIteratorValues( a.listPropertyValues( q ), new Resource[] { b, c } );

		assertIteratorValues( b.listPropertyValues( p ), new Resource[] { b, c } );

		assertIteratorValues( b.listPropertyValues( q ), new Resource[] { a } );

		assertIteratorValues( c.listPropertyValues( p ), new Resource[] { b, c } );

		assertIteratorValues( c.listPropertyValues( q ), new Resource[] { a } );

	}

	@Test
	public void testSameAs2() {
		OntModelSpec ontModelSpec = new OntModelSpec( OntModelSpec.OWL_DL_MEM_RULE_INF );
		ontModelSpec.setReasoner( new PelletReasoner() );
		OntModel model = ModelFactory.createOntologyModel( ontModelSpec );
		Individual i1 = model.createIndividual( "http://test#i1", OWL.Thing );
		Individual i2 = model.createIndividual( "http://test#i2", OWL.Thing );
		Property prop = model.createProperty( "http://test#prop" );
		i1.addProperty( prop, "test" );
		i1.addSameAs( i2 );

		// confirm that sameAs was created
		assertTrue( i1.isSameAs( i2 ) );

		// confirm that symmetric sameAs inferred
		assertTrue( i2.isSameAs( i1 ) );

		// confirm that the property is there
		assertTrue( i1.hasProperty( prop, "test" ) );

		// confirm that the property is there when querying with a predicate
		assertIteratorContains( i1.listProperties(), model.createStatement( i1, prop, "test" ) );

		// confirm that the property is copied over when querying with a
		// predicate
		assertTrue( i2.hasProperty( prop, "test" ) );

		// confirm that the property is copied over when querying with a
		// predicate
		assertIteratorContains( i2.listProperties(), model.createStatement( i2, prop, "test" ) );
	}

	@Test
	public void testSameAs3() {
		OntModelSpec ontModelSpec = new OntModelSpec( OntModelSpec.OWL_DL_MEM_RULE_INF );
		ontModelSpec.setReasoner( new PelletReasoner() );
		OntModel model = ModelFactory.createOntologyModel( ontModelSpec );
		Individual i1 = model.createIndividual( "http://test#i1", OWL.Thing );
		Individual i2 = model.createIndividual( "http://test#i2", OWL.Thing );
		OntClass c = model.createEnumeratedClass( "http://test#C", model.createList( new RDFNode[] {
				i1, i2 } ) );
		Individual i3 = model.createIndividual( "http://test#i3", c );

		assertTrue( !i1.isSameAs( i2 ) );
		assertTrue( !i1.isSameAs( i3 ) );
		assertIteratorValues( i1.listSameAs(), new Resource[] { i1 } );

		assertTrue( !i2.isSameAs( i1 ) );
		assertTrue( !i2.isSameAs( i3 ) );
		assertIteratorValues( i2.listSameAs(), new Resource[] { i2 } );

		assertTrue( !i3.isSameAs( i1 ) );

		assertTrue( !i3.isSameAs( i2 ) );
		assertIteratorValues( i3.listSameAs(), new Resource[] { i3 } );
	}

	@Test
	public void testSudaku() {
		String ns = "http://sudoku.owl#";

		OntModel model = ModelFactory.createOntologyModel( PelletReasonerFactory.THE_SPEC );
		model.read( base + "sudaku.owl" );

		OntClass[][] C = new OntClass[4][4];
		Individual[][] V = new Individual[4][4];
		Individual[] N = new Individual[4];
		for( int i = 1; i < 4; i++ ) {
			N[i] = model.getIndividual( ns + i );
			for( int j = 1; j < 4; j++ ) {
				V[i][j] = model.getIndividual( ns + "V" + i + j );
				C[i][j] = model.getOntClass( ns + "C" + i + j );
			}
		}

		V[2][1].setSameAs( N[2] );
		V[1][2].setSameAs( N[3] );

		// | ?1 | 3 | ?2 |
		// | 2 | ?1 | ?3 |
		// | ?3 | ?2 | ?1 |

		Individual[][] eq = new Individual[][] {
				{ V[1][1], V[2][2], V[3][3], N[1] }, { V[1][3], V[2][1], V[3][2], N[2] },
				{ V[1][2], V[2][3], V[3][1], N[3] } };
		for( int k = 0; k < 3; k++ ) {
			for( int i = 0; i < 4; i++ ) {
				Individual ind = eq[k][i];
				for( int j = 0; j < 4; j++ ) {
					// System.out.println( ind + " = " + eq[k][j] );
					assertTrue( ind.isSameAs( eq[k][j] ) );
				}
				assertIteratorValues( ind.listSameAs(), eq[k] );
			}
		}
	}

	@Test
	public void testFuncProp() {
		String ns = "urn:test:";

		OntModel model = ModelFactory.createOntologyModel( PelletReasonerFactory.THE_SPEC );

		Individual a = model.createIndividual( ns + "a", OWL.Thing );
		Individual b = model.createIndividual( ns + "b", OWL.Thing );
		Individual c = model.createIndividual( ns + "c", OWL.Thing );
		Individual d = model.createIndividual( ns + "d", OWL.Thing );

		ObjectProperty p = model.createObjectProperty( ns + "p" );
		a.addProperty( p, b );

		ObjectProperty q = model.createObjectProperty( ns + "q", true );
		a.addProperty( q, b );
		a.addProperty( q, d );

		c.addSameAs( b );

		assertIteratorValues( a.listPropertyValues( p ), new Resource[] { b, c, d } );

		Model values = ModelFactory.createDefaultModel();
		addStatements( values, a, OWL.sameAs, a );
		addStatements( values, b, OWL.sameAs, b, c, d );
		addStatements( values, c, OWL.sameAs, b, c, d );
		addStatements( values, d, OWL.sameAs, b, c, d );
		assertPropertyValues( model, OWL.sameAs, values );
	}

	@Test
	public void testHasValueReasoning() {
		String ns = "urn:test:";
		OntModelSpec spec = new OntModelSpec( OntModelSpec.OWL_DL_MEM );
		spec.setReasoner( new PelletReasoner() );
		OntModel model = ModelFactory.createOntologyModel( spec, null );
		OntClass HomeOwner = model.createClass( ns + "HomeOwner" );
		Individual bob = model.createIndividual( ns + "bob", HomeOwner );
		ObjectProperty hasNeighbor = model.createObjectProperty( ns + "hasNeighbor" );
		OntClass NeighborOfBob = model.createClass( ns + "NeighborOfBob" );
		NeighborOfBob
				.addEquivalentClass( model.createHasValueRestriction( null, hasNeighbor, bob ) );
		Individual susan = model.createIndividual( ns + "susan", HomeOwner );
		susan.setPropertyValue( hasNeighbor, bob );
		// model.write(System.out, "RDF/XML-ABBREV");

		assertTrue( "susan is not a NeighborOfBob", susan.hasRDFType( NeighborOfBob ) );
	}

	@Test
	public void testInfiniteChain() {
		OntModel model = ModelFactory.createOntologyModel( PelletReasonerFactory.THE_SPEC );
		model.setStrictMode( false );
		model.read( base + "infiniteChain.owl" );

		model.prepare();

		assertFalse( ((PelletInfGraph) model.getGraph()).getKB().isConsistent() );

		String ns = "http://www.example.org/test#";
		Property prop = model.getBaseModel().getProperty( ns + "ssn" );
		prop.removeAll( RDFS.range );
		model.rebind();

		assertTrue( ((PelletInfGraph) model.getGraph()).isConsistent() );
	}

	@Ignore("Inverse functional datatype property support conflicts with changes in r2442 and 2443")
	@Test
	public void testInfiniteChainDP() {
		OntModel model = ModelFactory.createOntologyModel( PelletReasonerFactory.THE_SPEC );
		model.read( base + "infiniteChainDP.owl" );

		assertFalse( ((PelletInfGraph) model.getGraph()).isConsistent() );

		String ns = "http://www.example.org/test#";
		Property prop = model.getBaseModel().getProperty( ns + "ssn" );
		prop.removeAll( RDFS.range );
		model.rebind();

		assertTrue( ((PelletInfGraph) model.getGraph()).isConsistent() );
	}

	@Test
	public void testParents() {
		String ns = "http://www.example.org/test#";

		OntModel model = ModelFactory.createOntologyModel( PelletReasonerFactory.THE_SPEC );
		model.read( base + "parents.owl" );

		Individual Bob = model.getIndividual( ns + "Bob" );
		Individual Mom = model.getIndividual( ns + "Mom" );
		Individual Dad = model.getIndividual( ns + "Dad" );

		OntProperty hasParent = model.getObjectProperty( ns + "hasParent" );
		OntProperty hasFather = model.getObjectProperty( ns + "hasFather" );
		OntProperty hasMother = model.getObjectProperty( ns + "hasMother" );
		OntProperty topObjProp = model.getObjectProperty( OWL2.topObjectProperty.getURI() );

		model.prepare();

		assertTrue( ((PelletInfGraph) model.getGraph()).getKB().isConsistent() );

		assertIteratorValues( model.listObjectsOfProperty( Bob, hasParent ), new Resource[] {
				Mom, Dad } );

		assertIteratorValues( model.listObjectsOfProperty( hasFather ), new Object[] { Dad } );

		assertIteratorValues( model.listObjectsOfProperty( hasMother ), new Object[] { Mom } );

		assertIteratorValues( model.listStatements( null, hasParent, (Resource) null ),
				new Statement[] {
						ResourceFactory.createStatement( Bob, hasParent, Mom ),
						ResourceFactory.createStatement( Bob, hasParent, Dad ) } );

		assertIteratorValues( model.listStatements( Bob, null, Dad ), new Statement[] {
				ResourceFactory.createStatement( Bob, topObjProp, Dad ),
				ResourceFactory.createStatement( Bob, hasParent, Dad ),
				ResourceFactory.createStatement( Bob, hasFather, Dad ) } );

		assertIteratorValues( model.listObjectsOfProperty( Bob, hasFather ), new Resource[] { Dad } );

		assertIteratorValues( model.listObjectsOfProperty( Bob, hasMother ), new Resource[] { Mom } );
	}

	@Test
	public void testTeams() {
		String ns = "http://owl.man.ac.uk/2005/sssw/teams#";

		OntModel model = ModelFactory.createOntologyModel( PelletReasonerFactory.THE_SPEC );

		model.read( base + "teams.owl" );

		Individual t1 = model.getIndividual( ns + "OntologyFC" );

		OntClass Male = model.getOntClass( ns + "Male" );
		OntClass Female = model.getOntClass( ns + "Female" );
		Individual Sam = model.getIndividual( ns + "Sam" );
		Individual Chris = model.getIndividual( ns + "Chris" );

		OntClass Team = model.getOntClass( ns + "Team" );
		OntClass MixedTeam = model.getOntClass( ns + "MixedTeam" );
		OntClass NonSingletonTeam = model.getOntClass( ns + "NonSingletonTeam" );
		OntClass SingletonTeam = model.getOntClass( ns + "SingletonTeam" );

		model.prepare();

		assertTrue( Sam.isDifferentFrom( Chris ) );
		assertTrue( Chris.isDifferentFrom( Sam ) );

		assertTrue( MixedTeam.hasSuperClass( Team ) );
		assertFalse( MixedTeam.hasSuperClass( SingletonTeam ) );
		assertIteratorValues( MixedTeam.listSuperClasses(), new Resource[] {
				Team, NonSingletonTeam, OWL.Thing } );
		assertIteratorValues( MixedTeam.listSuperClasses( true ),
				new Resource[] { NonSingletonTeam } );

		assertTrue( NonSingletonTeam.hasSubClass( MixedTeam ) );
		assertIteratorValues( NonSingletonTeam.listSubClasses(), new Resource[] {
				MixedTeam, OWL.Nothing } );
		assertIteratorValues( NonSingletonTeam.listSubClasses( true ), new Resource[] { MixedTeam } );

		assertTrue( t1.hasRDFType( MixedTeam ) );
		assertTrue( t1.hasRDFType( MixedTeam, true ) );
		assertIteratorValues( t1.listRDFTypes( false ), new Resource[] {
				Team, NonSingletonTeam, MixedTeam, OWL.Thing } );
		assertIteratorValues( t1.listRDFTypes( true ), new Resource[] { MixedTeam } );

		Male.removeDisjointWith( Female );
		Female.removeDisjointWith( Male );
		Sam.removeDifferentFrom( Chris );
		Chris.removeDifferentFrom( Sam );

		assertTrue( !Sam.isDifferentFrom( Chris ) );
		assertTrue( !Chris.isDifferentFrom( Sam ) );

		assertTrue( MixedTeam.hasSuperClass( Team ) );
		assertIteratorValues( MixedTeam.listSuperClasses(), new Resource[] { Team, OWL.Thing } );

		assertTrue( !NonSingletonTeam.hasSuperClass( MixedTeam ) );
		assertIteratorValues( NonSingletonTeam.listSuperClasses(),
				new Resource[] { Team, OWL.Thing } );
		assertIteratorValues( NonSingletonTeam.listSuperClasses( true ), new Resource[] { Team } );

		assertTrue( t1.hasRDFType( MixedTeam ) );
		assertTrue( t1.hasRDFType( MixedTeam, true ) );
		assertIteratorValues( t1.listRDFTypes( false ),
				new Resource[] { Team, MixedTeam, OWL.Thing } );
		assertIteratorValues( t1.listRDFTypes( true ), new Resource[] { MixedTeam } );
	}

	@Test
	public void testPropertyAssertions2() {
		String ns = "urn:test:";

		OntModel model = ModelFactory.createOntologyModel( PelletReasonerFactory.THE_SPEC );
		OntClass Person = model.createClass( ns + "Person" );
		OntProperty hasFather = model.createObjectProperty( ns + "hasFather" );
		OntProperty hasBioFather = model.createObjectProperty( ns + "hasBioFather", true );
		hasBioFather.addSuperProperty( hasFather );
		Person.addSuperClass( model.createMinCardinalityRestriction( null, hasBioFather, 1 ) );

		Individual Bob = model.createIndividual( ns + "Bob", Person );
		Individual Dad = model.createIndividual( ns + "Dad", Person );
		Bob.addProperty( hasBioFather, Dad );
		Bob.addRDFType( model.createCardinalityRestriction( null, hasFather, 1 ) );

		model = ModelFactory.createOntologyModel( PelletReasonerFactory.THE_SPEC, model );

		assertIteratorValues( model.listObjectsOfProperty( Bob, hasFather ), new Resource[] { Dad } );

		assertIteratorValues( model.listObjectsOfProperty( Bob, hasBioFather ),
				new Resource[] { Dad } );
	}

	@Test
	public void testTransitive1() {
		OntModel model = ModelFactory.createOntologyModel( PelletReasonerFactory.THE_SPEC );
		model.read( base + "agencies.owl" );

		model.prepare();

		String ns = "http://www.owl-ontologies.com/unnamed.owl#";
		Individual Forest_Service = model.getIndividual( ns + "Forest_Service" );
		ObjectProperty comprises = model.getObjectProperty( ns + "comprises" );
		Individual Executive = model.getIndividual( ns + "Executive" );
		Individual USDA = model.getIndividual( ns + "USDA" );

		assertTrue( "Forest_Service, comprises, Executive", model.contains( Forest_Service,
				comprises, Executive ) );

		assertIteratorValues( model.listObjectsOfProperty( Forest_Service, comprises ),
				new Resource[] { USDA, Executive } );

		assertIteratorValues( model.listSubjectsWithProperty( comprises, Executive ),
				new Resource[] {
						model.getIndividual( ns + "USDA" ),
						model.getIndividual( ns + "DOE" ),
						model.getIndividual( ns + "DHS" ),
						model.getIndividual( ns + "HHS" ),
						model.getIndividual( ns + "HUD" ),
						model.getIndividual( ns + "DOC" ),
						model.getIndividual( ns + "DOD" ),
						model.getIndividual( ns + "DOI" ),
						model.getIndividual( ns + "Research__Economics___Education" ),
						model.getIndividual( ns + "Forest_Service" ),
						model.getIndividual( ns + "Rural_Development" ),
						model.getIndividual( ns + "Natural_Resources_Conservation_Service" ),
						model.getIndividual( ns + "Economic_Research_Service" ),
						model.getIndividual( ns + "Farm_Service_Agency" ),
						model.getIndividual( ns
								+ "Cooperative_State_Research__Education__and_Extension_Service" ),
						model.getIndividual( ns + "Animal___Plant_Health_Inspection_Service" ),
						model.getIndividual( ns + "Agricultural_Research_Service" ),
						model.getIndividual( ns + "National_Agricultural_Library" ), } );
	}

	@Test
	public void testTransitive2() {
		OntModel model = ModelFactory.createOntologyModel( PelletReasonerFactory.THE_SPEC );
		model.read( base + "cyclic_transitive.owl" );

		model.prepare();

		String ns = "http://www.example.org/test#";

		OntClass Probe = model.getOntClass( ns + "Probe" );
		Individual Instance1 = model.getIndividual( ns + "Instance1" );
		Individual Instance2 = model.getIndividual( ns + "Instance2" );
		Individual Instance3 = model.getIndividual( ns + "Instance3" );

		assertIteratorValues( Probe.listInstances(), new Resource[] {
				Instance1, Instance2, Instance3 } );
	}

	@Test
	public void testTransitiveSubProperty1() {
		String ns = "urn:test:";

		OntModel model = ModelFactory.createOntologyModel( OntModelSpec.OWL_MEM );

		ObjectProperty knows = model.createObjectProperty( ns + "knows" );

		ObjectProperty hasRelative = model.createObjectProperty( ns + "hasRelative" );
		// a person knows all his/her relatives
		hasRelative.addSuperProperty( knows );
		// being a relative is transitive (but knowing someone is not
		// transitive)
		hasRelative.addRDFType( OWL.TransitiveProperty );

		ObjectProperty hasParent = model.createObjectProperty( ns + "hasParent" );
		// a parent is also a relative
		hasParent.addSuperProperty( hasRelative );

		OntClass cls = model.createClass( ns + "cls" );
		Individual a = cls.createIndividual( ns + "a" );
		Individual b = cls.createIndividual( ns + "b" );
		Individual c = cls.createIndividual( ns + "c" );
		Individual d = cls.createIndividual( ns + "d" );
		Individual e = cls.createIndividual( ns + "e" );
		Individual f = cls.createIndividual( ns + "f" );
		Individual g = cls.createIndividual( ns + "g" );

		OntModelSpec spec = new OntModelSpec( OntModelSpec.OWL_DL_MEM );
		// spec.setReasoner( ReasonerRegistry.getDIGReasoner() );
		spec.setReasoner( PelletReasonerFactory.theInstance().create() );
		model = ModelFactory.createOntologyModel( spec, model );

		// This is the ABox data used in this example:
		//
		// hasParent hasParent hasRelative
		// a -----------> b -----------> c -------------> f
		// | |
		// | | knows
		// | +--------------+
		// | |
		// | knows knows V
		// +------------> d -----------> e
		// |
		// | hasRelative
		// +--------------> g

		model.add( a, hasParent, b ); // (1)
		model.add( b, hasParent, c ); // (2)

		model.add( a, knows, d ); // (3)
		model.add( d, knows, e ); // (4)

		model.add( b, knows, e ); // (5)

		model.add( c, hasRelative, f ); // (6)

		model.add( d, hasRelative, g ); // (6)

		// (1) implies a hasRelative b, a knows b
		assertTrue( model.contains( a, hasRelative, b ) );
		assertTrue( model.contains( a, knows, b ) );

		// (2) implies b hasRelative c, b knows c
		assertTrue( model.contains( b, hasRelative, c ) );
		assertTrue( model.contains( b, knows, c ) );

		// (1) and (2) implies implies a hasRelative c, a knows c
		assertTrue( model.contains( a, hasRelative, c ) );
		assertTrue( model.contains( a, knows, c ) );

		// (2) and (6) implies b hasRelative f, b knows f
		assertTrue( model.contains( b, hasRelative, f ) );
		assertTrue( model.contains( b, knows, f ) );

		// (1), (2) and (6) implies implies a hasRelative f, a knows f
		assertTrue( model.contains( a, hasRelative, f ) );
		assertTrue( model.contains( a, knows, f ) );

		// Neither (1) and (5) nor (3) and (4) implies a hasRelative e
		assertTrue( !model.contains( a, hasRelative, e ) );

		// Neither (1) and (5) nor (3) and (4) implies a knows e
		assertTrue( !model.contains( a, knows, e ) );

		assertTrue( !model.contains( a, knows, g ) );

		assertTrue( !model.contains( a, hasRelative, g ) );

		// checking get functions
		assertIteratorValues( model.listObjectsOfProperty( a, hasRelative ), new Resource[] {
				b, c, f } );

		assertIteratorValues( model.listObjectsOfProperty( a, knows ),
				new Resource[] { b, c, d, f } );

		assertIteratorValues( model.listObjectsOfProperty( b, knows ), new Resource[] { c, e, f } );

		assertIteratorValues( model.listSubjectsWithProperty( knows, e ), new Resource[] { b, d } );

		assertIteratorValues( model.listSubjectsWithProperty( hasRelative, f ), new Resource[] {
				a, b, c } );
	}

	@Test
	public void testTransitiveSubProperty2() {
		String ns = "http://www.co-ode.org/ontologies/test/pellet/transitive.owl#";

		OntModel model = ModelFactory.createOntologyModel( PelletReasonerFactory.THE_SPEC );

		model.read( base + "transitiveSub.owl" );

		OntClass ThingsThatpSomeC = model.getOntClass( ns + "ThingsThatpSomeC" );
		OntClass A = model.getOntClass( ns + "A" );
		OntClass B = model.getOntClass( ns + "B" );

		assertTrue( A.hasSuperClass( ThingsThatpSomeC ) );
		assertTrue( B.hasSuperClass( ThingsThatpSomeC ) );

		assertIteratorContains( A.listSuperClasses(), ThingsThatpSomeC );
		assertIteratorContains( B.listSuperClasses(), ThingsThatpSomeC );
	}

	@Test
	public void testNominals() {
		String ns = "http://www.example.org/test#";

		OntModel model = ModelFactory.createOntologyModel( PelletReasonerFactory.THE_SPEC );

		model.read( base + "nominals.owl" );

		OntClass Color = model.getOntClass( ns + "Color" );
		Individual red = model.getIndividual( ns + "red" );

		OntClass PrimaryColors = model.getOntClass( ns + "PrimaryColors" );

		OntClass MyFavoriteColors = model.getOntClass( ns + "MyFavoriteColors" );

		OntClass HasFourPrimaryColors = model.getOntClass( ns + "HasFourPrimaryColors" );

		model.prepare();

		assertTrue( model.contains( red, RDF.type, MyFavoriteColors ) );

		assertTrue( model.contains( HasFourPrimaryColors, RDFS.subClassOf, OWL.Nothing ) );

		assertIteratorValues( Color.listSubClasses(), new Resource[] {
				PrimaryColors, MyFavoriteColors, HasFourPrimaryColors, OWL.Nothing } );
	}

	@Test
	public void testDatatypeProperties() {
		String ns = "urn:test:";

		OntModel model = ModelFactory.createOntologyModel( PelletReasonerFactory.THE_SPEC );
		OntClass Person = model.createClass( ns + "Person" );
		Individual john = model.createIndividual( ns + "JohnDoe", Person );
		DatatypeProperty email = model.createDatatypeProperty( ns + "email", false );

		john.addProperty( email, "john.doe@unknown.org" );
		john.addProperty( email, "jdoe@unknown.org" );

		assertTrue( model.validate().isValid() );

		assertIteratorValues( model.listSubjectsWithProperty( email, "john.doe@unknown.org" ),
				new Resource[] { john } );

		assertTrue( model.contains( null, email, "john.doe@unknown.org" ) );

		assertTrue( !model.contains( null, email, john ) );

		DatatypeProperty name1 = model.createDatatypeProperty( ns + "name1", true );

		john.addProperty( name1, "Name", "en" );
		john.addProperty( name1, "Nom", "fr" );

		assertTrue( model.validate().isValid() );

		DatatypeProperty name2 = model.createDatatypeProperty( ns + "name2", true );

		john.addProperty( name2, "Name" );
		john.addProperty( name2, "Nom" );

		assertTrue( !model.validate().isValid() );
	}

	@Test
	public void testDatatypeHierarchy() {
		OntModel model = ModelFactory.createOntologyModel( PelletReasonerFactory.THE_SPEC );
		model.read( base + "all_datatypes.owl" );

		OntModel hierarchy = ModelFactory.createOntologyModel( OntModelSpec.OWL_MEM );
		model.read( base + "datatype_hierarchy.owl" );

		Iterator<?> i = hierarchy.listClasses();
		while( i.hasNext() ) {
			OntClass cls = (OntClass) i.next();

			assertIteratorValues( model.getOntClass( cls.getURI() ).listSubClasses( true ), cls
					.listSubClasses() );
		}
	}

	@Test
	public void testDataPropCard1() {
		String ns = "urn:test:";

		OntModel model = ModelFactory.createOntologyModel( PelletReasonerFactory.THE_SPEC );
		DatatypeProperty prop = model.createDatatypeProperty( ns + "prop" );
		OntClass C = model.createClass( ns + "C" );
		C.addSuperClass( model.createCardinalityRestriction( null, prop, 2 ) );
		Individual x = model.createIndividual( ns + "x", C );
		x.addProperty( prop, "literal" );

		model.prepare();

		assertTrue( ((PelletInfGraph) model.getGraph()).isConsistent() );
	}

	@Test
	public void testDataPropCard2() {
		String ns = "urn:test:";

		OntModel model = ModelFactory.createOntologyModel( PelletReasonerFactory.THE_SPEC );
		DatatypeProperty prop = model.createDatatypeProperty( ns + "prop" );
		OntClass C = model.createClass( ns + "C" );
		C.addSuperClass( model.createCardinalityRestriction( null, prop, 2 ) );
		Individual x = model.createIndividual( ns + "x", C );
		x.addProperty( prop, "literal1" );
		x.addProperty( prop, "literal2" );
		x.addProperty( prop, "literal3" );

		assertTrue( !model.validate().isValid() );
	}

	@Test
	public void testSubDataPropCard() {
		String ns = "urn:test:";

		OntModel model = ModelFactory.createOntologyModel( PelletReasonerFactory.THE_SPEC );
		DatatypeProperty prop = model.createDatatypeProperty( ns + "prop" );
		DatatypeProperty sub = model.createDatatypeProperty( ns + "sub" );
		sub.addSuperProperty( prop );

		OntClass C = model.createClass( ns + "C" );
		C.addSuperClass( model.createCardinalityRestriction( null, prop, 2 ) );
		Individual x = model.createIndividual( ns + "x", C );

		Literal val1 = model.createLiteral( "val1" );
		x.addProperty( prop, val1 );
		Literal val2 = model.createLiteral( "val2" );
		x.addProperty( sub, val2 );

		assertTrue( model.validate().isValid() );

		assertPropertyValues( model, x, prop, val1, val2 );
	}

	@Test
	public void testUniqueNameAssumption() {
		String ns = "urn:test:";

		OntModel model = ModelFactory.createOntologyModel( PelletReasonerFactory.THE_SPEC );

		OntClass Country = model.createClass( ns + "Country" );
		Individual USA = model.createIndividual( ns + "USA", Country );
		Individual UnitedStates = model.createIndividual( ns + "UnitedStates", Country );

		OntProperty livesIn = model.createObjectProperty( ns + "livesIn" );
		livesIn.convertToFunctionalProperty();

		OntClass Person = model.createClass( ns + "Person" );
		Individual JohnDoe = model.createIndividual( ns + "JohnDoe", Person );
		JohnDoe.addProperty( livesIn, USA );
		JohnDoe.addProperty( livesIn, UnitedStates );

		assertTrue( model.contains( JohnDoe, RDF.type, Person ) );
		assertTrue( model.contains( USA, OWL.sameAs, UnitedStates ) );
		assertIteratorValues( model.listObjectsOfProperty( JohnDoe, livesIn ), new Resource[] {
				USA, UnitedStates } );
	}

	@Test
	public void testESG() {
		String ns = "http://www.csm.ornl.gov/~7lp/onto-library/esg1.1#";

		OntModel model = ModelFactory.createOntologyModel( PelletReasonerFactory.THE_SPEC );
		model.getDocumentManager().setProcessImports( false );

		model.read( base + "ESG1.1.owl" );

		model.prepare();

		assertTrue( ((PelletInfGraph) model.getGraph()).getKB().isConsistent() );

		Individual jdl62 = model.getIndividual( ns + "JDL_00062" );
		Individual jdl63 = model.getIndividual( ns + "JDL_00063" );

		assertTrue( jdl62.isSameAs( jdl63 ) );
		assertTrue( jdl63.isSameAs( jdl62 ) );

		assertIteratorValues( jdl62.listSameAs(), new Resource[] { jdl62, jdl63 } );

		assertIteratorValues( jdl63.listSameAs(), new Resource[] { jdl62, jdl63 } );

		model.getDocumentManager().setProcessImports( true );
		// ((PelletInfGraph) model.getGraph()).getKB().timers.print();
	}

	@Test
	public void testDatapropertyRange() {
		OntModel model = ModelFactory.createOntologyModel( PelletReasonerFactory.THE_SPEC );
		model.read( base + "datataype_range.owl" );

		model.prepare();

		Iterator<?> i = model.listDatatypeProperties();
		while( i.hasNext() ) {
			DatatypeProperty p = (DatatypeProperty) i.next();
			Iterator<?> j = p.listRange();
			while( j.hasNext() ) {
				Resource range = (Resource) j.next();
				assertTrue( TypeMapper.getInstance().getTypeByName( range.getURI() ) != null );
			}
		}
	}

	@Test
	public void testMultipleDatatypes() {
		String ns = "urn:test:";

		OntModel model = ModelFactory.createOntologyModel( PelletReasonerFactory.THE_SPEC );

		OntProperty f = model.createDatatypeProperty( ns + "f" );
		f.addRange( XSD.xfloat );

		OntProperty d = model.createDatatypeProperty( ns + "d" );
		d.addRange( XSD.xdouble );

		OntClass C = model.createClass( ns + "C" );
		C.addSuperClass( model.createMinCardinalityRestriction( null, f, 5 ) );
		C.addSuperClass( model.createMinCardinalityRestriction( null, d, 5 ) );

		model.prepare();

		assertFalse( C.hasSuperClass( OWL.Nothing ) );
	}

	@Test
	public void testUserDefinedFloatDatatypes() {
		String ns = "http://www.lancs.ac.uk/ug/dobsong/owl/float_test.owl#";

		OntModel model = ModelFactory.createOntologyModel( PelletReasonerFactory.THE_SPEC );
		model.read( base + "float_test.owl" );

		model.prepare();

		assertTrue( model.validate().isValid() );

		OntClass ThingWithFloatValue = model.getOntClass( ns + "ThingWithFloatValue" );
		OntClass ThingWithFloatProbability = model.getOntClass( ns + "ThingWithProbabilityValue" );

		Individual exampleThingWithFloatValue = model.getIndividual( ns
				+ "exampleThingWithFloatValue" );
		Individual exampleThingWithFloatProbability = model.getIndividual( ns
				+ "exampleThingWithProbabilityValue" );

		assertTrue( ThingWithFloatValue.hasSubClass( ThingWithFloatProbability ) );
		assertTrue( !ThingWithFloatProbability.hasSubClass( ThingWithFloatValue ) );

		assertTrue( exampleThingWithFloatValue.hasRDFType( ThingWithFloatValue ) );
		assertTrue( !exampleThingWithFloatValue.hasRDFType( ThingWithFloatProbability ) );

		assertTrue( exampleThingWithFloatProbability.hasRDFType( ThingWithFloatValue ) );
		assertTrue( exampleThingWithFloatProbability.hasRDFType( ThingWithFloatProbability ) );
	}

	@Test
	public void testUserDefinedDecimalDatatypes() {
		String ns = "http://www.lancs.ac.uk/ug/dobsong/owl/decimal_test.owl#";

		OntModel model = ModelFactory.createOntologyModel( PelletReasonerFactory.THE_SPEC );
		model.read( base + "decimal_test.owl" );

		model.prepare();

		assertTrue( model.validate().isValid() );

		OntClass ThingWithDecimalValue = model.getOntClass( ns + "ThingWithDecimalValue" );
		OntClass ThingWithDecimalProbability = model.getOntClass( ns
				+ "ThingWithDecimalProbability" );
		OntClass ThingWithIntegerValue = model.getOntClass( ns + "ThingWithIntegerValue" );

		Individual exampleThingWithDecimalValue = model.getIndividual( ns
				+ "exampleThingWithDecimalValue" );
		Individual exampleThingWithDecimalProbability = model.getIndividual( ns
				+ "exampleThingWithDecimalProbability" );

		assertTrue( ThingWithDecimalValue.hasSubClass( ThingWithIntegerValue ) );
		assertTrue( ThingWithDecimalValue.hasSubClass( ThingWithDecimalProbability ) );

		assertTrue( exampleThingWithDecimalValue.hasRDFType( ThingWithDecimalValue ) );

		assertTrue( exampleThingWithDecimalProbability.hasRDFType( ThingWithIntegerValue ) );
		assertTrue( exampleThingWithDecimalProbability.hasRDFType( ThingWithDecimalProbability ) );
		assertTrue( exampleThingWithDecimalProbability.hasRDFType( ThingWithDecimalValue ) );

		assertTrue(!ThingWithDecimalValue.hasSuperClass(ThingWithIntegerValue));
		assertTrue(!ThingWithIntegerValue.hasSubClass(ThingWithDecimalProbability));
	}

	@Test
	public void testBuiltinDatatypesWithValidValues() {
		String ns = "urn:test:";

		Object[] datatypes = {
				XSD.anyURI, "http://www.w3.com", "\nhttp://www.w3.com\r", XSD.xboolean, "true", "1", "\ntrue", XSD.xbyte,
				"8", "\t\r\n8 ", XSD.date, "2004-03-15", XSD.dateTime, "2003-12-25T08:30:00",
				"2003-12-25T08:30:00.001", "2003-12-25T08:30:00-05:00", "2003-12-25T08:30:00Z",
				XSD.decimal, "3.1415292", XSD.xdouble, "3.1415292", "INF", "NaN", XSD.duration,
				"P8M3DT7H33M2S", "P1Y", "P1M", "P1Y2MT2H", XSD.xfloat, "3.1415292", "-1E4",
				"12.78e-2", "INF", "NaN", XSD.gDay, "---11", XSD.gMonth, "--02", XSD.gMonthDay,
				"--02-14", XSD.gYear, "0001", "1999", XSD.gYearMonth, "1972-08", XSD.xint, "77",
				XSD.integer, "77", XSD.xlong, "214", XSD.negativeInteger, "-123",
				XSD.nonNegativeInteger, "2", XSD.nonPositiveInteger, "0", XSD.positiveInteger,
				"500", XSD.xshort, "476", XSD.xstring, "Test", XSD.time, "13:02:00", };

		OntModel model = ModelFactory.createOntologyModel( PelletReasonerFactory.THE_SPEC );
		Individual ind = model.createIndividual( ns + "test", OWL.Thing );
		for( int i = 0; i < datatypes.length; ) {
			Resource datatype = (Resource) datatypes[i++];
			OntProperty p = model.createDatatypeProperty( ns + "prop_" + datatype.getLocalName() );
			p.addRange( datatype );

			while( i < datatypes.length && datatypes[i] instanceof String ) {
				Literal value = model.createTypedLiteral( (String) datatypes[i], datatype.getURI() );
				ind.addProperty( p, value );
				i++;
			}
		}

		model.prepare();

		assertTrue( model.validate().isValid() );
	}

	@Test
	public void testBuiltinDatatypesWithInvalidValues() {
		String ns = "urn:test:";

		Object[] datatypes = {
				XSD.anyURI, "http://www.w3.com\\invalid", XSD.xboolean, "True", "01", XSD.xbyte,
				"-12093421034", "257", "2147483648", XSD.date, "2004-15-03", "2004/03/15", "03-15-2004", XSD.dateTime,
				"2003-12-25", XSD.decimal, "x3.1415292", XSD.xdouble, "Inf", XSD.duration, "P-8M",
				XSD.xfloat, "3.1g-1", XSD.gDay, "11", "Monday", "Mon", XSD.gMonth, "02", "Feb",
				"February", XSD.gMonthDay, "02-14", "02/14", XSD.gYear, "0000", "542",
				XSD.gYearMonth, "1972/08", "197208", XSD.xint, "2147483648", "9223372036854775808",
				XSD.integer, "1.1", XSD.xlong, "9223372036854775808", XSD.negativeInteger, "0",
				"1", XSD.nonNegativeInteger, "-1", XSD.nonPositiveInteger, "1",
				XSD.positiveInteger, "-1", XSD.xshort, "32768", "1.1", };

		for( boolean addRangeRestriction : new boolean[] { false, true } ) {
			for( int i = 0; i < datatypes.length; ) {
				Resource datatype = (Resource) datatypes[i++];

				while( i < datatypes.length && datatypes[i] instanceof String ) {
					OntModel model = ModelFactory
							.createOntologyModel( PelletReasonerFactory.THE_SPEC );
					Individual ind = model.createIndividual( ns + "test", OWL.Thing );

					OntProperty p = model.createDatatypeProperty( ns + "prop_"
							+ datatype.getLocalName() );
					if( addRangeRestriction ) {
	                    p.addRange( datatype );
                    }

					Literal value = model.createTypedLiteral( (String) datatypes[i], datatype
							.getURI() );
					ind.addProperty( p, value );

					assertFalse( value.getLexicalForm() + " should not belong to "
							+ datatype.getLocalName(), model.validate().isValid() );
					i++;
				}
			}
		}
	}

	@Test
	public void testBuiltinDatatypesWithCardinalityRestriction() {
		String ns = "urn:test:";

		DatatypeReasoner dtReasoner = new DatatypeReasonerImpl();
		for( ATermAppl uri : dtReasoner.listDataRanges() ) {
			OntModel model = ModelFactory.createOntologyModel( PelletReasonerFactory.THE_SPEC );
			DatatypeProperty prop = model.createDatatypeProperty( ns + "prop" );
			Resource datatype = model.createResource( uri.getName() );
			prop.addRange( datatype );
			OntClass C = model.createClass( ns + "C" );
			int cardinality = datatype.equals( XSD.xboolean )
				? 2
				: 10;
			C.addSuperClass( model.createCardinalityRestriction( null, prop, cardinality ) );
			model.createIndividual( ns + "x", C );

			model.prepare();

			assertTrue( ((PelletInfGraph) model.getGraph()).isConsistent() );
		}
	}

	@Test
	public void testBuiltinDatatypesWithHasValueRestriction() {
		String ns = "urn:test:";

		Object[] datatypes = {
				XSD.anyURI, "http://www.w3.com", XSD.xboolean, "true", "1", XSD.xbyte, "8",
				XSD.date, "2004-03-15", XSD.dateTime, "2003-12-25T08:30:00",
				"2003-12-25T08:30:00.001", "2003-12-25T08:30:00-05:00", "2003-12-25T08:30:00Z",
				XSD.decimal, "3.1415292", XSD.xdouble, "3.1415292", "INF", "NaN", XSD.duration,
				"P8M3DT7H33M2S", "P1Y", "P1M", "P1Y2MT2H", XSD.xfloat, "3.1415292", "-1E4",
				"12.78e-2", "INF", "NaN", XSD.gDay, "---11", XSD.gMonth, "--02", XSD.gMonthDay,
				"--02-14", XSD.gYear, "0001", "1999", XSD.gYearMonth, "1972-08", XSD.xint, "77",
				XSD.integer, "77", XSD.xlong, "214", XSD.negativeInteger, "-123",
				XSD.nonNegativeInteger, "2", XSD.nonPositiveInteger, "0", XSD.positiveInteger,
				"500", XSD.xshort, "476", XSD.xstring, "Test", XSD.time, "13:02:00", };

		for( int i = 0; i < datatypes.length; ) {
			Resource datatype = (Resource) datatypes[i++];
			OntModel model = ModelFactory.createOntologyModel( PelletReasonerFactory.THE_SPEC );

			OntProperty p = model.createDatatypeProperty( ns + "prop" );
			p.addRange( datatype );

			int start = i;
			while( i < datatypes.length && datatypes[i] instanceof String ) {
				Individual ind = model.createIndividual( ns + "testInd" + i, OWL.Thing );
				Literal value = model.createTypedLiteral( (String) datatypes[i], datatype.getURI() );
				ind.addProperty( p, value );

				OntClass c = model.createClass( ns + "testCls" + i );
				c.addEquivalentClass( model.createHasValueRestriction( null, p, value ) );

				i++;
			}

			model.prepare();

			for( int j = start; j < i; j++ ) {
				assertTrue( datatype.getLocalName() + " " + datatypes[j], model.getIndividual(
						ns + "testInd" + j ).hasRDFType( model.getIndividual( ns + "testCls" + j ) ) );
			}
		}
	}

	@Test
	public void testFamily() {
		String ns = "http://www.example.org/family#";

		OntModel model = ModelFactory.createOntologyModel( PelletReasonerFactory.THE_SPEC, null );
		model.read( base + "family.owl" );

		ObjectProperty hasBrother = model.getObjectProperty( ns + "hasBrother" );
		ObjectProperty hasSon = model.getObjectProperty( ns + "hasSon" );
		ObjectProperty hasFather = model.getObjectProperty( ns + "hasFather" );
		ObjectProperty hasParent = model.getObjectProperty( ns + "hasParent" );
		ObjectProperty hasChild = model.getObjectProperty( ns + "hasChild" );
		ObjectProperty hasMother = model.getObjectProperty( ns + "hasMother" );
		ObjectProperty hasDaughter = model.getObjectProperty( ns + "hasDaughter" );
		ObjectProperty hasAncestor = model.getObjectProperty( ns + "hasAncestor" );
		ObjectProperty likes = model.getObjectProperty( ns + "likes" );
		ObjectProperty isMarriedTo = model.getObjectProperty( ns + "isMarriedTo" );
		ObjectProperty dislikes = model.getObjectProperty( ns + "dislikes" );
		ObjectProperty hasSister = model.getObjectProperty( ns + "hasSister" );
		ObjectProperty hasDescendant = model.getObjectProperty( ns + "hasDescendant" );
		ObjectProperty hasSibling = model.getObjectProperty( ns + "hasSibling" );
		OntClass Child = model.getOntClass( ns + "Child" );
		OntClass Person = model.getOntClass( ns + "Person" );
		OntClass PersonWithAtLeastTwoMaleChildren = model.getOntClass( ns
				+ "PersonWithAtLeastTwoMaleChildren" );
		OntClass PersonWithAtLeastTwoFemaleChildren = model.getOntClass( ns
				+ "PersonWithAtLeastTwoFemaleChildren" );
		OntClass PersonWithAtLeastTwoChildren = model.getOntClass( ns
				+ "PersonWithAtLeastTwoChildren" );
		OntClass PersonWithAtLeastFourChildren = model.getOntClass( ns
				+ "PersonWithAtLeastFourChildren" );
		OntClass Teen = model.getOntClass( ns + "Teen" );
		OntClass Teenager = model.getOntClass( ns + "Teenager" );
		OntClass Male = model.getOntClass( ns + "Male" );
		OntClass Adult = model.getOntClass( ns + "Adult" );
		OntClass Female = model.getOntClass( ns + "Female" );
		OntClass Senior = model.getOntClass( ns + "Senior" );
		Individual grandmother = model.getIndividual( ns + "grandmother" );
		Individual grandfather = model.getIndividual( ns + "grandfather" );
		Individual father = model.getIndividual( ns + "father" );
		Individual son = model.getIndividual( ns + "son" );
		Individual mother = model.getIndividual( ns + "mother" );
		Individual daughter = model.getIndividual( ns + "daughter" );
		Individual personX = model.getIndividual( ns + "personX" );
		Individual personY = model.getIndividual( ns + "personY" );
		Individual personZ = model.getIndividual( ns + "personZ" );

		model.prepare();

		KnowledgeBase kb = ((PelletInfGraph) model.getGraph()).getKB();

		for( int test = 0; test < 2; test++ ) {
			if( test != 0 ) {
	            kb.realize();
            }

			assertTrue( hasAncestor.hasRDFType( OWL2.TransitiveProperty ) );
			assertTrue( hasDescendant.hasRDFType( OWL2.TransitiveProperty ) );
			assertTrue( isMarriedTo.hasRDFType( OWL2.SymmetricProperty ) );
			assertTrue( isMarriedTo.hasRDFType( OWL2.IrreflexiveProperty ) );

			assertTrue( hasParent.hasSuperProperty( hasAncestor, false ) );
			assertTrue( hasFather.hasSuperProperty( hasAncestor, false ) );
			assertTrue( hasMother.hasSuperProperty( hasAncestor, false ) );
			assertTrue( hasChild.hasSuperProperty( hasDescendant, false ) );

			assertTrue( likes.hasProperty( OWL2.propertyDisjointWith, dislikes ) );
			assertTrue( dislikes.hasProperty( OWL2.propertyDisjointWith, likes ) );
			assertTrue( hasFather.hasProperty( OWL2.propertyDisjointWith, hasMother ) );
			assertTrue( hasMother.hasProperty( OWL2.propertyDisjointWith, hasFather ) );

			assertTrue( grandfather.hasRDFType( Person ) );
			assertTrue( grandfather.hasRDFType( Male ) );
			assertTrue( grandfather.hasRDFType( Senior ) );
			assertTrue( grandfather.hasRDFType( PersonWithAtLeastTwoChildren ) );
			assertTrue( grandfather.hasRDFType( PersonWithAtLeastTwoMaleChildren ) );
			assertTrue( grandfather.hasProperty( isMarriedTo, grandmother ) );
			assertTrue( grandfather.hasProperty( hasChild, father ) );
			assertTrue( grandfather.hasProperty( hasSon, father ) );
			assertTrue( grandfather.isDifferentFrom( grandmother ) );
			assertTrue( grandfather.isDifferentFrom( father ) );
			assertTrue( grandfather.isDifferentFrom( mother ) );
			assertTrue( grandfather.isDifferentFrom( son ) );
			assertTrue( grandfather.isDifferentFrom( daughter ) );

			assertTrue( grandmother.hasRDFType( Person ) );
			assertTrue( grandmother.hasRDFType( Female ) );
			assertTrue( grandmother.hasRDFType( Senior ) );
			assertTrue( grandmother.hasProperty( isMarriedTo, grandfather ) );
			assertTrue( grandmother.hasProperty( hasChild, father ) );
			assertFalse( grandmother.hasProperty( hasSon, father ) );

			assertTrue( father.hasRDFType( Person ) );
			assertTrue( father.hasRDFType( Male ) );
			assertTrue( father.hasRDFType( Adult ) );
			assertTrue( father.hasProperty( hasParent, grandfather ) );
			assertTrue( father.hasProperty( hasParent, grandmother ) );
			assertTrue( father.hasProperty( hasFather, grandfather ) );
			assertTrue( father.hasProperty( hasMother, grandmother ) );
			assertTrue( father.hasProperty( hasChild, son ) );
			assertTrue( father.hasProperty( hasSon, son ) );
			assertTrue( father.hasProperty( hasChild, daughter ) );
			assertFalse( father.hasProperty( hasDaughter, daughter ) );

			assertTrue( mother.hasRDFType( Person ) );
			assertTrue( mother.hasRDFType( Female ) );

			assertTrue( son.hasRDFType( Male ) );
			assertTrue( son.hasRDFType( Teenager ) );
			assertTrue( son.hasRDFType( Teen ) );
			assertTrue( son.hasProperty( hasParent, father ) );
			assertTrue( son.hasProperty( hasFather, father ) );
			assertTrue( son.hasProperty( hasSibling, daughter ) );
			assertTrue( son.hasProperty( hasSister, daughter ) );

			assertTrue( daughter.hasRDFType( Female ) );
			assertTrue( daughter.hasRDFType( Child ) );
			assertTrue( daughter.hasProperty( hasAncestor, grandfather ) );
			assertTrue( daughter.hasProperty( hasAncestor, grandmother ) );
			assertTrue( daughter.hasProperty( hasParent, father ) );
			assertTrue( daughter.hasProperty( hasFather, father ) );
			assertTrue( daughter.hasProperty( hasParent, mother ) );
			assertTrue( daughter.hasProperty( hasMother, mother ) );
			assertTrue( daughter.hasProperty( hasSibling, son ) );
			assertFalse( daughter.hasProperty( hasBrother, son ) );

			assertTrue( personX.isDifferentFrom( personY ) );
			assertTrue( personX.isDifferentFrom( personZ ) );
			assertTrue( personY.isDifferentFrom( personZ ) );

			assertTrue( Teen.hasEquivalentClass( Teenager ) );
			assertTrue( Senior.hasSuperClass( Adult ) );
			assertTrue( Adult.hasSubClass( Senior ) );

			assertTrue( Person.hasSubClass( PersonWithAtLeastTwoMaleChildren ) );
			assertTrue( Person.hasSubClass( PersonWithAtLeastTwoFemaleChildren ) );
			assertTrue( Person.hasSubClass( PersonWithAtLeastTwoChildren ) );
			assertTrue( Person.hasSubClass( PersonWithAtLeastFourChildren ) );
			assertTrue( PersonWithAtLeastTwoChildren.hasSubClass( PersonWithAtLeastFourChildren ) );
			assertTrue( PersonWithAtLeastTwoChildren.hasSubClass( PersonWithAtLeastTwoMaleChildren ) );
			assertTrue( PersonWithAtLeastTwoChildren
					.hasSubClass( PersonWithAtLeastTwoFemaleChildren ) );

			assertFalse( PersonWithAtLeastTwoFemaleChildren
					.hasSubClass( PersonWithAtLeastTwoMaleChildren ) );
			assertFalse( PersonWithAtLeastTwoMaleChildren
					.hasSubClass( PersonWithAtLeastTwoFemaleChildren ) );
		}
	}

	@Test
	public void testSibling() {
		String ns = "http://www.example.org/test#";

		OntModel model = ModelFactory.createOntologyModel( PelletReasonerFactory.THE_SPEC );
		model.read( base + "sibling.owl" );

		Individual Bob = model.getIndividual( ns + "Bob" );
		Individual John = model.getIndividual( ns + "John" );
		Individual Jane = model.getIndividual( ns + "Jane" );

		Property hasBrother = model.getProperty( ns + "hasBrother" );
		Property hasSister = model.getProperty( ns + "hasSister" );

		assertPropertyValues( model, Bob, hasBrother, John );
		assertPropertyValues( model, Bob, hasSister, Jane );
	}

	@Test
	public void testDLSafeRules() {
		String ns = "http://owldl.com/ontologies/dl-safe.owl#";

		OntModel model = ModelFactory.createOntologyModel( PelletReasonerFactory.THE_SPEC, null );
		model.read( base + "dl-safe.owl" );

		// ObjectProperty father = model.getObjectProperty( ns + "father" );
		ObjectProperty hates = model.getObjectProperty( ns + "hates" );
		ObjectProperty sibling = model.getObjectProperty( ns + "sibling" );

		OntClass BadChild = model.getOntClass( ns + "BadChild" );
		OntClass Child = model.getOntClass( ns + "Child" );
		// OntClass GoodChild = model.getOntClass( ns + "GoodChild" );
		OntClass Grandchild = model.getOntClass( ns + "Grandchild" );
		OntClass Person = model.getOntClass( ns + "Person" );

		Individual Abel = model.getIndividual( ns + "Abel" );
		Individual Cain = model.getIndividual( ns + "Cain" );
		Individual Oedipus = model.getIndividual( ns + "Oedipus" );
		Individual Remus = model.getIndividual( ns + "Remus" );
		Individual Romulus = model.getIndividual( ns + "Romulus" );

		model.prepare();

		KnowledgeBase kb = ((PelletInfGraph) model.getGraph()).getKB();

		for( int test = 0; test < 1; test++ ) {
			if( test != 0 ) {
	            kb.realize();
            }

			assertTrue( Abel.hasProperty( sibling, Cain ) );

			assertIteratorValues( Abel.listPropertyValues( sibling ), new Resource[] { Cain } );

			assertTrue( Cain.hasProperty( sibling, Abel ) );

			assertIteratorValues( Cain.listPropertyValues( sibling ), new Resource[] { Abel } );

			assertTrue( Cain.hasProperty( hates, Abel ) );

			assertTrue( Cain.hasRDFType( Grandchild ) );

			assertTrue( Cain.hasRDFType( BadChild ) );

			assertFalse( Romulus.hasProperty( sibling, Remus ) );

			assertTrue( Romulus.hasProperty( hates, Remus ) );

			assertTrue( Romulus.hasRDFType( Grandchild ) );

			assertFalse( Romulus.hasRDFType( BadChild ) );

			assertTrue( Oedipus.hasRDFType( Child ) );
		}

		assertIteratorValues( Cain.listRDFTypes( true ), new Object[] { BadChild, Child, Person } );
	}

	@Test
	public void testDLSafeConstants() {
		String ns = "http://owldl.com/ontologies/dl-safe-constants.owl#";

		OntModel model = ModelFactory.createOntologyModel( PelletReasonerFactory.THE_SPEC, null );
		model.read( base + "dl-safe-constants.owl" );

		OntClass DreamTeamMember = model.getOntClass( ns + "DreamTeamMember" );
		OntClass DreamTeamMember1 = model.getOntClass( ns + "DreamTeamMember1" );
		OntClass DreamTeamMember2 = model.getOntClass( ns + "DreamTeamMember2" );

		Individual Alice = model.getIndividual( ns + "Alice" );
		Individual Bob = model.getIndividual( ns + "Bob" );
		Individual Charlie = model.getIndividual( ns + "Charlie" );

		model.prepare();

		KnowledgeBase kb = ((PelletInfGraph) model.getGraph()).getKB();

		for( int test = 0; test < 1; test++ ) {
			if( test != 0 ) {
	            kb.realize();
            }

			assertIteratorValues( DreamTeamMember.listInstances(), new Object[] {
					Alice, Bob, Charlie } );

			assertIteratorValues( DreamTeamMember1.listInstances(), new Object[] {
					Alice, Bob, Charlie } );

			assertIteratorValues( DreamTeamMember2.listInstances(), new Object[] {
					Alice, Bob, Charlie } );
		}
	}

	@Test
	public void testMergeRestore() {
		String src = "" + "@prefix rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>.\r\n"
				+ "@prefix rdfs: <http://www.w3.org/2000/01/rdf-schema#>.\r\n"
				+ "@prefix owl: <http://www.w3.org/2002/07/owl#>.\r\n"
				+ "@prefix : <foo:bla#>.\r\n" + "\r\n" + ":one a :NoLeft .\r\n"
				+ ":one :right :two .\r\n" + ":two :right :three .\r\n"
				+ ":three :right :four .\r\n" + ":four :right :five .\r\n"
				+ ":five a :NoRight .\r\n" + "\r\n" + ":NoRight a owl:Class;\r\n"
				+ "      owl:intersectionOf ( " + "      [" + "         a owl:Restriction; "
				+ "         owl:onProperty :right; " + "         owl:cardinality 0 "
				+ "      ] \r\n" + "      [" + "         a owl:Restriction; "
				+ "         owl:onProperty :neighbor; " + "         owl:cardinality 1 "
				+ "      ] ) .\r\n" + "\r\n" + ":NoLeft a owl:Class;\r\n"
				+ "      owl:intersectionOf ( " + "      [" + "         a owl:Restriction; "
				+ "         owl:onProperty :left; " + "         owl:cardinality 0 "
				+ "      ] \r\n" + "      [" + "         a owl:Restriction; "
				+ "         owl:onProperty :neighbor; " + "         owl:cardinality 1 "
				+ "      ] ) .\r\n" + "\r\n"
				+ ":left a owl:FunctionalProperty; owl:inverseOf :right;\r\n"
				+ "      rdfs:subPropertyOf :neighbor .\r\n"
				+ ":right a owl:FunctionalProperty; \r\n"
				+ "      rdfs:subPropertyOf :neighbor .\r\n" + "\r\n"
				+ ":Universe a owl:Class;\r\n"
				+ "   owl:oneOf (:one :two :three :four :five );\r\n" + "   rdfs:subClassOf ["
				+ "          a owl:Restriction; " + "          owl:onProperty :neighbor;\r\n"
				+ "          owl:maxCardinality 2 ] .\r\n" + "\r\n"
				+ ":neighbor rdfs:domain :Universe; rdfs:range :Universe .\r\n" + "\r\n"
				+ ":x :neighbor :y . \r\n" + "";

		OntModel model = ModelFactory.createOntologyModel( PelletReasonerFactory.THE_SPEC );

		model.read( new StringReader( src ), null, "Turtle" );

		model.prepare();

		String ns = "foo:bla#";
		Property left = model.getProperty( ns + "left" );
		Property right = model.getProperty( ns + "right" );
		Resource[] r = new Resource[6];
		r[1] = model.getProperty( ns + "one" );
		r[2] = model.getProperty( ns + "two" );
		r[3] = model.getProperty( ns + "three" );
		r[4] = model.getProperty( ns + "four" );
		r[5] = model.getProperty( ns + "five" );

		assertTrue( model.contains( r[5], left, r[4] ) );

		Model rightValues = ModelFactory.createDefaultModel();
		Model leftValues = ModelFactory.createDefaultModel();
		for( int i = 1; i <= 5; i++ ) {
			if( i != 5 ) {
	            addStatements( rightValues, r[i], right, r[i + 1] );
            }
			if( i != 1 ) {
	            addStatements( leftValues, r[i], left, r[i - 1] );
            }
		}

		assertPropertyValues( model, left, leftValues );
		assertPropertyValues( model, right, rightValues );
	}

	@Test
	public void testDisjunction() {
		String ns = "urn:test:";
		OntModel model = ModelFactory.createOntologyModel( PelletReasonerFactory.THE_SPEC );

		OntClass A = model.createClass( ns + "A" );
		OntClass B = model.createClass( ns + "B" );
		OntClass notA = model.createComplementClass( null, A );
		OntClass notB = model.createComplementClass( null, B );
		OntClass AorB = model.createUnionClass( null, model.createList( new OntClass[] { A, B } ) );
		OntClass AorNotB = model.createUnionClass( null, model
				.createList( new OntClass[] { A, notB } ) );
		OntClass notAorB = model.createUnionClass( null, model
				.createList( new OntClass[] { notA, B } ) );

		Individual x = model.createIndividual( ns + "x", OWL.Thing );
		x.addRDFType( AorB );
		x.addRDFType( AorNotB );
		x.addRDFType( notAorB );

		assertTrue( x.hasRDFType( A ) );
		assertTrue( x.hasRDFType( B ) );
	}

	@Test
	public void testListDirectSubProps() {
		// This test case is to test the Jena interface for listing
		// direct sub properties (see ticket 99)

		String ns = "urn:test:";

		OntModel reasoner = ModelFactory.createOntologyModel( PelletReasonerFactory.THE_SPEC );

		ObjectProperty p = reasoner.createObjectProperty( ns + "p" );
		ObjectProperty subP = reasoner.createObjectProperty( ns + "subP" );
		ObjectProperty subSubP = reasoner.createObjectProperty( ns + "subSubP" );
		
		DatatypeProperty q = reasoner.createDatatypeProperty( ns + "q" );
		DatatypeProperty subQ = reasoner.createDatatypeProperty( ns + "subQ" );
		DatatypeProperty subSubQ = reasoner.createDatatypeProperty( ns + "subSubQ" );
		
		
		// create assertions in one RDF model
		Model assertions = ModelFactory.createDefaultModel();
		assertions.add( subP, RDFS.subPropertyOf, p );
		assertions.add( subSubP, RDFS.subPropertyOf, subP );
		assertions.add( subQ, RDFS.subPropertyOf, q );
		assertions.add( subSubQ, RDFS.subPropertyOf, subQ );

		// load the assertions to the reasoner
		reasoner.add( assertions );

		// create the inferences for testing in a separate RDF model
		Model inferences = ModelFactory.createDefaultModel();
		// all assertions should be inferred
		inferences.add( assertions );
		// rdfs:subPropertyOf is reflexive
		for( Property op : new Property[] { p, subP, subSubP, q, subQ, subSubQ } ) {
			inferences.add( op, RDFS.subPropertyOf, op );
		}
		// All object properties are a sub property of topObjectProperty
		for( Property op : new Property[] { p, subP, subSubP, OWL2.topObjectProperty, OWL2.bottomObjectProperty } ) {
			inferences.add( op, RDFS.subPropertyOf, OWL2.topObjectProperty );
			inferences.add( OWL2.bottomObjectProperty, RDFS.subPropertyOf, op );
		}
		// All data properties are a sub property of topDataProperty
		for( Property dp: new Property[] { q, subQ, subSubQ, OWL2.topDataProperty, OWL2.bottomDataProperty } ) {
			inferences.add( dp, RDFS.subPropertyOf, OWL2.topDataProperty );
			inferences.add( OWL2.bottomDataProperty, RDFS.subPropertyOf, dp );
		}
		// the real inferred relations
		inferences.add( subSubP, RDFS.subPropertyOf, p );
		inferences.add( subSubQ, RDFS.subPropertyOf, q );
		// check if all inferences hold
		assertPropertyValues( reasoner, RDFS.subPropertyOf, inferences );

		// check for direct sub-properties
		assertIteratorValues( p.listSubProperties( true ), new RDFNode[] { subP } );
		assertIteratorValues( subP.listSuperProperties( true ), new RDFNode[] { p } );
		assertIteratorValues( subP.listSubProperties( true ), new RDFNode[] { subSubP } );
		assertIteratorValues( subSubP.listSuperProperties( true ), new RDFNode[] { subP } );

		assertIteratorValues( q.listSubProperties( true ), new RDFNode[] { subQ } );
		assertIteratorValues( subQ.listSuperProperties( true ), new RDFNode[] { q } );
		assertIteratorValues( subQ.listSubProperties( true ), new RDFNode[] { subSubQ } );
		assertIteratorValues( subSubQ.listSuperProperties( true ), new RDFNode[] { subQ } );
	}

	@Test
	public void testTicket96() {
		OntModel pellet = ModelFactory.createOntologyModel( PelletReasonerFactory.THE_SPEC );

		pellet.read( base + "ticket-96-test-case.rdf" );

		assertTrue( pellet.validate().isValid() );
	}

	@Test
	public void testNaryDisjointness() {
		// tests whether owl:Alldifferent, owl:AllDisjointClasses,
		// owl:AllDisjointProperties
		// statements in RDF/XML will be parsed correctly to yield
		// owl:differentFrom,
		// owl:disjointWith, owl:propertyDisjointWith inferences

		String ns = "http://www.example.org/test#";

		OntModel model = ModelFactory.createOntologyModel( PelletReasonerFactory.THE_SPEC );
		model.read( base + "disjoints.owl" );

		Model inferences = ModelFactory.createDefaultModel();
		addStatements( inferences, OWL.Nothing, OWL.disjointWith, OWL.Nothing );
		addStatements( inferences, OWL.Nothing, OWL.disjointWith, OWL.Thing );
		addStatements( inferences, OWL.Thing, OWL.disjointWith, OWL.Nothing );
		for( int k = 1; k < 6; k += 3 ) {
			for( int i = k; i < k + 3; i++ ) {
				Resource c1 = model.getResource( ns + "C" + i );
				addStatements( inferences, c1, OWL.disjointWith, OWL.Nothing );
				addStatements( inferences, OWL.Nothing, OWL.disjointWith, c1 );
				for( int j = k; j < k + 3; j++ ) {
					if( i == j ) {
	                    continue;
                    }
					Resource c2 = model.getResource( ns + "C" + j );
					addStatements( inferences, c1, OWL.disjointWith, c2 );
				}
			}
		}
		assertPropertyValues( model, OWL.disjointWith, inferences );

		inferences = ModelFactory.createDefaultModel();
		addStatements( inferences, OWL2.bottomObjectProperty, OWL2.propertyDisjointWith, OWL2.bottomObjectProperty );
		addStatements( inferences, OWL2.topObjectProperty, OWL2.propertyDisjointWith, OWL2.bottomObjectProperty );
		addStatements( inferences, OWL2.bottomObjectProperty, OWL2.propertyDisjointWith, OWL2.topObjectProperty );
		addStatements( inferences, OWL2.bottomDataProperty, OWL2.propertyDisjointWith, OWL2.bottomDataProperty );
		addStatements( inferences, OWL2.topDataProperty, OWL2.propertyDisjointWith, OWL2.bottomDataProperty );
		addStatements( inferences, OWL2.bottomDataProperty, OWL2.propertyDisjointWith, OWL2.topDataProperty );
		for( String prefix : new String[] { "op", "dp" } ) {
			for( int k = 1; k < 6; k += 3 ) {
				for( int i = k; i < k + 3; i++ ) {
					Resource c1 = model.getResource( ns + prefix + i );
					if( prefix.equals( "op" ) ) {
						addStatements( inferences, c1, OWL2.propertyDisjointWith, OWL2.bottomObjectProperty );
						addStatements( inferences,  OWL2.bottomObjectProperty, OWL2.propertyDisjointWith, c1 );
					}
					else {
						addStatements( inferences, c1, OWL2.propertyDisjointWith, OWL2.bottomDataProperty );
						addStatements( inferences, OWL2.bottomDataProperty, OWL2.propertyDisjointWith, c1 );
					}
					for( int j = k; j < k + 3; j++ ) {
						if( i == j ) {
	                        continue;
                        }
						Resource c2 = model.getResource( ns + prefix + j );
						addStatements( inferences, c1, OWL2.propertyDisjointWith, c2 );
					}
				}
			}
		}
		assertPropertyValues( model, OWL2.propertyDisjointWith, inferences );

		inferences = ModelFactory.createDefaultModel();
		for( int k = 1; k < 6; k += 3 ) {
			for( int i = k; i < k + 3; i++ ) {
				Resource c1 = model.getResource( ns + "ind" + i );
				for( int j = k; j < k + 3; j++ ) {
					if( i == j ) {
	                    continue;
                    }
					Resource c2 = model.getResource( ns + "ind" + j );
					addStatements( inferences, c1, OWL.differentFrom, c2 );
				}
			}
		}
		assertPropertyValues( model, OWL2.differentFrom, inferences );
	}

	@Test
	public void testHasKey1() {
		String ns = "http://www.example.org#";
		OntModel model = ModelFactory.createOntologyModel( PelletReasonerFactory.THE_SPEC );

		Resource C = model.createClass( ns + "C" );
		Resource i = model.createResource( ns + "i" );
		Resource j = model.createResource( ns + "j" );
		Resource k = model.createResource( ns + "k" );
		Property p = model.createObjectProperty( ns + "p" );
		RDFList list = model.createList( new RDFNode[] { p } );

		model.add( C, OWL2.hasKey, list );
		model.add( i, RDF.type, C );
		model.add( i, p, k );
		model.add( j, RDF.type, C );
		model.add( j, p, k );

		model.prepare();

		assertTrue( model.contains( i, OWL.sameAs, j ) );
		assertTrue( model.contains( j, OWL.sameAs, i ) );
	}

	@Test
	public void testHasKey2() {
		String ns = "http://www.example.org#";
		OntModel model = ModelFactory.createOntologyModel( PelletReasonerFactory.THE_SPEC );

		Resource C = model.createClass( ns + "C" );
		Resource i = model.createResource( ns + "i" );
		Resource j = model.createResource( ns + "j" );
		Resource k = model.createResource( ns + "k" );
		Resource l = model.createResource( ns + "l" );
		Property p1 = model.createObjectProperty( ns + "p1" );
		Property p2 = model.createObjectProperty( ns + "p2" );
		RDFList list = model.createList( new RDFNode[] { p1, p2 } );

		model.add( C, OWL2.hasKey, list );
		model.add( i, RDF.type, C );
		model.add( i, p1, k );
		model.add( i, p2, l );
		model.add( j, RDF.type, C );
		model.add( j, p1, k );
		model.add( j, p2, l );

		model.prepare();

		assertTrue( model.contains( i, OWL.sameAs, j ) );
		assertTrue( model.contains( j, OWL.sameAs, i ) );
	}

	@Test
	public void testHasKey3() {
		String ns = "http://www.example.org#";
		OntModel model = ModelFactory.createOntologyModel( PelletReasonerFactory.THE_SPEC );

		Resource C = model.createClass( ns + "C" );
		Resource i = model.createResource( ns + "i" );
		Resource j = model.createResource( ns + "j" );
		Literal k = model.createLiteral( "k" );
		Property p = model.createDatatypeProperty( ns + "p" );
		RDFList list = model.createList( new RDFNode[] { p } );

		model.add( C, OWL2.hasKey, list );
		model.add( i, RDF.type, C );
		model.add( i, p, k );
		model.add( j, RDF.type, C );
		model.add( j, p, k );

		model.prepare();

		assertTrue( model.contains( i, OWL.sameAs, j ) );
		assertTrue( model.contains( j, OWL.sameAs, i ) );
	}

	@Test
	public void testHasKey4() {
		String ns = "http://www.example.org#";
		OntModel model = ModelFactory.createOntologyModel( PelletReasonerFactory.THE_SPEC );

		Resource C = model.createClass( ns + "C" );
		Resource D = model.createClass( ns + "D" );
		Resource i = model.createResource( ns + "i" );
		Resource j = model.createResource( ns + "j" );
		Resource k = model.createResource( ns + "k" );
		Property p = model.createObjectProperty( ns + "p" );
		RDFList list = model.createList( new RDFNode[] { p } );

		model.add( C, OWL2.hasKey, list );
		model.add( i, RDF.type, C );
		model.add( i, p, k );
		model.add( j, RDF.type, D );
		model.add( j, p, k );

		model.prepare();

		assertFalse( model.contains( i, OWL.sameAs, j ) );
		assertFalse( model.contains( j, OWL.sameAs, i ) );
	}

	@Test
	public void testHasKey5() {
		String ns = "http://www.example.org#";
		OntModel model = ModelFactory.createOntologyModel( PelletReasonerFactory.THE_SPEC );

		Resource C = model.createClass( ns + "C" );
		Resource i = model.createResource( ns + "i" );
		Resource j = model.createResource( ns + "j" );
		Resource k = model.createResource( ns + "k" );
		Resource l = model.createResource( ns + "l" );
		Property p = model.createObjectProperty( ns + "p" );
		RDFList list = model.createList( new RDFNode[] { p } );

		model.add( C, OWL2.hasKey, list );
		model.add( i, RDF.type, C );
		model.add( i, p, k );
		model.add( j, RDF.type, C );
		model.add( j, p, l );

		model.prepare();

		assertFalse( model.contains( i, OWL.sameAs, j ) );
		assertFalse( model.contains( j, OWL.sameAs, i ) );
	}

	@Test
	public void testHasKey6() {
		String ns = "http://www.example.org#";
		OntModel model = ModelFactory.createOntologyModel( PelletReasonerFactory.THE_SPEC );

		Resource C = model.createClass( ns + "C" );
		Resource i = model.createResource( ns + "i" );
		Resource j = model.createResource( ns + "j" );
		Resource k = model.createResource( ns + "k" );
		Property p = model.createObjectProperty( ns + "p" );
		Property q = model.createObjectProperty( ns + "q" );
		RDFList list = model.createList( new RDFNode[] { p } );

		model.add( C, OWL2.hasKey, list );
		model.add( i, RDF.type, C );
		model.add( i, p, k );
		model.add( j, RDF.type, C );
		model.add( j, q, k );

		model.prepare();

		assertFalse( model.contains( i, OWL.sameAs, j ) );
		assertFalse( model.contains( j, OWL.sameAs, i ) );
	}

	@Test
	public void testHasKey7() {
		String ns = "http://www.example.org#";
		OntModel model = ModelFactory.createOntologyModel( PelletReasonerFactory.THE_SPEC );

		OntClass D = model.createClass( ns + "D" );
		OntClass E = model.createClass( ns + "E" );
		OntClass C = model
				.createIntersectionClass( null, model.createList( new RDFNode[] { D, E } ) );
		Resource i = model.createResource( ns + "i" );
		Resource j = model.createResource( ns + "j" );
		Resource k = model.createResource( ns + "k" );
		Property p = model.createObjectProperty( ns + "p" );
		RDFList list = model.createList( new RDFNode[] { p } );

		model.add( C, OWL2.hasKey, list );
		model.add( i, RDF.type, C );
		model.add( i, p, k );
		model.add( j, RDF.type, C );
		model.add( j, p, k );

		model.prepare();

		assertTrue( model.contains( i, OWL.sameAs, j ) );
		assertTrue( model.contains( j, OWL.sameAs, i ) );
	}

	@Test
	public void testHasKey8() {
		String ns = "http://www.example.org#";
		OntModel model = ModelFactory.createOntologyModel( PelletReasonerFactory.THE_SPEC );

		Resource i = model.createResource( ns + "i" );
		Resource j = model.createResource( ns + "j" );
		Resource k = model.createResource( ns + "k" );
		Property p = model.createObjectProperty( ns + "p" );
		RDFList list = model.createList( new RDFNode[] { p } );

		model.add( OWL.Thing, OWL2.hasKey, list );
		model.add( i, RDF.type, OWL.Thing );
		model.add( i, p, k );
		model.add( j, RDF.type, OWL.Thing );
		model.add( j, p, k );

		model.prepare();

		assertTrue( model.contains( i, OWL.sameAs, j ) );
		assertTrue( model.contains( j, OWL.sameAs, i ) );
	}

	@Test
	public void testDataPropertyDefinition() {
		String ns = "foo://example#";
		String source1 = 
			"@prefix owl: <http://www.w3.org/2002/07/owl#>.\r\n"
			+ "@prefix rdfs: <http://www.w3.org/2000/01/rdf-schema#>.\r\n"
			+ "@prefix : <foo://example#>.\r\n"
			+ ":C rdfs:subClassOf [\n"
			+ "      a owl:Class; \n" 
			+ "      owl:intersectionOf( [\n"
			+ "                  a owl:Restriction;\n"
			+ "                  owl:onProperty :p ;\n"
			+ "                  owl:minCardinality \"1\"\n" 
			+ "                ] ) ] .";
			
		String source2 = 
			"@prefix owl: <http://www.w3.org/2002/07/owl#>.\n"
			+ "@prefix : <foo://example#>.\n"
			+ ":p a owl:DatatypeProperty .\n";
		
		Model model1 = ModelFactory.createDefaultModel();
		model1.read( new StringReader( source1 ), "", "N3" );
		Model model2 = ModelFactory.createDefaultModel();
		model2.read( new StringReader( source2 ), "", "N3" );

		Model ordered = ModelFactory.createModelForGraph( new Union( model1.getGraph(), model2.getGraph() ) );
		OntModel pellet = ModelFactory.createOntologyModel( PelletReasonerFactory.THE_SPEC, ordered );
		
		Property p = pellet.getProperty( ns + "p" );
		
		assertTrue( pellet.contains( p, RDFS.range, RDFS.Literal ) );
	}
	
	@Test
	public void testRemoveSubModel() {
		String ns = "http://www.example.org#";
		
		OntModel model = ModelFactory.createOntologyModel( PelletReasonerFactory.THE_SPEC );
		OntModel subModel = ModelFactory.createOntologyModel( OntModelSpec.OWL_MEM );

		OntClass C = model.createClass( ns + "C" );
		
		Resource a = model.createResource( ns + "a" );
		Resource b = model.createResource( ns + "b" );
		
		model.add( a, RDF.type, C );
		subModel.add( b, RDF.type, C );
				
		assertIteratorValues( model.listIndividuals( C ), new Resource[] { a } );
		
		model.addSubModel( subModel );	
		assertIteratorValues( model.listIndividuals( C ), new Resource[] { a, b } );
		
		model.removeSubModel( subModel );
		assertIteratorValues( model.listIndividuals( C ), new Resource[] { a } );
	}

	@Test
	public void testCardinalityParsing() {
		OntModel model = ModelFactory.createOntologyModel( PelletReasonerFactory.THE_SPEC );
		model.read( base + "cardinality_parsing.owl" );
		model.prepare();
		
		assertTrue( ((PelletInfGraph) model.getGraph()).getLoader().getUnpportedFeatures().isEmpty() );
	}
	

	@Test
	public void testAnnotationPropertyQuery() {
		String ns = "http://www.example.org#";
		
		OntModel model = ModelFactory.createOntologyModel( PelletReasonerFactory.THE_SPEC );
		Property p = model.createAnnotationProperty( ns + "p" );
		model.prepare();

		assertTrue(model.contains(p, RDF.type, OWL.AnnotationProperty));
		assertIteratorContains( model.listObjectsOfProperty( p, RDF.type ), OWL.AnnotationProperty );
		assertIteratorContains( model.listObjectsOfProperty( p, null ), OWL.AnnotationProperty );

		assertTrue(model.contains(RDFS.label, RDF.type, OWL.AnnotationProperty));
		assertIteratorContains( model.listObjectsOfProperty( RDFS.label, RDF.type ), OWL.AnnotationProperty );
		assertIteratorContains( model.listObjectsOfProperty( RDFS.label, null ), OWL.AnnotationProperty );
	}
	
	@Test
	public void testTopBottomPropertyAssertion() throws MalformedURLException {
		OntModel model = ModelFactory.createOntologyModel( PelletReasonerFactory.THE_SPEC, null );

		Resource a = model.createResource( "a", OWL.Thing );
		Resource b = model.createResource( "b", OWL.Thing );
		Literal lit = model.createLiteral( "l" );

		Statement[] stats = new Statement[] {
			model.createStatement( a, OWL2.topObjectProperty, b ),
			model.createStatement( a, OWL2.topDataProperty, lit ),
			model.createStatement( a, OWL2.bottomObjectProperty, b ),
			model.createStatement( a, OWL2.bottomDataProperty, lit )
		};
		
		for( int i = 0; i < stats.length; i++ ) {			
			model.add( stats[i] );
			model.prepare();
			assertEquals( i < 2, ((PelletInfGraph) model.getGraph()).isConsistent() );
			model.remove( stats[i] );
		}				
	}
	
	@Test
	public void testTopBottomPropertyInferences() throws MalformedURLException {
		OntModel model = ModelFactory.createOntologyModel( PelletReasonerFactory.THE_SPEC, null );

		Resource a = model.createResource( "a", OWL.Thing );
		Resource b = model.createResource( "b", OWL.Thing );
		Literal lit = model.createLiteral( "l" );

		Property p = model.createProperty( "p" );
		Property dp = model.createProperty( "dp" );
		
		model.add( a, p, b );
		model.add( a, dp, lit );
		
		assertTrue( model.contains( a, p, b ) );
		assertFalse( model.contains( b, p, a ) );
		assertTrue( model.contains( a, OWL2.topObjectProperty, b ) );
		assertTrue( model.contains( b, OWL2.topObjectProperty, a ) );
		
		assertTrue( model.contains( a, dp, lit ) );
		assertFalse( model.contains( b, dp, lit ) );
		assertTrue( model.contains( a, OWL2.topDataProperty, lit ) );
		assertTrue( model.contains( b, OWL2.topDataProperty, lit ) );
	}	
	
	@Test
	/**
	 * Test for the enhancement required in #252
	 */
	public void testBooleanDatatypeConstructors() {
		String ns = "http://www.example.org/test#";
		
		OntModel model = ModelFactory.createOntologyModel( PelletReasonerFactory.THE_SPEC );
					
		Resource nni = XSD.nonNegativeInteger;
		Resource npi = XSD.nonPositiveInteger;
		Resource ni = XSD.negativeInteger;
		Resource pi = XSD.positiveInteger;
		Resource i = XSD.integer;
		Resource f = XSD.xfloat;
		
		DatatypeProperty s = model.createDatatypeProperty( ns + "s" );
		
		OntClass c1 = model.createClass( ns + "c1" );
		c1.addEquivalentClass( model.createSomeValuesFromRestriction( null, s, pi ) );
		assertFalse( model.contains( c1, RDFS.subClassOf, OWL2.Nothing ) );
		
		OntClass c2 = model.createClass( ns + "c2" );
		Resource b2 = model.createResource();
		model.add(b2, RDF.type, OWL2.DataRange);
		model.add(b2, OWL2.datatypeComplementOf, pi);
		c2.addEquivalentClass( model.createSomeValuesFromRestriction( null, s, b2 ) );
		assertFalse( model.contains( c2, RDFS.subClassOf, OWL2.Nothing ) );
		
		OntClass c3 = model.createClass( ns + "c3" );
		RDFNode[] l3 = new RDFNode[2];
		l3[0] = pi; 
		l3[1] = ni;
		c3.addEquivalentClass( model.createSomeValuesFromRestriction( null, s, model.createIntersectionClass( null, model.createList( l3 ) ) ) );
		assertTrue( model.contains(c3, RDFS.subClassOf, OWL2.Nothing ) );
		
		OntClass c4 = model.createClass( ns + "c4" );
		RDFNode[] l41 = new RDFNode[2];
		l41[0] = pi; 
		l41[1] = ni;
		RDFNode[] l42 = new RDFNode[2];
		l42[0] = f; 
		l42[1] = model.createUnionClass( null, model.createList( l41 ) );
		c4.addEquivalentClass( model.createSomeValuesFromRestriction( null, s, model.createIntersectionClass( null, model.createList( l42 ) ) ) );
		assertTrue( model.contains(c4, RDFS.subClassOf, OWL2.Nothing ) );
		
		OntClass c5 = model.createClass( ns + "c5" );
		RDFNode[] l5 = new RDFNode[2];
		l5[0] = npi; 
		l5[1] = ni;
		c5.addEquivalentClass( model.createSomeValuesFromRestriction( null, s, model.createIntersectionClass( null, model.createList( l5 ) ) ) );
		assertFalse( model.contains(c5, RDFS.subClassOf, OWL2.Nothing ) );
		
		OntClass c6 = model.createClass( ns + "c6" );
		RDFNode[] l6 = new RDFNode[2];
		l6[0] = nni; 
		l6[1] = pi;
		c6.addEquivalentClass( model.createSomeValuesFromRestriction( null, s, model.createIntersectionClass( null, model.createList( l6 ) ) ) );
		assertFalse( model.contains(c6, RDFS.subClassOf, OWL2.Nothing ) );
		
		OntClass c7 = model.createClass( ns + "c7" );
		RDFNode[] l7 = new RDFNode[2];
		l7[0] = nni; 
		l7[1] = npi;
		c7.addEquivalentClass( model.createSomeValuesFromRestriction( null, s, model.createUnionClass( null, model.createList( l7 ) ) ) );
		assertFalse( model.contains(c7, RDFS.subClassOf, OWL2.Nothing ) );
		
		OntClass c8 = model.createClass( ns + "c8" );
		RDFNode[] l8 = new RDFNode[2];
		l8[0] = nni; 
		l8[1] = npi;
		c8.addEquivalentClass( model.createSomeValuesFromRestriction( null, s, model.createIntersectionClass( null, model.createList( l8 ) ) ) );
		assertFalse( model.contains(c8, RDFS.subClassOf, OWL2.Nothing ) );
		
		OntClass c9 = model.createClass( ns + "c9" );
		Resource fr9 = model.createResource();
		model.add( fr9, OWL2.maxExclusive, model.createTypedLiteral( 0 ) );
		Resource b9 = model.createResource();
		model.add( b9, RDF.type, RDFS.Datatype);
		model.add( b9, OWL2.onDatatype, i);
		model.add( b9, OWL2.withRestrictions, model.createList( new RDFNode[] { fr9 } ));
		RDFNode[] l9 = new RDFNode[2];
		l9[0] = pi; 
		l9[1] = b9;
		c9.addEquivalentClass( model.createSomeValuesFromRestriction( null, s, model.createIntersectionClass( null, model.createList( l9 ) ) ) );
		assertTrue( model.contains(c9, RDFS.subClassOf, OWL2.Nothing ) );
		
		OntClass c10 = model.createClass( ns + "c10" );
		DatatypeProperty p = model.createDatatypeProperty( ns + "p" );
		Resource b10 = model.createResource();
		model.add( b10, RDF.type, RDFS.Datatype);
		model.add( b10, OWL.unionOf, model.createList( new RDFNode[] { pi, ni } ));
		model.add(p, RDFS.range, b10 );
		c10.addEquivalentClass( model.createSomeValuesFromRestriction( null, p, XSD.anyURI ) );
		assertTrue( model.contains(c10, RDFS.subClassOf, OWL2.Nothing ) );
		
	}
	
	@Test
	public void datatypeDefinition() {
		String ns = "http://www.example.org/test#";

		OntModel model = ModelFactory.createOntologyModel( PelletReasonerFactory.THE_SPEC );
		model.read( base + "/datatypeDefinition.ttl" , "TTL");
		model.prepare();

		Resource a = model.getResource( ns + "a" );
		Resource b = model.getResource( ns + "b" );
		
		Resource A = model.getResource( ns + "A" );
		Resource B = model.getResource( ns + "B" );

		
		assertTrue( model.contains( B, RDFS.subClassOf, A ) );
		assertTrue( model.contains( a, RDF.type, A ) );
		assertFalse( model.contains( a, RDF.type, B ) );
		assertTrue( model.contains( b, RDF.type, A ) );
		assertTrue( model.contains( b, RDF.type, B ) );
	}
	
	@Test
	public void testDirectType() {
		OntModel model = ModelFactory.createOntologyModel( PelletReasonerFactory.THE_SPEC );

		String ns = "urn:test:";

		OntClass C1 = model.createClass( ns + "C1" );
		OntClass C2 = model.createClass( ns + "C2" );
		
		C1.addSubClass( C2 );
		
		Individual ind = model.createIndividual( ns + "ind", C2 );
			
		assertFalse( model.contains( ind, ReasonerVocabulary.directRDFType, C1 ) );
		assertTrue( model.contains( ind, ReasonerVocabulary.directRDFType, C2 ) );
		
		assertTrue( model.contains( C1, ReasonerVocabulary.directSubClassOf, OWL.Thing ) );
		assertFalse( model.contains( C2, ReasonerVocabulary.directSubClassOf, OWL.Thing ) );
		assertTrue( model.contains( C2, ReasonerVocabulary.directSubClassOf, C1 ) );
	}
	
	/**
	 * Ticket #445
	 */
	@Test
	public void testListStatementsWithNullPredicate() {
		OntModel model = ModelFactory.createOntologyModel( PelletReasonerFactory.THE_SPEC );
		
		model.prepare();
		
		String ns = "urn:test:";	

		Resource c = model.createResource(ns+"C");	
		Property p1 = model.createProperty(ns+"P1");
		Property p2 = model.createProperty(ns+"P2");
		Literal l = model.createLiteral("VAL");
		
		Statement s1 = new StatementImpl(c, p1, l);
		Statement s2 = new StatementImpl(c, p2, l);

		model.add(s1);
		model.add(new StatementImpl(p2, RDF.type, OWL.DatatypeProperty));
		model.add(new StatementImpl(p2, OWL.equivalentProperty, p1));

		StmtIterator iter = model.listStatements(c, null, l);

		List<Statement> results = new ArrayList<Statement>();
		while(iter.hasNext()) {
	        results.add(iter.next());
        }

		assertTrue(results.size() == 3); //s1, s2, and topProperty
		assertTrue(results.contains(s1));
		assertTrue(results.contains(s2));
	}

	@Test
	public void testUntypedProperty() {
		String ns = "http://www.example.org#";
		
		OntModel model = ModelFactory.createOntologyModel( PelletReasonerFactory.THE_SPEC );

		Resource C = model.createResource( ns + "C" );
		Resource p = model.createResource( ns + "p" );
		
		model.add( p, RDFS.domain, C );
		
		assertIteratorContains(model.listStatements(p, null, (RDFNode) null), model.createStatement(p, RDF.type, OWL.ObjectProperty));
	}
	
	@Test
	public void closeModel() {
		// ticket #487
		
		Model baseModel = ModelFactory.createDefaultModel();
		OntModel model = ModelFactory.createOntologyModel( PelletReasonerFactory.THE_SPEC, baseModel );

		model.close();
		
		assertTrue(model.isClosed());
		assertTrue(baseModel.isClosed());
	}
	
	@Test
	public void closeRecursive() {
		// ticket #487
		Model baseModel = ModelFactory.createDefaultModel();
		OntModel model = ModelFactory.createOntologyModel( PelletReasonerFactory.THE_SPEC, baseModel );

		((PelletInfGraph)model.getGraph()).close(true);
		
		assertTrue(model.isClosed());
		assertTrue(baseModel.isClosed());
	}
	
	@Test
	public void closeNonRecursive() {
		// ticket #487
		Model baseModel = ModelFactory.createDefaultModel();
		OntModel model = ModelFactory.createOntologyModel( PelletReasonerFactory.THE_SPEC, baseModel );

		((PelletInfGraph)model.getGraph()).close(false);
		
		assertTrue(model.isClosed());
		assertFalse(baseModel.isClosed());
	}
	
	@Test
	public void closeMultiple() {
		// ticket #487
		Model baseModel = ModelFactory.createDefaultModel();
		OntModel model = ModelFactory.createOntologyModel( PelletReasonerFactory.THE_SPEC, baseModel );

		((PelletInfGraph)model.getGraph()).close(false);
		
		assertTrue(model.isClosed());
		assertFalse(baseModel.isClosed());
		
		((PelletInfGraph)model.getGraph()).close(false);
		
		assertTrue(model.isClosed());
		assertFalse(baseModel.isClosed());
	}
	

	
	@Test
	public void testRemoveIndividual() {
		final Properties newOptions = PropertiesBuilder.singleton("PROCESS_JENA_UPDATES_INCREMENTALLY", "false");
		final Properties oldOptions = PelletOptions.setOptions(newOptions);
		try {
			String ns = "http://www.example.org/test#";

			OntModel model = ModelFactory.createOntologyModel(PelletReasonerFactory.THE_SPEC);

			Resource C = model.createClass(ns + "C");

			Property p = model.createObjectProperty(ns + "p");

			Resource i1 = model.createResource(ns + "i1");
			Resource i2 = model.createResource(ns + "i2");

			model.add(i1, RDF.type, C);
			model.add(i1, p, i2);

			// check consistency
			model.prepare();

			model.remove(i1, p, i2);

			assertTrue(model.contains(i1, RDF.type, OWL.Thing));
			assertFalse(model.contains(i2, RDF.type, OWL.Thing));

			model.remove(i1, RDF.type, C);
			assertFalse(model.contains(i1, RDF.type, OWL.Thing));

		} finally {
			PelletOptions.setOptions(oldOptions);
		}	
	}
	
	@Test
	public void testLoadingOrder() {
		String NS = "urn:test:";
		
		Property p1 = ResourceFactory.createProperty(NS+"p1");
		Resource C1 = ResourceFactory.createResource(NS+"C1");
		Property p2 = ResourceFactory.createProperty(NS+"p2");
		Resource C2 = ResourceFactory.createResource(NS+"C2");
		
		OntModel m1 = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM);
		m1.add(p1, RDF.type, OWL.DatatypeProperty);		
		m1.add(C1, RDFS.subClassOf, m1.createMinCardinalityRestriction(null, p2, 1));

		OntModel m2 = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM);
		m2.add(p2, RDF.type, OWL.DatatypeProperty);
		m2.add(C2, RDFS.subClassOf, m2.createMinCardinalityRestriction(null, p1, 1));
		

		OntModel reasoner = ModelFactory.createOntologyModel(PelletReasonerFactory.THE_SPEC);
		reasoner.addSubModel(m1);
		reasoner.addSubModel(m2);
		
		PelletInfGraph pellet = (PelletInfGraph) reasoner.getGraph();
		pellet.prepare();
		
		assertEquals(Collections.emptySet(), pellet.getLoader().getUnpportedFeatures());
	}
	
	@Test
	public void retrieveSubjectsOfBnode() {
		String NS = "urn:test:";
		Resource s = ResourceFactory.createResource(NS + "s");
		Property p = ResourceFactory.createProperty(NS + "p");
		Property q = ResourceFactory.createProperty(NS + "q");
		Resource o = ResourceFactory.createResource();
		OntModel pelletModel = ModelFactory.createOntologyModel(PelletReasonerFactory.THE_SPEC);
		pelletModel.add(q,RDFS.subPropertyOf,p);
		pelletModel.add(s,q,o);
		assertEquals(Collections.singletonList(s), pelletModel.listSubjectsWithProperty(p, o).toList());
	}

	
	@Test
	public void test549() {
		String ns = "urn:test:";

		OntModel model = ModelFactory.createOntologyModel( PelletReasonerFactory.THE_SPEC );
		model.read( base + "/float_intervals.ttl" , "TTL");
		model.prepare();

		Resource C1 = model.getResource( ns + "46-60" );
		Resource C2 = model.getResource( ns + "76-80" );
		
		Resource i1 = model.getResource( ns + "mark1" );
		Resource i2 = model.getResource( ns + "mark2" );

		assertIteratorValues(model.listSubjectsWithProperty(RDF.type, C1), i1);
		assertIteratorValues(model.listSubjectsWithProperty(RDF.type, C2), i2);
	}

	
	@Test
	public void testExtractor() {
		String ns = "urn:test:";

		Model rawModel = ModelFactory.createDefaultModel();
		
		Resource C = rawModel.createResource( ns + "C" );
		Resource D = rawModel.createResource( ns + "C" );
		Resource a = rawModel.createResource( ns + "a" );
		
		rawModel.add(a, RDF.type, C);
		rawModel.add(C, RDFS.subClassOf, D);
		
		OntModel model = ModelFactory.createOntologyModel( PelletReasonerFactory.THE_SPEC, rawModel );

		ModelExtractor extractor = new ModelExtractor(model);
		Model inferences = extractor.extractModel();
		
		assertTrue(inferences.contains(a, RDF.type, D));
	}
}
