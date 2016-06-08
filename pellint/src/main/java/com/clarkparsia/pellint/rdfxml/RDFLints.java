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
	private final Map<String, List<String>> _report;
	private final List<Statement> _missingStatements;

	public RDFLints()
	{
		_report = new LinkedHashMap<>();
		_missingStatements = CollectionUtil.makeList();
	}

	public void add(final String category, final List<String> msgs)
	{
		if (!msgs.isEmpty())
			_report.put(category, msgs);
	}

	public void addMissingStatements(final List<Statement> stmts)
	{
		_missingStatements.addAll(stmts);
	}

	public List<Statement> getMissingStatements()
	{
		return _missingStatements;
	}

	public boolean isEmpty()
	{
		return _report.isEmpty();
	}

	@Override
	public String toString()
	{
		if (_report.isEmpty())
			return "No RDF lints found.";

		final StringBuilder builder = new StringBuilder();

		for (final Entry<String, List<String>> entry : _report.entrySet())
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
