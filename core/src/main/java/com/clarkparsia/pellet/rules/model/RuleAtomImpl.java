// Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package com.clarkparsia.pellet.rules.model;

import java.util.List;

/**
 * <p>
 * Title: Rule Atom Implementation
 * </p>
 * <p>
 * Description:
 * </p>
 * <p>
 * Copyright: Copyright (c) 2007
 * </p>
 * <p>
 * Company: Clark & Parsia, LLC. <http://www.clarkparsia.com>
 * </p>
 *
 * @author Ron Alford
 */

public abstract class RuleAtomImpl<P> implements RuleAtom
{

	P predicate;

	public RuleAtomImpl(final P predicate)
	{
		this.predicate = predicate;
	}

	@Override
	public abstract void accept(RuleAtomVisitor visitor);

	@Override
	public boolean equals(final Object other)
	{
		if (other != null && getClass().equals(other.getClass()))
		{
			final RuleAtom atom = (RuleAtom) other;
			return getPredicate().equals(atom.getPredicate()) && getAllArguments().equals(atom.getAllArguments());
		}
		return false;
	}

	@Override
	public abstract List<? extends AtomObject> getAllArguments();

	@Override
	public P getPredicate()
	{
		return predicate;
	}

	@Override
	public int hashCode()
	{
		return predicate.hashCode() + getAllArguments().hashCode();
	}
}
