package com.clarkparsia.pellint.test.lintpattern.ontology;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.semanticweb.owlapi.model.OWLException;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;

import com.clarkparsia.owlapiv3.OWL;
import com.clarkparsia.pellint.lintpattern.ontology.TooManyDifferentIndividualsPattern;
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
public class TooManyDifferentIndividualsPatternTest extends PellintTestCase {

	private TooManyDifferentIndividualsPattern m_Pattern;
	
	@Before
	public void setUp() throws OWLOntologyCreationException {
		super.setUp();
		m_Pattern = new TooManyDifferentIndividualsPattern();
	}

	@Test
	public void testNone() throws OWLException {
		addAxiom(OWL.differentFrom(m_Ind[0], m_Ind[1]));
		addAxiom(OWL.differentFrom(m_Ind[2], m_Ind[3]));

		m_Pattern.setMaxAllowed(3);
		List<Lint> lints = m_Pattern.match(m_Ontology);
		assertEquals(0, lints.size());
		assertFalse(m_Pattern.isFixable());
	}
	
	@Test
	public void testOne() throws OWLException {
		addAxiom(OWL.differentFrom(CollectionUtil.asSet(m_Ind[0], m_Ind[1], m_Ind[2])));
		
		m_Pattern.setMaxAllowed(3);
		List<Lint> lints = m_Pattern.match(m_Ontology);
		assertEquals(0, lints.size());
		
		addAxiom(OWL.differentFrom(m_Ind[3], m_Ind[4]));
		lints = m_Pattern.match(m_Ontology);
		assertEquals(1, lints.size());
		Lint lint = lints.get(0);
		assertNull(lint.getLintFixer());
		assertEquals(6 + 2, lint.getSeverity().doubleValue(), DOUBLE_DELTA);
		assertSame(m_Ontology, lint.getParticipatingOntology());
	}
}