package jjtraveler;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/*
 * Simple visitor recognizing two nodes given at creation time.
 * Can be used to test generic visitors requiring a recognizing
 * argument.
 */

public class FailAtNodes implements jjtraveler.Visitor
{
	/*
	 * caching a VisitFailure for efficiency (preventing generation of a
	 * stacktrace)
	 */
	private static VisitFailure failure = new VisitFailure();

	Set visitables = new HashSet();

	public FailAtNodes(final Collection visitables)
	{
		this.visitables.addAll(visitables);
	}

	public FailAtNodes(final Visitable n)
	{
		visitables.add(n);
	}

	public FailAtNodes(final Visitable n1, final Visitable n2)
	{
		visitables.add(n1);
		visitables.add(n2);
	}

	@Override
	public Visitable visit(final Visitable x) throws VisitFailure
	{
		if (visitables.contains(x))
			throw failure;
		return x;
	}
}
