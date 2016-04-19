// Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package org.mindswap.pellet.utils.intset;

import java.util.NoSuchElementException;

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
public abstract class AbstractIntSet implements IntSet
{
	private int min = Integer.MAX_VALUE;
	private int max = Integer.MIN_VALUE;

	@Override
	public void addAll(final IntSet values)
	{
		final IntIterator i = values.iterator();
		while (i.hasNext())
			add(i.next());
	}

	protected void added(final int low, final int high)
	{
		if (low < min)
			min = low;
		if (high > max)
			max = high;
	}

	@Override
	public int max()
	{
		if (isEmpty())
			throw new NoSuchElementException();

		return max;
	}

	@Override
	public int min()
	{
		if (isEmpty())
			throw new NoSuchElementException();

		return min;
	}

	protected void removed(final int low, final int high)
	{
		if (isEmpty() || (low == min || high == max))
		{
			min = Integer.MAX_VALUE;
			max = Integer.MIN_VALUE;

			final IntIterator i = iterator();
			while (i.hasNext())
			{
				final int value = i.next();
				if (value < min)
					min = value;
				if (value > max)
					max = value;
			}
		}
	}

	@Override
	public IntSet union(final IntSet values)
	{
		final IntSet newSet = copy();
		newSet.addAll(values);

		return newSet;
	}

	@Override
	public String toString()
	{
		final StringBuffer s = new StringBuffer();
		s.append('[');
		final IntIterator i = iterator();
		while (i.hasNext())
		{
			if (s.length() > 1)
				s.append(',');
			s.append(String.valueOf(i.next()));
		}
		s.append(']');
		return s.toString();
	}
}
