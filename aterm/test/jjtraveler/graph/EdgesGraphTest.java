package jjtraveler.graph;

import jjtraveler.util.VisitorTestCase;

public class EdgesGraphTest extends VisitorTestCase
{

	public EdgesGraphTest(String test)
	{
		super(test);
	}

	public void testToDot()
	{
		Graph g = new EdgesGraph();
		g.addEdge(n0, n1);
		String expected = "digraph TEST {\n" + "Node-4 -> Node-2;\n" + "}\n";
		assertEquals(expected, g.toDot("TEST"));
	}

	public void testToRSF()
	{
		Graph g = new EdgesGraph();
		g.addEdge(n0, n1);
		String expected = "edge Node-4 Node-2\n";
		assertEquals(expected, g.toRSF());
	}
}
