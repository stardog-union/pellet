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
import openllet.aterm.ATermList;
import openllet.aterm.ATermPlaceholder;
import openllet.aterm.Visitor;
import openllet.shared.hash.SharedObject;

public class ATermApplImpl extends ATermImpl implements ATermAppl
{
	private AFun _fun;

	private ATerm[] _args;

	/**
	 * depricated Use the new constructor instead.
	 * 
	 * @param factory x
	 */
	protected ATermApplImpl(final PureFactory factory)
	{
		super(factory);
	}

	protected ATermApplImpl(final PureFactory factory, final ATermList annos, final AFun fun, final ATerm[] i_args)
	{
		super(factory, annos);

		_fun = fun;
		_args = i_args;

		setHashCode(hashFunction());
	}

	@Override
	public int getType()
	{
		return ATerm.APPL;
	}

	/**
	 * depricated Use the new constructor instead.
	 * 
	 * @param hashCode x
	 * @param annos x
	 * @param _fun x
	 * @param i_args x
	 */
	protected void init(final int hashCode, final ATermList annos, final AFun fun, final ATerm[] i_args)
	{
		super.init(hashCode, annos);
		_fun = fun;
		_args = new ATerm[i_args.length];

		System.arraycopy(i_args, 0, _args, 0, _args.length);
	}

	/**
	 * depricated Use the new constructor instead.
	 * 
	 * @param annos x
	 * @param _fun x
	 * @param i_args x
	 */
	protected void initHashCode(final ATermList annos, final AFun fun, final ATerm[] i_args)
	{
		_fun = fun;
		_args = i_args;
		internSetAnnotations(annos);
		setHashCode(hashFunction());
	}

	@Override
	public SharedObject duplicate()
	{
		return this;
	}

	protected ATermAppl make(final AFun fun, final ATerm[] i_args, final ATermList annos)
	{
		return getPureFactory().makeAppl(fun, i_args, annos);
	}

	protected ATermAppl make(final AFun fun, final ATerm[] i_args)
	{
		return make(fun, i_args, getPureFactory().makeList());
	}

	@Override
	public boolean equivalent(final SharedObject obj)
	{
		if (obj instanceof ATermAppl)
		{
			final ATermAppl peer = (ATermAppl) obj;
			if (peer.getType() != getType())
				return false;

			if (peer.getAFun().equals(_fun))
			{
				for (int i = 0; i < _args.length; i++)
				{
					if (!peer.getArgument(i).equals(_args[i])) { return false; }
				}
				return peer.getAnnotations().equals(getAnnotations());
			}
		}
		return false;
	}

	@Override
	protected boolean match(final ATerm pattern, final List<Object> list)
	{
		if (pattern.getType() == APPL)
		{
			final ATermAppl appl = (ATermAppl) pattern;
			if (_fun.equals(appl.getAFun())) { return matchArguments(appl.getArgumentArray(), list); }
			return false;
		}

		if (pattern.getType() == PLACEHOLDER)
		{
			final ATerm type = ((ATermPlaceholder) pattern).getPlaceholder();
			if (type.getType() == APPL)
			{
				final ATermAppl appl = (ATermAppl) type;
				final AFun afun = appl.getAFun();
				if (afun.getName().equals("appl") && !afun.isQuoted())
				{
					list.add(_fun.getName());
					return matchArguments(appl.getArgumentArray(), list);
				}
				else
					if (afun.getName().equals("str") && !afun.isQuoted())
					{
						if (_fun.isQuoted())
						{
							list.add(_fun.getName());
							return matchArguments(appl.getArgumentArray(), list);
						}
					}
					else
						if (afun.getName().equals("fun") && !afun.isQuoted())
						{
							if (!_fun.isQuoted())
							{
								list.add(_fun.getName());
								return matchArguments(appl.getArgumentArray(), list);
							}
						}
						else
							if (afun.getName().equals("id") && !afun.isQuoted())
							{
								if (!_fun.isQuoted())
								{
									list.add(_fun.getName());
									return matchArguments(appl.getArgumentArray(), list);
								}
							}
			}
		}

		return super.match(pattern, list);
	}

	boolean matchArguments(final ATerm[] pattern_args, final List<Object> list)
	{
		for (int i = 0; i < _args.length; i++)
		{
			if (i >= pattern_args.length)
				return false;

			final ATerm arg = _args[i];
			final ATerm pattern_arg = pattern_args[i];

			if (pattern_arg.getType() == PLACEHOLDER)
			{
				final ATerm ph_type = ((ATermPlaceholder) pattern_arg).getPlaceholder();
				if (ph_type.getType() == APPL)
				{
					final ATermAppl appl = (ATermAppl) ph_type;
					if (appl.getName().equals("list") && appl.getArguments().isEmpty())
					{
						ATermList result = getPureFactory().getEmpty();
						for (int j = _args.length - 1; j >= i; j--)
							result = result.insert(_args[j]);
						list.add(result);
						return true;
					}
				}
			}

			final List<Object> submatches = arg.match(pattern_arg);
			if (submatches == null)
				return false;
			list.addAll(submatches);
		}

		return _args.length == pattern_args.length;
	}

	@Override
	public ATerm[] getArgumentArray()
	{
		return _args;
	}

	@Override
	public AFun getAFun()
	{
		return _fun;
	}

	@Override
	public ATermList getArguments()
	{
		ATermList result = getPureFactory().getEmpty();

		for (int i = _args.length - 1; i >= 0; i--)
		{
			result = result.insert(_args[i]);
		}

		return result;
	}

	@Override
	public ATerm getArgument(final int index)
	{
		return _args[index];
	}

	@Override
	public ATermAppl setArgument(final ATerm newarg, final int index)
	{
		final ATerm[] newargs = _args.clone();
		newargs[index] = newarg;

		return make(_fun, newargs, getAnnotations());
	}

	@Override
	public boolean isQuoted()
	{
		return _fun.isQuoted();
	}

	@Override
	public String getName()
	{
		return _fun.getName();
	}

	@Override
	public int getArity()
	{
		return _args.length;
	}

	@Override
	public ATerm make(final List<Object> arguments)
	{
		final ATerm[] newargs = new ATerm[_args.length];
		for (int i = 0; i < _args.length; i++)
		{
			newargs[i] = _args[i].make(arguments);
		}

		final PureFactory pf = getPureFactory();
		final ATermList empty = pf.getEmpty();

		ATermList annos = getAnnotations();
		ATermList tempAnnos = empty;
		while (annos != empty)
		{
			tempAnnos = pf.makeList(annos.getFirst().make(arguments), tempAnnos);
			annos = annos.getNext();
		}
		final ATermList newAnnos = tempAnnos.reverse();

		return getPureFactory().makeAppl(_fun, newargs, newAnnos);
	}

	@Override
	public ATerm setAnnotations(final ATermList annos)
	{
		return getPureFactory().makeAppl(_fun, _args, annos);
	}

	@Override
	public ATerm accept(final Visitor<ATerm> v)
	{
		return v.visitAppl(this);
	}

	@Override
	public int getNrSubTerms()
	{
		return _args.length;
	}

	@Override
	public ATerm getSubTerm(final int index)
	{
		return _args[index];
	}

	@Override
	public ATerm setSubTerm(final int index, final ATerm t)
	{
		return setArgument(t, index);
	}

	private Object[] serialize()
	{
		final int arity = getArity();
		final Object[] o = new Object[arity + 2];
		for (int i = 0; i < arity; i++)
		{
			o[i] = getArgument(i);
		}
		o[arity] = getAnnotations();
		o[arity + 1] = getAFun();
		return o;
	}

	@SuppressWarnings({ "incomplete-switch", "fallthrough" })
	protected int hashFunction()
	{
		final int initval = 0; /* the previous hash value */
		int a, b, c, len;

		/* Set up the internal state */
		len = getArity();
		a = b = 0x9e3779b9; /* the golden ratio; an arbitrary value */
		c = initval; /* the previous hash value */
		/*---------------------------------------- handle most of the key */
		if (len >= 12) { return staticDoobs_hashFuntion(serialize());
		// return PureFactory.doobs_hashFunction(serialize());
		}

		/*------------------------------------- handle the last 11 bytes */
		c += len;
		c += (getAnnotations().hashCode() << 8);
		b += (getAFun().hashCode() << 8);

		switch (len)
		{
			case 11:
				c += (getArgument(10).hashCode() << 24);
			case 10:
				c += (getArgument(9).hashCode() << 16);
			case 9:
				c += (getArgument(8).hashCode() << 8);
			case 8:
				b += (getArgument(7).hashCode() << 24);
			case 7:
				b += (getArgument(6).hashCode() << 16);
			case 6:
				b += (getArgument(5).hashCode() << 8);
			case 5:
				b += (getArgument(4).hashCode());
			case 4:
				a += (getArgument(3).hashCode() << 24);
			case 3:
				a += (getArgument(2).hashCode() << 16);
			case 2:
				a += (getArgument(1).hashCode() << 8);
			case 1:
				a += (getArgument(0).hashCode());
				/* case 0: nothing left to add */
		}
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

	@SuppressWarnings({ "incomplete-switch", "fallthrough" })
	static private int staticDoobs_hashFuntion(final Object[] o)
	{
		// System.out.println("static doobs_hashFuntion");

		final int initval = 0; /* the previous hash value */
		int a, b, c, len;

		/* Set up the internal state */
		len = o.length;
		a = b = 0x9e3779b9; /* the golden ratio; an arbitrary value */
		c = initval; /* the previous hash value */

		/*---------------------------------------- handle most of the key */
		int k = 0;
		while (len >= 12)
		{
			a += (o[k + 0].hashCode() + (o[k + 1].hashCode() << 8) + (o[k + 2].hashCode() << 16) + (o[k + 3].hashCode() << 24));
			b += (o[k + 4].hashCode() + (o[k + 5].hashCode() << 8) + (o[k + 6].hashCode() << 16) + (o[k + 7].hashCode() << 24));
			c += (o[k + 8].hashCode() + (o[k + 9].hashCode() << 8) + (o[k + 10].hashCode() << 16) + (o[k + 11].hashCode() << 24));
			// mix(a,b,c);
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

			k += 12;
			len -= 12;
		}

		/*------------------------------------- handle the last 11 bytes */
		c += o.length;
		switch (len)
		/* all the case statements fall through */
		{
			case 11:
				c += (o[k + 10].hashCode() << 24);
			case 10:
				c += (o[k + 9].hashCode() << 16);
			case 9:
				c += (o[k + 8].hashCode() << 8);
				/* the first byte of c is reserved for the length */
			case 8:
				b += (o[k + 7].hashCode() << 24);
			case 7:
				b += (o[k + 6].hashCode() << 16);
			case 6:
				b += (o[k + 5].hashCode() << 8);
			case 5:
				b += o[k + 4].hashCode();
			case 4:
				a += (o[k + 3].hashCode() << 24);
			case 3:
				a += (o[k + 2].hashCode() << 16);
			case 2:
				a += (o[k + 1].hashCode() << 8);
			case 1:
				a += o[k + 0].hashCode();
				/* case 0: nothing left to add */
		}
		// mix(a,b,c);
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
