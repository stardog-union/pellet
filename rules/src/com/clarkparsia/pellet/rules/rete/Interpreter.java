// Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package com.clarkparsia.pellet.rules.rete;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.mindswap.pellet.ABox;
import org.mindswap.pellet.DependencySet;
import org.mindswap.pellet.exceptions.InternalReasonerException;
import org.mindswap.pellet.utils.Pair;
import org.mindswap.pellet.utils.SetUtils;

import aterm.ATermAppl;

/**
 * <p>
 * Title: Interpreter
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
public class Interpreter {

	private Set<AlphaNode>								dirtyAlphas;
	private List<Pair<Integer, Collection<AlphaNode>>>	dirtyHistory;
	private Set<Fact>									inferredFacts;
	private Set<Fact>									matchingFacts;
	public Compiler										rete;

	public Interpreter(ABox abox) {
		super();

		rete = new Compiler( this, abox );
		dirtyAlphas = new HashSet<AlphaNode>();
		dirtyHistory = new ArrayList<Pair<Integer, Collection<AlphaNode>>>();
		inferredFacts = new HashSet<Fact>();
		matchingFacts = new HashSet<Fact>();
	}

	public boolean addFact(Fact f) {
		return addFact( f, true );
	}

	private boolean addFact(Fact f, boolean initial) {
		if( knowFact( f ) )
			return false;

		if( !initial ) {
			// Need to change the branch number so we know when it was inferred
			f = new Fact( copyDS( f.getDependencySet() ), f.getElements() );
			inferredFacts.add( f );
		}

		boolean matched = false;
		for( AlphaNode a : rete.alphaIndex.match( f ) ) {
			a.add( f );
			matched = true;
			dirtyAlphas.add( a );
		}

		if( matched )
			matchingFacts.add( f );

		return matched;
	}

	/**
	 * @param ds
	 * @return
	 */
	private DependencySet copyDS(DependencySet ds) {
		ds = ds.copy( rete.abox.getBranch() );
		return ds;
	}

	public boolean isDirty() {
		return !dirtyAlphas.isEmpty();
	}

	private boolean knowFact(Fact f) {
		return matchingFacts.contains( f )
				|| (inferredFacts != null && inferredFacts.contains( f ));
	}

	private void processBetaNodes( List<BetaNode> dirtyBetas ) {
		while ( !dirtyBetas.isEmpty() ) {
			BetaNode betaNode = dirtyBetas.remove( 0 );
			
			if ( !betaNode.isDirty() )
				continue;
			
			List<Fact> inferences = betaNode.join();

			if( inferences != null && inferences.size() > 0 ) {
				if( betaNode.rule != null ) {
					List<TermTuple> termTuples = betaNode.rule.rhs;
					
					// Special case for empty head to
					if( termTuples.isEmpty() )
						termTuples = Collections.singletonList( new TermTuple(
								DependencySet.INDEPENDENT ) );
					
					for( TermTuple triple : termTuples ) {
						Set<Fact> results = betaNode.matchingFacts( triple, inferences );
						for( Fact result : results )
							addFact( result, false );
					}
				}
				else {
					for ( BetaNode child : betaNode.getBetas() ) {
						child.markDirty();
						dirtyBetas.add( child );
					}
				}
			}
		}
	}

	/**
	 * Remove all facts that have a term matching 't'
	 * 
	 * @return Return true if any fact was removed.
	 */
	public boolean removeMentions(ATermAppl t) {
		boolean changed = false;
		for( Iterator<Fact> iter = matchingFacts.iterator(); iter.hasNext(); ) {
			Fact fact = iter.next();
			for( ATermAppl element : fact.getElements() ) {
				if( element.equals( t ) ) {
					iter.remove();
					changed = true;
					for( AlphaNode a : rete.alphaIndex.match( fact ) ) {
						if( !a.remove( fact ) )
							throw new InternalReasonerException( "Could not remove fact" );

						dirtyAlphas.add( a );
					}
					break;
				}
			}
		}
		return changed;
	}

	/**
	 * Remove all facts from the interpreter, leaving the rules intact.
	 */
	public void reset() {
		rete.alphaIndex.reset();
		dirtyAlphas.clear();
		dirtyHistory.clear();
		inferredFacts.clear();
		matchingFacts.clear();
	}

	/**
	 * Restore abox to the given branch
	 * 
	 * @return true if a matching fact was removed. False otherwise.
	 */
	public boolean restore(int branch) {

		boolean changed = false;

		for( Iterator<Pair<Integer, Collection<AlphaNode>>> setIter = dirtyHistory.iterator(); setIter
				.hasNext(); ) {
			Pair<Integer, Collection<AlphaNode>> pair = setIter.next();
			if( pair.first > branch ) {
				for( AlphaNode a : pair.second ) {
					a.markDirty();
					dirtyAlphas.add( a );
					changed = true;
				}
				setIter.remove();
			}
			else
				break;
		}

		for( Iterator<Fact> factIter = matchingFacts.iterator(); factIter.hasNext(); ) {
			Fact fact = factIter.next();

			if( fact.getDependencySet().getBranch() > branch ) {
				factIter.remove();
				for( AlphaNode a : rete.alphaIndex.match( fact ) ) {
					if( !a.remove( fact ) )
						throw new InternalReasonerException( "Couldn't remove fact: " + fact );

					dirtyAlphas.add( a );
					changed = true;
				}
			}
		}

		for( Iterator<Fact> factIter = inferredFacts.iterator(); factIter.hasNext(); ) {
			Fact fact = factIter.next();

			if( fact.getDependencySet().getBranch() > branch )
				factIter.remove();
		}

		return changed;
	}

	public Set<Fact> run() {
		// Inserts empty fact to fire rules without bodies
		Collection<AlphaNode> savedDirties = new ArrayList<AlphaNode>( dirtyAlphas );
		dirtyHistory.add( 0, new Pair<Integer, Collection<AlphaNode>>( rete.abox.getBranch(),
				savedDirties ) );

		Collection<Fact> oldInferred = new ArrayList<Fact>( inferredFacts );
		while( isDirty() ) {
			List<BetaNode> dirtyBetas = new ArrayList<BetaNode>();

			for( AlphaNode alphaNode : dirtyAlphas )
				dirtyBetas.addAll( alphaNode.getBetas() );

			dirtyAlphas.clear();
			processBetaNodes( dirtyBetas );
		}

		return SetUtils.difference( inferredFacts, oldInferred );
	}
}
