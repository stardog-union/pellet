// Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package org.mindswap.pellet.utils;

import java.util.AbstractSet;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * A specialized immutable implementation of Set interface that always contains exactly two elements.
 *
 * @author Evren Sirin
 */
public class BinarySet<T> extends AbstractSet<T>
{
	private final T first;
	private final T second;

	private BinarySet(final T first, final T second)
	{
		if (first.equals(second))
			throw new IllegalArgumentException("Cannot create a binary set with single element: " + first);
		this.first = first;
		this.second = second;
	}

	public static <T> BinarySet<T> create(final T first, final T second)
	{
		return new BinarySet<>(first, second);
	}

	public T first()
	{
		return first;
	}

	public T second()
	{
		return second;
	}

	@Override
	public boolean contains(final Object o)
	{
		return first.equals(o) || second.equals(o);
	}

	@Override
	public Iterator<T> iterator()
	{
		return new Iterator<T>()
		{
			private int i = 0;

			@Override
			public boolean hasNext()
			{
				return i < 2;
			}

			@Override
			public T next()
			{
				if (!hasNext())
					throw new NoSuchElementException();

				return (i++ == 0) ? first : second;
			}

			@Override
			public void remove()
			{
				throw new UnsupportedOperationException();
			}
		};
	}

	@Override
	public int size()
	{
		return 2;
	}

}
