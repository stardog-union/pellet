// Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package com.clarkparsia.pellet.test.classification;

import static com.clarkparsia.pellet.utils.TermFactory.all;
import static com.clarkparsia.pellet.utils.TermFactory.inv;
import static com.clarkparsia.pellet.utils.TermFactory.list;
import static com.clarkparsia.pellet.utils.TermFactory.some;
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
public class SimpleClassificationTests extends AbstractKBTests
{
	public static junit.framework.Test suite()
	{
		return new JUnit4TestAdapter(SimpleClassificationTests.class);
	}

	@Test
	public void cdClassificationWithInverses()
	{
		classes(_C, _D, _E);
		objectProperties(_p);

		_kb.addSubClass(_C, some(_p, _D));
		_kb.addSubClass(_D, all(inv(_p), _E));

		assertTrue(_kb.isConsistent());
		assertTrue(_kb.isSubClassOf(_C, _E));

		_kb.classify();

		assertTrue(_kb.isSubClassOf(_C, _E));
	}

	@Test
	public void cdClassificationWithCyclicInverses()
	{
		classes(_C, _D, _E);
		objectProperties(_p, _q);

		_kb.addSubClass(_E, some(_p, _C));
		_kb.addSubClass(_C, all(inv(_p), _D));
		_kb.addSubClass(_D, some(_q, _E));

		assertTrue(_kb.isConsistent());
		assertTrue(_kb.isSubClassOf(_E, _D));

		_kb.classify();

		assertTrue(_kb.isSubClassOf(_E, _D));
	}

	@Test
	public void cdClassificationWithPropChain()
	{
		classes(_C, _D, _E);
		objectProperties(_p, _q, _r);

		_kb.addSubProperty(list(_p, _q), _r);
		_kb.addSubClass(_C, some(_p, some(_q, _D)));
		_kb.addSubClass(_D, all(inv(_r), _E));

		assertTrue(_kb.isConsistent());
		assertTrue(_kb.isSubClassOf(_C, _E));

		_kb.classify();

		_kb.printClassTree();

		assertTrue(_kb.isSubClassOf(_C, _E));
	}
}
