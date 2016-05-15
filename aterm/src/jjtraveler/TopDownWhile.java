package jjtraveler;

/**
 * <code>TopDownWhile(v) = Choice(Sequence(v,All(TopDownWhile(v))),Identity)</code>
 * <p>
 * Visitor combinator with one visitor argument that applies this visitor in pre-order fashion to all nodes, until it fails. Thus, traversal is cut off below the nodes where failure occurs.
 */

public class TopDownWhile<T extends Visitable> extends Choice<T>
{

	/* Create a visitor that applies its argument v in topdown
	 * fashion until it fails. Thus, traversal is cut off below
	 * the nodes where v fails.
	 */
	public TopDownWhile(final Visitor<T> v)
	{
		super(null, new Identity<>());
		setFirst(new Sequence<>(v, new All<>(this)));
	}

	/* Create a visitor that applies its argument v in topdown
	 * fashion until it fails, and subsequently applies its argument
	 * vFinally at the nodes where failure occurs.
	 */
	public TopDownWhile(final Visitor<T> v, final Visitor<T> vFinally)
	{
		super(null, vFinally);
		setFirst(new Sequence<>(v, new All<>(this)));
	}

}
