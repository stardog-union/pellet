// Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package org.mindswap.pellet.test;

import static com.clarkparsia.pellet.utils.TermFactory.term;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import com.clarkparsia.pellet.owlapi.PelletReasoner;
import com.clarkparsia.pellet.owlapi.PelletReasonerFactory;
import junit.framework.JUnit4TestAdapter;
import openllet.aterm.ATermAppl;
import org.junit.Test;
import org.mindswap.pellet.KnowledgeBase;
import org.mindswap.pellet.utils.ATermUtils;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLDataRange;
import org.semanticweb.owlapi.model.OWLDataSomeValuesFrom;
import org.semanticweb.owlapi.model.OWLDatatype;
import org.semanticweb.owlapi.model.OWLFacetRestriction;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.OWLSubClassOfAxiom;
import org.semanticweb.owlapi.vocab.OWL2Datatype;
import org.semanticweb.owlapi.vocab.OWLFacet;

/**
 * <p>
 * Title: TestIsClass
 * </p>
 * <p>
 * Description: This test cases have been written to resolve issue #141 and #140 where certain terms were wrongly evaluated as classes, e.g. value(literal(...))
 * </p>
 * <p>
 * Copyright: Copyright (c) 2008
 * </p>
 * <p>
 * Company: Clark & Parsia, LLC. <http://www.clarkparsia.com>
 * </p>
 *
 * @author Markus Stocker
 */
public class TestIsClass
{

	public static junit.framework.Test suite()
	{
		return new JUnit4TestAdapter(TestIsClass.class);
	}

	@Test
	public void testIsClass1()
	{
		final KnowledgeBase kb = new KnowledgeBase();

		final ATermAppl p = term("p");
		final ATermAppl l = ATermUtils.makeTermAppl("l");
		final ATermAppl v = ATermUtils.makeValue(ATermUtils.makeLiteral(l));
		final ATermAppl c = ATermUtils.makeSomeValues(p, v);

		kb.addProperty(p);

		assertTrue(kb.isClass(c));
	}

	@Test
	public void testIsClass2()
	{
		final KnowledgeBase kb = new KnowledgeBase();

		final ATermAppl l = ATermUtils.makeTermAppl("l");
		final ATermAppl v = ATermUtils.makeValue(ATermUtils.makeLiteral(l));

		assertFalse(kb.isClass(v));
	}

	@Test
	public void testIsClass3()
	{
		final KnowledgeBase kb = new KnowledgeBase();

		final ATermAppl l1 = ATermUtils.makeTermAppl("l1");
		final ATermAppl l2 = ATermUtils.makeTermAppl("l2");
		final ATermAppl v1 = ATermUtils.makeValue(ATermUtils.makeLiteral(l1));
		final ATermAppl v2 = ATermUtils.makeValue(ATermUtils.makeLiteral(l2));
		final ATermAppl t = ATermUtils.makeOr(v1, v2);

		assertFalse(kb.isClass(t));
	}

	@Test
	public void testIsClass4()
	{
		final KnowledgeBase kb = new KnowledgeBase();

		final ATermAppl l = ATermUtils.makeTermAppl("l");
		final ATermAppl v = ATermUtils.makeValue(ATermUtils.makeLiteral(l));
		final ATermAppl n = ATermUtils.makeNot(v);

		assertFalse(kb.isClass(n));
	}

	@Test
	public void testIsClass5() throws OWLOntologyCreationException
	{
		final OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
		final OWLDataFactory factory = manager.getOWLDataFactory();
		final OWLOntology ontology = manager.createOntology(IRI.create("http://example.org"));

		final OWLDatatype dataRange = factory.getOWLDatatype(OWL2Datatype.XSD_INTEGER.getIRI());
		final OWLFacetRestriction dataRangeFacetRestriction = factory.getOWLFacetRestriction(OWLFacet.MIN_EXCLUSIVE, 1);
		final OWLDataRange dataRangeRestriction = factory.getOWLDatatypeRestriction(dataRange, dataRangeFacetRestriction);

		final OWLDataProperty p = factory.getOWLDataProperty(IRI.create("http://example#p"));
		final OWLDataSomeValuesFrom dataSomeRestriction = factory.getOWLDataSomeValuesFrom(p, dataRangeRestriction);

		final OWLClass c = factory.getOWLClass(IRI.create("http://example#c"));

		final OWLSubClassOfAxiom sc = factory.getOWLSubClassOfAxiom(c, dataSomeRestriction);

		manager.addAxiom(ontology, sc);

		final PelletReasoner reasoner = PelletReasonerFactory.getInstance().createReasoner(ontology);

		assertTrue(reasoner.isConsistent());

		final KnowledgeBase kb = reasoner.getKB();
		assertTrue(kb.isClass(term("http://example#c")));

		// check for complex class that refers to a user-defined datatype
		ATermAppl term = reasoner.term(dataSomeRestriction);
		term = ATermUtils.normalize(term);
		assertTrue(kb.isClass(term));
	}
}
