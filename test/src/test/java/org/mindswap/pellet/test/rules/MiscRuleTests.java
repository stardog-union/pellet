// Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package org.mindswap.pellet.test.rules;

import static com.clarkparsia.pellet.utils.TermFactory.TOP;
import static com.clarkparsia.pellet.utils.TermFactory.all;
import static com.clarkparsia.pellet.utils.TermFactory.and;
import static com.clarkparsia.pellet.utils.TermFactory.hasValue;
import static com.clarkparsia.pellet.utils.TermFactory.literal;
import static com.clarkparsia.pellet.utils.TermFactory.max;
import static com.clarkparsia.pellet.utils.TermFactory.min;
import static com.clarkparsia.pellet.utils.TermFactory.not;
import static com.clarkparsia.pellet.utils.TermFactory.oneOf;
import static com.clarkparsia.pellet.utils.TermFactory.or;
import static com.clarkparsia.pellet.utils.TermFactory.some;
import static com.clarkparsia.pellet.utils.TermFactory.term;
import static com.clarkparsia.pellet.utils.TermFactory.value;
import static java.util.Collections.singleton;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mindswap.pellet.test.PelletTestCase.assertIteratorValues;
import static org.mindswap.pellet.utils.Namespaces.SWRLB;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import junit.framework.JUnit4TestAdapter;

import org.junit.Test;
import org.mindswap.pellet.DependencySet;
import org.mindswap.pellet.KnowledgeBase;
import org.mindswap.pellet.PelletOptions;
import org.mindswap.pellet.jena.PelletReasonerFactory;
import org.mindswap.pellet.test.PelletTestSuite;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLObjectSomeValuesFrom;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.SWRLAtom;
import org.semanticweb.owlapi.model.SWRLVariable;

import aterm.ATermAppl;

import com.clarkparsia.owlapiv3.OWL;
import com.clarkparsia.owlapiv3.SWRL;
import com.clarkparsia.pellet.datatypes.Datatypes;
import com.clarkparsia.pellet.owlapiv3.PelletReasoner;
import com.clarkparsia.pellet.rules.VariableUtils;
import com.clarkparsia.pellet.rules.model.AtomDConstant;
import com.clarkparsia.pellet.rules.model.AtomDVariable;
import com.clarkparsia.pellet.rules.model.AtomIConstant;
import com.clarkparsia.pellet.rules.model.AtomIObject;
import com.clarkparsia.pellet.rules.model.AtomIVariable;
import com.clarkparsia.pellet.rules.model.BuiltInAtom;
import com.clarkparsia.pellet.rules.model.ClassAtom;
import com.clarkparsia.pellet.rules.model.DatavaluedPropertyAtom;
import com.clarkparsia.pellet.rules.model.DifferentIndividualsAtom;
import com.clarkparsia.pellet.rules.model.IndividualPropertyAtom;
import com.clarkparsia.pellet.rules.model.Rule;
import com.clarkparsia.pellet.rules.model.RuleAtom;
import com.clarkparsia.pellet.rules.model.SameIndividualAtom;
import com.clarkparsia.pellet.rules.rete.AlphaNode;
import com.clarkparsia.pellet.rules.rete.BetaNode;
import com.clarkparsia.pellet.rules.rete.TermTuple;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;

public class MiscRuleTests {

	public final static String	base		= "file:" + PelletTestSuite.base + "swrl-test/misc/";
	private static final IRI	luigiFamily	= IRI
													.create( "http://www.csc.liv.ac.uk/~luigi/ontologies/basicFamily" );

	public static junit.framework.Test suite() {
		return new JUnit4TestAdapter( MiscRuleTests.class );
	}

	private void nonTrivialBuiltInTest() {
		KnowledgeBase kb = new KnowledgeBase();

		ATermAppl d1 = literal( "1", Datatypes.INT ), d2 = 
				literal( "2", Datatypes.INT ), d12 = literal( "3", Datatypes.INTEGER ), 
				i = term( "i" ), p = term( "p" ), q = term( "q" ), r = term( "r" );

		kb.addDatatypeProperty( p );
		kb.addDatatypeProperty( q );
		kb.addDatatypeProperty( r );

		kb.addIndividual( i );

		kb.addSubClass( TOP, hasValue( p, d1 ) );
		kb.addSubClass( TOP, hasValue( q, d2 ) );

		AtomIVariable x = new AtomIVariable( "x" );
		AtomDVariable z1 = new AtomDVariable( "z1" );
		AtomDVariable z2 = new AtomDVariable( "z2" );
		AtomDVariable z3 = new AtomDVariable( "z3" );

		List<RuleAtom> body = new ArrayList<RuleAtom>();
		body.add( new DatavaluedPropertyAtom( p, x, z1 ) );
		body.add( new DatavaluedPropertyAtom( q, x, z2 ) );
		body.add( new BuiltInAtom( SWRLB + "add", z3, z1, z2 ) );

		List<RuleAtom> head = new ArrayList<RuleAtom>();
		head.add( new DatavaluedPropertyAtom( r, x, z3 ) );

		kb.addRule( new Rule( head, body ) );

		kb.realize();
		assertTrue( kb.hasPropertyValue( i, r, d12 ) );

	}

	@Test
	public void betaNodeMarking() {
		TermTuple t = new TermTuple( DependencySet.INDEPENDENT );
		AlphaNode a = new AlphaNode( t );

		BetaNode b1 = new BetaNode( a, a, false );
		BetaNode b2 = new BetaNode( a, a, false );
		BetaNode b3 = new BetaNode( b1, b2, false );
		BetaNode b4 = new BetaNode( b3, b3, false );

		assertTrue( b1.isDirty() );
		assertTrue( b2.isDirty() );
		assertTrue( b3.isDirty() );
		assertTrue( b4.isDirty() );

		b1.join();
		b2.join();
		b3.join();
		b4.join();

		assertFalse( b1.isDirty() );
		assertFalse( b2.isDirty() );
		assertFalse( b3.isDirty() );
		assertFalse( b4.isDirty() );

		a.markDirty();

		assertTrue( b1.isDirty() );
		assertTrue( b2.isDirty() );
		assertTrue( b3.isDirty() );
		assertTrue( b4.isDirty() );

	}

	@Test
	public void builtInDateTime() {
		KnowledgeBase kb = new KnowledgeBase();

		ATermAppl amy = term( "Amy" ), basil = term( "Basil" ), clara = term( "Clara" ), desmond = term( "Desmond" );

		ATermAppl bDate = term( "bDate" ), bYear = term( "bYear" ), bMonth = term( "bMonth" ), bDay = term( "bDay" ), bTZ = term( "bTZ" );

		kb.addDatatypeProperty( bDate );
		kb.addDatatypeProperty( bYear );
		kb.addDatatypeProperty( bMonth );
		kb.addDatatypeProperty( bDay );
		kb.addDatatypeProperty( bTZ );

		kb.addIndividual( amy );
		kb.addPropertyValue( bDate, amy, literal( "2001-01-11", Datatypes.DATE ) );

		kb.addIndividual( basil );
		kb.addPropertyValue( bDate, basil, literal( "2002-02-12Z", Datatypes.DATE ) );

		kb.addIndividual( clara );
		kb.addPropertyValue( bYear, clara, literal( "2003", Datatypes.INTEGER ) );
		kb.addPropertyValue( bMonth, clara, literal( "3", Datatypes.INTEGER ) );
		kb.addPropertyValue( bDay, clara, literal( "13", Datatypes.INTEGER ) );

		kb.addIndividual( desmond );
		kb.addPropertyValue( bYear, desmond, literal( "2004", Datatypes.INTEGER ) );
		kb.addPropertyValue( bMonth, desmond, literal( "4", Datatypes.INTEGER ) );
		kb.addPropertyValue( bDay, desmond, literal( "14", Datatypes.INTEGER ) );
		kb.addPropertyValue( bTZ, desmond, literal( "+01:01" ) );

		AtomIVariable x = new AtomIVariable( "x" );
		AtomDVariable xDate = new AtomDVariable( "xDate" ), xYear = new AtomDVariable( "xYear" ), xMonth = new AtomDVariable(
				"xMonth" ), xDay = new AtomDVariable( "xDay" ), xTZ = new AtomDVariable( "xTZ" );

		RuleAtom dateBuiltIn = new BuiltInAtom( SWRLB + "date", xDate, xYear, xMonth, xDay );
		RuleAtom dateBuiltInTZ = new BuiltInAtom( SWRLB + "date", xDate, xYear, xMonth, xDay, xTZ );
		RuleAtom bDateAtom = new DatavaluedPropertyAtom( bDate, x, xDate );
		RuleAtom bYearAtom = new DatavaluedPropertyAtom( bYear, x, xYear );
		RuleAtom bMonthAtom = new DatavaluedPropertyAtom( bMonth, x, xMonth );
		RuleAtom bDayAtom = new DatavaluedPropertyAtom( bDay, x, xDay );
		RuleAtom bTZAtom = new DatavaluedPropertyAtom( bTZ, x, xTZ );

		Rule fromDate = new Rule( Arrays
				.asList( new RuleAtom[] { bYearAtom, bMonthAtom, bDayAtom } ), Arrays
				.asList( new RuleAtom[] { dateBuiltIn, bDateAtom } ) );
		kb.addRule( fromDate );

		Rule fromDateTZ = new Rule( Arrays.asList( new RuleAtom[] {
				bYearAtom, bMonthAtom, bDayAtom, bTZAtom } ), Arrays.asList( new RuleAtom[] {
				dateBuiltInTZ, bDateAtom } ) );
		kb.addRule( fromDateTZ );

		Rule toDate = new Rule( Arrays.asList( new RuleAtom[] { bDateAtom } ), Arrays
				.asList( new RuleAtom[] { dateBuiltIn, bYearAtom, bMonthAtom, bDayAtom } ) );
		kb.addRule( toDate );

		Rule toDateTZ = new Rule( Arrays.asList( new RuleAtom[] { bDateAtom } ),
				Arrays.asList( new RuleAtom[] {
						dateBuiltInTZ, bYearAtom, bMonthAtom, bDayAtom, bTZAtom } ) );
		kb.addRule( toDateTZ );

		assertTrue( kb.isConsistent() );

		assertTrue( kb.hasPropertyValue( amy, bYear, literal( "2001", Datatypes.INTEGER ) ) );
		assertTrue( kb.hasPropertyValue( amy, bMonth, literal( "1", Datatypes.INTEGER ) ) );
		assertTrue( kb.hasPropertyValue( amy, bDay, literal( "11", Datatypes.INTEGER ) ) );

		assertTrue( kb.hasPropertyValue( basil, bYear, literal( "2002", Datatypes.INTEGER ) ) );
		assertTrue( kb.hasPropertyValue( basil, bMonth, literal( "2", Datatypes.INTEGER ) ) );
		assertTrue( kb.hasPropertyValue( basil, bDay, literal( "12", Datatypes.INTEGER ) ) );
		assertTrue( kb.hasPropertyValue( basil, bTZ, literal( "Z" ) ) );

		assertTrue( kb.hasPropertyValue( clara, bDate, literal( "2003-03-13", Datatypes.DATE ) ) );

		assertTrue( kb.hasPropertyValue( desmond, bDate, literal( "2004-04-14+01:01", Datatypes.DATE ) ) );

	}

	@Test
	public void builtInMath() {
		KnowledgeBase kb = new KnowledgeBase();

		ATermAppl d1 = literal( "1", Datatypes.INT ), d2 = 
				literal( "1.5", Datatypes.FLOAT ), dif11 = literal(
				"0", Datatypes.INTEGER ), dif12 = literal( "-0.5", Datatypes.FLOAT ), dif21 = 
				literal( "0.5", Datatypes.FLOAT ), dif22 = literal(
				"0", Datatypes.FLOAT ), prod11 = literal( "1", Datatypes.INTEGER ), prod12 = 
				literal( "1.5", Datatypes.FLOAT ), prod22 = literal(
				"2.25", Datatypes.FLOAT ), quot11 = 
				literal( "1", Datatypes.DECIMAL ), quot12 = literal(
				Float.toString( (float)(1.0 / 1.5) ), Datatypes.FLOAT ), quot21 = 
				literal( "1.5", Datatypes.FLOAT ), quot22 = literal(
				"1", Datatypes.FLOAT ), sum11 = literal( "2", Datatypes.INTEGER ), sum12 = 
				literal( "2.5", Datatypes.FLOAT ), sum22 = literal(
				"3.0", Datatypes.FLOAT ), i = term( "i" ), p = 
				term( "p" ), sum = term( "sum" ), product = 
				term( "product" ), difference = term( "difference" ), quotient = 
				term( "quotient" );

		kb.addDatatypeProperty( p );
		kb.addDatatypeProperty( sum );
		kb.addDatatypeProperty( difference );
		kb.addDatatypeProperty( product );
		kb.addDatatypeProperty( quotient );

		kb.addIndividual( i );
		kb.addPropertyValue( p, i, d1 );
		kb.addPropertyValue( p, i, d2 );

		AtomIVariable x = new AtomIVariable( "x" );
		AtomDVariable z1 = new AtomDVariable( "z1" );
		AtomDVariable z2 = new AtomDVariable( "z2" );
		AtomDVariable z3 = new AtomDVariable( "z3" );
		AtomDVariable z4 = new AtomDVariable( "z4" );
		AtomDVariable z5 = new AtomDVariable( "z5" );
		AtomDVariable z6 = new AtomDVariable( "z6" );

		List<RuleAtom> body = new ArrayList<RuleAtom>();
		body.add( new DatavaluedPropertyAtom( p, x, z1 ) );
		body.add( new DatavaluedPropertyAtom( p, x, z2 ) );
		body.add( new BuiltInAtom( SWRLB + "add", z3, z1, z2 ) );
		body.add( new BuiltInAtom( SWRLB + "subtract", z4, z1, z2 ) );
		body.add( new BuiltInAtom( SWRLB + "multiply", z5, z1, z2 ) );
		body.add( new BuiltInAtom( SWRLB + "divide", z6, z1, z2 ) );

		List<RuleAtom> head = new ArrayList<RuleAtom>();
		head.add( new DatavaluedPropertyAtom( sum, x, z3 ) );
		head.add( new DatavaluedPropertyAtom( difference, x, z4 ) );
		head.add( new DatavaluedPropertyAtom( product, x, z5 ) );
		head.add( new DatavaluedPropertyAtom( quotient, x, z6 ) );

		Rule rule = new Rule( head, body );
		kb.addRule( rule );

		kb.realize();
		assertTrue( kb.hasPropertyValue( i, sum, sum11 ) );
		assertTrue( kb.hasPropertyValue( i, sum, sum12 ) );
		assertTrue( kb.hasPropertyValue( i, sum, sum22 ) );
		assertTrue( kb.hasPropertyValue( i, difference, dif11 ) );
		assertTrue( kb.hasPropertyValue( i, difference, dif12 ) );
		assertTrue( kb.hasPropertyValue( i, difference, dif21 ) );
		assertTrue( kb.hasPropertyValue( i, difference, dif22 ) );
		assertTrue( kb.hasPropertyValue( i, product, prod11 ) );
		assertTrue( kb.hasPropertyValue( i, product, prod12 ) );
		assertTrue( kb.hasPropertyValue( i, product, prod22 ) );
		assertTrue( kb.hasPropertyValue( i, quotient, quot11 ) );
		assertTrue( kb.hasPropertyValue( i, quotient, quot12 ) );
		assertTrue( kb.hasPropertyValue( i, quotient, quot21 ) );
		assertTrue( kb.hasPropertyValue( i, quotient, quot22 ) );

	}

	@Test
	public void builtInNonTrivialOldStrategy() {
		boolean whichStrategy = PelletOptions.USE_CONTINUOUS_RULES;
		PelletOptions.USE_CONTINUOUS_RULES = false;
		try {
			nonTrivialBuiltInTest();
		} finally {
			PelletOptions.USE_CONTINUOUS_RULES = whichStrategy;
		}

	}

	@Test
	public void builtInNonTrivialNewStrategy() {
		boolean whichStrategy = PelletOptions.USE_CONTINUOUS_RULES;
		PelletOptions.USE_CONTINUOUS_RULES = true;
		try {
			nonTrivialBuiltInTest();
		} finally {
			PelletOptions.USE_CONTINUOUS_RULES = whichStrategy;
		}
	}

	/**
	 * Simple property chain test. Mostly tests the rete engine
	 */
	@Test
	public void dataPropertyChain1() {
		KnowledgeBase kb = new KnowledgeBase();

		ATermAppl d = literal( "d" ), i = term( "i" ), j = 
				term( "j" ), k = term( "k" ), p = 
				term( "p" ), r = term( "r" );

		kb.addDatatypeProperty( p );
		kb.addObjectProperty( r );

		kb.addIndividual( i );
		kb.addIndividual( j );
		kb.addIndividual( k );

		kb.addPropertyValue( p, i, d );
		kb.addPropertyValue( r, i, j );
		kb.addPropertyValue( r, j, k );

		AtomIVariable x = new AtomIVariable( "x" ), y = new AtomIVariable( "y" );
		AtomDVariable z = new AtomDVariable( "z" );

		RuleAtom body1 = new IndividualPropertyAtom( r, x, y );
		RuleAtom body2 = new DatavaluedPropertyAtom( p, x, z ), head = new DatavaluedPropertyAtom(
				p, y, z );

		Rule rule = new Rule( Collections.singleton( head ), Arrays.asList( new RuleAtom[] {
				body1, body2 } ) );
		kb.addRule( rule );

		kb.realize();
		assertTrue( kb.hasPropertyValue( j, p, d ) );
		assertTrue( kb.hasPropertyValue( k, p, d ) );
	}

	/**
	 * More complicated property chain test. Tests the rule strategy
	 */
	@Test
	public void dataPropertyChain2() {
		KnowledgeBase kb = new KnowledgeBase();

		ATermAppl d = literal( "d" ), i = term( "i" ), j = 
				term( "j" ), k = term( "k" ), p = 
				term( "p" ), r = term( "r" );

		kb.addDatatypeProperty( p );
		kb.addObjectProperty( r );

		kb.addIndividual( i );
		kb.addIndividual( j );
		kb.addIndividual( k );

		kb.addSubClass( TOP, oneOf( i, j, k ) );
		kb.addSubClass( TOP, min( r, 3, TOP ) );

		kb.addPropertyValue( p, i, d );

		AtomIVariable x = new AtomIVariable( "x" ), y = new AtomIVariable( "y" );
		AtomDVariable z = new AtomDVariable( "z" );

		RuleAtom body1 = new IndividualPropertyAtom( r, x, y );
		RuleAtom body2 = new DatavaluedPropertyAtom( p, x, z ), head = new DatavaluedPropertyAtom(
				p, y, z );

		Rule rule = new Rule( Collections.singleton( head ), Arrays.asList( new RuleAtom[] {
				body1, body2 } ) );
		kb.addRule( rule );

		kb.realize();
		assertTrue( kb.hasPropertyValue( j, p, d ) );
		assertTrue( kb.hasPropertyValue( k, p, d ) );
	}

	@Test
	public void inferredProperties() throws Exception {
		ATermAppl d = literal( "foo" ), i = term( "i" ), j = term( "j" ), k = term( "k" ), p = term( "p" ), r = term( "r" );

		KnowledgeBase kb = new KnowledgeBase();
		kb.addIndividual( i );
		kb.addIndividual( j );
		kb.addIndividual( k );
		kb.addDatatypeProperty( p );
		kb.addObjectProperty( r );
		kb.addSubClass( TOP, min( r, 3, TOP ) );
		kb.addSubClass( TOP, or( value( i ), value( j ), value( k ) ) );
		kb.addPropertyValue( p, i, d );

		AtomIVariable x = new AtomIVariable( "x" ), y = new AtomIVariable( "y" );
		AtomDVariable z = new AtomDVariable( "z" );

		RuleAtom head = new DatavaluedPropertyAtom( p, x, z );
		RuleAtom body1 = new DatavaluedPropertyAtom( p, y, z );
		RuleAtom body2 = new IndividualPropertyAtom( r, x, y );

		Rule rule = new Rule( Collections.singleton( head ), Arrays.asList( body1, body2 ) );
		kb.addRule( rule );

		assertTrue( kb.hasPropertyValue( j, p, d ) );
		assertTrue( kb.hasPropertyValue( k, p, d ) );

	}

	@Test
	public void testRuleIndividuals() throws Exception {
		ATermAppl c = term( "C" ), d = term( "D" ), i = 
				term( "i" );

		KnowledgeBase kb = new KnowledgeBase();
		kb.addClass( c );
		kb.addClass( d );
		kb.addIndividual( i );
		kb.addType( i, c );

		kb.addRule( new Rule( Arrays.asList( new RuleAtom[] { new ClassAtom( d, new AtomIConstant(
				i ) ) } ), Arrays
				.asList( new RuleAtom[] { new ClassAtom( c, new AtomIConstant( i ) ) } ) ) );

		kb.realize();
		assertTrue( kb.getTypes( i ).contains( Collections.singleton( d ) ) );

	}

	@Test
	public void testRuleEquality() {
		ATermAppl r = term( "r" );
		ATermAppl i = term( "i" );
		ATermAppl j = term( "j" );

		AtomIObject x = new AtomIVariable( "x" );
		AtomIObject y = new AtomIVariable( "y" );

		KnowledgeBase kb = new KnowledgeBase();
		kb.addIndividual( i );
		kb.addIndividual( j );
		kb.addObjectProperty( r );
		kb.addSubClass( TOP, min( r, 1, TOP ) );
		kb.addSubClass( TOP, oneOf( i, j ) );

		kb.addRule( new Rule( Collections.singletonList( new DifferentIndividualsAtom( x, y ) ),
				Collections.singletonList( new IndividualPropertyAtom( r, x, y ) ) ) );

		kb.realize();
		assertTrue( kb.isConsistent() );
		assertTrue( kb.isDifferentFrom( i, j ) );
	}

	public void testLuigiFamilyJena() throws Exception {
		OntModel ontModel = ModelFactory.createOntologyModel( PelletReasonerFactory.THE_SPEC, null );
		ontModel.read( base + "basicFamilyReference.owl" );
		ontModel.read( base + "basicFamilyRules.owl" );

		Resource nella = ontModel.createResource( luigiFamily.resolve( "#Nella" ).toString() );
		Property hasUncle = ontModel.createProperty( luigiFamily.resolve( "#hasUncle" ).toString() );
		Resource dino = ontModel.createResource( luigiFamily.resolve( "#Dino" ).toString() );
		assertFalse( ontModel.contains( nella, hasUncle, dino ) );

	}

	public void testLuigiFamilyOWLApi() throws Exception {

		OWLOntologyManager manager = OWL.manager;
		OWLOntology familyRef = manager.loadOntology( IRI.create( base + "basicFamilyReference.owl" ) );
		OWLOntology familyRules = manager.loadOntology( IRI.create( base + "basicFamilyRules.owl" ) );
		
		final Set<OWLAxiom> axioms = new HashSet<OWLAxiom>();
		axioms.addAll( familyRef.getAxioms() );
		axioms.addAll( familyRules.getAxioms() );
		
		OWLOntology mergedOntology = OWL.Ontology( axioms );
		
		PelletReasoner reasoner = com.clarkparsia.pellet.owlapiv3.PelletReasonerFactory.getInstance().createReasoner( mergedOntology );

		OWLIndividual nella = OWL.Individual( luigiFamily.resolve( "#Nella" ) );
		OWLObjectProperty hasUncle = OWL.ObjectProperty( luigiFamily.resolve( "#hasUncle" ) );
		OWLIndividual dino = OWL.Individual( luigiFamily.resolve( "#Dino" ) );

		assertFalse( reasoner.isEntailed( OWL.propertyAssertion( nella, hasUncle, dino ) ) );

	}

	public void testUncleRule() {
		ATermAppl hasParent = term( "hasParent" ), hasSibling = 
				term( "hasSibling" ), hasUncle = term( "hasUncle" ), male = 
				term( "Male" );

		ATermAppl c11 = term( "c11" ), c12 = term( "c12" ), p1a = term( "p1a" ), p2a = term( "p2a" );

		KnowledgeBase kb = new KnowledgeBase();
		kb.addClass( male );
		kb.addObjectProperty( hasParent );
		kb.addObjectProperty( hasSibling );
		kb.addObjectProperty( hasUncle );

		kb.addIndividual( c11 );
		kb.addIndividual( c12 );
		kb.addIndividual( p1a );
		kb.addIndividual( p2a );

		kb.addPropertyValue( hasParent, c11, p1a );
		kb.addPropertyValue( hasSibling, c11, c12 );
		kb.addPropertyValue( hasParent, c12, p1a );
		kb.addPropertyValue( hasSibling, c12, c11 );
		kb.addPropertyValue( hasSibling, p1a, p2a );
		kb.addType( p2a, male );

		AtomIVariable x = new AtomIVariable( "x" ), y = new AtomIVariable( "y" ), z = new AtomIVariable(
				"z" );

		kb.addRule( new Rule( Arrays.asList( new RuleAtom[] { new IndividualPropertyAtom( hasUncle,
				y, z ) } ), Arrays.asList( new RuleAtom[] {
				new IndividualPropertyAtom( hasParent, y, x ), new ClassAtom( male, z ),
				new IndividualPropertyAtom( hasSibling, x, z ), } ) ) );

		assertIteratorValues( kb.getObjectPropertyValues( hasUncle, c11 ).iterator(),
				new Object[] { p2a, } );
	}

	public void testVariableUtils1() {
		AtomIVariable var1 = new AtomIVariable( "var1" ), var2 = new AtomIVariable( "var2" );
		RuleAtom atom = new SameIndividualAtom( var1, var2 );
		assertIteratorValues( VariableUtils.getVars( atom ).iterator(), new Object[] { var1, var2 } );
	}

	public void testVariableUtils2() {
		ATermAppl p = term( "p" );
		AtomIVariable var1 = new AtomIVariable( "var1" ), var2 = new AtomIVariable( "var2" );
		RuleAtom atom = new IndividualPropertyAtom( p, var1, var2 );
		assertIteratorValues( VariableUtils.getVars( atom ).iterator(), new Object[] { var1, var2 } );
	}

	/**
	 * This test created to verify that facts added to RETE before a clash, but
	 * not affected by the restore remain in the rete. Known to be a problem for
	 * USE_CONTINUOUS_RULES=true at r711
	 */
	@Test
	public void reteRestoreTest1() {
		ATermAppl a, b, c, x, y, p, q, A, B, C, G, H;
		KnowledgeBase kb = new KnowledgeBase();

		a = term( "a" );
		b = term( "b" );
		c = term( "c" );
		x = term( "x" );
		y = term( "y" );

		p = term( "p" );
		q = term( "q" );

		A = term( "A" );
		B = term( "B" );
		C = term( "C" );
		G = term( "G" );
		H = term( "H" );

		kb.addIndividual( a );
		kb.addIndividual( b );
		kb.addIndividual( c );
		kb.addIndividual( x );
		kb.addIndividual( y );

		kb.addObjectProperty( p );
		kb.addObjectProperty( q );

		kb.addClass( A );
		kb.addClass( B );
		kb.addClass( C );
		kb.addClass( G );
		kb.addClass( H );

		kb.addType( a, A );
		kb.addType( b, B );
		kb.addType( c, C );
		kb.addType( x, or( G, all( q, not( H ) ) ) );
		kb.addType( x, max( p, 2, TOP ) );
		kb.addType( y, H );
		kb.addPropertyValue( p, x, a );
		kb.addPropertyValue( p, x, b );
		kb.addPropertyValue( p, x, c );

		{
			AtomIVariable v = new AtomIVariable( "v" );
			RuleAtom body = new ClassAtom( some( p, or( and( A, B ), or(
					and( A, C ), and( B, C ) ) ) ), v );
			RuleAtom head = new IndividualPropertyAtom( q, v, new AtomIConstant( y ) );
			Rule rule = new Rule( Collections.singleton( head ), Collections.singleton( body ) );
			kb.addRule( rule );
		}

		{
			AtomIVariable v = new AtomIVariable( "v" );
			RuleAtom body = new ClassAtom( G, v );
			RuleAtom head = new IndividualPropertyAtom( p, v, new AtomIConstant( y ) );
			Rule rule = new Rule( Collections.singleton( head ), Collections.singleton( body ) );
			kb.addRule( rule );
		}

		assertTrue( kb.isConsistent() );
		assertIteratorValues( kb.getPropertyValues( p, x ).iterator(), new ATermAppl[] { a, b, c, y } );
		assertEquals( Collections.singletonList( y ), kb.getPropertyValues( q, x ) );
		assertFalse( kb.hasPropertyValue( x, q, c ) );
	}

	@Test
	public void testQualifiedCardinality() {
		// This test case is to test the inferences regarding qualified
		// cardinality restrictions in the presence of rules (see ticket 105)

		// the point of this test is test if the choose-rule is working
		// properly in the presence of rules. the rule we use in this
		// test case is completely irrelevant for the test. we are
		// adding the rule just to force the selection of XXXRuleStrategy.
		// we are using a qualified min cardinality restriction where the
		// qualification itself is also a defined concept. this setup ensures
		// that we need the application of choose rule to get the correct
		// entailments

		KnowledgeBase kb = new KnowledgeBase();

		ATermAppl C = term( "C" );
		ATermAppl D = term( "D" );
		ATermAppl E = term( "E" );

		ATermAppl a = term( "a" );
		ATermAppl b = term( "b" );
		ATermAppl c = term( "c" );

		ATermAppl p = term( "p" );

		kb.addClass( C );
		kb.addClass( D );
		kb.addClass( E );

		kb.addIndividual( a );
		kb.addIndividual( b );
		kb.addIndividual( c );

		// kb.addType( a, D );
		kb.addType( b, C );
		kb.addType( c, C );

		kb.addEquivalentClass( D, min( p, 2, E ) );
		kb.addEquivalentClass( E, some( p, C ) );

		kb.addObjectProperty( p );

		kb.addPropertyValue( p, a, b );
		kb.addPropertyValue( p, a, c );
		kb.addPropertyValue( p, b, b );
		kb.addPropertyValue( p, c, c );

		kb.addDifferent( b, c );

		{
			AtomIVariable x = new AtomIVariable( "x" );
			List<RuleAtom> body = new ArrayList<RuleAtom>();
			body.add( new ClassAtom( C, x ) );
			List<RuleAtom> head = new ArrayList<RuleAtom>();
			head.add( new ClassAtom( C, x ) );
			Rule rule = new Rule( head, body );
			kb.addRule( rule );
		}

		assertTrue( kb.isConsistent() );

		assertTrue( kb.isType( a, D ) );
		assertTrue( kb.isType( b, E ) );
		assertTrue( kb.isType( c, E ) );
	}
	
	@Test
	public void reteRestoreTest2() {

		// This test case is to test if restore/backtrack in RuleBranch 
		// modifies the original ABox (see ticket 302)

		KnowledgeBase kb = new KnowledgeBase();

		ATermAppl C = term( "C" );
		ATermAppl D = term( "D" );

		ATermAppl a = term( "a" );
		ATermAppl b = term( "b" );

		kb.addClass( C );
		kb.addClass( D );

		kb.addIndividual( a );
		kb.addIndividual( b );

		kb.addType( b, C );

		{
			AtomIVariable x = new AtomIVariable( "x" );
			List<RuleAtom> body = new ArrayList<RuleAtom>();
			body.add( new ClassAtom( C, x ) );
			List<RuleAtom> head = new ArrayList<RuleAtom>();
			head.add( new ClassAtom( D, x ) );
			Rule rule = new Rule( head, body );
			kb.addRule( rule );
		}

		assertTrue( kb.isConsistent() );

		// a is not inferred to be D since it is not C. as a result of
		// the initial consistency check not(C) is added to a
		assertFalse( kb.isType( a, D ) );
		// asking a != b adds a = b to a copy of the ABox and checks for
		// consistency. adding a = b causes a clash since b is a D. as a
		// result of that clash D(x) should be added to the copy. the bug
		// described in #302 causes this t be added to the original KB 
		assertFalse( kb.isDifferentFrom( a, b ) );
		// when we ask this query again if D(a) is added to the original
		// KB we will incorrectly conclude the instance relation holds
		assertFalse( kb.isType( a, D ) );
	
	}

	@Test
	public void testEmptyRuleHead1() {
		KnowledgeBase kb = new KnowledgeBase();

		ATermAppl i = term( "i" );
		ATermAppl C = term( "C" );
		AtomIVariable x = new AtomIVariable( "x" );

		kb.addClass( C );
		kb.addIndividual( i );
		kb.addType( i, C );

		List<RuleAtom> body = new ArrayList<RuleAtom>();
		List<RuleAtom> head = new ArrayList<RuleAtom>();

		body.add( new ClassAtom( C, x ) );

		kb.addRule( new Rule( head, body ) );

		assertFalse( kb.isConsistent() );
	}

	@Test
	public void testEmptyRuleHead2() {
		KnowledgeBase kb = new KnowledgeBase();

		ATermAppl i = term( "i" );
		ATermAppl C = term( "C" );
		ATermAppl D = term( "D" );
		AtomIVariable x = new AtomIVariable( "x" );

		kb.addClass( C );
		kb.addClass( D );
		kb.addSubClass( C, D );
		kb.addIndividual( i );
		kb.addType( i, C );

		List<RuleAtom> body = new ArrayList<RuleAtom>();
		List<RuleAtom> head = new ArrayList<RuleAtom>();

		body.add( new ClassAtom( C, x ) );

		kb.addRule( new Rule( head, body ) );

		assertFalse( kb.isConsistent() );
	}

	@Test
	public void testEmptyRuleHead3() {
		KnowledgeBase kb = new KnowledgeBase();

		ATermAppl i = term( "i" );
		ATermAppl C = term( "C" );
		ATermAppl D = term( "D" );
		AtomIVariable x = new AtomIVariable( "x" );

		kb.addClass( C );
		kb.addClass( D );
		kb.addSubClass( C, D );
		kb.addIndividual( i );
		kb.addType( i, D );

		List<RuleAtom> body = new ArrayList<RuleAtom>();
		List<RuleAtom> head = new ArrayList<RuleAtom>();

		body.add( new ClassAtom( D, x ) );

		kb.addRule( new Rule( head, body ) );

		assertFalse( kb.isConsistent() );
	}

	@Test
	public void testEmptyRuleHead4() {
		KnowledgeBase kb = new KnowledgeBase();

		ATermAppl i = term( "i" );
		ATermAppl R = term( "R" );
		ATermAppl l = literal( "l" );
		AtomIVariable v = new AtomIVariable( "v" );
		AtomDConstant c = new AtomDConstant( l );

		kb.addIndividual( i );
		kb.addDatatypeProperty( R );
		kb.addPropertyValue( R, i, l );

		List<RuleAtom> body = new ArrayList<RuleAtom>();
		List<RuleAtom> head = new ArrayList<RuleAtom>();

		body.add( new DatavaluedPropertyAtom( R, v, c ) );

		kb.addRule( new Rule( head, body ) );

		assertFalse( kb.isConsistent() );
	}

	@Test
	public void testEmptyRuleHead5() {
		KnowledgeBase kb = new KnowledgeBase();

		ATermAppl i = term( "i" );
		ATermAppl C = term( "C" );
		ATermAppl D = term( "D" );
		ATermAppl CuD = or( C, D );
		AtomIVariable x = new AtomIVariable( "x" );

		kb.addClass( C );
		kb.addClass( D );
		kb.addClass( CuD );
		kb.addIndividual( i );
		kb.addType( i, C );

		List<RuleAtom> body = new ArrayList<RuleAtom>();
		List<RuleAtom> head = new ArrayList<RuleAtom>();

		body.add( new ClassAtom( CuD, x ) );

		kb.addRule( new Rule( head, body ) );

		assertFalse( kb.isConsistent() );
	}

	@Test
	public void testTransitiveProperty() {
		KnowledgeBase kb = new KnowledgeBase();

		ATermAppl a = term( "a" );
		ATermAppl b = term( "b" );
		ATermAppl c = term( "c" );
		ATermAppl p = term( "p" );
		ATermAppl q = term( "q" );

		AtomIVariable x = new AtomIVariable( "x" );
		AtomIVariable y = new AtomIVariable( "y" );

		kb.addObjectProperty( p );
		kb.addObjectProperty( q );
		kb.addTransitiveProperty( p );
		
		kb.addIndividual( a );
		kb.addIndividual( b );
		kb.addIndividual( c );
		kb.addPropertyValue( p, a, b );
		kb.addPropertyValue( p, b, c );

		List<RuleAtom> body = new ArrayList<RuleAtom>();
		List<RuleAtom> head = new ArrayList<RuleAtom>();

		body.add( new IndividualPropertyAtom( p, x, y ) );
		head.add( new IndividualPropertyAtom( q, x, y ) );

		kb.addRule( new Rule( head, body ) );

		assertTrue( kb.hasPropertyValue( a, p, b ) );
		assertTrue( kb.hasPropertyValue( a, p, c ) );
		assertTrue( kb.hasPropertyValue( b, p, c ) );

		assertTrue( kb.hasPropertyValue( a, q, b ) );
		assertTrue( kb.hasPropertyValue( a, q, c ) );
		assertTrue( kb.hasPropertyValue( b, q, c ) );

		Map<ATermAppl, List<ATermAppl>> results = kb.getPropertyValues( p );
		assertIteratorValues( results.get( b ).iterator(), new ATermAppl[] { c } );
		assertIteratorValues( results.get( a ).iterator(), new ATermAppl[] { b, c } );

		results = kb.getPropertyValues( q );
		assertIteratorValues( results.get( b ).iterator(), new ATermAppl[] { c } );
		assertIteratorValues( results.get( a ).iterator(), new ATermAppl[] { b, c } );
	}
	

	@Test
	public void testUnsafeVariable() {

		// This test case is to test if restore/backtrack in RuleBranch 
		// modifies the original ABox (see ticket 302)

		KnowledgeBase kb = new KnowledgeBase();

		ATermAppl C = term( "C" );
		ATermAppl p = term( "p" );

		ATermAppl a = term( "a" );
		ATermAppl b = term( "b" );

		kb.addClass( C );
		kb.addObjectProperty( p );

		kb.addIndividual( a );
		kb.addIndividual( b );

		kb.addEquivalentClass( C, some( p, TOP ) );
		
		AtomIVariable x = new AtomIVariable( "x" );
		AtomIVariable y = new AtomIVariable( "y" );
		List<RuleAtom> body = new ArrayList<RuleAtom>();
		body.add( new ClassAtom( C, x ) );
		List<RuleAtom> head = new ArrayList<RuleAtom>();
		head.add( new IndividualPropertyAtom( p, x, y ) );
		Rule rule = new Rule( head, body );
		kb.addRule( rule );
		
		assertEquals( singleton( rule ), kb.getRules() );	
		assertNull( kb.getNormalizedRules().get( rule ) );

		assertTrue( kb.isConsistent() );		
		assertFalse( kb.isType( a, C ) );		
	}
		
	@Test
	public void reflexiveRule() {
		KnowledgeBase kb = new KnowledgeBase();

		ATermAppl A = term( "A" );		
		
		ATermAppl p = term( "p" );
		
		ATermAppl a = term( "a" );
		ATermAppl b = term( "b" );
		ATermAppl c = term( "c" );
		
		kb.addClass( A );
				
		kb.addObjectProperty( p );
		
		kb.addIndividual( a );
		kb.addIndividual( b );
		kb.addIndividual( c );
		
		kb.addPropertyValue( p, a, a );
		kb.addPropertyValue( p, b, a );
		kb.addPropertyValue( p, b, c );
		
		AtomIVariable x = new AtomIVariable( "x" );	
		List<RuleAtom> body = Arrays.<RuleAtom>asList( new IndividualPropertyAtom( p, x, x ) );
		List<RuleAtom> head = Arrays.<RuleAtom>asList( new ClassAtom( A, x ) );
	
		kb.addRule( new Rule( head, body ) );
	
		assertTrue( kb.isConsistent() );
		
		assertTrue( kb.isType( a, A ) );
		assertFalse( kb.isType( b, A ) );
		assertFalse( kb.isType( c, A ) );
	}
	
	@Test
	public void propertyAtomWithAConstant() {
		KnowledgeBase kb = new KnowledgeBase();

		ATermAppl A = term( "A" );		
		
		ATermAppl p = term( "p" );
		
		ATermAppl a = term( "a" );
		ATermAppl b = term( "b" );
		ATermAppl c = term( "c" );
		
		kb.addClass( A );
				
		kb.addObjectProperty( p );
		
		kb.addIndividual( a );
		kb.addIndividual( b );
		kb.addIndividual( c );
		
		kb.addPropertyValue( p, a, b );
		kb.addPropertyValue( p, b, c );
		kb.addPropertyValue( p, c, c );
		
		AtomIVariable x = new AtomIVariable( "x" );	
		List<RuleAtom> body = Arrays.<RuleAtom>asList( new IndividualPropertyAtom( p, x, new AtomIConstant( c ) ) );
		List<RuleAtom> head = Arrays.<RuleAtom>asList( new ClassAtom( A, x ) );
	
		kb.addRule( new Rule( head, body ) );
	
		assertTrue( kb.isConsistent() );
		
		assertFalse( kb.isType( a, A ) );
		assertTrue( kb.isType( b, A ) );
		assertTrue( kb.isType( c, A ) );
	}
	
	@Test
	public void complexConsequent() {
		Set<SWRLAtom> bodyAtoms = new HashSet<SWRLAtom>();
		Set<SWRLAtom> headAtoms = new HashSet<SWRLAtom>();

		OWLIndividual individualA = OWL.Individual( "a" );
		OWLClassExpression classC = OWL.Class( "C" );
		OWLClassExpression classD = OWL.Class( "D" );
		OWLClassExpression classE = OWL.Class( "E" );
		OWLObjectProperty propertyP = OWL.ObjectProperty( "p" );
		SWRLVariable variable = SWRL.variable( "x" );

		OWLObjectSomeValuesFrom restriction = OWL.some( propertyP, classD );

		// C(?x)
		bodyAtoms.add( SWRL.classAtom( classC, variable ) );
		// Ep.D(?x)
		headAtoms.add( SWRL.classAtom( restriction, variable ) );

		OWLAxiom[] axioms = new OWLAxiom[] {
				// C(?x) -> Ep.D(?x)
			SWRL.rule( bodyAtoms, headAtoms ),
			// E = Ep.D
			OWL.equivalentClasses( classE, restriction ),
			// C(a)
			OWL.classAssertion( individualA, classC ),
			// E(a)
			OWL.classAssertion( individualA, classE ) };

		OWLOntology ontology = OWL.Ontology( axioms );

		PelletReasoner reasoner = com.clarkparsia.pellet.owlapiv3.PelletReasonerFactory
				.getInstance().createNonBufferingReasoner( ontology );

		assertTrue( reasoner.isConsistent() );
		assertTrue( reasoner.isEntailed( OWL.classAssertion( individualA, classE ) ) );

		OWL.manager.removeOntology( ontology );
	}

	@Test
	public void testDifferentFromInBody() throws Exception {
		OntModel ontModel = ModelFactory.createOntologyModel( PelletReasonerFactory.THE_SPEC, null );
		ontModel.read( base + "sibling-rule.n3", "TTL" );

		Resource alice = ontModel.createResource( "family:alice" );
		Property sibling = ontModel.createProperty( "family:sibling" );
		Resource bob = ontModel.createResource( "family:bob" );
		assertTrue( ontModel.contains( alice, sibling, bob ) );
		assertTrue( ontModel.contains( bob, sibling, alice ) );

		assertEquals( Collections.singletonList(bob), ontModel.listObjectsOfProperty( alice, sibling ).toList() );
		assertEquals( Collections.singletonList(alice), ontModel.listObjectsOfProperty( bob, sibling ).toList() );
	}
}
