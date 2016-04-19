// Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package org.mindswap.pellet.utils.intset;

import java.util.BitSet;
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
public class BitIntSet implements IntSet
{
	private final BitSet bits;

	public BitIntSet()
	{
		bits = new BitSet();
	}

	public BitIntSet(final BitIntSet other)
	{
		bits = (BitSet) other.bits.clone();
	}

	@Override
	public void add(final int value)
	{
		if (value < 0)
			throw new UnsupportedOperationException("Negatibe integers cannot be added to this set");

		bits.set(value);
	}

	@Override
	public void addAll(final IntSet values)
	{
		if (values instanceof BitIntSet)
			bits.or(((BitIntSet) values).bits);
		else
		{
			final IntIterator i = values.iterator();
			while (i.hasNext())
				add(i.next());
		}
	}

	@Override
	public boolean contains(final int value)
	{
		return bits.get(value);
	}

	@Override
	public IntSet copy()
	{
		return new BitIntSet(this);
	}

	@Override
	public boolean isEmpty()
	{
		return bits.isEmpty();
	}

	@Override
	public IntIterator iterator()
	{
		return new IntIterator()
		{
			private int next = bits.nextSetBit(0);

			@Override
			public boolean hasNext()
			{
				return next != -1;
			}

			@Override
			public int next()
			{
				final int curr = next;
				if (curr == -1)
					throw new NoSuchElementException();
				next = bits.nextSetBit(0);
				return curr;
			}
		};
	}

	@Override
	public int max()
	{
		return bits.length() - 1;
	}

	@Override
	public int min()
	{
		return bits.nextSetBit(0);
	}

	@Override
	public void remove(final int value)
	{
		if (value >= 0)
			bits.clear(value);
	}

	@Override
	public int size()
	{
		return bits.cardinality();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public IntSet union(final IntSet values)
	{
		final IntSet newSet = copy();
		newSet.addAll(values);

		return newSet;
	}

}
