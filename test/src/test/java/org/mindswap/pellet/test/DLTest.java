// Portions Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// Clark & Parsia, LLC parts of this source code are available under the terms of the Affero General Public License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package org.mindswap.pellet.test;

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

@RunWith(Parameterized.class)
public class DLTest {

	public static final String base = PelletTestSuite.base;

	@Parameters(name="{0}")
	public static Collection<Object[]> getParameters() {
		Collection<Object[]> parameters = new ArrayList<Object[]>();

		List<String> dlBenchmarkBaseNames =
						Arrays.asList("bike1", "bike2", "bike3", "bike4", "bike5", "bike6",
										"bike7", "bike8", "bike9", "bio", "ckb-gcis", "ckb-roles",
										"datamont-gcis-cd", "datamont-gcis", "datamont-roles",
										"embassi-1", "embassi-2", "embassi-3", "fss-gcis", "fss-roles",
										"modkit", "pdwq", "people", "platt", "uml-1", "uml-2", "umls-1",
										"veda-all", "wines", "wisber-gcis", "wisber-roles");

		addParameters(base + "dl-benchmark/tbox/", dlBenchmarkBaseNames, parameters);

		List<String> krssBaseNames =
						Arrays.asList("test2", "test3", "test4", "test5");

		addParameters(base + "krss-tests/", krssBaseNames, parameters);

		return parameters;
	}

	private static void addParameters(String dirName, List<String> baseNames, Collection<Object[]> parameters) {
		for(String nextBaseName : baseNames) {
			parameters.add(new Object[] { nextBaseName, dirName });
		}
	}

	private DLBenchmarkTest test = new DLBenchmarkTest();

	private String baseName;

	private String tkbFile;

	private String treeFile;

	private String dirName;

	@Before
	public void setUp() throws Exception {
		tkbFile = dirName + baseName+".tkb";
		treeFile = dirName + baseName+".tree";
	}

	@Test
	public void runTest() throws Exception {
		assertTrue(test.doTBoxTest( baseName, tkbFile, treeFile ));
	}

	public DLTest(String baseName, String dirName) {
		this.baseName = baseName;
		this.dirName = dirName;
	}

}
