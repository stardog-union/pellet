// Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public
// License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of
// proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package com.clarkparsia.pellet.sparqldl.model;

import aterm.ATermAppl;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * <p>
 * Title:
 * </p>
 * <p>
 * Description:
 * </p>
 * <p>
 * Copyright: Copyright (c) 2009
 * </p>
 * <p>
 * Company: Clark & Parsia, LLC. <http://www.clarkparsia.com>
 * </p>
 *
 * @author Evren Sirin
 */
public class NotKnownQueryAtom implements QueryAtom
{
	private final List<QueryAtom> atoms;
	private boolean isGround;
	private final List<ATermAppl> args;

	public NotKnownQueryAtom(final QueryAtom atom)
	{
		this(Collections.singletonList(atom));
	}

	public NotKnownQueryAtom(final List<QueryAtom> atoms)
	{
		this.atoms = Collections.unmodifiableList(atoms);

		isGround = true;
		args = new ArrayList<>();
		for (final QueryAtom atom : atoms)
		{
			args.addAll(atom.getArguments());
			if (isGround && !atom.isGround())
				isGround = false;
		}
	}

	@Override
	public QueryAtom apply(final ResultBinding binding)
	{
		List<QueryAtom> newAtoms;
		if (atoms.size() == 1)
			newAtoms = Collections.singletonList(atoms.get(0).apply(binding));
		else
		{
			newAtoms = new ArrayList<>();
			for (final QueryAtom atom : atoms)
				newAtoms.add(atom.apply(binding));
		}

		return new NotKnownQueryAtom(newAtoms);
	}

	@Override
	public boolean equals(final Object obj)
	{
		if (!(obj instanceof NotKnownQueryAtom))
			return false;

		return atoms.equals(((NotKnownQueryAtom) obj).atoms);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<ATermAppl> getArguments()
	{
		return args;
	}

	public List<QueryAtom> getAtoms()
	{
		return atoms;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public QueryPredicate getPredicate()
	{
		return QueryPredicate.NotKnown;
	}

	@Override
	public int hashCode()
	{
		return 17 * atoms.hashCode();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isGround()
	{
		return isGround;
	}

	@Override
	public String toString()
	{
		return "NotKnown" + atoms;
	}
}
