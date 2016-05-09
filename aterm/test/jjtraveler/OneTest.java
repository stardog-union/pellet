package jjtraveler;

import jjtraveler.util.VisitorTestCase;

public class OneTest extends VisitorTestCase
{

	public OneTest(final String test)
	{
		super(test);
	}

	public void testOne()
	{
		final Identity id = new Identity();
		final One one = new One(logVisitor(id));
		final Logger expected = new Logger(id, new Visitable[] { n1 });
		try
		{
			final Visitable nodeReturned = one.visit(n0);
			assertEquals(expected, logger);
			assertEquals(n0, nodeReturned);
		}
		catch (final VisitFailure vf)
		{
			assertEquals(expected, logger);
			//	    assertEquals(n0, nodeReturned);
			fail("VisitFailure should not occur!");
		}
	}

	public void testOneLeaf()
	{
		final Identity id = new Identity();
		final One one = new One(logVisitor(id));
		final Logger expected = new Logger();
		Visitable nodeReturned = null;

		try
		{
			nodeReturned = one.visit(n11);
			fail("One(leaf) should fail!");
		}
		catch (final VisitFailure vf)
		{
			assertEquals(expected, logger);
			assertNull(nodeReturned);
		}
	}

}
