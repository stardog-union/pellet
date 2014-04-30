// Portions Copyright (c) 2006 - 2008, Clark & Parsia, LLC.
// <http://www.clarkparsia.com>
// Clark & Parsia, LLC parts of this source code are available under the terms
// of the Affero General Public License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of
// proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package org.mindswap.pellet.test;

import static com.clarkparsia.pellet.utils.TermFactory.BOTTOM;
import static com.clarkparsia.pellet.utils.TermFactory.TOP;
import static com.clarkparsia.pellet.utils.TermFactory.TOP_LIT;
import static com.clarkparsia.pellet.utils.TermFactory.all;
import static com.clarkparsia.pellet.utils.TermFactory.and;
import static com.clarkparsia.pellet.utils.TermFactory.card;
import static com.clarkparsia.pellet.utils.TermFactory.hasValue;
import static com.clarkparsia.pellet.utils.TermFactory.inv;
import static com.clarkparsia.pellet.utils.TermFactory.literal;
import static com.clarkparsia.pellet.utils.TermFactory.max;
import static com.clarkparsia.pellet.utils.TermFactory.maxExclusive;
import static com.clarkparsia.pellet.utils.TermFactory.maxInclusive;
import static com.clarkparsia.pellet.utils.TermFactory.min;
import static com.clarkparsia.pellet.utils.TermFactory.minExclusive;
import static com.clarkparsia.pellet.utils.TermFactory.minInclusive;
import static com.clarkparsia.pellet.utils.TermFactory.not;
import static com.clarkparsia.pellet.utils.TermFactory.oneOf;
import static com.clarkparsia.pellet.utils.TermFactory.or;
import static com.clarkparsia.pellet.utils.TermFactory.restrict;
import static com.clarkparsia.pellet.utils.TermFactory.self;
import static com.clarkparsia.pellet.utils.TermFactory.some;
import static com.clarkparsia.pellet.utils.TermFactory.term;
import static com.clarkparsia.pellet.utils.TermFactory.value;
import static java.util.Collections.singleton;
import static java.util.Collections.singletonList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mindswap.pellet.test.PelletTestCase.assertIteratorValues;
import static org.mindswap.pellet.test.PelletTestCase.assertNotSubClass;
import static org.mindswap.pellet.test.PelletTestCase.assertSatisfiable;
import static org.mindswap.pellet.test.PelletTestCase.assertSubClass;
import static org.mindswap.pellet.test.PelletTestCase.assertUnsatisfiable;

import java.io.File;
import java.net.MalformedURLException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import junit.framework.JUnit4TestAdapter;

import org.junit.Ignore;
import org.junit.Test;
import org.mindswap.pellet.Clash;
import org.mindswap.pellet.KBLoader;
import org.mindswap.pellet.KnowledgeBase;
import org.mindswap.pellet.PelletOptions;
import org.mindswap.pellet.Role;
import org.mindswap.pellet.jena.JenaLoader;
import org.mindswap.pellet.taxonomy.Taxonomy;
import org.mindswap.pellet.taxonomy.TaxonomyNode;
import org.mindswap.pellet.utils.ATermUtils;
import org.mindswap.pellet.utils.FileUtils;
import org.mindswap.pellet.utils.SetUtils;
import org.mindswap.pellet.utils.iterator.FlattenningIterator;
import org.mindswap.pellet.utils.iterator.IteratorUtils;

import aterm.ATerm;
import aterm.ATermAppl;
import aterm.ATermList;

import com.clarkparsia.pellet.datatypes.Datatypes;
import com.clarkparsia.pellet.datatypes.Facet;
import com.clarkparsia.pellet.datatypes.types.real.XSDByte;
import com.clarkparsia.pellet.datatypes.types.real.XSDDecimal;
import com.clarkparsia.pellet.datatypes.types.real.XSDInteger;
import com.clarkparsia.pellet.datatypes.types.text.XSDString;
import com.clarkparsia.pellet.utils.PropertiesBuilder;
import com.clarkparsia.pellet.utils.TermFactory;

public class MiscTests extends AbstractKBTests {
	public static String	base	= "file:" + PelletTestSuite.base + "misc/";

	public static void main(String args[]) {
		junit.textui.TestRunner.run( MiscTests.suite() );
	}

	public static junit.framework.Test suite() {
		return new JUnit4TestAdapter( MiscTests.class );
	}

	/**
	 * Test that an individual added to a complete ABox isn't discarded if
	 * backtracking occurs in that ABox
	 */
	@Test
	public void backtrackPreservesAssertedIndividuals() {
		KnowledgeBase kb = new KnowledgeBase();

		kb.addIndividual( term( "x" ) );

		kb.addClass( term( "C" ) );
		kb.addClass( term( "D" ) );

		kb.addDatatypeProperty( term( "p" ) );
		kb.addFunctionalProperty( term( "p" ) );

		kb.addSubClass( term( "C" ), ATermUtils.makeSomeValues( term( "p" ), ATermUtils
				.makeValue( ATermUtils.makePlainLiteral( "0" ) ) ) );
		kb.addSubClass( term( "D" ), ATermUtils.makeSomeValues( term( "p" ), ATermUtils
				.makeValue( ATermUtils.makePlainLiteral( "1" ) ) ) );

		kb.addType( term( "x" ), ATermUtils.makeOr( ATermUtils.makeList( new ATerm[] {
				term( "C" ), term( "D" ) } ) ) );

		/*
		 * At this point we can get onto one of two branches. In one p(x,"0"),
		 * in the other p(x,"1") The branch point is a concept disjunction and
		 * we're provoking a datatype clash.
		 */
		assertTrue( kb.isConsistent() );

		/*
		 * Add the individual to the now completed ABox
		 */
		kb.addIndividual( term( "y" ) );

		assertTrue( kb.isConsistent() );
		assertNotNull( kb.getABox().getIndividual( term( "y" ) ) );

		/*
		 * This assertion causes a clash regardless of which branch we are on
		 */
		kb.addPropertyValue( term( "p" ), term( "x" ), ATermUtils.makePlainLiteral( "2" ) );

		assertFalse( kb.isConsistent() );

		/*
		 * In 2.0.0-rc5 (and perhaps earlier, this assertion fails)
		 */
		assertNotNull( kb.getABox().getIndividual( term( "y" ) ) );
	}

	@Test
	public void testFileUtilsToURI() throws MalformedURLException {

		assertEquals( new File( "build.xml" ).toURI().toURL().toString(), FileUtils
				.toURI( "build.xml" ) );
		assertEquals( "http://example.com/foo", FileUtils.toURI( "http://example.com/foo" ) );
		assertEquals( "file:///foo", FileUtils.toURI( "file:///foo" ) );
		assertEquals( "ftp://example.com/foo", FileUtils.toURI( "ftp://example.com/foo" ) );
		assertEquals( "https://example.com/foo", FileUtils.toURI( "https://example.com/foo" ) );
	}

	@Test
	public void testQualifiedCardinalityObjectProperty() {
		ATermAppl sub = term( "sub" );
		ATermAppl sup = term( "sup" );

		classes( c, d, sub, sup );
		objectProperties( p, f );
		
		kb.addFunctionalProperty( f );
		
		kb.addSubClass( sub, sup );
		
		assertSatisfiable( kb, and( min( p, 2, and( c, d ) ), max( p, 2, c ), some( p, or( and( c,
				not( d ) ), c ) ) ) );
		assertSubClass( kb, min( p, 4, TOP ), min( p, 2, TOP ) );
		assertNotSubClass( kb, min( p, 1, TOP ), min( p, 2, TOP ) );
		assertNotSubClass( kb, min( p, 1, c ), min( p, 1, d ) );
		assertNotSubClass( kb, and( some( p, c ), some( p, not( c ) ) ), min( p, 2, d ) );
		assertSubClass( kb, min( p, 3, c ), min( p, 2, c ) );
		assertSubClass( kb, min( p, 3, c ), min( p, 2, TOP ) );
		assertSubClass( kb, min( p, 2, c ), min( p, 2, TOP ) );
		assertNotSubClass( kb, min( p, 2, c ), min( p, 2, d ) );
		assertSubClass( kb, min( p, 2, and( c, d ) ), some( p, c ) );
		assertSubClass( kb, max( p, 1, sup ), max( p, 2, sub ) );
		assertSubClass( kb, and( max( f, 1, TOP ), all( f, c ) ), max( f, 1, c ) );
		assertSubClass( kb, and( min( p, 2, c ), min( p, 2, not( c ) ) ), min( p, 4, TOP ) );
	}
	
	@Test
	public void testQualifiedCardinalityDataProperty() {
		KnowledgeBase kb = new KnowledgeBase();

		ATermAppl c = restrict( Datatypes.INTEGER, minInclusive( literal( 10 ) ) );
		ATermAppl d = restrict( Datatypes.INTEGER, maxInclusive( literal( 20 ) ) );

		ATermAppl p = term( "p" );
		ATermAppl f = term( "f" );
		ATermAppl sub = Datatypes.SHORT;
		ATermAppl sup = Datatypes.INTEGER;

		kb.addDatatype(sub);
		kb.addDatatype(sup);
		kb.addDatatypeProperty( p );
		kb.addDatatypeProperty( f );
		kb.addFunctionalProperty( f );

		assertSatisfiable( kb, and( min( p, 2, and( c, d ) ), max( p, 2, c ), some( p, or( and( c,
				not( d ) ), c ) ) ) );
		assertSubClass( kb, min( p, 4, TOP_LIT ), min( p, 2, TOP_LIT ) );
		assertNotSubClass( kb, min( p, 1, TOP_LIT ), min( p, 2, TOP_LIT ) );
		assertNotSubClass( kb, min( p, 1, c ), min( p, 1, d ) );
		assertNotSubClass( kb, and( some( p, c ), some( p, not( c ) ) ), min( p, 2, d ) );
		assertSubClass( kb, min( p, 3, c ), min( p, 2, c ) );
		assertSubClass( kb, min( p, 3, c ), min( p, 2, TOP_LIT ) );
		assertSubClass( kb, min( p, 2, c ), min( p, 2, TOP_LIT ) );
		assertNotSubClass( kb, min( p, 2, c ), min( p, 2, d ) );
		assertSubClass( kb, min( p, 2, and( c, d ) ), some( p, c ) );
		assertSubClass( kb, max( p, 1, sup ), max( p, 2, sub ) );
		assertSubClass( kb, and( max( f, 1, TOP_LIT ), all( f, c ) ), max( f, 1, c ) );
		assertSubClass( kb, and( min( p, 2, c ), min( p, 2, not( c ) ) ), min( p, 4, TOP_LIT ) );
	}

	@Test
	public void testQualifiedCardinality3() {
		KnowledgeBase kb = new KnowledgeBase();

		ATermAppl c = term( "z" );
		ATermAppl d = term( "d" );
		ATermAppl e = term( "e" );
		ATermAppl notD = term( "notD" );

		ATermAppl p = term( "p" );

		ATermAppl x = term( "x" );
		ATermAppl y3 = term( "y3" );

		kb.addObjectProperty( p );
		kb.addClass( c );
		kb.addClass( d );
		kb.addClass( e );
		kb.addClass( notD );

		kb.addDisjointClass( d, notD );
		// kb.addSubClass( c, or(e,not(d)) );

		kb.addIndividual( x );
		kb.addIndividual( y3 );

		kb.addType( x, and( min( p, 2, and( d, e ) ), max( p, 2, d ) ) );
		kb.addType( y3, not( e ) );
		kb.addType( y3, some( inv( p ), value( x ) ) );
		kb.addType( y3, or( d, c ) );

		assertTrue( kb.isConsistent() );
	}

	@Test
	public void testSelfRestrictions() {
		KnowledgeBase kb = new KnowledgeBase();

		ATermAppl c = term( "c" );
		ATermAppl d = term( "d" );

		ATermAppl p = term( "p" );

		kb.addClass( c );
		kb.addClass( d );

		kb.addObjectProperty( p );

		kb.addRange( p, d );

		kb.addSubClass( c, and( self( p ), some( p, TOP ) ) );

		assertTrue( kb.isConsistent() );

		assertTrue( kb.isSatisfiable( c ) );
		assertTrue( kb.isSubClassOf( c, d ) );

	}
	
	@Test
	/**
	 * Test for the enhancement required in #252
	 */
	public void testBooleanDatatypeConstructors() {
		KnowledgeBase kb = new KnowledgeBase();

		ATermAppl nni = Datatypes.NON_NEGATIVE_INTEGER;
		ATermAppl npi = Datatypes.NON_POSITIVE_INTEGER;
		ATermAppl ni = Datatypes.NEGATIVE_INTEGER;
		ATermAppl pi = Datatypes.POSITIVE_INTEGER;
		ATermAppl f = Datatypes.FLOAT;
		
		ATermAppl s = term( "s" );
		kb.addDatatypeProperty( s );
		
		assertSatisfiable( kb, some( s, pi ) );
		assertSatisfiable( kb, some( s, not ( pi ) ) );
		assertUnsatisfiable( kb, some( s, and( pi, ni ) ) );
		assertUnsatisfiable( kb, some( s, and( f, or( pi, ni ) ) ) );
		assertSatisfiable( kb, some( s, and( npi, ni ) ) );
		assertSatisfiable( kb, some( s, and( nni, pi ) ) );
		assertSatisfiable( kb, some( s, or( nni, npi ) ) );
		assertSatisfiable( kb, some( s, and( nni, npi ) ) );
	}	

	@Test
	public void testSelfRestrictionRestore() {
		KnowledgeBase kb = new KnowledgeBase();

		ATermAppl C = term( "c" );
		ATermAppl D = term( "d" );

		ATermAppl p = term( "p" );
		ATermAppl q = term( "q" );
		
		ATermAppl a = term( "a" );

		kb.addClass( C );
		kb.addClass( D );

		kb.addObjectProperty( p );
		kb.addObjectProperty( q );

		kb.addSubClass( C, or( not( self( p ) ), not( self( q ) ) ) );

		kb.addIndividual( a );
		kb.addType( a, C );

		assertTrue( kb.isConsistent() );

		assertFalse( kb.isType( a, not( self( p ) ) ) );
		assertFalse( kb.isType( a, not( self( q ) ) ) );
	}	
	
	@Test
	public void testReflexive1() {
		KnowledgeBase kb = new KnowledgeBase();

		ATermAppl c = term( "c" );
		ATermAppl d = term( "d" );
		ATermAppl sub = term( "sub" );
		ATermAppl sup = term( "sup" );

		ATermAppl p = term( "p" );
		ATermAppl r = term( "r" );
		ATermAppl weakR = term( "weakR" );

		ATermAppl x = term( "x" );
		ATermAppl y = term( "y" );

		kb.addClass( c );
		kb.addClass( d );
		kb.addClass( sub );
		kb.addClass( sup );
		kb.addSubClass( sub, sup );
		kb.addSubClass( some( weakR, TOP ), self( weakR ) );

		kb.addObjectProperty( p );
		kb.addObjectProperty( r );
		kb.addObjectProperty( weakR );
		kb.addReflexiveProperty( r );
		kb.addRange( r, d );

		kb.addIndividual( x );
		kb.addType( x, self( p ) );
		kb.addType( x, not( some( weakR, value( x ) ) ) );
		kb.addIndividual( y );
		kb.addPropertyValue( weakR, y, x );

		assertTrue( kb.isConsistent() );

		assertTrue( kb.isSubClassOf( and( c, self( p ) ), some( p, c ) ) );
		assertTrue( kb.isSubClassOf( and( c, min( r, 1, not( c ) ) ), min( r, 2, TOP ) ) );
		assertTrue( kb.isSubClassOf( min( r, 1, c ), d ) );
		assertTrue( kb.hasPropertyValue( x, p, x ) );
		assertTrue( kb.hasPropertyValue( y, weakR, y ) );
		assertTrue( kb.isDifferentFrom( x, y ) );
		assertTrue( kb.isDifferentFrom( y, x ) );
		assertTrue( kb.isType( x, some( r, value( x ) ) ) );
		assertTrue( kb.isSatisfiable( and( self( p ), self( inv( p ) ), max( p, 1, TOP ) ) ) );
	}

	@Test
	public void testReflexive2() {
		KnowledgeBase kb = new KnowledgeBase();

		ATermAppl a = term( "a" );
		ATermAppl c = term( "c" );
		ATermAppl r = term( "r" );

		kb.addIndividual( a );
		kb.addClass( c );
		kb.addSubClass( c, all( r, BOTTOM ) );
		kb.addSubClass( c, oneOf( a ) );

		kb.addObjectProperty( r );
		kb.addReflexiveProperty( r );

		assertSatisfiable( kb, c, false );
	}
	

	@Test
	public void testReflexive3() {
		classes( C );
		objectProperties( r );
		individuals( a, b, c );
		
		kb.addEquivalentClass( C, self( r ) );

		kb.addPropertyValue( r, a, a );
		kb.addType( b, C );
		kb.addPropertyValue( r, c, a );
		kb.addPropertyValue( r, c, b );

		assertTrue( kb.hasPropertyValue( a, r, a ) );
		assertTrue( kb.hasPropertyValue( b, r, b ) );
		assertFalse( kb.hasPropertyValue( c, r, c ) );
		
		Map<ATermAppl,List<ATermAppl>> allRs = kb.getPropertyValues( r );
		assertIteratorValues( allRs.get( a ).iterator(), new ATermAppl[] { a } );
		assertIteratorValues( allRs.get( b ).iterator(), new ATermAppl[] { b } );
		assertIteratorValues( allRs.get( c ).iterator(), new ATermAppl[] { a, b } );
		
		assertTrue( kb.isType( a, C ) );
		assertTrue( kb.isType( b, C ) );
		assertFalse( kb.isType( c, C ) );
		
		assertEquals( kb.getInstances( C ), SetUtils.create( a, b ) );
	}
	
	@Test
	public void testAsymmetry() {
		ATermAppl p = term( "p" );

		KnowledgeBase kb = new KnowledgeBase();
		kb.addObjectProperty( p );
		kb.addAsymmetricProperty( p );

		assertTrue( kb.isIrreflexiveProperty( p ) );
	}

	@Test
	public void testResrictedDataRange() {
		byte MIN = 0;
		byte MAX = 127;
		int COUNT = MAX - MIN + 1;

		KnowledgeBase kb = new KnowledgeBase();

		ATermAppl C = term( "C" );
		ATermAppl D = term( "D" );
		ATermAppl E = term( "E" );

		ATermAppl p = term( "p" );

		ATermAppl x = term( "x" );
		ATermAppl y = term( "y" );

		kb.addClass( C );
		kb.addClass( D );
		kb.addClass( E );

		kb.addDatatypeProperty( p );
		kb.addRange( p, ATermUtils.makeRestrictedDatatype( XSDInteger.getInstance().getName(), new ATermAppl[] {
			ATermUtils.makeFacetRestriction( Facet.XSD.MIN_INCLUSIVE.getName(), ATermUtils.makeTypedLiteral( Byte.toString( MIN ), XSDByte.getInstance().getName() ) ),
			ATermUtils.makeFacetRestriction( Facet.XSD.MAX_INCLUSIVE.getName(), ATermUtils.makeTypedLiteral( Byte.toString( MAX ), XSDByte.getInstance().getName() ) )
		}) );

		kb.addSubClass( C, card( p, COUNT + 1, ATermUtils.TOP_LIT ) );
		kb.addSubClass( D, card( p, COUNT, ATermUtils.TOP_LIT ) );
		kb.addSubClass( E, card( p, COUNT - 1, ATermUtils.TOP_LIT ) );

		kb.addIndividual( x );
		kb.addType( x, D );

		kb.addIndividual( y );
		kb.addType( y, E );

		assertFalse( kb.isSatisfiable( C ) );
		assertTrue( kb.isSatisfiable( D ) );
		assertTrue( kb.isSatisfiable( E ) );

		assertTrue( kb.hasPropertyValue( x, p, ATermUtils.makeTypedLiteral( "5",
				XSDInteger.getInstance().getName() ) ) );
		assertFalse( kb.hasPropertyValue( y, p, ATermUtils.makeTypedLiteral( "5",
				XSDDecimal.getInstance().getName() ) ) );
	}

	@Test
	public void testMaxCardinality() {
		KnowledgeBase kb = new KnowledgeBase();

		kb.addObjectProperty( term( "p" ) );
		kb.addObjectProperty( term( "q" ) );
		kb.addFunctionalProperty( term( "q" ) );

		kb.addClass( term( "C" ) );
		kb.addSubClass( term( "C" ), ATermUtils.makeMax( term( "p" ), 2, ATermUtils.TOP ) );

		kb.addClass( term( "D1" ) );
		kb.addClass( term( "D2" ) );
		kb.addClass( term( "D3" ) );
		kb.addClass( term( "D4" ) );
		kb.addClass( term( "E1" ) );
		kb.addClass( term( "E2" ) );
		kb.addClass( term( "E3" ) );
		kb.addClass( term( "E4" ) );
		kb.addSubClass( term( "D1" ), ATermUtils.makeSomeValues( term( "q" ), term( "E1" ) ) );
		kb.addSubClass( term( "D2" ), ATermUtils.makeSomeValues( term( "q" ), term( "E2" ) ) );
		kb.addSubClass( term( "D3" ), ATermUtils.makeSomeValues( term( "q" ), term( "E3" ) ) );
		kb.addSubClass( term( "D4" ), ATermUtils.makeSomeValues( term( "q" ), term( "E4" ) ) );

		kb.addIndividual( term( "x" ) );
		kb.addType( term( "x" ), term( "C" ) );
		kb.addIndividual( term( "x1" ) );
		kb.addType( term( "x1" ), term( "D1" ) );
		kb.addIndividual( term( "x2" ) );
		kb.addType( term( "x2" ), term( "D2" ) );
		kb.addIndividual( term( "x3" ) );
		kb.addType( term( "x3" ), term( "D3" ) );
		kb.addIndividual( term( "x4" ) );
		kb.addType( term( "x4" ), term( "D4" ) );

		kb.addPropertyValue( term( "p" ), term( "x" ), term( "x1" ) );
		kb.addPropertyValue( term( "p" ), term( "x" ), term( "x2" ) );
		kb.addPropertyValue( term( "p" ), term( "x" ), term( "x3" ) );
		kb.addPropertyValue( term( "p" ), term( "x" ), term( "x4" ) );

		kb.addDisjointClass( term( "E1" ), term( "E2" ) );
		kb.addDisjointClass( term( "E1" ), term( "E4" ) );
		kb.addDisjointClass( term( "E2" ), term( "E3" ) );

		assertTrue( kb.isConsistent() );

		assertTrue( kb.isSameAs( term( "x1" ), term( "x3" ) ) );
		assertTrue( kb.isSameAs( term( "x3" ), term( "x1" ) ) );
		assertTrue( kb.isSameAs( term( "x2" ), term( "x4" ) ) );

		assertTrue( kb.getSames( term( "x1" ) ).contains( term( "x3" ) ) );
		assertTrue( kb.getSames( term( "x2" ) ).contains( term( "x4" ) ) );
	}

	@Test
	public void testDifferentFrom2() {
		KnowledgeBase kb = new KnowledgeBase();

		kb.addClass( term( "C" ) );
		kb.addClass( term( "D" ) );
		kb.addDisjointClass( term( "C" ), term( "D" ) );

		kb.addIndividual( term( "a" ) );
		kb.addType( term( "a" ), term( "C" ) );

		kb.addIndividual( term( "b" ) );
		kb.addType( term( "b" ), term( "D" ) );

		kb.classify();

		assertTrue( kb.getDifferents( term( "a" ) ).contains( term( "b" ) ) );
		assertTrue( kb.getDifferents( term( "b" ) ).contains( term( "a" ) ) );
	}

	@Test
	public void testSHOIN() {
		KnowledgeBase kb = new KnowledgeBase();

		kb.addObjectProperty( term( "R1" ) );
		kb.addObjectProperty( term( "invR1" ) );
		kb.addObjectProperty( term( "R2" ) );
		kb.addObjectProperty( term( "invR2" ) );
		kb.addObjectProperty( term( "S1" ) );
		kb.addObjectProperty( term( "invS1" ) );
		kb.addObjectProperty( term( "S2" ) );
		kb.addObjectProperty( term( "invS2" ) );

		kb.addInverseProperty( term( "R1" ), term( "invR1" ) );
		kb.addInverseProperty( term( "R2" ), term( "invR2" ) );
		kb.addInverseProperty( term( "S1" ), term( "invS1" ) );
		kb.addInverseProperty( term( "S2" ), term( "invS2" ) );

		kb.addIndividual( term( "o1" ) );
		kb.addIndividual( term( "o2" ) );

		kb.addSubClass( value( term( "o1" ) ), and( max( term( "invR1" ), 2, ATermUtils.TOP ), all(
				term( "invR1" ), some( term( "S1" ), some( term( "invS2" ), some( term( "R2" ),
						value( term( "o2" ) ) ) ) ) ) ) );

		kb.addSubClass( value( term( "o2" ) ), and( max( term( "invR2" ), 2, ATermUtils.TOP ), all(
				term( "invR2" ), some( term( "S2" ), some( term( "invS1" ), some( term( "R1" ),
						value( term( "o1" ) ) ) ) ) ) ) );

		assertTrue( kb.isConsistent() );

		assertTrue( kb.isSatisfiable( and( value( term( "o1" ) ), some( term( "invR1" ), TOP ) ) ) );
	}

	// @Test public void testEconn1() throws Exception {
	// String ns =
	// "http://www.mindswap.org/2004/multipleOnt/FactoredOntologies/EasyTests/Easy2/people.owl#";
	//        
	// OWLReasoner reasoner = new OWLReasoner();
	// reasoner.setEconnEnabled( true );
	//        
	// reasoner.load( ns );
	//		
	// assertTrue( reasoner.isConsistent() );
	//        
	// assertTrue( !reasoner.isSatisfiable( ResourceFactory.createResource( ns +
	// "Unsat1" ) ) );
	// assertTrue( !reasoner.isSatisfiable( ResourceFactory.createResource( ns +
	// "Unsat2" ) ) );
	// assertTrue( !reasoner.isSatisfiable( ResourceFactory.createResource( ns +
	// "Unsat3" ) ) );
	// assertTrue( !reasoner.isSatisfiable( ResourceFactory.createResource( ns +
	// "Unsat4" ) ) );
	// }

	@Test
	public void testCyclicTBox1() {
		KnowledgeBase kb = new KnowledgeBase();

		ATermAppl C = term( "C" );
		kb.addEquivalentClass( C, not( C ) );

		assertFalse( kb.isConsistent() );
	}



	/**
	 * Test for ticket #123
	 * 
	 * An axiom like A = B or (not B) cause problems in classification process
	 * (runtime exception in CD classification). Due to disjunction A is
	 * discovered to be a told subsumer of B. A is marked as non-primitive but
	 * since marking is done on unfolding map all we see is A = TOP and B is
	 * left as CD. In phase 1, B is tried to be CD-classified but A is eft for
	 * phase 2 thus unclassified at that time causing the exception.
	 */
	@Test
	public void testTopClass2() {
		KnowledgeBase kb = new KnowledgeBase();

		ATermAppl A = term( "A" );
		ATermAppl B = term( "B" );
		ATermAppl C = term( "C" );

		ATermAppl p = term( "p" );
		
		kb.addClass( A );
		kb.addClass( B );
		kb.addClass( C );
		kb.addObjectProperty( p );

		kb.addEquivalentClass( A, or( B, not( B ) ) );
		// the following restriction is only to ensure we don't use the
		// EL classifier
		kb.addSubClass( C, min( p, 2, TOP ) );

		assertTrue( kb.isConsistent() );

		kb.classify();

		assertTrue( kb.isEquivalentClass( A, TOP ) );
		assertFalse( kb.isEquivalentClass( B, TOP ) );
	}
	
	/**
	 * Same as {@link #testTopClass2()} but tests the EL classifier.
	 */
	@Test
	public void testTopClass2EL() {
		// This test was failing due to the issue explained in #157 at some point but not anymore
		// The issue explained in #157 is still valid even though this test passes 
		KnowledgeBase kb = new KnowledgeBase();

		ATermAppl A = term( "A" );
		ATermAppl B = term( "B" );

		kb.addClass( A );
		kb.addClass( B );

		kb.addEquivalentClass( A, or( B, not( B ) ) );

		assertTrue( kb.isConsistent() );

		kb.classify();

		assertTrue( kb.isEquivalentClass( A, TOP ) );
		assertFalse( kb.isEquivalentClass( B, TOP ) );
	}

	/**
	 * An axiom like B = B or (not B) cause problems in classification process
	 * because B was marked disjoint with itself.
	 */
	@Test
	public void testTopClass3() {
		KnowledgeBase kb = new KnowledgeBase();

		ATermAppl A = term( "A" );
		ATermAppl B = term( "B" );
		ATermAppl C = term( "C" );

		kb.addClass( A );
		kb.addClass( B );
		kb.addClass( C );

		kb.addEquivalentClass( A, B );
		kb.addEquivalentClass( B, or( B, not( B ) ) );
		kb.addSubClass( C, A );

		assertTrue( kb.isConsistent() );

		kb.classify();

		assertTrue( kb.isEquivalentClass( A, TOP ) );
		assertTrue( kb.isEquivalentClass( B, TOP ) );
		assertFalse( kb.isEquivalentClass( C, TOP ) );
	}

	/**
	 * not A subClassOf A implies A is TOP
	 */
	@Test
	public void testTopClass4() {
		classes( A, B, C );

		kb.addSubClass( not( A ), A );
		kb.addSubClass( B, A );

		assertTrue( kb.isConsistent() );

		kb.classify();

		assertTrue( kb.isEquivalentClass( A, TOP ) );
	}

	/**
	 * not A subClassOf B does not imply disjointness between concepts A and B.
	 */
	@Test
	public void testNonDisjointness() {
		classes( A, B, C );
		
		kb.addSubClass( not( A ), B );
		kb.addSubClass( C, and( A, B ) );

		assertTrue( kb.isConsistent() );

		kb.classify();

		assertTrue( kb.isSatisfiable( C ) );
	}

	@Test
	public void testCyclicTBox2() {
		KnowledgeBase kb = new KnowledgeBase();

		ATermAppl B = term( "B" );
		ATermAppl C = term( "C" );
		ATermAppl D = term( "D" );

		kb.addClass( B );
		kb.addClass( C );
		kb.addClass( D );
		kb.addSubClass( C, B );
		kb.addSubClass( D, C );
		kb.addEquivalentClass( D, B );

		kb.classify();

		assertTrue( kb.isEquivalentClass( B, C ) );
		assertTrue( kb.isEquivalentClass( B, D ) );
		assertTrue( kb.isEquivalentClass( D, C ) );
	}

	@Test
	public void testCyclicTBox3() {
		List<ATermAppl> classes = Arrays.asList( term( "C0" ), term( "C1" ), term( "C2" ) );

		Taxonomy<ATermAppl> taxonomy = new Taxonomy<ATermAppl>( classes, ATermUtils.TOP,
				ATermUtils.BOTTOM );

		TaxonomyNode<ATermAppl> top = taxonomy.getTop();
		@SuppressWarnings("unchecked")
		TaxonomyNode<ATermAppl>[] nodes = new TaxonomyNode[classes.size()];
		int i = 0;
		for( ATermAppl c : classes ) {
	        nodes[i++] = taxonomy.getNode( c );
        }

		taxonomy.addSuper( classes.get( 1 ), classes.get( 2 ) );
		taxonomy.addSuper( classes.get( 0 ), classes.get( 1 ) );

		taxonomy.merge( top, nodes[0] );

		assertTrue( top.getSupers().isEmpty() );
		assertTrue( top.getEquivalents().containsAll( classes ) );
	}

	@Test
	public void testComplexTypes() {
		KnowledgeBase kb = new KnowledgeBase();

		ATermAppl a = term( "a" );
		ATermAppl p = term( "p" );
		ATermAppl q = term( "q" );

		kb.addIndividual( a );

		kb.addType( a, min( p, 3, TOP ) );
		kb.addType( a, max( q, 2, TOP ) );
		kb.addType( a, min( q, 1, TOP ) );
		kb.addType( a, min( q, 1, TOP ) );

		kb.addObjectProperty( p );
		kb.addObjectProperty( q );

		assertTrue( kb.isConsistent() );
	}

	@Test
	public void testBottomSub() {

		// See also: http://cvsdude.com/trac/clark-parsia/pellet-devel/ticket/7

		KnowledgeBase kb = new KnowledgeBase();

		ATermAppl c = term( "c" );
		kb.addClass( c );
		kb.addSubClass( ATermUtils.BOTTOM, c );
		kb.classify();

		assertTrue( kb.isSubClassOf( ATermUtils.BOTTOM, c ) );
	}

	@Test
	@Ignore("Known to fail because different lexical forms are stored in one canonical literal")
	public void testCanonicalLiteral() {
		KnowledgeBase kb = new KnowledgeBase();

		ATermAppl a = term( "a" );
		ATermAppl p = term( "p" );
		ATermAppl q = term( "q" );
		ATermAppl plain = ATermUtils.makePlainLiteral( "lit" );
		ATermAppl typed = ATermUtils.makeTypedLiteral( "lit", XSDString.getInstance().getName() );

		kb.addIndividual( a );
		kb.addDatatypeProperty( p );
		kb.addDatatypeProperty( q );

		kb.addPropertyValue( p, a, plain );
		kb.addPropertyValue( q, a, typed );

		assertIteratorValues( kb.getDataPropertyValues( p, a ).iterator(),
				new ATermAppl[] { plain } );
		assertIteratorValues( kb.getDataPropertyValues( q, a ).iterator(),
				new ATermAppl[] { typed } );

	}

	@Test
	public void testSimpleABoxRemove() {
		KnowledgeBase kb = new KnowledgeBase();

		ATermAppl a = term( "a" );
		ATermAppl C = term( "C" );
		ATermAppl D = term( "D" );

		kb.addClass( C );
		kb.addClass( D );

		kb.addIndividual( a );
		kb.addType( a, C );
		kb.addType( a, D );

		kb.removeType( a, D );

		assertTrue( kb.isConsistent() );
		assertTrue( kb.isType( a, C ) );
		assertFalse( kb.isType( a, D ) );
	}

	@Test
	public void testABoxRemovalWithAllValues() {
		KnowledgeBase kb = new KnowledgeBase();

		ATermAppl a = term( "a" );
		ATermAppl b = term( "b" );

		ATermAppl C = term( "C" );

		ATermAppl p = term( "p" );

		kb.addClass( C );

		kb.addObjectProperty( p );

		kb.addIndividual( a );
		kb.addIndividual( b );

		kb.addType( a, all( p, C ) );
		kb.addType( b, C );

		kb.addPropertyValue( p, a, b );

		kb.removeType( b, C );

		kb.removePropertyValue( p, a, b );

		assertTrue( kb.isConsistent() );
		assertFalse( kb.isType( b, C ) );
		assertFalse( kb.hasPropertyValue( a, p, b ) );
	}

	@Test
	public void testIncrementalTBoxDisjointRemove1() {
		// Add and remove disjointness

		Properties newOptions = PropertiesBuilder.singleton( "USE_TRACING", "true" );
		Properties savedOptions = PelletOptions.setOptions( newOptions );

		try {
			ATermAppl A = ATermUtils.makeTermAppl( "A" );
			ATermAppl B = ATermUtils.makeTermAppl( "B" );
			ATermAppl C = ATermUtils.makeTermAppl( "C" );
			ATermAppl x = ATermUtils.makeTermAppl( "x" );

			KnowledgeBase kb = new KnowledgeBase();

			kb.addClass( A );
			kb.addClass( B );
			kb.addClass( C );
			kb.addIndividual( x );

			kb.addSubClass( C, A );

			kb.addType( x, C );
			kb.addType( x, B );

			Set<Set<ATermAppl>> expectedTypes = new HashSet<Set<ATermAppl>>();
			expectedTypes.add( Collections.singleton( ATermUtils.TOP ) );
			expectedTypes.add( Collections.singleton( A ) );
			expectedTypes.add( Collections.singleton( B ) );
			expectedTypes.add( Collections.singleton( C ) );

			assertTrue( kb.isConsistent() );

			Set<Set<ATermAppl>> actualTypes = kb.getTypes( x );
			assertEquals( expectedTypes, actualTypes );

			kb.addDisjointClass( A, B );
			assertFalse( kb.isConsistent() );

			assertTrue( kb.removeAxiom( ATermUtils.makeDisjoint( A, B ) ) );
			assertTrue( kb.isConsistent() );

			actualTypes = kb.getTypes( x );
			assertEquals( expectedTypes, actualTypes );

		} finally {
			PelletOptions.setOptions( savedOptions );
		}
	}

	@Test
	public void testIncrementalTBoxDisjointRemove2() {
		// Add and remove disjointness, which is redundant with
		// another axiom

		Properties newOptions = PropertiesBuilder.singleton( "USE_TRACING", "true" );
		Properties savedOptions = PelletOptions.setOptions( newOptions );

		try {
			classes( A, B, C );
			individuals( a );

			kb.addSubClass( C, A );

			kb.addType( a, C );
			kb.addType( a, B );

			Set<Set<ATermAppl>> expectedTypes = new HashSet<Set<ATermAppl>>();
			expectedTypes.add( Collections.singleton( ATermUtils.TOP ) );
			expectedTypes.add( Collections.singleton( A ) );
			expectedTypes.add( Collections.singleton( B ) );
			expectedTypes.add( Collections.singleton( C ) );

			assertTrue( kb.isConsistent() );

			Set<Set<ATermAppl>> actualTypes = kb.getTypes( a );
			assertEquals( expectedTypes, actualTypes );

			kb.addSubClass( A, ATermUtils.makeNot( B ) );
			assertFalse( kb.isConsistent() );

			kb.addDisjointClass( A, B );
			assertFalse( kb.isConsistent() );

			assertTrue( kb.removeAxiom( ATermUtils.makeDisjoint( A, B ) ) );
			assertFalse( kb.isConsistent() );

			assertTrue( kb.removeAxiom( ATermUtils.makeSub( A, ATermUtils.makeNot( B ) ) ) );
			assertTrue( kb.isConsistent() );

			actualTypes = kb.getTypes( a );
			assertEquals( expectedTypes, actualTypes );

		} finally {
			PelletOptions.setOptions( savedOptions );
		}
	}

	@Test
	public void testIncrementalTBoxDisjointRemove3() {
		// Repeat of testIncrementalTBoxDisjointRemove3,
		// using n-ary disjoint axiom syntax

		Properties newOptions = PropertiesBuilder.singleton( "USE_TRACING", "true" );
		Properties savedOptions = PelletOptions.setOptions( newOptions );

		try {
			classes( A, B, C );
			individuals( a );

			kb.addSubClass( C, A );

			kb.addType( a, C );
			kb.addType( a, B );

			Set<Set<ATermAppl>> expectedTypes = new HashSet<Set<ATermAppl>>();
			expectedTypes.add( Collections.singleton( ATermUtils.TOP ) );
			expectedTypes.add( Collections.singleton( A ) );
			expectedTypes.add( Collections.singleton( B ) );
			expectedTypes.add( Collections.singleton( C ) );

			assertTrue( kb.isConsistent() );

			Set<Set<ATermAppl>> actualTypes = kb.getTypes( a );
			assertEquals( expectedTypes, actualTypes );

			kb.addSubClass( A, ATermUtils.makeNot( B ) );
			assertFalse( kb.isConsistent() );

			ATermList list = ATermUtils.toSet( new ATerm[] { A, B, D }, 3 );
			kb.addDisjointClasses( list );
			assertFalse( kb.isConsistent() );

			assertTrue( kb.removeAxiom( ATermUtils.makeDisjoints( list ) ) );
			assertFalse( kb.isConsistent() );

			assertTrue( kb.removeAxiom( ATermUtils.makeSub( A, ATermUtils.makeNot( B ) ) ) );
			assertTrue( kb.isConsistent() );

			actualTypes = kb.getTypes( a );
			assertEquals( expectedTypes, actualTypes );
		} finally {
			PelletOptions.setOptions( savedOptions );
		}
	}

	@Test
	public void testIncrementalTBoxDisjointRemove4() {
		// test that a disjoint axiom absorbed into domain axiom cannot
		// be removed

		Properties newOptions = new PropertiesBuilder().set( "USE_TRACING", "true" ).set(
				"USE_ROLE_ABSORPTION", "true" ).build();
		Properties savedOptions = PelletOptions.setOptions( newOptions );

		try {
			ATermAppl A = ATermUtils.makeTermAppl( "A" );
			ATermAppl B = ATermUtils.makeTermAppl( "B" );
			ATermAppl p = ATermUtils.makeTermAppl( "p" );

			KnowledgeBase kb = new KnowledgeBase();

			kb.addClass( A );
			kb.addClass( B );
			kb.addObjectProperty( p );

			ATermAppl or1 = or( A, some( p, A ) );
			ATermAppl or2 = or( B, some( p, B ) );

			ATermList list = ATermUtils.toSet( new ATerm[] { or1, or2 }, 2 );
			kb.addDisjointClasses( list );

			assertTrue( kb.isConsistent() );

			ATermAppl disjoint = ATermUtils.makeDisjoints( list );
			assertFalse( kb.removeAxiom( disjoint ) );
		} finally {
			PelletOptions.setOptions( savedOptions );
		}
	}

	@Test
	public void testIncrementalTBoxDisjointRemove5() {
		// Same as testIncrementalTBoxDisjointRemove4 but
		// uses n-ary disjointness axioms

		Properties newOptions = new PropertiesBuilder().set( "USE_TRACING", "true" ).set(
				"USE_ROLE_ABSORPTION", "true" ).build();
		Properties savedOptions = PelletOptions.setOptions( newOptions );

		try {
			ATermAppl A = ATermUtils.makeTermAppl( "A" );
			ATermAppl B = ATermUtils.makeTermAppl( "B" );
			ATermAppl p = ATermUtils.makeTermAppl( "p" );

			KnowledgeBase kb = new KnowledgeBase();

			kb.addClass( A );
			kb.addClass( B );
			kb.addObjectProperty( p );

			ATermAppl or1 = or( A, some( p, A ) );
			ATermAppl or2 = or( B, some( p, B ) );

			kb.addDisjointClass( or1, or2 );

			assertTrue( kb.isConsistent() );

			ATermAppl disjoint = ATermUtils.makeDisjoint( or1, or2 );
			assertFalse( kb.removeAxiom( disjoint ) );
		} finally {
			PelletOptions.setOptions( savedOptions );
		}
	}

	@Test
	public void testIncrementalTBoxDisjointRemove6() {
		// test that a disjoint axiom absorbed into range axiom cannot
		// be removed

		Properties newOptions = new PropertiesBuilder().set( "USE_TRACING", "true" ).set(
				"USE_ROLE_ABSORPTION", "true" ).build();
		Properties savedOptions = PelletOptions.setOptions( newOptions );

		try {
			ATermAppl A = ATermUtils.makeTermAppl( "A" );
			ATermAppl p = ATermUtils.makeTermAppl( "p" );

			KnowledgeBase kb = new KnowledgeBase();

			kb.addClass( A );
			kb.addObjectProperty( p );
			
			kb.addSubClass( TOP, all( p, A ) );
			
			Role r = kb.getRole( p );

			assertTrue( kb.isConsistent() );
			assertTrue( r.getRanges().contains( A ) );
			
			assertFalse( kb.removeAxiom( ATermUtils.makeSub( TOP, all( p, A ) ) ) );			
		} finally {
			PelletOptions.setOptions( savedOptions );
		}
	}
	
	@Test
	public void testAssertedSameAs() {
		// This test case is to test the processing of sameAs processing
		// where there are redundancies in the assertions (see ticket 138)

		KnowledgeBase kb = new KnowledgeBase();

		ATermAppl a = term( "a" );
		ATermAppl b = term( "b" );
		ATermAppl c = term( "c" );
		ATermAppl d = term( "d" );
		ATermAppl e = term( "e" );
		ATermAppl f = term( "f" );

		kb.addIndividual( a );
		kb.addIndividual( b );
		kb.addIndividual( c );
		kb.addIndividual( d );
		kb.addIndividual( e );
		kb.addIndividual( f );

		kb.addSame( a, b );
		kb.addSame( b, c );
		kb.addSame( c, d );
		kb.addSame( a, d );
		kb.addSame( b, d );
		kb.addSame( e, f );
		kb.addDifferent( e, f );

		assertFalse( kb.isConsistent() );
	}

	@Test
	public void testSubPropertyRestore() {
		// This test case is to test the restoring of edges with
		// subproperties (see ticket 109)

		KnowledgeBase kb = new KnowledgeBase();

		ATermAppl a = term( "a" );
		ATermAppl b = term( "b" );
		ATermAppl c = term( "c" );
		ATermAppl d = term( "d" );

		ATermAppl p = term( "p" );
		ATermAppl q = term( "q" );
		ATermAppl invP = term( "invP" );
		ATermAppl invQ = term( "invQ" );

		kb.addIndividual( a );
		kb.addIndividual( b );
		kb.addIndividual( c );
		kb.addIndividual( d );

		kb.addObjectProperty( p );
		kb.addObjectProperty( q );
		kb.addObjectProperty( invP );
		kb.addObjectProperty( invQ );

		// first add the ABox assertions to make sure none is ignored
		kb.addPropertyValue( p, a, b );
		kb.addPropertyValue( q, a, b );

		// add the subproperty axiom later
		kb.addSubProperty( p, q );
		kb.addInverseProperty( p, invP );
		kb.addInverseProperty( q, invQ );

		// force b to be merged to one of c or d
		kb.addType( b, or( value( c ), value( d ) ) );

		assertTrue( kb.isConsistent() );

		// ask a query that will force the merge to be restored. with the bug
		// the q would not be restored causing either an internal exception or
		// the query to fail
		assertTrue( kb.isType( b, and( some( invP, value( a ) ), some( invQ, value( a ) ) ) ) );
	}

	@Test
	public void testInverseProperty() {
		// This test case is to test the retrieval of inverse
		// properties (see ticket 117)

		KnowledgeBase kb = new KnowledgeBase();

		ATermAppl p = term( "p" );
		ATermAppl q = term( "q" );
		ATermAppl invP = term( "invP" );
		ATermAppl invQ = term( "invQ" );

		kb.addObjectProperty( p );
		kb.addObjectProperty( q );
		kb.addObjectProperty( invP );
		kb.addObjectProperty( invQ );
		kb.addInverseProperty( p, invP );
		kb.addInverseProperty( q, invQ );

		assertEquals( Collections.singleton( invP ), kb.getInverses( p ) );
		assertEquals( Collections.singleton( invQ ), kb.getInverses( q ) );
		assertEquals( Collections.singleton( p ), kb.getInverses( invP ) );
		assertEquals( Collections.singleton( q ), kb.getInverses( invQ ) );
	}

	@Test
	public void testUndefinedTerms() {
		// This test case is to test the retrieval of equivalences
		// for undefined classes/properties (see ticket 90)

		KnowledgeBase kb = new KnowledgeBase();

		ATermAppl C = term( "C" );
		kb.addClass( C );

		ATermAppl p = term( "p" );
		kb.addObjectProperty( p );

		ATermAppl undef = term( "undef" );

		assertEquals( Collections.singleton( p ), kb.getAllEquivalentProperties( p ) );
		assertEquals( Collections.emptySet(), kb.getEquivalentProperties( p ) );
		assertEquals( Collections.singleton( Collections.singleton( TermFactory.BOTTOM_OBJECT_PROPERTY ) ), kb.getSubProperties( p ) );
		assertEquals( Collections.singleton( Collections.singleton( TermFactory.TOP_OBJECT_PROPERTY ) ), kb.getSuperProperties( p ) );

		assertEquals( Collections.singleton( C ), kb.getAllEquivalentClasses( C ) );
		assertEquals( Collections.emptySet(), kb.getEquivalentClasses( C ) );
		assertEquals( Collections.emptySet(), kb.getSubClasses( p ) );
		assertEquals( Collections.emptySet(), kb.getSuperClasses( p ) );

		assertEquals( Collections.emptySet(), kb.getAllEquivalentProperties( undef ) );
		assertEquals( Collections.emptySet(), kb.getEquivalentProperties( undef ) );
		assertEquals( Collections.emptySet(), kb.getSubProperties( undef ) );
		assertEquals( Collections.emptySet(), kb.getSuperProperties( undef ) );

		assertEquals( Collections.emptySet(), kb.getAllEquivalentClasses( undef ) );
		assertEquals( Collections.emptySet(), kb.getEquivalentClasses( undef ) );
		assertEquals( Collections.emptySet(), kb.getSubClasses( undef ) );
		assertEquals( Collections.emptySet(), kb.getSuperClasses( undef ) );
	}

	@Test
	public void testDatatypeReasoner() {
		// This test case checks datatype reasoner to handle obvious
		// contradictions, e.g. intersection of datatypes D and not(D)
		// See the bug reported in ticket #127

		dataProperties( p );
		individuals( a );

		kb.addRange( p, Datatypes.FLOAT );

		kb.addPropertyValue( p, a, literal( 42.0f ) );

		assertTrue( kb.isConsistent() );

		assertTrue( kb.isType( a, some( p, Datatypes.FLOAT ) ) );
	}

	@Test
	public void testCRonDTP() {
		// Test for ticket #143

		Properties newOptions = new PropertiesBuilder().set( "SILENT_UNDEFINED_ENTITY_HANDLING",
				"false" ).build();
		Properties savedOptions = PelletOptions.setOptions( newOptions );
		
		try {
			KnowledgeBase kb = new KnowledgeBase();
	
			ATermAppl p = term( "p" );
			ATermAppl c = and( all( p, value( literal( "s" ) ) ), min( p, 2, value( literal( "l" ) ) ) );
	
			kb.addDatatypeProperty( p );
	
			assertFalse( kb.isSatisfiable( c ) );
		} 
		finally {
			PelletOptions.setOptions( savedOptions );
		}		
	}
	
	@Test
	public void testInvalidTransitivity2() {
		KBLoader[] loaders = { new JenaLoader() };
		for( KBLoader loader : loaders ) {
			KnowledgeBase kb = loader.createKB( base + "invalidTransitivity.owl" );
			
			for( Role r : kb.getRBox().getRoles() ) {
				if ( !ATermUtils.isBuiltinProperty( r.getName() ) ) {
					assertTrue(r.toString(), r.isSimple());
					assertFalse(r.toString(), r.isTransitive());
				}
			}

			for( ATermAppl p : kb.getObjectProperties() ) {
				if ( !ATermUtils.isBuiltinProperty( p ) ) {
					assertFalse( p.toString(), kb.isTransitiveProperty( p ) );
				}
			}
		}
	}

	
	@Test
	public void testInternalization() {
		KnowledgeBase kb = new KnowledgeBase();

		ATermAppl A = term( "A" );
		ATermAppl B = term( "B" );
		ATermAppl C = term( "C" );

		kb.addClass( A );
		kb.addClass( B );
		kb.addClass( C );

		kb.addSubClass( TOP, and( or( B, not( A ) ), or( C, not( B ) ) ) );

		assertSubClass( kb, A, B );

		assertSubClass( kb, B, C );
		
		kb.classify();
	}
	
	@Test
	public void testNominalCache() {
		// this case tests isMergable check and specifically the correctness of
		// ConceptCache.isIndependent value. concept C below will be merged to
		// either a or b with a dependency. if that dependency is not recorded
		// isMergable returns incorrect results 
		
		KnowledgeBase kb = new KnowledgeBase();

		ATermAppl A = term( "A" );
		ATermAppl B = term( "B" );
		ATermAppl C = term( "C" );
		ATermAppl a = term( "a" );
		ATermAppl b = term( "b" );

		kb.addClass( C );
		kb.addIndividual( a );
		kb.addIndividual( b );

		kb.addSubClass( C, oneOf( a, b ) );
		kb.addEquivalentClass( A, oneOf( a ) );
		kb.addEquivalentClass( B, oneOf( b ) );
		kb.addDisjointClass( A, B );

		assertTrue( kb.isConsistent() );
		
		assertTrue( kb.isSatisfiable( C ) );

		assertNotSubClass( kb, C, A );
		assertNotSubClass( kb, C, B );

		assertNotSubClass( kb, A, C );
		assertNotSubClass( kb, B, C );
		assertFalse( kb.isType( a, C ) );
		assertFalse( kb.isType( b, C ) );
		assertFalse( kb.isType( a, not( C ) ) );
		assertFalse( kb.isType( b, not( C ) ) );
	}
	
	/*
	 * From bug #312
	 */
	@Test
	public void testNominalValueInteraction() {
		KnowledgeBase kb = new KnowledgeBase();
		
		ATermAppl t1 = term("T1");
		ATermAppl p = term("p");
		ATermAppl i1 = term("i1");
		ATermAppl i21 = term("i21");
		ATermAppl i22 = term("i22");
		ATermAppl t1eq = ATermUtils.makeAnd( ATermUtils.makeHasValue( p, i21 ), ATermUtils.makeHasValue( p, i22 ) );
		ATermAppl test = term("test");
		
		kb.addClass( t1 );
		kb.addObjectProperty( p );
		kb.addIndividual( i1 );
		kb.addIndividual( i21 );
		kb.addIndividual( i22 );
		kb.addIndividual( test );
		
		kb.addEquivalentClass( t1, t1eq );
		kb.addSame( i1, i21 );
		kb.addSame( i21, i1 );
		
		kb.addPropertyValue( p, test, i21 );
		kb.addPropertyValue( p, test, i22 );
		
		Set<ATermAppl> t1inds = kb.retrieve( t1eq, kb.getIndividuals() );
		assertEquals( "Individual test should be of type T1. ", Collections.singleton( test ), t1inds);
		
	}
	
	@Test
	public void testMultiEdgesWithTransitivity() {
		// Demonstrate the problem described in #223
		// This test is more complicated than necessary to ensure
		// that bug is triggered regardless of the traversal order
		// in getTransitivePropertyValues function. With the bug
		// if we visit b before c we will miss the value e and if we visit c
		// before b we miss the value d.
		
		KnowledgeBase kb = new KnowledgeBase();

		ATermAppl a = term( "a" );
		ATermAppl b = term( "b" );
		ATermAppl c = term( "c" );
		ATermAppl d = term( "d" );
		ATermAppl e = term( "e" );
		
		ATermAppl p = term( "p" );
		ATermAppl r = term( "r" );
		ATermAppl s = term( "s" );

		kb.addObjectProperty( p );
		kb.addObjectProperty( r );
		kb.addObjectProperty( s );
		
		kb.addTransitiveProperty( r );
		kb.addTransitiveProperty( s );
		
		kb.addSubProperty( r, p );
		kb.addSubProperty( s, p );		

		kb.addIndividual( a );
		kb.addIndividual( b );
		kb.addIndividual( c );
		kb.addIndividual( d );
		kb.addIndividual( e );
		
		kb.addPropertyValue( r, a, b );
		kb.addPropertyValue( r, b, c );
		kb.addPropertyValue( r, b, d );
		kb.addPropertyValue( s, a, c );
		kb.addPropertyValue( s, c, b );
		kb.addPropertyValue( s, c, e );

		assertTrue( kb.hasPropertyValue( a, p, b ) );
		assertTrue( kb.hasPropertyValue( a, p, c ) );
		assertTrue( kb.hasPropertyValue( a, p, d ) );
		assertTrue( kb.hasPropertyValue( a, p, e ) );
		assertIteratorValues( kb.getPropertyValues( p, a ).iterator(), new ATermAppl[] { b, c, d, e } );
	}	
	
	@Test
	public void testLiteralMerge() {
		// Tests the issue described in #250
		KnowledgeBase kb = new KnowledgeBase();

		ATermAppl a = term( "a" );
		ATermAppl b = term( "b" );
		ATermAppl p = term( "p" );
		
		kb.addIndividual( a );
		kb.addIndividual( b );

		kb.addDatatypeProperty( p );
		kb.addFunctionalProperty( p );
		
		// a has a p-successor which is an integer
		kb.addType( a, some( p, XSDInteger.getInstance().getName() ) );
		// bogus axiom to force full datatype reasoning
		kb.addType( a, max( p, 2, TOP_LIT ) );
		
		// b has an asserted p value which is a string
		kb.addPropertyValue( p, b, literal( "b" ) );

		// check consistency whihc 
		assertTrue( kb.isConsistent() );

		// this query will force a and b to be merged which will cause
		// their p values to be merged
		assertTrue( kb.isDifferentFrom( a, b ) );
	}
	
	@Test
	public void testDatatypeSubProperty1a() {
		// Tests the issue described in #250
		// The sub/equivalent property query was turned into a satisfiability
		// test where a fresh datatype is used. If the property in question
		// has a range the intersection of defined range with the fresh 
		// datatype returned to be empty causing the reasoner to conclude
		// subproperty relation hols even though it does not

		KnowledgeBase kb = new KnowledgeBase();

		ATermAppl p = term( "p" );
		ATermAppl q = term( "q" );
		
		ATermAppl[] ranges = {
			null, XSDInteger.getInstance().getName(), XSDString.getInstance().getName()
		};
		
		for( ATermAppl rangeP : ranges ) {
			for( ATermAppl rangeQ : ranges ) {
				kb.clear();

				kb.addDatatypeProperty( p );
				kb.addDatatypeProperty( q );
				
				if( rangeP != null ) {
	                kb.addRange( p, rangeP );
                }
				if( rangeQ != null ) {
	                kb.addRange( q, rangeQ );
                }
				
				assertTrue( kb.isConsistent() );

				assertFalse( kb.isSubPropertyOf( p, q ) );
				assertFalse( kb.isSubPropertyOf( q, p ) );

				assertFalse( kb.isEquivalentProperty( p, q ) );
				assertFalse( kb.isEquivalentProperty( q, p ) );
			}			
		}
	}
	
	@Test
	public void testDatatypeSubProperty1b() {
		// Another variation of testDatatypeSubProperty1 where super
		// property has a range but not the sub property

		KnowledgeBase kb = new KnowledgeBase();

		ATermAppl C = term( "C" );
		
		ATermAppl p = term( "p" );
		ATermAppl q = term( "q" );
		
		kb.addClass( C );
		
		kb.addDatatypeProperty( p );
		kb.addDatatypeProperty( q );
		
		kb.addDomain( p, C );
		
		kb.addRange( q, XSDInteger.getInstance().getName() );
		
		kb.addSubClass( C, some( q, TOP_LIT ) );
		
		assertTrue( kb.isConsistent() );

		assertFalse( kb.isSubPropertyOf( p, q ) );
		assertFalse( kb.isSubPropertyOf( q, p ) );

		assertFalse( kb.isEquivalentProperty( p, q ) );
		assertFalse( kb.isEquivalentProperty( q, p ) );
	}

	@Test
	public void testCachedNominalEdge() {
		KnowledgeBase kb = new KnowledgeBase();

		ATermAppl A = term( "A" );
		ATermAppl B = term( "B" );
		ATermAppl C = term( "C" );
		ATermAppl D = term( "D" );
		
		ATermAppl p = term( "p" );
		
		ATermAppl b = term( "b" );
		ATermAppl c = term( "c" );
		
		kb.addClass( A );
		kb.addClass( B );
		kb.addClass( C );
		kb.addClass( D );
		
		kb.addObjectProperty( p );

		kb.addIndividual( b );
		kb.addIndividual( c );
		
		kb.addEquivalentClass( A, oneOf( b, c ) );
		kb.addEquivalentClass( B, hasValue( p, b ) );
		kb.addEquivalentClass( C, hasValue( p, c ) );
		kb.addEquivalentClass( D, and( some( p, A ), min( p, 1, value(b) ), min( p, 1, value(c) ), max( p, 1,
				TOP ) ) );		
		
		assertTrue( kb.isConsistent() );

		kb.classify();
		
		assertTrue( kb.isSubClassOf( D, B ) );
		assertTrue( kb.isSubClassOf( D, C ) );
	}
	

	@Test
	public void testDisjoints() {
		KnowledgeBase kb = new KnowledgeBase();

		ATermAppl A = term( "A" );
		ATermAppl B = term( "B" );
		ATermAppl C = term( "C" );
		ATermAppl D = term( "D" );

		kb.addClass( A );
		kb.addClass( B );
		kb.addClass( C );
		kb.addClass( D );
		
		kb.addSubClass( B, A );
		kb.addSubClass( D, C );
		kb.addComplementClass( B, C );
		
		assertTrue( kb.isConsistent() );		

		assertIteratorValues( kb.getDisjointClasses( TOP ).iterator(),
				new Object[] { singleton( BOTTOM ) } );
		assertIteratorValues( kb.getDisjointClasses( A ).iterator(),
				new Object[] { singleton( BOTTOM ) } );
		assertIteratorValues( kb.getDisjointClasses( B ).iterator(), new Object[] {
				singleton( BOTTOM ), singleton( C ), singleton( D ) } );
		assertIteratorValues( kb.getDisjointClasses( C ).iterator(), new Object[] {
				singleton( BOTTOM ), singleton( B ) } );
		assertIteratorValues( kb.getDisjointClasses( D ).iterator(), new Object[] {
				singleton( BOTTOM ), singleton( B ) } );	
		assertIteratorValues( kb.getDisjointClasses( BOTTOM ).iterator(), new Object[] {
			singleton( TOP ), singleton( A ), singleton( B ), singleton( C ), singleton( D ),
			singleton( BOTTOM ) } );	

		assertIteratorValues( kb.getComplements( TOP ).iterator(), new Object[] { BOTTOM } );
		assertTrue( kb.getComplements( A ).isEmpty() );
		assertIteratorValues( kb.getComplements( B ).iterator(), new Object[] { C } );
		assertIteratorValues( kb.getComplements( C ).iterator(), new Object[] { B } );
		assertTrue( kb.getComplements( D ).isEmpty() );
		assertIteratorValues( kb.getComplements( BOTTOM ).iterator(), new Object[] { TOP } );

		assertIteratorValues( kb.getDisjointClasses( not( A ) ).iterator(),
				new Object[] { singleton( BOTTOM ), singleton( A ), singleton( B ) } );
		assertIteratorValues( kb.getDisjointClasses( not( B ) ).iterator(), new Object[] {
				singleton( BOTTOM ), singleton( B ) } );
		assertIteratorValues( kb.getDisjointClasses( not( C ) ).iterator(), new Object[] {
				singleton( BOTTOM ), singleton( C ), singleton( D ) } );
		assertIteratorValues( kb.getDisjointClasses( not( D ) ).iterator(), new Object[] {
				singleton( BOTTOM ), singleton( D ) } );		

		assertIteratorValues( kb.getComplements( not( A ) ).iterator(), new Object[] { A } );
		assertIteratorValues( kb.getComplements( not( B ) ).iterator(), new Object[] { B } );
		assertIteratorValues( kb.getComplements( not( C ) ).iterator(), new Object[] { C } );
		assertIteratorValues( kb.getComplements( not( D ) ).iterator(), new Object[] { D } );
	}
	
	/**
	 * But #305
	 */
	@Test
	public void testDisjointDataProperties() {
		KnowledgeBase kb = new KnowledgeBase();
		
		ATermAppl p = term( "p" );
		ATermAppl q = term( "q" );
		
		kb.addDatatypeProperty( p );
		kb.addDatatypeProperty( q );
		kb.addRange( p, Datatypes.INT );
		kb.addRange( q, Datatypes.INT );
		
		assertFalse( "p and q should not be disjoint!", kb.isDisjointProperty( p, q ) );
	}
	

	@Test
	public void testRemovePruned() {
		KnowledgeBase kb = new KnowledgeBase();

		ATermAppl A = term( "A" );
		ATermAppl B = term( "B" );
		ATermAppl C = term( "C" );
		
		ATermAppl p = term( "p" );
		
		ATermAppl a = term( "a" );
		ATermAppl b = term( "b" );

		kb.addClass( A );
		kb.addClass( B );
		kb.addClass( C );

		kb.addObjectProperty( p );

		kb.addIndividual( a );
		kb.addIndividual( b );
		
		kb.addEquivalentClass( A, value( a ) );
		kb.addSubClass( A, all( inv(p), not( B ) ) );
		kb.addSubClass( B, or( some( p, A ), C ) );
		
		kb.addType( b, B );
		
		assertTrue( kb.isConsistent() );

		assertTrue( kb.isType( b, C ) );
		assertFalse( kb.isType( a, C ) );
	}
	
	@Test
	public void testDataAssertions() {
		KnowledgeBase kb = new KnowledgeBase();

		ATermAppl A = term( "A" );
		
		ATermAppl p = term( "p" );
		
		ATermAppl a = term( "a" );
		
		ATermAppl oneDecimal = literal( "1", Datatypes.DECIMAL );
		ATermAppl oneInteger = literal( "1", Datatypes.INTEGER );
		ATermAppl oneByte = literal( "1", Datatypes.BYTE );
		ATermAppl oneFloat = literal( "1", Datatypes.FLOAT );

		kb.addClass( A );

		kb.addDatatypeProperty( p );

		kb.addIndividual( a );

		kb.addPropertyValue( p, a, oneInteger );
		assertTrue( kb.isConsistent() );

		assertTrue( kb.hasPropertyValue( a, p, oneDecimal ) );
		assertTrue( kb.hasPropertyValue( a, p, oneInteger ) );
		assertTrue( kb.hasPropertyValue( a, p, oneByte ) );
		assertFalse( kb.hasPropertyValue( a, p, oneFloat ) );
		assertEquals( singletonList( oneInteger ), kb.getDataPropertyValues( p, a ) );
	}
	
	@Test
	public void testDatatypeIntersection() {
		KnowledgeBase kb = new KnowledgeBase();

		ATermAppl A = term( "A" );		
		ATermAppl p = term( "p" );		
		ATermAppl a = term( "a" );		
		
		ATermAppl zeroDecimal = literal( "0", Datatypes.DECIMAL );
		ATermAppl zeroInteger = literal( "0", Datatypes.INTEGER );
		ATermAppl zeroByte = literal( "0", Datatypes.BYTE );
		ATermAppl zeroFloat = literal( "0", Datatypes.FLOAT );

		kb.addClass( A );
		kb.addDatatypeProperty( p );
		kb.addIndividual( a );
		
		kb.addSubClass( A, some( p, Datatypes.NON_POSITIVE_INTEGER ) );
		kb.addSubClass( A, all( p, Datatypes.NON_NEGATIVE_INTEGER ) );
		
		kb.addType( a, A );
		
		assertTrue( kb.isConsistent() );

		assertTrue( kb.hasPropertyValue( a, p, zeroDecimal ) );
		assertTrue( kb.hasPropertyValue( a, p, zeroInteger ) );
		assertTrue( kb.hasPropertyValue( a, p, zeroByte ) );
		assertFalse( kb.hasPropertyValue( a, p, zeroFloat ) );
		assertEquals( singletonList( zeroDecimal ), kb.getDataPropertyValues( p, a ) );
	}
	
	@Test
	public void testDataOneOf() {
		KnowledgeBase kb = new KnowledgeBase();

		ATermAppl A = term( "A" );
		
		ATermAppl p = term( "p" );
		ATermAppl q = term( "q" );
		
		ATermAppl a = term( "a" );
		
		ATermAppl lit1 = literal( "test" );
		ATermAppl lit2 = literal( "1", Datatypes.DECIMAL );
		
		kb.addClass( A );

		kb.addDatatypeProperty( p );
		kb.addDatatypeProperty( q );

		kb.addIndividual( a );
		
		kb.addType( a, A );

		kb.addSubClass( A, min( p, 1, TOP_LIT ) );
		kb.addRange( p, oneOf( lit1 ) );
		
		kb.addSubClass( A, some( q, TOP_LIT ) );
		kb.addRange( q, oneOf( lit2 ) );
		
		assertTrue( kb.isConsistent() );


		assertEquals( singletonList( lit1 ), kb.getDataPropertyValues( p, a ) );
		assertEquals( singletonList( lit2 ), kb.getDataPropertyValues( q, a ) );
		assertTrue( kb.hasPropertyValue( a, p, lit1 ) );
		assertTrue( kb.hasPropertyValue( a, q, lit2 ) );
	}	

	@Test
	public void testDisjointSelf() {
		KnowledgeBase kb = new KnowledgeBase();

		ATermAppl A = term( "A" );		
		ATermAppl p = term( "p" );		
		
		kb.addClass( A );
		kb.addObjectProperty( p );

		kb.addDisjointClasses( Arrays.asList( A, self(p) ) );		

		kb.classify();
		
		assertTrue( kb.isSatisfiable( A ) );
	}
	
	@Test
	public void testDisjointPropertiesCache() {
		// test case for issue #336 to verify AbstractConceptCache.isMergable does
		// not return incorrect results.
		KnowledgeBase kb = new KnowledgeBase();
		
		ATermAppl p1 = term( "p1" );
		ATermAppl p2 = term( "p2" );
		
		ATermAppl a = term( "a" );
		ATermAppl b = term( "b" );
		ATermAppl c = term( "c" );
		
		kb.addObjectProperty( p1 );
		kb.addObjectProperty( p2 );
		kb.addDisjointProperty( p1, p2 );
		
		kb.addIndividual( a );
		kb.addIndividual( b );
		kb.addIndividual( c );
		
		kb.addPropertyValue( p1, a, c );
		kb.addPropertyValue( p2, b, a );
		
		ATermAppl notp1a = ATermUtils.makeNot( ATermUtils.makeHasValue( p1, a ) );
		
		// no caching so consistency checking will be used here
		assertFalse( kb.isType( a, notp1a ) );
		assertTrue( kb.isType( b, notp1a ) );
		
		// call getInstances so some caching will happen
		assertEquals( singleton( b ), kb.getInstances( notp1a, false ) );
		
		// now cached nodes will be used for mergable check 
		assertFalse( kb.isType( a, notp1a ) );
		assertTrue( kb.isType( b, notp1a ) );
		
	}
	
	@Test
	public void testSynoymClassification() {	
		// Fixes the problem identified in #270. If there are two equivalent concepts
		// where one is primitive and the other is non-primitive CD classifier was
		// picking primitive flag and returning incorrect classification results.
		
		KnowledgeBase kb = new KnowledgeBase();

		ATermAppl A = term( "A" );
		ATermAppl B = term( "B" );
		ATermAppl C = term( "C" );
		
		ATermAppl p = term( "p" );
	
		kb.addClass( A );
		kb.addClass( B );
		kb.addClass( C );
		
		kb.addDatatypeProperty( p );
		
		// B is completely defined except this equivalence
		kb.addEquivalentClass( A, B );
		// A is not primitive because of the domain axiom
		kb.addDomain( p, A );
		// C should be inferred to be a subclass of A and B
		kb.addSubClass( C, some(p, TOP_LIT ) );
		
		kb.classify();
		
		assertSubClass( kb, C, A );
		assertSubClass( kb, C, B );
	}	
	
	@Test
	public void testUndefinedProperty() {	
		// Test for #351. Calling getPropertyValues for an undefinde property should not throw NPE
		
		KnowledgeBase kb = new KnowledgeBase();

		ATermAppl p = term( "p" );
		ATermAppl q = term( "q" );
		
		ATermAppl a = term( "a" );
		ATermAppl b = term( "b" );
		
		kb.addObjectProperty( p );
		
		kb.addIndividual( a );
		kb.addIndividual( b );
	
		kb.addPropertyValue( p, a, b );
		
		kb.isConsistent();
		
		assertTrue( kb.getPropertyValues( q ).isEmpty() );
	}
	
	@Test
	public void testGetSubClassBehavior() {
		classes( c, d, e );
		
		kb.addEquivalentClass( c, d );
		kb.addSubClass( e, d );
		
		Set<Set<ATermAppl>> result = new HashSet<Set<ATermAppl>>();
		result.add( Collections.singleton( ATermUtils.BOTTOM ) );
		result.add( Collections.singleton( e ) );
		assertEquals( result, kb.getSubClasses( c, false ) );
	}
	
	
	@Test
	public void test354() {
		// test case for issue #354.
		classes( B );
		objectProperties( p );
		individuals( a, b, c );
			
		kb.addFunctionalProperty( p );		
		
		kb.addEquivalentClass( B, oneOf( b, c ) );
		
		assertFalse( kb.isType( a, not( B ) ) );

		kb.isSatisfiable( B );
		
		assertFalse( kb.isType( a, not( B ) ) );		
	}
	
	@Test
	public void test370() {
		// test case for issue #370.
		dataProperties( p );
		individuals( a );
		
		ATermAppl dt = restrict( Datatypes.DECIMAL, 
				minExclusive( literal( "0.99", Datatypes.DECIMAL ) ),
				maxExclusive( literal( "1.01", Datatypes.DECIMAL ) ) );		
		
		kb.addType( a, min( p, 3, dt ) );
		
		assertTrue( kb.isConsistent() );
	}		
	
	@Test
	public void test348() {
		// test case for issue #348.
		classes( B, C, D, E );
		individuals( a, b, c, d, e );
		
		kb.addType( a, oneOf( b, c ) );
		kb.addType( a, oneOf( d, e ) );
		
		kb.addType( b, B );
		kb.addType( c, C );
		kb.addType( d, D );
		kb.addType( e, E );
		
		assertTrue( kb.isConsistent() );		
				
		assertEquals( Collections.singleton( b ), kb.retrieve( B, Arrays.asList( a, b, d, e ) ) );
		assertEquals( Collections.singleton( c ), kb.retrieve( C, Arrays.asList( a, c, d, e ) ) );
		assertEquals( Collections.singleton( d ), kb.retrieve( D, Arrays.asList( a, d, b, c ) ) );
		assertEquals( Collections.singleton( e ), kb.retrieve( E, Arrays.asList( a, e, b, c ) ) );
	}	
	
	
	@Test
	public void test375() {
		// test case for issue #375.
		classes( A, B, C );
		dataProperties( p );
		
		ATermAppl dt = restrict( Datatypes.INTEGER, minExclusive( literal( 1 ) ) );

		kb.addRange( p, XSDInteger.getInstance().getName() );
		kb.addSubClass( A, C );
		kb.addEquivalentClass( A, some( p, dt ) );
		kb.addSubClass( B, C );
		kb.addEquivalentClass( B, hasValue( p, literal( 2 ) ) );
		
		assertTrue( kb.isConsistent() );
		
		assertSubClass( kb, B, A );
		
		kb.classify();
		kb.printClassTree();
		
		assertSubClass( kb, B, A );
		
	}	
	
	@Test
	public void minCardinalityOnIrreflexive() {
		// related to #400
		classes( A );
		objectProperties( p );
		individuals( a );

		kb.addIrreflexiveProperty( p );
		kb.addSubClass( A, min( p, 1, TOP ) );
		kb.addEquivalentClass( A, oneOf( a ) );
		kb.addEquivalentClass( TOP, A );
		
		assertFalse( kb.isConsistent() );
	}
	
	@Test
	public void subPropertyWithSameRange() {
		// test #435
		classes( A );
		objectProperties( p, q, r );

		kb.addRange( p, A );
		kb.addDomain( p, some( q, A ) );
				
		assertTrue( kb.isConsistent() );
		
		assertFalse( kb.isSubPropertyOf( p, q ) );
		assertFalse( kb.isSubPropertyOf( q, p ) );
	}
	
	@Test
	public void roleAbsorptionWithQCR() {
		classes( A, B, C );
		objectProperties( p );
		
		kb.addSubClass( A, B );
		kb.addEquivalentClass( A, min( p, 1, B ) );
		kb.addSubClass( C, min( p, 1, TOP ) );		

		assertNotSubClass( kb, C, A );
	}
	
	@Test
	public void testUnsatClasses1() {
		classes(B, C, D);

		kb.addSubClass(B, and(C, D));

		assertTrue(kb.getUnsatisfiableClasses().isEmpty());
		assertEquals(singleton(BOTTOM), kb.getAllUnsatisfiableClasses());

		assertFalse(kb.isClassified());

		assertTrue(kb.getUnsatisfiableClasses().isEmpty());
		assertEquals(singleton(BOTTOM), kb.getAllUnsatisfiableClasses());
	}
	
	@Test
	public void testUnsatClasses2() {
		classes(B, C, D);

		kb.addDisjointClass(C, D);
		kb.addSubClass(B, and(C, D));

		assertEquals(singleton(B), kb.getUnsatisfiableClasses());
		assertEquals(SetUtils.create(B, BOTTOM), kb.getAllUnsatisfiableClasses());

		assertFalse(kb.isClassified());

		assertEquals(singleton(B), kb.getUnsatisfiableClasses());
		assertEquals(SetUtils.create(B, BOTTOM), kb.getAllUnsatisfiableClasses());
	}	

	@Test
	public void testGuessingRule() {
		classes(C, D);
		objectProperties(p);
		individuals(a, b);
		
		kb.addEquivalentClass(C, hasValue(inv(p), a));

		kb.addType(a, card(p, 2, D));
		kb.addType(a, card(p, 3, TOP));

		assertTrue(kb.isConsistent());
		assertTrue(kb.isSatisfiable(C));
	}

	@Test
	public void testGuessingRule2() {
		// ticket #488
		classes(A, B, C);
		objectProperties(p, q);
		individuals(a);
		
		kb.addInverseProperty(p, q);
		kb.addDomain(p, A);
		kb.addRange(p, or(B, C));

		kb.addSubClass(A, card(p, 1, B));
		kb.addSubClass(A, card(p, 1, C));
		
		kb.addSubClass(B, card(q, 1, A));
		kb.addSubClass(C, card(q, 1, A));
		
		kb.addDisjointClasses(Arrays.asList(A, B, C));

		kb.addEquivalentClass(A, oneOf(a));
		
		assertTrue(kb.isConsistent());
		assertEquals(Collections.emptySet(), kb.getUnsatisfiableClasses());
	}

	@Test
	public void test484() {
		dataProperties(p);
		individuals(a);

		ATermAppl dt = restrict(Datatypes.INTEGER, minExclusive(literal(0)), maxExclusive(literal(0)));

		kb.addType(a, some(p, dt));

		assertFalse(kb.isConsistent());

		assertEquals(Clash.ClashType.EMPTY_DATATYPE, kb.getABox().getLastClash().getType());
	}
	
	@Test
	public void test485() {
		Properties oldOptions = PelletOptions.setOptions(PropertiesBuilder.singleton("DISABLE_EL_CLASSIFIER", "true"));
		try {
			classes(A, B, C);
			objectProperties(p, q);
			individuals(a, b);

			kb.addSubClass(B, A);
			kb.addSubClass(C, A);
			
			kb.addDomain(p, B);
			kb.addDomain(q, A);

			kb.addType(a, A);
			kb.addType(b, B);

			kb.realize();

			assertEquals(SetUtils.create(A, B ,TOP), IteratorUtils.toSet(new FlattenningIterator<ATermAppl>(kb.getSuperClasses(some(p, TOP)))));
			assertEquals(SetUtils.create(A,TOP), IteratorUtils.toSet(new FlattenningIterator<ATermAppl>(kb.getSuperClasses(some(q, TOP)))));
		}
		finally {
			PelletOptions.setOptions(oldOptions);
		}
	}
	
	@Test
	public void test518() {
		// tests if the interaction between some values restriction and inverses ends up creating a cycle in the
		// completion graph
		
		classes(A, B, C);
		objectProperties(p, q);

		kb.addInverseFunctionalProperty(p);
		kb.addSubProperty(q, inv(p));
		
		assertFalse(kb.isSatisfiable(some(p,some(q,all(p,BOTTOM)))));
	}
	
	@Test
	public void test553() {
		KnowledgeBase kb = new KnowledgeBase();
		KnowledgeBase copyKB = kb.copy();
		assertTrue(copyKB != kb);
		assertTrue(copyKB.getABox().getKB() == copyKB);
	}
	
	@Test
	public void testFunctionalSubDataProperty() {
		// test for ticket #551
		
		individuals(a);
		dataProperties(p, q);

		kb.addFunctionalProperty(p);
		kb.addSubProperty(q, p);
		
		kb.addPropertyValue(p, a, literal("val1"));
		kb.addPropertyValue(q, a, literal("val2"));
		
		assertFalse(kb.isConsistent());
	}
	
	@Test
	public void test549() {
		int n = 5;
		ATermAppl[] ind = new ATermAppl[n];
		for (int i = 0; i < n; i++) {
	        ind[i] = term("ind" + i);
        }
		ATermAppl[] cls = new ATermAppl[n];
		for (int i = 0; i < n; i++) {
			cls[i] = term("C" + i);
        }
		
		classes(cls);
		dataProperties(p);
		individuals(ind);
		
		kb.addClass(C);

		float lower = 1.0f;
		float increment = 1.0f;
		for (int i = 0; i < n; i++) {
			kb.addSubClass(cls[i], C);
			kb.addType(ind[i], C);
			
			float upper = lower + increment;
			ATermAppl dt = term("D" + i);
			ATermAppl def = restrict(Datatypes.FLOAT, minInclusive(literal(lower)), maxExclusive(literal(upper)));
			kb.addDatatypeDefinition(dt, def);
			
			kb.addEquivalentClass(cls[i], some(p, dt));
			kb.addPropertyValue(p, ind[i], literal(lower));
			lower = upper;
		}
		
//		kb.realize();
//		kb.printClassTree();

		for (int i = 0; i < n; i++) {
			assertEquals(Collections.singleton(ind[i]), kb.getInstances(cls[i]));
        }

	}
	
	@Test
	public void test532a() {
		classes(A, B, C, D);
		individuals(a, b, c, d);
		objectProperties(p, q);

		kb.addDisjointClasses(Arrays.asList(A, B, C, D));

		kb.addType(a, or(A, B));
		kb.addType(b, or(C, D));

		assertTrue(kb.isConsistent());

		kb.addSame(a, b);

		assertFalse(kb.isConsistent());
	}
	
	@Test
	public void test532b() {
		// variation of the condition in 532 where the nodes involved in MaxBranch are merged 
		classes(C, D, E);
		individuals(a, b, c, d, e, f);
		objectProperties(p);

		kb.addType(a, max(p, 2, TOP));
		kb.addType(a, min(p, 2, TOP));
		kb.addPropertyValue(p, a, b);
		kb.addPropertyValue(p, a, c);
		kb.addPropertyValue(p, a, d);

		assertTrue(kb.isConsistent());
		
		kb.addSame(c, e);
		kb.addSame(d, e);

		assertTrue(kb.isConsistent());
	}
	
	@Test
	public void test560() {
		classes(A, B);
		individuals(a);
		objectProperties(p, q);

		kb.addFunctionalProperty(p);
		kb.addSubProperty(q, p);
		kb.addSubClass(A, hasValue(q,a));
		kb.addType(a, all(inv(q), all(inv(p), oneOf(a))));
		
		assertTrue(kb.isConsistent());
		assertTrue(kb.isSatisfiable(and(some(p,A), some(q,B))));
	}
}


