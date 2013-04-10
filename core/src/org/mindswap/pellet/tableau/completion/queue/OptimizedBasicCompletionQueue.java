// Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

/**
 * <p>
 * Title:
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
 * @author Christian Halaschek-Wiener
 */

package org.mindswap.pellet.tableau.completion.queue;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.mindswap.pellet.ABox;
import org.mindswap.pellet.Individual;
import org.mindswap.pellet.Literal;
import org.mindswap.pellet.Node;

import aterm.ATermAppl;


/**
 * An optimized basic queue for individuals that need to have completion rules applied 
 */
public class OptimizedBasicCompletionQueue extends CompletionQueue {
    
	/**
	 * The queue - array - each entry is an arraylist for a particular rule type 
	 */
	protected List<ATermAppl>[] queue;
	
	/**
	 * Set to track duplicates for new elements list for queue
	 */
	protected Set<ATermAppl>[] newQueue;
	
	//TODO: This will be refactored; however currently there are some unit tests which will not 	
	//terminate due to the order in which the completion rules are applied to individuals 
	//ont the queue. An example of this is MiscTests.testIFDP3() - in this example,
	//if the LiteralRule is applied to the individual "b" first, then an infinite number
	//of non-deterministic choices are created...talk to Evren about this.
	
	/**
	 * List to hold new elements for the queue
	 */
	protected List<ATermAppl>[] newQueueList;
	
	
	/**
	 * List of current index pointer for each queue
	 */
	protected int current[];
	
	/**
	 * List of current index pointer for each queue
	 */
	protected int end[];
	
	/**
	 * List of current index pointer for the stopping point at each queue 
	 */
	protected int cutOff[];
	
	/**
	 * Flag set for when the kb is restored - in this case we do not want to flush the queue immediatly
	 */
	protected boolean backtracked;

	

	/**
	 * Constructor - create queue
	 * 
	 * @param abox
	 */
	@SuppressWarnings("unchecked")
	public OptimizedBasicCompletionQueue(ABox abox) {
		super(abox);
		final int nSelectors = NodeSelector.numSelectors();
		queue = (List<ATermAppl>[]) new ArrayList[nSelectors];
		newQueue = (Set<ATermAppl>[]) new HashSet[nSelectors];
		newQueueList = (List<ATermAppl>[]) new ArrayList[nSelectors];

		current = new int[nSelectors];
		cutOff = new int[nSelectors];
		end = new int[nSelectors];
		
		for( int i = 0; i < nSelectors; i++ ){			
			queue[i] = new ArrayList<ATermAppl>();
			newQueue[i] = new HashSet<ATermAppl>();
			newQueueList[i] = new ArrayList<ATermAppl>();
			
			current[i] = 0;
			cutOff[i] = 0;
			end[i] = 0;
		}
		
		backtracked = false;
	}		

	
	/**
	 * Find the next individual in a given queue 
	 * 
	 * @param type
	 */
	protected void findNext(int type) {
		for( ; current[type] < cutOff[type]; current[type]++ ) {
			Node node = abox.getNode( queue[type].get( current[type] ) );

			//because we do not maitain the queue during restore this node could be non-existent
			if(node == null)
				continue;
			
			node = node.getSame();
			
			if( ( ( node instanceof Literal && allowLiterals() ) || (node instanceof Individual && !allowLiterals())) && !node.isPruned())
				break;			
		}
	}
	

	
	/**
	 * Test if there is another element on the queue to process
	 * 
	 * @param type
	 * @return
	 */
	public boolean hasNext() {
		findNext(currentType);
		return current[currentType] < cutOff[currentType];
	}

	
	
	/**
	 * Reset the queue to be the current nodes in the abox; Also reset the type index to 0
	 * 
	 * @param branch
	 */
	public void restore(int branch) {
		for( int i = 0; i < NodeSelector.numSelectors(); i++){
			queue[i].addAll(newQueueList[i]);
			newQueue[i].clear();
			newQueueList[i].clear();
			end[i] = queue[i].size();
			current[i] = 0;
			cutOff[i] = end[i];
		}
		backtracked = true;
	}
	
	
	
	/**
	 * Get the next element of a queue of a given type 
	 * @param type
	 * @return
	 */
	public Individual next() {
		//get the next index
		findNext(currentType);
		Individual ind = (Individual)abox.getNode(queue[currentType].get(current[currentType]));
		ind = ( Individual ) ind.getSame();
		current[currentType]++;
		return ind;		
	}
	
	
	/**
	 * Get the next element of a queue of a given type 
	 * @param type
	 * @return
	 */
	public Node nextLiteral() {
		//get the next index
		findNext(currentType);
		Node node = abox.getNode(queue[currentType].get(current[currentType]));		
		node = node.getSame();		
		current[currentType]++;
		return node;
	}
	

	@Override
	public void add(QueueElement x, NodeSelector s) {
		int type = s.ordinal();
		if( !newQueue[type].contains( x.getNode() ) ) {
			newQueue[type].add( x.getNode() );
			newQueueList[type].add( x.getNode() );
		}
	}


	@Override
	public void add(QueueElement x) {
		for( int i = 0; i < NodeSelector.numSelectors(); i++ ) {
			if( !newQueue[i].contains( x.getNode() ) ) {
				newQueue[i].add( x.getNode() );
				newQueueList[i].add( x.getNode() );
			}
		}
	}
	
	/**
	 * Reset the cutoff for a given type index
	 * @param type
	 */
	@Override
	public void reset(NodeSelector s){
		currentType = s.ordinal();
		cutOff[currentType] = end[currentType];
		current[currentType] = 0;
	}
	
	
	/**
	 * Set branch pointers to current pointer. This is done whenever abox.incrementBranch is called 
	 * @param branch
	 */
	public void incrementBranch(int branch){
		return;
	}
	

	/**
	 * Copy the queue
	 * 
	 * @return
	 */
	public OptimizedBasicCompletionQueue copy(){
		OptimizedBasicCompletionQueue copy = new OptimizedBasicCompletionQueue(this.abox);
		
		for( int i = 0; i < NodeSelector.numSelectors(); i++ ){
			copy.queue[i] = new ArrayList<ATermAppl>(this.queue[i]);
			copy.newQueue[i] = new HashSet<ATermAppl>(this.newQueue[i]);
			copy.newQueueList[i] = new ArrayList<ATermAppl>(this.newQueueList[i]);	
			
			copy.current[i] = this.current[i];
			copy.cutOff[i] = this.cutOff[i];
			copy.end[i] = this.end[i];
		}
		
		copy.backtracked = this.backtracked;
			
		copy.setAllowLiterals(this.allowLiterals());
		
		
		//copy branch effects
//		for(int i = 0; i < branchEffects.size(); i++){
//			HashSet<ATermAppl> cp = new HashSet<ATermAppl>();
//			cp.addAll((Set<ATermAppl>)branchEffects.get(i));
//			copy.branchEffects.add(cp);		
//		}
//		
		return copy;
	}
	
	
	/**
	 * Set the abox for the queue
	 * @param ab
	 */
	public void setABox(ABox ab){
    		this.abox = ab;
    }
	
	
	
	/**
	 * Print method for a given queue type
	 * 
	 * @param type
	 */
	public void print(int type){
		if(type > NodeSelector.numSelectors())
			return;
		System.out.println("Queue " + type + ": " + queue[type]);
	}
	
	
	
	/**
	 * Print method for entire queue
	 *
	 */
	public void print(){
		for(int i = 0; i < NodeSelector.numSelectors(); i++)
			System.out.println("Queue " + i + ": " + queue[i]);
	}

	
	/**
	 * Remove method for abstract class
	 */
	public void remove() {
		throw new RuntimeException("Remove is not supported");
	}
	

	@Override
	public void flushQueue() {
		for(int i = 0; i < NodeSelector.numSelectors(); i++){
	
			if(!backtracked && !closed){
				queue[i].clear();
			}else{
				if(closed){
					if(!abox.isClosed())
						closed = false;
				}
			}
			
			queue[i].addAll(newQueueList[i]);
			
			newQueue[i].clear();
			newQueueList[i].clear();
	
			end[i] = queue[i].size();
		}
		
		backtracked = false;
	}
	

	@Override
	protected  void flushQueue( NodeSelector s ){
		
		int index = s.ordinal();
		
		if( index == NodeSelector.UNIVERSAL.ordinal() || !backtracked ) {
			queue[index].clear();
		}
		
		queue[index].addAll(newQueueList[index]);
		
		newQueue[index].clear();
		newQueueList[index].clear();

		end[index] = queue[index].size();	
	}
	
	
	@Override
	public  void clearQueue( NodeSelector s ){
		
		int index = s.ordinal();
		
		queue[index].clear();
		
		newQueue[index].clear();
		newQueueList[index].clear();

		end[index] = queue[index].size();	
	}

}