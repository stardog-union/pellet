package com.clarkparsia.pellet.datatypes.types.real;

import com.clarkparsia.pellet.datatypes.exceptions.InvalidLiteralException;
import java.math.BigInteger;
import javax.xml.bind.DatatypeConverter;
import org.mindswap.pellet.utils.ATermUtils;
import org.mindswap.pellet.utils.Namespaces;

/**
 * <p>
 * Title: <code>xsd:nonPositiveInteger</code>
 * </p>
 * <p>
 * Description: Singleton implementation of <code>xsd:nonPositiveInteger</code> datatype
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
public class XSDNonPositiveInteger extends AbstractDerivedIntegerType
{

	private static final XSDNonPositiveInteger instance = new XSDNonPositiveInteger();

	public static XSDNonPositiveInteger getInstance()
	{
		return instance;
	}

	private XSDNonPositiveInteger()
	{
		super(ATermUtils.makeTermAppl(Namespaces.XSD + "nonPositiveInteger"), null, 0);
	}

	@Override
	protected Number fromLexicalForm(final String lexicalForm) throws InvalidLiteralException
	{
		try
		{
			final BigInteger n = DatatypeConverter.parseInteger(lexicalForm);
			if (BigInteger.ZERO.compareTo(n) < 0)
				throw new InvalidLiteralException(getName(), lexicalForm);
			return n;
		}
		catch (final NumberFormatException e)
		{
			throw new InvalidLiteralException(getName(), lexicalForm, e);
		}
	}
}
