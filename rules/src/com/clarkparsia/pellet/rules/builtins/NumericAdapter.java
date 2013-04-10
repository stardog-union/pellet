// Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package com.clarkparsia.pellet.rules.builtins;

import org.mindswap.pellet.ABox;
import org.mindswap.pellet.Literal;

/**
* <p>
* Title: Numeric Adapter
* </p>
* <p>
* Description: Adapter from Numeric Functions to built-in Function.
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

public class NumericAdapter implements Function {
	
	private NumericFunction function;
	
	public NumericAdapter( NumericFunction function ) {
		this.function = function;
	}
	
	public Literal apply( ABox abox, Literal expected, Literal... args ) {
		Number expectedNum = null;
		Number result = null;
		Literal resultLit = null;
		
		if (expected != null) {
			if (!(expected.getValue() instanceof Number)) {
				ABox.log.info("Testing non-numeric against the result of a numeric function '"
								+ function + "': " + expected);
				return null;
			}
			expectedNum = (Number) expected.getValue();
		}
		
		Number[] numArgs = new Number[ args.length ];
		for ( int i = 0; i < args.length; i++ ) {
			if ( args[i].getValue() instanceof Number ) {
				numArgs[i] = (Number) args[i].getValue();
			} else {
				ABox.log.info( "Non numeric arguments to numeric function '" + function + "': " + args[i]);
				return null;
			}
		}
		
		NumericPromotion promoter = new NumericPromotion();
		promoter.promote( numArgs );
		FunctionApplicationVisitor visitor = new FunctionApplicationVisitor( function, expectedNum );
		promoter.accept( visitor );
		
		result = visitor.getResult();
		if ( result != null ) {
			if ( expected != null ) {
				resultLit = expected;
			} else {
				NumberToLiteralVisitor converter = new NumberToLiteralVisitor( abox );
				promoter.promote( result );
				promoter.accept( converter );
				resultLit = converter.getLiteral();
			}
		}
		
		return resultLit;
	}

}
