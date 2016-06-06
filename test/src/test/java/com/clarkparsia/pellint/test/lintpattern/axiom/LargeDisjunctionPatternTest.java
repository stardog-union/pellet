package com.clarkparsia.pellint.test.lintpattern.axiom;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;

import com.clarkparsia.owlapi.OWL;
import com.clarkparsia.pellint.lintpattern.axiom.LargeDisjunctionPattern;
import com.clarkparsia.pellint.model.Lint;
import com.clarkparsia.pellint.test.PellintTestCase;
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
public class LargeDisjunctionPatternTest extends PellintTestCase
{

	private LargeDisjunctionPattern _pattern;

	@Override
	@Before
	public void setUp() throws OWLOntologyCreationException
	{
		super.setUp();
		_pattern = new LargeDisjunctionPattern();
	}

	@Test
	public void testNone()
	{
		_pattern.setMaxAllowed(3);

		final OWLClassExpression union = OWL.or(_cls[1], OWL.Thing, OWL.Nothing);
		final OWLAxiom axiom = OWL.subClassOf(_cls[0], union);
		assertNull(_pattern.match(_ontology, axiom));
		assertFalse(_pattern.isFixable());
	}

	@Test
	public void testSimple()
	{
		_pattern.setMaxAllowed(2);

		final OWLClassExpression union = OWL.or(_cls[1], _cls[2], _cls[3]);
		final OWLAxiom axiom = OWL.subClassOf(_cls[0], union);
		final Lint lint = _pattern.match(_ontology, axiom);
		assertNotNull(lint);
		assertSame(_pattern, lint.getPattern());
		assertEquals(1, lint.getParticipatingAxioms().size());
		assertNull(lint.getLintFixer());
		assertEquals(3.0, lint.getSeverity().doubleValue(), DOUBLE_DELTA);
		assertSame(_ontology, lint.getParticipatingOntology());
	}

	@Test
	public void testDisjointClasses()
	{
		_pattern.setMaxAllowed(2);
		final OWLClassExpression union = OWL.or(_cls[1], _cls[2], _cls[3]);
		final OWLAxiom axiom = OWL.disjointClasses(_cls[0], union);
		assertNotNull(_pattern.match(_ontology, axiom));
	}

	@Test
	public void testNested1()
	{
		_pattern.setMaxAllowed(2);

		final OWLClassExpression union = OWL.or(_cls[1], _cls[2], _cls[3]);
		final OWLClassExpression all = OWL.all(_pro[0], union);
		final OWLClassExpression and = OWL.and(all, _cls[4]);
		final OWLAxiom axiom = OWL.equivalentClasses(and, _cls[0]);
		assertNotNull(_pattern.match(_ontology, axiom));
	}

	@Test
	public void testNested2()
	{
		_pattern.setMaxAllowed(2);

		final OWLClassExpression union1 = OWL.or(_cls[1], _cls[2], _cls[3]);
		final OWLClassExpression all = OWL.all(_pro[0], union1);
		final OWLClassExpression union2 = OWL.or(all, _cls[4]);
		final OWLAxiom axiom = OWL.equivalentClasses(union2, _cls[0]);
		assertNotNull(_pattern.match(_ontology, axiom));
	}
}
