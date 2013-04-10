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
public class UnionQueryAtom implements QueryAtom {
	private List<List<QueryAtom>>	union;
	private boolean					isGround;
	private List<ATermAppl>			args;

	@SuppressWarnings("unchecked")
	public UnionQueryAtom(List<QueryAtom> atoms1, List<QueryAtom> atoms2) {
		this( Arrays.asList( atoms1, atoms2 ) );
	}

	public UnionQueryAtom(List<List<QueryAtom>> union) {
		if( union.isEmpty() )
			throw new IllegalArgumentException(
					"Empty collection of atoms not allowed in NotKnown atom" );

		this.union = Collections.unmodifiableList( union );

		isGround = true;
		args = new ArrayList<ATermAppl>();
		for( List<QueryAtom> atoms : union ) {
			for( QueryAtom atom : atoms ) {
				args.addAll( atom.getArguments() );
				if ( isGround && !atom.isGround() )
					isGround = false;
			}
		}
	}

	public QueryAtom apply(final ResultBinding binding) {
		List<List<QueryAtom>> newUnion = new ArrayList<List<QueryAtom>>();
		for( List<QueryAtom> atoms : union ) {
			List<QueryAtom>newAtoms = new ArrayList<QueryAtom>();
			for( QueryAtom atom : atoms ) {
				newAtoms.add( atom.apply( binding ) );
			}
			newUnion.add( newAtoms );
		}

		return new UnionQueryAtom( newUnion );
	}

	@Override
	public boolean equals(Object obj) {
		if( !(obj instanceof UnionQueryAtom) )
			return false;

		return union.equals( ((UnionQueryAtom) obj).union );
	}

	/**
	 * {@inheritDoc}
	 */
	public List<ATermAppl> getArguments() {
		return args;
	}

	public List<List<QueryAtom>> getUnion() {
		return union;
	}

	/**
	 * {@inheritDoc}
	 */
	public QueryPredicate getPredicate() {
		return QueryPredicate.Union;
	}

	@Override
	public int hashCode() {
		return 31 * union.hashCode();
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean isGround() {
		return isGround;
	}

	@Override
	public String toString() {
		return "Union" + union;
	}
}
