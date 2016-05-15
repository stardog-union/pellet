package jjtraveler;

/**
 * A combinator that generates a loggable event each time that its
 * argument visitor is invoked.
 */

public class LogVisitor<T extends Visitable> implements Visitor<T>
{
	protected Visitor<T> visitor;
	protected Logger logger;

	public LogVisitor(final Visitor<T> v, final Logger l)
	{
		visitor = v;
		logger = l;
	}

	@Override
	public T visit(final T visitable) throws VisitFailure
	{
		logger.log(new Event(visitor, visitable));
		return visitor.visit(visitable);
	}
}
