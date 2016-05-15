package jjtraveler;

/**
 * Abstract visitor implementation that has no return value.
 */

public abstract class VoidVisitor<T extends Visitable> implements Visitor<T>
{

	/**
	 * Forward to <code>voidVisit()</code> and return the incoming
	 * visitable as result.
	 */
	@Override
	public final T visit(final T any) throws VisitFailure
	{
		voidVisit(any);
		return any;
	}

	/**
	 * Like <code>visit()</code>, except no visitable needs to be
	 * returned.
	 */
	public abstract void voidVisit(final T any) throws VisitFailure;

}
