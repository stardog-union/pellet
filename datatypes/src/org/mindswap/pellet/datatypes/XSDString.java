
package org.mindswap.pellet.datatypes;

import org.mindswap.pellet.utils.ATermUtils;
import org.mindswap.pellet.utils.Namespaces;

import aterm.ATermAppl;



/**
 * @author Evren Sirin
 */
public class XSDString extends BaseAtomicDatatype implements AtomicDatatype {
	
	private static final String	STRING_DT_NAME	= Namespaces.XSD + "string";
	public static XSDString		instance		= new XSDString();

	XSDString() {
		super( ATermUtils.makeTermAppl( STRING_DT_NAME ) );
	}

	public AtomicDatatype getPrimitiveType() {
		return instance;
	}
	
	public Object getValue(String value, String datatypeURI) {
		assert (value != null) &&  (datatypeURI != null) : "Null inputs";
		assert datatypeURI.equals( "" ) || datatypeURI.equals( STRING_DT_NAME ) : "Unexpected datatype";

		return datatypeURI.equals( STRING_DT_NAME ) || datatypeURI.equals( "" )
			? ATermUtils.makeTypedLiteral( value, STRING_DT_NAME )
			: ATermUtils.makePlainLiteral( value );
	}
	
	public boolean contains(Object value) {
		if( value instanceof ATermAppl ) {
			ATermAppl a = (ATermAppl) value;
			if( ATermUtils.isLiteral( a ) ) {
				String dt = ATermUtils.getLiteralDatatype( a );
				String lang = ATermUtils.getLiteralLang( a );
				return (dt.equals( "" ) || (lang.equals( "" ) && dt.equals( STRING_DT_NAME )))
						&& super.contains( value );
				// If changes are made to support datatypes derived from
				// xsd:string, the check above for datatype name must change
			}
		}
		return false;
	}
	
	public Datatype deriveByRestriction(String facet, String value) throws UnsupportedOperationException {
	    throw new UnsupportedOperationException("xsd:string does not support facet " + facet);			
	}
}
