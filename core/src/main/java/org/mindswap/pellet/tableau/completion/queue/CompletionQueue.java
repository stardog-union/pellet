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

import aterm.ATermAppl;
import java.util.logging.Logger;
import org.mindswap.pellet.ABox;
import org.mindswap.pellet.IndividualIterator;
import org.mindswap.pellet.Node;

/**
 * <p>
 * Title: Completion Queue
 * </p>
 * <p>
 * Description: A _queue for individuals that need to have completion rules applied
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
public abstract class CompletionQueue extends IndividualIterator
{

	public final static Logger log = Logger.getLogger(CompletionQueue.class.getName());

	/**
	 * Pointer to the _abox
	 */
	//protected ABox _abox;

	private boolean allowLiterals;

	protected int currentType;

	protected boolean closed;

	/**
	 * Constructor - create _queue
	 *
	 * @param _abox
	 */
	protected CompletionQueue(final ABox abox)
	{
		super(abox);
		closed = false;
		allowLiterals = false;
	}

	/**
	 * Find the next individual in a given _queue
	 * 
	 * @param type
	 */
	protected abstract void findNext(int type);

	/**
	 * Reset the _queue to be the _current _nodes in the _abox; Also reset the type _index to 0
	 *
	 * @param _branch
	 */
	public abstract void restore(int branch);

	/**
	 * Add an element to the _queue
	 * 
	 * @param x
	 * @param type
	 */
	public abstract void add(QueueElement x, NodeSelector s);

	/**
	 * Add an element to all queues
	 * 
	 * @param x
	 * @param type
	 */
	public abstract void add(QueueElement x);

	/**
	 * Reset the _current pointer
	 * 
	 * @param type
	 */
	@Override
	public abstract void reset(NodeSelector s);

	/**
	 * Set _branch pointers to _current pointer. This is done whenever _abox.incrementBranch is called
	 * 
	 * @param _branch
	 */
	public abstract void incrementBranch(int branch);

	/**
	 * Copy the _queue
	 *
	 * @return
	 */
	public abstract CompletionQueue copy();

	/**
	 * Set the _abox for the _queue
	 * 
	 * @param ab
	 */
	public void setABox(final ABox ab)
	{
		this._abox = ab;
	}

	/**
	 * Print method for a given _queue type
	 *
	 * @param type
	 */
	public abstract void print(int type);

	/**
	 * Print method for entire _queue
	 */
	public abstract void print();

	/**
	 * Print _branch information
	 */
	public void printBranchInfo()
	{
		return;
	}

	/**
	 * Set flag to allow literals
	 *
	 * @param val
	 */
	public void setAllowLiterals(final boolean val)
	{
		allowLiterals = val;
	}

	/**
	 * Flush the _queue
	 */
	public abstract void flushQueue();

	/**
	 * Flush the _queue
	 */
	protected abstract void flushQueue(NodeSelector s);

	/**
	 * Clear the _queue
	 */
	public abstract void clearQueue(NodeSelector s);

	/**
	 * Get flag to allow literals
	 *
	 * @return
	 */
	protected boolean allowLiterals()
	{
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
	protected ATermAppl getNextLabel()
	{
		return null;
	}

	public void setClosed(final boolean isClash)
	{
		closed = isClash;
	}
}
