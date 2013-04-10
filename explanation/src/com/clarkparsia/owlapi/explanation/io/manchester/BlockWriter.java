// Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package com.clarkparsia.owlapi.explanation.io.manchester;

import java.io.PrintWriter;
import java.io.Writer;

/**
 * <p>
 * Title:
 * </p>
 * <p>
 * Description: A convenience class that wraps a PrinWriter instance to provide
 * support for generating nicely aligned attribute. This class is geared towards
 * textual outputs and is used for aligning the outputs in consecutive lines.
 * </p>
 * <p>
 * The idea is to create virtual blocks of output in a line (i.e. invisible
 * tabbing points) and when a new line is printed tab the output with spaces (or
 * some similar invisible character) to the last tabbing points. Alignment
 * blocks are created in a LIFO fashion that is and you can only align w.r.t.
 * latest block.
 * </p>
 * <p>
 * This class is an abstract class to provide some general functionality that
 * can be reused in implementations that supports purely textual output or HTML
 * output.
 * </p>
 * <p>
 * Copyright: Copyright (c) 2008
 * </p>
 * <p>
 * Company: Clark & Parsia, LLC. <http://www.clarkparsia.com>
 * </p>
 * 
 * @author Evren Sirin
 */
public abstract class BlockWriter extends PrintWriter {
	protected String	pad		= null;
	protected boolean	newLine	= true;

	/**
	 * @param out
	 */
	public BlockWriter(Writer out, String pad) {
		super( out );
		
		this.pad = pad;
	}

	/**
	 * Do the preprocessing step required at the beginning of each line for
	 * alignment;
	 */
	protected abstract void startNewLine();

	/**
	 * Clear all the blocks previously defined.
	 */
	public abstract void clearBlocks();

	/**
	 * Start a new block for alignment. This will mark the current location in
	 * the current line as the point to be used for alignment next time a new
	 * line is printed. Previously defined alignment blocks will be inaccessible
	 * until this block is closed with a {@link #endBlock()} call. 
	 */
	public abstract void startBlock();

	/**
	 * Ends the current alignment block. All subsequent lines will be aligned
	 * w.r.t. the previous block or not at all if there was no previous bloack.
	 * 
	 * @throws IllegalStateException
	 *             if there is no block previously created with a
	 *             {@link #startBlock()} call.
	 */
	public abstract void endBlock() throws IllegalStateException;	

	/**
	 *{@inheritDoc}
	 */
	@Override
	public void println() {
		// print the new line
		super.println();

		// set the flag to indicate we will need the alignment preprocessing
		// next time something is printed
		newLine = true;
	}
	
	/**
	 *{@inheritDoc}
	 */
	@Override
	public void write(int c) {
		// do the preprocessing
		startNewLine();

		// do the write
		super.write( c );
	}

	/**
	 *{@inheritDoc}
	 */
	@Override
	public void write(char[] buf, int off, int len) {
		// do the preprocessing
		startNewLine();

		// do the write
		super.write( buf, off, len );		
	}

	/**
	 *{@inheritDoc}
	 */
	@Override
	public void write(String s, int off, int len) {
		// do the preprocessing
		startNewLine();

		// do the write
		super.write( s, off, len );
	}

	public abstract void printSpace();
}
