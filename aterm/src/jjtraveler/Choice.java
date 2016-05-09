package jjtraveler;

/**
 * <code>Choice(v1,v2) = v1</code> if v1 succeeds
 * <p>
 * <code>Choice(v1,v2) = v2</code> if v1 fails
 * <p>
 * Visitor combinator with two visitor arguments, that tries to apply the first visitor and if it fails tries the other (left-biased choice).
 * <p>
 * Note that any side-effects of v1 are not undone when it fails.
 */

public class Choice implements Visitor
{

	Visitor first;
	Visitor then;

	public Choice(final Visitor first, final Visitor then)
	{
		this.first = first;
		this.then = then;
	}

	@Override
	public Visitable visit(final Visitable visitable) throws VisitFailure
	{
		try
		{
			return first.visit(visitable);
		}
		catch (final VisitFailure f)
		{
			return then.visit(visitable);
		}
	}

	protected void setFirst(final Visitor first)
	{
		this.first = first;
	}

	protected void setThen(final Visitor then)
	{
		this.then = then;
	}
}
