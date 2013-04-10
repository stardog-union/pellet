// Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public
// License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of
// proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package com.clarkparsia.pellet.test.classification;

import org.junit.Test;
import org.mindswap.pellet.test.PelletTestSuite;

/**
 * 
 * @author Evren Sirin
 */
public abstract class AbstractClassificationTest {
	public static final String	base	= PelletTestSuite.base + "modularity/";
	
	protected static boolean FAIL_AT_FIRST_ERROR = false;	
	
	public void testFile(String fileName) throws Exception {
		String common = "file:"+ base + fileName;
		testClassification( common + ".owl", common + "-conclusions.owl" );
	}

	public abstract void testClassification(String inputOnt, String classifiedOnt) throws Exception;

	@Test
	public void galenClassifyTest() throws Exception {
		testFile( "galen" );
	}

	@Test
	public void koalaClassifyTest() throws Exception {
		testFile( "koala" );
	}

	@Test
	public void sumoClassifyTest() throws Exception {
		testFile( "SUMO" );
	}

	@Test
	public void sweetClassifyTest() throws Exception {
		testFile( "SWEET" );
	}

	@Test
	public void wineClassifyTest() throws Exception {
		testFile( "wine" );
	}
	
	@Test
	public void miniTambisTest() throws Exception {
		testFile( "miniTambis" );
	}
	
	@Test
	public void owl2PrimerTest() throws Exception {
		testFile( "OWL2Primer" );
	}
	
	@Test	
	public void sioTest() throws Exception {
		testFile( "sio" );
	}
	
	@Test	
	public void mechanicalEngineeringTest() throws Exception {
		testFile( "MechanicalEngineering" );
	}
}
