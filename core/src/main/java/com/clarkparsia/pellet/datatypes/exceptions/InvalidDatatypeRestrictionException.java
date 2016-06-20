package com.clarkparsia.pellet.datatypes.exceptions;

import openllet.aterm.ATermAppl;
import org.mindswap.pellet.utils.ATermUtils;

/**
 * @author Evren Sirin
 */
public class InvalidDatatypeRestrictionException extends DatatypeReasonerException
{

	private static final long serialVersionUID = 3L;

	private final ATermAppl _datatype;

	public InvalidDatatypeRestrictionException(final ATermAppl datatype)
	{
		super("Invalid _datatype restriction on " + ATermUtils.toString(datatype));

		this._datatype = datatype;
	}

	public ATermAppl getDatatypeRestriction()
	{
		return _datatype;
	}
}
