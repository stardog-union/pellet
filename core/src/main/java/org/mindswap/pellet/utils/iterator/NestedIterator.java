// Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package org.mindswap.pellet.utils.iterator;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * 
 * @author Evren Sirin
 */
public abstract class NestedIterator<Outer, Inner> implements Iterator<Inner> {
	private Iterator<? extends Outer> outerIterator;
	private Iterator<? extends Inner> innerIterator;

	public NestedIterator(Iterable<? extends Outer> outerIterable) {
		this( outerIterable.iterator() );
	}
	
	public NestedIterator(Iterator<? extends Outer> outerIterator) {
		this.outerIterator = outerIterator;
		findIterator();
	}

	private void findIterator() {
		while( outerIterator.hasNext() ) {
			Outer subj = outerIterator.next();
			innerIterator = getInnerIterator( subj );

			if( innerIterator.hasNext() )
				return;
		}

		innerIterator = IteratorUtils.emptyIterator();
	}
	
	public abstract Iterator<? extends Inner> getInnerIterator(Outer outer);

	public boolean hasNext() {
		return innerIterator.hasNext();
	}

	public Inner next() {
		if( !hasNext() )
			throw new NoSuchElementException();

		Inner value = innerIterator.next();
		
		if( !innerIterator.hasNext() )
			findIterator();
		
		return value;
	}
	
	public void remove() {
		innerIterator.remove();
	}
}