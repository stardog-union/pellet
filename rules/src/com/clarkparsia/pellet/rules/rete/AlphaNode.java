// Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package com.clarkparsia.pellet.rules.rete;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.mindswap.pellet.utils.ATermUtils;

import aterm.ATermAppl;

/**
 * <p>
 * Title: Alpha Node
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
 */
public class AlphaNode extends Node {

	protected TermTuple			pattern;

	public AlphaNode(TermTuple t) {
		this.pattern = t;
		this.vars = pattern.getVars();
		
		//Collections.sort(vars);
	}
	
	public boolean add(Fact fact) {
		List<ATermAppl> key = match( fact );

		if( key != null ) {
			markDirty();
			return index.add( key, new Fact( fact.getDependencySet(), key ) );
		}
		return false;
	}

	/**
	 * Mark any dependent beta nodes as 'dirty' (needing to be reprocessed)
	 */
	public void markDirty() {
		for ( BetaNode beta : getBetas() ) {
			beta.markDirty();
		}
	}
	
	/**
	 * Determine whether the fact matches the node's pattern
	 * 
	 * @return a list of constants in key order.
	 */
	private List<ATermAppl> match(Fact fact) {

		if( fact.getElements().size() != pattern.getElements().size() )
			return null;

		Map<ATermAppl, ATermAppl> bindings = new HashMap<ATermAppl, ATermAppl>();
		List<ATermAppl> pList = this.pattern.getElements();
		List<ATermAppl> fList = fact.getElements();
		ATermAppl p = null;
		ATermAppl f = null;
		for( int i = 0; i < pattern.getElements().size(); i++ ) {
			p = pList.get( i );
			f = fList.get( i );

			if( !ATermUtils.isVar(p) ) {
				if( !p.equals( f ) )
					return null;
			}
			else if( !bindings.containsKey( p ) )
				bindings.put( p, f );
			else if( !bindings.get( p ).equals( f ) )
				return null;
		}

		List<ATermAppl> bindingList = new ArrayList<ATermAppl>();
		for( ATermAppl var : getKey() ) {
			bindingList.add( bindings.get( var ) );
		}
		return bindingList;
	}
	
	public boolean remove( Fact fact ) {
		List<ATermAppl> key = match( fact );

		if( key != null ) {
			markDirty();
			return index.remove( key, new Fact( fact.getDependencySet(), key ) );
		}
		return false;
	}
	
	public void reset() {
		super.reset();
		for ( BetaNode beta : getBetas() ) {
			beta.reset();
		}
	}

	public String toString() {
		return "AlphaNode(" + pattern.toString() + ")";
	}
}
