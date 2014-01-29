// Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package com.clarkparsia.pellet.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * <p>
 * Title: Collection Utilities
 * </p>
 * <p>
 * Description:
 * </p>
 * <p>
 * Copyright: Copyright (c) 2008
 * </p>
 * <p>
 * Company: Clark & Parsia, LLC. <http://www.clarkparsia.com>
 * </p>
 * 
 * @author Harris Lin
 * @author Evren Sirin
 */
public class CollectionUtils {
	public static <K, V> Map<K, V> makeIdentityMap() {
		return new IdentityHashMap<K, V>();
	}

	public static <K, V> Map<K, V> makeIdentityMap(int size) {
		return new IdentityHashMap<K, V>( size );
	}

	public static <K, V> Map<K, V> makeIdentityMap(Map<? extends K, ? extends V> map) {
		return new IdentityHashMap<K, V>( map );
	}

	public static <T> Set<T> makeIdentitySet() {
		return new IdentityHashSet<T>();
	}

	public static <T> Set<T> makeIdentitySet(int size) {
		return new IdentityHashSet<T>( size );
	}

	public static <T> Set<T> makeIdentitySet(Collection<? extends T> a) {
		return new IdentityHashSet<T>( a );
	}

	public static <T> List<T> makeList() {
		return new ArrayList<T>();
	}

	public static <T> List<T> makeList(int size) {
		return new ArrayList<T>( size );
	}

	public static <T> List<T> makeList(Collection<? extends T> a) {
		return new ArrayList<T>( a );
	}

	public static <K, V> Map<K, V> makeMap() {
		return new HashMap<K, V>();
	}

	public static <K, V> Map<K, V> makeMap(int size) {
		return new HashMap<K, V>( size );
	}

	public static <K, V> Map<K, V> makeMap(Map<? extends K, ? extends V> map) {
		return new HashMap<K, V>( map );
	}

	public static <T> Set<T> makeSet() {
		return new HashSet<T>();
	}

	public static <T> Set<T> makeSet(int size) {
		return new HashSet<T>( size );
	}

	public static <T> Set<T> makeSet(Collection<? extends T> a) {
		return new HashSet<T>( a );
	}
}
