package jjtraveler;

/*
 * Simple visitor recognizing two nodes given at creation time.
 * Can be used to test generic visitors requiring a recognizing
 * argument.
 */

public class SucceedAtNodes<T extends Visitable> implements Visitor<T>
{
	Visitor<T> _success;

	public SucceedAtNodes(final T n1, final T n2)
	{
		_success = new Not<>(new FailAtNodes<>(n1, n2));
	}

	public SucceedAtNodes(final T n)
	{
		_success = new Not<>(new FailAtNodes<>(n));
	}

	@Override
	public T visit(final T x) throws VisitFailure
	{
		return _success.visit(x);
	}
}
