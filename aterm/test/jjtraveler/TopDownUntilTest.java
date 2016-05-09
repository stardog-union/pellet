package jjtraveler;

import jjtraveler.util.VisitorTestCase;

/**
 * Test cases for TopDownUntil,
 * illustrating its effect with a
 * failing condition.
 *
 *
 * @author Arie van Deursen; Jun 30, 2003
 * @version $Id$
 */

public class TopDownUntilTest extends VisitorTestCase
{

	public TopDownUntilTest(final String test)
	{
		super(test);
	}

	Visitor stopAt;
	Logger expected;

	@Override
	public void setUp()
	{
		super.setUp();
		stopAt = new SucceedAtNodes(n1, n2);
		expected = new Logger();
	}

	public void testTopDownUntil() throws VisitFailure
	{
		expected.log(new Event(stopAt, n0));
		expected.log(new Event(stopAt, n1));
		expected.log(new Event(stopAt, n2));

		final Visitable nodeReturned = new TopDownUntil(logVisitor(stopAt)).visit(n0);

		assertEquals(expected, logger);
		assertEquals(n0, nodeReturned);
	}

	public void testTopDownAtBorder() throws VisitFailure
	{
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
