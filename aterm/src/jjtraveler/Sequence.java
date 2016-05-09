package jjtraveler;

/**
 * <code>x.accept(Sequence(v1,v2)) = x.accept(v1) ; x.accept(v2)</code>
 * <p>
 * Basic visitor combinator with two visitor arguments, that applies these visitors one after the other (sequential composition).
 */

public class Sequence implements Visitor
{

	public Visitor first;
	public Visitor then;

	public Sequence(final Visitor first, final Visitor then)
	{
		this.first = first;
		this.then = then;
	}

	public Sequence(final Visitor v1, final Visitor v2, final Visitor v3)
	{
		first = v1;
		then = new Sequence(v2, v3);
	}

	@Override
	public Visitable visit(final Visitable any) throws VisitFailure
	{
		return then.visit(first.visit(any));
	}

	protected void setArgumentAt(final int i, final Visitor v)
	{
		switch (i)
		{
			case 1:
				first = v;
				return;
			case 2:
				then = v;
				return;
			default:
				throw new RuntimeException("Argument out of bounds: " + i);
		}
	}
}
