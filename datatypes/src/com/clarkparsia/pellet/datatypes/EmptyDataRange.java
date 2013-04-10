package com.clarkparsia.pellet.datatypes;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * <p>
 * Title: Empty Data Range
 * </p>
 * <p>
 * Description: Re-usable empty data range implementation. Cannot be static so
 * that parameterization is handled correctly.
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
public class EmptyDataRange<T> implements DataRange<T> {

	final private Iterator<T>	iterator;

	public EmptyDataRange() {
		this.iterator = new EmptyIterator<T>();
	}

	public boolean contains(Object value) {
		return false;
	}

	public boolean containsAtLeast(int n) {
		return n <= 0;
	}

	public T getValue(int i) {
		throw new NoSuchElementException();
	}

	public boolean isEmpty() {
		return true;
	}

	public boolean isEnumerable() {
		return true;
	}

	public boolean isFinite() {
		return true;
	}

	public int size() {
		return 0;
	}

	public Iterator<T> valueIterator() {
		return iterator;
	}

}
