// Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public
// License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of
// proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package com.clarkparsia.pellet.sparqldl.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.mindswap.pellet.utils.ATermUtils;

import aterm.ATermAppl;

/**
 * <p>
 * Title: Implementation of the Core of undistinguished variables.
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
public class CoreImpl extends QueryAtomImpl implements Core {

	private List<ATermAppl>			distVars	= null;
	private List<ATermAppl>			consts		= null;

	private Collection<ATermAppl>	undistVars	= null;

	private Collection<QueryAtom>	atoms;

	public CoreImpl(final List<ATermAppl> arguments, final Collection<ATermAppl> uv,
			final Collection<QueryAtom> atoms) {
		super( QueryPredicate.UndistVarCore, arguments );

		this.atoms = atoms;
		this.undistVars = uv;
	}

	private void setup() {
		distVars = new ArrayList<ATermAppl>();
		consts = new ArrayList<ATermAppl>();
		for( final ATermAppl a : arguments ) {
			if( ATermUtils.isVar( a ) ) {
				distVars.add( a );
			}
			else {
				consts.add( a );
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * @see org.mindswap.pellet.sparqldl.model.CoreIF#getConstants()
	 */
	public Collection<ATermAppl> getConstants() {
		if( consts == null ) {
			setup();
		}

		return consts;
	}

	/*
	 * (non-Javadoc)
	 * @see org.mindswap.pellet.sparqldl.model.CoreIF#getDistVars()
	 */
	public Collection<ATermAppl> getDistVars() {
		if( distVars == null ) {
			setup();
		}

		return distVars;
	}

	/*
	 * (non-Javadoc)
	 * @see org.mindswap.pellet.sparqldl.model.CoreIF#getUndistVars()
	 */
	public Collection<ATermAppl> getUndistVars() {
		if( undistVars == null ) {
			setup();
		}

		return undistVars;
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * org.mindswap.pellet.sparqldl.model.CoreIF#apply(org.mindswap.pellet.sparqldl
	 * .model.ResultBinding)
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

		final List<QueryAtom> newAtoms = new ArrayList<QueryAtom>();

		for( final QueryAtom a : atoms ) {
			newAtoms.add( a.apply( binding ) );
		}

		return new CoreImpl( newArguments, undistVars, newAtoms );
	}

	@Override
	public int hashCode() {
		return arguments.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if( this == obj )
			return true;
		if( obj == null )
			return false;
		if( getClass() != obj.getClass() )
			return false;
		final CoreImpl other = (CoreImpl) obj;

		return arguments.equals( other.arguments );
	}
}
