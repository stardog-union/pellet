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

/**
 * Any visitor class should implement the Visitor interface.
 */
public interface Visitor<T extends ATerm>
{
	/**
	 * Pay a visit to any visitable object.
	 */
	public T visit(final T any);

	public T visitATerm(ATerm arg);

	default T visitInt(final ATermInt arg)
	{
		return visitATerm(arg);
	}

	default T visitLong(final ATermLong arg)
	{
		return visitATerm(arg);
	}

	default T visitReal(final ATermReal arg)
	{
		return visitATerm(arg);
	}

	default T visitAppl(final ATermAppl arg)
	{
		return visitATerm(arg);
	}

	default T visitList(final ATermList arg)
	{
		return visitATerm(arg);
	}

	default T visitPlaceholder(final ATermPlaceholder arg)
	{
		return visitATerm(arg);
	}

	default T visitBlob(final ATermBlob arg)
	{
		return visitATerm(arg);
	}

	public T visitAFun(final AFun fun);
}
