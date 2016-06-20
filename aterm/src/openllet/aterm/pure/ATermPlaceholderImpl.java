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
import openllet.shared.hash.HashFunctions;
import openllet.shared.hash.SharedObject;

public class ATermPlaceholderImpl extends ATermImpl implements ATermPlaceholder
{
	private ATerm _type;

	/**
	 * depricated Use the new constructor instead.
	 * 
	 * @param factory x
	 */
	@Deprecated
	protected ATermPlaceholderImpl(final PureFactory factory)
	{
		super(factory);
	}

	protected ATermPlaceholderImpl(final PureFactory factory, final ATermList annos, final ATerm type)
	{
		super(factory, annos);

		_type = type;

		setHashCode(HashFunctions.doobs(new Object[] { annos, type }));
	}

	@Override
	public int getType()
	{
		return ATerm.PLACEHOLDER;
	}

	/**
	 * depricated Use the new constructor instead.
	 * 
	 * @param hashCode x
	 * @param annos x
	 * @param _type x
	 */
	@Deprecated
	protected void init(final int hashCode, final ATermList annos, final ATerm type)
	{
		super.init(hashCode, annos);
		_type = type;
	}

	@Override
	public SharedObject duplicate()
	{
		return this;
	}

	@Override
	public boolean equivalent(final SharedObject obj)
	{
		if (obj instanceof ATermPlaceholder)
		{
			final ATermPlaceholder peer = (ATermPlaceholder) obj;
			if (peer.getType() != getType())
				return false;

			return (peer.getPlaceholder() == _type && peer.getAnnotations().equals(getAnnotations()));
		}

		return false;
	}

	@Override
	public boolean match(final ATerm pattern, final List<Object> list)
	{
		if (pattern.getType() == ATerm.PLACEHOLDER)
		{
			final ATerm t = ((ATermPlaceholder) pattern).getPlaceholder();
			if (t.getType() == ATerm.APPL)
			{
				final ATermAppl appl = (ATermAppl) t;
				final AFun afun = appl.getAFun();
				if (afun.getName().equals("placeholder") && afun.getArity() == 0 && !afun.isQuoted())
				{
					list.add(t);
					return true;
				}
			}
		}

		return super.match(pattern, list);
	}

	@Override
	public ATerm make(final List<Object> args)
	{
		final ATermAppl appl = (ATermAppl) _type;
		final AFun fun = appl.getAFun();
		final String name = fun.getName();

		if (!fun.isQuoted())
		{
			if (fun.getArity() == 0)
			{
				switch (name)
				{
					case "term":
					{
						final ATerm t = (ATerm) args.get(0);
						args.remove(0);
						return t;
					}
					case "list":
					{
						final ATermList l = (ATermList) args.get(0);
						args.remove(0);
						return l;
					}
					case "bool":
					{
						final Boolean b = (Boolean) args.get(0);
						args.remove(0);
						return _factory.makeAppl(_factory.makeAFun(b.toString(), 0, false));
					}
					case "int":
					{
						final Integer i = (Integer) args.get(0);
						args.remove(0);
						return _factory.makeInt(i.intValue());
					}
					case "real":
					{
						final Double d = (Double) args.get(0);
						args.remove(0);
						return _factory.makeReal(d.doubleValue());
					}
					case "blob":
					{
						final byte[] data = (byte[]) args.get(0);
						args.remove(0);
						return _factory.makeBlob(data);
					}
					case "placeholder":
					{
						final ATerm t = (ATerm) args.get(0);
						args.remove(0);
						return _factory.makePlaceholder(t);
					}
					case "str":
					{
						final String str = (String) args.get(0);
						args.remove(0);
						return _factory.makeAppl(_factory.makeAFun(str, 0, true));
					}
					case "id":
					{
						final String str = (String) args.get(0);
						args.remove(0);
						return _factory.makeAppl(_factory.makeAFun(str, 0, false));
					}
					case "fun":
					{
						final String str = (String) args.get(0);
						args.remove(0);
						return _factory.makeAppl(_factory.makeAFun(str, 0, false));
					}
					default:
					{
						throw new RuntimeException("Unknow ATerm function name : " + name);
					}
				}
			}
			if (name.equals("appl"))
			{
				final ATermList oldargs = appl.getArguments();
				final String newname = (String) args.get(0);
				args.remove(0);
				final ATermList newargs = (ATermList) oldargs.make(args);
				final AFun newfun = _factory.makeAFun(newname, newargs.getLength(), false);
				return _factory.makeApplList(newfun, newargs);
			}
		}
		throw new RuntimeException("illegal pattern: " + this);
	}

	@Override
	public ATerm getPlaceholder()
	{
		return _type;
	}

	public ATerm setPlaceholder(final ATerm newtype)
	{
		return getPureFactory().makePlaceholder(newtype, getAnnotations());
	}

	@Override
	public ATerm setAnnotations(final ATermList annos)
	{
		return getPureFactory().makePlaceholder(_type, annos);
	}

	@Override
	public ATerm accept(final Visitor<ATerm> v)
	{
		return v.visitPlaceholder(this);
	}

	@Override
	public int getNrSubTerms()
	{
		return 1;
	}

	@Override
	public ATerm getSubTerm(final int index)
	{
		return _type;
	}

	@Override
	public ATerm setSubTerm(final int index, final ATerm t)
	{
		if (index == 1) { return setPlaceholder(t); }
		throw new RuntimeException("no " + index + "-th child!");
	}

}
