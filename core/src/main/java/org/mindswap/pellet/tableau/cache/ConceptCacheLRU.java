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

import org.mindswap.pellet.KnowledgeBase;
import org.mindswap.pellet.utils.ATermUtils;

import aterm.ATermAppl;

/**
 * <p>
 * Title:
 * </p>
 * <p>
 * Description: LRU implementation of ConceptCache. Primitive concepts and their
 * negation are always kept in the cache. The least recently used complex
 * concept will be removed from the cache if the max size is reached.
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
public class ConceptCacheLRU extends AbstractConceptCache implements ConceptCache {
	private Map<ATermAppl, CachedNode>				primitive;
	private LinkedHashMap<ATermAppl, CachedNode>	nonPrimitive;
	
	private CacheSafety					cacheSafety;

	/**
	 * Creates an empty ConceptCacheImpl with no size restrictions
	 * 
	 * @param maxSize
	 */
	public ConceptCacheLRU(KnowledgeBase kb) {
		this( kb, Integer.MAX_VALUE );
	}

	/**
	 * Creates an empty cache with at most <code>maxSize</code> elements which
	 * are neither named or negations of names.
	 * 
	 * @param maxSize
	 */
	@SuppressWarnings("serial")
	public ConceptCacheLRU(KnowledgeBase kb, int maxSize) {
		super( maxSize );
		
		cacheSafety = CacheSafetyFactory.createCacheSafety( kb.getExpressivity() );
				
		primitive = new HashMap<ATermAppl, CachedNode>();
		nonPrimitive = new LinkedHashMap<ATermAppl, CachedNode>( 16, 0.75f, true ) {
			protected boolean removeEldestEntry(Map.Entry<ATermAppl, CachedNode> eldest) {
				return nonPrimitive.size() > getMaxSize();
			}
		};
	}
	
	public CacheSafety getSafety() {
		return cacheSafety;
	}

	public void clear() {
		primitive.clear();
		nonPrimitive.clear();
	}

	public boolean containsKey(Object key) {
		if( primitive.containsKey( key ) || nonPrimitive.containsKey( key ) ) {
			return true;
		}
		return false;
	}

	public boolean containsValue(Object value) {
		if( primitive.containsValue( value ) || nonPrimitive.containsValue( value ) ) {
			return true;
		}
		return false;
	}

	public Set<java.util.Map.Entry<ATermAppl, CachedNode>> entrySet() {
		Set<java.util.Map.Entry<ATermAppl, CachedNode>> returnSet;
		returnSet = new HashSet<java.util.Map.Entry<ATermAppl, CachedNode>>( primitive.entrySet() );
		returnSet.addAll( nonPrimitive.entrySet() );
		return returnSet;
	}

	public CachedNode get(Object key) {
		if( primitive.containsKey( key ) )
			return primitive.get( key );
		return nonPrimitive.get( key );
	}

	public boolean isEmpty() {
		return primitive.isEmpty() && nonPrimitive.isEmpty();
	}

	public Set<ATermAppl> keySet() {
		Set<ATermAppl> keys = new HashSet<ATermAppl>( primitive.keySet() );
		keys.addAll( nonPrimitive.keySet() );
		return keys;
	}

	public CachedNode put(ATermAppl key, CachedNode value) {
		if( ATermUtils.isPrimitiveOrNegated( key ) ) {
			CachedNode prev = primitive.put( key, value );
			if( isFull() ) {
				nonPrimitive.entrySet();
			}
			return prev;
		}

		return nonPrimitive.put( key, value );
	}

	public void putAll(Map<? extends ATermAppl, ? extends CachedNode> t) {
		for( java.util.Map.Entry<? extends ATermAppl, ? extends CachedNode> entry : t.entrySet() ) {
			put( entry.getKey(), entry.getValue() );
		}
	}

	public CachedNode remove(Object key) {
		if( primitive.containsKey( key ) )
			return primitive.remove( key );
		return nonPrimitive.remove( key );
	}

	public int size() {
		return primitive.size() + nonPrimitive.size();
	}

	public Collection<CachedNode> values() {
		Set<CachedNode> valueSet = new HashSet<CachedNode>( primitive.values() );
		valueSet.addAll( nonPrimitive.values() );
		return valueSet;
	}

	public String toString() {
		return "[Cache size: " + primitive.size() + "," + nonPrimitive.size() + "]";
	}
	
}
