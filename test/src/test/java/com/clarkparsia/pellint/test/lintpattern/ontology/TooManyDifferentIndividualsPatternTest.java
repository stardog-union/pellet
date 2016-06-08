package com.clarkparsia.pellint.test.lintpattern.ontology;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;

import com.clarkparsia.owlapi.OWL;
import com.clarkparsia.pellint.lintpattern.ontology.TooManyDifferentIndividualsPattern;
import com.clarkparsia.pellint.model.Lint;
import com.clarkparsia.pellint.test.PellintTestCase;
import com.clarkparsia.pellint.util.CollectionUtil;
import java.util.List;
import org.junit.Before;
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
public class TooManyDifferentIndividualsPatternTest extends PellintTestCase
{

	private TooManyDifferentIndividualsPattern _pattern;

	@Override
	@Before
	public void setUp() throws OWLOntologyCreationException
	{
		super.setUp();
		_pattern = new TooManyDifferentIndividualsPattern();
	}

	@Test
	public void testNone() throws OWLException
	{
		addAxiom(OWL.differentFrom(_ind[0], _ind[1]));
		addAxiom(OWL.differentFrom(_ind[2], _ind[3]));

		_pattern.setMaxAllowed(3);
		final List<Lint> lints = _pattern.match(_ontology);
		assertEquals(0, lints.size());
		assertFalse(_pattern.isFixable());
	}

	@Test
	public void testOne() throws OWLException
	{
		addAxiom(OWL.differentFrom(CollectionUtil.asSet(_ind[0], _ind[1], _ind[2])));

		_pattern.setMaxAllowed(3);
		List<Lint> lints = _pattern.match(_ontology);
		assertEquals(0, lints.size());

		addAxiom(OWL.differentFrom(_ind[3], _ind[4]));
		lints = _pattern.match(_ontology);
		assertEquals(1, lints.size());
		final Lint lint = lints.get(0);
		assertNull(lint.getLintFixer());
		assertEquals(6 + 2, lint.getSeverity().doubleValue(), DOUBLE_DELTA);
		assertSame(_ontology, lint.getParticipatingOntology());
	}
}
