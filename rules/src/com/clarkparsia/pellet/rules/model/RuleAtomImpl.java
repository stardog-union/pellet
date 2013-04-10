// Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package com.clarkparsia.pellet.rules.model;

import java.util.List;

/**
 * <p>
 * Title: Rule Atom Implementation
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

public abstract class RuleAtomImpl<P> implements RuleAtom {

	P predicate;
	
	public RuleAtomImpl( P predicate ) {
		this.predicate = predicate;
	}
	
	public abstract void accept( RuleAtomVisitor visitor );
	
	public boolean equals( Object other ) {
		if ( other != null && getClass().equals( other.getClass() ) ) {
			RuleAtom atom = ( RuleAtom ) other;
			return getPredicate().equals( atom.getPredicate() ) && getAllArguments().equals( atom.getAllArguments() );
		}
		return false;
	}
	
	public abstract List<? extends AtomObject> getAllArguments();
	
	public P getPredicate() {
		return predicate;
	}
	
	public int hashCode() {
		return predicate.hashCode() + getAllArguments().hashCode();
	}	
}
