// Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package org.mindswap.pellet.utils.iterator;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;

/**
 * 
 * @author Evren Sirin
 */
public class IteratorUtils {
	private static class SingletonIterator<T> implements Iterator<T> {
		private T element;
		private boolean consumed;
		
		private SingletonIterator(T element) {
			this.element = element;
			this.consumed = false;
		}
		
        public boolean hasNext() {
            return !consumed;
        }
        public T next() {
        	if( !hasNext() )
        		throw new NoSuchElementException();
        	consumed = true;
        	return element;
        }
        public void remove() {
            throw new UnsupportedOperationException();
        }
    }
    
	private static final Iterator<Object> EMPTY_ITERATOR = new Iterator<Object>() {
        public boolean hasNext() {
            return false;
        }
        public Object next() {
            throw new NoSuchElementException();
        }
        public void remove() {
            throw new UnsupportedOperationException();
        }
    };    
	
	public static <T> Iterator<T> concat(Iterator<? extends T> i1, Iterator<? extends T> i2) {
		return new MultiIterator<T>( i1, i2 );
	}
    
    @SuppressWarnings("unchecked")
	public static final <T> Iterator<T> emptyIterator() {
    	return (Iterator<T>) EMPTY_ITERATOR;
    }
    
	public static final <T> Iterator<T> singletonIterator(T element) {
    	return new SingletonIterator<T>( element );
    }
	
	public static <T> Set<T> toSet(Iterator<T> i) {
		Set<T> set = new HashSet<T>();
		while( i.hasNext() )
			set.add( i.next() );
		return set;
	}
	
	public static <T> List<T> toList(Iterator<T> i) {
		List<T> set = new ArrayList<T>();
		while( i.hasNext() )
			set.add( i.next() );
		return set;
	}
	
	public static <T> Iterator<T> flatten(Iterator<? extends Iterable<T>> iterator) {
		return new FlattenningIterator<T>( iterator );
	}
	
	public static <T> Iterator<T> singleton(final T element) {
		return iterator( element );
	}
	
	public static <T> Iterator<T> iterator(final T element) {		
		return new Iterator<T>() {
			private boolean hasNext = true;
			
	        public boolean hasNext() {
	            return hasNext;
	        }
	        public T next() {
	        	if( !hasNext )
	        		throw new NoSuchElementException();
	        	hasNext = false;
	        	return element;
	        }
	        
	        public void remove() {
	            throw new UnsupportedOperationException();
	        }
	    };
	}
    
	public static <T> Iterator<T> iterator(T... elements) {
		return new ArrayIterator<T>( elements, elements.length );
	}
	
	public static <T> Iterator<T> iterator(int size, T... elements) {
		return new ArrayIterator<T>( elements, size );
	}
	
	private static class ArrayIterator<E> implements Iterator<E> {
		private final E[]	array;
		private int			size;
		private int			curr	= 0;

		public ArrayIterator(E[] array, int size) {
			this.array = array;
			this.size = size;
		}

		public boolean hasNext() {
			return curr != size;
		}

		public E next() {
			if( !hasNext() )
				throw new NoSuchElementException();

			return array[curr++];
		}

		public void remove() {
			throw new UnsupportedOperationException();
		}
	}
}
