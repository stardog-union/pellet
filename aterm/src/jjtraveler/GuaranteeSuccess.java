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

public class GuaranteeSuccess implements Visitor
{
	Visitor v;

	/**
	 * Indicate that the argument visitor is guaranteed to succeed.
	 */
	public GuaranteeSuccess(final Visitor v)
	{
		this.v = v;
	}

	/* Visit the current visitable with the argument visitor v,
	 * and turn any VisitFailure that might occur into a
	 * RuntimeException.
	 */
	@Override
	public Visitable visit(final Visitable visitable)
	{
		try
		{
			return v.visit(visitable);
		}
		catch (final VisitFailure f)
		{
			throw new RuntimeException(f.getMessage());
		}
	}
}
