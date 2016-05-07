// Portions Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// Clark & Parsia, LLC parts of this source code are available under the terms of the Affero General Public License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com
//
// ---
// Portions Copyright (c) 2003 Ron Alford, Mike Grove, Bijan Parsia, Evren Sirin
// Alford, Grove, Parsia, Sirin parts of this source code are available under the terms of the MIT License.
//
// The MIT License
//
// Permission is hereby granted, free of charge, to any person obtaining a copy
// of this software and associated documentation files (the "Software"), to
// deal in the Software without restriction, including without limitation the
// rights to use, copy, modify, merge, publish, distribute, sublicense, and/or
// sell copies of the Software, and to permit persons to whom the Software is
// furnished to do so, subject to the following conditions:
//
// The above copyright notice and this permission notice shall be included in
// all copies or substantial portions of the Software.
//
// THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
// IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
// FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
// AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
// LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
// FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS
// IN THE SOFTWARE.

package org.mindswap.pellet.output;

import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

/**
 * Create a table data structure that has a list of column names and list of data rows. The only function of this class is to print the data in a table format.
 * Data can be given at once by the constructor or can be added incrementally with the addRow function.
 *
 * @author Evren Sirin
 */
public class TableData
{
	Collection<Object> _data;

	List<?> _colNames;

	boolean[] _rightAligned;

	int _colWidths[] = null;

	String _colSep = " | ";

	public TableData(final Collection<Object> data, final List<?> colNames)
	{
		this._data = data;
		this._colNames = colNames;

		final int cols = colNames.size();
		_colWidths = new int[cols];
		_rightAligned = new boolean[cols];
	}

	public TableData(final List<?> colNames)
	{
		this._data = new ArrayList<>();
		this._colNames = colNames;

		final int cols = colNames.size();
		_colWidths = new int[cols];
		_rightAligned = new boolean[cols];
	}

	public TableData(final String[] colNames)
	{
		this(Arrays.asList(colNames));
	}

	public void setAlignment(final boolean[] rightAligned)
	{
		if (rightAligned.length != _colNames.size())
			throw new IllegalArgumentException("Alignment has " + rightAligned.length + " elements but table has " + _colNames.size() + " columns");

		this._rightAligned = rightAligned;
	}

	public void setrightAligned(final int colIndex, final boolean rightAligned)
	{
		this._rightAligned[colIndex] = rightAligned;
	}

	/**
	 * @deprecated Use {@link add(List)} instead
	 */
	@Deprecated
	public void addRow(final List<?> row)
	{
		add(row);
	}

	public void add(final List<?> row)
	{
		if (row.size() != _colNames.size())
			throw new IllegalArgumentException("Row has " + row.size() + " elements but table has " + _colNames.size() + " columns");

		_data.add(row);
	}

	public void print(final OutputStream writer)
	{
		print(new PrintWriter(writer));
	}

	public void print(final PrintWriter out)
	{
		printText(out);
	}

	public void print(final Writer writer)
	{
		printText(writer);
	}

	private void printText(final Writer writer)
	{
		final PrintWriter pw = (writer instanceof PrintWriter) ? (PrintWriter) writer : new PrintWriter(writer);

		computeHeaderWidths();
		computeRowWidths();

		final int lineWidth = computeLineWidth();
		final int numCols = _colNames.size();

		final String[] row = new String[numCols];
		for (int col = 0; col < row.length; col++)
			row[col] = _colNames.get(col).toString();
		printRow(pw, row);

		for (int i = 0; i < lineWidth; i++)
			pw.print('=');
		pw.println();

		for (final Iterator<Object> i = _data.iterator(); i.hasNext();)
		{
			final Collection<?> rowData = (Collection<?>) i.next();

			final Iterator<?> j = rowData.iterator();
			for (int col = 0; j.hasNext(); col++)
			{
				final Object value = j.next();
				row[col] = value == null ? "<null>" : value.toString();
			}
			printRow(pw, row);
		}

		pw.flush();
	}

	private void printRow(final PrintWriter pw, final String[] row)
	{
		for (int col = 0; col < row.length; col++)
		{
			final String s = row[col];
			final int pad = _colWidths[col];
			final StringBuffer sbuff = new StringBuffer(120);

			if (col > 0)
				sbuff.append(_colSep);

			if (!_rightAligned[col])
				sbuff.append(s);

			for (int j = 0; j < pad - s.length(); j++)
				sbuff.append(' ');

			if (_rightAligned[col])
				sbuff.append(s);

			pw.print(sbuff);
		}
		pw.println();
	}

	private int computeLineWidth()
	{
		final int numCols = _colWidths.length;
		int lineWidth = 0;
		for (int i = 0; i < numCols; i++)
			lineWidth += _colWidths[i];

		lineWidth += (numCols - 1) * _colSep.length();

		return lineWidth;
	}

	private void computeHeaderWidths()
	{
		final Iterator<?> k = _colNames.iterator();
		for (int col = 0; k.hasNext(); col++)
		{
			final Object value = k.next();
			final String str = (value == null) ? "<null>" : value.toString();
			_colWidths[col] = str.length();
		}
	}

	private void computeRowWidths()
	{
		for (final Iterator<Object> i = _data.iterator(); i.hasNext();)
		{
			final Collection<?> rowData = (Collection<?>) i.next();

			final Iterator<?> j = rowData.iterator();
			for (int col = 0; j.hasNext(); col++)
			{
				final Object value = j.next();
				final String str = (value == null) ? "<null>" : value.toString();

				if (_colWidths[col] < str.length())
					_colWidths[col] = str.length();
			}
		}
	}

	public int getRowCount()
	{
		return _data.size();
	}

	public int getColCount()
	{
		return _colNames.size();
	}

	public void sort(final String colName)
	{
		sort(_colNames.indexOf(colName));
	}

	@SuppressWarnings("unchecked")
	public <T> void sort(final int col)
	{
		final Object a[] = _data.toArray();
		Arrays.sort(a, (l1, l2) -> ((Comparable<T>) ((List<T>) l1).get(col)).compareTo(((List<T>) l2).get(col)));
		_data = Arrays.asList(a);
	}

	@SuppressWarnings("unchecked")
	public <T> void sort(final int col, final Comparator<T> c)
	{
		final Object a[] = _data.toArray();
		Arrays.sort(a, (l1, l2) -> c.compare(((List<T>) l1).get(col), ((List<T>) l2).get(col)));
		_data = Arrays.asList(a);
	}

	@Override
	public String toString()
	{
		final StringWriter sw = new StringWriter();
		printText(sw);

		return sw.toString();
	}
}
