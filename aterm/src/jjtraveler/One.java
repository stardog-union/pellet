package jjtraveler;

/**
 * <code>T(t1,..,ti,..,tN).accept(One(v)) = T(t1,...ti.accept(v),..,tN)</code> if <code>ti</code> is the first child that succeeds.
 * <p>
 * Basic visitor combinator with one visitor argument, that applies this visitor to exactly one child. If no children are visited successfully, then One(v) fails.
 * <p>
 * Note that side-effects of failing visits to children are not undone.
 */

public class One implements Visitor
{
	/*
	 * caching a VisitFailure for efficiency (preventing generation of a
	 * stacktrace)
	 */
	private static VisitFailure failure = new VisitFailure();

	public Visitor v;

	public One(final Visitor v)
	{
		this.v = v;
	}

	@Override
	public Visitable visit(final Visitable any) throws VisitFailure
	{
		final int childCount = any.getChildCount();
		for (int i = 0; i < childCount; i++)
			try
			{
				return any.setChildAt(i, v.visit(any.getChildAt(i)));
			}
			catch (final VisitFailure f)
			{
			}
		throw failure;
	}

}
