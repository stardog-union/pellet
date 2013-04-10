package com.clarkparsia.pellint.test.lintpattern.ontology;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;

import java.util.List;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.semanticweb.owlapi.model.OWLException;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;

import com.clarkparsia.owlapiv3.OWL;
import com.clarkparsia.pellint.lintpattern.ontology.ExistentialExplosionPattern;
import com.clarkparsia.pellint.model.Lint;
import com.clarkparsia.pellint.test.PellintTestCase;

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
public class ExistentialExplosionPatternTest extends PellintTestCase {
	
	private ExistentialExplosionPattern m_Pattern;
	
	@Before
	public void setUp() throws OWLOntologyCreationException {
		super.setUp();
		m_Pattern = new ExistentialExplosionPattern();
	}

	@Test
	public void testStage1SimpleCycle() throws OWLException {
		addAxiom(OWL.subClassOf(m_Cls[0], OWL.some(m_Pro[0], m_Cls[1])));
		addAxiom(OWL.subClassOf(m_Cls[1], OWL.and(OWL.some(m_Pro[1], m_Cls[1]), OWL.some(m_Pro[1], m_Cls[2]))));
		addAxiom(OWL.subClassOf(m_Cls[2], OWL.some(m_Pro[1], m_Cls[3])));
		addAxiom(OWL.subClassOf(m_Cls[3], OWL.some(m_Pro[1], m_Cls[0])));
		addAxiom(OWL.subClassOf(OWL.some(m_Pro[2], m_Cls[1]), m_Cls[3]));
		
		final int EXPECTED_SIZE = 1;
		m_Pattern.setMaxTreeSize(EXPECTED_SIZE);
		List<Lint> lints = m_Pattern.match(m_Ontology);
		assertEquals(0, lints.size());
		assertFalse(m_Pattern.isFixable());
	}
	
	@Test
	public void testStage1ZeroCard() throws OWLException {
		addAxiom(OWL.subClassOf(m_Cls[0], OWL.or(OWL.min(m_Pro[0], 0, m_Cls[1]), OWL.min(m_Pro[0], 0, m_Cls[2]))));
		addAxiom(OWL.subClassOf(m_Cls[1], OWL.max(m_Pro[0], 0, m_Cls[0])));
		addAxiom(OWL.subClassOf(m_Cls[1], OWL.max(m_Pro[0], 0, m_Cls[3])));
		addAxiom(OWL.subClassOf(m_Cls[2], OWL.exactly(m_Pro[0], 0, m_Cls[3])));
		addAxiom(OWL.subClassOf(m_Cls[2], OWL.exactly(m_Pro[0], 0, m_Cls[4])));
		addAxiom(OWL.subClassOf(m_Cls[3], OWL.exactly(m_Pro[0], 0, m_Cls[0])));
		addAxiom(OWL.subClassOf(m_Cls[4], OWL.exactly(m_Pro[0], 0, m_Cls[0])));
		
		final int EXPECTED_SIZE = 0;
		m_Pattern.setMaxTreeSize(EXPECTED_SIZE);
		List<Lint> lints = m_Pattern.match(m_Ontology);
		assertEquals(0, lints.size());
	}
	
	@Test
	public void testStage1Depth2() throws OWLException {
		addAxiom(OWL.subClassOf(m_Cls[0], OWL.some(m_Pro[1], m_Cls[1])));
		addAxiom(OWL.subClassOf(m_Cls[0], OWL.some(m_Pro[1], m_Cls[2])));
		addAxiom(OWL.subClassOf(m_Cls[1], OWL.min(m_Pro[0], 2, m_Cls[0])));
		addAxiom(OWL.subClassOf(m_Cls[1], OWL.min(m_Pro[0], 5, m_Cls[3])));
		addAxiom(OWL.subClassOf(m_Cls[2], OWL.exactly(m_Pro[0], 1, m_Cls[3])));
		addAxiom(OWL.subClassOf(m_Cls[2], OWL.exactly(m_Pro[0], 1, m_Cls[4])));
		addAxiom(OWL.subClassOf(m_Cls[3], OWL.exactly(m_Pro[0], 1, m_Cls[0])));
		addAxiom(OWL.subClassOf(m_Cls[4], OWL.exactly(m_Pro[0], 1, m_Cls[0])));
		
		final int EXPECTED_SIZE = 2 * 2 * 2 * 1 * 1;
		m_Pattern.setMaxTreeSize(EXPECTED_SIZE);
		List<Lint> lints = m_Pattern.match(m_Ontology);
		assertEquals(0, lints.size());
		
		m_Pattern.setMaxTreeSize(EXPECTED_SIZE - 1);
		lints = m_Pattern.match(m_Ontology);
		assertEquals(1, lints.size());
		Lint lint = lints.get(0);
		assertEquals(EXPECTED_SIZE, lint.getSeverity().doubleValue(), DOUBLE_DELTA);
		assertEquals(5, lint.getParticipatingClasses().size());
		assertSame(m_Ontology, lint.getParticipatingOntology());
	}
	
	@Test
	public void testStage2IndirectCycle1() throws OWLException {
		addAxiom(OWL.subClassOf(m_Cls[0], OWL.some(m_Pro[0], m_Cls[1])));
		addAxiom(OWL.subClassOf(m_Cls[1], OWL.some(m_Pro[1], m_Cls[2])));
		addAxiom(OWL.subClassOf(m_Cls[2], m_Cls[0]));
		
		final int EXPECTED_SIZE = 1;
		m_Pattern.setMaxTreeSize(EXPECTED_SIZE);
		List<Lint> lints = m_Pattern.match(m_Ontology);
		assertEquals(0, lints.size());
		
		m_Pattern.setMaxTreeSize(EXPECTED_SIZE - 1);
		lints = m_Pattern.match(m_Ontology);
		assertEquals(1, lints.size());
	}
	
	@Test
	public void testStage2IndirectCycle2() throws OWLException {
		addAxiom(OWL.subClassOf(m_Cls[0], OWL.some(m_Pro[0], m_Cls[1])));
		addAxiom(OWL.subClassOf(m_Cls[0], OWL.some(m_Pro[1], m_Cls[2])));
		addAxiom(OWL.subClassOf(m_Cls[1], OWL.some(m_Pro[1], m_Cls[3])));
		addAxiom(OWL.subClassOf(m_Cls[2], OWL.some(m_Pro[1], m_Cls[3])));
		addAxiom(OWL.equivalentClasses(m_Cls[3], m_Cls[4]));
		addAxiom(OWL.equivalentClasses(m_Cls[4], m_Cls[0]));
		
		final int EXPECTED_SIZE = 2 * 1 * 1;
		m_Pattern.setMaxTreeSize(EXPECTED_SIZE);
		List<Lint> lints = m_Pattern.match(m_Ontology);
		assertEquals(0, lints.size());
		
		m_Pattern.setMaxTreeSize(EXPECTED_SIZE - 1);
		lints = m_Pattern.match(m_Ontology);
		assertEquals(1, lints.size());
	}

	@Test
	public void testStage3Individuals() throws OWLException {
		addAxiom(OWL.subClassOf(m_Cls[0], OWL.some(m_Pro[0], m_Cls[1])));
		addAxiom(OWL.subClassOf(m_Cls[1], OWL.some(m_Pro[1], m_Cls[2])));
		addAxiom(OWL.subClassOf(m_Cls[2], OWL.some(m_Pro[1], m_Cls[0])));
		addAxiom(OWL.subClassOf(m_Cls[3], m_Cls[0]));
		addAxiom(OWL.subClassOf(m_Cls[4], m_Cls[1]));
		
		for (int i = 0; i < m_Cls.length; i++) {
			addAxiom(OWL.classAssertion(m_Ind[i], m_Cls[i]));
		}
		
		final int EXPECTED_SIZE = 1 * 5;
		m_Pattern.setMaxTreeSize(EXPECTED_SIZE - 1);
		List<Lint> lints = m_Pattern.match(m_Ontology);
		assertEquals(1, lints.size());
		Lint lint = lints.get(0);
		assertEquals(EXPECTED_SIZE, lint.getSeverity().doubleValue(), DOUBLE_DELTA);
		assertEquals(3, lint.getParticipatingClasses().size());
	}

	@Ignore("See ticket #288")
	@Test
	public void testStage4RemoveCycles() throws OWLException {
		addAxiom(OWL.subClassOf(m_Cls[0], OWL.some(m_Pro[0], m_Cls[1])));
		addAxiom(OWL.subClassOf(m_Cls[1], OWL.some(m_Pro[1], m_Cls[2])));
		addAxiom(OWL.subClassOf(m_Cls[2], OWL.some(m_Pro[1], m_Cls[0])));
		
		for (int i = 0; i < m_Cls.length; i++) {
			addAxiom(OWL.classAssertion(m_Ind[i], m_Cls[i]));
		}
		
		final int EXPECTED_SIZE = 3 + 3 + 4;
		m_Pattern.setMaxTreeSize(EXPECTED_SIZE - 1);
		List<Lint> lints = m_Pattern.match(m_Ontology);
		assertEquals(1, lints.size());
		Lint lint = lints.get(0);
		assertEquals(EXPECTED_SIZE, lint.getSeverity().doubleValue(), DOUBLE_DELTA);
		assertEquals(3, lint.getParticipatingClasses().size());
	}

	@Test
	public void testStage4Tree() throws OWLException {
		addAxiom(OWL.subClassOf(m_Cls[0], OWL.some(m_Pro[0], m_Cls[1])));
		addAxiom(OWL.subClassOf(m_Cls[0], OWL.some(m_Pro[1], m_Cls[1])));
		addAxiom(OWL.subClassOf(m_Cls[0], OWL.min(m_Pro[0], 1, m_Cls[2])));
		addAxiom(OWL.subClassOf(m_Cls[0], OWL.min(m_Pro[1], 1, m_Cls[2])));
		addAxiom(OWL.subClassOf(m_Cls[1], OWL.some(m_Pro[1], m_Cls[3])));
		addAxiom(OWL.subClassOf(m_Cls[1], OWL.exactly(m_Pro[0], 1, m_Cls[4])));
		addAxiom(OWL.subClassOf(m_Cls[1], OWL.exactly(m_Pro[1], 1, m_Cls[4])));
		addAxiom(OWL.subClassOf(m_Cls[1], OWL.exactly(m_Pro[2], 1, m_Cls[4])));
		addAxiom(OWL.subClassOf(m_Cls[2], OWL.exactly(m_Pro[3], 1, m_Cls[4])));
		
		addAxiom(OWL.classAssertion(m_Ind[0], m_Cls[0]));
		
		final int EXPECTED_SIZE_C2 = 1 + 1*1;
		final int EXPECTED_SIZE_C1 = 1 + 1*1 + 3*1;
		final int EXPECTED_SIZE_C0 = 1 + EXPECTED_SIZE_C1*2 + EXPECTED_SIZE_C2*2;
		m_Pattern.setMaxTreeSize(EXPECTED_SIZE_C0);
		List<Lint> lints = m_Pattern.match(m_Ontology);
		assertEquals(0, lints.size());

		m_Pattern.setMaxTreeSize(EXPECTED_SIZE_C0 - 1);
		lints = m_Pattern.match(m_Ontology);
		assertEquals(1, lints.size());
	}
} 