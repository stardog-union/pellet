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
 * Title: <code>xsd:double</code>
 * </p>
 * <p>
 * Description: Singleton implementation of <code>xsd:double</code> datatype
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
public class XSDDouble implements Datatype<Double> {

	private static final XSDDouble	instance;
	private static final Logger		log;

	static {
		log = Logger.getLogger( XSDDouble.class.getCanonicalName() );

		instance = new XSDDouble();
	}

	public static XSDDouble getInstance() {
		return instance;
	}

	private final RestrictedFloatingPointDatatype<Double>	dataRange;
	private final int										hashCode;
	private final ATermAppl									name;

	/**
	 * Private constructor forces use of {@link #getInstance()}
	 */
	private XSDDouble() {
		name = ATermUtils.makeTermAppl( Namespaces.XSD + "double" );
		hashCode = name.hashCode();

		dataRange = new RestrictedFloatingPointDatatype<Double>( this, IEEEDoubleType.getInstance() );
	}

	public RestrictedDatatype<Double> asDataRange() {
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
			final Double f = DatatypeConverter.parseDouble( lexicalForm );
			/*
			 * TODO: Determine if this is, in fact a functional mapping
			 */
			final String canonicalForm = DatatypeConverter.printDouble( f );
			if( canonicalForm.equals( lexicalForm ) )
				return input;
			else
				return ATermUtils.makeTypedLiteral( canonicalForm, name );
		} catch( NumberFormatException e ) {
			log.severe( format( "Number format exception (%s) cause while parsing double %s", e
					.getMessage(), lexicalForm ) );
			throw new InvalidLiteralException( name, lexicalForm );
		}
	}

	public ATermAppl getLiteral(Object value) {
		if( IEEEDoubleType.getInstance().isInstance( value ) )
			return ATermUtils.makeTypedLiteral( DatatypeConverter.printDouble( IEEEDoubleType
					.getInstance().cast( value ) ), name );
		else
			throw new IllegalArgumentException();
	}

	public ATermAppl getName() {
		return name;
	}

	public Datatype<?> getPrimitiveDatatype() {
		return this;
	}

	public Double getValue(ATermAppl literal) throws InvalidLiteralException {
		if( !ATermUtils.isLiteral( literal ) )
			throw new IllegalArgumentException();
		if( !literal.getArgument( ATermUtils.LIT_URI_INDEX ).equals( name ) )
			throw new IllegalArgumentException();

		final String lexicalForm = ATermUtils.getLiteralValue( literal );
		try {
			return DatatypeConverter.parseDouble( lexicalForm );
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
