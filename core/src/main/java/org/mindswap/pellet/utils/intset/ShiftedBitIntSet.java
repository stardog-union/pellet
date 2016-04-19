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
public class ShiftedBitIntSet extends BitIntSet
{
	private int min = Integer.MAX_VALUE;

	public ShiftedBitIntSet()
	{
		super();
	}

	public ShiftedBitIntSet(final ShiftedBitIntSet other)
	{
		super(other);

		min = other.min;
	}

	@Override
	public void add(final int value)
	{
		if (isEmpty())
		{
			min = value;
			super.add(0);
		}
		else
			if (value >= min)
				super.add(value - min);
			else
				throw new UnsupportedOperationException("Not implemented");
	}

	@Override
	public boolean contains(final int value)
	{
		if (value >= min)
			return super.contains(value - min);
		else
			return false;
	}

	@Override
	public IntSet copy()
	{
		return new ShiftedBitIntSet(this);
	}

	@Override
	public IntIterator iterator()
	{
		return new IntIterator()
		{
			private final IntIterator base = ShiftedBitIntSet.super.iterator();

			@Override
			public boolean hasNext()
			{
				return base.hasNext();
			}

			@Override
			public int next()
			{
				return min + base.next();
			}
		};
	}

	@Override
	public int max()
	{
		if (isEmpty())
			throw new NoSuchElementException();
		else
			return min + super.max();
	}

	@Override
	public int min()
	{
		if (isEmpty())
			throw new NoSuchElementException();
		else
			return min;
	}

	@Override
	public void remove(final int value)
	{
		if (value >= min)
		{
			super.remove(value - min);
			min = super.min();
		}
	}

}
