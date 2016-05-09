package jjtraveler;

import jjtraveler.util.VisitorTestCase;

/**
 * Various test cases for the rather tricky all spines bottom up.
 *
 * @author Arie van Deursen, CWI
 * @date December 2002
 */

public class AllSpinesBottomUpTest extends VisitorTestCase
{

	public AllSpinesBottomUpTest(final String test)
	{
		super(test);
	}

	public void testn1Success() throws VisitFailure
	{
		final Visitor action = new Identity();
		final Visitor goDown = new Identity();
		final Visitor successNode = new SucceedAtNodes(n1);
		final AllSpinesBottomUp asbu = new AllSpinesBottomUp(goDown, successNode, logVisitor(action));

		final Logger expected = new Logger(action, new Visitable[] { n1, n0 });

		final Visitable nodeReturned = asbu.visit(n0);

		assertEquals("visit trace", expected, logger);
		assertEquals("return value", n0, nodeReturned);
	}

	public void testn2Success() throws VisitFailure
	{
		final Visitor action = new Identity();
		final Visitor goDown = new Identity();
		final Visitor successNode = new SucceedAtNodes(n2);
		final AllSpinesBottomUp asbu = new AllSpinesBottomUp(goDown, successNode, logVisitor(action));

		final Logger expected = new Logger(action, new Visitable[] { n2, n0 });

		final Visitable nodeReturned = asbu.visit(n0);

		assertEquals("visit trace", expected, logger);
		assertEquals("return value", n0, nodeReturned);
	}

	public void testFailAtn1() throws VisitFailure
	{
		final Visitor action = new Identity();
		final Visitor goDown = new FailAtNodes(n1);
		final Visitor successNode = new SucceedAtNodes(n12, n2);
		final AllSpinesBottomUp asbu = new AllSpinesBottomUp(goDown, successNode, logVisitor(action));

		final Logger expected = new Logger(action, new Visitable[] { n2, n0 });

		final Visitable nodeReturned = asbu.visit(n0);

		assertEquals("visit trace", expected, logger);
		assertEquals("return value", n0, nodeReturned);
	}

	public void testMultiplePaths() throws VisitFailure
	{
		final Visitor action = new Identity();
		final Visitor goDown = new Identity();
		final Visitor successNode = new SucceedAtNodes(n12, n2);
		final AllSpinesBottomUp asbu = new AllSpinesBottomUp(goDown, successNode, logVisitor(action));

		final Logger expected = new Logger(action, new Visitable[] { n12, n1, n2, n0 });

		final Visitable nodeReturned = asbu.visit(n0);

		assertEquals("visit trace", expected, logger);
		assertEquals("return value", n0, nodeReturned);
	}
}
