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
import aterm.ATermBlob;
import aterm.ATermFactory;
import aterm.ATermInt;
import aterm.ATermList;
import aterm.ATermLong;
import aterm.ATermPlaceholder;
import aterm.ATermReal;
import aterm.ParseError;
import aterm.pure.binary.BAFReader;
import aterm.pure.binary.BinaryReader;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import shared.SharedObject;
import shared.SharedObjectFactory;

public class PureFactory extends SharedObjectFactory implements ATermFactory
{

	private static int DEFAULT_TERM_TABLE_SIZE = 16; // means 2^16 entries

	private final ATermList empty;

	static boolean isBase64(int c)
	{
		return Character.isLetterOrDigit(c) || c == '+' || c == '/';
	}

	static public int abbrevSize(int abbrev)
	{
		int size = 1;

		if (abbrev == 0) { return 2; }

		while (abbrev > 0)
		{
			size++;
			abbrev /= 64;
		}

		return size;
	}

	public PureFactory()
	{
		this(DEFAULT_TERM_TABLE_SIZE);
	}

	public PureFactory(int termTableSize)
	{
		super(termTableSize);

		final ATermListImpl protoList = new ATermListImpl(this);

		/*
		 * 240146486 is a fix-point hashcode such that
		 * empty.hashcode = empty.getAnnotations().hashCode
		 * this magic value can be found using: findEmptyHashCode()
		 */
		protoList.init(240146486, null, null, null);
		empty = (ATermList) build(protoList);
		//int magicHash = ((ATermListImpl) empty).findEmptyHashCode();
		((ATermListImpl) empty).init(240146486, empty, null, null);

	}

	@Override
	public ATermInt makeInt(int val)
	{
		return makeInt(val, empty);
	}

	@Override
	public ATermLong makeLong(long val)
	{
		return makeLong(val, empty);
	}

	@Override
	public ATermReal makeReal(double val)
	{
		return makeReal(val, empty);
	}

	@Override
	public ATermList makeList()
	{
		return empty;
	}

	@Override
	public ATermList makeList(ATerm singleton)
	{
		return makeList(singleton, empty, empty);
	}

	@Override
	public ATermList makeList(ATerm first, ATermList next)
	{
		return makeList(first, next, empty);
	}

	@Override
	public ATermPlaceholder makePlaceholder(ATerm type)
	{
		return makePlaceholder(type, empty);
	}

	@Override
	public ATermBlob makeBlob(byte[] data)
	{
		return makeBlob(data, empty);
	}

	@Override
	public AFun makeAFun(String name, int arity, boolean isQuoted)
	{
		return (AFun) build(new AFunImpl(this, name, arity, isQuoted));
	}

	public ATermInt makeInt(int value, ATermList annos)
	{
		return (ATermInt) build(new ATermIntImpl(this, annos, value));
	}

	public ATermLong makeLong(long value, ATermList annos)
	{
		return (ATermLong) build(new ATermLongImpl(this, annos, value));
	}

	public ATermReal makeReal(double value, ATermList annos)
	{
		return (ATermReal) build(new ATermRealImpl(this, annos, value));
	}

	public ATermPlaceholder makePlaceholder(ATerm type, ATermList annos)
	{
		return (ATermPlaceholder) build(new ATermPlaceholderImpl(this, annos, type));
	}

	public ATermBlob makeBlob(byte[] data, ATermList annos)
	{
		return (ATermBlob) build(new ATermBlobImpl(this, annos, data));
	}

	public ATermList makeList(ATerm first, ATermList next, ATermList annos)
	{
		return (ATermList) build(new ATermListImpl(this, annos, first, next));
	}

	private static ATerm[] array0 = new ATerm[0];

	@Override
	public ATermAppl makeAppl(AFun fun, ATerm[] args)
	{
		return makeAppl(fun, args, empty);
	}

	public ATermAppl makeAppl(AFun fun, ATerm[] args, ATermList annos)
	{
		return (ATermAppl) build(new ATermApplImpl(this, annos, fun, args));
	}

	@Override
	public ATermAppl makeApplList(AFun fun, ATermList list)
	{
		return makeApplList(fun, list, empty);
	}

	public ATermAppl makeApplList(AFun fun, ATermList list, ATermList annos)
	{
		ATerm[] arg_array;

		arg_array = new ATerm[list.getLength()];

		int i = 0;
		while (!list.isEmpty())
		{
			arg_array[i++] = list.getFirst();
			list = list.getNext();
		}
		return makeAppl(fun, arg_array, annos);
	}

	@Override
	public ATermAppl makeAppl(AFun fun)
	{
		return makeAppl(fun, array0);
	}

	@Override
	public ATermAppl makeAppl(AFun fun, ATerm arg)
	{
		final ATerm[] argarray1 = new ATerm[] { arg };
		return makeAppl(fun, argarray1);
	}

	@Override
	public ATermAppl makeAppl(AFun fun, ATerm arg1, ATerm arg2)
	{
		final ATerm[] argarray2 = new ATerm[] { arg1, arg2 };
		return makeAppl(fun, argarray2);
	}

	@Override
	public ATermAppl makeAppl(AFun fun, ATerm arg1, ATerm arg2, ATerm arg3)
	{
		final ATerm[] argarray3 = new ATerm[] { arg1, arg2, arg3 };
		return makeAppl(fun, argarray3);
	}

	@Override
	public ATermAppl makeAppl(AFun fun, ATerm arg1, ATerm arg2, ATerm arg3, ATerm arg4)
	{
		final ATerm[] argarray4 = new ATerm[] { arg1, arg2, arg3, arg4 };
		return makeAppl(fun, argarray4);
	}

	@Override
	public ATermAppl makeAppl(AFun fun, ATerm arg1, ATerm arg2, ATerm arg3, ATerm arg4, ATerm arg5)
	{
		final ATerm[] argarray5 = new ATerm[] { arg1, arg2, arg3, arg4, arg5 };
		return makeAppl(fun, argarray5);
	}

	@Override
	public ATermAppl makeAppl(AFun fun, ATerm arg1, ATerm arg2, ATerm arg3, ATerm arg4, ATerm arg5, ATerm arg6)
	{
		final ATerm[] args = { arg1, arg2, arg3, arg4, arg5, arg6 };
		return makeAppl(fun, args);
	}

	public ATermAppl makeAppl(AFun fun, ATerm arg1, ATerm arg2, ATerm arg3, ATerm arg4, ATerm arg5, ATerm arg6, ATerm arg7)
	{
		final ATerm[] args = { arg1, arg2, arg3, arg4, arg5, arg6, arg7 };
		return makeAppl(fun, args);
	}

	public ATermList getEmpty()
	{
		return empty;
	}

	private ATerm parseAbbrev(ATermReader reader) throws IOException
	{
		ATerm result;
		int abbrev;

		int c = reader.read();

		abbrev = 0;
		while (isBase64(c))
		{
			abbrev *= 64;
			if (c >= 'A' && c <= 'Z')
			{
				abbrev += c - 'A';
			}
			else
				if (c >= 'a' && c <= 'z')
				{
					abbrev += c - 'a' + 26;
				}
				else
					if (c >= '0' && c <= '9')
					{
						abbrev += c - '0' + 52;
					}
					else
						if (c == '+')
						{
							abbrev += 62;
						}
						else
							if (c == '/')
							{
								abbrev += 63;
							}
							else
							{
								throw new RuntimeException("not a base-64 digit: " + c);
							}

			c = reader.read();
		}

		result = reader.getTerm(abbrev);

		return result;
	}

	private ATerm parseNumber(ATermReader reader) throws IOException
	{
		final StringBuilder str = new StringBuilder();
		ATerm result;

		do
		{
			str.append((char) reader.getLastChar());
		} while (Character.isDigit(reader.read()));

		if (reader.getLastChar() != '.' && reader.getLastChar() != 'e' && reader.getLastChar() != 'E' && reader.getLastChar() != 'l' && reader.getLastChar() != 'L')
		{
			int val;
			try
			{
				val = Integer.parseInt(str.toString());
			}
			catch (final NumberFormatException e)
			{
				throw new ParseError("malformed int");
			}
			result = makeInt(val);
		}
		else
			if (reader.getLastChar() == 'l' || reader.getLastChar() == 'L')
			{
				reader.read();
				long val;
				try
				{
					val = Long.parseLong(str.toString());
				}
				catch (final NumberFormatException e)
				{
					throw new ParseError("malformed long");
				}
				result = makeLong(val);
			}
			else
			{
				if (reader.getLastChar() == '.')
				{
					str.append('.');
					reader.read();
					if (!Character.isDigit(reader.getLastChar()))
						throw new ParseError("digit expected");
					do
					{
						str.append((char) reader.getLastChar());
					} while (Character.isDigit(reader.read()));
				}
				if (reader.getLastChar() == 'e' || reader.getLastChar() == 'E')
				{
					str.append((char) reader.getLastChar());
					reader.read();
					if (reader.getLastChar() == '-' || reader.getLastChar() == '+')
					{
						str.append((char) reader.getLastChar());
						reader.read();
					}
					if (!Character.isDigit(reader.getLastChar()))
						throw new ParseError("digit expected!");
					do
					{
						str.append((char) reader.getLastChar());
					} while (Character.isDigit(reader.read()));
				}
				double val;
				try
				{
					val = Double.valueOf(str.toString()).doubleValue();
				}
				catch (final NumberFormatException e)
				{
					throw new ParseError("malformed real");
				}
				result = makeReal(val);
			}
		return result;
	}

	private String parseId(ATermReader reader) throws IOException
	{
		int c = reader.getLastChar();
		final StringBuilder buf = new StringBuilder(32);

		do
		{
			buf.append((char) c);
			c = reader.read();
		} while (Character.isLetterOrDigit(c) || c == '_' || c == '-' || c == '+' || c == '*' || c == '$');

		return buf.toString();
	}

	@SuppressWarnings("static-method")
	private String parseString(ATermReader reader) throws IOException
	{
		boolean escaped;
		final StringBuilder str = new StringBuilder();

		do
		{
			escaped = false;
			if (reader.read() == '\\')
			{
				reader.read();
				escaped = true;
			}

			final int lastChar = reader.getLastChar();
			if (lastChar == -1)
				throw new ParseError("Unterminated quoted function symbol: " + str);

			if (escaped)
			{
				switch (lastChar)
				{
					case 'n':
						str.append('\n');
						break;
					case 't':
						str.append('\t');
						break;
					case 'b':
						str.append('\b');
						break;
					case 'r':
						str.append('\r');
						break;
					case 'f':
						str.append('\f');
						break;
					case '\\':
						str.append('\\');
						break;
					case '\'':
						str.append('\'');
						break;
					case '\"':
						str.append('\"');
						break;
					case '0':
					case '1':
					case '2':
					case '3':
					case '4':
					case '5':
					case '6':
					case '7':
						str.append(reader.readOct());
						break;
					default:
						str.append('\\').append((char) reader.getLastChar());
				}
			}
			else
				if (lastChar != '\"')
				{
					str.append((char) lastChar);
				}
		} while (escaped || reader.getLastChar() != '"');

		return str.toString();
	}

	private ATermList parseATerms(ATermReader reader) throws IOException
	{
		final ATerm[] terms = parseATermsArray(reader);
		ATermList result = empty;
		for (int i = terms.length - 1; i >= 0; i--)
		{
			result = makeList(terms[i], result);
		}

		return result;
	}

	private ATerm[] parseATermsArray(ATermReader reader) throws IOException
	{
		final List<ATerm> list = new ArrayList<>();

		ATerm term = parseFromReader(reader);
		list.add(term);
		while (reader.getLastChar() == ',')
		{
			reader.readSkippingWS();
			term = parseFromReader(reader);
			list.add(term);
		}

		final ATerm[] array = new ATerm[list.size()];
		final ListIterator<ATerm> iter = list.listIterator();
		int index = 0;
		while (iter.hasNext())
		{
			array[index++] = iter.next();
		}
		return array;
	}

	private ATerm parseFromReader(ATermReader reader) throws IOException
	{
		ATerm result;
		int c, start, end;
		String funname;

		start = reader.getPosition();
		switch (reader.getLastChar())
		{
			case -1:
				throw new ParseError("premature EOF encountered.");

			case '#':
				return parseAbbrev(reader);

			case '[':
				c = reader.readSkippingWS();
				if (c == -1) { throw new ParseError("premature EOF encountered."); }

				if (c == ']')
				{
					c = reader.readSkippingWS();
					result = empty;
				}
				else
				{
					result = parseATerms(reader);
					if (reader.getLastChar() != ']') { throw new ParseError("expected ']' but got '" + (char) reader.getLastChar() + "'"); }
					c = reader.readSkippingWS();
				}

				break;

			case '<':
				c = reader.readSkippingWS();
				final ATerm ph = parseFromReader(reader);

				if (reader.getLastChar() != '>') { throw new ParseError("expected '>' but got '" + (char) reader.getLastChar() + "'"); }

				c = reader.readSkippingWS();

				result = makePlaceholder(ph);

				break;

			case '"':
				funname = parseString(reader);

				c = reader.readSkippingWS();
				if (reader.getLastChar() == '(')
				{
					c = reader.readSkippingWS();
					if (c == -1) { throw new ParseError("premature EOF encountered."); }
					if (reader.getLastChar() == ')')
					{
						result = makeAppl(makeAFun(funname, 0, true));
					}
					else
					{
						final ATerm[] list = parseATermsArray(reader);

						if (reader.getLastChar() != ')') { throw new ParseError("expected ')' but got '" + reader.getLastChar() + "'"); }
						result = makeAppl(makeAFun(funname, list.length, true), list);
					}
					c = reader.readSkippingWS();
				}
				else
				{
					result = makeAppl(makeAFun(funname, 0, true));
				}

				break;

			case '(':
				c = reader.readSkippingWS();
				if (c == -1) { throw new ParseError("premature EOF encountered."); }
				if (reader.getLastChar() == ')')
				{
					result = makeAppl(makeAFun("", 0, false));
				}
				else
				{
					final ATerm[] list = parseATermsArray(reader);

					if (reader.getLastChar() != ')') { throw new ParseError("expected ')' but got '" + (char) reader.getLastChar() + "'"); }
					result = makeAppl(makeAFun("", list.length, false), list);
				}
				c = reader.readSkippingWS();

				break;

			case '-':
			case '0':
			case '1':
			case '2':
			case '3':
			case '4':
			case '5':
			case '6':
			case '7':
			case '8':
			case '9':
				result = parseNumber(reader);
				c = reader.skipWS();
				break;

			default:
				c = reader.getLastChar();
				if (Character.isLetter(c))
				{
					funname = parseId(reader);
					c = reader.skipWS();
					if (reader.getLastChar() == '(')
					{
						c = reader.readSkippingWS();
						if (c == -1) { throw new ParseError("premature EOF encountered."); }
						if (reader.getLastChar() == ')')
						{
							result = makeAppl(makeAFun(funname, 0, false));
						}
						else
						{
							final ATerm[] list = parseATermsArray(reader);

							if (reader.getLastChar() != ')') { throw new ParseError("expected ')' but got '" + (char) reader.getLastChar() + "'"); }
							result = makeAppl(makeAFun(funname, list.length, false), list);
						}
						c = reader.readSkippingWS();
					}
					else
					{
						result = makeAppl(makeAFun(funname, 0, false));
					}
				}
				else
				{
					throw new ParseError("illegal character: '" + (char) reader.getLastChar() + "'");
				}
		}

		if (reader.getLastChar() == '{')
		{
			ATermList annos;
			if (reader.readSkippingWS() == '}')
			{
				reader.readSkippingWS();
				annos = empty;
			}
			else
			{
				annos = parseATerms(reader);
				if (reader.getLastChar() != '}') { throw new ParseError("'}' expected '" + (char) reader.getLastChar() + "'"); }
				reader.readSkippingWS();
			}
			result = result.setAnnotations(annos);
		}

		/* Parse some ToolBus anomalies for backwards compatibility */
		if (reader.getLastChar() == ':')
		{
			reader.read();
			final ATerm anno = parseFromReader(reader);
			result = result.setAnnotation(parse("type"), anno);
		}

		if (reader.getLastChar() == '?')
		{
			reader.readSkippingWS();
			result = result.setAnnotation(parse("result"), parse("true"));
		}

		end = reader.getPosition();
		reader.storeNextTerm(result, end - start);

		return result;
	}

	@Override
	public ATerm parse(String trm)
	{
		try
		{
			final ATermReader reader = new ATermReader(new StringReader(trm), trm.length());
			reader.readSkippingWS();
			final ATerm result = parseFromReader(reader);
			return result;
		}
		catch (final IOException e)
		{
			throw new ParseError("premature end of string");
		}
	}

	@Override
	public ATerm make(String trm)
	{
		return parse(trm);
	}

	@Override
	public ATerm make(String pattern, List<Object> args)
	{
		return make(parse(pattern), args);
	}

	@Override
	public ATerm make(String pattern, Object arg1)
	{
		final List<Object> args = new LinkedList<>();
		args.add(arg1);
		return make(pattern, args);
	}

	@Override
	public ATerm make(String pattern, Object arg1, Object arg2)
	{
		final List<Object> args = new LinkedList<>();
		args.add(arg1);
		args.add(arg2);
		return make(pattern, args);
	}

	@Override
	public ATerm make(String pattern, Object arg1, Object arg2, Object arg3)
	{
		final List<Object> args = new LinkedList<>();
		args.add(arg1);
		args.add(arg2);
		args.add(arg3);
		return make(pattern, args);
	}

	@Override
	public ATerm make(String pattern, Object arg1, Object arg2, Object arg3, Object arg4)
	{
		final List<Object> args = new LinkedList<>();
		args.add(arg1);
		args.add(arg2);
		args.add(arg3);
		args.add(arg4);
		return make(pattern, args);
	}

	@Override
	public ATerm make(String pattern, Object arg1, Object arg2, Object arg3, Object arg4, Object arg5)
	{
		final List<Object> args = new LinkedList<>();
		args.add(arg1);
		args.add(arg2);
		args.add(arg3);
		args.add(arg4);
		args.add(arg5);
		return make(pattern, args);
	}

	@Override
	public ATerm make(String pattern, Object arg1, Object arg2, Object arg3, Object arg4, Object arg5, Object arg6)
	{
		final List<Object> args = new LinkedList<>();
		args.add(arg1);
		args.add(arg2);
		args.add(arg3);
		args.add(arg4);
		args.add(arg5);
		args.add(arg6);
		return make(pattern, args);
	}

	@Override
	public ATerm make(String pattern, Object arg1, Object arg2, Object arg3, Object arg4, Object arg5, Object arg6, Object arg7)
	{
		final List<Object> args = new LinkedList<>();
		args.add(arg1);
		args.add(arg2);
		args.add(arg3);
		args.add(arg4);
		args.add(arg5);
		args.add(arg6);
		args.add(arg7);
		return make(pattern, args);
	}

	@Override
	public ATerm make(ATerm pattern, List<Object> args)
	{
		return pattern.make(args);
	}

	ATerm parsePattern(String pattern) throws ParseError
	{
		return parse(pattern);
	}

	protected boolean isDeepEqual(ATermImpl t1, ATerm t2)
	{
		throw new UnsupportedOperationException("not yet implemented!");
	}

	private ATerm readFromSharedTextFile(ATermReader reader) throws IOException
	{
		reader.initializeSharing();
		return parseFromReader(reader);
	}

	private ATerm readFromTextFile(ATermReader reader) throws IOException
	{
		return parseFromReader(reader);
	}

	@Override
	public ATerm readFromTextFile(InputStream stream) throws IOException
	{
		final ATermReader reader = new ATermReader(new BufferedReader(new InputStreamReader(stream)));
		reader.readSkippingWS();

		return readFromTextFile(reader);
	}

	@Override
	public ATerm readFromSharedTextFile(InputStream stream) throws IOException
	{
		final ATermReader reader = new ATermReader(new BufferedReader(new InputStreamReader(stream)));
		reader.readSkippingWS();

		if (reader.getLastChar() != '!') { throw new IOException("not a shared text file!"); }

		reader.readSkippingWS();

		return readFromSharedTextFile(reader);
	}

	@Override
	public ATerm readFromBinaryFile(InputStream stream) throws IOException
	{
		return readFromBinaryFile(stream, false);
	}

	private ATerm readFromBinaryFile(InputStream stream, boolean headerRead) throws ParseError, IOException
	{
		final BAFReader r = new BAFReader(this, stream);
		return r.readFromBinaryFile(headerRead);
	}

	private ATerm readSAFFromOldStyleStream(InputStream stream) throws IOException
	{
		final ByteArrayOutputStream baos = new ByteArrayOutputStream();
		final byte[] buffer = new byte[4096];
		int nrOfBytesRead;
		while ((nrOfBytesRead = stream.read(buffer, 0, buffer.length)) != -1)
		{
			baos.write(buffer, 0, nrOfBytesRead);
		}
		return BinaryReader.readTermFromSAFString(this, baos.toByteArray());
	}

	@Override
	public ATerm readFromFile(InputStream stream) throws IOException
	{
		int firstToken;
		do
		{
			firstToken = stream.read();
			if (firstToken == -1)
				throw new IOException("Premature EOF.");
		} while (Character.isWhitespace((char) firstToken));

		final char typeByte = (char) firstToken;

		if (typeByte == '!')
		{
			final ATermReader reader = new ATermReader(new BufferedReader(new InputStreamReader(stream)));
			reader.readSkippingWS();
			return readFromSharedTextFile(reader);
		}
		else
			if (typeByte == '?')
			{
				return readSAFFromOldStyleStream(stream);
			}
			else
				if (Character.isLetterOrDigit(typeByte) || typeByte == '_' || typeByte == '[' || typeByte == '-')
				{
					final ATermReader reader = new ATermReader(new BufferedReader(new InputStreamReader(stream)));
					reader.last_char = typeByte; // Reinsert the type into the stream (since in this case it wasn't a type byte).
					return readFromTextFile(reader);
				}
				else
					if (firstToken == 0)
					{
						final BufferedInputStream bis = new BufferedInputStream(stream);
						if (BAFReader.isBinaryATerm(bis)) { return readFromBinaryFile(bis, true); }
					}
		throw new RuntimeException("Unsupported file type");
	}

	@Override
	public ATerm readFromFile(String filename) throws IOException
	{
		ATerm result;

		try (FileInputStream fis = new FileInputStream(filename))
		{
			result = readFromFile(fis);
		}

		return result;
	}

	/**
	 * @see ATermFactory#importTerm(ATerm)
	 */
	@Override
	public ATerm importTerm(ATerm term)
	{
		final SharedObject object = (SharedObject) term;
		if (contains(object))
			return term;

		ATerm result;

		switch (term.getType())
		{
			case ATerm.APPL:
				final ATermAppl appl = (ATermAppl) term;

				final AFun fun = (AFun) importTerm(appl.getAFun());

				final int nrOfArguments = appl.getArity();
				final ATerm[] newArguments = new ATerm[nrOfArguments];
				for (int i = nrOfArguments - 1; i >= 0; i--)
				{
					newArguments[i] = importTerm(appl.getArgument(i));
				}

				result = makeAppl(fun, newArguments);
				break;
			case ATerm.LIST:
				final ATermList list = (ATermList) term;
				if (list.isEmpty())
				{
					result = empty;
					break;
				}
				final ATerm first = importTerm(list.getFirst());
				final ATermList next = (ATermList) importTerm(list.getNext());

				result = makeList(first, next);
				break;
			case ATerm.INT:
				final ATermInt integer = (ATermInt) term;

				result = makeInt(integer.getInt());
				break;
			case ATerm.LONG:
				final ATermLong elongatedType = (ATermLong) term;

				result = makeLong(elongatedType.getLong());
				break;
			case ATerm.REAL:
				final ATermReal real = (ATermReal) term;

				result = makeReal(real.getReal());
				break;
			case ATerm.PLACEHOLDER:
				final ATermPlaceholder placeHolder = (ATermPlaceholder) term;

				result = makePlaceholder(importTerm(placeHolder.getPlaceholder()));
				break;
			case ATerm.AFUN:
				final AFun afun = (AFun) term;

				return makeAFun(afun.getName(), afun.getArity(), afun.isQuoted());
			default:
				throw new RuntimeException("Unknown term type id: " + term.getType());
		}

		if (term.hasAnnotations())
		{
			final ATermList annotations = term.getAnnotations();
			result = result.setAnnotations(annotations);
		}

		return result;
	}
}

class ATermReader
{
	private static final int INITIAL_TABLE_SIZE = 2048;
	private static final int TABLE_INCREMENT = 4096;

	private static final int INITIAL_BUFFER_SIZE = 1024;

	private final Reader reader;

	int last_char;
	private int pos;

	private int nr_terms;
	private ATerm[] table;

	private char[] buffer;
	private int limit;
	private int bufferPos;

	public ATermReader(Reader reader)
	{
		this(reader, INITIAL_BUFFER_SIZE);
	}

	public ATermReader(Reader reader, int bufferSize)
	{
		this.reader = reader;
		last_char = -1;
		pos = 0;

		if (bufferSize < INITIAL_BUFFER_SIZE)
			buffer = new char[bufferSize];
		else
			buffer = new char[INITIAL_BUFFER_SIZE];
		limit = -1;
		bufferPos = -1;
	}

	public void initializeSharing()
	{
		table = new ATerm[INITIAL_TABLE_SIZE];
		nr_terms = 0;
	}

	public void storeNextTerm(ATerm t, int size)
	{
		if (table == null) { return; }

		if (size <= PureFactory.abbrevSize(nr_terms)) { return; }

		if (nr_terms == table.length)
		{
			final ATerm[] new_table = new ATerm[table.length + TABLE_INCREMENT];
			System.arraycopy(table, 0, new_table, 0, table.length);
			table = new_table;
		}

		table[nr_terms++] = t;
	}

	public ATerm getTerm(int index)
	{
		if (index < 0 || index >= nr_terms) { throw new RuntimeException("illegal index"); }
		return table[index];
	}

	public int read() throws IOException
	{
		if (bufferPos == limit)
		{
			limit = reader.read(buffer);
			bufferPos = 0;
		}

		if (limit == -1)
		{
			last_char = -1;
		}
		else
		{
			last_char = buffer[bufferPos++];
			pos++;
		}

		return last_char;
	}

	public int readSkippingWS() throws IOException
	{
		do
		{
			last_char = read();
		} while (Character.isWhitespace(last_char));

		return last_char;

	}

	public int skipWS() throws IOException
	{
		while (Character.isWhitespace(last_char))
		{
			last_char = read();
		}

		return last_char;
	}

	public int readOct() throws IOException
	{
		int val = Character.digit(last_char, 8);
		val += Character.digit(read(), 8);

		if (val < 0) { throw new ParseError("octal must have 3 octdigits."); }

		val += Character.digit(read(), 8);

		if (val < 0) { throw new ParseError("octal must have 3 octdigits"); }

		return val;
	}

	public int getLastChar()
	{
		return last_char;
	}

	public int getPosition()
	{
		return pos;
	}
}
