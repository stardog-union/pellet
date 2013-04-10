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

import java.util.logging.Level;


import java.util.HashSet;
import java.util.Set;

import org.mindswap.pellet.ABox;
import org.mindswap.pellet.Clash;
import org.mindswap.pellet.DependencySet;
import org.mindswap.pellet.Individual;
import org.mindswap.pellet.Node;
import org.mindswap.pellet.PelletOptions;
import org.mindswap.pellet.exceptions.InternalReasonerException;
import org.mindswap.pellet.tableau.completion.CompletionStrategy;
import org.mindswap.pellet.utils.ATermUtils;

import aterm.ATermAppl;


public class DisjunctionBranch extends Branch {
	protected Node node;
	protected ATermAppl disjunction;
	private ATermAppl[] disj;
	protected DependencySet[] prevDS;
	protected int[] order;
	
	public DisjunctionBranch(ABox abox, CompletionStrategy completion, Node node, ATermAppl disjunction, DependencySet ds, ATermAppl[] disj) {
		super(abox, completion, ds, disj.length);
		
		this.node = node;
		this.disjunction = disjunction;
		this.setDisj( disj );
        this.prevDS = new DependencySet[disj.length];
		this.order = new int[disj.length];
        for(int i = 0; i < disj.length; i++)
            order[i] = i;
	}
	
	public Node getNode() {
		return node;
	}
    
    protected String getDebugMsg() {
        return "DISJ: Branch (" + getBranch() + ") try (" + (getTryNext() + 1) + "/" + getTryCount()
            + ") " + node + " " +  ATermUtils.toString( disj[getTryNext()] ) + " " + ATermUtils.toString(disjunction);
    }
	
	public DisjunctionBranch copyTo(ABox abox) {
	    Node n = abox.getNode(node.getName());
	    DisjunctionBranch b = new DisjunctionBranch(abox, null, n, disjunction, getTermDepends(), disj);
	    b.setAnonCount( anonCount );
	    b.setNodeCount( nodeCount );
	    b.setBranch( branch );
	    b.setStrategy( strategy );
        b.setTryNext( tryNext );

        b.prevDS = new DependencySet[disj.length];
        System.arraycopy(prevDS, 0, b.prevDS, 0, disj.length );
        b.order = new int[disj.length];
        System.arraycopy(order, 0, b.order, 0, disj.length );

	    return b;
	}
	
	/**
	 * This function finds preferred disjuncts using different heuristics.
	 * 
	 * 1) A common kind of axiom that exist in a lot of  ontologies is 
	 * in the form A = and(B, some(p, C)) which is absorbed into an
	 * axiom like sub(B, or(A, all(p, not(C))). For these disjunctions,
	 * we always prefer picking all(p, C) because it causes an immediate 
	 * clash for the instances of A so there is no overhead. For 
	 * non-instances of A, this builds better pseudo models     
     * 
	 * @return
	 */
	private int preferredDisjunct() {
        if( disj.length != 2 ) 
            return -1;
        
        if( ATermUtils.isPrimitive( disj[0] ) && 
            ATermUtils.isAllValues( disj[1] ) &&
            ATermUtils.isNot( (ATermAppl) disj[1].getArgument( 1 ) ) )
            return 1;
            	                
        if( ATermUtils.isPrimitive( disj[1] ) && 
            ATermUtils.isAllValues( disj[0] ) &&
            ATermUtils.isNot( (ATermAppl) disj[0].getArgument( 1 ) ) )
            return 0;
        
        return -1;
	}
	
    public void setLastClash( DependencySet ds ) {
    		super.setLastClash( ds );
    		if(getTryNext()>=0)
    			prevDS[getTryNext()] = ds;
    }
    
	protected void tryBranch() {			
		abox.incrementBranch();
		
		int[] stats = null;
		if( PelletOptions.USE_DISJUNCT_SORTING ) {
		    stats = abox.getDisjBranchStats().get(disjunction);    
		    if(stats == null) {
		        int preference = preferredDisjunct();
		        stats = new int[disj.length];
		        for(int i = 0; i < disj.length; i++) {
		            stats[i] = (i != preference) ? 0 : Integer.MIN_VALUE;
		        }
		        abox.getDisjBranchStats().put(disjunction, stats); 
		    }
		    if(getTryNext() > 0) {
		        stats[order[getTryNext()-1]]++;
			}
			if(stats != null) {
			    int minIndex = getTryNext();
			    int minValue = stats[getTryNext()];
		        for(int i = getTryNext() + 1; i < stats.length; i++) {
		            boolean tryEarlier = ( stats[i] < minValue );		                
		            
		            if( tryEarlier ) {
		                minIndex = i;
		                minValue = stats[i];
		            }
		        }
		        if(minIndex != getTryNext()) {
		            ATermAppl selDisj = disj[minIndex];
		            disj[minIndex] = disj[getTryNext()];
		            disj[getTryNext()] = selDisj;
		            order[minIndex] = getTryNext();
		            order[getTryNext()] = minIndex;	            
		        }
			}
		}
		
		Node node = this.node.getSame();

		for(; getTryNext() < getTryCount(); tryNext++) {
			ATermAppl d = disj[getTryNext()];

			if(PelletOptions.USE_SEMANTIC_BRANCHING) {
				for(int m = 0; m < getTryNext(); m++)
					strategy.addType(node, ATermUtils.negate( disj[m] ), prevDS[m]);
			}

			DependencySet ds = null;
			if(getTryNext() == getTryCount() - 1 && !PelletOptions.SATURATE_TABLEAU) {
				ds = getTermDepends();
				for(int m = 0; m < getTryNext(); m++)
					ds = ds.union(prevDS[m], abox.doExplanation());

				//CHW - added for incremental reasoning and rollback through deletions
				if(PelletOptions.USE_INCREMENTAL_DELETION)
					ds.setExplain( getTermDepends().getExplain() );
				else
					ds.remove(getBranch());
			}
			else {
				//CHW - Changed for tracing purposes
				if(PelletOptions.USE_INCREMENTAL_DELETION)
					ds = getTermDepends().union(new DependencySet(getBranch()), abox.doExplanation());
				else{
					ds = new DependencySet(getBranch());
					//added for tracing
					Set<ATermAppl> explain = new HashSet<ATermAppl>();
					explain.addAll(getTermDepends().getExplain());
					ds.setExplain( explain );											
				}
            }
            
			if( log.isLoggable( Level.FINE ) ) 
                log.fine( getDebugMsg() );		
			
			ATermAppl notD = ATermUtils.negate(d);
			DependencySet clashDepends = PelletOptions.SATURATE_TABLEAU ? null : node.getDepends(notD);
			if(clashDepends == null) {
			    strategy.addType(node, d, ds);
				// we may still find a clash if concept is allValuesFrom
				// and there are some conflicting edges
				if(abox.isClosed()) 
					clashDepends = abox.getClash().getDepends();
			}
			else {
			    clashDepends = clashDepends.union(ds, abox.doExplanation());
			}
			
			// if there is a clash
			if(clashDepends != null) {				
				if( log.isLoggable( Level.FINE ) ) {
					Clash clash = abox.isClosed() ? abox.getClash() : Clash.atomic(node, clashDepends, d);
                    log.fine("CLASH: Branch " + getBranch() + " " + clash + "!" + " " + clashDepends.getExplain());
				}
				
				if( PelletOptions.USE_DISJUNCT_SORTING ) {
				    if(stats == null) {
				        stats = new int[disj.length];
				        for(int i = 0; i < disj.length; i++)
				            stats[i] = 0;
				        abox.getDisjBranchStats().put(disjunction, stats); 
				    }
					stats[order[getTryNext()]]++;
				}
				
				// do not restore if we do not have any more branches to try. after
				// backtrack the correct branch will restore it anyway. more
				// importantly restore clears the clash info causing exceptions
				if(getTryNext() < getTryCount() - 1 && clashDepends.contains(getBranch())) {
				    // do not restore if we find the problem without adding the concepts 
				    if(abox.isClosed()) {
				    	if( node.isLiteral() ) {
				    		abox.setClash( null );
				    		
				    		node.restore( branch );				    					    		
				    	}
				    	else {
							// restoring a single node is not enough here because one of the disjuncts could be an 
						    // all(r,C) that changed the r-neighbors
					        strategy.restoreLocal((Individual) node, this);
							
							// global restore sets the branch number to previous value so we need to
							// increment it again
							abox.incrementBranch();
				    	}
				    }
					
                    setLastClash( clashDepends );
				}
				else {
				    // set the clash only if we are returning from the function
					if(abox.doExplanation()) {
					    ATermAppl positive = (ATermUtils.isNot(notD) ? d : notD);
					    abox.setClash(Clash.atomic(node, clashDepends.union(ds, abox.doExplanation()), positive));
					}
					else
					    abox.setClash(Clash.atomic(node, clashDepends.union(ds, abox.doExplanation())));

					//CHW - added for inc reasoning
					if(PelletOptions.USE_INCREMENTAL_DELETION)
						abox.getKB().getDependencyIndex().addCloseBranchDependency(this, abox.getClash().getDepends());

			        return;
				}
			} 
			else 
				return;
		}
		
		// this code is not unreachable. if there are no branches left restore does not call this 
		// function, and the loop immediately returns when there are no branches left in this
		// disjunction. If this exception is thrown it shows a bug in the code.
		throw new InternalReasonerException("This exception should not be thrown!");
	}
	
	
	/**
	 * Added for to re-open closed branches.
	 * This is needed for incremental reasoning through deletions
	 * 
	 * @param index The shift index
	 */
	public void shiftTryNext(int openIndex){
		//save vals
//		int ord = order[openIndex];
		ATermAppl dis = disj[openIndex];
//		DependencySet preDS = prevDS[openIndex];

		//TODO: also need to handle semantic branching	
		if(PelletOptions.USE_SEMANTIC_BRANCHING){
//			if(this.ind.getDepends(ATermUtils.makeNot(dis)) != null){
//				//check if the depedency is the same as preDS - if so, then we know that we added it
//			}
		}
		
		//need to shift both prevDS and next and order
		//disjfirst
		for(int i = openIndex; i < disj.length-1; i++){
			disj[i] = disj[i+1];
			prevDS[i] = prevDS[i+1];
			order[i] = order[i];
		}

		//move open label to end
		disj[disj.length-1] = dis;
		prevDS[disj.length-1] = null;
		order[disj.length-1] = disj.length-1;
		
		//decrement trynext
		setTryNext( getTryNext() - 1 );		
	}
	
	
	/**
	 * 
	 */
	public void printLong(){
		for(int i = 0; i < disj.length; i++){
			System.out.println("Disj[" + i + "] " + disj[i]);
			System.out.println("prevDS[" + i + "] " + prevDS[i]);
			System.out.println("order[" + i + "] " + order[i]);
		}

		//decrement trynext
		System.out.println("trynext: " + getTryNext());
	}

	/**
	 * @param disj the disj to set
	 */
	public void setDisj(ATermAppl[] disj) {
		this.disj = disj;
	}

	/**
	 * @return the disj
	 */
	public ATermAppl getDisjunct(int i) {
		return disj[i];
	}

}