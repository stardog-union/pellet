// Copyright (c) 2006 - 2015, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public
// License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of
// proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package com.clarkparsia.pellet.utils;

import java.util.AbstractMap;
import java.util.AbstractSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterators;
import com.google.common.collect.Maps;

/**
 * A mutable map implementation that is view over a map that is not modified. The additions and removals to the base map are tracked internally
 * and all the map functions operate as if the changes have been applied to base map. Updating the base map outside this class will have undefined
 * consequences.
 *
 * @author Evren Sirin
 */
public class DeltaMap<K, V> extends AbstractMap<K, V> {
	private final Map<K, V> base;
	private final Map<K, V> additions = Maps.newHashMap();
	private final Map<K, V> removals = Maps.newHashMap();
	private int size;

	/**
	 * Creates a {@link DeltaMap} with the given base map.
	 */
	public DeltaMap(final Map<K, V> base) {
		this.base = base;
		this.size = base.size();
	}

	/**
	 * Clears all the changes tracked resetting the contents of this map to the base map.
	 */
	public void reset() {
		additions.clear();
		removals.clear();
		size = base.size();
	}

	/**
	 * Returns the additions that have been applied to this map.
	 */
	public Map<K, V> getAdditions() {
		return additions;
	}

	/**
	 * Returns the removals that have been applied to this map. Only the removals that are in the base map are tracked.
	 */
	public Map<K, V> getRemovals() {
		return removals;
	}

	@Override
	public void clear() {
		additions.clear();
		removals.putAll(base);
		size = 0;
	}

	@Override
	public boolean containsKey(final Object key) {
		return additions.containsKey(key) || !removals.containsKey(key) && base.containsKey(key);
	}

	@Override
	public V get(final Object key) {
		V result = additions.get(key);
		if (result == null && !removals.containsKey(key)) {
			result = base.get(key);
		}
		return result;
	}

	@Override
	public V put(final K key, final V value) {
		V result = additions.put(key, value);
		if (result == null && removals.remove(key) == null) {
			result = base.get(key);
		}
		if (result == null) {
			size++;
		}
		return result;
	}

	@Override
	public V remove(final Object e) {
		V result = additions.remove(e);
		if (result == null) {
			result = base.get(e);
			if (result != null) {
				removals.put((K) e, result);
			}
		}
		if (result != null) {
			size--;
		}
		return result;
	}

	@Override
	public Set<Entry<K, V>> entrySet() {
		return new AbstractSet<Entry<K, V>>() {
			@Override
			public Iterator<Entry<K, V>> iterator() {
				Iterator<Entry<K, V>> result = base.entrySet().iterator();

				if (!additions.isEmpty() || !removals.isEmpty()) {
					result = Iterators.filter(result, new Predicate<Entry<K, V>>() {
						@Override
						public boolean apply(final Entry<K, V> theEntry) {
							return !additions.containsKey(theEntry.getKey()) && !removals.containsKey(theEntry.getKey());
						}
					});
				}

				if (!additions.isEmpty()) {
					result = Iterators.concat(result, additions.entrySet().iterator());
				}

				return result;
			}

			@Override
			public boolean contains(final Object o) {
				if (!(o instanceof Map.Entry))
					return false;
				Map.Entry<?,?> e = (Map.Entry<?,?>) o;
				Object key = e.getKey();
				Object value = get(key);
				return Objects.equals(e.getValue(), value);
			}

			@Override
			public boolean remove(final Object o) {
				throw new UnsupportedOperationException();
			}

			@Override
			public int size() {
				return size;
			}
		};
	}

	@Override
	public int size() {
		return size;
	}
}