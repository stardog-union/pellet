// Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package org.mindswap.pellet.utils.iterator;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * @author Evren Sirin
 */
public class PairIterator<T> implements Iterator<T>
{
	private final Iterator<T> _first;
	private final Iterator<T> _second;

	public PairIterator(final Iterator<T> first, final Iterator<T> second)
	{
		this._first = first;
		this._second = second;
	}

	@Override
	public boolean hasNext()
	{
		return _first.hasNext() || _second.hasNext();
	}

	@Override
	public T next()
	{
		if (!hasNext())
			throw new NoSuchElementException();

		return _first.hasNext() ? _first.next() : _second.next();
	}

	@Override
	public void remove()
	{
		throw new UnsupportedOperationException();
	}
}
