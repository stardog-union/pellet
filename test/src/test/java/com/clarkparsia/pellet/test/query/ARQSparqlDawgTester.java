// Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public
// License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of
// proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package com.clarkparsia.pellet.test.query;

import com.clarkparsia.pellet.sparqldl.jena.JenaIOUtils;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.jena.query.Dataset;
import org.apache.jena.query.DatasetFactory;
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSetFactory;
import org.apache.jena.query.ResultSetFormatter;
import org.apache.jena.query.ResultSetRewindable;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.util.FileManager;
import org.mindswap.pellet.utils.URIUtils;

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
public class ARQSparqlDawgTester implements SparqlDawgTester
{

	private static final Logger log = Logger.getLogger(ARQSparqlDawgTester.class.getName());

	private final List<String> avoidList = Arrays.asList(new String[] {
			// FIXME with some effort some
			// of the following queries can
			// be handled

			// The following tests require [object/data]property punning in the
			// _data
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
			"eq-1", "eq-2" });

	private String queryURI = "";

	protected Set<String> graphURIs = new HashSet<>();

	protected Set<String> namedGraphURIs = new HashSet<>();

	protected Query query = null;

	private String resultURI = null;

	public ARQSparqlDawgTester()
	{
	}

	protected void afterExecution()
	{
		// do nothing
	}

	protected void beforeExecution()
	{
		// do nothing
	}

	protected Dataset createDataset()
	{
		if (query.getGraphURIs().isEmpty() && query.getNamedGraphURIs().isEmpty())
			return DatasetFactory.create(new ArrayList<>(graphURIs), new ArrayList<>(namedGraphURIs));
		else
			return DatasetFactory.create(query.getGraphURIs(), query.getNamedGraphURIs());

	}

	protected QueryExecution createQueryExecution()
	{
		return QueryExecutionFactory.create(query, createDataset());
	}

	@Override
	public void setDatasetURIs(final Set<String> graphURIs, final Set<String> namedGraphURIs)
	{
		this.graphURIs = graphURIs;
		this.namedGraphURIs = namedGraphURIs;
	}

	@Override
	public void setQueryURI(final String queryURI)
	{
		if (this.queryURI.equals(queryURI))
			return;

		this.queryURI = queryURI;
		query = QueryFactory.read(queryURI);
	}

	@Override
	public void setResult(final String resultURI)
	{
		this.resultURI = resultURI;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isParsable()
	{
		try
		{
			query = QueryFactory.read(queryURI);
			return true;
		}
		catch (final Exception e)
		{
			log.log(Level.INFO, e.getMessage(), e);
			return false;
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isCorrectlyEvaluated()
	{
		try
		{
			beforeExecution();
			final QueryExecution exec = createQueryExecution();

			if (resultURI == null)
			{
				log.log(Level.WARNING, "No result set associated with this test, assumuing success!");
				return true;
			}

			if (query.isSelectType())
			{
				final ResultSetRewindable expected = ResultSetFactory.makeRewindable(JenaIOUtils.parseResultSet(resultURI));
				final ResultSetRewindable real = ResultSetFactory.makeRewindable(exec.execSelect());

				final boolean correct = ResultSetUtils.assertEquals(expected, real);

				if (!correct)
				{
					logResults("Expected", expected);
					logResults("Real", real);
				}

				return correct;

			}
			else
				if (query.isAskType())
				{
					final boolean askReal = exec.execAsk();
					final boolean askExpected = JenaIOUtils.parseAskResult(resultURI);

					log.fine("Expected=" + askExpected);
					log.fine("Real=" + askReal);

					return askReal == askExpected;
				}
				else
					if (query.isConstructType())
					{
						final Model real = exec.execConstruct();
						final Model expected = FileManager.get().loadModel(resultURI);

						log.fine("Expected=" + real);
						log.fine("Real=" + expected);

						return real.isIsomorphicWith(expected);
					}
					else
						if (query.isDescribeType())
						{
							final Model real = exec.execDescribe();
							final Model expected = FileManager.get().loadModel(resultURI);

							log.fine("Expected=" + real);
							log.fine("Real=" + expected);

							return real.isIsomorphicWith(expected);
						}
						else
							throw new RuntimeException("The query has invalid type.");
		}
		catch (final IOException e)
		{
			log.log(Level.SEVERE, e.getMessage(), e);
			return false;
		}
		finally
		{
			afterExecution();
		}
	}

	private void logResults(final String name, final ResultSetRewindable results)
	{
		if (log.isLoggable(Level.WARNING))
		{
			results.reset();
			final StringBuilder sb = new StringBuilder(name + " (" + results.size() + ")=");

			while (results.hasNext())
			{
				final QuerySolution result = results.nextSolution();
				sb.append(result);
			}

			log.warning(sb.toString());
		}

		if (log.isLoggable(Level.FINE))
		{
			final ByteArrayOutputStream out = new ByteArrayOutputStream();
			ResultSetFormatter.out(out, results);
			log.fine(out.toString());
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isApplicable(final String testURI)
	{
		return !avoidList.contains(URIUtils.getLocalName(testURI));
	}

	@Override
	public String getName()
	{
		return "ARQ";
	}
}
