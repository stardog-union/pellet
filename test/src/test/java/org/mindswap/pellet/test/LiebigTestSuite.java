// Portions Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// Clark & Parsia, LLC parts of this source code are available under the terms of the Affero General Public License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package org.mindswap.pellet.test;

import java.io.File;
import java.io.FileFilter;
import java.util.Arrays;
import java.util.List;

import junit.framework.TestSuite;

import org.mindswap.pellet.utils.AlphaNumericComparator;

public class LiebigTestSuite extends TestSuite {
	public static String base = PelletTestSuite.base + "liebig-tests/";
		
	private static List TIMEOUTS = Arrays.asList(new String[] {
			"Manifest1b.rdf",
			"Manifest2b.rdf",	
			"Manifest10a.rdf"
	});

	private WebOntTest test;

	public LiebigTestSuite() {
		super( LiebigTestSuite.class.getName() );

		test = new WebOntTest();
		test.setAvoidFailTests(true);
		test.setBase("http://www.informatik.uni-ulm.de/ki/Liebig/reasoner-eval/", "file:" + base);
		test.setShowStats(WebOntTest.NO_STATS);

		File testDir = new File(base);

		File[] files = testDir.listFiles(new FileFilter() {
			public boolean accept(File file) {
				return file.getName().indexOf("Manifest") != -1;
			}
		});

		Arrays.sort(files, AlphaNumericComparator.CASE_INSENSITIVE);

		for (int j = 0; j < files.length; j++) {
			if( !TIMEOUTS.contains( files[j].getName() ) )
				addTest(new WebOntTestCase(test, files[j], "liebig-" + files[j].getName()));
		}

	}

	public static TestSuite suite() {
		return new LiebigTestSuite();
	}

	public static void main(String args[]) {
		junit.textui.TestRunner.run(suite());
	}

}
