package jjtraveler;

import jjtraveler.util.VisitorTestCase;

/**
 * Test the Child combinator, distinguishing
 * _condition failure and success.
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

	Visitor _childVisitor;
	Visitor _childAction;
	Visitor _condition;
	Logger _expected;

	@Override
	public void setUp()
	{
		super.setUp();
		_childAction = new Identity();
		_expected = new Logger();
	}

	public void testConditionFails() throws VisitFailure
	{
		_condition = new FailAtNodes(n0);
		_expected.log(new Event(_condition, n0));
		_childVisitor = new Child(logVisitor(_condition), logVisitor(_childAction));
		final Visitable nodeReturned = _childVisitor.visit(n0);
		assertEquals(_expected, logger);
		assertEquals(nodeReturned, n0);
	}

	public void testConditionSucceeds() throws VisitFailure
	{
		_condition = new SucceedAtNodes(n0);
		_expected.log(new Event(_condition, n0));
		_expected.log(new Event(_childAction, n1));
		_expected.log(new Event(_childAction, n2));
		_childVisitor = new Child(logVisitor(_condition), logVisitor(_childAction));
		final Visitable nodeReturned = _childVisitor.visit(n0);
		assertEquals(_expected, logger);
		assertEquals(nodeReturned, n0);
	}

}
