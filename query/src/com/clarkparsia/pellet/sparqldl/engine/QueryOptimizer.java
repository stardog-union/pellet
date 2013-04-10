// Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package com.clarkparsia.pellet.sparqldl.engine;

import java.util.logging.Level;
import java.util.logging.Logger;
import org.mindswap.pellet.PelletOptions;

import com.clarkparsia.pellet.sparqldl.model.Query;

/**
 * <p>
 * Title: Optimizer of the query. Provides query atoms for the engine in
 * particular ordering.
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
public class QueryOptimizer {

	private static final Logger LOG = Logger.getLogger(QueryOptimizer.class.getName());

	public QueryPlan getExecutionPlan(Query query) {
		if (PelletOptions.SAMPLING_RATIO == 0) {
			return new NoReorderingQueryPlan(query);
		}

		if (query.getAtoms().size() > PelletOptions.STATIC_REORDERING_LIMIT) {
			if (LOG.isLoggable( Level.FINE )) {
				LOG.fine("Using incremental query plan.");
			}
			return new IncrementalQueryPlan(query);
		} else {
			if (LOG.isLoggable( Level.FINE )) {
				LOG.fine("Using full query plan.");
			}
			return new CostBasedQueryPlanNew(query);
		}

	}
}
