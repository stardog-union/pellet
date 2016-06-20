// Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package org.mindswap.pellet.tableau.cache;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import openllet.aterm.ATermAppl;
import org.mindswap.pellet.KnowledgeBase;
import org.mindswap.pellet.utils.ATermUtils;

/**
 * <p>
 * Title:
 * </p>
 * <p>
 * Description: LRU implementation of ConceptCache. Primitive concepts and their negation are always kept in the _cache. The least recently used complex concept
 * will be removed from the _cache if the max size is reached.
 * </p>
 * <p>
 * Copyright: Copyright (c) 2007
 * </p>
 * <p>
 * Company: Clark & Parsia, LLC. <http://www.clarkparsia.com>
 * </p>
 *
 * @author Ron Alford
 */
public class ConceptCacheLRU extends AbstractConceptCache
{
	private Map<ATermAppl, CachedNode> primitive;
	private LinkedHashMap<ATermAppl, CachedNode> nonPrimitive;

	private CacheSafety cacheSafety;

	/**
	 * Creates an empty ConceptCacheImpl with no size restrictions
	 *
	 * @param maxSize
	 */
	public ConceptCacheLRU(final KnowledgeBase kb)
	{
		this(kb, Integer.MAX_VALUE);
	}

	/**
	 * Creates an empty _cache with at most <code>maxSize</code> elements which are neither named or negations of names.
	 *
	 * @param maxSize
	 */
	public ConceptCacheLRU(final KnowledgeBase kb, final int maxSize)
	{
		super(maxSize);

		cacheSafety = CacheSafetyFactory.createCacheSafety(kb.getExpressivity());

		primitive = new HashMap<>();
		nonPrimitive = new LinkedHashMap<ATermAppl, CachedNode>(16, 0.75f, true)
		{
			/**
			 * TODO
			 *
			 * @since
			 */
			private static final long serialVersionUID = 3701638684292370398L;

			@Override
			protected boolean removeEldestEntry(final Map.Entry<ATermAppl, CachedNode> eldest)
			{
				return nonPrimitive.size() > getMaxSize();
			}
		};
	}

	@Override
	public CacheSafety getSafety()
	{
		return cacheSafety;
	}

	@Override
	public void clear()
	{
		primitive.clear();
		nonPrimitive.clear();
	}

	@Override
	public boolean containsKey(final Object key)
	{
		if (primitive.containsKey(key) || nonPrimitive.containsKey(key))
			return true;
		return false;
	}

	@Override
	public boolean containsValue(final Object value)
	{
		if (primitive.containsValue(value) || nonPrimitive.containsValue(value))
			return true;
		return false;
	}

	@Override
	public Set<java.util.Map.Entry<ATermAppl, CachedNode>> entrySet()
	{
		Set<java.util.Map.Entry<ATermAppl, CachedNode>> returnSet;
		returnSet = new HashSet<>(primitive.entrySet());
		returnSet.addAll(nonPrimitive.entrySet());
		return returnSet;
	}

	@Override
	public CachedNode get(final Object key)
	{
		if (primitive.containsKey(key))
			return primitive.get(key);
		return nonPrimitive.get(key);
	}

	@Override
	public boolean isEmpty()
	{
		return primitive.isEmpty() && nonPrimitive.isEmpty();
	}

	@Override
	public Set<ATermAppl> keySet()
	{
		final Set<ATermAppl> keys = new HashSet<>(primitive.keySet());
		keys.addAll(nonPrimitive.keySet());
		return keys;
	}

	@Override
	public CachedNode put(final ATermAppl key, final CachedNode value)
	{
		if (ATermUtils.isPrimitiveOrNegated(key))
		{
			final CachedNode prev = primitive.put(key, value);
			if (isFull())
				nonPrimitive.entrySet();
			return prev;
		}

		return nonPrimitive.put(key, value);
	}

	@Override
	public void putAll(final Map<? extends ATermAppl, ? extends CachedNode> t)
	{
		for (final java.util.Map.Entry<? extends ATermAppl, ? extends CachedNode> entry : t.entrySet())
			put(entry.getKey(), entry.getValue());
	}

	@Override
	public CachedNode remove(final Object key)
	{
		if (primitive.containsKey(key))
			return primitive.remove(key);
		return nonPrimitive.remove(key);
	}

	@Override
	public int size()
	{
		return primitive.size() + nonPrimitive.size();
	}

	@Override
	public Collection<CachedNode> values()
	{
		final Set<CachedNode> valueSet = new HashSet<>(primitive.values());
		valueSet.addAll(nonPrimitive.values());
		return valueSet;
	}

	@Override
	public String toString()
	{
		return "[Cache size: " + primitive.size() + "," + nonPrimitive.size() + "]";
	}

}
