package jjtraveler.reflective;

import jjtraveler.Identity;
import jjtraveler.Visitable;

public class VisitableIdentity<T extends Visitable> extends Identity<T> implements VisitableVisitor<T>
{

	@Override
	public int getChildCount()
	{
		return 0;
	}

	@SuppressWarnings("unchecked")
	@Override
	public T getChildAt(int i)
	{
		throw new IndexOutOfBoundsException();
	}

	@SuppressWarnings("unchecked")
	@Override
	public T setChildAt(int i, Visitable child)
	{
		throw new IndexOutOfBoundsException();
	}

}
