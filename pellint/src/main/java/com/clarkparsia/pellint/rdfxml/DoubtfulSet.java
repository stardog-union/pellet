// Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package com.clarkparsia.pellint.rdfxml;

import java.util.HashSet;
import java.util.Set;

/**
 * <p>
 * Title:
 * </p>
 * <p>
 * Description:
 * </p>
 * <p>
 * Copyright: Copyright (c) 2008
 * </p>
 * <p>
 * Company: Clark & Parsia, LLC. <http://www.clarkparsia.com>
 * </p>
 *
 * @author Harris Lin
 */
public class DoubtfulSet<E> extends HashSet<E>
{
	private static final long serialVersionUID = 1L;

	private final HashSet<E> _definite;

	public DoubtfulSet()
	{
		_definite = new HashSet<>();
	}

	@Override
	public void clear()
	{
		super.clear();
		_definite.clear();
	}

	@Override
	public boolean remove(final Object o)
	{
		_definite.remove(o);
		return super.remove(o);
	}

	public boolean addDefinite(final E o)
	{
		super.add(o);
		return _definite.add(o);
	}

	public boolean containsDefinite(final E o)
	{
		return _definite.contains(o);
	}

	public Set<E> getDefiniteElements()
	{
		return _definite;
	}

	public Set<E> getDoubtfulElements()
	{
		final Set<E> doubtfulSet = new HashSet<>(this);
		doubtfulSet.removeAll(_definite);
		return doubtfulSet;
	}
}
