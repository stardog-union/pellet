package jjtraveler.util;

import jjtraveler.LogVisitor;
import jjtraveler.Logger;
import jjtraveler.Node;
import jjtraveler.Visitor;

import junit.framework.TestCase;

/**
 * This extension of TestCase can be used to test generic visitor
 * combinators.
 */

public abstract class VisitorTestCase extends TestCase
{

	/**
	 * Nodes in a simple tree that can be used for
	 * testing traversals.
	 * Names correspond to paths in the tree:
	 *
	 * <pre>
	 *        n0
	 *      /    \
	 *    n1     n2
	 *    / \
	 * n11  n12
	 * </pre>
	 */
	protected Node n0;
	protected Node n1;
	protected Node n11;
	protected Node n12;
	protected Node n2;

	public Node buildTree()
	{
		n11 = new Node(); // Node-0
		n12 = new Node(); // Node-1
		n1 = new Node(new Node[] { n11, n12 }); // Node-2
		n2 = new Node(); // Node-3
		n0 = new Node(new Node[] { n1, n2 }); // Node-4
		return n0;
	}

	protected Node rootOfDiamond;

	void buildDiamond()
	{
		final Node sink = new Node();
		rootOfDiamond = new Node(new Node[] { sink, sink });
	}

	protected Node rootOfCircle;

	void buildCircle()
	{
		final Node node = new Node(new Node[] { null });
		rootOfCircle = new Node(new Node[] { node });
		node.setChildAt(0, rootOfCircle);
	}

	public Logger logger;

	@Override
	protected void setUp()
	{
		Node.reset();
		buildTree();
		buildDiamond();
		buildCircle();
		logger = new Logger();
	}

	/**
	 * Many test cases will need a logging visitor:
	 * this methods returns one.
	 */
	public LogVisitor logVisitor(final Visitor v)
	{
		return new LogVisitor(v, logger);
	}

	public VisitorTestCase(final String name)
	{
		super(name);
	}
}
