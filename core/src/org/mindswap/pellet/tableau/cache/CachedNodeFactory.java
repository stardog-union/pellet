// Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package org.mindswap.pellet.tableau.cache;

import org.mindswap.pellet.Individual;

import aterm.ATermAppl;

/**
 * <p>Title: </p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: Clark & Parsia, LLC. <http://www.clarkparsia.com></p>
 *
 * @author Evren Sirin
 */
public class CachedNodeFactory {
	public static CachedNode createTopNode() {
		return CachedConstantNode.TOP;
	}

	public static CachedNode createBottomNode() {
		return CachedConstantNode.BOTTOM;
	}

	public static CachedNode createSatisfiableNode() {
		return CachedConstantNode.INCOMPLETE;
	}

	public static CachedNode createNode(ATermAppl name, Individual node) {
		return new CachedConceptNode( name, node );
	}
}
