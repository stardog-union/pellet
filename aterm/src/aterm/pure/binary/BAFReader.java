/*
 * Java version of the ATerm library
 * Copyright (C) 2006-2008, UiB, CWI, LORIA-INRIA
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Library General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Library General Public License for more details.
 * 
 * You should have received a copy of the GNU Library General Public
 * License along with this library; if not, write to the
 * Free Software Foundation, Inc., 59 Temple Place - Suite 330,
 * Boston, MA  02111-1307, USA.
 */
package aterm.pure.binary;

import java.io.BufferedInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;

import aterm.AFun;
import aterm.ATerm;
import aterm.ATermList;
import aterm.ParseError;
import aterm.pure.PureFactory;

/**
 * Reader for the binary aterm format (BAF).
 * 
 * @author Karl Trygve Kalleberg
 *
 */
public class BAFReader
{
	private static final int BAF_MAGIC = 0xBAF;
	private static final int BAF_VERSION = 0x300;
	private static final int HEADER_BITS = 32;
	private BitStream reader;
	private int nrUniqueSymbols = -1;
	private SymEntry[] symbols;
	private PureFactory factory;
	public static boolean isDebugging = false;

	private static class SymEntry
	{
		public AFun fun;
		public int arity;
		public int nrTerms;
		public int termWidth;
		public ATerm[] terms;
		public int[] nrTopSyms;
		public int[] symWidth;
		public int[][] topSyms;
	}

	public BAFReader(PureFactory factory, InputStream inputStream)
	{
		this.factory = factory;
		reader = new BitStream(inputStream);
	}

	public ATerm readFromBinaryFile(boolean headerAlreadyRead) throws ParseError, IOException
	{

		if (!headerAlreadyRead && !isBinaryATerm(reader))
			throw new ParseError("Input is not a BAF file");

		int val = reader.readInt();

		if (val != BAF_VERSION)
			throw new ParseError("Wrong BAF version (wanted " + BAF_VERSION + ", got " + val + "), giving up");

		nrUniqueSymbols = reader.readInt();
		int nrUniqueTerms = reader.readInt();

		if (isDebugging())
		{
			debug("" + nrUniqueSymbols + " unique symbols");
			debug("" + nrUniqueTerms + " unique terms");
		}

		symbols = new SymEntry[nrUniqueSymbols];

		readAllSymbols();

		int i = reader.readInt();

		return readTerm(symbols[i]);
	}

	private boolean isDebugging()
	{
		return isDebugging;
	}

	public static boolean isBinaryATerm(BufferedInputStream in) throws IOException
	{
		if (isBinaryATerm(new BitStream(in)))
			return true;
		return false;
	}

	private static boolean isBinaryATerm(BitStream in) throws IOException
	{
		try
		{
			int w1 = in.readInt();

			return (w1 == BAF_MAGIC);
		}
		catch (EOFException e)
		{
		}

		return false;
	}

	private void debug(String s)
	{
		System.err.println(s);
	}

	int level = 0;

	private ATerm readTerm(SymEntry e) throws ParseError, IOException
	{
		final int arity = e.arity;
		final ATerm[] args = new ATerm[arity];

		level++;

		if (isDebugging())
			debug("readTerm()/" + level + " - " + e.fun.getName() + "[" + arity + "]");

		for (int i = 0; i < arity; i++)
		{
			int val = reader.readBits(e.symWidth[i]);
			if (isDebugging())
			{
				debug(" [" + i + "] - " + val);
				debug(" [" + i + "] - " + e.topSyms[i].length);
			}
			SymEntry argSym = symbols[e.topSyms[i][val]];

			val = reader.readBits(argSym.termWidth);
			if (argSym.terms[val] == null)
			{
				if (isDebugging())
					debug(" [" + i + "] - recurse");
				argSym.terms[val] = readTerm(argSym);
			}

			if (argSym.terms[val] == null)
				throw new ParseError("Cannot be null");

			args[i] = argSym.terms[val];
		}

		final String name = e.fun.getName();

		if (name.equals("<int>"))
		{
			int val = reader.readBits(HEADER_BITS);
			level--;
			return factory.makeInt(val);
		}
		else
			if (name.equals("<real>"))
			{
				reader.flushBitsFromReader();
				String s = reader.readString();
				level--;
				return factory.makeReal(new Double(s).doubleValue());
			}
			else
				if (name.equals("[_,_]"))
				{
					if (isDebugging())
					{
						debug("--");
						for (int i = 0; i < args.length; i++)
							debug(" + " + args[i].getClass());
					}
					level--;
					return ((ATermList) args[1]).insert(args[0]);
				}
				else
					if (name.equals("[]"))
					{
						level--;
						return factory.makeList();
					}
					else
						if (name.equals("{_}"))
						{
							return args[0].setAnnotations((ATermList) args[1]);
						}
						else
							if (name.equals("<_>"))
							{
								return factory.makePlaceholder(args[0]);
							}
							else
								if (false)
								{
									// FIXME: Add blob case
									reader.flushBitsFromReader();
									String t = reader.readString();
									return factory.makeBlob(t.getBytes());
								}

		if (isDebugging())
		{
			debug(e.fun + " / " + args);
			for (int i = 0; i < args.length; i++)
				debug("" + args[i]);
		}
		level--;
		return factory.makeAppl(e.fun, args);
	}

	private void readAllSymbols() throws IOException
	{

		for (int i = 0; i < nrUniqueSymbols; i++)
		{
			SymEntry e = new SymEntry();
			symbols[i] = e;

			AFun fun = readSymbol();
			e.fun = fun;
			int arity = e.arity = fun.getArity();

			int v = reader.readInt();
			e.nrTerms = v;
			e.termWidth = bitWidth(v);
			// FIXME: original code is inconsistent at this point!
			e.terms = (v == 0) ? null : new ATerm[v];

			if (arity == 0)
			{
				e.nrTopSyms = null;
				e.symWidth = null;
				e.topSyms = null;
			}
			else
			{

				e.nrTopSyms = new int[arity];
				e.symWidth = new int[arity];
				e.topSyms = new int[arity][];
			}
			for (int j = 0; j < arity; j++)
			{
				v = reader.readInt();
				e.nrTopSyms[j] = v;
				e.symWidth[j] = bitWidth(v);
				e.topSyms[j] = new int[v];

				for (int k = 0; k < e.nrTopSyms[j]; k++)
				{
					v = reader.readInt();
					e.topSyms[j][k] = v;
				}
			}
		}
	}

	private int bitWidth(int v)
	{
		int nrBits = 0;

		if (v <= 1)
			return 0;

		while (v != 0)
		{
			v >>= 1;
			nrBits++;
		}

		return nrBits;
	}

	private AFun readSymbol() throws IOException
	{
		String s = reader.readString();
		int arity = reader.readInt();
		int quoted = reader.readInt();

		if (isDebugging())
			debug(s + " / " + arity + " / " + quoted);

		return factory.makeAFun(s, arity, quoted != 0);
	}

	public static class BitStream
	{

		InputStream stream;
		private int bitsInBuffer;
		private int bitBuffer;

		public BitStream(InputStream inputStream)
		{
			stream = inputStream;
		}

		public int readInt() throws IOException
		{
			int[] buf = new int[5];

			buf[0] = readByte();

			// Check if 1st character is enough
			if ((buf[0] & 0x80) == 0)
				return buf[0];

			buf[1] = readByte();

			// Check if 2nd character is enough
			if ((buf[0] & 0x40) == 0)
				return buf[1] + ((buf[0] & ~0xc0) << 8);

			buf[2] = readByte();

			// Check if 3rd character is enough
			if ((buf[0] & 0x20) == 0)
				return buf[2] + (buf[1] << 8) + ((buf[0] & ~0xe0) << 16);

			buf[3] = readByte();

			// Check if 4th character is enough
			if ((buf[0] & 0x10) == 0)
				return buf[3] + (buf[2] << 8) + (buf[1] << 16) + ((buf[0] & ~0xf0) << 24);

			buf[4] = readByte();

			return buf[4] + (buf[3] << 8) + (buf[2] << 16) + (buf[1] << 24);
		}

		private int readByte() throws IOException
		{
			int c = stream.read();
			if (c == -1)
				throw new EOFException();
			return c;
		}

		public String readString() throws IOException
		{
			int l = readInt();
			byte[] b = new byte[l];
			int v = 0;
			while (v < b.length)
			{
				v += stream.read(b, v, b.length - v);
			}
			return new String(b);
		}

		public int readBits(int nrBits) throws IOException
		{
			int mask = 1;
			int val = 0;

			for (int i = 0; i < nrBits; i++)
			{
				if (bitsInBuffer == 0)
				{
					int v = readByte();
					if (v == -1)
						return -1;
					bitBuffer = v;
					bitsInBuffer = 8;
				}
				val |= (((bitBuffer & 0x80) != 0) ? mask : 0);
				mask <<= 1;
				bitBuffer <<= 1;
				bitsInBuffer--;
			}

			return val;
		}

		public void flushBitsFromReader()
		{
			bitsInBuffer = 0;
		}
	}
}
