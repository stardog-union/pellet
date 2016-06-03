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

		private final Node node;

		private int index;

		private int lowlink;

		private boolean onStack;

		private NodeInfo(final Node n)
		{
			node = n;
			index = -1;
			lowlink = -1;
			onStack = false;
		}

		@Override
		public String toString()
		{
			return node.toString();
		}
	}

	private static class SCCComputer<E>
	{

		private List<Set<EntityNode<E>>> stronglyConnectedComponents;

		private int index;

		private ArrayList<NodeInfo> stack;

		private Map<Node, NodeInfo> nodeInfos;

		public List<Set<EntityNode<E>>> computeSCC(final ReachabilityGraph<E> graph)
		{
			stronglyConnectedComponents = new ArrayList<>();
			nodeInfos = new HashMap<>();

			final Collection<EntityNode<E>> nodes = graph.getEntityNodes();
			for (final Node node : nodes)
			{
				if (nodeInfos.containsKey(node))
					continue;

				computeSCC(node);
			}

			return stronglyConnectedComponents;
		}

		private void computeSCC(final Node node)
		{
			index = 0;

			stack = new ArrayList<>();

			final NodeInfo nodeInfo = new NodeInfo(node);
			visit(nodeInfo);
		}

		private void visit(final NodeInfo nodeInfo)
		{
			nodeInfos.put(nodeInfo.node, nodeInfo);
			nodeInfo.index = index;
			nodeInfo.lowlink = index;
			index = index + 1;

			stack.add(nodeInfo);
			nodeInfo.onStack = true;

			for (final Node out : nodeInfo.node.getOutputs())
			{
				// ignore AndNodes because connectivity through AndNode does not
				// necessarily mean equivalent modules
				if (out instanceof AndNode)
					continue;

				NodeInfo outInfo = nodeInfos.get(out);
				if (outInfo == null)
				{
					outInfo = new NodeInfo(out);
					visit(outInfo);
					nodeInfo.lowlink = Math.min(nodeInfo.lowlink, outInfo.lowlink);
				}
				else
					if (outInfo.onStack)
						nodeInfo.lowlink = Math.min(nodeInfo.lowlink, outInfo.index);
			}

			if (nodeInfo.lowlink == nodeInfo.index)
			{
				final Set<EntityNode<E>> connectedComponent = new HashSet<>();

				int i = stack.size() - 1;
				NodeInfo info = null;
				while (info != nodeInfo)
				{
					info = stack.get(i);
					info.onStack = false;
					// do not include OrNodes in the component
					if (info.node instanceof EntityNode)
						connectedComponent.add((EntityNode<E>) info.node);
					i--;
				}

				// ignore if the component was a singleton OrNode
				if (connectedComponent.size() > 0)
					stronglyConnectedComponents.add(connectedComponent);
				stack.subList(i + 1, stack.size()).clear();
			}
		}
	}
}
