// Portions Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// Clark & Parsia, LLC parts of this source code are available under the terms of the Affero General Public License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com
//
// ---
// Portions Copyright (c) 2003 Ron Alford, Mike Grove, Bijan Parsia, Evren Sirin
// Alford, Grove, Parsia, Sirin parts of this source code are available under the terms of the MIT License.
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

package org.mindswap.pellet.tableau.branch;

import java.util.logging.Logger;
import net.katk.tools.Log;
import org.mindswap.pellet.ABox;
import org.mindswap.pellet.Clash;
import org.mindswap.pellet.DependencySet;
import org.mindswap.pellet.Node;
import org.mindswap.pellet.PelletOptions;
import org.mindswap.pellet.tableau.completion.CompletionStrategy;

/**
 * <p>
 * Title:
 * </p>
 * <p>
 * Description:
 * </p>
 * <p>
 * Copyright: Copyright (c) 2009
 * </p>
 * <p>
 * Company: Clark & Parsia, LLC. <http://www.clarkparsia.com>
 * </p>
 *
 * @author Evren Sirin
 */
public abstract class Branch
{
	public static final Logger log = Log.getLogger(Branch.class);

	protected ABox _abox;
	protected CompletionStrategy _strategy;
	protected int _branch;
	protected int _tryCount;
	protected int _tryNext;

	private DependencySet _termDepends;
	private DependencySet _prevDS;

	// store things that can be changed after this _branch
	protected int _anonCount;
	protected int _nodeCount;

	Branch(final ABox abox, final CompletionStrategy strategy, final DependencySet ds, final int n)
	{
		this._abox = abox;
		this.setStrategy(strategy);

		setTermDepends(ds);
		setTryCount(n);
		_prevDS = DependencySet.EMPTY;
		setTryNext(0);

		setBranch(abox.getBranch());
		setAnonCount(abox.getAnonCount());
		setNodeCount(abox.size());
	}

	public void setLastClash(final DependencySet ds)
	{
		if (getTryNext() >= 0)
		{
			_prevDS = _prevDS.union(ds, _abox.doExplanation());
			if (PelletOptions.USE_INCREMENTAL_DELETION)
				//CHW - added for incremental deletions support THIS SHOULD BE MOVED TO SUPER
				_abox.getKB().getDependencyIndex().addCloseBranchDependency(this, ds);
		}
	}

	public DependencySet getCombinedClash()
	{
		return _prevDS;
	}

	public void setStrategy(final CompletionStrategy strategy)
	{
		this._strategy = strategy;
	}

	public boolean tryNext()
	{
		// nothing more to try, update the clash dependency
		if (getTryNext() == getTryCount())
			if (!_abox.isClosed())
				_abox.setClash(Clash.unexplained(getNode(), _termDepends));
			else
				_abox.getClash().setDepends(getCombinedClash());

		// if there is no clash try next possibility
		if (!_abox.isClosed())
			tryBranch();

		// there is a clash so there is no point in trying this
		// _branch again. remove this _branch from clash dependency
		if (_abox.isClosed())
			if (!PelletOptions.USE_INCREMENTAL_DELETION)
				_abox.getClash().getDepends().remove(getBranch());

		return !_abox.isClosed();
	}

	public abstract Branch copyTo(ABox abox);

	protected abstract void tryBranch();

	public abstract Node getNode();

	@Override
	public String toString()
	{
		//		return "Branch " + _branch + " (" + _tryCount + ")";
		return "Branch on _node " + getNode() + "  Branch number: " + getBranch() + " " + getTryNext() + "(" + getTryCount() + ")";
	}

	/**
	 * Added for to re-open closed branches. This is needed for incremental reasoning through deletions
	 *
	 * @param _index The shift _index
	 */
	public abstract void shiftTryNext(int index);

	/**
	 * @param _nodeCount the _nodeCount to set
	 */
	public void setNodeCount(final int nodeCount)
	{
		this._nodeCount = nodeCount;
	}

	/**
	 * @return the _nodeCount
	 */
	public int getNodeCount()
	{
		return _nodeCount;
	}

	public void setBranch(final int branch)
	{
		this._branch = branch;
	}

	/**
	 * @return the _branch
	 */
	public int getBranch()
	{
		return _branch;
	}

	/**
	 * @return the _anonCount
	 */
	public int getAnonCount()
	{
		return _anonCount;
	}

	/**
	 * @param _tryNext the _tryNext to set
	 */
	public void setTryNext(final int tryNext)
	{
		this._tryNext = tryNext;
	}

	/**
	 * @return the _tryNext
	 */
	public int getTryNext()
	{
		return _tryNext;
	}

	/**
	 * @param _tryCount the _tryCount to set
	 */
	public void setTryCount(final int tryCount)
	{
		this._tryCount = tryCount;
	}

	/**
	 * @return the _tryCount
	 */
	public int getTryCount()
	{
		return _tryCount;
	}

	/**
	 * @param _termDepends the _termDepends to set
	 */
	public void setTermDepends(final DependencySet termDepends)
	{
		this._termDepends = termDepends;
	}

	/**
	 * @return the _termDepends
	 */
	public DependencySet getTermDepends()
	{
		return _termDepends;
	}

	/**
	 * @param _anonCount the _anonCount to set
	 */
	public void setAnonCount(final int anonCount)
	{
		this._anonCount = anonCount;
	}

}
