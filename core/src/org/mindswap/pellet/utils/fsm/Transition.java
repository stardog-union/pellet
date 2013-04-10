// Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package org.mindswap.pellet.utils.fsm;

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
public class Transition<T> {
    private static Object EPSILON = null;
    
    private T name;

    private State<T> to;

    /**
    /* add edge with epsilon edge
     */
    @SuppressWarnings("unchecked")
	public Transition( State<T> t ) {
        name = (T) EPSILON;
        to = t;
    }

    /**
     * add edge for name from current state to state t on c
     */      
    public Transition( T name, State<T> to ) {
        this.name = name;
        this.to = to;
    }

    
    @Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null)
			? 0
			: name.hashCode());
		result = prime * result + ((to == null)
			? 0
			: to.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if( this == obj )
			return true;
		if( obj == null )
			return false;
		if( getClass() != obj.getClass() )
			return false;
		@SuppressWarnings("unchecked")
		Transition other = (Transition) obj;
		if( name == null ) {
			if( other.name != null )
				return false;
		}
		else if( !name.equals( other.name ) )
			return false;
		if( to == null ) {
			if( other.to != null )
				return false;
		}
		else if( !to.equals( other.to ) )
			return false;
		return true;
	}

	public boolean isEpsilon() {
    	return name == EPSILON;
    }
    
    public State<T> getTo() {
        return to;
    }

    public void setTo( State<T> to ) {
        this.to = to;
    }
    
    public T getName() {
        return name;
    }
    
    public boolean hasName( T c ) {
        return (name == EPSILON) ? c == EPSILON : (c == EPSILON) ? false : name.equals( c );
    }
        
    public String toString() {
        return (name == EPSILON ? "epsilon" : name.toString() ) + " -> " + to.getName();
    }
}
