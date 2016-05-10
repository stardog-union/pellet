// Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package org.mindswap.pellet.tableau.completion.rule;

import aterm.ATermAppl;
import aterm.ATermInt;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import org.mindswap.pellet.Clash;
import org.mindswap.pellet.DependencySet;
import org.mindswap.pellet.Edge;
import org.mindswap.pellet.EdgeList;
import org.mindswap.pellet.Individual;
import org.mindswap.pellet.Node;
import org.mindswap.pellet.NodeMerge;
import org.mindswap.pellet.PelletOptions;
import org.mindswap.pellet.Role;
import org.mindswap.pellet.tableau.branch.MaxBranch;
import org.mindswap.pellet.tableau.completion.CompletionStrategy;
import org.mindswap.pellet.tableau.completion.queue.NodeSelector;
import org.mindswap.pellet.utils.SetUtils;

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
public class MaxRule extends AbstractTableauRule
{
	public MaxRule(final CompletionStrategy strategy)
	{
		super(strategy, NodeSelector.MAX_NUMBER, BlockingType.INDIRECT);
	}

	/**
	 * Apply max rule to the _individual.
	 */
	@Override
	public void apply(final Individual x)
	{
		if (!x.canApply(Node.MAX))
			return;

		final List<ATermAppl> maxCardinality = x.getTypes(Node.MAX);
		for (int i = 0; i < maxCardinality.size(); i++)
		{
			final ATermAppl mc = maxCardinality.get(i);

			applyMaxRule(x, mc);

			if (_strategy.getABox().isClosed())
				return;

			if (x.isMerged())
				return;
		}
		x._applyNext[Node.MAX] = maxCardinality.size();
	}

	protected void applyMaxRule(final Individual x, final ATermAppl mc)
	{

		// max(r, n) is in normalized form not(min(p, n + 1))
		final ATermAppl max = (ATermAppl) mc.getArgument(0);

		final Role r = _strategy.getABox().getRole(max.getArgument(0));
		final int n = ((ATermInt) max.getArgument(1)).getInt() - 1;
		final ATermAppl c = (ATermAppl) max.getArgument(2);

		DependencySet ds = x.getDepends(mc);

		if (!PelletOptions.MAINTAIN_COMPLETION_QUEUE && ds == null)
			return;

		if (n == 1)
		{
			applyFunctionalMaxRule(x, r, c, ds);
			if (_strategy.getABox().isClosed())
				return;
		}
		else
		{
			boolean hasMore = true;

			while (hasMore)
			{
				hasMore = applyMaxRule(x, r, c, n, ds);

				if (_strategy.getABox().isClosed())
					return;

				if (x.isMerged())
					return;

				if (hasMore)
					// subsequent merges depend on the previous merge
					ds = ds.union(new DependencySet(_strategy.getABox().getBranches().size()), _strategy.getABox().doExplanation());
			}
		}
	}

	/**
	 * applyMaxRule
	 *
	 * @param x
	 * @param r
	 * @param k
	 * @param ds
	 * @return true if more merges are required for this maxCardinality
	 */
	protected boolean applyMaxRule(final Individual x, final Role r, final ATermAppl c, final int k, DependencySet ds)
	{

		final EdgeList edges = x.getRNeighborEdges(r);
		// find all distinct R-neighbors of x
		final Set<Node> neighbors = edges.getFilteredNeighbors(x, c);

		final int n = neighbors.size();

		// if( _log.isLoggable( Level.FINE ) )
		// _log.fine( "Neighbors: " + n + " maxCardinality: " + k);

		// if restriction was maxCardinality 0 then having any R-_neighbor
		// violates the restriction. no merge can fix this. compute the
		// dependency and return
		if (k == 0 && n > 0)
		{
			for (int e = 0; e < edges.size(); e++)
			{
				final Edge edge = edges.edgeAt(e);
				final Node neighbor = edge.getNeighbor(x);
				final DependencySet typeDS = neighbor.getDepends(c);
				if (typeDS != null)
				{
					final Role edgeRole = edge.getRole();
					final DependencySet subDS = r.getExplainSubOrInv(edgeRole);
					ds = ds.union(subDS, _strategy.getABox().doExplanation());
					ds = ds.union(edge.getDepends(), _strategy.getABox().doExplanation());
					ds = ds.union(typeDS, _strategy.getABox().doExplanation());

				}
			}

			_strategy.getABox().setClash(Clash.maxCardinality(x, ds, r.getName(), 0));
			return false;
		}

		// if there are less than n neighbors than max rule won't be triggered
		// return false because no more merge required for this role
		if (n <= k)
			return false;

		// create the pairs to be merged
		final List<NodeMerge> mergePairs = new ArrayList<>();
		final DependencySet differenceDS = findMergeNodes(neighbors, x, mergePairs);
		ds = ds.union(differenceDS, _strategy.getABox().doExplanation());

		// if no pairs were found, i.e. all were defined to be different from
		// each other, then it means this max cardinality restriction is
		// violated. dependency of this clash is on all the neighbors plus the
		// dependency of the restriction type
		if (mergePairs.size() == 0)
		{
			final DependencySet dsEdges = x.hasDistinctRNeighborsForMax(r, k + 1, c);
			if (dsEdges == null)
			{
				if (log.isLoggable(Level.FINE))
					log.fine("Cannot determine the exact clash dependency for " + x);
				_strategy.getABox().setClash(Clash.maxCardinality(x, ds));
				return false;
			}
			else
			{
				if (log.isLoggable(Level.FINE))
					log.fine("Early clash detection for max rule worked " + x + " has more than " + k + " " + r + " edges " + ds.union(dsEdges, _strategy.getABox().doExplanation()) + " " + x.getRNeighborEdges(r).getNeighbors(x));

				if (_strategy.getABox().doExplanation())
					_strategy.getABox().setClash(Clash.maxCardinality(x, ds.union(dsEdges, _strategy.getABox().doExplanation()), r.getName(), k));
				else
					_strategy.getABox().setClash(Clash.maxCardinality(x, ds.union(dsEdges, _strategy.getABox().doExplanation())));

				return false;
			}
		}

		// add the list of possible pairs to be merged in the _branch list
		final MaxBranch newBranch = new MaxBranch(_strategy.getABox(), _strategy, x, r, k, c, mergePairs, ds);
		_strategy.addBranch(newBranch);

		// try a merge that does not trivially fail
		if (newBranch.tryNext() == false)
			return false;

		if (log.isLoggable(Level.FINE))
			log.fine("hasMore: " + (n > k + 1));

		// if there were exactly k + 1 neighbors the previous step would
		// eliminate one _node and only n neighbors would be left. This means
		// restriction is satisfied. If there were more than k + 1 neighbors
		// merging one pair would not be enough and more merges are required,
		// thus false is returned
		return n > k + 1;
	}

	DependencySet findMergeNodes(final Set<Node> neighbors, final Individual node, final List<NodeMerge> pairs)
	{
		DependencySet ds = DependencySet.INDEPENDENT;

		final List<Node> nodes = new ArrayList<>(neighbors);
		for (int i = 0; i < nodes.size(); i++)
		{
			final Node y = nodes.get(i);
			for (int j = i + 1; j < nodes.size(); j++)
			{
				final Node x = nodes.get(j);

				if (y.isDifferent(x))
				{
					ds = ds.union(y.getDifferenceDependency(x), _strategy.getABox().doExplanation());
					continue;
				}

				// 1. if x is a nominal _node (of lower level), then Merge(y, x)
				if (x.getNominalLevel() < y.getNominalLevel())
					pairs.add(new NodeMerge(y, x));
				// 2. if y is a nominal _node or an ancestor of x, then Merge(x, y)
				else
					if (y.isNominal())
						pairs.add(new NodeMerge(x, y));
					// 3. if y is an ancestor of x, then Merge(x, y)
					// Note: y is an ancestor of x iff the max cardinality
					// on _node merges the "_node"'s parent y with "_node"'s
					// child x
					else
						if (y.hasSuccessor(node))
							pairs.add(new NodeMerge(x, y));
						// 4. else Merge(y, x)
						else
							pairs.add(new NodeMerge(y, x));
			}
		}

		return ds;
	}

	public void applyFunctionalMaxRule(final Individual x, final Role s, final ATermAppl c, DependencySet ds)
	{
		Set<Role> functionalSupers = s.getFunctionalSupers();
		if (functionalSupers.isEmpty())
			functionalSupers = SetUtils.singleton(s);
		LOOP: for (Role r : functionalSupers)
		{
			if (PelletOptions.USE_TRACING)
				ds = ds.union(s.getExplainSuper(r.getName()), _strategy.getABox().doExplanation()).union(r.getExplainFunctional(), _strategy.getABox().doExplanation());

			final EdgeList edges = x.getRNeighborEdges(r);

			// if there is not more than one edge then func max rule won't be triggered
			if (edges.size() <= 1)
				continue;

			// find all distinct R-neighbors of x
			final Set<Node> neighbors = edges.getFilteredNeighbors(x, c);

			// if there is not more than one _neighbor then func max rule won't be triggered
			if (neighbors.size() <= 1)
				continue;

			Node head = null;

			int edgeIndex = 0;
			final int edgeCount = edges.size();

			// find the head and its corresponding dependency information.
			// since head is not necessarily the first element in the
			// _neighbor list we need to first find the un-pruned _node
			for (; edgeIndex < edgeCount; edgeIndex++)
			{
				final Edge edge = edges.edgeAt(edgeIndex);
				head = edge.getNeighbor(x);

				if (head.isPruned() || !neighbors.contains(head))
					continue;

				// this _node is included in the merge list because the edge
				// exists and the _node has the qualification in its types
				ds = ds.union(edge.getDepends(), _strategy.getABox().doExplanation());
				ds = ds.union(head.getDepends(c), _strategy.getABox().doExplanation());
				ds = ds.union(r.getExplainSubOrInv(edge.getRole()), _strategy.getABox().doExplanation());
				break;
			}

			// now iterate through the rest of the elements in the neighbors
			// and merge them to the head _node. it is possible that we will
			// switch the head at some point because of merging rules such
			// that you always merge to a nominal of higher level
			for (edgeIndex++; edgeIndex < edgeCount; edgeIndex++)
			{
				final Edge edge = edges.edgeAt(edgeIndex);
				Node next = edge.getNeighbor(x);

				if (next.isPruned() || !neighbors.contains(next))
					continue;

				// it is possible that there are multiple edges to the same
				// _node, e.g. property p and its super property, so check if
				// we already merged this one
				if (head == null || head.isSame(next))
					continue;

				// this _node is included in the merge list because the edge
				// exists and the _node has the qualification in its types
				ds = ds.union(edge.getDepends(), _strategy.getABox().doExplanation());
				ds = ds.union(next.getDepends(c), _strategy.getABox().doExplanation());
				ds = ds.union(r.getExplainSubOrInv(edge.getRole()), _strategy.getABox().doExplanation());

				if (next.isDifferent(head))
				{
					ds = ds.union(head.getDepends(c), _strategy.getABox().doExplanation());
					ds = ds.union(next.getDepends(c), _strategy.getABox().doExplanation());
					ds = ds.union(next.getDifferenceDependency(head), _strategy.getABox().doExplanation());
					if (r.isFunctional())
						_strategy.getABox().setClash(Clash.functionalCardinality(x, ds, r.getName()));
					else
						_strategy.getABox().setClash(Clash.maxCardinality(x, ds, r.getName(), 1));

					break;
				}

				if (x.isNominal() && head.isBlockable() && next.isBlockable() && head.hasSuccessor(x) && next.hasSuccessor(x))
				{
					final Individual newNominal = _strategy.createFreshIndividual(null, ds);

					_strategy.addEdge(x, r, newNominal, ds);

					continue LOOP;
				}
				// always merge to a nominal (of lowest level) or an ancestor
				else
					if ((next.getNominalLevel() < head.getNominalLevel()) || (!head.isNominal() && next.hasSuccessor(x)))
					{
						final Node temp = head;
						head = next;
						next = temp;
					}

				if (log.isLoggable(Level.FINE))
					log.fine("FUNC: " + x + " for prop " + r + " merge " + next + " -> " + head + " " + ds);

				_strategy.mergeTo(next, head, ds);

				if (_strategy.getABox().isClosed())
					return;

				if (head.isPruned())
				{
					ds = ds.union(head.getMergeDependency(true), _strategy.getABox().doExplanation());
					head = head.getSame();
				}
			}
		}
	}

}
