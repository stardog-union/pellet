// Portions Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// Clark & Parsia, LLC parts of this source code are available under the terms of the Affero General Public License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package org.mindswap.pellet.utils.iterator;

import aterm.ATermAppl;
import aterm.ATermList;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

public class MultiListIterator implements Iterator<ATermAppl>
{
	private final List<ATermList> list = new ArrayList<>(2);

	private int index = 0;

	private ATermList curr;

	public MultiListIterator(final ATermList first)
	{
		curr = first;
	}

	@Override
	public boolean hasNext()
	{
		while (curr.isEmpty() && index < list.size())
			curr = list.get(index++);

		return !curr.isEmpty();
	}

	@Override
	public ATermAppl next()
	{
		if (!hasNext())
			throw new NoSuchElementException();

		final ATermAppl next = (ATermAppl) curr.getFirst();

		curr = curr.getNext();

		return next;
	}

	public void append(final ATermList other)
	{
		list.add(other);
	}

	@Override
	public void remove()
	{
		throw new UnsupportedOperationException();
	}
}
