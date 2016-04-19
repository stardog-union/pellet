// Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package com.clarkparsia.pellint.rdfxml;

import com.clarkparsia.pellint.util.CollectionUtil;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import org.apache.jena.rdf.model.Statement;

/**
 * <p>
 * Title:
 * </p>
 * <p>
 * Description:
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
public class RDFLints
{
	private final Map<String, List<String>> m_Report;
	private final List<Statement> m_MissingStatements;

	public RDFLints()
	{
		m_Report = new LinkedHashMap<String, List<String>>();
		m_MissingStatements = CollectionUtil.makeList();
	}

	public void add(final String category, final List<String> msgs)
	{
		if (!msgs.isEmpty())
			m_Report.put(category, msgs);
	}

	public void addMissingStatements(final List<Statement> stmts)
	{
		m_MissingStatements.addAll(stmts);
	}

	public List<Statement> getMissingStatements()
	{
		return m_MissingStatements;
	}

	public boolean isEmpty()
	{
		return m_Report.isEmpty();
	}

	@Override
	public String toString()
	{
		if (m_Report.isEmpty())
			return "No RDF lints found.";

		final StringBuilder builder = new StringBuilder();

		for (final Entry<String, List<String>> entry : m_Report.entrySet())
		{
			final String category = entry.getKey();
			final List<String> msgs = entry.getValue();
			if (!msgs.isEmpty())
			{
				builder.append("[").append(category).append("]\n");

				for (final String msg : msgs)
					builder.append("- ").append(msg).append("\n");

				builder.append("\n");
			}
		}
		return builder.toString();
	}
}
