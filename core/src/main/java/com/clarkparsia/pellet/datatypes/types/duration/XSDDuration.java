package com.clarkparsia.pellet.datatypes.types.duration;

import static com.clarkparsia.pellet.datatypes.types.datetime.RestrictedTimelineDatatype.getDatatypeFactory;

import com.clarkparsia.pellet.datatypes.AbstractBaseDatatype;
import com.clarkparsia.pellet.datatypes.Datatype;
import com.clarkparsia.pellet.datatypes.RestrictedDatatype;
import com.clarkparsia.pellet.datatypes.exceptions.InvalidLiteralException;
import javax.xml.datatype.Duration;
import openllet.aterm.ATermAppl;
import org.mindswap.pellet.utils.ATermUtils;
import org.mindswap.pellet.utils.Namespaces;

/**
 * <p>
 * Title: <code>xsd:string</code>
 * </p>
 * <p>
 * Description: Singleton implementation of <code>xsd:anyURI</code> datatype
 * </p>
 * <p>
 * Copyright: Copyright (c) 2009
 * </p>
 * <p>
 * Company: Clark & Parsia, LLC. <http://www.clarkparsia.com>
 * </p>
 *
 * @author Evren Sirin
 */
public class XSDDuration extends AbstractBaseDatatype<Duration>
{

	private static final XSDDuration instance;
	static final ATermAppl NAME;

	static
	{
		NAME = ATermUtils.makeTermAppl(Namespaces.XSD + "duration");
		instance = new XSDDuration();
	}

	public static XSDDuration getInstance()
	{
		return instance;
	}

	private final RestrictedDatatype<Duration> dataRange;

	private XSDDuration()
	{
		super(NAME);
		dataRange = new RestrictedDurationDatatype(this);
	}

	@Override
	public RestrictedDatatype<Duration> asDataRange()
	{
		return dataRange;
	}

	@Override
	public ATermAppl getCanonicalRepresentation(final ATermAppl input) throws InvalidLiteralException
	{
		return ATermUtils.makeTypedLiteral(getValue(input).toString(), NAME);
	}

	@Override
	public ATermAppl getLiteral(final Object value)
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public Datatype<?> getPrimitiveDatatype()
	{
		return this;
	}

	@Override
	public Duration getValue(final ATermAppl literal) throws InvalidLiteralException
	{
		final String lexicalForm = getLexicalForm(literal);
		try
		{
			final Duration c = getDatatypeFactory().newDuration(lexicalForm);

			return c;
		}
		catch (final IllegalArgumentException e)
		{
			/*
			 * newXMLGregorianCalendar will throw an IllegalArgumentException if
			 * the lexical form is not one of the XML Schema datetime types
			 */
			throw new InvalidLiteralException(getName(), lexicalForm, e);
		}
		catch (final IllegalStateException e)
		{
			/*
			 * getXMLSchemaType will throw an IllegalStateException if the
			 * combination of fields set in the calendar object doesn't match
			 * one of the XML Schema datetime types
			 */
			throw new InvalidLiteralException(getName(), lexicalForm, e);
		}
	}

	@Override
	public boolean isPrimitive()
	{
		return false;
	}
}
