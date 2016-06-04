// Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package com.clarkparsia.pellet.test.rbox;

import static com.clarkparsia.pellet.utils.TermFactory.all;
import static com.clarkparsia.pellet.utils.TermFactory.hasValue;
import static com.clarkparsia.pellet.utils.TermFactory.inv;
import static com.clarkparsia.pellet.utils.TermFactory.list;
import static com.clarkparsia.pellet.utils.TermFactory.not;
import static com.clarkparsia.pellet.utils.TermFactory.some;
import static com.clarkparsia.pellet.utils.TermFactory.term;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mindswap.pellet.test.PelletTestCase.addStatements;
import static org.mindswap.pellet.test.PelletTestCase.assertIteratorValues;
import static org.mindswap.pellet.test.PelletTestCase.assertPropertyValues;

import aterm.ATermAppl;
import java.util.Arrays;
import junit.framework.JUnit4TestAdapter;
import org.apache.jena.ontology.ObjectProperty;
import org.apache.jena.ontology.OntClass;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Resource;
import org.junit.Ignore;
import org.junit.Test;
import org.mindswap.pellet.KnowledgeBase;
import org.mindswap.pellet.Role;
import org.mindswap.pellet.jena.PelletInfGraph;
import org.mindswap.pellet.jena.PelletReasonerFactory;
import org.mindswap.pellet.test.AbstractKBTests;
import org.mindswap.pellet.test.PelletTestSuite;
import org.mindswap.pellet.utils.ATermUtils;

/**
 * <p>
 * Title:
 * </p>
 * <p>
 * Description:
 * </p>
 * <p>
 * Copyright: Copyright (c) 2009
 * </p>
 * <p>
 * Company: Clark & Parsia, LLC. <http://www.clarkparsia.com>
 * </p>
 *
 * @author Evren Sirin
 */
public class PropertyChainTests extends AbstractKBTests
{
	public static String _base = "file:" + PelletTestSuite.base + "misc/";

	public static junit.framework.Test suite()
	{
		return new JUnit4TestAdapter(PropertyChainTests.class);
	}

	@Test
	public void testInversesInPropertyChain()
	{
		final ATermAppl hasMother = term("hasMother");
		final ATermAppl hasParent = term("hasParent");
		final ATermAppl hasSibling = term("hasSibling");
		final ATermAppl hasChild = term("hasChild");

		individuals(_a, _b, _c);
		objectProperties(hasMother, hasParent, hasSibling, hasChild);

		_kb.addSubProperty(hasMother, hasParent);
		_kb.addInverseProperty(hasChild, hasParent);
		_kb.addSubProperty(list(hasParent, hasChild), hasSibling);

		_kb.addPropertyValue(hasMother, _a, _c);
		_kb.addPropertyValue(hasMother, _b, _c);

		assertTrue(_kb.hasPropertyValue(_a, hasSibling, _b));
		assertTrue(_kb.hasPropertyValue(_b, hasSibling, _a));
		assertIteratorValues(_kb.getPropertyValues(hasSibling, _a).iterator(), new ATermAppl[] { _a, _b });
		assertIteratorValues(_kb.getPropertyValues(hasSibling, _b).iterator(), new ATermAppl[] { _a, _b });
	}

	@Test
	public void testAnonymousInversesInPropertyChain()
	{
		final ATermAppl hasMother = term("hasMother");
		final ATermAppl hasParent = term("hasParent");
		final ATermAppl hasSibling = term("hasSibling");

		individuals(_a, _b, _c);
		objectProperties(hasMother, hasParent, hasSibling);

		_kb.addSubProperty(hasMother, hasParent);
		_kb.addSubProperty(list(hasParent, inv(hasParent)), hasSibling);

		_kb.addIndividual(_a);
		_kb.addIndividual(_b);
		_kb.addIndividual(_c);
		_kb.addPropertyValue(hasMother, _a, _c);
		_kb.addPropertyValue(hasMother, _b, _c);

		assertTrue(_kb.hasPropertyValue(_a, hasSibling, _b));
		assertTrue(_kb.hasPropertyValue(_b, hasSibling, _a));
		assertIteratorValues(_kb.getPropertyValues(hasSibling, _a).iterator(), new ATermAppl[] { _a, _b });
		assertIteratorValues(_kb.getPropertyValues(hasSibling, _b).iterator(), new ATermAppl[] { _a, _b });
	}

	@Test
	public void testRoleAbsorptionWithPropertyChain()
	{
		classes(_A, _B, _C, _D);
		objectProperties(_p, _q, _r);

		_kb.addSubProperty(list(_p, _q), _r);

		_kb.addSubClass(_C, _B);
		_kb.addEquivalentClass(_C, some(_r, _D));

		_kb.addSubClass(_A, some(_p, some(_q, _D)));

		assertTrue(_kb.isSubClassOf(_A, _C));

		_kb.classify();

		assertTrue(_kb.isSubClassOf(_A, _C));
	}

	@Ignore("See ticket #294")
	@Test
	public void testNestedPropertyChains()
	{
		individuals(_a, _b, _c, _d);
		objectProperties(_p, _q, _r, _s, _f);

		_kb.addTransitiveProperty(_r);
		_kb.addTransitiveProperty(_s);

		_kb.addSubProperty(list(_p, _q), _r);
		_kb.addSubProperty(list(_r, _s), _f);

		_kb.addPropertyValue(_p, _a, _b);
		_kb.addPropertyValue(_q, _b, _c);
		_kb.addPropertyValue(_s, _c, _d);

		assertTrue(_kb.hasPropertyValue(_a, _f, _d));

		assertTrue(_kb.isType(_a, hasValue(_f, _d)));
	}

	@Ignore("See ticket #294")
	@Test
	public void testTransitivePropertyChain()
	{
		classes(_C, _D);
		objectProperties(_p, _q, _r);

		_kb.addSubProperty(list(_p, _q), _r);
		_kb.addTransitiveProperty(_p);

		_kb.addSubClass(_C, some(_p, some(_q, some(_p, some(_q, _D)))));
		_kb.addSubClass(_C, all(_r, not(_D)));

		assertFalse(_kb.isSatisfiable(_C));
	}

	@Test
	public void testSimplePropertyChain()
	{
		final KnowledgeBase kb = new KnowledgeBase();

		final ATermAppl C = term("C");
		final ATermAppl D = term("D");
		final ATermAppl p = term("p");
		final ATermAppl q = term("q");
		final ATermAppl r = term("r");
		final ATermAppl a = term("a");
		final ATermAppl b = term("b");
		final ATermAppl c = term("c");

		kb.addObjectProperty(p);
		kb.addObjectProperty(q);
		kb.addObjectProperty(r);
		kb.addSubProperty(list(p, q), r);

		kb.addClass(C);
		kb.addClass(D);
		kb.addSubClass(C, some(p, some(q, all(inv(r), D))));

		kb.addIndividual(a);
		kb.addIndividual(b);
		kb.addIndividual(c);
		kb.addPropertyValue(p, a, b);
		kb.addPropertyValue(q, b, c);

		assertTrue(kb.isSubClassOf(C, D));
		assertTrue(kb.hasPropertyValue(a, r, c));
		assertEquals(Arrays.asList(c), kb.getPropertyValues(r, a));
	}

	@Test
	public void invalidCycle1()
	{
		final KnowledgeBase kb = new KnowledgeBase();

		final ATermAppl B = term("B");
		final ATermAppl C = term("C");
		final ATermAppl D = term("D");
		final ATermAppl p = term("p");
		final ATermAppl q = term("q");
		final ATermAppl r = term("r");
		final ATermAppl a = term("a");
		final ATermAppl b = term("b");
		final ATermAppl c = term("c");
		final ATermAppl d = term("d");

		kb.addObjectProperty(p);
		kb.addObjectProperty(q);
		kb.addObjectProperty(r);
		kb.addSubProperty(list(p, q, r), q);

		kb.addClass(B);
		kb.addClass(C);
		kb.addClass(D);
		kb.addSubClass(B, some(p, some(q, some(r, D))));
		kb.addSubClass(C, some(q, D));

		kb.addIndividual(a);
		kb.addIndividual(b);
		kb.addIndividual(c);
		kb.addIndividual(d);
		kb.addPropertyValue(p, a, b);
		kb.addPropertyValue(q, b, c);
		kb.addPropertyValue(r, c, d);

		kb.prepare();

		assertTrue(kb.getRole(q).isSimple());

		assertFalse(kb.isSubClassOf(B, C));
		assertFalse(kb.hasPropertyValue(a, q, c));
		assertTrue(kb.getPropertyValues(q, a).isEmpty());
	}

	@Test
	public void validCycle1()
	{
		final KnowledgeBase kb = new KnowledgeBase();

		final ATermAppl C = term("C");
		final ATermAppl D = term("D");
		final ATermAppl p = term("p");
		final ATermAppl q = term("q");
		final ATermAppl r = term("r");
		final ATermAppl a = term("a");
		final ATermAppl b = term("b");
		final ATermAppl c = term("c");
		final ATermAppl d = term("d");

		kb.addObjectProperty(p);
		kb.addObjectProperty(q);
		kb.addObjectProperty(r);
		kb.addSubProperty(list(p, q, r), p);

		kb.addClass(C);
		kb.addClass(D);
		kb.addSubClass(C, some(p, some(q, some(r, all(inv(p), D)))));

		kb.addIndividual(a);
		kb.addIndividual(b);
		kb.addIndividual(c);
		kb.addIndividual(d);
		kb.addPropertyValue(p, a, b);
		kb.addPropertyValue(q, b, c);
		kb.addPropertyValue(r, c, d);

		kb.prepare();

		assertFalse(kb.getRole(p).isSimple());

		assertTrue(kb.isSubClassOf(C, D));
		assertTrue(kb.hasPropertyValue(a, p, d));
		assertIteratorValues(kb.getPropertyValues(p, a).iterator(), new ATermAppl[] { b, d });
	}

	@Test
	public void validCycle3()
	{
		final KnowledgeBase kb = new KnowledgeBase();

		final ATermAppl C = term("C");
		final ATermAppl D = term("D");
		final ATermAppl p = term("p");
		final ATermAppl q = term("q");
		final ATermAppl r = term("r");
		final ATermAppl r2 = term("r2");
		final ATermAppl a = term("a");
		final ATermAppl b = term("b");
		final ATermAppl c = term("c");
		final ATermAppl d = term("d");

		kb.addObjectProperty(p);
		kb.addObjectProperty(q);
		kb.addObjectProperty(r);
		kb.addObjectProperty(r2);
		kb.addEquivalentProperty(r, r2);
		kb.addSubProperty(list(p, q, r2), r);

		kb.addClass(C);
		kb.addClass(D);
		kb.addSubClass(C, some(p, some(q, some(r, all(inv(r), D)))));

		kb.addIndividual(a);
		kb.addIndividual(b);
		kb.addIndividual(c);
		kb.addIndividual(d);
		kb.addPropertyValue(p, a, b);
		kb.addPropertyValue(q, b, c);
		kb.addPropertyValue(r, c, d);

		kb.prepare();

		assertFalse(kb.getRole(r).isSimple());

		assertTrue(kb.isSubClassOf(C, D));
		assertTrue(kb.hasPropertyValue(a, r, d));
		assertEquals(Arrays.asList(d), kb.getPropertyValues(r, a));
	}

	@Test
	public void testPropertyChain()
	{
		testPropertyChainBase("propertyChain.owl");
	}

	public void testPropertyChainBase(final String filename)
	{
		final String ns = "http://www.example.org/test#";

		final OntModel model = ModelFactory.createOntologyModel(PelletReasonerFactory.THE_SPEC);
		model.read(_base + filename);

		final OntClass C = model.getOntClass(ns + "C");
		final OntClass S0 = model.getOntClass(ns + "S0");
		final OntClass R0 = model.getOntClass(ns + "R0");
		final OntClass R1 = model.getOntClass(ns + "R1");
		final ObjectProperty r = model.getObjectProperty(ns + "r");
		final ObjectProperty s = model.getObjectProperty(ns + "s");

		final int n = 17;
		final Resource[] a = new Resource[n];
		for (int i = 0; i < n; i++)
			a[i] = model.getResource(ns + "a" + i);

		final Resource[] theList = new Resource[] { a[1], a[2], a[3], a[4], a[5], a[6], a[8], a[10], a[12], a[14], a[16] };

		assertTrue(r.isTransitiveProperty());
		assertFalse(s.isTransitiveProperty());

		assertIteratorValues(C.listInstances(), theList);

		assertIteratorValues(S0.listInstances(), theList);

		assertIteratorValues(R0.listInstances(), new Resource[] { a[7], a[9] });

		assertIteratorValues(R1.listInstances(), new Resource[] { a[2], a[3], a[4], a[5], a[6] });

		final Model rValues = ModelFactory.createDefaultModel();
		addStatements(rValues, a[0], r, a[7], a[9]);
		addStatements(rValues, a[1], r, a[2], a[3], a[4], a[5], a[6]);
		addStatements(rValues, a[2], r, a[4], a[5], a[6]);
		addStatements(rValues, a[4], r, a[5], a[6]);
		addStatements(rValues, a[7], r, a[9]);
		addStatements(rValues, a[8], r, a[10]);
		assertPropertyValues(model, r, rValues);

		final Model sValues = ModelFactory.createDefaultModel();
		addStatements(sValues, a[0], s, a[1], a[2], a[3], a[4], a[5], a[6], a[8], a[10], a[12], a[14], a[16]);
		addStatements(sValues, a[7], s, a[8], a[10], a[12]);
		addStatements(sValues, a[8], s, a[11]);
		addStatements(sValues, a[9], s, a[12]);
		addStatements(sValues, a[10], s, a[11]);
		addStatements(sValues, a[13], s, a[14]);
		addStatements(sValues, a[15], s, a[16]);
		assertPropertyValues(model, s, sValues);
	}

	@Test
	public void testPropertyChainDeprecated()
	{
		testPropertyChainBase("propertyChainDeprecated.owl");
	}

	@Test
	public void testPropertyChainInvalid()
	{
		final OntModel model = ModelFactory.createOntologyModel(PelletReasonerFactory.THE_SPEC);
		model.read(_base + "propertyChainInvalid.owl");

		model.prepare();

		final KnowledgeBase kb = ((PelletInfGraph) model.getGraph()).getKB();

		for (final Role r : kb.getRBox().getRoles())
			if (!ATermUtils.isBuiltinProperty(r.getName()))
			{
				assertTrue(r + " is not simple", r.isSimple());
				assertFalse(r + " is transitive", r.isTransitive());
				assertFalse(r + " has complex sub roles", r.hasComplexSubRole());
			}
	}

	@Test
	public void testPropertyChainInverses()
	{
		final String ns = "http://www.example.org/test#";

		final OntModel model = ModelFactory.createOntologyModel(PelletReasonerFactory.THE_SPEC);
		model.read(_base + "propertyChainInverses.owl");

		final ObjectProperty p = model.getObjectProperty(ns + "p");
		final ObjectProperty q = model.getObjectProperty(ns + "q");

		final int n = 8;
		final Resource[] a = new Resource[n];
		for (int i = 0; i < n; i++)
			a[i] = model.getResource(ns + "a" + i);

		final Model pValues = ModelFactory.createDefaultModel();
		addStatements(pValues, a[0], p, a[1], a[3], a[4]);
		addStatements(pValues, a[3], p, a[4]);
		addStatements(pValues, a[6], p, a[0], a[1], a[3], a[4]);
		addStatements(pValues, a[7], p, a[6], a[0], a[1], a[3], a[4]);
		assertPropertyValues(model, p, pValues);

		final Model qValues = ModelFactory.createDefaultModel();
		addStatements(qValues, a[0], q, a[2], a[5]);
		addStatements(qValues, a[3], q, a[5]);
		addStatements(qValues, a[6], q, a[2], a[5]);
		addStatements(qValues, a[7], q, a[2], a[5]);
		assertPropertyValues(model, q, qValues);
	}

	@Test
	public void testPropertyChainValid()
	{
		final String ns = "http://www.example.org/test#";

		final OntModel model = ModelFactory.createOntologyModel(PelletReasonerFactory.THE_SPEC);
		model.read(_base + "propertyChainValid.owl");

		final ObjectProperty r = model.getObjectProperty(ns + "r");

		final int n = 6;
		final Resource[] a = new Resource[n];
		for (int i = 0; i < n; i++)
			a[i] = model.getResource(ns + "a" + i);

		final Model rValues = ModelFactory.createDefaultModel();
		addStatements(rValues, a[0], r, a[3], a[5]);
		addStatements(rValues, a[1], r, a[3], a[5]);
		addStatements(rValues, a[2], r, a[3]);
		addStatements(rValues, a[4], r, a[5]);
		assertPropertyValues(model, r, rValues);
	}

	@Test
	public void invalidCycle2()
	{
		final KnowledgeBase kb = new KnowledgeBase();

		final ATermAppl B = term("B");
		final ATermAppl C = term("C");
		final ATermAppl D = term("D");
		final ATermAppl p = term("p");
		final ATermAppl q2 = term("q2");
		final ATermAppl q = term("q");
		final ATermAppl r = term("r");
		final ATermAppl a = term("a");
		final ATermAppl b = term("b");
		final ATermAppl c = term("c");
		final ATermAppl d = term("d");

		kb.addObjectProperty(p);
		kb.addObjectProperty(q2);
		kb.addObjectProperty(q);
		kb.addObjectProperty(r);

		kb.addEquivalentProperty(q, q2);
		kb.addSubProperty(list(p, q2, r), q);

		kb.addClass(B);
		kb.addClass(C);
		kb.addClass(D);
		kb.addSubClass(B, some(p, some(q, some(r, D))));
		kb.addSubClass(C, some(q, D));

		kb.addIndividual(a);
		kb.addIndividual(b);
		kb.addIndividual(c);
		kb.addIndividual(d);
		kb.addPropertyValue(p, a, b);
		kb.addPropertyValue(q, b, c);
		kb.addPropertyValue(r, c, d);

		kb.prepare();

		assertTrue(kb.getRole(q).isSimple());

		assertFalse(kb.isSubClassOf(B, C));
		assertFalse(kb.hasPropertyValue(a, q, c));
		assertTrue(kb.getPropertyValues(q, a).isEmpty());
	}

	@Test
	public void validCycle2()
	{
		final KnowledgeBase kb = new KnowledgeBase();

		final ATermAppl C = term("C");
		final ATermAppl D = term("D");
		final ATermAppl p = term("p");
		final ATermAppl q = term("q");
		final ATermAppl r = term("r");
		final ATermAppl a = term("a");
		final ATermAppl b = term("b");
		final ATermAppl c = term("c");
		final ATermAppl d = term("d");

		kb.addObjectProperty(p);
		kb.addObjectProperty(q);
		kb.addObjectProperty(r);
		kb.addSubProperty(list(p, q, r), r);

		kb.addClass(C);
		kb.addClass(D);
		kb.addSubClass(C, some(p, some(q, some(r, all(inv(r), D)))));

		kb.addIndividual(a);
		kb.addIndividual(b);
		kb.addIndividual(c);
		kb.addIndividual(d);
		kb.addPropertyValue(p, a, b);
		kb.addPropertyValue(q, b, c);
		kb.addPropertyValue(r, c, d);

		kb.prepare();

		assertFalse(kb.getRole(r).isSimple());

		assertTrue(kb.isSubClassOf(C, D));
		assertTrue(kb.hasPropertyValue(a, r, d));
		assertEquals(Arrays.asList(d), kb.getPropertyValues(r, a));
	}

	@Test
	public void updateAfterConsistency()
	{
		classes(_C, _D);
		objectProperties(_p, _q, _r, _s);
		individuals(_a, _b, _c, _d);

		_kb.addSubProperty(list(_p, _q, _r), _s);

		_kb.addSubClass(_C, all(_s, _D));

		_kb.addType(_a, _C);
		_kb.addPropertyValue(_p, _a, _b);
		_kb.addPropertyValue(_q, _b, _c);

		assertTrue(_kb.isConsistent());
		assertFalse(_kb.isType(_d, _D));

		_kb.addPropertyValue(_r, _c, _d);

		assertTrue(_kb.isType(_d, _D));
	}
}
