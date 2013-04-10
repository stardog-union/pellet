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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.TreeMap;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.mindswap.pellet.exceptions.InternalReasonerException;
import org.mindswap.pellet.utils.Bool;

import com.clarkparsia.pellet.utils.CollectionUtils;

/*
 * Created on Aug 13, 2003
 */

/**
 * @author Evren Sirin
 */
public class Taxonomy<T> {
	private class DatumEquivalentsPairIterator<U> implements Iterator<Map.Entry<Set<U>, Object>> {

		private Iterator<TaxonomyNode<U>>	i;
		private Object						key;

		public DatumEquivalentsPairIterator(Taxonomy<U> t, Object key) {
			this.key = key;
			i = t.getNodes().iterator();
		}

		public boolean hasNext() {
			return i.hasNext();
		}

		public Entry<Set<U>, Object> next() {
			TaxonomyNode<U> current = i.next();
			return new SimpleImmutableEntry<Set<U>, Object>( Collections.unmodifiableSet( current
					.getEquivalents() ), current.getDatum( key ) );
		}

		public void remove() {
			throw new UnsupportedOperationException();
		}
	}

	private class DepthFirstDatumOnlyIterator<U> implements Iterator<Object> {

		private Object					key;
		private List<TaxonomyNode<U>>	pending;
		private Set<TaxonomyNode<U>>	visited;

		public DepthFirstDatumOnlyIterator(Taxonomy<U> t, U u, Object key) {
			this.key = key;
			visited = new HashSet<TaxonomyNode<U>>();
			pending = new ArrayList<TaxonomyNode<U>>();
			TaxonomyNode<U> node = t.getNode( u );
			if( node != null )
				pending.add( node );
		}

		public boolean hasNext() {
			return !pending.isEmpty();
		}

		public Object next() {
			if( pending.isEmpty() )
				throw new NoSuchElementException();

			TaxonomyNode<U> current = pending.remove( pending.size() - 1 );
			visited.add( current );
			for( TaxonomyNode<U> sub : current.getSubs() ) {
				if( !visited.contains( sub ) )
					pending.add( sub );
			}

			return current.getDatum( key );
		}

		public void remove() {
			throw new UnsupportedOperationException();
		}
	}

	private class SimpleImmutableEntry<K, V> implements Map.Entry<K, V> {

		private K	key;
		private V	value;

		public SimpleImmutableEntry(K key, V value) {
			super();
			this.key = key;
			this.value = value;
		}

		public K getKey() {
			return key;
		}

		public V getValue() {
			return value;
		}

		public V setValue(V value) {
			throw new UnsupportedOperationException();
		}
	}

	public static final Logger			log			= Logger.getLogger( Taxonomy.class.getName() );

	private static final boolean		SUB			= true;

	private static final boolean		SUPER		= false;

	public static final boolean			TOP_DOWN	= true;

	protected TaxonomyNode<T>			bottomNode;
	protected Map<T, TaxonomyNode<T>>	nodes;
	protected TaxonomyNode<T>			topNode;
	
	protected short depth = 0;
	protected int totalBranching = 0;

	public Taxonomy() {
		this( null, null, null );
	}

	public Taxonomy(Collection<T> elements, T top, T bottom) {
		nodes = CollectionUtils.makeMap();

		if( top == null )
			topNode = new TaxonomyNode<T>( (T) null, /* hidden = */true );
		else {
			topNode = new TaxonomyNode<T>( top, /* hidden = */false );
			nodes.put( top, topNode );
		}

		if( bottom == null )
			bottomNode = new TaxonomyNode<T>( (T) null, /* hidden = */true );
		else {
			bottomNode = new TaxonomyNode<T>( bottom, /* hidden = */false );
			nodes.put( bottom, bottomNode );
		}

		if( elements == null || elements.isEmpty() )
			topNode.addSub( bottomNode );
		else
			for( T t : elements ) {
				addNode( t, /* hidden = */false );
			}

		// precaution to avoid creating an invalid taxonomy is now done by
		// calling assertValid function because the taxonomy might be invalid
		// during the merge operation but it is guaranteed to be valid after
		// the merge is completed. so we check for validity at the very end
		// TOP_NODE.setSupers( Collections.EMPTY_LIST );
		// BOTTOM_NODE.setSubs( Collections.EMPTY_LIST );
	}

	public void addEquivalentNode(T t, TaxonomyNode<T> node) {
		node.addEquivalent( t );
		nodes.put( t, node );
	}

	/**
	 * Add a collection of elements equivalent to an element already in the
	 * taxonomy.
	 */
	public void addEquivalents(T t, Collection<T> eqs) {

		assert nodes.keySet().contains( t ) : "Element " + t.toString() + " not in taxonomy";

		TaxonomyNode<T> node = nodes.get( t );
		for( T eq : eqs ) {
			assert !nodes.keySet().contains( eq ) : "Element " + eq.toString()
					+ " alread in taxonomy";
			node.addEquivalent( eq );
			nodes.put( eq, node );
		}
	}
	
	/**
	 * Add a node with known supers and subs. Any direct relations between subs
	 * and supers are removed.
	 * 
	 * @param equivalents
	 *            a non-empty set of equivalent elements defining the node (one
	 *            of which becomes the label)
	 * @param sups
	 *            collection of supers, all of which must already exist in the
	 *            taxonomy
	 * @param subs
	 *            collection of subs, all of which must already exist in the
	 *            taxonomy
	 * @param hidden
	 *            indicates hidden or not
	 * @return the new node
	 */
	public TaxonomyNode<T> addNode(Collection<T> equivalents, Collection<T> sups,
			Collection<T> subs, boolean hidden) {

		assert !equivalents.isEmpty() : "Taxonomy nodes must have at least one element";
		assert nodes.keySet().containsAll( sups ) : "At least one super element not in taxonomy";
		assert nodes.keySet().containsAll( subs ) : "At least one sub element not in taxonomy";

		TaxonomyNode<T> node = new TaxonomyNode<T>( equivalents, hidden );
		for( T t : equivalents ) {
			nodes.put( t, node );
		}
		
		short depth = 1;

		// Super handling
		{
			/*
			 * Note the special case when no supers are provided and top is
			 * hidden. Top points to the new node, but not the reverse
			 */
			if( sups.isEmpty() ) {
				if( topNode.isHidden() ) {
					topNode.addSub( node );
					if( topNode.getSubs().size() == 2 )
						topNode.removeSub( bottomNode );
				}
				else
					node.addSupers( Collections.singleton( topNode ) );
				
				totalBranching += 1;
			}
			else {
				Set<TaxonomyNode<T>> supNodes = new HashSet<TaxonomyNode<T>>();
				for( T sup : sups ) {
					TaxonomyNode<T> supNode = nodes.get( sup );
					if( supNode.depth >= depth )
						depth = (short) (supNode.depth + 1);
					supNodes.add( supNode );
				}
				node.depth = depth;
				if( depth > this.depth )
					this.depth = depth;
				node.addSupers( supNodes );
				
				totalBranching += supNodes.size(); 
			}
		}

		// Sub handling
		{
			Set<TaxonomyNode<T>> subNodes;
			if( subs.isEmpty() ) {
				if( bottomNode.isHidden() ) {
					bottomNode.addSupers( Collections.singleton( node ) );
					bottomNode.getSupers().removeAll( node.getSupers() );
				}
				else
					node.addSub( bottomNode );
				
				totalBranching += 1;
			}
			else {
				subNodes = new HashSet<TaxonomyNode<T>>();
				for( T sub : subs ) {
					subNodes.add( nodes.get( sub ) );
				}
				node.addSubs( subNodes );
				
				totalBranching += subNodes.size(); 
			}
		}

		node.removeMultiplePaths();

		return node;
	}

	public TaxonomyNode<T> addNode(T t, boolean hidden) {
		TaxonomyNode<T> node = new TaxonomyNode<T>( t, hidden );
		topNode.addSub( node );
		node.addSub( bottomNode );
		nodes.put( t, node );
		return node;
	}

	/**
	 * Add a collection of elements as subs to an element
	 */
	public void addSuper(Collection<T> subs, T sup) {

		assert nodes.keySet().containsAll( subs ) : "At least one sub element not in taxonomy";
		assert nodes.keySet().contains( sup ) : "Super element " + sup.toString()
				+ " not in taxonomy";

		Set<TaxonomyNode<T>> subNodes = new HashSet<TaxonomyNode<T>>();
		for( T sub : subs ) {
			subNodes.add( nodes.get( sub ) );
		}
		TaxonomyNode<T> supNode = nodes.get( sup );

		for( TaxonomyNode<T> subNode : subNodes ) {
			if( subNode.getSupers().size() == 1 && subNode.getSupers().contains( topNode ) )
				topNode.removeSub( subNode );
		}

		if( supNode.getSubs().size() == 1 && supNode.getSubs().contains( bottomNode ) )
			supNode.removeSub( bottomNode );

		supNode.addSubs( subNodes );

	}

	/**
	 * Add a sub/super relation
	 */
	public void addSuper(T sub, T sup) {

		assert nodes.keySet().contains( sub ) : "Sub element " + sub.toString()
				+ " not in taxonomy";
		assert nodes.keySet().contains( sup ) : "Super element " + sup.toString()
				+ " not in taxonomy";

		TaxonomyNode<T> subNode = nodes.get( sub );
		TaxonomyNode<T> supNode = nodes.get( sup );
		if( subNode.equals( supNode ) )
			throw new InternalReasonerException(
					"Equivalent elements cannot have sub/super relationship" );

		if( subNode.getSupers().size() == 1 && subNode.getSupers().iterator().next() == topNode )
			topNode.removeSub( subNode );

		if( supNode.getSubs().size() == 1 && supNode.getSubs().iterator().next() == bottomNode )
			supNode.removeSub( bottomNode );

		supNode.addSub( subNode );
	}

	/**
	 * Add a collection of supers to an element
	 */
	public void addSupers(T sub, Collection<T> sups) {

		assert nodes.keySet().contains( sub ) : "Sub element " + sub.toString()
				+ " not in taxonomy";
		assert nodes.keySet().containsAll( sups ) : "At least one super element not in taxonomy";

		TaxonomyNode<T> subNode = nodes.get( sub );
		Set<TaxonomyNode<T>> supNodes = new HashSet<TaxonomyNode<T>>();
		for( T sup : sups ) {
			supNodes.add( nodes.get( sup ) );
		}

		if( subNode.getSupers().size() == 1 && subNode.getSupers().contains( topNode ) )
			topNode.removeSub( subNode );

		for( TaxonomyNode<T> supNode : supNodes ) {
			if( supNode.getSubs().size() == 1 && supNode.getSubs().contains( bottomNode ) )
				supNode.removeSub( bottomNode );
		}

		subNode.addSupers( supNodes );
	}

	public void assertValid() {
		assert topNode.getSupers().isEmpty() : "Top node in the taxonomy has parents";
		assert bottomNode.getSubs().isEmpty() : "Bottom node in the taxonomy has children";
	}

	/**
	 * Given a list of concepts, find all the Least Common Ancestors (LCA). Note
	 * that a taxonomy is DAG not a tree so we do not have a unique LCA but a
	 * set of LCA.
	 */
	public List<T> computeLCA(List<T> list) {
		// FIXME does not work when one of the elements is an ancestor of the
		// rest
		// TODO what to do with equivalent classes?
		// TODO improve efficiency

		if( list.isEmpty() )
			return null;

		// get the first concept
		T t = list.get( 0 );

		// add all its ancestor as possible LCA candidates
		List<T> ancestors = new ArrayList<T>( getFlattenedSupers( t, /* direct = */false ) );

		for( int i = 1; (i < list.size()) && (ancestors.size() > 0); i++ ) {
			t = list.get( i );

			// take the intersection of possible candidates to get rid of
			// uncommon ancestors
			ancestors.retainAll( getFlattenedSupers( t, /* direct = */false ) );
		}

		Set<T> toBeRemoved = new HashSet<T>();

		// we have all common ancestors now remove the ones that have
		// descendants in the list
		for( T a : ancestors ) {

			if( toBeRemoved.contains( a ) )
				continue;

			Set<T> supers = getFlattenedSupers( a, /* direct = */false );
			toBeRemoved.addAll( supers );
		}

		ancestors.removeAll( toBeRemoved );

		return ancestors;
	}

	public boolean contains(T t) {
		return nodes.containsKey( t );
	}

	/**
	 * Iterate over nodes in taxonomy (no specific order)returning pair of
	 * equivalence set and datum associated with {@code key} for each. Useful,
	 * e.g., to collect equivalence sets matching some condition on the datum
	 * (as in all classes which have a particular instances)
	 * 
	 * @param key
	 *            key associated with datum returned
	 * @return iterator over equivalence set, datum pairs
	 */
	public Iterator<Map.Entry<Set<T>, Object>> datumEquivalentsPair(Object key) {
		return new DatumEquivalentsPairIterator<T>( this, key );
	}

	/**
	 * Iterate down taxonomy in a depth first traversal, beginning with class
	 * {@code c}, returning only datum associated with {@code key} for each.
	 * Useful, e.g., to collect datum values in a transitive closure (as in all
	 * instances of a class).
	 * 
	 * @param t
	 *            starting location in taxonomy
	 * @param key
	 *            key associated with datum returned
	 * @return datum iterator
	 */
	public Iterator<Object> depthFirstDatumOnly(T t, Object key) {
		return new DepthFirstDatumOnlyIterator<T>( this, t, key );
	}

	/**
	 * Returns all the classes that are equivalent to class c. Class c itself is
	 * included in the result.
	 * 
	 * @param t
	 *            class whose equivalent classes are found
	 * @return A set of ATerm objects
	 */
	public Set<T> getAllEquivalents(T t) {
		TaxonomyNode<T> node = nodes.get( t );

		if( node == null )
			return new HashSet<T>();

		Set<T> result = new HashSet<T>( node.getEquivalents() );

		return result;
	}

	public TaxonomyNode<T> getBottom() {
		return bottomNode;
	}

	public Set<T> getClasses() {
		return nodes.keySet();
	}

	/**
	 * Get datum on taxonomy elements associated with {@code key}
	 * 
	 * @param t
	 *            identifies the taxonomy element
	 * @param key
	 *            identifies the specific datum
	 * @return the datum (or {@code null} if none is associated with {@code key})
	 */
	public Object getDatum(T t, Object key) {
		TaxonomyNode<T> node = nodes.get( t );
		return (node == null)
			? null
			: node.getDatum( key );
	}

	/**
	 * Returns all the classes that are equivalent to class c. Class c itself is
	 * NOT included in the result.
	 * 
	 * @param t
	 *            class whose equivalent classes are found
	 * @return A set of ATerm objects
	 */
	public Set<T> getEquivalents(T t) {
		Set<T> result = getAllEquivalents( t );
		result.remove( t );

		return result;
	}

	/**
	 * As in {@link #getSubs(Object, boolean)} except the return value is the
	 * union of nested sets
	 */
	public Set<T> getFlattenedSubs(T t, boolean direct) {
		return getFlattenedSubSupers( t, direct, SUB );
	}

	/**
	 * Use {@link #getFlattenedSubs(Object, boolean)} or
	 *             {@link #getFlattenedSupers(Object, boolean)} this method will
	 *             become private
	 */
	private Set<T> getFlattenedSubSupers(T t, boolean direct, boolean subOrSuper) {
		TaxonomyNode<T> node = nodes.get( t );

		Set<T> result = new HashSet<T>();

		List<TaxonomyNode<T>> visit = new ArrayList<TaxonomyNode<T>>();
		visit.addAll( (subOrSuper == SUB)
			? node.getSubs()
			: node.getSupers() );

		for( int i = 0; i < visit.size(); i++ ) {
			node = visit.get( i );

			if( node.isHidden() )
				continue;

			Set<T> add = node.getEquivalents();
			result.addAll( add );

			if( !direct )
				visit.addAll( (subOrSuper == SUB)
					? node.getSubs()
					: node.getSupers() );
		}

		return result;
	}

	/**
	 * As in {@link #getSupers(Object, boolean)} except the return value is the
	 * union of nested sets
	 */
	public Set<T> getFlattenedSupers(T t, boolean direct) {
		return getFlattenedSubSupers( t, direct, SUPER );
	}

	public TaxonomyNode<T> getNode(T t) {
		return nodes.get( t );
	}

	public Collection<TaxonomyNode<T>> getNodes() {
		return nodes.values();
	}

	/**
	 * Returns all the (named) subclasses of class c. The class c itself is not
	 * included in the list but all the other classes that are equivalent to c
	 * are put into the list. Also note that the returned list will always have
	 * at least one element, that is the BOTTOM concept. By definition BOTTOM
	 * concept is subclass of every concept. This function is equivalent to
	 * calling getSubClasses(c, true).
	 * 
	 * @param t
	 *            class whose subclasses are returned
	 * @return A set of sets, where each set in the collection represents an
	 *         equivalence class. The elements of the inner class are ATermAppl
	 *         objects.
	 */
	public Set<Set<T>> getSubs(T t) {
		return getSubs( t, false );
	}

	/**
	 * Returns the (named) subclasses of class c. Depending on the second
	 * parameter the resulting list will include either all subclasses or only
	 * the direct subclasses. A class d is a direct subclass of c iff
	 * <ol>
	 * <li>d is subclass of c</li>
	 * <li>there is no other class x different from c and d such that x is
	 * subclass of c and d is subclass of x</li>
	 * </ol>
	 * The class c itself is not included in the list but all the other classes
	 * that are sameAs c are put into the list. Also note that the returned list
	 * will always have at least one element. The list will either include one
	 * other concept from the hierarchy or the BOTTOM concept if no other class
	 * is subsumed by c. By definition BOTTOM concept is subclass of every
	 * concept.
	 * 
	 * @param t
	 *            Class whose subclasses are found
	 * @param direct
	 *            If true return only direct subclasses elese return all the
	 *            subclasses
	 * @return A set of sets, where each set in the collection represents an
	 *         equivalence class. The elements of the inner class are ATermAppl
	 *         objects.
	 */
	public Set<Set<T>> getSubs(T t, boolean direct) {
		return getSubSupers( t, direct, SUB );
	}

	/**
	 *  Use {@link #getSubs(Object, boolean)} or
	 *             {@link #getSupers(Object, boolean)} this method will become
	 *             private
	 */
	private Set<Set<T>> getSubSupers(T t, boolean direct, boolean subOrSuper) {
		TaxonomyNode<T> node = nodes.get( t );

		if( node == null )
			return Collections.emptySet();

		Set<Set<T>> result = new HashSet<Set<T>>();

		List<TaxonomyNode<T>> visit = new ArrayList<TaxonomyNode<T>>();
		visit.addAll( (subOrSuper == SUB)
			? node.getSubs()
			: node.getSupers() );

		for( int i = 0; i < visit.size(); i++ ) {
			node = visit.get( i );

			if( node.isHidden() )
				continue;

			Set<T> add = new HashSet<T>( node.getEquivalents() );
			if( !add.isEmpty() ) {
				result.add( add );
			}

			if( !direct )
				visit.addAll( (subOrSuper == SUB)
					? node.getSubs()
					: node.getSupers() );
		}

		return result;
	}

	/**
	 * Returns all the superclasses (implicitly or explicitly defined) of class
	 * c. The class c itself is not included in the list. but all the other
	 * classes that are sameAs c are put into the list. Also note that the
	 * returned list will always have at least one element, that is TOP concept.
	 * By definition TOP concept is superclass of every concept. This function
	 * is equivalent to calling getSuperClasses(c, true).
	 * 
	 * @param t
	 *            class whose superclasses are returned
	 * @return A set of sets, where each set in the collection represents an
	 *         equivalence class. The elements of the inner class are ATermAppl
	 *         objects.
	 */
	public Set<Set<T>> getSupers(T t) {
		return getSupers( t, false );
	}

	/**
	 * Returns the (named) superclasses of class c. Depending on the second
	 * parameter the resulting list will include either all or only the direct
	 * superclasses. A class d is a direct superclass of c iff
	 * <ol>
	 * <li> d is superclass of c </li>
	 * <li> there is no other class x such that x is superclass of c and d is
	 * superclass of x </li>
	 * </ol>
	 * The class c itself is not included in the list but all the other classes
	 * that are sameAs c are put into the list. Also note that the returned list
	 * will always have at least one element. The list will either include one
	 * other concept from the hierarchy or the TOP concept if no other class
	 * subsumes c. By definition TOP concept is superclass of every concept.
	 * 
	 * @param t
	 *            Class whose subclasses are found
	 * @param direct
	 *            If true return all the superclasses else return only direct
	 *            superclasses
	 * @return A set of sets, where each set in the collection represents an
	 *         equivalence class. The elements of the inner class are ATermAppl
	 *         objects.
	 */
	public Set<Set<T>> getSupers(T t, boolean direct) {
		return getSubSupers( t, direct, SUPER );
	}

	public TaxonomyNode<T> getTop() {
		return topNode;
	}

	/**
	 * Checks if x is equivalent to y
	 * 
	 * @param x
	 *            Name of the first class
	 * @param y
	 *            Name of the second class
	 * @return true if x is equivalent to y
	 */
	public Bool isEquivalent(T x, T y) {
		TaxonomyNode<T> nodeX = nodes.get( x );
		TaxonomyNode<T> nodeY = nodes.get( y );

		if( nodeX == null || nodeY == null )
			return Bool.UNKNOWN;
		else if( nodeX.equals( nodeY ) )
			return Bool.TRUE;
		else
			return Bool.FALSE;
	}

	/**
	 * Checks if x has an ancestor y.
	 * 
	 * @param x
	 *            Name of the node
	 * @param y
	 *            Name of the ancestor ode
	 * @return true if x has an ancestor y
	 */
	public Bool isSubNodeOf(T x, T y) {
		TaxonomyNode<T> nodeX = nodes.get( x );
		TaxonomyNode<T> nodeY = nodes.get( y );

		if( nodeX == null || nodeY == null )
			return Bool.UNKNOWN;
		else if( nodeX.equals( nodeY ) )
			return Bool.TRUE;

		if( nodeX.isHidden() ) {
			if( nodeY.isHidden() )
				return Bool.UNKNOWN;
			else
				return getFlattenedSupers( x, /* direct = */false ).contains( y )
					? Bool.TRUE
					: Bool.FALSE;
		}
		else
			return getFlattenedSubs( y, false ).contains( x )
				? Bool.TRUE
				: Bool.FALSE;
	}

	public void merge(TaxonomyNode<T> node1, TaxonomyNode<T> node2) {
		List<TaxonomyNode<T>> mergeList = new ArrayList<TaxonomyNode<T>>( 2 );
		mergeList.add( node1 );
		mergeList.add( node2 );

		TaxonomyNode<T> node = mergeNodes( mergeList );

		removeCycles( node );
	}

	private TaxonomyNode<T> mergeNodes(List<TaxonomyNode<T>> mergeList) {

		assert mergeList.size() > 1 : "Attempt to merge less than two nodes";

		if( log.isLoggable( Level.FINER ) )
			log.finer( "Merge " + mergeList );

		TaxonomyNode<T> node = null;
		if( mergeList.contains( topNode ) ) {
			node = topNode;
		}
		else if( mergeList.contains( bottomNode ) ) {
			node = bottomNode;
		}
		else
			node = mergeList.get( 0 );

		Set<TaxonomyNode<T>> merged = new HashSet<TaxonomyNode<T>>();
		merged.add( node );

		for( TaxonomyNode<T> other : mergeList ) {

			if( merged.contains( other ) )
				continue;
			else
				merged.add( other );

			for( TaxonomyNode<T> sub : other.getSubs() ) {
				if( (sub != bottomNode) && !mergeList.contains( sub ) ) {
					if( (node.getSubs().size() == 1)
							&& (node.getSubs().iterator().next() == bottomNode) )
						node.removeSub( bottomNode );
					node.addSub( sub );
				}
			}

			for( TaxonomyNode<T> sup : other.getSupers() ) {
				if( (sup != topNode) && !mergeList.contains( sup ) ) {
					if( (node.getSupers().size() == 1)
							&& (node.getSupers().iterator().next() == topNode) )
						topNode.removeSub( node );
					sup.addSub( node );
				}
			}

			other.disconnect();

			for( T t : other.getEquivalents() ) {
				addEquivalentNode( t, node );
			}

		}

		node.clearData();

		if( node != topNode && node.getSupers().isEmpty() )
			topNode.addSub( node );

		if( node != bottomNode && node.getSubs().isEmpty() )
			node.addSub( bottomNode );

		return node;
	}

	/**
	 * Set a datum value associated with {@code key} on a taxonomy element
	 * 
	 * @param t
	 *            identifies the taxonomy element
	 * @param key
	 *            identifies the datum
	 * @param value
	 *            the datum
	 * @return previous value of datum or {@code null} if not set
	 */
	public Object putDatum(T t, Object key, Object value) {
		TaxonomyNode<T> node = nodes.get( t );
		if( node == null )
			throw new RuntimeException( t + " is an unknown class!" );

		return node.putDatum( key, value );
	}

	/**
	 * Remove an element from the taxonomy.
	 */
	public void remove(T t) {
		assert nodes.containsKey( t ) : "Element not contained in taxonomy";

		TaxonomyNode<T> node = nodes.remove( t );
		if( node.getEquivalents().size() == 1 ) {
			Collection<TaxonomyNode<T>> subs = node.getSubs();
			Collection<TaxonomyNode<T>> supers = node.getSupers();
			node.disconnect();
			for( TaxonomyNode<T> sup : supers )
				sup.addSubs( subs );
		}
		else
			node.removeEquivalent( t );
	}

	/**
	 * Walk through the super nodes of the given node and when a cycle is
	 * detected merge all the nodes in that path
	 */
	public void removeCycles(TaxonomyNode<T> node) {
		if( !nodes.get( node.getName() ).equals( node ) )
			throw new InternalReasonerException( "This node does not exist in the taxonomy: "
					+ node.getName() );
		removeCycles( node, new ArrayList<TaxonomyNode<T>>() );
	}

	/**
	 * Given a node and (a possibly empty) path of sub nodes, remove cycles by
	 * merging all the nodes in the path.
	 */
	private boolean removeCycles(TaxonomyNode<T> node, List<TaxonomyNode<T>> path) {
		// cycle detected
		if( path.contains( node ) ) {
			mergeNodes( path );
			return true;
		}
		else {
			// no cycle yet, add this node to the path and continue
			path.add( node );
			
			List<TaxonomyNode<T>> supers = new ArrayList<TaxonomyNode<T>>( node.getSupers() );
			for( int i = 0; i < supers.size(); ) {
				TaxonomyNode<T> sup = supers.get( i );
				// remove cycles involving super node
				removeCycles( sup, path );
				// if the super has been removed then no need
				// to increment the index
				if( i < supers.size() && supers.get( i ).equals( sup ) )
					i++;
			}
			
			// remove the node from the path
			path.remove( path.size() - 1 );
			
			return false;
		}
	}

	public Object removeDatum(T t, Object key) {
		return getNode( t ).removeDatum( key );
	}
	
	/**
	 * Clear existing supers for an element and set to a new collection
	 */
	public void resetSupers(T t, Collection<T> supers) {

		assert nodes.keySet().contains( t ) : "Element " + t.toString() + " not in taxonomy";
		assert nodes.keySet().containsAll( supers ) : "Supers not all contained in taxonomy";

		TaxonomyNode<T> node = nodes.get( t );

		List<TaxonomyNode<T>> initial = new ArrayList<TaxonomyNode<T>>( node.getSupers() );
		for( TaxonomyNode<T> n : initial )
			n.removeSub( node );

		if( supers.isEmpty() ) {
			topNode.addSub( node );
		}
		else {
			Set<TaxonomyNode<T>> added = new HashSet<TaxonomyNode<T>>();
			for( T sup : supers ) {
				TaxonomyNode<T> n = nodes.get( sup );
				if( added.add( n ) )
					n.addSub( node );
			}
		}
	}

	
	/**
	 * Sort the nodes in the taxonomy using topological ordering starting from
	 * top to bottom.
	 * 
	 * @param includeEquivalents
	 *            If false the equivalents in a node will be ignored and only
	 *            the name of the node will be added to the result
	 * @return List of node names sorted in topological ordering
	 */	
	public List<T> topologocialSort(boolean includeEquivalents) {
		return topologocialSort( includeEquivalents, null );
	}
	
	/**
	 * Sort the nodes in the taxonomy using topological ordering starting from
	 * top to bottom.
	 * 
	 * @param includeEquivalents
	 *            If false the equivalents in a node will be ignored and only
	 *            the name of the node will be added to the result
	 * @param comparator
	 *            comparator to use sort the nodes at same level,
	 *            <code>null</code> if no special ordering is needed
	 * @return List of node names sorted in topological ordering
	 */
	public List<T> topologocialSort(boolean includeEquivalents, Comparator<? super T> comparator) {
		Map<TaxonomyNode<T>, Integer> degrees = new HashMap<TaxonomyNode<T>, Integer>();
		Map<T,TaxonomyNode<T>> nodesPending = comparator == null
			? new HashMap<T, TaxonomyNode<T>>()
			: new TreeMap<T, TaxonomyNode<T>>( comparator );
		Set<TaxonomyNode<T>> nodesLeft = new HashSet<TaxonomyNode<T>>();
		List<T> nodesSorted = new ArrayList<T>();

		log.fine( "Topological sort..." );

		for( TaxonomyNode<T> node : nodes.values() ) {
			if (node.isHidden())
				continue;
			
			nodesLeft.add( node );
			int degree = node.getSupers().size();
			if( degree == 0 ) {
				nodesPending.put( node.getName(), node );
				degrees.put( node, 0 );
			}
			else
				degrees.put( node, Integer.valueOf( degree ) );
		}

		for( int i = 0, size = nodesLeft.size(); i < size; i++ ) {
			if( nodesPending.isEmpty() )
				throw new InternalReasonerException( "Cycle detected in the taxonomy!" );

			TaxonomyNode<T> node = nodesPending.values().iterator().next();

			int deg = degrees.get( node );
			if( deg != 0 )
				throw new InternalReasonerException( "Cycle detected in the taxonomy " + node + " "
						+ deg + " " + nodesSorted.size() + " " + nodes.size() );

			nodesPending.remove( node.getName() );
			nodesLeft.remove( node );
			if( includeEquivalents )
				nodesSorted.addAll( node.getEquivalents() );
			else
				nodesSorted.add( node.getName() );

			for( TaxonomyNode<T> sub : node.getSubs() ) {
				int degree = degrees.get( sub );
				if( degree == 1 ) {
					nodesPending.put( sub.getName(), sub );
					degrees.put( sub, 0 );
				}
				else
					degrees.put( sub, degree - 1 );
			}
		}

		if( !nodesLeft.isEmpty() )
			throw new InternalReasonerException( "Failed to sort elements: " + nodesLeft );

		log.fine( "done" );

		return nodesSorted;
	}
}
