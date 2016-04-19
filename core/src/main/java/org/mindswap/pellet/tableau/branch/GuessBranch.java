// Portions Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// Clark & Parsia, LLC parts of this source code are available under the terms of the Affero General Public License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package org.mindswap.pellet.tableau.branch;

import aterm.ATermAppl;
import java.util.logging.Level;
import org.mindswap.pellet.ABox;
import org.mindswap.pellet.Clash;
import org.mindswap.pellet.DependencySet;
import org.mindswap.pellet.Individual;
import org.mindswap.pellet.PelletOptions;
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
 * Copyright: Copyright (c) 2006
 * </p>
 * <p>
 * Company: Clark & Parsia, LLC. <http://www.clarkparsia.com>
 * </p>
 *
 * @author Evren sirin
 */
public class GuessBranch extends IndividualBranch
{
	private final Role r;

	private final int minGuess;
	private final ATermAppl qualification;

	public GuessBranch(final ABox abox, final CompletionStrategy strategy, final Individual x, final Role r, final int minGuess, final int maxGuess, final ATermAppl q, final DependencySet ds)
	{
		super(abox, strategy, x, ds, maxGuess - minGuess + 1);

		this.r = r;
		this.minGuess = minGuess;
		this.qualification = q;
	}

	@Override
	public IndividualBranch copyTo(final ABox abox)
	{
		final Individual x = abox.getIndividual(ind.getName());
		final IndividualBranch b = new GuessBranch(abox, null, x, r, minGuess, minGuess + getTryCount() - 1, qualification, getTermDepends());
		b.setAnonCount(getAnonCount());
		b.setNodeCount(nodeCount);
		b.setBranch(branch);
		b.setStrategy(strategy);
		b.setTryNext(tryNext);

		return b;
	}

	@Override
	protected void tryBranch()
	{
		abox.incrementBranch();

		DependencySet ds = getTermDepends();
		for (; getTryNext() < getTryCount(); tryNext++)
		{
			// start with max possibility and decrement at each try  
			final int n = minGuess + getTryCount() - getTryNext() - 1;

			if (log.isLoggable(Level.FINE))
				log.fine("GUES: (" + (getTryNext() + 1) + "/" + getTryCount() + ") at branch (" + getBranch() + ") to  " + ind + " -> " + r + " -> anon" + (n == 1 ? "" : (abox.getAnonCount() + 1) + " - anon") + (abox.getAnonCount() + n) + " " + ds);

			ds = ds.union(new DependencySet(getBranch()), abox.doExplanation());

			// add the min cardinality restriction just to make early clash detection easier
			strategy.addType(ind, ATermUtils.makeMin(r.getName(), n, qualification), ds);

			// add the max cardinality for guess
			strategy.addType(ind, ATermUtils.makeNormalizedMax(r.getName(), n, qualification), ds);

			// create n distinct nominal successors
			final Individual[] y = new Individual[n];
			for (int c1 = 0; c1 < n; c1++)
			{
				y[c1] = strategy.createFreshIndividual(null, ds);

				strategy.addEdge(ind, r, y[c1], ds);
				y[c1] = y[c1].getSame();
				strategy.addType(y[c1], qualification, ds);
				y[c1] = y[c1].getSame();
				for (int c2 = 0; c2 < c1; c2++)
					y[c1].setDifferent(y[c2], ds);
			}

			final boolean earlyClash = abox.isClosed();
			if (earlyClash)
			{
				if (log.isLoggable(Level.FINE))
					log.fine("CLASH: Branch " + getBranch() + " " + abox.getClash() + "!");

				final DependencySet clashDepends = abox.getClash().getDepends();

				if (clashDepends.contains(getBranch()))
				{
					// we need a global restore here because the merge operation modified three
					// different nodes and possibly other global variables
					strategy.restore(this);

					// global restore sets the branch number to previous value so we need to
					// increment it again
					abox.incrementBranch();

					setLastClash(clashDepends);
				}
				else
					return;
			}
			else
				return;
		}

		ds = getCombinedClash();

		//CHW - removed for rollback through deletions
		if (!PelletOptions.USE_INCREMENTAL_DELETION)
			ds.remove(getBranch());

		abox.setClash(Clash.unexplained(ind, ds));

		return;
	}

	@Override
	public String toString()
	{
		if (getTryNext() < getTryCount())
			return "Branch " + getBranch() + " guess rule on " + ind + " for role  " + r;

		return "Branch " + getBranch() + " guess rule on " + ind + " for role  " + r + " exhausted merge possibilities";
	}

	/**
	 * Added for to re-open closed branches. This is needed for incremental reasoning through deletions Currently this method does nothing as we cannot support
	 * incremental reasoning when both nominals and inverses are used - this is the only case when the guess rule is needed.
	 *
	 * @param index The shift index
	 */
	@Override
	public void shiftTryNext(final int openIndex)
	{
		//decrement trynext
		//tryNext--;
	}

}
