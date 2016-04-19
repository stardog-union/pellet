// Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package com.clarkparsia.modularity.test;

import static com.clarkparsia.owlapi.OWL.Thing;
import static com.clarkparsia.owlapi.OWL.all;
import static com.clarkparsia.owlapi.OWL.and;
import static com.clarkparsia.owlapi.OWL.classAssertion;
import static com.clarkparsia.owlapi.OWL.declaration;
import static com.clarkparsia.owlapi.OWL.disjointClasses;
import static com.clarkparsia.owlapi.OWL.domain;
import static com.clarkparsia.owlapi.OWL.equivalentClasses;
import static com.clarkparsia.owlapi.OWL.label;
import static com.clarkparsia.owlapi.OWL.not;
import static com.clarkparsia.owlapi.OWL.or;
import static com.clarkparsia.owlapi.OWL.propertyAssertion;
import static com.clarkparsia.owlapi.OWL.range;
import static com.clarkparsia.owlapi.OWL.some;
import static com.clarkparsia.owlapi.OWL.subClassOf;

import com.clarkparsia.owlapi.OWL;
import java.util.Arrays;
import org.junit.Test;
import org.semanticweb.owlapi.model.AddImport;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLException;
import org.semanticweb.owlapi.model.OWLOntology;

/**
 * <p>
 * Title:
 * </p>
 * <p>
 * Description: Test modular classification for correctness against unified classification
 * <p>
 * Copyright: Copyright (c) 2007
 * </p>
 * <p>
 * Company: Clark & Parsia, LLC. <http://www.clarkparsia.com>
 * </p>
 *
 * @author Mike Smith
 */
public abstract class AbstractIncrementalClassifierTest extends AbstractModularityTest
{
	public AbstractIncrementalClassifierTest()
	{
	}

	private void updateTest(final OWLAxiom[] axioms, final OWLAxiom[] additions, final OWLAxiom[] deletions) throws OWLException
	{
		createOntology(axioms);

		TestUtils.runUpdateTest(ontology, createModuleExtractor(), Arrays.asList(additions), Arrays.asList(deletions));
	}

	private void disjointnessTest(final OWLAxiom[] axioms)
	{
		createOntology(axioms);

		TestUtils.runDisjointnessTest(ontology, createModuleExtractor());
	}

	private void disjointnessUpdateTest(final OWLAxiom[] axioms, final OWLAxiom[] additions, final OWLAxiom[] deletions)
	{
		createOntology(axioms);

		TestUtils.runDisjointnessUpdateTest(ontology, createModuleExtractor(), Arrays.asList(additions), Arrays.asList(deletions));
	}

	private void instancesTest(final OWLAxiom[] axioms)
	{
		createOntology(axioms);

		TestUtils.runInstancesTest(ontology, createModuleExtractor());
	}

	private void typesTest(final OWLAxiom[] axioms)
	{
		createOntology(axioms);

		TestUtils.runTypesTest(ontology, createModuleExtractor());
	}

	private void instancesUpdateTest(final OWLAxiom[] axioms, final OWLAxiom[] additions, final OWLAxiom[] deletions)
	{
		createOntology(axioms);

		TestUtils.runInstancesUpdateTest(ontology, createModuleExtractor(), Arrays.asList(additions), Arrays.asList(deletions));
	}

	private void typesUpdateTest(final OWLAxiom[] axioms, final OWLAxiom[] additions, final OWLAxiom[] deletions)
	{
		createOntology(axioms);

		TestUtils.runTypesUpdateTest(ontology, createModuleExtractor(), Arrays.asList(additions), Arrays.asList(deletions));
	}

	@Test
	public void unsatisfiableTest1() throws OWLException
	{
		final OWLAxiom[] axioms = { subClassOf(A, B), subClassOf(A, C), subClassOf(D, Thing) };
		final OWLAxiom[] additions = { disjointClasses(B, C) };
		final OWLAxiom[] deletions = {};

		updateTest(axioms, additions, deletions);
	}

	@Test
	public void unsatisfiableTest2() throws OWLException
	{
		final OWLAxiom[] axioms = { subClassOf(C, B), subClassOf(B, A), subClassOf(D, Thing) };
		final OWLAxiom[] additions = { subClassOf(B, not(A)) };
		final OWLAxiom[] deletions = {};

		updateTest(axioms, additions, deletions);
	}

	@Test
	public void indirectSubClassTest() throws OWLException
	{
		final OWLAxiom[] axioms = { subClassOf(A, Thing), equivalentClasses(A, some(p, C)), equivalentClasses(B, some(p, D)), subClassOf(C, Thing), subClassOf(D, C), subClassOf(E, Thing), subClassOf(F, E) };
		final OWLAxiom[] additions = {};
		final OWLAxiom[] deletions = { subClassOf(D, C) };

		updateTest(axioms, additions, deletions);
	}

	@Test
	public void leafAddTest() throws OWLException
	{
		final OWLAxiom[] axioms = { subClassOf(A, Thing), subClassOf(B, Thing), subClassOf(C, B) };
		final OWLAxiom[] additions = { subClassOf(D, A) };
		final OWLAxiom[] deletions = {};

		updateTest(axioms, additions, deletions);
	}

	/**
	 * Test that changes to make an unsatisfiable class satisfiable cause it to be reparented in the taxonomy. (Failed in r94 and earlier).
	 *
	 * @throws OWLException
	 */
	@Test
	public void makeSatisfiable() throws OWLException
	{
		final OWLAxiom[] axioms = { subClassOf(A, B), subClassOf(A, C), disjointClasses(B, C) };
		final OWLAxiom[] additions = {};
		final OWLAxiom[] deletions = { subClassOf(A, B) };

		updateTest(axioms, additions, deletions);
	}

	@Test
	public void annotationOnlyTest() throws OWLException
	{
		final OWLAxiom[] axioms = { subClassOf(A, Thing), subClassOf(B, A), label(B, "B label") };
		final OWLAxiom[] additions = {};
		final OWLAxiom[] deletions = { subClassOf(B, A) };

		updateTest(axioms, additions, deletions);
	}

	@Test
	public void internalAddTest() throws OWLException
	{
		final OWLAxiom[] axioms = { subClassOf(A, Thing), subClassOf(C, Thing), subClassOf(D, Thing) };
		final OWLAxiom[] additions = { subClassOf(B, Thing), subClassOf(C, B), subClassOf(D, B) };
		final OWLAxiom[] deletions = {};

		updateTest(axioms, additions, deletions);
	}

	@Test
	public void internalDeleteTest() throws OWLException
	{
		final OWLAxiom[] axioms = { subClassOf(A, Thing), subClassOf(B, Thing), subClassOf(C, B), subClassOf(D, B) };
		final OWLAxiom[] additions = {};
		final OWLAxiom[] deletions = { subClassOf(B, Thing), subClassOf(C, B), subClassOf(D, B) };

		updateTest(axioms, additions, deletions);
	}

	@Test
	public void internalMergeTest() throws OWLException
	{
		final OWLAxiom[] axioms = { subClassOf(A, Thing), subClassOf(B, A), subClassOf(C, B), subClassOf(D, C), subClassOf(E, B) };
		final OWLAxiom[] additions = { subClassOf(B, C) };
		final OWLAxiom[] deletions = {};

		updateTest(axioms, additions, deletions);
	}

	@Test
	public void internalSplitTest() throws OWLException
	{
		final OWLAxiom[] axioms = { subClassOf(A, Thing), subClassOf(B, A), subClassOf(C, B), subClassOf(D, C), subClassOf(E, B), subClassOf(B, C) };
		final OWLAxiom[] additions = {};
		final OWLAxiom[] deletions = { subClassOf(B, C) };

		updateTest(axioms, additions, deletions);
	}

	/**
	 * Test that adding and removing axioms together cancels out. This matches behavior from Protege of creating a new class, Class_1, then renaming with a
	 * useful name. Known to fail in r136.
	 *
	 * @throws OWLException
	 */
	@Test
	public void addAndRename() throws OWLException
	{
		final OWLAxiom[] axioms = { subClassOf(A, Thing), subClassOf(B, Thing) };

		final OWLAxiom[] additions = { declaration(C), subClassOf(C, A), declaration(D), subClassOf(D, A) };
		final OWLAxiom[] deletions = { declaration(C), subClassOf(C, A) };

		updateTest(axioms, additions, deletions);
	}

	@Test
	public void switchSubTreeTest() throws OWLException
	{
		final OWLAxiom[] axioms = { subClassOf(A, Thing), subClassOf(B, A), subClassOf(E, A), subClassOf(C, B), subClassOf(D, C), subClassOf(F, E), subClassOf(G, F) };
		final OWLAxiom[] additions = { subClassOf(C, E), subClassOf(F, B) };
		final OWLAxiom[] deletions = { subClassOf(C, B), subClassOf(F, E) };

		updateTest(axioms, additions, deletions);
	}

	@Test
	public void indirectModuleTest() throws OWLException
	{
		final OWLAxiom[] axioms = { subClassOf(A, and(B, C, some(p, C))), subClassOf(B, or(all(p, not(C)), D)), subClassOf(D, E) };
		final OWLAxiom[] additions = { subClassOf(A, not(E)) };
		final OWLAxiom[] deletions = {};

		updateTest(axioms, additions, deletions);
	}

	/**
	 * Test that removal of all non-local axioms, but not *all* axioms referencing a class do not cause it to be removed from the class hierarchy. In this test
	 * A [= B & A [= all(p,C) , A [= B is non-local w.r.t. A and is removed. A [= all(p,C) remains, but is local w.r.t. A and contains additional entities not
	 * in the module signature, so the module for A becomes empty. (Failed in r93 and earlier)
	 *
	 * @throws OWLException
	 */
	@Test
	public void deleteAllAxiomsInModuleTest() throws OWLException
	{
		final OWLAxiom[] axioms = { subClassOf(A, B), subClassOf(A, all(p, C)) };
		final OWLAxiom[] additions = {};
		final OWLAxiom[] deletions = { subClassOf(A, B) };

		updateTest(axioms, additions, deletions);
	}

	/**
	 * Test that changes to remove an unsatisfiable cause it to be removed. (Failed in r94 and earlier).
	 *
	 * @throws OWLException
	 */
	@Test
	public void deleteUnsatisfiable() throws OWLException
	{
		final OWLAxiom[] axioms = { subClassOf(A, B), subClassOf(A, C), disjointClasses(B, C) };
		final OWLAxiom[] additions = {};
		final OWLAxiom[] deletions = { subClassOf(A, B), subClassOf(A, C) };

		updateTest(axioms, additions, deletions);
	}

	/**
	 * Test that adding a non-local axiom is handled correctly.
	 *
	 * @throws OWLException
	 */
	@Test
	public void addNonLocal() throws OWLException
	{
		final OWLAxiom[] axioms = { declaration(A) };
		final OWLAxiom[] additions = { equivalentClasses(B, all(p, B)) };
		final OWLAxiom[] deletions = {};

		updateTest(axioms, additions, deletions);
	}

	@Test
	public void deleteNonLocal() throws OWLException
	{
		final OWLAxiom[] axioms = { equivalentClasses(A, all(p, B)), subClassOf(C, all(p, B)), subClassOf(D, all(p, B)), subClassOf(D, C) };
		final OWLAxiom[] additions = {};
		final OWLAxiom[] deletions = { subClassOf(C, all(p, B)) };

		updateTest(axioms, additions, deletions);
	}

	@Test
	public void basicDisjointnessTest()
	{
		final OWLAxiom[] axioms = { disjointClasses(A, B), subClassOf(C, A), subClassOf(D, B), equivalentClasses(E, A) };

		disjointnessTest(axioms);
	}

	@Test
	public void basicDisjointnessUpdateTest()
	{
		final OWLAxiom[] axioms = { disjointClasses(A, B), subClassOf(C, A), subClassOf(D, B), equivalentClasses(E, A) };
		final OWLAxiom[] additions = { disjointClasses(D, C) };
		final OWLAxiom[] deletions = { disjointClasses(A, B) };

		disjointnessUpdateTest(axioms, additions, deletions);
	}

	@Test
	public void basicInstancesTest()
	{
		final OWLAxiom[] axioms = { classAssertion(a, A), classAssertion(b, B), domain(p, C), range(p, D), propertyAssertion(a, p, b) };

		instancesTest(axioms);
	}

	@Test
	public void basicInstancesUpdateTest()
	{
		final OWLAxiom[] axioms = { classAssertion(a, A), classAssertion(b, B), domain(p, C), range(p, D), propertyAssertion(a, p, b) };
		final OWLAxiom[] additions = { range(p, E) };
		final OWLAxiom[] deletions = { range(p, D) };

		instancesUpdateTest(axioms, additions, deletions);
	}

	@Test
	public void basicTypesTest()
	{
		final OWLAxiom[] axioms = { classAssertion(a, A), classAssertion(b, B), domain(p, C), range(p, D), propertyAssertion(a, p, b) };

		typesTest(axioms);
	}

	@Test
	public void basicTypesUpdateTest()
	{
		final OWLAxiom[] axioms = { classAssertion(a, A), classAssertion(b, B), domain(p, C), range(p, D), propertyAssertion(a, p, b) };
		final OWLAxiom[] additions = { range(p, E) };
		final OWLAxiom[] deletions = { range(p, D) };

		typesUpdateTest(axioms, additions, deletions);
	}

	@Test
	public void importsTest() throws OWLException
	{
		final OWLAxiom[] axioms1 = { subClassOf(A, B) };
		final OWLAxiom[] axioms2 = { subClassOf(B, C), subClassOf(D, E) };
		final OWLAxiom[] additions = { subClassOf(A, D) };
		final OWLAxiom[] deletions = {};

		final OWLOntology ontology1 = OWL.Ontology(axioms1);
		final OWLOntology ontology2 = OWL.Ontology(axioms2);

		try
		{
			OWL.manager.applyChange(new AddImport(ontology1, OWL.factory.getOWLImportsDeclaration(ontology2.getOntologyID().getOntologyIRI().get())));

			TestUtils.runUpdateTest(ontology1, createModuleExtractor(), Arrays.asList(additions), Arrays.asList(deletions));
		}
		finally
		{
			OWL.manager.removeOntology(ontology1);
			OWL.manager.removeOntology(ontology2);
		}
	}
}
