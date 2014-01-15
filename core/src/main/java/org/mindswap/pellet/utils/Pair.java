// Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package org.mindswap.pellet.utils;

/**
 * <p>Title: </p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2006</p>
 *
 * <p>Company: Clark & Parsia, LLC. <http://www.clarkparsia.com></p>
 *
 * @author Evren sirin
 */
public class Pair<F,S> {
    public F first;
    public S second;

    public Pair( F first, S second ) {
        if( first == null || second == null )
            throw new NullPointerException();
        
        this.first = first;
        this.second = second;        
    }
    
    public static <F,S> Pair<F,S> create(F f, S s) {
    	return new Pair<F,S>( f, s );
    }

    public int hashCode() {
        return first.hashCode() + second.hashCode();        
    }
    
    @SuppressWarnings("unchecked")
	public boolean equals( Object o ) {
        if( o == this )
            return true;

        if( !(o instanceof Pair) )
            return false;

        Pair p = (Pair) o;

        return first.equals( p.first ) && second.equals( p.second );
    }
    
    public String toString() {
        return "[" + first + ", " + second + "]";
    }
}
