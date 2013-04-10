// Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package org.mindswap.pellet.tableau.cache;

import java.util.Map;

import org.mindswap.pellet.DependencySet;
import org.mindswap.pellet.EdgeList;
import org.mindswap.pellet.Role;

import aterm.ATermAppl;

/**
 * <p>
 * Title:
 * </p>
 * <p>
 * Description: Represents a cached node that is used in different KBs.
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
public class CachedConstantNode implements CachedNode {
	public static final CachedNode	TOP			= new CachedConstantNode( CachedNodeType.TOP );
	public static final CachedNode	BOTTOM		= new CachedConstantNode( CachedNodeType.BOTTOM );
	public static final CachedNode	INCOMPLETE	= new CachedConstantNode( CachedNodeType.INCOMPLETE );

	private enum CachedNodeType {
		TOP, BOTTOM, INCOMPLETE
	}

	private CachedNodeType	type;

	private CachedConstantNode(CachedNodeType type) {
		this.type = type;
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean isIndependent() {
		return true;
	}

	/**
	 * {@inheritDoc}
	 */
	public EdgeList getInEdges() {
		throw new UnsupportedOperationException();
	}

	/**
	 * {@inheritDoc}
	 */
	public EdgeList getOutEdges() {
		throw new UnsupportedOperationException();
	}

	/**
	 * {@inheritDoc}
	 */
	public Map<ATermAppl, DependencySet> getDepends() {
		throw new UnsupportedOperationException();
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean hasRNeighbor(Role role) {
		throw new UnsupportedOperationException();
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean isNamedIndividual() {
		throw new UnsupportedOperationException();
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean isBottom() {
		return type == CachedNodeType.BOTTOM;
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean isComplete() {
		return type != CachedNodeType.INCOMPLETE;
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean isTop() {
		return type == CachedNodeType.TOP;
	}

	public String toString() {
		return "Cached." + type;
	}

	/**
	 * {@inheritDoc}
	 */
	public ATermAppl getName() {
		return null;
	}
}
