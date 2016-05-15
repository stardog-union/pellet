package jjtraveler;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/*
 * Simple visitor recognizing two nodes given at creation time.
 * Can be used to test generic visitors requiring a recognizing
 * argument.
 */

public class FailAtNodes<T extends Visitable> implements Visitor<T>
{
	/*
	 * caching a VisitFailure for efficiency (preventing generation of a
	 * stacktrace)
	 */
	private static VisitFailure _failure = new VisitFailure();

	private final Set<T> _visitables = new HashSet<>();

	public FailAtNodes(final Collection<T> visitables)
	{
		_visitables.addAll(visitables);
	}

	public FailAtNodes(final T n)
	{
		_visitables.add(n);
	}

	public FailAtNodes(final T n1, final T n2)
	{
		_visitables.add(n1);
		_visitables.add(n2);
	}

	@Override
	public T visit(final T x) throws VisitFailure
	{
		if (_visitables.contains(x))
			throw _failure;
		return x;
	}
}
