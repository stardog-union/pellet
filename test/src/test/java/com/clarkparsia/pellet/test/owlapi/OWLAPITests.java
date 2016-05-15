// Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public
// License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of
// proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package com.clarkparsia.pellet.test.owlapi;

import static com.clarkparsia.owlapi.OWL.Class;
import static com.clarkparsia.owlapi.OWL.DataProperty;
import static com.clarkparsia.owlapi.OWL.Individual;
import static com.clarkparsia.owlapi.OWL.ObjectProperty;
import static com.clarkparsia.owlapi.OWL.all;
import static com.clarkparsia.owlapi.OWL.asymmetric;
import static com.clarkparsia.owlapi.OWL.classAssertion;
import static com.clarkparsia.owlapi.OWL.differentFrom;
import static com.clarkparsia.owlapi.OWL.disjointClasses;
import static com.clarkparsia.owlapi.OWL.disjointProperties;
import static com.clarkparsia.owlapi.OWL.equivalentClasses;
import static com.clarkparsia.owlapi.OWL.equivalentProperties;
import static com.clarkparsia.owlapi.OWL.functional;
import static com.clarkparsia.owlapi.OWL.inverse;
import static com.clarkparsia.owlapi.OWL.inverseFunctional;
import static com.clarkparsia.owlapi.OWL.irreflexive;
import static com.clarkparsia.owlapi.OWL.max;
import static com.clarkparsia.owlapi.OWL.min;
import static com.clarkparsia.owlapi.OWL.oneOf;
import static com.clarkparsia.owlapi.OWL.or;
import static com.clarkparsia.owlapi.OWL.propertyAssertion;
import static com.clarkparsia.owlapi.OWL.reflexive;
import static com.clarkparsia.owlapi.OWL.sameAs;
import static com.clarkparsia.owlapi.OWL.some;
import static com.clarkparsia.owlapi.OWL.subClassOf;
import static com.clarkparsia.owlapi.OWL.subPropertyOf;
import static com.clarkparsia.owlapi.OWL.symmetric;
import static com.clarkparsia.owlapi.OWL.transitive;
import static com.clarkparsia.owlapi.OntologyUtils.addAxioms;
import static com.clarkparsia.owlapi.OntologyUtils.loadOntology;
import static com.clarkparsia.owlapi.OntologyUtils.removeAxioms;
import static com.clarkparsia.owlapi.SWRL.classAtom;
import static com.clarkparsia.owlapi.SWRL.propertyAtom;
import static com.clarkparsia.owlapi.SWRL.rule;
import static com.clarkparsia.owlapi.SWRL.variable;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mindswap.pellet.test.PelletTestCase.assertIteratorValues;

import aterm.ATermAppl;
import com.clarkparsia.modularity.IncrementalClassifier;
import com.clarkparsia.owlapi.OWL;
import com.clarkparsia.owlapi.SWRL;
import com.clarkparsia.owlapi.XSD;
import com.clarkparsia.pellet.owlapi.AxiomConverter;
import com.clarkparsia.pellet.owlapi.PelletReasoner;
import com.clarkparsia.pellet.owlapi.PelletReasonerFactory;
import com.clarkparsia.pellet.utils.PropertiesBuilder;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;
import junit.framework.JUnit4TestAdapter;
import org.junit.Ignore;
import org.junit.Test;
import org.mindswap.pellet.KnowledgeBase;
import org.mindswap.pellet.PelletOptions;
import org.mindswap.pellet.Role;
import org.mindswap.pellet.exceptions.TimeoutException;
import org.mindswap.pellet.test.MiscTests;
import org.mindswap.pellet.test.PelletTestSuite;
import org.mindswap.pellet.utils.ATermUtils;
import org.mindswap.pellet.utils.SetUtils;
import org.mindswap.pellet.utils.Timer;
import org.mindswap.pellet.utils.progress.ConsoleProgressMonitor;
import org.mindswap.pellet.utils.progress.ProgressMonitor;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLDatatype;
import org.semanticweb.owlapi.model.OWLException;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.SWRLAtom;
import org.semanticweb.owlapi.reasoner.FreshEntityPolicy;
import org.semanticweb.owlapi.reasoner.IndividualNodeSetPolicy;
import org.semanticweb.owlapi.reasoner.NullReasonerProgressMonitor;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.reasoner.OWLReasonerConfiguration;
import org.semanticweb.owlapi.reasoner.SimpleConfiguration;
import org.semanticweb.owlapi.search.EntitySearcher;

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
 * @author Evren Sirin
 */
public class OWLAPITests extends AbstractOWLAPITests
{
	public static junit.framework.Test suite()
	{
		return new JUnit4TestAdapter(OWLAPITests.class);
	}

	@Test
	public void testOWL2()
	{
		final String ns = "http://www.example.org/test#";

		final OWLOntology ont = loadOntology(base + "owl2.owl");

		final PelletReasoner reasoner = PelletReasonerFactory.getInstance().createReasoner(ont);
		try
		{
			testOWL2Reasoner(ns, reasoner);
		}
		finally
		{
			reasoner.dispose();
		}
	}

	@Test
	public void testOWL2Incremental()
	{
		final String ns = "http://www.example.org/test#";

		final OWLOntology ont = loadOntology(base + "owl2.owl");

		final IncrementalClassifier classifier = new IncrementalClassifier(ont);

		try
		{
			// force classification
			classifier.classify();
			// force realization
			final OWLNamedIndividual ind1 = Individual(ns + "ind1");
			classifier.getTypes(ind1, true);
			testOWL2Reasoner(ns, classifier);
		}
		finally
		{
			classifier.dispose();
		}
	}

	private void testOWL2Reasoner(final String ns, final OWLReasoner reasoner)
	{
		final OWLClass C = Class(ns + "C");
		final OWLClass D = Class(ns + "D");
		final OWLClass D1 = Class(ns + "D1");
		final OWLClass D2 = Class(ns + "D2");
		final OWLClass D3 = Class(ns + "D3");

		final OWLClass test1 = Class(ns + "test1");
		final OWLClass test2 = Class(ns + "test2");
		final OWLClass test3 = Class(ns + "test3");

		final OWLClass OlderThan10 = Class(ns + "OlderThan10");
		final OWLClass YoungerThan20 = Class(ns + "YoungerThan20");
		final OWLClass Teenager = Class(ns + "Teenager");
		final OWLClass Teen = Class(ns + "Teen");

		final OWLNamedIndividual ind1 = Individual(ns + "ind1");
		final OWLNamedIndividual ind3 = Individual(ns + "ind3");
		final OWLNamedIndividual ind4 = Individual(ns + "ind4");
		final OWLNamedIndividual ind5 = Individual(ns + "ind5");
		final OWLNamedIndividual ind6 = Individual(ns + "ind6");

		final OWLObjectProperty p = ObjectProperty(ns + "p");
		final OWLObjectProperty r = ObjectProperty(ns + "r");
		final OWLObjectProperty invR = ObjectProperty(ns + "invR");
		final OWLObjectProperty ir = ObjectProperty(ns + "ir");
		final OWLObjectProperty as = ObjectProperty(ns + "as");
		final OWLObjectProperty d1 = ObjectProperty(ns + "d1");
		final OWLObjectProperty d2 = ObjectProperty(ns + "d2");

		assertTrue(reasoner.isConsistent());

		assertTrue(reasoner.isEntailed(reflexive(r)));
		assertTrue(reasoner.isEntailed(reflexive(invR)));
		assertTrue(reasoner.isEntailed(irreflexive(ir)));
		assertTrue(reasoner.isEntailed(asymmetric(as)));

		assertTrue(reasoner.isEntailed(equivalentClasses(D, or(D1, D2, D3))));
		assertTrue(reasoner.isEntailed(equivalentClasses(D, test1)));
		assertTrue(reasoner.isEntailed(disjointClasses(D1, D2)));
		assertTrue(reasoner.isEntailed(disjointClasses(D1, D3)));
		assertTrue(reasoner.isEntailed(disjointClasses(D2, D3)));

		assertTrue(reasoner.isEntailed(disjointProperties(d1, d2)));
		assertTrue(reasoner.isEntailed(disjointProperties(d2, d1)));
		assertFalse(reasoner.isEntailed(disjointProperties(p, r)));

		assertTrue(reasoner.isEntailed(propertyAssertion(ind1, r, ind1)));
		assertTrue(reasoner.isEntailed(propertyAssertion(ind1, invR, ind1)));
		assertTrue(reasoner.isEntailed(differentFrom(ind1, ind3)));
		assertTrue(reasoner.isEntailed(differentFrom(ind1, ind4)));
		assertTrue(reasoner.isEntailed(differentFrom(ind5, ind6)));
		assertTrue(reasoner.isEntailed(propertyAssertion(ind1, p, ind1)));
		assertTrue(reasoner.isEntailed(classAssertion(ind1, test2)));
		assertTrue(reasoner.isEntailed(classAssertion(ind1, test3)));
		assertIteratorValues(reasoner.getTypes(ind1, false).getFlattened().iterator(), new Object[] { OWL.Thing, C, test2, test3 });

		assertTrue(reasoner.isEntailed(subClassOf(Teenager, OlderThan10)));
		assertTrue(reasoner.isEntailed(subClassOf(Teenager, YoungerThan20)));
		assertTrue(reasoner.isEntailed(equivalentClasses(Teenager, Teen)));

		//		assertTrue( reasoner.getDataProperties().contains( DataProperty( Namespaces.OWL + "topDataProperty") ) );
		//		assertTrue( reasoner.getDataProperties().contains( DataProperty( Namespaces.OWL + "bottomDataProperty") ) );
		//		assertTrue( reasoner.getObjectProperties().contains( ObjectProperty( Namespaces.OWL + "topObjectProperty") ) );
		//		assertTrue( reasoner.getObjectProperties().contains( ObjectProperty( Namespaces.OWL + "bottomObjectProperty") ) );
	}

	@Test
	public void testUncle()
	{
		final String ns = "http://www.example.org/test#";

		final OWLOntology ont = loadOntology(base + "uncle.owl");

		final PelletReasoner reasoner = PelletReasonerFactory.getInstance().createReasoner(ont);

		final OWLNamedIndividual Bob = Individual(ns + "Bob");
		final OWLNamedIndividual Sam = Individual(ns + "Sam");

		final OWLObjectProperty uncleOf = ObjectProperty(ns + "uncleOf");

		assertPropertyValues(reasoner, Bob, uncleOf, Sam);
	}

	@Test
	public void testSibling()
	{
		final String ns = "http://www.example.org/test#";

		final OWLOntology ont = loadOntology(base + "sibling.owl");

		final PelletReasoner reasoner = PelletReasonerFactory.getInstance().createReasoner(ont);

		final OWLNamedIndividual Bob = Individual(ns + "Bob");
		final OWLNamedIndividual John = Individual(ns + "John");
		final OWLNamedIndividual Jane = Individual(ns + "Jane");

		final OWLObjectProperty hasBrother = ObjectProperty(ns + "hasBrother");
		final OWLObjectProperty hasSister = ObjectProperty(ns + "hasSister");

		assertPropertyValues(reasoner, Bob, hasBrother, John);
		assertPropertyValues(reasoner, Bob, hasSister, Jane);
	}

	public static void assertInstances(final PelletReasoner reasoner, final OWLClass subj, final boolean direct, final OWLNamedIndividual... values)
	{
		final Set<OWLNamedIndividual> expected = new HashSet<>(Arrays.asList(values));

		assertEquals(expected, reasoner.getInstances(subj, direct).getFlattened());
	}

	public static void assertPropertyValues(final PelletReasoner reasoner, final OWLNamedIndividual subj, final OWLObjectProperty pred, final OWLIndividual... values)
	{
		final Set<OWLIndividual> expected = new HashSet<>(Arrays.asList(values));

		assertEquals(expected, reasoner.getObjectPropertyValues(subj, pred).getFlattened());
	}

	public static void assertPropertyValues(final PelletReasoner reasoner, final OWLNamedIndividual subj, final OWLDataProperty pred, final OWLLiteral values)
	{
		final Set<OWLLiteral> expected = new HashSet<>(Arrays.asList(values));

		assertEquals(expected, reasoner.getDataPropertyValues(subj, pred));
	}

	public static void assertTypes(final PelletReasoner reasoner, final OWLNamedIndividual subj, final boolean direct, final OWLClass... values)
	{
		final Set<OWLClass> expected = new HashSet<>(Arrays.asList(values));

		assertEquals(expected, reasoner.getTypes(subj, direct).getFlattened());
	}

	@Test
	public void testPropertyChain()
	{
		final String ns = "http://www.example.org/test#";

		final OWLOntology ont = loadOntology(base + "propertyChain.owl");

		final PelletReasoner reasoner = PelletReasonerFactory.getInstance().createReasoner(ont);

		final OWLClass C = Class(ns + "C");
		final OWLClass S0 = Class(ns + "S0");
		final OWLClass R0 = Class(ns + "R0");
		final OWLClass R1 = Class(ns + "R1");
		final OWLObjectProperty r = ObjectProperty(ns + "r");
		final OWLObjectProperty s = ObjectProperty(ns + "s");

		final OWLNamedIndividual[] a = new OWLNamedIndividual[17];
		for (int i = 0; i < a.length; i++)
			a[i] = Individual(ns + "a" + i);

		final OWLIndividual[] theList = new OWLIndividual[] { a[1], a[2], a[3], a[4], a[5], a[6], a[8], a[10], a[12], a[14], a[16] };

		assertTrue(reasoner.isConsistent());

		assertTrue(reasoner.isEntailed(OWL.transitive(r)));
		assertFalse(reasoner.isEntailed(OWL.transitive(s)));

		assertIteratorValues(reasoner.getInstances(C, false).getFlattened().iterator(), theList);

		assertIteratorValues(reasoner.getInstances(S0, false).getFlattened().iterator(), theList);

		assertIteratorValues(reasoner.getInstances(R0, false).getFlattened().iterator(), new OWLIndividual[] { a[7], a[9] });

		assertIteratorValues(reasoner.getInstances(R1, false).getFlattened().iterator(), new OWLIndividual[] { a[2], a[3], a[4], a[5], a[6] });

		assertIteratorValues(reasoner.getObjectPropertyValues(a[0], r).getFlattened().iterator(), new OWLIndividual[] { a[7], a[9] });

		assertIteratorValues(reasoner.getObjectPropertyValues(a[1], r).getFlattened().iterator(), new OWLIndividual[] { a[2], a[3], a[4], a[5], a[6] });

		assertIteratorValues(reasoner.getObjectPropertyValues(a[0], s).getFlattened().iterator(), theList);

	}

	@Test
	public void testQualifiedCardinality1() throws OWLException
	{
		final String ns = "http://www.example.org/test#";

		final OWLOntology ont = loadOntology(base + "qcr.owl");

		final PelletReasoner reasoner = PelletReasonerFactory.getInstance().createReasoner(ont);

		final OWLClass sub = Class(ns + "sub");
		final OWLClass sup = Class(ns + "sup");

		assertTrue(reasoner.isConsistent());

		assertTrue(reasoner.isEntailed(subClassOf(sub, sup)));
		assertTrue(reasoner.getSubClasses(sup, false).getFlattened().contains(sub));
		assertTrue(reasoner.getSuperClasses(sub, false).getFlattened().contains(sup));
	}

	@Test
	public void testReflexive2()
	{
		final String ns = "http://www.example.org/test#";
		final String foaf = "http://xmlns.com/foaf/0.1/";

		final OWLOntology ont = loadOntology(base + "reflexive.owl");

		final PelletReasoner reasoner = PelletReasonerFactory.getInstance().createReasoner(ont);

		final OWLObjectProperty[] knows = { ObjectProperty(foaf + "knows"), ObjectProperty(ns + "knows2"), ObjectProperty(ns + "knows3") };

		final OWLNamedIndividual[] people = new OWLNamedIndividual[5];
		for (int i = 0; i < people.length; i++)
		{
			people[i] = Individual(ns + "P" + (i + 1));

			for (final OWLObjectProperty know : knows)
			{
				assertTrue(people[i] + " " + know, reasoner.isEntailed(propertyAssertion(people[i], know, people[i])));

				assertPropertyValues(reasoner, people[i], know, people[i]);
			}
		}
	}

	@Test
	public void testInfiniteChain() throws Exception
	{
		final OWLOntology ont = loadOntology(base + "infiniteChain.owl");

		final PelletReasoner reasoner = PelletReasonerFactory.getInstance().createReasoner(ont);

		assertTrue(!reasoner.isConsistent());
	}

	@Test
	public void testRemoveLiteralNoBuffering1() throws Exception
	{
		testRemoveLiteral("ind1", false);
	}

	@Test
	public void testRemoveLiteralNoBuffering2() throws Exception
	{
		testRemoveLiteral("ind2", false);
	}

	@Test
	public void testRemoveLiteralWithBuffering1() throws Exception
	{
		testRemoveLiteral("ind1", true);
	}

	@Test
	public void testRemoveLiteralWithBuffering2() throws Exception
	{
		testRemoveLiteral("ind2", true);
	}

	public void testRemoveLiteral(final String indName, final boolean buffering) throws Exception
	{
		final String ns = "http://www.example.org/test#";

		final OWLOntology ont = loadOntology(base + "RemoveLiteral.owl");

		final PelletReasoner reasoner = buffering ? PelletReasonerFactory.getInstance().createReasoner(ont) : PelletReasonerFactory.getInstance().createNonBufferingReasoner(ont);

		final OWLDataProperty pInt = DataProperty(ns + "pInt");
		final OWLDataProperty pDouble = DataProperty(ns + "pDouble");
		final OWLDataProperty pBoolean = DataProperty(ns + "pBoolean");

		final OWLNamedIndividual ind = Individual(ns + indName);

		final OWLLiteral valDouble = EntitySearcher.getDataPropertyValues(ind, pDouble, ont).iterator().next();
		final OWLLiteral valInt = EntitySearcher.getDataPropertyValues(ind, pInt, ont).iterator().next();
		final OWLLiteral valBoolean = EntitySearcher.getDataPropertyValues(ind, pBoolean, ont).iterator().next();

		assertTrue(reasoner.isConsistent());

		removeAxioms(ont, propertyAssertion(ind, pDouble, valDouble));
		if (buffering)
		{
			assertFalse(reasoner.getDataPropertyValues(ind, pDouble).isEmpty());
			reasoner.flush();
		}
		assertTrue(reasoner.getDataPropertyValues(ind, pDouble).isEmpty());

		removeAxioms(ont, propertyAssertion(ind, pInt, valInt));
		if (buffering)
		{
			assertFalse(reasoner.getDataPropertyValues(ind, pInt).isEmpty());
			reasoner.flush();
		}
		assertTrue(reasoner.getDataPropertyValues(ind, pInt).isEmpty());

		removeAxioms(ont, propertyAssertion(ind, pBoolean, valBoolean));
		if (buffering)
		{
			assertFalse(reasoner.getDataPropertyValues(ind, pBoolean).isEmpty());
			reasoner.flush();
		}
		assertTrue(reasoner.getDataPropertyValues(ind, pBoolean).isEmpty());

		// assertTrue( reasoner.getDataPropertyRelationships( ind ).isEmpty() );

		final OWLLiteral newVal = OWL.constant(0.0D);
		addAxioms(ont, propertyAssertion(ind, pDouble, newVal));
		if (buffering)
			reasoner.flush();

		assertTrue(reasoner.isConsistent());
	}

	@Test
	public void testFamily()
	{
		final String ns = "http://www.example.org/family#";

		final OWLOntology ont = loadOntology(base + "family.owl");
		for (final OWLAxiom axiom : ont.getAxioms())
			System.out.println(axiom);

		final PelletReasoner reasoner = PelletReasonerFactory.getInstance().createReasoner(ont);

		try
		{
			testFamily(ns, reasoner);

			reasoner.getKB().realize();

			testFamily(ns, reasoner);
		}
		finally
		{
			reasoner.dispose();
		}
	}

	@Test
	public void testFamilyIncremental()
	{
		final String ns = "http://www.example.org/family#";

		final OWLOntology ont = loadOntology(base + "family.owl");

		final IncrementalClassifier classifier = new IncrementalClassifier(ont);

		try
		{
			// force classification
			classifier.classify();

			testFamily(ns, classifier);

			// force realization
			final OWLNamedIndividual ind1 = Individual(ns + "ind1");
			classifier.getTypes(ind1, true);

			testFamily(ns, classifier);
		}
		finally
		{
			classifier.dispose();
		}
	}

	private void testFamily(final String ns, final OWLReasoner reasoner)
	{
		final OWLObjectProperty hasBrother = ObjectProperty(ns + "hasBrother");
		final OWLObjectProperty hasSon = ObjectProperty(ns + "hasSon");
		final OWLObjectProperty hasFather = ObjectProperty(ns + "hasFather");
		final OWLObjectProperty hasParent = ObjectProperty(ns + "hasParent");
		final OWLObjectProperty hasChild = ObjectProperty(ns + "hasChild");
		final OWLObjectProperty hasMother = ObjectProperty(ns + "hasMother");
		final OWLObjectProperty hasDaughter = ObjectProperty(ns + "hasDaughter");
		final OWLObjectProperty hasAncestor = ObjectProperty(ns + "hasAncestor");
		final OWLObjectProperty likes = ObjectProperty(ns + "likes");
		final OWLObjectProperty isMarriedTo = ObjectProperty(ns + "isMarriedTo");
		final OWLObjectProperty dislikes = ObjectProperty(ns + "dislikes");
		final OWLObjectProperty hasSister = ObjectProperty(ns + "hasSister");
		final OWLObjectProperty hasDescendant = ObjectProperty(ns + "hasDescendant");
		final OWLObjectProperty hasSibling = ObjectProperty(ns + "hasSibling");
		final OWLClass Child = Class(ns + "Child");
		final OWLClass Person = Class(ns + "Person");
		final OWLClass PersonWithAtLeastTwoMaleChildren = Class(ns + "PersonWithAtLeastTwoMaleChildren");
		final OWLClass PersonWithAtLeastTwoFemaleChildren = Class(ns + "PersonWithAtLeastTwoFemaleChildren");
		final OWLClass PersonWithAtLeastTwoChildren = Class(ns + "PersonWithAtLeastTwoChildren");
		final OWLClass PersonWithAtLeastFourChildren = Class(ns + "PersonWithAtLeastFourChildren");
		final OWLClass Teen = Class(ns + "Teen");
		final OWLClass Teenager = Class(ns + "Teenager");
		final OWLClass Male = Class(ns + "Male");
		final OWLClass Adult = Class(ns + "Adult");
		final OWLClass Female = Class(ns + "Female");
		final OWLClass Senior = Class(ns + "Senior");
		final OWLIndividual grandmother = Individual(ns + "grandmother");
		final OWLIndividual grandfather = Individual(ns + "grandfather");
		final OWLIndividual father = Individual(ns + "father");
		final OWLIndividual son = Individual(ns + "son");
		final OWLIndividual mother = Individual(ns + "mother");
		final OWLIndividual daughter = Individual(ns + "daughter");
		final OWLIndividual personX = Individual(ns + "personX");
		final OWLIndividual personY = Individual(ns + "personY");
		final OWLIndividual personZ = Individual(ns + "personZ");

		assertTrue(reasoner.isConsistent());

		assertTrue(reasoner.isEntailed(transitive(hasAncestor)));
		assertFalse(reasoner.isEntailed(functional(hasAncestor)));

		assertTrue(reasoner.isEntailed(transitive(hasDescendant)));
		assertFalse(reasoner.isEntailed(functional(hasDescendant)));

		assertTrue(reasoner.isEntailed(symmetric(isMarriedTo)));
		assertTrue(reasoner.isEntailed(irreflexive(isMarriedTo)));

		assertTrue(reasoner.isEntailed(subPropertyOf(hasParent, hasAncestor)));
		assertTrue(reasoner.isEntailed(subPropertyOf(hasFather, hasAncestor)));
		assertTrue(reasoner.isEntailed(subPropertyOf(hasMother, hasAncestor)));
		assertTrue(reasoner.isEntailed(subPropertyOf(hasChild, hasDescendant)));

		assertTrue(reasoner.isEntailed(disjointProperties(likes, dislikes)));
		assertTrue(reasoner.isEntailed(disjointProperties(dislikes, likes)));
		assertTrue(reasoner.isEntailed(disjointProperties(hasFather, hasMother)));
		assertTrue(reasoner.isEntailed(disjointProperties(hasMother, hasFather)));

		assertTrue(reasoner.isEntailed(classAssertion(grandfather, Person)));
		assertTrue(reasoner.isEntailed(classAssertion(grandfather, PersonWithAtLeastTwoChildren)));
		assertTrue(reasoner.isEntailed(classAssertion(grandfather, PersonWithAtLeastTwoMaleChildren)));
		assertTrue(reasoner.isEntailed(classAssertion(grandfather, Male)));
		assertTrue(reasoner.isEntailed(classAssertion(grandfather, Senior)));
		assertTrue(reasoner.isEntailed(propertyAssertion(grandfather, isMarriedTo, grandmother)));
		assertTrue(reasoner.isEntailed(propertyAssertion(grandfather, hasChild, father)));
		assertTrue(reasoner.isEntailed(propertyAssertion(grandfather, hasSon, father)));
		assertTrue(reasoner.isEntailed(differentFrom(grandfather, grandmother)));
		assertTrue(reasoner.isEntailed(differentFrom(grandfather, father)));
		assertTrue(reasoner.isEntailed(differentFrom(grandfather, mother)));
		assertTrue(reasoner.isEntailed(differentFrom(grandfather, son)));
		assertTrue(reasoner.isEntailed(differentFrom(grandfather, daughter)));

		assertTrue(reasoner.isEntailed(classAssertion(grandmother, Person)));
		assertTrue(reasoner.isEntailed(classAssertion(grandmother, Female)));
		assertTrue(reasoner.isEntailed(classAssertion(grandmother, Senior)));
		assertTrue(reasoner.isEntailed(propertyAssertion(grandmother, isMarriedTo, grandfather)));
		assertTrue(reasoner.isEntailed(propertyAssertion(grandmother, hasChild, father)));
		assertFalse(reasoner.isEntailed(propertyAssertion(grandmother, hasSon, father)));

		assertTrue(reasoner.isEntailed(classAssertion(father, Person)));
		assertTrue(reasoner.isEntailed(classAssertion(father, Male)));
		assertTrue(reasoner.isEntailed(classAssertion(father, Adult)));
		assertTrue(reasoner.isEntailed(propertyAssertion(father, hasParent, grandfather)));
		assertTrue(reasoner.isEntailed(propertyAssertion(father, hasParent, grandmother)));
		assertTrue(reasoner.isEntailed(propertyAssertion(father, hasFather, grandfather)));
		assertTrue(reasoner.isEntailed(propertyAssertion(father, hasMother, grandmother)));
		assertTrue(reasoner.isEntailed(propertyAssertion(father, hasChild, son)));
		assertTrue(reasoner.isEntailed(propertyAssertion(father, hasSon, son)));
		assertTrue(reasoner.isEntailed(propertyAssertion(father, hasChild, daughter)));
		assertFalse(reasoner.isEntailed(propertyAssertion(father, hasDaughter, daughter)));

		assertTrue(reasoner.isEntailed(classAssertion(mother, Person)));
		assertTrue(reasoner.isEntailed(classAssertion(mother, Female)));

		assertTrue(reasoner.isEntailed(classAssertion(son, Male)));
		assertTrue(reasoner.isEntailed(classAssertion(son, Teenager)));
		assertTrue(reasoner.isEntailed(classAssertion(son, Teen)));
		assertTrue(reasoner.isEntailed(propertyAssertion(son, hasParent, father)));
		assertTrue(reasoner.isEntailed(propertyAssertion(son, hasFather, father)));
		assertTrue(reasoner.isEntailed(propertyAssertion(son, hasSibling, daughter)));
		assertTrue(reasoner.isEntailed(propertyAssertion(son, hasSister, daughter)));

		assertTrue(reasoner.isEntailed(classAssertion(daughter, Female)));
		assertTrue(reasoner.isEntailed(classAssertion(daughter, Child)));
		assertTrue(reasoner.isEntailed(propertyAssertion(daughter, hasAncestor, grandfather)));
		assertTrue(reasoner.isEntailed(propertyAssertion(daughter, hasAncestor, grandmother)));
		assertTrue(reasoner.isEntailed(propertyAssertion(daughter, hasParent, father)));
		assertTrue(reasoner.isEntailed(propertyAssertion(daughter, hasFather, father)));
		assertTrue(reasoner.isEntailed(propertyAssertion(daughter, hasParent, mother)));
		assertTrue(reasoner.isEntailed(propertyAssertion(daughter, hasMother, mother)));
		assertTrue(reasoner.isEntailed(propertyAssertion(daughter, hasSibling, son)));
		assertFalse(reasoner.isEntailed(propertyAssertion(daughter, hasBrother, son)));

		assertTrue(reasoner.isEntailed(differentFrom(personX, personY)));
		assertTrue(reasoner.isEntailed(differentFrom(personX, personZ)));
		assertTrue(reasoner.isEntailed(differentFrom(personY, personZ)));

		assertTrue(reasoner.isEntailed(equivalentClasses(Teen, Teenager)));
		assertTrue(reasoner.isEntailed(subClassOf(Senior, Adult)));

		assertTrue(reasoner.isEntailed(subClassOf(PersonWithAtLeastTwoMaleChildren, Person)));
		assertTrue(reasoner.isEntailed(subClassOf(PersonWithAtLeastTwoFemaleChildren, Person)));
		assertTrue(reasoner.isEntailed(subClassOf(PersonWithAtLeastTwoChildren, Person)));
		assertTrue(reasoner.isEntailed(subClassOf(PersonWithAtLeastFourChildren, Person)));

		assertTrue(reasoner.isEntailed(subClassOf(PersonWithAtLeastFourChildren, PersonWithAtLeastTwoChildren)));
		assertTrue(reasoner.isEntailed(subClassOf(PersonWithAtLeastTwoMaleChildren, PersonWithAtLeastTwoChildren)));
		assertTrue(reasoner.isEntailed(subClassOf(PersonWithAtLeastTwoFemaleChildren, PersonWithAtLeastTwoChildren)));

		assertFalse(reasoner.isEntailed(subClassOf(PersonWithAtLeastTwoFemaleChildren, PersonWithAtLeastTwoMaleChildren)));
		assertFalse(reasoner.isEntailed(subClassOf(PersonWithAtLeastTwoMaleChildren, PersonWithAtLeastTwoFemaleChildren)));

		// _kb.timers.print();
	}

	/*
	 * Verifies that OWL 2 entity declarations are parsed from RDF/XML and handled correctly.
	 */
	//	@Test
	//	public void entityDeclarations() {
	//		String ns = "http://www.example.org/test#";
	//
	//		OWLOntology ont = loadOntology( base + "/entityDeclarations.owl" );
	//
	//		PelletReasoner reasoner = PelletReasonerFactory.getInstance().createReasoner( ont );
	//		assertTrue( reasoner.isConsistent() );
	//
	//		KnowledgeBase _kb = reasoner.getKB();
	//
	//		assertTrue( reasoner.isDefined( Individual( ns + "a" ) ) );
	//		assertEquals( 1, reasoner.getIndividuals().size() );
	//
	//		assertTrue( reasoner.isDefined( Class( ns + "C" ) ) );
	//		assertEquals( 1, reasoner.getClasses().size() );
	//		// FIXME: OWLAPI should support Datatype definition checking
	//		assertFalse( _kb.isDatatype( ATermUtils.makeTermAppl( ns + "C" ) ) );
	//
	//		assertFalse( reasoner.isDefined( Class( ns + "D" ) ) );
	//		// FIXME: OWLAPI should support Datatype definition checking
	//		// FIXME: OWLAPI _loader does not parse Datatype declarations
	//		//assertTrue( _kb.isDatatype( ATermUtils.makeTermAppl( ns + "D" ) ) );
	//
	//		/* FIXME: There is no positive check here because OWLAPI does not support annotation property declaration. */
	//		assertFalse( reasoner.isDefined( DataProperty( ns + "p" ) ) );
	//		assertFalse( reasoner.isDefined( ObjectProperty( ns + "p" ) ) );
	//		
	//		assertTrue( reasoner.isDefined( ObjectProperty( ns + "q" ) ) );
	//		assertEquals( 2 + 1, reasoner.getObjectProperties().size() );
	//		assertFalse( _kb.isAnnotationProperty( ATermUtils.makeTermAppl( ns + "r" ) ) );
	//		assertFalse( reasoner.isDefined( DataProperty( ns + "q" ) ) );
	//
	//		assertTrue( reasoner.isDefined( DataProperty( ns + "r" ) ) );
	//		assertEquals( 2 + 1, reasoner.getDataProperties().size() );
	//		assertFalse( _kb.isAnnotationProperty( ATermUtils.makeTermAppl( ns + "r" ) ) );
	//		assertFalse( reasoner.isDefined( ObjectProperty( ns + "r" ) ) );
	//	}

	@Test
	public void testAnonInverse()
	{
		final String ns = "http://www.example.org/test#";

		final OWLOntology ont = loadOntology(base + "anon_inverse.owl");

		final OWLClass C = Class(ns + "C");
		final OWLClass D = Class(ns + "D");
		final OWLObjectProperty r = ObjectProperty(ns + "r");
		final OWLClassExpression desc = some(inverse(r), D);

		final PelletReasoner reasoner = PelletReasonerFactory.getInstance().createReasoner(ont);

		assertEquals(Collections.singleton(C), reasoner.getSubClasses(desc, true).getFlattened());

		assertTrue(reasoner.isEntailed(inverseFunctional(ObjectProperty(ns + "functionalP"))));

		assertTrue(reasoner.isEntailed(functional(ObjectProperty(ns + "inverseFunctionalP"))));

		assertTrue(reasoner.isEntailed(transitive(ObjectProperty(ns + "transitiveP"))));

		assertTrue(reasoner.isEntailed(symmetric(ObjectProperty(ns + "symmetricP"))));

		assertTrue(reasoner.isEntailed(reflexive(ObjectProperty(ns + "reflexiveP"))));

		assertTrue(reasoner.isEntailed(irreflexive(ObjectProperty(ns + "irreflexiveP"))));

		assertTrue(reasoner.isEntailed(asymmetric(ObjectProperty(ns + "asymmetricP"))));

		final OWLObjectProperty p1 = ObjectProperty(ns + "p1");
		final OWLObjectProperty p2 = ObjectProperty(ns + "p2");
		final OWLObjectProperty p3 = ObjectProperty(ns + "p3");
		assertTrue(reasoner.isEntailed(equivalentProperties(p1, p2)));
		assertTrue(reasoner.isEntailed(equivalentProperties(p1, p3)));
		assertTrue(reasoner.isEntailed(equivalentProperties(p2, p3)));
	}

	@Test
	public void testDLSafeRules()
	{
		final String ns = "http://owldl.com/ontologies/dl-safe.owl#";

		final OWLOntology ont = loadOntology(base + "dl-safe.owl");

		final PelletReasoner reasoner = PelletReasonerFactory.getInstance().createReasoner(ont);

		// OWLObjectProperty father = factory.getOWLObjectProperty( URI.create(
		// ns + "father" ) );
		final OWLObjectProperty hates = ObjectProperty(ns + "hates");
		final OWLObjectProperty sibling = ObjectProperty(ns + "sibling");

		final OWLClass BadChild = Class(ns + "BadChild");
		final OWLClass Child = Class(ns + "Child");
		// OWLClass GoodChild = Class( ns +
		// "GoodChild" );
		final OWLClass Grandchild = Class(ns + "Grandchild");
		final OWLClass Person = Class(ns + "Person");

		final OWLNamedIndividual Abel = Individual(ns + "Abel");
		final OWLNamedIndividual Cain = Individual(ns + "Cain");
		final OWLNamedIndividual Oedipus = Individual(ns + "Oedipus");
		final OWLNamedIndividual Remus = Individual(ns + "Remus");
		final OWLNamedIndividual Romulus = Individual(ns + "Romulus");

		for (int test = 0; test < 2; test++)
		{
			if (test != 0)
				reasoner.prepareReasoner();

			assertTrue(reasoner.isEntailed(propertyAssertion(Abel, sibling, Cain)));

			assertPropertyValues(reasoner, Abel, sibling, Cain);

			assertTrue(reasoner.isEntailed(propertyAssertion(Cain, sibling, Abel)));

			assertPropertyValues(reasoner, Cain, sibling, Abel);

			assertTrue(reasoner.isEntailed(propertyAssertion(Cain, hates, Abel)));

			assertFalse(reasoner.isEntailed(propertyAssertion(Abel, hates, Cain)));

			assertTrue(reasoner.isEntailed(classAssertion(Cain, Grandchild)));

			assertTrue(reasoner.isEntailed(classAssertion(Cain, BadChild)));

			assertFalse(reasoner.isEntailed(propertyAssertion(Romulus, sibling, Remus)));

			assertTrue(reasoner.isEntailed(classAssertion(Romulus, Grandchild)));

			assertFalse(reasoner.isEntailed(classAssertion(Romulus, BadChild)));

			assertTrue(reasoner.isEntailed(classAssertion(Oedipus, Child)));
		}

		assertIteratorValues(reasoner.getTypes(Cain, true).getFlattened().iterator(), new Object[] { BadChild, Child, Person });
	}

	@Test
	public void testDLSafeConstants()
	{
		final String ns = "http://owldl.com/ontologies/dl-safe-constants.owl#";

		final OWLOntology ont = loadOntology(base + "dl-safe-constants.owl");

		final PelletReasoner reasoner = PelletReasonerFactory.getInstance().createReasoner(ont);

		final OWLClass DreamTeamMember = Class(ns + "DreamTeamMember");
		final OWLClass DreamTeamMember1 = Class(ns + "DreamTeamMember1");
		final OWLClass DreamTeamMember2 = Class(ns + "DreamTeamMember2");

		final OWLIndividual Alice = Individual(ns + "Alice");
		final OWLIndividual Bob = Individual(ns + "Bob");
		final OWLIndividual Charlie = Individual(ns + "Charlie");

		for (int test = 0; test < 1; test++)
		{
			if (test != 0)
				reasoner.prepareReasoner();

			assertIteratorValues(reasoner.getInstances(DreamTeamMember, false).getFlattened().iterator(), new Object[] { Alice, Bob, Charlie });

			assertIteratorValues(reasoner.getInstances(DreamTeamMember1, false).getFlattened().iterator(), new Object[] { Alice, Bob, Charlie });

			assertIteratorValues(reasoner.getInstances(DreamTeamMember2, false).getFlattened().iterator(), new Object[] { Alice, Bob, Charlie });
		}
	}

	@Test
	public void testInvalidTransitivity() throws Exception
	{
		final String ns = "http://www.example.org/test#";

		final OWLClass C = Class(ns + "C");

		final OWLObjectProperty p1 = ObjectProperty(ns + "p1");
		final OWLObjectProperty p2 = ObjectProperty(ns + "p2");

		final OWLIndividual x = Individual(ns + "x");
		final OWLIndividual y = Individual(ns + "y");
		final OWLIndividual z = Individual(ns + "z");

		final OWLOntology ont = OWL.Ontology(transitive(p1), classAssertion(x, all(p1, C)), propertyAssertion(x, p1, y), propertyAssertion(y, p1, z));

		PelletReasoner reasoner = PelletReasonerFactory.getInstance().createReasoner(ont);

		assertTrue(reasoner.isEntailed(classAssertion(y, C)));
		assertTrue(reasoner.isEntailed(classAssertion(z, C)));

		final OWLAxiom[] axioms = new OWLAxiom[] { functional(p1), inverseFunctional(p1), irreflexive(p1), asymmetric(p1), disjointProperties(p1, p2), subClassOf(C, min(p1, 2)), classAssertion(x, max(p1, 3)), disjointClasses(C, min(p1, 2)) };

		for (final OWLAxiom axiom : axioms)
		{
			addAxioms(ont, axiom);

			reasoner = PelletReasonerFactory.getInstance().createReasoner(ont);
			assertTrue(axiom.toString(), reasoner.isEntailed(classAssertion(y, C)));
			assertFalse(axiom.toString(), reasoner.isEntailed(classAssertion(z, C)));

			removeAxioms(ont, axiom);
		}
	}

	@Test
	public void testInvalidTransitivity2()
	{
		final OWLOntology ont = loadOntology(base + "invalidTransitivity.owl");

		final PelletReasoner reasoner = PelletReasonerFactory.getInstance().createReasoner(ont);

		final KnowledgeBase kb = reasoner.getKB();
		kb.prepare();

		for (final Role r : kb.getRBox().getRoles())
			if (!ATermUtils.isBuiltinProperty(r.getName()))
			{
				assertTrue(r.toString(), r.isSimple());
				assertFalse(r.toString(), r.isTransitive());
			}

		for (final ATermAppl p : kb.getObjectProperties())
			if (!ATermUtils.isBuiltinProperty(p))
				assertFalse(p.toString(), kb.isTransitiveProperty(p));
	}

	@Test
	public void testSameAs1()
	{
		final String ns = "urn:test:";

		final IRI ontIRI = IRI.create(base + "invalidTransitivity.owl");

		final OWLNamedIndividual a = Individual(ns + "a");
		final OWLNamedIndividual b = Individual(ns + "b");
		final OWLNamedIndividual c = Individual(ns + "c");

		final OWLObjectProperty p = ObjectProperty(ns + "p");
		final OWLObjectProperty q = ObjectProperty(ns + "q");

		final Set<OWLAxiom> axioms = new HashSet<>();

		axioms.add(propertyAssertion(a, p, c));

		axioms.add(propertyAssertion(b, p, b));

		axioms.add(propertyAssertion(c, q, a));

		axioms.add(sameAs(b, c));

		axioms.add(propertyAssertion(a, q, c));

		final OWLOntology ont = OWL.Ontology(axioms, ontIRI);

		final PelletReasoner reasoner = PelletReasonerFactory.getInstance().createReasoner(ont);

		assertEquals(SetUtils.create(a), reasoner.getSameIndividuals(a).getEntities());
		assertEquals(SetUtils.create(b, c), reasoner.getSameIndividuals(b).getEntities());
		assertEquals(SetUtils.create(b, c), reasoner.getSameIndividuals(c).getEntities());

		assertPropertyValues(reasoner, a, p, b, c);

		assertPropertyValues(reasoner, a, q, b, c);

		assertPropertyValues(reasoner, b, p, b, c);

		assertPropertyValues(reasoner, b, q, a);

		assertPropertyValues(reasoner, c, p, b, c);

		assertPropertyValues(reasoner, c, q, a);
	}

	@Test
	public void testSameAs3() throws OWLException
	{
		final String ns = "urn:test:";

		final IRI ontIRI = IRI.create(base + "test.owl");

		final OWLNamedIndividual i1 = Individual(ns + "i1");
		final OWLNamedIndividual i2 = Individual(ns + "i2");
		final OWLNamedIndividual i3 = Individual(ns + "i3");

		final OWLClass c = Class(ns + "c");

		final Set<OWLAxiom> axioms = new HashSet<>();

		axioms.add(equivalentClasses(c, oneOf(i1, i2)));

		axioms.add(classAssertion(i3, c));

		final OWLOntology ont = OWL.Ontology(axioms, ontIRI);

		final PelletReasoner reasoner = PelletReasonerFactory.getInstance().createReasoner(ont);

		assertTrue(!reasoner.isEntailed(sameAs(i1, i2)));
		assertTrue(!reasoner.isEntailed(sameAs(i1, i3)));
		assertEquals(SetUtils.create(i1), reasoner.getSameIndividuals(i1).getEntities());

		assertTrue(!reasoner.isEntailed(sameAs(i2, i1)));
		assertTrue(!reasoner.isEntailed(sameAs(i2, i3)));
		assertEquals(SetUtils.create(i2), reasoner.getSameIndividuals(i2).getEntities());

		assertTrue(!reasoner.isEntailed(sameAs(i3, i1)));
		assertTrue(!reasoner.isEntailed(sameAs(i3, i2)));
		assertEquals(SetUtils.create(i3), reasoner.getSameIndividuals(i3).getEntities());

	}

	public static class TimedProgressMonitor extends ConsoleProgressMonitor
	{
		private final int limit; // in milliseconds

		public TimedProgressMonitor(final int limit)
		{
			this.limit = limit;
		}

		@Override
		public void taskFinished()
		{
			super.taskFinished();
		}

		@Override
		public void taskStarted()
		{
			super.taskStarted();
		}

		@Override
		public boolean isCanceled()
		{
			final long elapsedTime = _timer.getElapsed();
			return elapsedTime > limit;
		}
	}

	@Test
	// This tests ticket 148
	// Canceling realization with REALIZE_BY_INDIVIDUAL=false throws an NPE
	public void testRealizeByIndividualsNPE() throws Exception
	{
		final Properties newOptions = PropertiesBuilder.singleton("REALIZE_INDIVIDUAL_AT_A_TIME", "true");
		final Properties savedOptions = PelletOptions.setOptions(newOptions);

		try
		{
			final ProgressMonitor monitor = new TimedProgressMonitor(1);

			final OWLOntology ont = loadOntology(base + "food.owl");

			final PelletReasoner pellet = PelletReasonerFactory.getInstance().createReasoner(ont);
			final KnowledgeBase kb = pellet.getKB();

			kb.classify();

			kb.getTaxonomyBuilder().setProgressMonitor(monitor);

			kb.realize();

			assertFalse(kb.isRealized());
		}
		finally
		{
			PelletOptions.setOptions(savedOptions);
		}
	}

	@Test
	// This tests ticket 147
	// Not having _timeout functionality in classification and realization makes
	// it harder to interrupt these processes
	public void testClassificationTimeout() throws Exception
	{
		boolean timeout = false;

		final OWLOntology ont = loadOntology(base + "food.owl");

		final PelletReasoner pellet = PelletReasonerFactory.getInstance().createReasoner(ont);
		final KnowledgeBase kb = pellet.getKB();

		final Timer timer = kb.timers.createTimer("classify");
		timer.setTimeout(1);

		try
		{
			kb.classify();
		}
		catch (final TimeoutException e)
		{
			timeout = true;
		}

		assertTrue(timeout);
		assertFalse(kb.isClassified());
	}

	@Test
	// This tests ticket 147
	// Not having _timeout functionality in classification and realization makes
	// it harder to interrupt these processes
	public void testRealizationTimeout() throws Exception
	{
		boolean timeout = false;

		final OWLOntology ont = loadOntology("file:" + PelletTestSuite.base + "modularity/SWEET.owl");

		final PelletReasoner pellet = PelletReasonerFactory.getInstance().createReasoner(ont);
		final KnowledgeBase kb = pellet.getKB();

		final Timer timer = kb.timers.createTimer("realize");
		timer.setTimeout(1);

		try
		{
			kb.classify();
		}
		catch (final TimeoutException e)
		{
			timeout = true;
		}

		assertFalse(timeout);
		assertTrue(kb.isClassified());

		long time = System.currentTimeMillis();
		try
		{
			kb.realize();
			time = System.currentTimeMillis() - time;
		}
		catch (final TimeoutException e)
		{
			timeout = true;
		}

		assertTrue("Timeout failed: " + timer + "\nAll _timers:\n" + kb.timers, timeout);
		assertFalse(kb.isRealized());
	}

	@Test
	public void testAxiomConverterRules1()
	{
		final KnowledgeBase kb = new KnowledgeBase();
		final AxiomConverter converter = new AxiomConverter(kb, OWL.manager.getOWLDataFactory());

		final ATermAppl C = ATermUtils.makeTermAppl("C");
		final ATermAppl D = ATermUtils.makeTermAppl("D");
		final ATermAppl x = ATermUtils.makeVar("x");

		kb.addClass(C);
		kb.addClass(D);

		final ATermAppl[] head = new ATermAppl[] { ATermUtils.makeTypeAtom(x, D) };
		final ATermAppl[] body = new ATermAppl[] { ATermUtils.makeTypeAtom(x, C) };

		final ATermAppl rule = ATermUtils.makeRule(head, body);

		final OWLAxiom actual = converter.convert(rule);

		final Set<SWRLAtom> antecedent = new HashSet<>();
		final Set<SWRLAtom> consequent = new HashSet<>();

		antecedent.add(classAtom(Class("C"), variable("x")));
		consequent.add(classAtom(Class("D"), variable("x")));

		final OWLAxiom expected = rule(antecedent, consequent);

		assertEquals(expected, actual);
	}

	@Test
	public void testAxiomConverterRules1b()
	{
		final KnowledgeBase kb = new KnowledgeBase();
		final AxiomConverter converter = new AxiomConverter(kb, OWL.manager.getOWLDataFactory());

		final ATermAppl C = ATermUtils.makeTermAppl("C");
		final ATermAppl D = ATermUtils.makeTermAppl("D");
		final ATermAppl x = ATermUtils.makeVar("x");
		final ATermAppl name = ATermUtils.makeTermAppl("MyRule");

		kb.addClass(C);
		kb.addClass(D);

		final ATermAppl[] head = new ATermAppl[] { ATermUtils.makeTypeAtom(x, D) };
		final ATermAppl[] body = new ATermAppl[] { ATermUtils.makeTypeAtom(x, C) };

		final ATermAppl rule = ATermUtils.makeRule(name, head, body);

		final OWLAxiom actual = converter.convert(rule);

		final Set<SWRLAtom> antecedent = new HashSet<>();
		final Set<SWRLAtom> consequent = new HashSet<>();

		antecedent.add(classAtom(Class("C"), variable("x")));
		consequent.add(classAtom(Class("D"), variable("x")));

		final OWLAxiom expected = rule(name.getName(), antecedent, consequent);

		assertEquals(expected, actual);
	}

	public void testAxiomConverterRules1c()
	{
		final KnowledgeBase kb = new KnowledgeBase();
		final AxiomConverter converter = new AxiomConverter(kb, OWL.manager.getOWLDataFactory());

		final ATermAppl C = ATermUtils.makeTermAppl("C");
		final ATermAppl D = ATermUtils.makeTermAppl("D");
		final ATermAppl x = ATermUtils.makeVar("x");
		final ATermAppl name = ATermUtils.makeBnode("MyRule");

		kb.addClass(C);
		kb.addClass(D);

		final ATermAppl[] head = new ATermAppl[] { ATermUtils.makeTypeAtom(x, D) };
		final ATermAppl[] body = new ATermAppl[] { ATermUtils.makeTypeAtom(x, C) };

		final ATermAppl rule = ATermUtils.makeRule(name, head, body);

		final OWLAxiom actual = converter.convert(rule);

		final Set<SWRLAtom> antecedent = new HashSet<>();
		final Set<SWRLAtom> consequent = new HashSet<>();

		antecedent.add(classAtom(Class("C"), variable("x")));
		consequent.add(classAtom(Class("D"), variable("x")));

		final OWLAxiom expected = rule(name.getArgument(0).toString(), true, antecedent, consequent);

		assertEquals(expected, actual);
	}

	@Test
	public void testAxiomConverterRules2()
	{
		final KnowledgeBase kb = new KnowledgeBase();
		final AxiomConverter converter = new AxiomConverter(kb, OWL.manager.getOWLDataFactory());

		final ATermAppl C = ATermUtils.makeTermAppl("C");
		final ATermAppl D = ATermUtils.makeTermAppl("D");
		final ATermAppl i = ATermUtils.makeTermAppl("i");

		kb.addClass(C);
		kb.addClass(D);
		kb.addIndividual(i);

		final ATermAppl[] head = new ATermAppl[] { ATermUtils.makeTypeAtom(i, D) };
		final ATermAppl[] body = new ATermAppl[] { ATermUtils.makeTypeAtom(i, C) };

		final ATermAppl rule = ATermUtils.makeRule(head, body);

		final OWLAxiom actual = converter.convert(rule);

		final Set<SWRLAtom> antecedent = new HashSet<>();
		final Set<SWRLAtom> consequent = new HashSet<>();

		antecedent.add(classAtom(Class("C"), SWRL.individual(OWL.Individual("i"))));
		consequent.add(classAtom(Class("D"), SWRL.individual(OWL.Individual("i"))));

		final OWLAxiom expected = rule(antecedent, consequent);

		assertEquals(expected, actual);
	}

	@Test
	public void testAxiomConverterRules3()
	{
		final KnowledgeBase kb = new KnowledgeBase();
		final OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
		final OWLDataFactory df = manager.getOWLDataFactory();
		final AxiomConverter converter = new AxiomConverter(kb, df);

		final ATermAppl p = ATermUtils.makeTermAppl("p");
		final ATermAppl q = ATermUtils.makeTermAppl("q");
		final ATermAppl x = ATermUtils.makeVar("x");
		final ATermAppl y = ATermUtils.makeVar("y");

		kb.addObjectProperty(p);
		kb.addObjectProperty(q);

		final ATermAppl[] head = new ATermAppl[] { ATermUtils.makePropAtom(q, x, y) };
		final ATermAppl[] body = new ATermAppl[] { ATermUtils.makePropAtom(p, x, y) };

		final ATermAppl rule = ATermUtils.makeRule(head, body);

		final OWLAxiom actual = converter.convert(rule);

		final Set<SWRLAtom> antecedent = new HashSet<>();
		final Set<SWRLAtom> consequent = new HashSet<>();

		antecedent.add(propertyAtom(ObjectProperty("p"), variable("x"), variable("y")));
		consequent.add(propertyAtom(ObjectProperty("q"), variable("x"), variable("y")));

		final OWLAxiom expected = rule(antecedent, consequent);

		assertEquals(expected, actual);
	}

	@Test
	public void testAxiomConverterRules4()
	{
		final KnowledgeBase kb = new KnowledgeBase();
		final AxiomConverter converter = new AxiomConverter(kb, OWL.manager.getOWLDataFactory());

		final ATermAppl r = ATermUtils.makeTermAppl("r");
		final ATermAppl s = ATermUtils.makeTermAppl("s");
		final ATermAppl x = ATermUtils.makeVar("x");
		final ATermAppl y = ATermUtils.makeVar("y");

		kb.addDatatypeProperty(r);
		kb.addDatatypeProperty(s);

		final ATermAppl[] head = new ATermAppl[] { ATermUtils.makePropAtom(r, x, y) };
		final ATermAppl[] body = new ATermAppl[] { ATermUtils.makePropAtom(s, x, y) };

		final ATermAppl rule = ATermUtils.makeRule(head, body);

		final OWLAxiom actual = converter.convert(rule);

		final Set<SWRLAtom> antecedent = new HashSet<>();
		final Set<SWRLAtom> consequent = new HashSet<>();

		antecedent.add(propertyAtom(DataProperty("s"), variable("x"), variable("y")));
		consequent.add(propertyAtom(DataProperty("r"), variable("x"), variable("y")));

		final OWLAxiom expected = rule(antecedent, consequent);

		assertEquals(expected, actual);
	}

	@Test
	public void typeInheritanceWithAnonIndividual() throws OWLOntologyCreationException
	{
		final OWLAxiom[] axioms = { OWL.subClassOf(C, D), OWL.classAssertion(anon, C) };

		final OWLOntology ont = OWL.Ontology(axioms);
		final PelletReasoner pellet = PelletReasonerFactory.getInstance().createReasoner(ont);

		assertTrue(pellet.getInstances(D, true).getNodes().size() == 0);

		assertTrue(pellet.getInstances(D, false).getNodes().size() == 1);
	}

	@Test
	public void testSubClassDirectParameter()
	{
		final OWLAxiom[] axioms = { OWL.subClassOf(E, D), OWL.subClassOf(D, C) };

		final OWLOntology ont = OWL.Ontology(axioms);
		final PelletReasoner pellet = PelletReasonerFactory.getInstance().createReasoner(ont);

		assertTrue(pellet.getSubClasses(C, true).getNodes().size() == 1);
		assertTrue(pellet.getSubClasses(C, false).getNodes().size() == 3); // includes owl:Nothing
	}

	private PelletReasoner setupReasonerIndividualNodeSetPolicy(final IndividualNodeSetPolicy p)
	{
		final OWLAxiom[] axioms = { OWL.classAssertion(a, C), OWL.classAssertion(b, C), OWL.classAssertion(c, C), OWL.sameAs(a, b), OWL.differentFrom(b, c), OWL.differentFrom(a, c) };

		final OWLOntology ont = OWL.Ontology(axioms);
		final OWLReasonerConfiguration config = new SimpleConfiguration(new NullReasonerProgressMonitor(), FreshEntityPolicy.ALLOW, Long.MAX_VALUE, p);
		final PelletReasoner pellet = PelletReasonerFactory.getInstance().createReasoner(ont, config);

		return pellet;
	}

	@Test
	public void testIndividualNodeSetPolicyBySameAs()
	{
		final PelletReasoner pellet = setupReasonerIndividualNodeSetPolicy(IndividualNodeSetPolicy.BY_SAME_AS);

		assertTrue(pellet.getInstances(C, true).getNodes().size() == 2);
		assertTrue(pellet.getDifferentIndividuals(c).getNodes().size() == 1);
	}

	@Test
	public void testIndividualNodeSetPolicyByName()
	{
		final PelletReasoner pellet = setupReasonerIndividualNodeSetPolicy(IndividualNodeSetPolicy.BY_NAME);

		assertTrue(pellet.getInstances(C, true).getNodes().size() == 3);
		assertTrue(pellet.getDifferentIndividuals(c).getNodes().size() == 2);
	}

	@Test
	public void testTopBottomPropertyAssertion()
	{
		final OWLAxiom[] axioms = { OWL.propertyAssertion(a, OWL.topObjectProperty, b), OWL.propertyAssertion(a, OWL.topDataProperty, lit), OWL.propertyAssertion(a, OWL.bottomObjectProperty, b), OWL.propertyAssertion(a, OWL.bottomDataProperty, lit) };

		for (int i = 0; i < axioms.length; i++)
		{
			final OWLOntology ont = OWL.Ontology(axioms[i]);
			final PelletReasoner pellet = PelletReasonerFactory.getInstance().createReasoner(ont);

			assertEquals(i < 2, pellet.isConsistent());
		}
	}

	@Test
	public void testTopBottomPropertyInferences()
	{
		final boolean prevValue = PelletOptions.HIDE_TOP_PROPERTY_VALUES;
		PelletOptions.HIDE_TOP_PROPERTY_VALUES = false;
		try
		{

			createReasoner(OWL.propertyAssertion(a, p, b), OWL.classAssertion(c, C), OWL.propertyAssertion(a, dp, lit));

			assertTrue(reasoner.isEntailed(OWL.subPropertyOf(p, OWL.topObjectProperty)));
			assertTrue(reasoner.isEntailed(OWL.subPropertyOf(OWL.bottomObjectProperty, p)));
			assertTrue(reasoner.isEntailed(OWL.subPropertyOf(dp, OWL.topDataProperty)));
			assertTrue(reasoner.isEntailed(OWL.subPropertyOf(OWL.bottomDataProperty, dp)));

			assertEquals(Collections.singleton(p), reasoner.getSubObjectProperties(OWL.topObjectProperty, true).getFlattened());
			assertEquals(Collections.singleton(OWL.bottomObjectProperty), reasoner.getSubObjectProperties(p, true).getFlattened());
			assertEquals(Collections.singleton(dp), reasoner.getSubDataProperties(OWL.topDataProperty, true).getFlattened());
			assertEquals(Collections.singleton(OWL.bottomDataProperty), reasoner.getSubDataProperties(dp, true).getFlattened());

			assertTrue(reasoner.isEntailed(propertyAssertion(a, p, b)));
			assertFalse(reasoner.isEntailed(propertyAssertion(b, p, a)));
			assertTrue(reasoner.isEntailed(propertyAssertion(a, OWL.topObjectProperty, b)));
			assertTrue(reasoner.isEntailed(propertyAssertion(b, OWL.topObjectProperty, a)));

			assertTrue(reasoner.isEntailed(propertyAssertion(a, dp, lit)));
			assertFalse(reasoner.isEntailed(propertyAssertion(b, dp, lit)));
			assertTrue(reasoner.isEntailed(propertyAssertion(a, OWL.topDataProperty, lit)));
			assertTrue(reasoner.isEntailed(propertyAssertion(b, OWL.topDataProperty, lit)));

			assertPropertyValues(reasoner, a, p, b);
			assertTrue(reasoner.getObjectPropertyValues(b, p).getFlattened().isEmpty());
			assertPropertyValues(reasoner, a, OWL.topObjectProperty, a, b, c);
			assertPropertyValues(reasoner, b, OWL.topObjectProperty, a, b, c);
			assertPropertyValues(reasoner, a, OWL.topDataProperty, lit);
			assertPropertyValues(reasoner, b, OWL.topDataProperty, lit);

		}
		finally
		{
			PelletOptions.HIDE_TOP_PROPERTY_VALUES = prevValue;
		}
	}

	@Ignore
	@Test
	public void testSetTheory() throws OWLException
	{
		// This tests #388

		final String ns = "http://www.integratedmodelling.org/ks/tarassandbox/set-theory.owl#";

		final OWLOntology ont = loadOntology(base + "set-theory.owl");

		final PelletReasoner reasoner = PelletReasonerFactory.getInstance().createReasoner(ont);

		reasoner.getKB().classify();

		assertEquals(SetUtils.create(OWL.Class(ns + "SetOfXSets"), OWL.Class(ns + "XSet"), OWL.Class(ns + "XThing")), reasoner.getSubClasses(OWL.Class(ns + "XSetTheoryClass"), true).getFlattened());
	}

	@Test
	public void datatypeDefinition()
	{
		final OWLDatatype between5and10 = OWL.Datatype("between5and10");
		final OWLDatatype between6and8 = OWL.Datatype("between6and8");

		createReasoner(OWL.datatypeDefinition(between5and10, OWL.restrict(XSD.INTEGER, OWL.minInclusive(5), OWL.maxInclusive(10))), OWL.datatypeDefinition(between6and8, OWL.restrict(XSD.INTEGER, OWL.minInclusive(6), OWL.maxInclusive(8))), OWL.equivalentClasses(A, OWL.some(dp, between5and10)), OWL.equivalentClasses(B, OWL.some(dp, between6and8)), OWL.propertyAssertion(a, dp, OWL.constant(9)), OWL.propertyAssertion(b, dp, OWL.constant(7)));

		assertTrue(reasoner.isEntailed(OWL.subClassOf(B, A)));
		assertTrue(reasoner.isEntailed(OWL.classAssertion(a, A)));
		assertFalse(reasoner.isEntailed(OWL.classAssertion(a, B)));
		assertTrue(reasoner.isEntailed(OWL.classAssertion(b, A)));
		assertTrue(reasoner.isEntailed(OWL.classAssertion(b, B)));
	}

	@Test
	public void dataRangeEntailment()
	{
		createReasoner(OWL.range(dp, XSD.INT));

		assertTrue(reasoner.isEntailed(OWL.range(dp, XSD.INT)));
		assertTrue(reasoner.isEntailed(OWL.range(dp, XSD.INTEGER)));
		assertFalse(reasoner.isEntailed(OWL.range(dp, XSD.FLOAT)));
		assertFalse(reasoner.isEntailed(OWL.range(dp, XSD.STRING)));
	}

	@Test
	/**
	 * Tests for the bug reported in #149
	 */
	public void test149()
	{
		createReasoner(OWL.inverseProperties(p, q));

		assertTrue(reasoner.isEntailed(OWL.equivalentProperties(p, OWL.inverse(q))));
	}

	@Test
	/**
	 * Tests for the bug reported in #150
	 */
	public void test150_1()
	{
		createReasoner(OWL.disjointProperties(p, OWL.inverse(q)));

		assertTrue(reasoner.isEntailed(OWL.disjointProperties(p, OWL.inverse(q))));
	}

	@Test
	/**
	 * Tests for the bug reported in #150
	 */
	public void test150_2()
	{
		createReasoner(OWL.domain(p, C), OWL.range(q, D), OWL.disjointClasses(C, D));

		assertTrue(reasoner.isEntailed(OWL.disjointProperties(p, OWL.inverse(q))));
		assertTrue(reasoner.isEntailed(OWL.disjointProperties(OWL.inverse(p), q)));
	}

	@Test
	/**
	 * Test for the enhancement required in #252
	 */
	public void testBooleanDatatypeConstructors()
	{
		final OWLDatatype nni = XSD.NON_NEGATIVE_INTEGER;
		final OWLDatatype npi = XSD.NON_POSITIVE_INTEGER;
		final OWLDatatype ni = XSD.NEGATIVE_INTEGER;
		final OWLDatatype pi = XSD.POSITIVE_INTEGER;
		final OWLDatatype f = XSD.FLOAT;
		final OWLDatatype i = XSD.INTEGER;

		createReasoner(OWL.declaration(nni), OWL.declaration(npi), OWL.declaration(ni), OWL.declaration(pi), OWL.declaration(f), OWL.declaration(dq), OWL.range(dp, OWL.dataAnd(pi, ni)));

		assertTrue(reasoner.isSatisfiable(OWL.some(dq, pi)));
		assertTrue(reasoner.isSatisfiable(OWL.some(dq, OWL.dataNot(pi))));
		assertFalse(reasoner.isSatisfiable(OWL.some(dq, OWL.dataAnd(pi, ni))));
		assertFalse(reasoner.isSatisfiable(OWL.some(dq, OWL.dataAnd(f, OWL.dataOr(pi, ni)))));
		assertTrue(reasoner.isSatisfiable(OWL.some(dq, OWL.dataAnd(npi, ni))));
		assertTrue(reasoner.isSatisfiable(OWL.some(dq, OWL.dataAnd(nni, pi))));
		assertTrue(reasoner.isSatisfiable(OWL.some(dq, OWL.dataOr(nni, npi))));
		assertTrue(reasoner.isSatisfiable(OWL.some(dq, OWL.dataAnd(nni, npi))));
		assertFalse(reasoner.isSatisfiable(OWL.some(dq, OWL.dataAnd(pi, OWL.restrict(i, OWL.maxExclusive(0))))));
		assertFalse(reasoner.isSatisfiable(OWL.some(dp, XSD.ANY_TYPE)));
	}

	/**
	 * Test for #447
	 */
	@Test
	public void testGetEquivalentClasses()
	{
		createReasoner(OWL.equivalentClasses(A, B), OWL.equivalentClasses(B, C));

		assertEquals(SetUtils.create(A, B, C), reasoner.getEquivalentClasses(A).getEntities());
		assertEquals(SetUtils.create(A, B, C), reasoner.getEquivalentClasses(B).getEntities());
		assertEquals(SetUtils.create(A, B, C), reasoner.getEquivalentClasses(C).getEntities());
	}

	@Test
	public void testGetEquivalentObjectProperties()
	{
		createReasoner(OWL.equivalentProperties(p, q), OWL.equivalentProperties(q, r));

		assertEquals(SetUtils.create(p, q, r), reasoner.getEquivalentObjectProperties(p).getEntities());
		assertEquals(SetUtils.create(p, q, r), reasoner.getEquivalentObjectProperties(q).getEntities());
		assertEquals(SetUtils.create(p, q, r), reasoner.getEquivalentObjectProperties(r).getEntities());
	}

	@Test
	public void testGetEquivalentDataProperties()
	{
		createReasoner(OWL.equivalentDataProperties(dp, dq), OWL.equivalentDataProperties(dq, dr));

		assertEquals(SetUtils.create(dp, dq, dr), reasoner.getEquivalentDataProperties(dp).getEntities());
		assertEquals(SetUtils.create(dp, dq, dr), reasoner.getEquivalentDataProperties(dq).getEntities());
		assertEquals(SetUtils.create(dp, dq, dr), reasoner.getEquivalentDataProperties(dr).getEntities());
	}

	/**
	 * Test for #447
	 */
	@Test
	public void testGetUnsatClasses()
	{
		createReasoner(OWL.disjointClasses(A, B), OWL.equivalentClasses(C, OWL.and(A, B)));

		assertEquals(SetUtils.create(C, OWL.Nothing), reasoner.getUnsatisfiableClasses().getEntities());
		assertEquals(SetUtils.create(C, OWL.Nothing), reasoner.getEquivalentClasses(C).getEntities());
	}

	@Test
	public void test454()
	{
		final OWLOntology ont = loadOntology(base + "ticket-454-test-case.owl");

		final PelletReasoner reasoner = PelletReasonerFactory.getInstance().createReasoner(ont);

		assertFalse(reasoner.isConsistent());
	}

	@Test
	public void test456()
	{
		final OWLOntology ont = loadOntology(base + "ticket-456-test-case.owl");

		final PelletReasoner reasoner = PelletReasonerFactory.getInstance().createReasoner(ont);

		assertTrue(reasoner.isConsistent());
	}

	@Test
	public void testDataDomainWithEquivalents()
	{
		createReasoner(OWL.subClassOf(A, min(dp, 1)), OWL.domain(dp, A), OWL.subClassOf(A, B));

		assertTrue(reasoner.isEntailed(OWL.domain(dp, A)));
		assertEquals(SetUtils.create(A), reasoner.getDataPropertyDomains(dp, true).getFlattened());
		assertEquals(SetUtils.create(A, B, OWL.Thing), reasoner.getDataPropertyDomains(dp, false).getFlattened());
	}

	@Test
	public void testDataDomainWithSubClasses()
	{
		createReasoner(OWL.domain(dp, A), OWL.subClassOf(A, B));

		assertTrue(reasoner.isEntailed(OWL.domain(dp, A)));
		assertEquals(SetUtils.create(A), reasoner.getDataPropertyDomains(dp, true).getFlattened());
		assertEquals(SetUtils.create(A, B, OWL.Thing), reasoner.getDataPropertyDomains(dp, false).getFlattened());
	}

	@Test
	public void testObjecDomainWithEquivalents()
	{
		createReasoner(OWL.subClassOf(A, min(p, 1)), OWL.domain(p, A), OWL.domain(p, C), OWL.subClassOf(A, B));

		assertTrue(reasoner.isEntailed(OWL.domain(p, A)));
		assertEquals(SetUtils.create(A), reasoner.getObjectPropertyDomains(p, true).getFlattened());
		assertEquals(SetUtils.create(A, B, C, OWL.Thing), reasoner.getObjectPropertyDomains(p, false).getFlattened());
	}

	@Test
	public void testObjectDomainWithSubClasses()
	{
		createReasoner(OWL.domain(p, A), OWL.subClassOf(A, B));

		assertTrue(reasoner.isEntailed(OWL.domain(p, A)));
		assertEquals(SetUtils.create(A), reasoner.getObjectPropertyDomains(p, true).getFlattened());
		assertEquals(SetUtils.create(A, B, OWL.Thing), reasoner.getObjectPropertyDomains(p, false).getFlattened());
	}

	@Test
	public void testObjectRangeWithEquivalents()
	{
		createReasoner(OWL.equivalentClasses(C, some(inverse(p), OWL.Thing)), OWL.range(p, D), OWL.subClassOf(C, E));

		reasoner.getKB().printClassTree();

		assertTrue(reasoner.isEntailed(OWL.range(p, C)));
		assertEquals(SetUtils.create(C), reasoner.getEquivalentClasses(some(inverse(p), OWL.Thing)).getEntities());
		assertEquals(SetUtils.create(C), reasoner.getObjectPropertyRanges(p, true).getFlattened());
		assertEquals(SetUtils.create(C, D, E, OWL.Thing), reasoner.getObjectPropertyRanges(p, false).getFlattened());
	}

	@Test
	public void testObjectRangeWithSubClasses()
	{
		createReasoner(OWL.domain(p, A), OWL.range(p, C), OWL.range(p, D), OWL.subClassOf(C, E));

		assertTrue(reasoner.isEntailed(OWL.range(p, C)));
		assertEquals(SetUtils.create(C, D), reasoner.getObjectPropertyRanges(p, true).getFlattened());
		assertEquals(SetUtils.create(C, D, E, OWL.Thing), reasoner.getObjectPropertyRanges(p, false).getFlattened());
	}

	@Test
	public void testQuotedLiteral()
	{
		final OWLLiteral literal = OWL.constant("\"test\"");

		createReasoner(OWL.propertyAssertion(a, dp, literal));

		assertTrue(reasoner.isEntailed(OWL.propertyAssertion(a, dp, literal)));
		assertEquals(Collections.singleton(literal), reasoner.getDataPropertyValues(a, dp));
	}

	@Test
	public void testComplementRemoval() throws OWLException
	{
		final String ns = "http://test#";

		final OWLOntology ont = loadOntology(MiscTests.base + "ticket539.ofn");

		final PelletReasoner reasoner = PelletReasonerFactory.getInstance().createNonBufferingReasoner(ont);

		assertFalse(reasoner.isConsistent());

		OWL.manager.removeAxiom(ont, OWL.subClassOf(OWL.Class(ns + "a_GROUP"), OWL.Class(ns + "a_TEMPORALTHING")));
		assertFalse(reasoner.isConsistent());

		OWL.manager.removeAxiom(ont, OWL.subClassOf(OWL.Class(ns + "a_INDIVIDUAL"), OWL.not(OWL.Class(ns + "a_SETORCOLLECTION"))));
		assertFalse(reasoner.isConsistent());
	}
}
