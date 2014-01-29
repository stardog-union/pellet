package com.clarkparsia.pellet.datatypes.exceptions;

import org.mindswap.pellet.utils.ATermUtils;

import aterm.ATermAppl;

/**
 * @author Evren Sirin
 */
public class InvalidDatatypeRestrictionException extends DatatypeReasonerException {

	private static final long	serialVersionUID	= 3L;

	private final ATermAppl		datatype;

	public InvalidDatatypeRestrictionException(ATermAppl datatype) {
		super( "Invalid datatype restriction on " + ATermUtils.toString(datatype) );
		
		this.datatype = datatype;
	}

	public ATermAppl getDatatypeRestriction() {
		return datatype;
	}
}
