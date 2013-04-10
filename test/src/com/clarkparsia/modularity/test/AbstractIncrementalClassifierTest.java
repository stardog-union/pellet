// Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package com.clarkparsia.modularity.test;

import static com.clarkparsia.owlapiv3.OWL.Thing;
import static com.clarkparsia.owlapiv3.OWL.all;
import static com.clarkparsia.owlapiv3.OWL.and;
import static com.clarkparsia.owlapiv3.OWL.classAssertion;
import static com.clarkparsia.owlapiv3.OWL.declaration;
import static com.clarkparsia.owlapiv3.OWL.disjointClasses;
import static com.clarkparsia.owlapiv3.OWL.domain;
import static com.clarkparsia.owlapiv3.OWL.equivalentClasses;
import static com.clarkparsia.owlapiv3.OWL.label;
import static com.clarkparsia.owlapiv3.OWL.not;
import static com.clarkparsia.owlapiv3.OWL.or;
import static com.clarkparsia.owlapiv3.OWL.propertyAssertion;
import static com.clarkparsia.owlapiv3.OWL.range;
import static com.clarkparsia.owlapiv3.OWL.some;
import static com.clarkparsia.owlapiv3.OWL.subClassOf;

import java.util.Arrays;

import org.junit.Test;
import org.semanticweb.owlapi.model.AddImport;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLException;
import org.semanticweb.owlapi.model.OWLOntology;

import com.clarkparsia.owlapiv3.OWL;

/**
 * <p>
 * Title:
 * </p>
 * <p>
 * Description: Test modular classification for correctness against unified
 * classification
 * <p>
 * Copyright: Copyright (c) 2007
 * </p>
 * <p>
 * Company: Clark & Parsia, LLC. <http://www.clarkparsia.com>
 * </p>
 * 
 * @author Mike Smith
 */
public abstract class AbstractIncrementalClassifierTest extends AbstractModularityTest {
	public AbstractIncrementalClassifierTest() {
	}

	private void updateTest(OWLAxiom[] axioms, OWLAxiom[] additions, OWLAxiom[] deletions)
			throws OWLException {
		createOntology( axioms );

		TestUtils.runUpdateTest( ontology, createModuleExtractor(), Arrays.asList( additions ), Arrays.asList( deletions ) );
	}
	
	private void disjointnessTest(OWLAxiom[] axioms) {
		createOntology( axioms );
		
		TestUtils.runDisjointnessTest( ontology, createModuleExtractor() );
	}
	
	private void disjointnessUpdateTest(OWLAxiom[] axioms, OWLAxiom[] additions, OWLAxiom[] deletions) {
		createOntology( axioms );
		
		TestUtils.runDisjointnessUpdateTest( ontology, createModuleExtractor(), Arrays.asList( additions ), Arrays.asList( deletions ) );
	}
	
	private void instancesTest(OWLAxiom[] axioms) {
		createOntology( axioms );
		
		TestUtils.runInstancesTest( ontology, createModuleExtractor() );
	}

	private void typesTest(OWLAxiom[] axioms) {
		createOntology( axioms );
		
		TestUtils.runTypesTest( ontology, createModuleExtractor() );
	}	

	private void instancesUpdateTest(OWLAxiom[] axioms, OWLAxiom[] additions, OWLAxiom[] deletions) {
		createOntology( axioms );
		
		TestUtils.runInstancesUpdateTest( ontology, createModuleExtractor(), Arrays.asList( additions ), Arrays.asList( deletions ) );
	}

	private void typesUpdateTest(OWLAxiom[] axioms, OWLAxiom[] additions, OWLAxiom[] deletions) {
		createOntology( axioms );
		
		TestUtils.runTypesUpdateTest( ontology, createModuleExtractor(), Arrays.asList( additions ), Arrays.asList( deletions ) );
	}	
	
	@Test
	public void unsatisfiableTest1() throws OWLException {
		OWLAxiom[] axioms = {
				subClassOf( A, B ), subClassOf( A, C ), subClassOf( D, Thing ) };
		OWLAxiom[] additions = { disjointClasses( B, C ) };
		OWLAxiom[] deletions = {};

		updateTest( axioms, additions, deletions );
	}

	@Test
	public void unsatisfiableTest2() throws OWLException {
		OWLAxiom[] axioms = {
				subClassOf( C, B ), subClassOf( B, A ), subClassOf( D, Thing ) };
		OWLAxiom[] additions = { subClassOf( B, not( A ) ) };
		OWLAxiom[] deletions = {};

		updateTest( axioms, additions, deletions );
	}

	@Test
	public void indirectSubClassTest() throws OWLException {
		OWLAxiom[] axioms = {
				subClassOf( A, Thing ),
				equivalentClasses( A, some( p, C ) ),
				equivalentClasses( B, some( p, D ) ),
				subClassOf( C, Thing ), subClassOf( D, C ),
				subClassOf( E, Thing ), subClassOf( F, E ) };
		OWLAxiom[] additions = {};
		OWLAxiom[] deletions = { subClassOf( D, C ) };

		updateTest( axioms, additions, deletions );
	}

	@Test
	public void leafAddTest() throws OWLException {
		OWLAxiom[] axioms = {
				subClassOf( A, Thing ), subClassOf( B, Thing ),
				subClassOf( C, B ) };
		OWLAxiom[] additions = { subClassOf( D, A ) };
		OWLAxiom[] deletions = {};

		updateTest( axioms, additions, deletions );
	}

	/**
	 * Test that changes to make an unsatisfiable class satisfiable cause it to
	 * be reparented in the taxonomy. (Failed in r94 and earlier).
	 * 
	 * @throws OWLException
	 */
	@Test
	public void makeSatisfiable() throws OWLException {
		OWLAxiom[] axioms = {
				subClassOf( A, B ), subClassOf( A, C ), disjointClasses( B, C ) };
		OWLAxiom[] additions = {};
		OWLAxiom[] deletions = { subClassOf( A, B ) };

		updateTest( axioms, additions, deletions );
	}

	@Test
	public void annotationOnlyTest() throws OWLException {
		OWLAxiom[] axioms = {
				subClassOf( A, Thing ), subClassOf( B, A ), label( B, "B label" ) };
		OWLAxiom[] additions = {};
		OWLAxiom[] deletions = { subClassOf( B, A ) };

		updateTest( axioms, additions, deletions );
	}

	@Test
	public void internalAddTest() throws OWLException {
		OWLAxiom[] axioms = {
				subClassOf( A, Thing ), subClassOf( C, Thing ),
				subClassOf( D, Thing ) };
		OWLAxiom[] additions = {
				subClassOf( B, Thing ), subClassOf( C, B ), subClassOf( D, B ) };
		OWLAxiom[] deletions = {};

		updateTest( axioms, additions, deletions );
	}

	@Test
	public void internalDeleteTest() throws OWLException {
		OWLAxiom[] axioms = {
				subClassOf( A, Thing ), subClassOf( B, Thing ),
				subClassOf( C, B ), subClassOf( D, B ) };
		OWLAxiom[] additions = {};
		OWLAxiom[] deletions = {
				subClassOf( B, Thing ), subClassOf( C, B ), subClassOf( D, B ) };

		updateTest( axioms, additions, deletions );
	}

	@Test
	public void internalMergeTest() throws OWLException {
		OWLAxiom[] axioms = {
				subClassOf( A, Thing ), subClassOf( B, A ), subClassOf( C, B ),
				subClassOf( D, C ), subClassOf( E, B ) };
		OWLAxiom[] additions = { subClassOf( B, C ) };
		OWLAxiom[] deletions = {};

		updateTest( axioms, additions, deletions );
	}

	@Test
	public void internalSplitTest() throws OWLException {
		OWLAxiom[] axioms = {
				subClassOf( A, Thing ), subClassOf( B, A ), subClassOf( C, B ),
				subClassOf( D, C ), subClassOf( E, B ), subClassOf( B, C ) };
		OWLAxiom[] additions = {};
		OWLAxiom[] deletions = { subClassOf( B, C ) };

		updateTest( axioms, additions, deletions );
	}

	/**
	 * Test that adding and removing axioms together cancels out. This matches
	 * behavior from Protege of creating a new class, Class_1, then renaming
	 * with a useful name.  Known to fail in r136.
	 * 
	 * @throws OWLException
	 */
	@Test
	public void addAndRename() throws OWLException {
		OWLAxiom[] axioms = { subClassOf( A, Thing ), subClassOf( B, Thing ) };

		OWLAxiom[] additions = {
				declaration( C ), subClassOf( C, A ), declaration( D ),
				subClassOf( D, A ) };
		OWLAxiom[] deletions = { declaration( C ), subClassOf( C, A ) };

		updateTest( axioms, additions, deletions );
	}
	
	@Test
	public void switchSubTreeTest() throws OWLException {
		OWLAxiom[] axioms = {
				subClassOf( A, Thing ), subClassOf( B, A ), subClassOf( E, A ),
				subClassOf( C, B ), subClassOf( D, C ), subClassOf( F, E ),
				subClassOf( G, F ) };
		OWLAxiom[] additions = { subClassOf( C, E ), subClassOf( F, B ) };
		OWLAxiom[] deletions = { subClassOf( C, B ), subClassOf( F, E ) };

		updateTest( axioms, additions, deletions );
	}

	@Test
	public void indirectModuleTest() throws OWLException {
		OWLAxiom[] axioms = {
				subClassOf( A, and( B, C, some( p, C ) ) ),
				subClassOf( B, or( all( p, not( C ) ), D ) ),
				subClassOf( D, E ) };
		OWLAxiom[] additions = { subClassOf( A, not( E ) ) };
		OWLAxiom[] deletions = {};

		updateTest( axioms, additions, deletions );
	}

	/**
	 * Test that removal of all non-local axioms, but not *all* axioms
	 * referencing a class do not cause it to be removed from the class
	 * hierarchy. In this test A [= B & A [= all(p,C) , A [= B is non-local
	 * w.r.t. A and is removed. A [= all(p,C) remains, but is local w.r.t. A and
	 * contains additional entities not in the module signature, so the module
	 * for A becomes empty. (Failed in r93 and earlier)
	 * 
	 * @throws OWLException
	 */
	@Test
	public void deleteAllAxiomsInModuleTest() throws OWLException {
		OWLAxiom[] axioms = { subClassOf( A, B ), subClassOf( A, all( p, C ) ) };
		OWLAxiom[] additions = {};
		OWLAxiom[] deletions = { subClassOf( A, B ) };

		updateTest( axioms, additions, deletions );
	}
	
	/**
	 * Test that changes to remove an unsatisfiable cause it to be removed.
	 * (Failed in r94 and earlier).
	 * 
	 * @throws OWLException
	 */
	@Test
	public void deleteUnsatisfiable() throws OWLException {
		OWLAxiom[] axioms = {
				subClassOf( A, B ), subClassOf( A, C ), disjointClasses( B, C ) };
		OWLAxiom[] additions = {};
		OWLAxiom[] deletions = { subClassOf( A, B ), subClassOf( A, C ) };

		updateTest( axioms, additions, deletions );
	}

	/**
	 * Test that adding a non-local axiom is handled correctly.
	 * 
	 * @throws OWLException
	 */
	@Test
	public void addNonLocal() throws OWLException {
		OWLAxiom[] axioms = { declaration( A ) };
		OWLAxiom[] additions = { equivalentClasses( B, all( p, B ) ) };
		OWLAxiom[] deletions = {};

		updateTest( axioms, additions, deletions );
	}	
	
	@Test
	public void deleteNonLocal() throws OWLException {
		OWLAxiom[] axioms = {
				equivalentClasses( A, all( p, B ) ), subClassOf( C, all( p, B ) ),
				subClassOf( D, all( p, B ) ), subClassOf( D, C ) };
		OWLAxiom[] additions = {};
		OWLAxiom[] deletions = { subClassOf( C, all( p, B ) ) };

		updateTest( axioms, additions, deletions );
	}
	
	@Test
	public void basicDisjointnessTest() {
		OWLAxiom[] axioms = { disjointClasses( A, B ), subClassOf( C, A ), subClassOf( D, B ), equivalentClasses( E, A ) };
		
		disjointnessTest( axioms );
	}
	
	@Test
	public void basicDisjointnessUpdateTest() {
		OWLAxiom[] axioms = { disjointClasses( A, B ), subClassOf( C, A ), subClassOf( D, B ), equivalentClasses( E, A ) };
		OWLAxiom[] additions = { disjointClasses( D, C ) };
		OWLAxiom[] deletions = { disjointClasses( A, B ) };
		
		disjointnessUpdateTest( axioms, additions, deletions );
	}
	
	@Test
	public void basicInstancesTest() {
		OWLAxiom[] axioms = { classAssertion(a, A), classAssertion(b, B), domain(p, C), range(p, D), propertyAssertion(a, p, b) };
		
		instancesTest( axioms );
	}
	
	@Test
	public void basicInstancesUpdateTest() {
		OWLAxiom[] axioms = { classAssertion(a, A), classAssertion(b, B), domain(p, C), range(p, D), propertyAssertion(a, p, b) };
		OWLAxiom[] additions = { range(p, E) };
		OWLAxiom[] deletions = { range(p, D) };
		
		instancesUpdateTest( axioms, additions, deletions );
	}
	
	@Test
	public void basicTypesTest() {
		OWLAxiom[] axioms = { classAssertion(a, A), classAssertion(b, B), domain(p, C), range(p, D), propertyAssertion(a, p, b) };
		
		typesTest( axioms );
	}
	
	
	@Test
	public void basicTypesUpdateTest() {
		OWLAxiom[] axioms = { classAssertion(a, A), classAssertion(b, B), domain(p, C), range(p, D), propertyAssertion(a, p, b) };
		OWLAxiom[] additions = { range(p, E) };
		OWLAxiom[] deletions = { range(p, D) };
		
		typesUpdateTest( axioms, additions, deletions );
	}	

	@Test
	public void importsTest() throws OWLException {
		OWLAxiom[] axioms1 = { subClassOf(A, B) };
		OWLAxiom[] axioms2 = { subClassOf(B, C), subClassOf(D, E) };
		OWLAxiom[] additions = { subClassOf(A, D) };
		OWLAxiom[] deletions = {};

		OWLOntology ontology1 = OWL.Ontology(axioms1);
		OWLOntology ontology2 = OWL.Ontology(axioms2);

		try {
			OWL.manager.applyChange(new AddImport(ontology1, OWL.factory.getOWLImportsDeclaration(ontology2.getOntologyID()
			                .getOntologyIRI())));
	
			TestUtils.runUpdateTest(ontology1, createModuleExtractor(), Arrays.asList(additions), Arrays.asList(deletions));
		}
		finally {
			OWL.manager.removeOntology(ontology1);
			OWL.manager.removeOntology(ontology2);
		}
	}
}