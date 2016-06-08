package com.clarkparsia.reachability;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author Evren Sirin
 */
public class SCC
{

	private SCC()
	{
		// No instance allow.
	}

	/**
	 * Computes the strongly connected components of a graph. This implementation is based on Tarjan's algorithm
	 */
	public static <E> List<Set<EntityNode<E>>> computeSCC(final ReachabilityGraph<E> graph)
	{
		return new SCCComputer<E>().computeSCC(graph);
	}

	/*
	 * Simple structure to keep track of info for _nodes.
	 */
	private static class NodeInfo
	{
		private final Node _node;

		private int _index;

		private int _lowlink;

		private boolean _onStack;

		private NodeInfo(final Node n)
		{
			_node = n;
			_index = -1;
			_lowlink = -1;
			_onStack = false;
		}

		@Override
		public String toString()
		{
			return _node.toString();
		}

		public boolean isEntityNode()
		{
			return _node.isEntityNode();
		}

		public <X> EntityNode<X> asEntityNode()
		{
			return _node.asEntityNode();
		}

	}

	private static class SCCComputer<E>
	{

		private final List<Set<EntityNode<E>>> _stronglyConnectedComponents = new ArrayList<>();

		private int _index;

		private ArrayList<NodeInfo> _stack;

		private final Map<Node, NodeInfo> _nodeInfos = new HashMap<>();

		public List<Set<EntityNode<E>>> computeSCC(final ReachabilityGraph<E> graph)
		{
			final Collection<EntityNode<E>> nodes = graph.getEntityNodes();
			for (final Node node : nodes)
			{
				if (_nodeInfos.containsKey(node))
					continue;

				computeSCC(node);
			}

			return _stronglyConnectedComponents;
		}

		private void computeSCC(final Node node)
		{
			_index = 0;
			_stack = new ArrayList<>();
			visit(new NodeInfo(node));
		}

		private void visit(final NodeInfo nodeInfo)
		{
			_nodeInfos.put(nodeInfo._node, nodeInfo);
			nodeInfo._index = _index;
			nodeInfo._lowlink = _index;
			_index = _index + 1;

			_stack.add(nodeInfo);
			nodeInfo._onStack = true;

			for (final Node out : nodeInfo._node.getOutputs())
			{
				// ignore AndNodes because connectivity through AndNode does not
				// necessarily mean equivalent modules
				if (out instanceof AndNode)
					continue;

				NodeInfo outInfo = _nodeInfos.get(out);
				if (outInfo == null)
				{
					outInfo = new NodeInfo(out);
					visit(outInfo);
					nodeInfo._lowlink = Math.min(nodeInfo._lowlink, outInfo._lowlink);
				}
				else
					if (outInfo._onStack)
						nodeInfo._lowlink = Math.min(nodeInfo._lowlink, outInfo._index);
			}

			if (nodeInfo._lowlink == nodeInfo._index)
			{
				final Set<EntityNode<E>> connectedComponent = new HashSet<>();

				int i = _stack.size() - 1;
				NodeInfo info = null;
				while (info != nodeInfo)
				{
					info = _stack.get(i);
					info._onStack = false;
					// do not include OrNodes in the component
					if (info.isEntityNode())
						connectedComponent.add(info.asEntityNode());
					i--;
				}

				// ignore if the component was a singleton OrNode
				if (connectedComponent.size() > 0)
					_stronglyConnectedComponents.add(connectedComponent);
				_stack.subList(i + 1, _stack.size()).clear();
			}
		}
	}
}
