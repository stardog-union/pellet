package jjtraveler;

import jjtraveler.util.VisitorTestCase;

/**
 * Various test cases for the rather tricky spine top down.
 *
 * @author Arie van Deursen, CWI
 * @date December 2002
 */

public class SpineTopDownTest extends VisitorTestCase
{

	public SpineTopDownTest(final String test)
	{
		super(test);
	}

	public void testSpineTopDownAtInnerNode() throws VisitFailure
	{
		final Visitor stop = new FailAtNodes(n1);
		final SpineTopDown spineTopDown = new SpineTopDown(logVisitor(stop));

		// n1 fails, so searching continues in n2.
		final Logger expected = new Logger(stop, new Visitable[] { n0, n1, n2 });

		final Visitable nodeReturned = spineTopDown.visit(n0);

		assertEquals("visit trace", expected, logger);
		assertEquals("return value", n0, nodeReturned);
	}

	public void testSpineTopDownAtLeaf() throws VisitFailure
	{
		final Visitor stop = new FailAtNodes(n11);
		final SpineTopDown spineTopDown = new SpineTopDown(logVisitor(stop));

		// n11 fails, so path to n12 is first to succeed.
		final Logger expected = new Logger(stop, new Visitable[] { n0, n1, n11, n12 });

		final Visitable nodeReturned = spineTopDown.visit(n0);

		assertEquals("visit trace", expected, logger);
		assertEquals("return value", n0, nodeReturned);
	}

	public void testSpineTopDownOnlySuccess() throws VisitFailure
	{
		final Visitor dontStop = new Identity();
		final SpineTopDown spineTopDown = new SpineTopDown(logVisitor(dontStop));

		// First path from n0 to n11 successful -- spinetopdown
		// won't search any further after that.
		final Logger expected = new Logger(dontStop, new Visitable[] { n0, n1, n11 });

		final Visitable nodeReturned = spineTopDown.visit(n0);

		assertEquals("visit trace", expected, logger);
		assertEquals("return value", n0, nodeReturned);
	}

	public void testSpineTopDownFailAtTop()
	{
		final Visitor stop = new FailAtNodes(n0);
		final SpineTopDown spineTopDown = new SpineTopDown(logVisitor(stop));
		final Logger expected = new Logger(stop, new Visitable[] { n0 });
		Visitable nodeReturned = null;
		try
		{
			nodeReturned = spineTopDown.visit(n0);
			fail("VisitFailure should have occured!");
		}
		catch (final VisitFailure vf)
		{
			assertEquals("visit trace", expected, logger);
			assertNull("return value", nodeReturned);
		}
	}

	public void testSpineTopDownFailAtInners()
	{
		final Visitor stop = new FailAtNodes(n1, n2);
		final SpineTopDown spineTopDown = new SpineTopDown(logVisitor(stop));
		final Logger expected = new Logger(stop, new Visitable[] { n0, n1, n2 });
		Visitable nodeReturned = null;
		try
		{
			nodeReturned = spineTopDown.visit(n0);
			fail("VisitFailure should have occured!");
		}
		catch (final VisitFailure vf)
		{
			assertEquals("visit trace", expected, logger);
			assertNull("return value", nodeReturned);
		}
	}
}
