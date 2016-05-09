package jjtraveler;

import jjtraveler.util.VisitorTestCase;

public class IfThenElseTest extends VisitorTestCase
{

	Identity idTrue = new Identity();

	Identity idFalse = new Identity();

	Visitable nodeReturned;

	public IfThenElseTest(final String test)
	{
		super(test);
	}

	public void testFalse() throws VisitFailure
	{
		final Logger expected = new Logger();
		expected.log(new Event(idFalse, n0));

		final Visitable n_nodeReturned = new IfThenElse(new Fail(), logVisitor(idTrue), logVisitor(idFalse)).visit(n0);

		assertEquals(expected, logger);
		assertEquals("input node is returned", n0, n_nodeReturned);
	}

	public void testTrue() throws VisitFailure
	{
		final Logger expected = new Logger();
		expected.log(new Event(idTrue, n0));

		final Visitable n_nodeReturned = new IfThenElse(new Identity(), logVisitor(idTrue), logVisitor(idFalse)).visit(n0);

		assertEquals(expected, logger);
		assertEquals(n0, n_nodeReturned);
	}

	public void testTrueFailingThen()
	{
		final Fail failingThen = new Fail();
		final Logger expected = new Logger();
		expected.log(new Event(failingThen, n0));

		try
		{
			nodeReturned = new IfThenElse(new Identity(), logVisitor(failingThen), logVisitor(idFalse)).visit(n0);
			fail();
		}
		catch (final VisitFailure vf)
		{
			assertEquals("trace", expected, logger);
			assertNull("returned node", nodeReturned);
		}

	}

}
