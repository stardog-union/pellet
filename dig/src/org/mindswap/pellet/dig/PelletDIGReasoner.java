// The MIT License
//
// Copyright (c) 2003 Ron Alford, Mike Grove, Bijan Parsia, Evren Sirin
//
// Permission is hereby granted, free of charge, to any person obtaining a copy
// of this software and associated documentation files (the "Software"), to
// deal in the Software without restriction, including without limitation the
// rights to use, copy, modify, merge, publish, distribute, sublicense, and/or
// sell copies of the Software, and to permit persons to whom the Software is
// furnished to do so, subject to the following conditions:
//
// The above copyright notice and this permission notice shall be included in
// all copies or substantial portions of the Software.
//
// THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
// IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
// FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
// AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
// LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
// FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS
// IN THE SOFTWARE.

package org.mindswap.pellet.dig;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Serializable;
import java.io.Writer;
import java.rmi.server.UID;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

import org.apache.xerces.parsers.DOMParser;
import org.apache.xml.serialize.OutputFormat;
import org.apache.xml.serialize.XMLSerializer;
import org.mindswap.pellet.KnowledgeBase;
import org.mindswap.pellet.utils.VersionInfo;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;


/*
 * Created on Jul 15, 2005
 */

/**
 * @author Evren Sirin
 *
 */
public class PelletDIGReasoner extends DIGAskHandler implements Serializable {
	private static final long serialVersionUID = 5134066314905452958L;

	private String baseURI = "urn:dig:pellet:kb";
    
    private Map<String, KnowledgeBase> allKBs;
    private String selectedKB;
    
    private Document identifier;
    
    private static final String[] supportLang = {
    	TOP, BOTTOM, CATOM, AND, OR, NOT, SOME, ALL, ATMOST, ATLEAST, ISET,
        RATOM, INVERSE, ATTRIBUTE, // FEATURE, 
        INTEQUALS, STRINGEQUALS, // INTMIN, INTMAX, INTRANGE, DEFINED 
    };
    
    private static final String[] supportTell = {
        DEFCONCEPT, IMPLIESC, EQUALC, DISJOINT,  
        DEFROLE, DEFATTRIBUTE, //DEFFEATURE,         
        IMPLIESR, EQUALR, DOMAIN, RANGE, TRANSITIVE, FUNCTIONAL, 
        DEFINDIVIDUAL,  INSTANCEOF, RELATED, VALUE,  
        RANGEINT, RANGESTRING
    };
    
    private static final String[] supportAsk = {
	    ALL_CONCEPT_NAMES, ALL_ROLE_NAMES, ALL_INDIVIDUALS, 
	    SATISFIABLE, SUBSUMES, DISJOINT, 
	    PARENTS, CHILDREN, ANCESTORS, DESCENDANTS, EQUIVALENTS, 
	    RPARENTS, RCHILDREN, RANCESTORS, RDESCENDANTS, 
	    INSTANCES, TYPES, INSTANCE, ROLE_FILLERS, RELATED_INDIVIDUALS, TOLD_VALUES
    };
    
    private transient DOMParser parser;
	
    public PelletDIGReasoner() {
        allKBs = new HashMap<String, KnowledgeBase>();
        selectedKB = "NO_KB";
    }
    
    private void ensureParser() {
    	 parser = new DOMParser();
     	try {
             parser.setFeature("http://apache.org/xml/features/dom/include-ignorable-whitespace", false);
         } 
     	catch(Exception e) {
            throw new RuntimeException( e );
         }
    }
    
    public void process( InputStream in, OutputStream out ) throws SAXException, IOException {
        process( new InputStreamReader( in ), new OutputStreamWriter( out ) );
    }

    public void process( Reader in, Writer out ) throws SAXException, IOException {
    	ensureParser();
        parser.parse( new InputSource( in ) );

		Document doc = parser.getDocument();
        		
		if( log.isLoggable( Level.FINE ) ) log.fine( "\n" + serialize( doc ) + "\n");
		
        Document result = process( doc );
        
        OutputFormat format  = new OutputFormat( result ); 
        format.setLineWidth( 0 );             
        format.setPreserveSpace( false );
        // always indent XML output, otherwise Protege hangs
        format.setIndenting( true );
        format.setOmitXMLDeclaration( false );
        
        XMLSerializer serial = new XMLSerializer( out, format );
        serial.asDOMSerializer();                            

        serial.serialize( result.getDocumentElement() );
        
        if( log.isLoggable( Level.FINE ) ) log.fine( "\n" + serialize( result ) + "\n" );
    }
    
    public Document process( Document cmd ) {
        Document result;
        
        Element cmdNode = cmd.getDocumentElement();
        String cmdName = getTagName( cmdNode );
        
        if( cmdName.equals( DIGConstants.GET_IDENTIFIER ) ) {
            result = getIdentifier();
        }
        else if( cmdName.equals( DIGConstants.NEWKB ) ) {
            try {
                String uri = newKB();
                
                result = DIGResponse.createKBResponse( uri );
            } catch( Exception e ) {
                result = DIGResponse.createErrorResponse( DIGErrors.CANNOT_CREATE_NEW_KNOWLEDGE, "Cannot create KB!");
            }
        }
        else if( cmdName.equals( DIGConstants.RELEASEKB ) ) {
            String uri = getURI( cmdNode );
            boolean success = releaseKB( uri );
            if( success )
                result = DIGResponse.createOkResponse();
            else
                result = DIGResponse.createErrorResponse( DIGErrors.KB_RELEASE_ERROR, "Cannot release KB " + uri );
        }
        else if( cmdName.equals( DIGConstants.ASKS ) ) {
            try {
                String uri = getURI( cmdNode );
                
                if( selectKB( uri ) )
                    result = asks( cmdNode );
                else
                    result = DIGResponse.createErrorResponse( DIGErrors.UNKNOWN_OR_STALE_KB_URI, uri + " is not known KB URI" );
            } catch( Exception e ) {
                e.printStackTrace();
                result = DIGResponse.createErrorResponse( DIGErrors.GENERAL_ASK_ERROR, e.getMessage() );           
            }
        }
        else if( cmdName.equals( DIGConstants.TELLS ) ) {
            try {
                String uri = getURI( cmdNode );

                if( selectKB( uri ) )
                    result = tells( cmdNode );
                else
                    result = DIGResponse.createErrorResponse( DIGErrors.UNKNOWN_OR_STALE_KB_URI, uri + " is not known KB URI" );
            } catch( Exception e ) {
                e.printStackTrace();
                return DIGResponse.createErrorResponse( DIGErrors.GENERAL_TELL_ERROR, e.getMessage() );
            }
        }
        else
            result = DIGResponse.createErrorResponse( DIGErrors.UNKNOWN_REQUEST,  "Unknown request " + cmd );
        
        return result;
    }
    
    public boolean selectKB( String uri ) {
        if( uri.equals( selectedKB ) )
            return true;
        
        if( !allKBs.containsKey( uri ) )
            return false;
        
        selectedKB = uri;
        
        KnowledgeBase kb = allKBs.get( uri );
        
        setKB( kb );
        
        return true;
    }

    public void unselectKB( String uri ) {
        if( !uri.equals( selectedKB ) )
            return;
        
        selectedKB = "NO_KB";
    }

    public Document getIdentifier() {
        if( identifier == null ) {
            DIGResponse response = new DIGResponse( DIGConstants.IDENTIFIER );
            
            identifier = response.getDocument();
            
            Element id = identifier.getDocumentElement();
            id.setAttribute( NAME, "Pellet" );
            id.setAttribute( VERSION, VersionInfo.getInstance().getVersionString() );           
            id.setAttribute( MESSAGE, "Pellet - OWL DL Reasoner" );
            
            Element supports = response.addElement( SUPPORTS, id );
            
            Element language = response.addElement( LANGUAGE, supports );
            for(int i = 0; i < supportLang.length; i++) 
                response.addElement( supportLang[i], language );                
            
            Element tell = response.addElement( TELL, supports );
            for(int i = 0; i < supportTell.length; i++) 
                response.addElement( supportTell[i], tell );                
            
            Element ask = response.addElement( ASK, supports );
            for(int i = 0; i < supportAsk.length; i++) 
                response.addElement( supportAsk[i], ask );                                        
        }
        
        return identifier;
    }

    public String newKB() {
        String newURI = baseURI + new UID().toString();
        
        newKB( newURI );
        
        return newURI;
    }

    public KnowledgeBase newKB( String newURI ) {
        KnowledgeBase newKB = new KnowledgeBase();
        
        allKBs.put( newURI, newKB );
        
        return newKB;
    }

    public boolean releaseKB(String uri) {
        unselectKB( uri );
        
        allKBs.remove( uri );
        
        return true;
    }
}
