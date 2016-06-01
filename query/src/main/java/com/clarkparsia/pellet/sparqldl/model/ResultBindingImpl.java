// Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package com.clarkparsia.pellet.sparqldl.model;

import aterm.ATermAppl;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * <p>
 * Title: Default implementation of the result binding.
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
public class ResultBindingImpl implements ResultBinding
{

	private final Map<ATermAppl, ATermAppl> _bindings = new HashMap<>();

	public ResultBindingImpl()
	{
	}

	private ResultBindingImpl(final Map<ATermAppl, ATermAppl> bindings)
	{
		this._bindings.putAll(bindings);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setValue(final ATermAppl var, final ATermAppl binding)
	{
		_bindings.put(var, binding);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setValues(final ResultBinding binding)
	{
		if (binding instanceof ResultBindingImpl)
			_bindings.putAll(((ResultBindingImpl) binding)._bindings);
		else
			for (final ATermAppl var : binding.getAllVariables())
				setValue(var, binding.getValue(var));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ATermAppl getValue(final ATermAppl var)
	{
		return _bindings.get(var);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isBound(final ATermAppl var)
	{
		return _bindings.containsKey(var);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Set<ATermAppl> getAllVariables()
	{
		return _bindings.keySet();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ResultBinding duplicate()
	{
		return new ResultBindingImpl(this._bindings);
	}

	@Override
	public String toString()
	{
		return _bindings.toString();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isEmpty()
	{
		return _bindings.isEmpty();
	}

	@Override
	public int hashCode()
	{
		final int PRIME = 31;
		int result = 1;
		result = PRIME * result + ((_bindings == null) ? 0 : _bindings.hashCode());
		return result;
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
		final ResultBindingImpl other = (ResultBindingImpl) obj;
		if (_bindings == null)
		{
			if (other._bindings != null)
				return false;
		}
		else
			if (!_bindings.equals(other._bindings))
				return false;
		return true;
	}
}
