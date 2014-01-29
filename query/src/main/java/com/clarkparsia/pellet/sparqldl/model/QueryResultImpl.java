// Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package com.clarkparsia.pellet.sparqldl.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Map.Entry;

import aterm.ATermAppl;

/**
 * <p>
 * Title: Default implementation of {@link QueryResult}
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
public class QueryResultImpl implements QueryResult {

	private Collection<ResultBinding>	bindings;

	private List<ATermAppl>				resultVars;

	private Query						query;
	private QueryParameters				parameters;

	public QueryResultImpl(final Query query) {
		this.query = query;
		this.parameters = query.getQueryParameters();
		this.resultVars = new ArrayList<ATermAppl>( query.getResultVars() );

		if( query.isDistinct() )
			bindings = new HashSet<ResultBinding>();
		else
			bindings = new ArrayList<ResultBinding>();
	}

	/**
	 * {@inheritDoc}
	 */
	public void add(ResultBinding binding) {
		bindings.add( process( binding ) );
	}

	@Override
	public boolean equals(Object obj) {
		if( this == obj )
			return true;
		if( obj == null )
			return false;
		if( getClass() != obj.getClass() )
			return false;
		final QueryResultImpl other = (QueryResultImpl) obj;
		if( bindings == null ) {
			if( other.bindings != null )
				return false;
		}
		else if( !bindings.equals( other.bindings ) )
			return false;
		if( resultVars == null ) {
			if( other.resultVars != null )
				return false;
		}
		else if( !resultVars.equals( other.resultVars ) )
			return false;
		return true;
	}

	/**
	 * {@inheritDoc}
	 */
	public List<ATermAppl> getResultVars() {
		return Collections.unmodifiableList( resultVars );
	}

	@Override
	public int hashCode() {
		final int PRIME = 31;
		int result = 1;
		result = PRIME * result + ((bindings == null)
			? 0
			: bindings.hashCode());
		result = PRIME * result + ((resultVars == null)
			? 0
			: resultVars.hashCode());
		return result;
	}

	public boolean isDistinct() {
		return (bindings instanceof Set);
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean isEmpty() {
		return size() == 0;
	}

	/**
	 * {@inheritDoc}
	 */
	public Iterator<ResultBinding> iterator() {
		return bindings.iterator();
	}

	/**
	 * {@inheritDoc}
	 */
	public int size() {
		return bindings.size();
	}

	@Override
	public String toString() {
		return bindings.toString();
	}

	private ResultBinding process(ResultBinding binding) {
		if( parameters == null )
			return binding;
		
		int numOfVars = query.getResultVars().size();
		
		// Add the query parameters to the binding if the variable is in the
		// query projection
		for( Iterator<Entry<ATermAppl, ATermAppl>> iter = parameters.entrySet().iterator(); iter.hasNext(); ) {
			Entry<ATermAppl, ATermAppl> entry = iter.next();
			ATermAppl var = entry.getKey();
			ATermAppl value = entry.getValue();
			
			if( numOfVars == 0 || query.getResultVars().contains( var ) )
				binding.setValue( var, value );
		}
		
		return binding;
	}
}
