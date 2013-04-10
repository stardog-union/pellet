// Portions Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// Clark & Parsia, LLC parts of this source code are available under the terms of the Affero General Public License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package org.mindswap.pellet.utils;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.mindswap.pellet.utils.iterator.PairIterator;

/**
 * @author Evren Sirin
 *
 */
public class CandidateSet<T> {
    private Set<T> knowns, unknowns;

    public CandidateSet() {
        this.knowns = new HashSet<T>();
        this.unknowns = new HashSet<T>();        
    }
    
    public CandidateSet( Set<T> knowns ) {
        this.knowns = new HashSet<T>( knowns );
        this.unknowns = new HashSet<T>();        
    }
    
    public CandidateSet( Set<T> knowns, Set<T> unknowns ) {
        this.knowns = new HashSet<T>( knowns );
        this.unknowns = new HashSet<T>( unknowns );
    }
    
    public Set<T> getKnowns() {
        return knowns;
    }
    
    public Set<T> getUnknowns() {
        return unknowns;
    }
    
    public void add( T obj, Bool isKnown ) {
        if( isKnown.isTrue() ) {
            knowns.add( obj );
        }
        else if( isKnown.isUnknown() ) {
            unknowns.add( obj );
        }            
    }
    
    public void update( T obj, Bool isCandidate ) {
        if( isCandidate.isTrue() ) {
            // do nothing
        }
        else if( isCandidate.isFalse() ) {
            remove( obj );
        }
        else {
            if( knowns.contains( obj ) ) {
                knowns.remove( obj );
                unknowns.add( obj );
            }                
        }            
    }
    
    public boolean remove( Object obj ) {
        return knowns.remove( obj ) || unknowns.remove( obj );
    }
    
    public boolean contains( Object obj ) {
        return knowns.contains( obj ) || unknowns.contains( obj );
    }

    public int size() {
        return knowns.size() + unknowns.size();
    }

    public Iterator<T> iterator() {
        return new PairIterator<T>( knowns.iterator(), unknowns.iterator() );
    }
    
    public String toString() {
        return "Knowns: " + knowns.size() + " Unknowns: " + unknowns.size();
    }
}
