package jjtraveler.util;

import jjtraveler.Visitable;
import jjtraveler.VoidVisitor;

public class ToATermString extends VoidVisitor
{
	private String string = "[WARNING] ToATermString visitor has not been invoked";

	public static String doToString(Visitable visitable)
	{
		ToATermString v = new ToATermString();
		return v.visitableToString(visitable);
	}

	public String visitableToString(Visitable visitable)
	{
		voidVisit(visitable);
		return getString();
	}

	public void voidVisit(Visitable visitable)
	{
		int childCount = visitable.getChildCount();
		String result = makeAFun(visitable);
		if (childCount != 0)
		{
			result += "(";
			for (int i = 0; i < childCount; i++)
			{
				if (i != 0)
				{
					result += ",";
				}
				result += visitableToString(visitable.getChildAt(i));
			}
			result += ")";
		}
		string = result;
	}

	public String getString()
	{
		return string;
	}

	public static String makeAFun(Visitable visitable)
	{
		String result = visitable.getClass().getName();
		int dotPos = result.lastIndexOf('.');
		if (dotPos != -1)
		{
			result = result.substring(dotPos + 1);
		}
		return result;
	}

}
