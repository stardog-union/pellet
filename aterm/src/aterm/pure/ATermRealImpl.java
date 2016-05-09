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

package aterm.pure;

import aterm.AFun;
import aterm.ATerm;
import aterm.ATermAppl;
import aterm.ATermList;
import aterm.ATermPlaceholder;
import aterm.ATermReal;
import aterm.Visitable;
import aterm.Visitor;
import java.util.List;
import jjtraveler.VisitFailure;
import shared.HashFunctions;
import shared.SharedObject;

public class ATermRealImpl extends ATermImpl implements ATermReal
{
	private double value;

	/**
	 * depricated Use the new constructor instead.
	 * 
	 * @param factory x
	 */
	protected ATermRealImpl(PureFactory factory)
	{
		super(factory);
	}

	protected ATermRealImpl(PureFactory factory, ATermList annos, double value)
	{
		super(factory, annos);

		this.value = value;

		setHashCode(HashFunctions.doobs(new Object[] { annos, new Double(value) }));
	}

	@Override
	public int getType()
	{
		return ATerm.REAL;
	}

	/**
	 * depricated Use the new constructor instead.
	 * 
	 * @param hashCode x
	 * @param annos x
	 * @param value x
	 */
	protected void init(int hashCode, ATermList annos, double value)
	{
		super.init(hashCode, annos);
		this.value = value;
	}

	@Override
	public SharedObject duplicate()
	{
		return this;
	}

	@Override
	public boolean equivalent(SharedObject obj)
	{
		if (obj instanceof ATermReal)
		{
			final ATermReal peer = (ATermReal) obj;
			if (peer.getType() != getType())
				return false;

			return (peer.getReal() == value && peer.getAnnotations().equals(getAnnotations()));
		}

		return false;
	}

	@Override
	protected boolean match(ATerm pattern, List<Object> list)
	{
		if (equals(pattern)) { return true; }

		if (pattern.getType() == ATerm.PLACEHOLDER)
		{
			final ATerm type = ((ATermPlaceholder) pattern).getPlaceholder();
			if (type.getType() == ATerm.APPL)
			{
				final ATermAppl appl = (ATermAppl) type;
				final AFun afun = appl.getAFun();
				if (afun.getName().equals("real") && afun.getArity() == 0 && !afun.isQuoted())
				{
					list.add(new Double(value));
					return true;
				}
			}
		}

		return super.match(pattern, list);
	}

	@Override
	public double getReal()
	{
		return value;
	}

	@Override
	public ATerm setAnnotations(ATermList annos)
	{
		return getPureFactory().makeReal(value, annos);
	}

	@Override
	public Visitable accept(Visitor v) throws VisitFailure
	{
		return v.visitReal(this);
	}
}
