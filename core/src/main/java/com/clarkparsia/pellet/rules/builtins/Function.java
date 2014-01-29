// Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package com.clarkparsia.pellet.rules.builtins;

import org.mindswap.pellet.ABox;
import org.mindswap.pellet.Literal;

/**
 * <p>
 * Title: Function
 * </p>
 * <p>
 * Description: Interface for built-ins that can bind the first argument to a built-in atom
 * to a new value.
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
public interface Function {

	/**
	 * Apply the function against a set of arguments.  Test the
	 * expected argument against the result.
	 * Return null if the arguments were invalid.
	 * If 'expected' was not null and matched the result, return 'expected'.
	 * If 'expected' was not null and did not match, return null;
	 * 
	 * Otherwise, return the result.
	 */
	public Literal apply( ABox abox, Literal expected, Literal... args );
	
}
