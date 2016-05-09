package jjtraveler.graph;

import jjtraveler.Visitable;

/**
 * General graph interface, where nodes are assumend to be Visitables..
 */

public interface Graph
{

	/**
	 * Add edge between two visitables.
	 */
	public void addEdge(Visitable from, Visitable to);

	/**
	 * Print the dot representation of the graph.
	 */
	public String toDot(String name);

	/**
	 * Print the Rigi Standard Format representation of the graph
	 */
	public String toRSF();

}
