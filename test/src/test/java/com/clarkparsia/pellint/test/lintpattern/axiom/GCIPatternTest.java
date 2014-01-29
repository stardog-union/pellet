package com.clarkparsia.pellint.test.lintpattern.axiom;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLException;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;

import com.clarkparsia.owlapiv3.OWL;
import com.clarkparsia.pellint.lintpattern.axiom.GCIPattern;
import com.clarkparsia.pellint.model.Lint;
import com.clarkparsia.pellint.test.PellintTestCase;
import com.clarkparsia.pellint.util.CollectionUtil;

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
public class GCIPatternTest extends PellintTestCase {
	
	private GCIPattern m_Pattern;
	
	@Before
	public void setUp() throws OWLOntologyCreationException {
		super.setUp();
		m_Pattern = new GCIPattern();
	}
	
	@Test
	public void testNone() throws OWLException {
		assertFalse(m_Pattern.isFixable());

		OWLAxiom axiom = OWL.equivalentClasses(m_Cls[0], m_Cls[1]);
		assertNull(m_Pattern.match(m_Ontology, axiom));
		
		axiom = OWL.equivalentClasses(CollectionUtil.<OWLClassExpression>asSet(m_Cls[0], m_Cls[1], m_Cls[2]));
		assertNull(m_Pattern.match(m_Ontology, axiom));
		
		axiom = OWL.equivalentClasses(CollectionUtil.<OWLClassExpression>asSet(m_Cls[0], m_P0AllC0, m_Cls[2]));
		assertNull(m_Pattern.match(m_Ontology, axiom));
	}
	
	@Test
	public void testComplexEquivalence() throws OWLException {
		OWLAxiom axiom = OWL.equivalentClasses(CollectionUtil.<OWLClassExpression>asSet(m_Cls[0], m_P0AllC0, m_P0SomeC1));
		
		Lint lint = m_Pattern.match(m_Ontology, axiom);
		assertNotNull(lint);
		assertTrue(lint.getParticipatingClasses().isEmpty());
		assertTrue(lint.getParticipatingAxioms().contains(axiom));
		
		assertNull(lint.getSeverity());
		assertSame(m_Ontology, lint.getParticipatingOntology());
	}

	@Test
	public void testOneOf() throws OWLException {
		OWLClassExpression oneOf = OWL.oneOf(m_Ind);
		OWLAxiom axiom = OWL.equivalentClasses(CollectionUtil.<OWLClassExpression>asSet(m_P0AllC0, oneOf));
		
		assertNull(m_Pattern.match(m_Ontology, axiom));
	}
	
	@Test
	public void testGCI() throws OWLException {
		OWLAxiom axiom = OWL.subClassOf(m_P0AllC0, m_Cls[1]);
		
		Lint lint = m_Pattern.match(m_Ontology, axiom);
		assertNotNull(lint);
		assertTrue(lint.getParticipatingClasses().isEmpty());
		assertTrue(lint.getParticipatingAxioms().contains(axiom));
	}
}