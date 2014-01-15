package com.clarkparsia.pellint.test.lintpattern.ontology;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLException;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;

import com.clarkparsia.owlapiv3.OWL;
import com.clarkparsia.pellint.lintpattern.ontology.EquivalentAndSubclassAxiomPattern;
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
public class EquivalentAndSubclassAxiomPatternTest extends PellintTestCase {
	
	private EquivalentAndSubclassAxiomPattern m_Pattern;
	
	@Before
	public void setUp() throws OWLOntologyCreationException {
		super.setUp();
		m_Pattern = new EquivalentAndSubclassAxiomPattern();
	}

	@Test
	public void testNone1() throws OWLException {
		addAxiom(OWL.subClassOf(m_Cls[0], m_P0AllC0));
		addAxiom(OWL.subClassOf(m_Cls[0], m_Cls[1]));
		addAxiom(OWL.equivalentClasses(m_Cls[1], m_Cls[2]));
		addAxiom(OWL.equivalentClasses(m_Cls[1], m_Cls[3]));
		
		assertTrue(m_Pattern.isFixable());
		List<Lint> lints = m_Pattern.match(m_Ontology);
		assertEquals(0, lints.size());
	}
	
	@Test
	public void testNone2() throws OWLException {
		addAxiom(OWL.subClassOf(m_Cls[0], m_P0AllC0));
		addAxiom(OWL.equivalentClasses(m_Cls[0], m_Cls[1]));
		addAxiom(OWL.equivalentClasses(m_Cls[0], OWL.Thing));
		
		List<Lint> lints = m_Pattern.match(m_Ontology);
		assertEquals(0, lints.size());
	}
	
	@Test
	public void testNone3() throws OWLException {
		addAxiom(OWL.equivalentClasses(m_Cls[0], m_P0AllC0));
		
		List<Lint> lints = m_Pattern.match(m_Ontology);
		assertEquals(0, lints.size());
	}
	
	@Test
	public void testEquivalentWithOneDescription() throws OWLException {
		OWLClassExpression min = OWL.min(m_Pro[0], 2, m_Cls[1]);
		OWLAxiom badAxiom = OWL.equivalentClasses(m_Cls[0], min);
		addAxiom(badAxiom);
		OWLAxiom fixedAxiom = OWL.subClassOf(m_Cls[0], min);
		
		addAxiom(OWL.subClassOf(m_Cls[0], m_P0AllC0));
		addAxiom(OWL.equivalentClasses(m_Cls[0], OWL.Thing));
		addAxiom(OWL.equivalentClasses(CollectionUtil.<OWLClassExpression>asSet(m_Cls[0], m_Cls[3], m_Cls[4])));
		
		List<Lint> lints = m_Pattern.match(m_Ontology);
		assertEquals(1, lints.size());
		
		Lint lint = lints.get(0);
		Set<OWLClass> participatingClasses = lint.getParticipatingClasses();
		assertEquals(1, participatingClasses.size());
		assertTrue(participatingClasses.contains(m_Cls[0]));
		
		LintFixer fixer = lint.getLintFixer();
		Set<? extends OWLAxiom> axiomsToRemove = fixer.getAxiomsToRemove();
		Set<? extends OWLAxiom> axiomsToAdd = fixer.getAxiomsToAdd();
		assertEquals(1, axiomsToRemove.size());
		assertTrue(axiomsToRemove.contains(badAxiom));
		assertEquals(1, axiomsToAdd.size());
		assertTrue(axiomsToAdd.contains(fixedAxiom));
		
		assertNull(lint.getSeverity());
		assertSame(m_Ontology, lint.getParticipatingOntology());
	}
	
	@Test
	public void testEquivalentWithManyDescriptions() throws OWLException {
		addAxiom(OWL.subClassOf(m_Cls[0], m_P0SomeC1));
		OWLClassExpression exactly = OWL.exactly(m_Pro[1], 2, m_Cls[2]);
		OWLAxiom badAxiom = OWL.equivalentClasses(CollectionUtil.asSet(m_Cls[0], m_Cls[3], m_Cls[4], exactly));
		addAxiom(badAxiom);
		Set<OWLClassExpression> restOf = CollectionUtil.asSet(m_Cls[3], m_Cls[4], exactly);
		OWLAxiom fixedAxiom1 = OWL.equivalentClasses(restOf);
		OWLAxiom fixedAxiom2 = OWL.subClassOf(m_Cls[0], OWL.and(restOf));
						
		List<Lint> lints = m_Pattern.match(m_Ontology);
		assertEquals(1, lints.size());
		
		LintFixer fixer = lints.get(0).getLintFixer();
		Set<? extends OWLAxiom> axiomsToRemove = fixer.getAxiomsToRemove();
		Set<? extends OWLAxiom> axiomsToAdd = fixer.getAxiomsToAdd();
		assertEquals(1, axiomsToRemove.size());
		assertTrue(axiomsToRemove.contains(badAxiom));
		assertEquals(2, axiomsToAdd.size());
		assertTrue(axiomsToAdd.contains(fixedAxiom1));
		assertTrue(axiomsToAdd.contains(fixedAxiom2));
	}
	
	@Test
	public void testOneOf() throws OWLException {
		addAxiom(OWL.subClassOf(m_Cls[0], m_P0AllC0));
		OWLClassExpression oneOf = OWL.oneOf(m_Ind);
		addAxiom(OWL.equivalentClasses(CollectionUtil.asSet(m_Cls[0], m_Cls[1], oneOf)));
		
		List<Lint> lints = m_Pattern.match(m_Ontology);
		assertEquals(0, lints.size());
	}

	@Test
	public void testOnlyEquivalences() throws OWLException {
		OWLAxiom badAxiom1 = OWL.equivalentClasses(m_Cls[0], m_P0AllC0);
		OWLAxiom fixedAxiom1 = OWL.subClassOf(m_Cls[0], m_P0AllC0);
		OWLAxiom badAxiom2 = OWL.equivalentClasses(m_Cls[0], m_P0SomeC1);
		OWLAxiom fixedAxiom2 = OWL.subClassOf(m_Cls[0], m_P0SomeC1);
		
		addAxiom(badAxiom1);
		addAxiom(badAxiom2);
		
		List<Lint> lints = m_Pattern.match(m_Ontology);
		assertEquals(1, lints.size());
		
		Lint lint = lints.get(0);
		Set<OWLClass> participatingClasses = lint.getParticipatingClasses();
		assertEquals(1, participatingClasses.size());
		assertTrue(participatingClasses.contains(m_Cls[0]));
		
		LintFixer fixer = lint.getLintFixer();
		Set<? extends OWLAxiom> axiomsToRemove = fixer.getAxiomsToRemove();
		Set<? extends OWLAxiom> axiomsToAdd = fixer.getAxiomsToAdd();
		assertEquals(2, axiomsToRemove.size());
		assertTrue(axiomsToRemove.contains(badAxiom1));
		assertTrue(axiomsToRemove.contains(badAxiom2));
		assertEquals(2, axiomsToAdd.size());
		assertTrue(axiomsToAdd.contains(fixedAxiom1));
		assertTrue(axiomsToAdd.contains(fixedAxiom2));
	}
}
