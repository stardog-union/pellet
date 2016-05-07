// The MIT License
//
// Copyright (c) 2007 Christian Halaschek-Wiener
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

package org.mindswap.pellet.tableau.completion.incremental;

import aterm.ATermAppl;
import org.mindswap.pellet.tableau.branch.Branch;

/**
 * A depedency for a closed disjunct, merge pair, etc. for a _branch
 *
 * @author Christian Halaschek-Wiener
 */
public class CloseBranchDependency extends BranchDependency
{

	private final int tryNext;

	private final Branch theBranch;

	public CloseBranchDependency(final ATermAppl assertion, final int tryNext, final Branch theBranch)
	{
		super(assertion);
		this.tryNext = tryNext;
		this.theBranch = theBranch;
	}

	/**
	 * @return
	 */
	public ATermAppl getInd()
	{
		return theBranch.getNode().getName();
	}

	/**
	 * ToString method
	 */
	@Override
	public String toString()
	{
		return "Branch [" + theBranch.getNode().getName() + "]  -  [" + theBranch.getBranch() + "]";
	}

	/**
	 * Equals method
	 */
	@Override
	public boolean equals(final Object other)
	{
		if (other instanceof CloseBranchDependency)
			return this.getInd().equals(((CloseBranchDependency) other).getInd()) && this.getBranch() == ((CloseBranchDependency) other).getBranch() && this.tryNext == ((CloseBranchDependency) other).tryNext;
		else
			return false;
	}

	/**
	 * Hashcode method TODO: this may not be sufficient
	 */
	@Override
	public int hashCode()
	{
		return this.getInd().hashCode() + this.getBranch() + this.tryNext;
	}

	public int getBranch()
	{
		return theBranch.getBranch();
	}

	public int getTryNext()
	{
		return tryNext;
	}

	public Branch getTheBranch()
	{
		return theBranch;
	}

}
