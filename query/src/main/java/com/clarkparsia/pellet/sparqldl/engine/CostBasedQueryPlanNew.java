// Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public
// License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of
// proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package com.clarkparsia.pellet.sparqldl.engine;

import aterm.ATermAppl;
import com.clarkparsia.pellet.sparqldl.model.Query;
import com.clarkparsia.pellet.sparqldl.model.QueryAtom;
import com.clarkparsia.pellet.sparqldl.model.QueryPredicate;
import com.clarkparsia.pellet.sparqldl.model.ResultBinding;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.katk.tools.Log;
import org.mindswap.pellet.exceptions.UnsupportedQueryException;
import org.mindswap.pellet.utils.ATermUtils;

/**
 * <p>
 * Title: Query Plan the Uses Full Query Reordering.
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
public class CostBasedQueryPlanNew extends QueryPlan
{
	private static final Logger _logger = Log.getLogger(CostBasedQueryPlanNew.class);

	private List<QueryAtom> _sortedAtoms;

	private int _index;

	private int _size;

	private QueryCost _cost;

	public CostBasedQueryPlanNew(final Query query)
	{
		super(query);

		QuerySizeEstimator.computeSizeEstimate(query);

		_index = 0;
		_size = query.getAtoms().size();
		_cost = new QueryCost(query.getKB());
		_sortedAtoms = null;

		if (_size == 0)
			return;
		else
			if (_size == 1)
				_sortedAtoms = query.getAtoms();
			else
			{
				final double minCost = chooseOrdering(new ArrayList<>(query.getAtoms()), new ArrayList<QueryAtom>(_size), new HashSet<ATermAppl>(), false, Double.POSITIVE_INFINITY);

				if (_sortedAtoms == null)
					throw new UnsupportedQueryException("No safe ordering for query: " + query);

				if (_logger.isLoggable(Level.FINE))
					_logger.log(Level.FINE, "WINNER : Cost=" + minCost + " ,atoms=" + _sortedAtoms);
			}
	}

	/**
	 * Recursive function that will inspect all possible orderings for a list of query atoms and returns the _cost for the best ordering (min _cost) found. Best
	 * ordering is saved in the _sortedAtoms field. The ordering of atoms is created recursively where each step adds one more atom to the _current ordering.
	 * Current ordering is discarded if it is found to be non-optimal and we have already found an ordering which is not non-optimal. Non-optimal heuristic
	 * currently is defined as follows: For each atom at position i > 1 in the ordered list, there should be at least one atom at position j < i s.t. two atoms
	 * share at least one variable. This heuristics is defined to avoid even considering cartesian products, e.g. ClassAtom(?x, A), ClassAtom(?y,B),
	 * PropertyValueAtom(?x, p, ?y). For some queries, all orderings may be non-optimal, e.g. ClassAtom(?x,A), ClassAtom(?y, B).
	 *
	 * @param atoms Atoms that have not yet been added to the ordered list
	 * @param orderedAtoms Atoms that have been ordered so far
	 * @param boundVars Variables that have referenced by the atoms in the ordered list
	 * @param notOptimal Current ordered list is found to be non-optimal
	 * @param minCost Minimum _cost found so far
	 * @return Minimum _cost found from an ordering that has the given ordered list as the prefix
	 */
	private double chooseOrdering(final List<QueryAtom> atoms, final List<QueryAtom> orderedAtoms, final Set<ATermAppl> boundVars, final boolean notOptimal, double minCostParam)
	{
		double minCost = minCostParam;
		if (atoms.isEmpty())
		{
			if (notOptimal)
			{
				if (_sortedAtoms == null)
					_sortedAtoms = new ArrayList<>(orderedAtoms);
			}
			else
			{
				final double queryCost = _cost.estimate(orderedAtoms);
				_logger.fine("Cost " + queryCost + " for " + orderedAtoms);
				if (queryCost < minCost)
				{
					_sortedAtoms = new ArrayList<>(orderedAtoms);
					minCost = queryCost;
				}
			}

			return minCost;
		}

		LOOP: for (int i = 0; i < atoms.size(); i++)
		{
			final QueryAtom atom = atoms.get(i);

			boolean newNonOptimal = notOptimal;
			final Set<ATermAppl> newBoundVars = new HashSet<>(boundVars);
			// TODO reorder UV atoms after all class and property variables are
			// bound.

			if (!atom.isGround())
			{
				int boundCount = 0;
				int unboundCount = 0;

				for (final ATermAppl a : atom.getArguments())
					if (ATermUtils.isVar(a))
						if (newBoundVars.add(a))
						{
							unboundCount++;

							/*
							 * It is not valid to have an ordering like
							 * NotKnown(ClassAtom(?x, A)), ClassAtom(?x, B).
							 * This is because variables in negation atom will
							 * not be bound by the query evaluation. However, if
							 * an atom in the query later binds the variable to
							 * a value the result will be incorrect because
							 * earlier evaluation of negation was not evaluated
							 * with that binding.
							 */
							if (atom.getPredicate().equals(QueryPredicate.NotKnown))
								for (int j = 0; j < atoms.size(); j++)
								{
									final QueryAtom nextAtom = atoms.get(j);

									if (i == j || nextAtom.getPredicate().equals(QueryPredicate.NotKnown))
										continue;

									if (nextAtom.getArguments().contains(a))
									{
										if (_logger.isLoggable(Level.FINE))
											_logger.fine("Unbound vars for not");
										continue LOOP;
									}
								}
						}
						else
							boundCount++;

				if (boundCount == 0 && newBoundVars.size() > unboundCount)
					if (_sortedAtoms != null)
					{
						if (_logger.isLoggable(Level.FINE))
							_logger.fine("Stop at not optimal ordering");
						continue;
					}
					else
					{
						if (_logger.isLoggable(Level.FINE))

							_logger.fine("Continue not optimal ordering, no solution yet.");
						newNonOptimal = true;
					}
			}

			atoms.remove(atom);
			orderedAtoms.add(atom);

			if (_logger.isLoggable(Level.FINE))
				_logger.fine("Atom[" + i + "/" + atoms.size() + "] " + atom + " from " + atoms + " to " + orderedAtoms);

			minCost = chooseOrdering(atoms, orderedAtoms, newBoundVars, newNonOptimal, minCost);

			atoms.add(i, atom);
			orderedAtoms.remove(orderedAtoms.size() - 1);
		}

		return minCost;
	}

	@Override
	public QueryAtom next(final ResultBinding binding)
	{
		return _sortedAtoms.get(_index++).apply(binding);
	}

	@Override
	public boolean hasNext()
	{
		return _index < _size;
	}

	@Override
	public void back()
	{
		_index--;
	}

	@Override
	public void reset()
	{
		_index = 0;
	}
}
