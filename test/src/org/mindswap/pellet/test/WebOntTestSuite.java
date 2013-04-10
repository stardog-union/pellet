// Portions Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// Clark & Parsia, LLC parts of this source code are available under the terms of the Affero General Public License v3.

package org.mindswap.pellet.test;

import java.io.File;
import java.io.FileFilter;
import java.util.Arrays;

import junit.framework.TestSuite;

import org.mindswap.pellet.utils.AlphaNumericComparator;
import org.mindswap.pellet.utils.Comparators;

public class WebOntTestSuite extends TestSuite {
    public static String base = PelletTestSuite.base + "owl-test/";

    WebOntTest test;

    public WebOntTestSuite() {
        super( WebOntTestSuite.class.getName() );
        
        test = new WebOntTest();
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
				addTest( new WebOntTestCase( test, files[j], dirs[i].getName() + "-" + files[j].getName() ) );
			
		}        
    }
    
    public static TestSuite suite() {
        return new WebOntTestSuite();
    }
    
    public static void main(String args[]) { 
        junit.textui.TestRunner.run(suite());
    }

}
