// Portions Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// Clark & Parsia, LLC parts of this source code are available under the terms of the Affero General Public License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package org.mindswap.pellet.utils;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import org.mindswap.pellet.utils.iterator.PairIterator;

/**
 * @author Evren Sirin
 */
public class CandidateSet<T>
{
	private final Set<T> _knowns, _unknowns;

	public CandidateSet()
	{
		this._knowns = new HashSet<>();
		this._unknowns = new HashSet<>();
	}

	public CandidateSet(final Set<T> knowns)
	{
		this._knowns = new HashSet<>(knowns);
		this._unknowns = new HashSet<>();
	}

	public CandidateSet(final Set<T> knowns, final Set<T> unknowns)
	{
		this._knowns = new HashSet<>(knowns);
		this._unknowns = new HashSet<>(unknowns);
	}

	public Set<T> getKnowns()
	{
		return _knowns;
	}

	public Set<T> getUnknowns()
	{
		return _unknowns;
	}

	public void add(final T obj, final Bool isKnown)
	{
		if (isKnown.isTrue())
			_knowns.add(obj);
		else
			if (isKnown.isUnknown())
				_unknowns.add(obj);
	}

	public void update(final T obj, final Bool isCandidate)
	{
		if (isCandidate.isTrue())
		{
			// do nothing
		}
		else
			if (isCandidate.isFalse())
				remove(obj);
			else
				if (_knowns.contains(obj))
				{
					_knowns.remove(obj);
					_unknowns.add(obj);
				}
	}

	public boolean remove(final Object obj)
	{
		return _knowns.remove(obj) || _unknowns.remove(obj);
	}

	public boolean contains(final Object obj)
	{
		return _knowns.contains(obj) || _unknowns.contains(obj);
	}

	public int size()
	{
		return _knowns.size() + _unknowns.size();
	}

	public Iterator<T> iterator()
	{
		return new PairIterator<>(_knowns.iterator(), _unknowns.iterator());
	}

	@Override
	public String toString()
	{
		return "Knowns: " + _knowns.size() + " Unknowns: " + _unknowns.size();
	}
}
