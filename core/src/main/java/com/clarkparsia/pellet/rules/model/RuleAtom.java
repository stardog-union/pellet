// Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package com.clarkparsia.pellet.rules.model;

import java.util.Collection;

/**
 * <p>
 * Title: Rule Atom
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
 * @author Ron Alford
 */ 
public interface RuleAtom {

	public void accept( RuleAtomVisitor visitor );
	
	/**
	 * Return all arguments (constants and variables) to the rule atom.
	 */
	public Collection<? extends AtomObject> getAllArguments();
	
	/**
	 * Return the predicate for the rule atom. The type of this predicate
	 * will depend on the implementation of this interface.  Use
	 * the RuleAtomVisitor for type safety.
	 */
	public Object getPredicate();
	
}
