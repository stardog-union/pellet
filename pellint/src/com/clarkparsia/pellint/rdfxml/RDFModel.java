// Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package com.clarkparsia.pellint.rdfxml;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.clarkparsia.pellint.util.CollectionUtil;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;

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
public class RDFModel {
	private List<String> m_Comments;
	private Map<String, String> m_Namespaces;
	private List<Statement> m_AllStatements;
	private Map<Resource, Map<Property, Set<RDFNode>>> m_Statements;
	private Map<Property, List<Statement>> m_StatementsByPredicate;
	private Map<RDFNode, List<Statement>> m_StatementsByObject;
	private Set<RDFNode> m_BNodes;
	
	public RDFModel() {
		m_Comments = CollectionUtil.makeList();
		m_Namespaces = CollectionUtil.makeMap();
		m_AllStatements = CollectionUtil.makeList();
		m_Statements = CollectionUtil.makeMap();
		m_StatementsByPredicate = CollectionUtil.makeMap();
		m_StatementsByObject = CollectionUtil.makeMap();
		m_BNodes = CollectionUtil.makeSet();
	}

	public void add(RDFModel other) {
		m_Comments.addAll(other.m_Comments);
		m_Namespaces.putAll(other.m_Namespaces);
		addAllStatements(other.getStatements());
		m_BNodes.addAll(other.m_BNodes);
	}

	public void add(Model jenaModel) {
		addAllStatements(jenaModel.listStatements());
	}

	public void addComment(String comment) {
		m_Comments.add(comment);
	}

	public List<String> getComments() {
		return m_Comments;
	}

	public void addNamespace(String prefix, String uri) {
		m_Namespaces.put(prefix, uri);
	}

	public Map<String, String> getNamespaces() {
		return m_Namespaces;
	}

	public void addAllStatements(Iterator<Statement> stmts) {
		while (stmts.hasNext()) {
			addStatement(stmts.next());
		}
	}
	
	public void addAllStatements(List<Statement> stmts) {
		addAllStatements(stmts.iterator());
	}
	
	public void addAllStatementsWithExistingBNodesOnly(List<Statement> stmts) {
		for (Statement stmt : stmts) {
			Resource s = stmt.getSubject();
			if (s.isAnon() && !m_BNodes.contains(s)) continue;
			
			RDFNode o = stmt.getObject();
			if (o.isAnon() && !m_BNodes.contains(o)) continue;
			
			addStatement(stmt);
		}
	}


	public void addStatement(Statement stmt) {
		m_AllStatements.add(stmt);
		addToStatements(stmt);
		addToStatementsByPredicate(stmt);
		addToStatementsByObject(stmt);
		addToBNodes(stmt.getSubject());
		addToBNodes(stmt.getObject());
	}

	private void addToBNodes(RDFNode v) {
		if (v.isAnon())
			m_BNodes.add(v);
	}

	public List<Statement> getStatements() {
		return m_AllStatements;
	}

	public Collection<Statement> getStatementsByPredicate(Property predicate) {
		List<Statement> list = m_StatementsByPredicate.get(predicate);
		if (list == null) {
			return Collections.emptyList();
		} else {
			return list;
		}
	}

	public Collection<Statement> getStatementsByObject(RDFNode object) {
		List<Statement> list = m_StatementsByObject.get(object);
		if (list == null) {
			return Collections.emptyList();
		} else {
			return list;
		}
	}

	public Collection<RDFNode> getValues(Resource subject, Property predicate) {
		Map<Property, Set<RDFNode>> pMap = m_Statements.get(subject);
		if (pMap == null) {
			return Collections.emptyList();
		}
		
		Set<RDFNode> list = pMap.get(predicate);
		if (list == null) {
			return Collections.emptyList();
		} else {
			return list;
		}
	}

	public RDFNode getUniqueObject(Resource subject, Property predicate) {
		Collection<RDFNode> values = getValues(subject, predicate);
		if (values.isEmpty()) {
			return null;
		} else {
			return values.iterator().next();
		}
	}

	public boolean containsStatement(Resource subject, Property predicate, RDFNode object) {
		Collection<RDFNode> values = getValues(subject, predicate);
		if (values.isEmpty()) {
			return false;
		} else {
			return values.contains(object);
		}
	}

	private void addToStatements(Statement stmt) {
		Resource s = stmt.getSubject();
		Property p = stmt.getPredicate();
		RDFNode v = stmt.getObject();

		Map<Property, Set<RDFNode>> pMap = m_Statements.get(s);
		if (pMap == null) {
			pMap = CollectionUtil.makeMap();
			m_Statements.put(s, pMap);
		}
		Set<RDFNode> values = pMap.get(p);
		if (values == null) {
			values = CollectionUtil.makeSet();
			pMap.put(p, values);
		}
		values.add(v);
	}

	private void addToStatementsByPredicate(Statement stmt) {
		Property p = stmt.getPredicate();

		List<Statement> list = m_StatementsByPredicate.get(p);
		if (list == null) {
			list = CollectionUtil.makeList();
			m_StatementsByPredicate.put(p, list);
		}
		list.add(stmt);
	}
	
	private void addToStatementsByObject(Statement stmt) {
		RDFNode o = stmt.getObject();

		List<Statement> list = m_StatementsByObject.get(o);
		if (list == null) {
			list = CollectionUtil.makeList();
			m_StatementsByObject.put(o, list);
		}
		list.add(stmt);
	}
}
