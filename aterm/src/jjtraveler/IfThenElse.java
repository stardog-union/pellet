package jjtraveler;

public class IfThenElse implements Visitor
{

	Visitor condition;
	Visitor trueCase;
	Visitor falseCase;

	public IfThenElse(final Visitor c, final Visitor t, final Visitor f)
	{
		condition = c;
		trueCase = t;
		falseCase = f;
	}

	public IfThenElse(final Visitor c, final Visitor t)
	{
		condition = c;
		trueCase = t;
		falseCase = new Identity();
	}

	@Override
	public Visitable visit(final Visitable x) throws VisitFailure
	{
		boolean success;
		Visitable result;
		try
		{
			condition.visit(x);
			success = true;
		}
		catch (final VisitFailure vf)
		{
			success = false;
		}
		if (success)
			result = trueCase.visit(x);
		else
			result = falseCase.visit(x);
		return result;
	}
}
