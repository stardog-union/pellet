package jjtraveler.graph;

import jjtraveler.Visitable;
import jjtraveler.VoidVisitor;

public class MkEdgeFromParent extends VoidVisitor
{
	Visitable parent;
	Graph graph;

	public MkEdgeFromParent(Visitable parent, Graph graph)
	{
		this.parent = parent;
		this.graph = graph;
	}

	public void voidVisit(Visitable kid)
	{
		graph.addEdge(parent, kid);
	}
}
