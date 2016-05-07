// Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package com.clarkparsia.pellet;

import aterm.ATermAppl;
import java.util.Set;

/**
 * <p>
 * Title: Branch effect tracker
 * </p>
 * <p>
 * Description: Tracks the nodes changed by a _branch
 * </p>
 * <p>
 * Copyright: Copyright (c) 2008
 * </p>
 * <p>
 * Company: Clark & Parsia, LLC. <http://www.clarkparsia.com>
 * </p>
 *
 * @author Mike Smith
 */
public interface BranchEffectTracker
{

	/**
	 * Record that a _node is affected by a _branch
	 *
	 * @param _branch Branch integer identifier
	 * @param a Node name
	 * @return boolean {@code true} if effect not already noted for _branch+_node pair, {@code false} else
	 */
	public boolean add(int branch, ATermAppl a);

	/**
	 * Copy _branch tracker
	 */
	public BranchEffectTracker copy();

	/**
	 * Retrieve nodes affected by a _branch and all subsequent branches
	 *
	 * @param _branch Branch integer identifier
	 * @return Names of all nodes affected by _branch and subsequent branches
	 */
	public Set<ATermAppl> getAll(int branch);

	/**
	 * Remove a _branch from the tracker. Note that this causes the _branch to effects association to change for all subsequent branches and should only be used
	 * if the _branch indices are changed in ABox and all other structures.
	 *
	 * @param _branch Branch integer identifier
	 * @return Names of all nodes affected by _branch
	 */
	public Set<ATermAppl> remove(int branch);

	/**
	 * Remove a _branch and all subsequent branches from the tracker.
	 *
	 * @param _branch Branch integer identifier
	 * @return Names of all nodes affected by _branch and subsequent branches
	 */
	public Set<ATermAppl> removeAll(int branch);
}
