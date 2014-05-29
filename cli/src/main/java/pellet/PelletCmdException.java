// Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public
// License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of
// proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package pellet;

/**
 * <p>
 * Title: PelletCmdException
 * </p>
 * <p>
 * Description:
 * </p>
 * <p>
 * Copyright: Copyright (c) 2008
 * </p>
 * <p>
 * Company: Clark & Parsia, LLC. <http://www.clarkparsia.com>
 * </p>
 * 
 * @author Markus Stocker
 */
public class PelletCmdException extends RuntimeException {

	/**
	 * Create an exception with the given error message.
	 * 
	 * @param msg
	 */
	public PelletCmdException(String msg) {
		super( msg );
	}
	
	public PelletCmdException(Throwable cause) {
		super( cause );
	}
}
