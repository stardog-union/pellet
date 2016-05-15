package jjtraveler;

/**
 * <code>All(v).visit(T(t1,...,tN) = T(v.visit(t1), ..., v.visit(t1))</code>
 * <p>
 * Basic visitor combinator with one visitor argument, that applies this visitor to all children.
 */

public class All<T extends Visitable> implements Visitor<T>
{

	public Visitor<T> _v;

	public All(final Visitor<T> v)
	{
		this._v = v;
	}

	@Override
	public T visit(final T any) throws VisitFailure
	{
		final int childCount = any.getChildCount();
		T result = any;
		for (int i = 0; i < childCount; i++)
			result = result.setChildAt(i, _v.visit(result.getChildAt(i)));
		return result;
	}

	// Factory method
	public All<T> make(final Visitor<T> v)
	{
		return new All<>(v);
	}

	protected void setArgumentTo(final Visitor<T> v)
	{
		this._v = v;
	}
}
