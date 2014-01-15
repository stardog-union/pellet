// Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public
// License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of
// proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package com.clarkparsia.pellet.test.classification;

import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.Timeout;
import org.mindswap.pellet.test.PelletTestSuite;

import com.clarkparsia.UnstableTests;

/**
 * 
 * @author Evren Sirin
 */
public abstract class AbstractClassificationTest {
    
    /**
     * Timeout each individual classification test after 5 minutes.
     */
    @Rule
    public Timeout timeout = new Timeout(5 * 60 * 1000);
    
	public static final String	base	= PelletTestSuite.base + "modularity/";
	
	protected static boolean FAIL_AT_FIRST_ERROR = false;	
	
	public void testFile(String fileName) throws Exception {
		String common = base + fileName;
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
	
	@Category(UnstableTests.class)
	@Ignore("Consistently timing out")
	@Test	
	public void mechanicalEngineeringTest() throws Exception {
		testFile( "MechanicalEngineering" );
	}
}
