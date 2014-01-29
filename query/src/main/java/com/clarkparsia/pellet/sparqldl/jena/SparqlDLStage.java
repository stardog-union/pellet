// Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public
// License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of
// proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package com.clarkparsia.pellet.sparqldl.jena;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.mindswap.pellet.exceptions.UnsupportedQueryException;
import org.mindswap.pellet.jena.PelletInfGraph;
import org.mindswap.pellet.jena.graph.loader.GraphLoader;
import org.mindswap.pellet.utils.ATermUtils;

import aterm.ATermAppl;

import com.clarkparsia.pellet.sparqldl.engine.QueryEngine;
import com.clarkparsia.pellet.sparqldl.model.Query;
import com.clarkparsia.pellet.sparqldl.model.QueryResult;
import com.clarkparsia.pellet.sparqldl.model.ResultBinding;
import com.clarkparsia.pellet.sparqldl.model.ResultBindingImpl;
import com.clarkparsia.pellet.sparqldl.parser.ARQParser;
import com.hp.hpl.jena.graph.Graph;
import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.graph.Triple;
import com.hp.hpl.jena.sparql.core.BasicPattern;
import com.hp.hpl.jena.sparql.core.Var;
import com.hp.hpl.jena.sparql.engine.ExecutionContext;
import com.hp.hpl.jena.sparql.engine.QueryIterator;
import com.hp.hpl.jena.sparql.engine.binding.Binding;
import com.hp.hpl.jena.sparql.engine.iterator.QueryIterRepeatApply;
import com.hp.hpl.jena.sparql.engine.iterator.QueryIteratorResultSet;
import com.hp.hpl.jena.sparql.engine.main.StageGeneratorGeneric;

/**
 * <p>
 * Title:
 * </p>
 * <p>
 * Description: An implementation of ARQ query stage for PelletInfGraph. The
 * {@link BasicPattern} is converted into a native Pellet SPARQL-DL query and
 * answered by the Pellet query engine. The conversion to Pellet query might
 * fail if the pattern is not a SPARQL-DL query in which case the default ARQ
 * handler is used.
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
class SparqlDLStage {
	public final static Logger	log					= Logger.getLogger( SparqlDLStage.class
															.getName() );

	private ARQParser			parser;

	private BasicPattern		pattern;
	private Collection<String>	vars;

	public SparqlDLStage(BasicPattern pattern) {
		this( pattern, true );
	}

	public SparqlDLStage(BasicPattern pattern, boolean handleVariableSPO) {
		this.pattern = pattern;
		this.parser = new ARQParser(handleVariableSPO);

		initVars();
	}

	private void initVars() {
		vars = new LinkedHashSet<String>();
		for( int i = 0; i < pattern.size(); i++ ) {
			Triple t = pattern.get( i );

			if( ARQParser.isDistinguishedVariable( t.getSubject() ) )
				vars.add( t.getSubject().getName() );
			if( t.getPredicate().isVariable() )
				vars.add( t.getPredicate().getName() );
			if( ARQParser.isDistinguishedVariable( t.getObject() ) )
				vars.add( t.getObject().getName() );
		}
	}

	public QueryIterator build(QueryIterator input, ExecutionContext execCxt) {
		Graph graph = execCxt.getActiveGraph();
		if( !(graph instanceof PelletInfGraph) )
			throw new UnsupportedOperationException( "A Pellet-backed model is required" );

		PelletInfGraph pellet = (PelletInfGraph) graph;

		pellet.prepare();

		Query query = parsePattern( pellet );

		if( query != null ) {
			return new PelletQueryIterator( pellet, query, input, execCxt );
		}
		else {
			return new StageGeneratorGeneric().execute( pattern, input, execCxt );
		}
	}

	private Query parsePattern(PelletInfGraph pellet) {
		try {
			return parser.parse( pattern, vars, pellet.getKB(), false );
		} catch( UnsupportedQueryException e ) {
			if( log.isLoggable( Level.FINE ) )
				log.log( Level.FINE, "Falling back to Jena stage", e );

			return null;
		}
	}

	private static class PelletQueryIterator extends QueryIterRepeatApply {
		private PelletInfGraph	pellet;
		private Query			query;

		/**
		 * @param input
		 * @param context
		 */
		public PelletQueryIterator(PelletInfGraph pellet, Query query, QueryIterator input,
				ExecutionContext execCxt) {
			super( input, execCxt );

			this.pellet = pellet;
			this.query = query;
		}

		private ResultBinding convertBinding(Binding binding) {
			ResultBinding pelletBinding = new ResultBindingImpl();
			GraphLoader loader = pellet.getLoader();
			for( Iterator<?> vars = binding.vars(); vars.hasNext(); ) {
				Var var = (Var) vars.next();
				Node value = binding.get( var );
				if( value != null ) {
					ATermAppl pelletVar = ATermUtils.makeVar( var.getVarName() );
					ATermAppl pelletValue = loader.node2term( value );
					pelletBinding.setValue( pelletVar, pelletValue );
				}
			}

			return pelletBinding;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		protected QueryIterator nextStage(Binding binding) {
			Query newQuery = query.apply( convertBinding( binding ) );

			QueryResult results = QueryEngine.exec( newQuery );

			SparqlDLResultSet resultSet = new SparqlDLResultSet( results, null, binding );

			QueryIteratorResultSet iter = new QueryIteratorResultSet( resultSet );

			return iter;
		}
	}
}
