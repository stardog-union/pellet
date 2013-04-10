// The MIT License
//
// Copyright (c) 2007 Christian Halaschek-Wiener
//
// Permission is hereby granted, free of charge, to any person obtaining a copy
// of this software and associated documentation files (the "Software"), to
// deal in the Software without restriction, including without limitation the
// rights to use, copy, modify, merge, publish, distribute, sublicense, and/or
// sell copies of the Software, and to permit persons to whom the Software is
// furnished to do so, subject to the following conditions:
//
// The above copyright notice and this permission notice shall be included in
// all copies or substantial portions of the Software.
//
// THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
// IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
// FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
// AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
// LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
// FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS
// IN THE SOFTWARE.

package org.mindswap.pellet.tableau.completion.incremental;

import org.mindswap.pellet.tableau.branch.Branch;

import aterm.ATermAppl;


/**
 * A depedency for a closed disjunct, merge pair, etc. for a branch
 * 
 * 
 * @author Christian Halaschek-Wiener
 *
 */
public class CloseBranchDependency extends BranchDependency{

	private int tryNext;
	
	private Branch theBranch;
	
	
	public CloseBranchDependency(ATermAppl assertion, int tryNext, Branch theBranch){
		super(assertion);
		this.tryNext = tryNext;
		this.theBranch = theBranch;
	}


	/**
	 * 
	 * @return
	 */
	public ATermAppl getInd() {
		return theBranch.getNode().getName();
	}


	
	/**
	 * ToString method
	 */
	public String toString(){
		return "Branch [" + theBranch.getNode().getName() + "]  -  [" + theBranch.getBranch() + "]";
	}
	
	
	
	/**
	 * Equals method
	 */
	public boolean equals(Object other){
		if(other instanceof CloseBranchDependency){
			return this.getInd().equals(((CloseBranchDependency)other).getInd()) && this.getBranch() == ((CloseBranchDependency)other).getBranch() && this.tryNext == ((CloseBranchDependency)other).tryNext;
		}else
			return false;
	}
	
	
	/**
	 * Hashcode method
	 * TODO: this may not be sufficient
	 */
	public int hashCode(){ 
		return this.getInd().hashCode() + this.getBranch() + this.tryNext;
	}


	public int getBranch() {
		return theBranch.getBranch();
	}


	public int getTryNext() {
		return tryNext;
	}


	public Branch getTheBranch() {
		return theBranch;
	}

}
