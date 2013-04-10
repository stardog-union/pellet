// Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package org.mindswap.pellet.utils;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.mindswap.pellet.taxonomy.Taxonomy;
import org.mindswap.pellet.taxonomy.TaxonomyNode;

import aterm.ATermAppl;

/**
 * <p>
 * Title: TaxonomyUtils
 * </p>
 * <p>
 * Description: Utilities for manipulating taxonomy data structure
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
public class TaxonomyUtils {

	public static final Object	INSTANCES_KEY			= new Object();
	public static final Object	SUPER_EXPLANATION_KEY	= new Object();

	public static boolean addSuperExplanation(Taxonomy<ATermAppl> t, ATermAppl sub, ATermAppl sup,
			Set<ATermAppl> explanation) {

		Map<ATermAppl, Set<Set<ATermAppl>>> map = (Map<ATermAppl, Set<Set<ATermAppl>>>) t.getDatum(
				sub, SUPER_EXPLANATION_KEY );
		Set<Set<ATermAppl>> explanations;
		if( map == null ) {
			if( t.contains( sub ) ) {
				map = new HashMap<ATermAppl, Set<Set<ATermAppl>>>();
				t.putDatum( sub, SUPER_EXPLANATION_KEY, map );
				explanations = null;
			}
            else {
	            throw new RuntimeException( sub + " is an unknown class!" );
            }
		}
        else {
	        explanations = map.get( sup );
        }

		if( explanations == null ) {
			explanations = new HashSet<Set<ATermAppl>>();
			map.put( sup, explanations );
		}

		return explanations.add( explanation );
	}
	
	public static void clearSuperExplanation(Taxonomy<ATermAppl> t, ATermAppl c) {
		t.removeDatum( c, SUPER_EXPLANATION_KEY );
	}
	
	public static void clearAllInstances(Taxonomy<?> t) {
		for( TaxonomyNode<?> node : t.getNodes() ) {
			node.removeDatum( INSTANCES_KEY );
		}	
	}

	/**
	 * Retrieve all instances of a class (based on the current state of the
	 * taxonomy)
	 * 
	 * @param t
	 *            the taxonomy
	 * @param c
	 *            the class
	 * @return a set of all individuals that are instances of the class
	 */
	public static <T, I> Set<I> getAllInstances(Taxonomy<T> t, T c) {
		Iterator<Object> i = t.depthFirstDatumOnly( c, INSTANCES_KEY );
		if( !i.hasNext() ) {
	        throw new RuntimeException( c + " is an unknown class!" );
        }

		Set<I> instances = new HashSet<I>();
		do {
			Set<I> current = (Set<I>) i.next();
			if( current != null ) {
	            instances.addAll( current );
            }

		} while( i.hasNext() );

		return Collections.unmodifiableSet( instances );
	}

	/**
	 * Retrieve direct instances of a class (based on current state of the
	 * taxonomy)
	 * 
	 * @param t
	 *            the taxonomy
	 * @param c
	 *            the class
	 * @return a set of individuals that are instances of {@code c} and not
	 *         instances of any class {@code d} where {@code subClassOf(d,c)}
	 */
	public static <T, I> Set<I> getDirectInstances(Taxonomy<T> t, T c) {
		Set<I> instances = (Set<I>) t.getDatum( c, INSTANCES_KEY );
		if( instances == null ) {
			if( t.contains( c ) ) {
	            return Collections.emptySet();
            }

			throw new RuntimeException( c + " is an unknown class!" );
		}

		return Collections.unmodifiableSet( instances );
	}

	public static Set<Set<ATermAppl>> getSuperExplanations(Taxonomy<ATermAppl> t, ATermAppl sub,
			ATermAppl sup) {

		Map<ATermAppl, Set<Set<ATermAppl>>> map = (Map<ATermAppl, Set<Set<ATermAppl>>>) t.getDatum(
				sub, SUPER_EXPLANATION_KEY );
		if( map == null ) {
			return null;
		}

		Set<Set<ATermAppl>> explanations = map.get( sup );
		if( explanations == null ) {
	        return null;
        }

		return Collections.unmodifiableSet( explanations );
	}

	/**
	 * Get classes of which the individual is an instance (based on the current
	 * state of the taxonomy)
	 * 
	 * @param t
	 *            the taxonomy
	 * @param ind
	 *            the individual
	 * @param directOnly
	 *            {@code true} if only most specific classes are desired,
	 *            {@code false} if all classes are desired
	 * @return a set of sets of classes where each inner set is a collection of
	 *         equivalent classes
	 */
	public static <T> Set<Set<T>> getTypes(Taxonomy<T> t, Object ind,
			boolean directOnly) {
		Set<Set<T>> types = new HashSet<Set<T>>();
		Iterator<Map.Entry<Set<T>, Object>> i = t.datumEquivalentsPair( INSTANCES_KEY );
		while( i.hasNext() ) {
			Map.Entry<Set<T>, Object> pair = i.next();
			Set<T> instances = (Set<T>) pair.getValue();
			if( instances != null && instances.contains( ind ) ) {
				types.add( pair.getKey() );
				if( !directOnly ) {
					T a = pair.getKey().iterator().next();
					types.addAll( t.getSupers( a ) );
				}
			}
		}
		return Collections.unmodifiableSet( types );
	}

	/**
	 * Determine if an individual is an instance of a class (based on the
	 * current state of the taxonomy)
	 * 
	 * @param t
	 *            the taxonomy
	 * @param ind
	 *            the individual
	 * @param c
	 *            the class
	 * @return a boolean {@code true} if {@code instanceOf(ind,c)},
	 *         {@code false} else
	 */
	public static boolean isType(Taxonomy<ATermAppl> t, ATermAppl ind, ATermAppl c) {
		Iterator<Object> i = t.depthFirstDatumOnly( c, INSTANCES_KEY );
		if( !i.hasNext() ) {
	        throw new RuntimeException( c + " is an unknown class!" );
        }

		do {
			Set<ATermAppl> instances = (Set<ATermAppl>) i.next();
			if( instances != null && instances.contains( ind ) ) {
	            return true;
            }

		} while( i.hasNext() );

		return false;
	}
}
