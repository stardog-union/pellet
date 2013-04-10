// Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package com.clarkparsia.pellet.rules.builtins;

import java.math.BigDecimal;
import java.math.BigInteger;

/**
 * <p>
 * Title: Numeric Visitor
 * </p>
 * <p>
 * Description: Visitor interface for operations on arrays of numbers.
 * </p>
 * <p>
 * Copyright: Copyright (c) 2008
 * </p>
 * <p>
 * Company: Clark & Parsia, LLC. <http://www.clarkparsia.com>
 * </p>
 * 
 * @author Ron Alford
 */
public interface NumericVisitor {
	
	public void visit( BigDecimal[] args);
	public void visit( BigInteger[] args );
	public void visit( Double[] args);
	public void visit( Float[] args);
	
}
