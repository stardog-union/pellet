package com.clarkparsia.pellet.datatypes;

import static java.lang.String.format;

import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;

import javax.xml.bind.DatatypeConverter;

import org.mindswap.pellet.utils.ATermUtils;
import org.mindswap.pellet.utils.Namespaces;

import aterm.ATermAppl;

import com.clarkparsia.pellet.datatypes.exceptions.InvalidLiteralException;

/**
 * <p>
 * Title: <code>xsd:integer</code>
 * </p>
 * <p>
 * Description: Singleton implementation of <code>xsd:integer</code> datatype
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
public class XSDInteger implements Datatype<Number> {

	private static final XSDInteger	instance;
	private static final Logger		log;
	static {
		log = Logger.getLogger( XSDInteger.class.getCanonicalName() );

		instance = new XSDInteger();
	}

	public static XSDInteger getInstance() {
		return instance;
	}

	private final RestrictedRealDatatype	dataRange;
	private final int						hashCode;

	private final ATermAppl					name;

	private XSDInteger() {
		name = ATermUtils.makeTermAppl( Namespaces.XSD + "integer" );
		hashCode = name.hashCode();

		dataRange = new RestrictedRealDatatype( this, OWLRealInterval.allIntegers() );
	}

	public RestrictedDatatype<Number> asDataRange() {
		return dataRange;
	}

	@Override
	public boolean equals(Object obj) {
		if( this == obj )
			return true;
		if( obj == null )
			return false;
		if( getClass() != obj.getClass() )
			return false;

		return true;
	}

	public ATermAppl getCanonicalRepresentation(ATermAppl input) throws InvalidLiteralException {
		if( !ATermUtils.isLiteral( input ) )
			throw new IllegalArgumentException();
		if( !input.getArgument( ATermUtils.LIT_URI_INDEX ).equals( name ) )
			throw new IllegalArgumentException();

		final String lexicalForm = ATermUtils.getLiteralValue( input );
		try {
			DatatypeConverter.parseInteger( lexicalForm );
		} catch( NumberFormatException e ) {
			log.severe( format( "Number format exception (%s) cause while parsing integer %s", e
					.getMessage(), lexicalForm ) );
			throw new InvalidLiteralException( name, lexicalForm );
		}
		return XSDDecimal.getInstance().getCanonicalRepresentation(
				ATermUtils.makeTypedLiteral( lexicalForm, XSDDecimal.getInstance().getName() ) );
	}

	public List<Datatype<?>> getDerivedFrom() {
		return Collections.<Datatype<?>> singletonList( XSDDecimal.getInstance() );
	}

	public ATermAppl getLiteral(Object value) {
		throw new UnsupportedOperationException();
	}

	public ATermAppl getName() {
		return name;
	}

	public Datatype<?> getPrimitiveDatatype() {
		return XSDDecimal.getInstance();
	}

	public Number getValue(ATermAppl literal) throws InvalidLiteralException {
		if( !ATermUtils.isLiteral( literal ) )
			throw new IllegalArgumentException();
		if( !literal.getArgument( ATermUtils.LIT_URI_INDEX ).equals( name ) )
			throw new IllegalArgumentException();

		final String lexicalForm = ATermUtils.getLiteralValue( literal );
		try {
			return OWLRealUtils.getCanonicalObject( DatatypeConverter.parseInteger( lexicalForm ) );
		} catch( NumberFormatException e ) {
			throw new InvalidLiteralException( name, lexicalForm );
		}
	}

	@Override
	public int hashCode() {
		return hashCode;
	}

	public boolean isPrimitive() {
		return false;
	}

}
