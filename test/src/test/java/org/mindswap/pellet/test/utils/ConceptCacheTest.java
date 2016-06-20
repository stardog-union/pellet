// Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package org.mindswap.pellet.test.utils;

import junit.framework.TestCase;
import openllet.aterm.ATermAppl;
import org.mindswap.pellet.KnowledgeBase;
import org.mindswap.pellet.tableau.cache.CachedNode;
import org.mindswap.pellet.tableau.cache.CachedNodeFactory;
import org.mindswap.pellet.tableau.cache.ConceptCache;
import org.mindswap.pellet.tableau.cache.ConceptCacheLRU;
import org.mindswap.pellet.utils.ATermUtils;

public class ConceptCacheTest extends TestCase
{
	private ConceptCache _cache;

	private final ATermAppl _p1 = ATermUtils.makeTermAppl("p1");
	private final ATermAppl _p2 = ATermUtils.makeTermAppl("p2");
	private final ATermAppl _p3 = ATermUtils.makeNot(_p1);
	private final ATermAppl _p4 = ATermUtils.makeNot(_p2);

	private final ATermAppl _np1 = ATermUtils.makeAnd(_p1, _p2);
	private final ATermAppl _np2 = ATermUtils.makeOr(_p1, _p2);
	private final ATermAppl _np3 = ATermUtils.makeAnd(_p3, _p4);
	private final ATermAppl _np4 = ATermUtils.makeOr(_p3, _p4);

	private final CachedNode _DUMMY = CachedNodeFactory.createSatisfiableNode();

	@Override
	public void setUp()
	{
		_cache = new ConceptCacheLRU(new KnowledgeBase(), 3);
	}

	public void testPut()
	{
		_cache.put(_p1, _DUMMY);
		_cache.put(_p2, _DUMMY);
		_cache.put(_p3, _DUMMY);
		_cache.put(_p4, _DUMMY);
		_cache.put(_np1, _DUMMY);
		_cache.put(_np2, _DUMMY);
		_cache.put(_np3, _DUMMY);

		assertEquals(7, _cache.size());

		_cache.get(_np1);
		_cache.get(_np3);
		_cache.put(_np4, _DUMMY);

		assertEquals(7, _cache.size());
		assertFalse(_cache.containsKey(_np2));
		assertTrue(_cache.containsKey(_np4));

	}
}
