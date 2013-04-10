// Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package org.mindswap.pellet.tableau.completion.incremental;

import aterm.ATermAppl;


/**
 * A type dependency.
 * 
 * @author Christian Halaschek-Wiener
 *
 */
public class TypeDependency implements Dependency{

	/**
	 * The type
	 */
	private ATermAppl type;
	
	/**
	 * The individual
	 */
	private ATermAppl ind;
	
	
	/**
	 * Constructor
	 * 
	 * @param ind
	 * @param type
	 */
	public TypeDependency(ATermAppl ind, ATermAppl type){
		this.type = type;
		this.ind = ind;
	}


	/**
	 * Get the individual
	 * 
	 * @return
	 */
	public ATermAppl getInd() {
		return ind;
	}


	/**
	 * Get the type
	 * 
	 * @return
	 */
	public ATermAppl getType() {
		return type;
	}
	
	
	
	/**
	 * ToString method
	 */
	public String toString(){
		return "Type [" + ind + "]  - [" + type + "]";
	}
	
	
	
	/**
	 * Equals method
	 */
	public boolean equals(Object other){
		if(other instanceof TypeDependency){
			return this.ind.equals(((TypeDependency)other).ind) && this.type.equals(((TypeDependency)other).type);	
		}else
			return false;
	}
	
	
	/**
	 * Hashcode method
	 */
	public int hashCode(){ 
		return this.ind.hashCode() + this.type.hashCode(); 
	}

}
