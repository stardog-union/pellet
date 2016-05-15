package jjtraveler;

/**
 * Top down traversal as long as a _condition holds, with separate actions
 * during the traversal and at the border.
 * This combinator can be used to express a number of other combinators;
 * these are offered through factory methods.
 *
 * @author Arie van Deursen
 * @version $Id$
 */

public class DoWhileSuccess<T extends Visitable> extends DefinedCombinator<T>
{

	private Visitor<T> action = new Identity<>();
	private Visitor<T> condition = new Identity<>();
	private Visitor<T> atBorder = new Identity<>();

	public DoWhileSuccess(final Visitor<T> condition, final Visitor<T> action)
	{
		this.condition = condition;
		this.action = action;
	}

	/**
	 * Same as TopDownWhile(v)
	 */
	public DoWhileSuccess(final Visitor<T> condition)
	{
		this.condition = condition;
	}

	/**
	 * Most generic form, having different behavior in
	 * conditions, at success, and at the failing border.
	 */
	public DoWhileSuccess(final Visitor<T> condition, final Visitor<T> action, final Visitor<T> atBorder)
	{
		this.condition = condition;
		this.action = action;
		this.atBorder = atBorder;
	}

	/**
	 * Reuse DoWhileSuccess(v) as a TopDownWhile(v).
	 */
	public static <T extends Visitable> DoWhileSuccess<T> TopDownWhile(final Visitor<T> v1)
	{
		return new DoWhileSuccess<>(v1);
	}

	/**
	 * Reuse DoWhileSuccess(v1,id,v2) as a TopDownWhile(v1,v2)
	 */
	public static <T extends Visitable> DoWhileSuccess<T> TopDownWhile(final Visitor<T> v1, final Visitor<T> v2)
	{
		return new DoWhileSuccess<>(v1, new Identity<>(), v2);
	}

	/**
	 * Reuse DoWhileSuccess(id,v,id) as a TopDown(v);
	 */
	public static <T extends Visitable> DoWhileSuccess<T> TopDown(final Visitor<T> v)
	{
		return new DoWhileSuccess<>(new Identity<>(), v, new Identity<>());
	}

	/**
	 * Reuse DoWhileSuccess(not(v)) as a TopDownUntil(v);
	 */
	public static <T extends Visitable> DoWhileSuccess<T> TopDownUntil(final Visitor<T> v1)
	{
		return new DoWhileSuccess<>(new Not<>(v1));
	}

	/**
	 * Reuse DoWhileSuccess(not(v1),id,action) to create
	 * a TopDownUntil(_condition, borderAction);
	 */
	static <T extends Visitable> DoWhileSuccess<T> TopDownUntil(final Visitor<T> condition, final Visitor<T> borderAction)
	{
		return new DoWhileSuccess<>(new Not<>(condition), new Identity<>(), borderAction);
	}

	@Override
	protected Visitor<T> getDefinition()
	{
		return new IfThenElse<>(condition, new Sequence<>(action, new All<>(this)), atBorder);
	}
}
