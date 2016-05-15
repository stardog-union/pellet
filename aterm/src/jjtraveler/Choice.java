package jjtraveler;

/**
 * <code>Choice(v1,v2) = v1</code> if v1 succeeds
 * <p>
 * <code>Choice(v1,v2) = v2</code> if v1 fails
 * <p>
 * Visitor combinator with two visitor arguments, that tries to apply the _first visitor and if it fails tries the other (left-biased choice).
 * <p>
 * Note that any side-effects of v1 are not undone when it fails.
 */

public class Choice<T extends Visitable> implements Visitor<T>
{
	private Visitor<T> _first;
	private Visitor<T> _then;

	public Visitor<T> getFirst()
	{
		return _first;
	}

	protected void setFirst(final Visitor<T> first)
	{
		_first = first;
	}

	public Visitor<T> getThen()
	{
		return _then;
	}

	protected void setThen(final Visitor<T> then)
	{
		_then = then;
	}

	public Choice(final Visitor<T> first, final Visitor<T> then)
	{
		_first = first;
		_then = then;
	}

	@Override
	public T visit(final T visitable) throws VisitFailure
	{
		try
		{
			return _first.visit(visitable);
		}
		catch (final VisitFailure f)
		{
			return _then.visit(visitable);
		}
	}

}
