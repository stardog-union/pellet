// Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package com.clarkparsia.pellet.sparqldl.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import openllet.aterm.ATermAppl;
import java.util.Set;

/**
 * <p>
 * Title: Default implementation of {@link QueryResult}
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
 * @author Petr Kremen
 */
public class QueryResultImpl implements QueryResult
{

	private Collection<ResultBinding> _bindings;

	private final List<ATermAppl> _resultVars;

	private final Query _query;
	private final QueryParameters _parameters;

	public QueryResultImpl(final Query query)
	{
		this._query = query;
		this._parameters = query.getQueryParameters();
		this._resultVars = new ArrayList<>(query.getResultVars());

		if (query.isDistinct())
			_bindings = new HashSet<>();
		else
			_bindings = new ArrayList<>();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void add(final ResultBinding binding)
	{
		_bindings.add(process(binding));
	}

	@Override
	public boolean equals(final Object obj)
	{
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		final QueryResultImpl other = (QueryResultImpl) obj;
		if (_bindings == null)
		{
			if (other._bindings != null)
				return false;
		}
		else
			if (!_bindings.equals(other._bindings))
				return false;
		if (_resultVars == null)
		{
			if (other._resultVars != null)
				return false;
		}
		else
			if (!_resultVars.equals(other._resultVars))
				return false;
		return true;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<ATermAppl> getResultVars()
	{
		return Collections.unmodifiableList(_resultVars);
	}

	@Override
	public int hashCode()
	{
		final int PRIME = 31;
		int result = 1;
		result = PRIME * result + ((_bindings == null) ? 0 : _bindings.hashCode());
		result = PRIME * result + ((_resultVars == null) ? 0 : _resultVars.hashCode());
		return result;
	}

	@Override
	public boolean isDistinct()
	{
		return (_bindings instanceof Set);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isEmpty()
	{
		return size() == 0;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Iterator<ResultBinding> iterator()
	{
		return _bindings.iterator();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int size()
	{
		return _bindings.size();
	}

	@Override
	public String toString()
	{
		return _bindings.toString();
	}

	private ResultBinding process(final ResultBinding binding)
	{
		if (_parameters == null)
			return binding;

		final int numOfVars = _query.getResultVars().size();

		// Add the _query _parameters to the binding if the variable is in the
		// _query projection
		for (final Entry<ATermAppl, ATermAppl> entry : _parameters.entrySet())
		{
			final ATermAppl var = entry.getKey();
			final ATermAppl value = entry.getValue();

			if (numOfVars == 0 || _query.getResultVars().contains(var))
				binding.setValue(var, value);
		}

		return binding;
	}
}
