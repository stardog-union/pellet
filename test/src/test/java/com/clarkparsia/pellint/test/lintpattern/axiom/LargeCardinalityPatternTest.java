package com.clarkparsia.pellint.test.lintpattern.axiom;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;

import com.clarkparsia.owlapi.OWL;
import com.clarkparsia.pellint.lintpattern.axiom.LargeCardinalityPattern;
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
public class LargeCardinalityPatternTest extends PellintTestCase
{

	private LargeCardinalityPattern _pattern;

	@Override
	@Before
	public void setUp() throws OWLOntologyCreationException
	{
		super.setUp();
		_pattern = new LargeCardinalityPattern();
	}

	@Test
	public void testNone()
	{
		_pattern.setMaxAllowed(3);

		final OWLClassExpression maxCard = OWL.max(_pro[0], 2);
		final OWLAxiom axiom = OWL.subClassOf(_cls[0], maxCard);
		assertNull(_pattern.match(_ontology, axiom));
		assertFalse(_pattern.isFixable());
	}

	@Test
	public void testOneMax()
	{
		_pattern.setMaxAllowed(2);

		final OWLClassExpression maxCard = OWL.max(_pro[0], 3);
		final OWLAxiom axiom = OWL.disjointClasses(_cls[0], maxCard);
		final Lint lint = _pattern.match(_ontology, axiom);
		assertNotNull(lint);
		assertSame(_pattern, lint.getPattern());
		assertEquals(1, lint.getParticipatingAxioms().size());
		assertNull(lint.getLintFixer());
		assertEquals(3.0, lint.getSeverity().doubleValue(), DOUBLE_DELTA);
		assertSame(_ontology, lint.getParticipatingOntology());
	}

	@Test
	public void testTwoMin()
	{
		_pattern.setMaxAllowed(2);

		final OWLClassExpression minCard1 = OWL.min(_pro[0], 3, _cls[0]);
		OWLAxiom axiom = OWL.equivalentClasses(_cls[1], minCard1);
		assertNotNull(_pattern.match(_ontology, axiom));

		final OWLClassExpression minCard2 = OWL.min(_pro[0], 100, _cls[2]);
		axiom = OWL.subClassOf(minCard2, _cls[3]);
		assertNotNull(_pattern.match(_ontology, axiom));
	}

	@Test
	public void testNested()
	{
		_pattern.setMaxAllowed(2);

		final OWLClassExpression exactCard = OWL.exactly(_pro[0], 3, _cls[0]);
		final OWLClassExpression and = OWL.or(_cls[1], exactCard);
		OWLAxiom axiom = OWL.subClassOf(and, _cls[2]);
		assertNotNull(_pattern.match(_ontology, axiom));

		final OWLClassExpression minCard = OWL.min(_pro[0], 3, _cls[0]);
		final OWLClassExpression union = OWL.or(_cls[1], minCard);
		axiom = OWL.subClassOf(union, _cls[2]);
		assertNotNull(_pattern.match(_ontology, axiom));

		final OWLClassExpression maxCard1 = OWL.max(_pro[0], 3, _cls[1]);
		final OWLClassExpression and2 = OWL.and(_cls[2], maxCard1);
		axiom = OWL.subClassOf(and2, _cls[3]);
		assertNotNull(_pattern.match(_ontology, axiom));

		final OWLClassExpression maxCard2 = OWL.max(_pro[0], 2, _cls[2]);
		axiom = OWL.subClassOf(_cls[4], maxCard2);
		assertNull(_pattern.match(_ontology, axiom));
	}
}
