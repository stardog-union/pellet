// Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package com.clarkparsia.pellet.rules;

import java.util.Iterator;
import java.util.NoSuchElementException;
import org.mindswap.pellet.Literal;
import org.mindswap.pellet.Node;

/**
 * <p>
 * Title: Literal Filter
 * </p>
 * <p>
 * Description: Filters an _iterator of _nodes for literals.
 * </p>
 * <p>
 * Copyright: Copyright (c) 2007
 * </p>
 * <p>
 * Company: Clark & Parsia, LLC. <http://www.clarkparsia.com>
 * </p>
 *
 * @author Ron Alford
 */
public class LiteralFilter implements Iterator<Literal>
{

	private final Iterator<Node> _iterator;
	private Literal _next;

	public LiteralFilter(final Iterator<Node> iterator)
	{
		this._iterator = iterator;
	}

	@Override
	public boolean hasNext()
	{
		while ((_next == null) && _iterator.hasNext())
		{
			final Node node = _iterator.next();
			if (node.isLiteral() && node.isRootNominal())
				_next = (Literal) node;
		}
		return _next != null;
	}

	@Override
	public Literal next()
	{
		if (!hasNext())
			throw new NoSuchElementException();

		final Literal result = _next;
		_next = null;

		return result;
	}

	@Override
	public void remove()
	{
		_iterator.remove();
	}

}
