// Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package com.clarkparsia.pellint.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
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
 */
public class CollectionUtil {
	public static <T> List<T> makeList() {
		return new ArrayList<T>();
	}
	
	public static <T> Set<T> makeSet() {
		return new HashSet<T>();
	}
	
	public static <K,V> Map<K,V> makeMap() {
		return new HashMap<K,V>();
	}

	public static <T> List<T> copy(List<? extends T> a) {
		return new ArrayList<T>(a);
	}

	public static <T> Set<T> copy(Set<? extends T> a) {
		return new HashSet<T>(a);
	}

	public static <T> Set<T> asSet(T... a) {
		return new HashSet<T>(Arrays.asList(a));
	}
}
