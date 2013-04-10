package com.clarkparsia.pellet.datatypes.types.real;

import javax.xml.bind.DatatypeConverter;

import org.mindswap.pellet.utils.ATermUtils;
import org.mindswap.pellet.utils.Namespaces;

import com.clarkparsia.pellet.datatypes.exceptions.InvalidLiteralException;

/**
 * <p>
 * Title: <code>xsd:unsignedByte</code>
 * </p>
 * <p>
 * Description: Singleton implementation of <code>xsd:unsignedByte</code>
 * datatype
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
public class XSDUnsignedByte extends AbstractDerivedIntegerType {

	private static final XSDUnsignedByte	instance;
	private static final short				MAX_VALUE;

	static {
		MAX_VALUE = 255;

		instance = new XSDUnsignedByte();
	}

	public static XSDUnsignedByte getInstance() {
		return instance;
	}

	private XSDUnsignedByte() {
		super( ATermUtils.makeTermAppl( Namespaces.XSD + "unsignedByte" ), 0, MAX_VALUE );
	}

	@Override
	protected Number fromLexicalForm(String lexicalForm) throws InvalidLiteralException {
		try {
			final short i = DatatypeConverter.parseShort( lexicalForm );
			if( i < 0 )
				throw new InvalidLiteralException( getName(), lexicalForm );
			if( i > MAX_VALUE )
				throw new InvalidLiteralException( getName(), lexicalForm );
			return i;
		} catch( NumberFormatException e ) {
			throw new InvalidLiteralException( getName(), lexicalForm );
		}
	}
}
