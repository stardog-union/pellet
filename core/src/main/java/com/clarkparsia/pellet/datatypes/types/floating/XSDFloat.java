package com.clarkparsia.pellet.datatypes.types.floating;

import aterm.ATermAppl;
import com.clarkparsia.pellet.datatypes.AbstractBaseDatatype;
import com.clarkparsia.pellet.datatypes.Datatype;
import com.clarkparsia.pellet.datatypes.RestrictedDatatype;
import com.clarkparsia.pellet.datatypes.exceptions.InvalidLiteralException;
import javax.xml.bind.DatatypeConverter;
import org.mindswap.pellet.utils.ATermUtils;
import org.mindswap.pellet.utils.Namespaces;

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
public class XSDFloat extends AbstractBaseDatatype<Float>
{

	private static final XSDFloat instance;

	static
	{
		instance = new XSDFloat();
	}

	public static XSDFloat getInstance()
	{
		return instance;
	}

	private final RestrictedFloatingPointDatatype<Float> dataRange;

	/**
	 * Private constructor forces use of {@link #getInstance()}
	 */
	private XSDFloat()
	{
		super(ATermUtils.makeTermAppl(Namespaces.XSD + "float"));
		dataRange = new RestrictedFloatingPointDatatype<>(this, IEEEFloatType.getInstance());
	}

	@Override
	public RestrictedDatatype<Float> asDataRange()
	{
		return dataRange;
	}

	@Override
	public ATermAppl getCanonicalRepresentation(final ATermAppl input) throws InvalidLiteralException
	{
		final Float f = getValue(input);
		final String canonicalForm = DatatypeConverter.printFloat(f);
		if (canonicalForm.equals(ATermUtils.getLiteralValue(input)))
			return input;
		else
			return ATermUtils.makeTypedLiteral(canonicalForm, getName());
	}

	@Override
	public ATermAppl getLiteral(final Object value)
	{
		if (value instanceof Float)
			return ATermUtils.makeTypedLiteral(DatatypeConverter.printFloat((Float) value), getName());
		else
			throw new IllegalArgumentException();
	}

	@Override
	public Datatype<?> getPrimitiveDatatype()
	{
		return this;
	}

	@Override
	public Float getValue(final ATermAppl literal) throws InvalidLiteralException
	{
		final String lexicalForm = getLexicalForm(literal);
		try
		{
			return DatatypeConverter.parseFloat(lexicalForm);
		}
		catch (final NumberFormatException e)
		{
			throw new InvalidLiteralException(getName(), lexicalForm);
		}
	}

	@Override
	public boolean isPrimitive()
	{
		return true;
	}
}
