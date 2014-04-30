// Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public
// License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of
// proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package com.clarkparsia.pellet.test.query;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.mindswap.pellet.jena.PelletReasonerFactory;

import com.clarkparsia.pellet.sparqldl.jena.SparqlDLExecutionFactory;
import com.clarkparsia.pellet.sparqldl.jena.SparqlDLExecutionFactory.QueryEngineType;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.query.Dataset;
import com.hp.hpl.jena.query.DatasetFactory;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.rdf.model.ModelFactory;

/**
 * <p>
 * Title: TestExecBooleanComponents
 * </p>
 * <p>
 * Description: The test case tests the ticket 126 reported on trac Ground
 * boolean queries with disconnected queries are evaluated wrong
 * </p>
 * <p>
 * Copyright: Copyright (c) 2008
 * </p>
 * <p>
 * Company: Clark & Parsia, LLC. <http://www.clarkparsia.com>
 * </p>
 * 
 * @author Markus Stocker
 * @author Evren Sirin
 */
@RunWith(Parameterized.class)
public class TestGroundBooleanQueryComponents {

	private static String	sourceDir	= "test/data/";
	private static String	sourceURL	= sourceDir + "misc/food.owl";
	private static String	queryPrefix	= sourceDir + "/query/ground-boolean-query-components-";

	@Parameters
	public static List<Object[]> getParameters() {
		List<Object[]> params = new ArrayList<Object[]>();

		for( QueryEngineType queryEngineType : QueryEngineType.values() ) {
			for( int q = 1; q <= 4; q++ ) {
				String queryFile = queryPrefix + q + ".rq";
				boolean result = q < 3;
				params.add( new Object[] { queryEngineType, queryFile, result } );
			}
		}

		return params;
	}

	private final QueryEngineType	queryEngineType;
	private final String			queryURL;
	private final boolean			expectedResult;

	private static OntModel	model;

	@BeforeClass
	public static void setUp() {
		sourceURL = "file:" + sourceURL;
		model = ModelFactory.createOntologyModel( PelletReasonerFactory.THE_SPEC );
		model.read( sourceURL );

		model.prepare();
	}

	public TestGroundBooleanQueryComponents(QueryEngineType queryEngineType, String queryURL, boolean expectedResult) {
		this.queryEngineType = queryEngineType;
		this.queryURL = queryURL;
		this.expectedResult = expectedResult;
	}

	@Test
	public void test() {
		Query query = QueryFactory.read( queryURL );
		Dataset dataset = DatasetFactory.create( model );

		QueryExecution qe = SparqlDLExecutionFactory.create( query, dataset, null, queryEngineType );
		assertEquals( "Failed query engine: " + queryEngineType + " query: " + queryURL, expectedResult, qe.execAsk() );
	}
}
