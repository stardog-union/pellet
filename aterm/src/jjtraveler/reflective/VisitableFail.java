package jjtraveler.reflective;

import jjtraveler.Fail;
import jjtraveler.Visitable;

public class VisitableFail<T extends Visitable> extends Fail<T> implements VisitableVisitor<T>
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
