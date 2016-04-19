package com.clarkparsia.pellet.datatypes.exceptions;

import aterm.ATermAppl;
import org.mindswap.pellet.utils.ATermUtils;

/**
 * @author Evren Sirin
 */
public class InvalidDatatypeRestrictionException extends DatatypeReasonerException
{

	private static final long serialVersionUID = 3L;

	private final ATermAppl datatype;

	public InvalidDatatypeRestrictionException(final ATermAppl datatype)
	{
		super("Invalid datatype restriction on " + ATermUtils.toString(datatype));

		this.datatype = datatype;
	}

	public ATermAppl getDatatypeRestriction()
	{
		return datatype;
	}
}
