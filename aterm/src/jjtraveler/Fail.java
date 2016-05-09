package jjtraveler;

/**
 * <code>x.accept(Fail)</code> always raises a VisitFailure exception.
 * <p>
 * Basic visitor combinator without arguments, that always fails.
 * <p>
 * Test case documentation: <a href="FailTest.java">FailTest</a>
 */

public class Fail implements Visitor
{
	/* Constructing exceptions is very expensive because a
	 * stacktrace is generated. We store a static
	 * reference to a reusable exception here, making the
	 * stacktrace unusable, but at least it is fast!
	 */
	static private VisitFailure failure = new VisitFailure();

	/**
	 * Construct Fail combinator with empty failure message.
	 */
	public Fail()
	{
	}

	/**
	 * Construct Fail combinator with a failure message to be passed to the
	 * VisitFailure that it throws.
	 */
	public Fail(final String message)
	{
		failure.setMessage(message);
	}

	@Override
	public Visitable visit(final Visitable any) throws VisitFailure
	{
		throw failure;
	}
}
