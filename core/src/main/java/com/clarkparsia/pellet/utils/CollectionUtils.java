// Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package com.clarkparsia.pellet.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * <p>
 * Title: Collection Utilities
 * </p>
 * <p>
 * Description:
 * </p>
 * <p>
 * Copyright: Copyright (c) 2008
 * </p>
 * <p>
 * Company: Clark & Parsia, LLC. <http://www.clarkparsia.com>
 * </p>
 *
 * @author Harris Lin
 * @author Evren Sirin
 */
public class CollectionUtils
{
	public static <K, V> Map<K, V> makeIdentityMap()
	{
		return new IdentityHashMap<>();
	}

	public static <K, V> Map<K, V> makeIdentityMap(final int size)
	{
		return new IdentityHashMap<>(size);
	}

	public static <K, V> Map<K, V> makeIdentityMap(final Map<? extends K, ? extends V> map)
	{
		return new IdentityHashMap<>(map);
	}

	public static <T> Set<T> makeIdentitySet()
	{
		return new IdentityHashSet<>();
	}

	public static <T> Set<T> makeIdentitySet(final int size)
	{
		return new IdentityHashSet<>(size);
	}

	public static <T> Set<T> makeIdentitySet(final Collection<? extends T> a)
	{
		return new IdentityHashSet<>(a);
	}

	public static <T> List<T> makeList()
	{
		return new ArrayList<>();
	}

	public static <T> List<T> makeList(final int size)
	{
		return new ArrayList<>(size);
	}

	public static <T> List<T> makeList(final Collection<? extends T> a)
	{
		return new ArrayList<>(a);
	}

	public static <K, V> Map<K, V> makeMap()
	{
		return new HashMap<>();
	}

	public static <K, V> Map<K, V> makeMap(final int size)
	{
		return new HashMap<>(size);
	}

	public static <K, V> Map<K, V> makeMap(final Map<? extends K, ? extends V> map)
	{
		return new HashMap<>(map);
	}

	public static <T> Set<T> makeSet()
	{
		return new HashSet<>();
	}

	public static <T> Set<T> makeSet(final int size)
	{
		return new HashSet<>(size);
	}

	public static <T> Set<T> makeSet(final Collection<? extends T> a)
	{
		return new HashSet<>(a);
	}
}
