// Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public
// License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of
// proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package com.clarkparsia.pellet.test.rbox;

import static com.clarkparsia.pellet.utils.TermFactory.TOP;
import static com.clarkparsia.pellet.utils.TermFactory.TOP_LIT;
import static com.clarkparsia.pellet.utils.TermFactory.some;
import static com.clarkparsia.pellet.utils.TermFactory.term;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import aterm.ATermAppl;
import junit.framework.JUnit4TestAdapter;
import org.junit.Test;
import org.mindswap.pellet.test.AbstractKBTests;
import org.mindswap.pellet.test.PelletTestSuite;

public class DisjointPropertyTests extends AbstractKBTests
{
	public static String _base = "file:" + PelletTestSuite.base + "misc/";

	public static junit.framework.Test suite()
	{
		return new JUnit4TestAdapter(DisjointPropertyTests.class);
	}

	@Test
	public void simpleInconcistency()
	{
		individuals(_a, _b, _c);
		objectProperties(_p, _q);

		_kb.addDisjointProperty(_p, _q);

		_kb.addPropertyValue(_p, _a, _b);
		_kb.addPropertyValue(_q, _a, _b);

		assertFalse(_kb.isConsistent());
	}

	@Test
	public void subPropertyInconcistency()
	{
		individuals(_a, _b, _c);
		objectProperties(_p, _q);

		_kb.addDisjointProperty(_p, _q);

		final ATermAppl subP = term("subP");

		_kb.addObjectProperty(subP);
		_kb.addSubProperty(subP, _p);

		_kb.addPropertyValue(subP, _a, _b);
		_kb.addPropertyValue(_q, _a, _b);

		assertFalse(_kb.isConsistent());
	}

	@Test
	public void superPropertyConcistency()
	{
		individuals(_a, _b, _c);
		objectProperties(_p, _q);

		_kb.addDisjointProperty(_p, _q);

		final ATermAppl supP = term("supP");

		_kb.addObjectProperty(supP);
		_kb.addSubProperty(_p, supP);

		_kb.addPropertyValue(supP, _a, _b);
		_kb.addPropertyValue(_q, _b, _b);

		assertTrue(_kb.isConsistent());
	}

	@Test
	public void invPropertyInconcistency()
	{
		individuals(_a, _b, _c);
		objectProperties(_p, _q);

		_kb.addDisjointProperty(_p, _q);

		final ATermAppl invP = term("invP");

		_kb.addObjectProperty(invP);
		_kb.addInverseProperty(invP, _p);

		_kb.addPropertyValue(invP, _b, _a);
		_kb.addPropertyValue(_q, _a, _b);

		assertFalse(_kb.isConsistent());
	}

	@Test
	public void differentFromSubjects()
	{
		individuals(_a, _b, _c);
		objectProperties(_p, _q);

		_kb.addDisjointProperty(_p, _q);

		_kb.addPropertyValue(_p, _a, _c);
		_kb.addPropertyValue(_q, _b, _c);

		assertTrue(_kb.isDifferentFrom(_a, _b));
	}

	@Test
	public void differentFromObjects()
	{
		individuals(_a, _b, _c);
		objectProperties(_p, _q);

		_kb.addDisjointProperty(_p, _q);

		_kb.addPropertyValue(_p, _a, _b);
		_kb.addPropertyValue(_q, _a, _c);

		assertTrue(_kb.isDifferentFrom(_b, _c));
	}

	@Test
	public void test547a()
	{
		objectProperties(_p, _q, _r);

		_kb.addDisjointProperty(_p, _q);
		_kb.addSubClass(some(_p, TOP), some(_q, TOP));

		assertTrue(_kb.isConsistent());
		assertTrue(_kb.isDisjointProperty(_p, _q));
		assertFalse(_kb.isDisjointProperty(_p, _r));
		assertFalse(_kb.isDisjointProperty(_q, _r));
	}

	@Test
	public void test547b()
	{
		dataProperties(_p, _q, _r);

		_kb.addDisjointProperty(_p, _q);
		_kb.addSubClass(some(_p, TOP_LIT), some(_q, TOP_LIT));

		assertTrue(_kb.isConsistent());
		assertTrue(_kb.isDisjointProperty(_p, _q));
		assertFalse(_kb.isDisjointProperty(_p, _r));
		assertFalse(_kb.isDisjointProperty(_q, _r));
	}
}
