package com.clarkparsia.pellet.datatypes.types.floating;

import javax.xml.bind.DatatypeConverter;

import org.mindswap.pellet.utils.ATermUtils;
import org.mindswap.pellet.utils.Namespaces;

import aterm.ATermAppl;

import com.clarkparsia.pellet.datatypes.AbstractBaseDatatype;
import com.clarkparsia.pellet.datatypes.Datatype;
import com.clarkparsia.pellet.datatypes.RestrictedDatatype;
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
public class XSDDouble extends AbstractBaseDatatype<Double> {

	private static final XSDDouble	instance;

	static {
		instance = new XSDDouble();
	}

	public static XSDDouble getInstance() {
		return instance;
	}

	private final RestrictedFloatingPointDatatype<Double>	dataRange;

	/**
	 * Private constructor forces use of {@link #getInstance()}
	 */
	private XSDDouble() {
		super( ATermUtils.makeTermAppl( Namespaces.XSD + "double" ) );
		dataRange = new RestrictedFloatingPointDatatype<Double>( this, IEEEDoubleType.getInstance() );
	}

	public RestrictedDatatype<Double> asDataRange() {
		return dataRange;
	}

	public ATermAppl getCanonicalRepresentation(ATermAppl input) throws InvalidLiteralException {
		final Double f = getValue( input );
		final String canonicalForm = DatatypeConverter.printDouble( f );
		if( canonicalForm.equals( ATermUtils.getLiteralValue( input ) ) )
			return input;
		else
			return ATermUtils.makeTypedLiteral( canonicalForm, getName() );
	}

	public ATermAppl getLiteral(Object value) {
		if( value instanceof Double )
			return ATermUtils.makeTypedLiteral( DatatypeConverter.printDouble( (Double) value ),
					getName() );
		else
			throw new IllegalArgumentException();
	}

	public Datatype<?> getPrimitiveDatatype() {
		return this;
	}

	public Double getValue(ATermAppl literal) throws InvalidLiteralException {
		final String lexicalForm = getLexicalForm( literal );
		try {
			return DatatypeConverter.parseDouble( lexicalForm );
		} catch( NumberFormatException e ) {
			throw new InvalidLiteralException( getName(), lexicalForm );
		}
	}

	public boolean isPrimitive() {
		return true;
	}

}
