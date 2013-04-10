// Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package com.clarkparsia.pellet.rules.builtins;

import java.math.BigDecimal;
import java.math.BigInteger;

import org.mindswap.pellet.ABox;
import org.mindswap.pellet.Literal;
import org.mindswap.pellet.exceptions.InternalReasonerException;
import org.mindswap.pellet.utils.ATermUtils;
import org.mindswap.pellet.utils.Namespaces;

/**
 * <p>
 * Title: Number To Literal Visitor
 * </p>
 * <p>
 * Description: Convert from a Number object to a pellet Literal.
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
public class NumberToLiteralVisitor implements NumericVisitor {

	private ABox abox;
	private Literal result;
	
	public NumberToLiteralVisitor( ABox abox ) {
		this.abox = abox;
	}
	
	private void argCheck(Number[] args) {
		if ( args.length != 1 )
			throw new InternalReasonerException( "Wrong number of arguments to visitor." );
	}
	
	public Literal getLiteral() { return result; }
	
	private void setLiteral( Number arg, String typeURI ) {
		result = abox.addLiteral( ATermUtils.makeTypedLiteral( arg.toString(), typeURI ) );
	}
	
	public void visit(BigDecimal[] args) {
		argCheck( args );
		setLiteral( args[0], Namespaces.XSD + "decimal" );
	}

	public void visit(BigInteger[] args) {
		argCheck(args);
		setLiteral(args[0], Namespaces.XSD + "integer");
	}

	public void visit(Double[] args) {
		argCheck( args );
		setLiteral( args[0], Namespaces.XSD + "double" );
	}

	public void visit(Float[] args) {
		argCheck( args );
		setLiteral( args[0], Namespaces.XSD + "float" );
	}

}
