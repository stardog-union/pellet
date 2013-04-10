// Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public
// License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of
// proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package com.clarkparsia.pellet.test.owlapi;

import static com.clarkparsia.owlapiv3.OWL.Class;
import static com.clarkparsia.owlapiv3.OWL.DataProperty;
import static com.clarkparsia.owlapiv3.OWL.Individual;
import static com.clarkparsia.owlapiv3.OWL.ObjectProperty;
import static com.clarkparsia.owlapiv3.OWL.all;
import static com.clarkparsia.owlapiv3.OWL.asymmetric;
import static com.clarkparsia.owlapiv3.OWL.classAssertion;
import static com.clarkparsia.owlapiv3.OWL.constant;
import static com.clarkparsia.owlapiv3.OWL.differentFrom;
import static com.clarkparsia.owlapiv3.OWL.disjointClasses;
import static com.clarkparsia.owlapiv3.OWL.disjointProperties;
import static com.clarkparsia.owlapiv3.OWL.equivalentClasses;
import static com.clarkparsia.owlapiv3.OWL.equivalentProperties;
import static com.clarkparsia.owlapiv3.OWL.functional;
import static com.clarkparsia.owlapiv3.OWL.inverse;
import static com.clarkparsia.owlapiv3.OWL.inverseFunctional;
import static com.clarkparsia.owlapiv3.OWL.irreflexive;
import static com.clarkparsia.owlapiv3.OWL.max;
import static com.clarkparsia.owlapiv3.OWL.min;
import static com.clarkparsia.owlapiv3.OWL.oneOf;
import static com.clarkparsia.owlapiv3.OWL.or;
import static com.clarkparsia.owlapiv3.OWL.propertyAssertion;
import static com.clarkparsia.owlapiv3.OWL.reflexive;
import static com.clarkparsia.owlapiv3.OWL.sameAs;
import static com.clarkparsia.owlapiv3.OWL.some;
import static com.clarkparsia.owlapiv3.OWL.subClassOf;
import static com.clarkparsia.owlapiv3.OWL.subPropertyOf;
import static com.clarkparsia.owlapiv3.OWL.symmetric;
import static com.clarkparsia.owlapiv3.OWL.transitive;
import static com.clarkparsia.owlapiv3.OntologyUtils.addAxioms;
import static com.clarkparsia.owlapiv3.OntologyUtils.loadOntology;
import static com.clarkparsia.owlapiv3.OntologyUtils.removeAxioms;
import static com.clarkparsia.owlapiv3.SWRL.classAtom;
import static com.clarkparsia.owlapiv3.SWRL.propertyAtom;
import static com.clarkparsia.owlapiv3.SWRL.rule;
import static com.clarkparsia.owlapiv3.SWRL.variable;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mindswap.pellet.test.PelletTestCase.assertIteratorValues;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

import junit.framework.JUnit4TestAdapter;

import org.junit.Ignore;
import org.junit.Test;
import org.mindswap.pellet.KnowledgeBase;
import org.mindswap.pellet.PelletOptions;
import org.mindswap.pellet.Role;
import org.mindswap.pellet.exceptions.TimeoutException;
import org.mindswap.pellet.test.MiscTests;
import org.mindswap.pellet.test.PelletTestSuite;
import org.mindswap.pellet.utils.ATermUtils;
import org.mindswap.pellet.utils.SetUtils;
import org.mindswap.pellet.utils.Timer;
import org.mindswap.pellet.utils.progress.ConsoleProgressMonitor;
import org.mindswap.pellet.utils.progress.ProgressMonitor;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLDatatype;
import org.semanticweb.owlapi.model.OWLException;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.SWRLAtom;
import org.semanticweb.owlapi.reasoner.FreshEntityPolicy;
import org.semanticweb.owlapi.reasoner.IndividualNodeSetPolicy;
import org.semanticweb.owlapi.reasoner.NullReasonerProgressMonitor;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.reasoner.OWLReasonerConfiguration;
import org.semanticweb.owlapi.reasoner.SimpleConfiguration;

import aterm.ATermAppl;

import com.clarkparsia.modularity.IncrementalClassifier;
import com.clarkparsia.owlapiv3.OWL;
import com.clarkparsia.owlapiv3.SWRL;
import com.clarkparsia.owlapiv3.XSD;
import com.clarkparsia.pellet.owlapiv3.AxiomConverter;
import com.clarkparsia.pellet.owlapiv3.PelletReasoner;
import com.clarkparsia.pellet.owlapiv3.PelletReasonerFactory;
import com.clarkparsia.pellet.utils.PropertiesBuilder;

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
public class OWLAPIv3Tests extends AbstractOWLAPITests {	
	public static junit.framework.Test suite() {
		return new JUnit4TestAdapter( OWLAPIv3Tests.class );
	}

	@Test
	public void testOWL2() throws OWLException {
		String ns = "http://www.example.org/test#";

		OWLOntology ont = loadOntology( base + "owl2.owl" );
		
		PelletReasoner reasoner = PelletReasonerFactory.getInstance().createReasoner( ont ) ;
		try {
			testOWL2Reasoner( ns, reasoner );
		}
		finally {
			reasoner.dispose();
		}
	}
	
	@Test
	public void testOWL2Incremental() throws OWLException {
		String ns = "http://www.example.org/test#";

		OWLOntology ont = loadOntology( base + "owl2.owl" );

		IncrementalClassifier classifier = new IncrementalClassifier( ont );
		
		try {
	        // force classification
	        classifier.classify();
	        // force realization
	        OWLNamedIndividual ind1 = Individual(ns + "ind1");
	        classifier.getTypes(ind1, true);
	        testOWL2Reasoner(ns, classifier);
        }
        finally {
	        classifier.dispose();
        }				
	}
	
	private void testOWL2Reasoner( String ns, OWLReasoner reasoner ) {
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

		OWLNamedIndividual ind1 = Individual( ns + "ind1" );
		OWLNamedIndividual ind3 = Individual( ns + "ind3" );
		OWLNamedIndividual ind4 = Individual( ns + "ind4" );
		OWLNamedIndividual ind5 = Individual( ns + "ind5" );
		OWLNamedIndividual ind6 = Individual( ns + "ind6" );

		OWLObjectProperty p = ObjectProperty( ns + "p" );
		OWLObjectProperty r = ObjectProperty( ns + "r" );
		OWLObjectProperty invR = ObjectProperty( ns + "invR" );
		OWLObjectProperty ir = ObjectProperty( ns + "ir" );
		OWLObjectProperty as = ObjectProperty( ns + "as" );
		OWLObjectProperty d1 = ObjectProperty( ns + "d1" );
		OWLObjectProperty d2 = ObjectProperty( ns + "d2" );

		assertTrue( reasoner.isConsistent() );

		assertTrue( reasoner.isEntailed( reflexive( r ) ) );
		assertTrue( reasoner.isEntailed( reflexive( invR ) ) );
		assertTrue( reasoner.isEntailed( irreflexive( ir ) ) );
		assertTrue( reasoner.isEntailed( asymmetric( as ) ) );

		assertTrue( reasoner.isEntailed( equivalentClasses( D, or( D1, D2, D3 ) ) ) );
		assertTrue( reasoner.isEntailed( equivalentClasses( D, test1 ) ) );
		assertTrue( reasoner.isEntailed( disjointClasses( D1, D2 ) ) );
		assertTrue( reasoner.isEntailed( disjointClasses( D1, D3 ) ) );
		assertTrue( reasoner.isEntailed( disjointClasses( D2, D3 ) ) );

		assertTrue( reasoner.isEntailed( disjointProperties( d1, d2 ) ) );
		assertTrue( reasoner.isEntailed( disjointProperties( d2, d1 ) ) );
		assertFalse( reasoner.isEntailed( disjointProperties( p, r ) ) );

		assertTrue( reasoner.isEntailed( propertyAssertion( ind1, r, ind1 ) ) );
		assertTrue( reasoner.isEntailed( propertyAssertion( ind1, invR, ind1 ) ) );
		assertTrue( reasoner.isEntailed( differentFrom( ind1, ind3 ) ) );
		assertTrue( reasoner.isEntailed( differentFrom( ind1, ind4 ) ) );
		assertTrue( reasoner.isEntailed( differentFrom( ind5, ind6 ) ) );
		assertTrue( reasoner.isEntailed( propertyAssertion( ind1, p, ind1 ) ) );
		assertTrue( reasoner.isEntailed( classAssertion( ind1, test2 ) ) );
		assertTrue( reasoner.isEntailed( classAssertion( ind1, test3 ) ) );
		assertIteratorValues( reasoner.getTypes( ind1, false ).getFlattened().iterator(),
				new Object[] { OWL.Thing, C, test2, test3 } );

		assertTrue( reasoner.isEntailed( subClassOf( Teenager, OlderThan10 ) ) );
		assertTrue( reasoner.isEntailed( subClassOf( Teenager, YoungerThan20 ) ) );
		assertTrue( reasoner.isEntailed( equivalentClasses( Teenager, Teen ) ) );

//		assertTrue( reasoner.getDataProperties().contains( DataProperty( Namespaces.OWL + "topDataProperty") ) );
//		assertTrue( reasoner.getDataProperties().contains( DataProperty( Namespaces.OWL + "bottomDataProperty") ) );
//		assertTrue( reasoner.getObjectProperties().contains( ObjectProperty( Namespaces.OWL + "topObjectProperty") ) );
//		assertTrue( reasoner.getObjectProperties().contains( ObjectProperty( Namespaces.OWL + "bottomObjectProperty") ) );
	}

	@Test
	public void testUncle() throws OWLException {
		String ns = "http://www.example.org/test#";

		OWLOntology ont = loadOntology( base + "uncle.owl" );

		PelletReasoner reasoner = PelletReasonerFactory.getInstance().createReasoner( ont );

		OWLNamedIndividual Bob = Individual( ns + "Bob" );
		OWLNamedIndividual Sam = Individual( ns + "Sam" );

		OWLObjectProperty uncleOf = ObjectProperty( ns + "uncleOf" );

		assertPropertyValues( reasoner, Bob, uncleOf, Sam );
	}

	@Test
	public void testSibling() throws OWLException {
		String ns = "http://www.example.org/test#";

		OWLOntology ont = loadOntology( base + "sibling.owl" );

		PelletReasoner reasoner = PelletReasonerFactory.getInstance().createReasoner( ont );

		OWLNamedIndividual Bob = Individual( ns + "Bob" );
		OWLNamedIndividual John = Individual( ns + "John" );
		OWLNamedIndividual Jane = Individual( ns + "Jane" );

		OWLObjectProperty hasBrother = ObjectProperty( ns + "hasBrother" );
		OWLObjectProperty hasSister = ObjectProperty( ns + "hasSister" );

		assertPropertyValues( reasoner, Bob, hasBrother, John );
		assertPropertyValues( reasoner, Bob, hasSister, Jane );
	}
	
	public static void assertInstances(PelletReasoner reasoner, OWLClass subj, boolean direct,
			OWLNamedIndividual... values) {
		Set<OWLNamedIndividual> expected = new HashSet<OWLNamedIndividual>( Arrays.asList( values ) );

		assertEquals( expected, reasoner.getInstances( subj, direct ).getFlattened() );
	}
	
	public static void assertPropertyValues(PelletReasoner reasoner, OWLNamedIndividual subj,
			OWLObjectProperty pred, OWLIndividual... values) {
		Set<OWLIndividual> expected = new HashSet<OWLIndividual>( Arrays.asList( values ) );

		assertEquals( expected, reasoner.getObjectPropertyValues( subj, pred ).getFlattened() );
	}
	
	public static void assertPropertyValues(PelletReasoner reasoner, OWLNamedIndividual subj,
			OWLDataProperty pred, OWLLiteral values) {
		Set<OWLLiteral> expected = new HashSet<OWLLiteral>( Arrays.asList( values ) );

		assertEquals( expected, reasoner.getDataPropertyValues( subj, pred ) );
	}
	
	public static void assertTypes(PelletReasoner reasoner, OWLNamedIndividual subj, boolean direct,
			OWLClass... values) {
		Set<OWLClass> expected = new HashSet<OWLClass>( Arrays.asList( values ) );

		assertEquals( expected, reasoner.getTypes( subj, direct ).getFlattened() );
	}


	@Test
	public void testPropertyChain() throws OWLException {
		String ns = "http://www.example.org/test#";

		OWLOntology ont = loadOntology( base + "propertyChain.owl" );

		PelletReasoner reasoner = PelletReasonerFactory.getInstance().createReasoner(  ont );

		OWLClass C = Class( ns + "C" );
		OWLClass S0 = Class( ns + "S0" );
		OWLClass R0 = Class( ns + "R0" );
		OWLClass R1 = Class( ns + "R1" );
		OWLObjectProperty r = ObjectProperty( ns + "r" );
		OWLObjectProperty s = ObjectProperty( ns + "s" );

		OWLNamedIndividual[] a = new OWLNamedIndividual[17];
		for( int i = 0; i < a.length; i++ ) {
	        a[i] = Individual( ns + "a" + i );
        }

		OWLIndividual[] theList = new OWLIndividual[] {
				a[1], a[2], a[3], a[4], a[5], a[6], a[8], a[10], a[12], a[14], a[16] };

		assertTrue( reasoner.isConsistent() );

		assertTrue( reasoner.isEntailed( OWL.transitive( r ) ) );
		assertFalse( reasoner.isEntailed( OWL.transitive( s ) ) );

		assertIteratorValues( reasoner.getInstances( C, false ).getFlattened().iterator(), theList );

		assertIteratorValues( reasoner.getInstances( S0, false ).getFlattened().iterator(), theList );

		assertIteratorValues( reasoner.getInstances( R0, false ).getFlattened().iterator(), new OWLIndividual[] {
				a[7], a[9] } );

		assertIteratorValues( reasoner.getInstances( R1, false ).getFlattened().iterator(), new OWLIndividual[] {
				a[2], a[3], a[4], a[5], a[6] } );

		assertIteratorValues( reasoner.getObjectPropertyValues( a[0], r ).getFlattened().iterator(),
				new OWLIndividual[] { a[7], a[9] } );

		assertIteratorValues( reasoner.getObjectPropertyValues( a[1], r ).getFlattened().iterator(),
				new OWLIndividual[] { a[2], a[3], a[4], a[5], a[6] } );

		assertIteratorValues( reasoner.getObjectPropertyValues( a[0], s ).getFlattened().iterator(), theList );

	}

	@Test
	public void testQualifiedCardinality1() throws OWLException {
		String ns = "http://www.example.org/test#";

		OWLOntology ont = loadOntology( base + "qcr.owl" );

		PelletReasoner reasoner = PelletReasonerFactory.getInstance().createReasoner( ont );

		OWLClass sub = Class( ns + "sub" );
		OWLClass sup = Class( ns + "sup" );

		assertTrue( reasoner.isConsistent() );

		assertTrue( reasoner.isEntailed( subClassOf( sub, sup ) ) );
		assertTrue( reasoner.getSubClasses( sup, false ).getFlattened().contains( sub ) );
		assertTrue( reasoner.getSuperClasses( sub, false ).getFlattened().contains( sup ) );
	}

	@Test
	public void testReflexive2() throws OWLException {
		String ns = "http://www.example.org/test#";
		String foaf = "http://xmlns.com/foaf/0.1/";

		OWLOntology ont = loadOntology( base + "reflexive.owl" );

		PelletReasoner reasoner = PelletReasonerFactory.getInstance().createReasoner( ont );

		OWLObjectProperty[] knows = {
				ObjectProperty( foaf + "knows" ), ObjectProperty( ns + "knows2" ),
				ObjectProperty( ns + "knows3" ) };

		OWLNamedIndividual[] people = new OWLNamedIndividual[5];
		for( int i = 0; i < people.length; i++ ) {
			people[i] = Individual( ns + "P" + (i + 1) );

			for( int j = 0; j < knows.length; j++ ) {
				assertTrue( people[i] + " " + knows[j], reasoner.isEntailed( propertyAssertion(
						people[i], knows[j], people[i] ) ) );

				assertPropertyValues( reasoner, people[i], knows[j], people[i] );
			}
		}
	}

	@Test
	public void testInfiniteChain() throws Exception {
		OWLOntology ont = loadOntology( base + "infiniteChain.owl" );

		PelletReasoner reasoner = PelletReasonerFactory.getInstance().createReasoner( ont );

		assertTrue( !reasoner.isConsistent() );
	}
	
	@Test
	public void testRemoveLiteralNoBuffering1() throws Exception {
		testRemoveLiteral( "ind1", false );
	}
	
	@Test
	public void testRemoveLiteralNoBuffering2() throws Exception {
		testRemoveLiteral( "ind2", false );
	}
	
	@Test
	public void testRemoveLiteralWithBuffering1() throws Exception {
		testRemoveLiteral( "ind1", true );
	}
	
	@Test
	public void testRemoveLiteralWithBuffering2() throws Exception {
		testRemoveLiteral( "ind2", true );
	}
	
	public void testRemoveLiteral(String indName, boolean buffering) throws Exception {
		String ns = "http://www.example.org/test#";

		OWLOntology ont = loadOntology( base + "RemoveLiteral.owl" );

		PelletReasoner reasoner = buffering
			? PelletReasonerFactory.getInstance().createReasoner( ont )
			: PelletReasonerFactory.getInstance().createNonBufferingReasoner( ont );

		OWLDataProperty pInt = DataProperty( ns + "pInt" );
		OWLDataProperty pDouble = DataProperty( ns + "pDouble" );
		OWLDataProperty pBoolean = DataProperty( ns + "pBoolean" );

		OWLNamedIndividual ind = Individual( ns + indName );

		OWLLiteral valDouble = ind.getDataPropertyValues( pDouble, ont ).iterator().next();
		OWLLiteral valInt = ind.getDataPropertyValues( pInt, ont ).iterator().next();
		OWLLiteral valBoolean = ind.getDataPropertyValues( pBoolean, ont ).iterator().next();

		assertTrue( reasoner.isConsistent() );
		
		removeAxioms(ont, propertyAssertion( ind, pDouble, valDouble ) );
		if( buffering ) {
			assertFalse( reasoner.getDataPropertyValues( ind, pDouble ).isEmpty() );
			reasoner.flush();
		}
		assertTrue( reasoner.getDataPropertyValues( ind, pDouble ).isEmpty() );

		removeAxioms( ont, propertyAssertion( ind, pInt, valInt ) );
		if( buffering ) {
			assertFalse( reasoner.getDataPropertyValues( ind, pInt ).isEmpty() );
			reasoner.flush();
		}
		assertTrue( reasoner.getDataPropertyValues( ind, pInt ).isEmpty() );

		removeAxioms( ont, propertyAssertion( ind, pBoolean, valBoolean ) );
		if( buffering ) {
			assertFalse( reasoner.getDataPropertyValues( ind, pBoolean ).isEmpty() );
			reasoner.flush();
		}
		assertTrue( reasoner.getDataPropertyValues( ind, pBoolean ).isEmpty() );

		// assertTrue( reasoner.getDataPropertyRelationships( ind ).isEmpty() );

		OWLLiteral newVal = constant( "0.0", XSD.DOUBLE );
		addAxioms( ont, propertyAssertion( ind, pDouble, newVal ) );
		if( buffering ) {
	        reasoner.flush();
        }

		assertTrue( reasoner.isConsistent() );
	}

	@Test
	public void testFamily() throws OWLException {
		String ns = "http://www.example.org/family#";

		OWLOntology ont = loadOntology( base + "family.owl" );
		for (OWLAxiom axiom : ont.getAxioms()) {
			System.out.println(axiom);	        
        }

		PelletReasoner reasoner = PelletReasonerFactory.getInstance().createReasoner( ont );
		
		try {
	        testFamily( ns, reasoner );
	        
	        reasoner.getKB().realize();
	        
	        testFamily( ns, reasoner );
        }
        finally {
    		reasoner.dispose();
        }
	}
	
	@Test
	public void testFamilyIncremental() {
		String ns = "http://www.example.org/family#";

		OWLOntology ont = loadOntology( base + "family.owl" );

		IncrementalClassifier classifier = new IncrementalClassifier( ont );
		
		try {
	        // force classification
	        classifier.classify();
	        
	        testFamily( ns, classifier );
	        
	        // force realization
	        OWLNamedIndividual ind1 = Individual( ns + "ind1" );
	        classifier.getTypes(ind1, true);
	        
	        testFamily( ns, classifier );
        }
        finally {
    		classifier.dispose();
        }
	}
	
	private void testFamily( String ns, OWLReasoner reasoner ) {
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

		assertTrue( reasoner.isEntailed( transitive( hasAncestor ) ) );
		assertFalse( reasoner.isEntailed( functional( hasAncestor ) ) );

		assertTrue( reasoner.isEntailed( transitive( hasDescendant ) ) );
		assertFalse( reasoner.isEntailed( functional( hasDescendant ) ) );

		assertTrue( reasoner.isEntailed( ( symmetric( isMarriedTo ) ) ) );
		assertTrue( reasoner.isEntailed( ( irreflexive( isMarriedTo ) ) ) );

		assertTrue( reasoner.isEntailed( subPropertyOf( hasParent, hasAncestor ) ) );
		assertTrue( reasoner.isEntailed( subPropertyOf( hasFather, hasAncestor ) ) );
		assertTrue( reasoner.isEntailed( subPropertyOf( hasMother, hasAncestor ) ) );
		assertTrue( reasoner.isEntailed( subPropertyOf( hasChild, hasDescendant ) ) );

		assertTrue( reasoner.isEntailed( disjointProperties( likes, dislikes ) ) );
		assertTrue( reasoner.isEntailed( disjointProperties( dislikes, likes ) ) );
		assertTrue( reasoner.isEntailed( disjointProperties( hasFather, hasMother ) ) );
		assertTrue( reasoner.isEntailed( disjointProperties( hasMother, hasFather ) ) );

		assertTrue( reasoner.isEntailed( classAssertion( grandfather, Person ) ) );
		assertTrue( reasoner.isEntailed( classAssertion( grandfather, PersonWithAtLeastTwoChildren ) ) );
		assertTrue( reasoner.isEntailed( classAssertion( grandfather, PersonWithAtLeastTwoMaleChildren ) ) );
		assertTrue( reasoner.isEntailed( classAssertion( grandfather, Male ) ) );
		assertTrue( reasoner.isEntailed( classAssertion( grandfather, Senior ) ) );
		assertTrue( reasoner.isEntailed( propertyAssertion( grandfather, isMarriedTo,
						grandmother ) ) );
		assertTrue( reasoner.isEntailed( propertyAssertion( grandfather, hasChild, father ) ) );
		assertTrue( reasoner.isEntailed( propertyAssertion( grandfather, hasSon, father ) ) );
		assertTrue( reasoner.isEntailed( differentFrom( grandfather, grandmother ) ) );
		assertTrue( reasoner.isEntailed( differentFrom( grandfather, father ) ) );
		assertTrue( reasoner.isEntailed( differentFrom( grandfather, mother ) ) );
		assertTrue( reasoner.isEntailed( differentFrom( grandfather, son ) ) );
		assertTrue( reasoner.isEntailed( differentFrom( grandfather, daughter ) ) );

		assertTrue( reasoner.isEntailed( classAssertion( grandmother, Person ) ) );
		assertTrue( reasoner.isEntailed( classAssertion( grandmother, Female ) ) );
		assertTrue( reasoner.isEntailed( classAssertion( grandmother, Senior ) ) );
		assertTrue( reasoner.isEntailed( propertyAssertion( grandmother, isMarriedTo,
						grandfather ) ) );
		assertTrue( reasoner.isEntailed( propertyAssertion( grandmother, hasChild, father ) ) );
		assertFalse( reasoner.isEntailed( propertyAssertion( grandmother, hasSon, father ) ) );

		assertTrue( reasoner.isEntailed( classAssertion( father, Person ) ) );
		assertTrue( reasoner.isEntailed( classAssertion( father, Male ) ) );
		assertTrue( reasoner.isEntailed( classAssertion( father, Adult ) ) );
		assertTrue( reasoner.isEntailed( propertyAssertion( father, hasParent, grandfather ) ) );
		assertTrue( reasoner.isEntailed( propertyAssertion( father, hasParent, grandmother ) ) );
		assertTrue( reasoner.isEntailed( propertyAssertion( father, hasFather, grandfather ) ) );
		assertTrue( reasoner.isEntailed( propertyAssertion( father, hasMother, grandmother ) ) );
		assertTrue( reasoner.isEntailed( propertyAssertion( father, hasChild, son ) ) );
		assertTrue( reasoner.isEntailed( propertyAssertion( father, hasSon, son ) ) );
		assertTrue( reasoner.isEntailed( propertyAssertion( father, hasChild, daughter ) ) );
		assertFalse( reasoner.isEntailed( propertyAssertion( father, hasDaughter, daughter ) ) );

		assertTrue( reasoner.isEntailed( classAssertion( mother, Person ) ) );
		assertTrue( reasoner.isEntailed( classAssertion( mother, Female ) ) );

		assertTrue( reasoner.isEntailed( classAssertion( son, Male ) ) );
		assertTrue( reasoner.isEntailed( classAssertion( son, Teenager ) ) );
		assertTrue( reasoner.isEntailed( classAssertion( son, Teen ) ) );
		assertTrue( reasoner.isEntailed( propertyAssertion( son, hasParent, father ) ) );
		assertTrue( reasoner.isEntailed( propertyAssertion( son, hasFather, father ) ) );
		assertTrue( reasoner.isEntailed( propertyAssertion( son, hasSibling, daughter ) ) );
		assertTrue( reasoner.isEntailed( propertyAssertion( son, hasSister, daughter ) ) );

		assertTrue( reasoner.isEntailed( classAssertion( daughter, Female ) ) );
		assertTrue( reasoner.isEntailed( classAssertion( daughter, Child ) ) );
		assertTrue( reasoner.isEntailed( propertyAssertion( daughter, hasAncestor, grandfather ) ) );
		assertTrue( reasoner.isEntailed( propertyAssertion( daughter, hasAncestor, grandmother ) ) );
		assertTrue( reasoner.isEntailed( propertyAssertion( daughter, hasParent, father ) ) );
		assertTrue( reasoner.isEntailed( propertyAssertion( daughter, hasFather, father ) ) );
		assertTrue( reasoner.isEntailed( propertyAssertion( daughter, hasParent, mother ) ) );
		assertTrue( reasoner.isEntailed( propertyAssertion( daughter, hasMother, mother ) ) );
		assertTrue( reasoner.isEntailed( propertyAssertion( daughter, hasSibling, son ) ) );
		assertFalse( reasoner.isEntailed( propertyAssertion( daughter, hasBrother, son ) ) );

		assertTrue( reasoner.isEntailed( differentFrom( personX, personY ) ) );
		assertTrue( reasoner.isEntailed( differentFrom( personX, personZ ) ) );
		assertTrue( reasoner.isEntailed( differentFrom( personY, personZ ) ) );

		assertTrue( reasoner.isEntailed( equivalentClasses( Teen, Teenager ) ) );
		assertTrue( reasoner.isEntailed( subClassOf( Senior, Adult ) ) );

		assertTrue( reasoner.isEntailed( subClassOf( PersonWithAtLeastTwoMaleChildren, Person ) ) );
		assertTrue( reasoner.isEntailed( subClassOf( PersonWithAtLeastTwoFemaleChildren, Person ) ) );
		assertTrue( reasoner.isEntailed( subClassOf( PersonWithAtLeastTwoChildren, Person ) ) );
		assertTrue( reasoner.isEntailed( subClassOf( PersonWithAtLeastFourChildren, Person ) ) );

		assertTrue( reasoner.isEntailed( subClassOf( PersonWithAtLeastFourChildren,
						PersonWithAtLeastTwoChildren ) ) );
		assertTrue( reasoner.isEntailed( subClassOf( PersonWithAtLeastTwoMaleChildren,
						PersonWithAtLeastTwoChildren ) ) );
		assertTrue( reasoner.isEntailed( subClassOf( PersonWithAtLeastTwoFemaleChildren,
						PersonWithAtLeastTwoChildren ) ) );

		assertFalse( reasoner.isEntailed( subClassOf( PersonWithAtLeastTwoFemaleChildren,
						PersonWithAtLeastTwoMaleChildren ) ) );
		assertFalse( reasoner.isEntailed( subClassOf( PersonWithAtLeastTwoMaleChildren,
						PersonWithAtLeastTwoFemaleChildren ) ) );

		// kb.timers.print();
	}

	/**
	 * Verifies that OWL 2 entity declarations are parsed from RDF/XML and
	 * handled correctly.
	 */
//	@Test
//	public void entityDeclarations() {
//		String ns = "http://www.example.org/test#";
//
//		OWLOntology ont = loadOntology( base + "/entityDeclarations.owl" );
//
//		PelletReasoner reasoner = PelletReasonerFactory.getInstance().createReasoner( ont );
//		assertTrue( reasoner.isConsistent() );
//
//		KnowledgeBase kb = reasoner.getKB();
//
//		assertTrue( reasoner.isDefined( Individual( ns + "a" ) ) );
//		assertEquals( 1, reasoner.getIndividuals().size() );
//
//		assertTrue( reasoner.isDefined( Class( ns + "C" ) ) );
//		assertEquals( 1, reasoner.getClasses().size() );
//		// FIXME: OWLAPI should support Datatype definition checking
//		assertFalse( kb.isDatatype( ATermUtils.makeTermAppl( ns + "C" ) ) );
//
//		assertFalse( reasoner.isDefined( Class( ns + "D" ) ) );
//		// FIXME: OWLAPI should support Datatype definition checking
//		// FIXME: OWLAPI loader does not parse Datatype declarations
//		//assertTrue( kb.isDatatype( ATermUtils.makeTermAppl( ns + "D" ) ) );
//
//		/* FIXME: There is no positive check here because OWLAPI does not support annotation property declaration. */
//		assertFalse( reasoner.isDefined( DataProperty( ns + "p" ) ) );
//		assertFalse( reasoner.isDefined( ObjectProperty( ns + "p" ) ) );
//		
//		assertTrue( reasoner.isDefined( ObjectProperty( ns + "q" ) ) );
//		assertEquals( 2 + 1, reasoner.getObjectProperties().size() );
//		assertFalse( kb.isAnnotationProperty( ATermUtils.makeTermAppl( ns + "r" ) ) );
//		assertFalse( reasoner.isDefined( DataProperty( ns + "q" ) ) );
//
//		assertTrue( reasoner.isDefined( DataProperty( ns + "r" ) ) );
//		assertEquals( 2 + 1, reasoner.getDataProperties().size() );
//		assertFalse( kb.isAnnotationProperty( ATermUtils.makeTermAppl( ns + "r" ) ) );
//		assertFalse( reasoner.isDefined( ObjectProperty( ns + "r" ) ) );
//	}

	@Test
	public void testAnonInverse() throws OWLException {
		String ns = "http://www.example.org/test#";

		OWLOntology ont = loadOntology( base + "anon_inverse.owl" );

		OWLClass C = Class( ns + "C" );
		OWLClass D = Class( ns + "D" );
		OWLObjectProperty r = ObjectProperty( ns + "r" );
		OWLClassExpression desc = some( inverse( r ), D );

		PelletReasoner reasoner = PelletReasonerFactory.getInstance().createReasoner( ont );

		assertEquals( Collections.singleton( C ), reasoner
				.getSubClasses( desc, true ).getFlattened() );

		assertTrue( reasoner.isEntailed( inverseFunctional( ObjectProperty( ns + "functionalP" ) ) ) );

		assertTrue( reasoner.isEntailed( functional( ObjectProperty( ns + "inverseFunctionalP" ) ) ) );

		assertTrue( reasoner.isEntailed( transitive( ObjectProperty( ns + "transitiveP" ) ) ) );

		assertTrue( reasoner.isEntailed( symmetric( ObjectProperty( ns + "symmetricP" ) ) ) );

		assertTrue( reasoner.isEntailed( reflexive( ObjectProperty( ns + "reflexiveP" ) ) ) );

		assertTrue( reasoner.isEntailed( irreflexive( ObjectProperty( ns + "irreflexiveP" ) ) ) );

		assertTrue( reasoner.isEntailed( asymmetric( ObjectProperty( ns + "asymmetricP" ) ) ) );

		OWLObjectProperty p1 = ObjectProperty( ns + "p1" );
		OWLObjectProperty p2 = ObjectProperty( ns + "p2" );
		OWLObjectProperty p3 = ObjectProperty( ns + "p3" );
		assertTrue( reasoner.isEntailed( equivalentProperties( p1, p2 ) ) );
		assertTrue( reasoner.isEntailed( equivalentProperties( p1, p3 ) ) );
		assertTrue( reasoner.isEntailed( equivalentProperties( p2, p3 ) ) );
	}

	@Test
	public void testDLSafeRules() throws OWLOntologyCreationException {
		String ns = "http://owldl.com/ontologies/dl-safe.owl#";

		OWLOntology ont = loadOntology( base + "dl-safe.owl" );

		PelletReasoner reasoner = PelletReasonerFactory.getInstance().createReasoner( ont );

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

		OWLNamedIndividual Abel = Individual( ns + "Abel" );
		OWLNamedIndividual Cain = Individual( ns + "Cain" );
		OWLNamedIndividual Oedipus = Individual( ns + "Oedipus" );
		OWLNamedIndividual Remus = Individual( ns + "Remus" );
		OWLNamedIndividual Romulus = Individual( ns + "Romulus" );

		for( int test = 0; test < 2; test++ ) {
			if( test != 0 ) {
	            reasoner.prepareReasoner();
            }

			assertTrue( reasoner.isEntailed( propertyAssertion( Abel, sibling, Cain ) ) );

			assertPropertyValues( reasoner, Abel, sibling, Cain);

			assertTrue( reasoner.isEntailed( propertyAssertion( Cain, sibling, Abel ) ) );

			assertPropertyValues( reasoner, Cain, sibling, Abel );

			assertTrue( reasoner.isEntailed( propertyAssertion( Cain, hates, Abel ) ) );

			assertFalse( reasoner.isEntailed( propertyAssertion( Abel, hates, Cain ) ) );

			assertTrue( reasoner.isEntailed( classAssertion( Cain, Grandchild ) ) );

			assertTrue( reasoner.isEntailed( classAssertion( Cain, BadChild ) ) );

			assertFalse( reasoner.isEntailed( propertyAssertion( Romulus, sibling, Remus ) ) );

			assertTrue( reasoner.isEntailed( classAssertion( Romulus, Grandchild ) ) );

			assertFalse( reasoner.isEntailed( classAssertion( Romulus, BadChild ) ) );

			assertTrue( reasoner.isEntailed( classAssertion( Oedipus, Child ) ) );
		}

		assertIteratorValues( reasoner.getTypes( Cain, true ).getFlattened().iterator(),
				new Object[] { BadChild, Child, Person } );
	}
	

	@Test
	public void testDLSafeConstants() throws OWLOntologyCreationException {
		String ns = "http://owldl.com/ontologies/dl-safe-constants.owl#";

		OWLOntology ont = loadOntology( base + "dl-safe-constants.owl" );

		PelletReasoner reasoner = PelletReasonerFactory.getInstance().createReasoner( ont );

		OWLClass DreamTeamMember = Class( ns + "DreamTeamMember" );
		OWLClass DreamTeamMember1 = Class( ns + "DreamTeamMember1" );
		OWLClass DreamTeamMember2 = Class( ns + "DreamTeamMember2" );

		OWLIndividual Alice = Individual( ns + "Alice" );
		OWLIndividual Bob = Individual( ns + "Bob" );
		OWLIndividual Charlie = Individual( ns + "Charlie" );

		for( int test = 0; test < 1; test++ ) {
			if( test != 0 ) {
	            reasoner.prepareReasoner();
            }

			assertIteratorValues( reasoner.getInstances( DreamTeamMember, false ).getFlattened().iterator(),
					new Object[] { Alice, Bob, Charlie } );

			assertIteratorValues( reasoner.getInstances( DreamTeamMember1, false ).getFlattened().iterator(),
					new Object[] { Alice, Bob, Charlie } );

			assertIteratorValues( reasoner.getInstances( DreamTeamMember2, false ).getFlattened().iterator(),
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

		OWLOntology ont = OWL.Ontology( transitive( p1 ),
				classAssertion( x, all( p1, C ) ), propertyAssertion( x, p1, y ),
				propertyAssertion( y, p1, z ) );

		PelletReasoner reasoner = PelletReasonerFactory.getInstance().createReasoner( ont );

		assertTrue( reasoner.isEntailed( classAssertion( y, C ) ) );
		assertTrue( reasoner.isEntailed( classAssertion( z, C ) ) );

		OWLAxiom[] axioms = new OWLAxiom[] {
				functional( p1 ), inverseFunctional( p1 ), irreflexive( p1 ), asymmetric( p1 ),
				disjointProperties( p1, p2 ), subClassOf( C, min( p1, 2 ) ),
				classAssertion( x, max( p1, 3 ) ), disjointClasses( C, min( p1, 2 ) ) };

		for( int i = 0; i < axioms.length; i++ ) {
			addAxioms( ont, axioms[i] );

			reasoner = PelletReasonerFactory.getInstance().createReasoner( ont );
			assertTrue( axioms[i].toString(), reasoner.isEntailed( classAssertion( y, C ) ) );
			assertFalse( axioms[i].toString(), reasoner.isEntailed( classAssertion( z, C ) ) );

			removeAxioms( ont, axioms[i] );
		}
	}

	@Test
	public void testInvalidTransitivity2() throws OWLOntologyCreationException {
		OWLOntology ont = loadOntology( base + "invalidTransitivity.owl" );

		PelletReasoner reasoner = PelletReasonerFactory.getInstance().createReasoner( ont );

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

		IRI ontIRI = IRI.create( base + "invalidTransitivity.owl" );

		OWLNamedIndividual a = Individual( ns + "a" );
		OWLNamedIndividual b = Individual( ns + "b" );
		OWLNamedIndividual c = Individual( ns + "c" );

		OWLObjectProperty p = ObjectProperty( ns + "p" );
		OWLObjectProperty q = ObjectProperty( ns + "q" );

		Set<OWLAxiom> axioms = new HashSet<OWLAxiom>();

		axioms.add( propertyAssertion( a, p, c ) );

		axioms.add( propertyAssertion( b, p, b ) );

		axioms.add( propertyAssertion( c, q, a ) );

		axioms.add( sameAs( b, c ) );

		axioms.add( propertyAssertion( a, q, c ) );

		OWLOntology ont = OWL.Ontology( axioms, ontIRI );

		PelletReasoner reasoner = PelletReasonerFactory.getInstance().createReasoner( ont );

		assertEquals( SetUtils.create( a ), reasoner.getSameIndividuals( a ).getEntities() );
		assertEquals( SetUtils.create( b, c ), reasoner.getSameIndividuals( b ).getEntities() );
		assertEquals( SetUtils.create( b, c ), reasoner.getSameIndividuals( c ).getEntities() );

		assertPropertyValues( reasoner, a, p, b, c );
		
		assertPropertyValues( reasoner, a, q, b, c );
		
		assertPropertyValues( reasoner, b, p, b, c );
		
		assertPropertyValues( reasoner, b, q, a );
		
		assertPropertyValues( reasoner, c, p, b, c );
		
		assertPropertyValues( reasoner, c, q, a );
	}

	@Test
	public void testSameAs3() throws OWLException {
		String ns = "urn:test:";

		IRI ontIRI = IRI.create( base + "test.owl" );

		OWLNamedIndividual i1 = Individual( ns + "i1" );
		OWLNamedIndividual i2 = Individual( ns + "i2" );
		OWLNamedIndividual i3 = Individual( ns + "i3" );

		OWLClass c = Class( ns + "c" );

		Set<OWLAxiom> axioms = new HashSet<OWLAxiom>();

		axioms.add( equivalentClasses( c, oneOf( i1, i2 ) ) );

		axioms.add( classAssertion( i3, c ) );

		OWLOntology ont = OWL.Ontology( axioms, ontIRI );

		PelletReasoner reasoner = PelletReasonerFactory.getInstance().createReasoner( ont );

		assertTrue( !reasoner.isEntailed( sameAs( i1, i2 ) ) );
		assertTrue( !reasoner.isEntailed( sameAs( i1, i3 ) ) );
		assertEquals( SetUtils.create( i1 ), reasoner.getSameIndividuals( i1 ).getEntities() );

		assertTrue( !reasoner.isEntailed( sameAs( i2, i1 ) ) );
		assertTrue( !reasoner.isEntailed( sameAs( i2, i3 ) ) );
		assertEquals( SetUtils.create( i2 ), reasoner.getSameIndividuals( i2 ).getEntities() );

		assertTrue( !reasoner.isEntailed( sameAs( i3, i1 ) ) );
		assertTrue( !reasoner.isEntailed( sameAs( i3, i2 ) ) );
		assertEquals( SetUtils.create( i3 ), reasoner.getSameIndividuals( i3 ).getEntities() );

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
		Properties newOptions = PropertiesBuilder.singleton( "REALIZE_INDIVIDUAL_AT_A_TIME", "true" );
		Properties savedOptions = PelletOptions.setOptions( newOptions );
		
		try {
			ProgressMonitor monitor = new TimedProgressMonitor( 1 );
	
			OWLOntology ont = loadOntology( base + "food.owl" );
			
			PelletReasoner pellet = PelletReasonerFactory.getInstance().createReasoner( ont );
			KnowledgeBase kb = pellet.getKB();
	
			kb.classify();
	
			kb.getTaxonomyBuilder().setProgressMonitor( monitor );
	
			kb.realize();
	
			assertFalse( kb.isRealized() );
		}
		finally {
			PelletOptions.setOptions( savedOptions );			
		}
	}

	@Test
	// This tests ticket 147
	// Not having timeout functionality in classification and realization makes
	// it harder to interrupt these processes
	public void testClassificationTimeout() throws Exception {
		boolean timeout = false;

		OWLOntology ont = loadOntology( base + "food.owl" );

		PelletReasoner pellet = PelletReasonerFactory.getInstance().createReasoner( ont );
		KnowledgeBase kb = pellet.getKB();

		Timer timer = kb.timers.createTimer( "classify" );
		timer.setTimeout( 1 );


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

		OWLOntology ont = loadOntology( "file:" + PelletTestSuite.base + "modularity/SWEET.owl" );

		PelletReasoner pellet = PelletReasonerFactory.getInstance().createReasoner( ont );
		KnowledgeBase kb = pellet.getKB();

		Timer timer = kb.timers.createTimer( "realize" );
		timer.setTimeout( 1 );

		try {
			kb.classify();
		} catch( TimeoutException e ) {
			timeout = true;
		}

		assertFalse( timeout );
		assertTrue( kb.isClassified() );

		long time = System.currentTimeMillis();
		try {
			kb.realize();
			time = System.currentTimeMillis() - time;
		} catch( TimeoutException e ) {
			timeout = true;
		}

		assertTrue( "Timeout failed: " + timer + "\nAll timers:\n" + kb.timers, timeout );
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

		Set<SWRLAtom> antecedent = new HashSet<SWRLAtom>();
		Set<SWRLAtom> consequent = new HashSet<SWRLAtom>();

		antecedent.add( classAtom( Class( "C" ), variable( "x" ) ) );
		consequent.add( classAtom( Class( "D" ), variable( "x" ) ) );

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

		Set<SWRLAtom> antecedent = new HashSet<SWRLAtom>();
		Set<SWRLAtom> consequent = new HashSet<SWRLAtom>();

		antecedent.add( classAtom( Class( "C" ), variable( "x" ) ) );
		consequent.add( classAtom( Class( "D" ), variable( "x" ) ) );

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

		Set<SWRLAtom> antecedent = new HashSet<SWRLAtom>();
		Set<SWRLAtom> consequent = new HashSet<SWRLAtom>();

		antecedent.add( classAtom( Class( "C" ), variable( "x" ) ) );
		consequent.add( classAtom( Class( "D" ), variable( "x" ) ) );

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

		Set<SWRLAtom> antecedent = new HashSet<SWRLAtom>();
		Set<SWRLAtom> consequent = new HashSet<SWRLAtom>();

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

		Set<SWRLAtom> antecedent = new HashSet<SWRLAtom>();
		Set<SWRLAtom> consequent = new HashSet<SWRLAtom>();

		antecedent.add( propertyAtom( ObjectProperty( "p" ), variable( "x" ), variable( "y" ) ) );
		consequent.add( propertyAtom( ObjectProperty( "q" ), variable( "x" ), variable( "y" ) ) );

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

		Set<SWRLAtom> antecedent = new HashSet<SWRLAtom>();
		Set<SWRLAtom> consequent = new HashSet<SWRLAtom>();

		antecedent.add( propertyAtom( DataProperty( "s" ), variable( "x" ), variable( "y" ) ) );
		consequent.add( propertyAtom( DataProperty( "r" ), variable( "x" ), variable( "y" ) ) );

		OWLAxiom expected = rule( antecedent, consequent );

		assertEquals( expected, actual );
	}

	@Test
	public void typeInheritanceWithAnonIndividual() throws OWLOntologyCreationException {
		OWLAxiom[] axioms = {
				OWL.subClassOf( C, D ),
				OWL.classAssertion( anon, C ) };
		
		OWLOntology ont = OWL.Ontology( axioms );
		PelletReasoner pellet = PelletReasonerFactory.getInstance().createReasoner( ont );

		assertTrue( pellet.getInstances( D, true ).getNodes().size() == 0 );

		assertTrue( pellet.getInstances( D, false ).getNodes().size() == 1 );
	}
	
	@Test
	public void testSubClassDirectParameter() {
		OWLAxiom[] axioms = {
				OWL.subClassOf( E, D ),
				OWL.subClassOf( D, C ) };
		
		OWLOntology ont = OWL.Ontology( axioms );
		PelletReasoner pellet = PelletReasonerFactory.getInstance().createReasoner( ont );

		assertTrue( pellet.getSubClasses( C, true ).getNodes().size() == 1 );		
		assertTrue( pellet.getSubClasses( C, false ).getNodes().size() == 3 ); // includes owl:Nothing
	}
	
	private PelletReasoner setupReasonerIndividualNodeSetPolicy(IndividualNodeSetPolicy p) {
		OWLAxiom[] axioms = {
				OWL.classAssertion( a, C ),
				OWL.classAssertion( b, C ),
				OWL.classAssertion( c, C ),
				OWL.sameAs( a, b ),
				OWL.differentFrom( b, c ),
				OWL.differentFrom( a, c )
		};
		
		OWLOntology ont = OWL.Ontology( axioms );
		OWLReasonerConfiguration config = new SimpleConfiguration( new NullReasonerProgressMonitor(), FreshEntityPolicy.ALLOW, Long.MAX_VALUE, p );
		PelletReasoner pellet = PelletReasonerFactory.getInstance().createReasoner( ont, config );
		
		return pellet;
	}
	
	@Test
	public void testIndividualNodeSetPolicyBySameAs() {
		PelletReasoner pellet = setupReasonerIndividualNodeSetPolicy(IndividualNodeSetPolicy.BY_SAME_AS);
		
		assertTrue( pellet.getInstances( C, true ).getNodes().size() == 2 );		
		assertTrue( pellet.getDifferentIndividuals( c ).getNodes().size() == 1 );
	}
	
	@Test
	public void testIndividualNodeSetPolicyByName() {
		PelletReasoner pellet = setupReasonerIndividualNodeSetPolicy(IndividualNodeSetPolicy.BY_NAME);
		
		assertTrue( pellet.getInstances( C, true ).getNodes().size() == 3 );		
		assertTrue( pellet.getDifferentIndividuals( c ).getNodes().size() == 2 );
	}
	
	@Test
	public void testTopBottomPropertyAssertion() throws OWLOntologyCreationException {
		OWLAxiom[] axioms = {
				OWL.propertyAssertion( a, OWL.topObjectProperty, b ),
				OWL.propertyAssertion( a, OWL.topDataProperty, lit ),
				OWL.propertyAssertion( a, OWL.bottomObjectProperty, b ),
				OWL.propertyAssertion( a, OWL.bottomDataProperty, lit ) };
		
		for( int i = 0; i < axioms.length; i++ ) {
			OWLOntology ont = OWL.Ontology( axioms[i] );
			PelletReasoner pellet = PelletReasonerFactory.getInstance().createReasoner( ont );

			assertEquals( i < 2, pellet.isConsistent() );
		}
	}
	
	@Test
	public void testTopBottomPropertyInferences() throws OWLOntologyCreationException {
		boolean prevValue = PelletOptions.HIDE_TOP_PROPERTY_VALUES; 
		PelletOptions.HIDE_TOP_PROPERTY_VALUES = false;
		try {
			
		createReasoner(
				OWL.propertyAssertion( a, p, b ),
				OWL.classAssertion( c, C ),
				OWL.propertyAssertion( a, dp, lit )
		);

		
		assertTrue( reasoner.isEntailed( OWL.subPropertyOf( p, OWL.topObjectProperty ) ) );
		assertTrue( reasoner.isEntailed( OWL.subPropertyOf( OWL.bottomObjectProperty, p ) ) );
		assertTrue( reasoner.isEntailed( OWL.subPropertyOf( dp, OWL.topDataProperty ) ) );
		assertTrue( reasoner.isEntailed( OWL.subPropertyOf( OWL.bottomDataProperty, dp ) ) );
		
		assertEquals( Collections.singleton( p ), reasoner.getSubObjectProperties( OWL.topObjectProperty, true ).getFlattened() );
		assertEquals( Collections.singleton( OWL.bottomObjectProperty ), reasoner.getSubObjectProperties( p, true ).getFlattened() );
		assertEquals( Collections.singleton( dp ), reasoner.getSubDataProperties( OWL.topDataProperty, true ).getFlattened() );
		assertEquals( Collections.singleton( OWL.bottomDataProperty ), reasoner.getSubDataProperties( dp, true ).getFlattened() );
		
		assertTrue( reasoner.isEntailed( propertyAssertion( a, p, b ) ) );
		assertFalse( reasoner.isEntailed( propertyAssertion( b, p, a ) ) );
		assertTrue( reasoner.isEntailed( propertyAssertion( a, OWL.topObjectProperty, b ) ) );
		assertTrue( reasoner.isEntailed( propertyAssertion( b, OWL.topObjectProperty, a ) ) );
		
		assertTrue( reasoner.isEntailed( propertyAssertion( a, dp, lit ) ) );
		assertFalse( reasoner.isEntailed( propertyAssertion( b, dp, lit ) ) );
		assertTrue( reasoner.isEntailed( propertyAssertion( a, OWL.topDataProperty, lit ) ) );
		assertTrue( reasoner.isEntailed( propertyAssertion( b, OWL.topDataProperty, lit ) ) );
		
		
		assertPropertyValues( reasoner, a, p, b );
		assertTrue( reasoner.getObjectPropertyValues( b, p ).getFlattened().isEmpty() );
		assertPropertyValues( reasoner, a , OWL.topObjectProperty, a, b, c );
		assertPropertyValues( reasoner, b , OWL.topObjectProperty, a, b, c );
		assertPropertyValues( reasoner, a , OWL.topDataProperty, lit );
		assertPropertyValues( reasoner, b , OWL.topDataProperty, lit );
		
		}
		finally { 
			PelletOptions.HIDE_TOP_PROPERTY_VALUES = prevValue;
		}
	}
	
	@Ignore
	@Test
	public void testSetTheory() throws OWLException {
		// This tests #388
		
		String ns = "http://www.integratedmodelling.org/ks/tarassandbox/set-theory.owl#";

		OWLOntology ont = loadOntology( base + "set-theory.owl" );

		PelletReasoner reasoner = PelletReasonerFactory.getInstance().createReasoner( ont );

		reasoner.getKB().classify();
		
		assertEquals( SetUtils.create( OWL.Class( ns + "SetOfXSets" ), OWL.Class( ns + "XSet" ),
				OWL.Class( ns + "XThing" ) ), reasoner.getSubClasses(
				OWL.Class( ns + "XSetTheoryClass" ), true ).getFlattened() );
	}
	
	@Test
	public void datatypeDefinition() {		
		OWLDatatype between5and10 = OWL.Datatype( "between5and10" );
		OWLDatatype between6and8 = OWL.Datatype( "between6and8" );
		
		createReasoner(
			OWL.datatypeDefinition( between5and10, OWL.restrict( XSD.INTEGER, OWL.minInclusive( 5 ), OWL.maxInclusive( 10 ) ) ),
			OWL.datatypeDefinition( between6and8, OWL.restrict( XSD.INTEGER, OWL.minInclusive( 6 ), OWL.maxInclusive( 8 ) ) ),
			OWL.equivalentClasses( A, OWL.some( dp, between5and10 ) ),
			OWL.equivalentClasses( B, OWL.some( dp, between6and8 ) ),
			OWL.propertyAssertion( a, dp, OWL.constant( 9 ) ),
			OWL.propertyAssertion( b, dp, OWL.constant( 7 ) )
		);
		
		assertTrue( reasoner.isEntailed( OWL.subClassOf( B, A ) ) );
		assertTrue( reasoner.isEntailed( OWL.classAssertion( a, A ) ) );
		assertFalse( reasoner.isEntailed( OWL.classAssertion( a, B ) ) );
		assertTrue( reasoner.isEntailed( OWL.classAssertion( b, A ) ) );
		assertTrue( reasoner.isEntailed( OWL.classAssertion( b, B ) ) );
	}
	
	
	@Test
	public void dataRangeEntailment() {		
		createReasoner(
			OWL.range( dp, XSD.INT )
		);
		
		assertTrue( reasoner.isEntailed( OWL.range( dp, XSD.INT ) ) );
		assertTrue( reasoner.isEntailed( OWL.range( dp, XSD.INTEGER ) ) );
		assertFalse( reasoner.isEntailed( OWL.range( dp, XSD.FLOAT ) ) );
		assertFalse( reasoner.isEntailed( OWL.range( dp, XSD.STRING ) ) );
	}
	

	@Test
	/**
	 * Tests for the bug reported in #149
	 */
	public void test149() throws OWLOntologyCreationException {
		createReasoner(OWL.inverseProperties(p, q));
		
		assertTrue(reasoner.isEntailed(OWL.equivalentProperties(p, OWL.inverse(q))));
	}
	
	@Test
	/**
	 * Tests for the bug reported in #150
	 */
	public void test150_1() {
		createReasoner(OWL.disjointProperties(p, OWL.inverse(q)));
		
		assertTrue(reasoner.isEntailed(OWL.disjointProperties(p, OWL.inverse(q))));		
	}
	
	@Test
	/**
	 * Tests for the bug reported in #150
	 */
	public void test150_2() {		
		createReasoner(
			OWL.domain(p, C),
			OWL.range(q, D),
			OWL.disjointClasses(C, D)
		);		
		
		assertTrue(reasoner.isEntailed(OWL.disjointProperties(p, OWL.inverse(q))));
		assertTrue(reasoner.isEntailed(OWL.disjointProperties(OWL.inverse(p), q)));
	}
	
	@Test
	/**
	 * Test for the enhancement required in #252
	 */
	public void testBooleanDatatypeConstructors() {		
		OWLDatatype nni = XSD.NON_NEGATIVE_INTEGER;
		OWLDatatype npi = XSD.NON_POSITIVE_INTEGER;
		OWLDatatype ni = XSD.NEGATIVE_INTEGER;
		OWLDatatype pi = XSD.POSITIVE_INTEGER;
		OWLDatatype f = XSD.FLOAT;
		OWLDatatype i = XSD.INTEGER;
		
		createReasoner(
			OWL.declaration(nni),
			OWL.declaration(npi),
			OWL.declaration(ni),
			OWL.declaration(pi),
			OWL.declaration(f),
			OWL.declaration(dq),
			OWL.range(dp, OWL.dataAnd( pi, ni ) ) 
		);
		
		assertTrue( reasoner.isSatisfiable(OWL.some( dq, pi ) ) );
		assertTrue( reasoner.isSatisfiable(OWL.some( dq, OWL.dataNot( pi ) ) ) );
		assertFalse( reasoner.isSatisfiable(OWL.some( dq, OWL.dataAnd( pi, ni ) ) ) );
		assertFalse( reasoner.isSatisfiable(OWL.some( dq, OWL.dataAnd( f, OWL.dataOr( pi, ni ) ) ) ) );
		assertTrue( reasoner.isSatisfiable(OWL.some( dq, OWL.dataAnd( npi, ni ) ) ) );
		assertTrue( reasoner.isSatisfiable(OWL.some( dq, OWL.dataAnd( nni, pi ) ) ) );
		assertTrue( reasoner.isSatisfiable(OWL.some( dq, OWL.dataOr( nni, npi ) ) ) );
		assertTrue( reasoner.isSatisfiable(OWL.some( dq, OWL.dataAnd( nni, npi ) ) ) );
		assertFalse( reasoner.isSatisfiable(OWL.some( dq, OWL.dataAnd( pi, OWL.restrict( i, OWL.maxExclusive( 0 ) ) ) ) ) );
		assertFalse( reasoner.isSatisfiable(OWL.some( dp , XSD.ANY_TYPE) ) );
	}
	
	/**
	 * Test for #447
	 */
	@Test
	public void testGetEquivalentClasses() {	
		createReasoner(
			OWL.equivalentClasses(A, B),
			OWL.equivalentClasses(B, C)
		);
		
		assertEquals(SetUtils.create(A, B, C), reasoner.getEquivalentClasses(A).getEntities());
		assertEquals(SetUtils.create(A, B, C), reasoner.getEquivalentClasses(B).getEntities());
		assertEquals(SetUtils.create(A, B, C), reasoner.getEquivalentClasses(C).getEntities());
	}
	
	@Test
	public void testGetEquivalentObjectProperties() {	
		createReasoner(
			OWL.equivalentProperties(p, q),
			OWL.equivalentProperties(q, r)
		);
		
		assertEquals(SetUtils.create(p, q, r), reasoner.getEquivalentObjectProperties(p).getEntities());
		assertEquals(SetUtils.create(p, q, r), reasoner.getEquivalentObjectProperties(q).getEntities());
		assertEquals(SetUtils.create(p, q, r), reasoner.getEquivalentObjectProperties(r).getEntities());
	}
	
	@Test
	public void testGetEquivalentDataProperties() {	
		createReasoner(
			OWL.equivalentDataProperties(dp, dq),
			OWL.equivalentDataProperties(dq, dr)
		);
		
		assertEquals(SetUtils.create(dp, dq, dr), reasoner.getEquivalentDataProperties(dp).getEntities());
		assertEquals(SetUtils.create(dp, dq, dr), reasoner.getEquivalentDataProperties(dq).getEntities());
		assertEquals(SetUtils.create(dp, dq, dr), reasoner.getEquivalentDataProperties(dr).getEntities());
	}	
	
	/**
	 * Test for #447
	 */
	@Test
	public void testGetUnsatClasses() {	
		createReasoner(
			OWL.disjointClasses(A, B),
			OWL.equivalentClasses(C, OWL.and(A, B))
		);
		
		assertEquals(SetUtils.create(C, OWL.Nothing), reasoner.getUnsatisfiableClasses().getEntities());
		assertEquals(SetUtils.create(C, OWL.Nothing), reasoner.getEquivalentClasses(C).getEntities());
	}
	
	@Test
	public void test454() {		
		OWLOntology ont = loadOntology( base + "ticket-454-test-case.owl" );

		PelletReasoner reasoner = PelletReasonerFactory.getInstance().createReasoner( ont );
		
		assertFalse(reasoner.isConsistent());
	}
	
	@Test
	public void test456() {		
		OWLOntology ont = loadOntology( base + "ticket-456-test-case.owl" );

		PelletReasoner reasoner = PelletReasonerFactory.getInstance().createReasoner( ont );
		
		assertTrue(reasoner.isConsistent());
	}
	
	@Test
	public void testDataDomainWithEquivalents() {		
		createReasoner(
		    OWL.subClassOf(A, min(dp, 1)),           
			OWL.domain(dp, A),
			OWL.subClassOf(A, B)
		);		
		
		assertTrue(reasoner.isEntailed(OWL.domain(dp, A)));
		assertEquals(SetUtils.create(A), reasoner.getDataPropertyDomains(dp, true).getFlattened());
		assertEquals(SetUtils.create(A, B, OWL.Thing), reasoner.getDataPropertyDomains(dp, false).getFlattened());
	}
	
	@Test
	public void testDataDomainWithSubClasses() {		
		createReasoner(
		   	OWL.domain(dp, A),
		   	OWL.subClassOf(A, B)
		);		

		assertTrue(reasoner.isEntailed(OWL.domain(dp, A)));
		assertEquals(SetUtils.create(A), reasoner.getDataPropertyDomains(dp, true).getFlattened());
		assertEquals(SetUtils.create(A, B, OWL.Thing), reasoner.getDataPropertyDomains(dp, false).getFlattened());
	}
	
	@Test
	public void testObjecDomainWithEquivalents() {		
		createReasoner(
		    OWL.subClassOf(A, min(p, 1)),           
			OWL.domain(p, A),
			OWL.domain(p, C),
			OWL.subClassOf(A, B)
		);		
		
		assertTrue(reasoner.isEntailed(OWL.domain(p, A)));
		assertEquals(SetUtils.create(A), reasoner.getObjectPropertyDomains(p, true).getFlattened());
		assertEquals(SetUtils.create(A, B, C, OWL.Thing), reasoner.getObjectPropertyDomains(p, false).getFlattened());
	}
	
	@Test
	public void testObjectDomainWithSubClasses() {		
		createReasoner(
		   	OWL.domain(p, A),
		   	OWL.subClassOf(A, B)
		);		

		assertTrue(reasoner.isEntailed(OWL.domain(p, A)));
		assertEquals(SetUtils.create(A), reasoner.getObjectPropertyDomains(p, true).getFlattened());
		assertEquals(SetUtils.create(A, B, OWL.Thing), reasoner.getObjectPropertyDomains(p, false).getFlattened());
	}
	
	@Test
	public void testObjectRangeWithEquivalents() {		
		createReasoner(
		    OWL.equivalentClasses(C, some(inverse(p), OWL.Thing)),
			OWL.range(p, D),
			OWL.subClassOf(C, E)
		);		
		
		reasoner.getKB().printClassTree();
		
		assertTrue(reasoner.isEntailed(OWL.range(p, C)));
		assertEquals(SetUtils.create(C), reasoner.getEquivalentClasses(some(inverse(p), OWL.Thing)).getEntities());
		assertEquals(SetUtils.create(C), reasoner.getObjectPropertyRanges(p, true).getFlattened());
		assertEquals(SetUtils.create(C, D, E, OWL.Thing), reasoner.getObjectPropertyRanges(p, false).getFlattened());
	}
	
	@Test
	public void testObjectRangeWithSubClasses() {		
		createReasoner(
   			OWL.domain(p, A),
			OWL.range(p, C),
			OWL.range(p, D),
			OWL.subClassOf(C, E)
		);		

		assertTrue(reasoner.isEntailed(OWL.range(p, C)));
		assertEquals(SetUtils.create(C, D), reasoner.getObjectPropertyRanges(p, true).getFlattened());
		assertEquals(SetUtils.create(C, D, E, OWL.Thing), reasoner.getObjectPropertyRanges(p, false).getFlattened());
	}
	
	@Test
	public void testQuotedLiteral() {	
		OWLLiteral literal = OWL.constant("\"test\"");
		
		createReasoner(
   			OWL.propertyAssertion(a, dp, literal)
		);		

		assertTrue(reasoner.isEntailed(OWL.propertyAssertion(a, dp, literal)));
		assertEquals(Collections.singleton(literal), reasoner.getDataPropertyValues(a, dp));
	}
	

	@Test
	public void testComplementRemoval() throws OWLException {
		String ns = "http://test#";

		OWLOntology ont = loadOntology( MiscTests.base + "ticket539.ofn" );

		PelletReasoner reasoner = PelletReasonerFactory.getInstance().createNonBufferingReasoner( ont );

		assertFalse(reasoner.isConsistent());

		OWL.manager.removeAxiom(ont, OWL.subClassOf(OWL.Class(ns+"a_GROUP"), OWL.Class(ns+"a_TEMPORALTHING")));		
		assertFalse(reasoner.isConsistent());
		
		OWL.manager.removeAxiom(ont, OWL.subClassOf(OWL.Class(ns+"a_INDIVIDUAL"), OWL.not(OWL.Class(ns+"a_SETORCOLLECTION"))));
		assertFalse(reasoner.isConsistent());
	}
}
