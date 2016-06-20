// Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package com.clarkparsia.pellet.impl;

import com.clarkparsia.pellet.IncrementalChangeTracker;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import openllet.aterm.ATermAppl;
import java.util.Set;
import org.mindswap.pellet.ABox;
import org.mindswap.pellet.DefaultEdge;
import org.mindswap.pellet.Edge;
import org.mindswap.pellet.Individual;
import org.mindswap.pellet.Node;

/**
 * <p>
 * Title: Simple incremental change tracker
 * </p>
 * <p>
 * Description: Basic implementation of {@link IncrementalChangeTracker} interface
 * </p>
 * <p>
 * Copyright: Copyright (c) 2008
 * </p>
 * <p>
 * Company: Clark & Parsia, LLC. <http://www.clarkparsia.com>
 * </p>
 *
 * @author Mike Smith
 */
public class SimpleIncrementalChangeTracker implements IncrementalChangeTracker
{

	private final HashSet<Edge> deletedEdges;
	private final HashMap<Node, Set<ATermAppl>> deletedTypes;
	private final HashSet<Edge> newEdges;
	private final HashSet<Individual> newIndividuals;
	private final HashSet<Node> unprunedNodes;
	private final HashSet<Individual> updatedIndividuals;

	public SimpleIncrementalChangeTracker()
	{
		deletedEdges = new HashSet<>();
		deletedTypes = new HashMap<>();
		newEdges = new HashSet<>();
		newIndividuals = new HashSet<>();
		unprunedNodes = new HashSet<>();
		updatedIndividuals = new HashSet<>();
	}

	private SimpleIncrementalChangeTracker(final SimpleIncrementalChangeTracker src, final ABox target)
	{

		this.deletedEdges = new HashSet<>(src.deletedEdges.size());
		this.newEdges = new HashSet<>(src.newEdges.size());

		for (final Edge se : src.deletedEdges)
		{
			final Individual s = target.getIndividual(se.getFrom().getName());
			if (s == null)
				throw new NullPointerException();
			final Node o = target.getNode(se.getTo().getName());
			if (o == null)
				throw new NullPointerException();

			this.newEdges.add(new DefaultEdge(se.getRole(), s, o, se.getDepends()));
		}

		this.deletedTypes = new HashMap<>(src.deletedTypes.size());

		for (final Map.Entry<Node, Set<ATermAppl>> e : src.deletedTypes.entrySet())
		{
			final Node n = target.getNode(e.getKey().getName());
			if (n == null)
				throw new NullPointerException();
			this.deletedTypes.put(n, new HashSet<>(e.getValue()));
		}

		for (final Edge se : src.newEdges)
		{
			final Individual s = target.getIndividual(se.getFrom().getName());
			if (s == null)
				throw new NullPointerException();
			final Node o = target.getNode(se.getTo().getName());
			if (o == null)
				throw new NullPointerException();

			this.newEdges.add(new DefaultEdge(se.getRole(), s, o, se.getDepends()));
		}

		this.newIndividuals = new HashSet<>(src.newIndividuals.size());

		for (final Individual si : src.newIndividuals)
		{
			final Individual ti = target.getIndividual(si.getName());
			if (ti == null)
				throw new NullPointerException();

			this.newIndividuals.add(ti);
		}

		this.unprunedNodes = new HashSet<>(src.unprunedNodes.size());

		for (final Node sn : src.unprunedNodes)
		{
			final Node tn = target.getNode(sn.getName());
			if (tn == null)
				throw new NullPointerException();

			this.unprunedNodes.add(tn);
		}

		this.updatedIndividuals = new HashSet<>(src.updatedIndividuals.size());

		for (final Individual si : src.updatedIndividuals)
		{
			final Individual ti = target.getIndividual(si.getName());
			if (ti == null)
				throw new NullPointerException();

			this.updatedIndividuals.add(ti);
		}
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.clarkparsia.pellet.IncrementalChangeTracker#addDeletedEdge(org.mindswap.pellet.Edge)
	 */
	@Override
	public boolean addDeletedEdge(final Edge e)
	{
		if (e == null)
			throw new NullPointerException();

		return deletedEdges.add(e);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.clarkparsia.pellet.IncrementalChangeTracker#addDeletedType(org.mindswap.pellet.Node,
	 *      openllet.aterm.ATermAppl)
	 */
	@Override
	public boolean addDeletedType(final Node n, final ATermAppl type)
	{
		if (n == null)
			throw new NullPointerException();
		if (type == null)
			throw new NullPointerException();

		Set<ATermAppl> existing = deletedTypes.get(n);
		if (existing == null)
		{
			existing = new HashSet<>();
			deletedTypes.put(n, existing);
		}

		return existing.add(type);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.clarkparsia.pellet.IncrementalChangeTracker#addNewEdge(org.mindswap.pellet.Edge)
	 */
	@Override
	public boolean addNewEdge(final Edge e)
	{
		if (e == null)
			throw new NullPointerException();

		return newEdges.add(e);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.clarkparsia.pellet.IncrementalChangeTracker#addNewIndividual(org.mindswap.pellet.Individual)
	 */
	@Override
	public boolean addNewIndividual(final Individual i)
	{
		if (i == null)
			throw new NullPointerException();

		return newIndividuals.add(i);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.clarkparsia.pellet.IncrementalChangeTracker#addUnprunedNode(org.mindswap.pellet.Node)
	 */
	@Override
	public boolean addUnprunedNode(final Node n)
	{
		if (n == null)
			throw new NullPointerException();

		return unprunedNodes.add(n);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.clarkparsia.pellet.IncrementalChangeTracker#addUpdatedIndividual(org.mindswap.pellet.Individual)
	 */
	@Override
	public boolean addUpdatedIndividual(final Individual i)
	{
		if (i == null)
			throw new NullPointerException();

		return updatedIndividuals.add(i);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.clarkparsia.pellet.IncrementalChangeTracker#clear()
	 */
	@Override
	public void clear()
	{
		deletedEdges.clear();
		deletedTypes.clear();
		newEdges.clear();
		newIndividuals.clear();
		unprunedNodes.clear();
		updatedIndividuals.clear();
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.clarkparsia.pellet.IncrementalChangeTracker#copy(org.mindswap.pellet.ABox)
	 */
	@Override
	public SimpleIncrementalChangeTracker copy(final ABox target)
	{
		return new SimpleIncrementalChangeTracker(this, target);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.clarkparsia.pellet.IncrementalChangeTracker#deletedEdges()
	 */
	@Override
	public Iterator<Edge> deletedEdges()
	{
		return Collections.unmodifiableSet(deletedEdges).iterator();
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.clarkparsia.pellet.IncrementalChangeTracker#deletedTypes()
	 */
	@Override
	public Iterator<Entry<Node, Set<ATermAppl>>> deletedTypes()
	{
		return Collections.unmodifiableMap(deletedTypes).entrySet().iterator();
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.clarkparsia.pellet.IncrementalChangeTracker#newEdges()
	 */
	@Override
	public Iterator<Edge> newEdges()
	{
		return Collections.unmodifiableSet(newEdges).iterator();
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.clarkparsia.pellet.IncrementalChangeTracker#newIndividuals()
	 */
	@Override
	public Iterator<Individual> newIndividuals()
	{
		return Collections.unmodifiableSet(newIndividuals).iterator();
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.clarkparsia.pellet.IncrementalChangeTracker#unprunedNodes()
	 */
	@Override
	public Iterator<Node> unprunedNodes()
	{
		return Collections.unmodifiableSet(unprunedNodes).iterator();
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.clarkparsia.pellet.IncrementalChangeTracker#updatedIndividuals()
	 */
	@Override
	public Iterator<Individual> updatedIndividuals()
	{
		return Collections.unmodifiableSet(updatedIndividuals).iterator();
	}
}
