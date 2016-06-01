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
import java.util.Arrays;
import java.util.List;
import org.mindswap.pellet.utils.ATermUtils;

/**
 * <p>
 * Title: Abstract implementation of the query atom.
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
 * @author Petr Kremen
 */
public class QueryAtomImpl implements QueryAtom
{

	protected final QueryPredicate _predicate;

	protected final List<ATermAppl> _arguments;

	protected boolean _ground;

	public QueryAtomImpl(final QueryPredicate predicate, final ATermAppl... arguments)
	{
		this(predicate, Arrays.asList(arguments));
	}

	public QueryAtomImpl(final QueryPredicate predicate, final List<ATermAppl> arguments)
	{
		if (predicate == null)
			throw new RuntimeException("Predicate cannot be null.");

		this._predicate = predicate;
		this._arguments = arguments;
		// this.vars = new HashSet<ATermAppl>();
		//
		_ground = true;
		for (final ATermAppl a : arguments)
			if (ATermUtils.isVar(a))
			{
				_ground = false;
				// vars.add(a);
				break;
			}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public QueryPredicate getPredicate()
	{
		return _predicate;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<ATermAppl> getArguments()
	{
		return _arguments;
	}

	// /**
	// * {@inheritDoc}
	// */
	// public Set<ATermAppl> getVariables() {
	// return vars;
	// }
	//
	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isGround()
	{
		return _ground;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public QueryAtom apply(final ResultBinding binding)
	{
		if (isGround())
			return this;

		final List<ATermAppl> newArguments = new ArrayList<>();

		for (final ATermAppl a : _arguments)
			if (binding.isBound(a))
				newArguments.add(binding.getValue(a));
			else
				newArguments.add(a);

		return newArguments.isEmpty() ? this : new QueryAtomImpl(_predicate, newArguments);
	}

	@Override
	public int hashCode()
	{
		return 31 * _predicate.hashCode() + _arguments.hashCode();
	}

	@Override
	public boolean equals(final Object obj)
	{
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		final QueryAtomImpl other = (QueryAtomImpl) obj;

		return _predicate.equals(other._predicate) && _arguments.equals(other._arguments);
	}

	@Override
	public String toString()
	{
		final StringBuffer sb = new StringBuffer();

		for (int i = 0; i < _arguments.size(); i++)
		{
			final ATermAppl a = _arguments.get(i);
			if (i > 0)
				sb.append(", ");

			sb.append(ATermUtils.toString(a));
		}

		return _predicate + "(" + sb.toString() + ")";
	}
}
