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
public abstract class AbstractIntSet implements IntSet {
	private int	min	= Integer.MAX_VALUE;
	private int	max	= Integer.MIN_VALUE;

	public void addAll(IntSet values) {
		IntIterator i = values.iterator();
		while( i.hasNext() ) {
			add( i.next() );
		}
	}

	protected void added(int low, int high) {
		if( low < min )
			min = low;
		if( high > max )
			max = high;
	}

	public int max() {
		if( isEmpty() )
			throw new NoSuchElementException();

		return max;
	}

	public int min() {
		if( isEmpty() )
			throw new NoSuchElementException();

		return min;
	}

	protected void removed(int low, int high) {
		if( isEmpty() || (low == min || high == max) ) {
			min = Integer.MAX_VALUE;
			max = Integer.MIN_VALUE;
			
			IntIterator i = iterator();
			while( i.hasNext() ) {
				int value = i.next();
				if( value < min )
					min = value;
				if( value > max )
					max = value;
			}
		}
	}
	
	public IntSet union(IntSet values) {
		IntSet newSet = copy();
		newSet.addAll( values );
		
		return newSet;
	}

	public String toString() {
		StringBuffer s = new StringBuffer();
		s.append( '[' );
		IntIterator i = iterator();
		while( i.hasNext() ) {
			if( s.length() > 1 )
				s.append( ',' );
			s.append( String.valueOf( i.next() ) );
		}
		s.append( ']' );
		return s.toString();
	}
}
