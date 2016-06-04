// Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package com.clarkparsia.pellet.test.rbox;

import static com.clarkparsia.pellet.utils.TermFactory.TOP;
import static com.clarkparsia.pellet.utils.TermFactory.TOP_OBJECT_PROPERTY;
import static com.clarkparsia.pellet.utils.TermFactory.inv;
import static com.clarkparsia.pellet.utils.TermFactory.not;
import static com.clarkparsia.pellet.utils.TermFactory.self;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import junit.framework.JUnit4TestAdapter;
import org.junit.Test;
import org.mindswap.pellet.test.AbstractKBTests;

/**
 * <p>
 * Title:
 * </p>
 * <p>
 * Description:
 * </p>
 * <p>
 * Copyright: Copyright (c) 2009
 * </p>
 * <p>
 * Company: Clark & Parsia, LLC. <http://www.clarkparsia.com>
 * </p>
 *
 * @author Evren Sirin
 */
public class PropertyCharacteristicsTests extends AbstractKBTests
{
	public static junit.framework.Test suite()
	{
		return new JUnit4TestAdapter(PropertyCharacteristicsTests.class);
	}

	@Test
	public void reflexivePropertyCausingMerge1()
	{
		// test #433
		classes(_A, _B);
		individuals(_a, _b);
		objectProperties(_p);

		_kb.addReflexiveProperty(_p);
		_kb.addSymmetricProperty(_p);
		_kb.addFunctionalProperty(_p);

		_kb.addSubClass(_A, _B);

		_kb.addType(_a, _A);
		_kb.addType(_b, _A);

		_kb.addPropertyValue(_p, _a, _b);

		assertTrue(_kb.isConsistent());
		assertTrue(_kb.isSameAs(_a, _b));
	}

	@Test
	public void reflexivePropertyCausingMerge2()
	{
		// test #433
		classes(_A);
		individuals(_a, _b);
		objectProperties(_p);

		_kb.addReflexiveProperty(_p);
		_kb.addSymmetricProperty(_p);
		_kb.addFunctionalProperty(_p);

		_kb.addDomain(TOP_OBJECT_PROPERTY, _A);

		_kb.addPropertyValue(_p, _a, _b);

		assertTrue(_kb.isConsistent());
		assertTrue(_kb.isSameAs(_a, _b));
	}

	@Test
	public void irreflexiveSH()
	{
		// test #433
		objectProperties(_p);

		_kb.addIrreflexiveProperty(inv(_p));

		assertTrue(_kb.isConsistent());
		assertTrue(_kb.isIrreflexiveProperty(_p));
	}

	@Test
	/**
	 * Tests for the bug reported in #376
	 */
	public void test376()
	{
		annotationProperties(_p);

		assertFalse(_kb.isFunctionalProperty(_p));
	}

	@Test
	public void testReflexiveDisjoint()
	{
		classes(_C);
		objectProperties(_p, _q);

		_kb.addReflexiveProperty(_p);
		_kb.addDomain(_q, _C);
		_kb.addRange(_q, not(_C));

		assertTrue(_kb.isConsistent());
		assertFalse(_kb.isDisjointProperty(_p, _q));
	}

	@Test
	public void testAsymmetricEquivalent()
	{
		objectProperties(_q, _r);

		_kb.addAsymmetricProperty(_q);
		_kb.addEquivalentProperty(_q, _r);

		assertTrue(_kb.isConsistent());
		assertTrue(_kb.isAsymmetricProperty(_q));
		assertTrue(_kb.isAsymmetricProperty(_r));
	}

	@Test
	public void testAsymmetricInverseDisjoint()
	{
		objectProperties(_p, _q, _r);

		_kb.addInverseProperty(_p, _q);
		_kb.addAsymmetricProperty(_q);
		_kb.addEquivalentProperty(_q, _r);

		assertTrue(_kb.isConsistent());
		assertTrue(_kb.isDisjointProperty(_p, _q));
		assertTrue(_kb.isDisjointProperty(_p, _r));
	}

	@Test
	public void testReflexiveSubPropertyExplicit()
	{
		objectProperties(_p, _q);

		_kb.addReflexiveProperty(_p);
		_kb.addSubProperty(_p, _q);

		assertTrue(_kb.isConsistent());
		assertTrue(_kb.isReflexiveProperty(_p));
		assertTrue(_kb.isReflexiveProperty(_q));
	}

	@Test
	public void testReflexiveSubPropertyImplicit()
	{
		classes(_C);
		objectProperties(_p, _q);

		_kb.addSubClass(TOP, self(_p));
		_kb.addSubProperty(_p, _q);

		assertTrue(_kb.isConsistent());
		assertTrue(_kb.isReflexiveProperty(_p));
		assertTrue(_kb.isReflexiveProperty(_q));
	}

	@Test
	public void testIrreflexive()
	{
		objectProperties(_p, _q);

		_kb.addIrreflexiveProperty(_p);

		assertTrue(_kb.isConsistent());
		assertTrue(_kb.isIrreflexiveProperty(_p));
	}

	@Test
	public void testIrreflexiveAsymetric()
	{
		objectProperties(_p, _q);

		_kb.addAsymmetricProperty(_p);

		assertTrue(_kb.isConsistent());
		assertTrue(_kb.isIrreflexiveProperty(_p));
		assertFalse(_kb.isReflexiveProperty(_p));
	}

	@Test
	public void testNotIrreflexive()
	{
		objectProperties(_p, _q);

		_kb.addIrreflexiveProperty(_p);
		_kb.addSubProperty(_p, _q);

		assertTrue(_kb.isConsistent());
		assertTrue(_kb.isIrreflexiveProperty(_p));
		assertFalse(_kb.isIrreflexiveProperty(_q));
	}

	@Test
	public void irreflexivePropertyCausingDifferentFrom()
	{
		// test #433
		individuals(_a, _b);
		objectProperties(_p);

		_kb.addIrreflexiveProperty(_p);

		_kb.addPropertyValue(_p, _a, _b);

		assertTrue(_kb.isConsistent());
		assertTrue(_kb.isDifferentFrom(_a, _b));
	}
}
