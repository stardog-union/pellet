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

package org.mindswap.pellet.utils;

import java.util.AbstractSet;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.mindswap.pellet.utils.iterator.IteratorUtils;

/**
 * Utility functions for {#link java.util.Set Set}s.
 * 
 * @author Evren Sirin
 */
public class SetUtils {
    private static class EmptySet extends AbstractSet<Object> {
		public Iterator<Object> iterator() {
			return IteratorUtils.emptyIterator();
		}

		public int size() {
			return 0;
		}

		public boolean contains(Object obj) {
			return false;
		}
	}
    
    public final static Set<?> EMPTY_SET = new EmptySet();
    
    /**
     * Adds the given object to the set but saves memory space
     * by allocating only the required amount for small sets. The 
     * idea is to use the specialized empty set and singleton set
     * implementations (which are immutable) for the sets of size 
     * 0 and 1. If the set is empty a new singleton set is created,
     * if set has one element we create a new set with two elements,
     * otherwise we simply add the element to the given set.This 
     * technique is most useful if the expected set size is 0 or 1. 
     *   
     * @param o 
     * @param set
     * @return
     */
    public static <T> Set<T> add( T o, Set<T> set ) {
        int size = set.size();
        if( size == 0 )
            set = singleton( o );
        else if( size == 1 ) {
            T existing = set.iterator().next();
            if( !existing.equals( o ) )
                set = binary( existing, o );
        }
        else    
            set.add( o );
        
        return set;
    }
    
    @SuppressWarnings("unchecked")
	public final static <T> Set<T> emptySet() {
    	return (Set<T>) EMPTY_SET;
    }

    public static <T> Set<T> remove( Object o, Set<T> set ) {
        int size = set.size();
        if( size == 0 ) {
            // do nothing
        }            
        else if( size == 1  ) {
            if( set.contains( o ) )
                set = Collections.emptySet();
        }
        else    
            set.remove( o );
        
        return set;
    }
    
    public final static <T> Set<T> singleton(T o) {
        return Collections.singleton( o );
    }

    public final static <T> Set<T> binary( T o1, T o2 ) {
        Set<T> set = new HashSet<T>();
        set.add( o1 );
        set.add( o2 );
        
        return set;
    }
    
	/**
	 * Returns the union of all the sets given in a collection. 
	 * 
	 * @param coll A Collection of sets
	 */
	public static <T> Set<T> union(Collection<? extends Collection<? extends T>> coll) {
		Set<T> set = new HashSet<T>();
		
		for( Collection<? extends T> innerColl : coll ) 
			set.addAll( innerColl );
		
		return set;
	}
	
	/**
	 * Returns the union of two collections 
	 * 
	 * @param coll A Collection of sets
	 */
	public static <T> Set<T> union( Collection<? extends T> c1, Collection<? extends T> c2 ) {
		Set<T> set = new HashSet<T>( c1 );
		set.addAll( c2 );
		
		return set;
	}	
    
	/**
	 * Returns the intersection of all the collections given in a collection. 
	 * 
	 * @param coll A Collection of sets
	 */
	public static <T> Set<T> intersection(Collection<? extends Collection<? extends T>> coll) {
		Iterator<? extends Collection<? extends T>> i = coll.iterator();
		
		if( !i.hasNext() )
			return new HashSet<T>();
		
		Set<T> set = new HashSet<T>( i.next() );
		while( i.hasNext() ) {
			Collection<? extends T> innerColl = i.next(); 
			set.retainAll( innerColl );
		}
		
		return set;
	}
	
	/**
	 * Returns the intersection of two collections 
	 * 
	 * @param coll A Collection of sets
	 */
	public static <T> Set<T> intersection( Collection<? extends T> c1, Collection<? extends T> c2 ) {
		Set<T> set = new HashSet<T>( c1 );
		set.retainAll(c2);
		
		return set;
	}	
	
	/**
	 * Checks if two collections have any elements in common 
	 */
	public static boolean intersects( Collection<?> c1, Collection<?> c2 ) {
        for(Iterator<?> i = c1.iterator(); i.hasNext();) {
            if( c2.contains( i.next() ) ) 
                return true;
        }

        return false;
    }		

	/**
	 * Checks if one set is subset of another one
	 * 
	 * @param sub
	 * @param sup
	 * @return
	 */
	public static boolean subset(Set<?> sub, Set<?> sup) {
		return sub.size() <= sup.size() && sup.containsAll( sub );
	}
	
	/**
	 * Checks if one set is equal of another one
	 * 
	 * @param sub
	 * @param sup
	 * @return
	 */
	public static <T> boolean equals(Set<T> s1, Set<T> s2) {
		return s1.size() == s2.size() && s1.containsAll( s2 );
	}
	
	/**
	 * Returns the difference of two sets. All the elements of second set is
	 * removed from the first set  
	 * 
	 * @param coll A Collection of sets
	 */
	public static <T> Set<T> difference(Collection<T> c1, Collection<? extends Object> c2) {
		Set<T> set = new HashSet<T>();
		set.addAll(c1);
		if(c2 instanceof Set) {
			set.removeAll(c2);
		}
		else {
			for( Object e : c2 ) {
				set.remove( e );
			}
		}
		
		return set;
	}		
	
	/**
	 * 
	 * Creates a list containing all the elements in the array
	 * 
	 * @param elements
	 * @return
	 */
	public static <T> Set<T> create(T... elems) {
		Set<T> set = new HashSet<T>( elems.length );
		for(int i = 0; i < elems.length; i++)
			set.add( elems[i] );
		
		return set;
	}
	
	
	/**
	 * 
	 * Creates a set containing all the elements in the collection
	 * 
	 * @param elements
	 * @return
	 */
	public static <T> Set<T> create( Collection<T> coll ) {
		return new HashSet<T>( coll );
	}	
}
