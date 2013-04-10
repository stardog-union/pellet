// Portions Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// Clark & Parsia, LLC parts of this source code are available under the terms of the Affero General Public License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package org.mindswap.pellet.datatypes;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.logging.Level;

import org.mindswap.pellet.utils.ATermUtils;
import org.mindswap.pellet.utils.Namespaces;

import aterm.ATermAppl;



/**
 * @author Evren Sirin
 */
public class XSDAnyURI extends BaseAtomicDatatype implements AtomicDatatype {
    

	private static final String	ANY_URI	= Namespaces.XSD + "anyURI";
	public static XSDAnyURI instance = new XSDAnyURI();

	XSDAnyURI() {
		super(ATermUtils.makeTermAppl(ANY_URI));
	}

	public AtomicDatatype getPrimitiveType() {
		return instance;
	}

	public Object getValue(String value, String datatypeURI) {
		try {
			return new URI(value.trim());
		} catch (URISyntaxException e) {
		    if(datatypeURI.equals(instance.name.getName())) {
		        DatatypeReasoner.log.log( Level.WARNING, "Invalid xsd:anyURI value: '" + value + "'", e );
		    }
			
			return null;
		}		
	}
	
	public boolean contains(Object value) {
		return (value instanceof URI) && super.contains(value);
	}

    public boolean isFinite() {
        return true;
    }

    public ATermAppl getValue( int n ) {
        return ATermUtils.makeTypedLiteral( "http://test" + n, ANY_URI );
    }	
}
