package com.clarkparsia.pellet.datatypes.types.real;

import com.clarkparsia.pellet.datatypes.exceptions.InvalidLiteralException;
import javax.xml.bind.DatatypeConverter;
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
public class XSDInteger extends AbstractDerivedIntegerType
{

	private static final XSDInteger instance = new XSDInteger();

	public static XSDInteger getInstance()
	{
		return instance;
	}

	private XSDInteger()
	{
		super(ATermUtils.makeTermAppl(Namespaces.XSD + "integer"), null, null);
	}

	@Override
	protected Number fromLexicalForm(final String lexicalForm) throws InvalidLiteralException
	{
		try
		{
			return DatatypeConverter.parseInteger(lexicalForm);
		}
		catch (final NumberFormatException e)
		{
			throw new InvalidLiteralException(getName(), lexicalForm);
		}
	}
}
