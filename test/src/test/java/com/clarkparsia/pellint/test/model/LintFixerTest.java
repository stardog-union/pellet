package com.clarkparsia.pellint.test.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import com.clarkparsia.owlapi.OWL;
import com.clarkparsia.pellint.model.LintFixer;
import com.clarkparsia.pellint.test.PellintTestCase;
import com.clarkparsia.pellint.util.CollectionUtil;
import java.util.Set;
import java.util.stream.Collectors;
import org.junit.Before;
import org.junit.Test;
import org.semanticweb.owlapi.model.OWLAxiom;
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
public class LintFixerTest extends PellintTestCase
{
	private OWLAxiom[] _axioms;

	@Override
	@Before
	public void setUp() throws OWLOntologyCreationException
	{
		super.setUp();

		_axioms = new OWLAxiom[] { OWL.subClassOf(_cls[0], OWL.or(_cls[1], _cls[2], _cls[3])), OWL.equivalentClasses(_cls[0], _cls[1]), OWL.differentFrom(_ind[2], _ind[3]) };
	}

	@Test
	public void testRemoveAndAdd() throws OWLException
	{
		addAxiom(_axioms[0]);
		addAxiom(_axioms[1]);

		final Set<OWLAxiom> axiomsToRemove = CollectionUtil.asSet(_axioms[0], _axioms[1]);
		final Set<OWLAxiom> axiomsToAdd = CollectionUtil.asSet(_axioms[2]);
		final LintFixer fixer = new LintFixer(axiomsToRemove, axiomsToAdd);
		assertTrue(fixer.apply(_manager, _ontology));

		final Set<OWLAxiom> axioms = _ontology.axioms().collect(Collectors.toSet());
		assertEquals(1, axioms.size());
		assertTrue(axioms.contains(_axioms[2]));
	}

	@Test
	public void testOldAxiomsDontExist() throws OWLException
	{
		addAxiom(_axioms[0]);

		final Set<OWLAxiom> axiomsToRemove = CollectionUtil.asSet(_axioms[0], _axioms[1]);
		final Set<OWLAxiom> axiomsToAdd = CollectionUtil.asSet(_axioms[2]);
		final LintFixer fixer = new LintFixer(axiomsToRemove, axiomsToAdd);
		assertFalse(fixer.apply(_manager, _ontology));

		final Set<OWLAxiom> axioms = _ontology.axioms().collect(Collectors.toSet());
		assertEquals(1, axioms.size());
		assertTrue(axioms.contains(_axioms[0]));
	}

	@Test
	public void testNewAxiomsAlreadyExist() throws OWLException
	{
		addAxiom(_axioms[0]);
		addAxiom(_axioms[1]);
		addAxiom(_axioms[2]);

		final Set<OWLAxiom> axiomsToRemove = CollectionUtil.asSet(_axioms[0], _axioms[1]);
		final Set<OWLAxiom> axiomsToAdd = CollectionUtil.asSet(_axioms[2]);
		final LintFixer fixer = new LintFixer(axiomsToRemove, axiomsToAdd);
		assertTrue(fixer.apply(_manager, _ontology));

		final Set<OWLAxiom> axioms = _ontology.axioms().collect(Collectors.toSet());
		assertEquals(1, axioms.size());
		assertTrue(axioms.contains(_axioms[2]));
	}

}
