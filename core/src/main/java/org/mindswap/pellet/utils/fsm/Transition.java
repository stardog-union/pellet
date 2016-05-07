// Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package org.mindswap.pellet.utils.fsm;

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
public class Transition<T>
{
	private static Object EPSILON = null;

	private final T _name;

	private State<T> _to;

	/**
	 * /* add edge with epsilon edge
	 */
	@SuppressWarnings("unchecked")
	public Transition(final State<T> t)
	{
		_name = (T) EPSILON;
		_to = t;
	}

	/**
	 * add edge for _name from _current state _to state t on c
	 */
	public Transition(final T name, final State<T> to)
	{
		this._name = name;
		this._to = to;
	}

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + ((_name == null) ? 0 : _name.hashCode());
		result = prime * result + ((_to == null) ? 0 : _to.hashCode());
		return result;
	}

	@Override
	public boolean equals(final Object obj)
	{
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		final Transition<?> other = (Transition<?>) obj;
		if (_name == null)
		{
			if (other._name != null)
				return false;
		}
		else
			if (!_name.equals(other._name))
				return false;
		if (_to == null)
		{
			if (other._to != null)
				return false;
		}
		else
			if (!_to.equals(other._to))
				return false;
		return true;
	}

	public boolean isEpsilon()
	{
		return _name == EPSILON;
	}

	public State<T> getTo()
	{
		return _to;
	}

	public void setTo(final State<T> to)
	{
		this._to = to;
	}

	public T getName()
	{
		return _name;
	}

	public boolean hasName(final T c)
	{
		return (_name == EPSILON) ? c == EPSILON : (c == EPSILON) ? false : _name.equals(c);
	}

	@Override
	public String toString()
	{
		return (_name == EPSILON ? "epsilon" : _name.toString()) + " -> " + _to.getName();
	}
}
