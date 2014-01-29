// Portions Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// Clark & Parsia, LLC parts of this source code are available under the terms of the Affero General Public License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com
//
// ---
// Portions Copyright (c) 2006 Christian Halaschek-Wiener
// Halaschek-Wiener parts of this source code are available under the terms of the MIT License.
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

package org.mindswap.pellet.tableau.completion.queue;

import java.util.logging.Logger;

import org.mindswap.pellet.ABox;
import org.mindswap.pellet.IndividualIterator;
import org.mindswap.pellet.Node;

import aterm.ATermAppl;


/**
 * <p>
 * Title: Completion Queue
 * </p>
 * <p>
 * Description: A queue for individuals that need to have completion rules
 * applied
 * </p>
 * <p>
 * Copyright: Copyright (c) 2008
 * </p>
 * <p>
 * Company: Clark & Parsia, LLC. <http://www.clarkparsia.com>
 * </p>
 * 
 * @author Christian Halaschek-Wiener
 */
public abstract class CompletionQueue extends IndividualIterator{
    
	public final static Logger log = Logger.getLogger( CompletionQueue.class.getName() );
	
	/**
	 * Pointer to the abox 
	 */
	//protected ABox abox;
	
	
	private boolean allowLiterals;
	
	protected int currentType;
	
	protected boolean closed;
	
	/**
	 * Constructor - create queue
	 * 
	 * @param abox
	 */
	protected CompletionQueue(ABox abox) {
		super(abox);		
		closed = false;
		allowLiterals = false;
	}		

	/**
	 * Find the next individual in a given queue 
	 * @param type
	 */
	protected abstract void  findNext(int type);
	

	
	/**
	 * Reset the queue to be the current nodes in the abox; Also reset the type index to 0
	 * 
	 * @param branch
	 */
	public abstract void restore(int branch);
	
	

	/**
	 * Add an element to the queue 
	 * @param x
	 * @param type
	 */
	public abstract void add(QueueElement x, NodeSelector s);
	
	/**
	 * Add an element to all queues 
	 * @param x
	 * @param type
	 */
	public abstract void add(QueueElement x);
	
	/**
	 * Reset the current pointer
	 * @param type
	 */
	@Override
	public abstract void reset(NodeSelector s);

	
	
	/**
	 * Set branch pointers to current pointer. This is done whenever abox.incrementBranch is called 
	 * @param branch
	 */
	public abstract void incrementBranch(int branch);
	

	/**
	 * Copy the queue
	 * 
	 * @return
	 */
	public abstract CompletionQueue copy();
	
	
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
	public abstract void print(int type);
	
	
	
	/**
	 * Print method for entire queue
	 *
	 */
	public abstract void print();

	
	/**
	 * Print branch information
	 *
	 */
	public void printBranchInfo(){
		return;
	}	
	
	/**
	 * Set flag to allow literals
	 * 
	 * @param val
	 */
	public void setAllowLiterals(boolean val){
		allowLiterals = val;
	}
	
	
	/**
	 * Flush the queue
	 * 
	 */
	public abstract void flushQueue();
	
	
	/**
	 * Flush the queue
	 * 
	 */
	protected abstract void flushQueue( NodeSelector s );
	
	
	/**
	 * Clear the queue
	 * 
	 */
	public abstract void clearQueue( NodeSelector s );
	
	/**
	 * Get flag to allow literals
	 * 
	 * @return
	 */
	protected boolean allowLiterals(){
		return allowLiterals;
	}
	
	/**
	 * Get next literal
	 * 
	 * @return
	 */
	public abstract Node nextLiteral();
	
	/**
	 * Get next label
	 * 
	 * @return
	 */
	protected ATermAppl getNextLabel(){
		return null;
	}
	
	
	public void setClosed( boolean isClash ){
		closed = isClash;
	}
}