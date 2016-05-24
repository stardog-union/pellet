package jjtraveler.reflective;

import jjtraveler.VisitFailure;
import jjtraveler.Visitable;
import jjtraveler.Visitor;

/**
 * A visitor for visiting VisitableVisitors
 */

public abstract class VisitorVisitor<T extends Visitable> implements Visitor<T>
{

	@SuppressWarnings("unchecked")
	@Override
	public T visit(T any) throws VisitFailure
	{
		if (any instanceof VisitableVisitor) { return (T) visitVisitor(any); }
		throw new VisitFailure();
	}

	public abstract VisitableVisitor<T> visitVisitor(T any) throws VisitFailure;

}
