// Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public
// License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of
// proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package org.mindswap.pellet.test;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;
import static junit.framework.Assert.fail;

import java.net.URI;
import java.util.Set;

import junit.framework.JUnit4TestAdapter;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.mindswap.pellet.PelletOptions;
import org.mindswap.pellet.owlapi.Reasoner;
import org.mindswap.pellet.utils.SetUtils;
import org.semanticweb.owl.apibinding.OWLManager;
import org.semanticweb.owl.model.AddAxiom;
import org.semanticweb.owl.model.OWLAxiom;
import org.semanticweb.owl.model.OWLClass;
import org.semanticweb.owl.model.OWLConstant;
import org.semanticweb.owl.model.OWLDataFactory;
import org.semanticweb.owl.model.OWLDataProperty;
import org.semanticweb.owl.model.OWLDataType;
import org.semanticweb.owl.model.OWLIndividual;
import org.semanticweb.owl.model.OWLObjectProperty;
import org.semanticweb.owl.model.OWLOntology;
import org.semanticweb.owl.model.OWLOntologyManager;
import org.semanticweb.owl.vocab.XSDVocabulary;

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
public class OWLAPIAxiomConversionTests {
	public static String				base			= "file:" + PelletTestSuite.base + "misc/";

	private static String				ns				= "urn:test#";

	private static OWLOntologyManager	manager			= OWLManager.createOWLOntologyManager();

	private static OWLDataFactory		factory			= manager.getOWLDataFactory();

	private static OWLClass				c1				= factory.getOWLClass( URI.create( ns
																+ "c1" ) );

	private static OWLClass				c2				= factory.getOWLClass( URI.create( ns
																+ "c2" ) );

	private static OWLClass				c3				= factory.getOWLClass( URI.create( ns
																+ "c3" ) );

	private static OWLObjectProperty	op1				= factory.getOWLObjectProperty( URI
																.create( ns + "op1" ) );

	private static OWLObjectProperty	op2				= factory.getOWLObjectProperty( URI
																.create( ns + "op2" ) );

	private static OWLDataProperty		dp1				= factory.getOWLDataProperty( URI
																.create( ns + "dp1" ) );

	private static OWLDataProperty		dp2				= factory.getOWLDataProperty( URI
																.create( ns + "dp2" ) );

	private static OWLIndividual		ind1			= factory.getOWLIndividual( URI.create( ns
																+ "ind1" ) );

	private static OWLIndividual		ind2			= factory.getOWLIndividual( URI.create( ns
																+ "ind2" ) );

	private static OWLIndividual		ind3			= factory.getOWLIndividual( URI.create( ns
																+ "ind3" ) );

	private static OWLIndividual		ind4			= factory.getOWLAnonymousIndividual( URI
																.create( ns + "ind4" ) );

	private static OWLConstant			lit1			= factory
																.getOWLTypedConstant(
																		"lit1",
																		factory
																				.getOWLDataType( XSDVocabulary.STRING
																						.getURI() ) );

	private static OWLDataType			d1				= factory.getOWLDataType( URI.create( ns
																+ "d1" ) );

	private static boolean				DEFAULT_TRACING	= PelletOptions.USE_TRACING;

	public static junit.framework.Test suite() {
		return new JUnit4TestAdapter( OWLAPIAxiomConversionTests.class );
	}

	@BeforeClass
	public static void turnOnTracing() {
		DEFAULT_TRACING = PelletOptions.USE_TRACING;
		PelletOptions.USE_TRACING = true;
	}

	@AfterClass
	public static void turnOffTracing() {
		PelletOptions.USE_TRACING = DEFAULT_TRACING;
	}

	private void testExplanation(OWLAxiom axiom) {
		try {
			URI uri = URI.create( ns + "ont" );

			manager.removeOntology( uri );

			OWLOntology ont = manager.createOntology( uri );

			manager.applyChange( new AddAxiom( ont, axiom ) );

			Reasoner reasoner = new Reasoner( manager );
			reasoner.getKB().setDoExplanation( true );
			reasoner.loadOntology( ont );

			assertTrue( "Entailment failed", reasoner.isEntailed( axiom ) );

			Set<OWLAxiom> explanation = reasoner.getExplanation();
			assertEquals( "Unexpected explanation", SetUtils.create( axiom ), explanation );
		} catch( Exception e ) {
			e.printStackTrace();

			fail( "Explanation failed" );
		}
	}

	@Test
	public void testSubClassAxiom() {
		testExplanation( factory.getOWLSubClassAxiom( c1, c2 ) );
	}

	@Test
	public void testNegativeObjectPropertyAssertion() {
		testExplanation( factory.getOWLNegativeObjectPropertyAssertionAxiom( ind1, op1, ind2 ) );
	}

	@Test
	public void testAntiSymmetricObjectPropertyAxiom() {
		testExplanation( factory.getOWLAntiSymmetricObjectPropertyAxiom( op1 ) );
	}

	@Test
	public void testReflexiveObjectPropertyAxiom() {
		testExplanation( factory.getOWLReflexiveObjectPropertyAxiom( op1 ) );
	}

	@Test
	public void testDisjointClassesAxiom() {
		testExplanation( factory.getOWLDisjointClassesAxiom( SetUtils.create( c1, c2 ) ) );
		testExplanation( factory.getOWLDisjointClassesAxiom( SetUtils.create( c1, c2, c3 ) ) );
	}

	@Test
	public void testDataPropertyDomainAxiom() {
		testExplanation( factory.getOWLDataPropertyDomainAxiom( dp1, c1 ) );
	}

	// Imports cannot be explained
	// public void visit(OWLImportsDeclaration arg0)

	// Annotations cannot be explained
	// public void visit(OWLAxiomAnnotationAxiom arg0)

	@Test
	public void testObjectPropertyDomainAxiom() {
		testExplanation( factory.getOWLObjectPropertyDomainAxiom( op1, c1 ) );
	}

	@Test
	public void testEquivalentObjectPropertiesAxiom() {
		testExplanation( factory
				.getOWLEquivalentObjectPropertiesAxiom( SetUtils.create( op1, op2 ) ) );
	}

	@Test
	public void testNegativeDataPropertyAssertion() {
		testExplanation( factory.getOWLNegativeDataPropertyAssertionAxiom( ind1, dp1, lit1 ) );
	}

	@Test
	public void testDifferentIndividualsAxiom() {
		testExplanation( factory.getOWLDifferentIndividualsAxiom( SetUtils.create( ind1, ind2 ) ) );
		testExplanation( factory.getOWLDifferentIndividualsAxiom( SetUtils
				.create( ind1, ind2, ind3 ) ) );
	}

	@Test
	public void testDisjointDataPropertiesAxiom() {
		testExplanation( factory.getOWLDisjointDataPropertiesAxiom( SetUtils.create( dp1, dp2 ) ) );
	}

	@Test
	public void testDisjointObjectPropertiesAxiom() {
		testExplanation( factory.getOWLDisjointObjectPropertiesAxiom( SetUtils.create( op1, op2 ) ) );
	}

	@Test
	public void testObjectPropertyRangeAxiom() {
		testExplanation( factory.getOWLObjectPropertyRangeAxiom( op1, c1 ) );
	}

	@Test
	public void testObjectPropertyAssertionAxiom() {
		testExplanation( factory.getOWLObjectPropertyAssertionAxiom( ind1, op1, ind2 ) );
	}

	@Test
	public void testFunctionalObjectPropertyAxiom() {
		testExplanation( factory.getOWLFunctionalObjectPropertyAxiom( op1 ) );
	}

	@Test
	public void testObjectSubPropertyAxiom() {
		testExplanation( factory.getOWLSubObjectPropertyAxiom( op1, op2 ) );
	}

	// @Test
	public void _testDisjointUnionAxiom() {
	}

	// Annotations cannot be explained
	// public void visit(OWLDeclarationAxiom arg0)

	// Annotations cannot be explained
	// public void visit(OWLEntityAnnotationAxiom arg0)

	// Annotations cannot be explained
	// public void visit(OWLOntologyAnnotationAxiom arg0)

	@Test
	public void testSymmetricObjectPropertyAxiom() {
		testExplanation( factory.getOWLSymmetricObjectPropertyAxiom( op1 ) );
	}

	// @Test
	public void testDataPropertyRangeAxiom() {
		testExplanation( factory.getOWLDataPropertyRangeAxiom( dp1, d1 ) );
	}

	@Test
	public void testFunctionalDataPropertyAxiom() {
		testExplanation( factory.getOWLFunctionalDataPropertyAxiom( dp1 ) );
	}

	@Test
	public void testEquivalentDataPropertiesAxiom() {
		testExplanation( factory.getOWLEquivalentDataPropertiesAxiom( SetUtils.create( dp1, dp2 ) ) );
	}

	@Test
	public void testClassAssertionAxiom() {
		testExplanation( factory.getOWLClassAssertionAxiom( ind1, c1 ) );
	}

	@Test
	@Ignore
	public void testClassAssertionAnonymousIndividualAxiom() {
		testExplanation( factory.getOWLClassAssertionAxiom( ind4, c1 ) );
	}

	@Test
	public void testEquivalentClassesAxiom() {
		testExplanation( factory.getOWLEquivalentClassesAxiom( SetUtils.create( c1, c2 ) ) );
	}

	@Test
	public void testDataPropertyAssertionAxiom() {
		testExplanation( factory.getOWLDataPropertyAssertionAxiom( ind1, dp1, lit1 ) );
	}

	@Test
	public void testTransitiveObjectPropertyAxiom() {
		testExplanation( factory.getOWLTransitiveObjectPropertyAxiom( op1 ) );
	}

	@Test
	public void testIrreflexiveObjectProperty() {
		testExplanation( factory.getOWLIrreflexiveObjectPropertyAxiom( op1 ) );
	}

	@Test
	public void testDataSubPropertyAxiom() {
		testExplanation( factory.getOWLSubDataPropertyAxiom( dp1, dp2 ) );
	}

	@Test
	public void testInverseFunctionalObjectPropertyAxiom() {
		testExplanation( factory.getOWLInverseFunctionalObjectPropertyAxiom( op1 ) );
	}

	@Test
	public void testSameIndividualsAxiom() {
		testExplanation( factory.getOWLSameIndividualsAxiom( SetUtils.create( ind1, ind2 ) ) );
	}

	// @Test
	public void _testObjectPropertyChainSubPropertyAxiom() {
	}

	@Test
	public void testInverseObjectPropertiesAxiom() {
		testExplanation( factory.getOWLInverseObjectPropertiesAxiom( op1, op2 ) );
	}

	// Rules cannot be explained
	// public void visit(SWRLRule arg0)
}
