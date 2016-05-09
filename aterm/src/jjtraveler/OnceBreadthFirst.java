package jjtraveler;

/**
 * <code>BF(v) = Seq(Try(Seq(v,All(EnQ))),IfThen(DeQ,BF(v)))</code>
 * <p>
 * Visit a tree in breadth-first order. The traversal is cut off below
 * nodes where the argument visitor fails. Guaranteed to succeed.
 */

import java.util.Collection;
import java.util.LinkedList;

public class OnceBreadthFirst
{

	public OnceBreadthFirst(final Visitor v)
	{
		pending = new LinkedList();
		this.v = v;
	}

	public OnceBreadthFirst(final Visitor v, final Collection c)
	{
		pending = new LinkedList(c);
		this.v = v;
	}

	LinkedList pending;
	Visitor v;

	public Visitable visit(final Visitable x) throws VisitFailure
	{
		try
		{
			return v.visit(x);
		}
		catch (final VisitFailure vf)
		{
		}
		final int childCount = x.getChildCount();
		for (int i = 0; i < childCount; i++)
			pending.addLast(x.getChildAt(i));
		if (pending.size() != 0)
		{
			Visitable next = (Visitable) pending.removeFirst();
			next = visit(next);
		}
		return x;
	}
}
