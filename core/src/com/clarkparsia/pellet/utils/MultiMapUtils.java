// Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package com.clarkparsia.pellet.utils;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

/**
 * <p>Title: </p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: Clark & Parsia, LLC. <http://www.clarkparsia.com></p>
 *
 * @author Evren Sirin
 */
public class MultiMapUtils {

	public static <K,V> boolean add(Map<K, Set<V>> map, K key, V value) {
	    Set<V> values = map.get( key );
	    if( values == null ) {
	        values = CollectionUtils.makeSet();
	        map.put( key, values );
	    }
	    
	    return values.add( value );
	}
	
	public static <K,V> boolean addAll(Map<K, Set<V>> map, K key, Collection<V> value) {
	    Set<V> values = map.get( key );
	    if( values == null ) {
	        values = CollectionUtils.makeSet();
	        map.put( key, values );
	    }
	    
	    return values.addAll( value );
	}

	public static <K,V> boolean contains(Map<K, Set<V>> map, K key, V value) {
	    Set<V> values = map.get( key );
	    return values != null && values.contains( value );
	}

	public static <K,V> Set<V> get(Map<K, Set<V>> map, K key) {
	    Set<V> values = map.get( key );
	    return values != null ? values : Collections.<V>emptySet();
	}
	
	public static <K,V> boolean remove(Map<K, Set<V>> map, K key, V value ) {
		boolean removed = false;
		
	    Set<V> values = map.get( key );
	    if( values != null ) { 
	        removed = values.remove( value );
	        
	        if( values.isEmpty() )
	        	map.remove( key );
	    }
	    
	    return removed;
	}

}
