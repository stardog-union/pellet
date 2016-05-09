package jjtraveler.graph;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import jjtraveler.VisitFailure;
import jjtraveler.Visitable;
import jjtraveler.VoidVisitor;

/**
 * Succeed if a node was not visited before. Fail otherwise.
 * In case of extensive use of "while not visited",
 * it is relevant for performance reasons to avoid the Not operator,
 * which causes exceptions to be thrown for every node.
 *
 * <p>
 * 
 * @author Arie van Deursen
 */

public class NotVisited extends VoidVisitor
{

	Set visited = new HashSet();

	/**
	 * Create with an initially empty collection of nodes that have
	 * already been visited.
	 */
	public NotVisited()
	{
	}

	public void voidVisit(Visitable x) throws VisitFailure
	{
		if (!visited.contains(x))
		{
			visited.add(x);
		}
		else
		{
			throw new VisitFailure();
		}
	}

	/**
	 * Return collection of visitables visited so far.
	 */
	public Collection getVisited()
	{
		return visited;
	}
}
