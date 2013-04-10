package com.clarkparsia.pellint.test.lintpattern.axiom;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLException;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;

import com.clarkparsia.owlapiv3.OWL;
import com.clarkparsia.pellint.lintpattern.axiom.EquivalentToTopPattern;
import com.clarkparsia.pellint.model.Lint;
import com.clarkparsia.pellint.model.LintFixer;
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
public class EquivalentToTopPatternTest extends PellintTestCase {
		
	private EquivalentToTopPattern m_Pattern;
	
	@Before
	public void setUp() throws OWLOntologyCreationException {
		super.setUp();
		m_Pattern = new EquivalentToTopPattern();
	}

	@Test
	public void testNone() throws OWLException {
		assertTrue(m_Pattern.isFixable());
		
		OWLAxiom axiom = OWL.equivalentClasses(CollectionUtil.<OWLClassExpression>asSet(OWL.Nothing, m_Cls[2], m_Cls[3]));
		assertNull(m_Pattern.match(m_Ontology, axiom));
		
		axiom = OWL.subClassOf(OWL.Thing, m_Cls[1]);
		assertNull(m_Pattern.match(m_Ontology, axiom));
	}
	
	@Test
	public void testSimple() throws OWLException {
		OWLAxiom axiom = OWL.equivalentClasses(CollectionUtil.<OWLClassExpression>asSet(OWL.Thing, m_Cls[0], m_Cls[1]));
		Lint lint = m_Pattern.match(m_Ontology, axiom);
		assertNotNull(lint);
		
		LintFixer fixer = lint.getLintFixer();
		assertTrue(fixer.getAxiomsToRemove().contains(axiom));
		assertTrue(fixer.getAxiomsToAdd().isEmpty());
		
		assertNull(lint.getSeverity());
		assertSame(m_Ontology, lint.getParticipatingOntology());
	}
	
}
