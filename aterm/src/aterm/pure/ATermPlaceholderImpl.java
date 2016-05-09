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
import aterm.Visitable;
import aterm.Visitor;
import java.util.List;
import jjtraveler.VisitFailure;
import shared.HashFunctions;
import shared.SharedObject;

public class ATermPlaceholderImpl extends ATermImpl implements ATermPlaceholder
{
	private ATerm type;

	/**
	 * depricated Use the new constructor instead.
	 * 
	 * @param factory x
	 */
	protected ATermPlaceholderImpl(PureFactory factory)
	{
		super(factory);
	}

	protected ATermPlaceholderImpl(PureFactory factory, ATermList annos, ATerm type)
	{
		super(factory, annos);

		this.type = type;

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
	 * @param type x
	 */
	protected void init(int hashCode, ATermList annos, ATerm type)
	{
		super.init(hashCode, annos);
		this.type = type;
	}

	@Override
	public SharedObject duplicate()
	{
		return this;
	}

	@Override
	public boolean equivalent(SharedObject obj)
	{
		if (obj instanceof ATermPlaceholder)
		{
			final ATermPlaceholder peer = (ATermPlaceholder) obj;
			if (peer.getType() != getType())
				return false;

			return (peer.getPlaceholder() == type && peer.getAnnotations().equals(getAnnotations()));
		}

		return false;
	}

	@Override
	public boolean match(ATerm pattern, List<Object> list)
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
	public ATerm make(List<Object> args)
	{
		ATermAppl appl;
		AFun fun;
		String name;

		appl = (ATermAppl) type;
		fun = appl.getAFun();
		name = fun.getName();
		if (!fun.isQuoted())
		{
			if (fun.getArity() == 0)
			{
				if (name.equals("term"))
				{
					final ATerm t = (ATerm) args.get(0);
					args.remove(0);

					return t;
				}
				else
					if (name.equals("list"))
					{
						final ATermList l = (ATermList) args.get(0);
						args.remove(0);

						return l;
					}
					else
						if (name.equals("bool"))
						{
							final Boolean b = (Boolean) args.get(0);
							args.remove(0);

							return factory.makeAppl(factory.makeAFun(b.toString(), 0, false));
						}
						else
							if (name.equals("int"))
							{
								final Integer i = (Integer) args.get(0);
								args.remove(0);

								return factory.makeInt(i.intValue());
							}
							else
								if (name.equals("real"))
								{
									final Double d = (Double) args.get(0);
									args.remove(0);

									return factory.makeReal(d.doubleValue());
								}
								else
									if (name.equals("blob"))
									{
										final byte[] data = (byte[]) args.get(0);
										args.remove(0);

										return factory.makeBlob(data);
									}
									else
										if (name.equals("placeholder"))
										{
											final ATerm t = (ATerm) args.get(0);
											args.remove(0);
											return factory.makePlaceholder(t);
										}
										else
											if (name.equals("str"))
											{
												final String str = (String) args.get(0);
												args.remove(0);
												return factory.makeAppl(factory.makeAFun(str, 0, true));
											}
											else
												if (name.equals("id"))
												{
													final String str = (String) args.get(0);
													args.remove(0);
													return factory.makeAppl(factory.makeAFun(str, 0, false));
												}
												else
													if (name.equals("fun"))
													{
														final String str = (String) args.get(0);
														args.remove(0);
														return factory.makeAppl(factory.makeAFun(str, 0, false));
													}
			}
			if (name.equals("appl"))
			{
				final ATermList oldargs = appl.getArguments();
				final String newname = (String) args.get(0);
				args.remove(0);
				final ATermList newargs = (ATermList) oldargs.make(args);
				final AFun newfun = factory.makeAFun(newname, newargs.getLength(), false);
				return factory.makeApplList(newfun, newargs);
			}
		}
		throw new RuntimeException("illegal pattern: " + this);
	}

	@Override
	public ATerm getPlaceholder()
	{
		return type;
	}

	public ATerm setPlaceholder(ATerm newtype)
	{
		return getPureFactory().makePlaceholder(newtype, getAnnotations());
	}

	@Override
	public ATerm setAnnotations(ATermList annos)
	{
		return getPureFactory().makePlaceholder(type, annos);
	}

	@Override
	public Visitable accept(Visitor v) throws VisitFailure
	{
		return v.visitPlaceholder(this);
	}

	@Override
	public int getNrSubTerms()
	{
		return 1;
	}

	@Override
	public ATerm getSubTerm(int index)
	{
		return type;
	}

	@Override
	public ATerm setSubTerm(int index, ATerm t)
	{
		if (index == 1) { return setPlaceholder(t); }
		throw new RuntimeException("no " + index + "-th child!");
	}

}
