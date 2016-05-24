// Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public
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
import net.katk.tools.Log;
import org.apache.jena.atlas.lib.NotImplemented;
import org.apache.jena.graph.Graph;
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
	public static Logger log = Log.getLogger(SparqlDLExecution.class);

	private static enum QueryType
	{
		ASK, CONSTRUCT, DESCRIBE, SELECT
	}

	private final Query query;

	private final Dataset source;

	private QuerySolution initialBinding;

	private boolean purePelletQueryExec = false;

	private boolean handleVariableSPO = true;

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
		this.query = query;
		this.source = source;
		this.handleVariableSPO = handleVariableSPO;

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
	@SuppressWarnings("unchecked")
	@Override
	public Model execConstruct(final Model model)
	{
		ensureQueryType(QueryType.CONSTRUCT);

		final ResultSet results = exec();

		if (results == null)
			QueryExecutionFactory.create(query, source, initialBinding).execConstruct(model);
		else
		{
			model.setNsPrefixes(source.getDefaultModel());
			model.setNsPrefixes(query.getPrefixMapping());

			final Set set = new HashSet();

			final Template template = query.getConstructTemplate();

			while (results.hasNext())
			{
				final Map bNodeMap = new HashMap();
				final Binding binding = results.nextBinding();
				template.subst(set, bNodeMap, binding);
			}

			for (final Iterator iter = set.iterator(); iter.hasNext();)
			{
				final Triple t = (Triple) iter.next();
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

		return (results != null) ? results.hasNext() : QueryExecutionFactory.create(query, source, initialBinding).execAsk();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ResultSet execSelect()
	{
		ensureQueryType(QueryType.SELECT);

		final ResultSet results = exec();

		return (results != null) ? results : QueryExecutionFactory.create(query, source, initialBinding).execSelect();

	}

	/**
	 * Returns the results of the given query using Pellet SPARQL-DL query engine or <code>null</code> if the query is not a valid SPARQL-DL query.
	 *
	 * @return the query results or <code>null</code> for unsupported queried
	 */
	private ResultSet exec()
	{
		try
		{
			if (source.listNames().hasNext())
				throw new UnsupportedQueryException("Named graphs is not supported by Pellet");

			final PelletInfGraph pelletInfGraph = (PelletInfGraph) source.getDefaultModel().getGraph();
			final KnowledgeBase kb = pelletInfGraph.getKB();

			pelletInfGraph.prepare();

			final QueryParameters queryParameters = new QueryParameters(initialBinding);

			final ARQParser parser = new ARQParser(handleVariableSPO);
			// The parser uses the query parameterization to resolve parameters
			// (i.e. variables) in the query
			parser.setInitialBinding(initialBinding);

			final com.clarkparsia.pellet.sparqldl.model.Query q = parser.parse(query, kb);
			// The query uses the query parameterization to resolve bindings
			// (i.e. for instance if the parameter variable is in query
			// projection, we need to add the initial binding to the resulting
			// bindings manually)
			q.setQueryParameters(queryParameters);

			ResultSet results = new SparqlDLResultSet(com.clarkparsia.pellet.sparqldl.engine.QueryEngine.exec(q), source.getDefaultModel(), queryParameters);

			final List<SortCondition> sortConditions = query.getOrderBy();
			if (sortConditions != null && !sortConditions.isEmpty())
				results = new SortedResultSet(results, sortConditions);

			if (query.hasOffset() || query.hasLimit())
			{
				final long offset = query.hasOffset() ? query.getOffset() : 0;
				final long limit = query.hasLimit() ? query.getLimit() : Long.MAX_VALUE;
				results = new SlicedResultSet(results, offset, limit);
			}

			return results;
		}
		catch (final UnsupportedQueryException e)
		{
			log.log(purePelletQueryExec ? Level.INFO : Level.FINE, "This is not a SPARQL-DL query: " + e.getMessage());

			if (purePelletQueryExec)
				throw e;
			else
			{
				log.fine("Falling back to Jena query engine");
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
		log.fine("Closing PelletQueryExecution '" + hashCode() + "'.");
	}

	@Override
	public void setInitialBinding(final QuerySolution startSolution)
	{
		initialBinding = startSolution;
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
		// return source;
	}

	private void ensureQueryType(final QueryType expectedType) throws QueryExecException
	{
		final QueryType actualType = getQueryType(query);
		if (actualType != expectedType)
			throw new QueryExecException("Attempt to execute a " + actualType + " query as a " + expectedType + " query");
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
		return purePelletQueryExec;
	}

	public void setPurePelletQueryExec(final boolean purePelletQueryExec)
	{
		this.purePelletQueryExec = purePelletQueryExec;
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
		return query;
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
