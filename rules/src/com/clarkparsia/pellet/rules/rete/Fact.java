// Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package com.clarkparsia.pellet.rules.rete;

import java.util.List;

import org.mindswap.pellet.DependencySet;

import aterm.ATermAppl;

/**
 * <p>
 * Title: Fact
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
public class Fact extends Tuple<ATermAppl> {

	public Fact(DependencySet ds, ATermAppl... constants) {
		super( ds, constants );
	}

	public Fact(DependencySet ds, List<ATermAppl> constants) {
		super( ds, constants );
	}

	public boolean equals(Object other) {
		if( other instanceof Fact ) {
			Fact otherFact = (Fact) other;
			return getElements().equals( otherFact.getElements() );
		}
		return false;
	}

	public String toString() {
		return "Fact( " + getElements() + ")";
	}

}
