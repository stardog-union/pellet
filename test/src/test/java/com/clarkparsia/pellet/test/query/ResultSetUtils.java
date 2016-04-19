// Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package com.clarkparsia.pellet.test.query;

import java.io.ByteArrayOutputStream;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.query.ResultSetFactory;
import org.apache.jena.query.ResultSetFormatter;
import org.apache.jena.query.ResultSetRewindable;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.ResourceFactory;
import org.apache.jena.sparql.resultset.ResultsFormat;
import org.junit.Assert;

/**
 * @author Evren Sirin
 */
public class ResultSetUtils
{
	private static final Logger log = Logger.getLogger(ResultSetUtils.class.getName());

	private static final RDFNode DUMMY_FOR_BNODE = ResourceFactory.createPlainLiteral("dummy node for bnode");

	public static boolean assertEquals(final ResultSet expectedResults, final ResultSet computedResults)
	{
		final ResultSetRewindable expected = ResultSetFactory.makeRewindable(expectedResults);
		final ResultSetRewindable computed = ResultSetFactory.makeRewindable(computedResults);

		if (expected.size() != computed.size())
		{
			logResults("Expected", expected);
			logResults("Real", computed);
			Assert.fail("Expected " + expected.size() + " but got " + computed.size());
		}

		final List<String> vars = expected.getResultVars();
		final Collection<Map<String, RDFNode>> results = results(computed);
		for (expected.reset(); expected.hasNext();)
		{
			final QuerySolution qs = expected.nextSolution();
			final Map<String, RDFNode> map = solutionMap(qs, vars);

			if (!results.contains(map))
			{
				logResults("Expected", expected);
				logResults("Real", computed);
				Assert.fail("Solution not found: " + map);

				Assert.fail("Expected " + expected.size() + " but got " + computed.size());
			}
		}

		return true;
	}

	public static Map<String, RDFNode> solutionMap(final QuerySolution qs, final List<String> vars)
	{
		final Map<String, RDFNode> result = new HashMap<>();
		for (final String var : vars)
		{
			final RDFNode val = qs.get(var);
			result.put(var, val.isAnon() ? DUMMY_FOR_BNODE : val);
		}
		return result;
	}

	public static Collection<Map<String, RDFNode>> results(final ResultSetRewindable rs)
	{
		rs.reset();
		final List<String> vars = rs.getResultVars();
		final Set<Map<String, RDFNode>> results = new HashSet<>();
		while (rs.hasNext())
		{
			final QuerySolution qs = rs.nextSolution();
			final Map<String, RDFNode> result = new HashMap<>();
			for (final String var : vars)
				result.put(var, qs.get(var));
			results.add(solutionMap(qs, vars));
		}
		return results;
	}

	public static void logResults(final String name, final ResultSetRewindable results)
	{
		if (log.isLoggable(Level.WARNING))
		{
			log.warning(name + " (" + results.size() + ")");
			results.reset();
			final ByteArrayOutputStream out = new ByteArrayOutputStream();
			ResultSetFormatter.output(out, results, ResultsFormat.FMT_RDF_TTL);
			log.warning("\n" + out.toString());
		}
	}
}
