package jjtraveler;

/**
 * The visitor combinator GuaranteeSuccess can be used to indicate
 * that its argument visitor is guaranteed to succeed. Note that the
 * visit method of GuaranteeSuccess does not throw VisitFailures,
 * while the visit method of its argument visitor might. If at
 * run-time the guarantee is violated, i.e. a VisitFailure occurs,
 * then then this VisitFailure will be caught and turned into a
 * RuntimeException.
 */

public class GuaranteeSuccess<T extends Visitable> implements Visitor<T>
{
	Visitor<T> _v;

	/**
	 * Indicate that the argument visitor is guaranteed to succeed.
	 */
	public GuaranteeSuccess(final Visitor<T> v)
	{
		this._v = v;
	}

	/* Visit the current visitable with the argument visitor v,
	 * and turn any VisitFailure that might occur into a
	 * RuntimeException.
	 */
	@Override
	public T visit(final T visitable)
	{
		try
		{
			return _v.visit(visitable);
		}
		catch (final VisitFailure f)
		{
			throw new RuntimeException(f.getMessage());
		}
	}
}
