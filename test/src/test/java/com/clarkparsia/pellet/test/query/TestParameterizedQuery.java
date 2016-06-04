// Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public
// License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of
// proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package com.clarkparsia.pellet.test.query;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

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
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.QuerySolutionMap;
import org.apache.jena.query.ResultSet;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.ResourceFactory;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.mindswap.pellet.jena.PelletReasonerFactory;

/**
 * <p>
 * Title:
 * </p>
 * <p>
 * Description:
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
public class TestParameterizedQuery
{
	private static final String NS = "http://example.org#";
	private static final String PREFIX = "PREFIX : <" + NS + ">";

	private static Dataset dataset;

	private static QuerySolutionMap binding(final String var, final RDFNode value)
	{
		final QuerySolutionMap b = new QuerySolutionMap();
		b.add(var, value);

		return b;
	}

	private static QuerySolutionMap binding(final String var1, final RDFNode value1, final String var2, final RDFNode value2)
	{
		final QuerySolutionMap b = new QuerySolutionMap();
		b.add(var1, value1);
		b.add(var2, value2);

		return b;
	}

	@Parameters
	public static List<Object[]> getParameters()
	{
		final Property pred = ResourceFactory.createProperty(NS + "p");
		final Resource x1 = ResourceFactory.createResource(NS + "x1");
		final Resource y1 = ResourceFactory.createResource(NS + "y1");
		final Resource x2 = ResourceFactory.createResource(NS + "x2");
		final Resource y2 = ResourceFactory.createResource(NS + "y2");

		final OntModel m = ModelFactory.createOntologyModel(PelletReasonerFactory.THE_SPEC);
		m.add(x1, pred, y1);
		m.add(x2, pred, y2);

		dataset = DatasetFactory.create(m);

		final Query q1 = QueryFactory.create(PREFIX + "SELECT ?x WHERE { ?x :p ?y }");
		final Query q2 = QueryFactory.create(PREFIX + "SELECT ?x ?y WHERE { ?x :p ?y }");
		final Query q3 = QueryFactory.create(PREFIX + "SELECT * WHERE { ?x :p ?y }");
		final Query q4 = QueryFactory.create(PREFIX + "SELECT * WHERE { :x1 :p ?y }");

		final QuerySolutionMap b1 = binding("y", y1);
		final QuerySolutionMap b2 = binding("x", x1);
		final QuerySolutionMap b3 = binding("y", y1, "x", x1);
		final QuerySolutionMap b4 = null;

		final List<Object[]> params = new ArrayList<>();

		for (final QueryEngineType qe : QueryEngineType.values())
		{
			params.add(new Object[] { qe, q1, b1, b2 });
			params.add(new Object[] { qe, q2, b2, b3 });
			params.add(new Object[] { qe, q3, b1, b3 });
			params.add(new Object[] { qe, q4, b4, b1 });
		}

		return params;
	}

	private final QueryEngineType _queryEngine;
	private final Query _query;
	private final QuerySolution _initialBinding;
	private final QuerySolution _expected;

	public TestParameterizedQuery(final QueryEngineType queryEngine, final Query query, final QuerySolutionMap initialBinding, final QuerySolutionMap expectedResults)
	{
		this._queryEngine = queryEngine;
		this._query = query;
		this._initialBinding = initialBinding;
		this._expected = expectedResults;
	}

	@Test
	public void test()
	{
		final QueryExecution qe = SparqlDLExecutionFactory.create(_query, dataset, _initialBinding, _queryEngine);
		final ResultSet rs = qe.execSelect();

		if (!rs.hasNext())
			assertTrue("No results found", rs.hasNext());

		final QuerySolution computed = rs.nextSolution();

		for (final Object name : rs.getResultVars())
		{
			final String var = name.toString();
			assertEquals("Different result for " + var, _expected.get(var), computed.get(var));
		}

		assertFalse("Extra results found", rs.hasNext());
	}
}
