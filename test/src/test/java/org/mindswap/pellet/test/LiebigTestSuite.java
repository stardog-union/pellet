// Portions Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// Clark & Parsia, LLC parts of this source code are available under the terms of the Affero General Public License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package org.mindswap.pellet.test;

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
import org.mindswap.pellet.utils.AlphaNumericComparator;


@RunWith(Parameterized.class)
public class LiebigTestSuite {
	public static String base = PelletTestSuite.base + "liebig-tests/";
		
	private static List TIMEOUTS = Arrays.asList(new String[] {
			"Manifest1b.rdf",
			"Manifest2b.rdf",	
			"Manifest10a.rdf"
	});

	@Parameters
	public static List<Object[]> getParameters() {
		List<Object[]> parameters = new ArrayList<Object[]>();

		WebOntTest test = new WebOntTest();
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
				parameters.add(new Object[] { new WebOntTestCase(test, files[j], "liebig-" + files[j].getName()) });
		}

		return parameters;
	}



	private final WebOntTestCase test;

	public LiebigTestSuite(WebOntTestCase test) {
		this.test = test;
	}

	@Test
	public void run() throws IOException {
		test.runTest();
	}

}
