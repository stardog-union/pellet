// Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package org.mindswap.pellet.test.rules;

import java.io.File;
import java.io.FileFilter;
import java.util.Arrays;
import java.util.List;

import junit.framework.TestSuite;

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

public class SWRLTestSuite extends TestSuite {
	public static String base = PelletTestSuite.base + "swrl-test/";
	
	private static List<File> IGNORE = Arrays.asList(
	);

	public static void main(String args[]) {
		junit.textui.TestRunner.run(suite());
	}

	public static TestSuite suite() {
		return new SWRLTestSuite();
	}

	public SWRLTestSuite() {
		super( SWRLTestSuite.class.getName() );

		WebOntTest test;
		test = new WebOntTest();
		test.setAvoidFailTests(true);
		test.setBase("http://owldl.com/ontologies/swrl/tests/", "file:" + base);
		test.setShowStats(WebOntTest.NO_STATS);

		File testDir = new File(base);
		File[] dirs = testDir.listFiles();
		
		Arrays.sort( dirs, Comparators.stringComparator );
		
		for (int i = 0; i < dirs.length; i++) {
			if(dirs[i].isFile()) continue;
		
			File[] files = dirs[i].listFiles(new FileFilter() {
				public boolean accept(File file) {
					return file.getName().indexOf("Manifest") != -1;
				}
			});
			Arrays.sort(files, AlphaNumericComparator.CASE_INSENSITIVE);
			
			for (int j = 0; j < files.length; j++) {
				if( !IGNORE.contains( files[j] ) )
					addTest(new WebOntTestCase(test, files[j], "swrl-" + dirs[i].getName()+"-"+files[j].getName()));
			}
		}
		

		

	}
}
