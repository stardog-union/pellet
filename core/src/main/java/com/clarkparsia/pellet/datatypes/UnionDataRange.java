package com.clarkparsia.pellet.datatypes;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Set;

/**
 * <p>
 * Title: Union _data range
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

	private final ArrayList<RestrictedDatatype<? extends T>> _ranges;
	private final Set<? extends T> _values;

	public UnionDataRange(final Collection<RestrictedDatatype<? extends T>> ranges, final Collection<? extends T> values)
	{
		this._ranges = new ArrayList<>(ranges);
		this._values = new HashSet<T>(values);
	}

	@Override
	public boolean contains(final Object value)
	{
		if (_values.contains(value))
			return true;

		for (final RestrictedDatatype<? extends T> rd : _ranges)
			if (rd.contains(value))
				return true;

		return false;
	}

	@SuppressWarnings("deprecation")
	@Override
	public boolean containsAtLeast(int n)
	{
		n -= _values.size();
		if (n <= 0)
			return true;

		for (final RestrictedDatatype<?> rd : _ranges)
		{
			if (rd.containsAtLeast(n))
				return true;

			n -= rd.size(); // FIXME This may crash.
		}

		return n <= 0;
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
	public Iterator<T> valueIterator()
	{

		/*
		 * This implementation avoids allocating the value iterators for the
		 * _data _ranges until (and only if) they are needed. This is a
		 * micro-optimization relative to using
		 * org.mindswap.pellet.utils.iterator.MultiIterator
		 */
		return new Iterator<T>()
		{
			final Iterator<? extends T> enumIt = _values.iterator();
			final Iterator<RestrictedDatatype<? extends T>> rangeIt = _ranges.iterator();
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
