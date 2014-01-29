// Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package com.clarkparsia.modularity.test;

import static com.clarkparsia.modularity.test.TestUtils.set;
import static com.clarkparsia.owlapiv3.OWL.Class;
import static com.clarkparsia.owlapiv3.OWL.ObjectProperty;
import static com.clarkparsia.owlapiv3.OWL.Thing;
import static com.clarkparsia.owlapiv3.OWL.domain;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLEntity;

import com.clarkparsia.owlapi.modularity.locality.LocalityClass;
import com.clarkparsia.owlapi.modularity.locality.SyntacticLocalityEvaluator;

public class SyntacticTopTopLocalityTests {

	private SyntacticLocalityEvaluator	evaluator	= new SyntacticLocalityEvaluator( LocalityClass.TOP_TOP );

	private void assertLocal(OWLAxiom a, OWLEntity... signature) {
		assertTrue( evaluator.isLocal( a, set( signature ) ) );
	}

	private void assertNonLocal(OWLAxiom a, OWLEntity... signature) {
		assertFalse( evaluator.isLocal( a, set( signature ) ) );
	}

	/**
	 * Test that object property domain axioms are handled correctly
	 */
	@Test
	public void objectDomain() {
		assertLocal( domain( ObjectProperty( "p" ), Class( "D" ) ) );
		assertNonLocal( domain( ObjectProperty( "p" ), Class( "D" ) ), Class( "D" ) );
		assertLocal( domain( ObjectProperty( "p" ), Class( "D" ) ), ObjectProperty( "p" ) );
		assertLocal( domain( ObjectProperty( "p" ), Thing ), ObjectProperty( "p" ) );
		assertNonLocal( domain( ObjectProperty( "p" ), Class( "D" ) ), ObjectProperty( "p" ),
				Class( "D" ) );
	}
}
