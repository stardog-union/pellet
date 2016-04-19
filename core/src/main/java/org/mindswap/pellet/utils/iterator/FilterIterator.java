package org.mindswap.pellet.utils.iterator;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * <p>
 * Title:
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
 * @author Evren Sirin
 */
public abstract class FilterIterator<T> implements Iterator<T>
{
	private final Iterator<T> iterator;

	/**
	 * Next element to be returned, or null if iterator is consumed
	 */
	private T next;

	public FilterIterator(final Iterator<T> iterator)
	{
		this.iterator = iterator;
	}

	private void findNext()
	{
		if (next != null)
			return;

		while (iterator.hasNext())
		{
			next = iterator.next();

			// if this element is filter
			if (!filter(next))
				return;
		}

		next = null;
	}

	@Override
	public boolean hasNext()
	{
		findNext();

		return next != null;
	}

	@Override
	public T next()
	{
		if (!hasNext())
			throw new NoSuchElementException();

		final T result = next;
		next = null;

		return result;
	}

	@Override
	public void remove()
	{
		throw new UnsupportedOperationException();
	}

	public abstract boolean filter(T obj);
}
