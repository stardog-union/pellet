package com.clarkparsia.pellet.datatypes;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * <p>
 * Title: Empty Data Range
 * </p>
 * <p>
 * Description: Re-usable empty _data range implementation. Cannot be static so that parameterization is handled correctly.
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
public class EmptyDataRange<T> implements DataRange<T>
{

	final private Iterator<T> iterator;

	public EmptyDataRange()
	{
		this.iterator = new EmptyIterator<>();
	}

	@Override
	public boolean contains(final Object value)
	{
		return false;
	}

	@Override
	public boolean containsAtLeast(final int n)
	{
		return n <= 0;
	}

	@Override
	public T getValue(final int i)
	{
		throw new NoSuchElementException();
	}

	@Override
	public boolean isEmpty()
	{
		return true;
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
		return 0;
	}

	@Override
	public Iterator<T> valueIterator()
	{
		return iterator;
	}

}
