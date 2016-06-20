/*
 * Copyright (c) 2002-2007, CWI and INRIA
 *
 * All rights reserved.
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *     * Redistributions of source code must retain the above copyright
 *       notice, this list of conditions and the following disclaimer.
 *     * Redistributions in binary form must reproduce the above copyright
 *       notice, this list of conditions and the following disclaimer in the
 *       documentation and/or other materials provided with the distribution.
 *     * Neither the name of the University of California, Berkeley nor the
 *       names of its contributors may be used to endorse or promote products
 *       derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE REGENTS AND CONTRIBUTORS ``AS IS'' AND ANY
 * EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE REGENTS AND CONTRIBUTORS BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package openllet.aterm;

public class ATermFwdVoid implements Visitor<ATerm>
{

	public ATermFwdVoid()
	{
		super();
	}

	@Override
	public ATerm visit(final ATerm v)
	{
		return v.accept(this);
	}

	@Override
	public ATerm visitATerm(final ATerm arg)
	{
		voidVisitATerm(arg);
		return arg;
	}

	@Override
	public ATerm visitInt(final ATermInt arg)
	{
		voidVisitInt(arg);
		return arg;
	}

	@Override
	public ATerm visitLong(final ATermLong arg)
	{
		voidVisitLong(arg);
		return arg;
	}

	@Override
	public ATerm visitReal(final ATermReal arg)
	{
		voidVisitReal(arg);
		return arg;
	}

	@Override
	public ATerm visitAppl(final ATermAppl arg)
	{
		voidVisitAppl(arg);
		return arg;
	}

	@Override
	public ATerm visitList(final ATermList arg)
	{
		voidVisitList(arg);
		return arg;
	}

	@Override
	public ATerm visitPlaceholder(final ATermPlaceholder arg)
	{
		voidVisitPlaceholder(arg);
		return arg;
	}

	@Override
	public ATerm visitBlob(final ATermBlob arg)
	{
		voidVisitBlob(arg);
		return arg;
	}

	@Override
	public ATerm visitAFun(final AFun fun)
	{
		return fun;
	}

	// methods to re-implement for void visitation

	public void voidVisitATerm(@SuppressWarnings("unused") final ATerm arg)
	{
		// Left empty intentionally.
	}

	public void voidVisitInt(final ATermInt arg)
	{
		voidVisitATerm(arg);
	}

	public void voidVisitLong(final ATermLong arg)
	{
		voidVisitATerm(arg);
	}

	public void voidVisitReal(final ATermReal arg)
	{
		voidVisitATerm(arg);
	}

	public void voidVisitAppl(final ATermAppl arg)
	{
		voidVisitATerm(arg);
	}

	public void voidVisitList(final ATermList arg)
	{
		voidVisitATerm(arg);
	}

	public void voidVisitPlaceholder(final ATermPlaceholder arg)
	{
		voidVisitATerm(arg);
	}

	public void voidVisitBlob(final ATermBlob arg)
	{
		voidVisitATerm(arg);
	}
}
