// Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package com.clarkparsia.pellet.rules.builtins;

import org.mindswap.pellet.Literal;

/**
 * <p>
 * Title: Binary Test
 * </p>
 * <p>
 * Description: A test between two literals
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
public abstract class BinaryTester implements Tester {

	public boolean test(Literal[] args) {
		if ( args.length == 2 ) {
			return test( args[0], args[1] );
		}
		return false;
	}
	
	/**
	 * Overriden to provide test functionality
	 */
	protected abstract boolean test( Literal l1, Literal l2 );

}
