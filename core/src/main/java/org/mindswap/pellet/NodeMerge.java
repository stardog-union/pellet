// Portions Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// Clark & Parsia, LLC parts of this _source code are available under the terms of the Affero General Public License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com
//
// ---
// Portions Copyright (c) 2003 Ron Alford, Mike Grove, Bijan Parsia, Evren Sirin
// Alford, Grove, Parsia, Sirin parts of this _source code are available under the terms of the MIT License.
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

package org.mindswap.pellet;

import openllet.aterm.ATermAppl;

/**
 * Stores a pair of _nodes to be merged. Order of _nodes is important, always first _node is going to be merged to the second one.
 *
 * @author Evren Sirin
 */
public class NodeMerge
{
	private ATermAppl _source;
	private ATermAppl _target;
	private DependencySet _ds;

	public NodeMerge(final Node y, final Node z)
	{
		this.setSource(y.getName());
		this.setTarget(z.getName());
	}

	public NodeMerge(final Node y, final Node z, final DependencySet ds)
	{
		this.setSource(y.getName());
		this.setTarget(z.getName());
		this._ds = ds;
	}

	NodeMerge(final ATermAppl y, final ATermAppl z)
	{
		this.setSource(y);
		this.setTarget(z);
	}

	@Override
	public String toString()
	{
		return getSource() + " -> " + getTarget() + " " + _ds;
	}

	/**
	 * @param _source the _source to set
	 */
	public void setSource(final ATermAppl source)
	{
		this._source = source;
	}

	/**
	 * @return the _source
	 */
	public ATermAppl getSource()
	{
		return _source;
	}

	/**
	 * @param _target the _target to set
	 */
	public void setTarget(final ATermAppl target)
	{
		this._target = target;
	}

	/**
	 * @return the _target
	 */
	public ATermAppl getTarget()
	{
		return _target;
	}

	/**
	 * @return the dependecy set
	 */
	public DependencySet getDepends()
	{
		return _ds;
	}
}
