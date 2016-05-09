package jjtraveler.reflective;

import jjtraveler.VisitFailure;
import jjtraveler.Visitable;
import jjtraveler.Visitor;

/**
 * A visitor for visiting VisitableVisitors
 */

public abstract class VisitorVisitor implements Visitor
{

	public Visitable visit(Visitable any) throws VisitFailure
	{
		if (any instanceof VisitableVisitor) { return visitVisitor((VisitableVisitor) any); }
		throw new VisitFailure();
	}

	public abstract VisitableVisitor visitVisitor(VisitableVisitor any) throws VisitFailure;

}
