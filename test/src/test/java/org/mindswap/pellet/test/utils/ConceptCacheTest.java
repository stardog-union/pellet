// Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package org.mindswap.pellet.test.utils;

import aterm.ATermAppl;
import junit.framework.TestCase;
import org.mindswap.pellet.KnowledgeBase;
import org.mindswap.pellet.tableau.cache.CachedNode;
import org.mindswap.pellet.tableau.cache.CachedNodeFactory;
import org.mindswap.pellet.tableau.cache.ConceptCache;
import org.mindswap.pellet.tableau.cache.ConceptCacheLRU;
import org.mindswap.pellet.utils.ATermUtils;

public class ConceptCacheTest extends TestCase
{
	private ConceptCache cache;

	private final ATermAppl p1 = ATermUtils.makeTermAppl("p1");
	private final ATermAppl p2 = ATermUtils.makeTermAppl("p2");
	private final ATermAppl p3 = ATermUtils.makeNot(p1);
	private final ATermAppl p4 = ATermUtils.makeNot(p2);

	private final ATermAppl np1 = ATermUtils.makeAnd(p1, p2);
	private final ATermAppl np2 = ATermUtils.makeOr(p1, p2);
	private final ATermAppl np3 = ATermUtils.makeAnd(p3, p4);
	private final ATermAppl np4 = ATermUtils.makeOr(p3, p4);

	private final CachedNode DUMMY = CachedNodeFactory.createSatisfiableNode();

	@Override
	public void setUp()
	{
		cache = new ConceptCacheLRU(new KnowledgeBase(), 3);
	}

	public void testPut()
	{
		cache.put(p1, DUMMY);
		cache.put(p2, DUMMY);
		cache.put(p3, DUMMY);
		cache.put(p4, DUMMY);
		cache.put(np1, DUMMY);
		cache.put(np2, DUMMY);
		cache.put(np3, DUMMY);

		assertEquals(7, cache.size());

		cache.get(np1);
		cache.get(np3);
		cache.put(np4, DUMMY);

		assertEquals(7, cache.size());
		assertFalse(cache.containsKey(np2));
		assertTrue(cache.containsKey(np4));

	}
}
