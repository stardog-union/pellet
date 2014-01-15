package com.clarkparsia.pellint.test.lintpattern.axiom;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLException;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;

import com.clarkparsia.owlapiv3.OWL;
import com.clarkparsia.pellint.lintpattern.axiom.EquivalentToAllValuePattern;
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
public class EquivalentToAllValuePatternTest extends PellintTestCase {
	
	private EquivalentToAllValuePattern m_Pattern;
	
	@Before
	public void setUp() throws OWLOntologyCreationException {
		super.setUp();
		m_Pattern = new EquivalentToAllValuePattern();
	}
	
	@Test
	public void testNone() throws OWLException {
		assertTrue(m_Pattern.isFixable());

		OWLAxiom axiom = OWL.subClassOf(m_Cls[0], m_P0AllC0);
		assertNull(m_Pattern.match(m_Ontology, axiom));
		
		axiom = OWL.disjointClasses(m_Cls[1], m_P0AllC0);
		assertNull(m_Pattern.match(m_Ontology, axiom));
		
		axiom = OWL.equivalentClasses(CollectionUtil.asSet(m_Cls[0], m_P0AllC0, m_Cls[2]));
		assertNull(m_Pattern.match(m_Ontology, axiom));
	}
	
	@Test
	public void testOne() throws OWLException {
		OWLAxiom axiom = OWL.equivalentClasses(m_Cls[0], m_P0AllC0);
		
		Lint lint = m_Pattern.match(m_Ontology, axiom);
		assertNotNull(lint);
		assertTrue(lint.getParticipatingClasses().contains(m_Cls[0]));
		
		LintFixer fixer = lint.getLintFixer();
		assertTrue(fixer.getAxiomsToRemove().contains(axiom));
		OWLAxiom expectedAxiom = OWL.subClassOf(m_Cls[0], m_P0AllC0);
		assertTrue(fixer.getAxiomsToAdd().contains(expectedAxiom));
		
		assertNull(lint.getSeverity());
		assertSame(m_Ontology, lint.getParticipatingOntology());
	}
}
