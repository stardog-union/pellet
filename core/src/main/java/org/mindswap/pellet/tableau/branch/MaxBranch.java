// Portions Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// Clark & Parsia, LLC parts of this source code are available under the terms of the Affero General Public License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com
//
// ---
// Portions Copyright (c) 2003 Ron Alford, Mike Grove, Bijan Parsia, Evren Sirin
// Alford, Grove, Parsia, Sirin parts of this source code are available under the terms of the MIT License.
//
// The MIT License
//
// Permission is hereby granted, free of charge, to any person obtaining a copy
// of this software and associated documentation files (the "Software"), to
// deal in the Software without restriction, including without limitation the
// rights to use, copy, modify, merge, publish, distribute, sublicense, and/or
// sell copies of the Software, and to permit persons to whom the Software is
// furnished to do so, subject to the following conditions:
//
// The above copyright notice and this permission notice shall be included in
// all copies or substantial portions of the Software.
//
// THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
// IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
// FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
// AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
// LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
// FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS
// IN THE SOFTWARE.

package org.mindswap.pellet.tableau.branch;

import aterm.ATermAppl;
import java.util.List;
import java.util.logging.Level;
import org.mindswap.pellet.ABox;
import org.mindswap.pellet.Clash;
import org.mindswap.pellet.DependencySet;
import org.mindswap.pellet.Edge;
import org.mindswap.pellet.EdgeList;
import org.mindswap.pellet.Individual;
import org.mindswap.pellet.Node;
import org.mindswap.pellet.NodeMerge;
import org.mindswap.pellet.PelletOptions;
import org.mindswap.pellet.Role;
import org.mindswap.pellet.exceptions.InternalReasonerException;
import org.mindswap.pellet.tableau.completion.CompletionStrategy;
import org.mindswap.pellet.tableau.completion.queue.NodeSelector;
import org.mindswap.pellet.tableau.completion.queue.QueueElement;
import org.mindswap.pellet.utils.ATermUtils;

public class MaxBranch extends IndividualBranch
{
	private final List<NodeMerge> _mergePairs;
	private final Role _r;
	private final int _n;
	private final ATermAppl _qualification;
	private DependencySet[] _prevDS;

	public MaxBranch(final ABox abox, final CompletionStrategy strategy, final Individual x, final Role r, final int n, final ATermAppl qualification, final List<NodeMerge> mergePairs, final DependencySet ds)
	{
		super(abox, strategy, x, ds, mergePairs.size());

		this._r = r;
		this._n = n;
		this._mergePairs = mergePairs;
		this._qualification = qualification;
		this._prevDS = new DependencySet[mergePairs.size()];
	}

	@Override
	public IndividualBranch copyTo(final ABox abox)
	{
		final Individual x = abox.getIndividual(ind.getName());
		final MaxBranch b = new MaxBranch(abox, null, x, _r, _n, _qualification, _mergePairs, getTermDepends());
		b.setAnonCount(getAnonCount());
		b.setNodeCount(_nodeCount);
		b.setBranch(_branch);
		b.setStrategy(_strategy);
		b.setTryNext(_tryNext);
		b._prevDS = new DependencySet[_prevDS.length];
		System.arraycopy(_prevDS, 0, b._prevDS, 0, getTryNext());

		return b;
	}

	@Override
	protected void tryBranch()
	{
		_abox.incrementBranch();

		//we must re-add this individual to the max queue. This is because we may still need to keep
		//applying the max rule for additional merges
		//recreate the label for the individuals
		ATermAppl maxCon = ATermUtils.makeMax(this._r.getName(), this._n, this._qualification);
		//normalize the label
		maxCon = ATermUtils.normalize(maxCon);

		if (PelletOptions.USE_COMPLETION_QUEUE)
		{
			final QueueElement qElement = new QueueElement(ind, maxCon);
			_abox.getCompletionQueue().add(qElement, NodeSelector.MAX_NUMBER);
			_abox.getCompletionQueue().add(qElement, NodeSelector.CHOOSE);
		}

		DependencySet ds = getTermDepends();
		for (; getTryNext() < getTryCount(); _tryNext++)
		{
			this._abox.getKB().timers.mainTimer.check();
			if (PelletOptions.USE_SEMANTIC_BRANCHING)
				for (int m = 0; m < getTryNext(); m++)
				{
					final NodeMerge nm = _mergePairs.get(m);
					final Node y = _abox.getNode(nm.getSource()).getSame();
					final Node z = _abox.getNode(nm.getTarget()).getSame();
					_strategy.setDifferent(y, z, _prevDS[m]);
					//_strategy.addType( y, ATermUtils.makeNot( ATermUtils.makeValue( z.getName() ) ), _prevDS[m] );
				}

			final NodeMerge nm = _mergePairs.get(getTryNext());
			final Node y = _abox.getNode(nm.getSource()).getSame();
			final Node z = _abox.getNode(nm.getTarget()).getSame();

			if (log.isLoggable(Level.FINE))
				log.fine("MAX : (" + (getTryNext() + 1) + "/" + _mergePairs.size() + ") at _branch (" + getBranch() + ") to  " + ind + " for prop " + _r + " _qualification " + _qualification + " merge " + y + " -> " + z + " " + ds);

			ds = ds.union(new DependencySet(getBranch()), _abox.doExplanation());

			// max cardinality merge also depends on all the edges
			// between the individual that has the cardinality and
			// nodes that are going to be merged
			final EdgeList rNeighbors = ind.getRNeighborEdges(_r);
			boolean yEdge = false, zEdge = false;
			for (final Edge edge : rNeighbors)
			{
				final Node neighbor = edge.getNeighbor(ind);

				if (neighbor.equals(y))
				{
					ds = ds.union(edge.getDepends(), _abox.doExplanation());
					yEdge = true;
				}
				else
					if (neighbor.equals(z))
					{
						ds = ds.union(edge.getDepends(), _abox.doExplanation());
						zEdge = true;
					}
			}

			// if there is no edge coming into the _node that is
			// going to be merged then it is not possible that
			// they are affected by the cardinality restriction
			// just die instead of possibly unsound results
			if (!yEdge || !zEdge)
				throw new InternalReasonerException("An error occurred related to the max cardinality restriction about " + _r);

			// if the _neighbor nodes did not have the _qualification
			// in their type list they would have not been affected
			// by the cardinality restriction. so this merges depends
			// on their types
			ds = ds.union(y.getDepends(_qualification), _abox.doExplanation());
			ds = ds.union(z.getDepends(_qualification), _abox.doExplanation());

			// if there were other merges based on the exact same cardinality
			// restriction then this merge depends on them, too (we wouldn't
			// have to merge these two nodes if the previous merge did not
			// eliminate some other possibilities)
			for (int b = _abox.getBranches().size() - 2; b >= 0; b--)
			{
				final Branch branch = _abox.getBranches().get(b);
				if (branch instanceof MaxBranch)
				{
					final MaxBranch prevBranch = (MaxBranch) branch;
					if (prevBranch.ind.equals(ind) && prevBranch._r.equals(_r) && prevBranch._qualification.equals(_qualification))
						ds.add(prevBranch.getBranch());
					else
						break;
				}
				else
					break;
			}

			_strategy.mergeTo(y, z, ds);

			//			_abox.validate();

			final boolean earlyClash = _abox.isClosed();
			if (earlyClash)
			{
				if (log.isLoggable(Level.FINE))
					log.fine("CLASH: Branch " + getBranch() + " " + _abox.getClash() + "!");

				final DependencySet clashDepends = _abox.getClash().getDepends();

				if (clashDepends.contains(getBranch()))
				{
					// we need a global restore here because the merge operation modified three
					// different nodes and possibly other global variables
					_strategy.restore(this);

					// global restore sets the _branch number to previous value so we need to
					// increment it again
					_abox.incrementBranch();

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

		if (_abox.doExplanation())
			_abox.setClash(Clash.maxCardinality(ind, ds, _r.getName(), _n));
		else
			_abox.setClash(Clash.maxCardinality(ind, ds));

		return;
	}

	@Override
	public void setLastClash(final DependencySet ds)
	{
		super.setLastClash(ds);
		if (getTryNext() >= 0)
			_prevDS[getTryNext()] = ds;
	}

	@Override
	public String toString()
	{
		if (getTryNext() < _mergePairs.size())
			return "Branch " + getBranch() + " max rule on " + ind + " merged  " + _mergePairs.get(getTryNext());

		return "Branch " + getBranch() + " max rule on " + ind + " exhausted merge possibilities";
	}

	/**
	 * Added for to re-open closed branches. This is needed for incremental reasoning through deletions
	 *
	 * @param index The shift index
	 */
	@Override
	public void shiftTryNext(final int openIndex)
	{
		//save vals
		//		DependencySet preDS = _prevDS[openIndex];

		//re-open the merge pair
		final NodeMerge nm = _mergePairs.remove(openIndex);
		_mergePairs.add(nm);

		//shift the previous ds
		for (int i = openIndex; i < _mergePairs.size(); i++)
			_prevDS[i] = _prevDS[i + 1];

		//move open label to end
		_prevDS[_mergePairs.size() - 1] = null;

		//decrement trynext
		setTryNext(getTryNext() - 1);
	}

}
