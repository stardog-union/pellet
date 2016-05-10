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
// Permission is hereby granted, free of charge, to any person obtaining a copy
// of this software and associated documentation files (the "Software"), to
// deal in the Software without restriction, including without limitation the
// rights to use, copy, modify, merge, publish, distribute, sublicense, and/or
// sell copies of the Software, and to permit persons to whom the Software is
// furnished to do so, subject to the following conditions:
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

package org.mindswap.pellet.taxonomy;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Evren Sirin
 */
public class TaxonomyNode<T>
{

	private final Map<Object, Object> _dataMap = new ConcurrentHashMap<>();

	private Set<T> _equivalents;

	private boolean _hidden;

	protected Boolean _mark;
	private T _name;

	protected short _depth = 0;

	private Set<TaxonomyNode<T>> _subs = new HashSet<>(2);
	private Set<TaxonomyNode<T>> _supers = new HashSet<>();

	public TaxonomyNode(final T name, final boolean hidden)
	{
		this._name = name;
		this._hidden = hidden;

		if (name == null)
			_equivalents = Collections.emptySet();
		else
			_equivalents = Collections.singleton(name);
	}

	public TaxonomyNode(final Collection<T> equivalents, final boolean hidden)
	{

		if (equivalents == null || equivalents.isEmpty())
		{
			this._name = null;
			this._equivalents = Collections.emptySet();
		}
		else
		{
			this._name = equivalents.iterator().next();
			this._equivalents = new HashSet<>(equivalents);
		}

		this._hidden = hidden;
	}

	public void addEquivalent(final T t)
	{
		if (_equivalents.size() < 2)
			_equivalents = new HashSet<>(_equivalents);

		_equivalents.add(t);
	}

	public void addSub(final TaxonomyNode<T> other)
	{
		if (this.equals(other) || _subs.contains(other))
			return;

		_subs.add(other);
		if (!_hidden)
			other._supers.add(this);
	}

	public void addSubs(final Collection<TaxonomyNode<T>> others)
	{
		for (final TaxonomyNode<T> t : others)
			addSub(t);
	}

	public void addSupers(final Collection<TaxonomyNode<T>> others)
	{
		_supers.addAll(others);
		if (!_hidden)
			for (final TaxonomyNode<T> other : others)
				other._subs.add(this);
	}

	public void clearData()
	{
		_dataMap.clear();
	}

	public boolean contains(final T t)
	{
		return _equivalents.contains(t);
	}

	public void disconnect()
	{
		for (final Iterator<TaxonomyNode<T>> j = _subs.iterator(); j.hasNext();)
		{
			final TaxonomyNode<T> sub = j.next();
			j.remove();
			sub._supers.remove(this);
		}

		for (final Iterator<TaxonomyNode<T>> j = _supers.iterator(); j.hasNext();)
		{
			final TaxonomyNode<T> sup = j.next();
			j.remove();
			sup._subs.remove(this);
		}
	}

	public Object getDatum(final Object key)
	{
		return _dataMap.get(key);
	}

	public Set<T> getEquivalents()
	{
		return _equivalents;
	}

	public T getName()
	{
		return _name;
	}

	public Collection<TaxonomyNode<T>> getSubs()
	{
		return _subs;
	}

	public Collection<TaxonomyNode<T>> getSupers()
	{
		return _supers;
	}

	public boolean isBottom()
	{
		return _subs.isEmpty();
	}

	public boolean isHidden()
	{
		return _hidden;
	}

	public boolean isLeaf()
	{
		return _subs.size() == 1 && _subs.iterator().next().isBottom();
	}

	public boolean isTop()
	{
		return _supers.isEmpty();
	}

	public void print()
	{
		print("");
	}

	public void print(String indent)
	{
		if (_subs.isEmpty())
			return;

		System.out.print(indent);
		final Iterator<T> i = _equivalents.iterator();
		while (i.hasNext())
		{
			System.out.print(i.next());
			if (i.hasNext())
				System.out.print(" = ");
		}
		System.out.println();

		indent += "  ";
		for (final TaxonomyNode<T> sub : _subs)
			sub.print(indent);
	}

	public Object putDatum(final Object key, final Object value)
	{
		return _dataMap.put(key, value);
	}

	public Object removeDatum(final Object key)
	{
		return _dataMap.remove(key);
	}

	public void removeMultiplePaths()
	{
		if (!_hidden)
			for (final TaxonomyNode<T> sup : _supers)
				for (final TaxonomyNode<T> sub : _subs)
					sup.removeSub(sub);
	}

	public void removeEquivalent(final T t)
	{
		_equivalents.remove(t);

		if (_name != null && _name.equals(t))
			_name = _equivalents.iterator().next();
	}

	public void removeSub(final TaxonomyNode<T> other)
	{
		_subs.remove(other);
		other._supers.remove(this);
	}

	public void setHidden(final boolean hidden)
	{
		this._hidden = hidden;
	}

	@Deprecated
	public void setSubs(final Set<TaxonomyNode<T>> subs)
	{
		this._subs = subs;
	}

	@Deprecated
	public void setSupers(final Set<TaxonomyNode<T>> supers)
	{
		this._supers = supers;
	}

	@Override
	public String toString()
	{
		return _name.toString();// + " = " + _equivalents;
	}
}
