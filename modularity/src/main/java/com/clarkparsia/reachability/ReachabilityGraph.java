// Copyright (c) 2006 - 2015, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package com.clarkparsia.reachability;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.katk.tools.Log;

/**
 * Graph with and/or _nodes designed to compute reachability
 *
 * @author Evren Sirin
 */
public class ReachabilityGraph<E>
{
	public static final Logger _logger = Log.getLogger(ReachabilityGraph.class);

	private final Map<E, EntityNode<E>> _entityNodes = new HashMap<>();

	private int _id = 0;

	private final Node _startNode = new Node() // FIXME TODO : We need to add a strong type system for the class Node
	{
		@Override
		public boolean inputActivated()
		{
			return false;
		}

		@Override
		public boolean isActive()
		{
			return true;
		}

		@Override
		public void reset()
		{
			// Nothing to do.
		}

		@Override
		public String toString()
		{
			return "START";
		}
	};

	private final Node nullNode = new Node()
	{
		@Override
		public boolean inputActivated()
		{
			throw new IllegalStateException("NULL node cannot have inputs");
		}

		@Override
		public void addOutput(final Node output)
		{
			// do not add the output because null node can never be activated
		}

		@Override
		public boolean isActive()
		{
			return false;
		}

		@Override
		public void reset()
		{
			// Nothing to do.
		}

		@Override
		public String toString()
		{
			return "NULL";
		}
	};

	public ReachabilityGraph()
	{
	}

	public Node createAndNode(final Set<Node> inputs)
	{
		if (inputs.isEmpty())
			throw new IllegalArgumentException();

		if (inputs.contains(getNullNode()))
			return getNullNode();

		inputs.remove(getStartNode());

		final int size = inputs.size();

		if (size == 0)
			return getStartNode();
		else
			if (size == 1)
				return inputs.iterator().next();

		final AndNode andNode = new AndNode(_id++);
		for (final Node input : inputs)
			input.addOutput(andNode);

		return andNode;
	}

	public EntityNode<E> createEntityNode(final E entity)
	{
		EntityNode<E> node = _entityNodes.get(entity);

		if (node == null)
		{
			node = new EntityNode<>(entity);
			_entityNodes.put(entity, node);
		}

		return node;
	}

	public Node createOrNode(final Set<Node> inputs)
	{
		if (inputs.isEmpty())
			throw new IllegalArgumentException();

		if (inputs.contains(getStartNode()))
			return getStartNode();

		inputs.remove(getNullNode());

		final int size = inputs.size();

		if (size == 0)
			return getNullNode();
		else
			if (size == 1)
				return inputs.iterator().next();

		final OrNode orNode = new OrNode(_id++);
		for (final Node input : inputs)
			input.addOutput(orNode);

		return orNode;
	}

	public Set<E> getEntities()
	{
		return _entityNodes.keySet();
	}

	public Collection<EntityNode<E>> getEntityNodes()
	{
		return _entityNodes.values();
	}

	public EntityNode<E> getNode(final E entity)
	{
		return _entityNodes.get(entity);
	}

	public Node getNullNode()
	{
		return nullNode;
	}

	public Node getStartNode()
	{
		return _startNode;
	}

	public void simplify()
	{
		collapseSCC();

		removeRedundancies();
	}

	private void collapseSCC()
	{
		int count = 0;
		final List<Set<EntityNode<E>>> components = SCC.computeSCC(this);
		for (final Set<EntityNode<E>> component : components)
		{
			if (component.size() == 1)
				continue;

			if (_logger.isLoggable(Level.FINER))
				_logger.finer("Merging " + component);

			final Iterator<EntityNode<E>> i = component.iterator();
			final EntityNode<E> rep = i.next();

			while (i.hasNext())
			{
				final EntityNode<E> node = i.next();

				for (final E entity : node.getEntities())
				{
					rep.addEntity(entity);
					_entityNodes.put(entity, rep);
				}

				for (final Node input : node.getInputs())
					input.addOutput(rep);
				for (final Node output : node.getOutputs())
					rep.addOutput(output);

				node.removeInOuts();
				count++;
			}
		}

		if (_logger.isLoggable(Level.FINE))
			_logger.fine("Merged " + count + " nodes");
	}

	private void removeRedundancies()
	{
		int removedNode = -1;
		int removedEdge = -1;

		while (removedNode != 0)
		{
			removedNode = 0;
			removedEdge = 0;
			for (final Node node : _entityNodes.values())
				for (final Node out : node.getOutputs().toArray(new Node[0]))
					if (out.isRedundant())
					{
						out.remove();
						removedNode++;
					}
					else
						if (out instanceof AndNode && out.hasOutput(node))
						{
							out.removeOutput(node);
							removedEdge++;
						}

			if (_logger.isLoggable(Level.FINE))
				_logger.fine("Removed " + removedNode + " nodes and " + removedEdge + " edges");
		}
	}
}
