// Copyright (c) 2006 - 2009, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public
// License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of
// proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package com.clarkparsia.pellet.test.rbox;

import static com.clarkparsia.owlapiv3.OWL.classAssertion;
import static com.clarkparsia.owlapiv3.OWL.constant;
import static com.clarkparsia.owlapiv3.OWL.declaration;
import static com.clarkparsia.owlapiv3.OWL.domain;
import static com.clarkparsia.owlapiv3.OWL.or;
import static com.clarkparsia.owlapiv3.OWL.propertyAssertion;
import static com.clarkparsia.owlapiv3.OWL.range;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Properties;

import junit.framework.JUnit4TestAdapter;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mindswap.pellet.PelletOptions;

import com.clarkparsia.owlapiv3.XSD;
import com.clarkparsia.pellet.owlapiv3.PelletLoader;
import com.clarkparsia.pellet.test.owlapi.AbstractOWLAPITests;
import com.clarkparsia.pellet.utils.PropertiesBuilder;

/**
 * <p>
 * Title:
 * </p>
 * <p>
 * Description: Tests primarily focused on the behavior of {@link PelletLoader}
 * </p>
 * <p>
 * Copyright: Copyright (c) 2009
 * </p>
 * <p>
 * Company: Clark & Parsia, LLC. <http://www.clarkparsia.com>
 * </p>
 * 
 * @author Mike Smith
 * @author Evren Sirin
 */
public class RBoxUpdateTests extends AbstractOWLAPITests {

	public static junit.framework.Test suite() {
		return new JUnit4TestAdapter( RBoxUpdateTests.class );
	}

	private static Properties oldOptions;

	@BeforeClass
	public static void enableTracing() {
		Properties newOptions = PropertiesBuilder.singleton( "USE_TRACING", "true" );
		oldOptions = PelletOptions.setOptions( newOptions );
	}

	@AfterClass
	public static void resetTracing() {
		PelletOptions.setOptions( oldOptions );
	}

	/**
	 * A data property domain axiom should be removable without causing a full
	 * KB reload
	 */
	@Test
	public void removeDataPropertyDomainAxiom() {
		createReasoner( 
			declaration( dp ),
			declaration( C ), 
			domain( dp, C ),
			propertyAssertion( a, dp, lit ) 
		);

		assertTrue( reasoner.isConsistent() );
		assertTrue( reasoner.isEntailed( classAssertion( a, C ) ) );

		boolean changeApplied = processRemove( domain( dp, C ) );
		assertTrue( "Unable to remove data property domain axiom", changeApplied );

		assertTrue( reasoner.isConsistent() );
		assertFalse( reasoner.isEntailed( classAssertion( a, C ) ) );
	}

	/**
	 * A data property domain axiom should be removable without causing a full
	 * KB reload even if it is a class expression
	 */
	@Test
	public void removeDataPropertyDomainAxiomExpression() {
		createReasoner( 
			declaration( dp ),
			declaration( C ), 
			declaration( D ), 
			domain( dp, or(C, D) ),
			propertyAssertion( a, dp, lit ) 
		);

		assertTrue( reasoner.isConsistent() );
		assertTrue( reasoner.isEntailed( classAssertion( a, or(C, D) ) ) );

		boolean changeApplied = processRemove( domain( dp, or(C, D) ) );
		assertTrue( "Unable to remove data property domain axiom", changeApplied );

		assertTrue( reasoner.isConsistent() );
		assertFalse( reasoner.isEntailed( classAssertion( a, or(C, D) ) ) );
	}
	
	/**
	 * A data property range axiom should be removable without causing a full KB
	 * reload
	 */
	@Test
	public void removeDataPropertyRangeAxiom() {

		createReasoner( 
				declaration( dp ),
				declaration( C ), range( dp, XSD.INTEGER ),
				propertyAssertion( a, dp, constant( "foo" ) ) );


		assertFalse( reasoner.isConsistent() );

		boolean changeApplied = processRemove( range( dp, XSD.INTEGER ) );
		assertTrue( "Unable to remove data property range axiom", changeApplied );

		assertTrue( reasoner.isConsistent() );
	}

	/**
	 * An object property domain axiom should be removable without causing a
	 * full KB reload
	 */
	@Test
	public void removeObjectPropertyDomainAxiom() {
		createReasoner( 
			declaration( p ), declaration( C ), 
			domain( p, C ),
			propertyAssertion( a, p, b ) 
		);

		assertTrue( reasoner.isConsistent() );
		assertTrue( reasoner.isEntailed( classAssertion( a, C ) ) );

		boolean changeApplied = processRemove( domain( p, C ) );
		assertTrue( "Unable to remove object property domain axiom", changeApplied );

		assertTrue( reasoner.isConsistent() );
		assertFalse( reasoner.isEntailed( classAssertion( a, C ) ) );
	}

	/**
	 * An object property domain axiom should be removable without causing a
	 * full KB reload even if it is a class expression
	 */
	@Test
	public void removeObjectPropertyDomainAxiomExpression() {
		createReasoner( 
			declaration( p ), declaration( C ), declaration( D ), 
			domain( p, or(C, D) ),
			propertyAssertion( a, p, b ) 
		);

		assertTrue( reasoner.isConsistent() );
		assertTrue( reasoner.isEntailed( classAssertion( a, or(C, D) ) ) );

		boolean changeApplied = processRemove( domain( p, or(C, D) ) );
		assertTrue( "Unable to remove object property domain axiom", changeApplied );

		assertTrue( reasoner.isConsistent() );
		assertFalse( reasoner.isEntailed( classAssertion( a, or(C, D) ) ) );
	}

	/**
	 * An object property range axiom should be removable without causing a full
	 * KB reload
	 */
	@Test
	public void removeObjectPropertyRangeAxiom() {
		createReasoner( 
			declaration( p ), declaration( C ), 
			range( p, C ),
			propertyAssertion( a, p, b ) 
		);

		assertTrue( reasoner.isConsistent() );
		assertTrue( reasoner.isEntailed( classAssertion( b, C ) ) );

		boolean changeApplied = processRemove( range( p, C ) );
		assertTrue( "Unable to remove object property range axiom", changeApplied );

		assertTrue( reasoner.isConsistent() );
		assertFalse( reasoner.isEntailed( classAssertion( b, C ) ) );
	}

	/**
	 * An object property range axiom should be removable without causing a full
	 * KB reload even if it is a class expression
	 */
	@Test
	public void removeObjectPropertyRangeAxiomExpression() {
		createReasoner( 
			declaration( p ), declaration( C ), declaration( D ), 
			range( p, or(C, D) ),
			propertyAssertion( a, p, b ) 
		);

		assertTrue( reasoner.isConsistent() );
		assertTrue( reasoner.isEntailed( classAssertion( b, or(C, D) ) ) );

		boolean changeApplied = processRemove( range( p, or(C, D) ) );
		assertTrue( "Unable to remove object property range axiom", changeApplied );

		assertTrue( reasoner.isConsistent() );
		assertFalse( reasoner.isEntailed( classAssertion( b, or(C, D) ) ) );
	}

	@Test
	public void removeAndAddObjectPropertyDomainAxiom() {
		createReasoner( 
			declaration( p ), declaration( C ), 
			domain( p, C ),
			propertyAssertion( a, p, b ) 
		);

		assertTrue( reasoner.isConsistent() );
		assertTrue( reasoner.isEntailed( classAssertion( a, C ) ) );

		boolean removeApplied = processRemove( domain( p, C ) );
		assertTrue( "Unable to remove object property domain axiom", removeApplied );

		boolean addApplied = processAdd( domain( p, C ) );
		assertTrue( "Unable to add object property domain axiom", addApplied );
		
		assertTrue( reasoner.isConsistent() );
		assertTrue( reasoner.isEntailed( classAssertion( a, C ) ) );
	}
}
