package com.clarkparsia.pellet.datatypes;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.NoSuchElementException;
import java.util.Set;

/**
 * <p>
 * Title: Data value enumeration
 * </p>
 * <p>
 * Description: Enumeration of data values (i.e., an OWL 2 DataOneOf)
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
public class DataValueEnumeration<T> implements DataRange<T> {

	private final Set<T>	values;
	private final int		size;

	public DataValueEnumeration(Collection<? extends T> values) {
		if( values == null )
			throw new NullPointerException();
		if( values.isEmpty() )
			throw new IllegalArgumentException();

		this.values = Collections.unmodifiableSet( new LinkedHashSet<T>( values ) );
		this.size = this.values.size();
	}

	public boolean contains(Object value) {
		return values.contains( value );
	}

	public boolean containsAtLeast(int n) {
		return size >= n;
	}

	public T getValue(int i) {
		/*
		 * Inefficient, but no one should be using this method!
		 */
		if( i >= size )
			throw new NoSuchElementException();

		Iterator<T> it = values.iterator();
		for( int j = 0; j < i; j++ )
			it.next();

		return it.next();
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
		return size;
	}

	public Iterator<T> valueIterator() {
		return values.iterator();
	}

	@Override
	public String toString() {
		return String.format( "OneOf%s", values );
	}
}
