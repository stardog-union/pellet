// Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package com.clarkparsia.pellint.model;

import java.text.DecimalFormat;

/**
 * <p>
 * Title: Severity
 * </p>
 * <p>
 * Description: The severity for a {@link com.clarkparsia.pellint.model.Lint}
 * relative to all the {@link com.clarkparsia.pellint.model.Lint} found within a particular {@link com.clarkparsia.pellint.lintpattern.LintPattern}.
 * It currently wraps Double, and a higer value represents higher severity.
 * </p>
 * <p>
 * Copyright: Copyright (c) 2008
 * </p>
 * <p>
 * Company: Clark & Parsia, LLC. <http://www.clarkparsia.com>
 * </p>
 * 
 * @author Harris Lin
 */
public class Severity implements Comparable<Severity> {
	private static final DecimalFormat FORMAT_EXP = new DecimalFormat("0.00E0");
	private static final DecimalFormat FORMAT_NORMAL = new DecimalFormat("0");
	private static final int STRING_LENGTH = 6;
	
	private Double m_Value;
	
	public Severity(double v) {
		m_Value = v;
	}

	@Override
	public boolean equals(Object o) {
		return o instanceof Severity && ((Severity)o).doubleValue() == doubleValue();
	}

	@Override
	public int hashCode() {
		return m_Value.hashCode();
	}
	
	public double doubleValue() {
		return m_Value;
	}

	@Override
	public String toString() {
		StringBuilder strBuilder = new StringBuilder();
		strBuilder.append('[');
		
		String value = (m_Value >= Math.pow(10, STRING_LENGTH) - 0.5) ? FORMAT_EXP.format(m_Value)
																	  : FORMAT_NORMAL.format(m_Value);

		for (int i = STRING_LENGTH - value.length(); i > 0 ; i--) {
			strBuilder.append(' ');
		}
		strBuilder.append(value).append(']');
		return strBuilder.toString();
	}

	public int compareTo(Severity other) {
		return m_Value.compareTo(other.m_Value);
	}
}
