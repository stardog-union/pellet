package jjtraveler.util;

import jjtraveler.Visitable;
import jjtraveler.VoidVisitor;

public class ToStringVisitor<T extends Visitable> extends VoidVisitor<T>
{
	protected String string = "[WARNING] ToStringVisitor has not been invoked";

	public static String doToString(Visitable visitable)
	{
		final ToStringVisitor<Visitable> v = new ToStringVisitor<>();
		return v.visitableToString(visitable);
	}

	public String visitableToString(Visitable visitable)
	{
		voidVisit(visitable);
		return getString();
	}

	@Override
	public void voidVisit(Visitable visitable)
	{
		string = visitable.toString();
	}

	public String getString()
	{
		return string;
	}
}
