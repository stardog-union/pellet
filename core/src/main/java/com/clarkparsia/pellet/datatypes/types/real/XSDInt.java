package com.clarkparsia.pellet.datatypes.types.real;

import javax.xml.bind.DatatypeConverter;

import org.mindswap.pellet.utils.ATermUtils;
import org.mindswap.pellet.utils.Namespaces;

import com.clarkparsia.pellet.datatypes.exceptions.InvalidLiteralException;

/**
 * <p>
 * Title: <code>xsd:int</code>
 * </p>
 * <p>
 * Description: Singleton implementation of <code>xsd:int</code> datatype
 * </p>
 * <p>
 * Copyright: Copyright (c) 2009
 * </p>
 * <p>
 * Company: Clark & Parsia, LLC. <http://www.clarkparsia.com>
 * </p>
 * 
 * @author Mike Smith
 */
public class XSDInt extends AbstractDerivedIntegerType {

	private static final XSDInt	instance;

	static {
		instance = new XSDInt();
	}

	public static XSDInt getInstance() {
		return instance;
	}

	private XSDInt() {
		super( ATermUtils.makeTermAppl( Namespaces.XSD + "int" ), Integer.MIN_VALUE,
				Integer.MAX_VALUE );
	}



	@Override
	protected Number fromLexicalForm(String lexicalForm) throws InvalidLiteralException {
		try {
			long n = DatatypeConverter.parseLong( lexicalForm );
			if( n < Integer.MIN_VALUE || n > Integer.MAX_VALUE )
				throw new InvalidLiteralException( getName(), lexicalForm );
			return Integer.valueOf( (int) n );
		} catch( NumberFormatException e ) {
			throw new InvalidLiteralException( getName(), lexicalForm );
		}
	}
}
