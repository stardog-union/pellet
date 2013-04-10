// Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public
// License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of
// proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package com.clarkparsia.pellet.test.owlapi;

import static com.clarkparsia.owlapi.OWL.Class;
import static com.clarkparsia.owlapi.OWL.DataProperty;
import static com.clarkparsia.owlapi.OWL.Individual;
import static com.clarkparsia.owlapi.OWL.ObjectProperty;
import static com.clarkparsia.owlapi.OWL.all;
import static com.clarkparsia.owlapi.OWL.asymmetric;
import static com.clarkparsia.owlapi.OWL.classAssertion;
import static com.clarkparsia.owlapi.OWL.constant;
import static com.clarkparsia.owlapi.OWL.disjointClasses;
import static com.clarkparsia.owlapi.OWL.disjointProperties;
import static com.clarkparsia.owlapi.OWL.equivalentClasses;
import static com.clarkparsia.owlapi.OWL.functional;
import static com.clarkparsia.owlapi.OWL.inverse;
import static com.clarkparsia.owlapi.OWL.inverseFunctional;
import static com.clarkparsia.owlapi.OWL.irreflexive;
import static com.clarkparsia.owlapi.OWL.max;
import static com.clarkparsia.owlapi.OWL.min;
import static com.clarkparsia.owlapi.OWL.oneOf;
import static com.clarkparsia.owlapi.OWL.or;
import static com.clarkparsia.owlapi.OWL.propertyAssertion;
import static com.clarkparsia.owlapi.OWL.sameAs;
import static com.clarkparsia.owlapi.OWL.some;
import static com.clarkparsia.owlapi.OWL.subClassOf;
import static com.clarkparsia.owlapi.OWL.transitive;
import static com.clarkparsia.owlapi.OntologyUtils.addAxioms;
import static com.clarkparsia.owlapi.OntologyUtils.getOntologyFromAxioms;
import static com.clarkparsia.owlapi.OntologyUtils.loadOntology;
import static com.clarkparsia.owlapi.OntologyUtils.removeAxioms;
import static com.clarkparsia.owlapi.SWRL.classAtom;
import static com.clarkparsia.owlapi.SWRL.dVariable;
import static com.clarkparsia.owlapi.SWRL.iVariable;
import static com.clarkparsia.owlapi.SWRL.propertyAtom;
import static com.clarkparsia.owlapi.SWRL.rule;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mindswap.pellet.test.PelletTestCase.assertIteratorValues;

import java.net.URI;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import junit.framework.JUnit4TestAdapter;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mindswap.pellet.KnowledgeBase;
import org.mindswap.pellet.PelletOptions;
import org.mindswap.pellet.Role;
import org.mindswap.pellet.exceptions.TimeoutException;
import org.mindswap.pellet.owlapi.AxiomConverter;
import org.mindswap.pellet.owlapi.Reasoner;
import org.mindswap.pellet.test.PelletTestSuite;
import org.mindswap.pellet.utils.ATermUtils;
import org.mindswap.pellet.utils.SetUtils;
import org.mindswap.pellet.utils.Timer;
import org.mindswap.pellet.utils.progress.ConsoleProgressMonitor;
import org.mindswap.pellet.utils.progress.ProgressMonitor;
import org.semanticweb.owl.apibinding.OWLManager;
import org.semanticweb.owl.inference.OWLReasonerException;
import org.semanticweb.owl.io.StringInputSource;
import org.semanticweb.owl.model.OWLAxiom;
import org.semanticweb.owl.model.OWLClass;
import org.semanticweb.owl.model.OWLConstant;
import org.semanticweb.owl.model.OWLDataFactory;
import org.semanticweb.owl.model.OWLDataProperty;
import org.semanticweb.owl.model.OWLDescription;
import org.semanticweb.owl.model.OWLException;
import org.semanticweb.owl.model.OWLIndividual;
import org.semanticweb.owl.model.OWLObjectProperty;
import org.semanticweb.owl.model.OWLOntology;
import org.semanticweb.owl.model.OWLOntologyCreationException;
import org.semanticweb.owl.model.OWLOntologyManager;
import org.semanticweb.owl.model.SWRLAtom;
import org.semanticweb.owlapi.vocab.Namespaces;

import aterm.ATermAppl;

import com.clarkparsia.owlapi.OWL;
import com.clarkparsia.owlapi.OntologyUtils;
import com.clarkparsia.owlapi.SWRL;
import com.clarkparsia.owlapi.XSD;

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
public class OWLAPITests {
	public static String	base	= "file:" + PelletTestSuite.base + "misc/";
	
	private OWLClass				A, B, C, D, E, F;
	private OWLObjectProperty		p, q, r;
	private OWLDataProperty			dp, dq, dr;
	private OWLIndividual			a, b, c;
	private OWLConstant				lit;

	@Before
	public void createEntities() {		
		A = Class( "A" );
		B = Class( "B" );
		C = Class( "C" );
		D = Class( "D" );
		E = Class( "E" );
		F = Class( "F" );

		p = ObjectProperty( "p" );
		q = ObjectProperty( "q" );
		r = ObjectProperty( "r" );

		dp = DataProperty( "dp" );
		dq = DataProperty( "dq" );
		dr = DataProperty( "dr" );

		a = Individual( "a" );
		b = Individual( "b" );
		c = Individual( "c" );
		
		lit = constant( "lit" );
	}
	public static junit.framework.Test suite() {
		return new JUnit4TestAdapter( OWLAPITests.class );
	}

	@Before
	@After
	public void resetOntologyManager() {
		for( OWLOntology o : OWL.manager.getOntologies() ) {
			OWL.manager.removeOntology( o.getURI() );
		}
	}

	@Test
	public void testOWL2() throws OWLException {
		String ns = "http://www.example.org/test#";

		OWLOntology ont = loadOntology( base + "owl2_owlapi2.owl" );

		Reasoner reasoner = new Reasoner( OWL.manager );
		reasoner.setOntology( ont );

		OWLClass C = Class( ns + "C" );
		OWLClass D = Class( ns + "D" );
		OWLClass D1 = Class( ns + "D1" );
		OWLClass D2 = Class( ns + "D2" );
		OWLClass D3 = Class( ns + "D3" );

		OWLClass test1 = Class( ns + "test1" );
		OWLClass test2 = Class( ns + "test2" );
		OWLClass test3 = Class( ns + "test3" );

		OWLClass OlderThan10 = Class( ns + "OlderThan10" );
		OWLClass YoungerThan20 = Class( ns + "YoungerThan20" );
		OWLClass Teenager = Class( ns + "Teenager" );
		OWLClass Teen = Class( ns + "Teen" );

		OWLIndividual ind1 = Individual( ns + "ind1" );
		OWLIndividual ind3 = Individual( ns + "ind3" );
		OWLIndividual ind4 = Individual( ns + "ind4" );
		OWLIndividual ind5 = Individual( ns + "ind5" );
		OWLIndividual ind6 = Individual( ns + "ind6" );

		OWLObjectProperty p = ObjectProperty( ns + "p" );
		OWLObjectProperty r = ObjectProperty( ns + "r" );
		OWLObjectProperty invR = ObjectProperty( ns + "invR" );
		OWLObjectProperty ir = ObjectProperty( ns + "ir" );
		OWLObjectProperty as = ObjectProperty( ns + "as" );
		OWLObjectProperty d1 = ObjectProperty( ns + "d1" );
		OWLObjectProperty d2 = ObjectProperty( ns + "d2" );

		assertTrue( reasoner.isConsistent() );

		assertTrue( reasoner.isReflexive( r ) );
		assertTrue( reasoner.isReflexive( invR ) );
		assertTrue( reasoner.isIrreflexive( ir ) );
		assertTrue( reasoner.isAntiSymmetric( as ) );

		assertTrue( reasoner.isEquivalentClass( D, or( D1, D2, D3 ) ) );
		assertTrue( reasoner.isEquivalentClass( D, test1 ) );
		assertTrue( reasoner.isDisjointWith( D1, D2 ) );
		assertTrue( reasoner.isDisjointWith( D1, D3 ) );
		assertTrue( reasoner.isDisjointWith( D2, D3 ) );

		assertTrue( reasoner.isDisjointWith( d1, d2 ) );
		assertTrue( reasoner.isDisjointWith( d2, d1 ) );
		assertFalse( reasoner.isDisjointWith( p, r ) );

		assertTrue( reasoner.hasObjectPropertyRelationship( ind1, r, ind1 ) );
		assertTrue( reasoner.hasObjectPropertyRelationship( ind1, invR, ind1 ) );
		assertTrue( reasoner.isDifferentFrom( ind1, ind3 ) );
		assertTrue( reasoner.isDifferentFrom( ind1, ind4 ) );
		assertTrue( reasoner.isDifferentFrom( ind5, ind6 ) );
		assertTrue( reasoner.hasObjectPropertyRelationship( ind1, p, ind1 ) );
		assertTrue( reasoner.hasType( ind1, test2 ) );
		assertTrue( reasoner.hasType( ind1, test3 ) );
		assertIteratorValues( SetUtils.union( reasoner.getTypes( ind1, false ) ).iterator(),
				new Object[] { OWL.Thing, C, test2, test3 } );

		assertTrue( reasoner.isSubClassOf( Teenager, OlderThan10 ) );
		assertTrue( reasoner.isSubClassOf( Teenager, YoungerThan20 ) );
		assertTrue( reasoner.isEquivalentClass( Teenager, Teen ) );
		System.out.println( reasoner.getDataProperties() );
		assertTrue( reasoner.getDataProperties().contains( DataProperty( Namespaces.OWL + "topDataProperty") ) );
		assertTrue( reasoner.getDataProperties().contains( DataProperty( Namespaces.OWL + "bottomDataProperty") ) );
		assertTrue( reasoner.getObjectProperties().contains( ObjectProperty( Namespaces.OWL + "topObjectProperty") ) );
		assertTrue( reasoner.getObjectProperties().contains( ObjectProperty( Namespaces.OWL + "bottomObjectProperty") ) );
	}

	@Test
	public void testUncle() throws OWLException {
		String ns = "http://www.example.org/test#";

		OWLOntology ont = loadOntology( base + "uncle.owl" );

		Reasoner reasoner = new Reasoner( OWL.manager );
		reasoner.setOntology( ont );

		OWLIndividual Bob = Individual( ns + "Bob" );
		OWLIndividual Sam = Individual( ns + "Sam" );

		OWLObjectProperty uncleOf = ObjectProperty( ns + "uncleOf" );

		assertPropertyValues( reasoner, Bob, uncleOf, Sam );
	}

	@Test
	public void testSibling() throws OWLException {
		String ns = "http://www.example.org/test#";

		OWLOntology ont = loadOntology( base + "sibling.owl" );

		Reasoner reasoner = new Reasoner( OWL.manager );
		reasoner.setOntology( ont );

		OWLIndividual Bob = Individual( ns + "Bob" );
		OWLIndividual John = Individual( ns + "John" );
		OWLIndividual Jane = Individual( ns + "Jane" );

		OWLObjectProperty hasBrother = ObjectProperty( ns + "hasBrother" );
		OWLObjectProperty hasSister = ObjectProperty( ns + "hasSister" );

		assertPropertyValues( reasoner, Bob, hasBrother, John );
		assertPropertyValues( reasoner, Bob, hasSister, Jane );
	}

	public static void assertPropertyValues(Reasoner reasoner, OWLIndividual subj,
			OWLObjectProperty pred, OWLIndividual... values) {
		assertIteratorValues( reasoner.getRelatedIndividuals( subj, pred ).iterator(), values );

		assertIteratorValues( reasoner.getObjectPropertyRelationships( subj ).get( pred )
				.iterator(), values );
	}

	@Test
	public void testPropertyChain() throws OWLException {
		String ns = "http://www.example.org/test#";

		OWLOntology ont = loadOntology( base + "propertyChain.owl" );

		Reasoner reasoner = new Reasoner( OWL.manager );
		reasoner.setOntology( ont );

		OWLClass C = Class( ns + "C" );
		OWLClass S0 = Class( ns + "S0" );
		OWLClass R0 = Class( ns + "R0" );
		OWLClass R1 = Class( ns + "R1" );
		OWLObjectProperty r = ObjectProperty( ns + "r" );
		OWLObjectProperty s = ObjectProperty( ns + "s" );

		OWLIndividual[] a = new OWLIndividual[17];
		for( int i = 0; i < a.length; i++ )
			a[i] = Individual( ns + "a" + i );

		OWLIndividual[] theList = new OWLIndividual[] {
				a[1], a[2], a[3], a[4], a[5], a[6], a[8], a[10], a[12], a[14], a[16] };

		assertTrue( reasoner.isConsistent() );

		assertTrue( reasoner.isTransitive( r ) );
		assertFalse( reasoner.isTransitive( s ) );

		assertIteratorValues( reasoner.getIndividuals( C, false ).iterator(), theList );

		assertIteratorValues( reasoner.getIndividuals( S0, false ).iterator(), theList );

		assertIteratorValues( reasoner.getIndividuals( R0, false ).iterator(), new OWLIndividual[] {
				a[7], a[9] } );

		assertIteratorValues( reasoner.getIndividuals( R1, false ).iterator(), new OWLIndividual[] {
				a[2], a[3], a[4], a[5], a[6] } );

		assertIteratorValues( reasoner.getRelatedIndividuals( a[0], r ).iterator(),
				new OWLIndividual[] { a[7], a[9] } );

		assertIteratorValues( reasoner.getRelatedIndividuals( a[1], r ).iterator(),
				new OWLIndividual[] { a[2], a[3], a[4], a[5], a[6] } );

		assertIteratorValues( reasoner.getRelatedIndividuals( a[0], s ).iterator(), theList );

	}

	@Test
	public void testQualifiedCardinality1() throws OWLException {
		String ns = "http://www.example.org/test#";

		OWLOntology ont = loadOntology( base + "qcr.owl" );

		Reasoner reasoner = new Reasoner( OWL.manager );
		reasoner.setOntology( ont );

		OWLClass sub = Class( ns + "sub" );
		OWLClass sup = Class( ns + "sup" );

		assertTrue( reasoner.isConsistent() );

		assertTrue( reasoner.isSubClassOf( sub, sup ) );
		assertTrue( reasoner.getDescendantClasses( sup ).contains( SetUtils.singleton( sub ) ) );
		assertTrue( reasoner.getAncestorClasses( sub ).contains( SetUtils.singleton( sup ) ) );
	}

	@Test
	public void testReflexive2() throws OWLException {
		String ns = "http://www.example.org/test#";
		String foaf = "http://xmlns.com/foaf/0.1/";

		OWLOntology ont = loadOntology( base + "reflexive.owl" );

		Reasoner reasoner = new Reasoner( OWL.manager );
		reasoner.setOntology( ont );

		OWLObjectProperty[] knows = {
				ObjectProperty( foaf + "knows" ), ObjectProperty( ns + "knows2" ),
				ObjectProperty( ns + "knows3" ) };

		OWLIndividual[] people = new OWLIndividual[5];
		for( int i = 0; i < people.length; i++ ) {
			people[i] = Individual( ns + "P" + (i + 1) );

			for( int j = 0; j < knows.length; j++ ) {
				assertTrue( people[i] + " " + knows[j], reasoner.hasObjectPropertyRelationship(
						people[i], knows[j], people[i] ) );

				assertIteratorValues( reasoner.getRelatedIndividuals( people[i], knows[j] )
						.iterator(), new OWLIndividual[] { people[i] } );
			}
		}

		Map<OWLIndividual, Set<OWLIndividual>> result = reasoner.getObjectPropertyAssertions( knows[0] );
		for( int i = 0; i < people.length; i++ ) {
			assertEquals("Expected property value not found: " + people[i], Collections.singleton(people[i]), result.remove(people[i]));
		}
		assertTrue("Unexpected property value: " + result, result.isEmpty());
	}

	@Test
	public void testInfiniteChain() throws Exception {
		OWLOntology ont = loadOntology( base + "infiniteChain.owl" );

		Reasoner reasoner = new Reasoner( OWL.manager );
		reasoner.setOntology( ont );

		assertTrue( !reasoner.isConsistent() );
	}

	@Test
	public void testRemoveLiteral() throws Exception {
		String ns = "http://www.example.org/test#";

		OWLOntology ont = loadOntology( base + "RemoveLiteral.owl" );

		Reasoner reasoner = new Reasoner( OWL.manager );
		reasoner.setOntology( ont );

		OWLDataProperty pInt = DataProperty( ns + "pInt" );
		OWLDataProperty pDouble = DataProperty( ns + "pDouble" );
		OWLDataProperty pBoolean = DataProperty( ns + "pBoolean" );

		OWLIndividual ind = Individual( ns + "ind1" );

		OWLConstant valDouble = ind.getDataPropertyValues( ont ).get(pDouble).iterator().next();
		OWLConstant valInt = ind.getDataPropertyValues( ont ).get(pInt).iterator().next();
		OWLConstant valBoolean = ind.getDataPropertyValues( ont ).get( pBoolean ).iterator().next();

		assertTrue( reasoner.isConsistent() );

		removeAxioms(ont, propertyAssertion( ind, pDouble, valDouble ) );
		reasoner.refresh();
		assertTrue( reasoner.getRelatedValues( ind, pDouble ).isEmpty() );

		removeAxioms( ont, propertyAssertion( ind, pInt, valInt ) );
		reasoner.refresh();
		assertTrue( reasoner.getRelatedValues( ind, pInt ).isEmpty() );

		removeAxioms( ont, propertyAssertion( ind, pBoolean, valBoolean ) );
		reasoner.refresh();
		assertTrue( reasoner.getRelatedValues( ind, pBoolean ).isEmpty() );

		assertTrue( reasoner.getDataPropertyRelationships( ind ).isEmpty() );

		OWLConstant newVal = constant( "0.0", XSD.DOUBLE );
		addAxioms( ont, propertyAssertion( ind, pDouble, newVal ) );
		reasoner.refresh();

		assertTrue( reasoner.isConsistent() );
	}

	@Test
	public void testFamily() throws OWLException {
		String ns = "http://www.example.org/family#";

		OWLOntology ont = loadOntology( base + "family.owl" );

		Reasoner reasoner = new Reasoner( OWL.manager );
		reasoner.setOntology( ont );

		OWLObjectProperty hasBrother = ObjectProperty( ns + "hasBrother" );
		OWLObjectProperty hasSon = ObjectProperty( ns + "hasSon" );
		OWLObjectProperty hasFather = ObjectProperty( ns + "hasFather" );
		OWLObjectProperty hasParent = ObjectProperty( ns + "hasParent" );
		OWLObjectProperty hasChild = ObjectProperty( ns + "hasChild" );
		OWLObjectProperty hasMother = ObjectProperty( ns + "hasMother" );
		OWLObjectProperty hasDaughter = ObjectProperty( ns + "hasDaughter" );
		OWLObjectProperty hasAncestor = ObjectProperty( ns + "hasAncestor" );
		OWLObjectProperty likes = ObjectProperty( ns + "likes" );
		OWLObjectProperty isMarriedTo = ObjectProperty( ns + "isMarriedTo" );
		OWLObjectProperty dislikes = ObjectProperty( ns + "dislikes" );
		OWLObjectProperty hasSister = ObjectProperty( ns + "hasSister" );
		OWLObjectProperty hasDescendant = ObjectProperty( ns + "hasDescendant" );
		OWLObjectProperty hasSibling = ObjectProperty( ns + "hasSibling" );
		OWLClass Child = Class( ns + "Child" );
		OWLClass Person = Class( ns + "Person" );
		OWLClass PersonWithAtLeastTwoMaleChildren = Class( ns + "PersonWithAtLeastTwoMaleChildren" );
		OWLClass PersonWithAtLeastTwoFemaleChildren = Class( ns
				+ "PersonWithAtLeastTwoFemaleChildren" );
		OWLClass PersonWithAtLeastTwoChildren = Class( ns + "PersonWithAtLeastTwoChildren" );
		OWLClass PersonWithAtLeastFourChildren = Class( ns + "PersonWithAtLeastFourChildren" );
		OWLClass Teen = Class( ns + "Teen" );
		OWLClass Teenager = Class( ns + "Teenager" );
		OWLClass Male = Class( ns + "Male" );
		OWLClass Adult = Class( ns + "Adult" );
		OWLClass Female = Class( ns + "Female" );
		OWLClass Senior = Class( ns + "Senior" );
		OWLIndividual grandmother = Individual( ns + "grandmother" );
		OWLIndividual grandfather = Individual( ns + "grandfather" );
		OWLIndividual father = Individual( ns + "father" );
		OWLIndividual son = Individual( ns + "son" );
		OWLIndividual mother = Individual( ns + "mother" );
		OWLIndividual daughter = Individual( ns + "daughter" );
		OWLIndividual personX = Individual( ns + "personX" );
		OWLIndividual personY = Individual( ns + "personY" );
		OWLIndividual personZ = Individual( ns + "personZ" );

		assertTrue( reasoner.isConsistent() );

		KnowledgeBase kb = reasoner.getKB();

		for( int test = 0; test < 2; test++ ) {
			if( test != 0 )
				kb.realize();

			assertTrue( reasoner.isTransitive( hasAncestor ) );
			assertFalse( reasoner.isFunctional( hasAncestor ) );

			assertTrue( reasoner.isTransitive( hasDescendant ) );
			assertFalse( reasoner.isFunctional( hasDescendant ) );

			assertTrue( reasoner.isSymmetric( isMarriedTo ) );
			assertTrue( reasoner.isIrreflexive( isMarriedTo ) );

			assertTrue( reasoner.isSubPropertyOf( hasParent, hasAncestor ) );
			assertTrue( reasoner.isSubPropertyOf( hasFather, hasAncestor ) );
			assertTrue( reasoner.isSubPropertyOf( hasMother, hasAncestor ) );
			assertTrue( reasoner.isSubPropertyOf( hasChild, hasDescendant ) );

			assertTrue( reasoner.isDisjointWith( likes, dislikes ) );
			assertTrue( reasoner.isDisjointWith( dislikes, likes ) );
			assertTrue( reasoner.isDisjointWith( hasFather, hasMother ) );
			assertTrue( reasoner.isDisjointWith( hasMother, hasFather ) );

			assertTrue( reasoner.hasType( grandfather, Person ) );
			assertTrue( reasoner.hasType( grandfather, PersonWithAtLeastTwoChildren ) );
			assertTrue( reasoner.hasType( grandfather, PersonWithAtLeastTwoMaleChildren ) );
			assertTrue( reasoner.hasType( grandfather, Male ) );
			assertTrue( reasoner.hasType( grandfather, Senior ) );
			assertTrue( reasoner.hasObjectPropertyRelationship( grandfather, isMarriedTo,
					grandmother ) );
			assertTrue( reasoner.hasObjectPropertyRelationship( grandfather, hasChild, father ) );
			assertTrue( reasoner.hasObjectPropertyRelationship( grandfather, hasSon, father ) );
			assertTrue( reasoner.isDifferentFrom( grandfather, grandmother ) );
			assertTrue( reasoner.isDifferentFrom( grandfather, father ) );
			assertTrue( reasoner.isDifferentFrom( grandfather, mother ) );
			assertTrue( reasoner.isDifferentFrom( grandfather, son ) );
			assertTrue( reasoner.isDifferentFrom( grandfather, daughter ) );

			assertTrue( reasoner.hasType( grandmother, Person ) );
			assertTrue( reasoner.hasType( grandmother, Female ) );
			assertTrue( reasoner.hasType( grandmother, Senior ) );
			assertTrue( reasoner.hasObjectPropertyRelationship( grandmother, isMarriedTo,
					grandfather ) );
			assertTrue( reasoner.hasObjectPropertyRelationship( grandmother, hasChild, father ) );
			assertFalse( reasoner.hasObjectPropertyRelationship( grandmother, hasSon, father ) );

			assertTrue( reasoner.hasType( father, Person ) );
			assertTrue( reasoner.hasType( father, Male ) );
			assertTrue( reasoner.hasType( father, Adult ) );
			assertTrue( reasoner.hasObjectPropertyRelationship( father, hasParent, grandfather ) );
			assertTrue( reasoner.hasObjectPropertyRelationship( father, hasParent, grandmother ) );
			assertTrue( reasoner.hasObjectPropertyRelationship( father, hasFather, grandfather ) );
			assertTrue( reasoner.hasObjectPropertyRelationship( father, hasMother, grandmother ) );
			assertTrue( reasoner.hasObjectPropertyRelationship( father, hasChild, son ) );
			assertTrue( reasoner.hasObjectPropertyRelationship( father, hasSon, son ) );
			assertTrue( reasoner.hasObjectPropertyRelationship( father, hasChild, daughter ) );
			assertFalse( reasoner.hasObjectPropertyRelationship( father, hasDaughter, daughter ) );

			assertTrue( reasoner.hasType( mother, Person ) );
			assertTrue( reasoner.hasType( mother, Female ) );

			assertTrue( reasoner.hasType( son, Male ) );
			assertTrue( reasoner.hasType( son, Teenager ) );
//			assertTrue( reasoner.hasType( son, Teen ) );
			assertTrue( reasoner.hasObjectPropertyRelationship( son, hasParent, father ) );
			assertTrue( reasoner.hasObjectPropertyRelationship( son, hasFather, father ) );
			assertTrue( reasoner.hasObjectPropertyRelationship( son, hasSibling, daughter ) );
			assertTrue( reasoner.hasObjectPropertyRelationship( son, hasSister, daughter ) );

			assertTrue( reasoner.hasType( daughter, Female ) );
			assertTrue( reasoner.hasType( daughter, Child ) );
			assertTrue( reasoner.hasObjectPropertyRelationship( daughter, hasAncestor, grandfather ) );
			assertTrue( reasoner.hasObjectPropertyRelationship( daughter, hasAncestor, grandmother ) );
			assertTrue( reasoner.hasObjectPropertyRelationship( daughter, hasParent, father ) );
			assertTrue( reasoner.hasObjectPropertyRelationship( daughter, hasFather, father ) );
			assertTrue( reasoner.hasObjectPropertyRelationship( daughter, hasParent, mother ) );
			assertTrue( reasoner.hasObjectPropertyRelationship( daughter, hasMother, mother ) );
			assertTrue( reasoner.hasObjectPropertyRelationship( daughter, hasSibling, son ) );
			assertFalse( reasoner.hasObjectPropertyRelationship( daughter, hasBrother, son ) );

			assertTrue( reasoner.isDifferentFrom( personX, personY ) );
			assertTrue( reasoner.isDifferentFrom( personX, personZ ) );
			assertTrue( reasoner.isDifferentFrom( personY, personZ ) );

//			assertTrue( reasoner.isEquivalentClass( Teen, Teenager ) );
			assertTrue( reasoner.isSubClassOf( Senior, Adult ) );

			assertTrue( reasoner.isSubClassOf( PersonWithAtLeastTwoMaleChildren, Person ) );
			assertTrue( reasoner.isSubClassOf( PersonWithAtLeastTwoFemaleChildren, Person ) );
			assertTrue( reasoner.isSubClassOf( PersonWithAtLeastTwoChildren, Person ) );
			assertTrue( reasoner.isSubClassOf( PersonWithAtLeastFourChildren, Person ) );

			assertTrue( reasoner.isSubClassOf( PersonWithAtLeastFourChildren,
					PersonWithAtLeastTwoChildren ) );
			assertTrue( reasoner.isSubClassOf( PersonWithAtLeastTwoMaleChildren,
					PersonWithAtLeastTwoChildren ) );
			assertTrue( reasoner.isSubClassOf( PersonWithAtLeastTwoFemaleChildren,
					PersonWithAtLeastTwoChildren ) );

			assertFalse( reasoner.isSubClassOf( PersonWithAtLeastTwoFemaleChildren,
					PersonWithAtLeastTwoMaleChildren ) );
			assertFalse( reasoner.isSubClassOf( PersonWithAtLeastTwoMaleChildren,
					PersonWithAtLeastTwoFemaleChildren ) );
		}

		// kb.timers.print();
	}

	/**
	 * Verifies that OWL 2 entity declarations are parsed from RDF/XML and
	 * handled correctly.
	 */
	@Test
	public void entityDeclarations() {
		String ns = "http://www.example.org/test#";

		OWLOntology ont = loadOntology( base + "/entityDeclarations.owl" );

		Reasoner reasoner = new Reasoner( OWL.manager );
		reasoner.loadOntologies( Collections.singleton( ont ) );
		assertTrue( reasoner.isConsistent() );

		KnowledgeBase kb = reasoner.getKB();

		assertTrue( reasoner.isDefined( Individual( ns + "a" ) ) );
		assertEquals( 1, reasoner.getIndividuals().size() );

		assertTrue( reasoner.isDefined( Class( ns + "C" ) ) );
		assertEquals( 1, reasoner.getClasses().size() );
		// FIXME: OWLAPI should support Datatype definition checking
		assertFalse( kb.isDatatype( ATermUtils.makeTermAppl( ns + "C" ) ) );

		assertFalse( reasoner.isDefined( Class( ns + "D" ) ) );
		// FIXME: OWLAPI should support Datatype definition checking
		// FIXME: OWLAPI loader does not parse Datatype declarations
		//assertTrue( kb.isDatatype( ATermUtils.makeTermAppl( ns + "D" ) ) );

		/* FIXME: There is no positive check here because OWLAPI does not support annotation property declaration. */
		assertFalse( reasoner.isDefined( DataProperty( ns + "p" ) ) );
		assertFalse( reasoner.isDefined( ObjectProperty( ns + "p" ) ) );
		
		assertTrue( reasoner.isDefined( ObjectProperty( ns + "q" ) ) );
		assertEquals( 2 + 1, reasoner.getObjectProperties().size() );
		assertFalse( kb.isAnnotationProperty( ATermUtils.makeTermAppl( ns + "r" ) ) );
		assertFalse( reasoner.isDefined( DataProperty( ns + "q" ) ) );

		assertTrue( reasoner.isDefined( DataProperty( ns + "r" ) ) );
		assertEquals( 2 + 1, reasoner.getDataProperties().size() );
		assertFalse( kb.isAnnotationProperty( ATermUtils.makeTermAppl( ns + "r" ) ) );
		assertFalse( reasoner.isDefined( ObjectProperty( ns + "r" ) ) );
	}

	@Test
	public void testAnonInverse() throws OWLException {
		String ns = "http://www.example.org/test#";

		OWLOntology ont = loadOntology( base + "anon_inverse.owl" );

		OWLClass C = Class( ns + "C" );
		OWLClass D = Class( ns + "D" );
		OWLObjectProperty r = ObjectProperty( ns + "r" );
		OWLDescription desc = some( inverse( r ), D );

		Reasoner reasoner = new Reasoner( OWL.manager );

		reasoner.loadOntologies( Collections.singleton( ont ) );

		assertEquals( Collections.singleton( Collections.singleton( C ) ), reasoner
				.getSubClasses( desc ) );

		assertTrue( reasoner.isInverseFunctional( ObjectProperty( ns + "functionalP" ) ) );

		assertTrue( reasoner.isFunctional( ObjectProperty( ns + "inverseFunctionalP" ) ) );

		assertTrue( reasoner.isTransitive( ObjectProperty( ns + "transitiveP" ) ) );

		assertTrue( reasoner.isSymmetric( ObjectProperty( ns + "symmetricP" ) ) );

		assertTrue( reasoner.isReflexive( ObjectProperty( ns + "reflexiveP" ) ) );

		assertTrue( reasoner.isIrreflexive( ObjectProperty( ns + "irreflexiveP" ) ) );

		assertTrue( reasoner.isAntiSymmetric( ObjectProperty( ns + "asymmetricP" ) ) );

		OWLObjectProperty p1 = ObjectProperty( ns + "p1" );
		OWLObjectProperty p2 = ObjectProperty( ns + "p2" );
		OWLObjectProperty p3 = ObjectProperty( ns + "p3" );
		assertTrue( reasoner.isEquivalentProperty( p1, p2 ) );
		assertTrue( reasoner.isEquivalentProperty( p1, p3 ) );
		assertTrue( reasoner.isEquivalentProperty( p2, p3 ) );
	}

	@Test
	public void testDLSafeRules() throws OWLOntologyCreationException, OWLReasonerException {
		String ns = "http://owldl.com/ontologies/dl-safe.owl#";

		OWLOntology ont = loadOntology( base + "dl-safe.owl" );

		Reasoner reasoner = new Reasoner( OWL.manager );
		reasoner.setOntology( ont );

		// OWLObjectProperty father = factory.getOWLObjectProperty( URI.create(
		// ns + "father" ) );
		OWLObjectProperty hates = ObjectProperty( ns + "hates" );
		OWLObjectProperty sibling = ObjectProperty( ns + "sibling" );

		OWLClass BadChild = Class( ns + "BadChild" );
		OWLClass Child = Class( ns + "Child" );
		// OWLClass GoodChild = Class( ns +
		// "GoodChild" );
		OWLClass Grandchild = Class( ns + "Grandchild" );
		OWLClass Person = Class( ns + "Person" );

		OWLIndividual Abel = Individual( ns + "Abel" );
		OWLIndividual Cain = Individual( ns + "Cain" );
		OWLIndividual Oedipus = Individual( ns + "Oedipus" );
		OWLIndividual Remus = Individual( ns + "Remus" );
		OWLIndividual Romulus = Individual( ns + "Romulus" );

		for( int test = 0; test < 2; test++ ) {
			if( test != 0 )
				reasoner.realise();

			assertTrue( reasoner.hasObjectPropertyRelationship( Abel, sibling, Cain ) );

			assertIteratorValues( reasoner.getRelatedIndividuals( Abel, sibling ).iterator(),
					new Object[] { Cain } );

			assertTrue( reasoner.hasObjectPropertyRelationship( Cain, sibling, Abel ) );

			assertIteratorValues( reasoner.getRelatedIndividuals( Cain, sibling ).iterator(),
					new Object[] { Abel } );

			assertTrue( reasoner.hasObjectPropertyRelationship( Cain, hates, Abel ) );

			assertFalse( reasoner.hasObjectPropertyRelationship( Abel, hates, Cain ) );

			assertTrue( reasoner.hasType( Cain, Grandchild ) );

			assertTrue( reasoner.hasType( Cain, BadChild ) );

			assertFalse( reasoner.hasObjectPropertyRelationship( Romulus, sibling, Remus ) );

			assertTrue( reasoner.hasType( Romulus, Grandchild ) );

			assertFalse( reasoner.hasType( Romulus, BadChild ) );

			assertTrue( reasoner.hasType( Oedipus, Child ) );
		}

		assertIteratorValues( SetUtils.union( reasoner.getTypes( Cain, true ) ).iterator(),
				new Object[] { BadChild, Child, Person } );
	}
	

	@Test
	public void testDLSafeConstants() throws OWLReasonerException, OWLOntologyCreationException {
		String ns = "http://owldl.com/ontologies/dl-safe-constants.owl#";

		OWLOntology ont = loadOntology( base + "dl-safe-constants.owl" );

		Reasoner reasoner = new Reasoner( OWL.manager );
		reasoner.setOntology( ont );

		OWLClass DreamTeamMember = Class( ns + "DreamTeamMember" );
		OWLClass DreamTeamMember1 = Class( ns + "DreamTeamMember1" );
		OWLClass DreamTeamMember2 = Class( ns + "DreamTeamMember2" );

		OWLIndividual Alice = Individual( ns + "Alice" );
		OWLIndividual Bob = Individual( ns + "Bob" );
		OWLIndividual Charlie = Individual( ns + "Charlie" );

		for( int test = 0; test < 1; test++ ) {
			if( test != 0 )
				reasoner.realise();

			assertIteratorValues( reasoner.getIndividuals( DreamTeamMember, false ).iterator(),
					new Object[] { Alice, Bob, Charlie } );

			assertIteratorValues( reasoner.getIndividuals( DreamTeamMember1, false ).iterator(),
					new Object[] { Alice, Bob, Charlie } );

			assertIteratorValues( reasoner.getIndividuals( DreamTeamMember2, false ).iterator(),
					new Object[] { Alice, Bob, Charlie } );
		}
	}

	@Test
	public void testInvalidTransitivity() throws Exception {
		String ns = "http://www.example.org/test#";

		OWLClass C = Class( ns + "C" );

		OWLObjectProperty p1 = ObjectProperty( ns + "p1" );
		OWLObjectProperty p2 = ObjectProperty( ns + "p2" );

		OWLIndividual x = Individual( ns + "x" );
		OWLIndividual y = Individual( ns + "y" );
		OWLIndividual z = Individual( ns + "z" );

		OWLOntology ont = getOntologyFromAxioms( transitive( p1 ),
				classAssertion( x, all( p1, C ) ), propertyAssertion( x, p1, y ),
				propertyAssertion( y, p1, z ) );

		Reasoner reasoner = new Reasoner( OWL.manager );
		reasoner.setOntology( ont );

		assertTrue( reasoner.hasType( y, C ) );
		assertTrue( reasoner.hasType( z, C ) );

		OWLAxiom[] axioms = new OWLAxiom[] {
				functional( p1 ), inverseFunctional( p1 ), irreflexive( p1 ), asymmetric( p1 ),
				disjointProperties( p1, p2 ), subClassOf( C, min( p1, 2 ) ),
				classAssertion( x, max( p1, 3 ) ), disjointClasses( C, min( p1, 2 ) ) };

		for( int i = 0; i < axioms.length; i++ ) {
			addAxioms( ont, axioms[i] );

			reasoner = new Reasoner( OWL.manager );
			reasoner.setOntology( ont );
			assertTrue( axioms[i].toString(), reasoner.isEntailed( classAssertion( y, C ) ) );
			assertFalse( axioms[i].toString(), reasoner.isEntailed( classAssertion( z, C ) ) );

			removeAxioms( ont, axioms[i] );
		}
	}

	@Test
	public void testInvalidTransitivity2() throws OWLOntologyCreationException {
		OWLOntology ont = loadOntology( base + "invalidTransitivity.owl" );

		Reasoner reasoner = new Reasoner( OWL.manager );
		reasoner.setOntology( ont );

		KnowledgeBase kb = reasoner.getKB();
		kb.prepare();

		for( Role r : kb.getRBox().getRoles() ) {
			if( !ATermUtils.isBuiltinProperty( r.getName() ) ) {
				assertTrue( r.toString(), r.isSimple() );
				assertFalse( r.toString(), r.isTransitive() );
			}
		}

		for( ATermAppl p : kb.getObjectProperties() ) {
			if ( !ATermUtils.isBuiltinProperty( p ) ) {
				assertFalse( p.toString(), kb.isTransitiveProperty( p ) );
			}
		}
	}

	@Test
	public void testSameAs1() throws OWLException {
		String ns = "urn:test:";

		URI ontURI = URI.create( base + "invalidTransitivity.owl" );

		OWLIndividual a = Individual( ns + "a" );
		OWLIndividual b = Individual( ns + "b" );
		OWLIndividual c = Individual( ns + "c" );

		OWLObjectProperty p = ObjectProperty( ns + "p" );
		OWLObjectProperty q = ObjectProperty( ns + "q" );

		Set<OWLAxiom> axioms = new HashSet<OWLAxiom>();

		axioms.add( propertyAssertion( a, p, c ) );

		axioms.add( propertyAssertion( b, p, b ) );

		axioms.add( propertyAssertion( c, q, a ) );

		axioms.add( sameAs( b, c ) );

		axioms.add( propertyAssertion( a, q, c ) );

		OWLOntology ont = getOntologyFromAxioms( axioms, ontURI );

		Reasoner reasoner = new Reasoner( OWL.manager );

		reasoner.loadOntology( ont );

		assertEquals( reasoner.getSameAsIndividuals( a ), Collections.emptySet() );
		assertEquals( reasoner.getSameAsIndividuals( b ), Collections.singleton( c ) );
		assertEquals( reasoner.getSameAsIndividuals( c ), Collections.singleton( b ) );

		assertEquals( reasoner.getObjectPropertyRelationships( a ).get( p ), SetUtils.create( b, c ) );

		assertEquals( reasoner.getObjectPropertyRelationships( a ).get( q ), SetUtils.create( b, c ) );

		assertEquals( reasoner.getObjectPropertyRelationships( b ).get( p ), SetUtils.create( b, c ) );

		assertEquals( reasoner.getObjectPropertyRelationships( b ).get( q ), SetUtils.create( a ) );

		assertEquals( reasoner.getObjectPropertyRelationships( c ).get( p ), SetUtils.create( b, c ) );

		assertEquals( reasoner.getObjectPropertyRelationships( c ).get( q ), SetUtils.create( a ) );

	}

	@Test
	public void testSameAs3() throws OWLException {
		String ns = "urn:test:";

		URI ontURI = URI.create( base + "test.owl" );

		OWLIndividual i1 = Individual( ns + "i1" );
		OWLIndividual i2 = Individual( ns + "i2" );
		OWLIndividual i3 = Individual( ns + "i3" );

		OWLClass c = Class( ns + "c" );

		Set<OWLAxiom> axioms = new HashSet<OWLAxiom>();

		axioms.add( equivalentClasses( c, oneOf( i1, i2 ) ) );

		axioms.add( classAssertion( i3, c ) );

		OWLOntology ont = getOntologyFromAxioms( axioms, ontURI );

		Reasoner reasoner = new Reasoner( OWL.manager );
		reasoner.loadOntology( ont );

		assertTrue( !reasoner.isSameAs( i1, i2 ) );
		assertTrue( !reasoner.isSameAs( i1, i3 ) );
		assertEquals( reasoner.getSameAsIndividuals( i1 ), Collections.emptySet() );

		assertTrue( !reasoner.isSameAs( i2, i1 ) );
		assertTrue( !reasoner.isSameAs( i2, i3 ) );
		assertEquals( reasoner.getSameAsIndividuals( i2 ), Collections.emptySet() );

		assertTrue( !reasoner.isSameAs( i3, i1 ) );
		assertTrue( !reasoner.isSameAs( i3, i2 ) );
		assertEquals( reasoner.getSameAsIndividuals( i2 ), Collections.emptySet() );

	}

	public static class TimedProgressMonitor extends ConsoleProgressMonitor {
		private final int	limit;	// in milliseconds

		public TimedProgressMonitor(int limit) {
			this.limit = limit;
		}

		@Override
		public void taskFinished() {
			super.taskFinished();
		}

		@Override
		public void taskStarted() {
			super.taskStarted();
		}

		@Override
		public boolean isCanceled() {
			long elapsedTime = timer.getElapsed();
			return elapsedTime > limit;
		}
	}

	@Test
	// This tests ticket 148
	// Canceling realization with REALIZE_BY_INDIVIDUAL=false throws an NPE
	public void testRealizeByIndividualsNPE() throws Exception {
		PelletOptions.REALIZE_INDIVIDUAL_AT_A_TIME = false;

		ProgressMonitor monitor = new TimedProgressMonitor( 1 );

		Reasoner pellet = new Reasoner( OWL.manager );
		KnowledgeBase kb = pellet.getKB();

		OWLOntology ont = loadOntology( base + "food.owl" );
		pellet.loadOntology( ont );

		kb.classify();

		kb.getTaxonomyBuilder().setProgressMonitor( monitor );

		kb.realize();

		assertFalse( kb.isRealized() );
	}

	@Test
	// This tests ticket 147
	// Not having timeout functionality in classification and realization makes
	// it harder to interrupt these processes
	public void testClassificationTimeout() throws Exception {
		boolean timeout = false;

		Reasoner pellet = new Reasoner( OWL.manager );
		KnowledgeBase kb = pellet.getKB();

		Timer timer = kb.timers.createTimer( "classify" );
		timer.setTimeout( 1 );

		OWLOntology ont = loadOntology( base + "food.owl" );
		pellet.loadOntology( ont );

		try {
			kb.classify();
		} catch( TimeoutException e ) {
			timeout = true;
		}

		assertTrue( timeout );
		assertFalse( kb.isClassified() );
	}

	@Test
	// This tests ticket 147
	// Not having timeout functionality in classification and realization makes
	// it harder to interrupt these processes
	public void testRealizationTimeout() throws Exception {
		boolean timeout = false;
		Reasoner pellet = new Reasoner( OWL.manager );
		KnowledgeBase kb = pellet.getKB();

		Timer timer = kb.timers.createTimer( "realize" );
		timer.setTimeout( 1 );

		OWLOntology ont = loadOntology( base + "food.owl" );
		pellet.loadOntology( ont );

		try {
			kb.classify();
		} catch( TimeoutException e ) {
			timeout = true;
		}

		assertFalse( timeout );
		assertTrue( kb.isClassified() );

		try {
			kb.realize();
		} catch( TimeoutException e ) {
			timeout = true;
		}

		assertTrue( timeout );
		assertFalse( kb.isRealized() );
	}

	@Test
	public void testAxiomConverterRules1() {
		KnowledgeBase kb = new KnowledgeBase();
		AxiomConverter converter = new AxiomConverter( kb, OWL.manager.getOWLDataFactory() );

		ATermAppl C = ATermUtils.makeTermAppl( "C" );
		ATermAppl D = ATermUtils.makeTermAppl( "D" );
		ATermAppl x = ATermUtils.makeVar( "x" );

		kb.addClass( C );
		kb.addClass( D );

		ATermAppl[] head = new ATermAppl[] { ATermUtils.makeTypeAtom( x, D ) };
		ATermAppl[] body = new ATermAppl[] { ATermUtils.makeTypeAtom( x, C ) };

		ATermAppl rule = ATermUtils.makeRule( head, body );

		OWLAxiom actual = converter.convert( rule );

		Set<SWRLAtom<?>> antecedent = new HashSet<SWRLAtom<?>>();
		Set<SWRLAtom<?>> consequent = new HashSet<SWRLAtom<?>>();

		antecedent.add( classAtom( Class( "C" ), iVariable( "x" ) ) );
		consequent.add( classAtom( Class( "D" ), iVariable( "x" ) ) );

		OWLAxiom expected = rule( antecedent, consequent );

		assertEquals( expected, actual );
	}


	@Test
	public void testAxiomConverterRules1b() {
		KnowledgeBase kb = new KnowledgeBase();
		AxiomConverter converter = new AxiomConverter( kb, OWL.manager.getOWLDataFactory() );

		ATermAppl C = ATermUtils.makeTermAppl( "C" );
		ATermAppl D = ATermUtils.makeTermAppl( "D" );
		ATermAppl x = ATermUtils.makeVar( "x" );
		ATermAppl name = ATermUtils.makeTermAppl( "MyRule" );

		kb.addClass( C );
		kb.addClass( D );

		ATermAppl[] head = new ATermAppl[] { ATermUtils.makeTypeAtom( x, D ) };
		ATermAppl[] body = new ATermAppl[] { ATermUtils.makeTypeAtom( x, C ) };

		ATermAppl rule = ATermUtils.makeRule( name, head, body );

		OWLAxiom actual = converter.convert( rule );

		Set<SWRLAtom<?>> antecedent = new HashSet<SWRLAtom<?>>();
		Set<SWRLAtom<?>> consequent = new HashSet<SWRLAtom<?>>();

		antecedent.add( classAtom( Class( "C" ), iVariable( "x" ) ) );
		consequent.add( classAtom( Class( "D" ), iVariable( "x" ) ) );

		OWLAxiom expected = rule( name.getName(), antecedent, consequent );

		assertEquals( expected, actual );
	}
	
	public void testAxiomConverterRules1c() {
		KnowledgeBase kb = new KnowledgeBase();
		AxiomConverter converter = new AxiomConverter( kb, OWL.manager.getOWLDataFactory() );

		ATermAppl C = ATermUtils.makeTermAppl( "C" );
		ATermAppl D = ATermUtils.makeTermAppl( "D" );
		ATermAppl x = ATermUtils.makeVar( "x" );
		ATermAppl name = ATermUtils.makeBnode( "MyRule" );

		kb.addClass( C );
		kb.addClass( D );

		ATermAppl[] head = new ATermAppl[] { ATermUtils.makeTypeAtom( x, D ) };
		ATermAppl[] body = new ATermAppl[] { ATermUtils.makeTypeAtom( x, C ) };

		ATermAppl rule = ATermUtils.makeRule( name, head, body );

		OWLAxiom actual = converter.convert( rule );

		Set<SWRLAtom<?>> antecedent = new HashSet<SWRLAtom<?>>();
		Set<SWRLAtom<?>> consequent = new HashSet<SWRLAtom<?>>();

		antecedent.add( classAtom( Class( "C" ), iVariable( "x" ) ) );
		consequent.add( classAtom( Class( "D" ), iVariable( "x" ) ) );

		OWLAxiom expected = rule( name.getArgument( 0 ).toString(), true, antecedent, consequent );

		assertEquals( expected, actual );
	}
	
	@Test
	public void testAxiomConverterRules2() {
		KnowledgeBase kb = new KnowledgeBase();
		AxiomConverter converter = new AxiomConverter( kb, OWL.manager.getOWLDataFactory() );

		ATermAppl C = ATermUtils.makeTermAppl( "C" );
		ATermAppl D = ATermUtils.makeTermAppl( "D" );
		ATermAppl i = ATermUtils.makeTermAppl( "i" );

		kb.addClass( C );
		kb.addClass( D );
		kb.addIndividual( i );

		ATermAppl[] head = new ATermAppl[] { ATermUtils.makeTypeAtom( i, D ) };
		ATermAppl[] body = new ATermAppl[] { ATermUtils.makeTypeAtom( i, C ) };

		ATermAppl rule = ATermUtils.makeRule( head, body );

		OWLAxiom actual = converter.convert( rule );

		Set<SWRLAtom<?>> antecedent = new HashSet<SWRLAtom<?>>();
		Set<SWRLAtom<?>> consequent = new HashSet<SWRLAtom<?>>();

		antecedent.add( classAtom( Class( "C" ), SWRL.individual( OWL.Individual( "i" ) ) ) );
		consequent.add( classAtom( Class( "D" ), SWRL.individual( OWL.Individual( "i" ) ) ) );

		OWLAxiom expected = rule( antecedent, consequent );

		assertEquals( expected, actual );
	}

	@Test
	public void testAxiomConverterRules3() {
		KnowledgeBase kb = new KnowledgeBase();
		OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
		OWLDataFactory df = manager.getOWLDataFactory();
		AxiomConverter converter = new AxiomConverter( kb, df );

		ATermAppl p = ATermUtils.makeTermAppl( "p" );
		ATermAppl q = ATermUtils.makeTermAppl( "q" );
		ATermAppl x = ATermUtils.makeVar( "x" );
		ATermAppl y = ATermUtils.makeVar( "y" );

		kb.addObjectProperty( p );
		kb.addObjectProperty( q );

		ATermAppl[] head = new ATermAppl[] { ATermUtils.makePropAtom( q, x, y ) };
		ATermAppl[] body = new ATermAppl[] { ATermUtils.makePropAtom( p, x, y ) };

		ATermAppl rule = ATermUtils.makeRule( head, body );

		OWLAxiom actual = converter.convert( rule );

		Set<SWRLAtom<?>> antecedent = new HashSet<SWRLAtom<?>>();
		Set<SWRLAtom<?>> consequent = new HashSet<SWRLAtom<?>>();

		antecedent.add( propertyAtom( ObjectProperty( "p" ), iVariable( "x" ), iVariable( "y" ) ) );
		consequent.add( propertyAtom( ObjectProperty( "q" ), iVariable( "x" ), iVariable( "y" ) ) );

		OWLAxiom expected = rule( antecedent, consequent );

		assertEquals( expected, actual );
	}

	@Test
	public void testAxiomConverterRules4() {
		KnowledgeBase kb = new KnowledgeBase();
		AxiomConverter converter = new AxiomConverter( kb, OWL.manager.getOWLDataFactory() );

		ATermAppl r = ATermUtils.makeTermAppl( "r" );
		ATermAppl s = ATermUtils.makeTermAppl( "s" );
		ATermAppl x = ATermUtils.makeVar( "x" );
		ATermAppl y = ATermUtils.makeVar( "y" );

		kb.addDatatypeProperty( r );
		kb.addDatatypeProperty( s );

		ATermAppl[] head = new ATermAppl[] { ATermUtils.makePropAtom( r, x, y ) };
		ATermAppl[] body = new ATermAppl[] { ATermUtils.makePropAtom( s, x, y ) };

		ATermAppl rule = ATermUtils.makeRule( head, body );

		OWLAxiom actual = converter.convert( rule );

		Set<SWRLAtom<?>> antecedent = new HashSet<SWRLAtom<?>>();
		Set<SWRLAtom<?>> consequent = new HashSet<SWRLAtom<?>>();

		antecedent.add( propertyAtom( DataProperty( "s" ), iVariable( "x" ), dVariable( "y" ) ) );
		consequent.add( propertyAtom( DataProperty( "r" ), iVariable( "x" ), dVariable( "y" ) ) );

		OWLAxiom expected = rule( antecedent, consequent );

		assertEquals( expected, actual );
	}

	@Test
	public void typeInhertianceWithAnonIndividual() throws OWLOntologyCreationException {
		String ns = "foo://test#";
		String src = "@prefix ex: <" + ns + "> .\r\n"
				+ "@prefix rdfs:  <http://www.w3.org/2000/01/rdf-schema#> .\r\n"
				+ "@prefix owl: <http://www.w3.org/2002/07/owl#> .\r\n" + "\r\n"
				+ "ex:C rdfs:subClassOf ex:D .\r\n" + "[] a ex:C .";

		Reasoner pellet = new Reasoner( OWL.manager );

		OWLOntology ont = OWL.manager.loadOntology( new StringInputSource( src ) );
		pellet.loadOntology( ont );

		OWLClass D = OWL.Class( ns + "D" );

		assertTrue( pellet.getIndividuals( D, true ).size() == 0 );

		assertTrue( pellet.getIndividuals( D, false ).size() == 1 );
	}
	
	@Test
	public void testTopBottomPropertyAssertion() throws OWLOntologyCreationException {
		Reasoner pellet = new Reasoner( OWL.manager );

		OWLAxiom[] axioms = {
				OWL.propertyAssertion( a, OWL.topObjectProperty, b ),
				OWL.propertyAssertion( a, OWL.topDataProperty, lit ),
				OWL.propertyAssertion( a, OWL.bottomObjectProperty, b ),
				OWL.propertyAssertion( a, OWL.bottomDataProperty, lit ) };
		for( int i = 0; i < axioms.length; i++ ) {
			OWLOntology ont = OntologyUtils.getOntologyFromAxioms( axioms[i] );
			pellet.setOntology( ont );
			assertEquals( i < 2, pellet.isConsistent() );
		}
	}
	
	@Test
	public void testTopBottomPropertyInferences() throws OWLOntologyCreationException {
		boolean prevValue = PelletOptions.HIDE_TOP_PROPERTY_VALUES; 
		PelletOptions.HIDE_TOP_PROPERTY_VALUES = false;
		try {
			
		OWLAxiom[] axioms = {
				OWL.propertyAssertion( a, p, b ),
				OWL.classAssertion( c, C ),
				OWL.propertyAssertion( a, dp, lit )
		};
		
		Reasoner pellet = new Reasoner( OWL.manager );
		OWLOntology ont = OntologyUtils.getOntologyFromAxioms( axioms );
		pellet.setOntology( ont );

		
		assertTrue( pellet.isSubPropertyOf( p, OWL.topObjectProperty ) );
		assertTrue( pellet.isSubPropertyOf( OWL.bottomObjectProperty, p ) );
		assertTrue( pellet.isSubPropertyOf( dp, OWL.topDataProperty ) );
		assertTrue( pellet.isSubPropertyOf( OWL.bottomDataProperty, dp ) );
		
		assertEquals( Collections.singleton( Collections.singleton( p ) ), pellet.getSubProperties( OWL.topObjectProperty ) );
		assertEquals( Collections.singleton( Collections.singleton( OWL.bottomObjectProperty ) ), pellet.getSubProperties( p ) );
		assertEquals( Collections.singleton( Collections.singleton( dp ) ), pellet.getSubProperties( OWL.topDataProperty ) );
		assertEquals( Collections.singleton( Collections.singleton( OWL.bottomDataProperty ) ), pellet.getSubProperties( dp ) );
		
		assertTrue( pellet.hasObjectPropertyRelationship( a, p, b ) );
		assertFalse( pellet.hasObjectPropertyRelationship( b, p, a ) );
		assertTrue( pellet.hasObjectPropertyRelationship( a, OWL.topObjectProperty, b ) );
		assertTrue( pellet.hasObjectPropertyRelationship( b, OWL.topObjectProperty, a ) );
		
		assertTrue( pellet.hasDataPropertyRelationship( a, dp, lit ) );
		assertFalse( pellet.hasDataPropertyRelationship( b, dp, lit ) );
		assertTrue( pellet.hasDataPropertyRelationship( a, OWL.topDataProperty, lit ) );
		assertTrue( pellet.hasDataPropertyRelationship( b, OWL.topDataProperty, lit ) );
		
		assertEquals( SetUtils.create( b ), pellet.getObjectPropertyRelationships( a ).get( p ) );
		assertFalse( pellet.getObjectPropertyRelationships( b ).containsKey( p ) );
		assertEquals( SetUtils.create( a, b, c ), pellet.getObjectPropertyRelationships( a ).get( OWL.topObjectProperty ) );
		assertEquals( SetUtils.create( a, b, c ), pellet.getObjectPropertyRelationships( b ).get( OWL.topObjectProperty ) );
		assertEquals( SetUtils.create( lit ), pellet.getDataPropertyRelationships( a ).get( OWL.topDataProperty ) );
		assertEquals( SetUtils.create( lit ), pellet.getDataPropertyRelationships( b ).get( OWL.topDataProperty ) );
		assertFalse( pellet.getDataPropertyRelationships( a ).containsKey( OWL.bottomObjectProperty ) );
		assertFalse( pellet.getDataPropertyRelationships( a ).containsKey( OWL.bottomDataProperty ) );
		
		assertEquals( SetUtils.create( a, b, c ), pellet.getObjectPropertyAssertions( OWL.topObjectProperty ).get( a ) );
		assertTrue( pellet.getObjectPropertyAssertions( OWL.bottomObjectProperty ).isEmpty() );
		assertEquals( SetUtils.create( lit ), pellet.getDataPropertyAssertions( OWL.topDataProperty ).get( c ) );
		assertTrue( pellet.getDataPropertyAssertions( OWL.bottomDataProperty ).isEmpty() );
		
		}
		finally { 
			PelletOptions.HIDE_TOP_PROPERTY_VALUES = prevValue;
		}
	}
}
