// Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package com.clarkparsia.pellet.test.query;

import java.io.IOException;
import java.util.EnumSet;
import java.util.Properties;

import junit.framework.TestCase;

import org.mindswap.pellet.PelletOptions;

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
public class SparqlDawgTestCase extends TestCase {

	private final Properties pelletOptions;
	
	private final Resource resource;

	private final ManifestEngine test;
	
	private final SparqlDawgTester tester;

	public SparqlDawgTestCase(SparqlDawgTester tester, ManifestEngine test, Resource resource, Properties pelletOptions) {
		super(resource.getLocalName() + "-" + tester.getName() );
		this.tester = tester;
		this.test = test;
		this.resource = resource;
		this.pelletOptions = pelletOptions;
	}

	/**
	 * {@inheritDoc}
	 */
	public void runTest() throws IOException {
		Properties oldOptions = PelletOptions.setOptions( pelletOptions );		
		try {
			assertTrue(EnumSet.of(ResultEnum.PASS, ResultEnum.SKIP).contains(
					test.doSingleTest(tester, resource).getResult()));
		}
		finally {
			PelletOptions.setOptions( oldOptions );
		}
	}
}