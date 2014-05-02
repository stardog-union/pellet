// Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public
// License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of
// proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package com.clarkparsia.pellet.test.query;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import junit.framework.TestSuite;

import org.mindswap.pellet.test.PelletTestSuite;

import com.clarkparsia.pellet.sparqldl.jena.SparqlDLExecutionFactory.QueryEngineType;
import com.hp.hpl.jena.rdf.model.Resource;

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
public class SparqlDawgTestSuite extends TestSuite {
	private static String getName(String manifestName) {
		String name = manifestName;

		try {
			URI uri = URI.create( manifestName );
			String[] pathComponents = uri.getPath().split( "/" );
			name = pathComponents[pathComponents.length - 2];
		} catch( Exception e ) {
			e.printStackTrace();
		}

		return name;
	}

	private static TestSuite addSuite(final TestSuite root, final String manifest,
			final Map<SparqlDawgTester, Properties> testers) {
		final ManifestEngine engine = new ManifestEngine( null, manifest );
		engine.setProcessor( new ManifestEngineProcessor() {

			TestSuite						current	= root;

			final Map<TestSuite, TestSuite>	map		= new HashMap<TestSuite, TestSuite>();

			/**
			 * {@inheritDoc}
			 */
			public void manifestStarted(String manifestName) {
				final TestSuite newSuite = new TestSuite( getName( manifestName ) );
				map.put( newSuite, current );
				current.addTest( newSuite );
				current = newSuite;
			}

			/**
			 * {@inheritDoc}
			 */
			public void test(Resource test) {
				for( Map.Entry<SparqlDawgTester, Properties> entry : testers.entrySet() ) {
					SparqlDawgTester tester = entry.getKey();
					Properties options = entry.getValue();
					current.addTest( new SparqlDawgTestCase( tester, engine, test, options ) );
				}

			}

			/**
			 * {@inheritDoc}
			 */
			public void manifestFinished(String manifestName) {
				current = map.get( current );
			}
		} );

		engine.run();

		return root;
	}

	public static TestSuite suite() {
		return new SparqlDawgTestSuite();
	}
	
	public SparqlDawgTestSuite() {
		super( SparqlDawgTestCase.class.toString() );
		
		SparqlDawgTester arqTester = new ARQSparqlDawgTester();
		// The third boolean parameter controls whether or not special handling
		// of variable SPO patterns is activated. We turn special handling off
		// for the test suite because handling variable SPO patterns can result
		// in a fall back to ARQ on a PelletInfGraph and return a result set
		// which is different to the result sets provided by the DAWG test suite.
		SparqlDawgTester basicSparqlDLTester = new PelletSparqlDawgTester( QueryEngineType.PELLET, false );
		SparqlDawgTester integratedSparqlDLTester = new PelletSparqlDawgTester( QueryEngineType.MIXED, false );

//		String dawgTests = PelletTestSuite.base
//				+ "sparql-dawg-tests/data-r2/manifest-evaluation.ttl";
		String sparqldlTests = PelletTestSuite.base + "sparqldl-tests/manifest.ttl";
		String sparqlSameAsTests = PelletTestSuite.base + "query/sameAs/manifest.ttl";
		Properties noUndistVars = new Properties();
		noUndistVars.setProperty( "IGNORE_UNSUPPORTED_AXIOMS", "false" );
		noUndistVars.setProperty( "TREAT_ALL_VARS_DISTINGUISHED", "true" );

		Properties undistVars = new Properties();
		undistVars.setProperty( "IGNORE_UNSUPPORTED_AXIOMS", "false" );
		undistVars.setProperty( "TREAT_ALL_VARS_DISTINGUISHED", "false" );

		Map<SparqlDawgTester, Properties> dawgTesters = new HashMap<SparqlDawgTester, Properties>();
		dawgTesters.put( arqTester, noUndistVars );
		dawgTesters.put( basicSparqlDLTester, noUndistVars );
		dawgTesters.put( integratedSparqlDLTester, noUndistVars );

		//addSuite( this, dawgTests, dawgTesters );

		Map<SparqlDawgTester, Properties> sparqldlTesters = new HashMap<SparqlDawgTester, Properties>();
		sparqldlTesters.put( basicSparqlDLTester, undistVars );
		sparqldlTesters.put( integratedSparqlDLTester, undistVars );

		addSuite( this, sparqldlTests, sparqldlTesters );
		
		Map<SparqlDawgTester, Properties> sparqlSameAsTesters = new HashMap<SparqlDawgTester, Properties>();
		sparqlSameAsTesters.put( basicSparqlDLTester, undistVars );
		addSuite( this, sparqlSameAsTests, sparqlSameAsTesters );
	}

	public static void main(String args[]) {
		junit.textui.TestRunner.run( suite() );
	}
}
