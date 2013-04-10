package com.clarkparsia.pellet.datatypes.types.real;

import org.mindswap.pellet.utils.ATermUtils;
import org.mindswap.pellet.utils.Namespaces;

import aterm.ATermAppl;

import com.clarkparsia.pellet.datatypes.AbstractBaseDatatype;
import com.clarkparsia.pellet.datatypes.Datatype;
import com.clarkparsia.pellet.datatypes.RestrictedDatatype;
import com.clarkparsia.pellet.datatypes.exceptions.InvalidLiteralException;

/**
 * <p>
 * Title: <code>owl:real</code>
 * </p>
 * <p>
 * Description: Singleton implementation of <code>owl:real</code> datatype
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
public class OWLReal extends AbstractBaseDatatype<Number> {

	private static final OWLReal	instance;

	static {
		instance = new OWLReal();
	}

	public static OWLReal getInstance() {
		return instance;
	}

	private final RestrictedRealDatatype	dataRange;

	private OWLReal() {
		super( ATermUtils.makeTermAppl( Namespaces.OWL + "real" ) );

		dataRange = new RestrictedRealDatatype( this, IntegerInterval.allIntegers(),
				ContinuousRealInterval.allReals(), ContinuousRealInterval.allReals() );
	}

	public RestrictedDatatype<Number> asDataRange() {
		return dataRange;
	}

	public ATermAppl getCanonicalRepresentation(ATermAppl input) throws InvalidLiteralException {
		final String lexicalForm = getLexicalForm( input );
		throw new InvalidLiteralException( getName(), lexicalForm );
	}

	public ATermAppl getLiteral(Object value) {
		if( value instanceof Rational ) {
			return OWLRational.getInstance().getLiteral( value );
		}
		else if( value instanceof Number ) {
			try {
				return XSDDecimal.getInstance().getLiteral( value );
			} catch( IllegalArgumentException e ) {
				throw new IllegalArgumentException();
			}
		}
		else
			throw new IllegalArgumentException();
	}

	public Datatype<?> getPrimitiveDatatype() {
		return this;
	}

	public Number getValue(ATermAppl literal) throws InvalidLiteralException {
		final String lexicalForm = getLexicalForm( literal );
		throw new InvalidLiteralException( getName(), lexicalForm );
	}

	public boolean isPrimitive() {
		return true;
	}

}
