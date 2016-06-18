package com.clarkparsia.pellint.test.lintpattern.ontology;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import com.clarkparsia.owlapi.OWL;
import com.clarkparsia.pellint.lintpattern.ontology.EquivalentAndSubclassAxiomPattern;
import com.clarkparsia.pellint.model.Lint;
import com.clarkparsia.pellint.model.LintFixer;
import com.clarkparsia.pellint.test.PellintTestCase;
import com.clarkparsia.pellint.util.CollectionUtil;
import java.util.List;
import java.util.Set;
import org.junit.Before;
import org.junit.Test;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLException;
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
public class EquivalentAndSubclassAxiomPatternTest extends PellintTestCase
{

	private EquivalentAndSubclassAxiomPattern _pattern;

	@Override
	@Before
	public void setUp() throws OWLOntologyCreationException
	{
		super.setUp();
		_pattern = new EquivalentAndSubclassAxiomPattern();
	}

	@Test
	public void testNone1() throws OWLException
	{
		addAxiom(OWL.subClassOf(_cls[0], _P0AllC0));
		addAxiom(OWL.subClassOf(_cls[0], _cls[1]));
		addAxiom(OWL.equivalentClasses(_cls[1], _cls[2]));
		addAxiom(OWL.equivalentClasses(_cls[1], _cls[3]));

		assertTrue(_pattern.isFixable());
		final List<Lint> lints = _pattern.match(_ontology);
		assertEquals(0, lints.size());
	}

	@Test
	public void testNone2() throws OWLException
	{
		addAxiom(OWL.subClassOf(_cls[0], _P0AllC0));
		addAxiom(OWL.equivalentClasses(_cls[0], _cls[1]));
		addAxiom(OWL.equivalentClasses(_cls[0], OWL.Thing));

		final List<Lint> lints = _pattern.match(_ontology);
		assertEquals(0, lints.size());
	}

	@Test
	public void testNone3() throws OWLException
	{
		addAxiom(OWL.equivalentClasses(_cls[0], _P0AllC0));

		final List<Lint> lints = _pattern.match(_ontology);
		assertEquals(0, lints.size());
	}

	@Test
	public void testEquivalentWithOneDescription() throws OWLException
	{
		final OWLClassExpression min = OWL.min(_pro[0], 2, _cls[1]);
		final OWLAxiom badAxiom = OWL.equivalentClasses(_cls[0], min);
		addAxiom(badAxiom);
		final OWLAxiom fixedAxiom = OWL.subClassOf(_cls[0], min);

		addAxiom(OWL.subClassOf(_cls[0], _P0AllC0));
		addAxiom(OWL.equivalentClasses(_cls[0], OWL.Thing));
		addAxiom(OWL.equivalentClasses(CollectionUtil.<OWLClassExpression> asSet(_cls[0], _cls[3], _cls[4])));

		final List<Lint> lints = _pattern.match(_ontology);
		assertEquals(1, lints.size());

		final Lint lint = lints.get(0);
		final Set<OWLClass> participatingClasses = lint.getParticipatingClasses();
		assertEquals(1, participatingClasses.size());
		assertTrue(participatingClasses.contains(_cls[0]));

		final LintFixer fixer = lint.getLintFixer();
		final Set<? extends OWLAxiom> axiomsToRemove = fixer.getAxiomsToRemove();
		final Set<? extends OWLAxiom> axiomsToAdd = fixer.getAxiomsToAdd();
		assertEquals(1, axiomsToRemove.size());
		assertTrue(axiomsToRemove.contains(badAxiom));
		assertEquals(1, axiomsToAdd.size());
		assertTrue(axiomsToAdd.contains(fixedAxiom));

		assertNull(lint.getSeverity());
		assertSame(_ontology, lint.getParticipatingOntology());
	}

	@Test
	public void testEquivalentWithManyDescriptions() throws OWLException
	{
		addAxiom(OWL.subClassOf(_cls[0], _P0SomeC1));
		final OWLClassExpression exactly = OWL.exactly(_pro[1], 2, _cls[2]);
		final OWLAxiom badAxiom = OWL.equivalentClasses(CollectionUtil.asSet(_cls[0], _cls[3], _cls[4], exactly));
		addAxiom(badAxiom);
		final Set<OWLClassExpression> restOf = CollectionUtil.asSet(_cls[3], _cls[4], exactly);
		final OWLAxiom fixedAxiom1 = OWL.equivalentClasses(restOf);
		final OWLAxiom fixedAxiom2 = OWL.subClassOf(_cls[0], OWL.and(restOf));

		final List<Lint> lints = _pattern.match(_ontology);
		assertEquals(1, lints.size());

		final LintFixer fixer = lints.get(0).getLintFixer();
		final Set<? extends OWLAxiom> axiomsToRemove = fixer.getAxiomsToRemove();
		final Set<? extends OWLAxiom> axiomsToAdd = fixer.getAxiomsToAdd();
		assertEquals(1, axiomsToRemove.size());
		assertTrue(axiomsToRemove.contains(badAxiom));
		assertEquals(2, axiomsToAdd.size());
		assertTrue(axiomsToAdd.contains(fixedAxiom1));
		assertTrue(axiomsToAdd.contains(fixedAxiom2));
	}

	@Test
	public void testOneOf() throws OWLException
	{
		addAxiom(OWL.subClassOf(_cls[0], _P0AllC0));
		final OWLClassExpression oneOf = OWL.oneOf(_ind);
		addAxiom(OWL.equivalentClasses(CollectionUtil.asSet(_cls[0], _cls[1], oneOf)));

		final List<Lint> lints = _pattern.match(_ontology);
		assertEquals(0, lints.size());
	}

	@Test
	public void testOnlyEquivalences() throws OWLException
	{
		final OWLAxiom badAxiom1 = OWL.equivalentClasses(_cls[0], _P0AllC0);
		final OWLAxiom fixedAxiom1 = OWL.subClassOf(_cls[0], _P0AllC0);
		final OWLAxiom badAxiom2 = OWL.equivalentClasses(_cls[0], _P0SomeC1);
		final OWLAxiom fixedAxiom2 = OWL.subClassOf(_cls[0], _P0SomeC1);

		addAxiom(badAxiom1);
		addAxiom(badAxiom2);

		final List<Lint> lints = _pattern.match(_ontology);
		assertEquals(1, lints.size());

		final Lint lint = lints.get(0);
		final Set<OWLClass> participatingClasses = lint.getParticipatingClasses();
		assertEquals(1, participatingClasses.size());
		assertTrue(participatingClasses.contains(_cls[0]));

		final LintFixer fixer = lint.getLintFixer();
		final Set<? extends OWLAxiom> axiomsToRemove = fixer.getAxiomsToRemove();
		final Set<? extends OWLAxiom> axiomsToAdd = fixer.getAxiomsToAdd();
		assertEquals(2, axiomsToRemove.size());
		assertTrue(axiomsToRemove.contains(badAxiom1));
		assertTrue(axiomsToRemove.contains(badAxiom2));
		assertEquals(2, axiomsToAdd.size());
		assertTrue(axiomsToAdd.contains(fixedAxiom1));
		assertTrue(axiomsToAdd.contains(fixedAxiom2));
	}
}
