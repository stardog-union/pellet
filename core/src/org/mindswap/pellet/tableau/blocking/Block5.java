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

/**
 * @author Evren Sirin
 */
public class Block5 implements BlockingCondition {
	public boolean isBlocked(BlockingContext cxt) {
		for( ATermAppl normMax : cxt.blocker.getTypes( Node.MAX ) ) {
			ATermAppl max = (ATermAppl) normMax.getArgument( 0 );
			Role t = cxt.blocked.getABox().getRole( max.getArgument( 0 ) );
			ATermAppl c = (ATermAppl) max.getArgument( 2 );

			if( t.isDatatypeRole() )
				continue;

			Role invT = t.getInverse();

			if( !cxt.isRSuccessor( invT ) )
				continue;
			
			if( cxt.blocked.getParent().hasType( ATermUtils.negate( c ) ) )
				continue;
			
			return false;
		}

		return true;
	}
}
