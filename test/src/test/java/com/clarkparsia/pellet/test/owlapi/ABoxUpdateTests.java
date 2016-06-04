// Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public
// License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of
// proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package com.clarkparsia.pellet.test.owlapi;

import static com.clarkparsia.owlapi.OWL.classAssertion;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import com.clarkparsia.owlapi.OWL;
import junit.framework.JUnit4TestAdapter;
import org.junit.Test;

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
public class ABoxUpdateTests extends AbstractOWLAPITests
{
	public static junit.framework.Test suite()
	{
		return new JUnit4TestAdapter(ABoxUpdateTests.class);
	}

	@Test
	public void removeType()
	{
		createReasoner(OWL.classAssertion(_a, _C), OWL.classAssertion(_a, _D));

		assertTrue(_reasoner.isConsistent());

		assertTrue(_reasoner.isEntailed(classAssertion(_a, _C)));
		assertTrue(_reasoner.isEntailed(classAssertion(_a, _D)));

		final boolean changeApplied = processRemove(OWL.classAssertion(_a, _D));
		assertTrue(changeApplied);

		assertTrue(_reasoner.isEntailed(classAssertion(_a, _C)));
		assertFalse(_reasoner.isEntailed(classAssertion(_a, _D)));
	}

	@Test
	public void removeTypeFromMergedNode()
	{
		createReasoner(OWL.classAssertion(_a, OWL.oneOf(_b, _c)), OWL.classAssertion(_a, _A), OWL.classAssertion(_b, _B), OWL.classAssertion(_c, _C), OWL.classAssertion(_a, _D));

		assertTrue(_reasoner.isConsistent());

		assertTrue(_reasoner.isEntailed(classAssertion(_a, _A)));
		assertFalse(_reasoner.isEntailed(classAssertion(_a, _B)));
		assertFalse(_reasoner.isEntailed(classAssertion(_a, _C)));
		assertTrue(_reasoner.isEntailed(classAssertion(_a, _D)));

		final boolean changeApplied = processRemove(OWL.classAssertion(_a, _D));
		assertTrue(changeApplied);

		assertTrue(_reasoner.isEntailed(classAssertion(_a, _A)));
		assertFalse(_reasoner.isEntailed(classAssertion(_a, _B)));
		assertFalse(_reasoner.isEntailed(classAssertion(_a, _C)));
		assertFalse(_reasoner.isEntailed(classAssertion(_a, _D)));
	}

	@Test
	public void removePropertyValue()
	{
		createReasoner(OWL.propertyAssertion(_a, _p, _b), OWL.propertyAssertion(_a, _p, _c));

		assertTrue(_reasoner.isConsistent());

		assertTrue(_reasoner.isEntailed(OWL.propertyAssertion(_a, _p, _b)));
		assertTrue(_reasoner.isEntailed(OWL.propertyAssertion(_a, _p, _c)));

		final boolean changeApplied = processRemove(OWL.propertyAssertion(_a, _p, _c));
		assertTrue(changeApplied);

		assertTrue(_reasoner.isEntailed(OWL.propertyAssertion(_a, _p, _b)));
		assertFalse(_reasoner.isEntailed(OWL.propertyAssertion(_a, _p, _c)));
	}
}
