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
 * Title: <code>xsd:float</code>
 * </p>
 * <p>
 * Description: Singleton implementation of <code>xsd:float</code> datatype
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
public class XSDFloat extends AbstractBaseDatatype<Float> {

	private static final XSDFloat	instance;

	static {
		instance = new XSDFloat();
	}

	public static XSDFloat getInstance() {
		return instance;
	}

	private final RestrictedFloatingPointDatatype<Float>	dataRange;

	/**
	 * Private constructor forces use of {@link #getInstance()}
	 */
	private XSDFloat() {
		super( ATermUtils.makeTermAppl( Namespaces.XSD + "float" ) );
		dataRange = new RestrictedFloatingPointDatatype<Float>( this, IEEEFloatType.getInstance() );
	}

	public RestrictedDatatype<Float> asDataRange() {
		return dataRange;
	}

	public ATermAppl getCanonicalRepresentation(ATermAppl input) throws InvalidLiteralException {
		final Float f = getValue( input );
		final String canonicalForm = DatatypeConverter.printFloat( f );
		if( canonicalForm.equals( ATermUtils.getLiteralValue( input ) ) )
			return input;
		else
			return ATermUtils.makeTypedLiteral( canonicalForm, getName() );
	}

	public ATermAppl getLiteral(Object value) {
		if( value instanceof Float )
			return ATermUtils.makeTypedLiteral( DatatypeConverter.printFloat( (Float) value ),
					getName() );
		else
			throw new IllegalArgumentException();
	}

	public Datatype<?> getPrimitiveDatatype() {
		return this;
	}

	public Float getValue(ATermAppl literal) throws InvalidLiteralException {
		final String lexicalForm = getLexicalForm( literal );
		try {
			return DatatypeConverter.parseFloat( lexicalForm );
		} catch( NumberFormatException e ) {
			throw new InvalidLiteralException( getName(), lexicalForm );
		}
	}

	public boolean isPrimitive() {
		return true;
	}
}
