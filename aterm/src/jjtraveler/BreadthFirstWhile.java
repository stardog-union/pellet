package jjtraveler;

/**
 * <code>BF(v) = Seq(Try(Seq(v,All(EnQ))),IfThen(DeQ,BF(v)))</code>
 * <p>
 * Visit a tree in breadth-first order. The traversal is cut off below
 * nodes where the argument visitor fails. Guaranteed to succeed.
 */

import java.util.Collection;
import java.util.LinkedList;

public class BreadthFirstWhile<T extends Visitable> implements Visitor<T>
{
	LinkedList<T> pending;
	Visitor<T> v;

	public BreadthFirstWhile(final Visitor<T> v)
	{
		pending = new LinkedList<>();
		this.v = v;
	}

	public BreadthFirstWhile(final Visitor<T> v, final Collection<T> c)
	{
		pending = new LinkedList<>(c);
		this.v = v;
	}

	@Override
	public T visit(final T x)
	{
		T result = x;
		try
		{
			result = v.visit(x);
			final int childCount = result.getChildCount();
			for (int i = 0; i < childCount; i++)
				pending.addLast(result.getChildAt(i));
		}
		catch (final VisitFailure vf)
		{
			vf.printStackTrace();
		}
		if (pending.size() != 0)
		{
			T next = pending.removeFirst();
			next = visit(next);
		}
		return result;
	}
}
