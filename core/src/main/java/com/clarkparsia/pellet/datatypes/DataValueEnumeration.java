package com.clarkparsia.pellet.datatypes;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.NoSuchElementException;
import java.util.Set;

/**
 * <p>
 * Title: Data value enumeration
 * </p>
 * <p>
 * Description: Enumeration of _data values (i.e., an OWL 2 DataOneOf)
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
public class DataValueEnumeration<T> implements DataRange<T>
{

	private final Set<T> values;
	private final int size;

	public DataValueEnumeration(final Collection<? extends T> values)
	{
		if (values == null)
			throw new NullPointerException();
		if (values.isEmpty())
			throw new IllegalArgumentException();

		this.values = Collections.unmodifiableSet(new LinkedHashSet<T>(values));
		this.size = this.values.size();
	}

	@Override
	public boolean contains(final Object value)
	{
		return values.contains(value);
	}

	@Override
	public boolean containsAtLeast(final int n)
	{
		return size >= n;
	}

	@Override
	public T getValue(final int i)
	{
		/*
		 * Inefficient, but no one should be using this method!
		 */
		if (i >= size)
			throw new NoSuchElementException();

		final Iterator<T> it = values.iterator();
		for (int j = 0; j < i; j++)
			it.next();

		return it.next();
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
		return size;
	}

	@Override
	public Iterator<T> valueIterator()
	{
		return values.iterator();
	}

	@Override
	public String toString()
	{
		return String.format("OneOf%s", values);
	}
}
