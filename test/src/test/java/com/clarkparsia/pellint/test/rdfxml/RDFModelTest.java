package com.clarkparsia.pellint.test.rdfxml;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.junit.Before;
import org.junit.Test;

import com.clarkparsia.pellint.rdfxml.RDFModel;
import com.clarkparsia.pellint.util.CollectionUtil;
import com.hp.hpl.jena.rdf.model.AnonId;
import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.ResourceFactory;
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
public class RDFModelTest {
	private RDFModel			m_Model;
	private RDFNode[]			m_BNodes;
	private Resource[]			m_Names;
	private Property[]			m_Predicates;
	private Literal[]			m_Literals;
	private Statement[]			m_Statements;
	private String[]			m_Comments;
	private Map<String, String>	m_Namespaces;

	@Before
	public void setUp() throws Exception {
		Model model = ModelFactory.createDefaultModel();

		m_Model = new RDFModel();

		m_BNodes = new RDFNode[5];
		for( int i = 0; i < m_BNodes.length; i++ ) {
			m_BNodes[i] = model.createResource( AnonId.create() );
		}

		m_Names = new Resource[5];
		for( int i = 0; i < m_Names.length; i++ ) {
			m_Names[i] = model.createResource( "tag:clarkparsia.com,2008:pellint:test:name#" + i );
		}

		m_Predicates = new Property[5];
		for( int i = 0; i < m_Predicates.length; i++ ) {
			m_Predicates[i] = model.createProperty( "tag:clarkparsia.com,2008:pellint:test:pred#" + i );
		}

		m_Literals = new Literal[5];
		for( int i = 0; i < m_Literals.length; i++ ) {
			m_Literals[i] = ResourceFactory.createPlainLiteral( "lit" + i );
		}

		m_Statements = new Statement[] {
				model.createStatement( m_Names[0], m_Predicates[0], m_BNodes[0] ),
				model.createStatement( m_Names[0], m_Predicates[0], m_Names[1] ),
				model.createStatement( m_Names[0], m_Predicates[0], m_Literals[0] ),
				model.createStatement( m_Names[1], m_Predicates[1], m_Names[0] ),
				model.createStatement( m_Names[1], m_Predicates[1], m_BNodes[1] ),
				model.createStatement( m_Names[2], m_Predicates[2], m_Names[3] ),
				model.createStatement( m_Names[2], m_Predicates[3], m_Names[0] ) };
		for( int i = 0; i < m_Statements.length; i++ ) {
			m_Model.addStatement( m_Statements[i] );
		}

		m_Comments = new String[] { "comment1" };
		for( int i = 0; i < m_Comments.length; i++ ) {
			m_Model.addComment( m_Comments[i] );
		}

		m_Namespaces = CollectionUtil.makeMap();
		m_Namespaces.put( "ns1", "tag:clarkparsia.com,2008" );
		for( Entry<String, String> entry : m_Namespaces.entrySet() ) {
			m_Model.addNamespace( entry.getKey(), entry.getValue() );
		}
	}

	@Test
	public void testComments() {
		assertEquals( Arrays.asList( m_Comments ), m_Model.getComments() );
	}

	@Test
	public void testNamespaces() {
		assertEquals( m_Namespaces, m_Model.getNamespaces() );
	}

	@Test
	public void testContains() {
		Resource newBNode = ResourceFactory.createResource( AnonId.create().getLabelString() );
		assertFalse( m_Model.containsStatement( newBNode, m_Predicates[0], newBNode ) );
		for( int i = 0; i < m_Statements.length; i++ ) {
			assertTrue( m_Model.containsStatement( m_Statements[i].getSubject(), m_Statements[i]
					.getPredicate(), m_Statements[i].getObject() ) );
		}
	}

	@Test
	public void testGetStatementsByObject() {
		Collection<Statement> statements = null;

		statements = m_Model.getStatementsByObject( m_BNodes[2] );
		assertTrue( statements.isEmpty() );

		statements = m_Model.getStatementsByObject( m_BNodes[0] );
		assertEquals( 1, statements.size() );
		assertTrue( statements.contains( m_Statements[0] ) );

		statements = m_Model.getStatementsByObject( m_Literals[0] );
		assertEquals( 1, statements.size() );
		assertTrue( statements.contains( m_Statements[2] ) );

		statements = m_Model.getStatementsByObject( m_Names[0] );
		assertEquals( 2, statements.size() );
		assertTrue( statements.contains( m_Statements[3] ) );
		assertTrue( statements.contains( m_Statements[6] ) );
	}

	@Test
	public void testGetStatementsByPredicate() {
		Collection<Statement> statements = null;

		statements = m_Model.getStatementsByPredicate( m_Predicates[4] );
		assertTrue( statements.isEmpty() );

		statements = m_Model.getStatementsByPredicate( m_Predicates[0] );
		assertEquals( 3, statements.size() );
		assertTrue( statements.contains( m_Statements[0] ) );
		assertTrue( statements.contains( m_Statements[1] ) );
		assertTrue( statements.contains( m_Statements[2] ) );

		statements = m_Model.getStatementsByPredicate( m_Predicates[3] );
		assertEquals( 1, statements.size() );
		assertTrue( statements.contains( m_Statements[6] ) );
	}

	@Test
	public void testGetValues() {
		Collection<RDFNode> values = null;

		values = m_Model.getValues( m_Names[3], m_Predicates[3] );
		assertTrue( values.isEmpty() );

		values = m_Model.getValues( m_Names[2], m_Predicates[4] );
		assertTrue( values.isEmpty() );

		values = m_Model.getValues( m_Names[0], m_Predicates[0] );
		assertEquals( 3, values.size() );
		assertTrue( values.contains( m_BNodes[0] ) );
		assertTrue( values.contains( m_Names[1] ) );
		assertTrue( values.contains( m_Literals[0] ) );
	}

	@Test
	public void testGetUniqueObject() {
		assertNull( m_Model.getUniqueObject( m_Names[3], m_Predicates[3] ) );
		assertEquals( m_Names[3], m_Model.getUniqueObject( m_Names[2], m_Predicates[2] ) );
	}

	@Test
	public void testAddModel() {
		RDFModel newModel = new RDFModel();
		newModel.add( m_Model );
		m_Model = newModel;

		testComments();
		testNamespaces();
		testContains();
		testGetStatementsByObject();
		testGetStatementsByPredicate();
		testGetValues();
		testGetUniqueObject();
	}

	@Test
	public void testAddWithBNodes() {
		Model model = ModelFactory.createDefaultModel();
		Resource newBNode = ResourceFactory.createResource( AnonId.create().getLabelString() );
		int oldSize = m_Model.getStatements().size();
		
		List<Statement> statements = Arrays.asList( model.createStatement( (Resource)m_BNodes[0],
				m_Predicates[0], m_BNodes[1] ), model.createStatement( newBNode, m_Predicates[0],
				m_BNodes[0] ), model.createStatement( (Resource)m_BNodes[0], m_Predicates[0], newBNode ) );
		m_Model.addAllStatementsWithExistingBNodesOnly( statements );
		
		assertEquals( oldSize + 3, m_Model.getStatements().size() );
		// assertEquals( newBNode, m_Model.getUniqueObject(
		// (Resource)m_BNodes[0], m_Predicates[0] ) );
		// assertNull( m_Model.getUniqueObject( newBNode, m_Predicates[0] ) );
	}
}