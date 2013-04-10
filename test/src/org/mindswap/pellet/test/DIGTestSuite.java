// Portions Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// Clark & Parsia, LLC parts of this source code are available under the terms of the Affero General Public License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package org.mindswap.pellet.test;

import java.io.File;
import java.io.FileFilter;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.Arrays;

import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.apache.xerces.parsers.DOMParser;
import org.mindswap.pellet.dig.DIGHandler;
import org.mindswap.pellet.dig.ElementList;
import org.mindswap.pellet.dig.PelletDIGReasoner;
import org.mindswap.pellet.utils.AlphaNumericComparator;
import org.mindswap.pellet.utils.FileUtils;
import org.mindswap.pellet.utils.PatternFilter;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;

/**
 * <p>Title: </p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2006</p>
 *
 * <p>Company: Clark & Parsia, LLC. <http://www.clarkparsia.com></p>
 *
 * @author Evren sirin
 */
public class DIGTestSuite extends TestSuite {
    public static String base = PelletTestSuite.base + "dig-test/";
    
    private static PelletDIGReasoner digReasoner = new PelletDIGReasoner();
    private static DOMParser parser = new DOMParser();
    private static String tellHeader = 
        "<?xml version=\"1.0\"?>\r\n" + 
        "<tells " +
        "   xmlns=\"http://dl.kr.org/dig/lang\" " +
        "   xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" " +
        "   xsi:schemaLocation=\"http://dl.kr.org/dig/lang http://potato.cs.man.ac.uk/dig/level0/dig.xsd\">";
    private static String tellFooter = "</tells>";
    private static String askHeader = 
        "<?xml version=\"1.0\"?>\r\n" + 
        "<asks " +
        "   xmlns=\"http://dl.kr.org/dig/2003/02/lang\" " +
        "   xsi=\"http://www.w3.org/2001/XMLSchema-instance\" " +
        "   schemaLocation=\"http://dl.kr.org/dig/2003/02/lang http://dl.kr.org/dig/2003/02/dig.xsd\" " +
        "   uri=\"\">\r\n";
    private static String askFooter = "</asks>";
    
    public static class DIGTestCase extends TestCase {
        String name;
        File dir;
        
        public DIGTestCase( File dir ) {
            super( "DIGTestCase-" + dir.getName() );
            
            this.dir = dir;
            this.name = dir.getName();
        }

        public void runTest() throws Exception {
            StringWriter out = new StringWriter();
            
            digReasoner.newKB( "" );
            
            String tell = tellHeader + FileUtils.readFile( dir + "/kb.xmlf" ) + tellFooter;
            
            digReasoner.process( new StringReader( tell ), out );
            
//            System.out.println( "Tell:\n" + out );

            String ask = askHeader + FileUtils.readFile( dir + "/queries.xmlf" ) + askFooter;
                        
            parser.parse( new InputSource( new StringReader( ask ) ) );
            
            Document result = digReasoner.process( parser.getDocument() );
            
//            System.out.println( "Ask:\n" + DIGHandler.serialize( result ) );
            
            digReasoner.releaseKB( "" );
            
            ElementList askList = DIGHandler.getElements( result.getDocumentElement() );
            for(int i = 0; i < askList.getLength(); i++) {
                Element askEl = askList.item( i );
                String tag = askEl.getTagName();      
                String id = DIGHandler.getId( askEl );
                
                if( tag.equals( "error" ) ) {
                    String msg = askEl.getFirstChild().getNodeValue();
                    assertTrue( "Test: " + name + " Query: " + id + " Error: " + msg, false );     
                }
                
                assertTrue( "Test: " + name + " Query: " + id + " Result: " + tag, id.startsWith( tag ) );                
            }
        }
    } 
    
    public DIGTestSuite(  ) {
        this( new PatternFilter( ".*", ".svn" ) );
    }
    
    public DIGTestSuite( FileFilter filter ) {
        super( DIGTestSuite.class.getName() );
        
        try {
            parser.setFeature( "http://apache.org/xml/features/dom/include-ignorable-whitespace", false );
        }
        catch( Exception e ) {
            throw new RuntimeException( e );
        }
        
		File dir = new File( base );
			File[] files = dir.listFiles( filter );
	
			if( files != null ) {
			Arrays.sort( files, AlphaNumericComparator.CASE_INSENSITIVE );
			
			for (int i = 0; i < files.length; i++) {
				addTest( new DIGTestCase( files[i] ) );			
			}        
		}
    }
    
    public static TestSuite suite() {
        return new DIGTestSuite();
    }    

    public static void main(String args[]) { 
    	for( int i = 0; i < 100; i++)
    		junit.textui.TestRunner.run( suite() );
    }    
}
