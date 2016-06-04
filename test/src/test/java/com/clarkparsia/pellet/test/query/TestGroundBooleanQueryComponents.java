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

	private static String _sourceDir = "test/data/";
	private static String _sourceURL = _sourceDir + "misc/food.owl";
	private static String _queryPrefix = _sourceDir + "/query/ground-boolean-query-components-";

	@Parameters
	public static List<Object[]> getParameters()
	{
		final List<Object[]> params = new ArrayList<>();

		for (final QueryEngineType queryEngineType : QueryEngineType.values())
			for (int q = 1; q <= 4; q++)
			{
				final String queryFile = _queryPrefix + q + ".rq";
				final boolean result = q < 3;
				params.add(new Object[] { queryEngineType, queryFile, result });
			}

		return params;
	}

	private final QueryEngineType _queryEngineType;
	private final String _queryURL;
	private final boolean _expectedResult;

	private static OntModel _model;

	@BeforeClass
	public static void setUp()
	{
		_sourceURL = "file:" + _sourceURL;
		_model = ModelFactory.createOntologyModel(PelletReasonerFactory.THE_SPEC);
		_model.read(_sourceURL);

		_model.prepare();
	}

	public TestGroundBooleanQueryComponents(final QueryEngineType queryEngineType, final String queryURL, final boolean expectedResult)
	{
		this._queryEngineType = queryEngineType;
		this._queryURL = queryURL;
		this._expectedResult = expectedResult;
	}

	@Test
	public void test()
	{
		final Query query = QueryFactory.read(_queryURL);
		final Dataset dataset = DatasetFactory.create(_model);

		final QueryExecution qe = SparqlDLExecutionFactory.create(query, dataset, null, _queryEngineType);
		assertEquals("Failed query engine: " + _queryEngineType + " query: " + _queryURL, _expectedResult, qe.execAsk());
	}
}
