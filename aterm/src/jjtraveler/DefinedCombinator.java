package jjtraveler;

/**
 * Abstract class for combinators with an explicit definition.
 * This class helps to set the definition, and to invoke the
 * definition upon visit.
 *
 * @author Arie van Deursen, CWI
 * @version $Id$
 */

public abstract class DefinedCombinator implements Visitor
{

	/**
	 * The definition can be provided by setting the
	 * rhs instance variable.
	 */
	protected Visitor rhs;

	/**
	 * Provide the definition for this combinator.
	 */
	protected void setDefinition(final Visitor definition)
	{
		rhs = definition;
	}

	/**
	 * Return the defining visitor for this combinator.
	 * following the abstract method design pattern,
	 * this method can be refined in subclasses if necessary.
	 */
	protected Visitor getDefinition()
	{
		return rhs;
	}

	/**
	 * Visiting defined combinators amounts to visiting
	 * their definition.
	 */
	@Override
	public Visitable visit(final Visitable x) throws VisitFailure
	{
		return getDefinition().visit(x);
	}

}
