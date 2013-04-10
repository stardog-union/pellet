// Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public
// License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of
// proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package com.clarkparsia.explanation.test;

import static org.junit.Assume.assumeTrue;

import java.net.URI;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mindswap.pellet.PelletOptions;
import org.mindswap.pellet.utils.SetUtils;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLDatatype;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.SWRLVariable;
import org.semanticweb.owlapi.vocab.SWRLBuiltInsVocabulary;

import com.clarkparsia.owlapiv3.OWL;
import com.clarkparsia.owlapiv3.SWRL;
import com.clarkparsia.owlapiv3.XSD;

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
public abstract class AbstractExplanationTest {
	static final String				BASEPATH	= "file:data/";
	static final OWLOntologyManager	manager		= OWL.manager;
	static final URI				ontologyURI	= URI.create( "http://www.example.org/test#" );

	protected boolean				classify;

	private OWLClass				A, B, C, D, E, F;
	private OWLObjectProperty		p, q, r;
	private OWLDataProperty			dp, dq, dr;
	private OWLIndividual			a, b, c, d, anon1;
	private OWLDatatype				dt;
	private SWRLVariable			x, y;
	private SWRLVariable			dx;

	public AbstractExplanationTest(boolean classify) {
		this.classify = classify;
	}

	@Before
	public void createEntities() {
		A = OWL.Class( ontologyURI + "A" );
		B = OWL.Class( ontologyURI + "B" );
		C = OWL.Class( ontologyURI + "C" );
		D = OWL.Class( ontologyURI + "D" );
		E = OWL.Class( ontologyURI + "E" );
		F = OWL.Class( ontologyURI + "F" );

		p = OWL.ObjectProperty( ontologyURI + "p" );
		q = OWL.ObjectProperty( ontologyURI + "q" );
		r = OWL.ObjectProperty( ontologyURI + "r" );

		dp = OWL.DataProperty( ontologyURI + "dp" );
		dq = OWL.DataProperty( ontologyURI + "dq" );
		dr = OWL.DataProperty( ontologyURI + "dr" );

		a = OWL.Individual( ontologyURI + "a" );
		b = OWL.Individual( ontologyURI + "b" );
		c = OWL.Individual( ontologyURI + "c" );
		d = OWL.Individual( ontologyURI + "d" );
		
		dt = OWL.Datatype( ontologyURI + "dt" );

		anon1 = OWL.AnonymousIndividual( "anon1" );

		x = SWRL.variable( ontologyURI + "x" );
		y = SWRL.variable( ontologyURI + "y" );

		dx = SWRL.variable( ontologyURI + "dx" );
	}

	public void testInconsistencyExplanations(int max, OWLAxiom... explanations) throws Exception {
		testInconsistencyExplanations( max, new OWLAxiom[][] { explanations } );
	}

	public void testInconsistencyExplanations(int max, OWLAxiom[]... explanations) throws Exception {
		testExplanations( OWL.subClassOf(OWL.Thing, OWL.Nothing), max, explanations );
	}

	public void testExplanations(OWLAxiom axiom, int max, OWLAxiom... explanations) throws Exception {
		testExplanations( axiom, max, new OWLAxiom[][] { explanations } );
	}
	
	public void testExplanations(OWLAxiom axiom, int max, OWLAxiom[]... explanations)
			throws Exception {
		Set<Set<OWLAxiom>> explanationSet = new HashSet<Set<OWLAxiom>>();

		for( OWLAxiom[] explanation : explanations ) {
			explanationSet.add( SetUtils.create( explanation ) );
		}

		testExplanations( axiom, max, explanationSet );
	}

	public abstract void testExplanations(OWLAxiom axiom, int max,
			Set<Set<OWLAxiom>> expectedExplanations) throws Exception;

	public abstract void setupGenerators(Collection<OWLAxiom> ontologyAxioms) throws Exception;

	@After
	public void after() {
		for( OWLOntology ont : manager.getOntologies() ) {
	        manager.removeOntology( ont );
        }
	}

	/**
	 * Test that entities appearing in annotations only can still be used in
	 * explanation requests.
	 */
	@Ignore("This test is not valid anymore since annotation subjects do not carry type information")
	@Test
	public void annotationOnlyEntity() throws Exception {
		OWLAxiom[] axioms = {
				OWL.equivalentClasses( A, OWL.Thing ), OWL.comment( B, "Annotation only class" ) };

		setupGenerators( Arrays.asList( axioms ) );
		testExplanations( OWL.subClassOf( B, A ), 0, new OWLAxiom[] { axioms[0] } );
	}

	/**
	 * Test that entities appearing in annotations only can still be used in
	 * explanation requests.
	 */
	@Test
	public void annotationOnlyDuringMUPSEntity() throws Exception {
		OWLAxiom[] axioms = {
				OWL.subClassOf( A, OWL.Thing ), OWL.subClassOf( B, A ),
				OWL.comment( B, "Annotation only class" ) };

		setupGenerators( Arrays.asList( axioms ) );
		testExplanations( OWL.subClassOf( B, A ), 0, new OWLAxiom[] { axioms[1] } );
	}

	/**
	 * Test that anonymous individuals as the object of property assertions are
	 * translated correctly
	 */
	@Test
	public void anonymousIndividualPropertyAssertion() throws Exception {
		assumeTrue( !(this instanceof JenaExplanationTest) );
		
		OWLAxiom[] axioms = {
				OWL.propertyAssertion( a, p, anon1 ), OWL.classAssertion( anon1, A ),
				OWL.subClassOf( OWL.some( p, A ), B ) };

		setupGenerators( Arrays.asList( axioms ) );
		testExplanations( OWL.classAssertion( a, B ), 0, axioms );
	}

	/**
	 * Test for built-in datatype
	 */
	/**
	 * Test that entities appearing in declarations only can still be used in
	 * explanation requests.
	 */
	@Test
	public void declarationOnlyEntity() throws Exception {
		OWLAxiom[] axioms = { OWL.equivalentClasses( A, OWL.Thing ), OWL.declaration( B ) };

		setupGenerators( Arrays.asList( axioms ) );
		testExplanations( OWL.subClassOf( B, A ), 0, new OWLAxiom[] { axioms[0] } );
	}

	/**
	 * Test that entities appearing in declarations only can still be used in
	 * explanation requests (in uninteresting ways).
	 */
	@Test
	public void declarationOnlyIrrelevantEntity() throws Exception {
		OWLAxiom[] axioms = { OWL.subClassOf( B, A ), OWL.declaration( p ) };

		setupGenerators( Arrays.asList( axioms ) );
		testExplanations( OWL.subClassOf( OWL.and( B, OWL.some( p, OWL.Thing ) ), A ), 0,
				new OWLAxiom[] { axioms[0] } );
	}

	@Test
	public void disjointRange() throws Exception {
		OWLAxiom[] axioms = {
				OWL.subClassOf( A, OWL.some( p, B ) ), OWL.range( p, C ),
				OWL.disjointClasses( B, C ) };

		OWLAxiom axiom = OWL.equivalentClasses( A, OWL.Nothing );
		OWLAxiom[] explanation = new OWLAxiom[] {
				OWL.subClassOf( A, OWL.some( p, B ) ), OWL.range( p, C ),
				OWL.disjointClasses( B, C ) };

		setupGenerators( Arrays.asList( axioms ) );
		testExplanations( axiom, 0, explanation );
	}

	@Test
	public void disjointRangeSuper() throws Exception {
		OWLAxiom[] axioms = {
				OWL.subClassOf( A, OWL.some( p, B ) ), OWL.range( p, C ), OWL.subClassOf( B, D ),
				OWL.disjointClasses( D, C ), OWL.subClassOf( A, E ), OWL.subClassOf( B, F ) };

		setupGenerators( Arrays.asList( axioms ) );

		// explain disjointness of B and C first so reasoner will cache this
		// result
		testExplanations( OWL.disjointClasses( B, C ), 0, new OWLAxiom[] { axioms[2], axioms[3] } );

		// explain the unsatisfiability of A and make sure cached results do not
		// interfere with explanation
		testExplanations( OWL.equivalentClasses( A, OWL.Nothing ), 0, new OWLAxiom[] {
				axioms[0], axioms[1], axioms[2], axioms[3] } );
	}

	@Test
	public void disjointSupers() throws Exception {
		OWLAxiom[] axioms = {
				OWL.subClassOf( A, B ), OWL.subClassOf( A, C ), OWL.disjointClasses( B, C ) };

		setupGenerators( Arrays.asList( axioms ) );
		testExplanations( OWL.equivalentClasses( A, OWL.Nothing ), 0, axioms );
	}

	@Test
	public void koalaHardWorkingDomain() throws Exception {
		String ns = "http://protege.stanford.edu/plugins/owl/owl-library/koala.owl#";
		OWLOntology ontology = manager.loadOntology( IRI
				.create( "file:test/data/modularity/koala.owl" ) );
		OWLClass animal = OWL.Class( ns + "Animal" );
		OWLClass person = OWL.Class( ns + "Person" );
		OWLDataProperty hardWorking = OWL.DataProperty( ns + "isHardWorking" );
		setupGenerators( ontology.getAxioms() );
		testExplanations( OWL.domain( hardWorking, animal ), 0, new OWLAxiom[] {
				OWL.subClassOf( person, animal ), OWL.domain( hardWorking, person ) } );
	}

	@Test
	public void multipleEquivalentClasses() throws Exception {
		// test cached explanations
		OWLAxiom[] axioms = {
				OWL.equivalentClasses( A, C ), OWL.subClassOf( A, D ), OWL.subClassOf( D, E ),
				OWL.subClassOf( B, E ), OWL.equivalentClasses( A, B ),
				// the following axiom is to ensure that we are not in
				// EL expressivity because we want to test CD optimized
				// classifier which cached explanations
				OWL.subClassOf( A, OWL.all( p, A ) ) };

		setupGenerators( Arrays.asList( axioms ) );
		testExplanations( OWL.subClassOf( C, D ), 0, new OWLAxiom[] { axioms[0], axioms[1] } );
		testExplanations( OWL.subClassOf( A, D ), 0, new OWLAxiom[] { axioms[1] } );
		testExplanations( OWL.subClassOf( B, D ), 0, new OWLAxiom[] { axioms[1], axioms[4] } );
	}

	@Test
	public void ruleTest1() throws Exception {
		OWLAxiom[] axioms = {
				OWL.classAssertion( a, A ),
				OWL.classAssertion( b, B ),
				OWL.propertyAssertion( a, p, b ),
				SWRL.rule( SWRL.antecedent( SWRL.classAtom( A, x ) ), SWRL.consequent( SWRL
						.classAtom( B, x ) ) ),
				SWRL.rule( SWRL.antecedent( SWRL.classAtom( B, x ), SWRL.propertyAtom( p, x, y ),
						SWRL.classAtom( B, y ) ), SWRL.consequent( SWRL.classAtom( C, x ) ) ) };

		setupGenerators( Arrays.asList( axioms ) );

		testExplanations( OWL.classAssertion( a, A ), 0, new OWLAxiom[] { axioms[0] } );
		testExplanations( OWL.classAssertion( a, B ), 0, new OWLAxiom[] { axioms[0], axioms[3] } );
		testExplanations( OWL.classAssertion( a, C ), 1, axioms );
	}

	@Test
	public void ruleTest1b() throws Exception {
		OWLAxiom[] axioms = {
				OWL.classAssertion( a, A ),
				OWL.classAssertion( b, B ),
				OWL.propertyAssertion( a, p, b ),
				SWRL.rule( ontologyURI + "rule1", true, SWRL.antecedent( SWRL.classAtom( A, x ) ),
						SWRL.consequent( SWRL.classAtom( B, x ) ) ),
				SWRL.rule( ontologyURI + "rule2", SWRL.antecedent( SWRL.classAtom( B, x ), SWRL
						.propertyAtom( p, x, y ), SWRL.classAtom( B, y ) ), SWRL.consequent( SWRL
						.classAtom( C, x ) ) ) };

		setupGenerators( Arrays.asList( axioms ) );

		testExplanations( OWL.classAssertion( a, A ), 0, new OWLAxiom[] { axioms[0] } );
		testExplanations( OWL.classAssertion( a, B ), 0, new OWLAxiom[] { axioms[0], axioms[3] } );
		testExplanations( OWL.classAssertion( a, C ), 1, axioms );
	}

	@Test
	public void ruleTest2() throws Exception {
		OWLAxiom[] axioms = {
				OWL.subPropertyOf( q, p ),
				OWL.propertyAssertion( a, q, b ),
				OWL.classAssertion( b, B ),
				SWRL.rule( SWRL.antecedent( SWRL.propertyAtom( p, x, y ), SWRL.classAtom( B, y ) ),
						SWRL.consequent( SWRL.classAtom( A, x ) ) ) };

		setupGenerators( Arrays.asList( axioms ) );

		testExplanations( OWL.classAssertion( a, A ), 1, axioms );
	}

	@Test
	public void ruleTest3() throws Exception {
		OWLAxiom[] axioms = {
				OWL.inverseProperties( q, p ),
				OWL.propertyAssertion( b, q, a ),
				OWL.classAssertion( b, B ),
				SWRL.rule( SWRL.antecedent( SWRL.propertyAtom( p, x, y ), SWRL.classAtom( B, y ) ),
						SWRL.consequent( SWRL.classAtom( A, x ) ) ) };

		setupGenerators( Arrays.asList( axioms ) );

		testExplanations( OWL.classAssertion( a, A ), 1, axioms );
	}

	@Test
	public void ruleTest4() throws Exception {
		OWLAxiom[] axioms = {
				OWL.classAssertion( a, A ),
				SWRL.rule( SWRL.antecedent( SWRL.classAtom( A, x ) ), SWRL.consequent( SWRL
						.classAtom( B, x ), SWRL.classAtom( C, x ) ) ) };

		setupGenerators( Arrays.asList( axioms ) );

		testExplanations( OWL.classAssertion( a, B ), 1, axioms );
		testExplanations( OWL.classAssertion( a, C ), 1, axioms );
	}

	@Test
	public void ruleTest5() throws Exception {
		OWLAxiom[] axioms = {
				OWL.propertyAssertion( a, p, b ),
				SWRL.rule( SWRL.antecedent( SWRL.propertyAtom( p, x, y ) ), SWRL.consequent( SWRL
						.classAtom( A, x ), SWRL.classAtom( B, y ) ) ) };

		setupGenerators( Arrays.asList( axioms ) );

		testExplanations( OWL.classAssertion( a, A ), 1, axioms );
		testExplanations( OWL.classAssertion( b, B ), 1, axioms );
	}

	@Test
	public void ruleTest6() throws Exception {
		OWLAxiom[] axioms = {
				OWL.propertyAssertion( a, p, b ),
				OWL.classAssertion( b, B ),
				SWRL.rule( SWRL.antecedent( SWRL.classAtom( OWL.some( p, B ), x ) ), SWRL
						.consequent( SWRL.classAtom( C, x ) ) ) };

		setupGenerators( Arrays.asList( axioms ) );

		testExplanations( OWL.classAssertion( a, C ), 1, axioms );
	}

	@Test
	public void ruleBuiltinTest1() throws Exception {
		OWLAxiom[] axioms = {
				OWL.classAssertion( a, A ),
				OWL.propertyAssertion( a, dp, OWL.constant( 9 ) ),
				SWRL.rule( SWRL.antecedent( SWRL.classAtom( A, x ), SWRL.propertyAtom( dp, x, dx ),
						SWRL.lessThan( dx, SWRL.constant( 10 ) ) ), SWRL.consequent( SWRL
						.classAtom( B, x ) ) ) };

		setupGenerators( Arrays.asList( axioms ) );

		testExplanations( OWL.classAssertion( a, B ), 0, axioms );
	}

	@Test
	public void ruleBuiltinTest2() throws Exception {
		OWLAxiom[] axioms = {
				OWL.classAssertion( a, A ),
				OWL.propertyAssertion( a, dp, OWL.constant( 9 ) ),
				SWRL.rule( SWRL.antecedent( SWRL.classAtom( A, x ), SWRL.propertyAtom( dp, x, dx ),
						SWRL.greaterThan( dx, SWRL.constant( 5 ) ), SWRL.lessThan( dx, SWRL
								.constant( 10 ) ) ), SWRL.consequent( SWRL.classAtom( B, x ) ) ) };

		setupGenerators( Arrays.asList( axioms ) );

		testExplanations( OWL.classAssertion( a, B ), 0, axioms );
	}

	@Test
	public void ruleBuiltinTest3() throws Exception {

		SWRLVariable y = SWRL.variable( ontologyURI + "year" );
		SWRLVariable m = SWRL.variable( ontologyURI + "month" );
		SWRLVariable d = SWRL.variable( ontologyURI + "day" );

		OWLDataProperty year = OWL.DataProperty( ontologyURI + "year" );
		OWLDataProperty month = OWL.DataProperty( ontologyURI + "month" );
		OWLDataProperty day = OWL.DataProperty( ontologyURI + "day" );

		OWLAxiom[] axioms = {
				OWL.propertyAssertion( a, dp, OWL.constant( "2009-01-02", XSD.DATE ) ),
				SWRL.rule( SWRL.antecedent( SWRL.propertyAtom( dp, x, dx ), SWRL.builtIn(
						SWRLBuiltInsVocabulary.DATE, dx, y, m, d ) ), SWRL.consequent( SWRL
						.propertyAtom( year, x, y ), SWRL.propertyAtom( month, x, m ), SWRL
						.propertyAtom( day, x, d ) ) ) };

		setupGenerators( Arrays.asList( axioms ) );

		testExplanations( OWL.propertyAssertion( a, year, OWL.constant( 2009 ) ), 1,
				new OWLAxiom[] { axioms[0], axioms[1] } );
	}

	@Test
	public void userDefinedDatatype1() throws Exception {
		OWLAxiom[] axioms = {
				OWL.classAssertion( a, A ),
				OWL.propertyAssertion( a, dp, OWL.constant( 9 ) ),
				OWL.equivalentClasses( B, OWL.and( A, OWL.some( dp, OWL.restrict( XSD.INTEGER, OWL
						.maxExclusive( 10 ) ) ) ) ) };

		setupGenerators( Arrays.asList( axioms ) );

		testExplanations( OWL.classAssertion( a, B ), 0, axioms );
	}

	@Test
	public void userDefinedDatatype2() throws Exception {
		OWLAxiom[] axioms = {
				OWL.classAssertion( a, A ),
				OWL.subClassOf( A, OWL.and( OWL.max( dp, 1 ), OWL.some( dp, OWL.restrict(
						XSD.INTEGER, OWL.minExclusive( 10 ) ) ) ) ),
				OWL.equivalentClasses( B, OWL.and( OWL.min( dp, 1 ), OWL.all( dp, OWL.restrict(
						XSD.INTEGER, OWL.minExclusive( 5 ) ) ) ) ) };

		setupGenerators( Arrays.asList( axioms ) );

		testExplanations( OWL.classAssertion( a, B ), 0, axioms );
	}

	@Test
	public void userDefinedDatatype3() throws Exception {
		OWLAxiom[] axioms = {
				OWL.classAssertion( a, A ),
				OWL.propertyAssertion( a, dp, OWL.constant( 9 ) ),
				OWL.equivalentClasses( B, OWL.and( A, OWL.some( dp, dt ) ) ),
				OWL.datatypeDefinition(dt, OWL.restrict( XSD.INTEGER, OWL
						.maxExclusive( 10 ) ) ) };

		setupGenerators( Arrays.asList( axioms ) );

		testExplanations( OWL.classAssertion( a, B ), 0, axioms );
	}

	@Test
	public void datatypeEnumeration() throws Exception {
		OWLAxiom[] axioms = { 
				OWL.propertyAssertion(a, dp, OWL.constant(1)),
				OWL.propertyAssertion(a, dp, OWL.constant(2)),
		        OWL.equivalentClasses(A, OWL.some(dp, dt)),
		        OWL.datatypeDefinition(dt, OWL.oneOf(OWL.constant(1), OWL.constant(2), OWL.constant(3))) };

		setupGenerators(Arrays.asList(axioms));

		testExplanations(OWL.classAssertion(a, A), 0,  
				new OWLAxiom[] { axioms[0], axioms[2], axioms[3] },  
				new OWLAxiom[] { axioms[1], axioms[2], axioms[3] });
	}
	
	@Test
	public void transitiveProperty() throws Exception {
		OWLAxiom[] axioms = {
				OWL.transitive( p ), OWL.propertyAssertion( a, p, b ),
				OWL.propertyAssertion( b, p, c ), OWL.equivalentClasses( A, OWL.value( p, c ) ) };

		setupGenerators( Arrays.asList( axioms ) );

		testExplanations( OWL.classAssertion( b, A ), 0, new OWLAxiom[] { axioms[2], axioms[3] } );
		testExplanations( OWL.classAssertion( a, A ), 0, axioms );
	}

	@Test
	public void propertyChain1() throws Exception {
		OWLAxiom[] axioms = {
				OWL.subPropertyOf( new OWLObjectProperty[] { p, q }, r ),
				OWL.propertyAssertion( a, p, b ), OWL.propertyAssertion( b, q, c ) };

		setupGenerators( Arrays.asList( axioms ) );

		testExplanations( OWL.propertyAssertion( a, p, b ), 0, new OWLAxiom[] { axioms[1] } );
		testExplanations( OWL.propertyAssertion( a, r, c ), 0, axioms );
	}

	@Test
	public void propertyChain2() throws Exception {
		OWLAxiom[] axioms = {
				OWL.subPropertyOf( new OWLObjectProperty[] { p, q }, r ),
				OWL.propertyAssertion( a, p, b ), OWL.propertyAssertion( b, q, c ),
				OWL.equivalentClasses( A, OWL.some( r, OWL.Thing ) ) };

		setupGenerators( Arrays.asList( axioms ) );

		testExplanations( OWL.classAssertion( a, A ), 0, axioms );
	}

	@Test
	public void inferredSubProperty() throws Exception {
		OWLAxiom[] axioms = {
				OWL.subPropertyOf( p, r ), OWL.subPropertyOf( r, q ),
				OWL.propertyAssertion( a, p, b ) };

		setupGenerators( Arrays.asList( axioms ) );

		testExplanations( OWL.propertyAssertion( a, r, b ), 0, new OWLAxiom[] {
				axioms[0], axioms[2] } );
		testExplanations( OWL.propertyAssertion( a, q, b ), 0, axioms );
	}

	@Test
	public void multipleDatatypeRange() throws Exception {
		OWLAxiom[] axioms = {
				OWL.range( dp, XSD.BYTE ), OWL.range( dp, XSD.NON_POSITIVE_INTEGER ),
				OWL.range( dp, XSD.NON_NEGATIVE_INTEGER ), OWL.subClassOf( A, OWL.min( dp, 1 ) ),
				OWL.classAssertion( a, A ) };

		setupGenerators( Arrays.asList( axioms ) );

		testExplanations( OWL.propertyAssertion( a, dp, OWL.constant( 0 ) ), 0, new OWLAxiom[] {
				axioms[1], axioms[2], axioms[3], axioms[4] } );
	}

	@Test
	public void subPropertiesOfFunctionalDataProperty() throws Exception {
		OWLAxiom[] axioms = {
				OWL.functional( dp ), OWL.subPropertyOf( dq, dp ), OWL.subPropertyOf( dr, dp ),
				OWL.propertyAssertion( a, dq, OWL.constant( 1 ) ),
				OWL.classAssertion( a, OWL.some( dr, XSD.INTEGER ) ) };

		setupGenerators( Arrays.asList( axioms ) );

		testExplanations( OWL.propertyAssertion( a, dp, OWL.constant( 1 ) ), 0, new OWLAxiom[] {
				axioms[1], axioms[3] } );
		testExplanations( OWL.propertyAssertion( a, dr, OWL.constant( 1 ) ), 0, axioms );
	}

	@Test
	public void simpleSubClass() throws Exception {
		// this test case is to check the effect of classification and caching
		// on explanations. the last axiom is to ebsure the EL classifier will
		// not be used
		OWLAxiom[] axioms = {
				OWL.subClassOf( A, B ), OWL.subClassOf( B, C ), OWL.subClassOf( A, OWL.all( p, B ) ) };

		setupGenerators( Arrays.asList( axioms ) );

		testExplanations( OWL.subClassOf( A, C ), 0, axioms[0], axioms[1] );
	}
	
	@Test
	public void simpleType() throws Exception {
		// this test case is to check the effect of realization and caching 
		// on explanations. the last axiom is to ebsure the EL classifier will
		// not be used
		OWLAxiom[] axioms = {
				OWL.classAssertion( a, A ), OWL.subClassOf( A, B ),
				OWL.subClassOf( A, OWL.all( p, B ) ) };

		setupGenerators( Arrays.asList( axioms ) );

		testExplanations( OWL.classAssertion( a, B ), 0, axioms[0], axioms[1] );
	}
	
	@Test
	public void simplePropertyAssertion() throws Exception {
		// this test case is to check the effect of hasObviousPropertyValue
		OWLAxiom[] axioms = {
				OWL.propertyAssertion( a, p, b ) };

		setupGenerators( Arrays.asList( axioms ) );

		testExplanations( OWL.propertyAssertion( a, p, b ), 0, axioms );
	}
	
	@Test
	public void subPropertyAssertion() throws Exception {
		// this test case is to check the effect of hasObviousPropertyValue
		OWLAxiom[] axioms = {
				OWL.propertyAssertion( a, p, b ), OWL.subPropertyOf( p, q ) };

		setupGenerators( Arrays.asList( axioms ) );

		testExplanations( OWL.propertyAssertion( a, q, b ), 0, axioms );
	}
	
	@Test
	public void functionalPropertyInMaxCardinality() throws Exception {
		OWLAxiom[] axioms = {
				OWL.functional( p ), OWL.subClassOf( C, OWL.min( p , 2 ) ) };

		setupGenerators( Arrays.asList( axioms ) );

		testExplanations( OWL.subClassOf( C, OWL.Nothing ), 0, axioms );
	}

	@Test
	public void expressionInDomain() throws Exception {
		OWLAxiom[] axioms = {
				OWL.subClassOf( A, OWL.some( p, B ) ), OWL.domain( p, OWL.or(C, D) ),
				OWL.disjointClasses( A, C ) };

		setupGenerators( Arrays.asList( axioms ) );
		testExplanations( OWL.subClassOf( A, D ), 0, axioms );
	}
	
	@Test
	public void expressionInRange() throws Exception {
		OWLAxiom[] axioms = {
				OWL.subClassOf( A, OWL.some( p, B ) ), OWL.range( p, OWL.or(C, D) ),
				OWL.disjointClasses( B, C ), OWL.disjointClasses( B, D ) };

		setupGenerators( Arrays.asList( axioms ) );
		testExplanations( OWL.subClassOf( A, OWL.Nothing ), 0, axioms );
	}
	
	@Test
	public void differentFromAndFunctionality() throws Exception {
		assumeTrue(!classify);
		
		OWLAxiom[] axioms = { 
				OWL.functional(p), OWL.propertyAssertion(a, p, b), OWL.propertyAssertion(a, p, c),
		        OWL.propertyAssertion(a, p, d), OWL.differentFrom(b, c), OWL.differentFrom(c, d) };

		setupGenerators( Arrays.asList( axioms ) );

		testInconsistencyExplanations( 0, new OWLAxiom[] { axioms[0],
			axioms[1], axioms[2], axioms[4]}, new OWLAxiom[] { axioms[0],
			axioms[2], axioms[3], axioms[5]} );
	}
}
