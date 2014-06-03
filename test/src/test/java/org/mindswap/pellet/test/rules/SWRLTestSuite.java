// Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package org.mindswap.pellet.test.rules;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.mindswap.pellet.test.PelletTestSuite;
import org.mindswap.pellet.test.WebOntTest;
import org.mindswap.pellet.test.WebOntTestCase;
import org.mindswap.pellet.utils.AlphaNumericComparator;
import org.mindswap.pellet.utils.Comparators;

/**
 * <p>
 * Title: SWRL Test Suite
 * </p>
 * <p>
 * Description: Regression tests collected for the rule engine in Pellet.
 * </p>
 * <p>
 * Copyright: Copyright (c) 2007
 * </p>
 * <p>
 * Company: Clark & Parsia, LLC. <http://www.clarkparsia.com>
 * </p>
 * 
 * @author Ron Alford
 */
@RunWith(Parameterized.class)
public class SWRLTestSuite {
	public static final String base = PelletTestSuite.base + "swrl-test/";
	
	private static List<File> IGNORE = Arrays.asList(
		new File (base + "equalities/Manifest002.rdf")
	);

	@Parameters(name= "{0}")
	public static List<Object[]> getParameters() {
		List<Object[]> parameters = new ArrayList<Object[]>();

		WebOntTest test = new WebOntTest();
		test.setAvoidFailTests(true);
		test.setBase("http://owldl.com/ontologies/swrl/tests/", "file:" + base);
		test.setShowStats(WebOntTest.NO_STATS);

		File testDir = new File(base);
		File[] dirs = testDir.listFiles();
		
		Arrays.sort( dirs, Comparators.stringComparator );
		
		System.out.println(Arrays.toString(dirs));
		for (int i = 0; i < dirs.length; i++) {
			System.out.println(dirs[i].getAbsolutePath());
			if(dirs[i].isFile()) continue;
		
			File[] files = dirs[i].listFiles(new FileFilter() {
				public boolean accept(File file) {
					return file.getName().indexOf("Manifest") != -1;
				}
			});
			Arrays.sort(files, AlphaNumericComparator.CASE_INSENSITIVE);
			
			for (int j = 0; j < files.length; j++) {
				if( !IGNORE.contains( files[j] ) )
					parameters.add(new Object[] { new WebOntTestCase(test, files[j], "swrl-" + dirs[i].getName()+"-"+files[j].getName())});
			}
		}
		
		return parameters;
	}

	private final WebOntTestCase test;

	public SWRLTestSuite(WebOntTestCase test) {
		this.test = test;
	}

	@Test
	public void run() throws IOException {
		test.runTest();
	}
}
