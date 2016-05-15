package jjtraveler;

/**
 * <code>Not(v)</code> succeeds if and only if <code>v</code> fails.
 */

public class Not<T extends Visitable> implements Visitor<T>
{
	/*
	 * caching a VisitFailure for efficiency (preventing generation of a
	 * stacktrace)
	 */
	private static VisitFailure _failure = new VisitFailure();

	Visitor<T> _v;

	public Not(final Visitor<T> v)
	{
		this._v = v;
	}

	@Override
	public T visit(final T x) throws VisitFailure
	{
		try
		{
			_v.visit(x);
		}
		catch (final VisitFailure f)
		{
			return x;
		}
		throw _failure;
	}
}
