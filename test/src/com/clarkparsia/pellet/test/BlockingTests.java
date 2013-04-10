// Portions Copyright (c) 2006 - 2008, Clark & Parsia, LLC.
// <http://www.clarkparsia.com>
// Clark & Parsia, LLC parts of this source code are available under the terms
// of the Affero General Public License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of
// proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package com.clarkparsia.pellet.test;

import static com.clarkparsia.pellet.utils.TermFactory.BOTTOM;
import static com.clarkparsia.pellet.utils.TermFactory.TOP;
import static com.clarkparsia.pellet.utils.TermFactory.all;
import static com.clarkparsia.pellet.utils.TermFactory.and;
import static com.clarkparsia.pellet.utils.TermFactory.inv;
import static com.clarkparsia.pellet.utils.TermFactory.list;
import static com.clarkparsia.pellet.utils.TermFactory.max;
import static com.clarkparsia.pellet.utils.TermFactory.not;
import static com.clarkparsia.pellet.utils.TermFactory.some;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import junit.framework.JUnit4TestAdapter;

import org.junit.Test;
import org.mindswap.pellet.owlapi.OWLAPILoader;
import org.mindswap.pellet.test.AbstractKBTests;
import org.mindswap.pellet.test.MiscTests;

public class BlockingTests extends AbstractKBTests {
	public static junit.framework.Test suite() {
		return new JUnit4TestAdapter( BlockingTests.class );
	}

	@Test
	public void transitivityInverse() {
		classes( C, D );
		objectProperties( p, q, r );
		
		kb.addTransitiveProperty( r );
		kb.addSubProperty( r, q );
		kb.addSubClass( D, all( q, C ) );

		assertTrue( kb.isConsistent() );
		
		assertFalse( kb.isSatisfiable(
			some( p, and( D, 
					      some( p, and( some( inv( r ), D ), 
					    		        some( r, not( C ) ) ) ) ) ) ) );
	}	

	@Test
	public void propertyChain() {
		classes(C, D);
		objectProperties(p, q, r, s);

		kb.addDisjointClass(C, D);
		kb.addSubProperty(list(p, inv(q), r, s), s);
		kb.addSubClass(D, all(s, C));
		kb.addSubClass(D, some(p, some(inv(q), some(r, some(s, D)))));

		assertTrue(kb.isConsistent());

		assertFalse(kb.isSatisfiable(D));
	}
	
	@Test
	public void propertyChainInverse() {
		classes( C, D );
		objectProperties( p, q, r );
		
		kb.addSubProperty( list( r, p ) , q );
		kb.addSubClass( D, all( q, C ) );

		assertTrue( kb.isConsistent() );
		
		assertFalse( kb.isSatisfiable(
			some( p, and( D, 
					      some( p, and( some( inv( r ), D ), 
					    		        some( p, not( C ) ) ) ) ) ) ) );
	}			

	@Test
	public void propertyChainInverseCardinality() {
		classes( C, D );
		objectProperties( p, q, r );
		
		// functionality has no effect, it is used to force 
		// double blocking instead of equality blocking
		kb.addFunctionalProperty( p );
		kb.addSubProperty( list( r, p ) , q );
		kb.addSubClass( D, all( q, C ) );

		assertTrue( kb.isConsistent() );
		
		assertFalse( kb.isSatisfiable(
			some( p, and( D, 
					      some( p, and( some( inv( r ), D ), 
					    		        some( p, not( C ) ) ) ) ) ) ) );
	}	

	@Test
	public void doubleBlockingExample() {
		classes( C, D );
		objectProperties( f, r );
		
		kb.addTransitiveProperty( r );
		kb.addSubProperty( f, r );
		kb.addEquivalentClass( D, and( C, some( f, not( C ) ) ) );
		kb.addSubClass( TOP, max( f, 1, TOP ) );

		assertTrue( kb.isConsistent() );
		
		assertFalse( kb.isSatisfiable(
			and( not( C ),
				 some( inv( f ), D ),
				 all( inv( r ), some( inv( f ), D ) ) ) ) );
	}
	
	@Test
	public void complexInconsistent() {
		kb = new OWLAPILoader().createKB( MiscTests.base + "one+one-inconsistent.owl" );
		
		assertFalse( kb.isConsistent() );
	}
	
	@Test
	public void complexAllUnsat() {
		kb = new OWLAPILoader().createKB( MiscTests.base + "one+one-consistent-but-all-unsat.owl" );
		
		assertTrue( kb.isConsistent() );
		
		assertEquals( kb.getClasses(), kb.getUnsatisfiableClasses() );
	}
	
	@Test
	public void complexAllInfSat() {
		kb = new OWLAPILoader().createKB( MiscTests.base + "one+one-consistent-and-all-inf-sat.owl" );
		
		assertTrue( kb.isConsistent() );
		
		assertTrue( kb.getUnsatisfiableClasses().isEmpty() );
	}
	

	@Test
	public void deadlockBlock() {
		classes( C, D );
		objectProperties( p, q, r );
		
		kb.addSubClass( D, BOTTOM );

		assertTrue( kb.isConsistent() );
		
		assertFalse( kb.isSatisfiable( and( some( p, some( p, D ) ), some( p, D ) ) ) );
	}
	

	@Test
	public void yoyo() {
		classes( A );
		objectProperties( r );
		individuals( a, b );
		
		kb.addFunctionalProperty( r );		
		kb.addSubClass( A, all( r, some( r, TOP ) ) );
		kb.addType( a, A );
		kb.addType( a, some( r, TOP ) );
		kb.addPropertyValue( r, a, a );
		kb.addPropertyValue( r, a, b );

		assertTrue( kb.isConsistent() );
		
		assertTrue( kb.isSatisfiable( A ) );
	}	

}