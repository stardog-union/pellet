// Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package com.clarkparsia.pellet.sparqldl.engine;

import com.clarkparsia.pellet.sparqldl.model.Query;
import com.clarkparsia.pellet.sparqldl.model.Query.VarType;
import com.clarkparsia.pellet.sparqldl.model.QueryAtom;
import com.clarkparsia.pellet.sparqldl.model.QueryAtomFactory;
import com.clarkparsia.pellet.sparqldl.model.QueryImpl;
import com.clarkparsia.pellet.sparqldl.model.QueryPredicate;
import com.clarkparsia.pellet.sparqldl.model.QueryResult;
import com.clarkparsia.pellet.sparqldl.model.QueryResultImpl;
import com.clarkparsia.pellet.sparqldl.model.ResultBinding;
import com.clarkparsia.pellet.sparqldl.model.ResultBindingImpl;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import openllet.aterm.ATermAppl;
import openllet.shared.tools.Log;
import org.mindswap.pellet.KnowledgeBase;
import org.mindswap.pellet.utils.ATermUtils;

/**
 * <p>
 * Title: Abstract class for all purely ABox engines.
 * </p>
 * <p>
 * Description: All variable name spaces are disjoint.
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
public abstract class AbstractABoxEngineWrapper implements QueryExec
{
	public static final Logger _logger = Log.getLogger(QueryEngine.class);

	public static final QueryExec distCombinedQueryExec = new CombinedQueryEngine();

	protected Query schemaQuery;

	protected Query aboxQuery;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public QueryResult exec(final Query query)
	{
		if (_logger.isLoggable(Level.FINE))
			_logger.fine("Executing query " + query.getAtoms());

		partitionQuery(query);

		QueryResult newResult;

		boolean shouldHaveBinding;
		final QueryResult result;

		if (schemaQuery.getAtoms().isEmpty())
		{
			shouldHaveBinding = false;
			result = new QueryResultImpl(query);
			result.add(new ResultBindingImpl());
		}
		else
		{
			if (_logger.isLoggable(Level.FINE))
				_logger.fine("Executing TBox query: " + schemaQuery);
			result = distCombinedQueryExec.exec(schemaQuery);

			shouldHaveBinding = org.mindswap.pellet.utils.SetUtils.intersects(query.getDistVarsForType(VarType.CLASS), query.getResultVars()) || org.mindswap.pellet.utils.SetUtils.intersects(query.getDistVarsForType(VarType.PROPERTY), query.getResultVars());
		}
		if (shouldHaveBinding && result.isEmpty())
			return result;

		if (_logger.isLoggable(Level.FINE))
			_logger.fine("Partial _binding after schema query : " + result);

		if (aboxQuery.getAtoms().size() > 0)
		{
			newResult = new QueryResultImpl(query);
			for (final ResultBinding binding : result)
			{
				final Query query2 = aboxQuery.apply(binding);

				if (_logger.isLoggable(Level.FINE))
					_logger.fine("Executing ABox query: " + query2);
				final QueryResult aboxResult = execABoxQuery(query2);

				for (final ResultBinding newBinding : aboxResult)
				{
					for (final ATermAppl var : binding.getAllVariables())
						newBinding.setValue(var, binding.getValue(var));

					newResult.add(newBinding);
				}
			}
		}
		else
		{
			newResult = result;
			if (_logger.isLoggable(Level.FINER))
				_logger.finer("ABox query empty ... returning.");
		}
		return newResult;
	}

	private final void partitionQuery(final Query query)
	{

		schemaQuery = new QueryImpl(query);
		aboxQuery = new QueryImpl(query);

		for (final QueryAtom atom : query.getAtoms())
			switch (atom.getPredicate())
			{
				case Type:
				case PropertyValue:
					//			case SameAs:
					//			case DifferentFrom:
					aboxQuery.add(atom);
					break;
				default:
			}

		final List<QueryAtom> atoms = new ArrayList<>(query.getAtoms());
		atoms.removeAll(aboxQuery.getAtoms());

		for (final QueryAtom atom : atoms)
			schemaQuery.add(atom);

		for (final VarType t : VarType.values())
			for (final ATermAppl a : query.getDistVarsForType(t))
			{
				if (aboxQuery.getVars().contains(a))
					aboxQuery.addDistVar(a, t);
				if (schemaQuery.getVars().contains(a))
					schemaQuery.addDistVar(a, t);
			}

		for (final ATermAppl a : query.getResultVars())
		{
			if (aboxQuery.getVars().contains(a))
				aboxQuery.addResultVar(a);
			if (schemaQuery.getVars().contains(a))
				schemaQuery.addResultVar(a);
		}

		for (final ATermAppl v : aboxQuery.getDistVarsForType(VarType.CLASS))
			if (!schemaQuery.getVars().contains(v))
				schemaQuery.add(QueryAtomFactory.SubClassOfAtom(v, ATermUtils.TOP));

		for (final ATermAppl v : aboxQuery.getDistVarsForType(VarType.PROPERTY))
			if (!schemaQuery.getVars().contains(v))
				schemaQuery.add(QueryAtomFactory.SubPropertyOfAtom(v, v));

	}

	protected abstract QueryResult execABoxQuery(final Query q);
}

class BindingIterator implements Iterator<ResultBinding>
{
	private final List<List<ATermAppl>> varB = new ArrayList<>();

	private final List<ATermAppl> vars = new ArrayList<>();

	private final int[] indices;

	private boolean more = true;

	public BindingIterator(final Map<ATermAppl, Set<ATermAppl>> bindings)
	{
		vars.addAll(bindings.keySet());

		for (final ATermAppl var : vars)
		{
			final Set<ATermAppl> values = bindings.get(var);
			if (values.isEmpty())
			{
				more = false;
				break;
			}
			else
				varB.add(new ArrayList<>(values));
		}

		indices = new int[vars.size()];
	}

	private boolean incIndex(final int index)
	{
		if (indices[index] + 1 < varB.get(index).size())
			indices[index]++;
		else
			if (index == indices.length - 1)
				return false;
			else
			{
				indices[index] = 0;
				return incIndex(index + 1);
			}

		return true;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean hasNext()
	{
		return more;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ResultBinding next()
	{
		if (!more)
			return null;

		final ResultBinding next = new ResultBindingImpl();

		for (int i = 0; i < indices.length; i++)
			next.setValue(vars.get(i), varB.get(i).get(indices[i]));

		more = incIndex(0);

		return next;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void remove()
	{
		throw new UnsupportedOperationException("Removal from this iterator is not supported.");
	}
}

class LiteralIterator implements Iterator<ResultBinding>
{
	private final int[] _indices;

	private final ResultBinding _binding;

	private final Set<ATermAppl> _litVars;

	private final List<List<ATermAppl>> _litVarBindings = new ArrayList<>();

	private boolean _more = true;

	public LiteralIterator(final Query q, final ResultBinding binding)
	{
		final KnowledgeBase kb = q.getKB();
		this._binding = binding;
		this._litVars = q.getDistVarsForType(VarType.LITERAL);

		_indices = new int[_litVars.size()];
		int index = 0;
		for (final ATermAppl litVar : _litVars)
		{
			// final Datatype dtype = ;// q.getDatatype(litVar); TODO after
			// recognizing Datatypes and adjusting Query model supply the
			// corresponding literal.

			final List<ATermAppl> foundLiterals = new ArrayList<>();
			boolean first = true;

			for (final QueryAtom atom : q.findAtoms(QueryPredicate.PropertyValue, null, null, litVar))
			{

				ATermAppl subject = atom.getArguments().get(0);
				final ATermAppl predicate = atom.getArguments().get(1);

				if (ATermUtils.isVar(subject))
					subject = binding.getValue(subject);

				_litVarBindings.add(index, new ArrayList<ATermAppl>());

				final List<ATermAppl> act = kb.getDataPropertyValues(predicate, subject); // dtype);

				if (first)
					foundLiterals.addAll(act);
				else
				{
					foundLiterals.retainAll(act);
					first = false;
				}
			}

			if (foundLiterals.size() > 0)
				_litVarBindings.get(index++).addAll(foundLiterals);
			else
				_more = false;
		}
	}

	private boolean incIndex(final int index)
	{
		if (_indices[index] + 1 < _litVarBindings.get(index).size())
			_indices[index]++;
		else
			if (index == _indices.length - 1)
				return false;
			else
			{
				_indices[index] = 0;
				return incIndex(index + 1);
			}

		return true;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void remove()
	{
		throw new UnsupportedOperationException("Removal from this iterator is not supported.");
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean hasNext()
	{
		return _more;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ResultBinding next()
	{
		if (!_more)
			return null;

		final ResultBinding next = _binding.duplicate();

		int index = 0;
		for (final ATermAppl o1 : _litVars)
		{
			final ATermAppl o2 = _litVarBindings.get(index).get(_indices[index++]);
			next.setValue(o1, o2);
		}

		_more = incIndex(0);

		return next;
	}
}
