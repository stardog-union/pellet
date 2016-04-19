// Copyright (c) 2006 - 2015, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package com.clarkparsia.reachability;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Computes reachability in a directed graph with and/or nodes.
 *
 * @author Evren Sirin
 */
public class Reachability<E>
{

	public static final Logger log = Logger.getLogger(Reachability.class.getName());

	private final ReachabilityGraph<E> graph;

	private Set<E> activatedEntities;

	private Set<Node> affectedNodes;

	private Queue<Node> waitingQueue;

	public Reachability(final ReachabilityGraph<E> graph)
	{
		this.graph = graph;
	}

	public ReachabilityGraph<E> getGraph()
	{
		return graph;
	}

	private void reset()
	{
		activatedEntities = new HashSet<E>();
		affectedNodes = new HashSet<Node>();
		waitingQueue = new LinkedList<Node>();
		waitingQueue.add(graph.getStartNode());
	}

	private void activateNode(final EntityNode<E> node)
	{
		if (node.isActive())
			throw new IllegalStateException();

		affectedNodes.add(node);
		activatedEntities.addAll(node.getEntities());
		waitingQueue.add(node);

		node.inputActivated();

		if (log.isLoggable(Level.FINE))
			log.fine("Activated: " + node);
	}

	public boolean contains(final E entity)
	{
		return graph.getNode(entity) != null;
	}

	public Set<E> computeReachable(final Iterable<E> initialEntities)
	{
		reset();

		for (final E initialEntity : initialEntities)
		{
			final EntityNode<E> initialNode = graph.getNode(initialEntity);
			if (initialNode == null)
				throw new IllegalArgumentException("Unknown entity: " + initialEntity);
			if (!initialNode.isActive())
				activateNode(initialNode);
		}

		while (!waitingQueue.isEmpty())
		{
			final Node node = waitingQueue.poll();

			assert node.isActive();

			for (final Node outputNode : node.getOutputs())
			{
				if (outputNode.isActive())
				{
					if (log.isLoggable(Level.FINE))
						log.fine("Already activated: " + outputNode);
					continue;
				}

				affectedNodes.add(outputNode);

				if (outputNode.inputActivated())
				{
					if (log.isLoggable(Level.FINE))
						log.fine("Activated: " + outputNode);

					waitingQueue.add(outputNode);
					if (outputNode instanceof EntityNode)
						activatedEntities.addAll(entityNode(outputNode).getEntities());
				}
			}
		}

		for (final Node node : affectedNodes)
			node.reset();

		return activatedEntities;
	}

	@SuppressWarnings("unchecked")
	private EntityNode<E> entityNode(final Node node)
	{
		return (EntityNode) node;
	}
}
