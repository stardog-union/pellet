package jjtraveler.graph;

import jjtraveler.All;
import jjtraveler.Identity;
import jjtraveler.Sequence;
import jjtraveler.TopDownUntil;
import jjtraveler.Visitable;
import jjtraveler.Visitor;
import jjtraveler.VoidVisitor;

/**
 * <code>MkEdgesToKids(IsKid)
 *       = TopDownUntil(Sequence(IsKid,(MkEdgeFromParent))</code>
 * <p>
 * <code>MkEdgesToKids 
 *       = MkEdgesToKids(Identity)</code>
 * <p>
 * Add edges to a given graph from the current visitable to selected visitables below the current one. By default, the selected visitables are the immediate children.
 */

public class MkEdgesToKids extends VoidVisitor
{
	Graph graph;
	Visitor isKid;

	/**
	 * Construct a visitor that adds edges to the given graph, where
	 * the current visitable is the edge source, and <code>isKid</code> is used to select the edge targets.
	 */
	public MkEdgesToKids(Graph graph, Visitor isKid)
	{
		this.graph = graph;
		this.isKid = isKid;
	}

	/**
	 * Construct a visitor that adds edges to the given graph, where
	 * the current visitable is the edge source, and its immediate
	 * children are the edge targets.
	 */
	public MkEdgesToKids(Graph graph)
	{
		this.graph = graph;
		this.isKid = new Identity();
	}

	public void voidVisit(Visitable parent) throws jjtraveler.VisitFailure
	{
		Visitor nodeAction = new Sequence(isKid, new MkEdgeFromParent(parent, graph));
		Visitor v = new All(new TopDownUntil(nodeAction));
		v.visit(parent);
	}
}
