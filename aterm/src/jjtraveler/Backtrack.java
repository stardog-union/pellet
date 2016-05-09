package jjtraveler;

/*
   The Backtrack(StateVisitor) combinator saves the state of its
   argument visitor before executing it, and restores this state
   afterwards. Note that the argument visitor should clone its state
   before modifying it, otherwise state restoration will not work
   properly.
 */

public class Backtrack implements Visitor
{

	StateVisitor v;

	public Backtrack(final StateVisitor v)
	{
		this.v = v;
	}

	@Override
	public Visitable visit(final Visitable x) throws VisitFailure
	{
		final Object state = v.getState();
		final Visitable result = v.visit(x);
		v.setState(state);
		return result;
	}

}
