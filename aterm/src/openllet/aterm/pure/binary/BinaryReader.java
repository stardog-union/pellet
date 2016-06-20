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

package openllet.aterm.pure.binary;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;
import openllet.aterm.AFun;
import openllet.aterm.ATerm;
import openllet.aterm.ATermList;
import openllet.aterm.pure.PureFactory;

/**
 * Reconstructs an ATerm from the given (series of) buffer(s). It can be retrieved when the
 * construction of the term is done / when _isDone() returns true.
 * 
 * For example (yes I know this code is crappy, but it's simple):<blockquote>
 * 
 * <pre>
 * ByteBuffer buffer = ByteBuffer.allocate(8192);
 * BinaryWriter bw = new BinaryWriter(openllet.aterm);
 * while (!bw.isDone())
 * {
 * 	int bytesRead = channel.read(buffer); // Read the next chunk of data from the stream.
 * 	if (!buffer.hasRemaining() || bytesRead == -1)
 * 	{
 * 		bw.serialize(buffer);
 * 		buffer.clear();
 * 	}
 * }
 * </pre>
 * 
 * </blockquote>
 * 
 * @author Arnold Lankamp
 */
public class BinaryReader
{
	private final static int ISSHAREDFLAG = 0x00000080;
	private final static int TYPEMASK = 0x0000000f;
	private final static int ANNOSFLAG = 0x00000010;

	private final static int ISFUNSHARED = 0x00000040;
	private final static int APPLQUOTED = 0x00000020;

	private final static int INITIALSHAREDTERMSARRAYSIZE = 1024;

	private final static int STACKSIZE = 256;

	private final PureFactory factory;

	private int _sharedTermIndex;
	private ATerm[] _sharedTerms;
	private final List<AFun> _applSignatures;

	private ATermConstruct[] _stack;
	private int _stackPosition;

	private int _tempType = -1;
	private byte[] _tempBytes = null;
	private int _tempBytesIndex = 0;
	private int _tempArity = -1;
	private boolean _tempIsQuoted = false;

	private ByteBuffer _currentBuffer;

	private boolean _isDone = false;

	/**
	 * Constructor.
	 * 
	 * @param factory
	 *            The factory to use for reconstruction of the ATerm.
	 */
	public BinaryReader(final PureFactory factory)
	{
		super();

		this.factory = factory;

		_sharedTerms = new ATerm[INITIALSHAREDTERMSARRAYSIZE];
		_applSignatures = new ArrayList<>();
		_sharedTermIndex = 0;

		_stack = new ATermConstruct[STACKSIZE];
		_stackPosition = -1;
	}

	/**
	 * Resizes the openllet.shared.hash terms array when needed. When we're running low on space the capacity
	 * will be doubled.
	 */
	private void ensureSharedTermsCapacity()
	{
		final int sharedTermsArraySize = _sharedTerms.length;
		if (_sharedTermIndex + 1 >= sharedTermsArraySize)
		{
			final ATerm[] newSharedTermsArray = new ATerm[sharedTermsArraySize << 1];
			System.arraycopy(_sharedTerms, 0, newSharedTermsArray, 0, sharedTermsArraySize);
			_sharedTerms = newSharedTermsArray;
		}
	}

	/**
	 * Constructs (a part of) the ATerm from the binary representation present in the given buffer.
	 * This method will 'remember' where it was left.
	 * 
	 * @param buffer
	 *            The buffer that contains (a part of) the binary representation of the ATerm.
	 */
	public void deserialize(final ByteBuffer buffer)
	{
		_currentBuffer = buffer;

		if (_tempType != -1)
			readData();

		while (buffer.hasRemaining())
		{
			final byte header = buffer.get();

			if ((header & ISSHAREDFLAG) == ISSHAREDFLAG)
			{
				final int index = readInt();
				final ATerm term = _sharedTerms[index];
				_stackPosition++;

				linkTerm(term);
			}
			else
			{
				final int type = (header & TYPEMASK);

				final ATermConstruct ac = new ATermConstruct(type, ((header & ANNOSFLAG) == ANNOSFLAG), _sharedTermIndex++);

				ensureSharedTermsCapacity();

				_stack[++_stackPosition] = ac;

				TYPECHECK: switch (type)
				{
					case ATerm.APPL:
						touchAppl(header);
						break TYPECHECK;
					case ATerm.LIST:
						touchList();
						break TYPECHECK;
					case ATerm.INT:
						touchInt();
						break TYPECHECK;
					case ATerm.REAL:
						touchReal();
						break TYPECHECK;
					case ATerm.LONG:
						touchLong();
						break TYPECHECK;
					case ATerm.BLOB:
						touchBlob();
						break TYPECHECK;
					case ATerm.PLACEHOLDER:
						touchPlaceholder();
						break TYPECHECK;
					default:
						throw new RuntimeException("Unknown type id: " + type + ". Current buffer position: " + _currentBuffer.position());
				}
			}

			// Make sure the _stack remains large enough
			ensureStackCapacity();
		}
	}

	/**
	 * Resizes the _stack when needed. When we're running low on _stack space the capacity will be
	 * doubled.
	 */
	private void ensureStackCapacity()
	{
		final int stackSize = _stack.length;
		if (_stackPosition + 1 >= stackSize)
		{
			final ATermConstruct[] newStack = new ATermConstruct[(stackSize << 1)];
			System.arraycopy(_stack, 0, newStack, 0, _stack.length);
			_stack = newStack;
		}
	}

	/**
	 * Checks if we are done serializing.
	 * 
	 * @return True if we are done; false otherwise.
	 */
	public boolean isDone()
	{
		return _isDone;
	}

	/**
	 * Returns the reconstructed ATerm. A RuntimeException will be thrown when we are not yet done
	 * with the reconstruction of the ATerm.
	 * 
	 * @return The reconstructed ATerm.
	 */
	public ATerm getRoot()
	{
		if (!_isDone)
			throw new RuntimeException("Can't retrieve the root of the tree while it's still being constructed.");

		return _sharedTerms[0];
	}

	/**
	 * Resets the temporary data. We don't want to hold it if it's not necessary.
	 */
	private void resetTemp()
	{
		_tempType = -1;
		_tempBytes = null;
		_tempBytesIndex = 0;
	}

	/**
	 * Reads a series of bytes from the buffer. When the nessecary amount of bytes is read a term
	 * of the corresponding type will be constructed and (if possible) linked with it's parent.
	 */
	private void readData()
	{
		final int length = _tempBytes.length;
		int bytesToRead = (length - _tempBytesIndex);
		final int remaining = _currentBuffer.remaining();
		if (remaining < bytesToRead)
			bytesToRead = remaining;

		_currentBuffer.get(_tempBytes, _tempBytesIndex, bytesToRead);
		_tempBytesIndex += bytesToRead;

		if (_tempBytesIndex == length)
		{
			if (_tempType == ATerm.APPL)
			{
				final AFun fun = factory.makeAFun(new String(_tempBytes), _tempArity, _tempIsQuoted);
				_applSignatures.add(fun);

				final ATermConstruct ac = _stack[_stackPosition];
				if (_tempArity == 0 && !ac.hasAnnos)
				{
					final ATerm term = factory.makeAppl(fun);
					_sharedTerms[ac.termIndex] = term;
					linkTerm(term);
				}
				else
				{
					ac.tempTerm = fun;
					ac.subTerms = new ATerm[_tempArity];
				}
			}
			else
				if (_tempType == ATerm.BLOB)
				{
					final ATermConstruct ac = _stack[_stackPosition];
					final ATerm term = factory.makeBlob(_tempBytes);

					if (!ac.hasAnnos)
					{
						_sharedTerms[ac.termIndex] = term;
						linkTerm(term);
					}
					else
					{
						ac.tempTerm = term;
					}
				}
				else
				{
					throw new RuntimeException("Unsupported chunkified type: " + _tempType);
				}

			resetTemp();
		}
	}

	/**
	 * Starts the deserialization process of a appl.
	 * 
	 * @param header
	 *            The header of the appl.
	 */
	private void touchAppl(final byte header)
	{
		if ((header & ISFUNSHARED) == ISFUNSHARED)
		{
			final int key = readInt();

			final AFun fun = _applSignatures.get(key);

			final int arity = fun.getArity();

			final ATermConstruct ac = _stack[_stackPosition];

			if (arity == 0 && !ac.hasAnnos)
			{
				final ATerm term = factory.makeAppl(fun);
				_sharedTerms[ac.termIndex] = term;
				linkTerm(term);
			}
			else
			{
				ac.tempTerm = fun;
				ac.subTerms = new ATerm[arity];
			}
		}
		else
		{
			_tempIsQuoted = ((header & APPLQUOTED) == APPLQUOTED);
			_tempArity = readInt();
			final int nameLength = readInt();

			_tempType = ATerm.APPL;
			_tempBytes = new byte[nameLength];
			_tempBytesIndex = 0;

			readData();
		}
	}

	/**
	 * Deserialializes a list.
	 */
	private void touchList()
	{
		final int size = readInt();

		final ATermConstruct ac = _stack[_stackPosition];
		ac.subTerms = new ATerm[size];

		if (size == 0)
		{
			final ATerm term = factory.makeList();

			if (!ac.hasAnnos)
			{
				_sharedTerms[ac.termIndex] = term;
				linkTerm(term);
			}
			else
			{
				ac.tempTerm = term;
			}
		}
	}

	/**
	 * Deserialializes an int.
	 */
	private void touchInt()
	{
		final int value = readInt();

		final ATermConstruct ac = _stack[_stackPosition];
		final ATerm term = factory.makeInt(value);

		if (!ac.hasAnnos)
		{
			_sharedTerms[ac.termIndex] = term;
			linkTerm(term);
		}
		else
		{
			ac.tempTerm = term;
		}
	}

	/**
	 * Deserialializes a real.
	 */
	private void touchReal()
	{
		final double value = readDouble();

		final ATermConstruct ac = _stack[_stackPosition];
		final ATerm term = factory.makeReal(value);

		if (!ac.hasAnnos)
		{
			_sharedTerms[ac.termIndex] = term;
			linkTerm(term);
		}
		else
		{
			ac.tempTerm = term;
		}
	}

	/**
	 * Deserialializes a long.
	 */
	private void touchLong()
	{
		final long value = readLong();

		final ATermConstruct ac = _stack[_stackPosition];
		final ATerm term = factory.makeLong(value);

		if (!ac.hasAnnos)
		{
			_sharedTerms[ac.termIndex] = term;
			linkTerm(term);
		}
		else
		{
			ac.tempTerm = term;
		}
	}

	/**
	 * Starts the deserialization process for a BLOB.
	 */
	private void touchBlob()
	{
		final int length = readInt();

		_tempType = ATerm.BLOB;
		_tempBytes = new byte[length];
		_tempBytesIndex = 0;

		readData();
	}

	/**
	 * Deserialializes a placeholder.
	 */
	private void touchPlaceholder()
	{
		// A placeholder doesn't have content

		final ATermConstruct ac = _stack[_stackPosition];
		ac.subTerms = new ATerm[1];
	}

	/**
	 * Constructs a term from the given structure.
	 * 
	 * @param ac
	 *            A structure that contains all the nessecary data to contruct the associated term.
	 * @return The constructed openllet.aterm.
	 */
	private ATerm buildTerm(final ATermConstruct ac)
	{
		ATerm constructedTerm;
		final ATerm[] subTerms = ac.subTerms;

		final int type = ac.type;
		if (type == ATerm.APPL)
		{
			final AFun fun = (AFun) ac.tempTerm;
			constructedTerm = factory.makeAppl(fun, subTerms, ac.annos);
		}
		else
			if (type == ATerm.LIST)
			{
				ATermList list = factory.makeList();
				for (int i = subTerms.length - 1; i >= 0; i--)
				{
					list = factory.makeList(subTerms[i], list);
				}

				if (ac.hasAnnos)
					list = (ATermList) list.setAnnotations(ac.annos);

				constructedTerm = list;
			}
			else
				if (type == ATerm.PLACEHOLDER)
				{
					final ATerm placeholder = factory.makePlaceholder(subTerms[0]);

					constructedTerm = placeholder;
				}
				else
					if (ac.hasAnnos)
					{
						constructedTerm = ac.tempTerm.setAnnotations(ac.annos);
					}
					else
					{
						throw new RuntimeException("Unable to construct term.\n");
					}

		return constructedTerm;
	}

	/**
	 * Links the given term with it's parent.
	 * 
	 * @param aTerm
	 *            The term that needs to be linked.
	 */
	private void linkTerm(final ATerm aTerm)
	{
		ATerm term = aTerm;

		while (_stackPosition != 0)
		{
			final ATermConstruct parent = _stack[--_stackPosition];

			final ATerm[] subTerms = parent.subTerms;
			final boolean hasAnnos = parent.hasAnnos;
			if (subTerms != null && subTerms.length > parent.subTermIndex)
			{
				subTerms[parent.subTermIndex++] = term;

				if (parent.subTerms.length != parent.subTermIndex || hasAnnos)
					return;

				if (!hasAnnos)
					parent.annos = factory.makeList();
			}
			else
				if (hasAnnos && (term instanceof ATermList))
				{
					parent.annos = (ATermList) term;
				}
				else
				{
					throw new RuntimeException("Encountered a term that didn't fit anywhere. Type: " + term.getType());
				}

			term = buildTerm(parent);

			_sharedTerms[parent.termIndex] = term;
		}

		if (_stackPosition == 0)
			_isDone = true;
	}

	private final static int SEVENBITS = 0x0000007f;
	private final static int SIGNBIT = 0x00000080;
	private final static int BYTEMASK = 0x000000ff;
	private final static int BYTEBITS = 8;
	private final static int LONGBITS = 8;

	/**
	 * Reconstructs an integer from the following 1 to 5 bytes in the buffer (depending on how many
	 * we used to represent the value). See the documentation of
	 * openllet.aterm.binary.BinaryWriter#writeInt(int) for more information.
	 * 
	 * @return The reconstructed integer.
	 */
	private int readInt()
	{
		byte part = _currentBuffer.get();
		int result = (part & SEVENBITS);

		if ((part & SIGNBIT) == 0)
			return result;

		part = _currentBuffer.get();
		result |= ((part & SEVENBITS) << 7);
		if ((part & SIGNBIT) == 0)
			return result;

		part = _currentBuffer.get();
		result |= ((part & SEVENBITS) << 14);
		if ((part & SIGNBIT) == 0)
			return result;

		part = _currentBuffer.get();
		result |= ((part & SEVENBITS) << 21);
		if ((part & SIGNBIT) == 0)
			return result;

		part = _currentBuffer.get();
		result |= ((part & SEVENBITS) << 28);
		return result;
	}

	/**
	 * Reconstructs a double from the following 8 bytes in the buffer.
	 * 
	 * @return The reconstructed double.
	 */
	private double readDouble()
	{
		final long result = readLong();
		return Double.longBitsToDouble(result);
	}

	/**
	 * Reconstructs a long from the following 8 bytes in the buffer.
	 * 
	 * @return The reconstructed long.
	 */
	private long readLong()
	{
		long result = 0;
		for (int i = 0; i < LONGBITS; i++)
		{
			result |= ((((long) _currentBuffer.get()) & BYTEMASK) << (i * BYTEBITS));
		}
		return result;
	}

	/**
	 * Reads the ATerm from the given SAF encoded file.
	 * 
	 * @param pureFactory
	 *            The factory to use.
	 * @param file
	 *            The file that contains the SAF encoded term.
	 * @return The constructed ATerm.
	 * @throws IOException Thrown when an error occurs while reading the given file.
	 */
	public static ATerm readTermFromSAFFile(final PureFactory pureFactory, final File file) throws IOException
	{
		final BinaryReader binaryReader = new BinaryReader(pureFactory);

		final ByteBuffer byteBuffer = ByteBuffer.allocate(65536);
		final ByteBuffer sizeBuffer = ByteBuffer.allocate(2);

		try (FileInputStream fis = new FileInputStream(file))
		{
			try (FileChannel fc = fis.getChannel())
			{
				// Consume the SAF identification token.
				byteBuffer.limit(1);
				int bytesRead = fc.read(byteBuffer);
				if (bytesRead != 1)
					throw new IOException("Unable to read SAF identification token.\n");

				do
				{
					sizeBuffer.clear();
					bytesRead = fc.read(sizeBuffer);
					if (bytesRead <= 0)
						break;
					else
						if (bytesRead != 2)
							throw new IOException("Unable to read block size bytes from file: " + bytesRead + ".\n");
					sizeBuffer.flip();

					int blockSize = (sizeBuffer.get() & 0x000000ff) + ((sizeBuffer.get() & 0x000000ff) << 8);
					if (blockSize == 0)
						blockSize = 65536;

					byteBuffer.clear();
					byteBuffer.limit(blockSize);
					bytesRead = fc.read(byteBuffer);
					byteBuffer.flip();
					if (bytesRead != blockSize)
						throw new IOException("Unable to read bytes from file " + bytesRead + " vs " + blockSize + ".");

					binaryReader.deserialize(byteBuffer);
				} while (bytesRead > 0);

				if (!binaryReader.isDone())
					throw new RuntimeException("Term incomplete, missing data.\n");
			}
		}

		return binaryReader.getRoot();
	}

	/**
	 * Reads the ATerm from the given SAF encoded data.
	 * 
	 * @param pureFactory
	 *            The factory to use.
	 * @param data
	 *            The SAF encoded data.
	 * @return The constructed ATerm.
	 */
	public static ATerm readTermFromSAFString(final PureFactory pureFactory, final byte[] data)
	{
		final BinaryReader binaryReader = new BinaryReader(pureFactory);

		final int length = data.length;
		int position = 0;
		do
		{

			int blockSize = data[position++] & 0x000000ff;
			blockSize += (data[position++] & 0x000000ff) << 8;
			if (blockSize == 0)
				blockSize = 65536;

			final ByteBuffer byteBuffer = ByteBuffer.allocate(blockSize);

			byteBuffer.put(data, position, blockSize);
			position += blockSize;
			byteBuffer.flip();

			binaryReader.deserialize(byteBuffer);
		} while (position < length);

		if (!binaryReader.isDone())
			throw new RuntimeException("Term incomplete, missing data.\n");

		return binaryReader.getRoot();
	}
}
