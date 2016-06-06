package com.clarkparsia.pellint.test.rdfxml;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import com.clarkparsia.pellint.rdfxml.RDFModel;
import com.clarkparsia.pellint.util.CollectionUtil;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import org.apache.jena.rdf.model.AnonId;
import org.apache.jena.rdf.model.Literal;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.ResourceFactory;
import org.apache.jena.rdf.model.Statement;
import org.junit.Before;
import org.junit.Test;

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
public class RDFModelTest
{
	private RDFModel _model;
	private RDFNode[] _bNodes;
	private Resource[] _names;
	private Property[] _predicates;
	private Literal[] _literals;
	private Statement[] _statements;
	private String[] _comments;
	private Map<String, String> _namespaces;

	@Before
	public void setUp()
	{
		final Model model = ModelFactory.createDefaultModel();

		_model = new RDFModel();

		_bNodes = new RDFNode[5];
		for (int i = 0; i < _bNodes.length; i++)
			_bNodes[i] = model.createResource(AnonId.create());

		_names = new Resource[5];
		for (int i = 0; i < _names.length; i++)
			_names[i] = model.createResource("tag:clarkparsia.com,2008:pellint:test:name#" + i);

		_predicates = new Property[5];
		for (int i = 0; i < _predicates.length; i++)
			_predicates[i] = model.createProperty("tag:clarkparsia.com,2008:pellint:test:pred#" + i);

		_literals = new Literal[5];
		for (int i = 0; i < _literals.length; i++)
			_literals[i] = ResourceFactory.createPlainLiteral("lit" + i);

		_statements = new Statement[] { model.createStatement(_names[0], _predicates[0], _bNodes[0]), model.createStatement(_names[0], _predicates[0], _names[1]), model.createStatement(_names[0], _predicates[0], _literals[0]), model.createStatement(_names[1], _predicates[1], _names[0]), model.createStatement(_names[1], _predicates[1], _bNodes[1]), model.createStatement(_names[2], _predicates[2], _names[3]), model.createStatement(_names[2], _predicates[3], _names[0]) };
		for (final Statement m_Statement : _statements)
			_model.addStatement(m_Statement);

		_comments = new String[] { "comment1" };
		for (final String m_Comment : _comments)
			_model.addComment(m_Comment);

		_namespaces = CollectionUtil.makeMap();
		_namespaces.put("ns1", "tag:clarkparsia.com,2008");
		for (final Entry<String, String> entry : _namespaces.entrySet())
			_model.addNamespace(entry.getKey(), entry.getValue());
	}

	@Test
	public void testComments()
	{
		assertEquals(Arrays.asList(_comments), _model.getComments());
	}

	@Test
	public void testNamespaces()
	{
		assertEquals(_namespaces, _model.getNamespaces());
	}

	@Test
	public void testContains()
	{
		final Resource newBNode = ResourceFactory.createResource(AnonId.create().getLabelString());
		assertFalse(_model.containsStatement(newBNode, _predicates[0], newBNode));
		for (final Statement m_Statement : _statements)
			assertTrue(_model.containsStatement(m_Statement.getSubject(), m_Statement.getPredicate(), m_Statement.getObject()));
	}

	@Test
	public void testGetStatementsByObject()
	{
		Collection<Statement> statements = null;

		statements = _model.getStatementsByObject(_bNodes[2]);
		assertTrue(statements.isEmpty());

		statements = _model.getStatementsByObject(_bNodes[0]);
		assertEquals(1, statements.size());
		assertTrue(statements.contains(_statements[0]));

		statements = _model.getStatementsByObject(_literals[0]);
		assertEquals(1, statements.size());
		assertTrue(statements.contains(_statements[2]));

		statements = _model.getStatementsByObject(_names[0]);
		assertEquals(2, statements.size());
		assertTrue(statements.contains(_statements[3]));
		assertTrue(statements.contains(_statements[6]));
	}

	@Test
	public void testGetStatementsByPredicate()
	{
		Collection<Statement> statements = null;

		statements = _model.getStatementsByPredicate(_predicates[4]);
		assertTrue(statements.isEmpty());

		statements = _model.getStatementsByPredicate(_predicates[0]);
		assertEquals(3, statements.size());
		assertTrue(statements.contains(_statements[0]));
		assertTrue(statements.contains(_statements[1]));
		assertTrue(statements.contains(_statements[2]));

		statements = _model.getStatementsByPredicate(_predicates[3]);
		assertEquals(1, statements.size());
		assertTrue(statements.contains(_statements[6]));
	}

	@Test
	public void testGetValues()
	{
		Collection<RDFNode> values = null;

		values = _model.getValues(_names[3], _predicates[3]);
		assertTrue(values.isEmpty());

		values = _model.getValues(_names[2], _predicates[4]);
		assertTrue(values.isEmpty());

		values = _model.getValues(_names[0], _predicates[0]);
		assertEquals(3, values.size());
		assertTrue(values.contains(_bNodes[0]));
		assertTrue(values.contains(_names[1]));
		assertTrue(values.contains(_literals[0]));
	}

	@Test
	public void testGetUniqueObject()
	{
		assertNull(_model.getUniqueObject(_names[3], _predicates[3]));
		assertEquals(_names[3], _model.getUniqueObject(_names[2], _predicates[2]));
	}

	@Test
	public void testAddModel()
	{
		final RDFModel newModel = new RDFModel();
		newModel.add(_model);
		_model = newModel;

		testComments();
		testNamespaces();
		testContains();
		testGetStatementsByObject();
		testGetStatementsByPredicate();
		testGetValues();
		testGetUniqueObject();
	}

	@Test
	public void testAddWithBNodes()
	{
		final Model model = ModelFactory.createDefaultModel();
		final Resource newBNode = ResourceFactory.createResource(AnonId.create().getLabelString());
		final int oldSize = _model.getStatements().size();

		final List<Statement> statements = Arrays.asList(model.createStatement((Resource) _bNodes[0], _predicates[0], _bNodes[1]), model.createStatement(newBNode, _predicates[0], _bNodes[0]), model.createStatement((Resource) _bNodes[0], _predicates[0], newBNode));
		_model.addAllStatementsWithExistingBNodesOnly(statements);

		assertEquals(oldSize + 3, _model.getStatements().size());
		// assertEquals( newBNode, m_Model.getUniqueObject(
		// (Resource)m_BNodes[0], m_Predicates[0] ) );
		// assertNull( m_Model.getUniqueObject( newBNode, m_Predicates[0] ) );
	}
}
