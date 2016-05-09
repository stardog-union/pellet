package jjtraveler;

import jjtraveler.util.VisitorTestCase;

public class OnceTopDownTest extends VisitorTestCase
{

	public OnceTopDownTest(final String test)
	{
		super(test);
	}

	public void testOnceTopDownIsLeaf()
	{
		final Visitor isLeaf = new All(new Fail());
		final OnceTopDown onceTopDown = new OnceTopDown(logVisitor(isLeaf));
		final Logger expected = new Logger(isLeaf, new Visitable[] { n0, n1, n11 });
		try
		{
			final Visitable nodeReturned = onceTopDown.visit(n0);
			assertEquals("visit trace", expected, logger);
			assertEquals("return value", n0, nodeReturned);
		}
		catch (final VisitFailure vf)
		{
			fail("VisitFailure should not occur!");
		}
	}

	public void testOnceTopDownFail()
	{
		final Visitor f = new Fail();
		final OnceTopDown onceTopDown = new OnceTopDown(logVisitor(f));
		final Logger expected = new Logger(f, new Visitable[] { n0, n1, n11, n12, n2 });
		Visitable nodeReturned = null;
		try
		{
			nodeReturned = onceTopDown.visit(n0);
			fail("VisitFailure should have occured!");
		}
		catch (final VisitFailure vf)
		{
			assertEquals("visit trace", expected, logger);
			assertNull("return value", nodeReturned);
		}
	}

}
