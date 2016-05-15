package jjtraveler;

/**
 * <code>Try(v) = Choice(v,Identity)</code>
 * <p>
 * Visitor combinator with one visitor argument that tries to apply this visitor to the current visitable. If v fails, Try(v) still succeeds.
 */

public class Try<T extends Visitable> extends Choice<T>
{
	public Try(final Visitor<T> v)
	{
		super(v, new Identity<>());
	}
}
