// Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package com.clarkparsia.pellet.rules.builtins;

import java.math.BigDecimal;
import java.math.BigInteger;

import org.mindswap.pellet.exceptions.InternalReasonerException;

/**
 * <p>
 * Title: Numeric Comparison Visitor
 * </p>
 * <p>
 * Description: Compares two numbers of the same type against each other for equality.
 * Throws an exception if there are more than two arguments.
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

public class NumericComparisonVisitor implements NumericVisitor {
	
	private int result;
	
	private void argCheck(Number[] args) {
		if ( args.length != 2 )
			throw new InternalReasonerException( "Wrong number of arguments to comparison visitor." );
	}
	
	public int getComparison() { return result; }
	
	public void visit(BigDecimal[] args) {
		argCheck( args );
		result = args[0].compareTo( args[1] );
	}

	public void visit(BigInteger[] args) {
		argCheck( args );
		result = args[0].compareTo( args[1] );
	}

	public void visit(Double[] args) {
		argCheck( args );
		result = args[0].compareTo( args[1] );
	}

	public void visit(Float[] args) {
		argCheck( args );
		result = args[0].compareTo( args[1] );
	}

}
