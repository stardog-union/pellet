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

	private final HashSet<E> m_Definite;

	public DoubtfulSet()
	{
		m_Definite = new HashSet<>();
	}

	@Override
	public void clear()
	{
		super.clear();
		m_Definite.clear();
	}

	@Override
	public boolean remove(final Object o)
	{
		m_Definite.remove(o);
		return super.remove(o);
	}

	public boolean addDefinite(final E o)
	{
		super.add(o);
		return m_Definite.add(o);
	}

	public boolean containsDefinite(final E o)
	{
		return m_Definite.contains(o);
	}

	public Set<E> getDefiniteElements()
	{
		return m_Definite;
	}

	public Set<E> getDoubtfulElements()
	{
		final Set<E> doubtfulSet = new HashSet<>(this);
		doubtfulSet.removeAll(m_Definite);
		return doubtfulSet;
	}
}
