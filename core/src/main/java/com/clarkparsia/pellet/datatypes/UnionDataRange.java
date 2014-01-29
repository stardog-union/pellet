package com.clarkparsia.pellet.datatypes;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Set;

/**
 * <p>
 * Title: Union data range
 * </p>
 * <p>
 * Description: A disjunction of {@link DataRange} objects
 * </p>
 * <p>
 * Copyright: Copyright (c) 2009
 * </p>
 * <p>
 * Company: Clark & Parsia, LLC. <http://www.clarkparsia.com>
 * </p>
 * 
 * @author Mike Smith
 */
public class UnionDataRange<T> implements DataRange<T> {

	private final ArrayList<RestrictedDatatype<? extends T>>	ranges;
	private final Set<? extends T>								values;

	public UnionDataRange(Collection<RestrictedDatatype<? extends T>> ranges,
			Collection<? extends T> values) {
		this.ranges = new ArrayList<RestrictedDatatype<? extends T>>( ranges );
		this.values = new HashSet<T>( values );
	}

	public boolean contains(Object value) {
		if( values.contains( value ) )
			return true;

		for( RestrictedDatatype<? extends T> rd : ranges ) {
			if( rd.contains( value ) )
				return true;
		}

		return false;
	}

	public boolean containsAtLeast(int n) {
		n -= values.size();
		if( n <= 0 )
			return true;

		for( RestrictedDatatype<?> rd : ranges ) {
			if( rd.containsAtLeast( n ) )
				return true;

			n -= rd.size();
		}

		return n <= 0;
	}

	public T getValue(int i) {
		throw new UnsupportedOperationException();
	}

	public boolean isEmpty() {
		return false;
	}

	public boolean isEnumerable() {
		return true;
	}

	public boolean isFinite() {
		return true;
	}

	public int size() {
		throw new UnsupportedOperationException();
	}

	public Iterator<T> valueIterator() {

		/*
		 * This implementation avoids allocating the value iterators for the
		 * data ranges until (and only if) they are needed. This is a
		 * micro-optimization relative to using
		 * org.mindswap.pellet.utils.iterator.MultiIterator
		 */
		return new Iterator<T>() {
			final Iterator<? extends T>						enumIt	= values.iterator();
			final Iterator<RestrictedDatatype<? extends T>>	rangeIt	= ranges.iterator();
			Iterator<? extends T>							valueIt	= null;

			public boolean hasNext() {
				if( enumIt.hasNext() )
					return true;

				if( valueIt == null ) {
					if( rangeIt.hasNext() )
						valueIt = rangeIt.next().valueIterator();
					else
						return false;
				}

				while( !valueIt.hasNext() ) {
					if( rangeIt.hasNext() )
						valueIt = rangeIt.next().valueIterator();
					else
						return false;
				}
				return true;
			}

			public T next() {
				if( !hasNext() )
					throw new NoSuchElementException();

				if( valueIt == null )
					return enumIt.next();

				return valueIt.next();
			}

			public void remove() {
				throw new UnsupportedOperationException();
			}
		};
	}
}
