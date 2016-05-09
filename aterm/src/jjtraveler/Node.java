package jjtraveler;

/**
 * An implementation of the <code>Visitable</code> interface for
 * testing purposes.
 */

public class Node implements jjtraveler.Visitable
{

	Node[] kids;
	int nodeID;

	static int nodeCounter = 0;

	/**
	 * Create a new node with given kids. Each created node will have
	 * a different nodeID.
	 */
	public static Node factory(final Node[] kids)
	{
		final Node result = new Node(kids, nodeCounter);
		nodeCounter++;
		return result;
	}

	public static void reset()
	{
		nodeCounter = 0;
	}

	public Node(final Node[] kids, final int nodeID)
	{
		this.kids = kids;
		this.nodeID = nodeID;
	}

	public Node()
	{
		kids = new Node[] {};
		nodeID = nodeCounter++;
	}

	public Node(final Node[] kids)
	{
		this.kids = kids;
		nodeID = nodeCounter++;
	}

	public Node accept(final NodeVisitor v) throws jjtraveler.VisitFailure
	{
		return v.visitNode(this);
	}

	@Override
	public int getChildCount()
	{
		return kids.length;
	}

	@Override
	public jjtraveler.Visitable getChildAt(final int i)
	{
		return kids[i];
	}

	@Override
	public jjtraveler.Visitable setChildAt(final int i, final jjtraveler.Visitable child)
	{
		kids[i] = (Node) child;
		return this;
	}

	@Override
	public String toString()
	{
		return "Node-" + nodeID;
	}
}
