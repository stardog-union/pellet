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
 * A basic queue for individuals that need to have completion rules applied
 */
public class BasicCompletionQueue extends CompletionQueue
{
	/**
	 * The queue - array - each entry is an arraylist for a particular rule type
	 */
	protected List<ATermAppl> queue;

	/**
	 * Set to track duplicates for new elements list for queue
	 */
	protected Set<ATermAppl> newQueue;

	//TODO: This will be refactored; however currently there are some unit tests which will not
	//terminate due to the _order in which the completion rules are applied to individuals
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
	 * @param _abox
	 */
	public BasicCompletionQueue(final ABox abox)
	{
		super(abox);
		queue = new ArrayList<>();
		newQueue = new HashSet<>();
		newQueueList = new ArrayList<>();

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
	@Override
	protected void findNext(final int type)
	{
		for (; current < cutOff; current++)
		{
			Node node = abox.getNode(queue.get(current));

			//because we do not maitain the queue during restore this _node could be non-existent
			if (node == null)
				continue;

			node = node.getSame();

			if (((node.isLiteral() && allowLiterals()) || (node.isIndividual() && !allowLiterals())) && !node.isPruned())
				break;
		}
	}

	/**
	 * Test if there is another element on the queue to process
	 *
	 * @param type
	 * @return
	 */
	@Override
	public boolean hasNext()
	{
		findNext(-1);
		return current < cutOff;
	}

	/**
	 * Reset the queue to be the current nodes in the _abox; Also reset the type index to 0
	 *
	 * @param _branch
	 */
	@Override
	public void restore(final int branch)
	{
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
	 * 
	 * @param type
	 * @return
	 */
	@Override
	public Individual next()
	{
		//get the next index
		findNext(-1);
		Individual ind = abox.getIndividual(queue.get(current));
		ind = ind.getSame();
		current++;
		return ind;

	}

	/**
	 * Get the next element of a queue of a given type
	 * 
	 * @param type
	 * @return
	 */
	@Override
	public Node nextLiteral()
	{
		//get the next index
		findNext(-1);
		Node node = abox.getNode(queue.get(current));
		node = node.getSame();
		current++;
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
		if (!newQueue.contains(x.getNode()))
		{
			newQueue.add(x.getNode());
			newQueueList.add(x.getNode());
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
		cutOff = end;
		current = 0;
	}

	/**
	 * Set _branch pointers to current pointer. This is done whenever _abox.incrementBranch is called
	 * 
	 * @param _branch
	 */
	@Override
	public void incrementBranch(final int branch)
	{
		return;
	}

	/**
	 * Copy the queue
	 *
	 * @return
	 */
	@Override
	public BasicCompletionQueue copy()
	{
		final BasicCompletionQueue copy = new BasicCompletionQueue(this.abox);

		copy.queue = new ArrayList<>(this.queue);
		copy.newQueue = new HashSet<>(this.newQueue);
		copy.newQueueList = new ArrayList<>(this.newQueueList);

		copy.current = this.current;
		copy.cutOff = this.cutOff;
		copy.backtracked = this.backtracked;
		copy.end = this.end;
		copy.setAllowLiterals(this.allowLiterals());

		return copy;
	}

	/**
	 * Set the _abox for the queue
	 * 
	 * @param ab
	 */
	@Override
	public void setABox(final ABox ab)
	{
		this.abox = ab;
	}

	/**
	 * Print method for a given queue type
	 *
	 * @param type
	 */
	@Override
	public void print(final int type)
	{
		System.out.println("Queue: " + queue);
	}

	/**
	 * Print method for entire queue
	 */
	@Override
	public void print()
	{
		System.out.println("Queue: " + queue);
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
		if (!backtracked && !closed)
			queue.clear();
		else
			if (closed)
				if (!abox.isClosed())
					closed = false;

		queue.addAll(newQueueList);

		newQueue.clear();
		newQueueList.clear();

		end = queue.size();

		backtracked = false;
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
