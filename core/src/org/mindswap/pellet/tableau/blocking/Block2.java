// Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package org.mindswap.pellet.tableau.blocking;

import java.util.Iterator;
import java.util.Set;

import org.mindswap.pellet.Node;
import org.mindswap.pellet.Role;
import org.mindswap.pellet.utils.ATermUtils;

import aterm.ATerm;
import aterm.ATermAppl;
import aterm.ATermList;

/**
 * @author Evren Sirin
 */
public class Block2 implements BlockingCondition {
	public boolean isBlocked(BlockingContext cxt) {
		for (ATermAppl av : cxt.blocker.getTypes(Node.ALL)) {
			ATerm p = av.getArgument(0);
			ATermAppl c = (ATermAppl) av.getArgument(1);
			Role s = cxt.blocked.getABox().getRole(p);

			if (p instanceof ATermList) {
				ATermList chain = (ATermList) p;

				if (!isBlockedByChain(cxt, chain, c))
					return false;
			}
			else if (s.isDatatypeRole()) {
				continue;
			}
			else {
				Role invS = s.getInverse();

				if (cxt.isRSuccessor(invS) && !cxt.blocked.getParent().hasType(c))
					return false;

				if (!s.isSimple()) {
					Set<ATermList> subRoleChains = s.getSubRoleChains();
					for (Iterator<ATermList> it = subRoleChains.iterator(); it.hasNext();) {
						ATermList chain = it.next();

						if (!isBlockedByChain(cxt, chain, c))
							return false;
					}
				}
			}
		}

		return true;
	}

	protected boolean isBlockedByChain(BlockingContext cxt, ATermList chain, ATermAppl c) {
		Role firstRole = cxt.blocked.getABox().getRole(chain.getFirst());

		return !cxt.getIncomingRoles().contains(firstRole.getInverse())
		       && cxt.blocked.getParent().hasType(ATermUtils.makeAllValues(chain.getNext(), c));
	}
}
