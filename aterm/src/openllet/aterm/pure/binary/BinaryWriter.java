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
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import openllet.aterm.AFun;
import openllet.aterm.ATerm;
import openllet.aterm.ATermAppl;
import openllet.aterm.ATermBlob;
import openllet.aterm.ATermFwdVoid;
import openllet.aterm.ATermInt;
import openllet.aterm.ATermList;
import openllet.aterm.ATermLong;
import openllet.aterm.ATermPlaceholder;
import openllet.aterm.ATermReal;

/**
 * Writes the given ATerm to a (streamable) binary format. Supply the constructor of this class with
 * a ATerm and keep calling the serialize method until the finished() method returns true.
 * 
 * For example (yes I know this code is crappy, but it's simple):<blockquote>
 * 
 * <pre>
 * ByteBuffer buffer = ByteBuffer.allocate(8192);
 * BinaryWriter bw = new BinaryWriter(openllet.aterm);
 * while (!bw.isFinished())
 * {
 * 	buffer.clear();
 * 	bw.serialize(buffer);
 * 	while (buffer.hasRemaining())
 * 		channel.write(buffer); // Write the chunk of data to a channel
 * }
 * </pre>
 * 
 * </blockquote>
 * 
 * @author Arnold Lankamp
 */
public class BinaryWriter extends ATermFwdVoid
{
	private final static int ISSHAREDFLAG = 0x00000080;
	private final static int ANNOSFLAG = 0x00000010;

	private final static int ISFUNSHARED = 0x00000040;
	private final static int APPLQUOTED = 0x00000020;

	private final static int STACKSIZE = 256;

	private final static int MINIMUMFREESPACE = 10;

	private final Map<ATerm, Integer> _sharedTerms;
	private int _currentKey;
	private final Map<AFun, Integer> _applSignatures;
	private int _sigKey;

	private ATermMapping[] _stack;
	private int _stackPosition;
	private ATerm _currentTerm;
	private int _indexInTerm;
	private byte[] _tempNameWriteBuffer;

	private ByteBuffer _currentBuffer;

	/**
	 * Constructor.
	 * 
	 * @param root is the ATerm that needs to be serialized.
	 */
	public BinaryWriter(final ATerm root)
	{
		super();

		_sharedTerms = new HashMap<>();
		_currentKey = 0;
		_applSignatures = new HashMap<>();
		_sigKey = 0;

		_stack = new ATermMapping[STACKSIZE];
		_stackPosition = 0;

		final ATermMapping tm = new ATermMapping(root);

		_stack[_stackPosition] = tm;
		_currentTerm = root;

		_indexInTerm = 0;
		_tempNameWriteBuffer = null;
	}

	/**
	 * Serializes the term from the position where it left of the last time this method was called.
	 * Note that the buffer will be flipped before returned.
	 * 
	 * @param buffer that will be filled with data.
	 */
	public void serialize(final ByteBuffer buffer)
	{
		_currentBuffer = buffer;

		while (_currentTerm != null)
		{
			if (buffer.remaining() < MINIMUMFREESPACE)
				break;

			final Integer id = _sharedTerms.get(_currentTerm);
			if (id != null)
			{
				buffer.put((byte) ISSHAREDFLAG);
				writeInt(id.intValue());

				_stackPosition--; // Pop the term from the _stack, since it's subtree is openllet.shared.hash.
			}
			else
			{
				visit(_currentTerm);

				if (_currentTerm.getType() == ATerm.LIST)
					_stack[_stackPosition].nextPartOfList = (ATermList) _currentTerm; // <- for ATermList->next optimizaton.

				// Don't add the term to the openllet.shared.hash list until we are completely done with it.
				if (_indexInTerm == 0)
					_sharedTerms.put(_currentTerm, new Integer(_currentKey++));
				else
					break;
			}

			_currentTerm = getNextTerm();
		}

		buffer.flip();
	}

	/**
	 * Checks if we are done serializing.
	 * 
	 * @return true when we are done serializing; false otherwise.
	 */
	public boolean isFinished()
	{
		return (_currentTerm == null);
	}

	/**
	 * Finds the next term we are going to serialize, based on the current state of the _stack.
	 * 
	 * @return The next term we are going to serialize.
	 */
	private ATerm getNextTerm()
	{
		ATerm next = null;

		// Make sure the _stack remains large enough
		ensureStackCapacity();

		while (next == null && _stackPosition > -1)
		{
			final ATermMapping current = _stack[_stackPosition];
			final ATerm term = current.term;

			if (term.getChildCount() > current.subTermIndex + 1)
			{
				if (term.getType() != ATerm.LIST)
				{
					next = term.getChildAt(++current.subTermIndex);
				}
				else
				{
					final ATermList nextList = current.nextPartOfList;
					next = nextList.getFirst();
					current.nextPartOfList = nextList.getNext();

					current.subTermIndex++;
				}

				final ATermMapping child = new ATermMapping(next);
				_stack[++_stackPosition] = child;
			}
			else
				if (!current.annosDone && term.hasAnnotations())
				{
					next = term.getAnnotations();

					final ATermMapping annos = new ATermMapping(next);
					_stack[++_stackPosition] = annos;

					current.annosDone = true;
				}
				else
				{
					_stackPosition--;
				}
		}

		return next;
	}

	/**
	 * Resizes the _stack when needed. When we're running low on _stack space the capacity will be
	 * doubled.
	 */
	private void ensureStackCapacity()
	{
		final int stackSize = _stack.length;
		if (_stackPosition + 1 == stackSize)
		{
			final ATermMapping[] newStack = new ATermMapping[(stackSize << 1)];
			System.arraycopy(_stack, 0, newStack, 0, _stack.length);
			_stack = newStack;
		}
	}

	/**
	 * Returns a header for the given term.
	 * 
	 * @param term
	 *            The term we are requesting a header for.
	 * @return The constructed header.
	 */
	private static byte getHeader(final ATerm term)
	{
		byte header = (byte) term.getType();
		if (term.hasAnnotations())
			header = (byte) (header | ANNOSFLAG);

		return header;
	}

	/**
	 * Serializes the given appl. The function name of the appl can be serialized in chunks.
	 * 
	 * @see openllet.aterm.ATermFwdVoid#voidVisitAppl(ATermAppl)
	 */
	@Override
	public void voidVisitAppl(final ATermAppl arg)
	{
		if (_indexInTerm == 0)
		{
			byte header = getHeader(arg);

			final AFun fun = arg.getAFun();
			final Integer key = _applSignatures.get(fun);
			if (key == null)
			{
				if (arg.isQuoted())
					header = (byte) (header | APPLQUOTED);
				_currentBuffer.put(header);

				writeInt(arg.getArity());

				final String name = fun.getName();
				final byte[] nameBytes = name.getBytes();
				final int length = nameBytes.length;
				writeInt(length);

				int endIndex = length;
				final int remaining = _currentBuffer.remaining();
				if (remaining < endIndex)
					endIndex = remaining;

				_currentBuffer.put(nameBytes, 0, endIndex);

				if (endIndex != length)
				{
					_indexInTerm = endIndex;
					_tempNameWriteBuffer = nameBytes;
				}

				_applSignatures.put(fun, new Integer(_sigKey++));
			}
			else
			{
				header = (byte) (header | ISFUNSHARED);
				_currentBuffer.put(header);

				writeInt(key.intValue());
			}
		}
		else
		{
			final int length = _tempNameWriteBuffer.length;

			int endIndex = length;
			final int remaining = _currentBuffer.remaining();
			if ((_indexInTerm + remaining) < endIndex)
				endIndex = (_indexInTerm + remaining);

			_currentBuffer.put(_tempNameWriteBuffer, _indexInTerm, (endIndex - _indexInTerm));
			_indexInTerm = endIndex;

			if (_indexInTerm == length)
			{
				_indexInTerm = 0;
				_tempNameWriteBuffer = null;
			}
		}
	}

	/**
	 * Serializes the given blob. A blob can be serialized in chunks.
	 * 
	 * @see openllet.aterm.ATermFwdVoid#voidVisitBlob(ATermBlob)
	 */
	@Override
	public void voidVisitBlob(final ATermBlob arg)
	{
		final int size = arg.getBlobSize();
		if (_indexInTerm == 0)
		{
			_currentBuffer.put(getHeader(arg));

			writeInt(size);
		}

		final byte[] blobBytes = arg.getBlobData();

		int bytesToWrite = size - _indexInTerm;
		final int remaining = _currentBuffer.remaining();
		if (remaining < bytesToWrite)
		{
			bytesToWrite = remaining;
		}

		_currentBuffer.put(blobBytes, _indexInTerm, bytesToWrite);
		_indexInTerm += bytesToWrite;

		if (_indexInTerm == size)
			_indexInTerm = 0;
	}

	/**
	 * Serializes the given int. Ints will always be serialized in one piece.
	 * 
	 * @see openllet.aterm.ATermFwdVoid#voidVisitInt(ATermInt)
	 */
	@Override
	public void voidVisitInt(final ATermInt arg)
	{
		_currentBuffer.put(getHeader(arg));

		writeInt(arg.getInt());
	}

	/**
	 * Serializes the given long.
	 * 
	 * @see openllet.aterm.ATermFwdVoid#voidVisitLong(ATermLong)
	 */
	@Override
	public void voidVisitLong(final ATermLong arg)
	{
		_currentBuffer.put(getHeader(arg));

		writeLong(arg.getLong());
	}

	/**
	 * Serializes the given list. List information will always be serialized in one piece.
	 * 
	 * @see openllet.aterm.ATermFwdVoid#voidVisitList(ATermList)
	 */
	@Override
	public void voidVisitList(final ATermList arg)
	{
		final byte header = getHeader(arg);
		_currentBuffer.put(header);

		writeInt(arg.getLength());
	}

	/**
	 * Serializes the given placeholder. Placeholders will always be serialized in one piece.
	 * 
	 * @see openllet.aterm.ATermFwdVoid#voidVisitPlaceholder(ATermPlaceholder)
	 */
	@Override
	public void voidVisitPlaceholder(final ATermPlaceholder arg)
	{
		_currentBuffer.put(getHeader(arg));

		// Do nothing, serializing its header is enough.
	}

	/**
	 * Serializes the given real. Reals will always be serialized in one peice.
	 * 
	 * @see openllet.aterm.ATermFwdVoid#voidVisitReal(ATermReal)
	 */
	@Override
	public void voidVisitReal(final ATermReal arg)
	{
		_currentBuffer.put(getHeader(arg));

		writeDouble(arg.getReal());
	}

	private final static int SEVENBITS = 0x0000007f;
	private final static int SIGNBIT = 0x00000080;
	private final static int LONGBITS = 8;

	/**
	 * Splits the given integer in separate bytes and writes it to the buffer. It will occupy the
	 * smallest amount of bytes possible. This is done in the following way: the sign bit will be
	 * used to indicate that more bytes coming, if this is set to 0 we know we are done. Since we
	 * are mostly writing small values, this will save a considerable amount of space. On the other
	 * hand a large number will occupy 5 bytes instead of the regular 4.
	 * 
	 * @param value
	 *            The integer that needs to be split and written.
	 */
	private void writeInt(final int value)
	{
		final int intValue = value;

		if ((intValue & 0xffffff80) == 0)
		{
			_currentBuffer.put((byte) (intValue & SEVENBITS));
			return;
		}
		_currentBuffer.put((byte) ((intValue & SEVENBITS) | SIGNBIT));

		if ((intValue & 0xffffc000) == 0)
		{
			_currentBuffer.put((byte) ((intValue >>> 7) & SEVENBITS));
			return;
		}
		_currentBuffer.put((byte) (((intValue >>> 7) & SEVENBITS) | SIGNBIT));

		if ((intValue & 0xffe00000) == 0)
		{
			_currentBuffer.put((byte) ((intValue >>> 14) & SEVENBITS));
			return;
		}
		_currentBuffer.put((byte) (((intValue >>> 14) & SEVENBITS) | SIGNBIT));

		if ((intValue & 0xf0000000) == 0)
		{
			_currentBuffer.put((byte) ((intValue >>> 21) & SEVENBITS));
			return;
		}
		_currentBuffer.put((byte) (((intValue >>> 21) & SEVENBITS) | SIGNBIT));

		_currentBuffer.put((byte) ((intValue >>> 28) & SEVENBITS));
	}

	/**
	 * Splits the given double in separate bytes and writes it to the buffer. Doubles will always
	 * occupy 8 bytes, since the convertion of a floating point number to a long will always cause
	 * the high order bits to be occupied.
	 * 
	 * @param value
	 *            The integer that needs to be split and written.
	 */
	private void writeDouble(final double value)
	{
		final long longValue = Double.doubleToLongBits(value);
		writeLong(longValue);
	}

	private void writeLong(final long value)
	{
		for (int i = 0; i < LONGBITS; i++)
		{
			_currentBuffer.put((byte) (value >>> (i * 8)));
		}
	}

	/**
	 * Writes the given openllet.aterm to the given file. Blocks of 65536 bytes will be used.
	 * 
	 * @param aTerm
	 *            The ATerm that needs to be writen to file.
	 * @param file
	 *            The file to write to.
	 * @throws IOException
	 *             Thrown when an error occurs while writing to the given file.
	 */
	public static void writeTermToSAFFile(final ATerm aTerm, final File file) throws IOException
	{
		final BinaryWriter binaryWriter = new BinaryWriter(aTerm);

		final ByteBuffer byteBuffer = ByteBuffer.allocate(65536);
		final ByteBuffer sizeBuffer = ByteBuffer.allocate(2);

		try (FileOutputStream fos = new FileOutputStream(file))
		{
			try (FileChannel fc = fos.getChannel())
			{

				byteBuffer.put((byte) '?');
				byteBuffer.flip();
				fc.write(byteBuffer);

				do
				{
					byteBuffer.clear();
					binaryWriter.serialize(byteBuffer);

					final int blockSize = byteBuffer.limit();
					sizeBuffer.clear();
					sizeBuffer.put((byte) (blockSize & 0x000000ff));
					sizeBuffer.put((byte) ((blockSize >>> 8) & 0x000000ff));
					sizeBuffer.flip();

					fc.write(sizeBuffer);

					fc.write(byteBuffer);
				} while (!binaryWriter.isFinished());
			}
		}
	}

	/**
	 * Writes the given openllet.aterm to a byte array. The reason this method returns a byte array instead
	 * of a Java String is because otherwise the binary data would be corrupted, since it would be
	 * automatically encoded in UTF-16 format (which would completely mess everything up). Blocks
	 * of 65536 bytes will be used.
	 * 
	 * @param aTerm
	 *            The ATerm that needs to be written to a byte array.
	 * @return The serialized representation of the given ATerm contained in a byte array.
	 */
	public static byte[] writeTermToSAFString(final ATerm aTerm)
	{
		final List<ByteBuffer> buffers = new ArrayList<>();
		int totalBytesWritten = 0;

		final BinaryWriter binaryWriter = new BinaryWriter(aTerm);
		do
		{
			final ByteBuffer byteBuffer = ByteBuffer.allocate(65536);
			binaryWriter.serialize(byteBuffer);

			buffers.add(byteBuffer);
			totalBytesWritten += byteBuffer.limit() + 2; // Increment by: buffer size + 2 bytes length spec.
		} while (!binaryWriter.isFinished());

		final byte[] data = new byte[totalBytesWritten];
		int position = 0;
		final int numberOfBuffers = buffers.size();
		for (int i = 0; i < numberOfBuffers; i++)
		{
			final ByteBuffer buffer = buffers.get(i);
			final int blockSize = buffer.limit();
			data[position++] = (byte) (blockSize & 0x000000ff);
			data[position++] = (byte) ((blockSize >>> 8) & 0x000000ff);

			System.arraycopy(buffer.array(), 0, data, position, blockSize);
			position += blockSize;
		}

		return data;
	}
}
