package jjtraveler;

import jjtraveler.util.VisitorTestCase;

public class TimeLogVisitorTest extends VisitorTestCase
{

	public TimeLogVisitorTest(final String test)
	{
		super(test);
	}

	public void testVisitorTiming() throws VisitFailure
	{
		final Logger l = new Logger();
		final TimeLogVisitor tlv = new TimeLogVisitor(new Sleep(1), l);
		(new TopDown(new Sequence(tlv, new Sleep(1)))).visit(n0);
		System.err.println("Elapsed: " + tlv.getElapsedTime());
		System.err.println("Consumed: " + tlv.getConsumedTime());
		assertTrue(tlv.getElapsedTime() >= 0);
		assertTrue(tlv.getConsumedTime() >= 0);
		assertTrue(tlv.getElapsedTime() >= tlv.getConsumedTime());
	}

	public static class Sleep implements Visitor
	{
		int sleepTime;

		public Sleep(final int i)
		{
			sleepTime = i;
		}

		@Override
		public Visitable visit(final Visitable x)
		{
			try
			{
				Thread.sleep(sleepTime);
			}
			catch (final InterruptedException e)
			{
				e.printStackTrace();
			}
			return x;
		}
	}

}
