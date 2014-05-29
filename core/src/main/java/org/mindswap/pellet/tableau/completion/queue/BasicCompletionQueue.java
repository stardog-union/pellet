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

import java.util.logging.Logger;
import org.mindswap.pellet.ABox;
import org.mindswap.pellet.Individual;
import org.mindswap.pellet.Node;
import org.mindswap.pellet.tableau.completion.queue.NodeSelector;


import aterm.ATermAppl;


/**
 * A basic queue for individuals that need to have completion rules applied 
 */
public class BasicCompletionQueue extends CompletionQueue {
	/**
	 * The queue - array - each entry is an arraylist for a particular rule type 
	 */
	protected List<ATermAppl> queue;
	
	/**
	 * Set to track duplicates for new elements list for queue
	 */
	protected Set<ATermAppl> newQueue;
	
	//TODO: This will be refactored; however currently there are some unit tests which will not 	
	//terminate due to the order in which the completion rules are applied to individuals 
	//ont the queue. An example of this is MiscTests.testIFDP3() - in this example,
	//if the LiteralRule is applied to the individual "b" first, then an infinite number
	//of non-deterministic choices are created...talk to Evren about this.
	
	/**
	 * List to hold new elements for the queue
	 */
	protected List<ATermAppl> newQueueList;
	
	
	/**
	 * List of current index pointer for each queue
	 */
	protected int current;
	
	/**
	 * List of current index pointer for each queue
	 */
	protected int end;
	
	/**
	 * List of current index pointer for the stopping point at each queue 
	 */
	protected int cutOff;
	
	/**
	 * Flag set for when the kb is restored - in this case we do not want to flush the queue immediatly
	 */
	protected boolean backtracked;

	

	/**
	 * Constructor - create queue
	 * 
	 * @param abox
	 */
	public BasicCompletionQueue(ABox abox) {
		super(abox);
		queue = new ArrayList<ATermAppl>();
		newQueue = new HashSet<ATermAppl>();
		newQueueList = new ArrayList<ATermAppl>();

		current = 0;
		cutOff = 0;
		end = 0;
		backtracked = false;
	}		

	
	/**
	 * Find the next individual in a given queue 
	 * 
	 * @param type
	 */
	protected void findNext(int type) {
		for( ; current < cutOff; current++ ) {
			Node node = abox.getNode( queue.get( current ) );

			//because we do not maitain the queue during restore this node could be non-existent
			if(node == null)
				continue;
			
			node = node.getSame();
			
			if( ( ( node.isLiteral() && allowLiterals() ) || (node.isIndividual() && !allowLiterals())) && !node.isPruned())
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
		findNext(-1);
		return current < cutOff;
	}

	
	
	/**
	 * Reset the queue to be the current nodes in the abox; Also reset the type index to 0
	 * 
	 * @param branch
	 */
	public void restore(int branch) {
		queue.addAll(newQueueList);
		newQueue.clear();
		newQueueList.clear();
		end = queue.size();
		current = 0;
		cutOff = end;
		backtracked = true;
	}
	
	
	
	/**
	 * Get the next element of a queue of a given type 
	 * @param type
	 * @return
	 */
	public Individual next() {
		//get the next index
		findNext(-1);
		Individual ind = abox.getIndividual(queue.get(current));
		ind = ind.getSame();
		current++;
		return ind;
		
	}
	
	
	/**
	 * Get the next element of a queue of a given type 
	 * @param type
	 * @return
	 */
	public Node nextLiteral() {
		//get the next index
		findNext(-1);
		Node node = abox.getNode(queue.get(current));		
		node = node.getSame();		
		current++;
		return node;
	}
	

	@Override
	public void add(QueueElement x, NodeSelector s) {
		add( x );
	}
	
	@Override
	public void add(QueueElement x) {
		if( !newQueue.contains( x.getNode() ) ) {
			newQueue.add( x.getNode() );
			newQueueList.add( x.getNode() );
		}
	}

	
	
	/**
	 * Reset the cutoff for a given type index
	 * @param type
	 */
	@Override
	public void reset(NodeSelector s){
		cutOff = end;
		current = 0;
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
	public BasicCompletionQueue copy(){
		BasicCompletionQueue copy = new BasicCompletionQueue(this.abox);
		
		copy.queue = new ArrayList<ATermAppl>(this.queue);
		copy.newQueue = new HashSet<ATermAppl>(this.newQueue);
		copy.newQueueList = new ArrayList<ATermAppl>(this.newQueueList);	
		
		copy.current = this.current;
		copy.cutOff = this.cutOff;
		copy.backtracked = this.backtracked;
		copy.end = this.end;
		copy.setAllowLiterals(this.allowLiterals());
		
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
		System.out.println("Queue: " + queue);
	}
	
	
	
	/**
	 * Print method for entire queue
	 *
	 */
	public void print(){
		System.out.println("Queue: " + queue);
	}

	
	/**
	 * Remove method for abstract class
	 */
	public void remove() {
		throw new RuntimeException("Remove is not supported");
	}
	
	
	@Override
	public void flushQueue() {
		if(!backtracked && !closed){
			queue.clear();
		}else{
			if(closed){
				if(!abox.isClosed())
					closed = false;
			}
		}

		queue.addAll(newQueueList);

		newQueue.clear();
		newQueueList.clear();

		end = queue.size();
		
		backtracked = false;
	}
	
	@Override
	protected void flushQueue(NodeSelector s) {
		return;
	}
	
	@Override
	public void clearQueue(NodeSelector s) {
		return;
	}	

}