package jjtraveler;

/*
 * Simple visitor recognizing two nodes given at creation time.
 * Can be used to test generic visitors requiring a recognizing
 * argument.
 */

public class SucceedAtNodes implements jjtraveler.Visitor
{
	Visitor success;

	public SucceedAtNodes(final Visitable n1, final Visitable n2)
	{
		success = new Not(new FailAtNodes(n1, n2));
	}

	public SucceedAtNodes(final Visitable n)
	{
		success = new Not(new FailAtNodes(n));
	}

	@Override
	public Visitable visit(final Visitable x) throws VisitFailure
	{
		return success.visit(x);
	}
}
