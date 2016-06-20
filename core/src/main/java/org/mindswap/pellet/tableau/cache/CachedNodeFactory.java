// Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package org.mindswap.pellet.tableau.cache;

import openllet.aterm.ATermAppl;
import org.mindswap.pellet.Individual;

/**
 * <p>
 * Title:
 * </p>
 * <p>
 * Description:
 * </p>
 * <p>
 * Copyright: Copyright (c) 2008
 * </p>
 * <p>
 * Company: Clark & Parsia, LLC. <http://www.clarkparsia.com>
 * </p>
 *
 * @author Evren Sirin
 */
public class CachedNodeFactory
{
	public static CachedNode createTopNode()
	{
		return CachedConstantNode._TOP;
	}

	public static CachedNode createBottomNode()
	{
		return CachedConstantNode._BOTTOM;
	}

	public static CachedNode createSatisfiableNode()
	{
		return CachedConstantNode._INCOMPLETE;
	}

	public static CachedNode createNode(final ATermAppl name, final Individual node)
	{
		return new CachedConceptNode(name, node);
	}
}
