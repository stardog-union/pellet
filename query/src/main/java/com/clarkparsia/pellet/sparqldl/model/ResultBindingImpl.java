// Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package com.clarkparsia.pellet.sparqldl.model;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.mindswap.pellet.exceptions.InternalReasonerException;

import aterm.ATermAppl;

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
public class ResultBindingImpl implements ResultBinding {

	private final Map<ATermAppl, ATermAppl> bindings = new HashMap<ATermAppl, ATermAppl>();

	public ResultBindingImpl() {
	}

	private ResultBindingImpl(final Map<ATermAppl, ATermAppl> bindings) {
		this.bindings.putAll(bindings);
	}

	/**
	 * {@inheritDoc}
	 */
	public void setValue(ATermAppl var, ATermAppl binding) {
		bindings.put(var, binding);
	}

	/**
	 * {@inheritDoc}
	 */
	public void setValues(ResultBinding binding) {
		if (binding instanceof ResultBindingImpl) {
			bindings.putAll(((ResultBindingImpl) binding).bindings);
		} else {
			for (final ATermAppl var : binding.getAllVariables()) {
				setValue(var, binding.getValue(var));
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public ATermAppl getValue(ATermAppl var) {
		return bindings.get(var);
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean isBound(ATermAppl var) {
		return bindings.containsKey(var);
	}

	/**
	 * {@inheritDoc}
	 */
	public Set<ATermAppl> getAllVariables() {
		return bindings.keySet();
	}

	/**
	 * {@inheritDoc}
	 */
	public ResultBinding duplicate() {
		return new ResultBindingImpl( this.bindings );
	}

	@Override
	public String toString() {
		return bindings.toString();
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean isEmpty() {
		return bindings.isEmpty();
	}

	@Override
	public int hashCode() {
		final int PRIME = 31;
		int result = 1;
		result = PRIME * result
				+ ((bindings == null) ? 0 : bindings.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		final ResultBindingImpl other = (ResultBindingImpl) obj;
		if (bindings == null) {
			if (other.bindings != null)
				return false;
		} else if (!bindings.equals(other.bindings))
			return false;
		return true;
	}
}
