// Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package com.clarkparsia.pellet.test.rbox;

import static com.clarkparsia.pellet.utils.TermFactory.TOP;
import static com.clarkparsia.pellet.utils.TermFactory.TOP_OBJECT_PROPERTY;
import static com.clarkparsia.pellet.utils.TermFactory.inv;
import static com.clarkparsia.pellet.utils.TermFactory.not;
import static com.clarkparsia.pellet.utils.TermFactory.self;
import static org.junit.Assert.assertFalse;
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
public class PropertyCharacteristicsTests extends AbstractKBTests {
	public static junit.framework.Test suite() {
		return new JUnit4TestAdapter( PropertyCharacteristicsTests.class );
	}

	
	@Test
	public void reflexivePropertyCausingMerge1() {
		// test #433
		classes( A, B );
		individuals( a, b );
		objectProperties( p );

		kb.addReflexiveProperty( p );
		kb.addSymmetricProperty( p );
		kb.addFunctionalProperty( p );
		
		kb.addSubClass( A, B );
		
		kb.addType( a, A );
		kb.addType( b, A );
		
		kb.addPropertyValue( p, a, b );
				
		assertTrue( kb.isConsistent() );
		assertTrue( kb.isSameAs( a, b ) );
	}
	
	@Test
	public void reflexivePropertyCausingMerge2() {
		// test #433
		classes( A );
		individuals( a, b );
		objectProperties( p );

		kb.addReflexiveProperty( p );
		kb.addSymmetricProperty( p );
		kb.addFunctionalProperty( p );
		
		kb.addDomain( TOP_OBJECT_PROPERTY, A );
		
		kb.addPropertyValue( p, a, b );
				
		assertTrue( kb.isConsistent() );
		assertTrue( kb.isSameAs( a, b ) );
	}
	
	@Test
	public void irreflexiveSH() {
		// test #433
		objectProperties( p );

		kb.addIrreflexiveProperty( inv( p ) );

		assertTrue( kb.isConsistent() );
		assertTrue( kb.isIrreflexiveProperty( p ) );
	}
	

	
	@Test
	/**
	 * Tests for the bug reported in #376
	 */
	public void test376() {
		annotationProperties( p );
		
		assertFalse( kb.isFunctionalProperty( p ) );
	}
	
	@Test
	public void testReflexiveDisjoint() {
		classes(C);
		objectProperties(p, q);
		
		kb.addReflexiveProperty(p);
		kb.addDomain(q, C);
		kb.addRange(q, not(C));

		assertTrue(kb.isConsistent());
		assertFalse(kb.isDisjointProperty(p, q));
	}	

	@Test
	public void testAsymmetricEquivalent() {
		objectProperties(q, r);
		
		kb.addAsymmetricProperty(q);
		kb.addEquivalentProperty(q, r);

		assertTrue(kb.isConsistent());
		assertTrue(kb.isAsymmetricProperty(q));
		assertTrue(kb.isAsymmetricProperty(r));
	}

	@Test
	public void testAsymmetricInverseDisjoint() {
		objectProperties(p, q, r);
		
		kb.addInverseProperty(p, q);
		kb.addAsymmetricProperty(q);
		kb.addEquivalentProperty(q, r);

		assertTrue(kb.isConsistent());
		assertTrue(kb.isDisjointProperty(p, q));
		assertTrue(kb.isDisjointProperty(p, r));
	}	

	@Test
	public void testReflexiveSubPropertyExplicit() {
		objectProperties(p, q);
		
		kb.addReflexiveProperty(p);
		kb.addSubProperty(p, q);

		assertTrue(kb.isConsistent());
		assertTrue(kb.isReflexiveProperty(p));
		assertTrue(kb.isReflexiveProperty(q));
	}

	@Test
	public void testReflexiveSubPropertyImplicit() {
		classes(C);
		objectProperties(p, q);
		
		kb.addSubClass(TOP, self(p));
		kb.addSubProperty(p, q);

		assertTrue(kb.isConsistent());
		assertTrue(kb.isReflexiveProperty(p));
		assertTrue(kb.isReflexiveProperty(q));
	}

	@Test
	public void testIrreflexive() {
		objectProperties(p, q);
		
		kb.addIrreflexiveProperty(p);

		assertTrue(kb.isConsistent());
		assertTrue(kb.isIrreflexiveProperty(p));
	}

	@Test
	public void testIrreflexiveAsymetric() {
		objectProperties(p, q);
		
		kb.addAsymmetricProperty(p);

		assertTrue(kb.isConsistent());
		assertTrue(kb.isIrreflexiveProperty(p));
		assertFalse(kb.isReflexiveProperty(p));
	}
	
	@Test
	public void testNotIrreflexive() {
		objectProperties(p, q);
		
		kb.addIrreflexiveProperty(p);
		kb.addSubProperty(p, q);

		assertTrue(kb.isConsistent());
		assertTrue(kb.isIrreflexiveProperty(p));
		assertFalse(kb.isIrreflexiveProperty(q));
	}
	
	@Test
	public void irreflexivePropertyCausingDifferentFrom() {
		// test #433
		individuals( a, b );
		objectProperties( p );

		kb.addIrreflexiveProperty( p );
		
		kb.addPropertyValue( p, a, b );		
				
		assertTrue( kb.isConsistent() );
		assertTrue( kb.isDifferentFrom( a, b ) );
	}
}
