// Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package com.clarkparsia.pellet.sparqldl.engine;

import aterm.ATermAppl;
import com.clarkparsia.pellet.sparqldl.model.Query;
import com.clarkparsia.pellet.sparqldl.model.QueryAtom;
import com.clarkparsia.pellet.sparqldl.model.QueryPredicate;
import com.clarkparsia.pellet.sparqldl.model.QueryResult;
import com.clarkparsia.pellet.sparqldl.model.QueryResultImpl;
import com.clarkparsia.pellet.sparqldl.model.ResultBinding;
import com.clarkparsia.pellet.sparqldl.model.ResultBindingImpl;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.katk.tools.Log;
import org.mindswap.pellet.KnowledgeBase;
import org.mindswap.pellet.utils.ATermUtils;

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
public class OptimizedQueryEngine2 extends AbstractABoxEngineWrapper
{
	public static final Logger _logger = Log.getLogger(QueryEngine.class);

	private QueryResult _results;

	private KnowledgeBase _kb;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean supports(final Query q)
	{
		return !q.getDistVars().isEmpty();
	}

	private void exec(final Query q, final ResultBinding binding, final boolean first)
	{
		if (q.getDistVars().isEmpty())
		{
			_results.add(binding);
			return;
		}

		final Iterator<ATermAppl> i = q.getDistVars().iterator();

		final ATermAppl var = i.next();

		final Collection<ATermAppl> empty = Collections.emptySet();
		final ATermAppl clazz = q.rollUpTo(var, empty, false);

		if (_logger.isLoggable(Level.FINE))
			_logger.fine("Rolling up " + var + " to " + clazz);

		final Collection<ATermAppl> instances;

		if (first)
		{
			instances = new HashSet<>(_kb.getIndividuals());
			for (final QueryAtom atom : q.findAtoms(QueryPredicate.PropertyValue, var, null, null))
				instances.retainAll(_kb.retrieveIndividualsWithProperty(atom.getArguments().get(1)));

			for (final QueryAtom atom : q.findAtoms(QueryPredicate.PropertyValue, null, null, var))
				instances.retainAll(_kb.retrieveIndividualsWithProperty(ATermUtils.makeInv(atom.getArguments().get(1))));
		}
		else
			instances = _kb.getInstances(clazz);

		for (final ATermAppl b : instances)
		{
			if (_logger.isLoggable(Level.FINE))
				_logger.fine("trying " + var + " --> " + b);
			final ResultBinding newBinding = binding.duplicate();

			newBinding.setValue(var, b);
			final Query q2 = q.apply(newBinding);
			exec(q2, newBinding, false);
		}
	}

	@Override
	public QueryResult execABoxQuery(final Query q)
	{
		_results = new QueryResultImpl(q);

		this._kb = q.getKB();

		final long satCount = _kb.getABox().stats.satisfiabilityCount;
		final long consCount = _kb.getABox().stats.consistencyCount;

		exec(q, new ResultBindingImpl(), true);

		if (_logger.isLoggable(Level.FINE))
		{
			_logger.fine("Total satisfiability operations: " + (_kb.getABox().stats.satisfiabilityCount - satCount));
			_logger.fine("Total consistency operations: " + (_kb.getABox().stats.consistencyCount - consCount));
			_logger.fine("Results of ABox query : " + _results);
		}

		return _results;
	}
}
