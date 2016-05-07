package com.clarkparsia.pellet.datatypes;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Set;

/**
 * <p>
 * Title: Union data range
 * </p>
 * <p>
 * Description: A _disjunction of {@link DataRange} objects
 * </p>
 * <p>
 * Copyright: Copyright (c) 2009
 * </p>
 * <p>
 * Company: Clark & Parsia, LLC. <http://www.clarkparsia.com>
 * </p>
 *
 * @author Mike Smith
 */
public class UnionDataRange<T> implements DataRange<T>
{

	private final ArrayList<RestrictedDatatype<? extends T>> ranges;
	private final Set<? extends T> values;

	public UnionDataRange(final Collection<RestrictedDatatype<? extends T>> ranges, final Collection<? extends T> values)
	{
		this.ranges = new ArrayList<>(ranges);
		this.values = new HashSet<T>(values);
	}

	@Override
	public boolean contains(final Object value)
	{
		if (values.contains(value))
			return true;

		for (final RestrictedDatatype<? extends T> rd : ranges)
			if (rd.contains(value))
				return true;

		return false;
	}

	@Override
	public boolean containsAtLeast(int n)
	{
		n -= values.size();
		if (n <= 0)
			return true;

		for (final RestrictedDatatype<?> rd : ranges)
		{
			if (rd.containsAtLeast(n))
				return true;

			n -= rd.size();
		}

		return n <= 0;
	}

	@Override
	public T getValue(final int i)
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean isEmpty()
	{
		return false;
	}

	@Override
	public boolean isEnumerable()
	{
		return true;
	}

	@Override
	public boolean isFinite()
	{
		return true;
	}

	@Override
	public int size()
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public Iterator<T> valueIterator()
	{

		/*
		 * This implementation avoids allocating the value iterators for the
		 * data ranges until (and only if) they are needed. This is a
		 * micro-optimization relative to using
		 * org.mindswap.pellet.utils.iterator.MultiIterator
		 */
		return new Iterator<T>()
		{
			final Iterator<? extends T> enumIt = values.iterator();
			final Iterator<RestrictedDatatype<? extends T>> rangeIt = ranges.iterator();
			Iterator<? extends T> valueIt = null;

			@Override
			public boolean hasNext()
			{
				if (enumIt.hasNext())
					return true;

				if (valueIt == null)
					if (rangeIt.hasNext())
						valueIt = rangeIt.next().valueIterator();
					else
						return false;

				while (!valueIt.hasNext())
					if (rangeIt.hasNext())
						valueIt = rangeIt.next().valueIterator();
					else
						return false;
				return true;
			}

			@Override
			public T next()
			{
				if (!hasNext())
					throw new NoSuchElementException();

				if (valueIt == null)
					return enumIt.next();

				return valueIt.next();
			}

			@Override
			public void remove()
			{
				throw new UnsupportedOperationException();
			}
		};
	}
}
