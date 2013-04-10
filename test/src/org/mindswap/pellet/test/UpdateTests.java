// Portions Copyright (c) 2006 - 2008, Clark & Parsia, LLC.
// <http://www.clarkparsia.com>
// Clark & Parsia, LLC parts of this source code are available under the terms
// of the Affero General Public License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of
// proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package org.mindswap.pellet.test;

import static com.clarkparsia.pellet.utils.TermFactory.hasValue;
import static com.clarkparsia.pellet.utils.TermFactory.or;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import junit.framework.JUnit4TestAdapter;

import org.junit.Test;

import aterm.ATermAppl;

public class UpdateTests extends AbstractKBTests {
	public static String	base	= "file:" + PelletTestSuite.base + "misc/";

	public static junit.framework.Test suite() {
		return new JUnit4TestAdapter( UpdateTests.class );
	}

	/**
	 * Test case for #404
	 */
	@Test
	public void addPropertyValueAfterConsistency() {
		objectProperties( p );
		individuals( a, c, d );

		// either p(a, c) or p(a, d) holds
		kb.addType( a, or( hasValue( p, c ), hasValue( p, d ) ) );

		assertTrue( kb.isConsistent() );

		// check which non-deterministic choice was made
		ATermAppl succ = kb.getABox().getIndividual( a ).getOutEdges().edgeAt(0).getToName();
		ATermAppl nonSucc = succ.equals( c ) ? d : c;
		
		// no entailment can be made yet
		assertFalse( kb.hasPropertyValue( a, p, nonSucc ) );
		assertFalse( kb.hasPropertyValue( a, p, succ ) );
		
		// assert the property value in non-deterministic choice
		kb.addPropertyValue( p, a, succ );

		assertTrue( kb.isConsistent() );

		// this entailment still does not hold
		assertFalse( kb.hasPropertyValue( a, p, nonSucc ) );
		// this entailment should now hold
		assertTrue( kb.hasPropertyValue( a, p, succ ) );		
	}

	/**
	 * Test case for #399
	 */
	@Test
	public void addTypeValueWithNonDeterministic() {
		classes( C, D );
		individuals( a );

		// either C(a) or D(a) holds
		kb.addType( a, or( C, D ) );

		assertTrue( kb.isConsistent() );

		// check which non-deterministic choice was made
		ATermAppl type = kb.getABox().getIndividual( a ).hasType( C ) ? C : D;
		ATermAppl nonType = type.equals( C ) ? D : C;
		
		// no entailment can be made yet
		assertFalse( kb.isType( a, nonType ) );
		assertFalse( kb.isType( a, type ) );

		// assert the type in non-deterministic choice
		kb.addType( a, type );

		assertTrue( kb.isConsistent() );

		// this entailment still does not hold
		assertFalse( kb.isType( a, nonType ) );
		// this entailment should now hold
		assertTrue( kb.isType( a, type ) );
	}
}
