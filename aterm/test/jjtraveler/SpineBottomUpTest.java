package jjtraveler;

import jjtraveler.util.VisitorTestCase;

/**
 * Various test cases for the rather tricky spine bottom up.
 *
 * @author Arie van Deursen, CWI
 * @date December 2002
 */

public class SpineBottomUpTest extends VisitorTestCase
{

	public SpineBottomUpTest(final String test)
	{
		super(test);
	}

	public void testSpineBottomUpAtInnerNode() throws VisitFailure
	{
		final Visitor stop = new FailAtNodes(n1);
		final SpineBottomUp spineBottomUp = new SpineBottomUp(logVisitor(stop));

		// after having tried n11, n1 fails.
		// searching then continues in n2 and goes up to n0.
		final Logger expected = new Logger(stop, new Visitable[] { n11, n1, n2, n0 });

		final Visitable nodeReturned = spineBottomUp.visit(n0);

		assertEquals("visit trace", expected, logger);
		assertEquals("return value", n0, nodeReturned);
	}

	public void testSpineBottomUpAtLeaf() throws VisitFailure
	{
		final Visitor stop = new FailAtNodes(n11);
		final SpineBottomUp spineBottomUp = new SpineBottomUp(logVisitor(stop));

		// n11 fails, so path to n12 is first to succeed,
		// and visited bottom up.
		final Logger expected = new Logger(stop, new Visitable[] { n11, n12, n1, n0 });

		final Visitable nodeReturned = spineBottomUp.visit(n0);

		assertEquals("visit trace", expected, logger);
		assertEquals("return value", n0, nodeReturned);
	}

	public void testSpineBottomUpOnlySuccess() throws VisitFailure
	{
		final Visitor dontStop = new Identity();
		final SpineBottomUp spineBottomUp = new SpineBottomUp(logVisitor(dontStop));

		// First path from n0 to n11 successful -- spinetopdown
		// won't search any further after that.
		final Logger expected = new Logger(dontStop, new Visitable[] { n11, n1, n0 });

		final Visitable nodeReturned = spineBottomUp.visit(n0);

		assertEquals("visit trace", expected, logger);
		assertEquals("return value", n0, nodeReturned);
	}

	public void testSpineBottomUpFailAtTop()
	{
		final Visitor stop = new FailAtNodes(n0);
		final SpineBottomUp spineBottomUp = new SpineBottomUp(logVisitor(stop));
		final Logger expected = new Logger(stop, new Visitable[] { n11, n1, n0 });
		Visitable nodeReturned = null;
		try
		{
			nodeReturned = spineBottomUp.visit(n0);
			fail("VisitFailure should have occured!");
		}
		catch (final VisitFailure vf)
		{
			assertEquals("visit trace", expected, logger);
			assertNull("return value", nodeReturned);
		}
	}

	public void testSpineBottomUpFailAtInners()
	{
		final Visitor stop = new FailAtNodes(n1, n2);
		final SpineBottomUp spineBottomUp = new SpineBottomUp(logVisitor(stop));
		final Logger expected = new Logger(stop, new Visitable[] { n11, n1, n2 });
		Visitable nodeReturned = null;
		try
		{
			nodeReturned = spineBottomUp.visit(n0);
			fail("VisitFailure should have occured!");
		}
		catch (final VisitFailure vf)
		{
			assertEquals("visit trace", expected, logger);
			assertNull("return value", nodeReturned);
		}
	}
}
