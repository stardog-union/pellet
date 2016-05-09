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

package aterm.stream;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;

/**
 * This is an unsynchronized buffered outputstream writer. By using this you can
 * bypass most of the (unnecessary) synchronization and method calls that occur
 * in its standard library equivalent. Data will be written into the underlaying
 * stream using the system's default character encoding.
 * 
 * @author Arnold Lankamp
 */
public class BufferedOutputStreamWriter extends Writer
{
	private final static int DEFAULTBUFFERSIZE = 8192;

	private final OutputStream stream;

	private byte[] buffer = null;
	private int bufferPos = 0;
	private int limit = 0;

	private boolean failures = false;

	/**
	 * Contructor.
	 * 
	 * @param stream
	 *            The stream to write too.
	 */
	public BufferedOutputStreamWriter(OutputStream stream)
	{
		this(stream, DEFAULTBUFFERSIZE);
	}

	/**
	 * Constructor.
	 * 
	 * @param stream
	 *            The stream to write too.
	 * @param bufferSize
	 *            The size of the interal buffer.
	 */
	public BufferedOutputStreamWriter(OutputStream stream, int bufferSize)
	{
		super();

		this.stream = stream;

		buffer = new byte[bufferSize];
		limit = buffer.length;
	}

	/**
	 * Writes a single character.
	 * 
	 * @param c
	 *            The character to write.
	 */
	public void write(char c)
	{
		buffer[bufferPos++] = (byte) c;

		if (bufferPos == limit)
			flush();
	}

	/**
	 * Bulk write function.
	 * 
	 * @see Writer#write(char[], int, int)
	 */
	@Override
	public void write(char[] cbuf, int offset, int length)
	{
		write(new String(cbuf, offset, length));
	}

	/**
	 * Bulk write function, specificly meant for strings.
	 * 
	 * @see Writer#write(java.lang.String)
	 */
	@Override
	public void write(String s)
	{
		final byte[] bytes = s.getBytes();

		int bytesLeft = bytes.length;
		int startPos = 0;
		while (bytesLeft > 0)
		{
			int bytesToWrite = bytesLeft;
			final int freeSpace = limit - bufferPos;
			if (freeSpace < bytesToWrite)
				bytesToWrite = freeSpace;

			System.arraycopy(bytes, startPos, buffer, bufferPos, bytesToWrite);
			bufferPos += bytesToWrite;

			if (bufferPos == limit)
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
			stream.write(buffer, 0, bufferPos);
			bufferPos = 0;
			stream.flush();
		}
		catch (final IOException ioex)
		{
			failures = true;
		}
	}

	/**
	 * Closes this writer and its underlaying stream.
	 */
	@Override
	public void close()
	{
		try
		{
			flush();
			stream.close();
		}
		catch (final IOException ioex)
		{
			failures = true;
		}
	}

	/**
	 * Returns whether or not an error occured during operation of this writer.
	 * 
	 * @return True if a error occured, false otherwise.
	 */
	public boolean hasFailed()
	{
		return failures;
	}
}
