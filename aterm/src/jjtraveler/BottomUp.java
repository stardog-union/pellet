package jjtraveler;

/**
 * <code>BottomUp(v) = Sequence(All(BottomUp(v)),v)</code>
 * <p>
 * Visitor combinator with one visitor argument that applies this visitor exactly once to the current visitable and each of its descendants, following the bottomup (post-order) traversal strategy.
 */

public class BottomUp extends Sequence
{

	/*
	 * Since it is not allowed to reference `this' before the
	 * super type constructor has been called, we can not
	 * write `super(All(this),v)'
	 * Instead, we set the first argument first to `null', and
	 * set it to its proper value afterwards.
	 */
	public BottomUp(final Visitor v)
	{
		super(null, v);
		first = new All(this);
	}

}
