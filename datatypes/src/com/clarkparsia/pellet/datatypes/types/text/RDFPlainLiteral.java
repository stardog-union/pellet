package com.clarkparsia.pellet.datatypes.types.text;

import org.mindswap.pellet.utils.ATermUtils;
import org.mindswap.pellet.utils.Namespaces;

import aterm.ATermAppl;

import com.clarkparsia.pellet.datatypes.AbstractBaseDatatype;
import com.clarkparsia.pellet.datatypes.Datatype;
import com.clarkparsia.pellet.datatypes.RestrictedDatatype;
import com.clarkparsia.pellet.datatypes.exceptions.InvalidLiteralException;

/**
 * <p>
 * Title: <code>rdf:plainLiteral</code>
 * </p>
 * <p>
 * Description: Singleton implementation of <code>rdf:plainLiteral</code>
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
public class RDFPlainLiteral extends AbstractBaseDatatype<ATermAppl> {

	private static final RDFPlainLiteral	instance;

	static {
		instance = new RDFPlainLiteral();
		RestrictedTextDatatype.addPermittedDatatype( instance.getName() );
	}

	public static RDFPlainLiteral getInstance() {
		return instance;
	}

	private final RestrictedTextDatatype	dataRange;

	private RDFPlainLiteral() {
		super( ATermUtils.makeTermAppl( Namespaces.RDF + "PlainLiteral" ) );
		dataRange = new RestrictedTextDatatype( this, true );
	}

	public RestrictedDatatype<ATermAppl> asDataRange() {
		return dataRange;
	}

	public ATermAppl getCanonicalRepresentation(ATermAppl input) throws InvalidLiteralException {
		return getValue( input );
	}

	public ATermAppl getLiteral(Object value) {
		if( value instanceof ATermAppl ) {
			final ATermAppl literal = (ATermAppl) value;
			try {
				return getCanonicalRepresentation( literal );
			} catch( InvalidLiteralException e ) {
				throw new IllegalStateException( e );
			}
		}
		else
			throw new IllegalArgumentException();
	}

	public Datatype<?> getPrimitiveDatatype() {
		return this;
	}

	public ATermAppl getValue(ATermAppl literal) throws InvalidLiteralException {
		/*
		 * This call checks that the input is a literal and the datatype name
		 * matches. The return value is not needed because plain literal values
		 * cannot be canonicalized.
		 */
		getLexicalForm( literal );

		return literal;
	}

	public boolean isPrimitive() {
		return true;
	}
}
