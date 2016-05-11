package com.clarkparsia.pellet.datatypes.types.text;

import aterm.ATermAppl;
import com.clarkparsia.pellet.datatypes.AbstractBaseDatatype;
import com.clarkparsia.pellet.datatypes.Datatype;
import com.clarkparsia.pellet.datatypes.RestrictedDatatype;
import com.clarkparsia.pellet.datatypes.exceptions.InvalidLiteralException;
import org.mindswap.pellet.utils.ATermUtils;
import org.mindswap.pellet.utils.Namespaces;

/**
 * <p>
 * Copyright: Copyright (c) 2009
 * </p>
 * <p>
 * Company: Clark & Parsia, LLC. <http://www.clarkparsia.com>
 * </p>
 *
 * @author Héctor Pérez-Urbina
 */
public class XSDNormalizedString extends AbstractBaseDatatype<ATermAppl>
{

	private static final XSDNormalizedString instance = new XSDNormalizedString();
	private static final RDFPlainLiteral RDF_PLAIN_LITERAL = RDFPlainLiteral.getInstance();

	static
	{
		RestrictedTextDatatype.addPermittedDatatype(instance.getName());
	}

	public static XSDNormalizedString getInstance()
	{
		return instance;
	}

	private final RestrictedDatatype<ATermAppl> dataRange;

	private XSDNormalizedString()
	{
		super(ATermUtils.makeTermAppl(Namespaces.XSD + "normalizedString"));
		dataRange = new RestrictedTextDatatype(this, RestrictedTextDatatype.NORMALIZED_STRING);
	}

	@Override
	public RestrictedDatatype<ATermAppl> asDataRange()
	{
		return dataRange;
	}

	@Override
	public ATermAppl getCanonicalRepresentation(final ATermAppl input) throws InvalidLiteralException
	{
		return getValue(input);
	}

	@Override
	public ATermAppl getLiteral(final Object value)
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public Datatype<?> getPrimitiveDatatype()
	{
		return RDF_PLAIN_LITERAL;
	}

	@Override
	public ATermAppl getValue(final ATermAppl literal) throws InvalidLiteralException
	{
		final String lexicalForm = getLexicalForm(literal);
		return RDF_PLAIN_LITERAL.getCanonicalRepresentation(ATermUtils.makePlainLiteral(lexicalForm));
	}

	@Override
	public boolean isPrimitive()
	{
		return false;
	}
}
