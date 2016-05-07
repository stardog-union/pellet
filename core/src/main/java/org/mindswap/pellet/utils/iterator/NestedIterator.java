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
public abstract class NestedIterator<Outer, Inner> implements Iterator<Inner>
{
	private Iterator<? extends Outer> _outerIterator;
	private Iterator<? extends Inner> _innerIterator;

	public NestedIterator(final Iterable<? extends Outer> outerIterable)
	{
		this(outerIterable.iterator());
	}

	public NestedIterator(final Iterator<? extends Outer> outerIterator)
	{
		this._outerIterator = outerIterator;
		findIterator();
	}

	private void findIterator()
	{
		while (_outerIterator.hasNext())
		{
			final Outer subj = _outerIterator.next();
			_innerIterator = getInnerIterator(subj);

			if (_innerIterator.hasNext())
				return;
		}

		_innerIterator = IteratorUtils.emptyIterator();
	}

	public abstract Iterator<? extends Inner> getInnerIterator(Outer outer);

	@Override
	public boolean hasNext()
	{
		return _innerIterator.hasNext();
	}

	@Override
	public Inner next()
	{
		if (!hasNext())
			throw new NoSuchElementException();

		final Inner value = _innerIterator.next();

		if (!_innerIterator.hasNext())
			findIterator();

		return value;
	}

	@Override
	public void remove()
	{
		_innerIterator.remove();
	}
}
