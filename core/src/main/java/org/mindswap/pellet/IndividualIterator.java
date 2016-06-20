// Portions Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// Clark & Parsia, LLC parts of this source code are available under the terms of the Affero General Public License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package org.mindswap.pellet;

import java.util.Iterator;
import java.util.List;
import openllet.aterm.ATermAppl;
import org.mindswap.pellet.tableau.completion.queue.NodeSelector;

/**
 * An iterator to return _nodes in the _order they are added. Having a separate iterator instead of using _nodes.iterator() allows to change the _nodes table
 * without resetting the iteration process.
 *
 * @author Evren Sirin
 */
public class IndividualIterator implements Iterator<Individual>
{
	/**
	 * ABox where the individuals are stored
	 */
	protected ABox _abox;
	/**
	 * List of _node names
	 */
	protected List<ATermAppl> _nodeList;
	/**
	 * Last returned _index
	 */
	protected int _index;

	/**
	 * Index where iterator stops (size of list by default)
	 */
	protected int _stop;

	/**
	 * Create an iterator over all the individuals in the ABox
	 */
	public IndividualIterator(final ABox abox)
	{
		this._abox = abox;
		_nodeList = abox.getNodeNames();
		_stop = _nodeList.size();
		_index = 0;

		findNext();
	}

	protected void findNext()
	{
		for (; _index < _stop; _index++)
		{
			final Node node = _abox.getNode(_nodeList.get(_index));
			if (!node.isPruned() && node.isIndividual())
				break;
		}
	}

	@Override
	public boolean hasNext()
	{
		findNext();
		return _index < _stop;
	}

	public void reset(@SuppressWarnings("unused") final NodeSelector s)
	{
		_index = 0;
		findNext();
	}

	@Override
	public Individual next()
	{
		findNext();
		final Individual ind = _abox.getIndividual(_nodeList.get(_index++));

		return ind;
	}

	@Override
	public void remove()
	{
		throw new UnsupportedOperationException();
	}
}
