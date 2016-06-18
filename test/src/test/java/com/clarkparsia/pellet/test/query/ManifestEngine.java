// Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package com.clarkparsia.pellet.test.query;

import java.io.FileWriter;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import openllet.shared.tools.Log;
import org.apache.jena.query.ARQ;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;
import org.apache.jena.sparql.vocabulary.EARL;
import org.apache.jena.sparql.vocabulary.FOAF;
import org.apache.jena.util.FileManager;
import org.apache.jena.util.LocatorFile;
import org.apache.jena.vocabulary.RDF;
import org.mindswap.pellet.exceptions.UnsupportedFeatureException;
import org.mindswap.pellet.utils.VersionInfo;

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
 * Company: Clark & Parsia, LLC.
 * </p>
 *
 * @author Petr Kremen
 */

public class ManifestEngine
{
	private static final Logger _logger = Log.getLogger(ManifestEngine.class);

	// MANIFESTS
	private final String _manifest;

	// SINGLE TEST EXECUTOR
	private ManifestEngineProcessor _singleTestExecutor;

	// RESULTS
	private final List<SingleTestResult> _results = new ArrayList<>();

	private boolean _writeResults = false;

	public ManifestEngine(final SparqlDawgTester tester, final String manifest)
	{
		this._manifest = manifest;
		this._singleTestExecutor = new ManifestEngineProcessor()
		{
			/**
			 * {@inheritDoc}
			 */
			@Override
			public void manifestStarted(final String manifestURI)
			{
				_logger.fine("START _manifest: " + manifestURI);
			}

			/**
			 * {@inheritDoc}
			 */
			@Override
			public void test(final Resource test)
			{
				_results.add(doSingleTest(tester, test));
			}

			/**
			 * {@inheritDoc}
			 */
			@Override
			public void manifestFinished(final String manifestURI)
			{
				_logger.fine("FINISH _manifest: " + manifestURI);
			}
		};
	}

	public void setProcessor(final ManifestEngineProcessor processor)
	{
		this._singleTestExecutor = processor;
	}

	public ManifestEngineProcessor setProcessor()
	{
		return _singleTestExecutor;
	}

	public void run()
	{
		ARQ.setStrictMode();

		doTests();

		writeEarlResults();
	}

	private void writeEarlResults()
	{
		if (!_writeResults)
			return;

		final Model model = ModelFactory.createDefaultModel();

		model.setNsPrefix(EarlResultVocabulary.doapBaseNs, EarlResultVocabulary.doapBase);
		model.setNsPrefix("earl", EARL.getURI());
		model.setNsPrefix("foaf", FOAF.getURI());

		// assertor
		final Resource organization = model.createResource(FOAF.Organization);
		model.add(organization, FOAF.name, "No organization");
		model.add(organization, FOAF.homepage, "https://github.com/Galigator/openllet");

		// project
		final Resource project = model.createResource("https://github.com/Galigator/openllet", EarlResultVocabulary.Project);
		model.add(project, EarlResultVocabulary.doapName, "Openllet");

		final Resource release = model.createResource(EarlResultVocabulary.Version);

		model.add(release, EarlResultVocabulary.revision, VersionInfo.getInstance().getVersionString());
		model.add(project, EarlResultVocabulary.release, release);

		for (final SingleTestResult result : _results)
		{
			final Resource assertion = model.createResource(EARL.Assertion);
			model.add(assertion, EARL.assertedBy, organization);

			final Resource testResult = model.createResource(EARL.TestResult);

			final Resource resultState;

			switch (result.getResult())
			{
				case PASS:
					resultState = EARL.passed;
					break;
				case FAIL:
					resultState = EARL.failed;
					break;
				case SKIP:
					resultState = EARL.NotTested;
					break;
				default:
					throw new IllegalArgumentException("Unknown result type : " + result);
			}

			model.add(testResult, EARL.outcome, resultState);
			model.add(assertion, EARL.result, testResult);
			model.add(assertion, EARL.subject, project);
			model.add(assertion, EARL.test, model.createResource(result.getUri().toString()));
		}

		try
		{
			model.write(new FileWriter("dawg" + "-pellet-" + VersionInfo.getInstance().getVersionString() + ".rdf"), "RDF/XML-ABBREV");
		}
		catch (final IOException e)
		{
			_logger.log(Level.SEVERE, e.getMessage(), e);
		}
	}

	private void doTests()
	{
		_results.clear();

		try
		{
			final String base = new URI(_manifest).getPath();
			FileManager.get().addLocator(new LocatorFile(base));
		}
		catch (final URISyntaxException e)
		{
			e.printStackTrace();
		}
		_doTest(_manifest);

		printStatistics();
	}

	private void printStatistics()
	{
		final int[] sizes = new int[] { 60, 10, 10 };
		final String format = "| %1$-" + sizes[0] + "s| %2$-" + sizes[1] + "s| %3$-" + sizes[1] + "s|";

		final char[] a = new char[String.format(format, new Object[sizes.length]).length()];

		Arrays.fill(a, '=');
		final String separator = new String(a);

		_logger.fine(separator);
		_logger.fine(String.format(format, "name", "result", "time [ms]"));
		_logger.fine(separator);

		for (final SingleTestResult test : _results)
			_logger.log(Level.FINE, String.format(format, test.getUri().getFragment(), test.getResult(), test.getTime()));
		_logger.fine(separator);
	}

	private void _doTest(final String manifestURI)
	{
		_logger.fine("Processing _manifest : " + manifestURI + "'.");
		_singleTestExecutor.manifestStarted(manifestURI);
		try
		{
			final Model model = ModelFactory.createDefaultModel();

			final URI uri = URI.create(manifestURI);
			FileManager.get().readModel(model, uri.toString());

			final StmtIterator i = model.listStatements(null, RDF.type, SparqlDawgTestVocabulary.Manifest);
			while (i.hasNext())
			{
				final Statement stmt = i.nextStatement();

				final Resource manifest = stmt.getSubject();

				// DFS: include all nested manifests
				final Statement includeStmt = manifest.getProperty(SparqlDawgTestVocabulary.include);

				if (includeStmt != null)
				{
					final Resource manifestsCollection = includeStmt.getResource();
					final List<Resource> container = parseList(manifestsCollection);

					for (final Resource singleManifest : container)
						_doTest(singleManifest.getURI());
				}

				// execute single tests
				final Statement singleTests = manifest.getProperty(SparqlDawgTestVocabulary.entries);

				if (singleTests != null)
				{
					final List<Resource> set = parseList(singleTests.getResource());

					for (final Resource singleTest : set)
						_singleTestExecutor.test(singleTest);
				}

				_singleTestExecutor.manifestFinished(manifestURI);
			}
		}
		catch (final Exception e)
		{
			_logger.log(Level.SEVERE, e.getMessage(), e);
		}
	}

	public SingleTestResult doSingleTest(final SparqlDawgTester tester, final Resource singleTest)
	{
		final Resource testType = singleTest.getProperty(RDF.type).getResource();

		final Statement statement = singleTest.getProperty(SparqlDawgTestVocabulary.approval);
		final Resource testApprovalStatus;
		if (statement != null)
			testApprovalStatus = singleTest.getProperty(SparqlDawgTestVocabulary.approval).getResource();
		else
			testApprovalStatus = SparqlDawgTestVocabulary.NotClassified;

		_logger.fine("Test : " + singleTest);
		_logger.fine("Type : " + testType.getLocalName() + " ");
		_logger.finer("Name : " + singleTest.getProperty(SparqlDawgTestVocabulary.name).getString());
		_logger.finer("Appr.: " + testApprovalStatus.getLocalName());
		_logger.finer("Tester: " + tester.getClass().getName());

		final SingleTestResult result = doTestCase(tester, singleTest, testType);

		_logger.finer("");
		_logger.fine("Result: " + result.getResult());
		_logger.fine("Time Elapsed: " + result.getTime());
		_logger.fine("--------------------------------------------------------------------");

		return result;
	}

	private List<Resource> parseList(final Resource start)
	{
		final List<Resource> set = new ArrayList<>();
		Resource list = start;

		while (!RDF.nil.equals(list))
		{
			set.add(list.getProperty(RDF.first).getResource());
			list = list.getProperty(RDF.rest).getResource();
		}

		return set;
	}

	private SingleTestResult doSyntaxTest(final SparqlDawgTester tester, final Resource testCase, final boolean parsable)
	{
		final String queryFile = testCase.getProperty(SparqlDawgTestVocabulary.action).getResource().getURI();

		if (tester.isApplicable(testCase.getURI()))
		{
			tester.setQueryURI(queryFile);
			final long startPoint = System.currentTimeMillis();
			final boolean result = tester.isParsable();
			final long time = System.currentTimeMillis() - startPoint;

			if (result == parsable)
				return new SingleTestResult(URI.create(testCase.getURI()), ResultEnum.PASS, time);
			else
			{
				if (parsable)
					_logger.log(Level.SEVERE, "Fail: The input should be parsable, but parsing fails.");
				else
					_logger.log(Level.SEVERE, "Fail: The input should not be parsable, but parsing is succesful.");
				return new SingleTestResult(URI.create(testCase.getURI()), ResultEnum.FAIL, time);
			}
		}
		else
			return new SingleTestResult(URI.create(testCase.getURI()), ResultEnum.SKIP, 0);
	}

	private SingleTestResult doEvaluationTest(final SparqlDawgTester tester, final Resource testCase)
	{
		// more 'action' values are allowed !!!

		String queryFile = null;
		final Set<String> dataFiles = new HashSet<>();
		final Set<String> graphDataFiles = new HashSet<>();

		final StmtIterator i = testCase.listProperties(SparqlDawgTestVocabulary.action);
		while (i.hasNext())
		{
			final Resource actionNode = i.nextStatement().getResource();

			// QUERY
			final Statement qfCandidate = actionNode.getProperty(SparqlDawgTestVocabulary.query);

			if (qfCandidate != null)
				if (queryFile == null)
					queryFile = qfCandidate.getResource().getURI();
				else
					throw new IllegalArgumentException("More than 1 query has been set : " + queryFile + " vs. " + qfCandidate);

			// GRAPH
			final StmtIterator dataI = actionNode.listProperties(SparqlDawgTestVocabulary.data);
			while (dataI.hasNext())
				dataFiles.add(dataI.nextStatement().getResource().getURI());

			// NAMED GRAPH
			final StmtIterator graphDataI = actionNode.listProperties(SparqlDawgTestVocabulary.graphData);
			while (graphDataI.hasNext())
				graphDataFiles.add(graphDataI.nextStatement().getResource().getURI());
		}

		final Statement resultFileStmt = testCase.getProperty(SparqlDawgTestVocabulary.result);

		String resultFile = null;

		if (resultFileStmt != null)
			resultFile = resultFileStmt.getResource().getURI();

		if (tester.isApplicable(testCase.getURI()))
		{
			long startPoint = 0;
			try
			{
				tester.setDatasetURIs(dataFiles, graphDataFiles);
				tester.setQueryURI(queryFile);
				tester.setResult(resultFile);
				startPoint = System.currentTimeMillis();

				final boolean result = tester.isCorrectlyEvaluated();
				final long time = System.currentTimeMillis() - startPoint;

				if (result)
					return new SingleTestResult(URI.create(testCase.getURI()), ResultEnum.PASS, time);
				else
				{
					_logger.severe("Fail: Evaluation of the query is not correct.");
					return new SingleTestResult(URI.create(testCase.getURI()), ResultEnum.FAIL, time);
				}
			}
			catch (final UnsupportedFeatureException e)
			{
				_logger.log(Level.SEVERE, e.getMessage(), e);
				return new SingleTestResult(URI.create(testCase.getURI()), ResultEnum.SKIP, System.currentTimeMillis() - startPoint);
			}
			catch (final Exception e)
			{
				_logger.log(Level.SEVERE, e.getMessage(), e);
				return new SingleTestResult(URI.create(testCase.getURI()), ResultEnum.FAIL, System.currentTimeMillis() - startPoint);
			}

		}
		else
			return new SingleTestResult(URI.create(testCase.getURI()), ResultEnum.SKIP, 0);
	}

	private SingleTestResult doTestCase(final SparqlDawgTester tester, final Resource testCase, final Resource testType)
	{
		if (testType.equals(SparqlDawgTestVocabulary.PositiveSyntaxTest))
			return doSyntaxTest(tester, testCase, true);
		else
			if (testType.equals(SparqlDawgTestVocabulary.NegativeSyntaxTest))
				return doSyntaxTest(tester, testCase, false);
			else
				if (testType.equals(SparqlDawgTestVocabulary.QueryEvaluationTest))
					return doEvaluationTest(tester, testCase);

		throw new RuntimeException("Unknown test type " + testType.getLocalName() + " for " + testCase);
	}

	public boolean isWriteResults()
	{
		return _writeResults;
	}

	public void setWriteResults(final boolean writeResults)
	{
		this._writeResults = writeResults;
	}
}
