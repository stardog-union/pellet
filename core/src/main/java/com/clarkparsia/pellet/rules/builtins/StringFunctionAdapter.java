// Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package com.clarkparsia.pellet.rules.builtins;

import static com.clarkparsia.pellet.rules.builtins.ComparisonTesters.expectedIfEquals;

import org.mindswap.pellet.ABox;
import org.mindswap.pellet.Literal;
import org.mindswap.pellet.utils.ATermUtils;

import aterm.ATermAppl;

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
public class StringFunctionAdapter implements Function {

	String datatypeURI;
	StringToStringFunction function;
	
	public StringFunctionAdapter( StringToStringFunction function ) {
		this( function, null );
	}
	
	public StringFunctionAdapter( StringToStringFunction function, String datatypeURI ) {
		this.datatypeURI = datatypeURI;
		this.function = function;
	}
	
	public Literal apply(ABox abox, Literal expected, Literal... litArgs) {
		
		String[] args = new String[ litArgs.length ];
		for ( int i = 0; i < litArgs.length; i++ ) {
			args[i] = ATermUtils.getLiteralValue( litArgs[i].getTerm() );
		}
		
		String result = function.apply( args );
		if ( result == null )
			return null;
		
		ATermAppl resultTerm;
		if ( datatypeURI == null )
			resultTerm = ATermUtils.makePlainLiteral( result );
		else
			resultTerm = ATermUtils.makeTypedLiteral( result, datatypeURI );
		
		Literal resultLit = abox.addLiteral( resultTerm );

		return expectedIfEquals( expected, resultLit );
	}

}
