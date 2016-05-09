package jjtraveler;

import jjtraveler.util.VisitorTestCase;

/**
 * Test cases for Some, covering all failures, all successes,
 * and leaf nodes.
 *
 * @author Arie van Deursen, CWI
 * @date December 2002.
 */

public class SomeTest extends VisitorTestCase
{

	public SomeTest(final String test)
	{
		super(test);
	}

	public void testSomeIdentity() throws VisitFailure
	{
		final Identity id = new Identity();
		final Some some = new Some(logVisitor(id));
		final Logger expected = new Logger(id, new Visitable[] { n1, n2 });

		final Visitable nodeReturned = some.visit(n0);
		assertEquals(expected, logger);
		assertEquals(n0, nodeReturned);

	}

	public void testSomeAllFailures()
	{
		final Fail f = new Fail();
		final Some some = new Some(logVisitor(f));
		final Logger expected = new Logger(f, new Visitable[] { n1, n2 });

		Visitable nodeReturned = null;

		try
		{
			nodeReturned = some.visit(n0);
		}
		catch (final VisitFailure vf)
		{
			assertEquals(expected, logger);
			assertNull(nodeReturned);
		}

	}

	public void testSomeOneFailure() throws VisitFailure
	{
		final Visitor v = new FailAtNodes(n1);
		final Some some = new Some(logVisitor(v));
		final Logger expected = new Logger(v, new Visitable[] { n1, n2 });

		Visitable nodeReturned = null;

		nodeReturned = some.visit(n0);
		assertEquals(expected, logger);
		assertEquals(n0, nodeReturned);
	}

	public void testSomeLeaf()
	{
		final Identity id = new Identity();
		final Some some = new Some(logVisitor(id));
		final Logger expected = new Logger();
		Visitable nodeReturned = null;

		try
		{
			nodeReturned = some.visit(n11);
			fail("Some(leaf) should fail!");
		}
		catch (final VisitFailure vf)
		{
			assertEquals(expected, logger);
			assertNull(nodeReturned);
		}
	}

}
