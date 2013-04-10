// Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package com.clarkparsia.pellet.test.classification;

import static com.clarkparsia.pellet.utils.TermFactory.all;
import static com.clarkparsia.pellet.utils.TermFactory.inv;
import static com.clarkparsia.pellet.utils.TermFactory.list;
import static com.clarkparsia.pellet.utils.TermFactory.some;
import static org.junit.Assert.assertTrue;
import junit.framework.JUnit4TestAdapter;

import org.junit.Test;
import org.mindswap.pellet.test.AbstractKBTests;

/**
 * <p>
 * Title:
 * </p>
 * <p>
 * Description:
 * </p>
 * <p>
 * Copyright: Copyright (c) 2009
 * </p>
 * <p>
 * Company: Clark & Parsia, LLC. <http://www.clarkparsia.com>
 * </p>
 * 
 * @author Evren Sirin
 */
public class SimpleClassificationTests extends AbstractKBTests {
	public static junit.framework.Test suite() {
		return new JUnit4TestAdapter( SimpleClassificationTests.class );
	}
	
	@Test
	public void cdClassificationWithInverses() {
		classes( C, D, E );
		objectProperties( p );

		kb.addSubClass( C, some( p, D ) );
		kb.addSubClass( D, all( inv( p ), E ) );

		assertTrue( kb.isConsistent() );
		assertTrue( kb.isSubClassOf( C, E ) );
		
		kb.classify();
		
		assertTrue( kb.isSubClassOf( C, E ) );
	}
	
	@Test
	public void cdClassificationWithCyclicInverses() {
		classes( C, D, E );
		objectProperties( p, q );

		kb.addSubClass( E, some( p, C ) );
		kb.addSubClass( C, all( inv( p ), D ) );
		kb.addSubClass( D, some( q, E ) );

		assertTrue( kb.isConsistent() );
		assertTrue( kb.isSubClassOf( E, D ) );
		
		kb.classify();
		
		assertTrue( kb.isSubClassOf( E, D ) );
	}
	
	@Test
	public void cdClassificationWithPropChain() {
		classes( C, D, E );
		objectProperties( p, q, r );

		kb.addSubProperty( list( p, q ), r );
		kb.addSubClass( C, some( p, some( q, D ) ) );
		kb.addSubClass( D, all( inv( r ), E ) );

		assertTrue( kb.isConsistent() );
		assertTrue( kb.isSubClassOf( C, E ) );
		
		kb.classify();
		
		kb.printClassTree();
		
		assertTrue( kb.isSubClassOf( C, E ) );
	}
}
