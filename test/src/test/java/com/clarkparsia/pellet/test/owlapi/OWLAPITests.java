// Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public
// License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of
// proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package com.clarkparsia.pellet.test.owlapi;

import static com.clarkparsia.modularity.test.TestUtils.assertStreamAsSetEquals;
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
import java.util.stream.Stream;
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
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLOntology;
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

		final OWLOntology ont = loadOntology(_base + "owl2.owl");

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

		final OWLOntology ont = loadOntology(_base + "owl2.owl");

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
		assertIteratorValues(reasoner.getTypes(ind1, false).entities().iterator(), new Object[] { OWL.Thing, C, test2, test3 });

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

		final OWLOntology ont = loadOntology(_base + "uncle.owl");

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

		final OWLOntology ont = loadOntology(_base + "sibling.owl");

		final PelletReasoner reasoner = PelletReasonerFactory.getInstance().createReasoner(ont);

		final OWLNamedIndividual Bob = Individual(ns + "Bob");
		final OWLNamedIndividual John = Individual(ns + "John");
		final OWLNamedIndividual Jane = Individual(ns + "Jane");

		final OWLObjectProperty hasBrother = ObjectProperty(ns + "hasBrother");
		final OWLObjectProperty hasSister = ObjectProperty(ns + "hasSister");

		assertPropertyValues(reasoner, Bob, hasBrother, John);
		assertPropertyValues(reasoner, Bob, hasSister, Jane);
	}

	public static void assertInstances(final PelletReasoner reasoner, final OWLClass subj, final boolean direct, final OWLNamedIndividual... expected)
	{
		assertStreamAsSetEquals(Arrays.asList(expected).stream(), reasoner.getInstances(subj, direct).entities());
	}

	public static void assertPropertyValues(final PelletReasoner reasoner, final OWLNamedIndividual subj, final OWLObjectProperty pred, final OWLIndividual... expected)
	{
		assertStreamAsSetEquals(Arrays.asList(expected).stream(), reasoner.getObjectPropertyValues(subj, pred).entities());
	}

	public static void assertPropertyValues(final PelletReasoner reasoner, final OWLNamedIndividual subj, final OWLDataProperty pred, final OWLLiteral values)
	{
		final Set<OWLLiteral> expected = new HashSet<>(Arrays.asList(values));

		assertEquals(expected, reasoner.getDataPropertyValues(subj, pred));
	}

	public static void assertTypes(final PelletReasoner reasoner, final OWLNamedIndividual subj, final boolean direct, final OWLClass... expected)
	{
		assertStreamAsSetEquals(Arrays.asList(expected).stream(), reasoner.getTypes(subj, direct).entities());
	}

	@Test
	public void testPropertyChain()
	{
		final String ns = "http://www.example.org/test#";

		final OWLOntology ont = loadOntology(_base + "propertyChain.owl");

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

		assertIteratorValues(reasoner.getInstances(C, false).entities().iterator(), theList);

		assertIteratorValues(reasoner.getInstances(S0, false).entities().iterator(), theList);

		assertIteratorValues(reasoner.getInstances(R0, false).entities().iterator(), new OWLIndividual[] { a[7], a[9] });

		assertIteratorValues(reasoner.getInstances(R1, false).entities().iterator(), new OWLIndividual[] { a[2], a[3], a[4], a[5], a[6] });

		assertIteratorValues(reasoner.getObjectPropertyValues(a[0], r).entities().iterator(), new OWLIndividual[] { a[7], a[9] });

		assertIteratorValues(reasoner.getObjectPropertyValues(a[1], r).entities().iterator(), new OWLIndividual[] { a[2], a[3], a[4], a[5], a[6] });

		assertIteratorValues(reasoner.getObjectPropertyValues(a[0], s).entities().iterator(), theList);

	}

	@Test
	public void testQualifiedCardinality1()
	{
		final String ns = "http://www.example.org/test#";

		final OWLOntology ont = loadOntology(_base + "qcr.owl");

		final PelletReasoner reasoner = PelletReasonerFactory.getInstance().createReasoner(ont);

		final OWLClass sub = Class(ns + "sub");
		final OWLClass sup = Class(ns + "sup");

		assertTrue(reasoner.isConsistent());

		assertTrue(reasoner.isEntailed(subClassOf(sub, sup)));
		assertTrue(reasoner.getSubClasses(sup, false).entities().filter(x -> x.equals(sub)).findAny().isPresent());
		assertTrue(reasoner.getSuperClasses(sub, false).entities().filter(x -> x.equals(sup)).findAny().isPresent());
	}

	@Test
	public void testReflexive2()
	{
		final String ns = "http://www.example.org/test#";
		final String foaf = "http://xmlns.com/foaf/0.1/";

		final OWLOntology ont = loadOntology(_base + "reflexive.owl");

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
		final OWLOntology ont = loadOntology(_base + "infiniteChain.owl");

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

		final OWLOntology ont = loadOntology(_base + "RemoveLiteral.owl");

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

		final OWLOntology ont = loadOntology(_base + "family.owl");
		ont.axioms().map(OWLAxiom::toString).sorted().forEach(System.out::println);

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

		final OWLOntology ont = loadOntology(_base + "family.owl");

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
	//		OWLOntology ont = loadOntology( _base + "/entityDeclarations.owl" );
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

		final OWLOntology ont = loadOntology(_base + "anon_inverse.owl");

		final OWLClass C = Class(ns + "C");
		final OWLClass D = Class(ns + "D");
		final OWLObjectProperty r = ObjectProperty(ns + "r");
		final OWLClassExpression desc = some(inverse(r), D);

		final PelletReasoner reasoner = PelletReasonerFactory.getInstance().createReasoner(ont);

		assertStreamAsSetEquals(Stream.of(C), reasoner.getSubClasses(desc, true).entities());

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

		final OWLOntology ont = loadOntology(_base + "dl-safe.owl");

		final PelletReasoner reasoner = PelletReasonerFactory.getInstance().createReasoner(ont);

		// OWLObjectProperty father = _factory.getOWLObjectProperty( URI.create(
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

		assertIteratorValues(reasoner.getTypes(Cain, true).entities().iterator(), new Object[] { BadChild, Child, Person });
	}

	@Test
	public void testDLSafeConstants()
	{
		final String ns = "http://owldl.com/ontologies/dl-safe-constants.owl#";

		final OWLOntology ont = loadOntology(_base + "dl-safe-constants.owl");

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

			assertIteratorValues(reasoner.getInstances(DreamTeamMember, false).entities().iterator(), new Object[] { Alice, Bob, Charlie });

			assertIteratorValues(reasoner.getInstances(DreamTeamMember1, false).entities().iterator(), new Object[] { Alice, Bob, Charlie });

			assertIteratorValues(reasoner.getInstances(DreamTeamMember2, false).entities().iterator(), new Object[] { Alice, Bob, Charlie });
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
		final OWLOntology ont = loadOntology(_base + "invalidTransitivity.owl");

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

		final IRI ontIRI = IRI.create(_base + "invalidTransitivity.owl");

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

		assertStreamAsSetEquals(Stream.of(a), reasoner.getSameIndividuals(a).entities());
		assertStreamAsSetEquals(Stream.of(b, c), reasoner.getSameIndividuals(b).entities());
		assertStreamAsSetEquals(Stream.of(b, c), reasoner.getSameIndividuals(c).entities());

		assertPropertyValues(reasoner, a, p, b, c);

		assertPropertyValues(reasoner, a, q, b, c);

		assertPropertyValues(reasoner, b, p, b, c);

		assertPropertyValues(reasoner, b, q, a);

		assertPropertyValues(reasoner, c, p, b, c);

		assertPropertyValues(reasoner, c, q, a);
	}

	@Test
	public void testSameAs3()
	{
		final String ns = "urn:test:";

		final IRI ontIRI = IRI.create(_base + "test.owl");

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
		assertStreamAsSetEquals(Stream.of(i1), reasoner.getSameIndividuals(i1).entities());

		assertTrue(!reasoner.isEntailed(sameAs(i2, i1)));
		assertTrue(!reasoner.isEntailed(sameAs(i2, i3)));
		assertStreamAsSetEquals(Stream.of(i2), reasoner.getSameIndividuals(i2).entities());

		assertTrue(!reasoner.isEntailed(sameAs(i3, i1)));
		assertTrue(!reasoner.isEntailed(sameAs(i3, i2)));
		assertStreamAsSetEquals(Stream.of(i3), reasoner.getSameIndividuals(i3).entities());

	}

	public static class TimedProgressMonitor extends ConsoleProgressMonitor
	{
		private final int _limit; // in milliseconds

		public TimedProgressMonitor(final int limit)
		{
			this._limit = limit;
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
			return elapsedTime > _limit;
		}
	}

	@Test
	// This tests ticket 148
	// Canceling realization with REALIZE_BY_INDIVIDUAL=false throws an NPE
	public void testRealizeByIndividualsNPE()
	{
		final Properties newOptions = PropertiesBuilder.singleton("REALIZE_INDIVIDUAL_AT_A_TIME", "true");
		final Properties savedOptions = PelletOptions.setOptions(newOptions);

		try
		{
			final ProgressMonitor monitor = new TimedProgressMonitor(1);

			final OWLOntology ont = loadOntology(_base + "food.owl");

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
	public void testClassificationTimeout()
	{
		boolean timeout = false;

		final OWLOntology ont = loadOntology(_base + "food.owl");

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
	public void testRealizationTimeout()
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
		final AxiomConverter converter = new AxiomConverter(kb, OWL._manager.getOWLDataFactory());

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
		final AxiomConverter converter = new AxiomConverter(kb, OWL._manager.getOWLDataFactory());

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
		final AxiomConverter converter = new AxiomConverter(kb, OWL._manager.getOWLDataFactory());

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
		final AxiomConverter converter = new AxiomConverter(kb, OWL._manager.getOWLDataFactory());

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
		final AxiomConverter converter = new AxiomConverter(kb, OWL._manager.getOWLDataFactory());

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
	public void typeInheritanceWithAnonIndividual()
	{
		final OWLAxiom[] axioms = { OWL.subClassOf(_C, _D), OWL.classAssertion(_anon, _C) };

		final OWLOntology ont = OWL.Ontology(axioms);
		final PelletReasoner pellet = PelletReasonerFactory.getInstance().createReasoner(ont);

		assertTrue(pellet.getInstances(_D, true).nodes().count() == 0);

		assertTrue(pellet.getInstances(_D, false).nodes().count() == 1);
	}

	@Test
	public void testSubClassDirectParameter()
	{
		final OWLAxiom[] axioms = { OWL.subClassOf(_E, _D), OWL.subClassOf(_D, _C) };

		final OWLOntology ont = OWL.Ontology(axioms);
		final PelletReasoner pellet = PelletReasonerFactory.getInstance().createReasoner(ont);

		assertTrue(pellet.getSubClasses(_C, true).nodes().count() == 1);
		assertTrue(pellet.getSubClasses(_C, false).nodes().count() == 3); // includes owl:Nothing
	}

	private PelletReasoner setupReasonerIndividualNodeSetPolicy(final IndividualNodeSetPolicy p)
	{
		final OWLAxiom[] axioms = { OWL.classAssertion(_a, _C), OWL.classAssertion(_b, _C), OWL.classAssertion(_c, _C), OWL.sameAs(_a, _b), OWL.differentFrom(_b, _c), OWL.differentFrom(_a, _c) };

		final OWLOntology ont = OWL.Ontology(axioms);
		final OWLReasonerConfiguration config = new SimpleConfiguration(new NullReasonerProgressMonitor(), FreshEntityPolicy.ALLOW, Long.MAX_VALUE, p);
		final PelletReasoner pellet = PelletReasonerFactory.getInstance().createReasoner(ont, config);

		return pellet;
	}

	@Test
	public void testIndividualNodeSetPolicyBySameAs()
	{
		final PelletReasoner pellet = setupReasonerIndividualNodeSetPolicy(IndividualNodeSetPolicy.BY_SAME_AS);

		assertTrue(pellet.getInstances(_C, true).nodes().count() == 2);
		assertTrue(pellet.getDifferentIndividuals(_c).nodes().count() == 1);
	}

	@Test
	public void testIndividualNodeSetPolicyByName()
	{
		final PelletReasoner pellet = setupReasonerIndividualNodeSetPolicy(IndividualNodeSetPolicy.BY_NAME);

		assertTrue(pellet.getInstances(_C, true).nodes().count() == 3);
		assertTrue(pellet.getDifferentIndividuals(_c).nodes().count() == 2);
	}

	@Test
	public void testTopBottomPropertyAssertion()
	{
		final OWLAxiom[] axioms = { OWL.propertyAssertion(_a, OWL.topObjectProperty, _b), OWL.propertyAssertion(_a, OWL.topDataProperty, _lit), OWL.propertyAssertion(_a, OWL.bottomObjectProperty, _b), OWL.propertyAssertion(_a, OWL.bottomDataProperty, _lit) };

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

			createReasoner(OWL.propertyAssertion(_a, _p, _b), OWL.classAssertion(_c, _C), OWL.propertyAssertion(_a, _dp, _lit));

			assertTrue(_reasoner.isEntailed(OWL.subPropertyOf(_p, OWL.topObjectProperty)));
			assertTrue(_reasoner.isEntailed(OWL.subPropertyOf(OWL.bottomObjectProperty, _p)));
			assertTrue(_reasoner.isEntailed(OWL.subPropertyOf(_dp, OWL.topDataProperty)));
			assertTrue(_reasoner.isEntailed(OWL.subPropertyOf(OWL.bottomDataProperty, _dp)));

			assertStreamAsSetEquals(Stream.of(_p), _reasoner.getSubObjectProperties(OWL.topObjectProperty, true).entities());
			assertStreamAsSetEquals(Stream.of(OWL.bottomObjectProperty), _reasoner.getSubObjectProperties(_p, true).entities());
			assertStreamAsSetEquals(Stream.of(_dp), _reasoner.getSubDataProperties(OWL.topDataProperty, true).entities());
			assertStreamAsSetEquals(Stream.of(OWL.bottomDataProperty), _reasoner.getSubDataProperties(_dp, true).entities());

			assertTrue(_reasoner.isEntailed(propertyAssertion(_a, _p, _b)));
			assertFalse(_reasoner.isEntailed(propertyAssertion(_b, _p, _a)));
			assertTrue(_reasoner.isEntailed(propertyAssertion(_a, OWL.topObjectProperty, _b)));
			assertTrue(_reasoner.isEntailed(propertyAssertion(_b, OWL.topObjectProperty, _a)));

			assertTrue(_reasoner.isEntailed(propertyAssertion(_a, _dp, _lit)));
			assertFalse(_reasoner.isEntailed(propertyAssertion(_b, _dp, _lit)));
			assertTrue(_reasoner.isEntailed(propertyAssertion(_a, OWL.topDataProperty, _lit)));
			assertTrue(_reasoner.isEntailed(propertyAssertion(_b, OWL.topDataProperty, _lit)));

			assertPropertyValues(_reasoner, _a, _p, _b);
			assertTrue(!_reasoner.getObjectPropertyValues(_b, _p).entities().findAny().isPresent());
			assertPropertyValues(_reasoner, _a, OWL.topObjectProperty, _a, _b, _c);
			assertPropertyValues(_reasoner, _b, OWL.topObjectProperty, _a, _b, _c);
			assertPropertyValues(_reasoner, _a, OWL.topDataProperty, _lit);
			assertPropertyValues(_reasoner, _b, OWL.topDataProperty, _lit);

		}
		finally
		{
			PelletOptions.HIDE_TOP_PROPERTY_VALUES = prevValue;
		}
	}

	@Ignore
	@Test
	public void testSetTheory()
	{
		// This tests #388

		final String ns = "http://www.integratedmodelling.org/ks/tarassandbox/set-theory.owl#";

		final OWLOntology ont = loadOntology(_base + "set-theory.owl");

		final PelletReasoner reasoner = PelletReasonerFactory.getInstance().createReasoner(ont);

		reasoner.getKB().classify();

		assertStreamAsSetEquals(Stream.of(OWL.Class(ns + "SetOfXSets"), OWL.Class(ns + "XSet"), OWL.Class(ns + "XThing")), reasoner.getSubClasses(OWL.Class(ns + "XSetTheoryClass"), true).entities());
	}

	@Test
	public void datatypeDefinition()
	{
		final OWLDatatype between5and10 = OWL.Datatype("between5and10");
		final OWLDatatype between6and8 = OWL.Datatype("between6and8");

		createReasoner(OWL.datatypeDefinition(between5and10, OWL.restrict(XSD.INTEGER, OWL.minInclusive(5), OWL.maxInclusive(10))), OWL.datatypeDefinition(between6and8, OWL.restrict(XSD.INTEGER, OWL.minInclusive(6), OWL.maxInclusive(8))), OWL.equivalentClasses(_A, OWL.some(_dp, between5and10)), OWL.equivalentClasses(_B, OWL.some(_dp, between6and8)), OWL.propertyAssertion(_a, _dp, OWL.constant(9)), OWL.propertyAssertion(_b, _dp, OWL.constant(7)));

		assertTrue(_reasoner.isEntailed(OWL.subClassOf(_B, _A)));
		assertTrue(_reasoner.isEntailed(OWL.classAssertion(_a, _A)));
		assertFalse(_reasoner.isEntailed(OWL.classAssertion(_a, _B)));
		assertTrue(_reasoner.isEntailed(OWL.classAssertion(_b, _A)));
		assertTrue(_reasoner.isEntailed(OWL.classAssertion(_b, _B)));
	}

	@Test
	public void dataRangeEntailment()
	{
		createReasoner(OWL.range(_dp, XSD.INT));

		assertTrue(_reasoner.isEntailed(OWL.range(_dp, XSD.INT)));
		assertTrue(_reasoner.isEntailed(OWL.range(_dp, XSD.INTEGER)));
		assertFalse(_reasoner.isEntailed(OWL.range(_dp, XSD.FLOAT)));
		assertFalse(_reasoner.isEntailed(OWL.range(_dp, XSD.STRING)));
	}

	@Test
	/**
	 * Tests for the bug reported in #149
	 */
	public void test149()
	{
		createReasoner(OWL.inverseProperties(_p, _q));

		assertTrue(_reasoner.isEntailed(OWL.equivalentProperties(_p, OWL.inverse(_q))));
	}

	@Test
	/**
	 * Tests for the bug reported in #150
	 */
	public void test150_1()
	{
		createReasoner(OWL.disjointProperties(_p, OWL.inverse(_q)));

		assertTrue(_reasoner.isEntailed(OWL.disjointProperties(_p, OWL.inverse(_q))));
	}

	@Test
	/**
	 * Tests for the bug reported in #150
	 */
	public void test150_2()
	{
		createReasoner(OWL.domain(_p, _C), OWL.range(_q, _D), OWL.disjointClasses(_C, _D));

		assertTrue(_reasoner.isEntailed(OWL.disjointProperties(_p, OWL.inverse(_q))));
		assertTrue(_reasoner.isEntailed(OWL.disjointProperties(OWL.inverse(_p), _q)));
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

		createReasoner(OWL.declaration(nni), OWL.declaration(npi), OWL.declaration(ni), OWL.declaration(pi), OWL.declaration(f), OWL.declaration(_dq), OWL.range(_dp, OWL.dataAnd(pi, ni)));

		assertTrue(_reasoner.isSatisfiable(OWL.some(_dq, pi)));
		assertTrue(_reasoner.isSatisfiable(OWL.some(_dq, OWL.dataNot(pi))));
		assertFalse(_reasoner.isSatisfiable(OWL.some(_dq, OWL.dataAnd(pi, ni))));
		assertFalse(_reasoner.isSatisfiable(OWL.some(_dq, OWL.dataAnd(f, OWL.dataOr(pi, ni)))));
		assertTrue(_reasoner.isSatisfiable(OWL.some(_dq, OWL.dataAnd(npi, ni))));
		assertTrue(_reasoner.isSatisfiable(OWL.some(_dq, OWL.dataAnd(nni, pi))));
		assertTrue(_reasoner.isSatisfiable(OWL.some(_dq, OWL.dataOr(nni, npi))));
		assertTrue(_reasoner.isSatisfiable(OWL.some(_dq, OWL.dataAnd(nni, npi))));
		assertFalse(_reasoner.isSatisfiable(OWL.some(_dq, OWL.dataAnd(pi, OWL.restrict(i, OWL.maxExclusive(0))))));
		assertFalse(_reasoner.isSatisfiable(OWL.some(_dp, XSD.ANY_TYPE)));
	}

	/**
	 * Test for #447
	 */
	@Test
	public void testGetEquivalentClasses()
	{
		createReasoner(OWL.equivalentClasses(_A, _B), OWL.equivalentClasses(_B, _C));

		assertStreamAsSetEquals(Stream.of(_A, _B, _C), _reasoner.getEquivalentClasses(_A).entities());
		assertStreamAsSetEquals(Stream.of(_A, _B, _C), _reasoner.getEquivalentClasses(_B).entities());
		assertStreamAsSetEquals(Stream.of(_A, _B, _C), _reasoner.getEquivalentClasses(_C).entities());
	}

	@Test
	public void testGetEquivalentObjectProperties()
	{
		createReasoner(OWL.equivalentProperties(_p, _q), OWL.equivalentProperties(_q, _r));

		assertStreamAsSetEquals(Stream.of(_p, _q, _r), _reasoner.getEquivalentObjectProperties(_p).entities());
		assertStreamAsSetEquals(Stream.of(_p, _q, _r), _reasoner.getEquivalentObjectProperties(_q).entities());
		assertStreamAsSetEquals(Stream.of(_p, _q, _r), _reasoner.getEquivalentObjectProperties(_r).entities());
	}

	@Test
	public void testGetEquivalentDataProperties()
	{
		createReasoner(OWL.equivalentDataProperties(_dp, _dq), OWL.equivalentDataProperties(_dq, _dr));

		assertStreamAsSetEquals(Stream.of(_dp, _dq, _dr), _reasoner.getEquivalentDataProperties(_dp).entities());
		assertStreamAsSetEquals(Stream.of(_dp, _dq, _dr), _reasoner.getEquivalentDataProperties(_dq).entities());
		assertStreamAsSetEquals(Stream.of(_dp, _dq, _dr), _reasoner.getEquivalentDataProperties(_dr).entities());
	}

	/**
	 * Test for #447
	 */
	@Test
	public void testGetUnsatClasses()
	{
		createReasoner(OWL.disjointClasses(_A, _B), OWL.equivalentClasses(_C, OWL.and(_A, _B)));

		assertStreamAsSetEquals(Stream.of(_C, OWL.Nothing), _reasoner.getUnsatisfiableClasses().entities());
		assertStreamAsSetEquals(Stream.of(_C, OWL.Nothing), _reasoner.getEquivalentClasses(_C).entities());
	}

	@Test
	public void test454()
	{
		final OWLOntology ont = loadOntology(_base + "ticket-454-test-case.owl");

		final PelletReasoner reasoner = PelletReasonerFactory.getInstance().createReasoner(ont);

		assertFalse(reasoner.isConsistent());
	}

	@Test
	public void test456()
	{
		final OWLOntology ont = loadOntology(_base + "ticket-456-test-case.owl");

		final PelletReasoner reasoner = PelletReasonerFactory.getInstance().createReasoner(ont);

		assertTrue(reasoner.isConsistent());
	}

	@Test
	public void testDataDomainWithEquivalents()
	{
		createReasoner(OWL.subClassOf(_A, min(_dp, 1)), OWL.domain(_dp, _A), OWL.subClassOf(_A, _B));

		assertTrue(_reasoner.isEntailed(OWL.domain(_dp, _A)));
		assertStreamAsSetEquals(Stream.of(_A), _reasoner.getDataPropertyDomains(_dp, true).entities());
		assertStreamAsSetEquals(Stream.of(_A, _B, OWL.Thing), _reasoner.getDataPropertyDomains(_dp, false).entities());
	}

	@Test
	public void testDataDomainWithSubClasses()
	{
		createReasoner(OWL.domain(_dp, _A), OWL.subClassOf(_A, _B));

		assertTrue(_reasoner.isEntailed(OWL.domain(_dp, _A)));
		assertStreamAsSetEquals(Stream.of(_A), _reasoner.getDataPropertyDomains(_dp, true).entities());
		assertStreamAsSetEquals(Stream.of(_A, _B, OWL.Thing), _reasoner.getDataPropertyDomains(_dp, false).entities());
	}

	@Test
	public void testObjecDomainWithEquivalents()
	{
		createReasoner(OWL.subClassOf(_A, min(_p, 1)), OWL.domain(_p, _A), OWL.domain(_p, _C), OWL.subClassOf(_A, _B));

		assertTrue(_reasoner.isEntailed(OWL.domain(_p, _A)));
		assertStreamAsSetEquals(Stream.of(_A), _reasoner.getObjectPropertyDomains(_p, true).entities());
		assertStreamAsSetEquals(Stream.of(_A, _B, _C, OWL.Thing), _reasoner.getObjectPropertyDomains(_p, false).entities());
	}

	@Test
	public void testObjectDomainWithSubClasses()
	{
		createReasoner(OWL.domain(_p, _A), OWL.subClassOf(_A, _B));

		assertTrue(_reasoner.isEntailed(OWL.domain(_p, _A)));
		assertStreamAsSetEquals(Stream.of(_A), _reasoner.getObjectPropertyDomains(_p, true).entities());
		assertStreamAsSetEquals(Stream.of(_A, _B, OWL.Thing), _reasoner.getObjectPropertyDomains(_p, false).entities());
	}

	@Test
	public void testObjectRangeWithEquivalents()
	{
		createReasoner(OWL.equivalentClasses(_C, some(inverse(_p), OWL.Thing)), OWL.range(_p, _D), OWL.subClassOf(_C, _E));

		_reasoner.getKB().printClassTree();

		assertTrue(_reasoner.isEntailed(OWL.range(_p, _C)));
		assertStreamAsSetEquals(Stream.of(_C), _reasoner.getEquivalentClasses(some(inverse(_p), OWL.Thing)).entities());
		assertStreamAsSetEquals(Stream.of(_C), _reasoner.getObjectPropertyRanges(_p, true).entities());
		assertStreamAsSetEquals(Stream.of(_C, _D, _E, OWL.Thing), _reasoner.getObjectPropertyRanges(_p, false).entities());
	}

	@Test
	public void testObjectRangeWithSubClasses()
	{
		createReasoner(OWL.domain(_p, _A), OWL.range(_p, _C), OWL.range(_p, _D), OWL.subClassOf(_C, _E));

		assertTrue(_reasoner.isEntailed(OWL.range(_p, _C)));
		assertStreamAsSetEquals(Stream.of(_C, _D), _reasoner.getObjectPropertyRanges(_p, true).entities());
		assertStreamAsSetEquals(Stream.of(_C, _D, _E, OWL.Thing), _reasoner.getObjectPropertyRanges(_p, false).entities());
	}

	@Test
	public void testQuotedLiteral()
	{
		final OWLLiteral literal = OWL.constant("\"test\"");

		createReasoner(OWL.propertyAssertion(_a, _dp, literal));

		assertTrue(_reasoner.isEntailed(OWL.propertyAssertion(_a, _dp, literal)));
		assertEquals(Collections.singleton(literal), _reasoner.getDataPropertyValues(_a, _dp));
	}

	@Test
	public void testComplementRemoval()
	{
		final String ns = "http://test#";

		final OWLOntology ont = loadOntology(MiscTests._base + "ticket539.ofn");

		final PelletReasoner reasoner = PelletReasonerFactory.getInstance().createNonBufferingReasoner(ont);

		assertFalse(reasoner.isConsistent());

		ont.removeAxiom(OWL.subClassOf(OWL.Class(ns + "a_GROUP"), OWL.Class(ns + "a_TEMPORALTHING")));
		assertFalse(reasoner.isConsistent());

		ont.removeAxiom(OWL.subClassOf(OWL.Class(ns + "a_INDIVIDUAL"), OWL.not(OWL.Class(ns + "a_SETORCOLLECTION"))));
		assertFalse(reasoner.isConsistent());
	}
}
