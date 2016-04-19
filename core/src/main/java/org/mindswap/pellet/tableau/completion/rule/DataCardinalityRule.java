// Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package org.mindswap.pellet.tableau.completion.rule;

import aterm.ATerm;
import aterm.ATermAppl;
import aterm.ATermInt;
import aterm.ATermList;
import com.clarkparsia.pellet.datatypes.exceptions.DatatypeReasonerException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.mindswap.pellet.Clash;
import org.mindswap.pellet.DependencySet;
import org.mindswap.pellet.Individual;
import org.mindswap.pellet.Node;
import org.mindswap.pellet.Role;
import org.mindswap.pellet.exceptions.InternalReasonerException;
import org.mindswap.pellet.tableau.completion.CompletionStrategy;
import org.mindswap.pellet.tableau.completion.queue.NodeSelector;

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
public class DataCardinalityRule extends AbstractTableauRule
{
	public DataCardinalityRule(final CompletionStrategy strategy)
	{
		super(strategy, NodeSelector.DATATYPE, BlockingType.NONE);
	}

	@Override
	public void apply(final Individual x)
	{
		final Map<ATermAppl, Collection<ATermAppl>> dataranges = new HashMap<>();
		final Map<ATermAppl, DependencySet> rangeDepends = new HashMap<>();

		/*
		 * Gather all data properties that appear in universal restrictions on this node.
		 */
		for (final ATermAppl allDesc : x.getTypes(Node.ALL))
		{
			final ATerm rTerm = allDesc.getArgument(0);

			/*
			 * Skip object property chains
			 */
			if (rTerm instanceof ATermList)
				continue;

			final ATermAppl r = (ATermAppl) rTerm;
			final Role role = strategy.getABox().getRole(r);

			/*
			 * Skip any roles that are not datatype properties
			 */
			if (!role.isDatatypeRole())
				continue;

			/*
			 * Collect the data range and its dependency set
			 */
			Collection<ATermAppl> existing = dataranges.get(r);
			DependencySet ds = x.getDepends(allDesc);
			if (existing == null)
			{
				existing = new ArrayList<>();
				dataranges.put(r, existing);
			}
			else
				ds = ds.union(rangeDepends.get(r), strategy.getABox().doExplanation());
			existing.add((ATermAppl) allDesc.getArgument(1));
			rangeDepends.put(r, ds);

		}

		/*
		 * Get the ranges of any data properties that have min cardinality restrictions
		 */
		for (final ATermAppl minDesc : x.getTypes(Node.MIN))
		{
			/*
			 * TODO: Verify that minDesc will never have a property chain
			 */
			final ATermAppl r = (ATermAppl) minDesc.getArgument(0);
			final Role role = strategy.getABox().getRole(r);

			/*
			 * Skip any roles that are not datatype properties
			 */
			if (!role.isDatatypeRole())
				continue;

			final Set<ATermAppl> ranges = role.getRanges();
			if (!ranges.isEmpty())
			{
				Collection<ATermAppl> existing = dataranges.get(r);
				DependencySet ds;
				if (existing == null)
				{
					existing = new ArrayList<>();
					dataranges.put(r, existing);
					ds = DependencySet.EMPTY;
				}
				else
					ds = rangeDepends.get(r);

				for (final ATermAppl dataRange : role.getRanges())
				{
					/*
					 * TODO: Verify the dependency set handling here. The old
					 * implementation just used independent (thus could avoid
					 * this loop and call addAll)
					 */
					existing.add(dataRange);
					ds = ds.union(role.getExplainRange(dataRange), strategy.getABox().doExplanation());
					rangeDepends.put(r, ds);
				}
			}
		}

		/*
		 * For each of the min cardinality restrictions, verify that the data range is large enough
		 */
		for (final ATermAppl minDesc : x.getTypes(Node.MIN))
		{
			final ATermAppl r = (ATermAppl) minDesc.getArgument(0);
			final Role role = strategy.getABox().getRole(r);

			final Set<ATermAppl> drs = new HashSet<>();
			final Collection<ATermAppl> direct = dataranges.get(r);
			DependencySet ds;
			if (direct != null)
			{
				drs.addAll(direct);
				ds = rangeDepends.get(r);
			}
			else
				ds = DependencySet.EMPTY;

			ds = ds.union(x.getDepends(minDesc), strategy.getABox().doExplanation());

			for (final Role superRole : role.getSuperRoles())
			{
				final ATermAppl s = superRole.getName();
				final Collection<ATermAppl> inherited = dataranges.get(s);
				if (inherited != null)
				{
					drs.addAll(inherited);
					ds = ds.union(rangeDepends.get(s), strategy.getABox().doExplanation()).union(role.getExplainSuper(s), strategy.getABox().doExplanation());
				}
			}

			if (!drs.isEmpty())
			{
				final int n = ((ATermInt) minDesc.getArgument(1)).getInt();
				try
				{
					if (!strategy.getABox().getDatatypeReasoner().containsAtLeast(n, drs))
					{
						strategy.getABox().setClash(Clash.minMax(x, ds));
						return;
					}
				}
				catch (final DatatypeReasonerException e)
				{
					// TODO Better Error Handling
					throw new InternalReasonerException(e);
				}
			}
		}
	}
}
