package jjtraveler;

/**
 * Go down the tree until the _condition succeeds
 * on a node -- then apply the descendant action
 * to the children of that node.
 * The search does not recurse in nodes below
 * those that meet the _condition.
 * <p>
 * This allows expressions such as Descendant(ProcedureBodyRecognizer, Descendant(SwitchRecognizer, Action)) which would apply an Action to all switch statements that in turn are contained within ProceduresBodies.
 * <p>
 * See also the Child combinator.
 *
 * @author Arie van Deursen; Jun 30, 2003
 * @version $Id$
 */
public class Descendant<T extends Visitable> extends DefinedCombinator<T>
{
	public Descendant(final Visitor<T> condition, final Visitor<T> action)
	{
		setDefinition(new TopDownUntil<>(condition, new All<>(action)));
	}
}
