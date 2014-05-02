// Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package com.clarkparsia.pellet.test;

import static com.clarkparsia.pellet.utils.TermFactory.and;
import static com.clarkparsia.pellet.utils.TermFactory.or;
import static com.clarkparsia.pellet.utils.TermFactory.some;
import static com.clarkparsia.pellet.utils.TermFactory.term;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.util.HashSet;
import java.util.Set;

import junit.framework.JUnit4TestAdapter;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mindswap.pellet.KnowledgeBase;
import org.mindswap.pellet.PelletOptions;
import org.mindswap.pellet.utils.progress.AbstractProgressMonitor;

import aterm.ATermAppl;

import com.clarkparsia.pellet.utils.TermFactory;

/**
 * <p>
 * Title: TestKnowledgeBase
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
 * @author Markus Stocker
 */
public class TestKnowledgeBase {

	@BeforeClass
	public static void setUp() {
		PelletOptions.SILENT_UNDEFINED_ENTITY_HANDLING = false;
	}

	@AfterClass
	public static void tearDown() {
		PelletOptions.SILENT_UNDEFINED_ENTITY_HANDLING = true;
	}
	
	public static junit.framework.Test suite() {
		return new JUnit4TestAdapter( TestKnowledgeBase.class );
	}

	@Test
	public void testGetInstances1() {
		KnowledgeBase kb = new KnowledgeBase();

		ATermAppl C = term( "C" );
		ATermAppl i = term( "i" );

		kb.addClass( C );
		kb.addIndividual( i );
		kb.addType( i, C );
		
		Set<ATermAppl> expected = new HashSet<ATermAppl>();
		expected.add( i );

		// Named concept, all instances
		Set<ATermAppl> actual = kb.getInstances( C );

		assertEquals( expected, actual );
	}

	@Test
	public void testGetInstances2() {
		KnowledgeBase kb = new KnowledgeBase();

		ATermAppl C = term( "C" );
		ATermAppl i = term( "i" );
		ATermAppl j = term( "j" );

		kb.addClass( C );
		kb.addIndividual( i );
		kb.addIndividual( j );
		kb.addType( i, C );
		kb.addType( j, C );

		Set<ATermAppl> expected = new HashSet<ATermAppl>();
		expected.add( i );
		expected.add( j );

		// Named concept, all instances
		Set<ATermAppl> actual = kb.getInstances( C );

		assertEquals( expected, actual );
	}

	@Test
	public void testGetInstances3() {
		KnowledgeBase kb = new KnowledgeBase();

		ATermAppl C = term( "C" );
		ATermAppl D = term( "D" );
		ATermAppl i = term( "i" );
		ATermAppl j = term( "j" );

		kb.addClass( C );
		kb.addClass( D );
		kb.addIndividual( i );
		kb.addIndividual( j );
		kb.addSubClass( C, D );
		kb.addType( i, C );
		kb.addType( j, D );

		Set<ATermAppl> expectedD = new HashSet<ATermAppl>();
		expectedD.add( i );
		expectedD.add( j );
		// Named concept, all instances
		Set<ATermAppl> actualD = kb.getInstances( D );
		assertEquals( expectedD, actualD );

		Set<ATermAppl> expectedC = new HashSet<ATermAppl>();
		expectedC.add( i );
		// Named concept, all instances
		Set<ATermAppl> actualC = kb.getInstances( C );
		assertEquals( expectedC, actualC );
	}

	@Test
	public void testGetInstances4() {
		KnowledgeBase kb = new KnowledgeBase();

		ATermAppl C = term( "C" );
		ATermAppl D = term( "D" );
		ATermAppl i = term( "i" );
		ATermAppl j = term( "j" );

		kb.addClass( C );
		kb.addClass( D );
		kb.addIndividual( i );
		kb.addIndividual( j );
		kb.addSubClass( C, D );
		kb.addType( i, C );
		kb.addType( j, D );

		Set<ATermAppl> expectedD = new HashSet<ATermAppl>();
		expectedD.add( j );
		// Named concept, direct instances
		Set<ATermAppl> actualD = kb.getInstances( D, true );
		assertEquals( expectedD, actualD );

		Set<ATermAppl> expectedC = new HashSet<ATermAppl>();
		expectedC.add( i );
		// Named concept, direct instances
		Set<ATermAppl> actualC = kb.getInstances( C, true );
		assertEquals( expectedC, actualC );
	}

	@Test
	public void testGetInstances5() {
		KnowledgeBase kb = new KnowledgeBase();

		ATermAppl C = term( "C" );
		ATermAppl D = term( "D" );
		ATermAppl i = term( "i" );
		ATermAppl j = term( "j" );
		ATermAppl CaD = and( C, D );

		kb.addClass( C );
		kb.addClass( D );
		kb.addIndividual( i );
		kb.addIndividual( j );
		kb.addType( i, C );
		kb.addType( j, D );

		Set<ATermAppl> expected = new HashSet<ATermAppl>();
		// Anonymous concept, all instances
		Set<ATermAppl> actual = kb.getInstances( CaD );
		assertEquals( expected, actual );
	}

	@Test
	public void testGetInstances6() {
		KnowledgeBase kb = new KnowledgeBase();

		ATermAppl C = term( "C" );
		ATermAppl D = term( "D" );
		ATermAppl i = term( "i" );
		ATermAppl j = term( "j" );
		ATermAppl CoD = or( C, D );

		kb.addClass( C );
		kb.addClass( D );
		kb.addIndividual( i );
		kb.addIndividual( j );
		kb.addType( i, C );
		kb.addType( j, D );

		Set<ATermAppl> expected = new HashSet<ATermAppl>();
		expected.add( i );
		expected.add( j );
		Set<ATermAppl> actual = kb.getInstances( CoD );
		assertEquals( expected, actual );
	}

	@Test
	public void testGetInstances7() {
		KnowledgeBase kb = new KnowledgeBase();

		ATermAppl C = term( "C" );
		ATermAppl D = term( "D" );
		ATermAppl i = term( "i" );
		ATermAppl j = term( "j" );
		ATermAppl CaD = and( C, D );

		kb.addClass( C );
		kb.addClass( D );
		kb.addIndividual( i );
		kb.addIndividual( j );
		kb.addType( i, C );
		kb.addType( i, D );
		kb.addType( j, D );

		Set<ATermAppl> expected = new HashSet<ATermAppl>();
		expected.add( i );
		Set<ATermAppl> actual = kb.getInstances( CaD );
		assertEquals( expected, actual );
	}
	
	@Test
	public void testGetInstances8() {
		KnowledgeBase kb = new KnowledgeBase();

		ATermAppl C = TermFactory.term( "C" );
		ATermAppl E = TermFactory.term( "E" );
		ATermAppl p = TermFactory.term( "p" );
		ATermAppl D = TermFactory.some( p, E );
		ATermAppl i = TermFactory.term( "i" );

		kb.addClass( C );
		kb.addClass( E );
		kb.addObjectProperty( p );
		kb.addSubClass( D, C );
		kb.addIndividual( i );
		kb.addType( i, C );
		
		Set<ATermAppl> expected = new HashSet<ATermAppl>();
		// Retrieve direct instance of anonymous concept
		Set<ATermAppl> actual = kb.getInstances( D, true );
		assertEquals( expected, actual );
	}
	
	@Test
	public void testGetInstances9() {
		KnowledgeBase kb = new KnowledgeBase();

		ATermAppl C = term( "C" );
		ATermAppl E = term( "E" );
		ATermAppl p = term( "p" );
		ATermAppl D = some( p, E );
		ATermAppl i = term( "i" );

		kb.addClass( C );
		kb.addClass( E );
		kb.addObjectProperty( p );
		kb.addSubClass( D, C );
		kb.addIndividual( i );
		kb.addType( i, D );

		Set<ATermAppl> expected = new HashSet<ATermAppl>();
		expected.add( i );
		// Retrieve direct instance of anonymous concept
		Set<ATermAppl> actual = kb.getInstances( D, true );
		assertEquals( expected, actual );
	}
	
	@Test
	public void testGetInstances10() {
		KnowledgeBase kb = new KnowledgeBase();

		ATermAppl C = term( "C" );
		ATermAppl D = term( "D" );
		ATermAppl i = term( "i" );
		ATermAppl j = term( "j" );
		ATermAppl CoD = or( C, D );

		kb.addClass( C );
		kb.addClass( D );
		kb.addIndividual( i );
		kb.addIndividual( j );
		kb.addType( i, C );
		kb.addType( j, D );

		Set<ATermAppl> expected = new HashSet<ATermAppl>();
		Set<ATermAppl> actual = kb.getInstances( CoD, true );
		assertEquals( expected, actual );
	}

	@Test
	public void testGetInstances11() {
		KnowledgeBase kb = new KnowledgeBase();

		ATermAppl C = term( "C" );
		ATermAppl D = term( "D" );
		ATermAppl i = term( "i" );
		ATermAppl j = term( "j" );
		ATermAppl CaD = and( C, D );

		kb.addClass( C );
		kb.addClass( D );
		kb.addIndividual( i );
		kb.addIndividual( j );
		kb.addType( i, C );
		kb.addType( i, D );
		kb.addType( j, D );

		Set<ATermAppl> expected = new HashSet<ATermAppl>();
		expected.add( i );
		Set<ATermAppl> actual = kb.getInstances( CaD, true );
		assertEquals( expected, actual );
	}
	
	/**
	 * Tests for bug #449
	 */
	@Test
	public void testProgresMonitorRealization() {
		KnowledgeBase kb = new KnowledgeBase();
		
		ATermAppl A = term( "A" );
		ATermAppl B = term( "B" );
		ATermAppl C = term( "C" );
		ATermAppl D = term( "D" );
		ATermAppl E = term( "E" );
		
		kb.addClass( A );
		kb.addClass( B );
		kb.addClass( C );
		kb.addClass( D );
		kb.addClass( E );
		
		kb.addSubClass( B, A );
		kb.addSubClass( C, A );
		kb.addSubClass( D, B );
		kb.addSubClass( D, C );
		kb.addSubClass( E, D );
		
		ATermAppl i = term( "i" );
		ATermAppl j = term( "j" );
		
		kb.addIndividual( i );		
		kb.addType( i, E );
		
		kb.addIndividual( j );		
		kb.addType( j, E );
		
		TestProgressMonitor progressMonitor = new TestProgressMonitor();
		
		kb.setTaxonomyBuilderProgressMonitor( progressMonitor );
		
		kb.realize();
		
		assertFalse( progressMonitor.isProgressLengthExceeded() );
	}
	
	private static class TestProgressMonitor extends AbstractProgressMonitor {
		private boolean progressLengthExceeded;
		
		public TestProgressMonitor() {
			progressLengthExceeded = false;
		}
		
        protected void updateProgress() {
        	if ( getProgress() > getProgressLength() ) {
        		progressLengthExceeded = true;
        	}
        }
        
        public boolean isProgressLengthExceeded() {
        	return progressLengthExceeded;
        }
	}
}