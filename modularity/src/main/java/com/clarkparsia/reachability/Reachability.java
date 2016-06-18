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
import openllet.shared.tools.Log;

/**
 * Computes reachability in a directed _graph with and/or _nodes.
 *
 * @author Evren Sirin
 */
public class Reachability<E>
{

	public static final Logger _logger = Log.getLogger(Reachability.class);

	private final ReachabilityGraph<E> _graph;

	private Set<E> _activatedEntities;

	private Set<Node> _affectedNodes;

	private Queue<Node> _waitingQueue;

	public Reachability(final ReachabilityGraph<E> graph)
	{
		this._graph = graph;
	}

	public ReachabilityGraph<E> getGraph()
	{
		return _graph;
	}

	private void reset()
	{
		_activatedEntities = new HashSet<>();
		_affectedNodes = new HashSet<>();
		_waitingQueue = new LinkedList<>();
		_waitingQueue.add(_graph.getStartNode());
	}

	private void activateNode(final EntityNode<E> node)
	{
		if (node.isActive())
			throw new IllegalStateException();

		_affectedNodes.add(node);
		_activatedEntities.addAll(node.getEntities());
		_waitingQueue.add(node);

		node.inputActivated();

		if (_logger.isLoggable(Level.FINE))
			_logger.fine("Activated: " + node);
	}

	public boolean contains(final E entity)
	{
		return _graph.getNode(entity) != null;
	}

	public Set<E> computeReachable(final Iterable<E> initialEntities)
	{
		reset();

		for (final E initialEntity : initialEntities)
		{
			final EntityNode<E> initialNode = _graph.getNode(initialEntity);
			if (initialNode == null)
				throw new IllegalArgumentException("Unknown entity: " + initialEntity);
			if (!initialNode.isActive())
				activateNode(initialNode);
		}

		while (!_waitingQueue.isEmpty())
		{
			final Node node = _waitingQueue.poll();

			assert node.isActive();

			for (final Node outputNode : node.getOutputs())
			{
				if (outputNode.isActive())
				{
					if (_logger.isLoggable(Level.FINE))
						_logger.fine("Already activated: " + outputNode);
					continue;
				}

				_affectedNodes.add(outputNode);

				if (outputNode.inputActivated())
				{
					if (_logger.isLoggable(Level.FINE))
						_logger.fine("Activated: " + outputNode);

					_waitingQueue.add(outputNode);
					if (outputNode instanceof EntityNode)
						_activatedEntities.addAll(entityNode(outputNode).getEntities());
				}
			}
		}

		for (final Node node : _affectedNodes)
			node.reset();

		return _activatedEntities;
	}

	@SuppressWarnings("unchecked")
	private EntityNode<E> entityNode(final Node node)
	{
		return (EntityNode<E>) node;
	}
}
