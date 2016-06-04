// Portions Copyright (c) 2006 - 2008, Clark & Parsia, LLC.
// <http://www.clarkparsia.com>
// Clark & Parsia, LLC parts of this source code are available under the terms
// of the Affero General Public License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of
// proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package com.clarkparsia.pellet.test;

import static com.clarkparsia.pellet.utils.TermFactory.TOP;
import static com.clarkparsia.pellet.utils.TermFactory.all;
import static com.clarkparsia.pellet.utils.TermFactory.and;
import static com.clarkparsia.pellet.utils.TermFactory.inv;
import static com.clarkparsia.pellet.utils.TermFactory.list;
import static com.clarkparsia.pellet.utils.TermFactory.max;
import static com.clarkparsia.pellet.utils.TermFactory.not;
import static com.clarkparsia.pellet.utils.TermFactory.some;
import static com.clarkparsia.pellet.utils.TermFactory.term;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import aterm.ATermAppl;
import junit.framework.JUnit4TestAdapter;
import org.junit.Test;
import org.mindswap.pellet.test.AbstractKBTests;

public class CacheSafetyTests extends AbstractKBTests
{
	public static junit.framework.Test suite()
	{
		return new JUnit4TestAdapter(CacheSafetyTests.class);
	}

	@Test
	public void somePallInvP()
	{
		classes(_C, _D);
		objectProperties(_p);

		_kb.addSubClass(_D, some(_p, _C));
		_kb.addSubClass(_C, all(inv(_p), not(_D)));

		assertTrue(_kb.isConsistent());
		assertTrue(_kb.isSatisfiable(_C));
		assertFalse(_kb.isSatisfiable(_D));
	}

	@Test
	public void someSubPallInvP()
	{
		classes(_C, _D);
		objectProperties(_p, _q);

		_kb.addSubProperty(_q, _p);

		_kb.addSubClass(_D, some(_q, _C));
		_kb.addSubClass(_C, all(inv(_p), not(_D)));

		assertTrue(_kb.isConsistent());
		assertTrue(_kb.isSatisfiable(_C));
		assertFalse(_kb.isSatisfiable(_D));
	}

	@Test
	public void somePallInvPwithReflexivity()
	{
		classes(_C, _D);
		objectProperties(_p, _r);

		_kb.addReflexiveProperty(_r);

		_kb.addSubClass(_D, some(_p, _C));
		_kb.addSubClass(_C, all(inv(_p), not(_D)));

		assertTrue(_kb.isConsistent());
		assertTrue(_kb.isSatisfiable(_C));
		assertFalse(_kb.isSatisfiable(_D));
	}

	@Test
	public void somePallInvPSubClass()
	{
		classes(_B, _C, _D, _E);
		objectProperties(_p);

		_kb.addSubClass(_D, _E);
		_kb.addEquivalentClass(_E, some(_p, _C));
		_kb.addSubClass(_C, _B);
		_kb.addSubClass(_B, all(inv(_p), not(_D)));

		assertTrue(_kb.isConsistent());
		assertTrue(_kb.isSatisfiable(_C));
		assertFalse(_kb.isSatisfiable(_D));
	}

	@Test
	public void somePallInvSubP()
	{
		classes(_C, _D);
		objectProperties(_p, _q);

		_kb.addSubProperty(_p, _q);

		_kb.addSubClass(_D, some(_p, _C));
		_kb.addSubClass(_C, all(inv(_q), not(_D)));

		assertTrue(_kb.isConsistent());
		assertTrue(_kb.isSatisfiable(_C));
		assertFalse(_kb.isSatisfiable(_D));
	}

	@Test
	public void someFunctionalP()
	{
		classes(_C, _D);
		objectProperties(_p);

		_kb.addFunctionalProperty(_p);
		_kb.addSubClass(_D, some(inv(_p), _C));
		_kb.addSubClass(_C, some(_p, not(_D)));

		assertTrue(_kb.isConsistent());
		assertTrue(_kb.isSatisfiable(_C));
		assertFalse(_kb.isSatisfiable(_D));
	}

	@Test
	public void functionalInv()
	{
		classes(_C, _D);
		objectProperties(_f);

		_kb.addFunctionalProperty(_f);

		_kb.addSubClass(_D, some(inv(_f), _C));
		_kb.addSubClass(_C, some(_f, not(_D)));

		assertTrue(_kb.isConsistent());
		assertTrue(_kb.isSatisfiable(_C));
		assertFalse(_kb.isSatisfiable(_D));
	}

	@Test
	public void max1Inv()
	{
		classes(_C, _D);
		objectProperties(_f);

		_kb.addSubClass(_D, some(inv(_f), _C));
		_kb.addSubClass(_C, and(some(_f, not(_D)), max(_f, 1, TOP)));

		assertTrue(_kb.isConsistent());
		assertTrue(_kb.isSatisfiable(_C));
		assertFalse(_kb.isSatisfiable(_D));
	}

	@Test
	public void functionalInvTrans()
	{
		final ATermAppl invF = term("invF");
		final ATermAppl invR = term("invR");

		classes(_C, _D);
		objectProperties(_r, _f, invF, invR);

		_kb.addFunctionalProperty(_f);
		_kb.addInverseFunctionalProperty(_f);
		_kb.addInverseProperty(_f, invF);

		_kb.addTransitiveProperty(_r);
		_kb.addInverseProperty(_r, invR);

		_kb.addSubProperty(_f, _r);

		_kb.addEquivalentClass(_D, and(_C, some(_f, not(_C))));

		assertTrue(_kb.isConsistent());
		assertTrue(_kb.isSatisfiable(_C));
		assertTrue(_kb.isSatisfiable(not(_C)));
		assertTrue(_kb.isSatisfiable(_D));
		assertTrue(_kb.isSatisfiable(not(_D)));
		assertFalse(_kb.isSatisfiable(and(not(_C), some(invF, _D), all(invR, some(invF, _D)))));
	}

	@Test
	public void maxCardinalityInvTrans()
	{
		final ATermAppl invF = term("invF");
		final ATermAppl invR = term("invR");

		classes(_C, _D);
		objectProperties(_r, _f, invF, invR);

		_kb.addSubClass(TOP, max(_f, 1, TOP));
		// _kb.addSubClass( TOP, max( inv( f ), 1, TOP ) );
		_kb.addInverseProperty(_f, invF);

		_kb.addTransitiveProperty(_r);
		_kb.addInverseProperty(_r, invR);

		_kb.addSubProperty(_f, _r);

		_kb.addEquivalentClass(_D, and(_C, some(_f, not(_C))));

		assertTrue(_kb.isConsistent());
		assertFalse(_kb.isSatisfiable(and(not(_C), some(invF, _D), all(invR, some(invF, _D)))));
	}

	@Test
	public void maxCardinalitySub()
	{
		classes(_C, _D);
		objectProperties(_p, _r, _f);

		_kb.addSubClass(TOP, max(_f, 1, TOP));
		_kb.addSubProperty(_p, _f);
		_kb.addSubProperty(_r, _f);

		assertTrue(_kb.isConsistent());
		assertTrue(_kb.isSatisfiable(_C));
		assertTrue(_kb.isSatisfiable(all(inv(_r), not(_C))));
		assertFalse(_kb.isSatisfiable(and(_C, some(_r, TOP), some(_p, all(inv(_r), not(_C))))));
	}

	@Test
	public void functionalSubTrans()
	{
		classes(_A);
		objectProperties(_r, _f);

		_kb.addFunctionalProperty(_f);
		_kb.addTransitiveProperty(_r);
		_kb.addSubProperty(_f, _r);

		_kb.addEquivalentClass(_D, and(_C, some(_f, not(_C))));

		assertTrue(_kb.isConsistent());
		assertTrue(_kb.isSatisfiable(and(not(_A), some(inv(_f), _A), all(inv(_r), some(inv(_f), _A)))));
	}

	@Test
	public void maxCardinalitySubTrans()
	{
		classes(_A);
		objectProperties(_r, _f);

		_kb.addSubClass(TOP, max(_f, 1, TOP));
		_kb.addTransitiveProperty(_r);
		_kb.addSubProperty(_f, _r);

		_kb.addEquivalentClass(_D, and(_C, some(_f, not(_C))));

		assertTrue(_kb.isConsistent());
		assertTrue(_kb.isSatisfiable(and(not(_A), some(inv(_f), _A), all(inv(_r), some(inv(_f), _A)))));
	}

	@Test
	public void somePQallInvR()
	{
		classes(_C, _D);
		objectProperties(_p, _q, _r);

		_kb.addSubProperty(list(_p, _q), _r);
		_kb.addSubClass(_D, some(_p, some(_q, _C)));
		_kb.addSubClass(_C, all(inv(_r), not(_D)));

		assertTrue(_kb.isConsistent());
		assertTrue(_kb.isSatisfiable(_C));
		assertFalse(_kb.isSatisfiable(_D));
	}

	@Test
	public void somePQallAnonInvR1()
	{
		classes(_C);
		objectProperties(_p, _q, _r);

		_kb.addSubProperty(list(_p, _q), _r);

		assertTrue(_kb.isConsistent());
		assertTrue(_kb.isSatisfiable(_C));
		assertTrue(_kb.isSatisfiable(all(inv(_r), not(_C))));
		assertFalse(_kb.isSatisfiable(and(_C, some(_p, some(_q, all(inv(_r), not(_C)))))));
	}

	@Test
	public void somePQallAnonInvR2()
	{
		classes(_C);
		objectProperties(_p, _q, _r);

		_kb.addSubProperty(list(_p, _q), _r);

		assertTrue(_kb.isConsistent());
		assertTrue(_kb.isSatisfiable(_C));
		assertTrue(_kb.isSatisfiable(some(_q, all(inv(_r), not(_C)))));
		assertFalse(_kb.isSatisfiable(and(_C, some(_p, some(_q, all(inv(_r), not(_C)))))));
	}

	@Test
	public void nestedPropertyChains()
	{
		classes(_C);

		objectProperties(_p, _q, _r, _f);

		_kb.addSubProperty(list(_p, _q), _r);
		_kb.addSubProperty(list(_r, _q), _f);

		assertTrue(_kb.isConsistent());
		assertTrue(_kb.isSatisfiable(_C));
		assertTrue(_kb.isSatisfiable(all(inv(_f), not(_C))));
		assertFalse(_kb.isSatisfiable(and(_C, some(_p, some(_q, some(_q, all(inv(_f), not(_C))))))));
	}

	@Test
	public void cachedIntersectionUnsat()
	{
		classes(_B, _C, _D);
		objectProperties(_p);

		_kb.addDisjointClass(_C, _D);
		_kb.addSubClass(_B, some(_p, some(inv(_p), and(_C, _D))));

		assertTrue(_kb.isConsistent());
		assertTrue(_kb.isSatisfiable(_C));
		assertTrue(_kb.isSatisfiable(_D));
		assertFalse(_kb.isSatisfiable(_B));
	}

	@Test
	public void cachedIntersectionWithTop1()
	{
		classes(_B, _C, _D);
		objectProperties(_p);

		_kb.addEquivalentClass(_C, TOP);
		_kb.addSubClass(_B, some(_p, and(_C, _D)));

		assertTrue(_kb.isConsistent());
		assertFalse(_kb.isSatisfiable(not(_C)));
		assertTrue(_kb.isSatisfiable(_D));
		assertTrue(_kb.isSatisfiable(_B));
	}

	@Test
	public void cachedIntersectionWithTop2()
	{
		classes(_B, _C, _D);
		objectProperties(_p);

		_kb.addEquivalentClass(_C, TOP);
		_kb.addEquivalentClass(_D, TOP);
		_kb.addSubClass(_B, some(_p, and(_C, _D)));

		assertTrue(_kb.isConsistent());
		assertFalse(_kb.isSatisfiable(not(_C)));
		assertFalse(_kb.isSatisfiable(not(_D)));
		assertTrue(_kb.isSatisfiable(_B));
	}

	@Test
	public void cachedIntersectionWithTop3()
	{
		classes(_B, _C, _D, _E);
		objectProperties(_p);

		_kb.addEquivalentClass(_C, TOP);
		_kb.addEquivalentClass(_D, TOP);
		_kb.addSubClass(_B, some(_p, and(_C, _D, _E)));

		assertTrue(_kb.isConsistent());
		assertFalse(_kb.isSatisfiable(not(_C)));
		assertFalse(_kb.isSatisfiable(not(_D)));
		assertTrue(_kb.isSatisfiable(_B));
		assertTrue(_kb.isSatisfiable(_E));
		assertTrue(_kb.isSatisfiable(_B));
	}
}
