// Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package com.clarkparsia.pellet.sparqldl.engine;

import aterm.ATermAppl;
import com.clarkparsia.pellet.sparqldl.model.Query;
import com.clarkparsia.pellet.sparqldl.model.QueryAtom;
import java.util.HashSet;
import java.util.Set;
import org.mindswap.pellet.utils.ATermUtils;
import org.mindswap.pellet.utils.SizeEstimate;

/**
 * <p>
 * Title: Computation of size estimate for a knowledge base and a query.
 * </p>
 * <p>
 * Description:
 * </p>
 * <p>
 * Copyright: Copyright (c) 2007
 * </p>
 * <p>
 * Company: Clark & Parsia, LLC. <http://www.clarkparsia.com>
 * </p>
 *
 * @author Petr Kremen
 */
public class QuerySizeEstimator
{

	public static void computeSizeEstimate(final Query query)
	{
		final SizeEstimate sizeEstimate = query.getKB().getSizeEstimate();

		final Set<ATermAppl> concepts = new HashSet<>();
		final Set<ATermAppl> properties = new HashSet<>();
		//		boolean fullDone = false;
		for (final QueryAtom atom : query.getAtoms())
			for (final ATermAppl argument : atom.getArguments())
				if (!ATermUtils.isVar(argument))
				{
					if ((query.getKB().isClass(argument) || ATermUtils.isComplexClass(argument)) && !sizeEstimate.isComputed(argument))
						concepts.add(argument);

					if (query.getKB().isProperty(argument) && !sizeEstimate.isComputed(argument))
						properties.add(argument);
				}

		sizeEstimate.compute(concepts, properties);
	}
}
