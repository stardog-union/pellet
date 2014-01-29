// Portions Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// Clark & Parsia, LLC parts of this source code are available under the terms of the Affero General Public License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com
//
// ---
// Portions Copyright (c) 2003 Ron Alford, Mike Grove, Bijan Parsia, Evren Sirin
// Alford, Grove, Parsia, Sirin parts of this source code are available under the terms of the MIT License.
//
// The MIT License
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

package org.mindswap.pellet.tableau.branch;

import java.util.logging.Logger;

import org.mindswap.pellet.ABox;
import org.mindswap.pellet.Clash;
import org.mindswap.pellet.DependencySet;
import org.mindswap.pellet.Node;
import org.mindswap.pellet.PelletOptions;
import org.mindswap.pellet.tableau.completion.CompletionStrategy;

/**
 * <p>
 * Title:
 * </p>
 * <p>
 * Description:
 * </p>
 * <p>
 * Copyright: Copyright (c) 2009
 * </p>
 * <p>
 * Company: Clark & Parsia, LLC. <http://www.clarkparsia.com>
 * </p>
 * 
 * @author Evren Sirin
 */
public abstract class Branch {
    public static final Logger log = Logger.getLogger( Branch.class.getName() );
        
    protected ABox abox;
	protected CompletionStrategy strategy;
	protected int branch;
	protected int tryCount;
	protected int tryNext;
	
	private DependencySet termDepends;
    private DependencySet prevDS;
	
	// store things that can be changed after this branch
    protected int anonCount;
	protected int nodeCount;
	
	Branch(ABox abox, CompletionStrategy strategy, DependencySet ds, int n) {
		this.abox = abox;
		this.setStrategy( strategy );
		
		setTermDepends( ds );
		setTryCount( n );
		prevDS = DependencySet.EMPTY;
		setTryNext( 0 );			
		
		setBranch( abox.getBranch() );
		setAnonCount( abox.getAnonCount() );
		setNodeCount( abox.size() );
	}
    
    public void setLastClash( DependencySet ds ) {
    		if(getTryNext()>=0){
    			prevDS = prevDS.union( ds, abox.doExplanation() );
    			if(PelletOptions.USE_INCREMENTAL_DELETION){
	    			//CHW - added for incremental deletions support THIS SHOULD BE MOVED TO SUPER
	    			abox.getKB().getDependencyIndex().addCloseBranchDependency(this, ds);		
    			}
    		}
    }
	
    public DependencySet getCombinedClash() {
        return prevDS;
    }
    
	public void setStrategy(CompletionStrategy strategy) {
	    this.strategy = strategy;
	}
	
	public boolean tryNext() {			
		// nothing more to try, update the clash dependency
		if( getTryNext() == getTryCount() ) {
			if( !abox.isClosed() )
				abox.setClash( Clash.unexplained(getNode(), termDepends) );
			else
				abox.getClash().setDepends( getCombinedClash() );
		}
		
		// if there is no clash try next possibility
		if( !abox.isClosed() )
			tryBranch();

		// there is a clash so there is no point in trying this
		// branch again. remove this branch from clash dependency
		if( abox.isClosed() ) {
			if( !PelletOptions.USE_INCREMENTAL_DELETION )
				abox.getClash().getDepends().remove( getBranch() );
		}
		
		return !abox.isClosed();
	}
	
	public abstract Branch copyTo(ABox abox);
	
	protected abstract void tryBranch();
	
	public abstract Node getNode();
	
	public String toString() {
//		return "Branch " + branch + " (" + tryCount + ")";
		return "Branch on node " + getNode() + "  Branch number: "+ getBranch() + " " + getTryNext() + "(" + getTryCount() + ")";
	}
	
	
	/**
	 * Added for to re-open closed branches.
	 * This is needed for incremental reasoning through deletions
	 * 
	 * @param index The shift index
	 */
	public abstract void shiftTryNext(int index);

	/**
	 * @param nodeCount the nodeCount to set
	 */
	public void setNodeCount(int nodeCount) {
		this.nodeCount = nodeCount;
	}
	
	/**
	 * @return the nodeCount
	 */
	public int getNodeCount() {
		return nodeCount;
	}

	public void setBranch(int branch) {
		this.branch = branch;
	}
	
	/**
	 * @return the branch
	 */
	public int getBranch() {
		return branch;
	}

	/**
	 * @return the anonCount
	 */
	public int getAnonCount() {
		return anonCount;
	}

	/**
	 * @param tryNext the tryNext to set
	 */
	public void setTryNext(int tryNext) {
		this.tryNext = tryNext;
	}

	/**
	 * @return the tryNext
	 */
	public int getTryNext() {
		return tryNext;
	}

	/**
	 * @param tryCount the tryCount to set
	 */
	public void setTryCount(int tryCount) {
		this.tryCount = tryCount;
	}

	/**
	 * @return the tryCount
	 */
	public int getTryCount() {
		return tryCount;
	}

	/**
	 * @param termDepends the termDepends to set
	 */
	public void setTermDepends(DependencySet termDepends) {
		this.termDepends = termDepends;
	}

	/**
	 * @return the termDepends
	 */
	public DependencySet getTermDepends() {
		return termDepends;
	}

	/**
	 * @param anonCount the anonCount to set
	 */
	public void setAnonCount(int anonCount) {
		this.anonCount = anonCount;
	}
	
}