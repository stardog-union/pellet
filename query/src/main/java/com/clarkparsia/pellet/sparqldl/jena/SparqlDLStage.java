// Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public
// License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of
// proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package com.clarkparsia.pellet.sparqldl.jena;

import aterm.ATermAppl;
import com.clarkparsia.pellet.sparqldl.engine.QueryEngine;
import com.clarkparsia.pellet.sparqldl.model.Query;
import com.clarkparsia.pellet.sparqldl.model.QueryResult;
import com.clarkparsia.pellet.sparqldl.model.ResultBinding;
import com.clarkparsia.pellet.sparqldl.model.ResultBindingImpl;
import com.clarkparsia.pellet.sparqldl.parser.ARQParser;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.katk.tools.Log;
import org.apache.jena.graph.Graph;
import org.apache.jena.graph.Node;
import org.apache.jena.graph.Triple;
import org.apache.jena.sparql.core.BasicPattern;
import org.apache.jena.sparql.core.Var;
import org.apache.jena.sparql.engine.ExecutionContext;
import org.apache.jena.sparql.engine.QueryIterator;
import org.apache.jena.sparql.engine.binding.Binding;
import org.apache.jena.sparql.engine.iterator.QueryIterRepeatApply;
import org.apache.jena.sparql.engine.iterator.QueryIteratorResultSet;
import org.apache.jena.sparql.engine.main.StageGeneratorGeneric;
import org.mindswap.pellet.exceptions.UnsupportedQueryException;
import org.mindswap.pellet.jena.PelletInfGraph;
import org.mindswap.pellet.jena.graph.loader.GraphLoader;
import org.mindswap.pellet.utils.ATermUtils;

/**
 * <p>
 * Title:
 * </p>
 * <p>
 * Description: An implementation of ARQ query stage for PelletInfGraph. The {@link BasicPattern} is converted into a native Pellet SPARQL-DL query and answered
 * by the Pellet query engine. The conversion to Pellet query might fail if the _pattern is not a SPARQL-DL query in which case the default ARQ handler is used.
 * </p>
 * <p>
 * Copyright: Copyright (c) 2007
 * </p>
 * <p>
 * Company: Clark & Parsia, LLC. <http://www.clarkparsia.com>
 * </p>
 *
 * @author Evren Sirin
 */
class SparqlDLStage
{
	public final static Logger log = Log.getLogger(SparqlDLStage.class);

	private final ARQParser _parser;

	private final BasicPattern _pattern;
	private Collection<String> _vars;

	public SparqlDLStage(final BasicPattern pattern)
	{
		this(pattern, true);
	}

	public SparqlDLStage(final BasicPattern pattern, final boolean handleVariableSPO)
	{
		this._pattern = pattern;
		this._parser = new ARQParser(handleVariableSPO);

		initVars();
	}

	private void initVars()
	{
		_vars = new LinkedHashSet<>();
		for (int i = 0; i < _pattern.size(); i++)
		{
			final Triple t = _pattern.get(i);

			if (ARQParser.isDistinguishedVariable(t.getSubject()))
				_vars.add(t.getSubject().getName());
			if (t.getPredicate().isVariable())
				_vars.add(t.getPredicate().getName());
			if (ARQParser.isDistinguishedVariable(t.getObject()))
				_vars.add(t.getObject().getName());
		}
	}

	public QueryIterator build(final QueryIterator input, final ExecutionContext execCxt)
	{
		final Graph graph = execCxt.getActiveGraph();
		if (!(graph instanceof PelletInfGraph))
			throw new UnsupportedOperationException("A Pellet-backed model is required");

		final PelletInfGraph pellet = (PelletInfGraph) graph;

		pellet.prepare();

		final Query query = parsePattern(pellet);

		if (query != null)
			return new PelletQueryIterator(pellet, query, input, execCxt);
		else
			return new StageGeneratorGeneric().execute(_pattern, input, execCxt);
	}

	private Query parsePattern(final PelletInfGraph pellet)
	{
		try
		{
			return _parser.parse(_pattern, _vars, pellet.getKB(), false);
		}
		catch (final UnsupportedQueryException e)
		{
			if (log.isLoggable(Level.FINE))
				log.log(Level.FINE, "Falling back to Jena stage", e);

			return null;
		}
	}

	private static class PelletQueryIterator extends QueryIterRepeatApply
	{
		private final PelletInfGraph pellet;
		private final Query query;

		/**
		 * @param input
		 * @param context
		 */
		public PelletQueryIterator(final PelletInfGraph pellet, final Query query, final QueryIterator input, final ExecutionContext execCxt)
		{
			super(input, execCxt);

			this.pellet = pellet;
			this.query = query;
		}

		private ResultBinding convertBinding(final Binding binding)
		{
			final ResultBinding pelletBinding = new ResultBindingImpl();
			final GraphLoader loader = pellet.getLoader();
			for (final Iterator<?> vars = binding.vars(); vars.hasNext();)
			{
				final Var var = (Var) vars.next();
				final Node value = binding.get(var);
				if (value != null)
				{
					final ATermAppl pelletVar = ATermUtils.makeVar(var.getVarName());
					final ATermAppl pelletValue = loader.node2term(value);
					pelletBinding.setValue(pelletVar, pelletValue);
				}
			}

			return pelletBinding;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		protected QueryIterator nextStage(final Binding binding)
		{
			final Query newQuery = query.apply(convertBinding(binding));

			final QueryResult results = QueryEngine.exec(newQuery);

			final SparqlDLResultSet resultSet = new SparqlDLResultSet(results, null, binding);

			final QueryIteratorResultSet iter = new QueryIteratorResultSet(resultSet);

			return iter;
		}
	}
}
