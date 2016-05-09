package jjtraveler.util;

import jjtraveler.Visitable;

public class ToXMLString extends ToStringVisitor
{

	public static String doToString(Visitable visitable)
	{
		ToXMLString v = new ToXMLString();
		return v.visitableToString(visitable);
	}

	public void voidVisit(Visitable visitable)
	{
		int childCount = visitable.getChildCount();
		String result;
		String tag = makeTag(visitable);
		if (childCount != 0)
		{
			result = "<" + tag + ">";
			for (int i = 0; i < childCount; i++)
			{
				if (i != 0)
				{
					result += " ";
				}
				result += visitableToString(visitable.getChildAt(i));
			}
			result += "</" + tag + ">";
		}
		else
		{
			result = "<" + tag + "/>";
		}
		string = result;
	}

	public static String makeTag(Visitable visitable)
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
