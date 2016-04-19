package com.clarkparsia.pellet.datatypes;

import java.util.Iterator;

/**
 * <p>
 * Title: Negated Data Range
 * </p>
 * <p>
 * Description: A negated data range. By definition, this is infinite.
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
public class NegatedDataRange<T> implements DataRange<T>
{

	private final DataRange<? extends T> datarange;

	public NegatedDataRange(final DataRange<? extends T> datarange)
	{
		this.datarange = datarange;
	}

	@Override
	public boolean contains(final Object value)
	{
		return !datarange.contains(value);
	}

	@Override
	public boolean containsAtLeast(final int n)
	{
		return true;
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
		return false;
	}

	@Override
	public boolean isFinite()
	{
		return false;
	}

	@Override
	public int size()
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public Iterator<T> valueIterator()
	{
		throw new UnsupportedOperationException();
	}

	public DataRange<? extends T> getDataRange()
	{
		return datarange;
	}
}
