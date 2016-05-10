// Portions Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// Clark & Parsia, LLC parts of this source code are available under the terms of the Affero General Public License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package org.mindswap.pellet.utils;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * DisjointSet _data structure. Uses path compression and union by rank.
 *
 * @author Evren Sirin
 */
public class DisjointSet<T>
{
	private class Node<U>
	{
		U object;
		Node<U> parent = this;
		int rank = 0;

		Node(final U o)
		{
			object = o;
		}
	}

	private final Map<T, Node<T>> elements;

	public DisjointSet()
	{
		elements = new HashMap<>();
	}

	public void add(final T o)
	{
		if (elements.containsKey(o))
			return;

		elements.put(o, new Node<>(o));
	}

	public boolean contains(final T o)
	{
		return elements.containsKey(o);
	}

	public Collection<T> elements()
	{
		return Collections.unmodifiableSet(elements.keySet());
	}

	public T find(final T o)
	{
		return findRoot(o).object;
	}

	private Node<T> findRoot(final T o)
	{
		Node<T> node = elements.get(o);
		while (node.parent.parent != node.parent)
		{
			node.parent = node.parent.parent;
			node = node.parent;
		}

		return node.parent;
	}

	public Collection<Set<T>> getEquivalanceSets()
	{

		final Map<T, Set<T>> equivalanceSets = new HashMap<>();

		for (final T x : elements.keySet())
		{
			final T representative = find(x);

			Set<T> equivalanceSet = equivalanceSets.get(representative);
			if (equivalanceSet == null)
			{
				equivalanceSet = new HashSet<>();
				equivalanceSets.put(representative, equivalanceSet);
			}
			equivalanceSet.add(x);
		}

		return equivalanceSets.values();
	}

	public boolean isSame(final T x, final T y)
	{
		return find(x).equals(find(y));
	}

	@Override
	public String toString()
	{
		final StringBuffer buffer = new StringBuffer();

		buffer.append("{");
		for (final Iterator<Node<T>> i = elements.values().iterator(); i.hasNext();)
		{
			final Node<T> node = i.next();
			buffer.append(node.object);
			buffer.append(" -> ");
			buffer.append(node.parent.object);
			if (i.hasNext())
				buffer.append(", ");
		}
		buffer.append("}");

		return buffer.toString();
	}

	public Node<T> union(final T x, final T y)
	{
		Node<T> rootX = findRoot(x);
		Node<T> rootY = findRoot(y);

		if (rootX.rank > rootY.rank)
		{
			final Node<T> node = rootX;
			rootX = rootY;
			rootY = node;
		}
		else
			if (rootX.rank == rootY.rank)
				++rootY.rank;

		rootX.parent = rootY;

		return rootY;
	}

}
