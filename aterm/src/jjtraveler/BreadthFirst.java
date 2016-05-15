package jjtraveler;

/**
 * <code>BF(v) = Seq(v,All(EnQ),IfThen(DeQ,BF(v)))</code>
 * <p>
 * Visit a tree in breadth-first order. Fails iff the argument visitor fails
 * on any of the nodes of the tree.
 */

import java.util.Collection;
import java.util.LinkedList;

public class BreadthFirst<T extends Visitable> implements Visitor<T>
{
	LinkedList<T> pending;
	Visitor<T> v;

	public BreadthFirst(final Visitor<T> v)
	{
		pending = new LinkedList<>();
		this.v = v;
	}

	public BreadthFirst(final Visitor<T> v, final Collection<T> c)
	{
		pending = new LinkedList<>(c);
		this.v = v;
	}

	@Override
	public T visit(final T x) throws VisitFailure
	{
		final T result = v.visit(x);
		final int childCount = result.getChildCount();

		for (int i = 0; i < childCount; i++)
			pending.addLast(result.getChildAt(i));

		if (pending.size() != 0)
		{
			T next = pending.removeFirst();
			next = visit(next);
		}

		return result;
	}
}
