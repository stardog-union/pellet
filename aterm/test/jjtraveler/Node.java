package jjtraveler;

/**
 * An implementation of the <code>Visitable</code> interface for
 * testing purposes.
 */

public class Node implements Visitable
{

	Node[] _kids;
	int _nodeID;

	static int _nodeCounter = 0;

	/**
	 * Create a new node with given _kids. Each created node will have
	 * a different _nodeID.
	 */
	public static Node factory(final Node[] kids)
	{
		final Node result = new Node(kids, _nodeCounter);
		_nodeCounter++;
		return result;
	}

	public static void reset()
	{
		_nodeCounter = 0;
	}

	public Node(final Node[] kids, final int nodeID)
	{
		this._kids = kids;
		this._nodeID = nodeID;
	}

	public Node()
	{
		_kids = new Node[] {};
		_nodeID = _nodeCounter++;
	}

	public Node(final Node[] kids)
	{
		this._kids = kids;
		_nodeID = _nodeCounter++;
	}

	public Node accept(final NodeVisitor v) throws jjtraveler.VisitFailure
	{
		return v.visitNode(this);
	}

	@Override
	public int getChildCount()
	{
		return _kids.length;
	}

	@Override
	public Visitable getChildAt(final int i)
	{
		return _kids[i];
	}

	@Override
	public Visitable setChildAt(final int i, final jjtraveler.Visitable child)
	{
		_kids[i] = (Node) child;
		return this;
	}

	@Override
	public String toString()
	{
		return "Node-" + _nodeID;
	}
}
