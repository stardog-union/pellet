package com.clarkparsia.pellet.datatypes.exceptions;

import static java.lang.String.format;

import aterm.ATermAppl;

/**
 * <p>
 * Title: Invalid Literal Exception
 * </p>
 * <p>
 * Description:
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
public class InvalidLiteralException extends DatatypeReasonerException
{

	private static final long serialVersionUID = 1L;

	private final ATermAppl _dt;
	private final String _value;

	public InvalidLiteralException(final ATermAppl dt, final String value)
	{
		super(format("'%s' is not in the lexical space of datatype %s", value, dt.getName()));
		this._dt = dt;
		this._value = value;
	}

	public InvalidLiteralException(final ATermAppl dt, final String value, final Throwable cause)
	{
		this(dt, value);
		initCause(cause);
	}

	public ATermAppl getDatatype()
	{
		return _dt;
	}

	public String getValue()
	{
		return _value;
	}
}
