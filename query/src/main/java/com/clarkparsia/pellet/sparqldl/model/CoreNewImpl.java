// Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public
// License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of
// proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package com.clarkparsia.pellet.sparqldl.model;

import com.clarkparsia.pellet.sparqldl.model.Query.VarType;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import openllet.aterm.ATermAppl;
import org.mindswap.pellet.KnowledgeBase;
import org.mindswap.pellet.utils.ATermUtils;

/**
 * <p>
 * Title: Implementation of the Core of undistinguished variables.
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
public class CoreNewImpl implements Core
{

	private final QueryAtom _atom;

	private final Query _query;

	public CoreNewImpl(final Collection<QueryAtom> atoms, final Collection<ATermAppl> uv, final KnowledgeBase kb)
	{
		_query = new QueryImpl(kb, false);

		final List<ATermAppl> signature = new ArrayList<>();

		for (final QueryAtom atom : atoms)
		{
			_query.add(atom);

			// this is nasty - remodeling will be fine
			switch (atom.getPredicate())
			{
				case PropertyValue:
					final ATermAppl a1 = atom.getArguments().get(1);
					addI(atom.getArguments().get(0), signature, uv);
					addI(atom.getArguments().get(2), signature, uv);
					if (ATermUtils.isVar(a1))
						if (!uv.contains(a1))
							_query.addDistVar(a1, VarType.PROPERTY);
					break;
				case Type:
					final ATermAppl aa1 = atom.getArguments().get(1);
					addI(atom.getArguments().get(0), signature, uv);
					if (ATermUtils.isVar(aa1))
						if (!uv.contains(aa1))
							_query.addDistVar(aa1, VarType.CLASS);
					break;
				default:
					throw new IllegalArgumentException("Atom type " + atom.getPredicate() + " is not supported in a core.");
			}
		}

		_atom = new QueryAtomImpl(QueryPredicate.UndistVarCore, signature);
	}

	private CoreNewImpl(final Query query, final QueryAtom atom)
	{
		this._atom = atom;
		this._query = query;
	}

	private void addI(final ATermAppl aa0, final List<ATermAppl> signature, final Collection<ATermAppl> uv)
	{
		if (ATermUtils.isVar(aa0))
		{
			if (!uv.contains(aa0))
			{
				_query.addDistVar(aa0, VarType.INDIVIDUAL);
				signature.add(aa0);
			}
		}
		else
			signature.add(aa0);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public CoreNewImpl apply(final ResultBinding binding)
	{
		return new CoreNewImpl(_query.apply(binding), _atom.apply(binding));
	}

	@Override
	public int hashCode()
	{
		return _atom.hashCode() + 7 * _query.hashCode();
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
		final CoreNewImpl other = (CoreNewImpl) obj;

		return _atom.equals(other._atom) && _query.equals(other._query);
	}

	@Override
	public Set<ATermAppl> getConstants()
	{
		return _query.getConstants();
	}

	@Override
	public Set<ATermAppl> getDistVars()
	{
		return _query.getDistVars();
	}

	@Override
	public Set<ATermAppl> getUndistVars()
	{
		return _query.getUndistVars();
	}

	@Override
	public List<ATermAppl> getArguments()
	{
		return _atom.getArguments();
	}

	@Override
	public QueryPredicate getPredicate()
	{
		return _atom.getPredicate();
	}

	public Query getQuery()
	{
		return _query;
	}

	@Override
	public boolean isGround()
	{
		return _atom.isGround();
	}

	@Override
	public String toString()
	{
		return _atom.toString();
	}
}
