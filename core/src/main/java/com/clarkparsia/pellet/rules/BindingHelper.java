// Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package com.clarkparsia.pellet.rules;

import java.util.Collection;

import com.clarkparsia.pellet.rules.model.AtomVariable;

/**
 * <p>
 * Title: Binding Helper
 * </p>
 * <p>
 * Description: Binding helper interface.
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

public interface BindingHelper {

	/**
	 * Returns a set of variables which this binding helper can bind.
	 */
	public Collection<? extends AtomVariable> getBindableVars( Collection<AtomVariable> bound );
	
	/**
	 * Returns a set of variables which must be bound before this helper can generate bindings.
	 */
	public Collection<? extends AtomVariable> getPrerequisiteVars( Collection<AtomVariable> bound );
	
	/**
	 * Set the incoming binding for this helper.  This fixes
	 * any variables that are already bound by a preceding
	 * Binding Helper.
	 * 
	 * @param newBinding Binding map.  Implementation will
	 * copy map if needed.
	 */
	public void rebind( VariableBinding newBinding );
	
	/**
	 * Selects the next binding. 
	 * 
	 * @return True if a binding was available for this pattern  given
	 * the initial binding.  False otherwise.  Will return if the binding
	 * is not set.
	 */
	public boolean selectNextBinding();
	
	/**
	 * Set the variables this pattern uses in the given map.
	 * @param currentBinding
	 */
	public void setCurrentBinding( VariableBinding currentBinding );
	
}
