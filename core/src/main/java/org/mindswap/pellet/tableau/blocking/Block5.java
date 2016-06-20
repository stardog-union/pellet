// Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package org.mindswap.pellet.tableau.blocking;

import openllet.aterm.ATermAppl;
import org.mindswap.pellet.Node;
import org.mindswap.pellet.Role;
import org.mindswap.pellet.utils.ATermUtils;

/**
 * @author Evren Sirin
 */
public class Block5 implements BlockingCondition
{
	@Override
	public boolean isBlocked(final BlockingContext cxt)
	{
		for (final ATermAppl normMax : cxt._blocker.getTypes(Node.MAX))
		{
			final ATermAppl max = (ATermAppl) normMax.getArgument(0);
			final Role t = cxt._blocked.getABox().getRole(max.getArgument(0));
			final ATermAppl c = (ATermAppl) max.getArgument(2);

			if (t.isDatatypeRole())
				continue;

			final Role invT = t.getInverse();

			if (!cxt.isRSuccessor(invT))
				continue;

			if (cxt._blocked.getParent().hasType(ATermUtils.negate(c)))
				continue;

			return false;
		}

		return true;
	}
}
