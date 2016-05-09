package jjtraveler.graph;

import java.util.Collection;
import java.util.HashSet;

import jjtraveler.All;
import jjtraveler.Not;
import jjtraveler.VisitFailure;
import jjtraveler.Visitable;
import jjtraveler.VoidVisitor;

/**
 * Checks whether the current visitable is the root of a DAG: an
 * acyclic directed graph.
 */

public class IsDag extends VoidVisitor
{

	Collection visited;

	public IsDag(Collection visited)
	{
		this.visited = new HashSet(visited);
	}

	public IsDag()
	{
		this.visited = new HashSet();
	}

	public void voidVisit(Visitable x) throws VisitFailure
	{
		Visited v = new Visited(visited);
		(new Not(v)).visit(x);
		Collection c = v.getVisited();
		(new All(new IsDag(c))).visit(x);
	}

}
