package jjtraveler;

import jjtraveler.util.VisitorTestCase;

/**
 * Testing the Descendant(_condition, action)
 * considers _condition failure, and _condition success
 * at different nesting depths.
 *
 * @author Arie van Deursen; Jul 8, 2003
 * @version $Id$
 */
public class DescendantTest extends VisitorTestCase
{

	public DescendantTest(final String name)
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

	public void testConditionFailsAtTop() throws VisitFailure
	{
		condition = new FailAtNodes(n0);
		expected.log(new Event(condition, n0));
		expected.log(new Event(condition, n1));
		expected.log(new Event(childAction, n11));
		expected.log(new Event(childAction, n12));
		expected.log(new Event(condition, n2));
		childVisitor = new Descendant(logVisitor(condition), logVisitor(childAction));
		final Visitable nodeReturned = childVisitor.visit(n0);
		assertEquals(expected, logger);
		assertEquals(nodeReturned, n0);
	}

	public void testConditionSucceedsAtTop() throws VisitFailure
	{
		condition = new SucceedAtNodes(n0);
		expected.log(new Event(condition, n0));
		expected.log(new Event(childAction, n1));
		expected.log(new Event(childAction, n2));
		childVisitor = new Descendant(logVisitor(condition), logVisitor(childAction));
		final Visitable nodeReturned = childVisitor.visit(n0);
		assertEquals(expected, logger);
		assertEquals(nodeReturned, n0);
	}

}
