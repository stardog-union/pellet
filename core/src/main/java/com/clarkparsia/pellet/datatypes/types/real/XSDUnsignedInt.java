package com.clarkparsia.pellet.datatypes.types.real;

import com.clarkparsia.pellet.datatypes.exceptions.InvalidLiteralException;
import javax.xml.bind.DatatypeConverter;
import org.mindswap.pellet.utils.ATermUtils;
import org.mindswap.pellet.utils.Namespaces;

/**
 * <p>
 * Title: <code>xsd:unsignedInt</code>
 * </p>
 * <p>
 * Description: Singleton implementation of <code>xsd:unsignedInt</code> datatype
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
public class XSDUnsignedInt extends AbstractDerivedIntegerType
{

	private static final XSDUnsignedInt instance;
	private static final long MAX_VALUE;

	static
	{
		MAX_VALUE = 4294967295l;

		instance = new XSDUnsignedInt();
	}

	public static XSDUnsignedInt getInstance()
	{
		return instance;
	}

	private XSDUnsignedInt()
	{
		super(ATermUtils.makeTermAppl(Namespaces.XSD + "unsignedInt"), 0, MAX_VALUE);
	}

	@Override
	protected Number fromLexicalForm(final String lexicalForm) throws InvalidLiteralException
	{
		try
		{
			final long l = DatatypeConverter.parseLong(lexicalForm);
			if (l < 0)
				throw new InvalidLiteralException(getName(), lexicalForm);
			if (l > MAX_VALUE)
				throw new InvalidLiteralException(getName(), lexicalForm);
			return l;
		}
		catch (final NumberFormatException e)
		{
			throw new InvalidLiteralException(getName(), lexicalForm);
		}
	}
}
