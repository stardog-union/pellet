package jjtraveler;

/**
 * Any visitor class should implement the Visitor interface.
 */

public interface Visitor
{

	/**
	 * Pay a visit to any visitable object.
	 */
	public Visitable visit(Visitable any) throws VisitFailure;

}
