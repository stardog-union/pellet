// Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package com.clarkparsia.pellet.rules.model;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.mindswap.pellet.utils.URIUtils;

/**
 * <p>
 * Title: Built-In Atom
 * </p>
 * <p>
 * Description: 
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

public class BuiltInAtom extends RuleAtomImpl<String> {

	private List<AtomDObject> arguments;
	
	public BuiltInAtom( String predicate, AtomDObject... arguments ) {
		this( predicate, Arrays.asList( arguments ) );
	}
	
	public BuiltInAtom(String predicate, List<AtomDObject> arguments) {
		super(predicate);
		this.arguments = arguments;
	}

	public void accept(RuleAtomVisitor visitor) {
		visitor.visit( this );
	}

	@Override
	public List<AtomDObject> getAllArguments() {
		return Collections.unmodifiableList( arguments );
	}
	
	public String toString() {
		return URIUtils.getLocalName(getPredicate().toString())+"("+getAllArguments()+")";
	}
}
