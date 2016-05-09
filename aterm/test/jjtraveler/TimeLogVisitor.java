package jjtraveler;

/**
 * This specialization of the LogVisitor additionally times the
 * invocation and return moments of the argument visitor.
 */

public class TimeLogVisitor extends LogVisitor
{

	long firstInvocationTimeStamp = 0;
	long lastReturnTimeStamp;
	long consumedTime = 0;

	public TimeLogVisitor(final Visitor v, final Logger l)
	{
		super(v, l);
	}

	@Override
	public Visitable visit(final Visitable visitable) throws VisitFailure
	{
		final long startTime = System.currentTimeMillis();
		if (firstInvocationTimeStamp == 0)
			firstInvocationTimeStamp = startTime;

		logger.log(new Event(visitor, visitable));
		final Visitable result = visitor.visit(visitable);

		final long endTime = System.currentTimeMillis();
		lastReturnTimeStamp = endTime;
		consumedTime = consumedTime + (endTime - startTime);

		return result;
	}

	/**
	 * Retrieve the total elapsed time (in milliseconds) since the
	 * first invocation of the argument visitor.
	 */
	public long getElapsedTime()
	{
		return lastReturnTimeStamp - firstInvocationTimeStamp;
	}

	/**
	 * Retrieve the cumulatively consumed time (in milliseconds)
	 * during all executions of the argument visitor.
	 */
	public long getConsumedTime()
	{
		return consumedTime;
	}

}
