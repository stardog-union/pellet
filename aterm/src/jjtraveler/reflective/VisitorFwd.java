package jjtraveler.reflective;

import jjtraveler.VisitFailure;
import jjtraveler.Visitable;
import jjtraveler.Visitor;

public class VisitorFwd<T extends Visitable> extends VisitorVisitor<T>
{

	Visitor<T> visitor;

	public VisitorFwd(Visitor<T> visitor)
	{
		this.visitor = visitor;
	}

	@SuppressWarnings("unchecked")
	@Override
	public VisitableVisitor<T> visitVisitor(T x) throws VisitFailure
	{
		final T result = visitor.visit(x);
		if (result instanceof VisitableVisitor)
			return (VisitableVisitor<T>) result;
		else
			throw new VisitFailure();
	}
}
