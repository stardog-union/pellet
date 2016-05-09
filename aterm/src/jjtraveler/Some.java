package jjtraveler;

/**
 * <code>T(t1,..,ti,..,tN).accept(Some(v)) = T(t1,...ti.accept(v),..,tN)</code> for each <code>ti</code> that succeeds.
 * <p>
 * Basic visitor combinator with one visitor argument, that applies this visitor to all children. If no children are visited successfully, then Some(v) fails.
 * <p>
 * Note that side-effects of failing visits to children are not undone.
 *
 * @author Arie van Deursen. Based on One.java
 * @date December 2002.
 */

public class Some implements Visitor
{
	/*
	 * caching a VisitFailure for efficiency (preventing generation of a
	 * stacktrace)
	 */
	private static VisitFailure failure = new VisitFailure();

	public Visitor v;

	public Some(final Visitor v)
	{
		this.v = v;
	}

	@Override
	public Visitable visit(final Visitable any) throws VisitFailure
	{
		final int childCount = any.getChildCount();
		Visitable result = any;
		int successCount = 0;
		for (int i = 0; i < childCount; i++)
			try
			{
				result = result.setChildAt(i, v.visit(any.getChildAt(i)));
				successCount++;
			}
			catch (final VisitFailure f)
			{
			}
		if (successCount == 0)
		{
			failure.setMessage("Some: None of the " + childCount + " arguments of " + any + " succeeded.");
			throw failure;
		}
		return result;
	}

}
