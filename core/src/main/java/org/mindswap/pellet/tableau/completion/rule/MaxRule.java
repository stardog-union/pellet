// Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package org.mindswap.pellet.tableau.completion.rule;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;

import org.mindswap.pellet.Clash;
import org.mindswap.pellet.DependencySet;
import org.mindswap.pellet.Edge;
import org.mindswap.pellet.EdgeList;
import org.mindswap.pellet.Individual;
import org.mindswap.pellet.Node;
import org.mindswap.pellet.NodeMerge;
import org.mindswap.pellet.PelletOptions;
import org.mindswap.pellet.Role;
import org.mindswap.pellet.tableau.branch.MaxBranch;
import org.mindswap.pellet.tableau.completion.CompletionStrategy;
import org.mindswap.pellet.tableau.completion.queue.NodeSelector;
import org.mindswap.pellet.utils.SetUtils;

import aterm.ATermAppl;
import aterm.ATermInt;

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
public class MaxRule extends AbstractTableauRule {
	public MaxRule(CompletionStrategy strategy) {
		super( strategy, NodeSelector.MAX_NUMBER, BlockingType.INDIRECT );
	}

    /**
     * Apply max rule to the individual.
     */
    public void apply( Individual x ) {
        if( !x.canApply( Individual.MAX ) )
        	return;

        List<ATermAppl> maxCardinality = x.getTypes( Node.MAX );
        for( int i = 0; i < maxCardinality.size(); i++ ) {
            ATermAppl mc = maxCardinality.get( i );

            applyMaxRule( x, mc );
            
            if( strategy.getABox().isClosed() )
                return;

            if( x.isMerged() ) 
                return;
        }
        x.applyNext[Individual.MAX] = maxCardinality.size();
    }
    
    protected void applyMaxRule( Individual x, ATermAppl mc ) {
 
        // max(r, n) is in normalized form not(min(p, n + 1))
        ATermAppl max = (ATermAppl) mc.getArgument( 0 );

        Role r = strategy.getABox().getRole( max.getArgument( 0 ) );
        int n = ((ATermInt) max.getArgument( 1 )).getInt() - 1;
        ATermAppl c = (ATermAppl) max.getArgument( 2 );

        DependencySet ds = x.getDepends( mc );

        if(!PelletOptions.MAINTAIN_COMPLETION_QUEUE && ds == null)
        		return;
        		
        
        if( n == 1 ) {
            applyFunctionalMaxRule( x, r, c, ds );
            if( strategy.getABox().isClosed() )
                return;
        }
        else {
            boolean hasMore = true;
            
            while( hasMore ) {
            		hasMore = applyMaxRule( x, r, c, n, ds );

                if( strategy.getABox().isClosed() )
                    return;

                if( x.isMerged() ) {
                    return;
                }

                if( hasMore ) {
                    // subsequent merges depend on the previous merge
                    ds = ds.union( new DependencySet( strategy.getABox().getBranches().size() ), strategy.getABox().doExplanation() );
                }
            }
        }   
    }
    

    /**
     * 
     * applyMaxRule
     * 
     * @param x
     * @param r
     * @param k
     * @param ds
     * 
     * @return true if more merges are required for this maxCardinality
     */
    protected boolean applyMaxRule( Individual x, Role r, ATermAppl c, int k, DependencySet ds ) {

        EdgeList edges = x.getRNeighborEdges( r );
        // find all distinct R-neighbors of x
        Set<Node> neighbors = edges.getFilteredNeighbors( x, c );

        int n = neighbors.size();

        // if( log.isLoggable( Level.FINE ) )
        // log.fine( "Neighbors: " + n + " maxCardinality: " + k);

        // if restriction was maxCardinality 0 then having any R-neighbor
        // violates the restriction. no merge can fix this. compute the
        // dependency and return
        if( k == 0 && n > 0 ) {
            for( int e = 0; e < edges.size(); e++ ) {
                Edge edge = edges.edgeAt( e );
                Node neighbor = edge.getNeighbor( x );
                DependencySet typeDS = neighbor.getDepends( c );
                if( typeDS != null ) {
                	Role edgeRole = edge.getRole();
    				DependencySet subDS = r.getExplainSubOrInv( edgeRole );
					ds = ds.union( subDS, strategy.getABox().doExplanation() );
	                ds = ds.union( edge.getDepends(), strategy.getABox().doExplanation() );
	                ds = ds.union( typeDS, strategy.getABox().doExplanation() );
	                
                }
            }

            strategy.getABox().setClash( Clash.maxCardinality( x, ds, r.getName(), 0 ) );
            return false;
        }

        // if there are less than n neighbors than max rule won't be triggered
        // return false because no more merge required for this role
        if( n <= k )
            return false;        
        
        // create the pairs to be merged
        List<NodeMerge> mergePairs = new ArrayList<NodeMerge>();
        DependencySet differenceDS = findMergeNodes( neighbors, x, mergePairs );
        ds = ds.union( differenceDS, strategy.getABox().doExplanation() );

        // if no pairs were found, i.e. all were defined to be different from
        // each other, then it means this max cardinality restriction is
        // violated. dependency of this clash is on all the neighbors plus the
        // dependency of the restriction type
        if( mergePairs.size() == 0 ) {
            DependencySet dsEdges = x.hasDistinctRNeighborsForMax( r, k + 1, c );
            if( dsEdges == null ) {
            	if( log.isLoggable( Level.FINE ) )
                	log.fine( "Cannot determine the exact clash dependency for " + x );
                strategy.getABox().setClash( Clash.maxCardinality( x, ds ) );
                return false;
            }
            else {
                if( log.isLoggable( Level.FINE ) )
                    log.fine( "Early clash detection for max rule worked " + x + " has more than "
                        + k + " " + r + " edges " + ds.union( dsEdges, strategy.getABox().doExplanation() ) + " "
                        + x.getRNeighborEdges( r ).getNeighbors( x ) );

                if( strategy.getABox().doExplanation() )
                    strategy.getABox().setClash( Clash.maxCardinality( x, ds.union( dsEdges, strategy.getABox().doExplanation() ), r.getName(), k ) );
                else
                    strategy.getABox().setClash( Clash.maxCardinality( x, ds.union( dsEdges, strategy.getABox().doExplanation() ) ) );

                return false;
            }
        }

        // add the list of possible pairs to be merged in the branch list
        MaxBranch newBranch = new MaxBranch( strategy.getABox(), strategy, x, r, k, c, mergePairs, ds );
        strategy.addBranch( newBranch );

        // try a merge that does not trivially fail
        if( newBranch.tryNext() == false )
            return false;

        if( log.isLoggable( Level.FINE ) )
            log.fine( "hasMore: " + (n > k + 1) );

        // if there were exactly k + 1 neighbors the previous step would
        // eliminate one node and only n neighbors would be left. This means
        // restriction is satisfied. If there were more than k + 1 neighbors
        // merging one pair would not be enough and more merges are required,
        // thus false is returned
        return n > k + 1;
    }
    

    DependencySet findMergeNodes( Set<Node> neighbors, Individual node, List<NodeMerge> pairs ) {
        DependencySet ds = DependencySet.INDEPENDENT;

        List<Node> nodes = new ArrayList<Node>( neighbors );
        for( int i = 0; i < nodes.size(); i++ ) {
            Node y = nodes.get( i );
            for( int j = i + 1; j < nodes.size(); j++ ) {
                Node x = nodes.get( j );

                if( y.isDifferent( x ) ) {
                	ds = ds.union( y.getDifferenceDependency( x ), strategy.getABox().doExplanation() );
                    continue;
                }

                // 1. if x is a nominal node (of lower level), then Merge(y, x)
                if( x.getNominalLevel() < y.getNominalLevel() )
                    pairs.add( new NodeMerge( y, x ) );
                // 2. if y is a nominal node or an ancestor of x, then Merge(x, y)
                else if( y.isNominal() )
                    pairs.add( new NodeMerge( x, y ) );
                // 3. if y is an ancestor of x, then Merge(x, y)
                // Note: y is an ancestor of x iff the max cardinality
                // on node merges the "node"'s parent y with "node"'s
                // child x
                else if( y.hasSuccessor( node ) )
                    pairs.add( new NodeMerge( x, y ) );
                // 4. else Merge(y, x)
                else
                    pairs.add( new NodeMerge( y, x ) );
            }
        }

        return ds;
    }
    

    public void applyFunctionalMaxRule( Individual x, Role s, ATermAppl c, DependencySet ds ) {
        Set<Role> functionalSupers = s.getFunctionalSupers();
        if( functionalSupers.isEmpty() )
            functionalSupers = SetUtils.singleton( s );
        LOOP:
        for( Iterator<Role> it = functionalSupers.iterator(); it.hasNext(); ) {
            Role r = it.next();

            if (PelletOptions.USE_TRACING) {
            	ds = ds.union( s.getExplainSuper(r.getName()), strategy.getABox().doExplanation() ).union( r.getExplainFunctional(), strategy.getABox().doExplanation() );
            }
            
            EdgeList edges = x.getRNeighborEdges( r );

            // if there is not more than one edge then func max rule won't be triggered
            if( edges.size() <= 1 )
                continue;

            // find all distinct R-neighbors of x
            Set<Node> neighbors = edges.getFilteredNeighbors( x, c );

            // if there is not more than one neighbor then func max rule won't be triggered
            if( neighbors.size() <= 1 )
                continue;

            Node head = null;

            int edgeIndex = 0;
            int edgeCount = edges.size();

            // find the head and its corresponding dependency information. 
            // since head is not necessarily the first element in the 
            // neighbor list we need to first find the un-pruned node 
            for( ; edgeIndex < edgeCount; edgeIndex++ ) {
                Edge edge = edges.edgeAt( edgeIndex );
                head = edge.getNeighbor( x );

                if( head.isPruned() || !neighbors.contains( head ) )
                    continue;

                // this node is included in the merge list because the edge
                // exists and the node has the qualification in its types
                ds = ds.union( edge.getDepends(), strategy.getABox().doExplanation() );
                ds = ds.union( head.getDepends( c ), strategy.getABox().doExplanation() );
                ds = ds.union( r.getExplainSubOrInv( edge.getRole() ), strategy.getABox().doExplanation() );
                break;
            }

            // now iterate through the rest of the elements in the neighbors
            // and merge them to the head node. it is possible that we will
            // switch the head at some point because of merging rules such
            // that you always merge to a nominal of higher level
            for( edgeIndex++; edgeIndex < edgeCount; edgeIndex++ ) {
                Edge edge = edges.edgeAt( edgeIndex );
                Node next = edge.getNeighbor( x );

                if( next.isPruned() || !neighbors.contains( next ) )
                    continue;

                // it is possible that there are multiple edges to the same
                // node, e.g. property p and its super property, so check if
                // we already merged this one
                if( head.isSame( next ) )
                    continue;

                // this node is included in the merge list because the edge
                // exists and the node has the qualification in its types
                ds = ds.union( edge.getDepends(), strategy.getABox().doExplanation() );
                ds = ds.union( next.getDepends( c ), strategy.getABox().doExplanation() );
                ds = ds.union( r.getExplainSubOrInv( edge.getRole() ), strategy.getABox().doExplanation() );

                if( next.isDifferent( head ) ) {
                    ds = ds.union( head.getDepends( c ), strategy.getABox().doExplanation() );
                    ds = ds.union( next.getDepends( c ), strategy.getABox().doExplanation() );
                    ds = ds.union( next.getDifferenceDependency( head ), strategy.getABox().doExplanation() );
                    if( r.isFunctional() )
                        strategy.getABox().setClash( Clash.functionalCardinality( x, ds, r.getName() ) );
                    else
                        strategy.getABox().setClash( Clash.maxCardinality( x, ds, r.getName(), 1 ) );

                    break;
                }

                if( x.isNominal() && head.isBlockable() && next.isBlockable()
                    && head.hasSuccessor( x ) && next.hasSuccessor( x ) ) {
                    Individual newNominal = strategy.createFreshIndividual( null, ds );

                    strategy.addEdge( x, r, newNominal, ds );

                    continue LOOP;
                }
                // always merge to a nominal (of lowest level) or an ancestor
                else if( (next.getNominalLevel() < head.getNominalLevel())
                    || (!head.isNominal() && next.hasSuccessor( x )) ) {
                    Node temp = head;
                    head = next;
                    next = temp;
                }

                if( log.isLoggable( Level.FINE ) )
                    log.fine( "FUNC: " + x + " for prop " + r + " merge " + next + " -> " + head
                        + " " + ds );

                strategy.mergeTo( next, head, ds );

                if( strategy.getABox().isClosed() )
                    return;

                if( head.isPruned() ) {
                    ds = ds.union( head.getMergeDependency( true ), strategy.getABox().doExplanation() );
                    head = head.getSame();
                }
            }
        }
    }

}