package com.clarkparsia.pellint.test.lintpattern.ontology;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;

import com.clarkparsia.owlapi.OWL;
import com.clarkparsia.pellint.lintpattern.ontology.ExistentialExplosionPattern;
import com.clarkparsia.pellint.model.Lint;
import com.clarkparsia.pellint.test.PellintTestCase;
import java.util.List;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
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
public class ExistentialExplosionPatternTest extends PellintTestCase
{

	private ExistentialExplosionPattern _pattern;

	@Override
	@Before
	public void setUp() throws OWLOntologyCreationException
	{
		super.setUp();
		_pattern = new ExistentialExplosionPattern();
	}

	@Test
	public void testStage1SimpleCycle() throws OWLException
	{
		addAxiom(OWL.subClassOf(_cls[0], OWL.some(_pro[0], _cls[1])));
		addAxiom(OWL.subClassOf(_cls[1], OWL.and(OWL.some(_pro[1], _cls[1]), OWL.some(_pro[1], _cls[2]))));
		addAxiom(OWL.subClassOf(_cls[2], OWL.some(_pro[1], _cls[3])));
		addAxiom(OWL.subClassOf(_cls[3], OWL.some(_pro[1], _cls[0])));
		addAxiom(OWL.subClassOf(OWL.some(_pro[2], _cls[1]), _cls[3]));

		final int EXPECTED_SIZE = 1;
		_pattern.setMaxTreeSize(EXPECTED_SIZE);
		final List<Lint> lints = _pattern.match(_ontology);
		assertEquals(0, lints.size());
		assertFalse(_pattern.isFixable());
	}

	@Test
	public void testStage1ZeroCard() throws OWLException
	{
		addAxiom(OWL.subClassOf(_cls[0], OWL.or(OWL.min(_pro[0], 0, _cls[1]), OWL.min(_pro[0], 0, _cls[2]))));
		addAxiom(OWL.subClassOf(_cls[1], OWL.max(_pro[0], 0, _cls[0])));
		addAxiom(OWL.subClassOf(_cls[1], OWL.max(_pro[0], 0, _cls[3])));
		addAxiom(OWL.subClassOf(_cls[2], OWL.exactly(_pro[0], 0, _cls[3])));
		addAxiom(OWL.subClassOf(_cls[2], OWL.exactly(_pro[0], 0, _cls[4])));
		addAxiom(OWL.subClassOf(_cls[3], OWL.exactly(_pro[0], 0, _cls[0])));
		addAxiom(OWL.subClassOf(_cls[4], OWL.exactly(_pro[0], 0, _cls[0])));

		final int EXPECTED_SIZE = 0;
		_pattern.setMaxTreeSize(EXPECTED_SIZE);
		final List<Lint> lints = _pattern.match(_ontology);
		assertEquals(0, lints.size());
	}

	@Test
	public void testStage1Depth2() throws OWLException
	{
		addAxiom(OWL.subClassOf(_cls[0], OWL.some(_pro[1], _cls[1])));
		addAxiom(OWL.subClassOf(_cls[0], OWL.some(_pro[1], _cls[2])));
		addAxiom(OWL.subClassOf(_cls[1], OWL.min(_pro[0], 2, _cls[0])));
		addAxiom(OWL.subClassOf(_cls[1], OWL.min(_pro[0], 5, _cls[3])));
		addAxiom(OWL.subClassOf(_cls[2], OWL.exactly(_pro[0], 1, _cls[3])));
		addAxiom(OWL.subClassOf(_cls[2], OWL.exactly(_pro[0], 1, _cls[4])));
		addAxiom(OWL.subClassOf(_cls[3], OWL.exactly(_pro[0], 1, _cls[0])));
		addAxiom(OWL.subClassOf(_cls[4], OWL.exactly(_pro[0], 1, _cls[0])));

		final int EXPECTED_SIZE = 2 * 2 * 2 * 1 * 1;
		_pattern.setMaxTreeSize(EXPECTED_SIZE);
		List<Lint> lints = _pattern.match(_ontology);
		assertEquals(0, lints.size());

		_pattern.setMaxTreeSize(EXPECTED_SIZE - 1);
		lints = _pattern.match(_ontology);
		assertEquals(1, lints.size());
		final Lint lint = lints.get(0);
		assertEquals(EXPECTED_SIZE, lint.getSeverity().doubleValue(), DOUBLE_DELTA);
		assertEquals(5, lint.getParticipatingClasses().size());
		assertSame(_ontology, lint.getParticipatingOntology());
	}

	@Test
	public void testStage2IndirectCycle1() throws OWLException
	{
		addAxiom(OWL.subClassOf(_cls[0], OWL.some(_pro[0], _cls[1])));
		addAxiom(OWL.subClassOf(_cls[1], OWL.some(_pro[1], _cls[2])));
		addAxiom(OWL.subClassOf(_cls[2], _cls[0]));

		final int EXPECTED_SIZE = 1;
		_pattern.setMaxTreeSize(EXPECTED_SIZE);
		List<Lint> lints = _pattern.match(_ontology);
		assertEquals(0, lints.size());

		_pattern.setMaxTreeSize(EXPECTED_SIZE - 1);
		lints = _pattern.match(_ontology);
		assertEquals(1, lints.size());
	}

	@Test
	public void testStage2IndirectCycle2() throws OWLException
	{
		addAxiom(OWL.subClassOf(_cls[0], OWL.some(_pro[0], _cls[1])));
		addAxiom(OWL.subClassOf(_cls[0], OWL.some(_pro[1], _cls[2])));
		addAxiom(OWL.subClassOf(_cls[1], OWL.some(_pro[1], _cls[3])));
		addAxiom(OWL.subClassOf(_cls[2], OWL.some(_pro[1], _cls[3])));
		addAxiom(OWL.equivalentClasses(_cls[3], _cls[4]));
		addAxiom(OWL.equivalentClasses(_cls[4], _cls[0]));

		final int EXPECTED_SIZE = 2 * 1 * 1;
		_pattern.setMaxTreeSize(EXPECTED_SIZE);
		List<Lint> lints = _pattern.match(_ontology);
		assertEquals(0, lints.size());

		_pattern.setMaxTreeSize(EXPECTED_SIZE - 1);
		lints = _pattern.match(_ontology);
		assertEquals(1, lints.size());
	}

	@Test
	public void testStage3Individuals() throws OWLException
	{
		addAxiom(OWL.subClassOf(_cls[0], OWL.some(_pro[0], _cls[1])));
		addAxiom(OWL.subClassOf(_cls[1], OWL.some(_pro[1], _cls[2])));
		addAxiom(OWL.subClassOf(_cls[2], OWL.some(_pro[1], _cls[0])));
		addAxiom(OWL.subClassOf(_cls[3], _cls[0]));
		addAxiom(OWL.subClassOf(_cls[4], _cls[1]));

		for (int i = 0; i < _cls.length; i++)
			addAxiom(OWL.classAssertion(_ind[i], _cls[i]));

		final int EXPECTED_SIZE = 1 * 5;
		_pattern.setMaxTreeSize(EXPECTED_SIZE - 1);
		final List<Lint> lints = _pattern.match(_ontology);
		assertEquals(1, lints.size());
		final Lint lint = lints.get(0);
		assertEquals(EXPECTED_SIZE, lint.getSeverity().doubleValue(), DOUBLE_DELTA);
		assertEquals(3, lint.getParticipatingClasses().size());
	}

	@Ignore("See ticket #288")
	@Test
	public void testStage4RemoveCycles() throws OWLException
	{
		addAxiom(OWL.subClassOf(_cls[0], OWL.some(_pro[0], _cls[1])));
		addAxiom(OWL.subClassOf(_cls[1], OWL.some(_pro[1], _cls[2])));
		addAxiom(OWL.subClassOf(_cls[2], OWL.some(_pro[1], _cls[0])));

		for (int i = 0; i < _cls.length; i++)
			addAxiom(OWL.classAssertion(_ind[i], _cls[i]));

		final int EXPECTED_SIZE = 3 + 3 + 4;
		_pattern.setMaxTreeSize(EXPECTED_SIZE - 1);
		final List<Lint> lints = _pattern.match(_ontology);
		assertEquals(1, lints.size());
		final Lint lint = lints.get(0);
		assertEquals(EXPECTED_SIZE, lint.getSeverity().doubleValue(), DOUBLE_DELTA);
		assertEquals(3, lint.getParticipatingClasses().size());
	}

	@Test
	public void testStage4Tree() throws OWLException
	{
		addAxiom(OWL.subClassOf(_cls[0], OWL.some(_pro[0], _cls[1])));
		addAxiom(OWL.subClassOf(_cls[0], OWL.some(_pro[1], _cls[1])));
		addAxiom(OWL.subClassOf(_cls[0], OWL.min(_pro[0], 1, _cls[2])));
		addAxiom(OWL.subClassOf(_cls[0], OWL.min(_pro[1], 1, _cls[2])));
		addAxiom(OWL.subClassOf(_cls[1], OWL.some(_pro[1], _cls[3])));
		addAxiom(OWL.subClassOf(_cls[1], OWL.exactly(_pro[0], 1, _cls[4])));
		addAxiom(OWL.subClassOf(_cls[1], OWL.exactly(_pro[1], 1, _cls[4])));
		addAxiom(OWL.subClassOf(_cls[1], OWL.exactly(_pro[2], 1, _cls[4])));
		addAxiom(OWL.subClassOf(_cls[2], OWL.exactly(_pro[3], 1, _cls[4])));

		addAxiom(OWL.classAssertion(_ind[0], _cls[0]));

		final int EXPECTED_SIZE_C2 = 1 + 1 * 1;
		final int EXPECTED_SIZE_C1 = 1 + 1 * 1 + 3 * 1;
		final int EXPECTED_SIZE_C0 = 1 + EXPECTED_SIZE_C1 * 2 + EXPECTED_SIZE_C2 * 2;
		_pattern.setMaxTreeSize(EXPECTED_SIZE_C0);
		List<Lint> lints = _pattern.match(_ontology);
		assertEquals(0, lints.size());

		_pattern.setMaxTreeSize(EXPECTED_SIZE_C0 - 1);
		lints = _pattern.match(_ontology);
		assertEquals(1, lints.size());
	}
}
