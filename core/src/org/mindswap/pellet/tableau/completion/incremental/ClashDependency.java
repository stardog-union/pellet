// Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package org.mindswap.pellet.tableau.completion.incremental;


import org.mindswap.pellet.Clash;

import aterm.ATermAppl;

/**
 * A clash dependency.
 * 
 * @author Christian Halaschek-Wiener
 *
 */
public class ClashDependency implements Dependency{

	/**
	 * The assertion
	 */
	private ATermAppl assertion;
	
	
	/**
	 * The clash
	 */
	private Clash clash;
	
	/**
	 * Constructor
	 * @param assertion
	 * @param clash
	 */
	public ClashDependency(ATermAppl assertion, Clash clash){
		this.assertion = assertion;
		this.clash = clash;
	}


	
	
	/**
	 * ToString method
	 */
	public String toString(){
		return "Clash [" + assertion + "]  - [" + clash + "]";
	}
	
	
	
	/**
	 * Equals method
	 */
	public boolean equals(Object other){
		if(other instanceof ClashDependency){
			return this.assertion.equals(((ClashDependency)other).assertion) && this.clash.getNode().equals(((ClashDependency)other).clash.getNode()) && this.clash.getType() == ((ClashDependency)other).clash.getType() && this.clash.getDepends().equals(((ClashDependency)other).clash.getDepends());	
		}else
			return false;
	}
	
	
	/**
	 * Hashcode method
	 * TODO: this may not be sufficient
	 */
	public int hashCode(){ 
		return this.clash.getType().hashCode()+ this.clash.getDepends().hashCode() + this.clash.getNode().hashCode() + this.assertion.hashCode(); 
	}




	/**
	 * Get the assertion
	 *  
	 * @return
	 */
	protected ATermAppl getAssertion() {
		return assertion;
	}




	/**
	 * Get the clash
	 * 
	 * @return
	 */
	public Clash getClash() {
		return clash;
	}

}
