package jjtraveler;

import jjtraveler.util.VisitorTestCase;

/**
 * Test cases for the SuccessCounter
 * combinator.
 *
 * @author Arie van Deursen; Jun 30, 2003
 * @version $Id$
 */

public class SuccessCounterTest extends VisitorTestCase
{

	public SuccessCounterTest(final String test)
	{
		super(test);
	}

	public void testSuccessCounter() throws VisitFailure
	{
		final Visitor action = new FailAtNodes(n1, n2);
		final SuccessCounter sc = new SuccessCounter(action);

		final Visitable nodeReturned = (new TopDown(sc)).visit(n0);

		assertEquals(n0, nodeReturned);
		assertEquals(3, sc.getSuccesses());
		assertEquals(2, sc.getFailures());
	}
}
