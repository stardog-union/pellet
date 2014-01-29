// Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package com.clarkparsia.pellet.rules.rete;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.mindswap.pellet.utils.Pair;

/**
 * <p>
 * Title: Index
 * </p>
 * <p>
 * Description: An indexing structure that associates an
 * object with a list of objects as the key.
 * </p>
 * <p>
 * Copyright: Copyright (c) 2007
 * </p>
 * <p>
 * Company: Clark & Parsia, LLC. <http://www.clarkparsia.com>
 * </p>
 * 
 * @author Ron Alford
 */
public class Index<S, T> implements Iterable<T> {

	private static class IndexNode<I, J> {
		private Map<I, IndexNode<I, J>>	children;
		private Collection<J>			leaves;

		public IndexNode() {
			children = new HashMap<I, IndexNode<I, J>>();
			leaves = new HashSet<J>();
		}

		public boolean add(List<I> key, J obj) {
			if( key.size() == 0 ) {
				return leaves.add( obj );
			}
			else {
				I pivot = key.get( 0 );
				IndexNode<I, J> child = children.get( pivot );
				if( child == null ) {
					child = new IndexNode<I, J>();
					children.put( pivot, child );
				}
				return child.add( key.subList( 1, key.size() ), obj );
			}
		}

		private Collection<J> getAllLeaves() {
			Collection<J> results = new ArrayList<J>();
			getAllLeaves( results );
			return results;
		}

		private void getAllLeaves(Collection<J> result) {
			result.addAll( leaves );
			for( IndexNode<I, J> child : children.values() ) {
				child.getAllLeaves( result );
			}
		}
		
		public <V> void join(IndexNode<I, V> node, int shared, Collection<Pair<J, V>> results) {
			if( shared > 0 ) {
				for( Map.Entry<I, IndexNode<I, J>> entry : children.entrySet() ) {
					if( entry.getKey() != null ) {
						IndexNode<I, V> nodeChild = node.children.get( entry.getKey() );
						if( nodeChild != null ) {
							entry.getValue().join( nodeChild, shared - 1, results );
						}

						IndexNode<I, V> nullNodeChild = node.children.get( null );
						if( nullNodeChild != null ) {
							entry.getValue().join( nullNodeChild, shared - 1, results );
						}
					}
					else {
						for( IndexNode<I, V> child : node.children.values() ) {
							entry.getValue().join( child, shared - 1, results );
						}
					}
				}
			}
			else {
				for( J leaf : getAllLeaves() ) {
					for( V joinLeaf : node.getAllLeaves() ) {
						results.add( new Pair<J, V>( leaf, joinLeaf ) );
					}
				}
			}
		}

		public void match(List<I> key, Collection<J> results) {
			if( key.size() == 0 ) {
				results.addAll( leaves );
			}
			else {
				List<I> subKey = key.subList( 1, key.size() );

				IndexNode<I, J> pivotChild = children.get( key.get( 0 ) );
				if( pivotChild != null )
					pivotChild.match( subKey, results );

				IndexNode<I, J> nullChild = children.get( null );
				if( nullChild != null )
					nullChild.match( subKey, results );
			}
		}

		public void print(StringBuilder buffer, String prefix) {
			if( leaves.size() > 0 ) {
				buffer.append( leaves.toString() );
			}
			buffer.append( ":\n" );
			prefix = prefix + " ";
			for( Map.Entry<I, IndexNode<I, J>> entry : children.entrySet() ) {
				buffer.append( prefix ).append( entry.getKey() ).append( " " );
				entry.getValue().print( buffer, prefix );
			}
		}
		
		public boolean remove( List<I> key, J obj ) {
			if( key.size() == 0 ) {
				return leaves.remove( obj );
			}
			else {
				I pivot = key.get( 0 );
				IndexNode<I, J> child = children.get( pivot );
				if( child == null ) {
					return false;
				}
				boolean result = child.remove( key.subList( 1, key.size() ), obj );
				if ( result && child.leaves.isEmpty() )
					children.remove( child );
				return result;
			}
		}

		public String toString() {
			StringBuilder result = new StringBuilder( "Index Node " );
			print( result, "" );
			return result.toString();
		}

	}
	
	private int size;
	private IndexNode<S, T>	root;

	public Index() {
		clear();
	}

	/**
	 * Add an object to the index.
	 * @param key null key positions are counted as wild-cards.  
	 * @param obj
	 * @return
	 */
	public boolean add(List<S> key, T obj) {
		if ( root.add( key, obj ) ) {
			size++;
			return true;
		}
		return false;
	}
	
	/**
	 * Remove all nodes from the index.
	 */
	public void clear() {
		root = new IndexNode<S, T>();
		size = 0;
	}


	public Iterator<T> iterator() {
		return root.getAllLeaves().iterator();
	}
	
	/**
	 * Return a join of this index to the given index, joining on the first
	 * <code>shared</code> variables.
	 */
	public <U> Collection<Pair<T, U>> join(Index<S, U> index, int shared) {
		Collection<Pair<T, U>> results = new ArrayList<Pair<T, U>>();
		root.join( index.root, shared, results );
		return results;
	}

	/**
	 * Return all matches to the key.  There may be no null values in the key.
	 * The returned objects will be stored under keys whose elements
	 * are either equal to the corresponding element of the given key or are null.
	 */
	public Collection<T> match(List<S> key) {
		Collection<T> results = new ArrayList<T>();
		root.match( key, results );
		return results;
	}
	
	/**
	 * Remove the element of the index stored under the key 'key'.
	 * Return true if the element exists and was removed.
	 * Otherwise, remove false.
	 */
	public boolean remove( List<S> key, T obj ) {
		if ( root.remove( key, obj ) ) {
			size--;
			return true;
		}
		return false;
	}
	
	/**
	 * Return the number of objects added to the index.
	 */
	public int size() {
		return size;
	}

	public String toString() {
		StringBuilder buffer = new StringBuilder( "Index " );
		root.print( buffer, "" );

		return buffer.toString();
	}

}
