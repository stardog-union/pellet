// Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package org.mindswap.pellet.tableau.cache;

import aterm.ATermAppl;
import com.clarkparsia.pellet.utils.CollectionUtils;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import org.mindswap.pellet.DependencySet;
import org.mindswap.pellet.Edge;
import org.mindswap.pellet.EdgeList;
import org.mindswap.pellet.Individual;
import org.mindswap.pellet.Node;
import org.mindswap.pellet.PelletOptions;
import org.mindswap.pellet.Role;
import org.mindswap.pellet.utils.ATermUtils;

/**
 * <p>
 * Title:
 * </p>
 * <p>
 * Description: A node cached as the result of satisfiability checking for a concept.
 * </p>
 * <p>
 * Copyright: Copyright (c) 2008
 * </p>
 * <p>
 * Company: Clark & Parsia, LLC. <http://www.clarkparsia.com>
 * </p>
 *
 * @author Evren Sirin
 */
public class CachedConceptNode implements CachedNode
{
	private final ATermAppl name;
	private final EdgeList inEdges;
	private final EdgeList outEdges;
	private final Map<ATermAppl, DependencySet> types;
	private final boolean isIndependent;

	/**
	 * @param depends
	 * @param node
	 */
	public CachedConceptNode(final ATermAppl name, Individual node)
	{
		this.name = name;

		// if the node is merged, get the representative node and check
		// also if the merge depends on a branch
		isIndependent = node.getMergeDependency(true).isIndependent();
		node = node.getSame();

		outEdges = copyEdgeList(node, true);
		inEdges = copyEdgeList(node, false);

		// collect all transitive property values
		if (node.getABox().getKB().getExpressivity().hasNominal())
			collectComplexPropertyValues(node);

		types = CollectionUtils.makeIdentityMap(node.getDepends());
		for (final Map.Entry<ATermAppl, DependencySet> e : types.entrySet())
			e.setValue(e.getValue().cache());
	}

	private void collectComplexPropertyValues(final Individual subj)
	{
		final Set<Role> collected = new HashSet<>();
		for (final Edge edge : subj.getOutEdges())
		{
			final Role role = edge.getRole();

			// only collect non-simple, i.e. complex, roles
			// TODO we might not need to collect all non-simple roles
			// collecting only the base ones, i.e. minimal w.r.t. role
			// ordering, would be enough
			if (role.isSimple() || !collected.add(role))
				continue;

			collected.add(role);

			collectComplexPropertyValues(subj, role);
		}

		for (final Edge edge : subj.getInEdges())
		{
			final Role role = edge.getRole().getInverse();

			if (role.isSimple() || !collected.add(role))
				continue;

			collectComplexPropertyValues(subj, role);
		}
	}

	private void collectComplexPropertyValues(final Individual subj, final Role role)
	{
		final Set<ATermAppl> knowns = new HashSet<>();
		final Set<ATermAppl> unknowns = new HashSet<>();

		subj.getABox().getObjectPropertyValues(subj.getName(), role, knowns, unknowns, false);

		for (final ATermAppl val : knowns)
			outEdges.addEdge(new CachedOutEdge(role, val, DependencySet.INDEPENDENT));
		for (final ATermAppl val : unknowns)
			outEdges.addEdge(new CachedOutEdge(role, val, DependencySet.DUMMY));
	}

	/**
	 * Create an immutable copy of the given edge list and trimmed to the size.
	 *
	 * @param edgeList
	 * @return
	 */
	private EdgeList copyEdgeList(final Individual node, final boolean out)
	{
		final EdgeList edgeList = out ? node.getOutEdges() : node.getInEdges();
				final EdgeList cachedEdges = new EdgeList(edgeList.size());
				for (final Edge edge : edgeList)
		{
					final Edge cachedEdge = out ? new CachedOutEdge(edge) : new CachedInEdge(edge);
							cachedEdges.addEdge(cachedEdge);

			if (PelletOptions.CHECK_NOMINAL_EDGES)
			{
								final Node neighbor = edge.getNeighbor(node);
								final Map<Node, DependencySet> mergedNodes = neighbor.getAllMerged();
				final DependencySet edgeDepends = edge.getDepends();
				for (final Entry<Node, DependencySet> entry : mergedNodes.entrySet())
				{
					final Node mergedNode = entry.getKey();
					if (mergedNode.isRootNominal() && !mergedNode.equals(neighbor))
					{
						final Role r = edge.getRole();
						final ATermAppl n = mergedNode.getName();
						final DependencySet ds = edgeDepends.union(entry.getValue(), false).cache();
						final Edge e = out ? new CachedOutEdge(r, n, ds) : new CachedInEdge(r, n, ds);
						cachedEdges.addEdge(e);
					}
				}
							}
		}

		return cachedEdges;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isIndependent()
	{
		return isIndependent;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public EdgeList getInEdges()
	{
		return inEdges;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public EdgeList getOutEdges()
	{
		return outEdges;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Map<ATermAppl, DependencySet> getDepends()
	{
		return types;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean hasRNeighbor(final Role role)
	{
		return outEdges.hasEdge(role) || (role.isObjectRole() && inEdges.hasEdge(role.getInverse()));

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isBottom()
	{
		return false;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isComplete()
	{
		return true;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isNamedIndividual()
	{
		return false;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isTop()
	{
		return false;
	}

	@Override
	public ATermAppl getName()
	{
		return name;
	}

	@Override
	public String toString()
	{
		return ATermUtils.toString(name);
	}
}
