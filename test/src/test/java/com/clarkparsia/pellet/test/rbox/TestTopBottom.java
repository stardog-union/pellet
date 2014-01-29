package com.clarkparsia.pellet.test.rbox;

import static com.clarkparsia.pellet.utils.TermFactory.BOTTOM;
import static com.clarkparsia.pellet.utils.TermFactory.BOTTOM_DATA_PROPERTY;
import static com.clarkparsia.pellet.utils.TermFactory.BOTTOM_LIT;
import static com.clarkparsia.pellet.utils.TermFactory.BOTTOM_OBJECT_PROPERTY;
import static com.clarkparsia.pellet.utils.TermFactory.TOP;
import static com.clarkparsia.pellet.utils.TermFactory.TOP_DATA_PROPERTY;
import static com.clarkparsia.pellet.utils.TermFactory.TOP_LIT;
import static com.clarkparsia.pellet.utils.TermFactory.TOP_OBJECT_PROPERTY;
import static com.clarkparsia.pellet.utils.TermFactory.all;
import static com.clarkparsia.pellet.utils.TermFactory.and;
import static com.clarkparsia.pellet.utils.TermFactory.hasValue;
import static com.clarkparsia.pellet.utils.TermFactory.literal;
import static com.clarkparsia.pellet.utils.TermFactory.not;
import static com.clarkparsia.pellet.utils.TermFactory.oneOf;
import static com.clarkparsia.pellet.utils.TermFactory.or;
import static com.clarkparsia.pellet.utils.TermFactory.some;
import static com.clarkparsia.pellet.utils.TermFactory.term;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import junit.framework.JUnit4TestAdapter;

import org.junit.Test;
import org.mindswap.pellet.KnowledgeBase;

import aterm.ATermAppl;

public class TestTopBottom {
	public static junit.framework.Test suite() {
		return new JUnit4TestAdapter( TestTopBottom.class );
	}
	
	@Test
	public void bottomDataAssertion() {
		KnowledgeBase kb = new KnowledgeBase();
		
		ATermAppl x = term( "x" );
		ATermAppl y = literal( "y" );
		
		kb.addIndividual( x );
		
		assertFalse( kb.hasPropertyValue( x, BOTTOM_DATA_PROPERTY, y ) );
		
		assertTrue( kb.isType( x, not( some( BOTTOM_DATA_PROPERTY, TOP_LIT ) ) ) );
		assertTrue( kb.isType( x, not( hasValue( BOTTOM_DATA_PROPERTY, y ) ) ) );
		
		kb.addPropertyValue( BOTTOM_DATA_PROPERTY, x, y );
		assertFalse( kb.isConsistent() );
	}
	
	@Test
	public void bottomDataDomain() {
		KnowledgeBase kb = new KnowledgeBase();
		
		assertTrue( kb.hasDomain( BOTTOM_DATA_PROPERTY, BOTTOM ) );
	}
	
	@Test
	public void bottomDataFunctional() {
		KnowledgeBase kb = new KnowledgeBase();
		
		assertTrue( kb.isFunctionalProperty( BOTTOM_DATA_PROPERTY ) );
	}
	
	@Test
	public void bottomDataRange() {
		KnowledgeBase kb = new KnowledgeBase();
		
		assertTrue( kb.hasRange( BOTTOM_DATA_PROPERTY, BOTTOM_LIT ) );
	}
	
	@Test
	public void bottomDataSuper() {
		KnowledgeBase kb = new KnowledgeBase();
		
		ATermAppl p = term("p");
		kb.addDatatypeProperty( p );
		
		assertTrue( kb.isSubPropertyOf( BOTTOM_DATA_PROPERTY, p ) );
	}
	
	@Test
	public void bottomObjectAssertion() {
		KnowledgeBase kb = new KnowledgeBase();
		
		ATermAppl x = term( "x" );
		ATermAppl y = term( "y" );
		
		kb.addIndividual( x );
		kb.addIndividual( y );
		
		assertFalse( kb.hasPropertyValue( x, BOTTOM_OBJECT_PROPERTY, x ) );
		assertFalse( kb.hasPropertyValue( x, BOTTOM_OBJECT_PROPERTY, y ) );
		assertFalse( kb.hasPropertyValue( y, BOTTOM_OBJECT_PROPERTY, x ) );
		assertFalse( kb.hasPropertyValue( y, BOTTOM_OBJECT_PROPERTY, y ) );
		
		assertTrue( kb.isType( x, not( some( BOTTOM_OBJECT_PROPERTY, TOP ) ) ) );
		assertTrue( kb.isType( x, not( hasValue( BOTTOM_OBJECT_PROPERTY, x ) ) ) );
		assertTrue( kb.isType( x, not( hasValue( BOTTOM_OBJECT_PROPERTY, y ) ) ) );
		
		assertTrue( kb.isType( y, not( some( BOTTOM_OBJECT_PROPERTY, TOP ) ) ) );
		assertTrue( kb.isType( y, not( hasValue( BOTTOM_OBJECT_PROPERTY, x ) ) ) );
		assertTrue( kb.isType( y, not( hasValue( BOTTOM_OBJECT_PROPERTY, y ) ) ) );
		
		kb.addPropertyValue( BOTTOM_OBJECT_PROPERTY, x, y );
		assertFalse( kb.isConsistent() );
	}
	
	@Test
	public void bottomObjectAsymm() {
		KnowledgeBase kb = new KnowledgeBase();
		
		assertTrue( kb.isAsymmetricProperty( BOTTOM_OBJECT_PROPERTY ) );
	}
	
	@Test
	public void bottomObjectFunc() {
		KnowledgeBase kb = new KnowledgeBase();
		
		assertTrue( kb.isFunctionalProperty( BOTTOM_OBJECT_PROPERTY ) );
		assertTrue( kb.getFunctionalProperties().contains( BOTTOM_OBJECT_PROPERTY ) );
	}
	
	@Test
	public void bottomObjectInverse() {
		KnowledgeBase kb = new KnowledgeBase();
		
		assertTrue( kb.isInverse( BOTTOM_OBJECT_PROPERTY, BOTTOM_OBJECT_PROPERTY ) );
	}
	
	@Test
	public void bottomObjectInverseManual() {
		KnowledgeBase kb = new KnowledgeBase();
		
		ATermAppl c = term( "_C_" );
		ATermAppl notC = not( c );

		ATermAppl r = BOTTOM_OBJECT_PROPERTY;
		ATermAppl test = and( c, or( some( r, all( r, notC ) ), some( r, all( r, notC ) ) ) );
		assertFalse( kb.isSatisfiable( test ) );
	}
	
	@Test
	public void bottomObjectInvFunc() {
		KnowledgeBase kb = new KnowledgeBase();
		
		assertTrue( kb.isInverseFunctionalProperty( BOTTOM_OBJECT_PROPERTY ) );
	}
	
	@Test
	public void bottomObjectIrreflexive() {
		KnowledgeBase kb = new KnowledgeBase();
		
		assertTrue( kb.isIrreflexiveProperty( BOTTOM_OBJECT_PROPERTY ) );
	}
	
	@Test
	public void bottomObjectReflexive() {
		KnowledgeBase kb = new KnowledgeBase();
		
		assertFalse( kb.isReflexiveProperty( BOTTOM_OBJECT_PROPERTY ) );
	}
	
	@Test
	public void bottomObjectSuper() {
		KnowledgeBase kb = new KnowledgeBase();
		
		ATermAppl p = term("p");
		kb.addObjectProperty( p );
		
		assertTrue( kb.isSubPropertyOf( BOTTOM_OBJECT_PROPERTY, p ) );
	}
	
	@Test
	public void bottomObjectSymm() {
		KnowledgeBase kb = new KnowledgeBase();
		
		assertTrue( kb.isSymmetricProperty( BOTTOM_OBJECT_PROPERTY ) );
	}
	
	@Test
	public void bottomObjectTransitive() {
		KnowledgeBase kb = new KnowledgeBase();
		
		assertTrue( kb.isTransitiveProperty( BOTTOM_OBJECT_PROPERTY ) );
	}
	
	@Test
	public void topDataAssertion() {
		KnowledgeBase kb = new KnowledgeBase();
		
		ATermAppl r = term( "r" );
		ATermAppl x = term( "x" );
		ATermAppl y = literal( "y" );
		
		kb.addDatatypeProperty( r );
		kb.addIndividual( x );
		kb.addPropertyValue( r, x, y );
		
		assertTrue( kb.hasPropertyValue( x, TOP_DATA_PROPERTY, y ) );
	}
	
	@Test
	public void topDataFunctional() {
		KnowledgeBase kb = new KnowledgeBase();
		
		assertFalse( kb.isFunctionalProperty( TOP_DATA_PROPERTY ) );
	}
	
	@Test
	public void topDataSuper() {
		KnowledgeBase kb = new KnowledgeBase();
		
		ATermAppl p = term("p");
		kb.addDatatypeProperty( p );
		
		assertTrue( kb.isSubPropertyOf( p, TOP_DATA_PROPERTY ) );
	}
	
	@Test
	public void topObjectAllValues() {
		KnowledgeBase kb = new KnowledgeBase();
		
		ATermAppl c = term( "C" );
		ATermAppl x = term( "x" );
		ATermAppl y = term( "y" );
		ATermAppl z = term( "z" );
		
		kb.addClass( c );
		kb.addIndividual( x );
		kb.addIndividual( y );
		kb.addIndividual( z );
		kb.addDifferent( x, y );
		kb.addDifferent( x, z );
		
		kb.addSubClass( c, oneOf( x, y ) );
		kb.addSubClass( TOP, all( TOP_OBJECT_PROPERTY, c ) );
		
		assertTrue( kb.isSameAs( y, z ) );
	}
	
	@Test
	public void topObjectallValuesFreshInd() {
		KnowledgeBase kb = new KnowledgeBase();
		
		ATermAppl c = term( "_C_" );
		ATermAppl notC = not( c );
		
		ATermAppl a = term( "a" );
		ATermAppl b = term( "b" );
		kb.addIndividual( a );
		kb.addType( a, all( TOP_OBJECT_PROPERTY, notC ) );
		kb.addIndividual( b );
		kb.addType( b, some( TOP_OBJECT_PROPERTY, c ) );
		
		assertFalse( kb.isConsistent() );
	
	}
	
	@Test
	public void topObjectAssertion() {
		KnowledgeBase kb = new KnowledgeBase();
		
		ATermAppl x = term( "x" );
		ATermAppl y = term( "y" );
		
		kb.addIndividual( x );
		kb.addIndividual( y );
		
		assertTrue( kb.hasPropertyValue( x, TOP_OBJECT_PROPERTY, x ) );
		assertTrue( kb.hasPropertyValue( x, TOP_OBJECT_PROPERTY, y ) );
		assertTrue( kb.hasPropertyValue( y, TOP_OBJECT_PROPERTY, x ) );
		assertTrue( kb.hasPropertyValue( y, TOP_OBJECT_PROPERTY, y ) );
	}
	
	@Test
	public void topObjectAsymm() {
		KnowledgeBase kb = new KnowledgeBase();
		
		assertFalse( kb.isAsymmetricProperty( TOP_OBJECT_PROPERTY ) );
	}
	
	@Test
	public void topObjectDomain() {
		KnowledgeBase kb = new KnowledgeBase();
		
		ATermAppl c = term( "C" );
		ATermAppl x = term( "x" );
		ATermAppl y = term( "y" );
		ATermAppl z = term( "z" );
		
		kb.addClass( c );
		kb.addIndividual( x );
		kb.addIndividual( y );
		kb.addIndividual( z );
		kb.addDifferent( x, y );
		kb.addDifferent( x, z );
		
		kb.addSubClass( c, oneOf( x, y ) );
		kb.addDomain( TOP_OBJECT_PROPERTY, c );
		
		assertTrue( kb.isSameAs( y, z ) );
	}
	
	@Test
	public void topObjectFunc() {
		KnowledgeBase kb = new KnowledgeBase();
		
		assertFalse( kb.isFunctionalProperty( TOP_OBJECT_PROPERTY ) );
	}
	
	@Test
	public void topObjectInverse() {
		KnowledgeBase kb = new KnowledgeBase();
		
		assertTrue( kb.isInverse( TOP_OBJECT_PROPERTY, TOP_OBJECT_PROPERTY ) );
	}
	
	@Test
	public void topObjectInverseManual() {
		KnowledgeBase kb = new KnowledgeBase();
		
		ATermAppl c = term( "_C_" );
		ATermAppl notC = not( c );

		ATermAppl r = TOP_OBJECT_PROPERTY;
		ATermAppl test = and( c, or( some( r, all( r, notC ) ), some( r, all( r, notC ) ) ) );
		assertFalse( kb.isSatisfiable( test ) );
	}
	
	@Test
	public void topObjectInvFunc() {
		KnowledgeBase kb = new KnowledgeBase();
		
		assertFalse( kb.isInverseFunctionalProperty( TOP_OBJECT_PROPERTY ) );
	}
	
	@Test
	public void topObjectIrreflexive() {
		KnowledgeBase kb = new KnowledgeBase();
		
		assertFalse( kb.isIrreflexiveProperty( TOP_OBJECT_PROPERTY ) );
	}
	
	@Test
	public void topObjectRange() {
		KnowledgeBase kb = new KnowledgeBase();
		
		ATermAppl c = term( "C" );
		ATermAppl x = term( "x" );
		ATermAppl y = term( "y" );
		ATermAppl z = term( "z" );
		
		kb.addClass( c );
		kb.addIndividual( x );
		kb.addIndividual( y );
		kb.addIndividual( z );
		kb.addDifferent( x, y );
		kb.addDifferent( x, z );
		
		kb.addSubClass( c, oneOf( x, y ) );
		kb.addRange( TOP_OBJECT_PROPERTY, c );
		
		assertTrue( kb.isSameAs( y, z ) );
	}
	
	@Test
	public void topObjectReflexive() {
		KnowledgeBase kb = new KnowledgeBase();
		
		assertTrue( kb.isReflexiveProperty( TOP_OBJECT_PROPERTY ) );	
	}
	
	@Test
	public void topObjectSomeValuesBottom() {
		KnowledgeBase kb = new KnowledgeBase();
		kb.addSubClass( TOP, some( TOP_OBJECT_PROPERTY, BOTTOM ) );
		
		assertFalse( kb.isConsistent() );
	}
	
	@Test
	public void topObjectSuper() {
		KnowledgeBase kb = new KnowledgeBase();
		
		ATermAppl p = term("p");
		kb.addObjectProperty( p );
		
		assertTrue( kb.isSubPropertyOf( p, TOP_OBJECT_PROPERTY ) );
	}
	
	@Test
	public void topObjectSymm() {
		KnowledgeBase kb = new KnowledgeBase();
		
		assertTrue( kb.isSymmetricProperty( TOP_OBJECT_PROPERTY ) );
	}
	
	@Test
	public void topObjectTransitive() {
		KnowledgeBase kb = new KnowledgeBase();
		
		assertTrue( kb.isTransitiveProperty( TOP_OBJECT_PROPERTY ) );
	}
	
	@Test
	public void topObjectDisjoints() {
		KnowledgeBase kb = new KnowledgeBase();
		
		ATermAppl p = term("p");
		ATermAppl subP = term("subP");
		ATermAppl q = term("q");
		
		kb.addObjectProperty( p );
		kb.addObjectProperty( subP );
		kb.addObjectProperty( q );
		
		kb.addSubProperty( subP, p );
		kb.addDisjointProperty( p, q );
		
		kb.getRoleTaxonomy( true ).getTop().print();
		kb.getRoleTaxonomy( false ).getTop().print();
		
		assertTrue( kb.isDisjointProperty( BOTTOM_OBJECT_PROPERTY, TOP_OBJECT_PROPERTY ) );
		assertTrue( kb.isDisjointProperty( TOP_OBJECT_PROPERTY, BOTTOM_OBJECT_PROPERTY ) );
		
		assertEquals( singletonSets( BOTTOM_OBJECT_PROPERTY ), kb.getDisjointProperties( TOP_OBJECT_PROPERTY ) );
		assertEquals( singletonSets( BOTTOM_OBJECT_PROPERTY, TOP_OBJECT_PROPERTY, p, subP, q ), kb.getDisjointProperties( BOTTOM_OBJECT_PROPERTY ) );
		assertEquals( singletonSets( BOTTOM_OBJECT_PROPERTY, q ), kb.getDisjointProperties( p ) );
		assertEquals( singletonSets( BOTTOM_OBJECT_PROPERTY, p, subP ), kb.getDisjointProperties( q ) );
		
		assertEquals( singletonSets( BOTTOM_OBJECT_PROPERTY ), kb.getDisjointProperties( TOP_OBJECT_PROPERTY, true ) );
		assertEquals( singletonSets( TOP_OBJECT_PROPERTY ), kb.getDisjointProperties( BOTTOM_OBJECT_PROPERTY, true ) );
		assertEquals( singletonSets( q ), kb.getDisjointProperties( p, true ) );
		assertEquals( singletonSets( p ), kb.getDisjointProperties( q, true ) );

	}

	public static <T> Set<Set<T>> singletonSets(T... es) {
		Set<Set<T>> set = new HashSet<Set<T>>();
		for( T e : es ) {
			set.add( Collections.singleton( e ) );
		}
		return set;
	}
}
