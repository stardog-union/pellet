package com.clarkparsia.pellet.datatypes.types.real;

import static java.lang.String.format;

import java.math.BigDecimal;
import java.util.logging.Logger;

import javax.xml.bind.DatatypeConverter;

import org.mindswap.pellet.utils.ATermUtils;
import org.mindswap.pellet.utils.Namespaces;

import aterm.ATermAppl;

import com.clarkparsia.pellet.datatypes.AbstractBaseDatatype;
import com.clarkparsia.pellet.datatypes.Datatype;
import com.clarkparsia.pellet.datatypes.OWLRealUtils;
import com.clarkparsia.pellet.datatypes.RestrictedDatatype;
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
public class XSDDecimal extends AbstractBaseDatatype<Number> {

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

	/**
	 * Private constructor forces use of {@link #getInstance()}
	 */
	private XSDDecimal() {
		super( ATermUtils.makeTermAppl( Namespaces.XSD + "decimal" ) );

		dataRange = new RestrictedRealDatatype( this, IntegerInterval.allIntegers(),
				ContinuousRealInterval.allReals(), null );
	}

	public RestrictedDatatype<Number> asDataRange() {
		return dataRange;
	}

	public ATermAppl getCanonicalRepresentation(ATermAppl input) throws InvalidLiteralException {
		final String lexicalForm = getLexicalForm( input );
		try {
			final BigDecimal d = DatatypeConverter.parseDecimal( lexicalForm );
			/*
			 * TODO: Determine if this is, in fact a functional mapping
			 */
			final String canonicalForm = DatatypeConverter.printDecimal( d );
			if( canonicalForm.equals( lexicalForm ) )
				return input;
			else
				return ATermUtils.makeTypedLiteral( canonicalForm, getName() );
		} catch( NumberFormatException e ) {
			log.severe( format( "Number format exception (%s) cause while parsing decimal %s", e
					.getMessage(), lexicalForm ) );
			throw new InvalidLiteralException( getName(), lexicalForm );
		}
	}

	public ATermAppl getLiteral(Object value) {
		if( dataRange.contains( value ) )
			return ATermUtils.makeTypedLiteral( OWLRealUtils.print( (Number) value ), getName() );
		else
			throw new IllegalArgumentException();
	}

	public Datatype<?> getPrimitiveDatatype() {
		return this;
	}

	public Number getValue(ATermAppl literal) throws InvalidLiteralException {
		final String lexicalForm = getLexicalForm( literal );
		try {
			return OWLRealUtils.getCanonicalObject( DatatypeConverter.parseDecimal( lexicalForm ) );
		} catch( NumberFormatException e ) {
			throw new InvalidLiteralException( getName(), lexicalForm );
		}
	}

	public boolean isPrimitive() {
		return false;
	}

}
