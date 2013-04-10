// Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public
// License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of
// proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package com.clarkparsia.pellet.test.owlapi;

import static com.clarkparsia.owlapiv3.OWL.classAssertion;
import static com.clarkparsia.pellet.utils.TermFactory.oneOf;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import junit.framework.JUnit4TestAdapter;

import org.junit.Test;
import org.semanticweb.owlapi.model.OWLException;

import com.clarkparsia.owlapiv3.OWL;
import com.clarkparsia.owlapiv3.OntologyUtils;

/**
 * <p>
 * Title:
 * </p>
 * <p>
 * Description:
 * </p>
 * <p>
 * Copyright: Copyright (c) 2008
 * </p>
 * <p>
 * Company: Clark & Parsia, LLC. <http://www.clarkparsia.com>
 * </p>
 * 
 * @author Evren Sirin
 */
public class ABoxUpdateTests extends AbstractOWLAPITests {
	public static junit.framework.Test suite() {
		return new JUnit4TestAdapter( ABoxUpdateTests.class );
	}

	@Test
	public void removeType() throws OWLException {
		createReasoner(
			OWL.classAssertion( a, C ),
			OWL.classAssertion( a, D ) 
		);

		assertTrue( reasoner.isConsistent() );

		assertTrue( reasoner.isEntailed( classAssertion( a, C ) ) );
		assertTrue( reasoner.isEntailed( classAssertion( a, D ) ) );
		
		boolean changeApplied = processRemove( OWL.classAssertion( a, D )  );
		assertTrue( changeApplied );

		assertTrue( reasoner.isEntailed( classAssertion( a, C ) ) );
		assertFalse( reasoner.isEntailed( classAssertion( a, D ) ) );
	}

	@Test
	public void removeTypeFromMergedNode() throws OWLException {
		createReasoner(
			OWL.classAssertion( a, OWL.oneOf( b, c ) ),
			OWL.classAssertion( a, A ),
			OWL.classAssertion( b, B ),
			OWL.classAssertion( c, C ),
			OWL.classAssertion( a, D ) 
		);

		assertTrue( reasoner.isConsistent() );

		assertTrue( reasoner.isEntailed( classAssertion( a, A ) ) );
		assertFalse( reasoner.isEntailed( classAssertion( a, B ) ) );
		assertFalse( reasoner.isEntailed( classAssertion( a, C ) ) );
		assertTrue( reasoner.isEntailed( classAssertion( a, D ) ) );
		
		boolean changeApplied = processRemove( OWL.classAssertion( a, D )  );
		assertTrue( changeApplied );

		assertTrue( reasoner.isEntailed( classAssertion( a, A ) ) );
		assertFalse( reasoner.isEntailed( classAssertion( a, B ) ) );
		assertFalse( reasoner.isEntailed( classAssertion( a, C ) ) );
		assertFalse( reasoner.isEntailed( classAssertion( a, D ) ) );
	}

	@Test
	public void removePropertyValue() throws OWLException {
		createReasoner(
			OWL.propertyAssertion( a, p, b ),
			OWL.propertyAssertion( a, p, c ) 
		);

		assertTrue( reasoner.isConsistent() );

		assertTrue( reasoner.isEntailed( OWL.propertyAssertion( a, p, b ) ) );
		assertTrue( reasoner.isEntailed( OWL.propertyAssertion( a, p, c ) ) );
		
		boolean changeApplied = processRemove( OWL.propertyAssertion( a, p, c ) );
		assertTrue( changeApplied );

		assertTrue( reasoner.isEntailed( OWL.propertyAssertion( a, p, b ) ) );
		assertFalse( reasoner.isEntailed( OWL.propertyAssertion( a, p, c ) ) );
	}
}
