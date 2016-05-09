package jjtraveler;

import jjtraveler.util.VisitorTestCase;

public class IdentityTest extends VisitorTestCase
{

	public IdentityTest(final String test)
	{
		super(test);
	}

	public void testIdentity() throws VisitFailure
	{
		final Identity id = new Identity();
		final Logger expected = new Logger(id, n0);
		final Visitable nodeReturned = logVisitor(id).visit(n0);
		assertEquals(expected, logger);
		assertEquals(n0, nodeReturned);
	}

}
