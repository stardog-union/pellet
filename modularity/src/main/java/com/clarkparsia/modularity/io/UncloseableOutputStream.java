// Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package com.clarkparsia.modularity.io;

import java.io.IOException;
import java.io.OutputStream;

/**
 * <p>
 * Title:
 * </p>
 * <p>
 * Description:
 * </p>
 * <p>
 * Copyright: Copyright (c) 2009
 * </p>
 * <p>
 * Company: Clark & Parsia, LLC. <http://www.clarkparsia.com>
 * </p>
 * 
 * A wrapper around the underlying stream that prevents the underlying stream from being closed (if passed to other methods that
 * are not very nice and close your stream). The initial motivation for this wrapper was ZipOutputStream where you write into multiple separate entries; 
 * some methods (which are not aware that they write to a ZipOutputStream) when they finish writing their data they close the stream, which terminates the whole
 * process of producing the zip file.  
 *
 * @author Blazej Bulka
 */
public class UncloseableOutputStream extends OutputStream {

	/**
	 * The underlying wrapped stream.
	 */
	private OutputStream stream;
	
	/**
	 * Wraps an output stream with a wrapper that does not allow the underlying stream to be closed.
	 * @param stream the underlying stream to be protected from closing.
	 */
	public UncloseableOutputStream( OutputStream stream ) {
		this.stream = stream;
	}

	/**
	 * Captures the close request, and does NOT forward it to the underlying stream.
	 */
	@Override
	public void close() {
		// nothing -- the whole purpose of this class
	}

	/**
	 * Forwards the call to the underlying stream.
	 */
	@Override
	public void flush() throws IOException {
		stream.flush();
	}
	
	/**
	 * Forwards the call to the underlying stream.
	 */
	@Override
	public void write( byte[] b ) throws IOException {
		stream.write( b );
	}
	
	/**
	 * Forwards the call to the underlying stream.
	 */
	@Override
	public void write( byte[] b, int off, int len ) throws IOException {
		stream.write( b, off, len );
	}

	/**
	 * Forwards the call to the underlying stream.
	 */
	@Override
	public void write( int b ) throws IOException {
		stream.write( b );
	}
	
	/**
	 * Gets the underlying stream. This method may be useful to actually close the underlying stream, when all the calls are finished.
	 * 
	 * @return the underlying stream.
	 */
	public OutputStream getUnderlyingStream() {
		return stream;
	}
}
