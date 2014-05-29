// Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package org.mindswap.pellet.tableau.blocking;

import org.mindswap.pellet.Node;
import org.mindswap.pellet.Role;
import org.mindswap.pellet.utils.ATermUtils;

import aterm.ATermAppl;
import aterm.ATermInt;

/**
 * @author Evren Sirin
 */
public class Block3 implements BlockingCondition {
	public boolean isBlocked(BlockingContext cxt) {
		for( ATermAppl normMax : cxt.blocker.getTypes( Node.MAX ) ) {
			ATermAppl max = (ATermAppl) normMax.getArgument( 0 );
			Role s = cxt.blocked.getABox().getRole( max.getArgument( 0 ) );
			int n = ((ATermInt) max.getArgument( 1 )).getInt() - 1;
			ATermAppl c = (ATermAppl) max.getArgument( 2 );

			if( s.isDatatypeRole() )
				continue;

			Role invS = s.getInverse();

			if( !cxt.isRSuccessor( invS ) )
				continue;
			
			if( cxt.blocked.getParent().hasType( ATermUtils.negate( c ) ) )
				continue;
			
			if( cxt.blocked.getParent().hasType( c ) 
				&& cxt.blocker.getRSuccessors( s, c ).size() < n )
				continue;

			return false;
		}

		return true;
	}
}
