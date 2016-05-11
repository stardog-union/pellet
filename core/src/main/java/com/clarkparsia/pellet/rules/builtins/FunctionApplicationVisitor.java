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
public class FunctionApplicationVisitor implements NumericVisitor
{

	NumericFunction _function;
	Number _result;

	public FunctionApplicationVisitor(final NumericFunction function)
	{
		this(function, null);
	}

	/**
	 * Takes a function and an optionally null expected value to compare against the function result. If the expected value is not null, the result and value
	 * will be promoted to the same type and checked for equality.
	 */
	public FunctionApplicationVisitor(final NumericFunction function, final Number expected)
	{
		this._function = function;
		this._result = expected;
	}

	/**
	 * Returns the result of the function application. If the application was a failure, the result will be null. If the expected value was non-null and matched
	 * the result once both were promoted, the result will be the expected value (unpromoted).
	 */
	public Number getResult()
	{
		return _result;
	}

	private void testAndSetResult(final Number theResult)
	{
		if (_result == null)
			_result = theResult;
		else
		{

			final NumericComparisonVisitor visitor = new NumericComparisonVisitor();
			final NumericPromotion promoter = new NumericPromotion();
			promoter.promote(_result, theResult);
			promoter.accept(visitor);

			if (visitor.getComparison() == 0)
				_result = theResult;
			else
				_result = null;

		}
	}

	@Override
	public void visit(final BigDecimal[] args)
	{
		testAndSetResult(_function.apply(args));
	}

	@Override
	public void visit(final BigInteger[] args)
	{
		testAndSetResult(_function.apply(args));
	}

	@Override
	public void visit(final Double[] args)
	{
		testAndSetResult(_function.apply(args));
	}

	@Override
	public void visit(final Float[] args)
	{
		testAndSetResult(_function.apply(args));
	}

}
