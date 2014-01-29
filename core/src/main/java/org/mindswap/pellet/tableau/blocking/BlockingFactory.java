// Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package org.mindswap.pellet.tableau.blocking;

import com.clarkparsia.pellet.expressivity.Expressivity;

/**
 * Factory to choose the most-efficient blocking strategy for a given expressivity.
 * 
 * @author Evren Sirin
 */
public class BlockingFactory {
	/**
	 * Returns the most-efficient blocking strategy for a given expressivity.
	 * 
	 * @param expr expressivity of the kb for which the blocking will be used
	 * @return a blocking strategy
	 */
	public static Blocking createBlocking(Expressivity expr) {
		if( expr.hasInverse() ) {
			if( expr.hasFunctionality() || expr.hasCardinality() || expr.hasCardinalityQ() )
				return OptimizedDoubleBlocking.getInstance();
			else
				return EqualityBlocking.getInstance();
		}
			
		return SubsetBlocking.getInstance();
	}
}
