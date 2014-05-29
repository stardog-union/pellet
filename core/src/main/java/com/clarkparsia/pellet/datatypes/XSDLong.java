package com.clarkparsia.pellet.datatypes;

import static java.lang.String.format;

import java.util.logging.Logger;

import javax.xml.bind.DatatypeConverter;

import org.mindswap.pellet.utils.ATermUtils;
import org.mindswap.pellet.utils.Namespaces;

import aterm.ATermAppl;

import com.clarkparsia.pellet.datatypes.exceptions.InvalidLiteralException;

/**
 * <p>
 * Title: <code>xsd:long</code>
 * </p>
 * <p>
 * Description: Singleton implementation of <code>xsd:long</code> datatype
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
public class XSDLong implements Datatype<Number> {

	private static final XSDLong	instance;
	private static final Logger		log;
	static {
		log = Logger.getLogger( XSDLong.class.getCanonicalName() );

		instance = new XSDLong();
	}

	public static XSDLong getInstance() {
		return instance;
	}

	private final RestrictedDatatype<Number>	dataRange;
	private final int							hashCode;
	private final ATermAppl						name;

	private XSDLong() {
		name = ATermUtils.makeTermAppl( Namespaces.XSD + "long" );
		hashCode = name.hashCode();

		final OWLRealInterval i = new OWLRealInterval( Long.MIN_VALUE, Long.MAX_VALUE, true, true,
				OWLRealInterval.LineType.INTEGER_ONLY );
		dataRange = new RestrictedRealDatatype( this, i );
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
			DatatypeConverter.parseLong( lexicalForm );
		} catch( NumberFormatException e ) {
			log.severe( format( "Number format exception (%s) cause while parsing long %s", e
					.getMessage(), lexicalForm ) );
			throw new InvalidLiteralException( name, lexicalForm );
		}
		return XSDDecimal.getInstance().getCanonicalRepresentation(
				ATermUtils.makeTypedLiteral( lexicalForm, XSDDecimal.getInstance().getName() ) );
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
			return OWLRealUtils.getCanonicalObject( DatatypeConverter.parseLong( lexicalForm ) );
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
