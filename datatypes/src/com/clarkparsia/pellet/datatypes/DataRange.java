package com.clarkparsia.pellet.datatypes;

import java.util.Iterator;

/**
 * <p>
 * Title: Data Range
 * </p>
 * <p>
 * Description: Data range interface shared by all data ranges (restricted
 * datatypes, enumerations, etc.)
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
public interface DataRange<T> {

	/**
	 * Check if a data range contains a particular value
	 * 
	 * @param value
	 *            the value to check
	 * @return <code>true</code> if the data range contains <code>value</code>,
	 *         <code>false</code> else
	 */
	public boolean contains(Object value);

	/**
	 * Check that a data range contains a minimum number of elements
	 * 
	 * @param n
	 *            the number of elements
	 * @return <code>true</code> if the data range contains <code>n</code> or
	 *         more elements, <code>false</code> otherwise
	 */
	public boolean containsAtLeast(int n);

	/**
	 * Convenience method equivalent to <code>!containsAtLeast(0)</code>
	 * 
	 * @return <code>true</code> if the data range contains no elements,
	 *         <code>false</code> otherwise
	 */
	public boolean isEmpty();

	/**
	 * Query if values in the data range can be enumerated.
	 * 
	 * @return <code>true</code> if the data range is enumerable,
	 *         <code>false</code> otherwise
	 */
	public boolean isEnumerable();

	/**
	 * Query if there are a finite number of values in the data range.
	 * 
	 * @return <code>true</code> if the data range is finite, <code>false</code>
	 *         otherwise
	 */
	public boolean isFinite();

	/**
	 * Return the size of the data range. <i>Necessary to support
	 * {@link LiteralValueBranch} constructor</i>
	 * 
	 * @return the size of the data range
	 * @throws IllegalStateException
	 *             if {@link #isFinite()} returns <code>false</code>
	 * @deprecated Use {@link #containsAtLeast(int)}
	 */
	public int size();

	/**
	 * Return a value from an enumerable data range. <i>Necessary to support
	 * {@link LiteralValueBranch} shiftTryNext</i>
	 * 
	 * @param i
	 *            the index of the value in the data range
	 * @return the value
	 * @throws IllegalStateException
	 *             if {@link #isEnumerable()} returns <code>false</code>
	 * @deprecated Use {@link #valueIterator()}
	 */
	public T getValue(int i);

	/**
	 * Get a (possibly infinite) iterator over values in the data range.
	 * 
	 * @return an {@link Iterator}
	 * @throws IllegalStateException
	 *             if {@link #isEnumerable()} returns <code>false</code>
	 */
	public Iterator<T> valueIterator();
}