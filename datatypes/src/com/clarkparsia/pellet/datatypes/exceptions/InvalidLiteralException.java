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
public class InvalidLiteralException extends DatatypeReasonerException {

	private static final long	serialVersionUID	= 1L;

	private final ATermAppl		dt;
	private final String		value;

	public InvalidLiteralException(ATermAppl dt, String value) {
		super( format( "'%s' is not in the lexical space of datatype %s", value, dt.getName() ) );
		this.dt = dt;
		this.value = value;
	}

	public InvalidLiteralException(ATermAppl dt, String value, Throwable cause) {
		this( dt, value );
		initCause( cause );
	}

	public ATermAppl getDatatype() {
		return dt;
	}

	public String getValue() {
		return value;
	}
}
