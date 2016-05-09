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
import aterm.ATermList;
import aterm.Visitor;
import aterm.stream.BufferedOutputStreamWriter;
import java.io.IOException;
import jjtraveler.VisitFailure;
import shared.SharedObject;

public class AFunImpl extends ATermImpl implements AFun
{
	private String name;

	private int arity;

	private boolean isQuoted;

	/**
	 * depricated Use the new constructor instead.
	 * 
	 * @param factory x
	 */
	protected AFunImpl(PureFactory factory)
	{
		super(factory);
	}

	protected AFunImpl(PureFactory factory, String name, int arity, boolean isQuoted)
	{
		super(factory, null);

		this.name = name.intern();
		this.arity = arity;
		this.isQuoted = isQuoted;

		setHashCode(hashFunction());
	}

	/**
	 * depricated Use the new constructor instead.
	 * 
	 * @param hashCode x
	 * @param name x
	 * @param arity x
	 * @param isQuoted x
	 */
	protected void init(int hashCode, String name, int arity, boolean isQuoted)
	{
		super.init(hashCode, null);

		this.name = name.intern();
		this.arity = arity;
		this.isQuoted = isQuoted;
	}

	/**
	 * depricated Use the new constructor instead.
	 * 
	 * @param name x
	 * @param arity x
	 * @param isQuoted x
	 */
	protected void initHashCode(String name, int arity, boolean isQuoted)
	{
		this.name = name.intern();
		this.arity = arity;
		this.isQuoted = isQuoted;
		this.setHashCode(this.hashFunction());
	}

	@Override
	public SharedObject duplicate()
	{
		return this;
	}

	@Override
	public boolean equivalent(SharedObject obj)
	{
		if (obj instanceof AFun)
		{
			final AFun peer = (AFun) obj;
			return peer.getName().equals(name) && peer.getArity() == arity && peer.isQuoted() == isQuoted;
		}
		return false;
	}

	@Override
	public int getType()
	{
		return ATerm.AFUN;
	}

	@Override
	public String getName()
	{
		return name;
	}

	@Override
	public int getArity()
	{
		return arity;
	}

	@Override
	public boolean isQuoted()
	{
		return isQuoted;
	}

	@Override
	public ATerm getAnnotation(ATerm key)
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public ATermList getAnnotations()
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public ATerm setAnnotations(ATermList annos)
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public int serialize(BufferedOutputStreamWriter writer) throws IOException
	{
		int bytesWritten = 0;
		if (isQuoted)
		{
			writer.write('"');
			bytesWritten++;
		}

		final int numberOfCharacters = name.length();
		bytesWritten += numberOfCharacters;
		for (int i = 0; i < numberOfCharacters; i++)
		{
			char c = name.charAt(i);
			switch (c)
			{
				case '\n':
					writer.write('\\');
					writer.write('n');
					bytesWritten++;
					break;
				case '\t':
					writer.write('\\');
					writer.write('t');
					bytesWritten++;
					break;
				case '\b':
					writer.write('\\');
					writer.write('b');
					bytesWritten++;
					break;
				case '\r':
					writer.write('\\');
					writer.write('r');
					bytesWritten++;
					break;
				case '\f':
					writer.write('\\');
					writer.write('f');
					bytesWritten++;
					break;
				case '\\':
					writer.write('\\');
					writer.write('\\');
					bytesWritten++;
					break;
				case '\'':
					writer.write('\\');
					writer.write('\'');
					bytesWritten++;
					break;
				case '\"':
					writer.write('\\');
					writer.write('\"');
					bytesWritten++;
					break;

				case '!':
				case '@':
				case '#':
				case '$':
				case '%':
				case '^':
				case '&':
				case '*':
				case '(':
				case ')':
				case '-':
				case '_':
				case '+':
				case '=':
				case '|':
				case '~':
				case '{':
				case '}':
				case '[':
				case ']':
				case ';':
				case ':':
				case '<':
				case '>':
				case ',':
				case '.':
				case '?':
				case ' ':
				case '/':
					writer.write(c);
					break;

				default:
					if (Character.isLetterOrDigit(c))
					{
						writer.write(c);
					}
					else
					{
						writer.write('\\');
						writer.write(('0' + c / 64));
						c = (char) (c % 64);
						writer.write(('0' + c / 8));
						c = (char) (c % 8);
						writer.write(('0' + c));

						bytesWritten += 3;
					}
			}
		}

		if (isQuoted)
		{
			writer.write('"');
			bytesWritten++;
		}

		return bytesWritten;
	}

	@Override
	public String toString()
	{
		final StringBuilder result = new StringBuilder(name.length());

		if (isQuoted)
		{
			result.append('"');
		}

		for (int i = 0; i < name.length(); i++)
		{
			char c = name.charAt(i);
			switch (c)
			{
				case '\n':
					result.append('\\');
					result.append('n');
					break;
				case '\t':
					result.append('\\');
					result.append('t');
					break;
				case '\b':
					result.append('\\');
					result.append('b');
					break;
				case '\r':
					result.append('\\');
					result.append('r');
					break;
				case '\f':
					result.append('\\');
					result.append('f');
					break;
				case '\\':
					result.append('\\');
					result.append('\\');
					break;
				case '\'':
					result.append('\\');
					result.append('\'');
					break;
				case '\"':
					result.append('\\');
					result.append('\"');
					break;

				case '!':
				case '@':
				case '#':
				case '$':
				case '%':
				case '^':
				case '&':
				case '*':
				case '(':
				case ')':
				case '-':
				case '_':
				case '+':
				case '=':
				case '|':
				case '~':
				case '{':
				case '}':
				case '[':
				case ']':
				case ';':
				case ':':
				case '<':
				case '>':
				case ',':
				case '.':
				case '?':
				case ' ':
				case '/':
					result.append(c);
					break;

				default:
					if (Character.isLetterOrDigit(c))
					{
						result.append(c);
					}
					else
					{
						result.append('\\');
						result.append((char) ('0' + c / 64));
						c = (char) (c % 64);
						result.append((char) ('0' + c / 8));
						c = (char) (c % 8);
						result.append((char) ('0' + c));
					}
			}
		}

		if (isQuoted)
		{
			result.append('"');
		}

		return result.toString();
	}

	@Override
	public aterm.Visitable accept(Visitor v) throws VisitFailure
	{
		return v.visitAFun(this);
	}

	@SuppressWarnings({ "fallthrough", "incomplete-switch" })
	private int hashFunction()
	{
		int a, b, c;
		/* Set up the internal state */
		a = b = 0x9e3779b9; /* the golden ratio; an arbitrary value */
		/*------------------------------------- handle the last 11 bytes */
		final int len = name.length();
		if (len >= 12) { return hashFunction2(); }
		c = (isQuoted) ? 7 * arity + 1 : arity + 1;
		c += len;
		switch (len)
		{
			case 11:
				c += (name.charAt(10) << 24);
			case 10:
				c += (name.charAt(9) << 16);
			case 9:
				c += (name.charAt(8) << 8);
				/* the first byte of c is reserved for the length */
			case 8:
				b += (name.charAt(7) << 24);
			case 7:
				b += (name.charAt(6) << 16);
			case 6:
				b += (name.charAt(5) << 8);
			case 5:
				b += name.charAt(4);
			case 4:
				a += (name.charAt(3) << 24);
			case 3:
				a += (name.charAt(2) << 16);
			case 2:
				a += (name.charAt(1) << 8);
			case 1:
				a += name.charAt(0);
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

		return c;
	}

	@SuppressWarnings({ "fallthrough", "incomplete-switch" })
	private int hashFunction2()
	{
		int offset = 0;
		final int count = name.length();
		final char[] source = new char[count];

		offset = 0;
		name.getChars(0, count, source, 0);
		int a, b, c;
		/* Set up the internal state */
		int len = count;
		a = b = 0x9e3779b9; /* the golden ratio; an arbitrary value */
		c = (isQuoted) ? 7 * (arity + 1) : arity + 1; // to avoid collison
		/*------------------------------------- handle the last 11 bytes */
		int k = offset;

		while (len >= 12)
		{
			a += (source[k + 0] + (source[k + 1] << 8) + (source[k + 2] << 16) + (source[k + 3] << 24));
			b += (source[k + 4] + (source[k + 5] << 8) + (source[k + 6] << 16) + (source[k + 7] << 24));
			c += (source[k + 8] + (source[k + 9] << 8) + (source[k + 10] << 16) + (source[k + 11] << 24));
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
		/*---------------------------------------- handle most of the key */
		c += count;
		switch (len)
		{
			case 11:
				c += (source[k + 10] << 24);
			case 10:
				c += (source[k + 9] << 16);
			case 9:
				c += (source[k + 8] << 8);
				/* the first byte of c is reserved for the length */
			case 8:
				b += (source[k + 7] << 24);
			case 7:
				b += (source[k + 6] << 16);
			case 6:
				b += (source[k + 5] << 8);
			case 5:
				b += source[k + 4];
			case 4:
				a += (source[k + 3] << 24);
			case 3:
				a += (source[k + 2] << 16);
			case 2:
				a += (source[k + 1] << 8);
			case 1:
				a += source[k + 0];
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

		//System.out.println("static doobs_hashFunctionAFun = " + c + ": " + name);
		return c;
	}

}
