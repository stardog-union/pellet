package com.clarkparsia.pellet.datatypes.types.real;

import com.clarkparsia.pellet.datatypes.exceptions.InvalidLiteralException;
import java.math.BigInteger;
import javax.xml.bind.DatatypeConverter;
import org.mindswap.pellet.utils.ATermUtils;
import org.mindswap.pellet.utils.Namespaces;

/**
 * <p>
 * Title: <code>xsd:nonNegativeInteger</code>
 * </p>
 * <p>
 * Description: Singleton implementation of <code>xsd:nonNegativeInteger</code> datatype
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
public class XSDNonNegativeInteger extends AbstractDerivedIntegerType
{

	private static final XSDNonNegativeInteger instance = new XSDNonNegativeInteger();

	public static XSDNonNegativeInteger getInstance()
	{
		return instance;
	}

	private XSDNonNegativeInteger()
	{
		super(ATermUtils.makeTermAppl(Namespaces.XSD + "nonNegativeInteger"), 0, null);
	}

	@Override
	protected Number fromLexicalForm(final String lexicalForm) throws InvalidLiteralException
	{
		try
		{
			final BigInteger n = DatatypeConverter.parseInteger(lexicalForm);
			if (BigInteger.ZERO.compareTo(n) > 0)
				throw new InvalidLiteralException(getName(), lexicalForm);
			return n;
		}
		catch (final NumberFormatException e)
		{
			throw new InvalidLiteralException(getName(), lexicalForm);
		}
	}
}
