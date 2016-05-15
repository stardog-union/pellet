package jjtraveler;

/*
   The Backtrack(StateVisitor) combinator saves the state of its
   argument visitor before executing it, and restores this state
   afterwards. Note that the argument visitor should clone its state
   before modifying it, otherwise state restoration will not work
   properly.
 */

public class Backtrack<T extends Visitable> implements Visitor<T>
{
	StateVisitor<T> _v;

	public Backtrack(final StateVisitor<T> v)
	{
		this._v = v;
	}

	@Override
	public T visit(final T x) throws VisitFailure
	{
		final Object state = _v.getState();
		final T result = _v.visit(x);
		_v.setState(state);
		return result;
	}
}
