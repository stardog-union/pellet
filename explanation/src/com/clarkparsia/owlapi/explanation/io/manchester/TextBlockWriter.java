// Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package com.clarkparsia.owlapi.explanation.io.manchester;

import java.io.Writer;
import java.util.ArrayList;

/**
 * <p>
 * Title:
 * </p>
 * <p>
 * Description: Concrete implementation of {@link BlockWriter} for purely
 * textual output like console output. It can probably be used for any kind of
 * output where monospaced font is used.
 * </p>
 * This implementation simply counts the number of characters printed on one
 * line and pads the next line with the same number of spaces.
 * <p>
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
public class TextBlockWriter extends BlockWriter {
	/**
	 * Number of spaces that need to be printed for each block
	 */
	private ArrayList<Integer>	blockColumns	= new ArrayList<Integer>();
	
	/**
	 * The current column (number of the characters printed) for the current
	 * line
	 */
	private int					column			= 0;
	
	/**
	 * @param out
	 */
	public TextBlockWriter(Writer out) {
		super( out, " " );
	}

	/**
	 * {@inheritDoc}
	 */
	protected void startNewLine() {
		if( newLine ) {
			newLine = false;

			if( !blockColumns.isEmpty() ) {
				int blockStart = blockColumns.get( blockColumns.size() - 1 );
				indent( blockStart );
				column = blockStart;
			}
			else {
				column = 0;
			}
		}
	}
	
	public void println() {
		super.println();
		
		column = 0;		
	}
	
	@Override
	public void printSpace() {
		super.print( " " );
	}
	
	/**
	 * Print given number of spaces.
	 * 
	 * @param count
	 */
	public void indent(int count) {
		for( int i = 0; i < count; i++ ) {
			print( pad );
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public void clearBlocks() {
		blockColumns.clear();
	}

	/**
	 * {@inheritDoc}
	 */
	public void startBlock() {
		// save the current column
		blockColumns.add( column );
	}

	/**
	 * {@inheritDoc}
	 */
	public void endBlock() {
		if( blockColumns.isEmpty() )
			throw new IllegalStateException( "No block to end!" );
		
		// remove the lastly column
		blockColumns.remove( blockColumns.size() - 1 );
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void write(char[] buf, int off, int len) {
		super.write( buf, off, len );
		
		column += len;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void write(int c) {
		super.write( c );
		
		column += 1;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void write(String s, int off, int len) {
		super.write( s, off, len );
		
		column += len;
	}
}
