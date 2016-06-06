package com.clarkparsia.modularity.test;

import static org.junit.Assert.assertEquals;

import com.clarkparsia.owlapi.OWL;
import com.clarkparsia.reachability.EntityNode;
import com.clarkparsia.reachability.Node;
import com.clarkparsia.reachability.ReachabilityGraph;
import java.util.HashSet;
import java.util.Set;
import org.junit.Test;
import org.semanticweb.owlapi.model.OWLEntity;

/**
 * @author Evren Sirin
 */
public class GraphSimplifyTests
{
	private ReachabilityGraph<OWLEntity> _graph;
	private OWLEntity[] _entities;
	private EntityNode[] _nodes;

	private void addEdge(final int in, final int out)
	{
		_nodes[in].addOutput(_nodes[out]);
	}

	private void createGraph(final int n)
	{
		_graph = new ReachabilityGraph<>();

		_entities = new OWLEntity[n];
		_nodes = new EntityNode[n];
		for (int i = 0; i < n; i++)
		{
			_entities[i] = OWL.Class("entity" + i);
			_nodes[i] = _graph.createEntityNode(_entities[i]);
		}
	}

	private void simplify()
	{
		_graph.simplify();

		for (int i = 0; i < _nodes.length; i++)
			_nodes[i] = _graph.getNode(_entities[i]);
	}

	private void testOutputs(final int n, final int... outputs)
	{
		final Set<Node> computed = _nodes[n].getOutputs();
		final Set<Node> expected = new HashSet<>();
		for (final int i : outputs)
			expected.add(_nodes[i]);

		assertEquals(expected, computed);
	}

	@Test
	public void simpleTest1()
	{
		createGraph(8);

		addEdge(0, 1);

		addEdge(1, 2);
		addEdge(1, 4);
		addEdge(1, 5);

		addEdge(2, 3);
		addEdge(2, 6);

		addEdge(3, 2);
		addEdge(3, 7);

		addEdge(4, 0);
		addEdge(4, 5);

		addEdge(5, 6);

		addEdge(6, 5);

		addEdge(7, 3);
		addEdge(7, 6);

		simplify();

		// scc = { { 0, 1, 4 }, { 2, 3, 7 }, { 5, 6 } }

		testOutputs(0, 2, 5);
		testOutputs(2, 5);
		testOutputs(5);
	}

	@Test
	public void simpleTest2()
	{
		createGraph(11);

		addEdge(0, 1);

		addEdge(1, 2);
		addEdge(1, 3);
		addEdge(1, 4);

		addEdge(2, 5);

		addEdge(4, 1);
		addEdge(4, 6);

		addEdge(5, 2);
		addEdge(5, 7);

		addEdge(6, 7);
		addEdge(6, 8);

		addEdge(7, 10);

		addEdge(8, 9);

		addEdge(9, 6);

		addEdge(10, 8);

		simplify();

		// scc = { { 0 }, { 1, 4 }, { 3 }, { 2, 5 }, { 6, 7, 8, 9, 10 } }

		testOutputs(0, 1);
		testOutputs(1, 2, 3, 6);
		testOutputs(2, 6);
		testOutputs(3);
	}

	@Test
	public void disconnectedTest()
	{
		createGraph(7);

		addEdge(0, 1);
		addEdge(1, 2);
		addEdge(2, 3);
		addEdge(3, 2);

		addEdge(4, 5);
		addEdge(5, 6);
		addEdge(6, 4);

		simplify();

		// scc = { { 0 }, { 1 }, { 2, 3 }, { 4, 5, 6 } }

		testOutputs(0, 1);
		testOutputs(1, 2);
		testOutputs(2);
		testOutputs(4);
	}
}
