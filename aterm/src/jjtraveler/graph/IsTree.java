package jjtraveler.graph;

import jjtraveler.Not;
import jjtraveler.TopDown;

/**
 * Checks whether the current visitable is the root of a tree
 * structure: an object graph without cycles where every object has a
 * single parent.
 */

public class IsTree extends TopDown
{

	/**
	 * <code>IsTree = TopDown(Not(Visited))</code>
	 */
	public IsTree()
	{
		super(new Not(new Visited()));
	}
}
