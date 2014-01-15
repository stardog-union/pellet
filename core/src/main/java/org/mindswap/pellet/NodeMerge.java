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


package org.mindswap.pellet;

import aterm.ATermAppl;

/**
 * Stores a pair of nodes to be merged. Order of nodes is important, always first node is 
 * going to be merged to the second one.
 * 
 * @author Evren Sirin
 */
public class NodeMerge {
	private ATermAppl source;
	private ATermAppl target;
	private DependencySet ds;
	
	public NodeMerge(Node y, Node z) {
		this.setSource( y.getName() );
		this.setTarget( z.getName() );
	}
	
	public NodeMerge(Node y, Node z, DependencySet ds) {
		this.setSource( y.getName() );
		this.setTarget( z.getName() );		
		this.ds = ds;
	}
	
	NodeMerge(ATermAppl y, ATermAppl z) {
		this.setSource( y );
		this.setTarget( z );
	}
	
	public String toString() {
		return getSource() + " -> " + getTarget() + " " + ds;
	}

	/**
	 * @param source the source to set
	 */
	public void setSource(ATermAppl source) {
		this.source = source;
	}

	/**
	 * @return the source
	 */
	public ATermAppl getSource() {
		return source;
	}

	/**
	 * @param target the target to set
	 */
	public void setTarget(ATermAppl target) {
		this.target = target;
	}

	/**
	 * @return the target
	 */
	public ATermAppl getTarget() {
		return target;
	}

	/**
	 * @return the dependecy set
	 */
	public DependencySet getDepends() {
		return ds;
	}
}