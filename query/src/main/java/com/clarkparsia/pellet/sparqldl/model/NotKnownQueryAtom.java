// Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public
// License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of
// proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package com.clarkparsia.pellet.sparqldl.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import aterm.ATermAppl;

/**
 * <p>
 * Title:
 * </p>
 * <p>
 * Description:
 * </p>
 * <p>
 * Copyright: Copyright (c) 2009
 * </p>
 * <p>
 * Company: Clark & Parsia, LLC. <http://www.clarkparsia.com>
 * </p>
 * 
 * @author Evren Sirin
 */
public class NotKnownQueryAtom implements QueryAtom {
	private List<QueryAtom>	atoms;
	private boolean isGround;
	private List<ATermAppl> args;

	public NotKnownQueryAtom(QueryAtom atom) {
		this( Collections.singletonList( atom ) );
	}

	public NotKnownQueryAtom(List<QueryAtom> atoms) {
		this.atoms = Collections.unmodifiableList( atoms );
		
		isGround = true;
		args = new ArrayList<ATermAppl>();
		for( QueryAtom atom : atoms ) {
			args.addAll( atom.getArguments() );
			if ( isGround && !atom.isGround() )
				isGround = false;
		}
	}

	public QueryAtom apply(final ResultBinding binding) {
		List<QueryAtom>	newAtoms;
		if( atoms.size() == 1 ) {
			 newAtoms = Collections.singletonList( atoms.get( 0 ).apply( binding ) );
		}
		else {
			newAtoms = new ArrayList<QueryAtom>();
			for( QueryAtom atom : atoms ) {
				newAtoms.add( atom.apply( binding ) );
			}
		}
		
		return new NotKnownQueryAtom( newAtoms );		
	}

	@Override
	public boolean equals(Object obj) {
		if( !(obj instanceof NotKnownQueryAtom) )
			return false;
		
		return atoms.equals( ((NotKnownQueryAtom) obj).atoms );
	}

	/**
	 * {@inheritDoc}
	 */
	public List<ATermAppl> getArguments() {
		return args;
	}

	public List<QueryAtom> getAtoms() {
		return atoms;
	}

	/**
	 * {@inheritDoc}
	 */
	public QueryPredicate getPredicate() {
		return QueryPredicate.NotKnown;
	}

	@Override
	public int hashCode() {
		return 17 * atoms.hashCode();
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean isGround() {
		return isGround;
	}

	@Override
	public String toString() {
		return "NotKnown" + atoms;
	}
}
