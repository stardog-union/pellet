// Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package org.mindswap.pellet.tableau.completion.incremental;

import aterm.ATermAppl;


/**
 * Abstract class for a branch dependency
 * 
 * @author Christian Halaschek-Wiener
 *
 */
public abstract class BranchDependency implements Dependency{

	/**
	 * The assertion which this branch is indexed on
	 */
	protected ATermAppl assertion;
	

	/**
	 * Constructor
	 * 
	 * @param assertion
	 */
	public BranchDependency(ATermAppl assertion){
		this.assertion = assertion; 
	}

	/**
	 * 
	 * @return
	 */
	public ATermAppl getAssertion() {
		return assertion;
	}
	
	
}
