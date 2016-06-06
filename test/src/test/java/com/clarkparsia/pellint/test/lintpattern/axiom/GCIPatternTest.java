package com.clarkparsia.pellint.test.lintpattern.axiom;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import com.clarkparsia.owlapi.OWL;
import com.clarkparsia.pellint.lintpattern.axiom.GCIPattern;
import com.clarkparsia.pellint.model.Lint;
import com.clarkparsia.pellint.test.PellintTestCase;
import com.clarkparsia.pellint.util.CollectionUtil;
import org.junit.Before;
import org.junit.Test;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;

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
public class GCIPatternTest extends PellintTestCase
{

	private GCIPattern _pattern;

	@Override
	@Before
	public void setUp() throws OWLOntologyCreationException
	{
		super.setUp();
		_pattern = new GCIPattern();
	}

	@Test
	public void testNone()
	{
		assertFalse(_pattern.isFixable());

		OWLAxiom axiom = OWL.equivalentClasses(_cls[0], _cls[1]);
		assertNull(_pattern.match(_ontology, axiom));

		axiom = OWL.equivalentClasses(CollectionUtil.<OWLClassExpression> asSet(_cls[0], _cls[1], _cls[2]));
		assertNull(_pattern.match(_ontology, axiom));

		axiom = OWL.equivalentClasses(CollectionUtil.<OWLClassExpression> asSet(_cls[0], _P0AllC0, _cls[2]));
		assertNull(_pattern.match(_ontology, axiom));
	}

	@Test
	public void testComplexEquivalence()
	{
		final OWLAxiom axiom = OWL.equivalentClasses(CollectionUtil.<OWLClassExpression> asSet(_cls[0], _P0AllC0, _P0SomeC1));

		final Lint lint = _pattern.match(_ontology, axiom);
		assertNotNull(lint);
		assertTrue(lint.getParticipatingClasses().isEmpty());
		assertTrue(lint.getParticipatingAxioms().contains(axiom));

		assertNull(lint.getSeverity());
		assertSame(_ontology, lint.getParticipatingOntology());
	}

	@Test
	public void testOneOf()
	{
		final OWLClassExpression oneOf = OWL.oneOf(_ind);
		final OWLAxiom axiom = OWL.equivalentClasses(CollectionUtil.<OWLClassExpression> asSet(_P0AllC0, oneOf));

		assertNull(_pattern.match(_ontology, axiom));
	}

	@Test
	public void testGCI()
	{
		final OWLAxiom axiom = OWL.subClassOf(_P0AllC0, _cls[1]);

		final Lint lint = _pattern.match(_ontology, axiom);
		assertNotNull(lint);
		assertTrue(lint.getParticipatingClasses().isEmpty());
		assertTrue(lint.getParticipatingAxioms().contains(axiom));
	}
}
