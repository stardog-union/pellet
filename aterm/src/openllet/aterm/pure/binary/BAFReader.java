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
package openllet.aterm.pure.binary;

import java.io.BufferedInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import openllet.aterm.AFun;
import openllet.aterm.ATerm;
import openllet.aterm.ATermList;
import openllet.aterm.ParseError;
import openllet.aterm.pure.PureFactory;
import openllet.shared.tools.Log;

/**
 * Reader for the binary openllet.aterm format (BAF).
 * 
 * @author Karl Trygve Kalleberg
 *
 */
public class BAFReader
{
	public static final Logger _logger = Log.getLogger(BAFReader.class);

	private static final int BAF_MAGIC = 0xBAF;
	private static final int BAF_VERSION = 0x300;
	private static final int HEADER_BITS = 32;
	private final BitStream reader;
	private int nrUniqueSymbols = -1;
	private SymEntry[] symbols;
	private final PureFactory factory;
	public static boolean isDebugging = false;

	public BAFReader(final PureFactory factory, final InputStream inputStream)
	{
		this.factory = factory;
		reader = new BitStream(inputStream);
	}

	public ATerm readFromBinaryFile(final boolean headerAlreadyRead) throws ParseError, IOException
	{

		if (!headerAlreadyRead && !isBinaryATerm(reader))
			throw new ParseError("Input is not a BAF file");

		final int val = reader.readInt();

		if (val != BAF_VERSION)
			throw new ParseError("Wrong BAF version (wanted " + BAF_VERSION + ", got " + val + "), giving up");

		nrUniqueSymbols = reader.readInt();
		final int nrUniqueTerms = reader.readInt();

		if (isDebugging())
		{
			debug("" + nrUniqueSymbols + " unique symbols");
			debug("" + nrUniqueTerms + " unique terms");
		}

		symbols = new SymEntry[nrUniqueSymbols];

		readAllSymbols();

		final int i = reader.readInt();

		return readTerm(symbols[i]);
	}

	private static boolean isDebugging()
	{
		return isDebugging;
	}

	public static boolean isBinaryATerm(final BufferedInputStream in) throws IOException
	{
		if (isBinaryATerm(new BitStream(in)))
			return true;
		return false;
	}

	private static boolean isBinaryATerm(final BitStream in) throws IOException
	{
		try
		{
			final int w1 = in.readInt();

			return (w1 == BAF_MAGIC);
		}
		catch (final EOFException e)
		{
			_logger.log(Level.FINE, "", e);
		}

		return false;
	}

	private static void debug(final String s)
	{
		_logger.info(s);
	}

	int level = 0;

	private ATerm readTerm(final SymEntry e) throws ParseError, IOException
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
			final SymEntry argSym = symbols[e.topSyms[i][val]];

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
		switch (name)
		{
			case "<int>":
			{
				final int val = reader.readBits(HEADER_BITS);
				level--;
				return factory.makeInt(val);
			}
			case "<real>":
			{
				reader.flushBitsFromReader();
				final String s = reader.readString();
				level--;
				return factory.makeReal(new Double(s).doubleValue());
			}
			case "[_,_]":
			{
				if (isDebugging())
				{
					debug("--");
					for (final ATerm arg : args)
						debug(" + " + arg.getClass());
				}
				level--;
				return ((ATermList) args[1]).insert(args[0]);
			}
			case "[]":
			{
				level--;
				return factory.makeList();
			}
			case "{_}":
			{
				return args[0].setAnnotations((ATermList) args[1]);
			}
			case "<_>":
			{
				return factory.makePlaceholder(args[0]);
			}
			default:
			{
				//				// FIXME: Add blob case
				//				reader.flushBitsFromReader();
				//				final String t = reader.readString();
				//				return factory.makeBlob(t.getBytes());				
			}
		}

		if (isDebugging())
		{
			debug(e.fun + " / " + args);
			for (final ATerm arg : args)
				debug("" + arg);
		}
		level--;
		return factory.makeAppl(e.fun, args);
	}

	private void readAllSymbols() throws IOException
	{

		for (int i = 0; i < nrUniqueSymbols; i++)
		{
			final SymEntry e = new SymEntry();
			symbols[i] = e;

			final AFun fun = readSymbol();
			e.fun = fun;
			final int arity = e.arity = fun.getArity();

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

	private static int bitWidth(int v)
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
		final String s = reader.readString();
		final int arity = reader.readInt();
		final int quoted = reader.readInt();

		if (isDebugging())
			debug(s + " / " + arity + " / " + quoted);

		return factory.makeAFun(s, arity, quoted != 0);
	}

	public static class BitStream
	{

		InputStream stream;
		private int bitsInBuffer;
		private int bitBuffer;

		public BitStream(final InputStream inputStream)
		{
			stream = inputStream;
		}

		public int readInt() throws IOException
		{
			final int[] buf = new int[5];

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
			final int c = stream.read();
			if (c == -1)
				throw new EOFException();
			return c;
		}

		public String readString() throws IOException
		{
			final int l = readInt();
			final byte[] b = new byte[l];
			int v = 0;
			while (v < b.length)
			{
				v += stream.read(b, v, b.length - v);
			}
			return new String(b);
		}

		public int readBits(final int nrBits) throws IOException
		{
			int mask = 1;
			int val = 0;

			for (int i = 0; i < nrBits; i++)
			{
				if (bitsInBuffer == 0)
				{
					final int v = readByte();
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
