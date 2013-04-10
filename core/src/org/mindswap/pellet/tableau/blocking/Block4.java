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
public class Block4 implements BlockingCondition {
	public boolean isBlocked(BlockingContext cxt) {
		for( ATermAppl min : cxt.blocker.getTypes( Node.MIN ) ) {
			if( !block4( cxt, min ) )
				return false;
		}

		for( ATermAppl normSome : cxt.blocker.getTypes( Node.SOME ) ) {
			ATermAppl some = (ATermAppl) normSome.getArgument( 0 );
			if( !block4( cxt, some ) )
				return false;
		}

		return true;
	}
	
	protected boolean block4(BlockingContext cxt, ATermAppl term) {
		Role t = cxt.blocked.getABox().getRole( term.getArgument( 0 ) );
		int m = 1;
		ATermAppl c;
		
		if( ATermUtils.isMin( term ) ) {
			c = (ATermAppl) term.getArgument( 2 );
			m = ((ATermInt) term.getArgument( 1 )).getInt();
		}
		else {
			c = ATermUtils.negate( (ATermAppl) term.getArgument( 1 ) );
		}

		if( t.isDatatypeRole() )
			return true;

		Role invT = t.getInverse();         
		
		if( cxt.isRSuccessor( invT )
			&& cxt.blocked.getParent().hasType( c ) )
			return true;
		
		if( cxt.blocker.getRSuccessors( t, c ).size() >= m )
			return true;
		
		return false;
	}
}
