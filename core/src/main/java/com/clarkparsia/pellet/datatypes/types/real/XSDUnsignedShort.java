package com.clarkparsia.pellet.datatypes.types.real;

import javax.xml.bind.DatatypeConverter;

import org.mindswap.pellet.utils.ATermUtils;
import org.mindswap.pellet.utils.Namespaces;

import com.clarkparsia.pellet.datatypes.exceptions.InvalidLiteralException;

/**
 * <p>
 * Title: <code>xsd:unsignedShort</code>
 * </p>
 * <p>
 * Description: Singleton implementation of <code>xsd:unsignedShort</code>
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
public class XSDUnsignedShort extends AbstractDerivedIntegerType {

	private static final XSDUnsignedShort	instance;
	private static final int				MAX_VALUE;

	static {
		MAX_VALUE = 65535;

		instance = new XSDUnsignedShort();
	}

	public static XSDUnsignedShort getInstance() {
		return instance;
	}

	private XSDUnsignedShort() {
		super( ATermUtils.makeTermAppl( Namespaces.XSD + "unsignedShort" ), 0, MAX_VALUE );
	}

	@Override
	protected Number fromLexicalForm(String lexicalForm) throws InvalidLiteralException {
		try {
			final int i = DatatypeConverter.parseInt( lexicalForm );
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
