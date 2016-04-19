// Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package com.clarkparsia.pellet.test;

import static com.clarkparsia.pellet.utils.TermFactory.and;
import static com.clarkparsia.pellet.utils.TermFactory.or;
import static com.clarkparsia.pellet.utils.TermFactory.some;
import static com.clarkparsia.pellet.utils.TermFactory.term;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import aterm.ATermAppl;
import com.clarkparsia.pellet.utils.TermFactory;
import java.util.HashSet;
import java.util.Set;
import junit.framework.JUnit4TestAdapter;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mindswap.pellet.KnowledgeBase;
import org.mindswap.pellet.PelletOptions;
import org.mindswap.pellet.utils.progress.AbstractProgressMonitor;

/**
 * <p>
 * Title: TestKnowledgeBase
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
 * @author Markus Stocker
 */
public class TestKnowledgeBase
{

	@BeforeClass
	public static void setUp()
	{
		PelletOptions.SILENT_UNDEFINED_ENTITY_HANDLING = false;
	}

	@AfterClass
	public static void tearDown()
	{
		PelletOptions.SILENT_UNDEFINED_ENTITY_HANDLING = true;
	}

	public static junit.framework.Test suite()
	{
		return new JUnit4TestAdapter(TestKnowledgeBase.class);
	}

	@Test
	public void testGetInstances1()
	{
		final KnowledgeBase kb = new KnowledgeBase();

		final ATermAppl C = term("C");
		final ATermAppl i = term("i");

		kb.addClass(C);
		kb.addIndividual(i);
		kb.addType(i, C);

		final Set<ATermAppl> expected = new HashSet<>();
		expected.add(i);

		// Named concept, all instances
		final Set<ATermAppl> actual = kb.getInstances(C);

		assertEquals(expected, actual);
	}

	@Test
	public void testGetInstances2()
	{
		final KnowledgeBase kb = new KnowledgeBase();

		final ATermAppl C = term("C");
		final ATermAppl i = term("i");
		final ATermAppl j = term("j");

		kb.addClass(C);
		kb.addIndividual(i);
		kb.addIndividual(j);
		kb.addType(i, C);
		kb.addType(j, C);

		final Set<ATermAppl> expected = new HashSet<>();
		expected.add(i);
		expected.add(j);

		// Named concept, all instances
		final Set<ATermAppl> actual = kb.getInstances(C);

		assertEquals(expected, actual);
	}

	@Test
	public void testGetInstances3()
	{
		final KnowledgeBase kb = new KnowledgeBase();

		final ATermAppl C = term("C");
		final ATermAppl D = term("D");
		final ATermAppl i = term("i");
		final ATermAppl j = term("j");

		kb.addClass(C);
		kb.addClass(D);
		kb.addIndividual(i);
		kb.addIndividual(j);
		kb.addSubClass(C, D);
		kb.addType(i, C);
		kb.addType(j, D);

		final Set<ATermAppl> expectedD = new HashSet<>();
		expectedD.add(i);
		expectedD.add(j);
		// Named concept, all instances
		final Set<ATermAppl> actualD = kb.getInstances(D);
		assertEquals(expectedD, actualD);

		final Set<ATermAppl> expectedC = new HashSet<>();
		expectedC.add(i);
		// Named concept, all instances
		final Set<ATermAppl> actualC = kb.getInstances(C);
		assertEquals(expectedC, actualC);
	}

	@Test
	public void testGetInstances4()
	{
		final KnowledgeBase kb = new KnowledgeBase();

		final ATermAppl C = term("C");
		final ATermAppl D = term("D");
		final ATermAppl i = term("i");
		final ATermAppl j = term("j");

		kb.addClass(C);
		kb.addClass(D);
		kb.addIndividual(i);
		kb.addIndividual(j);
		kb.addSubClass(C, D);
		kb.addType(i, C);
		kb.addType(j, D);

		final Set<ATermAppl> expectedD = new HashSet<>();
		expectedD.add(j);
		// Named concept, direct instances
		final Set<ATermAppl> actualD = kb.getInstances(D, true);
		assertEquals(expectedD, actualD);

		final Set<ATermAppl> expectedC = new HashSet<>();
		expectedC.add(i);
		// Named concept, direct instances
		final Set<ATermAppl> actualC = kb.getInstances(C, true);
		assertEquals(expectedC, actualC);
	}

	@Test
	public void testGetInstances5()
	{
		final KnowledgeBase kb = new KnowledgeBase();

		final ATermAppl C = term("C");
		final ATermAppl D = term("D");
		final ATermAppl i = term("i");
		final ATermAppl j = term("j");
		final ATermAppl CaD = and(C, D);

		kb.addClass(C);
		kb.addClass(D);
		kb.addIndividual(i);
		kb.addIndividual(j);
		kb.addType(i, C);
		kb.addType(j, D);

		final Set<ATermAppl> expected = new HashSet<>();
		// Anonymous concept, all instances
		final Set<ATermAppl> actual = kb.getInstances(CaD);
		assertEquals(expected, actual);
	}

	@Test
	public void testGetInstances6()
	{
		final KnowledgeBase kb = new KnowledgeBase();

		final ATermAppl C = term("C");
		final ATermAppl D = term("D");
		final ATermAppl i = term("i");
		final ATermAppl j = term("j");
		final ATermAppl CoD = or(C, D);

		kb.addClass(C);
		kb.addClass(D);
		kb.addIndividual(i);
		kb.addIndividual(j);
		kb.addType(i, C);
		kb.addType(j, D);

		final Set<ATermAppl> expected = new HashSet<>();
		expected.add(i);
		expected.add(j);
		final Set<ATermAppl> actual = kb.getInstances(CoD);
		assertEquals(expected, actual);
	}

	@Test
	public void testGetInstances7()
	{
		final KnowledgeBase kb = new KnowledgeBase();

		final ATermAppl C = term("C");
		final ATermAppl D = term("D");
		final ATermAppl i = term("i");
		final ATermAppl j = term("j");
		final ATermAppl CaD = and(C, D);

		kb.addClass(C);
		kb.addClass(D);
		kb.addIndividual(i);
		kb.addIndividual(j);
		kb.addType(i, C);
		kb.addType(i, D);
		kb.addType(j, D);

		final Set<ATermAppl> expected = new HashSet<>();
		expected.add(i);
		final Set<ATermAppl> actual = kb.getInstances(CaD);
		assertEquals(expected, actual);
	}

	@Test
	public void testGetInstances8()
	{
		final KnowledgeBase kb = new KnowledgeBase();

		final ATermAppl C = TermFactory.term("C");
		final ATermAppl E = TermFactory.term("E");
		final ATermAppl p = TermFactory.term("p");
		final ATermAppl D = TermFactory.some(p, E);
		final ATermAppl i = TermFactory.term("i");

		kb.addClass(C);
		kb.addClass(E);
		kb.addObjectProperty(p);
		kb.addSubClass(D, C);
		kb.addIndividual(i);
		kb.addType(i, C);

		final Set<ATermAppl> expected = new HashSet<>();
		// Retrieve direct instance of anonymous concept
		final Set<ATermAppl> actual = kb.getInstances(D, true);
		assertEquals(expected, actual);
	}

	@Test
	public void testGetInstances9()
	{
		final KnowledgeBase kb = new KnowledgeBase();

		final ATermAppl C = term("C");
		final ATermAppl E = term("E");
		final ATermAppl p = term("p");
		final ATermAppl D = some(p, E);
		final ATermAppl i = term("i");

		kb.addClass(C);
		kb.addClass(E);
		kb.addObjectProperty(p);
		kb.addSubClass(D, C);
		kb.addIndividual(i);
		kb.addType(i, D);

		final Set<ATermAppl> expected = new HashSet<>();
		expected.add(i);
		// Retrieve direct instance of anonymous concept
		final Set<ATermAppl> actual = kb.getInstances(D, true);
		assertEquals(expected, actual);
	}

	@Test
	public void testGetInstances10()
	{
		final KnowledgeBase kb = new KnowledgeBase();

		final ATermAppl C = term("C");
		final ATermAppl D = term("D");
		final ATermAppl i = term("i");
		final ATermAppl j = term("j");
		final ATermAppl CoD = or(C, D);

		kb.addClass(C);
		kb.addClass(D);
		kb.addIndividual(i);
		kb.addIndividual(j);
		kb.addType(i, C);
		kb.addType(j, D);

		final Set<ATermAppl> expected = new HashSet<>();
		final Set<ATermAppl> actual = kb.getInstances(CoD, true);
		assertEquals(expected, actual);
	}

	@Test
	public void testGetInstances11()
	{
		final KnowledgeBase kb = new KnowledgeBase();

		final ATermAppl C = term("C");
		final ATermAppl D = term("D");
		final ATermAppl i = term("i");
		final ATermAppl j = term("j");
		final ATermAppl CaD = and(C, D);

		kb.addClass(C);
		kb.addClass(D);
		kb.addIndividual(i);
		kb.addIndividual(j);
		kb.addType(i, C);
		kb.addType(i, D);
		kb.addType(j, D);

		final Set<ATermAppl> expected = new HashSet<>();
		expected.add(i);
		final Set<ATermAppl> actual = kb.getInstances(CaD, true);
		assertEquals(expected, actual);
	}

	/**
	 * Tests for bug #449
	 */
	@Test
	public void testProgresMonitorRealization()
	{
		final KnowledgeBase kb = new KnowledgeBase();

		final ATermAppl A = term("A");
		final ATermAppl B = term("B");
		final ATermAppl C = term("C");
		final ATermAppl D = term("D");
		final ATermAppl E = term("E");

		kb.addClass(A);
		kb.addClass(B);
		kb.addClass(C);
		kb.addClass(D);
		kb.addClass(E);

		kb.addSubClass(B, A);
		kb.addSubClass(C, A);
		kb.addSubClass(D, B);
		kb.addSubClass(D, C);
		kb.addSubClass(E, D);

		final ATermAppl i = term("i");
		final ATermAppl j = term("j");

		kb.addIndividual(i);
		kb.addType(i, E);

		kb.addIndividual(j);
		kb.addType(j, E);

		final TestProgressMonitor progressMonitor = new TestProgressMonitor();

		kb.setTaxonomyBuilderProgressMonitor(progressMonitor);

		kb.realize();

		assertFalse(progressMonitor.isProgressLengthExceeded());
	}

	private static class TestProgressMonitor extends AbstractProgressMonitor
	{
		private boolean progressLengthExceeded;

		public TestProgressMonitor()
		{
			progressLengthExceeded = false;
		}

		@Override
		protected void updateProgress()
		{
			if (getProgress() > getProgressLength())
				progressLengthExceeded = true;
		}

		public boolean isProgressLengthExceeded()
		{
			return progressLengthExceeded;
		}
	}
}
