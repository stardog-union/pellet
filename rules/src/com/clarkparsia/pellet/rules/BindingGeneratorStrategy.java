// Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package com.clarkparsia.pellet.rules;

import com.clarkparsia.pellet.rules.model.Rule;

/**
 * <p>
 * Title: Binding Generator Strategy
 * </p>
 * <p>
 * Description: Interface for BindingGenerator construction strategies.
 * The strategy takes a rule, and returns a binding generator that iterates
 * over possible bindings.
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

public interface BindingGeneratorStrategy {

	/**
	 * Creates a <code>BindingGenerator</code> for a single rule.
	 */
	public BindingGenerator createGenerator( Rule rule );
	
	public BindingGenerator createGenerator( Rule rule, VariableBinding initialBinding );
	
}
