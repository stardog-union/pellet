// Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package org.mindswap.pellet.utils.fsm;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import org.mindswap.pellet.exceptions.InternalReasonerException;
import org.mindswap.pellet.utils.Pair;

/**
 * <p>Title: </p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2006</p>
 *
 * <p>Company: Clark & Parsia, LLC. <http://www.clarkparsia.com></p>
 *
 * @author Evren Sirin
 */
public class TransitionGraph<T> {
    private State<T> initialState; // the initial state for the TG

    private Set<State<T>> allStates; // set of all states in the TG

    private Set<State<T>> finalStates; // set of final states for the TG

    private Set<T> alphabet; // set of all characters in TG

    public TransitionGraph() {
        initialState = null;
        allStates = new HashSet<State<T>>();
        finalStates = new HashSet<State<T>>();
        alphabet = new HashSet<T>();
    }
    
    /**
     * Returns the number of states in this transition graph
     * 
     * @return
     */
    public int size() {
    	return allStates.size();
    }
    
    // ---------------------------------------------------
    // adds a new state to the graph

    public State<T> newState() {
        State<T> s = new State<T>();
        allStates.add( s );
        return s;
    }    
    
    public Set<T> getAlpahabet() {
    	return Collections.unmodifiableSet( alphabet );
    }

    public Set<State<T>> getAllStates() {
        return Collections.unmodifiableSet( allStates );
    }
    
    public void setInitialState( State<T> s ) {
        initialState = s;
    }

    public State<T> getInitialState() {
        return initialState;
    }
    
    public void addFinalState( State<T> s ) {
        finalStates.add( s );
    }

    public Set<State<T>> getFinalStates() {
        return finalStates;
    }
    
    public State<T> getFinalState() {
        int size =  finalStates.size();
        
        if( size == 0 )
            throw new RuntimeException( "There are no final states!" );
        else if ( size > 1 )
            throw new RuntimeException( "There is more than one final state!" );
        
        return finalStates.iterator().next();
    }
    
    public void addTransition( State<T> begin, T transition, State<T> end ) {
    	if( transition == null )
        	throw new NullPointerException();
    	
        begin.addTransition( transition, end );
        alphabet.add( transition ); 
    }
    
    public void addTransition( State<T> begin, State<T> end ) {
        begin.addTransition( end );
    }
    
    public List<Pair<State<T>,State<T>>> findTransitions( T transition ) {
        List<Pair<State<T>,State<T>>> result = new ArrayList<Pair<State<T>,State<T>>>();
        
        for( State<T> s1 : allStates ) {
            State<T> s2 = s1.move( transition ); 
            
            if( s2 != null )
                result.add( new Pair<State<T>,State<T>>( s1, s2 ) );
        }
        
        return result;
    }
    
    public boolean isInitial( State<T> st ) {
        return initialState.equals( st );
    }
    
    public boolean isFinal( State<T> st ) {
        return finalStates.contains( st );
    }
    
    // ---------------------------------------------------
    // test whether Set<State<T>> is DFA final state (contains NFA final state)

    public boolean isAnyFinal( Set<State<T>> ss ) {
        for( State<T> st : ss ) {
            if( finalStates.contains( st ) )
                return true;
        }
        return false;
    }

    // ---------------------------------------------------
    // Return a TG that accepts just epsilon

    public TransitionGraph<T> epsilon() {
        TransitionGraph<T> tg = new TransitionGraph<T>();
        State<T> s = tg.newState();
        State<T> f = tg.newState();
        s.addTransition( f );
        tg.initialState = s;
        tg.finalStates.add( f );
        return tg;
    }

    // ---------------------------------------------------
    // Return a TG that accepts just a single character

    public static <T> TransitionGraph<T> symbol( T transition ) {
        TransitionGraph<T> tg = new TransitionGraph<T>();
        State<T> s = tg.newState();
        State<T> f = tg.newState();
        s.addTransition( transition, f );
        tg.initialState = s;
        tg.finalStates.add( f );
        tg.alphabet.add( transition );
        return tg;
    }

    // ---------------------------------------------------
    // given a DFA, print it out

    public String toString() {
        StringBuffer buf = new StringBuffer();

        buf.append( "[Transition Graph\n" );

        // print all states and edges
        for( State<T> st : allStates ) {
            buf.append( st.getName() ).append( ": " );
            Iterator<Transition<T>> i = st.getTransitions().iterator();
            while( i.hasNext() ) {
                buf.append( i.next() );
                if( i.hasNext() )
                    buf.append( ", " );
            }
            
            buf.append( "\n" );
        }

        // print start state
        buf.append( "initial state: " );
        buf.append( initialState.getName() );
        buf.append( "\n" );

        // print final state(s)
        buf.append( "final states: " );
        buf.append( finalStates );
        buf.append( "\n" );

        // print alphabet
        buf.append( "alphabet: " );
        buf.append( alphabet );
        buf.append( "\n" );
        buf.append( "]\n" );
        
        return buf.toString();
    }

    // ---------------------------------------------------
    // renumber states of TG in preorder, beginning at start state

    public TransitionGraph<T> renumber() {
    	Set<State<T>> processed = new HashSet<State<T>>();
        
        LinkedList<State<T>> workList = new LinkedList<State<T>>();

        int val = 0;
        workList.addFirst( initialState );

        while( workList.size() > 0 ) {
            State<T> s = workList.removeFirst();
            s.setName( val++ );
            processed.add(s);

            for( Transition<T> e : s.getTransitions() ) {
                if( processed.add( e.getTo() ) )
                    workList.addLast( e.getTo() );
            }
        }

        return this;
    }

    // ---------------------------------------------------
    // given a DFA and a string, trace the execution of
    // the DFA on the string and decide accept/reject

    public boolean accepts( List<T> str ) {
        State<T> s = initialState;
        for( T ch : str ) {
            s = s.move( ch );
            if( s == null ) {
                return false;
            }
        }

        return finalStates.contains( s );
    }

    // -------------------------------------------------------------//
    // -------------------------------------------------------------//
    // --------------Make changes past this point-------------------//
    // -------------------------------------------------------------//
    // -------------------------------------------------------------//

    // ---------------------------------------------------
    // modify TG so that it accepts strings accepted by
    // either current TG or new TG

    public TransitionGraph<T> choice( TransitionGraph<T> t ) {
        State<T> s = newState(); // new start state
        State<T> f = newState(); // new final state

        // combine all states and final states
        allStates.addAll( t.allStates );
        finalStates.addAll( t.finalStates );

        // add an epsilon edge from new start state to
        // current TG's and parameter TG's start state
        s.addTransition( initialState );
        s.addTransition( t.initialState );
        initialState = s;

        // from all final states add an epsilon edge to new final state
        for( State<T> fs : finalStates ) {
            fs.addTransition( f );
        }
        // make f the only final state
        finalStates.clear();
        finalStates.add( f );

        // combine the alphabets
        alphabet.addAll( t.alphabet );

        return this;
    }

    // ---------------------------------------------------
    // modify TG so that it accepts strings composed
    // of strings accepted by current TG followed strings
    // accepted by new TG

    public TransitionGraph<T> concat( TransitionGraph<T> t ) {
        State<T> s = newState(); // new start state
        State<T> f = newState(); // new final state

        // combine all states
        allStates.addAll( t.allStates );

        // add an epsilon edge from new start state to current
        // TG's start state and make it the start state
        s.addTransition( initialState );
        initialState = s;

        // from final states of current TG add an
        // epsilon edge to start state of parameter TG
        for( State<T> fs : finalStates ) {
            fs.addTransition( t.initialState );
        }

        // from final states of parameter TG add an
        // epsilon edge to new final state
        for( State<T> tfs : t.finalStates ) {
            tfs.addTransition( f );
        }

        // make f the only final state
        finalStates.clear();
        finalStates.add( f );

        // combine alphabets
        alphabet.addAll( t.alphabet );

        return this;
    }

    // ---------------------------------------------------
    // Return a TG that accepts any sequence of 0 or more
    // strings accepted by TG

    public TransitionGraph<T> closure() {
        State<T> s = newState(); // new start state
        State<T> f = newState(); // new final state

        // from final states of current TG add an epsilon
        // edge to old start state and new final state
        for( State<T> fs : finalStates ) {
        	fs.addTransition( initialState );
            fs.addTransition( f );
        }
        // make f the only final state
        finalStates.clear();
        finalStates.add( f );

        // add an epsilon edge from new start state to
        // old start state and to new final state
        s.addTransition( initialState );
        s.addTransition( f );
        initialState = s;

        return this;
    }
    
    public TransitionGraph<T> insert( TransitionGraph<T> tg, State<T> i, State<T> f ) {
        // combine the alphabets
        alphabet.addAll( tg.alphabet );

        // map each state in the input tg to a state in this tg
    	Map<State<T>,State<T>> newStates = new HashMap<State<T>,State<T>>();
    	// initial state of input tg will be mapped to state i
    	newStates.put( tg.getInitialState(), i );
    	// all the final states of input tg will be mapped to state f
    	for( State<T> fs : tg.getFinalStates() ) {
    		newStates.put( fs, f );
    	}
    	
    	// for each transition in tg, create a new transition in this tg
    	// creating new states as necessary
    	for( State<T> s1 : tg.allStates ) {
            State<T> n1 = newStates.get( s1 );
            if( n1 == null ) {
            	n1 = newState();
            	newStates.put( s1, n1 );
            }            
            
            for (Transition<T> t : s1.getTransitions()) {
				State<T> s2 = t.getTo();
				
				State<T> n2 = newStates.get( s2 );
	            if( n2 == null ) {
	            	n2 = newState();
	            	newStates.put( s2, n2 );
	            }
	            
	            if( t.isEpsilon() )
	            	n1.addTransition( n2 );
	            else
	            	n1.addTransition( t.getName(), n2 );
			}
        }
        
        return this;
    }

    // ---------------------------------------------------
    // compute a NFA move from a set of states
    // to states that are reachable by one edge labeled c

    public Set<State<T>> move( Set<State<T>> stateSet, T c ) {
        Set<State<T>> result = new HashSet<State<T>>();

        // for all the states in the set SS
        for( State<T> st : stateSet ) {
            // for all the edges from state st
            for( Transition<T> e : st.getTransitions() ) {
                // add the 'to' state if transition matches
                if( e.hasName( c ) )
                    result.add( e.getTo() );
            }
        }

        return result;
    }

    // ---------------------------------------------------
    // USER DEFINED FUNCTION
    // compute from a set of states, the states that are
    // reachable by any number of edges labeled epsilon
    // from only one state

    public Set<State<T>> epsilonClosure( State<T> s, Set<State<T>> result ) {
        // s is in the epsilon closure of itself
        result.add( s );

        // for each edge from s
        for( Transition<T> e : s.getTransitions() ) {
            // if this is an epsilon transition and the result
            // does not contain 'to' state then add the epsilon
            // closure of 'to' state to the result set
            if( e.isEpsilon() && !result.contains( e.getTo() ) )
                result = epsilonClosure( e.getTo(), result );
        }

        return result;
    }

    // ---------------------------------------------------
    // compute from a set of states, the states that are
    // reachable by any number of edges labeled epsilon

    public Set<State<T>> epsilonClosure( Set<State<T>> stateSet ) {
        Set<State<T>> result = new HashSet<State<T>>();

        // for each state in SS add their epsilon closure to the result
        for (State<T> s : stateSet) {
            result = epsilonClosure( s, result );
        }

        return result;
    }
    
    public boolean isDeterministic() {
    	if( !allStates.contains( initialState ) )
    		throw new InternalReasonerException();
    	
    	for (State<T> s : allStates) {
			Set<T> seenSymbols = new HashSet<T>();
			for (Transition<T> t : s.getTransitions() ) {
				T symbol = t.getName();
				
				if( t.isEpsilon() || !seenSymbols.add( symbol ) )
					return false;
			}
		}
    	
    	return true;
    }
    
    public boolean isConnected() {
    	Set<State<T>> visited = new HashSet<State<T>>();
    	Stack<State<T>> stack = new Stack<State<T>>(); 
    	
    	stack.push( initialState );
    	visited.add( initialState );
    	
    	while( !stack.isEmpty() ) {
    		State<T> state = stack.pop();
    		    		
			if( !allStates.contains( state ) )
				return false;
			
			for (Transition<T> t : state.getTransitions() ) {
				if( visited.add( t.getTo() ) ) {
					stack.push( t.getTo() );
				}
			}	
    		
    	}
    	
    	return visited.size() == allStates.size();
    }

    // ---------------------------------------------------
    // convert NFA into equivalent DFA

    public TransitionGraph<T> determinize() {
        // Define a map for the new states in DFA. The key for the
        // elements in map is the set of NFA states and the value
        // is the new state in DFA
        HashMap<Set<State<T>>,State<T>> dStates = new HashMap<Set<State<T>>,State<T>>();

        // start state of DFA is epsilon closure of start state in NFA
        State<T> s = new State<T>();
        Set<State<T>> ss = epsilonClosure( initialState, new HashSet<State<T>>() );
        
        initialState = s;

        // unmarked states in dStates will be processed
        Set<State<T>> processList = new HashSet<State<T>>();
        processList.add( s );
        dStates.put( ss, s );
        initialState = s;

        // if there are unprocessed states continue
        boolean moreToProcess = true;
        while( moreToProcess ) {
            State<T> u = null;
            Set<State<T>> U = null;

            moreToProcess = false;

            //find an unmarked state in mappings in dStates
            for( Map.Entry<Set<State<T>>,State<T>> entry : dStates.entrySet() ) {				
                s = entry.getValue();
                ss = entry.getKey();
                moreToProcess = processList.contains( s );
                
                if( moreToProcess )
                	break;
            }

            if( moreToProcess ) {
                for( T a : alphabet ) {
                    // find epsilon closure of move with a
                    U = epsilonClosure( move( ss, a ) );
                    // if result is empty continue
                    if( U.size() == 0 )
                        continue;
                    // check if this set of NFA states are
                    // already in dStates
                    u = dStates.get( U );

                    // if the result is equal to NFA states
                    // associated with the processed state
                    // then add an edge from s to itself
                    // else create a new state and add edge
                    if( u == null ) {
                        u = new State<T>();
                        processList.add( u );
                        dStates.put( U, u );
                    }
                    else if( u.equals( s ) )
                        u = s;
                    s.addTransition( a, u );
                }
                // update s in dStates (since key is unchanged only
                // the changed value i.e state s is updated in dStates)
                processList.remove( s );
                dStates.put( ss, s );
            }
        }
        // a set of final states for DFA
        Set<State<T>> acceptingStates = new HashSet<State<T>>();
        // clear all states
        allStates.clear();

        for( Map.Entry<Set<State<T>>,State<T>> entry : dStates.entrySet() ) {
            // find DFA state and corresponding set of NFA states
            s = entry.getValue();
            ss = entry.getKey();
            // add DFA state to state set
            allStates.add( s );
            // if any of NFA states are final update accepting states
            if( isAnyFinal( ss ) )
                acceptingStates.add( s );
        }
        // accepting states becomes final states
        finalStates.clear();
        finalStates = acceptingStates;

        return this;
    }

    public void setPartition( Set<State<T>> stateSet, int num, Map<State<T>,Integer> partitions ) {
    	for( State<T> s : stateSet ) {
    		partitions.put( s, num );
        }
    }
    
    // ---------------------------------------------------
    // given a DFA, produce an equivalent minimized DFA

    
	public TransitionGraph<T> minimize() {
        // partitions are set of states, where max # of sets = # of states
        List<Set<State<T>>> partitions = new ArrayList<Set<State<T>>>(allStates.size());
		Map<State<T>,Integer> partitionNumbers = new HashMap<State<T>, Integer>();
		Map<State<T>,State<T>> partitionRep = new HashMap<State<T>, State<T>>();
		

        // first partition is the set of final states
		Set<State<T>> firstPartition = new HashSet<State<T>>( finalStates );
        partitions.add( firstPartition );
        setPartition( firstPartition, 0, partitionNumbers );
        
        // check if there are any states that are not final
        if( firstPartition.size() < allStates.size() ) {
            // second partition is set of non-accepting states
        	Set<State<T>> secondPartition  = new HashSet<State<T>>( allStates );
        	secondPartition.removeAll( finalStates );
        	partitions.add( secondPartition );
            setPartition( secondPartition, 1, partitionNumbers );
        }

        for( int p = 0; p < partitions.size(); p++ ) {
            Iterator<State<T>> i = partitions.get(p).iterator();

            // store the first element of the set
            State<T> s = i.next();
            
        	Set<State<T>> newPartition = null;

            // for all the states in a partition
            while( i.hasNext() ) {
                State<T> t = i.next();

                // for all the symbols in an alphabet
                for( T a : alphabet ) {
                    // find move(a) for the first and current state
                    // if they go to different partitions
                    if( !isEquivalentState( s.move( a ), t.move( a ), partitionNumbers ) ) {
                        // if a new partition was not created in this iteration
                        // create a new partition
                        if( newPartition == null ) {
                            newPartition = new HashSet<State<T>>();
                        	partitions.add( newPartition );
                        }
                        
                        // remove current state from this partition						
                        i.remove();
                        // add it to the new partition
                        newPartition.add( t );
                        // set its partition number
                        partitionNumbers.put( t, partitions.size() - 1 );
                        // done with this state
                        break;
                    }
                }
            }
            
            if( newPartition != null ) {
                // start checking from the first partition
                p = -1;
            }
        }
        
        // store the partition num of the start state
        int startPartition = partitionNumbers.get( initialState );

        // for each partition the first state is marked as the representative 
        // of that partition and rest is removed from states
        for( int p = 0; p < partitions.size(); p++ ) {
            Iterator<State<T>> i = partitions.get( p ).iterator();
            State<T> s = i.next();
            partitionRep.put( s, s );
            if( p == startPartition )
                initialState = s;
            while( i.hasNext() ) {
                State<T> t = i.next();
                allStates.remove( t );
                finalStates.remove( t );
                // set rep so that we can later update 
                // edges to this state
                partitionRep.put( t, s );
            }
        }

        // correct any edges that are going to states that are removed, 
        // by updating the target state to be the rep of partition which
        // dead state belonged to
        for( State<T> t : allStates ) {
            for( Transition<T> edge : t.getTransitions() ) {
                edge.setTo( partitionRep.get( edge.getTo() ) );
            }
        }

        return this;
    }
	
	protected boolean isEquivalentState(State<T> s1, State<T> s2, Map<State<T>, Integer> partitionNum) {
		if( s1 == s2 )
			return true;
		if( s1 == null || s2 == null )
			return false;
		return partitionNum.get( s1 ).equals( partitionNum.get( s2 ) );
	}
}
