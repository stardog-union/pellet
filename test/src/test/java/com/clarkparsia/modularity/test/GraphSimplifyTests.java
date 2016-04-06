package com.clarkparsia.modularity.test;

import static org.junit.Assert.assertEquals;

import com.clarkparsia.owlapi.OWL;

import java.util.HashSet;
import java.util.Set;
import org.junit.Test;
import com.clarkparsia.reachability.EntityNode;
import com.clarkparsia.reachability.ReachabilityGraph;
import com.clarkparsia.reachability.Node;
import org.semanticweb.owlapi.model.OWLEntity;

/**
 * @author Evren Sirin
 */
public class GraphSimplifyTests {
	private ReachabilityGraph graph;

	private OWLEntity[]		entities;
	private EntityNode[]	nodes;

	private void addEdge(int in, int out) {
		nodes[in].addOutput( nodes[out] );
	}

	private void createGraph(int n) {
		graph = new ReachabilityGraph();

		entities = new OWLEntity[n];
		nodes = new EntityNode[n];
		for( int i = 0; i < n; i++ ) {
			entities[i] = OWL.Class("entity" + i);
			nodes[i] = graph.createEntityNode( entities[i] );
		}
	}
	
	private void simplify() {
		graph.simplify();
		
		for( int i = 0; i < nodes.length; i++ ) {
			nodes[i] = graph.getNode( entities[i] );
		}
	}

	private void testOutputs(int n, int... outputs) {
		Set<Node> computed = nodes[n].getOutputs();
		Set<Node> expected = new HashSet<Node>();
		for( int i : outputs ) {
			expected.add( nodes[i] );
		}
	
		assertEquals( expected, computed );
	}

	@Test
	public void simpleTest1() {
		createGraph( 8 );

		addEdge( 0, 1 );

		addEdge( 1, 2 );
		addEdge( 1, 4 );
		addEdge( 1, 5 );

		addEdge( 2, 3 );
		addEdge( 2, 6 );

		addEdge( 3, 2 );
		addEdge( 3, 7 );

		addEdge( 4, 0 );
		addEdge( 4, 5 );

		addEdge( 5, 6 );

		addEdge( 6, 5 );

		addEdge( 7, 3 );
		addEdge( 7, 6 );

		simplify();
		
		// scc = { { 0, 1, 4 }, { 2, 3, 7 }, { 5, 6 } }
		
		testOutputs( 0, 2, 5 );
		testOutputs( 2, 5 );
		testOutputs( 5 );
	}

	@Test
	public void simpleTest2() {
		createGraph( 11 );

		addEdge( 0, 1 );

		addEdge( 1, 2 );
		addEdge( 1, 3 );
		addEdge( 1, 4 );
		
		addEdge( 2, 5 );

		addEdge( 4, 1 );
		addEdge( 4, 6 );

		addEdge( 5, 2 );
		addEdge( 5, 7 );

		addEdge( 6, 7 );
		addEdge( 6, 8 );

		addEdge( 7, 10 );

		addEdge( 8, 9 );

		addEdge( 9, 6 );

		addEdge( 10, 8 );
		
		simplify();
		
		// scc = { { 0 }, { 1, 4 }, { 3 }, { 2, 5 }, { 6, 7, 8, 9, 10 } }

		testOutputs( 0, 1 );
		testOutputs( 1, 2, 3, 6 );
		testOutputs( 2, 6 );
		testOutputs( 3 );
	}

	@Test
	public void disconnectedTest() {
		createGraph( 7 );

		addEdge( 0, 1 );
		addEdge( 1, 2 );
		addEdge( 2, 3 );
		addEdge( 3, 2 );

		addEdge( 4, 5 );
		addEdge( 5, 6 );
		addEdge( 6, 4 );

		simplify();
		
		// scc = { { 0 }, { 1 }, { 2, 3 }, { 4, 5, 6 } }
		
		testOutputs( 0, 1 );
		testOutputs( 1, 2 );
		testOutputs( 2 );
		testOutputs( 4 );
	}
}
