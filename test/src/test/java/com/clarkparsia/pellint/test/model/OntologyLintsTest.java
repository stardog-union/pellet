package com.clarkparsia.pellint.test.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import com.clarkparsia.pellint.model.Lint;
import com.clarkparsia.pellint.model.OntologyLints;
import com.clarkparsia.pellint.test.PellintTestCase;
import com.clarkparsia.pellint.test.lintpattern.MockLintPattern;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.junit.Test;

/**
 * <p>
 * Copyright: Copyright (c) 2008
 * </p>
 * <p>
 * Company: Clark & Parsia, LLC. <http://www.clarkparsia.com>
 * </p>
 *
 * @author Harris Lin
 */
public class OntologyLintsTest extends PellintTestCase
{

	@Test
	public void testSize()
	{
		final OntologyLints ontologyLints = new OntologyLints(_ontology);
		assertSame(_ontology, ontologyLints.getOntology());
		assertTrue(ontologyLints.isEmpty());
		assertEquals(0, ontologyLints.size());

		final MockLint lint = new MockLint();
		final MockLintPattern pattern1 = new MockLintPattern(false);
		ontologyLints.addLint(pattern1, lint);
		assertFalse(ontologyLints.isEmpty());
		assertEquals(1, ontologyLints.size());

		final MockLintPattern pattern2 = new MockLintPattern(true);
		final List<Lint> lints = Arrays.<Lint> asList(new MockLint(), new MockLint(), new MockLint());
		ontologyLints.addLints(pattern2, lints);
		assertFalse(ontologyLints.isEmpty());
		assertEquals(1 + lints.size(), ontologyLints.size());
	}

	@Test
	public void testApplyFix()
	{
		final OntologyLints ontologyLints = new OntologyLints(_ontology);

		final MockLint unfixableLint = new MockLint();
		final MockLintPattern unfixablePattern = new MockLintPattern(false);
		ontologyLints.addLint(unfixablePattern, unfixableLint);
		final MockLintPattern fixablePattern = new MockLintPattern(true);
		final List<Lint> lints = Arrays.<Lint> asList(new MockLint(), new MockLint(), new MockLint());
		ontologyLints.addLints(fixablePattern, lints);

		assertEquals(Collections.singleton(unfixableLint), ontologyLints.applyFix(_manager));
	}
}
