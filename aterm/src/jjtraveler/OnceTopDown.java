package jjtraveler;

/**
 * <code>OnceTopDown(v) = Choice(v,One(OnceTopDown(v)))</code>
 * <p>
 * Visitor combinator with one visitor argument that applies this visitor exactly once to the current visitable or one of its descendants, following the topdown (pre-order) traversal strategy.
 */

public class OnceTopDown<T extends Visitable> extends Choice<T>
{
	/*
	 * Since it is not allowed to reference `this' before the
	 * super type constructor has been called, we can not
	 * write `super(v,One(this))'
	 * Instead, we set the second argument first to `null', and
	 * set it to its proper value afterwards.
	 */
	public OnceTopDown(final Visitor<T> v)
	{
		super(v, null);
		setThen(new One<>(this));
	}
}
