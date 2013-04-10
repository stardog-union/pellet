package com.clarkparsia.pellet.datatypes.types.bool;

import aterm.ATermAppl;

import com.clarkparsia.pellet.datatypes.AbstractBaseDatatype;
import com.clarkparsia.pellet.datatypes.Datatype;
import com.clarkparsia.pellet.datatypes.Datatypes;
import com.clarkparsia.pellet.datatypes.RestrictedDatatype;
import com.clarkparsia.pellet.datatypes.exceptions.InvalidLiteralException;
import com.clarkparsia.pellet.utils.TermFactory;

/**
 * <p>
 * Title: <code>xsd:boolean</code>
 * </p>
 * <p>
 * Description: Singleton implementation of <code>xsd:boolean</code> datatype
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
public class XSDBoolean extends AbstractBaseDatatype<Boolean> {

	private static final ATermAppl CANONICAL_FALSE_TERM;
	private static final ATermAppl CANONICAL_TRUE_TERM;
	private static final XSDBoolean instance;
	private static final ATermAppl NAME;

	static {
		NAME = Datatypes.BOOLEAN;
		CANONICAL_TRUE_TERM = TermFactory.literal( true );
		CANONICAL_FALSE_TERM = TermFactory.literal( false );

		instance = new XSDBoolean();
	}

	public static XSDBoolean getInstance() {
		return instance;
	}

	private final RestrictedBooleanDatatype	dataRange;

	private XSDBoolean() {
		super( NAME );
		dataRange = new RestrictedBooleanDatatype( this );
	}

	public RestrictedDatatype<Boolean> asDataRange() {
		return dataRange;
	}

	public ATermAppl getCanonicalRepresentation(ATermAppl input) throws InvalidLiteralException {
		if( input == CANONICAL_FALSE_TERM || input == CANONICAL_TRUE_TERM )
			return input;

		return getLiteral( getValue( input ) );
	}

	public ATermAppl getLiteral(Object value) {
		if( value instanceof Boolean ) {
			return (Boolean) value
				? CANONICAL_TRUE_TERM
				: CANONICAL_FALSE_TERM;
		}
		else
			throw new IllegalArgumentException();
	}

	public Datatype<?> getPrimitiveDatatype() {
		return this;
	}

	public Boolean getValue(ATermAppl literal) throws InvalidLiteralException {
		final String lexicalForm = getLexicalForm( literal ).trim();
		if( lexicalForm.equals("true") || lexicalForm.equals("1"))
			return Boolean.TRUE;
		else if( lexicalForm.equals("false") || lexicalForm.equals("0"))
			return Boolean.FALSE;
		else
			throw new InvalidLiteralException( getName(), lexicalForm );
	}

	public boolean isPrimitive() {
		return true;
	}

}
