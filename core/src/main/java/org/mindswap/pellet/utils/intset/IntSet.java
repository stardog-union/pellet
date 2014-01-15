// Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package org.mindswap.pellet.utils.intset;

import java.util.NoSuchElementException;

/**
 * <p>
 * Title:
 * </p>
 * <p>
 * Description: An interface describing a set of primitive integers. An
 * implementation may only accept certain types of integers (e.g. only positive
 * integers).
 * </p>
 * <p>
 * Copyright: Copyright (c) 2007
 * </p>
 * <p>
 * Company: Clark & Parsia, LLC. <http://www.clarkparsia.com>
 * </p>
 * 
 * @author Evren Sirin
 */
public interface IntSet {
	/**
	 * Adds a new integer to this set.
	 * 
	 * @param value
	 *            integer value to be added
	 * @throws IllegalArgumentException
	 *             if the value is not supported by this set implementation
	 */
	public void add(int value) throws IllegalArgumentException;

	/**
	 * Adds all the integers from another set to this set.
	 * 
	 * @param other
	 *            the set whose elements will be added
	 * @throws IllegalArgumentException
	 *             if any of the values in the other set is not supported by
	 *             this set implementation
	 */
	public void addAll(IntSet other);

	/**
	 * Checks if the given integer value is in this set.
	 * 
	 * @param value
	 *            the integer value being checked
	 * @return <code>true</code> if the value is in this set
	 */
	public boolean contains(int value);

	/**
	 * Creates a copy of this set.
	 * 
	 * @return a copy of this set
	 */
	public IntSet copy();

	/**
	 * Cehcks if the set is empty.
	 * 
	 * @return <code>true</code> if there are no elements in the set
	 */
	public boolean isEmpty();

	/**
	 * An iterator over the values of this set. There is no guarantee on the
	 * order of the elements.
	 * 
	 * @return an iterator over the values of the set
	 */
	public IntIterator iterator();

	/**
	 * Returns the maximum integer in this set.
	 * 
	 * @return the maximum integer in this set
	 * @throws NoSuchElementException
	 *             if the set is empty
	 */
	public int max() throws NoSuchElementException;

	/**
	 * Returns the minimum integer in this set.
	 * 
	 * @return the minimum integer in this set
	 * @throws NoSuchElementException
	 *             if the set is empty
	 */
	public int min();

	/**
	 * Remove the given integer value from this set.
	 * 
	 * @param value
	 *            the integer value to be removed
	 */
	public void remove(int value);

	/**
	 * Creates a new IntSet that is the union of this set and the given set.
	 * Neither of the sets are changed by this operation.
	 * 
	 * @param set the other set that will be included in the union result
	 * @return a new IntSet instance that is the union of two sets 
	 */
	public IntSet union(IntSet set);

	/**
	 * Returns the number of elements in the set.
	 * 
	 * @return the number of elements in the set
	 */
	public int size();
}
