// Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package com.clarkparsia.pellet.rules.rete;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.mindswap.pellet.ABox;
import org.mindswap.pellet.DependencySet;
import org.mindswap.pellet.utils.ATermUtils;
import org.mindswap.pellet.utils.Pair;

import aterm.ATermAppl;

/**
 * <p>
 * Title: Beta Node
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
public class BetaNode extends Node {

	private boolean			doExplanation;
	private boolean 		dirty;
	public Node				lnode;

	public List<BetaNode>	parents;
	public Node				rnode;

	public RuleNode			rule;

	public BetaNode(Node lnode, Node rnode, boolean doExplanation) {
		super();
		this.doExplanation = doExplanation;
		this.lnode = lnode;
		this.rnode = rnode;
		dirty = true;

		List<ATermAppl> shared = Utils.getSharedVars( lnode, rnode );
		//Collections.sort( shared );

		lnode.svars = shared;
		rnode.svars = shared;

		vars = Utils.concat( shared, lnode.vars );
		vars = Utils.concat( vars, rnode.vars );
		vars = Utils.removeDups( vars );

		parents = new ArrayList<BetaNode>();
		
		lnode.add( this );
		if ( !rnode.equals( lnode ) )
			rnode.add( this );

	}

	public HashMap<ATermAppl, ATermAppl> getBindings(List<ATermAppl> row) {
		HashMap<ATermAppl, ATermAppl> bindings = new HashMap<ATermAppl, ATermAppl>();

		for( int i = 0; i < vars.size(); i++ ) {
			bindings.put( vars.get( i ), row.get( i ) );
		}
		return bindings;
	}

	private ATermAppl getVar(ATermAppl var, List<ATermAppl> fact) {
		if( vars.contains( var ) ) {
			int index = getKey().indexOf( var );
			
			return fact.get( index );
		}
		else {
			ABox.log.warning( "Unbound rule variable: " + var + this.vars );
			
			return null;
		}
	}

	private int[] getVarPermutation() {
		List<ATermAppl> joinVars = new ArrayList<ATermAppl>( lnode.getKey() );
		joinVars.addAll( rnode.getKey() );
		joinVars = Utils.removeDups( joinVars );

		int[] perm = new int[vars.size()];
		for( int i = 0; i < joinVars.size(); i++ ) {
			perm[i] = getKeyPosition( joinVars.get( i ) );
		}
		return perm;
	}
	
	public boolean isDirty() { return dirty; }

	public List<Fact> join() {
		dirty = false;
		
		int[] permutation = getVarPermutation();
		List<Fact> facts = new ArrayList<Fact>();
		
		for( Pair<Fact, Fact> match : lnode.index.join( rnode.index, lnode.svars.size() ) ) {
			List<ATermAppl> constants = new ArrayList<ATermAppl>( permutation.length );
			constants.addAll( match.first.getElements() );
			if( rnode.vars.size() <= match.second.getElements().size() )
				constants.addAll( match.second.getElements().subList( lnode.svars.size(), rnode.vars.size() ) );

			ATermAppl[] factParts = new ATermAppl[permutation.length];
			for( int i = 0; i < permutation.length; i++ )
				factParts[permutation[i]] = constants.get( i );

			DependencySet ds = match.first.getDependencySet();
			ds = ds.union( match.second.getDependencySet(), doExplanation );

			List<ATermAppl> orderedConstants = Arrays.asList( factParts );
			Fact fact = new Fact( ds, orderedConstants );
			facts.add( fact );
			index.add( orderedConstants, fact );
		}
			
		return facts;
	}

	public void markDirty() {
		dirty = true;
		index.clear();
		for ( BetaNode child : getBetas() ) {
			child.markDirty();
		}
	}
	
	public Set<Fact> matchingFacts(TermTuple rhs, List<Fact> facts) {
		Set<Fact> results = new HashSet<Fact>();
		for( Fact f : facts ) {

			List<ATermAppl> constants = new ArrayList<ATermAppl>( rhs.getElements().size() );
			for( ATermAppl term : rhs.getElements() ) {
				if( ATermUtils.isVar(term))
					constants.add( getVar( term, f.getElements() ) );
				else
					constants.add( term );
			}
			DependencySet ds = f.getDependencySet();
			ds = ds.union( rhs.getDependencySet(), doExplanation );
			if( rule != null && rule.explain != null )
				ds = ds.union( rule.explain, doExplanation );
			results.add( new Fact( ds, constants ) );
		}
		return results;
	}
	
	@Override
	public void reset() {
		super.reset();
		for ( BetaNode parent : parents ) {
			parent.reset();
		}
		
	}

	public String toString() {

		return "BetaNode vars: " + vars.toString();

	}
}
