package jjtraveler;

/**
 * A combinator that generates a loggable event each time that its
 * argument visitor is invoked.
 */

public class LogVisitor implements Visitor
{
	protected Visitor visitor;
	protected Logger logger;

	public LogVisitor(final Visitor v, final Logger l)
	{
		visitor = v;
		logger = l;
	}

	@Override
	public Visitable visit(final Visitable visitable) throws VisitFailure
	{
		logger.log(new Event(visitor, visitable));
		return visitor.visit(visitable);
	}

}
