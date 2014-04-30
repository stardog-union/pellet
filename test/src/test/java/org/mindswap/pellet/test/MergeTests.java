// Portions Copyright (c) 2006 - 2008, Clark & Parsia, LLC.
// <http://www.clarkparsia.com>
// Clark & Parsia, LLC parts of this source code are available under the terms
// of the Affero General Public License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of
// proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package org.mindswap.pellet.test;

import static com.clarkparsia.pellet.utils.TermFactory.not;
import static com.clarkparsia.pellet.utils.TermFactory.oneOf;
import static com.clarkparsia.pellet.utils.TermFactory.some;
import static com.clarkparsia.pellet.utils.TermFactory.term;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import junit.framework.JUnit4TestAdapter;

import org.junit.Test;

import aterm.ATermAppl;

public class MergeTests extends AbstractKBTests {
	public static String	base	= "file:" + PelletTestSuite.base + "misc/";

	public static junit.framework.Test suite() {
		return new JUnit4TestAdapter( MergeTests.class );
	}	

	/*
	 * The purpose of this test case is create a merging chain x -> y -> z where
	 * each merge depends on a different non-deterministic branch. Then we do
	 * instance checking on x which should return true. This test shows that
	 * ABox.addType should add the type to each node in the merging chain to
	 * ensure that restores will not cause the added type to be lost.
	 */
	@Test
	public void instanceCheckForMergedNode() {
		classes( D, E );
		individuals( a, b, c, d, e );

		kb.addType( a, oneOf( b, c ) );

		kb.addSubClass( E, not( D ) );

		kb.addType( a, D );

		assertTrue( kb.isConsistent() );

		ATermAppl mergedTo = kb.getABox().getIndividual( a ).getMergedTo().getName();
		kb.addType( mergedTo, oneOf( d, e ) );

		assertTrue( kb.isConsistent() );

		assertTrue( kb.isType( a, not( E ) ) );
	}
	
	@Test
	public void addTypeToMergedNode() {
		classes( A, D );
		individuals( a, b, c );

		// a is either b or c
		kb.addType( a, oneOf( b, c ) );
		kb.addType( a, A );
		kb.addType( b, B );
		kb.addType( c, C );

		assertTrue( kb.isConsistent() );

		assertTrue( kb.isType( a, A ) );
		// we don't know which equality holds
		assertFalse( kb.isType( a, B ) );
		assertFalse( kb.isType( a, C ) );
		assertFalse( kb.isType( a, D ) );
		
		kb.addType( a, D );

		assertTrue( kb.isConsistent() );

		assertTrue( kb.isType( a, A ) );
		// we still don't know which equality holds
		assertFalse( kb.isType( a, B ) );
		assertFalse( kb.isType( a, C ) );
		// additional type causes a new inference
		assertTrue( kb.isType( a, D ) );
	}
	
	@Test
	public void removeTypeFromMergedNode() {
		classes( A, D );
		individuals( a, b, c );

		// a is either b or c
		kb.addType( a, oneOf( b, c ) );
		kb.addType( a, A );
		kb.addType( b, B );
		kb.addType( c, C );
		kb.addType( a, D );

		assertTrue( kb.isConsistent() );

		assertTrue( kb.isType( a, A ) );
		assertFalse( kb.isType( a, B ) );
		assertFalse( kb.isType( a, C ) );
		assertTrue( kb.isType( a, D ) );
		
		boolean removed = kb.removeType( a, D );
		
		assertTrue( removed );
		assertFalse( kb.isConsistencyDone() );
		
		assertTrue( kb.isConsistent() );

		assertTrue( kb.isType( a, A ) );
		assertFalse( kb.isType( a, B ) );
		assertFalse( kb.isType( a, C ) );
		assertFalse( kb.isType( a, D ) );
	}	
	
	@Test
	public void cannotRemoveInferredType() {
		classes( A, D );
		individuals( a, b, c, d );

		kb.addType( a, oneOf( b, c ) );
		kb.addType( a, A );
		kb.addType( b, B );
		kb.addType( c, C );
		kb.addType( d, D );
		kb.addSame( a, d );

		assertTrue( kb.isConsistent() );

		assertTrue( kb.isType( a, A ) );
		assertFalse( kb.isType( a, B ) );
		assertFalse( kb.isType( a, C ) );
		assertTrue( kb.isType( d, D ) );
		assertTrue( kb.isType( a, D ) );
		
		boolean removed = kb.removeType( a, D );
		
		assertTrue( !removed );
		
		assertTrue( kb.isConsistent() );

		assertTrue( kb.isType( a, A ) );
		assertFalse( kb.isType( a, B ) );
		assertFalse( kb.isType( a, C ) );
		assertTrue( kb.isType( d, D ) );
		assertTrue( kb.isType( a, D ) );
	}

	@Test
	public void addClashingTypeToMergedNode() {
		classes( A, B, C, D );
		individuals( a, b, c );

		// a is either b or c
		kb.addType( a, oneOf( b, c ) );
		kb.addType( a, A );
		kb.addType( b, B );
		kb.addType( c, C );

		assertTrue( kb.isConsistent() );

		// we don't know which equality holds
		assertTrue( kb.isType( a, A ) );
		assertFalse( kb.isType( a, B ) );
		assertFalse( kb.isType( a, C ) );
		
		// get which merge was chosen
		ATermAppl mergedTo = kb.getABox().getIndividual( a ).getMergedTo().getName();
		
		// add something to undo the merge
		if( mergedTo.equals( b ) ) 
			kb.addType( a, not( B ) );
		else
			kb.addType( a, not( C ) );

		assertTrue( kb.isConsistent() );

		assertTrue( kb.isType( a, A ) );
		// there is now a single possibility for the merge
		if( mergedTo.equals( b ) ) {
			assertTrue( kb.isType( a, C ) );
		}
		else {
			assertTrue( kb.isType( a, B ) );
		}
	}
	
	@Test
	public void addEdgeToMergedSubject() {
		objectProperties( p );
		individuals( a, b, c, d );

		// a is either b or c
		kb.addType( a, oneOf( b, c ) );

		assertTrue( kb.isConsistent() );

		// no edges to d
		assertFalse( kb.hasPropertyValue( a, p, d ) );
		assertFalse( kb.hasPropertyValue( b, p, d ) );
		assertFalse( kb.hasPropertyValue( c, p, d ) );
		
		kb.addPropertyValue( p, a, d );

		assertTrue( kb.isConsistent() );

		// there is an edge from a to d
		assertTrue( kb.hasPropertyValue( a, p, d ) );
		// still no edges from b or c to d
		assertFalse( kb.hasPropertyValue( b, p, d ) );
		assertFalse( kb.hasPropertyValue( c, p, d ) );
	}
	
	@Test
	public void addEdgeToMergedObject() {
		objectProperties( p );
		individuals( a, b, c, d );

		// a is either b or c
		kb.addType( a, oneOf( b, c ) );

		assertTrue( kb.isConsistent() );

		// no edges from d
		assertFalse( kb.hasPropertyValue( d, p, a ) );
		assertFalse( kb.hasPropertyValue( d, p, b ) );
		assertFalse( kb.hasPropertyValue( d, p, c ) );
		
		kb.addPropertyValue( p, d, a );

		assertTrue( kb.isConsistent() );

		// there is an edge from d to a
		assertTrue( kb.hasPropertyValue( d, p, a ) );
		// still no edges to b or c from d
		assertFalse( kb.hasPropertyValue( d, p, b ) );
		assertFalse( kb.hasPropertyValue( d, p, c ) );
	}

	@Test
	public void addEdgeToMergedSubjectObject() {
		objectProperties( p );
		individuals( a, b, c, d, e, f );

		// a is either b or c
		kb.addType( a, oneOf( b, c ) );
		// d is either e or f
		kb.addType( d, oneOf( e, f ) );
		
		assertTrue( kb.isConsistent() );

		// no edges to d
		assertFalse( kb.hasPropertyValue( a, p, d ) );
		assertFalse( kb.hasPropertyValue( b, p, d ) );
		assertFalse( kb.hasPropertyValue( c, p, d ) );
		assertFalse( kb.hasPropertyValue( a, p, e ) );
		assertFalse( kb.hasPropertyValue( b, p, e ) );
		assertFalse( kb.hasPropertyValue( c, p, e ) );
		assertFalse( kb.hasPropertyValue( a, p, f ) );
		assertFalse( kb.hasPropertyValue( b, p, f ) );
		assertFalse( kb.hasPropertyValue( c, p, f ) );
		
		kb.addPropertyValue( p, a, d );

		assertTrue( kb.isConsistent() );

		// there is only an edge from a to d
		assertTrue( kb.hasPropertyValue( a, p, d ) );
		assertFalse( kb.hasPropertyValue( b, p, d ) );
		assertFalse( kb.hasPropertyValue( c, p, d ) );
		assertFalse( kb.hasPropertyValue( a, p, e ) );
		assertFalse( kb.hasPropertyValue( b, p, e ) );
		assertFalse( kb.hasPropertyValue( c, p, e ) );
		assertFalse( kb.hasPropertyValue( a, p, f ) );
		assertFalse( kb.hasPropertyValue( b, p, f ) );
		assertFalse( kb.hasPropertyValue( c, p, f ) );
	}
	

	@Test
	public void addEdgeToMergedSubjectWithExistingEdge() {
		objectProperties( p );
		individuals( a, b, c, d, e );

		// a is either b or c
		kb.addType( a, oneOf( b, c ) );
		
		kb.addType( b, some( p, oneOf( d, e ) ) );
		kb.addType( c, some( p, oneOf( d, e ) ) );

		assertTrue( kb.isConsistent() );

		// no edges to d
		assertFalse( kb.hasPropertyValue( a, p, d ) );
		assertFalse( kb.hasPropertyValue( b, p, d ) );
		assertFalse( kb.hasPropertyValue( c, p, d ) );
		assertFalse( kb.hasPropertyValue( a, p, e ) );
		assertFalse( kb.hasPropertyValue( b, p, e ) );
		assertFalse( kb.hasPropertyValue( c, p, e ) );
		
		ATermAppl aMergedTo = kb.getABox().getIndividual( a ).getMergedTo().getName();
		ATermAppl aMergedToSucc = kb.getABox().getIndividual( aMergedTo ).getOutEdges().edgeAt( 0 ).getToName();
		ATermAppl aMergedToNotSucc = aMergedToSucc.equals( d ) ? e : d;
		
		kb.addPropertyValue( p, a, aMergedToSucc );		

		assertTrue( kb.isConsistent() );
	
		// there is only an edge from a to aMergedToSucc
		assertTrue( kb.hasPropertyValue( a, p, aMergedToSucc ) );
		assertFalse( kb.hasPropertyValue( a, p, aMergedToNotSucc ) );
		assertFalse( kb.hasPropertyValue( b, p, d ) );
		assertFalse( kb.hasPropertyValue( c, p, d ) );
		assertFalse( kb.hasPropertyValue( b, p, e ) );
		assertFalse( kb.hasPropertyValue( c, p, e ) );
	}
	

	
	@Test
	public void removeEdgeFromMergedObject() {
		objectProperties( p );
		individuals( a, b, c, d );

		kb.addType( a, oneOf( b, c ) );
		kb.addPropertyValue( p, d, a );
		
		assertTrue( kb.isConsistent() );

		assertTrue( kb.hasPropertyValue( d, p, a ) );
		assertFalse( kb.hasPropertyValue( d, p, b ) );
		assertFalse( kb.hasPropertyValue( d, p, c ) );
		
		kb.removePropertyValue( p, d, a );

		assertTrue( kb.isConsistent() );

		assertFalse( kb.hasPropertyValue( d, p, a ) );
		assertFalse( kb.hasPropertyValue( d, p, b ) );
		assertFalse( kb.hasPropertyValue( d, p, c ) );
	}
	
	@Test
	public void removeEdgeFromMergedSubject() {
		objectProperties( p );
		individuals( a, b, c, d );

		kb.addType( a, oneOf( b, c ) );

		assertTrue( kb.isConsistent() );
		kb.addPropertyValue( p, a, d );

		assertTrue( kb.hasPropertyValue( a, p, d ) );
		assertFalse( kb.hasPropertyValue( b, p, d ) );
		assertFalse( kb.hasPropertyValue( c, p, d ) );
		
		kb.removePropertyValue( p, a, d );

		assertTrue( kb.isConsistent() );

		assertFalse( kb.hasPropertyValue( a, p, d ) );
		assertFalse( kb.hasPropertyValue( b, p, d ) );
		assertFalse( kb.hasPropertyValue( c, p, d ) );
	}
	
	@Test
	public void removeEdgeFromMergedSubjectObject() {
		objectProperties( p );
		individuals( a, b, c, d, e, f );

		kb.addType( a, oneOf( b, c ) );
		kb.addType( d, oneOf( e, f ) );
		kb.addPropertyValue( p, a, d );
		
		assertTrue( kb.isConsistent() );

		assertTrue( kb.hasPropertyValue( a, p, d ) );
		assertFalse( kb.hasPropertyValue( b, p, d ) );
		assertFalse( kb.hasPropertyValue( c, p, d ) );
		assertFalse( kb.hasPropertyValue( a, p, e ) );
		assertFalse( kb.hasPropertyValue( b, p, e ) );
		assertFalse( kb.hasPropertyValue( c, p, e ) );
		assertFalse( kb.hasPropertyValue( a, p, f ) );
		assertFalse( kb.hasPropertyValue( b, p, f ) );
		assertFalse( kb.hasPropertyValue( c, p, f ) );
		
		kb.removePropertyValue( p, a, d );

		assertTrue( kb.isConsistent() );

		assertFalse( kb.hasPropertyValue( a, p, d ) );
		assertFalse( kb.hasPropertyValue( b, p, d ) );
		assertFalse( kb.hasPropertyValue( c, p, d ) );
		assertFalse( kb.hasPropertyValue( a, p, e ) );
		assertFalse( kb.hasPropertyValue( b, p, e ) );
		assertFalse( kb.hasPropertyValue( c, p, e ) );
		assertFalse( kb.hasPropertyValue( a, p, f ) );
		assertFalse( kb.hasPropertyValue( b, p, f ) );
		assertFalse( kb.hasPropertyValue( c, p, f ) );
	}
	
	@Test
	public void mergeManyIndividuals() {
		individuals( a );
		objectProperties( p );
		
		int N = 5000;
		
		ATermAppl[] b = new ATermAppl[N];
		ATermAppl[] c = new ATermAppl[N];
		
		kb.addObjectProperty( p );
		kb.addFunctionalProperty( p );
		
		kb.addIndividual( a );
		
		for( int i = 0; i < N; i++ ) {
			b[i] = term( "b" + i );
			kb.addIndividual( b[i] );
			c[i] = term( "c" + i );
			kb.addIndividual( c[i] );

			if( i == 0 ) {
				kb.addPropertyValue( p, a, b[i] );
				kb.addPropertyValue( p, a, c[i] );
			}
			else {
				kb.addPropertyValue( p, b[i-1], b[i] );
				kb.addPropertyValue( p, c[i-1], c[i] );
			}
		}
		
		assertTrue( kb.isConsistent() );
	}
}
