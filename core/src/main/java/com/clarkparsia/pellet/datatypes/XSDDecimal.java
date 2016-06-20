package com.clarkparsia.pellet.datatypes;

import static java.lang.String.format;

import com.clarkparsia.pellet.datatypes.exceptions.InvalidLiteralException;
import java.math.BigDecimal;
import java.util.logging.Logger;
import javax.xml.bind.DatatypeConverter;
import openllet.aterm.ATermAppl;
import openllet.shared.tools.Log;
import org.mindswap.pellet.utils.ATermUtils;
import org.mindswap.pellet.utils.Namespaces;

/**
 * <p>
 * Title: <code>xsd:decimal</code>
 * </p>
 * <p>
 * Description: Singleton implementation of <code>xsd:decimal</code> datatype
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
public class XSDDecimal implements Datatype<Number>
{

	private static final XSDDecimal instance = new XSDDecimal();
	private static final Logger _logger = Log.getLogger(XSDDecimal.class);

	public static XSDDecimal getInstance()
	{
		return instance;
	}

	private final RestrictedRealDatatype dataRange;
	private final int hashCode;
	private final ATermAppl name;

	/**
	 * Private constructor forces use of {@link #getInstance()}
	 */
	private XSDDecimal()
	{
		name = ATermUtils.makeTermAppl(Namespaces.XSD + "decimal");
		hashCode = name.hashCode();

		dataRange = new RestrictedRealDatatype(this, OWLRealInterval.allReals());
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
			final BigDecimal d = DatatypeConverter.parseDecimal(lexicalForm);
			/*
			 * TODO: Determine if this is, in fact a functional mapping
			 */
			final String canonicalForm = DatatypeConverter.printDecimal(d);
			if (canonicalForm.equals(lexicalForm))
				return input;
			else
				return ATermUtils.makeTypedLiteral(canonicalForm, name);
		}
		catch (final NumberFormatException e)
		{
			_logger.severe(format("Number format exception (%s) cause while parsing decimal %s", e.getMessage(), lexicalForm));
			throw new InvalidLiteralException(name, lexicalForm);
		}
	}

	@Override
	public ATermAppl getLiteral(final Object value)
	{
		if (dataRange.contains(value))
			return ATermUtils.makeTypedLiteral(OWLRealUtils.print((Number) value), name);
		else
			throw new IllegalArgumentException();
	}

	@Override
	public ATermAppl getName()
	{
		return name;
	}

	@Override
	public Datatype<?> getPrimitiveDatatype()
	{
		return this;
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
			return OWLRealUtils.getCanonicalObject(DatatypeConverter.parseDecimal(lexicalForm));
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
		return true;
	}

}
