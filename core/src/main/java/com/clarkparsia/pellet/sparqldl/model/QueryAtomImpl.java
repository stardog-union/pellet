// Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public
// License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of
// proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package com.clarkparsia.pellet.sparqldl.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.mindswap.pellet.utils.ATermUtils;

import aterm.ATermAppl;

/**
 * <p>
 * Title: Abstract implementation of the query atom.
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
public class QueryAtomImpl implements QueryAtom {

	protected final QueryPredicate	predicate;

	protected final List<ATermAppl>	arguments;

	protected boolean				ground;

	public QueryAtomImpl(final QueryPredicate predicate, final ATermAppl... arguments) {
		this( predicate, Arrays.asList( arguments ) );
	}

	public QueryAtomImpl(final QueryPredicate predicate, final List<ATermAppl> arguments) {
		if( predicate == null ) {
			throw new RuntimeException( "Predicate cannot be null." );
		}

		this.predicate = predicate;
		this.arguments = arguments;
		// this.vars = new HashSet<ATermAppl>();
		//
		ground = true;
		for( final ATermAppl a : arguments ) {
			if( ATermUtils.isVar( a ) ) {
				ground = false;
				// vars.add(a);
				break;
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public QueryPredicate getPredicate() {
		return predicate;
	}

	/**
	 * {@inheritDoc}
	 */
	public List<ATermAppl> getArguments() {
		return arguments;
	}

	// /**
	// * {@inheritDoc}
	// */
	// public Set<ATermAppl> getVariables() {
	// return vars;
	// }
	//
	/**
	 * {@inheritDoc}
	 */
	public boolean isGround() {
		return ground;
	}

	/**
	 * {@inheritDoc}
	 */
	public QueryAtom apply(final ResultBinding binding) {
		if( isGround() ) {
			return this;
		}

		final List<ATermAppl> newArguments = new ArrayList<ATermAppl>();

		for( final ATermAppl a : arguments ) {
			if( binding.isBound( a ) ) {
				newArguments.add( binding.getValue( a ) );
			}
			else {
				newArguments.add( a );
			}
		}

		return newArguments.isEmpty()
			? this
			: new QueryAtomImpl( predicate, newArguments );
	}

	@Override
	public int hashCode() {
		return 31 * predicate.hashCode() + arguments.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if( this == obj )
			return true;
		if( obj == null )
			return false;
		if( getClass() != obj.getClass() )
			return false;
		final QueryAtomImpl other = (QueryAtomImpl) obj;

		return predicate.equals( other.predicate ) && arguments.equals( other.arguments );
	}

	@Override
	public String toString() {
		final StringBuffer sb = new StringBuffer();

		for( int i = 0; i < arguments.size(); i++ ) {
			final ATermAppl a = arguments.get( i );
			if( i > 0 ) {
				sb.append( ", " );
			}

			sb.append( ATermUtils.toString( a ) );
		}

		return predicate + "(" + sb.toString() + ")";
	}
}
