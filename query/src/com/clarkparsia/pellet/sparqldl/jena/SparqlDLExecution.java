// Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public
// License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of
// proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package com.clarkparsia.pellet.sparqldl.jena;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import java.util.logging.Level;
import java.util.logging.Logger;
import org.mindswap.pellet.KnowledgeBase;
import org.mindswap.pellet.PelletOptions;
import org.mindswap.pellet.exceptions.UnsupportedQueryException;
import org.mindswap.pellet.jena.PelletInfGraph;

import com.clarkparsia.pellet.sparqldl.model.QueryParameters;
import com.clarkparsia.pellet.sparqldl.parser.ARQParser;
import com.hp.hpl.jena.graph.Graph;
import com.hp.hpl.jena.graph.Triple;
import com.hp.hpl.jena.query.Dataset;
import com.hp.hpl.jena.query.DatasetFactory;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryException;
import com.hp.hpl.jena.query.QueryExecException;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.query.SortCondition;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.sparql.engine.binding.Binding;
import com.hp.hpl.jena.sparql.syntax.Template;
import com.hp.hpl.jena.sparql.util.Context;
import com.hp.hpl.jena.sparql.util.ModelUtils;
import com.hp.hpl.jena.util.FileManager;

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
class SparqlDLExecution implements QueryExecution {
	public static Logger	log	= Logger.getLogger( SparqlDLExecution.class.getName() );

	private static enum QueryType {
		ASK, CONSTRUCT, DESCRIBE, SELECT
	}

	private Query			query;

	private Dataset			source;

	private QuerySolution	initialBinding;

	private boolean			purePelletQueryExec	= false;

	private boolean			handleVariableSPO	= true;

	public SparqlDLExecution(String query, Model source) {
		this( QueryFactory.create( query ), source );
	}

	public SparqlDLExecution(Query query, Model source) {
		this( query, DatasetFactory.create( source ) );
	}

	public SparqlDLExecution(Query query, Dataset source) {
		this( query, source, true );
	}

	public SparqlDLExecution(Query query, Dataset source, boolean handleVariableSPO) {
		this.query = query;
		this.source = source;
		this.handleVariableSPO = handleVariableSPO;

		Graph graph = source.getDefaultModel().getGraph();
		if( !(graph instanceof PelletInfGraph) )
			throw new QueryException(
					"PelletQueryExecution can only be used with Pellet-backed models" );

		if( PelletOptions.FULL_SIZE_ESTIMATE )
			((PelletInfGraph) graph).getKB().getSizeEstimate().computeAll();
	}

	/**
	 * {@inheritDoc}
	 */
	public Model execDescribe() {
		throw new UnsupportedOperationException( "Not supported yet!" );
	}

	/**
	 * {@inheritDoc}
	 */
	public Model execDescribe(Model model) {
		throw new UnsupportedOperationException( "Not supported yet!" );
	}

	/**
	 * {@inheritDoc}
	 */
	public Model execConstruct() {
		Model model = ModelFactory.createDefaultModel();

		execConstruct( model );

		return model;
	}

	/**
	 * {@inheritDoc}
	 */
	@SuppressWarnings("unchecked")
	public Model execConstruct(Model model) {
		ensureQueryType( QueryType.CONSTRUCT );

		ResultSet results = exec();

		if( results == null ) {
			QueryExecutionFactory.create( query, source, initialBinding ).execConstruct( model );
		}
		else {
			model.setNsPrefixes( source.getDefaultModel() );
			model.setNsPrefixes( query.getPrefixMapping() );

			Set set = new HashSet();

			Template template = query.getConstructTemplate();

			while( results.hasNext() ) {
				Map bNodeMap = new HashMap();
				Binding binding = results.nextBinding();
				template.subst( set, bNodeMap, binding );
			}

			for( Iterator iter = set.iterator(); iter.hasNext(); ) {
				Triple t = (Triple) iter.next();
				Statement stmt = ModelUtils.tripleToStatement( model, t );
				if( stmt != null )
					model.add( stmt );
			}

			close();
		}

		return model;
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean execAsk() {
		ensureQueryType( QueryType.ASK );

		ResultSet results = exec();

		return (results != null)
			? results.hasNext()
			: QueryExecutionFactory.create( query, source, initialBinding ).execAsk();
	}

	/**
	 * {@inheritDoc}
	 */
	public ResultSet execSelect() {
		ensureQueryType( QueryType.SELECT );

		ResultSet results = exec();

		return (results != null)
			? results
			: QueryExecutionFactory.create( query, source, initialBinding ).execSelect();

	}

	/**
	 * Returns the results of the given query using Pellet SPARQL-DL query
	 * engine or <code>null</code> if the query is not a valid SPARQL-DL
	 * query.
	 * 
	 * @return the query results or <code>null</code> for unsupported queried
	 */
	private ResultSet exec() {
		try {
			if( source.listNames().hasNext() )
				throw new UnsupportedQueryException( "Named graphs is not supported by Pellet" );

			PelletInfGraph pelletInfGraph = (PelletInfGraph) source.getDefaultModel().getGraph();
			KnowledgeBase kb = pelletInfGraph.getKB();

			pelletInfGraph.prepare();

			QueryParameters queryParameters = new QueryParameters( initialBinding );

			ARQParser parser = new ARQParser( handleVariableSPO );
			// The parser uses the query parameterization to resolve parameters
			// (i.e. variables) in the query
			parser.setInitialBinding( initialBinding );

			com.clarkparsia.pellet.sparqldl.model.Query q = parser.parse( query, kb );
			// The query uses the query parameterization to resolve bindings
			// (i.e. for instance if the parameter variable is in query
			// projection, we need to add the initial binding to the resulting
			// bindings manually)
			q.setQueryParameters( queryParameters );

			ResultSet results = new SparqlDLResultSet(
					com.clarkparsia.pellet.sparqldl.engine.QueryEngine.exec( q ), source
							.getDefaultModel(), queryParameters );

			List<SortCondition> sortConditions = query.getOrderBy();
			if( sortConditions != null && !sortConditions.isEmpty() ) {
				results = new SortedResultSet( results, sortConditions );
			}

			if( query.hasOffset() || query.hasLimit() ) {
				long offset = query.hasOffset()
					? query.getOffset()
					: 0;
				long limit = query.hasLimit()
					? query.getLimit()
					: Long.MAX_VALUE;
				results = new SlicedResultSet( results, offset, limit );
			}

			return results;
		} catch( UnsupportedQueryException e ) {
			log.log( purePelletQueryExec
				? Level.INFO
				: Level.FINE, "This is not a SPARQL-DL query: " + e.getMessage() );

			if( purePelletQueryExec ) {
				throw e;
			}
			else {
				log.fine( "Falling back to Jena query engine" );
				return null;
			}
		}
	}

	public void abort() {
		throw new UnsupportedOperationException( "Not supported yet!" );
	}

	/**
	 * {@inheritDoc}
	 */
	public void close() {
		log.fine( "Closing PelletQueryExecution '" + hashCode() + "'." );
	}

	public void setFileManager(FileManager manager) {
		throw new UnsupportedOperationException( "Not supported yet!" );
	}

	public void setInitialBinding(QuerySolution startSolution) {
		initialBinding = startSolution;
	}

	public Context getContext() {
		throw new UnsupportedOperationException( "Not supported yet!" );
	}

	/**
	 * {@inheritDoc}
	 */
	public Dataset getDataset() {
		throw new UnsupportedOperationException( "Not supported yet!" );
		// return source;
	}

	private void ensureQueryType(QueryType expectedType) throws QueryExecException {
		QueryType actualType = getQueryType( query );
		if( actualType != expectedType )
			throw new QueryExecException( "Attempt to execute a " + actualType + " query as a "
					+ expectedType + " query" );
	}

	private static QueryType getQueryType(Query query) {
		if( query.isSelectType() )
			return QueryType.SELECT;
		if( query.isConstructType() )
			return QueryType.CONSTRUCT;
		if( query.isDescribeType() )
			return QueryType.DESCRIBE;
		if( query.isAskType() )
			return QueryType.ASK;
		return null;
	}

	public boolean isPurePelletQueryExec() {
		return purePelletQueryExec;
	}

	public void setPurePelletQueryExec(boolean purePelletQueryExec) {
		this.purePelletQueryExec = purePelletQueryExec;
	}
}
