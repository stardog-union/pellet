// Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public
// License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of
// proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package com.clarkparsia.pellet.test.query;

import static org.junit.Assert.assertEquals;

import com.clarkparsia.pellet.sparqldl.jena.SparqlDLExecutionFactory;
import com.clarkparsia.pellet.sparqldl.jena.SparqlDLExecutionFactory.QueryEngineType;
import java.util.ArrayList;
import java.util.List;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.query.Dataset;
import org.apache.jena.query.DatasetFactory;
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.rdf.model.ModelFactory;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.mindswap.pellet.jena.PelletReasonerFactory;

/**
 * <p>
 * Title: TestExecBooleanComponents
 * </p>
 * <p>
 * Description: The test case tests the ticket 126 reported on trac Ground boolean queries with disconnected queries are evaluated wrong
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
public class TestGroundBooleanQueryComponents
{

	private static String sourceDir = "test/data/";
	private static String sourceURL = sourceDir + "misc/food.owl";
	private static String queryPrefix = sourceDir + "/query/ground-boolean-query-components-";

	@Parameters
	public static List<Object[]> getParameters()
	{
		final List<Object[]> params = new ArrayList<>();

		for (final QueryEngineType queryEngineType : QueryEngineType.values())
			for (int q = 1; q <= 4; q++)
			{
				final String queryFile = queryPrefix + q + ".rq";
				final boolean result = q < 3;
				params.add(new Object[] { queryEngineType, queryFile, result });
			}

		return params;
	}

	private final QueryEngineType queryEngineType;
	private final String queryURL;
	private final boolean expectedResult;

	private static OntModel model;

	@BeforeClass
	public static void setUp()
	{
		sourceURL = "file:" + sourceURL;
		model = ModelFactory.createOntologyModel(PelletReasonerFactory.THE_SPEC);
		model.read(sourceURL);

		model.prepare();
	}

	public TestGroundBooleanQueryComponents(final QueryEngineType queryEngineType, final String queryURL, final boolean expectedResult)
	{
		this.queryEngineType = queryEngineType;
		this.queryURL = queryURL;
		this.expectedResult = expectedResult;
	}

	@Test
	public void test()
	{
		final Query query = QueryFactory.read(queryURL);
		final Dataset dataset = DatasetFactory.create(model);

		final QueryExecution qe = SparqlDLExecutionFactory.create(query, dataset, null, queryEngineType);
		assertEquals("Failed query engine: " + queryEngineType + " query: " + queryURL, expectedResult, qe.execAsk());
	}
}
