// Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package org.mindswap.pellet.tableau.completion.rule;

import aterm.ATermAppl;
import aterm.ATermInt;
import java.util.List;
import org.mindswap.pellet.DependencySet;
import org.mindswap.pellet.Edge;
import org.mindswap.pellet.EdgeList;
import org.mindswap.pellet.Individual;
import org.mindswap.pellet.Node;
import org.mindswap.pellet.Role;
import org.mindswap.pellet.tableau.branch.GuessBranch;
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
public class GuessRule extends AbstractTableauRule
{
	public GuessRule(final CompletionStrategy strategy)
	{
		super(strategy, NodeSelector.GUESS, BlockingType.NONE);
	}

	@Override
	public void apply(final Individual x)
	{
		if (x.isBlockable())
			return;

		final List<ATermAppl> types = x.getTypes(Node.MAX);
		final int size = types.size();
		for (int j = 0; j < size; j++)
		{
			final ATermAppl mc = types.get(j);

			applyGuessingRule(x, mc);

			if (strategy.getABox().isClosed())
				return;

			//			if( x.isPruned() )
			//				break LOOP;
		}
	}

	private void applyGuessingRule(final Individual x, final ATermAppl mc)
	{
		// max(r, n) is in normalized form not(min(p, n + 1))
		final ATermAppl max = (ATermAppl) mc.getArgument(0);

		final Role r = strategy.getABox().getRole(max.getArgument(0));
		final int n = ((ATermInt) max.getArgument(1)).getInt() - 1;
		final ATermAppl c = (ATermAppl) max.getArgument(2);

		// obviously if r is a datatype role then there can be no r-predecessor
		// and we cannot apply the rule
		if (r.isDatatypeRole())
			return;

		// FIXME instead of doing the following check set a flag when the edge is added
		// check that x has to have at least one r neighbor y
		// which is blockable and has successor x
		// (so y is an inv(r) predecessor of x)
		boolean apply = false;
		EdgeList edges = x.getRPredecessorEdges(r.getInverse());
		for (int e = 0; e < edges.size(); e++)
		{
			final Edge edge = edges.edgeAt(e);
			final Individual pred = edge.getFrom();
			if (pred.isBlockable())
			{
				apply = true;
				break;
			}
		}
		if (!apply)
			return;

		if (x.getMaxCard(r) < n)
			return;

		if (x.hasDistinctRNeighborsForMin(r, n, ATermUtils.TOP, true))
			return;

		// if( n == 1 ) {
		// throw new InternalReasonerException(
		// "Functional rule should have been applied " +
		// x + " " + x.isNominal() + " " + edges);
		// }

		int guessMin = x.getMinCard(r, c);
		if (guessMin == 0)
			guessMin = 1;

		// TODO not clear what the correct ds is so be pessimistic and include everything
		DependencySet ds = x.getDepends(mc);
		edges = x.getRNeighborEdges(r);
		for (int e = 0; e < edges.size(); e++)
		{
			final Edge edge = edges.edgeAt(e);
			ds = ds.union(edge.getDepends(), strategy.getABox().doExplanation());
		}

		final GuessBranch newBranch = new GuessBranch(strategy.getABox(), strategy, x, r, guessMin, n, c, ds);
		strategy.addBranch(newBranch);

		newBranch.tryNext();
	}
}
