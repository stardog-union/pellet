package jjtraveler;

import jjtraveler.util.VisitorTestCase;

public class AllTest extends VisitorTestCase
{

	public AllTest(final String test)
	{
		super(test);
	}

	public void testAll()
	{
		final Identity id = new Identity();
		final All all = new All(logVisitor(id));
		final Logger expected = new Logger(id, new Visitable[] { n1, n2 });
		try
		{
			final Visitable nodeReturned = all.visit(n0);
			assertEquals(expected, logger);
			assertEquals(n0, nodeReturned);
		}
		catch (final VisitFailure vf)
		{
			fail("VisitFailure should not occur!");
		}
	}

}
