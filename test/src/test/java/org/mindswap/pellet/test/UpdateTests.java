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

import aterm.ATermAppl;
import junit.framework.JUnit4TestAdapter;
import org.junit.Test;

public class UpdateTests extends AbstractKBTests
{
	public static String _base = "file:" + PelletTestSuite.base + "misc/";

	public static junit.framework.Test suite()
	{
		return new JUnit4TestAdapter(UpdateTests.class);
	}

	/**
	 * Test case for #404
	 */
	@Test
	public void addPropertyValueAfterConsistency()
	{
		objectProperties(_p);
		individuals(_a, _c, _d);

		// either p(a, c) or p(a, d) holds
		_kb.addType(_a, or(hasValue(_p, _c), hasValue(_p, _d)));

		assertTrue(_kb.isConsistent());

		// check which non-deterministic choice was made
		final ATermAppl succ = _kb.getABox().getIndividual(_a).getOutEdges().edgeAt(0).getToName();
		final ATermAppl nonSucc = succ.equals(_c) ? _d : _c;

		// no entailment can be made yet
		assertFalse(_kb.hasPropertyValue(_a, _p, nonSucc));
		assertFalse(_kb.hasPropertyValue(_a, _p, succ));

		// assert the property value in non-deterministic choice
		_kb.addPropertyValue(_p, _a, succ);

		assertTrue(_kb.isConsistent());

		// this entailment still does not hold
		assertFalse(_kb.hasPropertyValue(_a, _p, nonSucc));
		// this entailment should now hold
		assertTrue(_kb.hasPropertyValue(_a, _p, succ));
	}

	/**
	 * Test case for #399
	 */
	@Test
	public void addTypeValueWithNonDeterministic()
	{
		classes(_C, _D);
		individuals(_a);

		// either C(a) or D(a) holds
		_kb.addType(_a, or(_C, _D));

		assertTrue(_kb.isConsistent());

		// check which non-deterministic choice was made
		final ATermAppl type = _kb.getABox().getIndividual(_a).hasType(_C) ? _C : _D;
		final ATermAppl nonType = type.equals(_C) ? _D : _C;

		// no entailment can be made yet
		assertFalse(_kb.isType(_a, nonType));
		assertFalse(_kb.isType(_a, type));

		// assert the type in non-deterministic choice
		_kb.addType(_a, type);

		assertTrue(_kb.isConsistent());

		// this entailment still does not hold
		assertFalse(_kb.isType(_a, nonType));
		// this entailment should now hold
		assertTrue(_kb.isType(_a, type));
	}
}
