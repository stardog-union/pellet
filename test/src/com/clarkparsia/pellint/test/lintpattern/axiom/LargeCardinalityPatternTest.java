package com.clarkparsia.pellint.test.lintpattern.axiom;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLException;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;

import com.clarkparsia.owlapiv3.OWL;
import com.clarkparsia.pellint.lintpattern.axiom.LargeCardinalityPattern;
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
public class LargeCardinalityPatternTest extends PellintTestCase {

	private LargeCardinalityPattern m_Pattern;
	
	@Before
	public void setUp() throws OWLOntologyCreationException {
		super.setUp();
		m_Pattern = new LargeCardinalityPattern();
	}
	
	@Test
	public void testNone() throws OWLException {
		m_Pattern.setMaxAllowed(3);
		
		OWLClassExpression maxCard = OWL.max(m_Pro[0], 2);
		OWLAxiom axiom = OWL.subClassOf(m_Cls[0], maxCard);
		assertNull(m_Pattern.match(m_Ontology, axiom));
		assertFalse(m_Pattern.isFixable());
	}
	
	@Test
	public void testOneMax() throws OWLException {
		m_Pattern.setMaxAllowed(2);

		OWLClassExpression maxCard = OWL.max(m_Pro[0], 3);
		OWLAxiom axiom = OWL.disjointClasses(m_Cls[0], maxCard);
		Lint lint = m_Pattern.match(m_Ontology, axiom);
		assertNotNull(lint);
		assertSame(m_Pattern, lint.getPattern());
		assertEquals(1, lint.getParticipatingAxioms().size());
		assertNull(lint.getLintFixer());
		assertEquals(3.0, lint.getSeverity().doubleValue(), DOUBLE_DELTA);
		assertSame(m_Ontology, lint.getParticipatingOntology());
	}
	
	@Test
	public void testTwoMin() throws OWLException {
		m_Pattern.setMaxAllowed(2);

		OWLClassExpression minCard1 = OWL.min(m_Pro[0], 3, m_Cls[0]);
		OWLAxiom axiom = OWL.equivalentClasses(m_Cls[1], minCard1);
		assertNotNull(m_Pattern.match(m_Ontology, axiom));
		
		OWLClassExpression minCard2 = OWL.min(m_Pro[0], 100, m_Cls[2]);
		axiom = OWL.subClassOf(minCard2, m_Cls[3]);
		assertNotNull(m_Pattern.match(m_Ontology, axiom));
	}
	
	@Test
	public void testNested() throws OWLException {
		m_Pattern.setMaxAllowed(2);

		OWLClassExpression exactCard = OWL.exactly(m_Pro[0], 3, m_Cls[0]);
		OWLClassExpression and = OWL.or(m_Cls[1], exactCard);
		OWLAxiom axiom = OWL.subClassOf(and, m_Cls[2]);
		assertNotNull(m_Pattern.match(m_Ontology, axiom));
		
		OWLClassExpression minCard = OWL.min(m_Pro[0], 3, m_Cls[0]);
		OWLClassExpression union = OWL.or(m_Cls[1], minCard);
		axiom = OWL.subClassOf(union, m_Cls[2]);
		assertNotNull(m_Pattern.match(m_Ontology, axiom));
		
		OWLClassExpression maxCard1 = OWL.max(m_Pro[0], 3, m_Cls[1]);
		OWLClassExpression and2 = OWL.and(m_Cls[2], maxCard1);
		axiom = OWL.subClassOf(and2, m_Cls[3]);
		assertNotNull(m_Pattern.match(m_Ontology, axiom));
		
		OWLClassExpression maxCard2 = OWL.max(m_Pro[0], 2, m_Cls[2]);
		axiom = OWL.subClassOf(m_Cls[4], maxCard2);
		assertNull(m_Pattern.match(m_Ontology, axiom));
	}
}
