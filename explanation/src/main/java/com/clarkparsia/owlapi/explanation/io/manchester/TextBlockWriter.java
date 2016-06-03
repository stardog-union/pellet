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
 * Description: Concrete implementation of {@link BlockWriter} for purely textual output like console output. It can probably be used for any kind of output
 * where monospaced font is used.
 * </p>
 * This implementation simply counts the number of characters printed on one line and pads the next line with the same number of spaces.
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
public class TextBlockWriter extends BlockWriter
{
	/**
	 * Number of spaces that need to be printed for each block
	 */
	private final ArrayList<Integer> _blockColumns = new ArrayList<>();

	/**
	 * The _current _column (number of the characters printed) for the _current line
	 */
	private int _column = 0;

	/**
	 * @param out
	 */
	public TextBlockWriter(final Writer outWriter)
	{
		super(outWriter, " ");
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void startNewLine()
	{
		if (_newLine)
		{
			_newLine = false;

			if (!_blockColumns.isEmpty())
			{
				final int blockStart = _blockColumns.get(_blockColumns.size() - 1);
				indent(blockStart);
				_column = blockStart;
			}
			else
				_column = 0;
		}
	}

	@Override
	public void println()
	{
		super.println();

		_column = 0;
	}

	@Override
	public void printSpace()
	{
		super.print(" ");
	}

	/**
	 * Print given number of spaces.
	 *
	 * @param count
	 */
	public void indent(final int count)
	{
		for (int i = 0; i < count; i++)
			print(_pad);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void clearBlocks()
	{
		_blockColumns.clear();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void startBlock()
	{
		// save the _current _column
		_blockColumns.add(_column);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void endBlock()
	{
		if (_blockColumns.isEmpty())
			throw new IllegalStateException("No block to _end!");

		// remove the lastly _column
		_blockColumns.remove(_blockColumns.size() - 1);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void write(final char[] buf, final int off, final int len)
	{
		super.write(buf, off, len);

		_column += len;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void write(final int c)
	{
		super.write(c);

		_column += 1;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void write(final String s, final int off, final int len)
	{
		super.write(s, off, len);

		_column += len;
	}
}
