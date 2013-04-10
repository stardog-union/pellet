// Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package org.mindswap.pellet.utils.intset;

import java.util.Arrays;
import java.util.NoSuchElementException;

/**
 * <p>
 * Title:
 * </p>
 * <p>
 * Description:
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
public class ArrayIntSet implements IntSet {
	private static int	INIT_CAPACITY	= 1;

	private int[]		ints;
	private int			size;

	public ArrayIntSet() {
		size = 0;
		ints = new int[INIT_CAPACITY];
	}

	public ArrayIntSet(ArrayIntSet other) {
		size = other.size;
		ints = new int[size];
		System.arraycopy( other.ints, 0, ints, 0, size );
	}
	
	private ArrayIntSet(ArrayIntSet set1, ArrayIntSet set2) {
		setToUnionOf( set1, set2 );
	}

	/**
	 * {@inheritDoc}
	 */
	public void add(int value) {
		ensureCapacity( size + 1 );

		int index = binarySearch( ints, value );
		if( index < 0 ) {
			index = -index - 1;
			if( index < size )
				System.arraycopy( ints, index, ints, index + 1, size - index );
			ints[index] = value;
			size++;
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public void addAll(IntSet values) {
		if( values instanceof ArrayIntSet ) {
			setToUnionOf( this, (ArrayIntSet) values );
		}
		else {
			ensureCapacity( size + values.size() );

			IntIterator i = values.iterator();
			while( i.hasNext() ) {
				add( i.next() );
			}
		}
	}
	
	private int binarySearch(int[] a, int key) {
		int low = 0;
		int high = size - 1;

		while( low <= high ) {
			int mid = (low + high) >>> 1;
			int midVal = a[mid];

			if( midVal < key )
				low = mid + 1;
			else if( midVal > key )
				high = mid - 1;
			else
				return mid; // key found
		}
		return -(low + 1); // key not found.
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean contains(int value) {
		return binarySearch( ints, value ) >= 0;
	}

	/**
	 * {@inheritDoc}
	 */
	public IntSet copy() {
		return new ArrayIntSet( this );
	}
	
	public int hashCode() {		
		return Arrays.hashCode( ints );
	}
	
	public boolean equals(Object o) {
		if( !(o instanceof IntSet) )
			return false;
		IntSet that = (IntSet) o;
		if( this.size() != that.size() )
			return false;
		IntIterator i = this.iterator();
		while( i.hasNext() ) {
			if( !that.contains( i.next() ) )
				return false;
		}
		return true;
	}

	private void ensureCapacity(int minCapacity) {
		int oldCapacity = ints.length;
		if( minCapacity > oldCapacity ) {
			int oldData[] = ints;
			int newCapacity = (oldCapacity * 3) / 2 + 1;
			if( newCapacity < minCapacity )
				newCapacity = minCapacity;
			ints = new int[newCapacity];
			System.arraycopy( oldData, 0, ints, 0, size );
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean isEmpty() {
		return size == 0;
	}

	/**
	 * {@inheritDoc}
	 */
	public IntIterator iterator() {
		return new IntIterator() {
			private int	next	= 0;

			public boolean hasNext() {
				return next < size;
			}

			public int next() {
				if( !hasNext() )
					throw new NoSuchElementException();

				return ints[next++];
			}
		};
	}

	/**
	 * {@inheritDoc}
	 */
	public int max() {
		if( isEmpty() )
			throw new NoSuchElementException();

		return ints[size - 1];
	}

	/**
	 * {@inheritDoc}
	 */
	public int min() {
		if( isEmpty() )
			throw new NoSuchElementException();

		return ints[0];
	}

	/**
	 * {@inheritDoc}
	 */
	public void remove(int value) {
		int index = binarySearch( ints, value );

		if( index >= 0 ) {
			if( index < size - 1 )
				System.arraycopy( ints, index + 1, ints, index, size - index - 1);
			size--;
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public int size() {
		return size;
	}

	/**
	 * {@inheritDoc}
	 */
	public String toString() {
		if( size == 0 )
			return "[]";

		StringBuilder buf = new StringBuilder();
		buf.append( '[' );
		buf.append( ints[0] );

		for( int i = 1; i < size; i++ ) {
			buf.append( ", " );
			buf.append( ints[i] );
		}

		buf.append( "]" );
		return buf.toString();
	}
	
	private void setToUnionOf(ArrayIntSet set1, ArrayIntSet set2) {
		int[] ints1 = set1.ints;
		int[] ints2 = set2.ints;
		int size1 = set1.size;
		int size2 = set2.size;
		size = size1 + size2;
		ints = new int[size];

		for( int i = 0, i1 = 0, i2 = 0; i < size; i++ ) {
			if( i1 == size1 ) {
				// we consumed ints1, so copy rest of i2 and finish
				System.arraycopy( ints2, i2, ints, i, size2 - i2 );
				break;
			}	
			else if( i2 == size2 ) {
				// we consumed ints2, so copy rest of i1 and finish
				System.arraycopy( ints1, i1, ints, i, size1 - i1 );
				break;
			}
			else if( ints1[i1] < ints2[i2] ) {
				// element in ints1 is smaller so copy it
				ints[i] = ints1[i1++];
			}
			else {
				// element in ints2 is not greater so copy it
				ints[i] = ints2[i2++];
				
				// is the element we copied from ints2 same as the one in ints1?
				if( ints[i] == ints1[i1] ) {
					// we have a duplicate so increment i1 and decrement size
					i1++;
					size--;
				}
			}
		}
	}

	public IntSet union(IntSet values) {
		return new ArrayIntSet( this, (ArrayIntSet) values );
	}
}
