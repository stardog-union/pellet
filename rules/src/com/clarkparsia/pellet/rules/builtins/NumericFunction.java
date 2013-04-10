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
 * Title: Numeric Function
 * </p>
 * <p>
 * Description: An interface for numeric function to implement for NumericBuiltIn
 * </p>
 * <p>
 * Copyright: Copyright (c) 2007
 * </p>
 * <p>
 * Company: Clark & Parsia, LLC. <http://www.clarkparsia.com>
 * </p>
 * 
 * @author Ron Alford
 */ 

public interface NumericFunction {

	public Number apply( BigDecimal... args );
	public Number apply( BigInteger... args );
	public Number apply( Double... args );
	public Number apply( Float... args );
	
}
