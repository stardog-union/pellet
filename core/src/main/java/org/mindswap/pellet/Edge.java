// Portions Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// Clark & Parsia, LLC parts of this source code are available under the terms of the Affero General Public License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com
 
package org.mindswap.pellet;

import aterm.ATermAppl;

/**
 * <p>Title: </p>
 *
 * <p>Description: Represents an edge in the tableau completion graph.</p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: Clark & Parsia, LLC. <http://www.clarkparsia.com></p>
 *
 * @author Evren Sirin
 */
public interface Edge {
	/**
	 * Given a node upon which this edge is incident, the opposite incident node is returned.
	 * 
	 * @param node a node upon which this edge is incident
	 * @return the other node this edge is incident upon
	 */
	public abstract Node getNeighbor(Node node);

	/**
	 * @return Returns the depends.
	 */
	public abstract DependencySet getDepends();
	
	public abstract void setDepends(DependencySet ds);

	/**
	 * @return Returns the source of this edge
	 */
	public abstract Individual getFrom();
	
	/**
	 * @return Returns the name of the source node
	 */
	public abstract ATermAppl getFromName();

	/**
	 * @return Returns the role.
	 */
	public abstract Role getRole();

	/**
	 * @return Returns the target of the edge
	 */
	public abstract Node getTo();

	/**
	 * @return Returns the name of the target node
	 */
	public abstract ATermAppl getToName();
}