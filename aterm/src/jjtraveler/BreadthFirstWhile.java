package jjtraveler;

/**
 * <code>BF(v) = Seq(Try(Seq(v,All(EnQ))),IfThen(DeQ,BF(v)))</code>
 * <p>
 * Visit a tree in breadth-first order. The traversal is cut off below
 * nodes where the argument visitor fails. Guaranteed to succeed.
 */

import java.util.Collection;
import java.util.LinkedList;

public class BreadthFirstWhile
{

	public BreadthFirstWhile(final Visitor v)
	{
		pending = new LinkedList();
		this.v = v;
	}

	public BreadthFirstWhile(final Visitor v, final Collection c)
	{
		pending = new LinkedList(c);
		this.v = v;
	}

	LinkedList pending;
	Visitor v;

	public Visitable visit(final Visitable x)
	{
		Visitable result = x;
		try
		{
			result = v.visit(x);
			final int childCount = result.getChildCount();
			for (int i = 0; i < childCount; i++)
				pending.addLast(result.getChildAt(i));
		}
		catch (final VisitFailure vf)
		{
		}
		if (pending.size() != 0)
		{
			Visitable next = (Visitable) pending.removeFirst();
			next = visit(next);
		}
		return result;
	}
}
