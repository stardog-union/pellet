package com.clarkparsia.pellet.datatypes;

import java.util.Iterator;

/**
 * <p>
 * Title: Negated Data Range
 * </p>
 * <p>
 * Description: A negated data range. By definition, this is infinite.
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
public class NegatedDataRange<T> implements DataRange<T> {

	private final DataRange<? extends T>	datarange;

	public NegatedDataRange(DataRange<? extends T> datarange) {
		this.datarange = datarange;
	}

	public boolean contains(Object value) {
		return !datarange.contains( value );
	}

	public boolean containsAtLeast(int n) {
		return true;
	}

	public T getValue(int i) {
		throw new UnsupportedOperationException();
	}

	public boolean isEmpty() {
		return false;
	}

	public boolean isEnumerable() {
		return false;
	}

	public boolean isFinite() {
		return false;
	}

	public int size() {
		throw new UnsupportedOperationException();
	}

	public Iterator<T> valueIterator() {
		throw new UnsupportedOperationException();
	}

	public DataRange<? extends T> getDataRange() {
		return datarange;
	}
}
