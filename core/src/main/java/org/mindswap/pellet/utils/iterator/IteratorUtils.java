// Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package org.mindswap.pellet.utils.iterator;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;

/**
 * @author Evren Sirin
 */
public class IteratorUtils
{
	private static class SingletonIterator<T> implements Iterator<T>
	{
		private final T element;
		private boolean consumed;

		private SingletonIterator(final T element)
		{
			this.element = element;
			this.consumed = false;
		}

		@Override
		public boolean hasNext()
		{
			return !consumed;
		}

		@Override
		public T next()
		{
			if (!hasNext())
				throw new NoSuchElementException();
			consumed = true;
			return element;
		}

		@Override
		public void remove()
		{
			throw new UnsupportedOperationException();
		}
	}

	private static final Iterator<Object> EMPTY_ITERATOR = new Iterator<Object>()
	{
		@Override
		public boolean hasNext()
		{
			return false;
		}

		@Override
		public Object next()
		{
			throw new NoSuchElementException();
		}

		@Override
		public void remove()
		{
			throw new UnsupportedOperationException();
		}
	};

	public static <T> Iterator<T> concat(final Iterator<? extends T> i1, final Iterator<? extends T> i2)
	{
		return new MultiIterator<>(i1, i2);
	}

	@SuppressWarnings("unchecked")
	public static final <T> Iterator<T> emptyIterator()
	{
		return (Iterator<T>) EMPTY_ITERATOR;
	}

	public static final <T> Iterator<T> singletonIterator(final T element)
	{
		return new SingletonIterator<>(element);
	}

	public static <T> Set<T> toSet(final Iterator<T> i)
	{
		final Set<T> set = new HashSet<>();
		while (i.hasNext())
			set.add(i.next());
		return set;
	}

	public static <T> List<T> toList(final Iterator<T> i)
	{
		final List<T> set = new ArrayList<>();
		while (i.hasNext())
			set.add(i.next());
		return set;
	}

	public static <T> Iterator<T> flatten(final Iterator<? extends Iterable<T>> iterator)
	{
		return new FlattenningIterator<>(iterator);
	}

	public static <T> Iterator<T> iterator(final T... elements)
	{
		return new ArrayIterator<>(elements, elements.length);
	}

	public static <T> Iterator<T> iterator(final int size, final T... elements)
	{
		return new ArrayIterator<>(elements, size);
	}

	private static class ArrayIterator<E> implements Iterator<E>
	{
		private final E[] array;
		private final int size;
		private int curr = 0;

		public ArrayIterator(final E[] array, final int size)
		{
			this.array = array;
			this.size = size;
		}

		@Override
		public boolean hasNext()
		{
			return curr != size;
		}

		@Override
		public E next()
		{
			if (!hasNext())
				throw new NoSuchElementException();

			return array[curr++];
		}

		@Override
		public void remove()
		{
			throw new UnsupportedOperationException();
		}
	}
}
