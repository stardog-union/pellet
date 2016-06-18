package com.clarkparsia.pellint.test.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import com.clarkparsia.pellint.util.OptimizedDirectedMultigraph;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.junit.Before;
import org.junit.Test;

/**
 * <p>
 * Copyright: Copyright (c) 2008
 * </p>
 * <p>
 * Company: Clark & Parsia, LLC. <http://www.clarkparsia.com>
 * </p>
 *
 * @author Harris Lin
 */
public class OptimizedDirectedMultigraphTest
{
	private static final String V[] = new String[] { "a", "b", "c" };
	private OptimizedDirectedMultigraph<String> _graph;

	@Before
	public void setUp()
	{
		_graph = new OptimizedDirectedMultigraph<>();
		for (final String v : V)
			_graph.addVertex(v);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testAddEdgeToNonExistentVertices()
	{
		// Adding edge to non-existent vertices should throw IllegalArgumentException
		_graph.addEdge("alien1", "alien2");
	}

	@Test(expected = IllegalArgumentException.class)
	public void testAddEdgeLoop()
	{
		// Adding a loop should throw IllegalArgumentException
		_graph.addEdge(V[0], V[0]);
	}

	@Test
	public void testAddEdgeSimple()
	{
		DefaultWeightedEdge edge = _graph.getEdge(V[0], V[1]);
		assertNull(edge);
		edge = _graph.addEdge("a", "b");
		assertEquals(1, _graph.getEdgeMultiplicity(edge));
		edge = _graph.getEdge("a", "b");
		assertEquals(1, _graph.getEdgeMultiplicity(edge));
	}

	@Test
	public void testAddEdgeMultiple()
	{
		_graph.addEdge("a", "b");
		_graph.addEdge("a", "b");
		_graph.addEdge("b", "a");
		_graph.addEdge("b", "a");
		_graph.addEdge("a", "c");
		DefaultWeightedEdge edge = _graph.getEdge("a", "b");
		assertEquals(2, _graph.getEdgeMultiplicity(edge));
		edge = _graph.getEdge("b", "a");
		assertEquals(2, _graph.getEdgeMultiplicity(edge));
		edge = _graph.getEdge("a", "c");
		assertEquals(1, _graph.getEdgeMultiplicity(edge));
	}

	@Test
	public void testAddEdgeWithMultiplicity()
	{
		_graph.addEdge("a", "b", 10);
		_graph.addEdge("a", "b", 1);
		_graph.addEdge("a", "b", 1);
		final DefaultWeightedEdge edge = _graph.getEdge("a", "b");
		assertEquals(12, _graph.getEdgeMultiplicity(edge));
	}

	@Test(expected = IllegalArgumentException.class)
	public void testAddEdgeWithZeroMultiplicity()
	{
		// Adding non-positive multiplicity should throw IllegalArgumentException
		_graph.addEdge("a", "b", 0);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testAddEdgeWithNegativeMultiplicity()
	{
		// Adding non-positive multiplicity should throw IllegalArgumentException
		_graph.addEdge("a", "b", -1);
	}

	@Test
	public void testRemoveEdge()
	{
		_graph.addEdge("a", "b", 10);
		_graph.removeEdge("a", "b");
		final DefaultWeightedEdge edge = _graph.getEdge("a", "b");
		assertNull(edge);
	}
}
