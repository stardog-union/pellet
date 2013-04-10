// Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package org.mindswap.pellet.tableau.cache;

import java.util.Map;

import org.mindswap.pellet.DependencySet;
import org.mindswap.pellet.EdgeList;
import org.mindswap.pellet.Individual;
import org.mindswap.pellet.Role;

import aterm.ATermAppl;

/**
 * <p>
 * Title:
 * </p>
 * <p>
 * Description: Represent the cached information for a concept or an individual.
 * For concepts this represents the root node of the tableau completion graph
 * built to check the satisfiability of the concept. For individuals, this is
 * the individual itself ({@link Individual} implements this interface}. The
 * cached node for concepts may be incomplete if the satisfiability status was
 * cached when the satisfiability of another concept was being computed.
 * Incomplete cached nodes will not have any information regarding types or
 * edges.
 * </p>
 * <p>
 * Copyright: Copyright (c) 2008
 * </p>
 * <p>
 * Company: Clark & Parsia, LLC. <http://www.clarkparsia.com>
 * </p>
 * 
 * @author Evren Sirin
 */
public interface CachedNode {
	/**
	 * Returns if this cached node is complete.
	 * 
	 * @return <code>true</code> if this cached node is complete
	 */
	public boolean isComplete();

	/**
	 * Returns if this is the cached node for BOTTOM concept.
	 * 
	 * @return <code>true</code> if this is the cached node for BOTTOM concept
	 */
	public boolean isTop();

	/**
	 * Returns if this is the cached node for TOP concept.
	 * 
	 * @return <code>true</code> if this is the cached node for TOP concept
	 */
	public boolean isBottom();

	/**
	 * Returns the types and their dependencies for this node.
	 * 
	 * @return a map from concepts to dependency sets
	 */
	public Map<ATermAppl, DependencySet> getDepends();

	/**
	 * Returns the outgoing edges of this node.
	 * 
	 * @return Outgoing edges of this node
	 */
	public EdgeList getOutEdges();

	/**
	 * Returns the incoming edges of this node.
	 * 
	 * @return Incoming edges of this node
	 */
	public EdgeList getInEdges();

	/**
	 * Checks if this node is connected to another node with the given role (or
	 * one of its subproperties). The node may have an incoming edge with the
	 * inverse of this role which would count as an r-neighbor.
	 * 
	 * @return Outgoing edges of this node
	 */
	public boolean hasRNeighbor(Role role);

	/**
	 * Returns the name of this node. For cached concept nodes this is the name
	 * of the concept.
	 * 
	 * @return Name of this node
	 */
	public ATermAppl getName();

	/**
	 * Returns if this node represent a named individual (not an anonymous individual 
	 * or a concept node)
	 * 
	 * @return If this node represent a named individual
	 */
	public boolean isNamedIndividual();

	/**
	 * Returns if this node was cached without any dependency to a
	 * non-deterministic branch. In the presence of nominals, when we are
	 * checking the satisfiability of a concept the root node may be merged to a
	 * nominal node and that merge may be due to a non-deterministic branch. In
	 * such cases the types and edges that are cached do not necessarily show
	 * types and edges that will exist in every clash-free tableau completion.
	 * 
	 * @return If this node was cached without any dependency to a
	 *         non-deterministic branch
	 */
	public boolean isIndependent();
}
