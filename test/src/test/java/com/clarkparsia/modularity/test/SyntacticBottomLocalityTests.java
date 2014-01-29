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
import static com.clarkparsia.owlapiv3.OWL.domain;
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

public class SyntacticBottomLocalityTests {

	private SyntacticLocalityEvaluator	evaluator	= new SyntacticLocalityEvaluator( LocalityClass.BOTTOM_BOTTOM );

	private void assertLocal(OWLAxiom a, OWLEntity... signature) {
		assertTrue( evaluator.isLocal( a, set( signature ) ) );
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
		assertNonLocal( subClassOf( not( Class( "B" ) ), Class( "A" ) ), Class( "A" ) );
		assertNonLocal( subClassOf( not( Class( "B" ) ), Class( "A" ) ), Class( "A" ),
				Class( "B" ) );
	}

	/**
	 * Test that complemented super descriptions in subclass axioms are handled
	 * correctly. (Known to fail in r100 and earlier)
	 */
	@Test
	public void objectComplementSuperCls() {
		assertLocal( subClassOf( Class( "A" ), not( Nothing ) ), Class( "A" ) );
		assertLocal( subClassOf( Class( "A" ), not( Class( "B" ) ) ), Class( "A" ) );
		assertNonLocal( subClassOf( Class( "A" ), not( Class( "B" ) ) ), Class( "A" ),
				Class( "B" ) );
	}

	/**
	 * Test that object property domain axioms are handled correctly
	 */
	@Test
	public void objectDomain() {
		assertLocal( domain( ObjectProperty( "p" ), Class( "D" ) ) );
		assertLocal( domain( ObjectProperty( "p" ), Class( "D" ) ), Class( "D" ) );
		assertNonLocal( domain( ObjectProperty( "p" ), Class( "D" ) ), ObjectProperty( "p" ) );
		assertLocal( domain( ObjectProperty( "p" ), Thing), ObjectProperty( "p" ) );
		assertNonLocal( domain( ObjectProperty( "p" ), Class( "D" ) ), ObjectProperty( "p" ),
				Class( "D" ) );
	}

	/**
	 * Test that in all circumstances where the role of an object existential is
	 * not in a signature, the axiom is non-local (Known to fail prior to r99)
	 */
	@Test
	public void objectExistentialSuperClsProp() {
		assertNonLocal( subClassOf( Class( "A" ), some( ObjectProperty( "p" ), Thing ) ),
				Class( "A" ) );
		assertNonLocal(
				subClassOf( Class( "A" ), some( ObjectProperty( "p" ), Nothing ) ),
				Class( "A" ) );
		assertNonLocal( subClassOf( Class( "A" ), some( ObjectProperty( "p" ),
				Class( "B" ) ) ), Class( "A" ) );
		assertNonLocal( subClassOf( Class( "A" ), some( ObjectProperty( "p" ),
				Class( "A" ) ) ), Class( "A" ) );
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
		assertNonLocal( subClassOf( Class( "A" ), Class( "B" ) ), Class( "A" ) );
		assertLocal( subClassOf( Class( "A" ), Class( "B" ) ), Class( "B" ) );
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
