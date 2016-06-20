// Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package org.mindswap.pellet.tableau.completion.incremental;

import openllet.aterm.ATermAppl;

/**
 * A dependency for a _node merge
 *
 * @author Christian Halaschek-Wiener
 */
public class MergeDependency implements Dependency
{

	/**
	 * The _individual that _ind is merged to
	 */
	private final ATermAppl _mergedIntoInd;

	/**
	 * The _individual that is merged into _mergedIntoInd
	 */
	private final ATermAppl _ind;

	/**
	 * Constructor
	 * 
	 * @param _ind
	 * @param _mergedIntoInd
	 */
	public MergeDependency(final ATermAppl ind, final ATermAppl mergedIntoInd)
	{
		this._mergedIntoInd = mergedIntoInd;
		this._ind = ind;
	}

	/**
	 * Get the _individual that is merged into the other
	 *
	 * @return
	 */
	public ATermAppl getInd()
	{
		return _ind;
	}

	/**
	 * Get the _individual that has _ind merged into it
	 *
	 * @return
	 */
	public ATermAppl getmergedIntoInd()
	{
		return _mergedIntoInd;
	}

	/**
	 * ToString method
	 */
	@Override
	public String toString()
	{
		return "Merge [" + _ind + "]  into [" + _mergedIntoInd + "]";
	}

	/**
	 * Equals method
	 */
	@Override
	public boolean equals(final Object other)
	{
		if (other instanceof MergeDependency)
			return this._ind.equals(((MergeDependency) other)._ind) && this._mergedIntoInd.equals(((MergeDependency) other)._mergedIntoInd);
		else
			return false;
	}

	/**
	 * Hashcode method TODO: this may not be sufficient
	 */
	@Override
	public int hashCode()
	{
		return this._ind.hashCode() + this._mergedIntoInd.hashCode();
	}

}
