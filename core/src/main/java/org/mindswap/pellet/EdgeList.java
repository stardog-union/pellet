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


import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Set;

import org.mindswap.pellet.utils.ATermUtils;

import aterm.ATermAppl;

/**
 * @author Evren Sirin
 *
 */
public class EdgeList implements Iterable<Edge> {
	private class EdgeIterator implements Iterator<Edge> {
		private int	curr = 0;

		public EdgeIterator() {
		}

		public boolean hasNext() {
			return curr != size;
		}

		public Edge next() {
			if( !hasNext() )
				throw new NoSuchElementException();

			return list[curr++];
		}

		public void remove() {
			removeEdge( --curr );
		}
	}
	
	private Edge[] list;
	private int size;
	
	public EdgeList() {
		this( 10 );
	}

	public EdgeList(int n) {
		list = new Edge[n];
		size = 0;
	}
	
	public EdgeList(EdgeList edges) {
		this( edges.size );
		
		addEdgeList( edges );
	}
	
	/**
	 * Create an immutable singleton EdgeList;
	 * 
	 * @param edge
	 */
	public EdgeList(Edge edge) {
		list = new Edge[1];
		list[0] = edge;
		size= 0;
	}	
	
	private void allocate(int minSize) {
		int oldSize = list.length;
		if( minSize > oldSize ) {
			Edge oldList[] = list;
			int newSize = (oldSize * 3) / 2 + 1;
			if( newSize < minSize )
				newSize = minSize;
			list = new Edge[newSize];
			System.arraycopy(oldList, 0, list, 0, oldSize);
		}
	}

	public void addEdgeList(EdgeList edges) {
		int edgesSize = edges.size;
		allocate(size + edgesSize); 
        System.arraycopy(edges.list, 0, list, size, edgesSize);
        size += edgesSize;		
	}
		
	public void addEdge(Edge e) {
		allocate(size + 1);
		list[size++] = e;
	}
	
	public boolean removeEdge(Edge edge) {
		for(int i = 0; i < size; i++) {
			Edge e = list[i];
            if( e.equals( edge ) ) {
            	removeEdge( i );
				return true;
            }
		}		
		
		return false;			
	}
	
	protected void removeEdge(int index) {
		list[index] = list[--size];
		list[size] = null;
	}
	
	public Edge edgeAt(int i) {
		return list[i];
	}
	
	public int size() {
		return size;
	}
	
	public EdgeList sort() {
	    EdgeList sorted = new EdgeList( this );
	    Arrays.sort( sorted.list, new Comparator<Edge>() {
            public int compare(Edge e1, Edge e2) {
                return e1.getDepends().max() - e2.getDepends().max();
            }});
	    return sorted;
	}
	
	public boolean isEmpty() {
		return size == 0;
	}
	
	public Iterator<Edge> iterator() {
		return new EdgeIterator();
	}
	
	private EdgeList findEdges(Role role, Individual from, Node to) {
		EdgeList result = new EdgeList();
		
		for(int i = 0; i < size; i++) {
			Edge e = list[i];
            if( (from == null || from.equals( e.getFrom() )) &&
                (role == null || e.getRole().isSubRoleOf(role)) &&
                (to == null || to.equals( e.getTo() )) )
				result.addEdge(e);
		}		
		
		return result;
	}
	
	public EdgeList getEdgesFromTo(Individual from, Node to) {
		return findEdges(null, from, to);	
	}
	
	public EdgeList getEdgesFrom(Individual from) {
		return findEdges(null, from, null);	
	}

	public EdgeList getEdgesTo(Node to) {
		return findEdges(null, null, to);	
	}

	public EdgeList getEdgesTo(Role r, Node to) {
		return findEdges(r, null, to);	
	}

	public EdgeList getEdgesFrom(Individual from, Role r) {
		return findEdges(r, from, null);	
	}
		
	public EdgeList getEdges(Role role) {
		EdgeList result = new EdgeList();
		
		for(int i = 0; i < size; i++) {
			Edge e = list[i];
            if( e.getRole().isSubRoleOf(role))
				result.addEdge(e);
		}		
		
		return result;
	}

	public Set<Role> getRoles() {
		Set<Role> result = new HashSet<Role>();
		
		for(int i = 0; i < size; i++) {
			Edge e = list[i];
			result.add(e.getRole());
		}
		
		return result;
	}

	public Set<Node> getNeighbors( Node node ) {
		Set<Node> result = new HashSet<Node>();
		
		for(int i = 0; i < size; i++) {
			Edge e = list[i];
			result.add(e.getNeighbor(node));
		}
		
		return result;
	}
	
    /**
     * Find the neighbors of a node that has a certain type. For literals, we collect
     * only the ones with the same language tag.
     * 
     * @param node The node whose neighbors are being sought
     * @param c The concept (or datatype) that each neighbor should belong to 
     * @return Set of nodes
     */
	public Set<Node> getFilteredNeighbors( Individual node, ATermAppl c ) {
        Set<Node> result = new HashSet<Node>();

        String lang = null;
		for(int i = 0; i < size; i++) {
			Edge edge = list[i];
            Node neighbor = edge.getNeighbor( node );

            if( !ATermUtils.isTop( c ) && !neighbor.hasType( c ) )
                continue;
            else if( neighbor instanceof Literal ) {
                Literal lit = (Literal) neighbor;
                if( lang == null ) {
                    lang = lit.getLang();
                    result.add( neighbor );
                }
                else if( lang.equals( lit.getLang() ) ) {
                    result.add( neighbor );
                }
            }
            else
                result.add( neighbor );
        }

        return result;
    }
	
	public boolean hasEdgeFrom(Individual from) {
		return hasEdge(from, null, null);
	}
	
	public boolean hasEdgeFrom(Individual from, Role role) {
		return hasEdge(from, role, null);
	}
	
	public boolean hasEdgeTo(Node to) {
		return hasEdge(null, null, to);
	}

	public boolean hasEdgeTo(Role role, Node to) {
		return hasEdge(null, role, to);
	}
	
	public boolean hasEdge(Role role) {
		return hasEdge(null, role, null);
	}
	
	/**
	 * Checks if this list contains an edge matching the given subject,
	 * predicate and object. A null parameter is treated as a wildcard
	 * matching every value and predicates are matched by considering
	 * the subproperty hierarchy, i.e. passing the parameter <code>sup</code>
	 * to this function will return <code>true</code> if an edge with
	 * subproperty <code>sub</code> exists.
	 * 
	 * @param from
	 * @param role
	 * @param to
	 * @return
	 */
	public boolean hasEdge(Individual from, Role role, Node to) {
		for(int i = 0; i < size; i++) {
			Edge e = list[i];
            if( (from == null || from.equals( e.getFrom() )) &&
                (role == null || e.getRole().isSubRoleOf(role)) &&
                (to == null || to.equals( e.getTo() )) )
				return true;
		}		
		
		return false;
	}
	
	/**
	 * Similar to {@link #hasEdge(Individual, Role, Node)} but does not
	 * consider subproperty hierarchy for matching so only exact predicate
	 * matches are considered.
	 * 
	 * @param from
	 * @param role
	 * @param to
	 * @return
	 */
	public boolean hasExactEdge(Individual from, Role role, Node to) {
		for(int i = 0; i < size; i++) {
			Edge e = list[i];
            if( (from == null || from.equals( e.getFrom() )) &&
                (role == null || e.getRole().equals(role)) &&
                (to == null || to.equals( e.getTo() )) )
				return true;
		}		
		
		return false;
	}
	
	public boolean hasEdge(Edge e) {
		return hasEdge(e.getFrom(), e.getRole(), e.getTo());
	}
	
	public Edge getExactEdge(Individual from, Role role, Node to) {
		for(int i = 0; i < size; i++) {
			Edge e = list[i];
            if( (from == null || from.equals( e.getFrom() )) &&
                (role == null || e.getRole().equals(role)) &&
                (to == null || to.equals( e.getTo() )) )
				return e;
		}		
		
		return null;
	}
	
	public DependencySet getDepends(boolean doExplanation) {
		DependencySet ds = DependencySet.INDEPENDENT;
		
		for(int i = 0; i < size; i++) {
			Edge e = list[i];
            ds = ds.union( e.getDepends(), doExplanation );
		}		
		
		return ds;
	}

	public String toString() {
		if( size == 0 )
			return "[]";

		StringBuilder b = new StringBuilder();
		b.append( '[' );
		b.append( list[0] );
		for( int i = 1; i < size; i++ ) {
			b.append( ", " );
			b.append( list[i] );
		} 
		b.append( ']' );
		return b.toString();
	}

	/**
	 * Resets the edges in this list to only asserted edges.
	 */
	public void reset() {
		for(int i = 0; i < size; i++) {
			Edge e = list[i];
            
			if( e.getDepends().getBranch() != DependencySet.NO_BRANCH ) {
				removeEdge( i-- );
			}
		}
	}
}
