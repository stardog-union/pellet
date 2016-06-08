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
import org.semanticweb.owlapi.model.OWLOntology;

/**
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

	private void updateTest(final OWLAxiom[] axioms, final OWLAxiom[] additions, final OWLAxiom[] deletions)
	{
		createOntology(axioms);

		TestUtils.runUpdateTest(_ontology, createModuleExtractor(), Arrays.asList(additions), Arrays.asList(deletions));
	}

	private void disjointnessTest(final OWLAxiom[] axioms)
	{
		createOntology(axioms);

		TestUtils.runDisjointnessTest(_ontology, createModuleExtractor());
	}

	private void disjointnessUpdateTest(final OWLAxiom[] axioms, final OWLAxiom[] additions, final OWLAxiom[] deletions)
	{
		createOntology(axioms);

		TestUtils.runDisjointnessUpdateTest(_ontology, createModuleExtractor(), Arrays.asList(additions), Arrays.asList(deletions));
	}

	private void instancesTest(final OWLAxiom[] axioms)
	{
		createOntology(axioms);

		TestUtils.runInstancesTest(_ontology, createModuleExtractor());
	}

	private void typesTest(final OWLAxiom[] axioms)
	{
		createOntology(axioms);

		TestUtils.runTypesTest(_ontology, createModuleExtractor());
	}

	private void instancesUpdateTest(final OWLAxiom[] axioms, final OWLAxiom[] additions, final OWLAxiom[] deletions)
	{
		createOntology(axioms);

		TestUtils.runInstancesUpdateTest(_ontology, createModuleExtractor(), Arrays.asList(additions), Arrays.asList(deletions));
	}

	private void typesUpdateTest(final OWLAxiom[] axioms, final OWLAxiom[] additions, final OWLAxiom[] deletions)
	{
		createOntology(axioms);

		TestUtils.runTypesUpdateTest(_ontology, createModuleExtractor(), Arrays.asList(additions), Arrays.asList(deletions));
	}

	@Test
	public void unsatisfiableTest1()
	{
		final OWLAxiom[] axioms = { subClassOf(_A, _B), subClassOf(_A, _C), subClassOf(_D, Thing) };
		final OWLAxiom[] additions = { disjointClasses(_B, _C) };
		final OWLAxiom[] deletions = {};

		updateTest(axioms, additions, deletions);
	}

	@Test
	public void unsatisfiableTest2()
	{
		final OWLAxiom[] axioms = { subClassOf(_C, _B), subClassOf(_B, _A), subClassOf(_D, Thing) };
		final OWLAxiom[] additions = { subClassOf(_B, not(_A)) };
		final OWLAxiom[] deletions = {};

		updateTest(axioms, additions, deletions);
	}

	@Test
	public void indirectSubClassTest()
	{
		final OWLAxiom[] axioms = { subClassOf(_A, Thing), equivalentClasses(_A, some(_p, _C)), equivalentClasses(_B, some(_p, _D)), subClassOf(_C, Thing), subClassOf(_D, _C), subClassOf(_E, Thing), subClassOf(_F, _E) };
		final OWLAxiom[] additions = {};
		final OWLAxiom[] deletions = { subClassOf(_D, _C) };

		updateTest(axioms, additions, deletions);
	}

	@Test
	public void leafAddTest()
	{
		final OWLAxiom[] axioms = { subClassOf(_A, Thing), subClassOf(_B, Thing), subClassOf(_C, _B) };
		final OWLAxiom[] additions = { subClassOf(_D, _A) };
		final OWLAxiom[] deletions = {};

		updateTest(axioms, additions, deletions);
	}

	/**
	 * Test that changes to make an unsatisfiable class satisfiable cause it to be reparented in the taxonomy. (Failed in r94 and earlier).
	 *
	 * @throws OWLException
	 */
	@Test
	public void makeSatisfiable()
	{
		final OWLAxiom[] axioms = { subClassOf(_A, _B), subClassOf(_A, _C), disjointClasses(_B, _C) };
		final OWLAxiom[] additions = {};
		final OWLAxiom[] deletions = { subClassOf(_A, _B) };

		updateTest(axioms, additions, deletions);
	}

	@Test
	public void annotationOnlyTest()
	{
		final OWLAxiom[] axioms = { subClassOf(_A, Thing), subClassOf(_B, _A), label(_B, "B label") };
		final OWLAxiom[] additions = {};
		final OWLAxiom[] deletions = { subClassOf(_B, _A) };

		updateTest(axioms, additions, deletions);
	}

	@Test
	public void internalAddTest()
	{
		final OWLAxiom[] axioms = { subClassOf(_A, Thing), subClassOf(_C, Thing), subClassOf(_D, Thing) };
		final OWLAxiom[] additions = { subClassOf(_B, Thing), subClassOf(_C, _B), subClassOf(_D, _B) };
		final OWLAxiom[] deletions = {};

		updateTest(axioms, additions, deletions);
	}

	@Test
	public void internalDeleteTest()
	{
		final OWLAxiom[] axioms = { subClassOf(_A, Thing), subClassOf(_B, Thing), subClassOf(_C, _B), subClassOf(_D, _B) };
		final OWLAxiom[] additions = {};
		final OWLAxiom[] deletions = { subClassOf(_B, Thing), subClassOf(_C, _B), subClassOf(_D, _B) };

		updateTest(axioms, additions, deletions);
	}

	@Test
	public void internalMergeTest()
	{
		final OWLAxiom[] axioms = { subClassOf(_A, Thing), subClassOf(_B, _A), subClassOf(_C, _B), subClassOf(_D, _C), subClassOf(_E, _B) };
		final OWLAxiom[] additions = { subClassOf(_B, _C) };
		final OWLAxiom[] deletions = {};

		updateTest(axioms, additions, deletions);
	}

	@Test
	public void internalSplitTest()
	{
		final OWLAxiom[] axioms = { subClassOf(_A, Thing), subClassOf(_B, _A), subClassOf(_C, _B), subClassOf(_D, _C), subClassOf(_E, _B), subClassOf(_B, _C) };
		final OWLAxiom[] additions = {};
		final OWLAxiom[] deletions = { subClassOf(_B, _C) };

		updateTest(axioms, additions, deletions);
	}

	/**
	 * Test that adding and removing axioms together cancels out. This matches behavior from Protege of creating a new class, Class_1, then renaming with a
	 * useful name. Known to fail in r136.
	 *
	 * @throws OWLException
	 */
	@Test
	public void addAndRename()
	{
		final OWLAxiom[] axioms = { subClassOf(_A, Thing), subClassOf(_B, Thing) };

		final OWLAxiom[] additions = { declaration(_C), subClassOf(_C, _A), declaration(_D), subClassOf(_D, _A) };
		final OWLAxiom[] deletions = { declaration(_C), subClassOf(_C, _A) };

		updateTest(axioms, additions, deletions);
	}

	@Test
	public void switchSubTreeTest()
	{
		final OWLAxiom[] axioms = { subClassOf(_A, Thing), subClassOf(_B, _A), subClassOf(_E, _A), subClassOf(_C, _B), subClassOf(_D, _C), subClassOf(_F, _E), subClassOf(_G, _F) };
		final OWLAxiom[] additions = { subClassOf(_C, _E), subClassOf(_F, _B) };
		final OWLAxiom[] deletions = { subClassOf(_C, _B), subClassOf(_F, _E) };

		updateTest(axioms, additions, deletions);
	}

	@Test
	public void indirectModuleTest()
	{
		final OWLAxiom[] axioms = { subClassOf(_A, and(_B, _C, some(_p, _C))), subClassOf(_B, or(all(_p, not(_C)), _D)), subClassOf(_D, _E) };
		final OWLAxiom[] additions = { subClassOf(_A, not(_E)) };
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
	public void deleteAllAxiomsInModuleTest()
	{
		final OWLAxiom[] axioms = { subClassOf(_A, _B), subClassOf(_A, all(_p, _C)) };
		final OWLAxiom[] additions = {};
		final OWLAxiom[] deletions = { subClassOf(_A, _B) };

		updateTest(axioms, additions, deletions);
	}

	/**
	 * Test that changes to remove an unsatisfiable cause it to be removed. (Failed in r94 and earlier).
	 *
	 * @throws OWLException
	 */
	@Test
	public void deleteUnsatisfiable()
	{
		final OWLAxiom[] axioms = { subClassOf(_A, _B), subClassOf(_A, _C), disjointClasses(_B, _C) };
		final OWLAxiom[] additions = {};
		final OWLAxiom[] deletions = { subClassOf(_A, _B), subClassOf(_A, _C) };

		updateTest(axioms, additions, deletions);
	}

	/**
	 * Test that adding a non-local axiom is handled correctly.
	 *
	 * @throws OWLException
	 */
	@Test
	public void addNonLocal()
	{
		final OWLAxiom[] axioms = { declaration(_A) };
		final OWLAxiom[] additions = { equivalentClasses(_B, all(_p, _B)) };
		final OWLAxiom[] deletions = {};

		updateTest(axioms, additions, deletions);
	}

	@Test
	public void deleteNonLocal()
	{
		final OWLAxiom[] axioms = { equivalentClasses(_A, all(_p, _B)), subClassOf(_C, all(_p, _B)), subClassOf(_D, all(_p, _B)), subClassOf(_D, _C) };
		final OWLAxiom[] additions = {};
		final OWLAxiom[] deletions = { subClassOf(_C, all(_p, _B)) };

		updateTest(axioms, additions, deletions);
	}

	@Test
	public void basicDisjointnessTest()
	{
		final OWLAxiom[] axioms = { disjointClasses(_A, _B), subClassOf(_C, _A), subClassOf(_D, _B), equivalentClasses(_E, _A) };

		disjointnessTest(axioms);
	}

	@Test
	public void basicDisjointnessUpdateTest()
	{
		final OWLAxiom[] axioms = { disjointClasses(_A, _B), subClassOf(_C, _A), subClassOf(_D, _B), equivalentClasses(_E, _A) };
		final OWLAxiom[] additions = { disjointClasses(_D, _C) };
		final OWLAxiom[] deletions = { disjointClasses(_A, _B) };

		disjointnessUpdateTest(axioms, additions, deletions);
	}

	@Test
	public void basicInstancesTest()
	{
		final OWLAxiom[] axioms = { classAssertion(_a, _A), classAssertion(_b, _B), domain(_p, _C), range(_p, _D), propertyAssertion(_a, _p, _b) };

		instancesTest(axioms);
	}

	@Test
	public void basicInstancesUpdateTest()
	{
		final OWLAxiom[] axioms = { classAssertion(_a, _A), classAssertion(_b, _B), domain(_p, _C), range(_p, _D), propertyAssertion(_a, _p, _b) };
		final OWLAxiom[] additions = { range(_p, _E) };
		final OWLAxiom[] deletions = { range(_p, _D) };

		instancesUpdateTest(axioms, additions, deletions);
	}

	@Test
	public void basicTypesTest()
	{
		final OWLAxiom[] axioms = { classAssertion(_a, _A), classAssertion(_b, _B), domain(_p, _C), range(_p, _D), propertyAssertion(_a, _p, _b) };

		typesTest(axioms);
	}

	@Test
	public void basicTypesUpdateTest()
	{
		final OWLAxiom[] axioms = { classAssertion(_a, _A), classAssertion(_b, _B), domain(_p, _C), range(_p, _D), propertyAssertion(_a, _p, _b) };
		final OWLAxiom[] additions = { range(_p, _E) };
		final OWLAxiom[] deletions = { range(_p, _D) };

		typesUpdateTest(axioms, additions, deletions);
	}

	@Test
	public void importsTest()
	{
		final OWLAxiom[] axioms1 = { subClassOf(_A, _B) };
		final OWLAxiom[] axioms2 = { subClassOf(_B, _C), subClassOf(_D, _E) };
		final OWLAxiom[] additions = { subClassOf(_A, _D) };
		final OWLAxiom[] deletions = {};

		final OWLOntology ontology1 = OWL.Ontology(axioms1);
		final OWLOntology ontology2 = OWL.Ontology(axioms2);

		try
		{
			OWL._manager.applyChange(new AddImport(ontology1, OWL._factory.getOWLImportsDeclaration(ontology2.getOntologyID().getOntologyIRI().get())));

			TestUtils.runUpdateTest(ontology1, createModuleExtractor(), Arrays.asList(additions), Arrays.asList(deletions));
		}
		finally
		{
			OWL._manager.removeOntology(ontology1);
			OWL._manager.removeOntology(ontology2);
		}
	}
}
