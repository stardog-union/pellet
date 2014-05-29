// Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package com.clarkparsia.pellint.format;

import com.clarkparsia.pellint.model.Lint;

/**
 * <p>
 * Title: Lint String Formatting Interface
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
 * @author Harris Lin
 */
public interface LintFormat {
	
	//Could add a new interface: StringBuilder format(StringBuilder builder, Lint lint)
	//if this turns out to be too inefficient - i.e. creating lots of StringBuilder's
	//all the time.
	public String format(Lint lint);
}
