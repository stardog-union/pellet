package jjtraveler;

public class IfThenElse<T extends Visitable> implements Visitor<T>
{

	Visitor<T> condition;
	Visitor<T> trueCase;
	Visitor<T> falseCase;

	public IfThenElse(final Visitor<T> c, final Visitor<T> t, final Visitor<T> f)
	{
		condition = c;
		trueCase = t;
		falseCase = f;
	}

	public IfThenElse(final Visitor<T> c, final Visitor<T> t)
	{
		condition = c;
		trueCase = t;
		falseCase = new Identity<>();
	}

	@Override
	public T visit(final T x) throws VisitFailure
	{
		boolean success;
		T result;
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
