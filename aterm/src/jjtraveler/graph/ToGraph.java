package jjtraveler.graph;

import jjtraveler.Sequence;
import jjtraveler.TopDownUntil;
import jjtraveler.Visitor;

/**
 * <code>ToGraph(isNode)
 *       = TopDownUntil(Sequence(isNode,
                        MkEdgesToKids(Sequence(isNode,
                                               ToGraph(isNode)))))</code>
 * <p>
 *
 * Creates a graph representation from a given object graph, where <code>isNode</code> determines for which objects a node is generated.
 */

public class ToGraph extends TopDownUntil
{

	public ToGraph(Graph graph, Visitor isNode)
	{
		super(null);
		Visitor nodeAction = new Sequence(isNode, new MkEdgesToKids(graph, new Sequence(isNode, this)));
		setArgument(nodeAction);
	}
}
