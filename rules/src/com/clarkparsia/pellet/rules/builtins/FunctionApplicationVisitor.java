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
 * Title: Function Application Visitor
 * </p>
 * <p>
 * Description: Visitor to apply numeric functions.
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
public class FunctionApplicationVisitor implements NumericVisitor {
	
	NumericFunction function;
	Number result;
	
	public FunctionApplicationVisitor( NumericFunction function ) {
		this( function, null );
	}
	
	/**
	 * Takes a function and an optionally null expected value to compare against the function result.
	 * If the expected value is not null, the result and value will be promoted to the same 
	 * type and checked for equality.
	 */
	public FunctionApplicationVisitor( NumericFunction function, Number expected ) {
		this.function = function;
		this.result = expected;
	}
	
	/**
	 * Returns the result of the function application. If the application was
	 * a failure, the result will be null. If the expected value was non-null
	 * and matched the result once both were promoted, the result will be the 
	 * expected value (unpromoted).
	 */
	public Number getResult() { return result; }
	
	private void testAndSetResult( Number theResult ) {
		if (result == null) {
			result = theResult;
		} else {

			NumericComparisonVisitor visitor = new NumericComparisonVisitor();
			NumericPromotion promoter = new NumericPromotion();
			promoter.promote(result, theResult);
			promoter.accept(visitor);

			if (visitor.getComparison() == 0) {
				result = theResult;
			} else {
				result = null;
			}

		}
	}
	
	public void visit(BigDecimal[] args) {
		testAndSetResult( function.apply( args ) );
	}

	public void visit(BigInteger[] args) {
		testAndSetResult( function.apply( args ) );
	}

	public void visit(Double[] args) {
		testAndSetResult( function.apply( args ) );
	}

	public void visit(Float[] args) {
		testAndSetResult( function.apply( args ) );
	}

}
