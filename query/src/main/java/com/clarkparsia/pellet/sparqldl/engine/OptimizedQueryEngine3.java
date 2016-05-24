// Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package com.clarkparsia.pellet.sparqldl.engine;

import aterm.ATermAppl;
import com.clarkparsia.pellet.sparqldl.model.Query;
import com.clarkparsia.pellet.sparqldl.model.Query.VarType;
import com.clarkparsia.pellet.sparqldl.model.QueryResult;
import com.clarkparsia.pellet.sparqldl.model.QueryResultImpl;
import com.clarkparsia.pellet.sparqldl.model.ResultBinding;
import com.clarkparsia.pellet.sparqldl.model.ResultBindingImpl;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.katk.tools.Log;
import org.mindswap.pellet.KnowledgeBase;

/**
 * <p>
 * Title: SimpleQueryEngine
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
public class OptimizedQueryEngine3 extends AbstractABoxEngineWrapper
{
	public static final Logger log = Log.getLogger(QueryEngine.class);

	@Override
	public boolean supports(final Query q)
	{
		return true; // TODO
	}

	@Override
	public QueryResult execABoxQuery(final Query q)
	{
		final QueryResult results = new QueryResultImpl(q);
		final KnowledgeBase kb = q.getKB();

		final long satCount = kb.getABox().stats.satisfiabilityCount;
		final long consCount = kb.getABox().stats.consistencyCount;

		if (q.getDistVars().isEmpty())
		{
			if (QueryEngine.execBooleanABoxQuery(q))
				results.add(new ResultBindingImpl());
		}
		else
		{
			final Map<ATermAppl, Set<ATermAppl>> varBindings = new HashMap<>();

			for (final ATermAppl currVar : q.getDistVarsForType(VarType.INDIVIDUAL))
			{
				final ATermAppl rolledUpClass = q.rollUpTo(currVar, Collections.EMPTY_SET, false);

				if (log.isLoggable(Level.FINER))
					log.finer("Rolled up class " + rolledUpClass);
				varBindings.put(currVar, kb.getInstances(rolledUpClass));
			}

			if (log.isLoggable(Level.FINER))
				log.finer("Var bindings: " + varBindings);

			final List<ATermAppl> varList = new ArrayList<>(varBindings.keySet()); // TODO

			final Map<ATermAppl, Collection<ResultBinding>> goodLists = new HashMap<>();

			final ATermAppl first = varList.get(0);
			final Collection<ResultBinding> c = new HashSet<>();

			for (final ATermAppl a : varBindings.get(first))
			{
				final ResultBinding bind = new ResultBindingImpl();
				bind.setValue(first, a);
				c.add(bind);
			}

			goodLists.put(first, c);

			Collection<ResultBinding> previous = goodLists.get(first);
			for (int i = 1; i < varList.size(); i++)
			{
				final ATermAppl next = varList.get(i);

				final Collection<ResultBinding> newBindings = new HashSet<>();

				for (final ResultBinding binding : previous)
					for (final ATermAppl testBind : varBindings.get(next))
					{
						final ResultBinding bindingCandidate = binding.duplicate();

						bindingCandidate.setValue(next, testBind);

						final boolean queryTrue = QueryEngine.execBooleanABoxQuery(q.apply(bindingCandidate));
						if (queryTrue)
						{
							newBindings.add(bindingCandidate);
							if (log.isLoggable(Level.FINER))
								log.finer("Accepted binding: " + bindingCandidate);
						}
						else
							if (log.isLoggable(Level.FINER))
								log.finer("Rejected binding: " + bindingCandidate);
					}

				previous = newBindings;
			}

			// no var. should be marked as both INDIVIDUAL and LITERAL in an
			// ABox query.
			final boolean hasLiterals = !q.getDistVarsForType(VarType.LITERAL).isEmpty();

			if (hasLiterals)
				for (final ResultBinding b : previous)
					for (final Iterator<ResultBinding> i = new LiteralIterator(q, b); i.hasNext();)
						results.add(i.next());
			else
				for (final ResultBinding b : previous)
					results.add(b);
			if (log.isLoggable(Level.FINE))
			{
				log.fine("Results: " + results);
				log.fine("Total satisfiability operations: " + (kb.getABox().stats.satisfiabilityCount - satCount));
				log.fine("Total consistency operations: " + (kb.getABox().stats.consistencyCount - consCount));
			}
		}
		return results;
	}
}
