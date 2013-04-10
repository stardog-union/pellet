// Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package com.clarkparsia.pellet.sparqldl.model;

import java.util.Set;

import aterm.ATermAppl;

/**
 * <p>
 * Title: Result Binding Interface
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
public interface ResultBinding {

	/**
	 * Gets value for given variable.
	 * 
	 * @param variable
	 *            for which return the value
	 * @return binding for the variable
	 */
	public ATermAppl getValue(final ATermAppl var);

	/**
	 * Sets all variable bindings according to the bindings.
	 * 
	 * @param bindings
	 *            to be set.
	 */
	public void setValues(final ResultBinding bindings);

	/**
	 * Sets one variable binding.
	 * 
	 * @param bindings
	 *            to be set.
	 * @param var
	 *            variable to set.
	 */
	public void setValue(final ATermAppl var, final ATermAppl binding);

	/**
	 * Checks whether given variable is bound.
	 * 
	 * @param var
	 *            variable to determine.
	 * @return true if the given variable is bound.
	 */
	public boolean isBound(final ATermAppl var);

	/**
	 * Returns all variables in this binding.
	 * 
	 * @return set of all variables.
	 */
	public Set<ATermAppl> getAllVariables();

	/**
	 * Checks for emptiness of the binding.
	 * 
	 * @return true if the binding doesn't contain a variable.
	 */
	public boolean isEmpty();

	/**
	 * Clones the binding.
	 * 
	 * @return new copy of the binding.
	 */
	public ResultBinding duplicate();
}
