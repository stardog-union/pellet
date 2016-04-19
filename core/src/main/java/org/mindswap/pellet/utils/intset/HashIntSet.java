// Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package org.mindswap.pellet.utils.intset;

import java.util.HashSet;
import java.util.Iterator;

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
public class HashIntSet extends AbstractIntSet implements IntSet
{
	private final HashSet<Integer> ints;

	public HashIntSet()
	{
		ints = new HashSet<>();
	}

	@Override
	public void add(final int value)
	{
		if (value < 0)
			throw new IndexOutOfBoundsException();

		ints.add(value);
		added(value, value);
	}

	@Override
	public void addAll(final IntSet values)
	{
		if (values instanceof HashIntSet)
		{
			ints.addAll(((HashIntSet) values).ints);
			if (!values.isEmpty())
				added(values.min(), values.max());
		}
		else
			super.addAll(values);
	}

	@Override
	public boolean contains(final int value)
	{
		return ints.contains(value);
	}

	@Override
	public IntSet copy()
	{
		final HashIntSet copy = new HashIntSet();
		copy.addAll(this);
		return copy;
	}

	@Override
	public boolean isEmpty()
	{
		return ints.isEmpty();
	}

	@Override
	public IntIterator iterator()
	{
		return new IntIterator()
		{
			private final Iterator<Integer> base = ints.iterator();

			@Override
			public boolean hasNext()
			{
				return base.hasNext();
			}

			@Override
			public int next()
			{
				return base.next();
			}
		};
	}

	@Override
	public void remove(final int value)
	{
		ints.remove(value);
		removed(value, value);
	}

	@Override
	public int size()
	{
		return ints.size();
	}

}
