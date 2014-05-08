// Portions Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// Clark & Parsia, LLC parts of this source code are available under the terms of the Affero General Public License v3.

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
import org.mindswap.pellet.utils.Comparators;

@RunWith(Parameterized.class)
public class WebOntTestSuite {
    public static String base = PelletTestSuite.base + "owl-test/";

	@Parameters(name= "{index}: {0}")
	public static List<Object[]> getParameters() {
		List<Object[]> parameters = new ArrayList<Object[]>();

        WebOntTest test = new WebOntTest();
        test.setAvoidFailTests( true );
        test.setBase( "file:" + base );
        test.setShowStats( WebOntTest.NO_STATS );
        
		File testDir = new File( base );
		File[] dirs = testDir.listFiles();
		
		Arrays.sort( dirs, Comparators.stringComparator );
		
		for (int i = 0; i < dirs.length; i++) {
			if(dirs[i].isFile()) continue;

			File[] files = dirs[i].listFiles(  new FileFilter() {
	            public boolean accept( File file ) {
	                return file.getName().indexOf( "Manifest" ) != -1;
	            }		    
			});
			
			Arrays.sort(files, AlphaNumericComparator.CASE_INSENSITIVE);
			
			for (int j = 0; j < files.length; j++)
				parameters.add(new Object[] { new WebOntTestCase( test, files[j], dirs[i].getName() + "-" + files[j].getName() ) } );
			
		}    
		
		return parameters;
    }

	private final WebOntTestCase test;

	public WebOntTestSuite(WebOntTestCase test) {
		this.test = test;
	}

	@Test
	public void run() throws IOException {
		test.runTest();
	}

}
