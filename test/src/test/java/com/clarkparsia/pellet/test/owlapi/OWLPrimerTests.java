// Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public
// License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of
// proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package com.clarkparsia.pellet.test.owlapi;

import static org.junit.Assert.assertTrue;

import com.clarkparsia.owlapi.OWL;
import com.clarkparsia.owlapi.OntologyUtils;
import com.clarkparsia.pellet.owlapi.PelletReasonerFactory;
import java.util.Set;
import junit.framework.JUnit4TestAdapter;
import org.junit.Test;
import org.mindswap.pellet.test.PelletTestSuite;
import org.mindswap.pellet.utils.SetUtils;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLObject;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLObjectPropertyExpression;
import org.semanticweb.owlapi.reasoner.Node;
import org.semanticweb.owlapi.reasoner.NodeSet;

/**
 * @author Evren Sirin
 */
public class OWLPrimerTests extends AbstractOWLAPITests
{
	protected static final String NS = "http://example.com/owl/families/";
	protected static final String NS2 = "http://example.org/otherOntologies/families/";

	protected static final OWLNamedIndividual John = OWL.Individual(NS + "John");
	protected static final OWLNamedIndividual Jack = OWL.Individual(NS + "Jack");
	protected static final OWLNamedIndividual Bill = OWL.Individual(NS + "Bill");
	protected static final OWLNamedIndividual Mary = OWL.Individual(NS + "Mary");
	protected static final OWLNamedIndividual MaryBrown = OWL.Individual(NS2 + "MaryBrown");

	protected static final OWLObjectProperty hasParent = OWL.ObjectProperty(NS + "hasParent");
	protected static final OWLObjectProperty hasSpouse = OWL.ObjectProperty(NS + "hasSpouse");
	protected static final OWLObjectProperty hasWife = OWL.ObjectProperty(NS + "hasWife");
	protected static final OWLObjectProperty hasChild = OWL.ObjectProperty(NS + "hasChild");
	protected static final OWLObjectProperty child = OWL.ObjectProperty(NS2 + "child");
	protected static final OWLObjectProperty parentOf = OWL.ObjectProperty(NS2 + "parentOf");

	public static junit.framework.Test suite()
	{
		return new JUnit4TestAdapter(OWLPrimerTests.class);
	}

	public OWLPrimerTests()
	{
	}

	@Override
	public void resetOntologyManager()
	{
		super.resetOntologyManager();

		_ontology = OntologyUtils.loadOntology("file:" + PelletTestSuite.base + "modularity/OWL2Primer.owl");
		_reasoner = PelletReasonerFactory.getInstance().createReasoner(_ontology);
	}

	protected <T> Set<T> node(final T... inds)
	{
		return SetUtils.create(inds);
	}

	protected Set<OWLObjectPropertyExpression> nodeOP(final OWLObjectPropertyExpression... inds)
	{
		return SetUtils.create(inds);
	}

	protected <E extends OWLObject> void assertEquals(final NodeSet<E> actual, final Set<E>... expected)
	{
		final Set<Set<E>> expectedSet = SetUtils.create(expected);
		for (final Node<E> node : actual.getNodes())
			assertTrue("Unexpected value: " + node.getEntities(), expectedSet.remove(node.getEntities()));
		assertTrue("Missing values: " + expectedSet, expectedSet.isEmpty());
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testJackDifferents()
	{
		assertEquals(_reasoner.getDifferentIndividuals(John), node(Jack), node(Bill), node(Mary, MaryBrown));
	}

	@Test
	public void testHasParentDisjoints()
	{
		assertTrue(_reasoner.isEntailed(OWL.disjointProperties(hasParent, hasSpouse)));
		assertTrue(_reasoner.isEntailed(OWL.disjointProperties(hasParent, hasWife)));
		assertTrue(_reasoner.isEntailed(OWL.disjointProperties(hasParent, child)));
		assertTrue(_reasoner.isEntailed(OWL.disjointProperties(hasParent, hasChild)));
		assertTrue(_reasoner.isEntailed(OWL.disjointProperties(hasParent, OWL.bottomObjectProperty)));
		assertEquals(_reasoner.getDisjointObjectProperties(hasParent), nodeOP(hasSpouse), nodeOP(OWL.bottomObjectProperty), nodeOP(hasWife), nodeOP(hasChild, child));
	}
}
