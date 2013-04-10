// Portions Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// Clark & Parsia, LLC parts of this source code are available under the terms of the Affero General Public License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package org.mindswap.pellet.test;

import java.io.File;
import java.io.FilenameFilter;
import java.util.Arrays;

import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.mindswap.pellet.utils.AlphaNumericComparator;

public class DLTestSuite extends TestSuite {
    public static String base = PelletTestSuite.base;

    private DLBenchmarkTest test = new DLBenchmarkTest();
       
    class DLTestCase extends TestCase {
        File name;
        boolean tbox;
        
        DLTestCase( File name, boolean tbox ) {
            super( "DLTestCase-" + name.getName() );
            this.name = name;
            this.tbox = tbox;
        }

        public void runTest() throws Exception {
            boolean pass = false;
            
            if( tbox )
                pass = test.doTBoxTest( name.getAbsolutePath() );
            else
                pass = test.doABoxTest( name.getAbsolutePath() );
            
            assertTrue( pass );
        }
    } 
    
    public DLTestSuite() {
        super( DLTestSuite.class.getName() );
        
        File[] dirs = new File[] {
        		new File( base + "dl-benchmark/tbox" ),
        		new File( base + "krss-tests" ),
        };
        
		for( File dir : dirs ) {
			File[] files = dir.listFiles( new FilenameFilter() {
				public boolean accept(File dir, String name) {
					// test1.tkb explicitly disabled due to #420
					return dir != null && name.endsWith(".tkb") && !name.equals( "test1.tkb" );
				}			
			});

			Arrays.sort(files, AlphaNumericComparator.CASE_INSENSITIVE);
			
			for (int i = 0; i < files.length; i++) {
				addTest( new DLTestCase( files[i], true ) );			
			}  
		}      
    }
    
    public static TestSuite suite() {
        return new DLTestSuite();
    }
}
