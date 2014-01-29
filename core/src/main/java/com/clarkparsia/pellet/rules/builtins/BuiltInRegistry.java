// Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public
// License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of
// proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package com.clarkparsia.pellet.rules.builtins;

import static org.mindswap.pellet.utils.Namespaces.SWRLB;

import java.util.HashMap;
import java.util.Map;

/**
 * <p>
 * Title: Built-In Registry
 * </p>
 * <p>
 * Description: Registry of built-ins used by pellet.
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
public class BuiltInRegistry {

	public static final BuiltInRegistry	instance	= new BuiltInRegistry();

	private Map<String, BuiltIn>		builtIns;

	private BuiltInRegistry() {
		builtIns = new HashMap<String, BuiltIn>();

		// Comparisons
		registerBuiltIn( SWRLB + "equal", tester( ComparisonTesters.equal ) );
		registerBuiltIn( SWRLB + "greaterThan", tester( ComparisonTesters.greaterThan ) );
		registerBuiltIn( SWRLB + "greaterThanOrEqual",
				tester( ComparisonTesters.greaterThanOrEqual ) );
		registerBuiltIn( SWRLB + "lessThan", tester( ComparisonTesters.lessThan ) );
		registerBuiltIn( SWRLB + "lessThanOrEqual", tester( ComparisonTesters.lessThanOrEqual ) );
		registerBuiltIn( SWRLB + "notEqual", tester( ComparisonTesters.notEqual ) );

		// DateTime
		registerBuiltIn( SWRLB + "date", generalFunc( DateTimeOperators.date ) );
		registerBuiltIn( SWRLB + "dateTime", generalFunc( DateTimeOperators.dateTime ) );
		registerBuiltIn( SWRLB + "dayTimeDuration", function( DateTimeOperators.dayTimeDuration ) );
		registerBuiltIn( SWRLB + "time", generalFunc( DateTimeOperators.time ) );
		registerBuiltIn( SWRLB + "yearMonthDuration",
				function( DateTimeOperators.yearMonthDuration ) );

		// URIs
		registerBuiltIn( SWRLB + "resolveURI", function( URIOperators.resolveURI ) );
		registerBuiltIn( SWRLB + "anyURI", function( URIOperators.anyURI ) );

		// Numeric
		registerBuiltIn( SWRLB + "abs", numeric( NumericOperators.abs ) );
		registerBuiltIn( SWRLB + "add", numeric( NumericOperators.add ) );
		registerBuiltIn( SWRLB + "ceiling", numeric( NumericOperators.ceiling ) );
		registerBuiltIn( SWRLB + "cos", numeric( NumericOperators.cos ) );
		registerBuiltIn( SWRLB + "divide", numeric( NumericOperators.divide ) );
		registerBuiltIn( SWRLB + "floor", numeric( NumericOperators.floor ) );
		registerBuiltIn( SWRLB + "integerDivide", numeric( NumericOperators.integerDivide ) );
		registerBuiltIn( SWRLB + "mod", numeric( NumericOperators.mod ) );
		registerBuiltIn( SWRLB + "multiply", numeric( NumericOperators.multiply ) );
		registerBuiltIn( SWRLB + "pow", numeric( NumericOperators.pow ) );
		registerBuiltIn( SWRLB + "round", numeric( NumericOperators.round ) );
		registerBuiltIn( SWRLB + "roundHalfToEven", numeric( NumericOperators.roundHalfToEven ) );
		registerBuiltIn( SWRLB + "sin", numeric( NumericOperators.sin ) );
		registerBuiltIn( SWRLB + "subtract", numeric( NumericOperators.subtract ) );
		registerBuiltIn( SWRLB + "tan", numeric( NumericOperators.tan ) );
		registerBuiltIn( SWRLB + "unaryMinus", numeric( NumericOperators.unaryMinus ) );
		registerBuiltIn( SWRLB + "unaryPlus", numeric( NumericOperators.unaryPlus ) );

		// String
		registerBuiltIn( SWRLB + "contains", tester( StringOperators.contains ) );
		registerBuiltIn( SWRLB + "containsIgnoreCase", tester( StringOperators.containsIgnoreCase ) );
		registerBuiltIn( SWRLB + "endsWith", tester( StringOperators.endsWith ) );
		registerBuiltIn( SWRLB + "lowerCase", function( StringOperators.lowerCase ) );
		registerBuiltIn( SWRLB + "matches", tester( StringOperators.matches ) );
		registerBuiltIn( SWRLB + "normalizeSpace", function( StringOperators.normalizeSpace ) );
		registerBuiltIn( SWRLB + "replace", function( StringOperators.replace ) );
		registerBuiltIn( SWRLB + "startsWith", tester( StringOperators.startsWith ) );
		registerBuiltIn( SWRLB + "stringConcat", function( StringOperators.stringConcat ) );
		registerBuiltIn( SWRLB + "stringEqualIgnoreCase",
				tester( StringOperators.stringEqualIgnoreCase ) );
		registerBuiltIn( SWRLB + "stringLength", function( StringOperators.stringLength ) );
		registerBuiltIn( SWRLB + "substring", function( StringOperators.substring ) );
		registerBuiltIn( SWRLB + "substringAfter", function( StringOperators.substringAfter ) );
		registerBuiltIn( SWRLB + "substringBefore", function( StringOperators.substringBefore ) );
		registerBuiltIn( SWRLB + "tokenize", StringOperators.tokenize );
		registerBuiltIn( SWRLB + "translate", function( StringOperators.translate ) );
		registerBuiltIn( SWRLB + "upperCase", function( StringOperators.upperCase ) );

		// Boolean
		registerBuiltIn( SWRLB + "booleanNot", generalFunc( BooleanOperators.booleanNot ) );
	}

	/**
	 * Returns a built-in registered by the given name. If none exists, return a
	 * built-in that will create an empty binding helper.
	 */
	public BuiltIn getBuiltIn(String name) {
		BuiltIn builtIn = builtIns.get( name );
		if( builtIn == null )
			builtIn = NoSuchBuiltIn.instance;
		return builtIn;
	}

	private BuiltIn function(Function function) {
		return new FunctionBuiltIn( function );
	}

	private BuiltIn generalFunc(GeneralFunction function) {
		return new GeneralFunctionBuiltIn( function );
	}

	private BuiltIn numeric(NumericFunction numeric) {
		return function( new NumericAdapter( numeric ) );
	}

	public void registerBuiltIn(String name, BuiltIn builtIn) {
		builtIns.put( name, builtIn );
	}

	private BuiltIn tester(Tester tester) {
		return new TesterBuiltIn( tester );
	}
}
