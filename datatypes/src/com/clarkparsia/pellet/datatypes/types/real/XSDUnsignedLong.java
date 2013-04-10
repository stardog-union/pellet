package com.clarkparsia.pellet.datatypes.types.real;

import java.math.BigInteger;

import javax.xml.bind.DatatypeConverter;

import org.mindswap.pellet.utils.ATermUtils;
import org.mindswap.pellet.utils.Namespaces;

import com.clarkparsia.pellet.datatypes.exceptions.InvalidLiteralException;

/**
 * <p>
 * Title: <code>xsd:unsignedLong</code>
 * </p>
 * <p>
 * Description: Singleton implementation of <code>xsd:unsignedLong</code>
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
public class XSDUnsignedLong extends AbstractDerivedIntegerType {

	private static final XSDUnsignedLong	instance;
	private static final BigInteger			MAX_VALUE;

	static {
		MAX_VALUE = new BigInteger( "18446744073709551615" );

		instance = new XSDUnsignedLong();
	}

	public static XSDUnsignedLong getInstance() {
		return instance;
	}

	private XSDUnsignedLong() {
		super( ATermUtils.makeTermAppl( Namespaces.XSD + "unsignedLong" ), 0, MAX_VALUE );
	}

	@Override
	protected Number fromLexicalForm(String lexicalForm) throws InvalidLiteralException {
		try {
			final BigInteger n = DatatypeConverter.parseInteger( lexicalForm );
			if( BigInteger.ZERO.compareTo( n ) > 0 )
				throw new InvalidLiteralException( getName(), lexicalForm );
			if( MAX_VALUE.compareTo( n ) < 0 )
				throw new InvalidLiteralException( getName(), lexicalForm );
			return n;
		} catch( NumberFormatException e ) {
			throw new InvalidLiteralException( getName(), lexicalForm );
		}
	}
}
