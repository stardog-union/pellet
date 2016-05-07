// Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package org.mindswap.pellet.tableau.completion.incremental;

import aterm.ATermAppl;

/**
 * A _type dependency.
 *
 * @author Christian Halaschek-Wiener
 */
public class TypeDependency implements Dependency
{

	/**
	 * The _type
	 */
	private final ATermAppl _type;

	/**
	 * The individual
	 */
	private final ATermAppl _ind;

	/**
	 * Constructor
	 *
	 * @param _ind
	 * @param _type
	 */
	public TypeDependency(final ATermAppl ind, final ATermAppl type)
	{
		this._type = type;
		this._ind = ind;
	}

	/**
	 * Get the individual
	 *
	 * @return
	 */
	public ATermAppl getInd()
	{
		return _ind;
	}

	/**
	 * Get the _type
	 *
	 * @return
	 */
	public ATermAppl getType()
	{
		return _type;
	}

	/**
	 * ToString method
	 */
	@Override
	public String toString()
	{
		return "Type [" + _ind + "]  - [" + _type + "]";
	}

	/**
	 * Equals method
	 */
	@Override
	public boolean equals(final Object other)
	{
		if (other instanceof TypeDependency)
			return this._ind.equals(((TypeDependency) other)._ind) && this._type.equals(((TypeDependency) other)._type);
		else
			return false;
	}

	/**
	 * Hashcode method
	 */
	@Override
	public int hashCode()
	{
		return this._ind.hashCode() + this._type.hashCode();
	}

}
