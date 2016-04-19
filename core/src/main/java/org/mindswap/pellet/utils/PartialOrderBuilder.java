// Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package org.mindswap.pellet.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import org.mindswap.pellet.taxonomy.Taxonomy;
import org.mindswap.pellet.taxonomy.TaxonomyNode;

/**
 * <p>
 * Title: Partial Order Builder
 * </p>
 * <p>
 * Description:
 * </p>
 * <p>
 * Copyright: Copyright (c) 2007
 * </p>
 * <p>
 * Company: Clark & Parsia, LLC. <http://www.clarkparsia.com>
 * </p>
 *
 * @author Mike Smith
 */
public class PartialOrderBuilder<T>
{

	private static final boolean CHILDREN_SEARCH = false;
	private static final boolean PARENTS_SEARCH = true;

	public static <T> Taxonomy<T> build(final Collection<? extends T> elements, final PartialOrderComparator<T> comparator)
	{
		return build(elements, comparator, null, null);
	}

	public static <T> Taxonomy<T> build(final Collection<? extends T> elements, final PartialOrderComparator<T> comparator, final T top, final T bottom)
	{
		final Taxonomy<T> hierarchy = new Taxonomy<>(null, top, bottom);
		final PartialOrderBuilder<T> builder = new PartialOrderBuilder<>(hierarchy, comparator);
		builder.addAll(elements);

		return hierarchy;
	}

	private PartialOrderComparator<T> comparator;

	private Taxonomy<T> taxonomy;

	/**
	 * Initialize the builder with given taxonomy and comparator.
	 */
	public PartialOrderBuilder(final Taxonomy<T> taxonomy, final PartialOrderComparator<T> comparator)
	{
		this.taxonomy = taxonomy;
		this.comparator = comparator;
	}

	public void add(final T toAdd)
	{
		add(toAdd, false);
	}

	/**
	 * Add a new element to the partial order of this builder with its comparator.
	 *
	 * @param toAdd the element to be added
	 */
	public void add(final T toAdd, final boolean hidden)
	{

		final Set<T> elements = taxonomy.getClasses();
		final int nElements = elements.size();

		if (nElements == 0)
		{
			final Set<T> empty = Collections.emptySet();
			taxonomy.addNode(Collections.singleton(toAdd), empty, empty, /* hidden = */false);
		}
		else
		{

			Collection<T> parents;
			Collection<T> children;

			// From max, work down to find parents
			{
				final Collection<T> maxElements = new ArrayList<>();
				for (final TaxonomyNode<T> n : taxonomy.getTop().getSubs())
					maxElements.add(n.getName());
				parents = search(taxonomy, toAdd, maxElements, comparator, PARENTS_SEARCH);

				if (parents == null)
					return;
			}

			// From the leaves reachable from parents, identify children
			{
				final Collection<T> leaves = new ArrayList<>();

				// Collect reachable leaves first
				{
					final Set<T> visited = new HashSet<>();
					final Queue<Set<T>> toVisit = new LinkedList<>();
					if (parents.isEmpty())
						for (final TaxonomyNode<T> n : taxonomy.getBottom().getSupers())
							leaves.add(n.getName());
					else
						for (final T p : parents)
						{
							final Set<Set<T>> subs = taxonomy.getSubs(p, /* direct = */true);
							if (!subs.isEmpty())
								toVisit.addAll(subs);
						}

					final Set<Set<T>> bottoms = Collections.singleton(taxonomy.getBottom().getEquivalents());

					while (!toVisit.isEmpty())
					{
						final Set<T> current = toVisit.remove();
						assert !current.isEmpty();
						final T rep = current.iterator().next();

						if (visited.contains(rep))
							continue;
						visited.addAll(current);
						final Set<Set<T>> subs = taxonomy.getSubs(rep, /* direct = */true);
						if (subs.equals(bottoms))
							leaves.add(rep);
						else
							toVisit.addAll(subs);
					}
				}

				children = search(taxonomy, toAdd, leaves, comparator, CHILDREN_SEARCH);
				if (children == null)
					return;
			}

			taxonomy.addNode(Collections.singleton(toAdd), parents, children, hidden);
		}
	}

	/**
	 * Adds a collection of elements to the partial order.
	 *
	 * @param elements new elements to add
	 */
	public void addAll(final Collection<? extends T> elements)
	{
		for (final T toInsert : elements)
			add(toInsert);
	}

	public PartialOrderComparator<T> getComparator()
	{
		return comparator;
	}

	public Taxonomy<T> getTaxonomy()
	{
		return taxonomy;
	}

	private Collection<T> search(final Taxonomy<T> tax, final T toInsert, final Collection<T> from, final PartialOrderComparator<T> comparator, final boolean direction)
	{

		final Collection<T> retSet = new ArrayList<>();

		final Queue<Map<T, ? extends Collection<T>>> pending = new LinkedList<>();
		pending.add(Collections.singletonMap((T) null, from));

		final Set<T> visited = new HashSet<>();

		/*
		 * Comment written as if maxToMin == true Each pass over the loop a node
		 * (called the candidate) and its children are pulled from the pending
		 * queue. If the node to be inserted is less than one of the children,
		 * that child and its children are pushed on to the pending queue. If
		 * the node to be inserted is equal to one of the children, it is
		 * inserted and processing stops. If the node to be inserted is not less
		 * than or equal to any of the children, the candidate node is a parent.
		 * The loop is iterated until the queue is empty, at which point all
		 * parents have been identified.
		 */
		while (!pending.isEmpty())
		{
			final Map.Entry<T, ? extends Collection<T>> pair = pending.remove().entrySet().iterator().next();
			final T candidate = pair.getKey();

			if (candidate != null)
			{
				if (visited.contains(candidate))
					continue;
				visited.addAll(tax.getAllEquivalents(candidate));
			}

			boolean hasSuccessors = false;
			final Collection<T> candidatesChildren = pair.getValue();
			for (final T child : candidatesChildren)
				switch (comparator.compare(toInsert, child))
				{
					case LESS:
						if (direction == PARENTS_SEARCH)
						{
							final Set<Set<T>> subs = tax.getSubs(child, /* direct = */true);
							final Collection<T> next = new ArrayList<>(subs.size());
							for (final Set<T> sub : subs)
								next.add(sub.iterator().next());
							pending.add(Collections.singletonMap(child, next));
							hasSuccessors = true;
						}
						break;
					case EQUAL:
						tax.addEquivalents(child, Collections.singleton(toInsert));
						return null;
					case GREATER:
						if (direction == CHILDREN_SEARCH)
						{
							final Set<Set<T>> sups = tax.getSupers(child, /* direct = */true);
							final Collection<T> next = new ArrayList<>(sups.size());
							for (final Set<T> sup : sups)
								next.add(sup.iterator().next());
							pending.add(Collections.singletonMap(child, next));
							hasSuccessors = true;
						}
						break;
					case INCOMPARABLE:
						break;
				}

			if (!hasSuccessors && (candidate != null))
				retSet.add(candidate);
		}

		return retSet;
	}

	public void setComparator(final PartialOrderComparator<T> comparator)
	{
		this.comparator = comparator;
	}

	public void setTaxonomy(final Taxonomy<T> taxonomy)
	{
		this.taxonomy = taxonomy;
	}
}
