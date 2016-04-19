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

	private final Map<ATermAppl, ATermAppl> bindings = new HashMap<>();

	public ResultBindingImpl()
	{
	}

	private ResultBindingImpl(final Map<ATermAppl, ATermAppl> bindings)
	{
		this.bindings.putAll(bindings);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setValue(final ATermAppl var, final ATermAppl binding)
	{
		bindings.put(var, binding);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setValues(final ResultBinding binding)
	{
		if (binding instanceof ResultBindingImpl)
			bindings.putAll(((ResultBindingImpl) binding).bindings);
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
		return bindings.get(var);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isBound(final ATermAppl var)
	{
		return bindings.containsKey(var);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Set<ATermAppl> getAllVariables()
	{
		return bindings.keySet();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ResultBinding duplicate()
	{
		return new ResultBindingImpl(this.bindings);
	}

	@Override
	public String toString()
	{
		return bindings.toString();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isEmpty()
	{
		return bindings.isEmpty();
	}

	@Override
	public int hashCode()
	{
		final int PRIME = 31;
		int result = 1;
		result = PRIME * result + ((bindings == null) ? 0 : bindings.hashCode());
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
		if (bindings == null)
		{
			if (other.bindings != null)
				return false;
		}
		else
			if (!bindings.equals(other.bindings))
				return false;
		return true;
	}
}
