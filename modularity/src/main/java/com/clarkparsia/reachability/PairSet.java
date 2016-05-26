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
 * An unmodifiable Set implementation that is a wrapper around a pair of sets without additional storage for set elements. There may be common elements in two
 * sets which will be not be visible to the outside, i.e. iterator will discard duplicates on-the-fly.
 *
 * @author Evren Sirin
 */
public class PairSet<T> extends AbstractSet<T>
{

	private final Set<T> _firstSet;

	private final Set<T> _secondSet;

	private final int _size;

	/**
	 * Iterate through first and second set filtering any duplicates that might be in both sets. We always iterate through the large set first because every
	 * element in the second set will be checked for possible duplicates.
	 */
	public class PairIterator implements Iterator<T>
	{

		/**
		 * The first set we iterate over (not necessarily same as _firstSet)
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

		public PairIterator()
		{
			// iterate over the large set first
			if (_firstSet.size() < _secondSet.size())
			{
				firstIteratedSet = _firstSet;
				firstIterator = _firstSet.iterator();
				secondIterator = _secondSet.iterator();
			}
			else
			{
				firstIteratedSet = _secondSet;
				firstIterator = _secondSet.iterator();
				secondIterator = _firstSet.iterator();
			}

			// find the next element to return
			findNext();
		}

		private void findNext()
		{
			if (firstIterator.hasNext())
				// get the next element from the first iterator (no need to
				// worry about duplicates since it is a set)
				next = firstIterator.next();
			else
			{
				// assume there are no more elements
				next = null;
				// iterate until we find an element from second set that is not
				// also in the first set
				while (secondIterator.hasNext() && next == null)
				{
					next = secondIterator.next();

					// if this element is a duplicate
					if (firstIteratedSet.contains(next))
						// invalidate this element and continue
						next = null;
				}
			}
		}

		@Override
		public boolean hasNext()
		{
			return next != null;
		}

		@Override
		public T next()
		{
			if (!hasNext())
				throw new NoSuchElementException();

			final T result = next;
			findNext();
			return result;
		}

		@Override
		public void remove()
		{
			throw new UnsupportedOperationException();
		}
	}

	public PairSet(final Set<T> first, final Set<T> second)
	{
		_firstSet = first;
		_secondSet = second;

		_size = computeUnionSize();
	}

	private int computeUnionSize()
	{
		int size = _firstSet.size() + _secondSet.size();
		if (_firstSet.size() < _secondSet.size())
		{
			for (final T e : _firstSet)
				if (_secondSet.contains(e))
					size--;
		}
		else
			for (final T e : _secondSet)
				if (_firstSet.contains(e))
					size--;

		return size;
	}

	@Override
	public boolean add(final T o)
	{
		throw new UnsupportedOperationException("Pair sets are read-only");
	}

	@Override
	public boolean contains(final Object o)
	{
		return _firstSet.contains(o) || _secondSet.contains(o);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Iterator<T> iterator()
	{
		return new PairIterator();
	}

	@Override
	public boolean remove(final Object o)
	{
		throw new UnsupportedOperationException("Pair sets are read-only");
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int size()
	{
		return _size;
	}

}
