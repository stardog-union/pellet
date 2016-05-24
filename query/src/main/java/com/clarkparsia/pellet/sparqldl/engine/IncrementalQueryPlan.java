// Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public
// License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of
// proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package com.clarkparsia.pellet.sparqldl.engine;

import com.clarkparsia.pellet.sparqldl.model.Query;
import com.clarkparsia.pellet.sparqldl.model.QueryAtom;
import com.clarkparsia.pellet.sparqldl.model.QueryPredicate;
import com.clarkparsia.pellet.sparqldl.model.ResultBinding;
import java.util.List;
import java.util.Stack;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.katk.tools.Log;
import org.mindswap.pellet.exceptions.InternalReasonerException;
import org.mindswap.pellet.utils.SetUtils;

/**
 * <p>
 * Title: Query Plan that recomputes the _cost of the query in a greedy way.
 * </p>
 * <p>
 * Description: TODO _cache costs - not to recompute them
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
public class IncrementalQueryPlan extends QueryPlan
{

	private static final Logger _logger = Log.getLogger(IncrementalQueryPlan.class);

	public final Stack<Integer> _explored;

	private final List<QueryAtom> _atoms;

	private final int _size;

	private final QueryCost _cost;

	public IncrementalQueryPlan(final Query query)
	{
		super(query);

		QuerySizeEstimator.computeSizeEstimate(query);

		_explored = new Stack<>();

		_atoms = query.getAtoms();

		_size = _atoms.size();

		_cost = new QueryCost(query.getKB());

		reset();
	}

	@Override
	public QueryAtom next(final ResultBinding binding)
	{
		int best = -1;
		QueryAtom bestAtom = null;
		double bestCost = Double.POSITIVE_INFINITY;

		LOOP: for (int i = 0; i < _size; i++)
			if (!_explored.contains(i))
			{
				final QueryAtom atom = _atoms.get(i);
				final QueryAtom atom2 = atom.apply(binding);

				if (atom2.getPredicate().equals(QueryPredicate.NotKnown) && !atom2.isGround())
					for (int j = 0; j < _atoms.size(); j++)
					{
						if (i == j || _explored.contains(j))
							continue;

						final QueryAtom nextAtom = _atoms.get(j);
						if (SetUtils.intersects(nextAtom.getArguments(), atom2.getArguments()))
						{
							if (_logger.isLoggable(Level.FINE))
								_logger.fine("Unbound vars for not");
							continue LOOP;
						}
					}

				final double atomCost = _cost.estimate(atom2);

				if (_logger.isLoggable(Level.FINER))
					_logger.finer("Atom=" + atom + ", _cost=" + _cost + ", best _cost=" + bestCost);
				if (atomCost <= bestCost)
				{
					bestCost = atomCost;
					bestAtom = atom2;
					best = i;
				}
			}

		if (best == -1)
			throw new InternalReasonerException("Cannot find a valid atom in " + _atoms + " where _explored=" + _explored);

		_explored.add(best);

		if (_logger.isLoggable(Level.FINER))
		{
			final StringBuffer indent = new StringBuffer();
			for (int j = 0; j < _explored.size(); j++)
				indent.append(" ");
			final String treePrint = indent.toString() + bestAtom + " : " + bestCost;

			_logger.finer(treePrint);
		}

		return bestAtom;
	}

	@Override
	public boolean hasNext()
	{
		return _explored.size() < _size;
	}

	@Override
	public void back()
	{
		_explored.pop();
	}

	@Override
	public void reset()
	{
		_explored.clear();
	}
}
