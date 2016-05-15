package jjtraveler;

/**
 * <code>x.accept(Identity) = x</code>
 * <p>
 * Basic visitor combinator without arguments that does nothing.
 * <p>
 * See also <a href="IdentityTest.java">IdentityTest</a>.
 */

public class Identity<T extends Visitable> implements Visitor<T>
{
	@Override
	public T visit(final T x)
	{
		return x;
	}
}
