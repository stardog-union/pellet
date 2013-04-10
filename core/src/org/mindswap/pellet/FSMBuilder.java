// Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package org.mindswap.pellet;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.mindswap.pellet.utils.Pair;
import org.mindswap.pellet.utils.fsm.State;
import org.mindswap.pellet.utils.fsm.Transition;
import org.mindswap.pellet.utils.fsm.TransitionGraph;

import aterm.ATermList;

/**
 * @author Evren Sirin
 */
public class FSMBuilder {
	public static Logger	log	= Logger.getLogger( FSMBuilder.class.getName() );
	
	private RBox rbox;

	public FSMBuilder(RBox rbox) {
		this.rbox = rbox;
	}
	
	public boolean build(Role s) {
		return build(s, new HashSet<Role>()) != null;
	}

	private TransitionGraph<Role> build(Role s, Set<Role> visited) {
		if( !visited.add( s ) ) {
			return null;
		}

		TransitionGraph<Role> tg = s.getFSM();
		if( tg == null ) {
			if( log.isLoggable( Level.FINE ) )
				log.fine( "Building NFA for " + s );

			tg = buildNondeterministicFSM( s, visited );
			
			if( tg == null ) {
				log.warning( "Cycle detected in the complex subproperty chain involving " + s );
				s.setForceSimple( true );
				rbox.ignoreTransitivity( s );
				return null;
			}

			assert tg.isConnected();

			if( log.isLoggable( Level.FINE ) )
				log.fine( "Determinize " + s + ": " + tg.size() + "\n" + tg );
			
			tg.determinize();

			assert tg.isConnected();
			assert tg.isDeterministic();

			if( log.isLoggable( Level.FINE ) )
				log.fine( "Minimize NFA for " + s + ": " + tg.size() + "\n" + tg );

			tg.minimize();

			if( log.isLoggable( Level.FINE ) )
				log.fine( "Minimized NFA for " + s + ": " + tg.size() + "\n" + tg );

			assert tg.isConnected();

			tg.renumber();

			if( log.isLoggable( Level.FINE ) )
				log.fine( "Renumbered " + s + ": " + tg.size() + "\n" + tg );
			
			assert tg.isConnected();

			setFSM( s, tg );

			setFSM( s.getInverse(), mirror( tg ).determinize().renumber() );
		}

		visited.remove( s );

		return tg;
	}

	private void setFSM(Role s, TransitionGraph<Role> tg) {
		if( log.isLoggable( Level.FINE ) )
			log.fine( "NFA for " + s + ":\n" + tg );

		s.setFSM( tg );
		
		Set<Role> eqRoles = s.getEquivalentProperties();
		eqRoles.remove( s );
		for( Role eqRole : eqRoles ) {
			eqRole.setFSM( tg );
		}
	}
	
	private TransitionGraph<Role> buildNondeterministicFSM(Role s, Set<Role> visited) {
		TransitionGraph<Role> tg = new TransitionGraph<Role>();

		State<Role> i = tg.newState();
		State<Role> f = tg.newState();

		tg.setInitialState( i );
		tg.addFinalState( f );

		tg.addTransition( i, s, f );
		
		if( s.isBuiltin() )
			return tg;

		for( Role sub : s.getProperSubRoles() ) {
			if( !sub.isBottom() && !sub.getInverse().isBottom() )
				tg.addTransition( i, sub, f );
		}

		for( ATermList subChain : s.getSubRoleChains() ) {
			if( !addRoleChainTransition( tg, s, subChain ) )
				return null;
		}
		
		Set<Role> alphabet = new HashSet<Role>( tg.getAlpahabet() );
		for( Role r : alphabet ) {
			for( Pair<State<Role>, State<Role>> pair : tg.findTransitions( r ) ) {
				if( s.isEquivalent( r ) ) {
					if( tg.isInitial( pair.first ) || tg.isFinal( pair.second )
							|| (tg.isFinal( pair.first ) && tg.isInitial( pair.second )) )
						continue;
					else
						return null;
				}

				TransitionGraph<Role> newGraph = build( r, visited );

				if( newGraph == null )
					return null;

				tg.insert( newGraph, pair.first, pair.second );
			}
		}

		return tg;
	}
	
	private boolean addRoleChainTransition(TransitionGraph<Role> tg, Role s, ATermList chain) {
		Role firstRole = rbox.getRole( chain.getFirst() );
		Role lastRole = rbox.getRole( chain.getLast() );
		boolean firstRoleSame = s.isEquivalent( firstRole );
		boolean lastRoleSame = s.isEquivalent( lastRole ); 
		int length = chain.getLength();
		
		if( firstRoleSame ) {
			if( lastRoleSame && length != 2 )
				return false;				

			addRoleChainTransition( tg, tg.getFinalState(), tg.getFinalState(), chain.getNext(), length - 1 );
		}
		else if( lastRoleSame ) {
			addRoleChainTransition( tg, tg.getInitialState(), tg.getInitialState(), chain, length - 1 );			
		}
		else {
			addRoleChainTransition( tg, tg.getInitialState(), tg.getFinalState(), chain, length );
		}		
		
		return true;
	}
	
	private void addRoleChainTransition(TransitionGraph<Role> tg, State<Role> initialState, State<Role> finalState, ATermList chain, int length) {	
		State<Role> prev = initialState; 
		for( int i = 0; i < length; i++, chain = chain.getNext() ) {
			Role role = rbox.getRole( chain.getFirst() );
			State<Role> next = i == length - 1 ? finalState : tg.newState();
			tg.addTransition( prev, role, next );
			prev = next;
		}				
	}

	private TransitionGraph<Role> mirror(TransitionGraph<Role> tg) {
		Map<State<Role>, State<Role>> newStates = new HashMap<State<Role>, State<Role>>();

		TransitionGraph<Role> mirror = new TransitionGraph<Role>();

		State<Role> oldInitialState = tg.getInitialState();
		State<Role> newFinalState = copyState( oldInitialState, mirror, newStates );

		mirror.addFinalState( newFinalState );

		Set<State<Role>> oldFinalStates = tg.getFinalStates();
		State<Role> newInitialState = null;
		if( oldFinalStates.size() == 1 ) {
			State<Role> oldFinalState = oldFinalStates.iterator().next();
			newInitialState = newStates.get( oldFinalState );
		}
		else {
			newInitialState = mirror.newState();
			for( State<Role> oldFinalState : oldFinalStates ) {
				mirror.addTransition( newInitialState, newStates.get( oldFinalState ) );
			}
		}

		mirror.setInitialState( newInitialState );

		return mirror;
	}

	private State<Role> copyState(State<Role> oldState, TransitionGraph<Role> newTG, Map<State<Role>, State<Role>> newStates) {
		State<Role> newState = newStates.get( oldState );
		if( newState == null ) {
			newState = newTG.newState();
			newStates.put( oldState, newState );
			for( Transition<Role> t : oldState.getTransitions() ) {
				State<Role> oldTo = t.getTo();
				State<Role> newFrom = copyState( oldTo, newTG, newStates );
				if( t.isEpsilon() )
					newTG.addTransition( newFrom, newState );
				else
					newTG.addTransition( newFrom, t.getName().getInverse(), newState );
			}
		}

		return newState;
	}
}
