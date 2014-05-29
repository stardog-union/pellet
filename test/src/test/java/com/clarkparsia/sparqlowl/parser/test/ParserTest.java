// Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package com.clarkparsia.sparqlowl.parser.test;

import static org.junit.Assert.assertEquals;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.mindswap.pellet.KnowledgeBase;
import org.mindswap.pellet.jena.JenaLoader;
import org.mindswap.pellet.test.PelletTestSuite;
import org.mindswap.pellet.utils.FileUtils;
import org.mindswap.pellet.utils.SetUtils;

import com.clarkparsia.pellet.sparqldl.parser.ARQParser;
import com.clarkparsia.sparqlowl.parser.arq.ARQTerpParser;
import com.clarkparsia.sparqlowl.parser.arq.TerpSyntax;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.Syntax;

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
public class ParserTest {
	public static final String base = PelletTestSuite.base + "/sparqldl-tests/simple/";
	
	@Parameters
	public static Collection<Object[]> getParameters() {
		Collection<Object[]> parameters = new ArrayList<Object[]>();
		
		addParameter(parameters, "simple", 1, 8, 6);
		addParameter(parameters, "parent", 1, 11);
		
		return parameters;
	}
	
	private static void addParameter(Collection<Object[]> parameters, String prefix, int minIndex, int maxIndex, Integer... ignoreIndices) {	
		Set<Integer> ignoreSet = SetUtils.create(ignoreIndices);
		for (int i = minIndex; i <= maxIndex; i++) {
			if( !ignoreSet.contains(i)) {
	            parameters.add( new Object[] { prefix + ".ttl", prefix + i + ".rq", prefix + i + ".terp" } );
            }    
        }
	}

	private static ARQParser parser;

	private final String kbFile;
	private KnowledgeBase kb;
	private final String sparqlFile;
	private final String sparqlOWLFile;
	
	public ParserTest(String kbFile, String sparqlFile, String sparqlOWLFile) {
		this.kbFile = kbFile;
		this.sparqlFile = sparqlFile;
		this.sparqlOWLFile = sparqlOWLFile;
	}
	
	@BeforeClass
	public static void beforeClass() {
		ARQTerpParser.registerFactory();
	}
	
	@AfterClass
	public static void afterClass() {
		ARQTerpParser.unregisterFactory();
	}
		
	@Before
	public void before() {
		kb = new JenaLoader().createKB(base + kbFile);
		parser = new ARQParser();
	}
	
	@After
	public void after() {
		kb = null;
		parser = null;
	}
	
	@Test
	public void compareQuery() throws FileNotFoundException, IOException {
		Query sparql = QueryFactory.create( FileUtils.readFile( base + sparqlFile ), Syntax.syntaxSPARQL );
		com.clarkparsia.pellet.sparqldl.model.Query expected = parser.parse( sparql, kb );
		
		Query sparqlOWL = QueryFactory.create( FileUtils.readFile( base + sparqlOWLFile ), TerpSyntax.getInstance() );		
		com.clarkparsia.pellet.sparqldl.model.Query actual = parser.parse( sparqlOWL, kb );
		
		assertEquals( expected.getAtoms(), actual.getAtoms() );
	}
}
