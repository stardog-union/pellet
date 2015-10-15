// Copyright (c) 2006 - 2015, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public
// License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of
// proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package com.clarkparsia.pellet.utils;

import java.util.AbstractSet;
import java.util.Iterator;
import java.util.Objects;
import java.util.Set;

import com.google.common.base.Predicates;
import com.google.common.collect.Iterators;
import com.google.common.collect.Sets;

/**
 * A mutable Set implementation that is view over a set that is not modified. The additions and removals to the base set are tracked internally
 * and all the set functions operate as if the changes have been applied to base set. Updating the base set outside this class will have undefined
 * consequences.
 *
 * @author Evren Sirin
 */
public class DeltaSet<T> extends AbstractSet<T> {
	private final Set<T> base;
	private final Set<T> additions = Sets.newHashSet();
	private final Set<T> removals = Sets.newHashSet();

	/**
	 * Creates a {@link DeltaSet} with the given base set.
	 */
	public DeltaSet(final Set<T> base) {
		this.base = Objects.requireNonNull(base);
	}

	/**
	 * Clears all the changes tracked resetting the contents of this set to the base set.
	 */
	public void reset() {
		additions.clear();
		removals.clear();
	}

	/**
	 * Returns the additions that have been applied to this set. Only the additions that are not in the base set are tracked.
	 */
	public Set<T> getAdditions() {
		return additions;
	}

	/**
	 * Returns the removals that have been applied to this set. Only the removals that are in the base set are tracked.
	 */
	public Set<T> getRemovals() {
		return removals;
	}

	@Override
	public void clear() {
		additions.clear();
		removals.addAll(base);
	}

	@Override
	public boolean add(final T e) {
		return (removals.remove(e) || !base.contains(e)) && additions.add(e);
	}

	@Override
	public boolean remove(final Object e) {
		return (additions.remove(e) || base.contains(e)) && removals.add((T) e);
	}

	@Override
	public Iterator<T> iterator() {
		Iterator<T> result = base.iterator();

		if (!removals.isEmpty()) {
			result = Iterators.filter(result, Predicates.not(Predicates.in(removals)));
		}

		if (!additions.isEmpty()) {
			result = Iterators.concat(result, additions.iterator());
		}

		return result;
	}

	@Override
	public int size() {
		return additions.size() + (base == null ? 0 : (base.size() - removals.size()));
	}
}