package com.clarkparsia.pellint.test.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.jgrapht.graph.DefaultWeightedEdge;
import org.junit.Before;
import org.junit.Test;

import com.clarkparsia.pellint.util.OptimizedDirectedMultigraph;

/**
 * <p>
 * Title:
 * </p>
 * <p>
 * Description: 
 * </p>
 * <p>
 * Copyright: Copyright (c) 2008
 * </p>
 * <p>
 * Company: Clark & Parsia, LLC. <http://www.clarkparsia.com>
 * </p>
 * 
 * @author Harris Lin
 */
public class OptimizedDirectedMultigraphTest {
	private static final String V[] = new String[] {"a", "b", "c"};
	private OptimizedDirectedMultigraph<String> m_Graph;
	
	@Before
	public void setUp() {
		m_Graph = new OptimizedDirectedMultigraph<String>();
		for (String v : V) {
			m_Graph.addVertex(v);
		}
	}

	@Test(expected=IllegalArgumentException.class)
	public void testAddEdgeToNonExistentVertices() {
		// Adding edge to non-existent vertices should throw IllegalArgumentException
		m_Graph.addEdge("alien1", "alien2");
	}

	@Test(expected=IllegalArgumentException.class)
	public void testAddEdgeLoop() {
		// Adding a loop should throw IllegalArgumentException
		m_Graph.addEdge(V[0], V[0]);
	}

	@Test
	public void testAddEdgeSimple() {
		DefaultWeightedEdge edge = m_Graph.getEdge(V[0], V[1]);
		assertNull(edge);
		edge = m_Graph.addEdge("a", "b");
		assertEquals(1, m_Graph.getEdgeMultiplicity(edge));
		edge = m_Graph.getEdge("a", "b");
		assertEquals(1, m_Graph.getEdgeMultiplicity(edge));
	}

	@Test
	public void testAddEdgeMultiple() {
		m_Graph.addEdge("a", "b");
		m_Graph.addEdge("a", "b");
		m_Graph.addEdge("b", "a");
		m_Graph.addEdge("b", "a");
		m_Graph.addEdge("a", "c");
		DefaultWeightedEdge edge = m_Graph.getEdge("a", "b");
		assertEquals(2, m_Graph.getEdgeMultiplicity(edge));
		edge = m_Graph.getEdge("b", "a");
		assertEquals(2, m_Graph.getEdgeMultiplicity(edge));
		edge = m_Graph.getEdge("a", "c");
		assertEquals(1, m_Graph.getEdgeMultiplicity(edge));
	}

	@Test
	public void testAddEdgeWithMultiplicity() {
		m_Graph.addEdge("a", "b", 10);
		m_Graph.addEdge("a", "b", 1);
		m_Graph.addEdge("a", "b", 1);
		DefaultWeightedEdge edge = m_Graph.getEdge("a", "b");
		assertEquals(12, m_Graph.getEdgeMultiplicity(edge));
	}

	@Test(expected=IllegalArgumentException.class)
	public void testAddEdgeWithZeroMultiplicity() {
		// Adding non-positive multiplicity should throw IllegalArgumentException
		m_Graph.addEdge("a", "b", 0);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testAddEdgeWithNegativeMultiplicity() {
		// Adding non-positive multiplicity should throw IllegalArgumentException
		m_Graph.addEdge("a", "b", -1);
	}

	@Test
	public void testRemoveEdge() {
		m_Graph.addEdge("a", "b", 10);
		m_Graph.removeEdge("a", "b");
		DefaultWeightedEdge edge = m_Graph.getEdge("a", "b");
		assertNull(edge);
	}
}
