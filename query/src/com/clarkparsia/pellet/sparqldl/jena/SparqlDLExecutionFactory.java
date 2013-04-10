// Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public
// License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of
// proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package com.clarkparsia.pellet.sparqldl.jena;

import org.mindswap.pellet.KnowledgeBase;
import org.mindswap.pellet.exceptions.UnsupportedQueryException;
import org.mindswap.pellet.jena.PelletInfGraph;

import com.clarkparsia.pellet.sparqldl.parser.ARQParser;
import com.hp.hpl.jena.graph.Graph;
import com.hp.hpl.jena.query.ARQ;
import com.hp.hpl.jena.query.Dataset;
import com.hp.hpl.jena.query.DatasetFactory;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryException;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.rdf.model.Model;

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
public class SparqlDLExecutionFactory {
	/**
	 * Different types of query engine that can be used for answering queries.
	 */
	public enum QueryEngineType {
		/**
		 * This is the standard ARQ query engine where all the query answering
		 * bits are handled by ARQ and Jena and the underlying Pellet model is
		 * queried with single triple patterns. For this reason, this query
		 * engine cannot handle complex class expressions in the query.
		 */
		ARQ,

		/**
		 * The mixed query engine uses ARQ to handle SPARQL algebra and the
		 * Pellet query engine is used to answer Basic Graph Patterns (BGPs).
		 * Unlike pure Pellet engine, this engine can answer any kind of SPARQL
		 * query however it might be slightly slower for queries supported by
		 * Pellet engine. On the other hand, this engine typically performs
		 * better than the ARQ engine since answering a BGP as a whole is faster
		 * than answering each triple in isolation.
		 */
		MIXED,

		/**
		 * This is the specialized Pellet query engine that will answer
		 * SPARQL-DL queries. This is the most efficient query engine to answer
		 * SPARQL queries with Pellet. However, some queries are not supported
		 * by this query engine and will cause QueryException to be thrown.
		 * Unsupported features are:
		 * <ul>
		 * <li>DESCRIBE queries</li>
		 * <li>Named graphs in queries</li>
		 * <li>OPTIONAL and UNION operators</li>
		 * </ul>
		 * All other SPARQL operators and query forms are supported.
		 */
		PELLET
	}

	/**
	 * Creates a QueryExecution by selecting the most appropriate
	 * {@link QueryEngineType} that can answer the given query.
	 * 
	 * @see QueryEngineType
	 * @param query
	 *            the query
	 * @param dataset
	 *            the target of the query
	 * @param initialBinding
	 *            initial binding that will be applied before query execution or
	 *            <code>null</code> if there is no initial binding
	 * @return a <code>QueryExecution</code> that will answer the query with the
	 *         given dataset
	 */
	public static QueryExecution create(Query query, Dataset dataset, QuerySolution initialBinding) {
		QueryEngineType engineType = QueryEngineType.ARQ;

		Graph graph = dataset.getDefaultModel().getGraph();
		// if underlying model is not Pellet then we'll use ARQ
		if( graph instanceof PelletInfGraph ) {
			// check for obvious things not supported by Pellet
			if( dataset.listNames().hasNext() || query.isDescribeType() ) {
				engineType = QueryEngineType.MIXED;
			}
			else {
				// try parsing the query and see if there are any problems
				PelletInfGraph pelletInfGraph = (PelletInfGraph) graph;

				KnowledgeBase kb = pelletInfGraph.getKB();
				pelletInfGraph.prepare();

				ARQParser parser = new ARQParser();
				// The parser uses the query parameterization to resolve
				// parameters
				// (i.e. variables) in the query
				parser.setInitialBinding( initialBinding );

				try {
					parser.parse( query, kb );
					// parsing successful so we can use Pellet engine
					engineType = QueryEngineType.PELLET;
				} catch( UnsupportedQueryException e ) {
					// parsing failed so we will use the mixed engine
					engineType = QueryEngineType.MIXED;
				}
			}
		}

		return create( query, dataset, initialBinding, engineType );
	}

	/**
	 * Creates a QueryExecution with the given {@link QueryEngineType}. If the
	 * query engine cannot handle the given query a QueryException may be thrown
	 * during query execution. Users are recommended to use
	 * {@link #create(Query, Dataset, QuerySolution)}
	 * 
	 * @param query
	 *            the query
	 * @param dataset
	 *            the target of the query
	 * @param initialBinding
	 *            used for parametrized queries
	 * @param queryEngineType
	 *            type of the query engine that will be used to answer the query
	 * @return a <code>QueryExecution</code> that will answer the query with the
	 *         given dataset
	 */
	public static QueryExecution create(Query query, Dataset dataset, QuerySolution initialBinding,
			QueryEngineType queryEngineType) {
		return create( query, dataset, initialBinding, queryEngineType, true );
	}

	/**
	 * Creates a QueryExecution with the given {@link QueryEngineType}. If the
	 * query engine cannot handle the given query a QueryException may be thrown
	 * during query execution. Users are recommended to use
	 * {@link #create(Query, Dataset, QuerySolution)}
	 * 
	 * @param query
	 *            the query
	 * @param dataset
	 *            the target of the query
	 * @param initialBinding
	 *            used for parametrized queries
	 * @param queryEngineType
	 *            type of the query engine that will be used to answer the query
	 * @param handleVariableSPO
	 *            If this variable is true then queries with variable SPO
	 *            statements are not handled by the SPARQL-DL engine but fall
	 *            back to ARQ
	 * @return a <code>QueryExecution</code> that will answer the query with the
	 *         given dataset
	 */
	public static QueryExecution create(Query query, Dataset dataset, QuerySolution initialBinding,
			QueryEngineType queryEngineType, boolean handleVariableSPO) throws QueryException {
		// the engine we will return
		QueryExecution queryExec = null;

		// create an engine based on the type
		switch ( queryEngineType ) {
		case PELLET:
			queryExec = new SparqlDLExecution( query, dataset, handleVariableSPO );
			((SparqlDLExecution) queryExec).setPurePelletQueryExec( true );
			break;
		case ARQ:
			queryExec = QueryExecutionFactory.create( query, dataset );
			break;
		case MIXED:
			queryExec = QueryExecutionFactory.create( query, dataset );
			queryExec.getContext().set( ARQ.stageGenerator,
					new SparqlDLStageGenerator( handleVariableSPO ) );
			break;
		default:
			throw new AssertionError();
		}

		// if given set the initial binding
		if( initialBinding != null )
			queryExec.setInitialBinding( initialBinding );

		// return it
		return queryExec;
	}

	/**
	 * Creates a QueryExecution object where the Basic Graph Patterns (BGPs)
	 * will be answered by native Pellet query engine whenever possible. The
	 * unsupported BGPs, i.e. the ones that are not in the SPARQL-DL fragment,
	 * will be answered by the Jena query engine. With this fall-back model all
	 * types of SPARQL queries are supported.
	 * 
	 * @param query
	 *            the query
	 * @param model
	 *            the target of the query
	 * @return a <code>QueryExecution</code> that will answer the query with the
	 *         given model
	 * @throws QueryException
	 *             if the given model is not associated with Pellet reasoner
	 */
	public static QueryExecution create(Query query, Model model) {
		return create( query, model, null );
	}

	/**
	 * Creates a QueryExecution by selecting the most appropriate
	 * {@link QueryEngineType} that can answer the given query.
	 * 
	 * @see QueryEngineType
	 * @param query
	 *            the query
	 * @param dataset
	 *            the target of the query
	 * @return a <code>QueryExecution</code> that will answer the query with the
	 *         given dataset
	 */
	public static QueryExecution create(Query query, Dataset dataset) {
		return create( query, dataset, null );
	}

	/**
	 * Creates a QueryExecution by selecting the most appropriate
	 * {@link QueryEngineType} that can answer the given query.
	 * 
	 * @see QueryEngineType
	 * @param query
	 *            the query
	 * @param model
	 *            the target of the query
	 * @param initialBinding
	 *            initial binding that will be applied before query execution or
	 *            <code>null</code> if there is no initial binding
	 * @return a <code>QueryExecution</code> that will answer the query with the
	 *         given dataset
	 */
	public static QueryExecution create(Query query, Model model, QuerySolution initialBinding) {
		return create( query, DatasetFactory.create( model ), initialBinding );
	}

	/**
	 * Creates a Pellet query engine that will answer the given query. This
	 * function should be used only if it is known that Pellet query engine can
	 * handle the given query. Otherwise query execution will result in an
	 * exception. for arbitrary queries it is safer to use the
	 * <code>create</code> functions.
	 * 
	 * @see QueryEngineType
	 * @param query
	 *            the query
	 * @param model
	 *            the target of the query
	 * @return a <code>QueryExecution</code> that will answer the query with the
	 *         given model
	 */
	public static QueryExecution createPelletExecution(Query query, Model model) {
		return create( query, DatasetFactory.create( model ), null, QueryEngineType.PELLET );
	}

	/**
	 * Creates a Pellet query engine that will answer the given query. This
	 * function should be used only if it is known that Pellet query engine can
	 * handle the given query. Otherwise query execution will result in an
	 * exception. for arbitrary queries it is safer to use the
	 * <code>create</code> functions.
	 * 
	 * @see QueryEngineType
	 * @param query
	 *            the query
	 * @param model
	 *            the target of the query
	 * @param initialBinding
	 *            initial binding that will be applied before query execution or
	 *            <code>null</code> if there is no initial binding
	 * @return a <code>QueryExecution</code> that will answer the query with the
	 *         given model
	 */
	public static QueryExecution createPelletExecution(Query query, Model model,
			QuerySolution initialBinding) {
		return create( query, DatasetFactory.create( model ), initialBinding,
				QueryEngineType.PELLET );
	}

	/**
	 * Creates a Pellet query engine that will answer the given query. This
	 * function should be used only if it is known that Pellet query engine can
	 * handle the given query. Otherwise query execution will result in an
	 * exception. for arbitrary queries it is safer to use the
	 * <code>create</code> functions.
	 * 
	 * @see QueryEngineType
	 * @param query
	 *            the query
	 * @param dataset
	 *            the target of the query
	 * @param initialBinding
	 *            initial binding that will be applied before query execution or
	 *            <code>null</code> if there is no initial binding
	 * @return a <code>QueryExecution</code> that will answer the query with the
	 *         given dataset
	 */
	public static QueryExecution createPelletExecution(Query query, Dataset dataset) {
		return create( query, dataset, null, QueryEngineType.PELLET );
	}

	/**
	 * Creates a Pellet query engine that will answer the given query. This
	 * function should be used only if it is known that Pellet query engine can
	 * handle the given query. Otherwise query execution will result in an
	 * exception. for arbitrary queries it is safer to use the
	 * <code>create</code> functions.
	 * 
	 * @see QueryEngineType
	 * @param query
	 *            the query
	 * @param dataset
	 *            the target of the query
	 * @param initialBinding
	 *            initial binding that will be applied before query execution or
	 *            <code>null</code> if there is no initial binding
	 * @return a <code>QueryExecution</code> that will answer the query with the
	 *         given dataset
	 */
	public static QueryExecution createPelletExecution(Query query, Dataset dataset,
			QuerySolution initialBinding) {
		return create( query, dataset, initialBinding, QueryEngineType.PELLET );		
	}
	
	/**
	 * @deprecated Use {@link #createPelletExecution(Query, Model)} instead
	 */
	public static QueryExecution createBasicExecution(Query query, Model model) {
		return createPelletExecution( query, model );
	}
	
	/**
	 * @deprecated Use {@link #createPelletExecution(Query, Dataset)} instead
	 */
	public static QueryExecution createBasicExecution(Query query, Dataset dataset) {
		return createPelletExecution( query, dataset );
	}

}
