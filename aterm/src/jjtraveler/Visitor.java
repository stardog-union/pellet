package jjtraveler;

/**
 * Any visitor class should implement the Visitor interface.
 */
public interface Visitor<T extends Visitable>
{
	/**
	 * Pay a visit to any visitable object.
	 */
	public T visit(final T any) throws VisitFailure;
}
