// Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public
// License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of
// proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package com.clarkparsia.pellet.sparqldl.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import openllet.aterm.ATermAppl;

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
public class UnionQueryAtom implements QueryAtom
{
	private final List<List<QueryAtom>> _union;
	private boolean _isGround;
	private final List<ATermAppl> _args;

	public UnionQueryAtom(final List<QueryAtom> atoms1, final List<QueryAtom> atoms2)
	{
		this(Arrays.asList(atoms1, atoms2));
	}

	public UnionQueryAtom(final List<List<QueryAtom>> union)
	{
		if (union.isEmpty())
			throw new IllegalArgumentException("Empty collection of atoms not allowed in NotKnown atom");

		this._union = Collections.unmodifiableList(union);

		_isGround = true;
		_args = new ArrayList<>();
		for (final List<QueryAtom> atoms : union)
			for (final QueryAtom atom : atoms)
			{
				_args.addAll(atom.getArguments());
				if (_isGround && !atom.isGround())
					_isGround = false;
			}
	}

	@Override
	public QueryAtom apply(final ResultBinding binding)
	{
		final List<List<QueryAtom>> newUnion = new ArrayList<>();
		for (final List<QueryAtom> atoms : _union)
		{
			final List<QueryAtom> newAtoms = new ArrayList<>();
			for (final QueryAtom atom : atoms)
				newAtoms.add(atom.apply(binding));
			newUnion.add(newAtoms);
		}

		return new UnionQueryAtom(newUnion);
	}

	@Override
	public boolean equals(final Object obj)
	{
		if (!(obj instanceof UnionQueryAtom))
			return false;

		return _union.equals(((UnionQueryAtom) obj)._union);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<ATermAppl> getArguments()
	{
		return _args;
	}

	public List<List<QueryAtom>> getUnion()
	{
		return _union;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public QueryPredicate getPredicate()
	{
		return QueryPredicate.Union;
	}

	@Override
	public int hashCode()
	{
		return 31 * _union.hashCode();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isGround()
	{
		return _isGround;
	}

	@Override
	public String toString()
	{
		return "Union" + _union;
	}
}
