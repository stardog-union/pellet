package jjtraveler;

import jjtraveler.util.VisitorTestCase;

public class FailTest extends VisitorTestCase
{

	public FailTest(final String test)
	{
		super(test);
	}

	public void testFail()
	{
		try
		{
			(new Fail()).visit(n0);
			fail();
		}
		catch (final VisitFailure vf)
		{
			final Logger expected = new Logger();
			assertEquals(expected, logger);
		}
	}

}
