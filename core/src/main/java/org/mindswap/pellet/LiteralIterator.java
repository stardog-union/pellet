// Portions Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// Clark & Parsia, LLC parts of this source code are available under the terms of the Affero General Public License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package org.mindswap.pellet;

import aterm.ATermAppl;
import java.util.Iterator;
import java.util.List;

/**
 * An iterator to return _nodes in the _order they are added. Having a seperate iterator instead of using _nodes.iterator() allows to change the _nodes table
 * without resetting the iteration process.
 *
 * @author Evren Sirin
 */
public class LiteralIterator implements Iterator<Literal>
{
	/**
	 * ABox where literals are stoired
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
	 * Index where iterator starts (0 be default)
	 */
	protected int _start;
	/**
	 * Index where iterator stops (size of list by default)
	 */
	protected int _stop;

	/**
	 * Create an iterator over all the individuals in the ABox
	 */
	public LiteralIterator(final ABox abox)
	{
		this(abox, true);
	}

	/**
	 * Create an iterator over all the individuals in the ABox but do not automatically find the first individual if findNext parameter is false
	 *
	 * @param _abox
	 * @param findNext
	 */
	protected LiteralIterator(final ABox abox, final boolean findNext)
	{
		this._abox = abox;
		_nodeList = abox.getNodeNames();
		_start = 0;
		_stop = _nodeList.size();
		_index = _start;

		if (findNext)
			findNext();
	}

	/**
	 * Create a limited iterator over the individuals in the ABox that only covers the individuals whose _index in _nodeList is between _start ans _stop indices.
	 *
	 * @param _abox
	 * @param _start
	 * @param _stop
	 */
	public LiteralIterator(final ABox abox, final int start, final int stop)
	{
		this._abox = abox;
		this._nodeList = abox.getNodeNames();
		this._start = start;
		this._stop = Math.max(stop, _nodeList.size());
		_index = start;

		findNext();
	}

	public int getIndex()
	{
		return _index;
	}

	protected void findNext()
	{
		for (; _index < _stop; _index++)
		{
			final Node node = _abox.getNode(_nodeList.get(_index));
			if (!node.isPruned() && node.isLiteral())
				break;
		}
	}

	@Override
	public boolean hasNext()
	{
		findNext();
		return _index < _stop;
	}

	public void reset()
	{
		_index = _start;
		findNext();
	}

	public void jump(final int i)
	{
		_index = i;
	}

	@Override
	public Literal next()
	{
		findNext();
		final Literal lit = _abox.getLiteral(_nodeList.get(_index++));

		return lit;
	}

	@Override
	public void remove()
	{
		throw new RuntimeException("Remove is not supported");
	}

}
