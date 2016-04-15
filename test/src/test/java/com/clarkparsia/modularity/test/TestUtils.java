// Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package com.clarkparsia.modularity.test;

import static java.util.Collections.emptySet;
import static java.util.Collections.singleton;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import com.clarkparsia.modularity.IncrementalClassifier;
import com.clarkparsia.modularity.ModuleExtractor;
import com.clarkparsia.modularity.PelletIncremantalReasonerFactory;
import com.clarkparsia.owlapi.OWL;
import com.clarkparsia.owlapi.OntologyUtils;
import com.clarkparsia.pellet.owlapi.PelletReasoner;
import com.clarkparsia.pellet.owlapi.PelletReasonerFactory;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.RandomAccess;
import java.util.Set;
import org.mindswap.pellet.PelletOptions;
import org.mindswap.pellet.utils.Comparators;
import org.semanticweb.owlapi.model.AddAxiom;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLException;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyChange;
import org.semanticweb.owlapi.model.RemoveAxiom;
import org.semanticweb.owlapi.reasoner.OWLReasoner;

/**
 * <p>
 * Title:
 * </p>
 * <p>
 * Description:
 * </p>
 * <p>
 * Copyright: Copyright (c) 2007
 * </p>
 * <p>
 * Company: Clark & Parsia, LLC. <http://www.clarkparsia.com>
 * </p>
 * 
 * @author Evren Sirin
 */
public class TestUtils
{
	/**
	 * Checks if there is a difference between two array of elements and prints
	 * a sorted, easy to read message showing the differences between two
	 * arrays. The elements of the array are compared with toString() values
	 * so this function is suitable only if the array elements have a unique
	 * string representation. For example, if the array element is a set then
	 * this function cannot be used reliabily.
	 * 
	 * @param expected
	 *            expected values
	 * @param computed
	 *            computed values
	 * @return <code>true</code> if there is a difference between the modules
	 */
	public static <T> void assertToStringEquals(String msg, T[] expected, T[] computed)
	{
		final Comparator<Object> comparator = Comparators.stringComparator;

		final List<T> onlyInComputed = new ArrayList<>();
		final List<T> onlyInExpected = new ArrayList<>();
		final List<T> both = new ArrayList<>();

		Arrays.sort(expected, comparator);

		Arrays.sort(computed, comparator);

		int i = 0, j = 0;
		while (i < computed.length && j < expected.length)
		{
			if (computed[i].equals(expected[j]))
			{
				both.add(computed[i]);
				i++;
				j++;
			}
			else
				if (comparator.compare(computed[i], expected[j]) < 0)
				{
					onlyInComputed.add(computed[i]);
					i++;
				}
				else
				{
					onlyInExpected.add(expected[j]);
					j++;
				}
		}

		while (i < computed.length)
		{
			onlyInComputed.add(computed[i++]);
		}

		while (j < expected.length)
		{
			onlyInExpected.add(expected[j++]);
		}

		if (!onlyInComputed.isEmpty() || !onlyInExpected.isEmpty())
		{
			System.err.println(msg);
			System.err.println("Both " + both.size() + " " + both);
			System.err.println("Computed " + onlyInComputed.size() + " " + onlyInComputed);
			System.err.println("Expected " + onlyInExpected.size() + " " + onlyInExpected);
			System.err.println();

			fail(msg);
		}
	}

	public static List<OWLOntologyChange> createChanges(OWLOntology ontology, Collection<? extends OWLAxiom> axioms, boolean add)
	{
		final List<OWLOntologyChange> changes = new ArrayList<>();
		for (final OWLAxiom axiom : axioms)
		{
			final OWLOntologyChange change = add ? new AddAxiom(ontology, axiom) : new RemoveAxiom(ontology, axiom);
			changes.add(change);
		}

		return changes;
	}

	public static <E> Set<E> flatten(Set<Set<E>> setOfSets)
	{
		final Set<E> result = new HashSet<>();
		for (final Set<E> set : setOfSets)
		{
			result.addAll(set);
		}
		return result;
	}

	public static double[] getSizes(Collection<? extends Collection<?>> collections)
	{
		final double[] sizes = new double[collections.size()];

		int i = 0;
		for (final Collection<?> collection : collections)
		{
			sizes[i++] = collection.size();
		}

		return sizes;
	}

	/**
	 * Selects a random axiom from an ontology
	 * 
	 * @param args
	 */
	public static OWLAxiom selectRandomAxiom(OWLOntology ontology) throws OWLException
	{
		final Set<OWLAxiom> selectedAxioms = selectRandomAxioms(ontology, 1);

		return selectedAxioms.iterator().next();
	}

	/**
	 * Selects a set of random axioms from an ontology
	 */
	public static Set<OWLAxiom> selectRandomAxioms(OWLOntology ontology, int count)
	{
		final Set<OWLAxiom> axioms = ontology.getAxioms();

		return selectRandomElements(axioms, count);
	}

	public static <T> Set<T> selectRandomElements(Collection<T> coll, int K)
	{
		// get the size
		final int N = coll.size();

		if (K > N)
			throw new IllegalArgumentException(K + " >= " + N);

		final List<T> list = (coll instanceof RandomAccess) ? (List<T>) coll : new ArrayList<>(coll);

		final Random rand = new Random();

		for (int k = 0; k < K; k++)
		{
			final int j = rand.nextInt(N - k) + k;
			Collections.swap(list, k, j);
		}

		return new HashSet<>(list.subList(0, K));
	}

	public static void assertClassificationEquals(OWLReasoner expected, OWLReasoner actual)
	{
		//		assertClassificationEquals( expected, actual, OWL.Nothing );
		actual.getRootOntology().classesInSignature().forEach(cls -> assertClassificationEquals(expected, actual, cls));
	}

	public static void assertClassificationEquals(OWLReasoner expected, OWLReasoner actual, OWLClass cls)
	{
		final Set<OWLClass> expectedEquivalents = expected.getEquivalentClasses(cls).getEntities();
		final Set<OWLClass> actualEquivalents = actual.getEquivalentClasses(cls).getEntities();

		System.out.println("--------equivalent-classes-----------");
		System.out.println(expectedEquivalents);
		System.out.println(actualEquivalents);
		System.out.println();
		assertEquals("Equivalents different for Class: " + cls, expectedEquivalents, actualEquivalents);

		final Set<OWLClass> expectedSupers = expected.getSuperClasses(cls, true).getFlattened();
		final Set<OWLClass> actualSupers = actual.getSuperClasses(cls, true).getFlattened();

		System.out.println("---------super-classes----------");
		System.out.println(expectedSupers);
		System.out.println(actualSupers);
		System.out.println();
		assertEquals("Supers different for Class: " + cls, expectedSupers, actualSupers);
	}

	public static void assertDisjointnessEquals(OWLReasoner expected, OWLReasoner actual)
	{
		for (final OWLClass cls : actual.getRootOntology().getClassesInSignature())
		{
			assertDisjointnessEquals(expected, actual, cls);
		}
	}

	public static void assertDisjointnessEquals(OWLReasoner expected, OWLReasoner actual, OWLClass cls)
	{
		final Set<OWLClass> expectedDisjoints = expected.getDisjointClasses(cls).getFlattened();
		final Set<OWLClass> actualDisjoints = actual.getDisjointClasses(cls).getFlattened();

		assertEquals("Disjoint classes different for Class: " + cls, expectedDisjoints, actualDisjoints);
	}

	public static void assertInstancesEquals(OWLReasoner expected, OWLReasoner actual)
	{
		for (final OWLClass cls : actual.getRootOntology().getClassesInSignature())
		{
			assertInstancesEquals(expected, actual, cls);
		}
	}

	public static void assertInstancesEquals(OWLReasoner expected, OWLReasoner actual, OWLClass cls)
	{
		final Set<OWLNamedIndividual> expectedIndividuals = expected.getInstances(cls, true).getFlattened();
		final Set<OWLNamedIndividual> actualIndividuals = actual.getInstances(cls, true).getFlattened();

		assertEquals("Instances different for Class: " + cls, expectedIndividuals, actualIndividuals);
	}

	public static void assertTypesEquals(OWLReasoner expected, OWLReasoner actual)
	{
		for (final OWLNamedIndividual ind : actual.getInstances(OWL.Thing, false).getFlattened())
		{
			assertTypesEquals(expected, actual, ind);
		}
	}

	public static void assertTypesEquals(OWLReasoner expected, OWLReasoner actual, OWLNamedIndividual individual)
	{
		final Set<OWLClass> expectedTypes = expected.getTypes(individual, true).getFlattened();
		final Set<OWLClass> actualTypes = actual.getTypes(individual, true).getFlattened();

		assertEquals("Types different for individual: " + individual, expectedTypes, actualTypes);
	}

	public static void runDisjointnessTest(OWLOntology ontology, ModuleExtractor modExtractor)
	{
		runComparisonTest(ontology, modExtractor, (expected, actual) -> assertDisjointnessEquals(expected, actual));
	}

	public static void runDisjointnessUpdateTest(OWLOntology ontology, ModuleExtractor modExtractor, Collection<OWLAxiom> additions, Collection<OWLAxiom> deletions)
	{
		runComparisonUpdateTest(ontology, modExtractor, additions, deletions, (expected, actual) -> assertDisjointnessEquals(expected, actual));
	}

	public static void runInstancesTest(OWLOntology ontology, ModuleExtractor modExtractor)
	{
		runComparisonTest(ontology, modExtractor, (expected, actual) -> assertInstancesEquals(expected, actual));
	}

	public static void runInstancesUpdateTest(OWLOntology ontology, ModuleExtractor modExtractor, Collection<OWLAxiom> additions, Collection<OWLAxiom> deletions)
	{
		runComparisonUpdateTest(ontology, modExtractor, additions, deletions, (expected, actual) -> assertInstancesEquals(expected, actual));
	}

	public static void runTypesTest(OWLOntology ontology, ModuleExtractor modExtractor)
	{
		runComparisonTest(ontology, modExtractor, (expected, actual) -> assertTypesEquals(expected, actual));
	}

	public static void runTypesUpdateTest(OWLOntology ontology, ModuleExtractor modExtractor, Collection<OWLAxiom> additions, Collection<OWLAxiom> deletions)
	{
		runComparisonUpdateTest(ontology, modExtractor, additions, deletions, (expected, actual) -> assertTypesEquals(expected, actual));
	}

	public static void runUpdateTest(OWLOntology ontology, ModuleExtractor modExtractor, Collection<OWLAxiom> additions, Collection<OWLAxiom> deletions)
	{
		runComparisonUpdateTest(ontology, modExtractor, additions, deletions, (expected, actual) -> assertClassificationEquals(expected, actual));
	}

	private static void runComparisonTest(OWLOntology ontology, ModuleExtractor modExtractor, ReasonerComparisonMethod comparisonMethod)
	{
		final PelletReasoner unified = PelletReasonerFactory.getInstance().createNonBufferingReasoner(ontology);
		final IncrementalClassifier modular = PelletIncremantalReasonerFactory.getInstance().createReasoner(ontology, modExtractor);

		PelletOptions.USE_CLASSIFICATION_MONITOR = PelletOptions.MonitorType.CONSOLE;
		modular.classify();
		unified.getKB().classify();

		comparisonMethod.compare(unified, modular);

		modular.dispose();
	}

	private static void runComparisonUpdateTest(OWLOntology ontology, ModuleExtractor modExtractor, Collection<OWLAxiom> additions, Collection<OWLAxiom> deletions, ReasonerComparisonMethod comparisonMethod)
	{
		final PelletReasoner unified = PelletReasonerFactory.getInstance().createNonBufferingReasoner(ontology);
		final IncrementalClassifier modular = PelletIncremantalReasonerFactory.getInstance().createReasoner(ontology, modExtractor);

		PelletOptions.USE_CLASSIFICATION_MONITOR = PelletOptions.MonitorType.CONSOLE;
		modular.classify();

		comparisonMethod.compare(unified, modular);

		OntologyUtils.addAxioms(ontology, additions);
		OntologyUtils.removeAxioms(ontology, deletions);

		modular.classify();
		unified.flush();
		unified.getKB().classify();

		modular.timers.print();

		comparisonMethod.compare(unified, modular);

		modular.dispose();
	}

	public static <T> Set<T> set(T... elements)
	{
		switch (elements.length)
		{
			case 0:
				return emptySet();
			case 1:
				return singleton(elements[0]);
			default:
				return new HashSet<>(Arrays.asList(elements));
		}
	}

	private interface ReasonerComparisonMethod
	{
		public void compare(OWLReasoner expected, OWLReasoner actual);
	}
}
