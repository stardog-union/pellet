// Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package org.mindswap.pellet.tableau.cache;

import aterm.ATermAppl;
import java.util.Map;
import org.mindswap.pellet.DependencySet;
import org.mindswap.pellet.EdgeList;
import org.mindswap.pellet.Role;

/**
 * <p>
 * Title:
 * </p>
 * <p>
 * Description: Represents a cached _node that is used in different KBs.
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
public class CachedConstantNode implements CachedNode
{
	private enum CachedNodeType
	{
		TOP, BOTTOM, INCOMPLETE
	}

	public static final CachedNode _TOP = new CachedConstantNode(CachedNodeType.TOP);
	public static final CachedNode _BOTTOM = new CachedConstantNode(CachedNodeType.BOTTOM);
	public static final CachedNode _INCOMPLETE = new CachedConstantNode(CachedNodeType.INCOMPLETE);

	private final CachedNodeType _type;

	private CachedConstantNode(final CachedNodeType type)
	{
		this._type = type;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isIndependent()
	{
		return true;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public EdgeList getInEdges()
	{
		throw new UnsupportedOperationException();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public EdgeList getOutEdges()
	{
		throw new UnsupportedOperationException();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Map<ATermAppl, DependencySet> getDepends()
	{
		throw new UnsupportedOperationException();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean hasRNeighbor(final Role role)
	{
		throw new UnsupportedOperationException();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isNamedIndividual()
	{
		throw new UnsupportedOperationException();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isBottom()
	{
		return _type == CachedNodeType.BOTTOM;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isComplete()
	{
		return _type != CachedNodeType.INCOMPLETE;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isTop()
	{
		return _type == CachedNodeType.TOP;
	}

	@Override
	public String toString()
	{
		return "Cached." + _type;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ATermAppl getName()
	{
		return null;
	}
}
