// Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package com.clarkparsia.sparqlowl.parser.test;

import static org.junit.Assert.assertEquals;

import com.clarkparsia.pellet.sparqldl.parser.ARQParser;
import com.clarkparsia.sparqlowl.parser.arq.ARQTerpParser;
import com.clarkparsia.sparqlowl.parser.arq.TerpSyntax;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import junit.framework.JUnit4TestAdapter;
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.Syntax;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.mindswap.pellet.KnowledgeBase;
import org.mindswap.pellet.jena.JenaLoader;
import org.mindswap.pellet.test.PelletTestSuite;
import org.mindswap.pellet.utils.FileUtils;

/**
 * <p>
 * Title:
 * </p>
 * <p>
 * Description:
 * </p>
 * <p>
 * Copyright: Copyright (c) 2009
 * </p>
 * <p>
 * Company: Clark & Parsia, LLC. <http://www.clarkparsia.com>
 * </p>
 *
 * @author Evren Sirin
 */
@RunWith(Parameterized.class)
public class ParserTestParentQueries
{
	public static final String base = PelletTestSuite.base + "/sparqldl-tests/simple/";

	public static junit.framework.Test suite()
	{
		return new JUnit4TestAdapter(ParserTestParentQueries.class);
	}

	@Parameters
	public static Collection<Object[]> getParameters()
	{
		final Collection<Object[]> parameters = new ArrayList<>();
		parameters.add(new Object[] { "parent1.rq", "parent1.sparqlowl" });
		parameters.add(new Object[] { "parent2.rq", "parent2.sparqlowl" });
		parameters.add(new Object[] { "parent3.rq", "parent3.sparqlowl" });
		parameters.add(new Object[] { "parent4.rq", "parent4.sparqlowl" });
		parameters.add(new Object[] { "parent5.rq", "parent5.sparqlowl" });
		parameters.add(new Object[] { "parent6.rq", "parent6.sparqlowl" });
		parameters.add(new Object[] { "parent7.rq", "parent7.sparqlowl" });
		parameters.add(new Object[] { "parent8.rq", "parent8.sparqlowl" });
		parameters.add(new Object[] { "parent9.rq", "parent9.sparqlowl" });
		//parameters.add( new Object[] { "parent10.rq", "parent10.sparqlowl" } );	//Not supported
		return parameters;
	}

	private static KnowledgeBase _kb;
	private static ARQParser _parser;

	private final String _sparqlFile;
	private final String _sparqlOWLFile;

	public ParserTestParentQueries(final String sparqlFile, final String sparqlOWLFile)
	{
		_sparqlFile = sparqlFile;
		_sparqlOWLFile = sparqlOWLFile;
	}

	@BeforeClass
	public static void beforeClass()
	{
		ARQTerpParser.registerFactory();

		final JenaLoader loader = new JenaLoader();
		_kb = loader.createKB(base + "parent.ttl");
		_parser = new ARQParser();
	}

	@AfterClass
	public static void afterClass()
	{
		ARQTerpParser.unregisterFactory();

		_kb = null;
		_parser = null;
	}

	@Test
	public void compareQuery() throws FileNotFoundException, IOException
	{
		final Query sparql = QueryFactory.create(FileUtils.readFile(base + _sparqlFile), Syntax.syntaxSPARQL);
		final com.clarkparsia.pellet.sparqldl.model.Query expected = _parser.parse(sparql, _kb);

		final Query sparqlOWL = QueryFactory.create(FileUtils.readFile(base + _sparqlOWLFile), TerpSyntax.getInstance());
		final com.clarkparsia.pellet.sparqldl.model.Query actual = _parser.parse(sparqlOWL, _kb);

		assertEquals(expected.getAtoms(), actual.getAtoms());
	}
}
