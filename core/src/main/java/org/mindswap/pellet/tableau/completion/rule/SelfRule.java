// Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package org.mindswap.pellet.tableau.completion.rule;

import java.util.List;
import java.util.logging.Level;

import org.mindswap.pellet.Individual;
import org.mindswap.pellet.Node;
import org.mindswap.pellet.PelletOptions;
import org.mindswap.pellet.Role;
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
public class SelfRule extends AbstractTableauRule {

	public SelfRule(CompletionStrategy strategy) {
		super( strategy, NodeSelector.ATOM, BlockingType.NONE );
	}

    public final void apply( Individual node ) {
        List<ATermAppl> types = node.getTypes( Node.ATOM );
        int size = types.size();
        for( int j = 0; j < size; j++ ) {
            ATermAppl c = types.get( j );

            if(!PelletOptions.MAINTAIN_COMPLETION_QUEUE && node.getDepends(c) == null)
				continue;

            
            if( ATermUtils.isSelf( c ) ) {
                ATermAppl pred = (ATermAppl) c.getArgument( 0 );
                Role role = strategy.getABox().getRole( pred );
                if( log.isLoggable( Level.FINE ) && !node.hasRSuccessor( role, node ) )
                    log.fine( "SELF: " + node + " " + role + " " + node.getDepends( c ) );
                strategy.addEdge( node, role, node, node.getDepends( c ) );

                if( strategy.getABox().isClosed() )
                    return;
            }
        }
    }
}
