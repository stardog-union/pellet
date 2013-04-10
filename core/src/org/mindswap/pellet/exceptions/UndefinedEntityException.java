// Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package org.mindswap.pellet.exceptions;

/**
 * <p>
 * Title: UndefinedEntityException
 * </p>
 * <p>
 * Description:
 * </p>
 * <p>
 * Copyright: Copyright (c) 2007
 * </p>
 * <p>
 * Company: Clark & Parsia, LLC. <http://www.clarkparsia.com>
 * </p>
 */
public class UndefinedEntityException extends PelletRuntimeException {

	private static final long	serialVersionUID	= 6971431370937629622L;

	public UndefinedEntityException() {
		super();
	}

	public UndefinedEntityException(String e) {
		super( e );
	}
}
