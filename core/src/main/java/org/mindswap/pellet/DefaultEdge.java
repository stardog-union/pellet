// Portions Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// Clark & Parsia, LLC parts of this source code are available under the terms of the Affero General Public License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com
//
// ---
// Portions Copyright (c) 2003 Ron Alford, Mike Grove, Bijan Parsia, Evren Sirin
// Alford, Grove, Parsia, Sirin parts of this source code are available under the terms of the MIT License.
//
// The MIT License
//
// Permission is hereby granted, free of charge, _to any person obtaining a copy
// of this software and associated documentation files (the "Software"), _to
// deal in the Software without restriction, including without limitation the
// rights _to use, copy, modify, merge, publish, distribute, sublicense, and/or
// sell copies of the Software, and _to permit persons _to whom the Software is
// furnished _to do so, subject _to the following conditions:
//
// The above copyright notice and this permission notice shall be included in
// all copies or substantial portions of the Software.
//
// THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
// IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
// FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
// AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
// LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
// FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS
// IN THE SOFTWARE.

package org.mindswap.pellet;

import openllet.aterm.ATermAppl;

/**
 * @author Evren Sirin
 */
public class DefaultEdge implements Edge
{
	private final Individual _from;
	private final Node _to;
	private final Role _role;

	private DependencySet _depends;

	public DefaultEdge(final Role name, final Individual from, final Node to)
	{
		this._role = name;
		this._from = from;
		this._to = to;
	}

	public DefaultEdge(final Role name, final Individual from, final Node to, final DependencySet d)
	{
		this._role = name;
		this._from = from;
		this._to = to;
		this._depends = d;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Node getNeighbor(final Node node)
	{
		if (_from.equals(node))
			return _to;
		else
			if (_to.equals(node))
				return _from;
			else
				return null;
	}

	@Override
	public String toString()
	{
		return "[" + _from + ", " + _role + ", " + _to + "] - " + _depends;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public DependencySet getDepends()
	{
		return _depends;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Individual getFrom()
	{
		return _from;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Role getRole()
	{
		return _role;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Node getTo()
	{
		return _to;
	}

	@Override
	public boolean equals(final Object other)
	{
		if (this == other)
			return true;
		if (!(other instanceof DefaultEdge))
			return false;
		final DefaultEdge that = (DefaultEdge) other;
		return _from.equals(that._from) && _role.equals(that._role) && _to.equals(that._to);
	}

	@Override
	public int hashCode()
	{
		int hashCode = 23;

		hashCode = 31 * hashCode + _role.hashCode();
		hashCode = 31 * hashCode + _from.hashCode();
		hashCode = 31 * hashCode + _to.hashCode();

		return hashCode;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ATermAppl getFromName()
	{
		return getFrom().getName();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ATermAppl getToName()
	{
		return getTo().getName();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setDepends(final DependencySet ds)
	{
		_depends = ds;
	}
}
