// Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package org.mindswap.pellet.utils.fsm;

import java.util.HashSet;
import java.util.Set;

import org.mindswap.pellet.Role;

/**
 * <p>Title: </p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2006</p>
 *
 * <p>Company: Clark & Parsia, LLC. <http://www.clarkparsia.com></p>
 *
 * @author Evren Sirin
 */
public class State<T> {
    private static int next_unused_name = 0;

    private int name; // number of state

    private Set<Transition<T>> transitions; // set of outgoing edges from state

    public State() {
        name = next_unused_name++;
        transitions = new HashSet<Transition<T>>();
    }

    /**
     * Create a transition from this state to the given state on
     * given symbol
     * 
     * @param symbol
     * @param s
     */
    public void addTransition( T symbol, State<T> s ) {
        if( symbol == null || s == null )
            throw new NullPointerException();
        
        if( !(symbol instanceof Role) )
            throw new ClassCastException();

        Transition<T> t = new Transition<T>( symbol, s );
        transitions.add( t );
    }

    /**
     * Create an epsilon transition from this state to the given state 
     * 
     * @param s     
     */
    public void addTransition( State<T> s ) {
        if( s == null )
            throw new NullPointerException();
        
        Transition<T> t = new Transition<T>( s );
        transitions.add( t );
    }
    
    /**
     * Returns the transitions for originating from this state.
     * 
     * @return the transitions for originating from this state
     */
    public Set<Transition<T>> getTransitions() {
        return transitions;
    }

	/**
	 * Returns a state reached from this state with the given symbol. If more
	 * than one state can be reached with the given symbol, an arbitrary one is
	 * returned.
	 * 
	 * @return a state reached from this state with the given symbol,
	 *         <code>null</code> otherwise
	 */
    public State<T> move( T symbol ) {
        for( Transition<T> t : transitions ) {
            if( t.hasName( symbol ) )
                return t.getTo();
        }
        return null;
    }

    public int getName() {
        return name;
    }

    public void setName(int i) {
        name = i;
    }
    
    public String toString() {
    	return  String.valueOf( name );
    }
}
