// Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package com.clarkparsia.pellet.test.tbox;

import static com.clarkparsia.pellet.utils.TermFactory.and;
import static com.clarkparsia.pellet.utils.TermFactory.inv;
import static com.clarkparsia.pellet.utils.TermFactory.or;
import static com.clarkparsia.pellet.utils.TermFactory.some;
import static com.clarkparsia.pellet.utils.TermFactory.term;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mindswap.pellet.utils.ATermUtils.makeAnd;
import static org.mindswap.pellet.utils.ATermUtils.makeEqClasses;
import static org.mindswap.pellet.utils.ATermUtils.makeNot;
import static org.mindswap.pellet.utils.ATermUtils.makeOr;
import static org.mindswap.pellet.utils.ATermUtils.makeSub;

import java.util.Collections;

import junit.framework.JUnit4TestAdapter;

import org.junit.Before;
import org.junit.Test;
import org.mindswap.pellet.PelletOptions;
import org.mindswap.pellet.tbox.TBox;
import org.mindswap.pellet.tbox.impl.Unfolding;
import org.mindswap.pellet.test.AbstractKBTests;
import org.mindswap.pellet.utils.iterator.IteratorUtils;

import aterm.ATermAppl;

/**
 * <p>
 * Title: TBoxTests
 * </p>
 * <p>
 * Description: TBox unit tests (those than can be done without depending on the
 * Knowledgebase or ABox)
 * </p>
 * <p>
 * Copyright: Copyright (c) 2008
 * </p>
 * <p>
 * Company: Clark & Parsia, LLC. <http://www.clarkparsia.com>
 * </p>
 * 
 * @author Mike Smith
 */
public class TBoxTests extends AbstractKBTests {

	public static junit.framework.Test suite() {
		return new JUnit4TestAdapter( TBoxTests.class );
	}

	private TBox tbox;
	
	
	@Before
	public void initializeKB() {
		super.initializeKB();
		tbox = kb.getTBox();
	}
	
	private void prepareTBox() {
		tbox.prepare();
	}

	/**
	 * Test that tbox axioms which have been "simplified away" during absorption
	 * are re-added if removal of another tbox axiom necessitates it.
	 */
	@Test
	public void removedByAbsorbReaddedOnChange() {

		boolean oldTracing = PelletOptions.USE_TRACING;
		PelletOptions.USE_TRACING = true;
		try {
			classes( A, B, C, D );

			ATermAppl axiom1 = makeEqClasses( A, makeOr( C, D ) );
			assertTrue( tbox.addAxiom( axiom1 ) );

			ATermAppl axiom2 = makeSub( A, B );
			assertTrue( tbox.addAxiom( axiom2 ) );

			Unfolding unfoldForAxiom2 = Unfolding.create( B, Collections.singleton( axiom2 ) );

			prepareTBox();

			/*
			 * At this stage the TBox does not *directly* contain A [= B , but
			 * it should be implicit by A [= C u D , C [= B , D [= B. Note that
			 * if this assertion fails, it does not necessarily mean that the
			 * TBox implementation is broken, it may mean the implementation has
			 * changed in a way that makes this test not useful.
			 */
			assertFalse( IteratorUtils.toSet( tbox.unfold( A ) ).contains( unfoldForAxiom2 ) );

			tbox.removeAxiom( axiom1 );
			prepareTBox();

			/*
			 * After the equivalence is removed, any simplification (e.g., the
			 * one above) must be corrected.
			 */
			assertTrue( IteratorUtils.toSet( tbox.unfold( A ) ).contains( unfoldForAxiom2 ) );
		} finally {
			PelletOptions.USE_TRACING = oldTracing;
		}
	}
	
	@Test
	public void assertedAxioms() {
		classes( A, B, C, D );

		ATermAppl axiom = makeSub( makeAnd(A, B), makeNot(B) );
		tbox.addAxiom( axiom );

		prepareTBox();

		assertTrue(tbox.getAxioms().size() > 1);
		assertTrue(tbox.getAxioms().contains(axiom));
		assertEquals(Collections.singleton(axiom), tbox.getAssertedAxioms());
	}
	
	@Test
	public void binaryAbsorption() {
		ATermAppl SPECIALCLIENT = term("SPECIALCLIENT");
		ATermAppl CLIENT = term("CLIENT");
		ATermAppl EXPENSIVE = term("EXPENSIVE");
		ATermAppl PROFITABLE = term("PROFITABLE");
		ATermAppl TRUSTEDCLIENT = term("TRUSTEDCLIENT");
		ATermAppl Recommend = term("Recommend");
		ATermAppl Buy = term("Buy");
		
		classes( SPECIALCLIENT, CLIENT, EXPENSIVE, PROFITABLE, TRUSTEDCLIENT );
		objectProperties( Buy, Recommend );

		tbox.addAxiom( makeSub( SPECIALCLIENT, TRUSTEDCLIENT ) );
		tbox.addAxiom( makeEqClasses( SPECIALCLIENT, 
				and( CLIENT , 
					 some(Buy, or(EXPENSIVE, PROFITABLE)), 
					 some(inv(Recommend), TRUSTEDCLIENT))));
		
		prepareTBox();
	}
	
	@Test
	public void removeAssertedAxioms() {
		boolean oldTracing = PelletOptions.USE_TRACING;
		PelletOptions.USE_TRACING = true;
		try {
			classes( A, B, C, D );
	
			ATermAppl axiom = makeSub( makeAnd(A, B), makeNot(B) );
			tbox.addAxiom( axiom );
	
			prepareTBox();
			
			tbox.removeAxiom(axiom);
			
			prepareTBox();
	
			assertTrue(tbox.getAxioms().isEmpty());
			assertTrue(tbox.getAssertedAxioms().isEmpty());
		} finally {
			PelletOptions.USE_TRACING = oldTracing;
		}
	}
}
