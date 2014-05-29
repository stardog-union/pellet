// Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package org.mindswap.pellet.tableau.blocking;

import org.mindswap.pellet.utils.SetUtils;

/**
 * @author Evren Sirin
 */
public class Block1 implements BlockingCondition {
	public boolean isBlocked(BlockingContext cxt) {
		return SetUtils.subset(cxt.blocked.getTypes(), cxt.blocker.getTypes());	
	}
}
