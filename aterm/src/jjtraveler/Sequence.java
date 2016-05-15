package jjtraveler;

/**
 * <code>x.accept(Sequence(v1,v2)) = x.accept(v1) ; x.accept(v2)</code>
 * <p>
 * Basic visitor combinator with two visitor arguments, that applies these visitors one after the other (sequential composition).
 */

public class Sequence<T extends Visitable> implements Visitor<T>
{

	public Visitor<T> first;
	public Visitor<T> then;

	public Sequence(final Visitor<T> first, final Visitor<T> then)
	{
		this.first = first;
		this.then = then;
	}

	public Sequence(final Visitor<T> v1, final Visitor<T> v2, final Visitor<T> v3)
	{
		first = v1;
		then = new Sequence<>(v2, v3);
	}

	@Override
	public T visit(final T any) throws VisitFailure
	{
		return then.visit(first.visit(any));
	}

	protected void setArgumentAt(final int i, final Visitor<T> v)
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
