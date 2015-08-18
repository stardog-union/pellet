// Copyright (c) 2006 - 2015, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package com.clarkparsia.reachability;

import java.util.AbstractSet;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Set;

/**
 * An unmodifiable Set implementation that is a wrapper around a pair of sets without additional storage for set elements. There may be common elements in two sets which will be
 * not be visible to the outside, i.e. iterator will discard duplicates on-the-fly.
 *
 * @author Evren Sirin
 */
public class PairSet<T> extends AbstractSet<T> implements Set<T> {

	private Set<T> firstSet;

	private Set<T> secondSet;

	private int size;

	/**
	 * Iterate through first and second set filtering any duplicates that might be in both sets. We always iterate through the large set first because every element in the second
	 * set will be checked for possible duplicates.
	 */
	public class PairIterator implements Iterator<T> {

		/**
		 * The first set we iterate over (not necessarily same as firstSet)
		 */
		private Set<T> firstIteratedSet;

		/**
		 * The iterator over the first iterated set
		 */
		private Iterator<T> firstIterator;

		/**
		 * The iterator over the second iterated set
		 */
		private Iterator<T> secondIterator;

		/**
		 * Next element to be returned, or null if both iterators are consumed
		 */
		private T next;

		public PairIterator() {
			// iterate over the large set first
			if (firstSet.size() < secondSet.size()) {
				firstIteratedSet = firstSet;
				firstIterator = firstSet.iterator();
				secondIterator = secondSet.iterator();
			}
			else {
				firstIteratedSet = secondSet;
				firstIterator = secondSet.iterator();
				secondIterator = firstSet.iterator();
			}

			// find the next element to return
			findNext();
		}

		private void findNext() {
			if (firstIterator.hasNext()) {
				// get the next element from the first iterator (no need to
				// worry about duplicates since it is a set)
				next = firstIterator.next();
			}
			else {
				// assume there are no more elements
				next = null;
				// iterate until we find an element from second set that is not
				// also in the first set
				while (secondIterator.hasNext() && next == null) {
					next = secondIterator.next();

					// if this element is a duplicate
					if (firstIteratedSet.contains(next)) {
						// invalidate this element and continue
						next = null;
					}
				}
			}
		}

		public boolean hasNext() {
			return next != null;
		}

		public T next() {
			if (!hasNext()) {
				throw new NoSuchElementException();
			}

			T result = next;
			findNext();
			return result;
		}

		public void remove() {
			throw new UnsupportedOperationException();
		}
	}

	public PairSet(Set<T> first, Set<T> second) {
		firstSet = first;
		secondSet = second;

		size = computeUnionSize();
	}

	private int computeUnionSize() {
		int size = firstSet.size() + secondSet.size();
		if (firstSet.size() < secondSet.size()) {
			for (T e : firstSet) {
				if (secondSet.contains(e)) {
					size--;
				}
			}
		}
		else {
			for (T e : secondSet) {
				if (firstSet.contains(e)) {
					size--;
				}
			}
		}

		return size;
	}

	@Override
	public boolean add(T o) {
		throw new UnsupportedOperationException("Pair sets are read-only");
	}

	@Override
	public boolean contains(Object o) {
		return firstSet.contains(o) || secondSet.contains(o);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Iterator<T> iterator() {
		return new PairIterator();
	}

	@Override
	public boolean remove(Object o) {
		throw new UnsupportedOperationException("Pair sets are read-only");
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int size() {
		return size;
	}

}
