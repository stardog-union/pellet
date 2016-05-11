package com.clarkparsia.pellet.datatypes.types.datetime;

import static com.clarkparsia.pellet.datatypes.types.datetime.RestrictedTimelineDatatype.getDatatypeFactory;

import aterm.ATermAppl;
import com.clarkparsia.pellet.datatypes.AbstractBaseDatatype;
import com.clarkparsia.pellet.datatypes.Datatype;
import com.clarkparsia.pellet.datatypes.exceptions.InvalidLiteralException;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.namespace.QName;
import org.mindswap.pellet.utils.ATermUtils;

public abstract class AbstractTimelineDatatype extends AbstractBaseDatatype<XMLGregorianCalendar>
{

	private final QName _schemaType;

	public AbstractTimelineDatatype(final ATermAppl name, final QName schemaType)
	{
		super(name);
		this._schemaType = schemaType;
	}

	@Override
	public ATermAppl getCanonicalRepresentation(final ATermAppl input) throws InvalidLiteralException
	{
		final XMLGregorianCalendar c = getValue(input);
		final String canonicalForm = c.toXMLFormat();
		if (canonicalForm.equals(ATermUtils.getLiteralValue(input)))
			return input;
		else
			return ATermUtils.makeTypedLiteral(canonicalForm, getName());
	}

	@Override
	public ATermAppl getLiteral(final Object value)
	{
		if (value instanceof XMLGregorianCalendar)
		{
			final XMLGregorianCalendar c = (XMLGregorianCalendar) value;
			if (!_schemaType.equals(c.getXMLSchemaType()))
				throw new IllegalArgumentException();
			return ATermUtils.makeTypedLiteral(c.toXMLFormat(), getName());
		}
		else
			throw new IllegalArgumentException();
	}

	@Override
	public Datatype<?> getPrimitiveDatatype()
	{
		return this;
	}

	@Override
	public XMLGregorianCalendar getValue(final ATermAppl literal) throws InvalidLiteralException
	{
		final String lexicalForm = getLexicalForm(literal);
		try
		{
			final XMLGregorianCalendar c = getDatatypeFactory().newXMLGregorianCalendar(lexicalForm);
			if (!_schemaType.equals(c.getXMLSchemaType()))
				throw new InvalidLiteralException(getName(), lexicalForm);

			return c;
		}
		catch (final IllegalArgumentException e)
		{
			/*
			 * newXMLGregorianCalendar will throw an IllegalArgumentException if
			 * the lexical form is not one of the XML Schema datetime types
			 */
			throw new InvalidLiteralException(getName(), lexicalForm);
		}
		catch (final IllegalStateException e)
		{
			/*
			 * getXMLSchemaType will throw an IllegalStateException if the
			 * combination of fields set in the calendar object doesn't match
			 * one of the XML Schema datetime types
			 */
			throw new InvalidLiteralException(getName(), lexicalForm);
		}
	}

	@Override
	public boolean isPrimitive()
	{
		return true;
	}

}
