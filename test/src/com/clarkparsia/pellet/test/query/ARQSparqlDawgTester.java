// Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public
// License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of
// proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package com.clarkparsia.pellet.test.query;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.mindswap.pellet.utils.URIUtils;

import com.clarkparsia.pellet.sparqldl.jena.JenaIOUtils;
import com.hp.hpl.jena.query.Dataset;
import com.hp.hpl.jena.query.DatasetFactory;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSetFactory;
import com.hp.hpl.jena.query.ResultSetFormatter;
import com.hp.hpl.jena.query.ResultSetRewindable;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.util.FileManager;

/**
 * <p>
 * Title: Engine for processing DAWG test manifests
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
public class ARQSparqlDawgTester implements SparqlDawgTester {

	private static final Logger	log				= Logger.getLogger( ARQSparqlDawgTester.class
														.getName() );

	private List<String>		avoidList		= Arrays.asList( new String[] {
												// FIXME with some effort some
			// of the following queries can
			// be handled

			// The following tests require [object/data]property punning in the
			// data
			"open-eq-07", "open-eq-08", "open-eq-09", "open-eq-10", "open-eq-11", "open-eq-12",

			// not an approved test (and in clear conflict with
			// "dawg-optional-filter-005-simplified",
			"dawg-optional-filter-005-not-simplified",

			// fails due to bugs in ARQ filter handling
			"date-2", "date-3",				

			// ?x p "+3"^^xsd:int does not match "3"^^xsd:int
			"unplus-1",
			
			// ?x p "01"^^xsd:int does not match "1"^^xsd:int
			"open-eq-03",		
			
			// "1"^^xsd:int does not match different lexical forms
			"eq-1", "eq-2"
	} );

	private String				queryURI		= "";

	protected Set<String>		graphURIs		= new HashSet<String>();

	protected Set<String>		namedGraphURIs	= new HashSet<String>();

	protected Query				query			= null;

	private String				resultURI		= null;

	public ARQSparqlDawgTester() {
	}

	protected void afterExecution() {
		// do nothing
	}

	protected void beforeExecution() {
		// do nothing
	}

	protected Dataset createDataset() {
		if( query.getGraphURIs().isEmpty() && query.getNamedGraphURIs().isEmpty() ) {
			return DatasetFactory.create( new ArrayList<String>( graphURIs ),
					new ArrayList<String>( namedGraphURIs ), FileManager.get(), "" );
		}
		else {
			return DatasetFactory.create( query.getGraphURIs(), query.getNamedGraphURIs(),
					FileManager.get(), "" );
		}

	}

	protected QueryExecution createQueryExecution() {
		return QueryExecutionFactory.create( query, createDataset() );
	}

	public void setDatasetURIs(Set<String> graphURIs, Set<String> namedGraphURIs) {
		this.graphURIs = graphURIs;
		this.namedGraphURIs = namedGraphURIs;
	}

	public void setQueryURI(String queryURI) {
		if( this.queryURI.equals( queryURI ) ) {
			return;
		}

		this.queryURI = queryURI;
		query = QueryFactory.read( queryURI );
	}

	public void setResult(String resultURI) {
		this.resultURI = resultURI;
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean isParsable() {
		try {
			query = QueryFactory.read( queryURI );
			return true;
		} catch( Exception e ) {
			log.log( Level.INFO, e.getMessage(), e );
			return false;
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean isCorrectlyEvaluated() {
		try {
			beforeExecution();
			final QueryExecution exec = createQueryExecution();

			if( resultURI == null ) {
				log.log( Level.WARNING,
						"No result set associated with this test, assumuing success!" );
				return true;
			}

			if( query.isSelectType() ) {
				final ResultSetRewindable real = ResultSetFactory
						.makeRewindable( exec.execSelect() );
				final ResultSetRewindable expected = ResultSetFactory.makeRewindable( JenaIOUtils
						.parseResultSet( resultURI ) );

				boolean correct = ResultSetUtils.assertEquals( real, expected );

				if( !correct ) {
					logResults( "Expected", expected );
					logResults( "Real", real );
				}

				return correct;

			}
			else if( query.isAskType() ) {
				final boolean askReal = exec.execAsk();
				final boolean askExpected = JenaIOUtils.parseAskResult( resultURI );

				log.fine( "Expected=" + askExpected );
				log.fine( "Real=" + askReal );

				return askReal == askExpected;
			}
			else if( query.isConstructType() ) {
				final Model real = exec.execConstruct();
				final Model expected = FileManager.get().loadModel( resultURI );

				log.fine( "Expected=" + real );
				log.fine( "Real=" + expected );

				return real.isIsomorphicWith( expected );
			}
			else if( query.isDescribeType() ) {
				final Model real = exec.execDescribe();
				final Model expected = FileManager.get().loadModel( resultURI );

				log.fine( "Expected=" + real );
				log.fine( "Real=" + expected );

				return real.isIsomorphicWith( expected );
			}
			else {
				throw new RuntimeException( "The query has invalid type." );
			}
		} catch( IOException e ) {
			log.log( Level.SEVERE, e.getMessage(), e );
			return false;
		} finally {
			afterExecution();
		}
	}

	private void logResults(String name, ResultSetRewindable results) {
		if( log.isLoggable( Level.WARNING ) ) {
			results.reset();
			StringBuilder sb = new StringBuilder( name + " (" + results.size() + ")=" );

			while( results.hasNext() ) {
				QuerySolution result = results.nextSolution();
				sb.append( result );
			}

			log.warning( sb.toString() );
		}

		if( log.isLoggable( Level.FINE ) ) {
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			ResultSetFormatter.out( out, results );
			log.fine( out.toString() );
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean isApplicable(final String testURI) {
		return !avoidList.contains( URIUtils.getLocalName( testURI ) );
	}

	public String getName() {
		return "ARQ";
	}
}
