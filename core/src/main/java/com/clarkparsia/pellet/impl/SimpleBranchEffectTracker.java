// Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package com.clarkparsia.pellet.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import aterm.ATermAppl;

import com.clarkparsia.pellet.BranchEffectTracker;

/**
 * <p>
 * Title: Simple Branch Effect Tracker
 * </p>
 * <p>
 * Description: Basic ArrayList<HashSet> implementation of BranchEffectTracker
 * </p>
 * <p>
 * Copyright: Copyright (c) 2008
 * </p>
 * <p>
 * Company: Clark & Parsia, LLC. <http://www.clarkparsia.com>
 * </p>
 * 
 * @author Mike Smith
 */
public class SimpleBranchEffectTracker implements BranchEffectTracker {

	private ArrayList<Set<ATermAppl>>	effects;

	public SimpleBranchEffectTracker() {
		effects = new ArrayList<Set<ATermAppl>>();
	}

	private SimpleBranchEffectTracker(SimpleBranchEffectTracker other) {
		final int n = other.effects.size();

		this.effects = new ArrayList<Set<ATermAppl>>( n );
		for( int i = 0; i < n; i++ ) {
			Set<ATermAppl> s = other.effects.get( i );
			this.effects.add( (s == null)
				? null
				: new HashSet<ATermAppl>( s ) );
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.clarkparsia.pellet.BranchEffectTracker#add(int, aterm.ATermAppl)
	 */
	public boolean add(int branch, ATermAppl a) {

		if( branch <= 0 )
			return false;

		final int diff = branch - effects.size();
		if( diff > 0 ) {
			@SuppressWarnings("unchecked")
			Set<ATermAppl> nulls[] = (Set<ATermAppl>[]) new Set[diff];
			effects.addAll( Arrays.asList( nulls ) );
		}

		Set<ATermAppl> existing = effects.get( branch - 1 );
		if( existing == null ) {
			existing = new HashSet<ATermAppl>();
			effects.set( branch - 1, existing );
		}

		return existing.add( a );
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.clarkparsia.pellet.BranchEffectTracker#copy()
	 */
	public SimpleBranchEffectTracker copy() {
		return new SimpleBranchEffectTracker( this );
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.clarkparsia.pellet.BranchEffectTracker#getAll(int)
	 */
	public Set<ATermAppl> getAll(int branch) {

		if( branch < 1 )
			throw new IllegalArgumentException();

		if( branch > effects.size() )
			return Collections.emptySet();

		Set<ATermAppl> ret = new HashSet<ATermAppl>();
		for( int i = branch - 1; i < effects.size(); i++ ) {
			Set<ATermAppl> s = effects.get( i );
			if( s != null )
				ret.addAll( s );
		}

		return ret;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.clarkparsia.pellet.BranchEffectTracker#remove(int)
	 */
	public Set<ATermAppl> remove(int branch) {

		if( branch < 1 )
			throw new IllegalArgumentException();

		if( branch > effects.size() )
			return Collections.emptySet();

		Set<ATermAppl> ret = effects.remove( branch - 1 );
		if( ret == null )
			return Collections.emptySet();

		return ret;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.clarkparsia.pellet.BranchEffectTracker#removeAll(int)
	 */
	public Set<ATermAppl> removeAll(int branch) {

		if( branch < 1 )
			throw new IllegalArgumentException();

		if( branch > effects.size() )
			return Collections.emptySet();

		Set<ATermAppl> ret = new HashSet<ATermAppl>();
		for( int i = (effects.size() - 1); i >= (branch - 1); i-- ) {
			Set<ATermAppl> s = effects.remove( i );
			if( s != null )
				ret.addAll( s );
		}

		return ret;
	}
}