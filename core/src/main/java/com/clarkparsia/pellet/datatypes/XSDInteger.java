package com.clarkparsia.pellet.datatypes;

import static java.lang.String.format;

import com.clarkparsia.pellet.datatypes.exceptions.InvalidLiteralException;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;
import javax.xml.bind.DatatypeConverter;
import openllet.aterm.ATermAppl;
import openllet.shared.tools.Log;
import org.mindswap.pellet.utils.ATermUtils;
import org.mindswap.pellet.utils.Namespaces;

/**
 * <p>
 * Title: <code>xsd:integer</code>
 * </p>
 * <p>
 * Description: Singleton implementation of <code>xsd:integer</code> datatype
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
public class XSDInteger implements Datatype<Number>
{

	private static final XSDInteger instance = new XSDInteger();
	private static final Logger _logger = Log.getLogger(XSDInteger.class);

	public static XSDInteger getInstance()
	{
		return instance;
	}

	private final RestrictedRealDatatype dataRange;
	private final int hashCode;

	private final ATermAppl name;

	private XSDInteger()
	{
		name = ATermUtils.makeTermAppl(Namespaces.XSD + "integer");
		hashCode = name.hashCode();

		dataRange = new RestrictedRealDatatype(this, OWLRealInterval.allIntegers());
	}

	@Override
	public RestrictedDatatype<Number> asDataRange()
	{
		return dataRange;
	}

	@Override
	public boolean equals(final Object obj)
	{
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;

		return true;
	}

	@Override
	public ATermAppl getCanonicalRepresentation(final ATermAppl input) throws InvalidLiteralException
	{
		if (!ATermUtils.isLiteral(input))
			throw new IllegalArgumentException();
		if (!input.getArgument(ATermUtils.LIT_URI_INDEX).equals(name))
			throw new IllegalArgumentException();

		final String lexicalForm = ATermUtils.getLiteralValue(input);
		try
		{
			DatatypeConverter.parseInteger(lexicalForm);
		}
		catch (final NumberFormatException e)
		{
			_logger.severe(format("Number format exception (%s) cause while parsing integer %s", e.getMessage(), lexicalForm));
			throw new InvalidLiteralException(name, lexicalForm);
		}
		return XSDDecimal.getInstance().getCanonicalRepresentation(ATermUtils.makeTypedLiteral(lexicalForm, XSDDecimal.getInstance().getName()));
	}

	public List<Datatype<?>> getDerivedFrom()
	{
		return Collections.<Datatype<?>> singletonList(XSDDecimal.getInstance());
	}

	@Override
	public ATermAppl getLiteral(final Object value)
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public ATermAppl getName()
	{
		return name;
	}

	@Override
	public Datatype<?> getPrimitiveDatatype()
	{
		return XSDDecimal.getInstance();
	}

	@Override
	public Number getValue(final ATermAppl literal) throws InvalidLiteralException
	{
		if (!ATermUtils.isLiteral(literal))
			throw new IllegalArgumentException();
		if (!literal.getArgument(ATermUtils.LIT_URI_INDEX).equals(name))
			throw new IllegalArgumentException();

		final String lexicalForm = ATermUtils.getLiteralValue(literal);
		try
		{
			return OWLRealUtils.getCanonicalObject(DatatypeConverter.parseInteger(lexicalForm));
		}
		catch (final NumberFormatException e)
		{
			throw new InvalidLiteralException(name, lexicalForm, e);
		}
	}

	@Override
	public int hashCode()
	{
		return hashCode;
	}

	@Override
	public boolean isPrimitive()
	{
		return false;
	}

}
