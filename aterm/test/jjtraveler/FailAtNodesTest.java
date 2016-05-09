package jjtraveler;

import jjtraveler.util.VisitorTestCase;

public class FailAtNodesTest extends VisitorTestCase
{

	public FailAtNodesTest(final String test)
	{
		super(test);
	}

	public void testFailAtSomeNode()
	{
		final Visitor v = new FailAtNodes(n0, n1);
		try
		{
			(logVisitor(v)).visit(n0);
			fail("VisitFailure should have occured");
		}
		catch (final VisitFailure vf)
		{
			final Logger expected = new Logger();
			expected.log(new Event(v, n0));
			assertEquals(expected, logger);
		}
	}

	public void testSucceedAtSomeNode() throws VisitFailure
	{
		final Visitor v = new FailAtNodes(n1, n2);
		(logVisitor(v)).visit(n0);
		final Logger expected = new Logger();
		expected.log(new Event(v, n0));
		assertEquals(expected, logger);
	}

	public void testFailAtSomeNodes() throws VisitFailure
	{
		final java.util.Collection<Node> nodes = new java.util.HashSet<>();
		nodes.add(n0);
		nodes.add(n1);
		nodes.add(n11);
		final Visitor v = new FailAtNodes(nodes);
		new Not((logVisitor(v))).visit(n0);
		new Not((logVisitor(v))).visit(n1);
		(logVisitor(v)).visit(n2);
		new Not((logVisitor(v))).visit(n11);
		(logVisitor(v)).visit(n12);
		final Logger expected = new Logger();
		expected.log(new Event(v, n0));
		expected.log(new Event(v, n1));
		expected.log(new Event(v, n2));
		expected.log(new Event(v, n11));
		expected.log(new Event(v, n12));
		assertEquals(expected, logger);
	}
}
