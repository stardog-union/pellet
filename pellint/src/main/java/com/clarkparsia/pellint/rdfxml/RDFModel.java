// Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package com.clarkparsia.pellint.rdfxml;

import com.clarkparsia.pellint.util.CollectionUtil;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
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
public class RDFModel
{
	private final List<String> _comments;
	private final Map<String, String> _namespaces;
	private final List<Statement> _allStatements;
	private final Map<Resource, Map<Property, Set<RDFNode>>> _statements;
	private final Map<Property, List<Statement>> _statementsByPredicate;
	private final Map<RDFNode, List<Statement>> _statementsByObject;
	private final Set<RDFNode> _BNodes;

	public RDFModel()
	{
		_comments = CollectionUtil.makeList();
		_namespaces = CollectionUtil.makeMap();
		_allStatements = CollectionUtil.makeList();
		_statements = CollectionUtil.makeMap();
		_statementsByPredicate = CollectionUtil.makeMap();
		_statementsByObject = CollectionUtil.makeMap();
		_BNodes = CollectionUtil.makeSet();
	}

	public void add(final RDFModel other)
	{
		_comments.addAll(other._comments);
		_namespaces.putAll(other._namespaces);
		addAllStatements(other.getStatements());
		_BNodes.addAll(other._BNodes);
	}

	public void add(final Model jenaModel)
	{
		addAllStatements(jenaModel.listStatements());
	}

	public void addComment(final String comment)
	{
		_comments.add(comment);
	}

	public List<String> getComments()
	{
		return _comments;
	}

	public void addNamespace(final String prefix, final String uri)
	{
		_namespaces.put(prefix, uri);
	}

	public Map<String, String> getNamespaces()
	{
		return _namespaces;
	}

	public void addAllStatements(final Iterator<Statement> stmts)
	{
		while (stmts.hasNext())
			addStatement(stmts.next());
	}

	public void addAllStatements(final List<Statement> stmts)
	{
		addAllStatements(stmts.iterator());
	}

	public void addAllStatementsWithExistingBNodesOnly(final List<Statement> stmts)
	{
		for (final Statement stmt : stmts)
		{
			final Resource s = stmt.getSubject();
			if (s.isAnon() && !_BNodes.contains(s))
				continue;

			final RDFNode o = stmt.getObject();
			if (o.isAnon() && !_BNodes.contains(o))
				continue;

			addStatement(stmt);
		}
	}

	public void addStatement(final Statement stmt)
	{
		_allStatements.add(stmt);
		addToStatements(stmt);
		addToStatementsByPredicate(stmt);
		addToStatementsByObject(stmt);
		addToBNodes(stmt.getSubject());
		addToBNodes(stmt.getObject());
	}

	private void addToBNodes(final RDFNode v)
	{
		if (v.isAnon())
			_BNodes.add(v);
	}

	public List<Statement> getStatements()
	{
		return _allStatements;
	}

	public Collection<Statement> getStatementsByPredicate(final Property predicate)
	{
		final List<Statement> list = _statementsByPredicate.get(predicate);
		if (list == null)
			return Collections.emptyList();
		else
			return list;
	}

	public Collection<Statement> getStatementsByObject(final RDFNode object)
	{
		final List<Statement> list = _statementsByObject.get(object);
		if (list == null)
			return Collections.emptyList();
		else
			return list;
	}

	public Collection<RDFNode> getValues(final Resource subject, final Property predicate)
	{
		final Map<Property, Set<RDFNode>> pMap = _statements.get(subject);
		if (pMap == null)
			return Collections.emptyList();

		final Set<RDFNode> list = pMap.get(predicate);
		if (list == null)
			return Collections.emptyList();
		else
			return list;
	}

	public RDFNode getUniqueObject(final Resource subject, final Property predicate)
	{
		final Collection<RDFNode> values = getValues(subject, predicate);
		if (values.isEmpty())
			return null;
		else
			return values.iterator().next();
	}

	public boolean containsStatement(final Resource subject, final Property predicate, final RDFNode object)
	{
		final Collection<RDFNode> values = getValues(subject, predicate);
		if (values.isEmpty())
			return false;
		else
			return values.contains(object);
	}

	private void addToStatements(final Statement stmt)
	{
		final Resource s = stmt.getSubject();
		final Property p = stmt.getPredicate();
		final RDFNode v = stmt.getObject();

		Map<Property, Set<RDFNode>> pMap = _statements.get(s);
		if (pMap == null)
		{
			pMap = CollectionUtil.makeMap();
			_statements.put(s, pMap);
		}
		Set<RDFNode> values = pMap.get(p);
		if (values == null)
		{
			values = CollectionUtil.makeSet();
			pMap.put(p, values);
		}
		values.add(v);
	}

	private void addToStatementsByPredicate(final Statement stmt)
	{
		final Property p = stmt.getPredicate();

		List<Statement> list = _statementsByPredicate.get(p);
		if (list == null)
		{
			list = CollectionUtil.makeList();
			_statementsByPredicate.put(p, list);
		}
		list.add(stmt);
	}

	private void addToStatementsByObject(final Statement stmt)
	{
		final RDFNode o = stmt.getObject();

		List<Statement> list = _statementsByObject.get(o);
		if (list == null)
		{
			list = CollectionUtil.makeList();
			_statementsByObject.put(o, list);
		}
		list.add(stmt);
	}
}
