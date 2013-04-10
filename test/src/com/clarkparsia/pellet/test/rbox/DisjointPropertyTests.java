// Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public
// License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of
// proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package com.clarkparsia.pellet.test.rbox;

import static com.clarkparsia.pellet.utils.TermFactory.TOP;
import static com.clarkparsia.pellet.utils.TermFactory.TOP_LIT;
import static com.clarkparsia.pellet.utils.TermFactory.some;
import static com.clarkparsia.pellet.utils.TermFactory.term;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import junit.framework.JUnit4TestAdapter;

import org.junit.Test;
import org.mindswap.pellet.test.AbstractKBTests;
import org.mindswap.pellet.test.PelletTestSuite;

import aterm.ATermAppl;

public class DisjointPropertyTests extends AbstractKBTests {
	public static String	base	= "file:" + PelletTestSuite.base + "misc/";

	public static junit.framework.Test suite() {
		return new JUnit4TestAdapter( DisjointPropertyTests.class );
	}
	
	@Test
	public void simpleInconcistency() {
		individuals(a, b, c);
		objectProperties(p, q);
		
		kb.addDisjointProperty(p, q);
		
		kb.addPropertyValue( p, a, b );
		kb.addPropertyValue( q, a, b );
		
		assertFalse( kb.isConsistent() );
	}
	
	@Test
	public void subPropertyInconcistency() {
		individuals(a, b, c);
		objectProperties(p, q);
		
		kb.addDisjointProperty(p, q);

		ATermAppl subP = term("subP");
		
		kb.addObjectProperty( subP );
		kb.addSubProperty( subP, p );
		
		kb.addPropertyValue( subP, a, b );
		kb.addPropertyValue( q, a, b );
		
		assertFalse( kb.isConsistent() );
	}
	
	@Test
	public void superPropertyConcistency() {
		individuals(a, b, c);
		objectProperties(p, q);
		
		kb.addDisjointProperty(p, q);

		ATermAppl supP = term("supP");
		
		kb.addObjectProperty( supP );
		kb.addSubProperty( p, supP );
		
		kb.addPropertyValue( supP, a, b );
		kb.addPropertyValue( q, b, b );
		
		assertTrue( kb.isConsistent() );
	}
	
	@Test
	public void invPropertyInconcistency() {
		individuals(a, b, c);
		objectProperties(p, q);
		
		kb.addDisjointProperty(p, q);

		ATermAppl invP = term("invP");
		
		kb.addObjectProperty( invP );
		kb.addInverseProperty( invP, p );
		
		kb.addPropertyValue( invP, b, a );
		kb.addPropertyValue( q, a, b );
		
		assertFalse( kb.isConsistent() );
	}	
	
	@Test
	public void differentFromSubjects() {
		individuals(a, b, c);
		objectProperties(p, q);
		
		kb.addDisjointProperty(p, q);

		kb.addPropertyValue( p, a, c );
		kb.addPropertyValue( q, b, c );
		
		assertTrue( kb.isDifferentFrom( a, b ) );
	}
	
	@Test
	public void differentFromObjects() {
		individuals(a, b, c);
		objectProperties(p, q);
		
		kb.addDisjointProperty(p, q);

		kb.addPropertyValue( p, a, b );
		kb.addPropertyValue( q, a, c );
		
		assertTrue( kb.isDifferentFrom( b, c ) );
	}
	
	@Test
	public void test547a() {
		objectProperties(p, q, r);
		
		kb.addDisjointProperty(p, q);
		kb.addSubClass(some(p,TOP), some(q,TOP));

		assertTrue(kb.isConsistent());
		assertTrue(kb.isDisjointProperty(p, q));
		assertFalse(kb.isDisjointProperty(p, r));
		assertFalse(kb.isDisjointProperty(q, r));
	}
	
	@Test
	public void test547b() {
		dataProperties(p, q, r);
		
		kb.addDisjointProperty(p, q);
		kb.addSubClass(some(p,TOP_LIT), some(q,TOP_LIT));

		assertTrue(kb.isConsistent());
		assertTrue(kb.isDisjointProperty(p, q));
		assertFalse(kb.isDisjointProperty(p, r));
		assertFalse(kb.isDisjointProperty(q, r));
	}
}
