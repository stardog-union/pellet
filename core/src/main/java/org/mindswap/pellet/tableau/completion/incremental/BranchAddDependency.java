// Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package org.mindswap.pellet.tableau.completion.incremental;

import aterm.ATermAppl;
import org.mindswap.pellet.tableau.branch.Branch;

/**
 * Dependency structure for when a _branch is added.
 *
 * @author Christian Halaschek-Wiener
 */
public class BranchAddDependency extends BranchDependency
{

	/**
	 * The actual _branch
	 */
	private final Branch _branch;

	/**
	 * Constructor
	 * 
	 * @param _index
	 * @param _branch
	 */
	public BranchAddDependency(final ATermAppl assertion, @SuppressWarnings("unused") final int index, final Branch branch)
	{
		super(assertion);
		this._branch = branch;
	}

	/**
	 * Get _branch
	 * 
	 * @return
	 */
	public Branch getBranch()
	{
		return _branch;
	}

	/**
	 * ToString method
	 */
	@Override
	public String toString()
	{
		return "Branch  - [" + _branch + "]";
	}

	/**
	 * Equals method
	 */
	@Override
	public boolean equals(final Object other)
	{
		if (other instanceof BranchAddDependency)
			return (this._branch.getBranch() == ((BranchAddDependency) other)._branch.getBranch()) && this._assertion.equals(((BranchAddDependency) other)._assertion);
		else
			return false;
	}

	/**
	 * Hashcode method
	 */
	@Override
	public int hashCode()
	{
		return this._branch.getBranch() + this._assertion.hashCode();
	}

}
