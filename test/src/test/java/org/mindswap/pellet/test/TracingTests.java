// Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public
// License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of
// proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package org.mindswap.pellet.test;

import static com.clarkparsia.pellet.utils.TermFactory.TOP;
import static com.clarkparsia.pellet.utils.TermFactory.all;
import static com.clarkparsia.pellet.utils.TermFactory.and;
import static com.clarkparsia.pellet.utils.TermFactory.list;
import static com.clarkparsia.pellet.utils.TermFactory.literal;
import static com.clarkparsia.pellet.utils.TermFactory.max;
import static com.clarkparsia.pellet.utils.TermFactory.minInclusive;
import static com.clarkparsia.pellet.utils.TermFactory.not;
import static com.clarkparsia.pellet.utils.TermFactory.oneOf;
import static com.clarkparsia.pellet.utils.TermFactory.or;
import static com.clarkparsia.pellet.utils.TermFactory.restrict;
import static com.clarkparsia.pellet.utils.TermFactory.some;
import static com.clarkparsia.pellet.utils.TermFactory.term;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mindswap.pellet.test.PelletTestCase.assertIteratorValues;
import static org.mindswap.pellet.utils.ATermUtils.makeList;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import junit.framework.JUnit4TestAdapter;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mindswap.pellet.KnowledgeBase;
import org.mindswap.pellet.PelletOptions;
import org.mindswap.pellet.utils.ATermUtils;
import org.mindswap.pellet.utils.SetUtils;

import aterm.ATerm;
import aterm.ATermAppl;
import aterm.ATermList;

import com.clarkparsia.pellet.datatypes.Datatypes;
import com.clarkparsia.pellet.datatypes.types.real.XSDInteger;
import com.clarkparsia.pellet.rules.RulesToATermTranslator;
import com.clarkparsia.pellet.rules.model.AtomIVariable;
import com.clarkparsia.pellet.rules.model.ClassAtom;
import com.clarkparsia.pellet.rules.model.IndividualPropertyAtom;
import com.clarkparsia.pellet.rules.model.Rule;
import com.clarkparsia.pellet.rules.model.RuleAtom;
import com.hp.hpl.jena.vocabulary.XSD;

public class TracingTests extends AbstractKBTests {

	public static junit.framework.Test suite() {
		return new JUnit4TestAdapter( TracingTests.class );
	}

	private final ATermAppl		bob	= ATermUtils.makeTermAppl( "Bob" ), robert = ATermUtils
										.makeTermAppl( "Robert" ), mary = ATermUtils
										.makeTermAppl( "Mary" ), victor = ATermUtils
										.makeTermAppl( "Victor" ), email = ATermUtils
										.makeTermAppl( "MaryAndBob@example.com" ),
			mbox = ATermUtils.makeTermAppl( "mbox" ), relative = ATermUtils
					.makeTermAppl( "relative" ), sibling = ATermUtils.makeTermAppl( "sibling" ),
			person = ATermUtils.makeTermAppl( "person" ),
			human = ATermUtils.makeTermAppl( "human" ), ssn = ATermUtils.makeTermAppl( "ssn" );

	private boolean			old_USE_TRACING;

	@Override
    @Before
	public void initializeKB() {
		super.initializeKB();
		
		old_USE_TRACING = PelletOptions.USE_TRACING;
		PelletOptions.USE_TRACING = true;
		kb.setDoExplanation( true );
	}

	@Override
    @After
	public void disposeKB() {
		super.disposeKB();
		
		PelletOptions.USE_TRACING = old_USE_TRACING;
	}
	
	public void explainInconsistency(ATermAppl... expected) {
		assertFalse( kb.isConsistent() );

		Set<ATermAppl> actual = kb.getExplanationSet();
		
		assertEquals( SetUtils.create( expected ), actual );
	}
	
	public void explainEntailment(boolean entailment, ATermAppl... expected) {
		assertTrue( entailment );

		Set<ATermAppl> actual = kb.getExplanationSet();
		
		assertEquals( SetUtils.create( expected ), actual );
	}

	@Test
	public void testAsymmetric() {
		objectProperties( p );
		individuals( a, b );
				
		kb.addAsymmetricProperty( p );

		kb.addPropertyValue( p, a, b );
		kb.addPropertyValue( p, b, a );

		explainInconsistency(
			ATermUtils.makeAsymmetric( p ),
			ATermUtils.makePropAtom( p, a, b ),
			ATermUtils.makePropAtom( p, b, a )
		);
	}

	@Test
	public void testBottomSatisfiable() {
		kb.addClass( human );

		explainEntailment( !kb.isSatisfiable( ATermUtils.makeAnd( ATermUtils.makeNot( human ),
				ATermUtils.BOTTOM ) ) );
	}

	@Test
	public void testCourse() {
		ATermAppl Course = term( "Person" );
		ATermAppl Person = term( "Course" );
		ATermAppl Man = term( "Man" );
		ATermAppl Woman = term( "Woman" );

		ATermAppl isTaughtBy = term( "isTaughtBy" );

		ATermAppl M1 = term( "M1" );
		ATermAppl W1 = term( "W1" );
		ATermAppl C1 = term( "C1" );
		ATermAppl P1 = term( "P1" );
		ATermAppl C2 = term( "C2" );

		kb.addClass( Course );
		kb.addClass( Person );
		kb.addClass( Man );
		kb.addClass( Woman );
		kb.addDisjointClass( Man, Woman );

		kb.addObjectProperty( isTaughtBy );
		kb.addFunctionalProperty( isTaughtBy );

		kb.addIndividual( C1 );
		kb.addIndividual( P1 );
		kb.addIndividual( M1 );
		kb.addIndividual( M1 );
		kb.addIndividual( W1 );
		kb.addIndividual( C2 );

		kb.addType( C1, Course );
		kb.addPropertyValue( isTaughtBy, C1, M1 );
		kb.addPropertyValue( isTaughtBy, C1, P1 );

		kb.addType( C2, Course );
		kb.addPropertyValue( isTaughtBy, C2, W1 );
		kb.addPropertyValue( isTaughtBy, C2, P1 );

		kb.addType( M1, Man );
		kb.addType( W1, Woman );
		kb.addType( P1, Person );

		explainInconsistency( 
			ATermUtils.makeTypeAtom( M1, Man ),
			ATermUtils.makePropAtom( isTaughtBy, C1, M1 ),
			ATermUtils.makePropAtom( isTaughtBy, C1, P1 ),
			ATermUtils.makeTypeAtom( W1, Woman ),
			ATermUtils.makePropAtom( isTaughtBy, C2, W1 ),
			ATermUtils.makePropAtom( isTaughtBy, C2, P1 ),
			ATermUtils.makeFunctional( isTaughtBy ),
			ATermUtils.makeDisjoint( Man, Woman )
		);
	}

	/**
	 * Test explanations for bad datatypes. Not implemented, known to fail.
	 */
	@Test
	public void testDatatypeStatement() {
		kb.addDatatypeProperty( ssn );
		kb.addIndividual( robert );

		ATermAppl ssn1 = ATermUtils.makeTypedLiteral( "bob", XSD.nonNegativeInteger.toString() );
		kb.addPropertyValue( ssn, robert, ssn1 );
		assertFalse( kb.isConsistent() );

		Set<ATermAppl> explanation = kb.getExplanationSet();
		
		assertTrue( explanation.contains( ATermUtils.makePropAtom( ssn, robert, ssn1 ) ) );
	}

	@Test
	public void testDisjunction() {
		classes( A, B );
		objectProperties( p );
		individuals( a, b );
		
		kb.addType( a, A );
		kb.addPropertyValue( p, a, b );
		kb.addType( a, or( not( A ), all( p, B ) ) );

		explainEntailment( kb.isType( b, B ),
			ATermUtils.makeTypeAtom( a, A ),
			ATermUtils.makePropAtom( p, a, b ), 
			ATermUtils.makeTypeAtom( a, or( not( A ), all( p, B ) ) )
		);
	}

	@Test
	public void testDomain() {
		classes( A, B );
		objectProperties( p );
		individuals( a, b );
		
		kb.addDomain( p, A );
		kb.addType( a, not( A ) );
		kb.addPropertyValue( p, a, b );

		explainInconsistency( 
			ATermUtils.makeDomain( p, A ),
			ATermUtils.makeTypeAtom( a, not( A ) ),
			ATermUtils.makePropAtom( p, a, b )  
		);
	}
	
	@Test
	public void testDomainRangeInverse() {
		classes( A );
		objectProperties( p );
		individuals( a, b );
		
		kb.addDomain( p, A );
		kb.addRange( p, not( A ) );
		kb.addInverseProperty( p, p );
		kb.addPropertyValue( p, a, b );

		explainInconsistency(
			ATermUtils.makeDomain( p, A ),
			ATermUtils.makeRange( p, not( A ) ),
			ATermUtils.makeInvProp( p, p ),
			ATermUtils.makePropAtom( p, a, b )
		);
	}

	@Test
	public void testDomainRangeSymmetric() {
		classes( A );
		objectProperties( p );
		individuals( a, b );
		
		kb.addDomain( p, A );
		kb.addRange( p, not( A ) );
		kb.addSymmetricProperty( p );
		kb.addPropertyValue( p, a, b );

		explainInconsistency(
			ATermUtils.makeDomain( p, A ),
			ATermUtils.makeRange( p, not( A ) ),
			ATermUtils.makeSymmetric( p ),
			ATermUtils.makePropAtom( p, a, b )
		);
	}

	@Test
	public void testEquivalentClass() {
		classes( A, B );

		kb.addSubClass( A, B );
		kb.addSubClass( B, A );

		explainEntailment( kb.isEquivalentClass( A, B ),
			ATermUtils.makeSub( A, B ), 
			ATermUtils.makeSub( B, A )
		);
	}

	@Test
	public void testFunctionalDataProp2() {
		kb.addDatatypeProperty( ssn );
		kb.addFunctionalProperty( ssn );
		kb.addIndividual( robert );

		ATermAppl ssn1 = ATermUtils.makePlainLiteral( "012345678" );
		ATermAppl ssn2 = ATermUtils.makePlainLiteral( "123456789" );

		kb.addPropertyValue( ssn, robert, ssn1 );
		kb.addPropertyValue( ssn, robert, ssn2 );

		assertFalse( kb.isConsistent() );

		Set<ATermAppl> explanation = kb.getExplanationSet();
		
		assertIteratorValues( explanation.iterator(), new Object[] {
				ATermUtils.makePropAtom( ssn, robert, ssn1 ),
				ATermUtils.makePropAtom( ssn, robert, ssn2 ), ATermUtils.makeFunctional( ssn ), } );
		assertTrue( explanation.size() == 3 );
	}

	@Test
	public void testFunctionalDataProp1() {
		ATermAppl C = term( "C" );
		ATermAppl D = XSDInteger.getInstance().getName();
		ATermAppl p = term( "p" );
		ATermAppl a = term( "a" );
		ATermAppl b = literal( "012345678", Datatypes.INTEGER );
		
		kb.addClass( C );
		kb.addClass( D );
		kb.addDatatypeProperty( p );
		kb.addIndividual( a );
		
		kb.addEquivalentClass( C, all( p, D ) );

		kb.addFunctionalProperty( p );
		
		kb.addPropertyValue( p, a, b );

		assertTrue( kb.isConsistent() );
		
		assertTrue( kb.isType( a, C ) );

		Set<ATermAppl> explanation = kb.getExplanationSet();
		
		assertIteratorValues( explanation.iterator(), new Object[] {
				ATermUtils.makeEqClasses( C, all( p, D ) ), 
				ATermUtils.makeFunctional( p ),
				ATermUtils.makePropAtom( p, a, b )  } );
	}
	

	@Test
	public void testFunctionalObjectProp1() {
		ATermAppl C = term( "C" );
		ATermAppl D = term( "D" );
		ATermAppl p = term( "p" );
		ATermAppl a = term( "a" );
		ATermAppl b = term( "b" );
		
		kb.addClass( C );
		kb.addClass( D );
		kb.addObjectProperty( p );
		kb.addIndividual( a );
		kb.addIndividual( b );
		
		kb.addEquivalentClass( C, all( p, D ) );

		kb.addFunctionalProperty( p );
		
		kb.addPropertyValue( p, a, b );
		kb.addType( b, D );

		assertTrue( kb.isConsistent() );
		
		assertTrue( kb.isType( a, C ) );

		Set<ATermAppl> explanation = kb.getExplanationSet();
		
		assertIteratorValues( explanation.iterator(), new Object[] {
				ATermUtils.makeEqClasses( C, all( p, D ) ), 
				ATermUtils.makeFunctional( p ),
				ATermUtils.makePropAtom( p, a, b ),
				ATermUtils.makeTypeAtom( b, D )  } );
	}
	
	@Test
	public void testInverseFunctionalDataProp() {
		ATermList different = ATermUtils.makeList( robert ).insert( mary );
		System.out.println( "Different: " + different );
		kb.addObjectProperty( mbox );
		kb.addInverseFunctionalProperty( mbox );
		kb.addIndividual( robert );
		kb.addIndividual( mary );
		kb.addIndividual( email );
		kb.addAllDifferent( different );

		kb.addPropertyValue( mbox, robert, email );
		kb.addPropertyValue( mbox, mary, email );

		assertFalse( kb.isConsistent() );

		Set<ATermAppl> explanation = kb.getExplanationSet();
		// System.out.println(explanation);
		assertIteratorValues( explanation.iterator(),
				new Object[] {
						ATermUtils.makePropAtom( mbox, robert, email ),
						ATermUtils.makePropAtom( mbox, mary, email ),
						ATermUtils.makeInverseFunctional( mbox ),
						ATermUtils.makeAllDifferent( different ), } );
	}

	@Test
	public void testIrreflexive() {
		kb.addObjectProperty( mbox );
		kb.addIrreflexiveProperty( mbox );
		kb.addIndividual( robert );
		kb.addPropertyValue( mbox, robert, robert );

		assertFalse( kb.isConsistent() );

		Set<ATermAppl> explanation = kb.getExplanationSet();
		assertIteratorValues( explanation.iterator(), new Object[] {
				ATermUtils.makeIrreflexive( mbox ),
				ATermUtils.makePropAtom( mbox, robert, robert ), } );
	}

	@Test
	public void testMaxOneDataProp() {
		kb.addClass( person );
		kb.addDatatypeProperty( ssn );
		ATermAppl max1ssn = ATermUtils.makeMax( ssn, 1, ATermUtils.TOP_LIT );
		kb.addSubClass( person, max1ssn );
		kb.addSubClass( person, ATermUtils.makeMin( ssn, 1, ATermUtils.TOP_LIT ) );
		kb.addIndividual( robert );
		kb.addType( robert, person );

		ATermAppl ssn1 = ATermUtils.makePlainLiteral( "012345678" );
		ATermAppl ssn2 = ATermUtils.makePlainLiteral( "123456789" );

		kb.addPropertyValue( ssn, robert, ssn1 );
		kb.addPropertyValue( ssn, robert, ssn2 );

		assertFalse( kb.isConsistent() );

		Set<ATermAppl> explanation = kb.getExplanationSet();
		
		assertIteratorValues( explanation.iterator(), new Object[] {
				ATermUtils.makePropAtom( ssn, robert, ssn1 ),
				ATermUtils.makePropAtom( ssn, robert, ssn2 ),
				ATermUtils.makeSub( person, max1ssn ), ATermUtils.makeTypeAtom( robert, person ), } );
	}

	@Test
	public void testRange() {
		ATermAppl notPerson = ATermUtils.makeNot( person );

		kb.addClass( person );
		kb.addObjectProperty( sibling );
		kb.addRange( sibling, person );
		kb.addIndividual( robert );
		kb.addIndividual( victor );
		kb.addType( victor, notPerson );
		kb.addPropertyValue( sibling, robert, victor );

		assertFalse( kb.isConsistent() );

		Set<ATermAppl> explanation = kb.getExplanationSet();

		assertIteratorValues( explanation.iterator(), new Object[] {
				ATermUtils.makeRange( sibling, person ),
				ATermUtils.makeTypeAtom( victor, notPerson ),
				ATermUtils.makePropAtom( sibling, robert, victor ), } );
	}

	@Test
	public void testReflexive() {
		ATermAppl notPerson = ATermUtils.makeNot( person );
		ATermAppl bobsType = ATermUtils.makeAllValues( relative, notPerson );
		kb.addClass( person );
		kb.addObjectProperty( relative );
		kb.addReflexiveProperty( relative );
		kb.addIndividual( robert );
		kb.addType( robert, person );
		kb.addType( robert, bobsType );
		kb.addIndividual( victor );
		kb.addType( victor, notPerson );

		// kb.addPropertyValue(relative, robert, victor);

		assertFalse( kb.isConsistent() );
		Set<ATermAppl> explanation = kb.getExplanationSet();
		assertIteratorValues( explanation.iterator(), new Object[] {
				ATermUtils.makeReflexive( relative ), ATermUtils.makeTypeAtom( robert, person ),
				ATermUtils.makeTypeAtom( robert, bobsType ),
		// ATermUtils.makePropAtom(relative, robert, victor),
				} );

	}

	@Test
	public void testSameAllDifferent() {
		kb.addIndividual( robert );
		kb.addIndividual( bob );
		kb.addIndividual( mary );

		ATermList list = ATermUtils.makeList( robert );
		list = ATermUtils.makeList( bob, list );
		list = ATermUtils.makeList( mary, list );
		kb.addAllDifferent( list );
		kb.addSame( robert, bob );

		assertFalse( kb.isConsistent() );

		Set<ATermAppl> explanation = kb.getExplanationSet();
		assertTrue( explanation.contains( ATermUtils.makeSameAs( robert, bob ) ) );
		assertTrue( explanation.contains( ATermUtils.makeAllDifferent( list ) ) );
		assertTrue( explanation.size() == 2 );

	}

	@Test
	public void testSameDifferent() {
		kb.addIndividual( robert );
		kb.addIndividual( bob );
		kb.addSame( robert, bob );
		kb.addDifferent( robert, bob );
		assertFalse( kb.isConsistent() );

		Set<ATermAppl> explanation = kb.getExplanationSet();
		assertTrue( explanation.contains( ATermUtils.makeSameAs( robert, bob ) ) );
		assertTrue( explanation.contains( ATermUtils.makeDifferent( robert, bob ) ) );
		assertTrue( explanation.size() == 2 );

	}

	@Test
	public void testSubProp1() {
		ATermAppl noRelatives = ATermUtils.makeMax( relative, 0, ATermUtils.TOP );
		kb.addIndividual( mary );
		kb.addIndividual( bob );
		kb.addObjectProperty( relative );
		kb.addObjectProperty( sibling );
		kb.addSubProperty( sibling, relative );
		kb.addType( bob, noRelatives );
		kb.addPropertyValue( sibling, bob, mary );

		assertFalse( kb.isConsistent() );

		Set<ATermAppl> explanation = kb.getExplanationSet();
		assertIteratorValues( explanation.iterator(), new Object[] {
				ATermUtils.makeSubProp( sibling, relative ),
				ATermUtils.makePropAtom( sibling, bob, mary ),
				ATermUtils.makeTypeAtom( bob, noRelatives ), } );

	}

	@Test
	public void testSubProp2() {
		ATermAppl nonHumanRelatives = ATermUtils.makeAllValues( relative, ATermUtils
				.makeNot( person ) );
		kb.addIndividual( mary );
		kb.addIndividual( bob );
		kb.addObjectProperty( relative );
		kb.addObjectProperty( sibling );
		kb.addSubProperty( sibling, relative );
		kb.addType( bob, nonHumanRelatives );
		kb.addType( mary, person );
		kb.addPropertyValue( sibling, bob, mary );

		assertFalse( kb.isConsistent() );

		Set<ATermAppl> explanation = kb.getExplanationSet();
		assertIteratorValues( explanation.iterator(), new Object[] {
				ATermUtils.makeSubProp( sibling, relative ),
				ATermUtils.makePropAtom( sibling, bob, mary ),
				ATermUtils.makeTypeAtom( bob, nonHumanRelatives ),
				ATermUtils.makeTypeAtom( mary, person ), } );

	}

	@Test
	public void testTopBottom() {
		kb = new KnowledgeBase();
		kb.addSubClass( ATermUtils.TOP, ATermUtils.BOTTOM );

		assertFalse( kb.isConsistent() );
		Set<ATermAppl> explanation = kb.getExplanationSet();
		assertIteratorValues( explanation.iterator(), new Object[] { ATermUtils.makeSub(
				ATermUtils.TOP, ATermUtils.BOTTOM ), } );
	}

	@Test
	public void testTransitive() {
		kb.addObjectProperty( sibling );
		kb.addTransitiveProperty( sibling );

		// ATermList different = ATermUtils.makeList(new ATermAppl[] { mary,
		// robert, victor} );
		kb.addIndividual( robert );
		kb.addIndividual( mary );
		kb.addIndividual( victor );
		// kb.addAllDifferent(different);

		// ATermAppl oneSibling = ATermUtils.makeMax(sibling, 1,
		// ATermUtils.TOP);
		ATermAppl notVictorsSibling = ATermUtils
				.makeNot( ATermUtils.makeHasValue( sibling, victor ) );
		kb.addType( robert, notVictorsSibling );

		kb.addPropertyValue( sibling, robert, mary );
		kb.addPropertyValue( sibling, mary, victor );

		assertFalse( kb.isConsistent() );
		Set<ATermAppl> explanation = kb.getExplanationSet();
		
		assertIteratorValues( explanation.iterator(), new Object[] {
				// ATermUtils.makeAllDifferent(different),
				ATermUtils.makeTypeAtom( robert, notVictorsSibling ),
				ATermUtils.makeTransitive( sibling ),
				ATermUtils.makePropAtom( sibling, robert, mary ),
				ATermUtils.makePropAtom( sibling, mary, victor ), } );

	}

	@Test
	public void testRuleExplanation() {
		KnowledgeBase kb = new KnowledgeBase();

		ATermAppl C = ATermUtils.makeTermAppl( "C" );
		ATermAppl D = ATermUtils.makeTermAppl( "D" );
		ATermAppl i = ATermUtils.makeTermAppl( "i" );

		List<RuleAtom> body = new ArrayList<RuleAtom>();
		List<RuleAtom> head = new ArrayList<RuleAtom>();

		body.add( new ClassAtom( C, new AtomIVariable( "x" ) ) );
		head.add( new ClassAtom( D, new AtomIVariable( "x" ) ) );

		Rule rule = new Rule( head, body );

		kb.addClass( C );
		kb.addClass( D );
		kb.addIndividual( i );
		kb.addType( i, C );
		kb.addRule( rule );

		kb.setDoExplanation( true );
		assertTrue( kb.isConsistent() );
		assertTrue( kb.isType( i, D ) );
		Set<ATermAppl> actual = kb.getExplanationSet();
		kb.setDoExplanation( false );

		Set<ATermAppl> expected = new HashSet<ATermAppl>();
		ATermAppl x = ATermUtils.makeVar( "x" );
		ATermAppl[] b = new ATermAppl[] { ATermUtils.makeTypeAtom( x, C ) };
		ATermAppl[] h = new ATermAppl[] { ATermUtils.makeTypeAtom( x, D ) };
		expected.add( ATermUtils.makeTypeAtom( i, C ) );
		expected.add( ATermUtils.makeRule( h, b ) );

		assertEquals( expected, actual );
	}
	
	@Test
	public void testInverseCardinality1() {
		ATermAppl C = term( "C" );
		ATermAppl p = term( "p" );
		ATermAppl invP = term( "invP" );
		ATermAppl a = term( "a" );
		ATermAppl b = term( "b" );
		
		kb.addClass( C );
		kb.addObjectProperty( p );
		kb.addObjectProperty( invP );
		kb.addIndividual( a );
		kb.addIndividual( b );
		
		kb.addSubClass( C, max( invP, 0, TOP ) );

		kb.addInverseProperty( p, invP );
		
		kb.addPropertyValue( p, b, a );
		kb.addType( a, C );

		assertFalse( kb.isConsistent() );
		
		Set<ATermAppl> explanation = kb.getExplanationSet();
		
		assertIteratorValues( explanation.iterator(), new Object[] {
				ATermUtils.makeSub( C, max( invP, 0, TOP ) ), 
				ATermUtils.makeInvProp( p, invP ),
				ATermUtils.makePropAtom( p, b, a ),
				ATermUtils.makeTypeAtom( a, C )  } );
	}
	
	
	@Test
	public void testInverseCardinality2() {
		ATermAppl C = term( "C" );
		ATermAppl p = term( "p" );
		ATermAppl invP = term( "invP" );
		ATermAppl a = term( "a" );
		ATermAppl b = term( "b" );
		ATermAppl c = term( "c" );
		ATermList inds = makeList( new ATerm[] { a, b, c } );
		
		kb.addClass( C );
		kb.addObjectProperty( p );
		kb.addObjectProperty( invP );
		kb.addIndividual( a );
		kb.addIndividual( b );
		kb.addIndividual( c );
		
		kb.addSubClass( C, max( invP, 1, TOP ) );

		kb.addInverseProperty( p, invP );
		
		kb.addPropertyValue( p, b, a );
		kb.addPropertyValue( p, c, a );
		kb.addType( a, C );
		kb.addAllDifferent( inds );

		assertFalse( kb.isConsistent() );
		
		Set<ATermAppl> explanation = kb.getExplanationSet();
		
		assertIteratorValues( explanation.iterator(), new Object[] {
				ATermUtils.makeSub( C, max( invP, 1, TOP ) ), 
				ATermUtils.makeInvProp( p, invP ),
				ATermUtils.makeAllDifferent( inds ), 
				ATermUtils.makePropAtom( p, b, a ),
				ATermUtils.makePropAtom( p, c, a ),
				ATermUtils.makeTypeAtom( a, C )  } );
	}
	
	@Test
	public void testInverseCardinality3() {
		ATermAppl C = term( "C" );
		ATermAppl p = term( "p" );
		ATermAppl invP = term( "invP" );
		ATermAppl a = term( "a" );
		ATermAppl b = term( "b" );
		ATermAppl c = term( "c" );
		ATermAppl d = term( "d" );
		ATermList inds = makeList( new ATerm[] { a, b, c, d } );
		
		kb.addClass( C );
		kb.addObjectProperty( p );
		kb.addObjectProperty( invP );
		kb.addIndividual( a );
		kb.addIndividual( b );
		kb.addIndividual( c );
		kb.addIndividual( d );
		
		kb.addSubClass( C, max( invP, 2, TOP ) );

		kb.addInverseProperty( p, invP );
		
		kb.addPropertyValue( p, b, a );
		kb.addPropertyValue( p, c, a );
		kb.addPropertyValue( p, d, a );
		kb.addType( a, C );
		kb.addAllDifferent( inds );

		assertFalse( kb.isConsistent() );
		
		Set<ATermAppl> explanation = kb.getExplanationSet();
		
		assertIteratorValues( explanation.iterator(), new Object[] {
				ATermUtils.makeSub( C, max( invP, 2, TOP ) ), 
				ATermUtils.makeInvProp( p, invP ),
				ATermUtils.makeAllDifferent( inds ), 
				ATermUtils.makePropAtom( p, b, a ),
				ATermUtils.makePropAtom( p, c, a ),
				ATermUtils.makePropAtom( p, d, a ),
				ATermUtils.makeTypeAtom( a, C )  } );
	}
	
	@Test
	public void testInverseAllValues1() {
		ATermAppl C = term( "C" );
		ATermAppl D = term( "D" );
		ATermAppl p = term( "p" );
		ATermAppl invP = term( "invP" );
		ATermAppl a = term( "a" );
		ATermAppl b = term( "b" );
		
		kb.addClass( C );
		kb.addClass( D );
		kb.addObjectProperty( p );
		kb.addObjectProperty( invP );
		kb.addIndividual( a );
		kb.addIndividual( b );
		
		kb.addSubClass( C, all( invP, D ) );

		kb.addInverseProperty( p, invP );
		
		kb.addPropertyValue( p, b, a );
		kb.addType( a, C );

		assertTrue( kb.isConsistent() );
		
		assertTrue( kb.isType( b, D ) );
		
		Set<ATermAppl> explanation = kb.getExplanationSet();
		
		assertIteratorValues( explanation.iterator(), new Object[] {
				ATermUtils.makeSub( C, all( invP, D ) ), 
				ATermUtils.makeInvProp( p, invP ),
				ATermUtils.makePropAtom( p, b, a ),
				ATermUtils.makeTypeAtom( a, C )  } );
	}
	


	@Test
	public void testInverseAllValues2() {
		classes(C, D);
		objectProperties(p, q);
		individuals(a, b, c);

		kb.addSubClass(C, all(q, D));

		kb.addTransitiveProperty(p);
		kb.addInverseProperty(p, q);

		kb.addPropertyValue(p, a, b);
		kb.addPropertyValue(p, b, c);
		kb.addType(c, C);
		kb.addType(a, not(D));

		assertFalse(kb.isConsistent());

		Set<ATermAppl> explanation = kb.getExplanationSet();

		assertIteratorValues(explanation.iterator(), new Object[] { ATermUtils.makeSub(C, all(q, D)),
		                ATermUtils.makeTransitive(p), ATermUtils.makeInvProp(p, q), ATermUtils.makePropAtom(p, a, b),
		                ATermUtils.makePropAtom(p, b, c), ATermUtils.makeTypeAtom(a, not(D)),
		                ATermUtils.makeTypeAtom(c, C) });
	}

	@Test
	public void testRestrictedDatatypeRange() {
		classes(C, D);
		dataProperties(p);
		individuals(a, b, c);

		kb.addRange(p, restrict(Datatypes.INTEGER, minInclusive(literal(10))));
		kb.addPropertyValue(p, a, literal(5));
		assertFalse(kb.isConsistent());

		Set<ATermAppl> explanation = kb.getExplanationSet();

		assertIteratorValues(explanation.iterator(), new Object[] { 
						ATermUtils.makeRange(p, restrict(Datatypes.INTEGER, minInclusive(literal(10)))),
		                ATermUtils.makePropAtom(p, a, literal(5)) });
	}
	

	@Test
	public void testDatatypeDefinitionInconsistency() {
		classes(C);
		dataProperties(p);
		individuals(a, b, c);

		kb.addRange(p, D);
		kb.addDatatypeDefinition(D, restrict(Datatypes.INTEGER, minInclusive(literal(10))));
		kb.addPropertyValue(p, a, literal(5));

		explainInconsistency(
			ATermUtils.makeRange(p, D),
			ATermUtils.makeDatatypeDefinition(D,restrict(Datatypes.INTEGER, minInclusive(literal(10)))),
		    ATermUtils.makePropAtom(p, a, literal(5)));
	}
	

	@Test
	public void testDatatypeDefinition() {
		classes(A);
		dataProperties(p);
		individuals(a);

		kb.addDatatypeDefinition(D, restrict(Datatypes.INTEGER, minInclusive(literal(10))));
		kb.addPropertyValue(p, a, literal(15));
		kb.addEquivalentClass(A, some(p, D));

		explainEntailment(kb.isType(a, A), 
			ATermUtils.makeEqClasses(A, some(p, D)),
			ATermUtils.makeDatatypeDefinition(D,restrict(Datatypes.INTEGER, minInclusive(literal(10)))),
		    ATermUtils.makePropAtom(p, a, literal(15)));
	}
	

	@Test
	public void testDatatypeEnumeration() {
		classes(A);
		objectProperties(q);
		dataProperties(p);
		individuals(a, b);

		kb.addDatatypeDefinition(D, oneOf(literal(1),literal(2)));
		kb.addPropertyValue(p, a, literal(1));
		kb.addPropertyValue(p, b, literal(2));
		kb.addPropertyValue(p, b, literal(3));		
		kb.addPropertyValue(q, a, b);
		kb.addEquivalentClass(A, and(some(p, D), some(q, some(p, not(D)))));

		explainEntailment(kb.isType(a, A), 
			ATermUtils.makeEqClasses(A, and(some(p, D), some(q, some(p, not(D))))),
			ATermUtils.makeDatatypeDefinition(D, oneOf(literal(1),literal(2))),
		    ATermUtils.makePropAtom(p, a, literal(1)),
		    ATermUtils.makePropAtom(p, b, literal(3)),
		    ATermUtils.makePropAtom(q, a, b));
	}
	
	@Test
	public void ruleInteractionWithInverses() {
		// Tests #446
		
		classes(A);
		objectProperties(p, q, r, f);
		dataProperties(p);
		individuals(a, b, c);

		AtomIVariable x = new AtomIVariable("x");
		AtomIVariable y = new AtomIVariable("y");
		AtomIVariable z = new AtomIVariable("z");

		kb.addSymmetricProperty(p);
		kb.addInverseProperty(q, r);

		kb.addPropertyValue(p, c, a);
		kb.addPropertyValue(f, a, b);

		List<RuleAtom> body = Arrays.<RuleAtom>asList(new IndividualPropertyAtom(f, x, y), new IndividualPropertyAtom(p, x, z));
		List<RuleAtom> head = Arrays.<RuleAtom>asList(new IndividualPropertyAtom(r, z, y));
		Rule rule = new Rule(head, body); 
		kb.addRule(rule);
		
		explainEntailment(kb.hasPropertyValue(b, q, c), 
		    ATermUtils.makePropAtom(p, c, a),
		    ATermUtils.makePropAtom(f, a, b),
		    ATermUtils.makeSymmetric(p),
		    ATermUtils.makeInvProp(q, r),
		    new RulesToATermTranslator().translate(rule));
		   
	}
	
	@Test
	public void propertyChainInstances() {
		// Tests #367
		
		objectProperties(p, q, r);
		individuals(a, b, c);
		
		kb.addSubProperty(list(p, q), r);

		kb.addPropertyValue(p, a, b);
		kb.addPropertyValue(q, b, c);
		
		explainEntailment(kb.hasPropertyValue(a, r, c), 
		    ATermUtils.makePropAtom(p, a, b),
		    ATermUtils.makePropAtom(q, b, c),
		    ATermUtils.makeSubProp(list(p, q), r));		   
	}	
	
	@Test
	public void propertyChainClasses() {
		// Tests #367
		
		classes(A, B, C);
		objectProperties(p, q, r);
		
		kb.addSubProperty(list(p, q), r);

		kb.addSubClass(A, some(p, some(q,B)));
		kb.addSubClass(some(r,B), C);
		
		explainEntailment(kb.isSubClassOf(A, C), 
		    ATermUtils.makeSub(A, some(p, some(q,B))),
		    ATermUtils.makeSub(some(r,B), C),
		    ATermUtils.makeSubProp(list(p, q), r));		   
	}	

	@Ignore("Fails due to #294")
	@Test
	public void propertyChainNested() {
		// Tests #367, #294
		
		classes(A, B, C);
		objectProperties(p, q, r, f);
		
		kb.addSubProperty(list(p, q), p);
		kb.addSubProperty(list(p, r), f);
		kb.addSubProperty(r, q);

		kb.addSubClass(A, some(p, some(r, some(r, B))));
		kb.addSubClass(some(f, B), C);
		
		explainEntailment(kb.isSubClassOf(A, C), 
		    ATermUtils.makeSub(A, some(p, some(q,B))),
		    ATermUtils.makeSub(some(r,B), C),
		    ATermUtils.makeSubProp(list(p, q), r),
		    ATermUtils.makeSubProp(list(r, p), f));		   
	}

	@Test
	public void testDomainExpression() {
		classes(A, B);
		objectProperties(p);
		individuals(a, b);

		kb.addDomain(p, or(A, B));
		kb.addType(a, not(or(A, B)));
		kb.addPropertyValue(p, a, b);

		explainInconsistency( 
			ATermUtils.makeDomain(p, or(A, B)),
			ATermUtils.makeTypeAtom(a, not(or(A, B))),
			ATermUtils.makePropAtom(p, a, b)  
		);
	}

	@Test
	public void testRangeExpression() {
		classes(A, B);
		objectProperties(p);
		individuals(a, b);

		kb.addRange(p, or(A, B));
		kb.addType(b, not(or(A, B)));
		kb.addPropertyValue(p, a, b);

		explainInconsistency( 
			ATermUtils.makeRange(p, or(A, B)),
			ATermUtils.makeTypeAtom(b, not(or(A, B))),
			ATermUtils.makePropAtom(p, a, b)  
		);
	}	
	
	@Test
	public void testFunctionalSubDataProperty() {
		// test for ticket #551
		
		individuals(a);
		dataProperties(p, q);

		kb.addFunctionalProperty(q);
		kb.addSubProperty(p, q);
		
		kb.addPropertyValue(p, a, literal(1));
		kb.addPropertyValue(q, a, literal(2));
		
		explainInconsistency( 
 			ATermUtils.makeFunctional(q),
 			ATermUtils.makeSubProp(p, q),
 			ATermUtils.makePropAtom(p, a, literal(1)),
 			ATermUtils.makePropAtom(q, a, literal(2))  
 		);
	}
}
