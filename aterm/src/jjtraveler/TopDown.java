package jjtraveler;

/**
 * <code>TopDown(v) = Sequence(v,All(TopDown(v)))</code>
 * <p>
 * Visitor combinator with one visitor argument that applies this visitor exactly once to the current visitable and each of its descendants, following the topdown (pre-order) traversal strategy.
 */

public class TopDown extends Sequence
{

	/*
	 * Since it is not allowed to reference `this' before the
	 * super type constructor has been called, we can not
	 * write `super(v,All(this))'
	 * Instead, we set the second argument first to `null', and
	 * set it to its proper value afterwards.
	 */
	public TopDown(final Visitor v)
	{
		super(v, null);
		then = new All(this);
	}

	// Factory method
	public TopDown make(final Visitor v)
	{
		return new TopDown(v);
	}

}
