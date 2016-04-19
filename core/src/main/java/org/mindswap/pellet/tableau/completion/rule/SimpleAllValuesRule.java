// Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package org.mindswap.pellet.tableau.completion.rule;

import aterm.ATermAppl;
import java.util.Iterator;
import java.util.List;
import org.mindswap.pellet.DependencySet;
import org.mindswap.pellet.Edge;
import org.mindswap.pellet.EdgeList;
import org.mindswap.pellet.Individual;
import org.mindswap.pellet.Node;
import org.mindswap.pellet.Role;
import org.mindswap.pellet.tableau.completion.CompletionStrategy;
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
public class SimpleAllValuesRule extends AllValuesRule
{
	public SimpleAllValuesRule(final CompletionStrategy strategy)
	{
		super(strategy);
	}

	@Override
	public void applyAllValues(final Individual x, final ATermAppl av, final DependencySet ds)
	{
		final ATermAppl p = (ATermAppl) av.getArgument(0);
		final ATermAppl c = (ATermAppl) av.getArgument(1);

		final Role s = strategy.getABox().getRole(p);

		if (s.isTop() && s.isObjectRole())
		{
			applyAllValuesTop(av, c, ds);
			return;
		}

		EdgeList edges = x.getRNeighborEdges(s);
		for (int e = 0; e < edges.size(); e++)
		{
			final Edge edgeToY = edges.edgeAt(e);
			final Node y = edgeToY.getNeighbor(x);
			DependencySet finalDS = ds.union(edgeToY.getDepends(), strategy.getABox().doExplanation());
			if (strategy.getABox().doExplanation())
			{
				final Role edgeRole = edgeToY.getRole();
				final DependencySet subDS = s.getExplainSubOrInv(edgeRole);
				finalDS = finalDS.union(subDS.getExplain(), true);
			}

			applyAllValues(x, s, y, c, finalDS);

			if (x.isMerged())
				return;
		}

		if (!s.isSimple())
			for (final Role r : s.getTransitiveSubRoles())
			{
				final ATermAppl allRC = ATermUtils.makeAllValues(r.getName(), c);

				edges = x.getRNeighborEdges(r);
				for (int e = 0; e < edges.size(); e++)
				{
					final Edge edgeToY = edges.edgeAt(e);
					final Node y = edgeToY.getNeighbor(x);
					DependencySet finalDS = ds.union(edgeToY.getDepends(), strategy.getABox().doExplanation());
					if (strategy.getABox().doExplanation())
					{
						finalDS = finalDS.union(r.getExplainTransitive().getExplain(), true);
						finalDS = finalDS.union(s.getExplainSubOrInv(edgeToY.getRole()), true);
					}

					applyAllValues(x, r, y, allRC, finalDS);

					if (x.isMerged())
						return;
				}
			}
	}

	@Override
	public void applyAllValues(final Individual subj, final Role pred, final Node obj, final DependencySet ds)
	{

		final List<ATermAppl> allValues = subj.getTypes(Node.ALL);
		int size = allValues.size();
		Iterator<ATermAppl> i = allValues.iterator();
		while (i.hasNext())
		{
			final ATermAppl av = i.next();
			final ATermAppl p = (ATermAppl) av.getArgument(0);
			final ATermAppl c = (ATermAppl) av.getArgument(1);

			final Role s = strategy.getABox().getRole(p);

			if (s.isTop() && s.isObjectRole())
			{
				applyAllValuesTop(av, c, ds);
				continue;
			}

			if (pred.isSubRoleOf(s))
			{
				DependencySet finalDS = ds.union(subj.getDepends(av), strategy.getABox().doExplanation());
				if (strategy.getABox().doExplanation())
					finalDS = finalDS.union(s.getExplainSubOrInv(pred).getExplain(), true);

				applyAllValues(subj, s, obj, c, finalDS);

				if (s.isTransitive())
				{
					final ATermAppl allRC = ATermUtils.makeAllValues(s.getName(), c);
					finalDS = ds.union(subj.getDepends(av), strategy.getABox().doExplanation());
					if (strategy.getABox().doExplanation())
						finalDS = finalDS.union(s.getExplainTransitive().getExplain(), true);

					applyAllValues(subj, s, obj, allRC, finalDS);
				}
			}

			// if there are self links through transitive properties restart
			if (size != allValues.size())
			{
				i = allValues.iterator();
				size = allValues.size();
			}
		}
	}

}
