package jjtraveler.reflective;

import jjtraveler.Sequence;
import jjtraveler.Visitable;

public class VisitableSequence extends Sequence implements VisitableVisitor
{

	public int getChildCount()
	{
		return 2;
	}

	public Visitable getChildAt(int i)
	{
		switch (i)
		{
			case 0:
				return (VisitableVisitor) first;
			case 1:
				return (VisitableVisitor) then;
			default:
				throw new IndexOutOfBoundsException();
		}
	}

	public Visitable setChildAt(int i, Visitable child)
	{
		switch (i)
		{
			case 0:
				first = (VisitableVisitor) child;
				return this;
			case 1:
				then = (VisitableVisitor) child;
				return this;
			default:
				throw new IndexOutOfBoundsException();
		}
	}

	public VisitableSequence(VisitableVisitor first, VisitableVisitor then)
	{
		super(first, then);
	}

}
