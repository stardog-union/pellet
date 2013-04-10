// Portions Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// Clark & Parsia, LLC parts of this source code are available under the terms of the Affero General Public License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com
//
// ---
// Portions Copyright (c) 2003 Ron Alford, Mike Grove, Bijan Parsia, Evren Sirin
// Alford, Grove, Parsia, Sirin parts of this source code are available under the terms of the MIT License.
//
// The MIT License
//
// Permission is hereby granted, free of charge, to any person obtaining a copy
// of this software and associated documentation files (the "Software"), to
// deal in the Software without restriction, including without limitation the
// rights to use, copy, modify, merge, publish, distribute, sublicense, and/or
// sell copies of the Software, and to permit persons to whom the Software is
// furnished to do so, subject to the following conditions:
//
// The above copyright notice and this permission notice shall be included in
// all copies or substantial portions of the Software.
//
// THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
// IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
// FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
// AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
// LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
// FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS
// IN THE SOFTWARE.

package org.mindswap.pellet;

import aterm.ATermAppl;

/**
 * @author Evren Sirin
 *
 */
public class DefaultEdge implements Edge {
	private Individual from;
	private Node to;
	private Role role;
	
	private DependencySet depends;
	
	public DefaultEdge(Role name, Individual from, Node to) {
		this.role = name;
		this.from = from;
		this.to = to;
	}
	
	public DefaultEdge(Role name, Individual from, Node to, DependencySet d) {
		this.role = name;
		this.from = from;
		this.to = to;
		this.depends = d;
	}
	
	/**
	 * {@inheritDoc}
	 */
	public Node getNeighbor( Node node ) {
		if( from.equals( node ) )
            return to;
        else if( to.equals( node ) )
            return from;
        else
            return null;
	}
	
	public String toString() {
		return "[" + from + ", " + role + ", " + to + "] - " + depends; 
	}
	/**
	 * {@inheritDoc}
	 */
	public DependencySet getDepends() {
		return depends;
	}
	/**
	 * {@inheritDoc}
	 */
	public Individual getFrom() {
		return from;
	}
	/**
	 * {@inheritDoc}
	 */
	public Role getRole() {
		return role;
	}
	/**
	 * {@inheritDoc}
	 */
	public Node getTo() {
		return to;
	}
	
    public boolean equals(Object other) {
        if(this == other) return true;
        if(!(other instanceof DefaultEdge)) return false;
        DefaultEdge that = (DefaultEdge) other;
        return from.equals(that.from) && role.equals(that.role) && to.equals(that.to);
    }
    
    public int hashCode() {
        int hashCode = 23;
        
        hashCode = 31 * hashCode + role.hashCode();
        hashCode = 31 * hashCode + from.hashCode();
        hashCode = 31 * hashCode + to.hashCode();
        
        return hashCode;
    }

	/**
	 * {@inheritDoc}
	 */
	public ATermAppl getFromName() {
		return getFrom().getName();
	}

	/**
	 * {@inheritDoc}
	 */
	public ATermAppl getToName() {
		return getTo().getName();
	}
	
	/**
	 * {@inheritDoc}
	 */	
	public void setDepends(DependencySet ds) {
		depends = ds;
	}
}
