// Portions Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// Clark & Parsia, LLC parts of this source code are available under the terms of the Affero General Public License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package org.mindswap.pellet.datatypes;

import org.mindswap.pellet.utils.ATermUtils;
import org.mindswap.pellet.utils.Namespaces;

import aterm.ATermAppl;


/**
 * @author Evren Sirin
 */
public class RDFXMLLiteral extends BaseAtomicDatatype implements AtomicDatatype {
	public static final RDFXMLLiteral instance = new RDFXMLLiteral();

	static class XMLValue {
		String value;
		
		XMLValue(String value) {
			this.value = value;
		}
		
		public int hashCode() {
			return value.hashCode();
		}
		
		public boolean equals(Object obj) {
			if(obj instanceof XMLValue) {
				XMLValue otherVal = (XMLValue) obj;
				String stringVal = otherVal.value;
				return value.equals(stringVal);
			}
			return false;
		}
		
		public String toString() {
		    return value;
		}
	}

	RDFXMLLiteral() {
		super(ATermUtils.makeTermAppl(Namespaces.RDF + "XMLLiteral"));
	}


	/* (non-Javadoc)
	 * @see org.mindswap.pellet.datatypes.AtomicDatatype#getPrimitiveType()
	 */
	public AtomicDatatype getPrimitiveType() {
		return instance;
	}

	/* (non-Javadoc)
	 * @see org.mindswap.pellet.datatypes.Datatype#getValue(java.lang.String)
	 */
	public Object getValue(String value, String datatypeURI) {
	    if(datatypeURI.equals(name.getName()))
	        return new XMLValue(value);
	    
	    return null;
	}
	
	public ATermAppl getValue( int n ) {
		return ATermUtils.makeTypedLiteral( "<text>" + n + "</text>", name.getName());
	}
	
	/* (non-Javadoc)
	 * @see org.mindswap.pellet.datatypes.Datatype#contains(java.lang.Object)
	 */
	public boolean contains(Object value) {
		return (value instanceof XMLValue) && super.contains(value);
	}
    
    public String toString() {
        return "rdf:XMLLiteral";
    }
}
