// Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This _source code is available under the terms of the Affero General Public
// License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of
// proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package com.clarkparsia.pellet.sparqldl.jena;

import com.clarkparsia.pellet.sparqldl.model.QueryParameters;
import com.clarkparsia.pellet.sparqldl.parser.ARQParser;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import openllet.shared.tools.Log;
import org.apache.jena.atlas.lib.NotImplemented;
import org.apache.jena.graph.Graph;
import org.apache.jena.graph.Node;
import org.apache.jena.graph.Triple;
import org.apache.jena.query.Dataset;
import org.apache.jena.query.DatasetFactory;
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryException;
import org.apache.jena.query.QueryExecException;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.query.SortCondition;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.sparql.core.Quad;
import org.apache.jena.sparql.engine.binding.Binding;
import org.apache.jena.sparql.syntax.Template;
import org.apache.jena.sparql.util.Context;
import org.apache.jena.sparql.util.ModelUtils;
import org.mindswap.pellet.KnowledgeBase;
import org.mindswap.pellet.PelletOptions;
import org.mindswap.pellet.exceptions.UnsupportedQueryException;
import org.mindswap.pellet.jena.PelletInfGraph;

/**
 * <p>
 * Title:
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
 * @author Evren Sirin
 */
class SparqlDLExecution implements QueryExecution
{
	public static Logger _logger = Log.getLogger(SparqlDLExecution.class);

	private static enum QueryType
	{
		ASK, CONSTRUCT, DESCRIBE, SELECT
	}

	private final Query _query;

	private final Dataset _source;

	private QuerySolution _initialBinding;

	private boolean _purePelletQueryExec = false;

	private boolean _handleVariableSPO = true;

	public SparqlDLExecution(final String query, final Model source)
	{
		this(QueryFactory.create(query), source);
	}

	public SparqlDLExecution(final Query query, final Model source)
	{
		this(query, DatasetFactory.create(source));
	}

	public SparqlDLExecution(final Query query, final Dataset source)
	{
		this(query, source, true);
	}

	public SparqlDLExecution(final Query query, final Dataset source, final boolean handleVariableSPO)
	{
		this._query = query;
		this._source = source;
		this._handleVariableSPO = handleVariableSPO;

		final Graph graph = source.getDefaultModel().getGraph();
		if (!(graph instanceof PelletInfGraph))
			throw new QueryException("PelletQueryExecution can only be used with Pellet-backed models");

		if (PelletOptions.FULL_SIZE_ESTIMATE)
			((PelletInfGraph) graph).getKB().getSizeEstimate().computeAll();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Model execDescribe()
	{
		throw new UnsupportedOperationException("Not supported yet!");
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Model execDescribe(final Model model)
	{
		throw new UnsupportedOperationException("Not supported yet!");
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Model execConstruct()
	{
		final Model model = ModelFactory.createDefaultModel();

		execConstruct(model);

		return model;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Model execConstruct(final Model model)
	{
		ensureQueryType(QueryType.CONSTRUCT);

		final ResultSet results = exec();

		if (results == null)
			QueryExecutionFactory.create(_query, _source, _initialBinding).execConstruct(model);
		else
		{
			model.setNsPrefixes(_source.getDefaultModel());
			model.setNsPrefixes(_query.getPrefixMapping());

			final Set<Triple> set = new HashSet<>();

			final Template template = _query.getConstructTemplate();

			while (results.hasNext())
			{
				final Map<Node, Node> bNodeMap = new HashMap<>();
				final Binding binding = results.nextBinding();
				template.subst(set, bNodeMap, binding);
			}

			for (final Iterator<Triple> iter = set.iterator(); iter.hasNext();)
			{
				final Triple t = iter.next();
				final Statement stmt = ModelUtils.tripleToStatement(model, t);
				if (stmt != null)
					model.add(stmt);
			}

			close();
		}

		return model;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean execAsk()
	{
		ensureQueryType(QueryType.ASK);

		final ResultSet results = exec();

		return (results != null) ? results.hasNext() : QueryExecutionFactory.create(_query, _source, _initialBinding).execAsk();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ResultSet execSelect()
	{
		ensureQueryType(QueryType.SELECT);

		final ResultSet results = exec();

		return (results != null) ? results : QueryExecutionFactory.create(_query, _source, _initialBinding).execSelect();

	}

	/**
	 * Returns the results of the given _query using Pellet SPARQL-DL _query engine or <code>null</code> if the _query is not a valid SPARQL-DL _query.
	 *
	 * @return the _query results or <code>null</code> for unsupported queried
	 */
	private ResultSet exec()
	{
		try
		{
			if (_source.listNames().hasNext())
				throw new UnsupportedQueryException("Named graphs is not supported by Pellet");

			final PelletInfGraph pelletInfGraph = (PelletInfGraph) _source.getDefaultModel().getGraph();
			final KnowledgeBase kb = pelletInfGraph.getKB();

			pelletInfGraph.prepare();

			final QueryParameters queryParameters = new QueryParameters(_initialBinding);

			final ARQParser parser = new ARQParser(_handleVariableSPO);
			// The parser uses the _query parameterization to resolve parameters
			// (i.e. variables) in the _query
			parser.setInitialBinding(_initialBinding);

			final com.clarkparsia.pellet.sparqldl.model.Query q = parser.parse(_query, kb);
			// The _query uses the _query parameterization to resolve bindings
			// (i.e. for instance if the parameter variable is in _query
			// projection, we need to add the initial binding to the resulting
			// bindings manually)
			q.setQueryParameters(queryParameters);

			ResultSet results = new SparqlDLResultSet(com.clarkparsia.pellet.sparqldl.engine.QueryEngine.exec(q), _source.getDefaultModel(), queryParameters);

			final List<SortCondition> sortConditions = _query.getOrderBy();
			if (sortConditions != null && !sortConditions.isEmpty())
				results = new SortedResultSet(results, sortConditions);

			if (_query.hasOffset() || _query.hasLimit())
			{
				final long offset = _query.hasOffset() ? _query.getOffset() : 0;
				final long limit = _query.hasLimit() ? _query.getLimit() : Long.MAX_VALUE;
				results = new SlicedResultSet(results, offset, limit);
			}

			return results;
		}
		catch (final UnsupportedQueryException e)
		{
			_logger.log(_purePelletQueryExec ? Level.INFO : Level.FINE, "This is not a SPARQL-DL _query: " + e.getMessage());

			if (_purePelletQueryExec)
				throw e;
			else
			{
				_logger.fine("Falling back to Jena _query engine");
				return null;
			}
		}
	}

	@Override
	public void abort()
	{
		throw new UnsupportedOperationException("Not supported yet!");
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void close()
	{
		_logger.fine("Closing PelletQueryExecution '" + hashCode() + "'.");
	}

	@Override
	public void setInitialBinding(final QuerySolution startSolution)
	{
		_initialBinding = startSolution;
	}

	@Override
	public Context getContext()
	{
		throw new UnsupportedOperationException("Not supported yet!");
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Dataset getDataset()
	{
		throw new UnsupportedOperationException("Not supported yet!");
		// return _source;
	}

	private void ensureQueryType(final QueryType expectedType) throws QueryExecException
	{
		final QueryType actualType = getQueryType(_query);
		if (actualType != expectedType)
			throw new QueryExecException("Attempt to execute a " + actualType + " _query as a " + expectedType + " _query");
	}

	private static QueryType getQueryType(final Query query)
	{
		if (query.isSelectType())
			return QueryType.SELECT;
		if (query.isConstructType())
			return QueryType.CONSTRUCT;
		if (query.isDescribeType())
			return QueryType.DESCRIBE;
		if (query.isAskType())
			return QueryType.ASK;
		return null;
	}

	public boolean isPurePelletQueryExec()
	{
		return _purePelletQueryExec;
	}

	public void setPurePelletQueryExec(final boolean purePelletQueryExec)
	{
		this._purePelletQueryExec = purePelletQueryExec;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Iterator<Triple> execConstructTriples()
	{
		return ModelUtils.statementsToTriples(execConstruct().listStatements());
	}

	@Override
	public Iterator<Quad> execConstructQuads()
	{
		throw new NotImplemented();
	}

	@Override
	public Dataset execConstructDataset()
	{
		throw new NotImplemented();
	}

	@Override
	public Dataset execConstructDataset(final Dataset dataset)
	{
		throw new NotImplemented();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Iterator<Triple> execDescribeTriples()
	{
		return ModelUtils.statementsToTriples(execDescribe().listStatements());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Query getQuery()
	{
		return _query;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public long getTimeout1()
	{
		// not supported yet
		return -1;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public long getTimeout2()
	{
		// not supported yet
		return -1;

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setTimeout(final long arg0)
	{
		// not supported yet
	}

	/**
	 * {@inheritDoc}
	 */

	@Override
	public void setTimeout(final long arg0, final TimeUnit arg1)
	{
		// not supported yet
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setTimeout(final long arg0, final long arg1)
	{
		// not supported yet
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setTimeout(final long arg0, final TimeUnit arg1, final long arg2, final TimeUnit arg3)
	{
		// not supported yet
	}

	@Override
	public boolean isClosed()
	{
		return false;
	}
}
