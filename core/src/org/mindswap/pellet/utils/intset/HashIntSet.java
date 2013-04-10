// Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package org.mindswap.pellet.utils.intset;

import java.util.HashSet;
import java.util.Iterator;

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
public class HashIntSet extends AbstractIntSet implements IntSet {
	private HashSet<Integer>	ints;

	public HashIntSet() {
		ints = new HashSet<Integer>();
	}

	public void add(int value) {
		if( value < 0 )
			throw new IndexOutOfBoundsException();
		
		ints.add( value );
		added( value, value );
	}

	public void addAll(IntSet values) {
		if( values instanceof HashIntSet ) {
			ints.addAll( ((HashIntSet) values).ints );
			if( !values.isEmpty() )
				added( values.min(), values.max() );
		}
		else {
			super.addAll( values );
		}
	}

	public boolean contains(int value) {
		return ints.contains( value );
	}

	public IntSet copy() {
		HashIntSet copy = new HashIntSet();		
		copy.addAll( this );
		return copy;
	}

	public boolean isEmpty() {
		return ints.isEmpty();
	}

	public IntIterator iterator() {
		return new IntIterator() {
			private Iterator<Integer> base = ints.iterator();

			public boolean hasNext() {
				return base.hasNext();
			}

			public int next() {				
				return base.next();
			}			
		};
	}

	public void remove(int value) {
		ints.remove( value );
		removed(value, value);
	}

	public int size() {
		return ints.size();
	}

}
