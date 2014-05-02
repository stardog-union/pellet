// Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package com.clarkparsia.pellet.test.el;

import static com.clarkparsia.pellet.utils.TermFactory.and;
import static com.clarkparsia.pellet.utils.TermFactory.inv;
import static com.clarkparsia.pellet.utils.TermFactory.list;
import static com.clarkparsia.pellet.utils.TermFactory.some;
import static com.clarkparsia.pellet.utils.TermFactory.term;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Collections;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

import junit.framework.JUnit4TestAdapter;

import org.junit.BeforeClass;
import org.junit.Test;
import org.mindswap.pellet.KBLoader;
import org.mindswap.pellet.KnowledgeBase;
import org.mindswap.pellet.PelletOptions;
import org.mindswap.pellet.jena.JenaLoader;
import org.mindswap.pellet.taxonomy.Taxonomy;
import org.mindswap.pellet.taxonomy.TaxonomyBuilder;
import org.mindswap.pellet.test.AbstractKBTests;
import org.mindswap.pellet.test.PelletTestSuite;
import org.mindswap.pellet.utils.ATermUtils;
import org.mindswap.pellet.utils.SetUtils;
import org.mindswap.pellet.utils.progress.SilentProgressMonitor;

import aterm.ATermAppl;

import com.clarkparsia.pellet.el.SimplifiedELClassifier;
import com.clarkparsia.pellet.utils.PropertiesBuilder;
import com.clarkparsia.pellet.utils.TermFactory;

/**
 * <p>
 * Title:
 * </p>
 * <p>
 * Description:
 * </p>
 * <p>
 * Copyright: Copyright (c) 2007
 * </p>
 * <p>
 * Company: Clark & Parsia, LLC. <http://www.clarkparsia.com>
 * </p>
 * 
 * @author Evren Sirin
 */
public class ELTests extends AbstractKBTests {
	private static final ATermAppl[] X = new ATermAppl[5];

	@BeforeClass
	public static void initTerms() {
		for( int i = 0; i < X.length; i++ ) {
			X[i] = term( "X" + i );
		}
	}

	private final Class<? extends TaxonomyBuilder>	builderClass;

	public static junit.framework.Test suite() {
		return new JUnit4TestAdapter( ELTests.class );
	}

	public ELTests() {
		this.builderClass = SimplifiedELClassifier.class ;
	}

	public static <T> Set<Set<T>> singletonSets(T... es) {
		Set<Set<T>> set = new HashSet<Set<T>>();
		for( T e : es ) {
			set.add( Collections.singleton( e ) );
		}
		return set;
	}

	public Taxonomy<ATermAppl> getHierarchy() {
		assertTrue( "Expressivity is not EL", kb.getExpressivity().isEL() );

		TaxonomyBuilder builder = null;
		try {
			builder = builderClass.newInstance();
			builder.setKB( kb );
		} catch( Exception e ) {
			throw new RuntimeException( e );
		}
builder.setProgressMonitor( new SilentProgressMonitor() );
		builder.classify();
		Taxonomy<ATermAppl> taxonomy = builder.getTaxonomy();
		
//		 taxonomy.getTop().print();

		return taxonomy;
	}

	@Test
	public void testEL1() {
		classes(A, B, C, D, E);
		objectProperties(p);
		
		kb.addSubClass( A, and( B, some( p, C ) ) );
		kb.addSubClass( some( p, ATermUtils.TOP ), D );
		kb.addSubClass( and( B, D ), E );

		Taxonomy<ATermAppl> hierarchy = getHierarchy();

		assertEquals( singletonSets( A ), hierarchy.getSubs( B, true ) );
	}

	@Test
	public void testEL2() {
		classes(A, C, D, E);
		objectProperties(p);
		
		kb.addSubClass( A, some( p, C ) );
		kb.addSubClass( C, D );
		kb.addSubClass( some( p, D ), E );

		Taxonomy<ATermAppl> hierarchy = getHierarchy();

		assertEquals( singletonSets( D ), hierarchy.getSupers( C, true ) );
		assertEquals( singletonSets( E ), hierarchy.getSupers( A, true ) );
	}

	@Test
	public void testEL3a() {
		classes(A, C, D, E, F);
		objectProperties(p);
		
		kb.addSubClass( A, some( p, C ) );
		kb.addSubClass( C, D );
		kb.addSubClass( C, E );
		kb.addSubClass( some( p, and( D, E ) ), F );

		Taxonomy<ATermAppl> hierarchy = getHierarchy();

		assertEquals( singletonSets( D, E ), hierarchy.getSupers( C, true ) );
		assertEquals( singletonSets( F ), hierarchy.getSupers( A, true ) );
	}

	@Test
	public void testEL3b() {
		classes(A, C, D, E, F, G);
		objectProperties(p);
		
		kb.addSubClass( A, some( p, C ) );
		kb.addSubClass( C, D );
		kb.addSubClass( C, E );
		kb.addSubClass( some( p, G ), F );
		kb.addEquivalentClass( G, and( D, E ) );

		Taxonomy<ATermAppl> hierarchy = getHierarchy();

		assertEquals( singletonSets( D, E ), hierarchy.getSupers( G, true ) );
		assertEquals( singletonSets( F ), hierarchy.getSupers( A, true ) );
	}

	@Test
	public void testEL3c() {
		classes(A, C, D, E);
		objectProperties(p);

		kb.addSubClass( A, some( p, and( C, D ) ) );
		kb.addSubClass( some( p, C ), E );

		Taxonomy<ATermAppl> hierarchy = getHierarchy();

		assertEquals( singletonSets( E ), hierarchy.getSupers( A, true ) );
	}

	@Test
	public void testEL4() {
		classes(A, B, C, D, E);

		kb.addSubClass( A, and( B, C, D ) );
		kb.addSubClass( and( C, D ), E );

		Taxonomy<ATermAppl> hierarchy = getHierarchy();

		assertEquals( singletonSets( A ), hierarchy.getSubs( B, true ) );
	}

	@Test
	public void testEL5a() {
		classes(A, B, C, D, E, F, G);
		objectProperties(p);

		kb.addSubClass( and( A, some( p, and( some( p, B ), C ) ) ), D );
		kb.addSubClass( E, A );
		kb.addSubClass( E, F );
		kb.addSubClass( F, some( p, G ) );
		kb.addSubClass( G, C );
		kb.addSubClass( G, some( p, B ) );

		Taxonomy<ATermAppl> hierarchy = getHierarchy();

		assertEquals( singletonSets( A, D, F ), hierarchy.getSupers( E, true ) );
	}

	@Test
	public void testEL5b() {
		classes(A, B, C, D, E, F, G, X[1], X[2], X[3]);
		objectProperties(p);

		kb.addSubClass( and( A, X[1] ), D );
		kb.addEquivalentClass( X[1], some( p, X[2] ) );
		kb.addEquivalentClass( X[2], and( X[3], C ) );
		kb.addEquivalentClass( X[3], some( p, B ) );
		kb.addSubClass( E, A );
		kb.addSubClass( E, F );
		kb.addSubClass( F, some( p, G ) );
		kb.addSubClass( G, C );
		kb.addSubClass( G, some( p, B ) );

		Taxonomy<ATermAppl> hierarchy = getHierarchy();

		assertEquals( singletonSets( A, D, F ), hierarchy.getSupers( E, true ) );
	}

	@Test
	public void testEL6() {
		classes(A, B, C, D, E, G);
		objectProperties(p);

		kb.addSubClass( and( A, some( p, and( B, C ) ) ), D );
		kb.addSubClass( E, A );
		kb.addSubClass( E, some( p, G ) );
		kb.addSubClass( G, B );
		kb.addSubClass( G, C );

		Taxonomy<ATermAppl> hierarchy = getHierarchy();

		assertEquals( singletonSets( A, D ), hierarchy.getSupers( E, true ) );
	}

	@Test
	public void testEL7() {
		classes(A, B, C, D, E);

		kb.addSubClass( A, B );
		kb.addSubClass( and( A, B ), and( C, D, ATermUtils.TOP ) );
		kb.addSubClass( and( A, C ), E );
		kb.addSubClass( and( A, D, ATermUtils.TOP ), E );

		Taxonomy<ATermAppl> hierarchy = getHierarchy();

		assertEquals( singletonSets( B, C, D, E ), hierarchy.getSupers( A, true ) );
	}

	@Test
	public void testEL8() {
		classes(A, B, C, D, E, F, G);
		objectProperties(p);

		kb.addSubClass( A, some( p, B ) );
		kb.addSubClass( B, C );
		kb.addSubClass( C, D );
		kb.addSubClass( some( p, and( D, ATermUtils.TOP ) ), E );

		Taxonomy<ATermAppl> hierarchy = getHierarchy();

		assertEquals( singletonSets( E ), hierarchy.getSupers( A, true ) );
	}

	@Test
	public void testELNormalization1() {
		classes(A, B, C, D);
		objectProperties(p);

		kb.addSubClass( A, some( p, and( B, C ) ) );
		kb.addSubClass( some( p, and( C, B ) ), D );

		Taxonomy<ATermAppl> hierarchy = getHierarchy();

		assertEquals( singletonSets( D ), hierarchy.getSupers( A, true ) );
	}

	@Test
	public void testELNormalization2() {
		classes(A, B, C, D, E);

		kb.addSubClass( A, and( B, and( C, D ) ) );
		kb.addSubClass( and( C, and( B, D ) ), E );

		Taxonomy<ATermAppl> hierarchy = getHierarchy();

		assertEquals( singletonSets( B, C, D, E ), hierarchy.getSupers( A, true ) );
	}

	@Test
	public void testELNormalization3() {
		classes(A, B, C, D, E, F, G);

		kb.addSubClass( A, and( B, and( C, D, and( E, F ) ) ) );
		kb.addSubClass( and( and( C, F ), and( B, D, E ) ), G );

		Taxonomy<ATermAppl> hierarchy = getHierarchy();

		assertEquals( singletonSets( B, C, D, E, F, G ), hierarchy.getSupers( A, true ) );
	}

	@Test
	public void testBottom1() {
		classes(A, B, C, D);

		kb.addSubClass( A, ATermUtils.BOTTOM );
		kb.addSubClass( C, and( A, B ) );
		kb.addSubClass( ATermUtils.BOTTOM, D );

		Taxonomy<ATermAppl> hierarchy = getHierarchy();

		assertEquals( SetUtils.create( A, C ), hierarchy.getEquivalents( ATermUtils.BOTTOM ) );
	}

	@Test
	public void testBottom2() {
		classes(A, B, C, D, E, F, G);
		objectProperties(p, q, r, s);

		kb.addSubClass( A, some( p, B ) );
		kb.addSubClass( some( p, B ), C );
		kb.addSubClass( C, ATermUtils.BOTTOM );

		Taxonomy<ATermAppl> hierarchy = getHierarchy();

		assertEquals( SetUtils.create( A, C ), hierarchy.getEquivalents( ATermUtils.BOTTOM ) );
	}
	
	@Test
	public void testTop1() {
		classes(A, B, C, D, E, F, G);
		objectProperties(p, q, r, s);


		kb.addSubClass( ATermUtils.TOP, A );
		kb.addSubClass( C, some( p, B ) );
		kb.addSubClass( some( p, A ), D );

		Taxonomy<ATermAppl> hierarchy = getHierarchy();

		assertEquals( singletonSets( D ), hierarchy.getSupers( C, true ) );
	}

	@Test
	public void testBottomWithSome1() {
		classes(A, B);
		objectProperties(p);


		kb.addSubClass( A, some( p, B ) );
		kb.addSubClass( B, ATermUtils.BOTTOM );

		Taxonomy<ATermAppl> hierarchy = getHierarchy();

		assertEquals( SetUtils.create( A, B ), hierarchy.getEquivalents( ATermUtils.BOTTOM ) );
	}

	@Test
	public void testBottomWithSome2() {
		classes(A, B, C, D, E, F, G);
		objectProperties(p, q, r, s);


		kb.addSubClass( B, some( p, A ) );
		kb.addSubClass( A, ATermUtils.BOTTOM );
		kb.addSubClass( C, some( q, B ) );

		Taxonomy<ATermAppl> hierarchy = getHierarchy();

		assertEquals( SetUtils.create( A, B, C ), hierarchy.getEquivalents( ATermUtils.BOTTOM ) );
	}

	@Test
	public void testDisjoint() {
		classes(A, B, C, D, E, F, G);
		objectProperties(p, q, r, s);


		kb.addSubClass( and( A, B ), ATermUtils.BOTTOM );
		kb.addSubClass( A, B );
		kb.addDisjointClass( C, D );
		kb.addEquivalentClass( C, D );

		Taxonomy<ATermAppl> hierarchy = getHierarchy();

		assertEquals( SetUtils.create( A, C, D ), hierarchy.getEquivalents( ATermUtils.BOTTOM ) );
	}

	@Test
	public void testDisjointWithSome1() {
		classes(A, B, C, D, E, F, G);
		objectProperties(p, q, r, s);


		kb.addSubProperty( p, q );
		kb.addSubClass( A, some( p, B ) );
		kb.addSubClass( A, D );
		kb.addSubClass( some( p, B ), some( p, C ) );
		kb.addDisjointClass( some( q, C ), D );

		Taxonomy<ATermAppl> hierarchy = getHierarchy();

		assertEquals( SetUtils.create( A ), hierarchy.getEquivalents( ATermUtils.BOTTOM ) );
	}

	@Test
	public void testDisjointWithSome2() {
		classes(A, B, C, D, E, F, G);
		objectProperties(p, q, r, s);


		kb.addSubClass( A, some( p, and( B, C ) ) );
		kb.addDisjointClass( B, C );

		Taxonomy<ATermAppl> hierarchy = getHierarchy();

		assertEquals( SetUtils.create( A ), hierarchy.getEquivalents( ATermUtils.BOTTOM ) );
	}

	@Test
	public void testRoles1a() {
		classes(A, B, C, D, E, F, G);
		objectProperties(p, q, r, s);


		kb.addSubProperty( p, q );
		kb.addSubClass( A, some( p, B ) );
		kb.addSubClass( some( q, B ), C );

		Taxonomy<ATermAppl> hierarchy = getHierarchy();

		assertEquals( singletonSets( C ), hierarchy.getSupers( A, true ) );
	}

	@Test
	public void testRoles1b() {
		classes(A, B, C, D, E, F, G);
		objectProperties(p, q, r, s);


		kb.addSubProperty( p, q );
		kb.addSubClass( A, and( D, some( p, B ) ) );
		kb.addSubClass( and( D, some( q, B ) ), C );

		Taxonomy<ATermAppl> hierarchy = getHierarchy();

		assertEquals( singletonSets( C, D ), hierarchy.getSupers( A, true ) );
	}

	@Test
	public void testRoles2a() {
		classes(A, B, C, D, E, F, G);
		objectProperties(p, q, r, s);


		kb.addSubProperty( list( p, p ), p );
		kb.addSubProperty( q, p );
		kb.addSubClass( A, some( q, some( p, B ) ) );
		kb.addSubClass( some( p, B ), C );

		Taxonomy<ATermAppl> hierarchy = getHierarchy();

		assertEquals( singletonSets( C ), hierarchy.getSupers( A, true ) );
	}

	@Test
	public void testRoles2b() {
		classes(A, B, C, D, E, F, G);
		objectProperties(p, q, r, s);


		kb.addSubProperty( list( p, q ), p );
		kb.addSubProperty( r, q );
		kb.addSubClass( A, and( F, some( p, B ) ) );
		kb.addSubClass( B, and( G, some( r, C ) ) );
		kb.addSubClass( C, some( q, D ) );
		kb.addSubClass( some( p, D ), E );

		Taxonomy<ATermAppl> hierarchy = getHierarchy();

		assertEquals( singletonSets( E, F ), hierarchy.getSupers( A, true ) );
	}

	@Test
	public void testRoles2c() {
		classes(A, B, C, D, E, F, G);
		objectProperties(p, q, r, s);


		kb.addSubProperty( list( p, q ), p );
		kb.addSubProperty( list( p, q ), r );
		kb.addSubClass( A, some( p, B ) );
		kb.addSubClass( B, some( q, C ) );
		kb.addSubClass( C, some( q, D ) );
		kb.addSubClass( some( r, D ), E );

		Taxonomy<ATermAppl> hierarchy = getHierarchy();

		assertEquals( singletonSets( E ), hierarchy.getSupers( A, true ) );
	}

	@Test
	public void testRoles3a() {
		classes(A, B, C, D, E, F, G);
		objectProperties(p, q, r, s);


		kb.addSubProperty( list( p, q, r ), p );
		kb.addSubClass( A, some( p, B ) );
		kb.addSubClass( B, some( q, C ) );
		kb.addSubClass( C, some( r, D ) );
		kb.addSubClass( some( p, D ), E );

		Taxonomy<ATermAppl> hierarchy = getHierarchy();

		assertEquals( singletonSets( E ), hierarchy.getSupers( A, true ) );
	}

	@Test
	public void testRoles3b() {
		classes(A, B, C, D, E, F, G);
		objectProperties(p, q, r, s);


		kb.addSubProperty( list( p, q, r ), p );
		kb.addSubProperty( list( p, q, s ), s );
		kb.addSubClass( A, some( p, B ) );
		kb.addSubClass( B, some( q, C ) );
		kb.addSubClass( C, some( r, D ) );
		kb.addSubClass( D, some( q, E ) );
		kb.addSubClass( E, some( s, F ) );
		kb.addSubClass( some( s, F ), G );

		Taxonomy<ATermAppl> hierarchy = getHierarchy();

		assertEquals( singletonSets( G ), hierarchy.getSupers( A, true ) );
	}

	@Test
	public void testRoles4() {
		classes(A, B, C, D, E, F, G, X[0], X[1]);
		objectProperties(p, q, r, s);


		kb.addSubProperty( list( q, r ), s );
		kb.addSubProperty( list( p, q, r, s ), p );
		kb.addSubProperty( list( p, s ), p );
		kb.addSubClass( X[0], X[1] );
		kb.addSubClass( A, and( X[0], some( p, B ) ) );
		kb.addSubClass( B, and( X[1], some( q, C ) ) );
		kb.addSubClass( C, and( X[2], some( r, D ) ) );
		kb.addSubClass( D, and( X[1], some( s, E ) ) );
		kb.addSubClass( and( X[0], some( p, E ) ), F );

		Taxonomy<ATermAppl> hierarchy = getHierarchy();

		assertEquals( singletonSets( X[0], F ), hierarchy.getSupers( A, true ) );
	}

	@Test
	public void testHeart() {
		classes(A, B, C, D, E, F, G);
		objectProperties(p, q, r, s);


		kb = new KnowledgeBase();
		ATermAppl endocardium = term( "Endocardium" );
		ATermAppl tissue = term( "Tissue" );
		ATermAppl heartWall = term( "HeartWall" );
		ATermAppl heartValve = term( "HeartValve" );
		ATermAppl bodyWall = term( "BodyWall" );
		ATermAppl bodyValve = term( "BodyValve" );
		ATermAppl heart = term( "Heart" );
		ATermAppl endocarditis = term( "Endocarditis" );
		ATermAppl inflammation = term( "Inflammation" );
		ATermAppl disease = term( "Disease" );
		ATermAppl heartDisease = term( "HeartDisease" );
		ATermAppl criticalDisease = term( "CriticalDisease" );
		ATermAppl contIn = term( "cont-in" );
		ATermAppl partOf = term( "part-of" );
		ATermAppl hasLoc = term( "has-loc" );
		ATermAppl actsOn = term( "acts-on" );

		kb.addClass( endocardium );
		kb.addClass( tissue );
		kb.addClass( heartWall );
		kb.addClass( heartValve );
		kb.addClass( bodyWall );
		kb.addClass( bodyValve );
		kb.addClass( heart );
		kb.addClass( endocarditis );
		kb.addClass( inflammation );
		kb.addClass( disease );
		kb.addClass( heartDisease );
		kb.addClass( criticalDisease );
		kb.addObjectProperty( contIn );
		kb.addObjectProperty( partOf );
		kb.addObjectProperty( hasLoc );
		kb.addObjectProperty( actsOn );

		kb.addSubClass( endocardium, and( tissue, some( contIn, heartWall ), some( contIn,
				heartValve ) ) );
		kb.addSubClass( heartWall, and( bodyWall, some( partOf, heart ) ) );
		kb.addSubClass( heartValve, and( bodyValve, some( partOf, heart ) ) );
		kb.addSubClass( endocarditis, and( inflammation, some( hasLoc, endocardium ) ) );
		kb.addSubClass( inflammation, and( disease, some( actsOn, tissue ) ) );
		kb.addSubClass( and( heartDisease, some( hasLoc, heartValve ) ), criticalDisease );
		kb.addEquivalentClass( heartDisease, and( disease, some( hasLoc, heart ) ) );
		kb.addSubProperty( list( partOf, partOf ), partOf );
		kb.addSubProperty( partOf, contIn );
		kb.addSubProperty( list( hasLoc, contIn ), hasLoc );

		Taxonomy<ATermAppl> hierarchy = getHierarchy();

		assertEquals( singletonSets( ATermUtils.TOP, inflammation, disease, heartDisease,
				criticalDisease ), hierarchy.getSupers( endocarditis ) );
	}

	@Test
	public void testDomain1() {
		classes(A, B);
		objectProperties(p);


		kb.addDomain( p, A );
		kb.addSubClass( B, some( p, ATermUtils.TOP ) );

		Taxonomy<ATermAppl> hierarchy = getHierarchy();

		assertEquals( singletonSets( A ), hierarchy.getSupers( B, true ) );
	}

	@Test
	public void testDomain2() {
		classes(A, B, C, D, E, F, G);
		objectProperties(p, q, r, s);


		kb.addDomain( p, and( A, B ) );
		kb.addDomain( p, C );
		kb.addSubClass( B, some( p, ATermUtils.TOP ) );

		Taxonomy<ATermAppl> hierarchy = getHierarchy();

		assertEquals( singletonSets( A, C ), hierarchy.getSupers( B, true ) );
	}

	@Test
	public void testDomainAbsorption() {
		classes(A, B, C, D, E, F, G);
		objectProperties(p, q, r, s);


		kb.addDomain( p, A );
		kb.addSubClass( and( some( p, B ), some( p, ATermUtils.TOP ) ), C );
		kb.addSubClass( E, some( p, D ) );

		Taxonomy<ATermAppl> hierarchy = getHierarchy();

		assertEquals( singletonSets( A ), hierarchy.getSupers( E, true ) );
	}

	@Test
	public void testDomainBottom() {
		classes(A, B, C, D, E, F, G);
		objectProperties(p, q, r, s);


		kb.addDomain( p, ATermUtils.BOTTOM );
		kb.addSubClass( A, some( p, B ) );

		Taxonomy<ATermAppl> hierarchy = getHierarchy();

		assertEquals( SetUtils.create( A ), hierarchy.getEquivalents( ATermUtils.BOTTOM ) );
	}

	@Test
	public void testReflexiveRole() {
		classes(A, B, C, D, E, F, G);
		objectProperties(p, q, r, s);


		kb.addReflexiveProperty( p );
		kb.addRange( p, A );
		kb.addRange( p, and( B, C ) );

		Taxonomy<ATermAppl> hierarchy = getHierarchy();

		assertEquals( SetUtils.create( A, B, C ), hierarchy.getEquivalents( ATermUtils.TOP ) );
	}

	@Test
	public void testRange1() {
		classes(A, B, C, D);
		objectProperties(p);


		kb.addRange( p, A );
		kb.addSubClass( B, some( p, C ) );
		kb.addSubClass( some( p, A ), D );

		Taxonomy<ATermAppl> hierarchy = getHierarchy();

		assertEquals( singletonSets( D ), hierarchy.getSupers( B, true ) );
	}

	@Test
	public void testRange2() {
		classes(A, B, C, D, E, F, G);
		objectProperties(p, q, r, s);


		kb.addRange( p, and( A, B ) );
		kb.addSubClass( C, some( p, D ) );
		kb.addSubClass( some( p, and( A, B ) ), E );

		Taxonomy<ATermAppl> hierarchy = getHierarchy();

		assertEquals( singletonSets( E ), hierarchy.getSupers( C, true ) );
	}

	@Test
	public void testRange3() {
		classes(A, B, C, D, E, F, G);
		objectProperties(p, q, r, s);


		kb.addRange( p, and( A, B ) );
		kb.addSubClass( C, some( p, D ) );
		kb.addSubClass( some( p, A ), E );

		Taxonomy<ATermAppl> hierarchy = getHierarchy();

		assertEquals( singletonSets( E ), hierarchy.getSupers( C, true ) );
	}
	
	@Test
	public void testRange5() {
		classes(A, B, C, D, E, F, G);
		objectProperties(p, q, r, s);


		kb.addRange( p, A );
		kb.addSubClass( B, and( A, some( p, C ) ) );
		kb.addSubClass( C, A );
		kb.addEquivalentClass( D, some( p, C ) );

		Taxonomy<ATermAppl> hierarchy = getHierarchy();

		assertEquals( singletonSets( B, C ), hierarchy.getSubs( A, true ) );
	}

	@Test
	public void testDomainNormalization1() {
		classes(A, B, C, D, E, F, G, X[1]);
		objectProperties(p, q, r, s);


		kb.addDomain( p, some( q, C ) );
		kb.addDomain( p, and( B, C ) );
		kb.addSubClass( D, some( p, X[1] ) );
		kb.addSubClass( some( q, C ), E );

		Taxonomy<ATermAppl> hierarchy = getHierarchy();

		assertEquals( singletonSets( B, C, E ), hierarchy.getSupers( D, true ) );
	}
	
	@Test
	public void testRangeNormalization1() {
		classes(A, B, C, D, E, F, G, X[1]);
		objectProperties(p, q, r, s);


		kb.addRange( p, A );
		kb.addRange( p, and( B, C ) );
		kb.addSubClass( D, some( p, X[1] ) );
		kb.addSubClass( some( p, and( and( A, B ), C ) ), E );

		Taxonomy<ATermAppl> hierarchy = getHierarchy();

		assertEquals( singletonSets( E ), hierarchy.getSupers( D, true ) );
	}

	@Test
	public void testRangeNormalization2() {
		classes(A, B, C, D, E, F, G, X[0]);
		objectProperties(p, q, r, s);


		kb.addRange( p, some( q, A ) );
		kb.addSubClass( B, some( p, X[0] ) );
		kb.addSubClass( some( p, some( q, A ) ), C );

		Taxonomy<ATermAppl> hierarchy = getHierarchy();

		assertEquals( singletonSets( C ), hierarchy.getSupers( B, true ) );
	}

	@Test
	public void testDomainAndRange() {
		classes(A, B, C, D, E, F, G);
		objectProperties(p, q, r, s);


		kb.addRange( p, A );
		kb.addDomain( q, B );
		kb.addSubClass( C, some( p, ATermUtils.TOP ) );
		kb.addSubClass( some( p, A ), some( q, ATermUtils.TOP ) );

		Taxonomy<ATermAppl> hierarchy = getHierarchy();

		assertEquals( singletonSets( B ), hierarchy.getSupers( C, true ) );
	}
	
	@Test
	public void testRange4() {
		classes(A, B, C, D, E, F, G);
		objectProperties(p, q, r, s);


		kb.addRange( p, C );
		kb.addSubClass( A, some( p, B ) );
		kb.addSubClass( and( B, C ), D );
		kb.addSubClass( some( p, D ), E );
		
		Taxonomy<ATermAppl> hierarchy = getHierarchy();

		assertEquals( singletonSets( A ), hierarchy.getSubs( E, true ) );
	}
	
	@Test
	public void testSomeConjunction() {
		classes(A, B, C, D, E, F, G);
		objectProperties(p, q, r, s);


		kb.addSubClass( A, some( p, and( B, C, D ) ) );
		kb.addSubClass( some( p, and( B, C ) ), E );
		
		Taxonomy<ATermAppl> hierarchy = getHierarchy();

		assertEquals( singletonSets( A ), hierarchy.getSubs( E, true ) );
	}
	
	@Test
	public void testDisjointRange() {
		classes(A, B, C, D, E, F, G);
		objectProperties(p, q, r, s);


		kb.addRange( p, C );
		kb.addSubClass( A, some( p, B ) );
		kb.addDisjointClass( B, C );
		
		Taxonomy<ATermAppl> hierarchy = getHierarchy();

		assertEquals( SetUtils.create( A ), hierarchy.getEquivalents( ATermUtils.BOTTOM ) );
	}

	@Test
	public void testDisjointRangeSuper() {
		classes(A, B, C, D, E, F, G);
		objectProperties(p, q, r, s);


		kb.addRange( p, C );
		kb.addSubClass( A, some( p, B ) );
		kb.addSubClass( B, D );
		kb.addDisjointClass( D, C );
		kb.addSubClass( A, E );
		kb.addSubClass( B, F );
		
		Taxonomy<ATermAppl> hierarchy = getHierarchy();

		assertEquals( SetUtils.create( A ), hierarchy.getEquivalents( ATermUtils.BOTTOM ) );
	}
	
	@Test
	public void testTicket424() {
		classes(A, B, C, D, E, F, G);
		objectProperties(p, q, r, s);


		KBLoader loader = new JenaLoader();
		
		KnowledgeBase kb = loader.createKB( new String[] { "file:" + PelletTestSuite.base + "misc/ticket-424-test-case.owl" } );
		
		Taxonomy<ATermAppl> toldTaxonomy = kb.getToldTaxonomy();
				
		try {

			for ( ATermAppl aTerm : kb.getClasses() ) {

				assertNotNull( toldTaxonomy.getNode( aTerm ) );
				
				toldTaxonomy.getFlattenedSubs(TermFactory.TOP, false);
				
			}
		} catch ( NullPointerException e ) {
			fail("Caught NullPointerException when querying the told taxonomy: ticket #424");
		}
	}
	
	@Test
	public void testTicket465() {
		classes(A, B, C, D, E, F, G);
		objectProperties(p, q, r, s);

		kb.addSubClass(B, A);
		kb.addSubClass(C, B);
		kb.addSubClass(F, C);
		kb.addSubClass(F, some(p, and(some(r, G), E)));
		kb.addEquivalentClass(D, and(some(q, E), A));
		kb.addSubProperty(p, q);		

		Taxonomy<ATermAppl> hierarchy = getHierarchy();

		assertEquals( singletonSets( F ), hierarchy.getSubs( D, true ) );
	}	

	@Test
	public void testNestedSubProperty() {
		classes(A, B, C);
		objectProperties(p, q, r);

		kb.addEquivalentClass( A, some( p, some( q, C ) ) );
		kb.addEquivalentClass( B, some( p, some( r, C ) ) );
		kb.addSubProperty( q, r );

		Taxonomy<ATermAppl> hierarchy = getHierarchy();

		assertEquals( singletonSets( A ), hierarchy.getSubs( B, true ) );
	}	
	
	/**
	 * Tests to verify whether PelletOptions.DISABLE_EL_CLASSIFIER = false is respected. (Ticket #461)
	 */
	@Test
	public void testELClassifierEnabled() {
		classes(A, B, C, D, E, F, G);
		objectProperties(p, q, r, s);


		boolean savedValue = PelletOptions.DISABLE_EL_CLASSIFIER ;

		try {
			PelletOptions.DISABLE_EL_CLASSIFIER = false;

			KBLoader loader = new JenaLoader();

			KnowledgeBase kb = loader.createKB( new String[] { "file:" + PelletTestSuite.base + "misc/ticket-424-test-case.owl" } );

			assertEquals( SimplifiedELClassifier.class, kb.getTaxonomyBuilder().getClass() );
		} 
		finally {
			PelletOptions.DISABLE_EL_CLASSIFIER = savedValue;
		}
	}

	/**
	 * Tests to verify whether PelletOptions.DISABLE_EL_CLASSIFIER = true is respected. (Ticket #461)
	 */
	@Test
	public void testELClassifierDisabled() {
		boolean savedValue = PelletOptions.DISABLE_EL_CLASSIFIER ;

		try {
			PelletOptions.DISABLE_EL_CLASSIFIER = true;

			KBLoader loader = new JenaLoader();

			KnowledgeBase kb = loader.createKB( new String[] { "file:" + PelletTestSuite.base + "misc/ticket-424-test-case.owl" } );

			assertFalse( SimplifiedELClassifier.class.equals( kb.getTaxonomyBuilder().getClass() ) );
		} 
		finally {
			PelletOptions.DISABLE_EL_CLASSIFIER = savedValue;
		}
	}
	
	/**
	 * Tests whether PelletOptions.DISABLE_EL_CLASSIFIER can be properly read from a properties file
	 */
	@Test
	public void testDisableELClassifierOptionRead() {
		Properties newOptions = new PropertiesBuilder().set( "DISABLE_EL_CLASSIFIER", "true" ).build();
		Properties savedOptions = PelletOptions.setOptions( newOptions );
		
		try {
			assertTrue( PelletOptions.DISABLE_EL_CLASSIFIER );
			
			newOptions = new PropertiesBuilder().set( "DISABLE_EL_CLASSIFIER", "false" ).build();
			PelletOptions.setOptions( newOptions );
			
			assertFalse( PelletOptions.DISABLE_EL_CLASSIFIER );
		} 
		finally {
			PelletOptions.setOptions( savedOptions );
		}
	}
	
	@Test
	public void testELExpressivityAnonymousInverseRestriction() {
		classes(C, D);
		objectProperties(p);
		
		kb.addSubClass(C, some(inv(p), D));
		
		assertFalse(kb.getExpressivity().isEL());

		assertFalse(SimplifiedELClassifier.class.equals(kb.getTaxonomyBuilder().getClass()));
	}
	
	@Test
	public void testELExpressivityAnonymousInverseChain() {
		classes(C, D);
		objectProperties(p, q, r);
		
		kb.addSubProperty(list(p, inv(q)), r);
		kb.addSubClass(C, some(p, D));
		
		assertFalse(kb.getExpressivity().isEL());

		assertFalse(SimplifiedELClassifier.class.equals(kb.getTaxonomyBuilder().getClass()));
	}
}