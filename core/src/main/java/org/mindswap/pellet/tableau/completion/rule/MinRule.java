// Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package org.mindswap.pellet.tableau.completion.rule;

import aterm.ATermAppl;
import aterm.ATermInt;
import java.util.List;
import java.util.logging.Level;
import org.mindswap.pellet.DependencySet;
import org.mindswap.pellet.Individual;
import org.mindswap.pellet.Node;
import org.mindswap.pellet.PelletOptions;
import org.mindswap.pellet.Role;
import org.mindswap.pellet.tableau.completion.CompletionStrategy;
import org.mindswap.pellet.tableau.completion.queue.NodeSelector;
import org.mindswap.pellet.utils.ATermUtils;

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
public class MinRule extends AbstractTableauRule
{
	public MinRule(final CompletionStrategy strategy)
	{
		super(strategy, NodeSelector.MIN_NUMBER, BlockingType.COMPLETE);
	}

	@Override
	public void apply(final Individual x)
	{
		if (!x.canApply(Node.MIN))
			return;

		// We get all the minCard restrictions in the _node and store
		// them in the list ''types''
		final List<ATermAppl> types = x.getTypes(Node.MIN);
		final int size = types.size();
		for (int j = x._applyNext[Node.MIN]; j < size; j++)
		{
			// mc stores the _current type (the _current minCard restriction)
			final ATermAppl mc = types.get(j);

			apply(x, mc);

			if (_strategy.getABox().isClosed())
				return;
		}
		x._applyNext[Node.MIN] = size;
	}

	protected void apply(final Individual x, final ATermAppl mc)
	{
		// We retrieve the role associated to the _current
		// min restriction
		final Role r = _strategy.getABox().getRole(mc.getArgument(0));
		final int n = ((ATermInt) mc.getArgument(1)).getInt();
		final ATermAppl c = (ATermAppl) mc.getArgument(2);

		// FIXME make sure all neighbors are safe
		if (x.hasDistinctRNeighborsForMin(r, n, c))
			return;

		final DependencySet ds = x.getDepends(mc);

		if (!PelletOptions.MAINTAIN_COMPLETION_QUEUE && ds == null)
			return;

		if (log.isLoggable(Level.FINE))
			log.fine("MIN : " + x + " -> " + r + " -> anon" + (n == 1 ? "" : (_strategy.getABox().getAnonCount() + 1) + " - anon") + (_strategy.getABox().getAnonCount() + n) + " " + ATermUtils.toString(c) + " " + ds);

		final Node[] y = new Node[n];
		for (int c1 = 0; c1 < n; c1++)
		{
			_strategy.checkTimer();
			if (r.isDatatypeRole())
				y[c1] = _strategy.getABox().addLiteral(ds);
			else
				y[c1] = _strategy.createFreshIndividual(x, ds);
			Node succ = y[c1];
			DependencySet finalDS = ds;

			_strategy.addEdge(x, r, succ, ds);
			if (succ.isPruned())
			{
				finalDS = finalDS.union(succ.getMergeDependency(true), _strategy.getABox().doExplanation());
				succ = succ.getMergedTo();
			}

			_strategy.addType(succ, c, finalDS);
			for (int c2 = 0; c2 < c1; c2++)
				succ.setDifferent(y[c2], finalDS);
		}
	}
}
