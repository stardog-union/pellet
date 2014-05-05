// Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package com.clarkparsia.modularity.test;

import static com.clarkparsia.owlapiv3.OWL.all;
import static com.clarkparsia.owlapiv3.OWL.equivalentClasses;
import static com.clarkparsia.owlapiv3.OWL.subClassOf;
import static com.clarkparsia.owlapiv3.OWL.classAssertion;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Collections;

import org.junit.Test;
import org.mindswap.pellet.utils.SetUtils;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLException;

import com.clarkparsia.modularity.IncrementalClassifier;
import com.clarkparsia.modularity.PelletIncremantalReasonerFactory;
import com.clarkparsia.owlapiv3.OWL;
import com.clarkparsia.owlapiv3.OntologyUtils;

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
 * @author Evren Sirin
 */
public abstract class ModularityUpdateTest extends AbstractModularityTest {

	@Test
	public void addNonLocal() throws OWLException {
		OWLAxiom[] axioms = { subClassOf( A, B ), subClassOf( C, D ) };
		createOntology( axioms );

		IncrementalClassifier modular = PelletIncremantalReasonerFactory.getInstance().createReasoner( ontology );
		modular.classify();
		
		assertTrue( modular.isEntailed( subClassOf( A, B ) ) );
		assertFalse( modular.isEntailed( subClassOf( B, C ) ) );
		assertTrue( modular.isEntailed( subClassOf( C, D ) ) );

		OntologyUtils.addAxioms( ontology, Arrays.asList( equivalentClasses( D, all( p, D ) ),
				subClassOf( B, C ) ) );
		modular.classify();
		assertTrue( modular.isEntailed( subClassOf( A, B ) ) );
		assertTrue( modular.isEntailed( subClassOf( B, C ) ) );
		assertTrue( modular.isEntailed( subClassOf( C, D ) ) );

		OntologyUtils.removeAxioms( ontology, Arrays.asList( subClassOf( A, B ) ) );
		modular.classify();
		assertFalse( modular.isEntailed( subClassOf( A, B ) ) );
		assertTrue( modular.isEntailed( subClassOf( B, C ) ) );
		assertTrue( modular.isEntailed( subClassOf( D, D ) ) );

		modular.dispose();
	}
	

	@Test
	public void deleteNonLocal() throws OWLException {
		OWLAxiom[] axioms = { subClassOf( A, B ), subClassOf( C, D ), equivalentClasses( D, all( p, D ) ) };
		createOntology( axioms );

		IncrementalClassifier modular = PelletIncremantalReasonerFactory.getInstance().createReasoner( ontology );
		modular.classify();
		
		assertTrue( modular.isEntailed( subClassOf( A, B ) ) );
		assertFalse( modular.isEntailed( subClassOf( B, C ) ) );
		assertTrue( modular.isEntailed( subClassOf( C, D ) ) );

		OntologyUtils.removeAxioms( ontology, Arrays.asList( equivalentClasses( D, all( p, D ) ) ) );
		OntologyUtils.addAxioms( ontology, Arrays.asList( subClassOf( B, C ) ) );
		modular.classify();
		assertTrue( modular.isEntailed( subClassOf( A, B ) ) );
		assertTrue( modular.isEntailed( subClassOf( B, C ) ) );
		assertTrue( modular.isEntailed( subClassOf( C, D ) ) );

		OntologyUtils.removeAxioms( ontology, Arrays.asList( subClassOf( A, B ) ) );
		modular.classify();
		assertFalse( modular.isEntailed( subClassOf( A, B ) ) );
		assertTrue( modular.isEntailed( subClassOf( B, C ) ) );
		assertTrue( modular.isEntailed( subClassOf( D, D ) ) );

		modular.dispose();
	}
	
	@Test
	public void testDeferredClassification() {
		OWLAxiom[] axioms = { subClassOf( A, B ), subClassOf( C, D ) };
		createOntology( axioms );

		IncrementalClassifier modular = PelletIncremantalReasonerFactory.getInstance().createReasoner( ontology );
		modular.classify();
		
		assertTrue(modular.isClassified());
		
		assertEquals(Collections.emptySet(), modular.getTypes(a, false).getFlattened());
		
		assertTrue(modular.isRealized());
		
		OntologyUtils.addAxioms( ontology, Arrays.asList( classAssertion( a, A ) ) );
		
		// despite of having added a new fact, the classifier should still be in classified state (the axiom was an A-Box axiom)
		assertTrue(modular.isClassified());
		assertFalse(modular.isRealized());
		
		assertEquals(SetUtils.create(A, B, OWL.Thing), modular.getTypes(a, false).getFlattened());		
		assertTrue(modular.isEntailed(subClassOf( A, B )));
		assertFalse(modular.isEntailed(subClassOf( A, C )));
		
		assertTrue(modular.isRealized());
		
		// now try to add a T-Box axiom
		OntologyUtils.addAxioms( ontology, Arrays.asList( subClassOf( A, C ) ) );
		
		// the classifier should no longer be in classified state
		assertFalse(modular.isClassified());
		assertFalse(modular.isRealized());
		
		// force classification
		modular.classify();
		
		// check whether the classifier returned to the classified state
		assertTrue(modular.isClassified());
		
		assertEquals(SetUtils.create(A, B, C, D, OWL.Thing), modular.getTypes(a, false).getFlattened());
		assertTrue(modular.isEntailed(subClassOf( A, B )));
		assertTrue(modular.isEntailed(subClassOf( A, C )));
	}
}