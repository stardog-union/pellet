// Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package org.mindswap.pellet.tableau.completion.incremental;

import org.mindswap.pellet.tableau.branch.Branch;

import aterm.ATermAppl;

/**
 * Dependency structure for when a branch is added.
 * 
 * @author Christian Halaschek-Wiener
 *
 */
public class BranchAddDependency extends BranchDependency{

	/**
	 * The actual branch
	 */
	private Branch branch;
	
	
	
	/**
	 * Constructor
	 * @param index
	 * @param branch
	 */
	public BranchAddDependency(ATermAppl assertion, int index, Branch branch){
		super(assertion);
		this.branch = branch;
	}



	/**
	 * Get branch
	 * @return
	 */
	public Branch getBranch() {
		return branch;
	}
	
	
	
	/**
	 * ToString method
	 */
	public String toString(){
		return "Branch  - [" + branch + "]";
	}
	
	
	
	/**
	 * Equals method
	 */
	public boolean equals(Object other){
		if(other instanceof BranchAddDependency){
			return (this.branch.getBranch() == ((BranchAddDependency)other).branch.getBranch()) && this.assertion.equals(((BranchAddDependency)other).assertion) ;	
		}else
			return false;
	}
	
	
	/**
	 * Hashcode method
	 */
	public int hashCode(){ 
		return this.branch.getBranch() + this.assertion.hashCode(); 
	}
	
}
