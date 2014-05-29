package com.clarkparsia.pellet.datatypes.types.datetime;

import static com.clarkparsia.pellet.datatypes.types.datetime.RestrictedTimelineDatatype.getDatatypeFactory;

import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.namespace.QName;

import org.mindswap.pellet.utils.ATermUtils;

import aterm.ATermAppl;

import com.clarkparsia.pellet.datatypes.AbstractBaseDatatype;
import com.clarkparsia.pellet.datatypes.Datatype;
import com.clarkparsia.pellet.datatypes.exceptions.InvalidLiteralException;

public abstract class AbstractTimelineDatatype extends AbstractBaseDatatype<XMLGregorianCalendar> {

	private final QName	schemaType;

	public AbstractTimelineDatatype(ATermAppl name, QName schemaType) {
		super( name );
		this.schemaType = schemaType;
	}

	public ATermAppl getCanonicalRepresentation(ATermAppl input) throws InvalidLiteralException {
		final XMLGregorianCalendar c = getValue( input );
		final String canonicalForm = c.toXMLFormat();
		if( canonicalForm.equals( ATermUtils.getLiteralValue( input ) ) )
			return input;
		else
			return ATermUtils.makeTypedLiteral( canonicalForm, getName() );
	}

	public ATermAppl getLiteral(Object value) {
		if( value instanceof XMLGregorianCalendar ) {
			final XMLGregorianCalendar c = (XMLGregorianCalendar) value;
			if( !schemaType.equals( c.getXMLSchemaType() ) )
				throw new IllegalArgumentException();
			return ATermUtils.makeTypedLiteral( c.toXMLFormat(), getName() );
		}
		else
			throw new IllegalArgumentException();
	}

	public Datatype<?> getPrimitiveDatatype() {
		return this;
	}

	public XMLGregorianCalendar getValue(ATermAppl literal) throws InvalidLiteralException {
		final String lexicalForm = getLexicalForm( literal );
		try {
			final XMLGregorianCalendar c = getDatatypeFactory().newXMLGregorianCalendar(
					lexicalForm );
			if( !schemaType.equals( c.getXMLSchemaType() ) )
				throw new InvalidLiteralException( getName(), lexicalForm );

			return c;
		} catch( IllegalArgumentException e ) {
			/*
			 * newXMLGregorianCalendar will throw an IllegalArgumentException if
			 * the lexical form is not one of the XML Schema datetime types
			 */
			throw new InvalidLiteralException( getName(), lexicalForm );
		} catch( IllegalStateException e ) {
			/*
			 * getXMLSchemaType will throw an IllegalStateException if the
			 * combination of fields set in the calendar object doesn't match
			 * one of the XML Schema datetime types
			 */
			throw new InvalidLiteralException( getName(), lexicalForm );
		}
	}

	public boolean isPrimitive() {
		return true;
	}

}