// Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package com.clarkparsia.modularity.test;

import static com.clarkparsia.modularity.test.TestUtils.set;
import static com.clarkparsia.owlapiv3.OWL.Class;
import static com.clarkparsia.owlapiv3.OWL.Nothing;
import static com.clarkparsia.owlapiv3.OWL.ObjectProperty;
import static com.clarkparsia.owlapiv3.OWL.Thing;
import static com.clarkparsia.owlapiv3.OWL.all;
import static com.clarkparsia.owlapiv3.OWL.max;
import static com.clarkparsia.owlapiv3.OWL.not;
import static com.clarkparsia.owlapiv3.OWL.some;
import static com.clarkparsia.owlapiv3.OWL.subClassOf;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLEntity;

import com.clarkparsia.owlapi.modularity.locality.LocalityClass;
import com.clarkparsia.owlapi.modularity.locality.SyntacticLocalityEvaluator;

public class SyntacticTopLocalityTests {

	private SyntacticLocalityEvaluator	evaluator	= new SyntacticLocalityEvaluator( LocalityClass.TOP_BOTTOM );

	private void assertLocal(OWLAxiom a, OWLEntity... signature) {
		assertTrue( evaluator.isLocal( a, set( signature )  ) );
	}

	private void assertNonLocal(OWLAxiom a, OWLEntity... signature) {
		assertFalse( evaluator.isLocal( a, set( signature ) ) );
	}

	/**
	 * Test that complemented sub class descriptions are handled correctly.
	 */
	@Test
	public void objectComplementSubCls() {
		assertLocal( subClassOf( not( Thing ), Class( "A" ) ), Class( "A" ) );
		assertLocal( subClassOf( not( Class( "B" ) ), Class( "A" ) ), Class( "A" ) );
		assertNonLocal( subClassOf( not( Class( "B" ) ), Class( "A" ) ), Class( "A" ),
				Class( "B" ) );
	}

	/**
	 * Test that filler classes used in existentials as superclass are handled
	 * correctly.
	 */
	@Test
	public void objectExistentialFillerSuperCls() {
		assertNonLocal( subClassOf( Class( "A" ), some( ObjectProperty( "p" ),
				Class( "B" ) ) ), Class( "A" ) );
		assertNonLocal( subClassOf( Class( "A" ), some( ObjectProperty( "p" ),
				Class( "B" ) ) ), Class( "A" ), ObjectProperty( "p" ) );
	}

	@Test
	public void objectMaxSubCls() {
		assertNonLocal( subClassOf( max( ObjectProperty( "p" ), 2, Thing ), Class( "A" ) ),
				Class( "A" ) );
		assertNonLocal(
				subClassOf( max( ObjectProperty( "p" ), 2, Thing ), Class( "A" ) ),
				Class( "A" ), ObjectProperty( "p" ) );
		assertNonLocal(
				subClassOf( max( ObjectProperty( "p" ), 2, Nothing ), Class( "A" ) ),
				Class( "A" ) );
		assertNonLocal(
				subClassOf( max( ObjectProperty( "p" ), 2, Nothing ), Class( "A" ) ),
				Class( "A" ), ObjectProperty( "p" ) );
		assertNonLocal( subClassOf( max( ObjectProperty( "p" ), 2, Class( "B" ) ),
				Class( "A" ) ), Class( "A" ) );
		assertNonLocal( subClassOf( max( ObjectProperty( "p" ), 2, Class( "B" ) ),
				Class( "A" ) ), Class( "A" ), ObjectProperty( "p" ) );
		assertNonLocal( subClassOf( max( ObjectProperty( "p" ), 2, Class( "B" ) ),
				Class( "A" ) ), Class( "A" ), ObjectProperty( "p" ), Class( "B" ) );
	}

	/**
	 * Test that named classes as super in subClass axioms are handled
	 * correctly.
	 */
	@Test
	public void objectSuperCls() {
		assertLocal( subClassOf( Class( "A" ), Class( "B" ) ), Class( "A" ) );
		assertNonLocal( subClassOf( Class( "A" ), Class( "B" ) ), Class( "B" ) );

	}

	/**
	 * Test that universal object restriction subclasses are handled correctly
	 */
	@Test
	public void objectUniversalSubCls() {
		assertNonLocal( subClassOf( all( ObjectProperty( "p" ), Class( "B" ) ),
				Class( "A" ) ), Class( "A" ) );
		assertNonLocal( subClassOf( all( ObjectProperty( "p" ), Class( "B" ) ),
				Class( "A" ) ), Class( "A" ), Class( "B" ) );
		assertNonLocal( subClassOf( all( ObjectProperty( "p" ), Class( "B" ) ),
				Class( "A" ) ), Class( "A" ), ObjectProperty( "p" ) );
	}
}
