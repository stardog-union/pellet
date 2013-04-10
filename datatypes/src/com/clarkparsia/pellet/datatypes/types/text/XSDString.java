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
 * Title: <code>xsd:string</code>
 * </p>
 * <p>
 * Description: Singleton implementation of <code>xsd:string</code> datatype
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
public class XSDString extends AbstractBaseDatatype<ATermAppl> {

	private static final XSDString			instance;
	private static final RDFPlainLiteral	RDF_PLAIN_LITERAL;

	static {
		RDF_PLAIN_LITERAL = RDFPlainLiteral.getInstance();

		instance = new XSDString();
		RestrictedTextDatatype.addPermittedDatatype( instance.getName() );
	}

	public static XSDString getInstance() {
		return instance;
	}

	private final RestrictedDatatype<ATermAppl>	dataRange;

	private XSDString() {
		super( ATermUtils.makeTermAppl( Namespaces.XSD + "string" ) );
		dataRange = new RestrictedTextDatatype( this, false );
	}

	public RestrictedDatatype<ATermAppl> asDataRange() {
		return dataRange;
	}

	public ATermAppl getCanonicalRepresentation(ATermAppl input) throws InvalidLiteralException {
		return getValue( input );
	}

	public ATermAppl getLiteral(Object value) {
		throw new UnsupportedOperationException();
	}

	public Datatype<?> getPrimitiveDatatype() {
		return RDF_PLAIN_LITERAL;
	}

	public ATermAppl getValue(ATermAppl literal) throws InvalidLiteralException {
		final String lexicalForm = getLexicalForm( literal );
		return RDF_PLAIN_LITERAL.getCanonicalRepresentation(
				ATermUtils.makePlainLiteral( lexicalForm ) );
	}

	public boolean isPrimitive() {
		return false;
	}
}
