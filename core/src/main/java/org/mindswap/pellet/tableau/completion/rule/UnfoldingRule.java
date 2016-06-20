// Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package org.mindswap.pellet.tableau.completion.rule;

import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import openllet.aterm.ATermAppl;
import openllet.shared.tools.Log;
import org.mindswap.pellet.DependencySet;
import org.mindswap.pellet.Individual;
import org.mindswap.pellet.Node;
import org.mindswap.pellet.PelletOptions;
import org.mindswap.pellet.tableau.completion.CompletionStrategy;
import org.mindswap.pellet.tableau.completion.queue.NodeSelector;
import org.mindswap.pellet.tbox.impl.Unfolding;
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
public class UnfoldingRule extends AbstractTableauRule
{
	@SuppressWarnings("hiding")
	public final static Logger _logger = Log.getLogger(UnfoldingRule.class);

	public UnfoldingRule(final CompletionStrategy strategy)
	{
		super(strategy, NodeSelector.ATOM, BlockingType.COMPLETE);
	}

	@Override
	public void apply(final Individual node)
	{
		if (!node.canApply(Node.ATOM))
			return;

		final List<ATermAppl> types = node.getTypes(Node.ATOM);
		int size = types.size();
		for (int j = node._applyNext[Node.ATOM]; j < size; j++)
		{
			final ATermAppl c = types.get(j);

			if (!PelletOptions.MAINTAIN_COMPLETION_QUEUE && node.getDepends(c) == null)
				continue;

			applyUnfoldingRule(node, c);

			if (_strategy.getABox().isClosed())
				return;

			// it is possible that unfolding added new atomic 
			// concepts that we need to further unfold
			size = types.size();
		}
		node._applyNext[Node.ATOM] = size;
	}

	protected void applyUnfoldingRule(final Individual node, final ATermAppl c)
	{
		final DependencySet ds = node.getDepends(c);

		if (!PelletOptions.MAINTAIN_COMPLETION_QUEUE && ds == null)
			return;

		final Iterator<Unfolding> unfoldingList = _strategy.getTBox().unfold(c);

		while (unfoldingList.hasNext())
		{
			final Unfolding unfolding = unfoldingList.next();
			final ATermAppl unfoldingCondition = unfolding.getCondition();
			DependencySet finalDS = node.getDepends(unfoldingCondition);

			if (finalDS == null)
				continue;

			final Set<ATermAppl> unfoldingDS = unfolding.getExplanation();
			finalDS = finalDS.union(ds, _strategy.getABox().doExplanation());
			finalDS = finalDS.union(unfoldingDS, _strategy.getABox().doExplanation());

			final ATermAppl unfoldedConcept = unfolding.getResult();

			if (_logger.isLoggable(Level.FINE) && !node.hasType(unfoldedConcept))
				_logger.fine("UNF : " + node + ", " + ATermUtils.toString(c) + " -> " + ATermUtils.toString(unfoldedConcept) + " - " + finalDS);

			_strategy.addType(node, unfoldedConcept, finalDS);
		}
	}
}
