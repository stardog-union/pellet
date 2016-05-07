// Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package org.mindswap.pellet.tableau.blocking;

import aterm.ATermAppl;
import org.mindswap.pellet.Node;
import org.mindswap.pellet.Role;
import org.mindswap.pellet.utils.ATermUtils;

/**
 * @author Evren Sirin
 */
public class Block6 implements BlockingCondition
{
	@Override
	public boolean isBlocked(final BlockingContext cxt)
	{
		for (final ATermAppl min : cxt._blocked.getParent().getTypes(Node.MIN))
		{
			final Role u = cxt._blocked.getABox().getRole(min.getArgument(0));
			final ATermAppl c = (ATermAppl) min.getArgument(2);

			if (u.isDatatypeRole())
				continue;

			if (cxt.isRSuccessor(u) && !cxt._blocked.hasType(ATermUtils.negate(c)))
				return false;
		}

		for (final ATermAppl normSome : cxt._blocked.getParent().getTypes(Node.SOME))
		{
			final ATermAppl some = (ATermAppl) normSome.getArgument(0);
			final Role u = cxt._blocked.getABox().getRole(some.getArgument(0));
			final ATermAppl notC = (ATermAppl) some.getArgument(1);

			if (u.isDatatypeRole())
				continue;

			if (cxt.isRSuccessor(u) && !cxt._blocked.hasType(notC))
				return false;
		}

		return true;
	}
}
