package jjtraveler;

/**
 * <code>SpineBottomUp(v) = Sequence(Choice(One(SpineBottomUp(v)),All(Fail)),v)</code>
 * <p>
 * Visitor combinator with one visitor argument that applies this visitor bottom-up along a path which reaches from one of the leaves to the root.
 */

public class SpineBottomUp<T extends Visitable> extends Sequence<T>
{
	public SpineBottomUp(final Visitor<T> v)
	{
		super(null, v);
		first = new Choice<>(new One<>(this), new All<>(new Fail<>()));
	}
}
