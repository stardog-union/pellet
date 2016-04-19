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
public class EquivalentAndSubclassAxiomPatternTest extends PellintTestCase
{

	private EquivalentAndSubclassAxiomPattern m_Pattern;

	@Override
	@Before
	public void setUp() throws OWLOntologyCreationException
	{
		super.setUp();
		m_Pattern = new EquivalentAndSubclassAxiomPattern();
	}

	@Test
	public void testNone1() throws OWLException
	{
		addAxiom(OWL.subClassOf(m_Cls[0], m_P0AllC0));
		addAxiom(OWL.subClassOf(m_Cls[0], m_Cls[1]));
		addAxiom(OWL.equivalentClasses(m_Cls[1], m_Cls[2]));
		addAxiom(OWL.equivalentClasses(m_Cls[1], m_Cls[3]));

		assertTrue(m_Pattern.isFixable());
		final List<Lint> lints = m_Pattern.match(m_Ontology);
		assertEquals(0, lints.size());
	}

	@Test
	public void testNone2() throws OWLException
	{
		addAxiom(OWL.subClassOf(m_Cls[0], m_P0AllC0));
		addAxiom(OWL.equivalentClasses(m_Cls[0], m_Cls[1]));
		addAxiom(OWL.equivalentClasses(m_Cls[0], OWL.Thing));

		final List<Lint> lints = m_Pattern.match(m_Ontology);
		assertEquals(0, lints.size());
	}

	@Test
	public void testNone3() throws OWLException
	{
		addAxiom(OWL.equivalentClasses(m_Cls[0], m_P0AllC0));

		final List<Lint> lints = m_Pattern.match(m_Ontology);
		assertEquals(0, lints.size());
	}

	@Test
	public void testEquivalentWithOneDescription() throws OWLException
	{
		final OWLClassExpression min = OWL.min(m_Pro[0], 2, m_Cls[1]);
		final OWLAxiom badAxiom = OWL.equivalentClasses(m_Cls[0], min);
		addAxiom(badAxiom);
		final OWLAxiom fixedAxiom = OWL.subClassOf(m_Cls[0], min);

		addAxiom(OWL.subClassOf(m_Cls[0], m_P0AllC0));
		addAxiom(OWL.equivalentClasses(m_Cls[0], OWL.Thing));
		addAxiom(OWL.equivalentClasses(CollectionUtil.<OWLClassExpression> asSet(m_Cls[0], m_Cls[3], m_Cls[4])));

		final List<Lint> lints = m_Pattern.match(m_Ontology);
		assertEquals(1, lints.size());

		final Lint lint = lints.get(0);
		final Set<OWLClass> participatingClasses = lint.getParticipatingClasses();
		assertEquals(1, participatingClasses.size());
		assertTrue(participatingClasses.contains(m_Cls[0]));

		final LintFixer fixer = lint.getLintFixer();
		final Set<? extends OWLAxiom> axiomsToRemove = fixer.getAxiomsToRemove();
		final Set<? extends OWLAxiom> axiomsToAdd = fixer.getAxiomsToAdd();
		assertEquals(1, axiomsToRemove.size());
		assertTrue(axiomsToRemove.contains(badAxiom));
		assertEquals(1, axiomsToAdd.size());
		assertTrue(axiomsToAdd.contains(fixedAxiom));

		assertNull(lint.getSeverity());
		assertSame(m_Ontology, lint.getParticipatingOntology());
	}

	@Test
	public void testEquivalentWithManyDescriptions() throws OWLException
	{
		addAxiom(OWL.subClassOf(m_Cls[0], m_P0SomeC1));
		final OWLClassExpression exactly = OWL.exactly(m_Pro[1], 2, m_Cls[2]);
		final OWLAxiom badAxiom = OWL.equivalentClasses(CollectionUtil.asSet(m_Cls[0], m_Cls[3], m_Cls[4], exactly));
		addAxiom(badAxiom);
		final Set<OWLClassExpression> restOf = CollectionUtil.asSet(m_Cls[3], m_Cls[4], exactly);
		final OWLAxiom fixedAxiom1 = OWL.equivalentClasses(restOf);
		final OWLAxiom fixedAxiom2 = OWL.subClassOf(m_Cls[0], OWL.and(restOf));

		final List<Lint> lints = m_Pattern.match(m_Ontology);
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
		addAxiom(OWL.subClassOf(m_Cls[0], m_P0AllC0));
		final OWLClassExpression oneOf = OWL.oneOf(m_Ind);
		addAxiom(OWL.equivalentClasses(CollectionUtil.asSet(m_Cls[0], m_Cls[1], oneOf)));

		final List<Lint> lints = m_Pattern.match(m_Ontology);
		assertEquals(0, lints.size());
	}

	@Test
	public void testOnlyEquivalences() throws OWLException
	{
		final OWLAxiom badAxiom1 = OWL.equivalentClasses(m_Cls[0], m_P0AllC0);
		final OWLAxiom fixedAxiom1 = OWL.subClassOf(m_Cls[0], m_P0AllC0);
		final OWLAxiom badAxiom2 = OWL.equivalentClasses(m_Cls[0], m_P0SomeC1);
		final OWLAxiom fixedAxiom2 = OWL.subClassOf(m_Cls[0], m_P0SomeC1);

		addAxiom(badAxiom1);
		addAxiom(badAxiom2);

		final List<Lint> lints = m_Pattern.match(m_Ontology);
		assertEquals(1, lints.size());

		final Lint lint = lints.get(0);
		final Set<OWLClass> participatingClasses = lint.getParticipatingClasses();
		assertEquals(1, participatingClasses.size());
		assertTrue(participatingClasses.contains(m_Cls[0]));

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
