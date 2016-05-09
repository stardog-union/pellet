package jjtraveler.reflective;

import jjtraveler.Fail;
import jjtraveler.Visitable;

public class VisitableFail extends Fail implements VisitableVisitor
{

	public int getChildCount()
	{
		return 0;
	}

	public Visitable getChildAt(int i)
	{
		throw new IndexOutOfBoundsException();
	}

	public Visitable setChildAt(int i, Visitable child)
	{
		throw new IndexOutOfBoundsException();
	}

}
