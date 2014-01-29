// Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package org.mindswap.pellet.tableau.cache;

import org.mindswap.pellet.DependencySet;
import org.mindswap.pellet.Edge;
import org.mindswap.pellet.Role;

import aterm.ATermAppl;

/**
 * <p>Title: </p>
 *
 * <p>Description: Represents a cached outgoing edge.</p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: Clark & Parsia, LLC. <http://www.clarkparsia.com></p>
 *
 * @author Evren Sirin
 */
public class CachedOutEdge extends CachedEdge {
	public CachedOutEdge(Edge edge) {
		super( edge.getRole(), edge.getToName(), edge.getDepends() );
	}

	public CachedOutEdge(Role role, ATermAppl to, DependencySet ds) {
		super( role, to, ds );
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public ATermAppl getToName() {
		return neighbor;
	}

}
