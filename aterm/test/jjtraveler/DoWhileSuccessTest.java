package jjtraveler;

import jjtraveler.util.VisitorTestCase;

/**
 * Various test cases for
 * the various selected forms of the
 * DoWhileSuccess combinator.
 *
 *
 * @author Arie van Deursen; Jun 30, 2003
 * @version $Id$
 */

public class DoWhileSuccessTest extends VisitorTestCase
{

	public DoWhileSuccessTest(final String test)
	{
		super(test);
	}

	public void testDoWhileSuccess() throws VisitFailure
	{
		final Visitor condition = new FailAtNodes(n1, n2);
		final Visitor action = new Identity();

		final Logger expected = new Logger();
		expected.log(new Event(condition, n0));
		expected.log(new Event(action, n0));
		expected.log(new Event(condition, n1));
		expected.log(new Event(condition, n2));

		final Visitable nodeReturned = new DoWhileSuccess(logVisitor(condition), logVisitor(action)).visit(n0);

		assertEquals(expected, logger);
		assertEquals(n0, nodeReturned);
	}

	public void testTopDownUntil() throws VisitFailure
	{
		final Visitor stopAt = new SucceedAtNodes(n1, n2);

		final Logger expected = new Logger();
		expected.log(new Event(stopAt, n0));
		expected.log(new Event(stopAt, n1));
		expected.log(new Event(stopAt, n2));

		final Visitable nodeReturned = DoWhileSuccess.TopDownUntil(logVisitor(stopAt)).visit(n0);

		assertEquals(expected, logger);
		assertEquals(n0, nodeReturned);
	}

	public void testTopDownUntilAtBorder() throws VisitFailure
	{
		final Visitor stopAt = new SucceedAtNodes(n1, n2);

		final Logger expected = new Logger();

		final Visitor borderAction = new Identity();

		expected.log(new Event(stopAt, n0));
		expected.log(new Event(stopAt, n1));
		expected.log(new Event(borderAction, n1));
		expected.log(new Event(stopAt, n2));
		expected.log(new Event(borderAction, n2));

		final Visitable nodeReturned = new TopDownUntil(logVisitor(stopAt), logVisitor(borderAction)).visit(n0);

		assertEquals(expected, logger);
		assertEquals(n0, nodeReturned);
	}

}
