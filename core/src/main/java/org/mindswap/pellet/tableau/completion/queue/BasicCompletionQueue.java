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

import aterm.ATermAppl;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.mindswap.pellet.ABox;
import org.mindswap.pellet.Individual;
import org.mindswap.pellet.Node;

/**
 * A basic _queue for individuals that need to have completion rules applied
 */
public class BasicCompletionQueue extends CompletionQueue
{
	/**
	 * The _queue - array - each entry is an arraylist for a particular rule type
	 */
	protected List<ATermAppl> _queue;

	/**
	 * Set to track duplicates for new elements list for _queue
	 */
	protected Set<ATermAppl> _newQueue;

	//TODO: This will be refactored; however currently there are some unit tests which will not
	//terminate due to the _order in which the completion rules are applied to individuals
	//ont the _queue. An example of this is MiscTests.testIFDP3() - in this example,
	//if the LiteralRule is applied to the individual "b" first, then an infinite number
	//of non-deterministic choices are created...talk to Evren about this.

	/**
	 * List to hold new elements for the _queue
	 */
	protected List<ATermAppl> _newQueueList;

	/**
	 * List of _current index pointer for each _queue
	 */
	protected int _current;

	/**
	 * List of _current index pointer for each _queue
	 */
	protected int _end;

	/**
	 * List of _current index pointer for the stopping point at each _queue
	 */
	protected int _cutOff;

	/**
	 * Flag set for when the kb is restored - in this case we do not want to flush the _queue immediatly
	 */
	protected boolean _backtracked;

	/**
	 * Constructor - create _queue
	 *
	 * @param _abox
	 */
	public BasicCompletionQueue(final ABox abox)
	{
		super(abox);
		_queue = new ArrayList<>();
		_newQueue = new HashSet<>();
		_newQueueList = new ArrayList<>();

		_current = 0;
		_cutOff = 0;
		_end = 0;
		_backtracked = false;
	}

	/**
	 * Find the next individual in a given _queue
	 *
	 * @param type
	 */
	@Override
	protected void findNext(final int type)
	{
		for (; _current < _cutOff; _current++)
		{
			Node node = abox.getNode(_queue.get(_current));

			//because we do not maitain the _queue during restore this _node could be non-existent
			if (node == null)
				continue;

			node = node.getSame();

			if (((node.isLiteral() && allowLiterals()) || (node.isIndividual() && !allowLiterals())) && !node.isPruned())
				break;
		}
	}

	/**
	 * Test if there is another element on the _queue to process
	 *
	 * @param type
	 * @return
	 */
	@Override
	public boolean hasNext()
	{
		findNext(-1);
		return _current < _cutOff;
	}

	/**
	 * Reset the _queue to be the _current _nodes in the _abox; Also reset the type index to 0
	 *
	 * @param _branch
	 */
	@Override
	public void restore(final int branch)
	{
		_queue.addAll(_newQueueList);
		_newQueue.clear();
		_newQueueList.clear();
		_end = _queue.size();
		_current = 0;
		_cutOff = _end;
		_backtracked = true;
	}

	/**
	 * Get the next element of a _queue of a given type
	 * 
	 * @param type
	 * @return
	 */
	@Override
	public Individual next()
	{
		//get the next index
		findNext(-1);
		Individual ind = abox.getIndividual(_queue.get(_current));
		ind = ind.getSame();
		_current++;
		return ind;

	}

	/**
	 * Get the next element of a _queue of a given type
	 * 
	 * @param type
	 * @return
	 */
	@Override
	public Node nextLiteral()
	{
		//get the next index
		findNext(-1);
		Node node = abox.getNode(_queue.get(_current));
		node = node.getSame();
		_current++;
		return node;
	}

	@Override
	public void add(final QueueElement x, final NodeSelector s)
	{
		add(x);
	}

	@Override
	public void add(final QueueElement x)
	{
		if (!_newQueue.contains(x.getNode()))
		{
			_newQueue.add(x.getNode());
			_newQueueList.add(x.getNode());
		}
	}

	/**
	 * Reset the cutoff for a given type index
	 * 
	 * @param type
	 */
	@Override
	public void reset(final NodeSelector s)
	{
		_cutOff = _end;
		_current = 0;
	}

	/**
	 * Set _branch pointers to _current pointer. This is done whenever _abox.incrementBranch is called
	 * 
	 * @param _branch
	 */
	@Override
	public void incrementBranch(final int branch)
	{
		return;
	}

	/**
	 * Copy the _queue
	 *
	 * @return
	 */
	@Override
	public BasicCompletionQueue copy()
	{
		final BasicCompletionQueue copy = new BasicCompletionQueue(this.abox);

		copy._queue = new ArrayList<>(this._queue);
		copy._newQueue = new HashSet<>(this._newQueue);
		copy._newQueueList = new ArrayList<>(this._newQueueList);

		copy._current = this._current;
		copy._cutOff = this._cutOff;
		copy._backtracked = this._backtracked;
		copy._end = this._end;
		copy.setAllowLiterals(this.allowLiterals());

		return copy;
	}

	/**
	 * Set the _abox for the _queue
	 * 
	 * @param ab
	 */
	@Override
	public void setABox(final ABox ab)
	{
		this.abox = ab;
	}

	/**
	 * Print method for a given _queue type
	 *
	 * @param type
	 */
	@Override
	public void print(final int type)
	{
		System.out.println("Queue: " + _queue);
	}

	/**
	 * Print method for entire _queue
	 */
	@Override
	public void print()
	{
		System.out.println("Queue: " + _queue);
	}

	/**
	 * Remove method for abstract class
	 */
	@Override
	public void remove()
	{
		throw new RuntimeException("Remove is not supported");
	}

	@Override
	public void flushQueue()
	{
		if (!_backtracked && !closed)
			_queue.clear();
		else
			if (closed)
				if (!abox.isClosed())
					closed = false;

		_queue.addAll(_newQueueList);

		_newQueue.clear();
		_newQueueList.clear();

		_end = _queue.size();

		_backtracked = false;
	}

	@Override
	protected void flushQueue(final NodeSelector s)
	{
		return;
	}

	@Override
	public void clearQueue(final NodeSelector s)
	{
		return;
	}

}
