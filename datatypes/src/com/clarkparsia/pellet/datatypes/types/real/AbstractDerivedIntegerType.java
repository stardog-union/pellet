package com.clarkparsia.pellet.datatypes.types.real;

import org.mindswap.pellet.utils.ATermUtils;

import aterm.ATermAppl;

import com.clarkparsia.pellet.datatypes.AbstractBaseDatatype;
import com.clarkparsia.pellet.datatypes.Datatype;
import com.clarkparsia.pellet.datatypes.OWLRealUtils;
import com.clarkparsia.pellet.datatypes.RestrictedDatatype;
import com.clarkparsia.pellet.datatypes.exceptions.InvalidLiteralException;

/**
 * <p>
 * Title: Abstract derived integer type
 * </p>
 * <p>
 * Description: Base implementation for integer datatypes derived from
 * <code>xsd:decimal</code>
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
public abstract class AbstractDerivedIntegerType extends AbstractBaseDatatype<Number> {

	private static final XSDDecimal				XSD_DECIMAL;
	static {
		XSD_DECIMAL = XSDDecimal.getInstance();
	}
	private final RestrictedDatatype<Number>	dataRange;

	public AbstractDerivedIntegerType(ATermAppl name, Number lower, Number upper) {
		super( name );

		if( lower != null && !OWLRealUtils.isInteger( lower ) )
			throw new IllegalArgumentException();
		if( upper != null && !OWLRealUtils.isInteger( upper ) )
			throw new IllegalArgumentException();
		if( lower != null && upper != null && OWLRealUtils.compare( lower, upper ) > 0 )
			throw new IllegalArgumentException();

		final IntegerInterval i = new IntegerInterval( lower == null
			? null
			: OWLRealUtils.getCanonicalObject( lower ), upper == null
			? null
			: OWLRealUtils.getCanonicalObject( upper ) );
		dataRange = new RestrictedRealDatatype( this, i, null, null );
	}

	public RestrictedDatatype<Number> asDataRange() {
		return dataRange;
	}

	/**
	 * Parse and validate a lexical form of the literal.
	 * 
	 * @param lexicalForm
	 * @return a <code>Number</code> representation of the literal
	 * @throws InvalidLiteralException
	 *             if the literal form is invalid or the value is out of range
	 */
	protected abstract Number fromLexicalForm(String lexicalForm) throws InvalidLiteralException;

	public ATermAppl getCanonicalRepresentation(ATermAppl input) throws InvalidLiteralException {
		final String lexicalForm = getLexicalForm( input );
		fromLexicalForm( lexicalForm );
		return XSD_DECIMAL.getCanonicalRepresentation( ATermUtils.makeTypedLiteral( lexicalForm,
				XSD_DECIMAL.getName() ) );
	}

	public ATermAppl getLiteral(Object value) {
		throw new UnsupportedOperationException();
	}

	public Datatype<?> getPrimitiveDatatype() {
		return XSDDecimal.getInstance();
	}

	public Number getValue(ATermAppl literal) throws InvalidLiteralException {
		final String lexicalForm = getLexicalForm( literal );
		return OWLRealUtils.getCanonicalObject( fromLexicalForm( lexicalForm ) );
	}

	public boolean isPrimitive() {
		return false;
	}
}
