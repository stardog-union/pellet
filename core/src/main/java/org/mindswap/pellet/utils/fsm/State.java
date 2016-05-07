// Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package org.mindswap.pellet.utils.fsm;

import java.util.HashSet;
import java.util.Set;
import org.mindswap.pellet.Role;

/**
 * <p>
 * Title:
 * </p>
 * <p>
 * Description:
 * </p>
 * <p>
 * Copyright: Copyright (c) 2006
 * </p>
 * <p>
 * Company: Clark & Parsia, LLC. <http://www.clarkparsia.com>
 * </p>
 *
 * @author Evren Sirin
 */
public class State<T>
{
	private static int next_unused_name = 0;

	private int _name; // number of state

	private final Set<Transition<T>> _transitions; // set of outgoing edges from state

	public State()
	{
		_name = next_unused_name++;
		_transitions = new HashSet<>();
	}

	/**
	 * Create a transition from this state to the given state on given symbol
	 * 
	 * @param symbol
	 * @param s
	 */
	public void addTransition(final T symbol, final State<T> s)
	{
		if (symbol == null || s == null)
			throw new NullPointerException();

		if (!(symbol instanceof Role))
			throw new ClassCastException();

		final Transition<T> t = new Transition<>(symbol, s);
		_transitions.add(t);
	}

	/**
	 * Create an epsilon transition from this state to the given state
	 * 
	 * @param s
	 */
	public void addTransition(final State<T> s)
	{
		if (s == null)
			throw new NullPointerException();

		final Transition<T> t = new Transition<>(s);
		_transitions.add(t);
	}

	/**
	 * Returns the _transitions for originating from this state.
	 * 
	 * @return the _transitions for originating from this state
	 */
	public Set<Transition<T>> getTransitions()
	{
		return _transitions;
	}

	/**
	 * Returns a state reached from this state with the given symbol. If more than one state can be reached with the given symbol, an arbitrary one is returned.
	 *
	 * @return a state reached from this state with the given symbol, <code>null</code> otherwise
	 */
	public State<T> move(final T symbol)
	{
		for (final Transition<T> t : _transitions)
			if (t.hasName(symbol))
				return t.getTo();
		return null;
	}

	public int getName()
	{
		return _name;
	}

	public void setName(final int i)
	{
		_name = i;
	}

	@Override
	public String toString()
	{
		return String.valueOf(_name);
	}
}
