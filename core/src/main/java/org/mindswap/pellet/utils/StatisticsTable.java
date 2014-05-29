// Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package org.mindswap.pellet.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Logger;

public class StatisticsTable<ROW, COL> {

	private static final Logger log = Logger.getLogger(StatisticsTable.class.getName());

	private Map<COL, Map<ROW, Number>> statistics = new HashMap<COL, Map<ROW, Number>>();

	private List<COL> cols = new ArrayList<COL>();
	private List<ROW> rows = new ArrayList<ROW>();

	private int firstColumnSize = 10;

	public void add(final ROW row, final COL col, final Number stat) {
		Map<ROW, Number> getCol = statistics.get(col);

		if (getCol == null) {
			getCol = new HashMap<ROW, Number>();
			statistics.put(col, getCol);
			cols.add(col);
		}

		Number getStat = getCol.get(row);

		if (getStat != null) {
			log.warning("Overwriting [" + row + " : " + col + "].");
		} else {
			if (!rows.contains(row)) {
				if (firstColumnSize < row.toString().length()) {
					firstColumnSize = row.toString().length();
				}
				rows.add(row);
			}
		}

		getCol.put(row, stat);
	}

	public void add(final COL col, final Map<ROW, ? extends Number> stat) {
		for (Entry <ROW, ? extends Number> entry : stat.entrySet() ) {
			add( entry.getKey(), col, entry.getValue());
		}
	}

	@Override
	public String toString() {
		String s = "";
		List<Integer> colSizes = new ArrayList<Integer>();

		for (COL col : cols) {
			colSizes.add(col.toString().length() + 2);
		}

		// format of first column
		String firstCol = "| %1$-" + (firstColumnSize + 2) + "s ";

		// format of one line
		StringBuffer lineFormat = new StringBuffer();

		for (int i = 1; i < colSizes.size() + 1; i++) {
			lineFormat.append( "| %" ).append( i ).append( "$-10.10s " );
		}

		lineFormat.append( "|\n" );

		// separator
		final char[] a = new char[String.format(lineFormat.toString(), cols.toArray())
				.length()
				+ String.format(firstCol, "").length()];

		Arrays.fill(a, '=');
		final String separator = new String(a);

		s += separator + "\n";
		s += String.format(firstCol, "")
				+ String.format(lineFormat.toString(), cols.toArray());
		s += separator + "\n";

		for (final ROW row : rows) {
			final List<Number> rowData = new ArrayList<Number>();
			for (final COL col : cols) {
				final Map<ROW, Number> map = statistics.get(col);
				Number stat = map.get(row);

				if (stat == null) {
					rowData.add(Double.POSITIVE_INFINITY);
				} else {
					rowData.add(stat);
				}
			}

			String rowName;

			// // TODO
			// try {
			// rowName = URI.create(row.toString()).getFragment();
			// } catch (Exception e) {
			rowName = row.toString();
			// }

			s += String.format(firstCol, new Object[] { rowName })
					+ String.format(lineFormat.toString(), rowData.toArray());
		}

		s += separator + "\n";

		return s;
	}
}
