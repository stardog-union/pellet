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
public class ShiftedBitIntSet extends BitIntSet {
	private int	min	= Integer.MAX_VALUE;

	public ShiftedBitIntSet() {
		super();
	}

	public ShiftedBitIntSet(ShiftedBitIntSet other) {
		super( other );

		min = other.min;
	}

	public void add(int value) {
		if( isEmpty() ) {
			min = value;
			super.add( 0 );
		}
		else if( value >= min ) {
			super.add( value - min );
		}
		else {
			throw new UnsupportedOperationException( "Not implemented" );
		}
	}

	public boolean contains(int value) {
		if( value >= min )
			return super.contains( value - min );
		else
			return false;
	}

	public IntSet copy() {
		return new ShiftedBitIntSet( this );
	}

	public IntIterator iterator() {
		return new IntIterator() {
			private IntIterator base = ShiftedBitIntSet.super.iterator();

			public boolean hasNext() {
				return base.hasNext();
			}

			public int next() {				
				return min + base.next();
			}			
		};
	}

	public int max() {
		if( isEmpty() )
			throw new NoSuchElementException();
		else
			return min + super.max();
	}

	public int min() {
		if( isEmpty() )
			throw new NoSuchElementException();
		else
			return min;
	}

	public void remove(int value) {
		if( value >= min ) {
			super.remove( value - min );
			min = super.min();
		}
	}

}
