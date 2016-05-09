package jjtraveler;

/**
 * <code>All(v).visit(T(t1,...,tN) = T(v.visit(t1), ..., v.visit(t1))</code>
 * <p>
 * Basic visitor combinator with one visitor argument, that applies this visitor to all children.
 */

public class All implements Visitor
{

	public Visitor v;

	public All(final Visitor v)
	{
		this.v = v;
	}

	@Override
	public Visitable visit(final Visitable any) throws VisitFailure
	{
		final int childCount = any.getChildCount();
		Visitable result = any;
		for (int i = 0; i < childCount; i++)
			result = result.setChildAt(i, v.visit(result.getChildAt(i)));
		return result;
	}

	// Factory method
	public All make(final Visitor v)
	{
		return new All(v);
	}

	protected void setArgumentTo(final Visitor v)
	{
		this.v = v;
	}
}
