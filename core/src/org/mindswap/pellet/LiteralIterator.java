// Portions Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// Clark & Parsia, LLC parts of this source code are available under the terms of the Affero General Public License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package org.mindswap.pellet;

import java.util.Iterator;
import java.util.List;

import org.mindswap.pellet.ABox;
import org.mindswap.pellet.Literal;
import org.mindswap.pellet.Node;

import aterm.ATermAppl;


/**
 * An iterator to return nodes in the order they are added. Having a seperate 
 * iterator instead of using nodes.iterator() allows to change the nodes
 * table without resetting the iteration process. 
 * 
 * @author Evren Sirin
 */
public class LiteralIterator implements Iterator<Literal> {
    /**
     * ABox where literals are stoired
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
	 * Index where iterator starts (0 be default)
	 */
	protected int start;
	/**
	 * Index where iterator stops (size of list by default)
	 */
	protected int stop;

	/**
	 * Create an iterator over all the individuals in the ABox
	 */
	public LiteralIterator(ABox abox) {
		this(abox, true);
	}		

	/**
	 * Create an iterator over all the individuals in the ABox 
	 * but do not automatically find the first individual if
	 * findNext parameter is false 
	 * 
	 * @param abox
	 * @param findNext
	 */
	protected LiteralIterator(ABox abox, boolean findNext) {
		this.abox = abox;
		nodeList = abox.getNodeNames();
		start = 0;
		stop = nodeList.size();
		index = start;
		
		if(findNext)
		    findNext();
	}		

	/**
	 * Create a limited iterator over the individuals in the ABox
	 * that only covers the individuals whose index in nodeList
	 * is between start ans stop indices.
	 * 
	 * @param abox
	 * @param start
	 * @param stop
	 */
	public LiteralIterator(ABox abox, int start, int stop) {
		this.abox = abox;
		this.nodeList = abox.getNodeNames();
		this.start = start;
		this.stop = Math.max( stop, nodeList.size() );
		index = start;

		findNext();
	}
    
    public int getIndex() {
        return index;
    }
	
	protected void findNext() {
		for(; index < stop; index++) {
		    Node node = abox.getNode( nodeList.get( index ) ) ;
			if( !node.isPruned() && node.isLiteral() )
				break;
		}
	}
	
	public boolean hasNext() {
		findNext();
		return index < stop;
	}
	
	public void reset() {
		index = start;
		findNext();
	}

	public void jump(int i) {
		index = i;
	}
	
	public Literal next() {
		findNext();
		Literal lit = abox.getLiteral(nodeList.get(index++));
		
		return lit;
	}

	public void remove() {
		throw new RuntimeException("Remove is not supported");
	}
	
}