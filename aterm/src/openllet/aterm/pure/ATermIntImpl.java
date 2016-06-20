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

package openllet.aterm.pure;

import java.util.List;
import openllet.aterm.AFun;
import openllet.aterm.ATerm;
import openllet.aterm.ATermAppl;
import openllet.aterm.ATermInt;
import openllet.aterm.ATermList;
import openllet.aterm.ATermPlaceholder;
import openllet.aterm.Visitor;
import openllet.shared.hash.SharedObject;

public class ATermIntImpl extends ATermImpl implements ATermInt
{
	private int _value;

	/**
	 * depricated Use the new constructor instead.
	 * 
	 * @param factory x
	 */
	@Deprecated
	protected ATermIntImpl(final PureFactory factory)
	{
		super(factory);
	}

	protected ATermIntImpl(final PureFactory factory, final ATermList annos, final int value)
	{
		super(factory, annos);

		_value = value;

		setHashCode(hashFunction());
	}

	@Override
	public int getType()
	{
		return ATerm.INT;
	}

	/**
	 * depricated Use the new constructor instead.
	 * 
	 * @param hashCode x
	 * @param annos x
	 * @param _value x
	 */
	protected void init(final int hashCode, final ATermList annos, final int value)
	{
		super.init(hashCode, annos);
		_value = value;
	}

	/**
	 * depricated Use the new constructor instead.
	 * 
	 * @param annos x
	 * @param _value x
	 */
	protected void initHashCode(final ATermList annos, final int value)
	{
		_value = value;
		internSetAnnotations(annos);
		setHashCode(hashFunction());
		//super.init(hashCode, annos);
	}

	@Override
	public SharedObject duplicate()
	{
		return this;
	}

	@Override
	public boolean equivalent(final SharedObject obj)
	{
		if (obj instanceof ATermInt)
		{
			final ATermInt peer = (ATermInt) obj;
			if (peer.getType() != getType())
				return false;

			return (peer.getInt() == _value && peer.getAnnotations().equals(getAnnotations()));
		}

		return false;
	}

	@Override
	protected boolean match(final ATerm pattern, final List<Object> list)
	{
		if (equals(pattern)) { return true; }

		if (pattern.getType() == ATerm.PLACEHOLDER)
		{
			final ATerm type = ((ATermPlaceholder) pattern).getPlaceholder();
			if (type.getType() == ATerm.APPL)
			{
				final ATermAppl appl = (ATermAppl) type;
				final AFun afun = appl.getAFun();
				if (afun.getName().equals("int") && afun.getArity() == 0 && !afun.isQuoted())
				{
					list.add(new Integer(_value));
					return true;
				}
			}
		}

		return super.match(pattern, list);
	}

	@Override
	public int getInt()
	{
		return _value;
	}

	@Override
	public ATerm setAnnotations(final ATermList annos)
	{
		return getPureFactory().makeInt(_value, annos);
	}

	@Override
	public ATerm accept(final Visitor<ATerm> v)
	{
		return v.visitInt(this);
	}

	private int hashFunction()
	{
		/* Set up the internal state */
		int a = 0x9e3779b9; /* the golden ratio; an arbitrary _value */
		int b = 0x9e3779b9; /* the golden ratio; an arbitrary _value */
		int c = 2; /* the previous hash _value */

		/*------------------------------------- handle the last 11 bytes */
		a += (getAnnotations().hashCode() << 8);
		a += (_value);

		a -= b;
		a -= c;
		a ^= (c >> 13);
		b -= c;
		b -= a;
		b ^= (a << 8);
		c -= a;
		c -= b;
		c ^= (b >> 13);
		a -= b;
		a -= c;
		a ^= (c >> 12);
		b -= c;
		b -= a;
		b ^= (a << 16);
		c -= a;
		c -= b;
		c ^= (b >> 5);
		a -= b;
		a -= c;
		a ^= (c >> 3);
		b -= c;
		b -= a;
		b ^= (a << 10);
		c -= a;
		c -= b;
		c ^= (b >> 15);

		/*-------------------------------------------- report the result */
		return c;
	}

}
