package com.clarkparsia.pellint.test.model;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.Test;
import org.semanticweb.owl.model.OWLOntologyChangeException;

import com.clarkparsia.pellint.model.Lint;
import com.clarkparsia.pellint.model.OntologyLints;
import com.clarkparsia.pellint.test.PellintTestCase;
import com.clarkparsia.pellint.test.lintpattern.MockLintPattern;

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
public class OntologyLintsTest extends PellintTestCase {

	@Test
	public void testSize() {
		OntologyLints ontologyLints = new OntologyLints(m_Ontology);
		assertSame(m_Ontology, ontologyLints.getOntology());
		assertTrue(ontologyLints.isEmpty());
		assertEquals(0, ontologyLints.size());
		
		MockLint lint = new MockLint();
		MockLintPattern pattern1 = new MockLintPattern(false);
		ontologyLints.addLint(pattern1, lint);
		assertFalse(ontologyLints.isEmpty());
		assertEquals(1, ontologyLints.size());
		
		MockLintPattern pattern2 = new MockLintPattern(true);
		List<Lint> lints = Arrays.<Lint>asList(new MockLint(), new MockLint(), new MockLint());
		ontologyLints.addLints(pattern2, lints);
		assertFalse(ontologyLints.isEmpty());
		assertEquals(1 + lints.size(), ontologyLints.size());
	}

	@Test
	public void testApplyFix() throws OWLOntologyChangeException {
		OntologyLints ontologyLints = new OntologyLints(m_Ontology);
		
		MockLint unfixableLint = new MockLint();
		MockLintPattern unfixablePattern = new MockLintPattern(false);
		ontologyLints.addLint(unfixablePattern, unfixableLint);
		MockLintPattern fixablePattern = new MockLintPattern(true);
		List<Lint> lints = Arrays.<Lint>asList(new MockLint(), new MockLint(), new MockLint());
		ontologyLints.addLints(fixablePattern, lints);
		
		assertEquals(Collections.singleton( unfixableLint ), ontologyLints.applyFix(m_Manager));
	}
}