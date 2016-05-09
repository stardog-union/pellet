package jjtraveler.graph;

import jjtraveler.Event;
import jjtraveler.Fail;
import jjtraveler.FailAtNodes;
import jjtraveler.Identity;
import jjtraveler.Logger;
import jjtraveler.Node;
import jjtraveler.VisitFailure;
import jjtraveler.Visitor;
import jjtraveler.util.VisitorTestCase;

public class ToGraphTest extends VisitorTestCase
{

	public ToGraphTest(String test)
	{
		super(test);
	}

	public void testASTToGraph() throws jjtraveler.VisitFailure
	{
		final EdgesGraph g = new EdgesGraph();
		final Visitor v = new ASTToGraph(g);
		v.visit(n0);
		final EdgesGraph expected = new EdgesGraph();
		expected.addEdge(n0, n1);
		expected.addEdge(n0, n2);
		expected.addEdge(n1, n11);
		expected.addEdge(n1, n12);
		assertEquals(expected, g);
	}

	public void testToGraphIdentity() throws jjtraveler.VisitFailure
	{
		final EdgesGraph g = new EdgesGraph();
		final Visitor v = new ToGraph(g, new Identity());
		v.visit(n0);
		final EdgesGraph expected = new EdgesGraph();
		expected.addEdge(n1, n11);
		expected.addEdge(n1, n12);
		expected.addEdge(n0, n1);
		expected.addEdge(n0, n2);
		assertEquals(expected, g);
	}

	public void testToGraphFail() throws jjtraveler.VisitFailure
	{
		final EdgesGraph g = new EdgesGraph();
		final Visitor v = new ToGraph(g, new Fail());
		v.visit(n0);
		final EdgesGraph expected = new EdgesGraph();
		assertEquals(expected, g);
	}

	public void testToGraphNoInternals() throws jjtraveler.VisitFailure
	{
		final EdgesGraph g = new EdgesGraph();
		final Visitor select = new FailAtNodes(n1, n2);
		final Visitor v = new ToGraph(g, select);
		v.visit(n0);
		final EdgesGraph expected = new EdgesGraph();
		expected.addEdge(n0, n11);
		expected.addEdge(n0, n12);
		assertEquals(expected, g);
	}

	public void testToGraphNoRoot() throws jjtraveler.VisitFailure
	{
		final EdgesGraph g = new EdgesGraph();
		final Visitor select = new FailAtNodes(n0, n0);
		final Visitor v = new ToGraph(g, logVisitor(select));
		v.visit(n0);
		final Logger expectedLogger = new Logger();
		expectedLogger.log(new Event(select, n0));
		expectedLogger.log(new Event(select, n1));
		expectedLogger.log(new Event(select, n11));
		expectedLogger.log(new Event(select, n11));
		expectedLogger.log(new Event(select, n12));
		expectedLogger.log(new Event(select, n12));
		expectedLogger.log(new Event(select, n2));
		final EdgesGraph expected = new EdgesGraph();
		expected.addEdge(n1, n11);
		expected.addEdge(n1, n12);
		assertEquals(expected, g);
		assertEquals(expectedLogger, logger);
	}

	public void testToGraphNoLeaves() throws jjtraveler.VisitFailure
	{
		final EdgesGraph g = new EdgesGraph();
		final java.util.Set<Node> leaves = new java.util.HashSet<>();
		leaves.add(n11);
		leaves.add(n12);
		leaves.add(n2);
		final Visitor select = new FailAtNodes(leaves);
		final Visitor v = new ToGraph(g, logVisitor(select));
		v.visit(n0);
		final Logger expectedLogger = new Logger();
		expectedLogger.log(new Event(select, n0));
		expectedLogger.log(new Event(select, n1));
		expectedLogger.log(new Event(select, n1));
		expectedLogger.log(new Event(select, n11));
		expectedLogger.log(new Event(select, n12));
		expectedLogger.log(new Event(select, n2));
		final EdgesGraph expected = new EdgesGraph();
		expected.addEdge(n0, n1);
		assertEquals(expected, g);
		assertEquals(expectedLogger, logger);
	}

	public void testMkEdgeFromParent() throws VisitFailure
	{
		final EdgesGraph g = new EdgesGraph();
		final Visitor v = new MkEdgeFromParent(n0, g);
		v.visit(n1);
		final EdgesGraph expectedGraph = new EdgesGraph();
		expectedGraph.addEdge(n0, n1);
		assertEquals(expectedGraph, g);
	}

	public void testMkEdgesToKids() throws VisitFailure
	{
		final EdgesGraph g = new EdgesGraph();
		final Visitor v = new MkEdgesToKids(g);
		v.visit(n0);
		final EdgesGraph expectedGraph = new EdgesGraph();
		expectedGraph.addEdge(n0, n1);
		expectedGraph.addEdge(n0, n2);
		assertEquals(expectedGraph, g);
	}

	public void testMkEdgesToKidsSelective() throws VisitFailure
	{
		final EdgesGraph g = new EdgesGraph();
		final Visitor v = new MkEdgesToKids(g, new FailAtNodes(n1, n2));
		v.visit(n0);
		final EdgesGraph expectedGraph = new EdgesGraph();
		expectedGraph.addEdge(n0, n11);
		expectedGraph.addEdge(n0, n12);
		assertEquals(expectedGraph, g);
	}

}
