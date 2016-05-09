package jjtraveler;

/**
 * <code>BF(v) = Seq(v,All(EnQ),IfThen(DeQ,BF(v)))</code>
 * <p>
 * Visit a tree in breadth-first order. Fails iff the argument visitor fails
 * on any of the nodes of the tree.
 */

import java.util.Collection;
import java.util.LinkedList;

public class BreadthFirst
{

	public BreadthFirst(final Visitor v)
	{
		pending = new LinkedList();
		this.v = v;
	}

	public BreadthFirst(final Visitor v, final Collection c)
	{
		pending = new LinkedList(c);
		this.v = v;
	}

	LinkedList pending;
	Visitor v;

	public Visitable visit(final Visitable x) throws VisitFailure
	{
		final Visitable result = v.visit(x);
		final int childCount = result.getChildCount();

		for (int i = 0; i < childCount; i++)
			pending.addLast(result.getChildAt(i));

		if (pending.size() != 0)
		{
			Visitable next = (Visitable) pending.removeFirst();
			next = visit(next);
		}

		return result;
	}
}
