// Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package org.mindswap.pellet.utils.intset;

import java.util.BitSet;
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
public class BitIntSet implements IntSet {
	private BitSet	bits;

	public BitIntSet() {
		bits = new BitSet();
	}

	public BitIntSet(BitIntSet other) {
		bits = (BitSet) other.bits.clone();
	}

	public void add(int value) {
		if( value < 0 )
			throw new UnsupportedOperationException(
					"Negatibe integers cannot be added to this set" );

		bits.set( value );
	}

	public void addAll(IntSet values) {
		if( values instanceof BitIntSet ) {
			bits.or( ((BitIntSet) values).bits );
		}
		else {
			IntIterator i = values.iterator();
			while( i.hasNext() ) {
				add( i.next() );
			}
		}
	}

	public boolean contains(int value) {
		return bits.get( value );
	}

	public IntSet copy() {
		return new BitIntSet( this );
	}

	public boolean isEmpty() {
		return bits.isEmpty();
	}

	public IntIterator iterator() {
		return new IntIterator() {
			private int	next	= bits.nextSetBit( 0 );

			public boolean hasNext() {
				return next != -1;
			}

			public int next() {
				int curr = next;
				if( curr == -1 )
					throw new NoSuchElementException();
				next = bits.nextSetBit( 0 );
				return curr;
			}
		};
	}

	public int max() {
		return bits.length() - 1;
	}

	public int min() {
		return bits.nextSetBit( 0 );
	}

	public void remove(int value) {
		if( value >= 0 )
			bits.clear( value );
	}

	public int size() {
		return bits.cardinality();
	}

	/**
	 * {@inheritDoc}
	 */
	public IntSet union(IntSet values) {
		IntSet newSet = copy();
		newSet.addAll( values );

		return newSet;
	}

}
