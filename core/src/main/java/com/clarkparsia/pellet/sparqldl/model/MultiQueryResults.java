// Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package com.clarkparsia.pellet.sparqldl.model;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.NoSuchElementException;

import aterm.ATermAppl;

/**
 * <p>
 * Title: Result combining more disjoint query results to prevent generating
 * cross-products.
 * </p>
 * <p>
 * Description:
 * </p>
 * <p>
 * Copyright: Copyright (c) 2007
 * </p>
 * <p>
 * Company: Clark & Parsia, LLC. <http://www.clarkparsia.com>
 * </p>
 * 
 * @author Petr Kremen
 */
public class MultiQueryResults implements QueryResult {

	private final List<ATermAppl>	resultVars;

	private final List<QueryResult>	queryResults;

	private int						size;

	public MultiQueryResults(final List<ATermAppl> resultVars, final List<QueryResult> queryResults) {
		this.resultVars = resultVars;
		this.queryResults = queryResults;

		size = 1;
		for( final QueryResult result : queryResults ) {
			size *= result.size();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public void add(ResultBinding binding) {
		throw new UnsupportedOperationException( "MultiQueryResults do not support addition!" );
	}

	/**
	 * {@inheritDoc}
	 */
	public List<ATermAppl> getResultVars() {
		return resultVars;
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean isDistinct() {
		for( final QueryResult result : queryResults ) {
			if( !result.isDistinct() )
				return false;
		}

		return true;
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean isEmpty() {
		return size == 0;
	}

	/**
	 * {@inheritDoc}
	 */
	public Iterator<ResultBinding> iterator() {
		return new Iterator<ResultBinding>() {	
			private List<Iterator<ResultBinding>> iterators = new ArrayList<Iterator<ResultBinding>>();

			private List<ResultBinding> bindings = new ArrayList<ResultBinding>();
			
			private boolean hasNext = init();

			private boolean  init() {
				for( QueryResult result : queryResults ) {
					Iterator<ResultBinding> iterator = result.iterator();
					
					if( !iterator.hasNext() )
						return false;
					
					iterators.add( iterator );
					bindings.add( iterator.next() );
				}
				
				return true;
			}
			
			private void findNext() {
				ListIterator<Iterator<ResultBinding>> i = iterators.listIterator();
				
				for( int index = 0; index < iterators.size(); index++ ) {
					Iterator<ResultBinding> iterator = i.next();
					if( iterator.hasNext() ) {
						bindings.set( index, iterator.next() );
						return;
					}
					else if( index == iterators.size() - 1 ) {
						hasNext = false;
						return;
					}
					else {
						iterator = queryResults.get( index ).iterator();
						i.set( iterator );
						bindings.set( index, iterator.next() );
					}
				}
			}
			
			public boolean hasNext() {
				return hasNext;
			}

			public ResultBinding next() {
				if( !hasNext() )
					throw new NoSuchElementException();
				
				ResultBinding result = new ResultBindingImpl();
				for( ResultBinding binding : bindings ) {
					result.setValues( binding );
				}
				
				findNext();
				
				return result;
			}

			public void remove() {
				throw new UnsupportedOperationException();
			}			
		};
	}

	/**
	 * {@inheritDoc}
	 */
	public int size() {
		return size;
	}
}
