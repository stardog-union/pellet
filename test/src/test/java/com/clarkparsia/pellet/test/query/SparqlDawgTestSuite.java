// Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public
// License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of
// proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package com.clarkparsia.pellet.test.query;

import com.clarkparsia.pellet.sparqldl.jena.SparqlDLExecutionFactory.QueryEngineType;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import org.apache.jena.rdf.model.Resource;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.mindswap.pellet.test.PelletTestSuite;

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
@RunWith(Parameterized.class)
public class SparqlDawgTestSuite
{
	@SuppressWarnings("unused")
	private static String getName(final String manifestName)
	{
		String name = manifestName;

		try
		{
			final URI uri = URI.create(manifestName);
			final String[] pathComponents = uri.getPath().split("/");
			name = pathComponents[pathComponents.length - 2];
		}
		catch (final Exception e)
		{
			e.printStackTrace();
		}

		return name;
	}

	private static void addSuite(final List<Object[]> parameters, final String manifest, final Map<SparqlDawgTester, Properties> testers)
	{
		final ManifestEngine engine = new ManifestEngine(null, manifest);
		engine.setProcessor(new ManifestEngineProcessor()
		{
			@Override
			public void manifestStarted(final String manifestName)
			{
				// Nothing to do
			}

			@Override
			public void test(final Resource test)
			{
				for (final Map.Entry<SparqlDawgTester, Properties> entry : testers.entrySet())
				{
					final SparqlDawgTester tester = entry.getKey();
					final Properties options = entry.getValue();
					parameters.add(new Object[] { new SparqlDawgTestCase(tester, engine, test, options) });
				}

			}

			@Override
			public void manifestFinished(final String manifestName)
			{
				// Nothing to do
			}
		});

		engine.run();
	}

	@Parameters
	public static List<Object[]> getParameters()
	{
		final List<Object[]> parameters = new ArrayList<>();

		final SparqlDawgTester arqTester = new ARQSparqlDawgTester();
		// The third boolean parameter controls whether or not special handling
		// of variable SPO patterns is activated. We turn special handling off
		// for the test suite because handling variable SPO patterns can result
		// in a fall back to ARQ on a PelletInfGraph and return a result set
		// which is different to the result sets provided by the DAWG test suite.
		final SparqlDawgTester basicSparqlDLTester = new PelletSparqlDawgTester(QueryEngineType.PELLET, false);
		final SparqlDawgTester integratedSparqlDLTester = new PelletSparqlDawgTester(QueryEngineType.MIXED, false);

		//		String dawgTests = PelletTestSuite.base
		//				+ "sparql-dawg-tests/data-r2/_manifest-evaluation.ttl";
		final String sparqldlTests = PelletTestSuite.base + "sparqldl-tests/manifest.ttl";
		final String sparqlSameAsTests = PelletTestSuite.base + "query/sameAs/manifest.ttl";
		final Properties noUndistVars = new Properties();
		noUndistVars.setProperty("IGNORE_UNSUPPORTED_AXIOMS", "false");
		noUndistVars.setProperty("TREAT_ALL_VARS_DISTINGUISHED", "true");

		final Properties undistVars = new Properties();
		undistVars.setProperty("IGNORE_UNSUPPORTED_AXIOMS", "false");
		undistVars.setProperty("TREAT_ALL_VARS_DISTINGUISHED", "false");

		final Map<SparqlDawgTester, Properties> dawgTesters = new HashMap<>();
		dawgTesters.put(arqTester, noUndistVars);
		dawgTesters.put(basicSparqlDLTester, noUndistVars);
		dawgTesters.put(integratedSparqlDLTester, noUndistVars);

		//addSuite( this, dawgTests, dawgTesters );

		final Map<SparqlDawgTester, Properties> sparqldlTesters = new HashMap<>();
		sparqldlTesters.put(basicSparqlDLTester, undistVars);
		sparqldlTesters.put(integratedSparqlDLTester, undistVars);

		addSuite(parameters, sparqldlTests, sparqldlTesters);

		final Map<SparqlDawgTester, Properties> sparqlSameAsTesters = new HashMap<>();
		sparqlSameAsTesters.put(basicSparqlDLTester, undistVars);
		addSuite(parameters, sparqlSameAsTests, sparqlSameAsTesters);

		return parameters;
	}

	private final SparqlDawgTestCase _test;

	public SparqlDawgTestSuite(final SparqlDawgTestCase test)
	{
		this._test = test;
	}

	@Test
	public void run()
	{
		_test.runTest();
	}

}
