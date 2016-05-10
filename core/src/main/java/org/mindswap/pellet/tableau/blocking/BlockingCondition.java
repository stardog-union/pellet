// Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public
// License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of
// proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package org.mindswap.pellet.tableau.blocking;

/**
 * A simple interface to check if the _blocker candidate in this context actually blocks the other _individual.
 *
 * @author Evren Sirin
 */
public interface BlockingCondition
{
	/**
	 * Check if the _blocker candidate in this context actually blocks the other _individual.
	 *
	 * @param cxt the _current blocking context
	 * @return <code>true</code> if the _individual is _blocked
	 */
	public boolean isBlocked(BlockingContext cxt);
}
