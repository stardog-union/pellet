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
 *     * Neither the _name of the University of California, Berkeley nor the
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

import java.io.IOException;
import openllet.aterm.AFun;
import openllet.aterm.ATerm;
import openllet.aterm.ATermList;
import openllet.aterm.Visitor;
import openllet.aterm.stream.BufferedOutputStreamWriter;
import openllet.shared.hash.SharedObject;

public class AFunImpl extends ATermImpl implements AFun
{
	private String _name;

	private int _arity;

	private boolean _isQuoted;

	/**
	 * depricated Use the new constructor instead.
	 * 
	 * @param _factory x
	 */
	protected AFunImpl(final PureFactory factory)
	{
		super(factory);
	}

	protected AFunImpl(final PureFactory factory, final String name, final int arity, final boolean isQuoted)
	{
		super(factory, null);

		this._name = name.intern();
		this._arity = arity;
		this._isQuoted = isQuoted;

		setHashCode(hashFunction());
	}

	/**
	 * depricated Use the new constructor instead.
	 * 
	 * @param hashCode x
	 * @param _name x
	 * @param _arity x
	 * @param _isQuoted x
	 */
	protected void init(final int hashCode, final String name, final int arity, final boolean isQuoted)
	{
		super.init(hashCode, null);

		this._name = name.intern();
		this._arity = arity;
		this._isQuoted = isQuoted;
	}

	/**
	 * depricated Use the new constructor instead.
	 * 
	 * @param _name x
	 * @param _arity x
	 * @param _isQuoted x
	 */
	protected void initHashCode(final String name, final int arity, final boolean isQuoted)
	{
		this._name = name.intern();
		this._arity = arity;
		this._isQuoted = isQuoted;
		setHashCode(hashFunction());
	}

	@Override
	public SharedObject duplicate()
	{
		return this;
	}

	@Override
	public boolean equivalent(final SharedObject obj)
	{
		if (obj instanceof AFun)
		{
			final AFun peer = (AFun) obj;
			return peer.getName().equals(_name) && peer.getArity() == _arity && peer.isQuoted() == _isQuoted;
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
		return _name;
	}

	@Override
	public int getArity()
	{
		return _arity;
	}

	@Override
	public boolean isQuoted()
	{
		return _isQuoted;
	}

	@Override
	public ATerm getAnnotation(final ATerm key)
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public ATermList getAnnotations()
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public ATerm setAnnotations(final ATermList annos)
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public int serialize(final BufferedOutputStreamWriter writer) throws IOException
	{
		int bytesWritten = 0;
		if (_isQuoted)
		{
			writer.write('"');
			bytesWritten++;
		}

		final int numberOfCharacters = _name.length();
		bytesWritten += numberOfCharacters;
		for (int i = 0; i < numberOfCharacters; i++)
		{
			char c = _name.charAt(i);
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

		if (_isQuoted)
		{
			writer.write('"');
			bytesWritten++;
		}

		return bytesWritten;
	}

	@Override
	public String toString()
	{
		final StringBuilder result = new StringBuilder(_name.length());

		if (_isQuoted)
		{
			result.append('"');
		}

		for (int i = 0; i < _name.length(); i++)
		{
			char c = _name.charAt(i);
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

		if (_isQuoted)
		{
			result.append('"');
		}

		return result.toString();
	}

	@Override
	public ATerm accept(final Visitor<ATerm> v)
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
		final int len = _name.length();
		if (len >= 12) { return hashFunction2(); }
		c = (_isQuoted) ? 7 * _arity + 1 : _arity + 1;
		c += len;
		switch (len)
		{
			case 11:
				c += (_name.charAt(10) << 24);
			case 10:
				c += (_name.charAt(9) << 16);
			case 9:
				c += (_name.charAt(8) << 8);
				/* the first byte of c is reserved for the length */
			case 8:
				b += (_name.charAt(7) << 24);
			case 7:
				b += (_name.charAt(6) << 16);
			case 6:
				b += (_name.charAt(5) << 8);
			case 5:
				b += _name.charAt(4);
			case 4:
				a += (_name.charAt(3) << 24);
			case 3:
				a += (_name.charAt(2) << 16);
			case 2:
				a += (_name.charAt(1) << 8);
			case 1:
				a += _name.charAt(0);
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
		final int count = _name.length();
		final char[] source = new char[count];

		offset = 0;
		_name.getChars(0, count, source, 0);
		int a, b, c;
		/* Set up the internal state */
		int len = count;
		a = b = 0x9e3779b9; /* the golden ratio; an arbitrary value */
		c = (_isQuoted) ? 7 * (_arity + 1) : _arity + 1; // to avoid collison
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

		//System.out.println("static doobs_hashFunctionAFun = " + c + ": " + _name);
		return c;
	}

}
