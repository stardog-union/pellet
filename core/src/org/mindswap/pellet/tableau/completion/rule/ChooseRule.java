// Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package org.mindswap.pellet.tableau.completion.rule;

import java.util.Iterator;
import java.util.List;

import org.mindswap.pellet.Edge;
import org.mindswap.pellet.EdgeList;
import org.mindswap.pellet.Individual;
import org.mindswap.pellet.Node;
import org.mindswap.pellet.PelletOptions;
import org.mindswap.pellet.Role;
import org.mindswap.pellet.tableau.branch.ChooseBranch;
import org.mindswap.pellet.tableau.completion.CompletionStrategy;
import org.mindswap.pellet.tableau.completion.queue.NodeSelector;
import org.mindswap.pellet.utils.ATermUtils;

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
public class ChooseRule extends AbstractTableauRule {

	public ChooseRule(CompletionStrategy strategy) {
		super( strategy, NodeSelector.CHOOSE, BlockingType.INDIRECT );
	}

    public void apply( Individual x ) {
        if( !x.canApply( Individual.MAX ) )
        	return;

        List<ATermAppl> maxCardinality = x.getTypes( Node.MAX );
        Iterator<ATermAppl> j = maxCardinality.iterator();

        while( j.hasNext() ) {
        	ATermAppl maxCard = j.next();
        	apply( x, maxCard );
        }
    }
    
    protected void apply( Individual x, ATermAppl maxCard ) {
        // max(r, n, c) is in normalized form not(min(p, n + 1, c))       
        ATermAppl max = (ATermAppl) maxCard.getArgument( 0 );
        Role r = strategy.getABox().getRole( max.getArgument( 0 ) );
        ATermAppl c = (ATermAppl) max.getArgument( 2 );

        if( ATermUtils.isTop( c ) )
            return;
        
        if(!PelletOptions.MAINTAIN_COMPLETION_QUEUE && x.getDepends(maxCard) == null)
    			return;

        EdgeList edges = x.getRNeighborEdges( r );
        for( Iterator<Edge> i = edges.iterator(); i.hasNext(); ) {
            Edge edge = i.next();
            Node neighbor = edge.getNeighbor( x );

            if( !neighbor.hasType( c ) && !neighbor.hasType( ATermUtils.negate( c ) ) ) {
                ChooseBranch newBranch = new ChooseBranch( strategy.getABox(), strategy, neighbor, c, x
                    .getDepends( maxCard ) );
                strategy.addBranch( newBranch );

                newBranch.tryNext();

                if( strategy.getABox().isClosed() )
                    return;
            }
        }    	
    }


}
