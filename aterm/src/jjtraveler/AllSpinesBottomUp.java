package jjtraveler;

/**
 * Perform an action in a bottom up fashion
 * for all nodes along a spine from successfull nodes to the root,
 * going down only as long as the goDown visitor holds.
 *
 * @author Arie van Deursen, CWI
 */

public class AllSpinesBottomUp<T extends Visitable> extends DefinedCombinator<T>
{
	Visitor<T> goDown;
	Visitor<T> successNode;
	Visitor<T> action;

	public AllSpinesBottomUp(final Visitor<T> goDown, final Visitor<T> successNode, final Visitor<T> action)
	{
		this.goDown = goDown;
		this.successNode = successNode;
		this.action = action;

		setDefinition(new IfThenElse<>(successNode, action, new IfThenElse<>(goDown, new Sequence<>(new Some<>(this), action), new Fail<>())));
	}
}
