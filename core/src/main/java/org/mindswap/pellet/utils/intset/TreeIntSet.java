// Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package org.mindswap.pellet.utils.intset;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.TreeSet;

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
public class TreeIntSet extends AbstractIntSet implements IntSet {
	private TreeSet<Integer>	ints;

	public TreeIntSet() {
		ints = new TreeSet<Integer>();
	}

	public TreeIntSet(TreeIntSet other) {
		ints = new TreeSet<Integer>( other.ints );
	}

	public void add(int value) {
		if( value < 0 )
			throw new IndexOutOfBoundsException();
		
		ints.add( value );
	}

	public void addAll(IntSet values) {
		if( values instanceof TreeIntSet ) {
			ints.addAll( ((TreeIntSet) values).ints );
		}
		else {
			super.addAll( values );
		}
	}

	public boolean contains(int value) {
		return ints.contains( value );
	}

	public IntSet copy() {
		return new TreeIntSet( this );
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

	public int max() {
		if( isEmpty() )
			throw new NoSuchElementException();
		else
			return ints.last();
	}

	public int min() {
		if( isEmpty() )
			throw new NoSuchElementException();
		else
			return ints.first();
	}

	public void remove(int value) {
		ints.remove( value );
	}

	public int size() {
		return ints.size();
	}

}
