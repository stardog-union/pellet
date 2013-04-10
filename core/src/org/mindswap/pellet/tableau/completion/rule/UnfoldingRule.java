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
import java.util.logging.Logger;

import org.mindswap.pellet.DependencySet;
import org.mindswap.pellet.Individual;
import org.mindswap.pellet.Node;
import org.mindswap.pellet.PelletOptions;
import org.mindswap.pellet.tableau.completion.CompletionStrategy;
import org.mindswap.pellet.tableau.completion.queue.NodeSelector;
import org.mindswap.pellet.tbox.impl.Unfolding;
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
public class UnfoldingRule extends AbstractTableauRule {
    public final static Logger log = Logger.getLogger( UnfoldingRule.class.getName() );

	public UnfoldingRule(CompletionStrategy strategy) {
		super( strategy, NodeSelector.ATOM, BlockingType.COMPLETE );
	}
    
    public void apply( Individual node ) {
        if( !node.canApply( Node.ATOM ) )
        	return;
        
        List<ATermAppl> types = node.getTypes( Node.ATOM );
        int size = types.size();
        for( int j = node.applyNext[Node.ATOM]; j < size; j++ ) {
            ATermAppl c = types.get( j );

            if(!PelletOptions.MAINTAIN_COMPLETION_QUEUE && node.getDepends(c) == null)
				continue;
            
            applyUnfoldingRule( node, c );
            
            if( strategy.getABox().isClosed() )
                return; 
            
            // it is possible that unfolding added new atomic 
            // concepts that we need to further unfold
            size = types.size();  
        }
        node.applyNext[Node.ATOM] = size;
    }
    
    protected void applyUnfoldingRule( Individual node, ATermAppl c ) {
    	DependencySet ds = node.getDepends( c );
        
        if(!PelletOptions.MAINTAIN_COMPLETION_QUEUE && ds == null)
			return;           	
        
        Iterator<Unfolding> unfoldingList = strategy.getTBox().unfold( c );

        while( unfoldingList.hasNext() ) {
			Unfolding unfolding = unfoldingList.next();
        	ATermAppl unfoldingCondition = unfolding.getCondition();
        	DependencySet finalDS = node.getDepends( unfoldingCondition );
        	
        	if( finalDS == null )
        		continue;
        	
			Set<ATermAppl> unfoldingDS = unfolding.getExplanation();  
        	finalDS = finalDS.union( ds, strategy.getABox().doExplanation() );
        	finalDS = finalDS.union( unfoldingDS, strategy.getABox().doExplanation() );
        	
			ATermAppl unfoldedConcept = unfolding.getResult();          	
        	
            if( log.isLoggable( Level.FINE ) && !node.hasType( unfoldedConcept ) )
                log.fine( "UNF : " + node + ", " + ATermUtils.toString(c) + " -> " + ATermUtils.toString( unfoldedConcept ) + " - " + finalDS );

            strategy.addType( node, unfoldedConcept, finalDS );
        }
    }
}
