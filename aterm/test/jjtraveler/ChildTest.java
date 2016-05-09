package jjtraveler;

import jjtraveler.util.VisitorTestCase;

/**
 * Test the Child combinator, distinguishing
 * condition failure and success.
 *
 * @author Arie van Deursen; Jul 8, 2003
 * @version $Id$
 */
public class ChildTest extends VisitorTestCase
{

	public ChildTest(final String name)
	{
		super(name);
	}

	Visitor childVisitor;
	Visitor childAction;
	Visitor condition;
	Logger expected;

	@Override
	public void setUp()
	{
		super.setUp();
		childAction = new Identity();
		expected = new Logger();
	}

	public void testConditionFails() throws VisitFailure
	{
		condition = new FailAtNodes(n0);
		expected.log(new Event(condition, n0));
		childVisitor = new Child(logVisitor(condition), logVisitor(childAction));
		final Visitable nodeReturned = childVisitor.visit(n0);
		assertEquals(expected, logger);
		assertEquals(nodeReturned, n0);
	}

	public void testConditionSucceeds() throws VisitFailure
	{
		condition = new SucceedAtNodes(n0);
		expected.log(new Event(condition, n0));
		expected.log(new Event(childAction, n1));
		expected.log(new Event(childAction, n2));
		childVisitor = new Child(logVisitor(condition), logVisitor(childAction));
		final Visitable nodeReturned = childVisitor.visit(n0);
		assertEquals(expected, logger);
		assertEquals(nodeReturned, n0);
	}

}
