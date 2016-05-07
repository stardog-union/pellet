// Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package org.mindswap.pellet.tableau.completion.rule;

import aterm.ATermAppl;
import java.util.Iterator;
import java.util.List;
import org.mindswap.pellet.Edge;
import org.mindswap.pellet.EdgeList;
import org.mindswap.pellet.Individual;
import org.mindswap.pellet.Node;
import org.mindswap.pellet.PelletOptions;
import org.mindswap.pellet.Role;
import org.mindswap.pellet.tableau.branch.ChooseBranch;
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
public class ChooseRule extends AbstractTableauRule
{

	public ChooseRule(final CompletionStrategy strategy)
	{
		super(strategy, NodeSelector.CHOOSE, BlockingType.INDIRECT);
	}

	@Override
	public void apply(final Individual x)
	{
		if (!x.canApply(Node.MAX))
			return;

		final List<ATermAppl> maxCardinality = x.getTypes(Node.MAX);
		final Iterator<ATermAppl> j = maxCardinality.iterator();

		while (j.hasNext())
		{
			final ATermAppl maxCard = j.next();
			apply(x, maxCard);
		}
	}

	protected void apply(final Individual x, final ATermAppl maxCard)
	{
		// max(r, n, c) is in normalized form not(min(p, n + 1, c))       
		final ATermAppl max = (ATermAppl) maxCard.getArgument(0);
		final Role r = _strategy.getABox().getRole(max.getArgument(0));
		final ATermAppl c = (ATermAppl) max.getArgument(2);

		if (ATermUtils.isTop(c))
			return;

		if (!PelletOptions.MAINTAIN_COMPLETION_QUEUE && x.getDepends(maxCard) == null)
			return;

		final EdgeList edges = x.getRNeighborEdges(r);
		for (final Edge edge : edges)
		{
			final Node neighbor = edge.getNeighbor(x);

			if (!neighbor.hasType(c) && !neighbor.hasType(ATermUtils.negate(c)))
			{
				final ChooseBranch newBranch = new ChooseBranch(_strategy.getABox(), _strategy, neighbor, c, x.getDepends(maxCard));
				_strategy.addBranch(newBranch);

				newBranch.tryNext();

				if (_strategy.getABox().isClosed())
					return;
			}
		}
	}

}
