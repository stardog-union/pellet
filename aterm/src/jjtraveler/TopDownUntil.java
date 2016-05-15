package jjtraveler;

/**
 * <code>TopDownUntil(v) = Choice(v,All(TopDownUntil(v)))</code>
 * <p>
 * Visitor combinator with one visitor argument that tries to apply this visitor in pre-order fashion to all nodes, until it succeeds. Thus, traversal is cut off below the nodes where success occurs.
 * <p>
 * In the Stratego Library, a strategy alltd(s) is present, that resembles this TopDownUntil(v) combinator.
 * <p>
 * Test case documentation: <a href="TopDownUntilTest.java">TopDownUntilTest</a>
 */

public class TopDownUntil<T extends Visitable> extends Choice<T>
{

	/* Create a visitor that applies its argument v in topdown
	 * fashion until it succeeds. Thus, traversal is cut off below
	 * the nodes where v succeeds.
	 */
	public TopDownUntil(final Visitor<T> v)
	{
		super(v, null);
		setThen(new All<>(this));
	}

	/* Create a visitor that applies its argument v in topdown
	 * fashion until it succeeds, and subsequently applies its argument
	 * vFinally at the nodes where failure occurs.
	 */
	public TopDownUntil(final Visitor<T> v, final Visitor<T> vFinally)
	{
		super(new Sequence<>(v, vFinally), null);
		setThen(new All<>(this));
	}

	protected void setArgument(final Visitor<T> v)
	{
		setFirst(v);
	}

}
