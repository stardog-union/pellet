// Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package org.mindswap.pellet.utils;

import aterm.ATermAppl;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import org.mindswap.pellet.taxonomy.Taxonomy;
import org.mindswap.pellet.taxonomy.TaxonomyNode;

/**
 * <p>
 * Title: TaxonomyUtils
 * </p>
 * <p>
 * Description: Utilities for manipulating taxonomy _data structure
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
public class TaxonomyUtils
{

	public static final Object INSTANCES_KEY = new Object();
	public static final Object SUPER_EXPLANATION_KEY = new Object();

	public static boolean addSuperExplanation(final Taxonomy<ATermAppl> t, final ATermAppl sub, final ATermAppl sup, final Set<ATermAppl> explanation)
	{
		@SuppressWarnings("unchecked")
		Map<ATermAppl, Set<Set<ATermAppl>>> map = (Map<ATermAppl, Set<Set<ATermAppl>>>) t.getDatum(sub, SUPER_EXPLANATION_KEY);
		Set<Set<ATermAppl>> explanations;
		if (map == null)
		{
			if (t.contains(sub))
			{
				map = new HashMap<>();
				t.putDatum(sub, SUPER_EXPLANATION_KEY, map);
				explanations = null;
			}
			else
				throw new RuntimeException(sub + " is an unknown class!");
		}
		else
			explanations = map.get(sup);

		if (explanations == null)
		{
			explanations = new HashSet<>();
			map.put(sup, explanations);
		}

		return explanations.add(explanation);
	}

	public static void clearSuperExplanation(final Taxonomy<ATermAppl> t, final ATermAppl c)
	{
		t.removeDatum(c, SUPER_EXPLANATION_KEY);
	}

	public static void clearAllInstances(final Taxonomy<?> t)
	{
		for (final TaxonomyNode<?> node : t.getNodes())
			node.removeDatum(INSTANCES_KEY);
	}

	/**
	 * Retrieve all instances of a class (based on the _current state of the taxonomy)
	 *
	 * @param t the taxonomy
	 * @param c the class
	 * @return a set of all individuals that are instances of the class
	 */
	public static <T, I> Set<I> getAllInstances(final Taxonomy<T> t, final T c)
	{
		final Iterator<Object> i = t.depthFirstDatumOnly(c, INSTANCES_KEY);
		if (!i.hasNext())
			throw new RuntimeException(c + " is an unknown class!");

		final Set<I> instances = new HashSet<>();
		do
		{
			@SuppressWarnings("unchecked")
			final Set<I> current = (Set<I>) i.next();
			if (current != null)
				instances.addAll(current);

		} while (i.hasNext());

		return Collections.unmodifiableSet(instances);
	}

	/**
	 * Retrieve direct instances of a class (based on _current state of the taxonomy)
	 *
	 * @param t the taxonomy
	 * @param c the class
	 * @return a set of individuals that are instances of {@code c} and not instances of any class {@code d} where {@code subClassOf(d,c)}
	 */
	public static <T, I> Set<I> getDirectInstances(final Taxonomy<T> t, final T c)
	{
		@SuppressWarnings("unchecked")
		final Set<I> instances = (Set<I>) t.getDatum(c, INSTANCES_KEY);
		if (instances == null)
		{
			if (t.contains(c))
				return Collections.emptySet();

			throw new RuntimeException(c + " is an unknown class!");
		}

		return Collections.unmodifiableSet(instances);
	}

	public static Set<Set<ATermAppl>> getSuperExplanations(final Taxonomy<ATermAppl> t, final ATermAppl sub, final ATermAppl sup)
	{
		@SuppressWarnings("unchecked")
		final Map<ATermAppl, Set<Set<ATermAppl>>> map = (Map<ATermAppl, Set<Set<ATermAppl>>>) t.getDatum(sub, SUPER_EXPLANATION_KEY);
		if (map == null)
			return null;

		final Set<Set<ATermAppl>> explanations = map.get(sup);
		if (explanations == null)
			return null;

		return Collections.unmodifiableSet(explanations);
	}

	/**
	 * Get classes of which the _individual is an instance (based on the _current state of the taxonomy)
	 *
	 * @param t the taxonomy
	 * @param ind the _individual
	 * @param directOnly {@code true} if only most specific classes are desired, {@code false} if all classes are desired
	 * @return a set of sets of classes where each inner set is a collection of equivalent classes
	 */
	public static <T> Set<Set<T>> getTypes(final Taxonomy<T> t, final Object ind, final boolean directOnly)
	{
		final Set<Set<T>> types = new HashSet<>();
		final Iterator<Map.Entry<Set<T>, Object>> i = t.datumEquivalentsPair(INSTANCES_KEY);
		while (i.hasNext())
		{
			final Map.Entry<Set<T>, Object> pair = i.next();
			@SuppressWarnings("unchecked")
			final Set<T> instances = (Set<T>) pair.getValue();
			if (instances != null && instances.contains(ind))
			{
				types.add(pair.getKey());
				if (!directOnly)
				{
					final T a = pair.getKey().iterator().next();
					types.addAll(t.getSupers(a));
				}
			}
		}
		return Collections.unmodifiableSet(types);
	}

	/**
	 * Determine if an _individual is an instance of a class (based on the _current state of the taxonomy)
	 *
	 * @param t the taxonomy
	 * @param ind the _individual
	 * @param c the class
	 * @return a boolean {@code true} if {@code instanceOf(ind,c)}, {@code false} else
	 */
	public static boolean isType(final Taxonomy<ATermAppl> t, final ATermAppl ind, final ATermAppl c)
	{
		final Iterator<Object> i = t.depthFirstDatumOnly(c, INSTANCES_KEY);
		if (!i.hasNext())
			throw new RuntimeException(c + " is an unknown class!");

		do
		{
			@SuppressWarnings("unchecked")
			final Set<ATermAppl> instances = (Set<ATermAppl>) i.next();
			if (instances != null && instances.contains(ind))
				return true;

		} while (i.hasNext());

		return false;
	}
}
