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
import junit.framework.JUnit4TestAdapter;

import org.junit.Test;
import org.mindswap.pellet.test.AbstractKBTests;

import aterm.ATermAppl;

public class CacheSafetyTests extends AbstractKBTests {
	public static junit.framework.Test suite() {
		return new JUnit4TestAdapter(CacheSafetyTests.class);
	}

	@Test
	public void somePallInvP() {
		classes(C, D);
		objectProperties(p);

		kb.addSubClass(D, some(p, C));
		kb.addSubClass(C, all(inv(p), not(D)));

		assertTrue(kb.isConsistent());
		assertTrue(kb.isSatisfiable(C));
		assertFalse(kb.isSatisfiable(D));
	}

	@Test
	public void someSubPallInvP() {
		classes(C, D);
		objectProperties(p, q);

		kb.addSubProperty(q, p);

		kb.addSubClass(D, some(q, C));
		kb.addSubClass(C, all(inv(p), not(D)));

		assertTrue(kb.isConsistent());
		assertTrue(kb.isSatisfiable(C));
		assertFalse(kb.isSatisfiable(D));
	}

	@Test
	public void somePallInvPwithReflexivity() {
		classes(C, D);
		objectProperties(p, r);

		kb.addReflexiveProperty(r);

		kb.addSubClass(D, some(p, C));
		kb.addSubClass(C, all(inv(p), not(D)));

		assertTrue(kb.isConsistent());
		assertTrue(kb.isSatisfiable(C));
		assertFalse(kb.isSatisfiable(D));
	}

	@Test
	public void somePallInvPSubClass() {
		classes(B, C, D, E);
		objectProperties(p);

		kb.addSubClass(D, E);
		kb.addEquivalentClass(E, some(p, C));
		kb.addSubClass(C, B);
		kb.addSubClass(B, all(inv(p), not(D)));

		assertTrue(kb.isConsistent());
		assertTrue(kb.isSatisfiable(C));
		assertFalse(kb.isSatisfiable(D));
	}

	@Test
	public void somePallInvSubP() {
		classes(C, D);
		objectProperties(p, q);

		kb.addSubProperty(p, q);

		kb.addSubClass(D, some(p, C));
		kb.addSubClass(C, all(inv(q), not(D)));

		assertTrue(kb.isConsistent());
		assertTrue(kb.isSatisfiable(C));
		assertFalse(kb.isSatisfiable(D));
	}

	@Test
	public void someFunctionalP() {
		classes(C, D);
		objectProperties(p);

		kb.addFunctionalProperty(p);
		kb.addSubClass(D, some(inv(p), C));
		kb.addSubClass(C, some(p, not(D)));

		assertTrue(kb.isConsistent());
		assertTrue(kb.isSatisfiable(C));
		assertFalse(kb.isSatisfiable(D));
	}

	@Test
	public void functionalInv() {
		classes(C, D);
		objectProperties(f);

		kb.addFunctionalProperty(f);

		kb.addSubClass(D, some(inv(f), C));
		kb.addSubClass(C, some(f, not(D)));

		assertTrue(kb.isConsistent());
		assertTrue(kb.isSatisfiable(C));
		assertFalse(kb.isSatisfiable(D));
	}

	@Test
	public void max1Inv() {
		classes(C, D);
		objectProperties(f);

		kb.addSubClass(D, some(inv(f), C));
		kb.addSubClass(C, and(some(f, not(D)), max(f, 1, TOP)));

		assertTrue(kb.isConsistent());
		assertTrue(kb.isSatisfiable(C));
		assertFalse(kb.isSatisfiable(D));
	}

	@Test
	public void functionalInvTrans() {
		ATermAppl invF = term("invF");
		ATermAppl invR = term("invR");

		classes(C, D);
		objectProperties(r, f, invF, invR);

		kb.addFunctionalProperty(f);
		kb.addInverseFunctionalProperty(f);
		kb.addInverseProperty(f, invF);

		kb.addTransitiveProperty(r);
		kb.addInverseProperty(r, invR);

		kb.addSubProperty(f, r);

		kb.addEquivalentClass(D, and(C, some(f, not(C))));

		assertTrue(kb.isConsistent());
		assertTrue(kb.isSatisfiable(C));
		assertTrue(kb.isSatisfiable(not(C)));
		assertTrue(kb.isSatisfiable(D));
		assertTrue(kb.isSatisfiable(not(D)));
		assertFalse(kb.isSatisfiable(and(not(C), some(invF, D), all(invR, some(invF, D)))));
	}

	@Test
	public void maxCardinalityInvTrans() {
		ATermAppl invF = term("invF");
		ATermAppl invR = term("invR");

		classes(C, D);
		objectProperties(r, f, invF, invR);

		kb.addSubClass(TOP, max(f, 1, TOP));
		// kb.addSubClass( TOP, max( inv( f ), 1, TOP ) );
		kb.addInverseProperty(f, invF);

		kb.addTransitiveProperty(r);
		kb.addInverseProperty(r, invR);

		kb.addSubProperty(f, r);

		kb.addEquivalentClass(D, and(C, some(f, not(C))));

		assertTrue(kb.isConsistent());
		assertFalse(kb.isSatisfiable(and(not(C), some(invF, D), all(invR, some(invF, D)))));
	}

	@Test
	public void maxCardinalitySub() {
		classes(C, D);
		objectProperties(p, r, f);

		kb.addSubClass(TOP, max(f, 1, TOP));
		kb.addSubProperty(p, f);
		kb.addSubProperty(r, f);

		assertTrue(kb.isConsistent());
		assertTrue(kb.isSatisfiable(C));
		assertTrue(kb.isSatisfiable(all(inv(r), not(C))));
		assertFalse(kb.isSatisfiable(and(C, some(r, TOP), some(p, all(inv(r), not(C))))));
	}

	@Test
	public void functionalSubTrans() {
		classes(A);
		objectProperties(r, f);

		kb.addFunctionalProperty(f);
		kb.addTransitiveProperty(r);
		kb.addSubProperty(f, r);

		kb.addEquivalentClass(D, and(C, some(f, not(C))));

		assertTrue(kb.isConsistent());
		assertTrue(kb.isSatisfiable(and(not(A), some(inv(f), A), all(inv(r), some(inv(f), A)))));
	}

	@Test
	public void maxCardinalitySubTrans() {
		classes(A);
		objectProperties(r, f);

		kb.addSubClass(TOP, max(f, 1, TOP));
		kb.addTransitiveProperty(r);
		kb.addSubProperty(f, r);

		kb.addEquivalentClass(D, and(C, some(f, not(C))));

		assertTrue(kb.isConsistent());
		assertTrue(kb.isSatisfiable(and(not(A), some(inv(f), A), all(inv(r), some(inv(f), A)))));
	}

	@Test
	public void somePQallInvR() {
		classes(C, D);
		objectProperties(p, q, r);

		kb.addSubProperty(list(p, q), r);
		kb.addSubClass(D, some(p, some(q, C)));
		kb.addSubClass(C, all(inv(r), not(D)));

		assertTrue(kb.isConsistent());
		assertTrue(kb.isSatisfiable(C));
		assertFalse(kb.isSatisfiable(D));
	}

	@Test
	public void somePQallAnonInvR1() {
		classes(C);
		objectProperties(p, q, r);

		kb.addSubProperty(list(p, q), r);

		assertTrue(kb.isConsistent());
		assertTrue(kb.isSatisfiable(C));
		assertTrue(kb.isSatisfiable(all(inv(r), not(C))));
		assertFalse(kb.isSatisfiable(and(C, some(p, some(q, all(inv(r), not(C)))))));
	}

	@Test
	public void somePQallAnonInvR2() {
		classes(C);
		objectProperties(p, q, r);

		kb.addSubProperty(list(p, q), r);

		assertTrue(kb.isConsistent());
		assertTrue(kb.isSatisfiable(C));
		assertTrue(kb.isSatisfiable(some(q, all(inv(r), not(C)))));
		assertFalse(kb.isSatisfiable(and(C, some(p, some(q, all(inv(r), not(C)))))));
	}

	@Test
	public void nestedPropertyChains() {
		classes(C);

		objectProperties(p, q, r, f);

		kb.addSubProperty(list(p, q), r);
		kb.addSubProperty(list(r, q), f);

		assertTrue(kb.isConsistent());
		assertTrue(kb.isSatisfiable(C));
		assertTrue(kb.isSatisfiable(all(inv(f), not(C))));
		assertFalse(kb.isSatisfiable(and(C, some(p, some(q, some(q, all(inv(f), not(C))))))));
	}

	@Test
	public void cachedIntersectionUnsat() {
		classes(B, C, D);
		objectProperties(p);

		kb.addDisjointClass(C, D);
		kb.addSubClass(B, some(p, some(inv(p), and(C, D))));

		assertTrue(kb.isConsistent());
		assertTrue(kb.isSatisfiable(C));
		assertTrue(kb.isSatisfiable(D));
		assertFalse(kb.isSatisfiable(B));
	}

	@Test
	public void cachedIntersectionWithTop1() {
		classes(B, C, D);
		objectProperties(p);

		kb.addEquivalentClass(C, TOP);
		kb.addSubClass(B, some(p, and(C, D)));

		assertTrue(kb.isConsistent());
		assertFalse(kb.isSatisfiable(not(C)));
		assertTrue(kb.isSatisfiable(D));
		assertTrue(kb.isSatisfiable(B));
	}

	@Test
	public void cachedIntersectionWithTop2() {
		classes(B, C, D);
		objectProperties(p);

		kb.addEquivalentClass(C, TOP);
		kb.addEquivalentClass(D, TOP);
		kb.addSubClass(B, some(p, and(C, D)));

		assertTrue(kb.isConsistent());
		assertFalse(kb.isSatisfiable(not(C)));
		assertFalse(kb.isSatisfiable(not(D)));
		assertTrue(kb.isSatisfiable(B));
	}

	@Test
	public void cachedIntersectionWithTop3() {
		classes(B, C, D, E);
		objectProperties(p);

		kb.addEquivalentClass(C, TOP);
		kb.addEquivalentClass(D, TOP);
		kb.addSubClass(B, some(p, and(C, D, E)));

		assertTrue(kb.isConsistent());
		assertFalse(kb.isSatisfiable(not(C)));
		assertFalse(kb.isSatisfiable(not(D)));		
		assertTrue(kb.isSatisfiable(B));
		assertTrue(kb.isSatisfiable(E));
		assertTrue(kb.isSatisfiable(B));
	}
}
