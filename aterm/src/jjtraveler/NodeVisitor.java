package jjtraveler;

/**
 * An (abstract) implementation of the <code>Visitor</code> interface for
 * testing purposes.
 */

public abstract class NodeVisitor implements jjtraveler.Visitor
{
	/* caching a VisitFailure for efficiency (preventing generation of a stacktrace) */
	private static jjtraveler.VisitFailure failure = new jjtraveler.VisitFailure();

	@Override
	public jjtraveler.Visitable visit(final jjtraveler.Visitable any) throws jjtraveler.VisitFailure
	{
		jjtraveler.Visitable result;
		if (any instanceof Node)
			result = ((Node) any).accept(this);
		else
			throw failure;
		return result;
	}

	public abstract Node visitNode(Node x) throws jjtraveler.VisitFailure;
}
