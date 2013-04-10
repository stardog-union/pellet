// Portions Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// Clark & Parsia, LLC parts of this source code are available under the terms of the Affero General Public License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package org.mindswap.pellet;

import java.util.Iterator;
import java.util.List;

import org.mindswap.pellet.ABox;
import org.mindswap.pellet.Individual;
import org.mindswap.pellet.Node;
import org.mindswap.pellet.tableau.completion.queue.NodeSelector;

import aterm.ATermAppl;


/**
 * An iterator to return nodes in the order they are added. Having a separate 
 * iterator instead of using nodes.iterator() allows to change the nodes
 * table without resetting the iteration process. 
 * 
 * @author Evren Sirin
 */
public class IndividualIterator implements Iterator<Individual> {
    /**
     * ABox where the individuals are stored
     */
	protected ABox abox;
	/**
	 * List of node names
	 */
	protected List<ATermAppl> nodeList;
	/**
	 * Last returned index
	 */
	protected int index;

	/**
	 * Index where iterator stops (size of list by default)
	 */
	protected int stop;

	/**
	 * Create an iterator over all the individuals in the ABox
	 */
	public IndividualIterator(ABox abox) {
		this.abox = abox;
		nodeList = abox.getNodeNames();
		stop = nodeList.size();
		index = 0;

		findNext();
	}		
    
	protected void findNext() {
		for(; index < stop; index++) {
		    Node node = abox.getNode( nodeList.get( index ) ) ;
			if( !node.isPruned() && node.isIndividual() )
				break;
		}
	}
	
	public boolean hasNext() {
		findNext();
		return index < stop;
	}
	
	public void reset(NodeSelector s) {
		index = 0;
		findNext();
	}

	public Individual next() {
		findNext();
		Individual ind = abox.getIndividual(nodeList.get(index++));
		
		return ind;
	}

	public void remove() {
		throw new UnsupportedOperationException();
	}
}