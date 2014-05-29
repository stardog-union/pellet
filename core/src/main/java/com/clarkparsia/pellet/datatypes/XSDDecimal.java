package com.clarkparsia.pellet.datatypes;

import static java.lang.String.format;

import java.math.BigDecimal;
import java.util.logging.Logger;

import javax.xml.bind.DatatypeConverter;

import org.mindswap.pellet.utils.ATermUtils;
import org.mindswap.pellet.utils.Namespaces;

import aterm.ATermAppl;

import com.clarkparsia.pellet.datatypes.exceptions.InvalidLiteralException;

/**
 * <p>
 * Title: <code>xsd:decimal</code>
 * </p>
 * <p>
 * Description: Singleton implementation of <code>xsd:decimal</code> datatype
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
public class XSDDecimal implements Datatype<Number> {

	private static final XSDDecimal	instance;
	private static final Logger		log;

	static {
		log = Logger.getLogger( XSDDecimal.class.getCanonicalName() );

		instance = new XSDDecimal();
	}

	public static XSDDecimal getInstance() {
		return instance;
	}

	private final RestrictedRealDatatype	dataRange;
	private final int						hashCode;
	private final ATermAppl					name;

	/**
	 * Private constructor forces use of {@link #getInstance()}
	 */
	private XSDDecimal() {
		name = ATermUtils.makeTermAppl( Namespaces.XSD + "decimal" );
		hashCode = name.hashCode();

		dataRange = new RestrictedRealDatatype( this, OWLRealInterval.allReals() );
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
			final BigDecimal d = DatatypeConverter.parseDecimal( lexicalForm );
			/*
			 * TODO: Determine if this is, in fact a functional mapping
			 */
			final String canonicalForm = DatatypeConverter.printDecimal( d );
			if( canonicalForm.equals( lexicalForm ) )
				return input;
			else
				return ATermUtils.makeTypedLiteral( canonicalForm, name );
		} catch( NumberFormatException e ) {
			log.severe( format( "Number format exception (%s) cause while parsing decimal %s", e
					.getMessage(), lexicalForm ) );
			throw new InvalidLiteralException( name, lexicalForm );
		}
	}

	public ATermAppl getLiteral(Object value) {
		if( dataRange.contains( value ) )
			return ATermUtils.makeTypedLiteral( OWLRealUtils.print( (Number) value ), name );
		else
			throw new IllegalArgumentException();
	}

	public ATermAppl getName() {
		return name;
	}

	public Datatype<?> getPrimitiveDatatype() {
		return this;
	}

	public Number getValue(ATermAppl literal) throws InvalidLiteralException {
		if( !ATermUtils.isLiteral( literal ) )
			throw new IllegalArgumentException();
		if( !literal.getArgument( ATermUtils.LIT_URI_INDEX ).equals( name ) )
			throw new IllegalArgumentException();

		final String lexicalForm = ATermUtils.getLiteralValue( literal );
		try {
			return OWLRealUtils.getCanonicalObject( DatatypeConverter.parseDecimal( lexicalForm ) );
		} catch( NumberFormatException e ) {
			throw new InvalidLiteralException( name, lexicalForm );
		}
	}

	@Override
	public int hashCode() {
		return hashCode;
	}

	public boolean isPrimitive() {
		return true;
	}

}
