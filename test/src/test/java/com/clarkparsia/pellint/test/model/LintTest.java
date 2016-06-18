package com.clarkparsia.pellint.test.model;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import com.clarkparsia.pellint.lintpattern.LintPattern;
import com.clarkparsia.pellint.model.Lint;
import com.clarkparsia.pellint.test.PellintTestCase;
import com.clarkparsia.pellint.test.lintpattern.MockLintPattern;
import org.junit.Before;
import org.junit.Test;
import org.semanticweb.owlapi.model.OWLOntologyChangeException;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;

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
public class LintTest extends PellintTestCase
{
	private LintPattern _mockPattern;

	@Override
	@Before
	public void setUp() throws OWLOntologyCreationException
	{
		super.setUp();
		_mockPattern = new MockLintPattern();
	}

	@Test
	public void testWithoutFixer() throws OWLOntologyChangeException
	{
		final Lint lint = new Lint(_mockPattern, _ontology);
		assertSame(_mockPattern, lint.getPattern());
		assertSame(_ontology, lint.getParticipatingOntology());
		assertFalse(lint.applyFix(_manager));
	}

	@Test
	public void testWithFixer() throws OWLOntologyChangeException
	{
		final Lint lint = new Lint(_mockPattern, _ontology);
		final MockLintFixer fixer = new MockLintFixer();
		lint.setLintFixer(fixer);
		assertTrue(lint.applyFix(_manager));
		assertTrue(fixer._applyCalled);
	}
}
