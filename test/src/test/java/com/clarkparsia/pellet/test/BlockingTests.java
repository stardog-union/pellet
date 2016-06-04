// Portions Copyright (c) 2006 - 2008, Clark & Parsia, LLC.
// <http://www.clarkparsia.com>
// Clark & Parsia, LLC parts of this source code are available under the terms
// of the Affero General Public License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of
// proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package com.clarkparsia.pellet.test;

import static com.clarkparsia.pellet.utils.TermFactory.BOTTOM;
import static com.clarkparsia.pellet.utils.TermFactory.TOP;
import static com.clarkparsia.pellet.utils.TermFactory.all;
import static com.clarkparsia.pellet.utils.TermFactory.and;
import static com.clarkparsia.pellet.utils.TermFactory.inv;
import static com.clarkparsia.pellet.utils.TermFactory.list;
import static com.clarkparsia.pellet.utils.TermFactory.max;
import static com.clarkparsia.pellet.utils.TermFactory.not;
import static com.clarkparsia.pellet.utils.TermFactory.some;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import com.clarkparsia.pellet.owlapi.OWLAPILoader;
import junit.framework.JUnit4TestAdapter;
import org.junit.Test;
import org.mindswap.pellet.test.AbstractKBTests;
import org.mindswap.pellet.test.MiscTests;

public class BlockingTests extends AbstractKBTests
{
	public static junit.framework.Test suite()
	{
		return new JUnit4TestAdapter(BlockingTests.class);
	}

	@Test
	public void transitivityInverse()
	{
		classes(_C, _D);
		objectProperties(_p, _q, _r);

		_kb.addTransitiveProperty(_r);
		_kb.addSubProperty(_r, _q);
		_kb.addSubClass(_D, all(_q, _C));

		assertTrue(_kb.isConsistent());

		assertFalse(_kb.isSatisfiable(some(_p, and(_D, some(_p, and(some(inv(_r), _D), some(_r, not(_C))))))));
	}

	@Test
	public void propertyChain()
	{
		classes(_C, _D);
		objectProperties(_p, _q, _r, _s);

		_kb.addDisjointClass(_C, _D);
		_kb.addSubProperty(list(_p, inv(_q), _r, _s), _s);
		_kb.addSubClass(_D, all(_s, _C));
		_kb.addSubClass(_D, some(_p, some(inv(_q), some(_r, some(_s, _D)))));

		assertTrue(_kb.isConsistent());

		assertFalse(_kb.isSatisfiable(_D));
	}

	@Test
	public void propertyChainInverse()
	{
		classes(_C, _D);
		objectProperties(_p, _q, _r);

		_kb.addSubProperty(list(_r, _p), _q);
		_kb.addSubClass(_D, all(_q, _C));

		assertTrue(_kb.isConsistent());

		assertFalse(_kb.isSatisfiable(some(_p, and(_D, some(_p, and(some(inv(_r), _D), some(_p, not(_C))))))));
	}

	@Test
	public void propertyChainInverseCardinality()
	{
		classes(_C, _D);
		objectProperties(_p, _q, _r);

		// functionality has no effect, it is used to force
		// double blocking instead of equality blocking
		_kb.addFunctionalProperty(_p);
		_kb.addSubProperty(list(_r, _p), _q);
		_kb.addSubClass(_D, all(_q, _C));

		assertTrue(_kb.isConsistent());

		assertFalse(_kb.isSatisfiable(some(_p, and(_D, some(_p, and(some(inv(_r), _D), some(_p, not(_C))))))));
	}

	@Test
	public void doubleBlockingExample()
	{
		classes(_C, _D);
		objectProperties(_f, _r);

		_kb.addTransitiveProperty(_r);
		_kb.addSubProperty(_f, _r);
		_kb.addEquivalentClass(_D, and(_C, some(_f, not(_C))));
		_kb.addSubClass(TOP, max(_f, 1, TOP));

		assertTrue(_kb.isConsistent());

		assertFalse(_kb.isSatisfiable(and(not(_C), some(inv(_f), _D), all(inv(_r), some(inv(_f), _D)))));
	}

	@Test
	public void complexInconsistent()
	{
		_kb = new OWLAPILoader().createKB(MiscTests.base + "one+one-inconsistent.owl");

		assertFalse(_kb.isConsistent());
	}

	@Test
	public void complexAllUnsat()
	{
		_kb = new OWLAPILoader().createKB(MiscTests.base + "one+one-consistent-but-all-unsat.owl");

		assertTrue(_kb.isConsistent());

		assertEquals(_kb.getClasses(), _kb.getUnsatisfiableClasses());
	}

	@Test
	public void complexAllInfSat()
	{
		_kb = new OWLAPILoader().createKB(MiscTests.base + "one+one-consistent-and-all-inf-sat.owl");

		assertTrue(_kb.isConsistent());

		assertTrue(_kb.getUnsatisfiableClasses().isEmpty());
	}

	@Test
	public void deadlockBlock()
	{
		classes(_C, _D);
		objectProperties(_p, _q, _r);

		_kb.addSubClass(_D, BOTTOM);

		assertTrue(_kb.isConsistent());

		assertFalse(_kb.isSatisfiable(and(some(_p, some(_p, _D)), some(_p, _D))));
	}

	@Test
	public void yoyo()
	{
		classes(_A);
		objectProperties(_r);
		individuals(_a, _b);

		_kb.addFunctionalProperty(_r);
		_kb.addSubClass(_A, all(_r, some(_r, TOP)));
		_kb.addType(_a, _A);
		_kb.addType(_a, some(_r, TOP));
		_kb.addPropertyValue(_r, _a, _a);
		_kb.addPropertyValue(_r, _a, _b);

		assertTrue(_kb.isConsistent());

		assertTrue(_kb.isSatisfiable(_A));
	}

}
