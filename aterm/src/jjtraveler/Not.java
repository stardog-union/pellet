package jjtraveler;

/**
 * <code>Not(v)</code> succeeds if and only if <code>v</code> fails.
 */

public class Not implements Visitor
{
	/*
	 * caching a VisitFailure for efficiency (preventing generation of a
	 * stacktrace)
	 */
	private static VisitFailure failure = new VisitFailure();

	Visitor v;

	public Not(final Visitor v)
	{
		this.v = v;
	}

	@Override
	public Visitable visit(final Visitable x) throws VisitFailure
	{
		try
		{
			v.visit(x);
		}
		catch (final VisitFailure f)
		{
			return x;
		}
		throw failure;
	}

}
