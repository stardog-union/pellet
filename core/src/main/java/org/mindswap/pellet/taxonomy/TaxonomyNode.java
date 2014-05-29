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

package org.mindswap.pellet.taxonomy;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * @author Evren Sirin
 */
public class TaxonomyNode<T> {

	private Map<Object, Object>		dataMap	= new HashMap<Object, Object>();

	private Set<T>					equivalents;

	private boolean					hidden;

	protected Boolean				mark;
	private T						name;

	protected short					depth	= 0;
	
	private Set<TaxonomyNode<T>>	subs = new HashSet<TaxonomyNode<T>>(2);
	private Set<TaxonomyNode<T>>	supers = new HashSet<TaxonomyNode<T>>();

	public TaxonomyNode(T name, boolean hidden) {
		this.name = name;
		this.hidden = hidden;

		if( name == null )
			equivalents = Collections.emptySet();
		else
			equivalents = Collections.singleton( name );
	}

	public TaxonomyNode(Collection<T> equivalents, boolean hidden) {

		if( equivalents == null || equivalents.isEmpty() ) {
			this.name = null;
			this.equivalents = Collections.emptySet();
		}
		else {
			this.name = equivalents.iterator().next();
			this.equivalents = new HashSet<T>( equivalents );
		}

		this.hidden = hidden;
	}

	public void addEquivalent(T t) {
		if( equivalents.size() < 2 )
			equivalents = new HashSet<T>( equivalents );

		equivalents.add( t );
	}

	public void addSub(TaxonomyNode<T> other) {
		if( this.equals( other ) || subs.contains( other ) ) {
			return;
		}

		subs.add( other );
		if( !hidden )
			other.supers.add( this );
	}

	public void addSubs(Collection<TaxonomyNode<T>> others) {
		for( TaxonomyNode<T> t : others ) {
			addSub( t );
		}
	}

	public void addSupers(Collection<TaxonomyNode<T>> others) {
		supers.addAll( others );
		if( !hidden ) {
			for( TaxonomyNode<T> other : others ) {
				other.subs.add( this );
			}
		}
	}

	public void clearData() {
		dataMap.clear();
	}

	public boolean contains(T t) {
		return equivalents.contains( t );
	}

	public void disconnect() {
		for( Iterator<TaxonomyNode<T>> j = subs.iterator(); j.hasNext(); ) {
			TaxonomyNode<T> sub = j.next();
			j.remove();
			sub.supers.remove( this );
		}

		for( Iterator<TaxonomyNode<T>> j = supers.iterator(); j.hasNext(); ) {
			TaxonomyNode<T> sup = j.next();
			j.remove();
			sup.subs.remove( this );
		}
	}

	public Object getDatum(Object key) {
		return dataMap.get( key );
	}

	public Set<T> getEquivalents() {
		return equivalents;
	}

	public T getName() {
		return name;
	}

	public Collection<TaxonomyNode<T>> getSubs() {
		return subs;
	}

	public Collection<TaxonomyNode<T>> getSupers() {
		return supers;
	}

	public boolean isBottom() {
		return subs.isEmpty();
	}

	public boolean isHidden() {
		return hidden;
	}

	public boolean isLeaf() {
		return subs.size() == 1 && subs.iterator().next().isBottom();
	}

	public boolean isTop() {
		return supers.isEmpty();
	}

	public void print() {
		print( "" );
	}

	public void print(String indent) {
		if( subs.isEmpty() )
			return;

		System.out.print( indent );
		Iterator<T> i = equivalents.iterator();
		while( i.hasNext() ) {
			System.out.print( i.next() );
			if( i.hasNext() )
				System.out.print( " = " );
		}
		System.out.println();
		
		indent += "  ";
		for( TaxonomyNode<T> sub : subs ) {
			sub.print( indent );
		}
	}

	public Object putDatum(Object key, Object value) {
		return dataMap.put( key, value );
	}
	

	public Object removeDatum(Object key) {
		return dataMap.remove( key );
	}

	public void removeMultiplePaths() {
		if( !hidden ) {
			for( TaxonomyNode<T> sup : supers ) {
				for( TaxonomyNode<T> sub : subs ) {
					sup.removeSub( sub );
				}
			}
		}
	}
	
	public void removeEquivalent(T t) {
		equivalents.remove( t );
		
		if( name != null && name.equals( t ) ) {
			name = equivalents.iterator().next();
		}
	}

	public void removeSub(TaxonomyNode<T> other) {
		subs.remove( other );
		other.supers.remove( this );
	}

	public void setHidden(boolean hidden) {
		this.hidden = hidden;
	}

	@Deprecated
	public void setSubs(Set<TaxonomyNode<T>> subs) {
		this.subs = subs;
	}

	@Deprecated
	public void setSupers(Set<TaxonomyNode<T>> supers) {
		this.supers = supers;
	}

	public String toString() {
		return name.toString();// + " = " + equivalents;
	}
}