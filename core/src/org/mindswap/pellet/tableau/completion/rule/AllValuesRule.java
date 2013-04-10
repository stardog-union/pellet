// Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package org.mindswap.pellet.tableau.completion.rule;

import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;

import org.mindswap.pellet.DependencySet;
import org.mindswap.pellet.Edge;
import org.mindswap.pellet.EdgeList;
import org.mindswap.pellet.Individual;
import org.mindswap.pellet.Node;
import org.mindswap.pellet.PelletOptions;
import org.mindswap.pellet.Role;
import org.mindswap.pellet.exceptions.InternalReasonerException;
import org.mindswap.pellet.tableau.completion.CompletionStrategy;
import org.mindswap.pellet.tableau.completion.queue.NodeSelector;
import org.mindswap.pellet.utils.ATermUtils;

import aterm.ATerm;
import aterm.ATermAppl;
import aterm.ATermList;

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
public class AllValuesRule extends AbstractTableauRule {
	public AllValuesRule(CompletionStrategy strategy) {
		super( strategy, NodeSelector.UNIVERSAL, BlockingType.NONE );
	}

   public  void apply( Individual x ) {
        List<ATermAppl> allValues = x.getTypes( Node.ALL );
        int size = allValues.size();
        Iterator<ATermAppl> i = allValues.iterator();
        while( i.hasNext() ) {
            ATermAppl av = i.next();
            DependencySet avDepends = x.getDepends( av );

            if(!PelletOptions.MAINTAIN_COMPLETION_QUEUE && avDepends == null)
				continue;
            
            applyAllValues( x, av, avDepends );

            if( x.isMerged() || strategy.getABox().isClosed() )
                return;

            // if there are self links through transitive properties restart
            if( size != allValues.size() ) {
                i = allValues.iterator();
                size = allValues.size();
            }
        }
    }

    
    /**
     * Apply the allValues rule for the given type with the given dependency. The concept is in the
     * form all(r,C) and this function adds C to all r-neighbors of x
     * 
     * @param x
     * @param av
     * @param ds
     */
    public void applyAllValues( Individual x, ATermAppl av, DependencySet ds ) {
        // Timer timer = kb.timers.startTimer("applyAllValues");

        if( av.getArity() == 0 )
            throw new InternalReasonerException();
        ATerm p = av.getArgument( 0 );
        ATermAppl c = (ATermAppl) av.getArgument( 1 );        
        
        ATermList roleChain = ATermUtils.EMPTY_LIST;
        Role s = null;
        if( p.getType() == ATerm.LIST ) {
            roleChain = (ATermList) p;
            s = strategy.getABox().getRole( roleChain.getFirst() );
            roleChain = roleChain.getNext();
        }
        else
            s = strategy.getABox().getRole( p );

        if ( s.isTop() && s.isObjectRole() ) {
        	applyAllValuesTop( av, c, ds );
        	return;
        }

        EdgeList edges = x.getRNeighborEdges( s );
        for( int e = 0; e < edges.size(); e++ ) {
            Edge edgeToY = edges.edgeAt( e );
            Node y = edgeToY.getNeighbor( x );
            DependencySet finalDS = ds.union( edgeToY.getDepends(), strategy.getABox().doExplanation() );
            
            if( roleChain.isEmpty() )
                applyAllValues( x, s, y, c, finalDS );
            else if(y.isIndividual()) {
                ATermAppl allRC = ATermUtils.makeAllValues( roleChain, c );

                strategy.addType( y, allRC, finalDS );
            }

            if( x.isMerged() || strategy.getABox().isClosed() )
                return;
        }

        if( !s.isSimple() ) {
            Set<ATermList> subRoleChains = s.getSubRoleChains();
            for( Iterator<ATermList> it = subRoleChains.iterator(); it.hasNext(); ) {
                ATermList chain = it.next();
                DependencySet subChainDS = ds.union(s.getExplainSub(chain), strategy.getABox().doExplanation() );
				if (!applyAllValuesPropertyChain(x, chain, c, subChainDS))
					return;
            }
        }
        
        if (!roleChain.isEmpty()) {
        	applyAllValuesPropertyChain(x, (ATermList) p, c, ds);
        }

        // timer.stop();
    }

    protected boolean applyAllValuesPropertyChain( Individual x, ATermList chain, ATermAppl c, DependencySet ds ) {
         Role r = strategy.getABox().getRole( chain.getFirst() );
         
         EdgeList edges = x.getRNeighborEdges( r );
         if( !edges.isEmpty() ) {
             ATermAppl allRC = ATermUtils.makeAllValues( chain.getNext(), c );

             for( int e = 0; e < edges.size(); e++ ) {
                 Edge edgeToY = edges.edgeAt( e );
                 Node y = edgeToY.getNeighbor( x );
                 DependencySet finalDS = ds.union( edgeToY.getDepends(), strategy.getABox().doExplanation() );
                 
                 applyAllValues( x, r, y, allRC, finalDS );

                 if( x.isMerged() || strategy.getABox().isClosed() )
                     return false;
             }
         }
         
         return true;
    }
    
    protected void applyAllValues( Individual subj, Role pred, Node obj, ATermAppl c, DependencySet ds ) {
        if( !obj.hasType( c ) ) {
            if( log.isLoggable( Level.FINE ) ) {
                log.fine( "ALL : " + subj + " -> " + pred + " -> " + obj + " : " + ATermUtils.toString( c ) + " - " + ds );
            }

            //because we do not maintain the queue it could be the case that this node is pruned, so return
            if(PelletOptions.USE_COMPLETION_QUEUE && !PelletOptions.MAINTAIN_COMPLETION_QUEUE && obj.isPruned())
            	return;
            

            strategy.addType( obj, c, ds );
        }
    }
    

    public void applyAllValues( Individual subj, Role pred, Node obj, DependencySet ds ) {
        List<ATermAppl> allValues = subj.getTypes( Node.ALL );
        int allValuesSize = allValues.size();
        Iterator<ATermAppl> i = allValues.iterator();
        while( i.hasNext() ) {
            ATermAppl av = i.next();

            ATerm p = av.getArgument( 0 );
            ATermAppl c = (ATermAppl) av.getArgument( 1 );
            
            ATermList roleChain = ATermUtils.EMPTY_LIST;
            Role s = null;
            if( p.getType() == ATerm.LIST ) {
                roleChain = (ATermList) p;
                s = strategy.getABox().getRole( roleChain.getFirst() );
                roleChain = roleChain.getNext();
            }
            else
                s = strategy.getABox().getRole( p );
                        
            if ( s.isTop() && s.isObjectRole() ) {
            	applyAllValuesTop( av, c, ds );
            	if( strategy.getABox().isClosed() )
                    return;
            	continue;
            }

            if( pred.isSubRoleOf( s ) ) {
                DependencySet finalDS = subj.getDepends( av );
				finalDS = finalDS.union( ds, strategy.getABox().doExplanation() );
				finalDS = finalDS.union( s.getExplainSubOrInv( pred ), strategy.getABox().doExplanation() );
                if( roleChain.isEmpty() )
                    applyAllValues( subj, s, obj, c, finalDS );
                else if (obj.isIndividual()) {
                    ATermAppl allRC = ATermUtils.makeAllValues( roleChain, c );

                    strategy.addType( obj, allRC, finalDS );
                }
                
                if( strategy.getABox().isClosed() )
                    return;
            }

            if( !s.isSimple() ) {
                DependencySet finalDS = subj.getDepends( av ).union( ds, strategy.getABox().doExplanation() );
                Set<ATermList> subRoleChains = s.getSubRoleChains();
                for( Iterator<ATermList> it = subRoleChains.iterator(); it.hasNext(); ) {
                    ATermList chain = it.next();
                    
//                    if( !pred.getName().equals( chain.getFirst() ) )
                    Role firstRole = strategy.getABox().getRole(chain.getFirst());
                    if( !pred.isSubRoleOf( firstRole ) )
                        continue;

                    ATermAppl allRC = ATermUtils.makeAllValues( chain.getNext(), c );

                    applyAllValues( subj, pred, obj, allRC, finalDS.union(
                    		firstRole.getExplainSub(pred.getName()), strategy.getABox().doExplanation()).union(
                    				s.getExplainSub(chain), strategy.getABox().doExplanation() ) );

                    if( subj.isMerged() || strategy.getABox().isClosed() )
                        return;
                }
            }

            if( subj.isMerged() )
                return;

            obj = obj.getSame();

            // if there are self links then restart
            if( allValuesSize != allValues.size() ) {
                i = allValues.iterator();
                allValuesSize = allValues.size();
            }
        }
    }
    
    /**
     * Apply all values restriction for the Top object role
     */
    void applyAllValuesTop( ATermAppl allTopC, ATermAppl c, DependencySet ds ) {
		for( Node node : strategy.getABox().getNodes() ) {
			if( node.isIndividual() && !node.isPruned() && !node.hasType( c ) ) {
				node.addType( c, ds );
				node.addType( allTopC, ds );
				
				if( strategy.getABox().isClosed() )
					break;
			}
		}
		
    }
}
