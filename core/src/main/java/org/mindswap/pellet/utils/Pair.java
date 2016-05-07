// Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package org.mindswap.pellet.utils;

/**
 * <p>
 * Title:
 * </p>
 * <p>
 * Description:
 * </p>
 * <p>
 * Copyright: Copyright (c) 2006
 * </p>
 * <p>
 * Company: Clark & Parsia, LLC. <http://www.clarkparsia.com>
 * </p>
 *
 * @author Evren sirin
 */
public class Pair<F, S>
{
	public F first;
	public S second;

	public Pair(final F first_, final S second_)
	{
		if (first_ == null || second_ == null)
			throw new NullPointerException();

		this.first = first_;
		this.second = second_;
	}

	public static <F, S> Pair<F, S> create(final F f, final S s)
	{
		return new Pair<>(f, s);
	}

	@Override
	public int hashCode()
	{
		return first.hashCode() + second.hashCode();
	}

	@Override
	public boolean equals(final Object o)
	{
		if (o == this)
			return true;

		if (!(o instanceof Pair))
			return false;

		final Pair<?, ?> p = (Pair<?, ?>) o;

		return first.equals(p.first) && second.equals(p.second);
	}

	@Override
	public String toString()
	{
		return "[" + first + ", " + second + "]";
	}
}
