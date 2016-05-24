package jjtraveler.reflective;

import jjtraveler.Sequence;
import jjtraveler.Visitable;

public class VisitableSequence<T extends Visitable> extends Sequence<T> implements VisitableVisitor<T>
{

	@Override
	public int getChildCount()
	{
		return 2;
	}

	@SuppressWarnings("unchecked")
	@Override
	public T getChildAt(int i)
	{
		switch (i)
		{
			case 0:
				return (T) first;
			case 1:
				return (T) then;
			default:
				throw new IndexOutOfBoundsException();
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public T setChildAt(int i, Visitable child)
	{
		switch (i)
		{
			case 0:
				first = (VisitableVisitor<T>) child;
				return (T) this; // because VisitableSequence is Visitable.
			case 1:
				then = (VisitableVisitor<T>) child;
				return (T) this; // because VisitableSequence is Visitable.
			default:
				throw new IndexOutOfBoundsException();
		}
	}

	public VisitableSequence(VisitableVisitor<T> first, VisitableVisitor<T> then)
	{
		super(first, then);
	}

}
