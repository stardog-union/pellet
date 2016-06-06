// Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public
// License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of
// proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package org.mindswap.pellet.test;

import static junit.framework.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import aterm.ATermAppl;
import com.clarkparsia.owlapi.OWL;
import com.clarkparsia.pellet.owlapi.AxiomConverter;
import com.clarkparsia.pellet.owlapi.PelletReasoner;
import com.clarkparsia.pellet.owlapi.PelletReasonerFactory;
import java.util.Set;
import junit.framework.JUnit4TestAdapter;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.mindswap.pellet.PelletOptions;
import org.mindswap.pellet.utils.SetUtils;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLDatatype;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.vocab.OWL2Datatype;

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
public class OWLAPIAxiomConversionTests
{
	public static String base = "file:" + PelletTestSuite.base + "misc/";

	private static String ns = "urn:test#";

	private static OWLOntologyManager _manager = OWLManager.createOWLOntologyManager();

	private static OWLDataFactory _factory = _manager.getOWLDataFactory();

	private static OWLClass c1 = _factory.getOWLClass(IRI.create(ns + "c1"));

	private static OWLClass c2 = _factory.getOWLClass(IRI.create(ns + "c2"));

	private static OWLClass c3 = _factory.getOWLClass(IRI.create(ns + "c3"));

	private static OWLObjectProperty _op1 = _factory.getOWLObjectProperty(IRI.create(ns + "op1"));

	private static OWLObjectProperty _op2 = _factory.getOWLObjectProperty(IRI.create(ns + "op2"));

	private static OWLDataProperty _dp1 = _factory.getOWLDataProperty(IRI.create(ns + "dp1"));

	private static OWLDataProperty _dp2 = _factory.getOWLDataProperty(IRI.create(ns + "dp2"));

	private static OWLIndividual _ind1 = _factory.getOWLNamedIndividual(IRI.create(ns + "ind1"));

	private static OWLIndividual _ind2 = _factory.getOWLNamedIndividual(IRI.create(ns + "ind2"));

	private static OWLIndividual _ind3 = _factory.getOWLNamedIndividual(IRI.create(ns + "ind3"));

	private static OWLIndividual _ind4 = _factory.getOWLNamedIndividual(IRI.create(ns + "ind4"));

	private static OWLLiteral _lit1 = _factory.getOWLLiteral("lit1", OWL2Datatype.XSD_STRING);

	private static OWLDatatype _d1 = _factory.getOWLDatatype(IRI.create(ns + "d1"));

	private static boolean DEFAULT_TRACING = PelletOptions.USE_TRACING;

	public static junit.framework.Test suite()
	{
		return new JUnit4TestAdapter(OWLAPIAxiomConversionTests.class);
	}

	@BeforeClass
	public static void turnOnTracing()
	{
		DEFAULT_TRACING = PelletOptions.USE_TRACING;
		PelletOptions.USE_TRACING = true;
	}

	@AfterClass
	public static void turnOffTracing()
	{
		PelletOptions.USE_TRACING = DEFAULT_TRACING;
	}

	private void testExplanation(final OWLAxiom axiom)
	{
		OWLOntology ont = null;
		try
		{
			ont = OWL.Ontology(axiom);

			final PelletReasoner reasoner = PelletReasonerFactory.getInstance().createReasoner(ont);
			reasoner.getKB().setDoExplanation(true);

			assertTrue("Entailment failed", reasoner.isEntailed(axiom));

			final Set<ATermAppl> terms = reasoner.getKB().getExplanationSet();
			assertTrue("Explanation incorrect " + terms, terms.size() == 1);

			final OWLAxiom explanation = new AxiomConverter(reasoner).convert(terms.iterator().next());
			assertEquals("Unexpected explanation", axiom, explanation);
		}
		finally
		{
			if (ont != null)
				_manager.removeOntology(ont);
		}
	}

	@Test
	public void testSubClassAxiom()
	{
		testExplanation(_factory.getOWLSubClassOfAxiom(c1, c2));
	}

	@Test
	public void testNegativeObjectPropertyAssertion()
	{
		testExplanation(_factory.getOWLNegativeObjectPropertyAssertionAxiom(_op1, _ind1, _ind2));
	}

	@Test
	public void testAntiSymmetricObjectPropertyAxiom()
	{
		testExplanation(_factory.getOWLAsymmetricObjectPropertyAxiom(_op1));
	}

	@Test
	public void testReflexiveObjectPropertyAxiom()
	{
		testExplanation(_factory.getOWLReflexiveObjectPropertyAxiom(_op1));
	}

	@Test
	public void testDisjointClassesAxiom()
	{
		testExplanation(_factory.getOWLDisjointClassesAxiom(SetUtils.create(c1, c2)));
		testExplanation(_factory.getOWLDisjointClassesAxiom(SetUtils.create(c1, c2, c3)));
	}

	@Test
	public void testDataPropertyDomainAxiom()
	{
		testExplanation(_factory.getOWLDataPropertyDomainAxiom(_dp1, c1));
	}

	// Imports cannot be explained
	// public void visit(OWLImportsDeclaration arg0)

	// Annotations cannot be explained
	// public void visit(OWLAxiomAnnotationAxiom arg0)

	@Test
	public void testObjectPropertyDomainAxiom()
	{
		testExplanation(_factory.getOWLObjectPropertyDomainAxiom(_op1, c1));
	}

	@Test
	public void testEquivalentObjectPropertiesAxiom()
	{
		testExplanation(_factory.getOWLEquivalentObjectPropertiesAxiom(SetUtils.create(_op1, _op2)));
	}

	@Test
	public void testNegativeDataPropertyAssertion()
	{
		testExplanation(_factory.getOWLNegativeDataPropertyAssertionAxiom(_dp1, _ind1, _lit1));
	}

	@Test
	public void testDifferentIndividualsAxiom()
	{
		testExplanation(_factory.getOWLDifferentIndividualsAxiom(SetUtils.create(_ind1, _ind2)));
		testExplanation(_factory.getOWLDifferentIndividualsAxiom(SetUtils.create(_ind1, _ind2, _ind3)));
	}

	@Test
	public void testDisjointDataPropertiesAxiom()
	{
		testExplanation(_factory.getOWLDisjointDataPropertiesAxiom(SetUtils.create(_dp1, _dp2)));
	}

	@Test
	public void testDisjointObjectPropertiesAxiom()
	{
		testExplanation(_factory.getOWLDisjointObjectPropertiesAxiom(SetUtils.create(_op1, _op2)));
	}

	@Test
	public void testObjectPropertyRangeAxiom()
	{
		testExplanation(_factory.getOWLObjectPropertyRangeAxiom(_op1, c1));
	}

	@Test
	public void testObjectPropertyAssertionAxiom()
	{
		testExplanation(_factory.getOWLObjectPropertyAssertionAxiom(_op1, _ind1, _ind2));
	}

	@Test
	public void testFunctionalObjectPropertyAxiom()
	{
		testExplanation(_factory.getOWLFunctionalObjectPropertyAxiom(_op1));
	}

	@Test
	public void testObjectSubPropertyAxiom()
	{
		testExplanation(_factory.getOWLSubObjectPropertyOfAxiom(_op1, _op2));
	}

	// @Test
	public void _testDisjointUnionAxiom()
	{
		// Nothing to do
	}

	// Annotations cannot be explained
	// public void visit(OWLDeclarationAxiom arg0)

	// Annotations cannot be explained
	// public void visit(OWLEntityAnnotationAxiom arg0)

	// Annotations cannot be explained
	// public void visit(OWLOntologyAnnotationAxiom arg0)

	@Test
	public void testSymmetricObjectPropertyAxiom()
	{
		testExplanation(_factory.getOWLSymmetricObjectPropertyAxiom(_op1));
	}

	// @Test
	public void testDataPropertyRangeAxiom()
	{
		testExplanation(_factory.getOWLDataPropertyRangeAxiom(_dp1, _d1));
	}

	@Test
	public void testFunctionalDataPropertyAxiom()
	{
		testExplanation(_factory.getOWLFunctionalDataPropertyAxiom(_dp1));
	}

	@Test
	public void testEquivalentDataPropertiesAxiom()
	{
		testExplanation(_factory.getOWLEquivalentDataPropertiesAxiom(SetUtils.create(_dp1, _dp2)));
	}

	@Test
	public void testClassAssertionAxiom()
	{
		testExplanation(_factory.getOWLClassAssertionAxiom(c1, _ind1));
	}

	@Test
	@Ignore
	public void testClassAssertionAnonymousIndividualAxiom()
	{
		testExplanation(_factory.getOWLClassAssertionAxiom(c1, _ind4));
	}

	@Test
	public void testEquivalentClassesAxiom()
	{
		testExplanation(_factory.getOWLEquivalentClassesAxiom(SetUtils.create(c1, c2)));
	}

	@Test
	public void testDataPropertyAssertionAxiom()
	{
		testExplanation(_factory.getOWLDataPropertyAssertionAxiom(_dp1, _ind1, _lit1));
	}

	@Test
	public void testTransitiveObjectPropertyAxiom()
	{
		testExplanation(_factory.getOWLTransitiveObjectPropertyAxiom(_op1));
	}

	@Test
	public void testIrreflexiveObjectProperty()
	{
		testExplanation(_factory.getOWLIrreflexiveObjectPropertyAxiom(_op1));
	}

	@Test
	public void testDataSubPropertyAxiom()
	{
		testExplanation(_factory.getOWLSubDataPropertyOfAxiom(_dp1, _dp2));
	}

	@Test
	public void testInverseFunctionalObjectPropertyAxiom()
	{
		testExplanation(_factory.getOWLInverseFunctionalObjectPropertyAxiom(_op1));
	}

	@Test
	public void testSameIndividualsAxiom()
	{
		testExplanation(_factory.getOWLSameIndividualAxiom(SetUtils.create(_ind1, _ind2)));
	}

	// @Test
	public void _testObjectPropertyChainSubPropertyAxiom()
	{
		// Nothing to do
	}

	@Test
	public void testInverseObjectPropertiesAxiom()
	{
		testExplanation(_factory.getOWLInverseObjectPropertiesAxiom(_op1, _op2));
	}

	// Rules cannot be explained
	// public void visit(SWRLRule arg0)
}
