package jjtraveler;

import jjtraveler.util.VisitorTestCase;

public class NestingDepthTest extends VisitorTestCase
{

	public NestingDepthTest(final String test)
	{
		super(test);
	}

	public void testDepth2()
	{
		final Visitor recognize = new SucceedAtNodes(n1, n12);
		final NestingDepth nd = new NestingDepth(recognize);
		nd.visit(n0);
		assertEquals(2, nd.getDepth());
	}

	public void testDepth1()
	{
		final Visitor recognize = new SucceedAtNodes(n1);
		final NestingDepth nd = new NestingDepth(recognize);
		nd.visit(n0);
		assertEquals(1, nd.getDepth());
	}

	public void testDepth11()
	{
		final Visitor recognize = new SucceedAtNodes(n1, n2);
		final NestingDepth nd = new NestingDepth(recognize);
		nd.visit(n0);
		assertEquals(1, nd.getDepth());
	}

	public void testNestingStopAt()
	{
		final Visitor recognize = new FailAtNodes(n1, n12);
		final Visitor goOnWhile = new SucceedAtNodes(n0, n1);
		final NestingDepth nd = new NestingDepth(recognize, goOnWhile);
		nd.visit(n0);
		assertEquals(1, nd.getDepth());
	}
}
