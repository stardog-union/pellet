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
public class Block6 implements BlockingCondition {
	public boolean isBlocked(BlockingContext cxt) {
		for( ATermAppl min : cxt.blocked.getParent().getTypes( Node.MIN ) ) {
			Role u = cxt.blocked.getABox().getRole( min.getArgument( 0 ) );
			ATermAppl c = (ATermAppl) min.getArgument( 2 );

			if( u.isDatatypeRole() )
				continue;

			if( cxt.isRSuccessor( u ) && !cxt.blocked.hasType( ATermUtils.negate( c ) ) )
				return false;
		}
		
		for( ATermAppl normSome : cxt.blocked.getParent().getTypes( Node.SOME ) ) {
			ATermAppl some = (ATermAppl) normSome.getArgument( 0 );
			Role u = cxt.blocked.getABox().getRole( some.getArgument( 0 ) );
			ATermAppl notC = (ATermAppl) some.getArgument( 1 );

			if( u.isDatatypeRole() )
				continue;

			if( cxt.isRSuccessor( u ) && !cxt.blocked.hasType( notC ) )
				return false;
		}

		return true;
	}
}
