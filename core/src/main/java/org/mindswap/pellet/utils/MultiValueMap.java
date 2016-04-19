// Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package org.mindswap.pellet.utils;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;

/**
 * <p>
 * Title:
 * </p>
 * <p>
 * Description:
 * </p>
 * <p>
 * Copyright: Copyright (c) 2007
 * </p>
 * <p>
 * Company: Clark & Parsia, LLC. <http://www.clarkparsia.com>
 * </p>
 *
 * @author Evren Sirin
 */
public class MultiValueMap<K, V> extends HashMap<K, Set<V>> implements Map<K, Set<V>>
{
	private static final long serialVersionUID = 2660982967886888197L;

	public MultiValueMap()
	{
	}

	public MultiValueMap(final int initialCapacity)
	{
		super(initialCapacity);
	}

	public Set<V> putSingle(final K key, final V value)
	{
		final Set<V> set = new HashSet<>();
		set.add(value);

		return super.put(key, set);
	}

	@Override
	public Set<V> put(final K key, final Set<V> values)
	{
		return super.put(key, values);
	}

	public boolean add(final K key, final V value)
	{
		Set<V> values = get(key);
		if (values == null)
		{
			values = new HashSet<>();
			super.put(key, values);
		}

		return values.add(value);
	}

	public boolean addAll(final K key, final Collection<? extends V> collection)
	{
		Set<V> values = get(key);
		if (values == null)
		{
			values = new HashSet<>();
			super.put(key, values);
		}

		return values.addAll(collection);
	}

	@Override
	public boolean remove(final Object key, final Object value)
	{
		boolean removed = false;

		final Set<V> values = get(key);
		if (values != null)
		{
			removed = values.remove(value);

			if (values.isEmpty())
				super.remove(key);
		}

		return removed;
	}

	public boolean contains(final K key, final V value)
	{
		final Set<V> values = get(key);
		if (values == null)
			return false;

		return values.contains(value);
	}

	public Iterator<V> flattenedValues()
	{
		return new Iterator<V>()
		{
			private final Iterator<Set<V>> setIterator = values().iterator();
			private Iterator<V> valueIterator = null;

			@Override
			public boolean hasNext()
			{
				while (valueIterator == null || !valueIterator.hasNext())
				{
					if (!setIterator.hasNext())
						return false;

					valueIterator = setIterator.next().iterator();
				}
				return true;
			}

			@Override
			public V next()
			{
				if (!hasNext())
					throw new NoSuchElementException();

				return valueIterator.next();
			}

			@Override
			public void remove()
			{
				setIterator.remove();
			}
		};
	}
}
