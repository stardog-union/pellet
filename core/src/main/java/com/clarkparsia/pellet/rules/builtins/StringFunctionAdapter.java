// Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package com.clarkparsia.pellet.rules.builtins;

import static com.clarkparsia.pellet.rules.builtins.ComparisonTesters.expectedIfEquals;

import aterm.ATermAppl;
import org.mindswap.pellet.ABox;
import org.mindswap.pellet.Literal;
import org.mindswap.pellet.utils.ATermUtils;

/**
 * <p>
 * Title: String-to-String Function Adapter
 * </p>
 * <p>
 * Description: Adapter from StringToStringFunction to Function
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
public class StringFunctionAdapter implements Function
{

	String datatypeURI;
	StringToStringFunction function;

	public StringFunctionAdapter(final StringToStringFunction function)
	{
		this(function, null);
	}

	public StringFunctionAdapter(final StringToStringFunction function, final String datatypeURI)
	{
		this.datatypeURI = datatypeURI;
		this.function = function;
	}

	@Override
	public Literal apply(final ABox abox, final Literal expected, final Literal... litArgs)
	{

		final String[] args = new String[litArgs.length];
		for (int i = 0; i < litArgs.length; i++)
			args[i] = ATermUtils.getLiteralValue(litArgs[i].getTerm());

		final String result = function.apply(args);
		if (result == null)
			return null;

		ATermAppl resultTerm;
		if (datatypeURI == null)
			resultTerm = ATermUtils.makePlainLiteral(result);
		else
			resultTerm = ATermUtils.makeTypedLiteral(result, datatypeURI);

		final Literal resultLit = abox.addLiteral(resultTerm);

		return expectedIfEquals(expected, resultLit);
	}

}
