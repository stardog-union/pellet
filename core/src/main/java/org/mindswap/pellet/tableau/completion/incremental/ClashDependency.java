// Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package org.mindswap.pellet.tableau.completion.incremental;

import openllet.aterm.ATermAppl;
import org.mindswap.pellet.Clash;

/**
 * A _clash dependency.
 *
 * @author Christian Halaschek-Wiener
 */
public class ClashDependency implements Dependency
{

	/**
	 * The _assertion
	 */
	private final ATermAppl _assertion;

	/**
	 * The _clash
	 */
	private final Clash _clash;

	/**
	 * Constructor
	 * 
	 * @param _assertion
	 * @param _clash
	 */
	public ClashDependency(final ATermAppl assertion, final Clash clash)
	{
		this._assertion = assertion;
		this._clash = clash;
	}

	/**
	 * ToString method
	 */
	@Override
	public String toString()
	{
		return "Clash [" + _assertion + "]  - [" + _clash + "]";
	}

	/**
	 * Equals method
	 */
	@Override
	public boolean equals(final Object other)
	{
		if (other instanceof ClashDependency)
			return this._assertion.equals(((ClashDependency) other)._assertion) && this._clash.getNode().equals(((ClashDependency) other)._clash.getNode()) && this._clash.getType() == ((ClashDependency) other)._clash.getType() && this._clash.getDepends().equals(((ClashDependency) other)._clash.getDepends());
		else
			return false;
	}

	/**
	 * Hashcode method TODO: this may not be sufficient
	 */
	@Override
	public int hashCode()
	{
		return this._clash.getType().hashCode() + this._clash.getDepends().hashCode() + this._clash.getNode().hashCode() + this._assertion.hashCode();
	}

	/**
	 * Get the _assertion
	 * 
	 * @return
	 */
	protected ATermAppl getAssertion()
	{
		return _assertion;
	}

	/**
	 * Get the _clash
	 *
	 * @return
	 */
	public Clash getClash()
	{
		return _clash;
	}

}
