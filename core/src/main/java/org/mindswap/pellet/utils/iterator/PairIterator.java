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
public class PairIterator<T> implements Iterator<T> {
    private Iterator<T> first;
    private Iterator<T> second;


    public PairIterator(Iterator<T> first, Iterator <T>second) {
        this.first = first;
        this.second = second;
    }
    
    public boolean hasNext() {
        return first.hasNext() || second.hasNext();
    }

    public T next() {
        if( !hasNext() )
            throw new NoSuchElementException();
        
        return first.hasNext() ? first.next() : second.next(); 
    }

    public void remove() {
        throw new UnsupportedOperationException();
    }
}