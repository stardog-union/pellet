package jjtraveler;

/**
 * An (abstract) implementation of the <code>Visitor</code> interface for
 * testing purposes.
 */

public abstract class NodeVisitor implements Visitor<Node>
{
	/* caching a VisitFailure for efficiency (preventing generation of a stacktrace) */
	private static VisitFailure failure = new VisitFailure();

	@Override
	public Node visit(final Node any) throws jjtraveler.VisitFailure
	{
		Node result;
		if (any instanceof Node)
			result = any.accept(this);
		else
			throw failure;
		return result;
	}

	public abstract Node visitNode(Node x) throws jjtraveler.VisitFailure;
}
