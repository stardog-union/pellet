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

package openllet.aterm.stream;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;
import java.util.logging.Logger;
import openllet.shared.tools.Log;

/**
 * This is an unsynchronized buffered outputstream writer. By using this you can
 * bypass most of the (unnecessary) synchronization and method calls that occur
 * in its standard library equivalent. Data will be written into the underlaying
 * _stream using the system's default character encoding.
 *
 * @author Arnold Lankamp
 */
public class BufferedOutputStreamWriter extends Writer
{
	public final static Logger _logger = Log.getLogger(BufferedOutputStreamWriter.class);

	private final static int DEFAULTBUFFERSIZE = 8192;

	private final OutputStream _stream;

	private byte[] _buffer = null;
	private int _bufferPos = 0;
	private int _limit = 0;

	private boolean _failures = false;

	/**
	 * Contructor.
	 *
	 * @param _stream
	 *            The _stream to write too.
	 */
	public BufferedOutputStreamWriter(final OutputStream stream)
	{
		this(stream, DEFAULTBUFFERSIZE);
	}

	/**
	 * Constructor.
	 *
	 * @param _stream
	 *            The _stream to write too.
	 * @param bufferSize
	 *            The size of the interal _buffer.
	 */
	public BufferedOutputStreamWriter(final OutputStream stream, final int bufferSize)
	{
		super();
		_stream = stream;
		_buffer = new byte[bufferSize];
		_limit = _buffer.length;
	}

	/**
	 * Writes a single character.
	 *
	 * @param c
	 *            The character to write.
	 */
	public void write(final char c)
	{
		_buffer[_bufferPos++] = (byte) c;

		if (_bufferPos == _limit)
			flush();
	}

	/**
	 * Bulk write function.
	 *
	 * @see Writer#write(char[], int, int)
	 */
	@Override
	public void write(final char[] cbuf, final int offset, final int length)
	{
		write(new String(cbuf, offset, length));
	}

	/**
	 * Bulk write function, specificly meant for strings.
	 *
	 * @see Writer#write(java.lang.String)
	 */
	@Override
	public void write(final String s)
	{
		final byte[] bytes = s.getBytes();

		int bytesLeft = bytes.length;
		int startPos = 0;
		while (bytesLeft > 0)
		{
			int bytesToWrite = bytesLeft;
			final int freeSpace = _limit - _bufferPos;
			if (freeSpace < bytesToWrite)
				bytesToWrite = freeSpace;

			System.arraycopy(bytes, startPos, _buffer, _bufferPos, bytesToWrite);
			_bufferPos += bytesToWrite;

			if (_bufferPos == _limit)
				flush();

			bytesLeft -= bytesToWrite;
			startPos += bytesToWrite;
		}
	}

	/**
	 * Forces the writing of all buffered data.
	 *
	 * @see Writer#flush()
	 */
	@Override
	public void flush()
	{
		try
		{
			_stream.write(_buffer, 0, _bufferPos);
			_bufferPos = 0;
			_stream.flush();
		}
		catch (final IOException ioex)
		{
			_failures = true;
			Log.error(_logger, ioex);
		}
	}

	/**
	 * Closes this writer and its underlaying _stream.
	 */
	@Override
	public void close()
	{
		try
		{
			flush();
			_stream.close();
		}
		catch (final IOException ioex)
		{
			_failures = true;
			Log.error(_logger, ioex);
		}
	}

	/**
	 * Returns whether or not an error occured during operation of this writer.
	 *
	 * @return True if a error occured, false otherwise.
	 */
	public boolean hasFailed()
	{
		return _failures;
	}
}
