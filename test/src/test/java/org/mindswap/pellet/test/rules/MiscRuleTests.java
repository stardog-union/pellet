// Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package org.mindswap.pellet.test.rules;

import static com.clarkparsia.pellet.utils.TermFactory.TOP;
import static com.clarkparsia.pellet.utils.TermFactory.all;
import static com.clarkparsia.pellet.utils.TermFactory.and;
import static com.clarkparsia.pellet.utils.TermFactory.hasValue;
import static com.clarkparsia.pellet.utils.TermFactory.literal;
import static com.clarkparsia.pellet.utils.TermFactory.max;
import static com.clarkparsia.pellet.utils.TermFactory.min;
import static com.clarkparsia.pellet.utils.TermFactory.not;
import static com.clarkparsia.pellet.utils.TermFactory.oneOf;
import static com.clarkparsia.pellet.utils.TermFactory.or;
import static com.clarkparsia.pellet.utils.TermFactory.some;
import static com.clarkparsia.pellet.utils.TermFactory.term;
import static com.clarkparsia.pellet.utils.TermFactory.value;
import static java.util.Collections.singleton;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mindswap.pellet.test.PelletTestCase.assertIteratorValues;
import static org.mindswap.pellet.utils.Namespaces.SWRLB;

import aterm.ATermAppl;
import com.clarkparsia.owlapi.OWL;
import com.clarkparsia.owlapi.SWRL;
import com.clarkparsia.pellet.datatypes.Datatypes;
import com.clarkparsia.pellet.owlapi.PelletReasoner;
import com.clarkparsia.pellet.rules.VariableUtils;
import com.clarkparsia.pellet.rules.model.AtomDConstant;
import com.clarkparsia.pellet.rules.model.AtomDVariable;
import com.clarkparsia.pellet.rules.model.AtomIConstant;
import com.clarkparsia.pellet.rules.model.AtomIObject;
import com.clarkparsia.pellet.rules.model.AtomIVariable;
import com.clarkparsia.pellet.rules.model.BuiltInAtom;
import com.clarkparsia.pellet.rules.model.ClassAtom;
import com.clarkparsia.pellet.rules.model.DatavaluedPropertyAtom;
import com.clarkparsia.pellet.rules.model.DifferentIndividualsAtom;
import com.clarkparsia.pellet.rules.model.IndividualPropertyAtom;
import com.clarkparsia.pellet.rules.model.Rule;
import com.clarkparsia.pellet.rules.model.RuleAtom;
import com.clarkparsia.pellet.rules.model.SameIndividualAtom;
import com.clarkparsia.pellet.utils.TermFactory;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import junit.framework.JUnit4TestAdapter;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;
import org.junit.Test;
import org.mindswap.pellet.PelletOptions;
import org.mindswap.pellet.jena.PelletReasonerFactory;
import org.mindswap.pellet.test.AbstractKBTests;
import org.mindswap.pellet.test.PelletTestSuite;
import org.semanticweb.owlapi.io.StringDocumentSource;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLObjectSomeValuesFrom;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.SWRLAtom;
import org.semanticweb.owlapi.model.SWRLVariable;

public class MiscRuleTests extends AbstractKBTests
{

	public final static String base = "file:" + PelletTestSuite.base + "swrl-test/misc/";
	private static final IRI luigiFamily = IRI.create("http://www.csc.liv.ac.uk/~luigi/ontologies/basicFamily");

	public static junit.framework.Test suite()
	{
		return new JUnit4TestAdapter(MiscRuleTests.class);
	}

	private void nonTrivialBuiltInTest()
	{
		final ATermAppl d1 = literal("1", Datatypes.INT), d2 = literal("2", Datatypes.INT), d12 = literal("3", Datatypes.INTEGER), i = term("i"), p = term("p"), q = term("q"), r = term("r");

		_kb.addDatatypeProperty(p);
		_kb.addDatatypeProperty(q);
		_kb.addDatatypeProperty(r);

		_kb.addIndividual(i);

		_kb.addSubClass(TOP, hasValue(p, d1));
		_kb.addSubClass(TOP, hasValue(q, d2));

		final AtomIVariable x = new AtomIVariable("x");
		final AtomDVariable z1 = new AtomDVariable("z1");
		final AtomDVariable z2 = new AtomDVariable("z2");
		final AtomDVariable z3 = new AtomDVariable("z3");

		final List<RuleAtom> body = new ArrayList<>();
		body.add(new DatavaluedPropertyAtom(p, x, z1));
		body.add(new DatavaluedPropertyAtom(q, x, z2));
		body.add(new BuiltInAtom(SWRLB + "add", z3, z1, z2));

		final List<RuleAtom> head = new ArrayList<>();
		head.add(new DatavaluedPropertyAtom(r, x, z3));

		_kb.addRule(new Rule(head, body));

		_kb.realize();
		assertTrue(_kb.hasPropertyValue(i, r, d12));

	}

	@Test
	public void builtInDateTime()
	{

		final ATermAppl amy = term("Amy"), basil = term("Basil"), clara = term("Clara"), desmond = term("Desmond");

		final ATermAppl bDate = term("bDate"), bYear = term("bYear"), bMonth = term("bMonth"), bDay = term("bDay"), bTZ = term("bTZ");

		_kb.addDatatypeProperty(bDate);
		_kb.addDatatypeProperty(bYear);
		_kb.addDatatypeProperty(bMonth);
		_kb.addDatatypeProperty(bDay);
		_kb.addDatatypeProperty(bTZ);

		_kb.addIndividual(amy);
		_kb.addPropertyValue(bDate, amy, literal("2001-01-11", Datatypes.DATE));

		_kb.addIndividual(basil);
		_kb.addPropertyValue(bDate, basil, literal("2002-02-12Z", Datatypes.DATE));

		_kb.addIndividual(clara);
		_kb.addPropertyValue(bYear, clara, literal("2003", Datatypes.INTEGER));
		_kb.addPropertyValue(bMonth, clara, literal("3", Datatypes.INTEGER));
		_kb.addPropertyValue(bDay, clara, literal("13", Datatypes.INTEGER));

		_kb.addIndividual(desmond);
		_kb.addPropertyValue(bYear, desmond, literal("2004", Datatypes.INTEGER));
		_kb.addPropertyValue(bMonth, desmond, literal("4", Datatypes.INTEGER));
		_kb.addPropertyValue(bDay, desmond, literal("14", Datatypes.INTEGER));
		_kb.addPropertyValue(bTZ, desmond, literal("+01:01"));

		final AtomIVariable x = new AtomIVariable("x");
		final AtomDVariable xDate = new AtomDVariable("xDate"), xYear = new AtomDVariable("xYear"), xMonth = new AtomDVariable("xMonth"), xDay = new AtomDVariable("xDay"), xTZ = new AtomDVariable("xTZ");

		final RuleAtom dateBuiltIn = new BuiltInAtom(SWRLB + "date", xDate, xYear, xMonth, xDay);
		final RuleAtom dateBuiltInTZ = new BuiltInAtom(SWRLB + "date", xDate, xYear, xMonth, xDay, xTZ);
		final RuleAtom bDateAtom = new DatavaluedPropertyAtom(bDate, x, xDate);
		final RuleAtom bYearAtom = new DatavaluedPropertyAtom(bYear, x, xYear);
		final RuleAtom bMonthAtom = new DatavaluedPropertyAtom(bMonth, x, xMonth);
		final RuleAtom bDayAtom = new DatavaluedPropertyAtom(bDay, x, xDay);
		final RuleAtom bTZAtom = new DatavaluedPropertyAtom(bTZ, x, xTZ);

		final Rule fromDate = new Rule(Arrays.asList(new RuleAtom[] { bYearAtom, bMonthAtom, bDayAtom }), Arrays.asList(new RuleAtom[] { dateBuiltIn, bDateAtom }));
		_kb.addRule(fromDate);

		final Rule fromDateTZ = new Rule(Arrays.asList(new RuleAtom[] { bYearAtom, bMonthAtom, bDayAtom, bTZAtom }), Arrays.asList(new RuleAtom[] { dateBuiltInTZ, bDateAtom }));
		_kb.addRule(fromDateTZ);

		final Rule toDate = new Rule(Arrays.asList(new RuleAtom[] { bDateAtom }), Arrays.asList(new RuleAtom[] { dateBuiltIn, bYearAtom, bMonthAtom, bDayAtom }));
		_kb.addRule(toDate);

		final Rule toDateTZ = new Rule(Arrays.asList(new RuleAtom[] { bDateAtom }), Arrays.asList(new RuleAtom[] { dateBuiltInTZ, bYearAtom, bMonthAtom, bDayAtom, bTZAtom }));
		_kb.addRule(toDateTZ);

		assertTrue(_kb.isConsistent());

		assertTrue(_kb.hasPropertyValue(amy, bYear, literal("2001", Datatypes.INTEGER)));
		assertTrue(_kb.hasPropertyValue(amy, bMonth, literal("1", Datatypes.INTEGER)));
		assertTrue(_kb.hasPropertyValue(amy, bDay, literal("11", Datatypes.INTEGER)));

		assertTrue(_kb.hasPropertyValue(basil, bYear, literal("2002", Datatypes.INTEGER)));
		assertTrue(_kb.hasPropertyValue(basil, bMonth, literal("2", Datatypes.INTEGER)));
		assertTrue(_kb.hasPropertyValue(basil, bDay, literal("12", Datatypes.INTEGER)));
		assertTrue(_kb.hasPropertyValue(basil, bTZ, literal("Z")));

		assertTrue(_kb.hasPropertyValue(clara, bDate, literal("2003-03-13", Datatypes.DATE)));

		assertTrue(_kb.hasPropertyValue(desmond, bDate, literal("2004-04-14+01:01", Datatypes.DATE)));

	}

	@Test
	public void builtInMath()
	{

		final ATermAppl d1 = literal("1", Datatypes.INT), d2 = literal("1.5", Datatypes.FLOAT), dif11 = literal("0", Datatypes.INTEGER), dif12 = literal("-0.5", Datatypes.FLOAT), dif21 = literal("0.5", Datatypes.FLOAT), dif22 = literal("0", Datatypes.FLOAT), prod11 = literal("1", Datatypes.INTEGER), prod12 = literal("1.5", Datatypes.FLOAT), prod22 = literal("2.25", Datatypes.FLOAT), quot11 = literal("1", Datatypes.DECIMAL), quot12 = literal(Float.toString((float) (1.0 / 1.5)), Datatypes.FLOAT), quot21 = literal("1.5", Datatypes.FLOAT), quot22 = literal("1", Datatypes.FLOAT), sum11 = literal("2", Datatypes.INTEGER), sum12 = literal("2.5", Datatypes.FLOAT), sum22 = literal("3.0", Datatypes.FLOAT), i = term("i"), p = term("p"), sum = term("sum"), product = term("product"), difference = term("difference"), quotient = term("quotient");

		_kb.addDatatypeProperty(p);
		_kb.addDatatypeProperty(sum);
		_kb.addDatatypeProperty(difference);
		_kb.addDatatypeProperty(product);
		_kb.addDatatypeProperty(quotient);

		_kb.addIndividual(i);
		_kb.addPropertyValue(p, i, d1);
		_kb.addPropertyValue(p, i, d2);

		final AtomIVariable x = new AtomIVariable("x");
		final AtomDVariable z1 = new AtomDVariable("z1");
		final AtomDVariable z2 = new AtomDVariable("z2");
		final AtomDVariable z3 = new AtomDVariable("z3");
		final AtomDVariable z4 = new AtomDVariable("z4");
		final AtomDVariable z5 = new AtomDVariable("z5");
		final AtomDVariable z6 = new AtomDVariable("z6");

		final List<RuleAtom> body = new ArrayList<>();
		body.add(new DatavaluedPropertyAtom(p, x, z1));
		body.add(new DatavaluedPropertyAtom(p, x, z2));
		body.add(new BuiltInAtom(SWRLB + "add", z3, z1, z2));
		body.add(new BuiltInAtom(SWRLB + "subtract", z4, z1, z2));
		body.add(new BuiltInAtom(SWRLB + "multiply", z5, z1, z2));
		body.add(new BuiltInAtom(SWRLB + "divide", z6, z1, z2));

		final List<RuleAtom> head = new ArrayList<>();
		head.add(new DatavaluedPropertyAtom(sum, x, z3));
		head.add(new DatavaluedPropertyAtom(difference, x, z4));
		head.add(new DatavaluedPropertyAtom(product, x, z5));
		head.add(new DatavaluedPropertyAtom(quotient, x, z6));

		final Rule rule = new Rule(head, body);
		_kb.addRule(rule);

		_kb.realize();
		assertTrue(_kb.hasPropertyValue(i, sum, sum11));
		assertTrue(_kb.hasPropertyValue(i, sum, sum12));
		assertTrue(_kb.hasPropertyValue(i, sum, sum22));
		assertTrue(_kb.hasPropertyValue(i, difference, dif11));
		assertTrue(_kb.hasPropertyValue(i, difference, dif12));
		assertTrue(_kb.hasPropertyValue(i, difference, dif21));
		assertTrue(_kb.hasPropertyValue(i, difference, dif22));
		assertTrue(_kb.hasPropertyValue(i, product, prod11));
		assertTrue(_kb.hasPropertyValue(i, product, prod12));
		assertTrue(_kb.hasPropertyValue(i, product, prod22));
		assertTrue(_kb.hasPropertyValue(i, quotient, quot11));
		assertTrue(_kb.hasPropertyValue(i, quotient, quot12));
		assertTrue(_kb.hasPropertyValue(i, quotient, quot21));
		assertTrue(_kb.hasPropertyValue(i, quotient, quot22));

	}

	@Test
	public void builtInNonTrivialOldStrategy()
	{
		final boolean whichStrategy = PelletOptions.USE_CONTINUOUS_RULES;
		PelletOptions.USE_CONTINUOUS_RULES = false;
		try
		{
			nonTrivialBuiltInTest();
		}
		finally
		{
			PelletOptions.USE_CONTINUOUS_RULES = whichStrategy;
		}

	}

	@Test
	public void builtInNonTrivialNewStrategy()
	{
		final boolean whichStrategy = PelletOptions.USE_CONTINUOUS_RULES;
		PelletOptions.USE_CONTINUOUS_RULES = true;
		try
		{
			nonTrivialBuiltInTest();
		}
		finally
		{
			PelletOptions.USE_CONTINUOUS_RULES = whichStrategy;
		}
	}

	/**
	 * Simple property chain test. Mostly tests the rete engine
	 */
	@Test
	public void dataPropertyChain1()
	{

		final ATermAppl d = literal("d"), i = term("i"), j = term("j"), k = term("k"), p = term("p"), r = term("r");

		_kb.addDatatypeProperty(p);
		_kb.addObjectProperty(r);

		_kb.addIndividual(i);
		_kb.addIndividual(j);
		_kb.addIndividual(k);

		_kb.addPropertyValue(p, i, d);
		_kb.addPropertyValue(r, i, j);
		_kb.addPropertyValue(r, j, k);

		final AtomIVariable x = new AtomIVariable("x"), y = new AtomIVariable("y");
		final AtomDVariable z = new AtomDVariable("z");

		final RuleAtom body1 = new IndividualPropertyAtom(r, x, y);
		final RuleAtom body2 = new DatavaluedPropertyAtom(p, x, z), head = new DatavaluedPropertyAtom(p, y, z);

		final Rule rule = new Rule(Collections.singleton(head), Arrays.asList(new RuleAtom[] { body1, body2 }));
		_kb.addRule(rule);

		_kb.realize();
		assertTrue(_kb.hasPropertyValue(j, p, d));
		assertTrue(_kb.hasPropertyValue(k, p, d));
	}

	/**
	 * More complicated property chain test. Tests the rule _strategy
	 */
	@Test
	public void dataPropertyChain2()
	{

		final ATermAppl d = literal("d"), i = term("i"), j = term("j"), k = term("k"), p = term("p"), r = term("r");

		_kb.addDatatypeProperty(p);
		_kb.addObjectProperty(r);

		_kb.addIndividual(i);
		_kb.addIndividual(j);
		_kb.addIndividual(k);

		_kb.addSubClass(TOP, oneOf(i, j, k));
		_kb.addSubClass(TOP, min(r, 3, TOP));

		_kb.addPropertyValue(p, i, d);

		final AtomIVariable x = new AtomIVariable("x"), y = new AtomIVariable("y");
		final AtomDVariable z = new AtomDVariable("z");

		final RuleAtom body1 = new IndividualPropertyAtom(r, x, y);
		final RuleAtom body2 = new DatavaluedPropertyAtom(p, x, z);
		final RuleAtom head = new DatavaluedPropertyAtom(p, y, z);

		final Rule rule = new Rule(Collections.singleton(head), Arrays.asList(body1, body2));
		_kb.addRule(rule);

		_kb.realize();
		assertTrue(_kb.hasPropertyValue(j, p, d));
		assertTrue(_kb.hasPropertyValue(k, p, d));
	}

	@SuppressWarnings("unused")
	@Test
	public void inferredProperties() throws Exception
	{
		final ATermAppl d = literal("foo"), i = term("i"), j = term("j"), k = term("k"), p = term("p"), r = term("r");

		_kb.addIndividual(i);
		_kb.addIndividual(j);
		_kb.addIndividual(k);
		_kb.addDatatypeProperty(p);
		_kb.addObjectProperty(r);
		_kb.addSubClass(TOP, min(r, 3, TOP));
		_kb.addSubClass(TOP, or(value(i), value(j), value(k)));
		_kb.addPropertyValue(p, i, d);

		final AtomIVariable x = new AtomIVariable("x"), y = new AtomIVariable("y");
		final AtomDVariable z = new AtomDVariable("z");

		final RuleAtom head = new DatavaluedPropertyAtom(p, x, z);
		final RuleAtom body1 = new DatavaluedPropertyAtom(p, y, z);
		final RuleAtom body2 = new IndividualPropertyAtom(r, x, y);

		final Rule rule = new Rule(Collections.singleton(head), Arrays.asList(body1, body2));
		_kb.addRule(rule);

		_kb.ensureConsistency();

		assertTrue(_kb.hasPropertyValue(j, r, i));
		assertTrue(_kb.hasPropertyValue(k, r, i));

		assertTrue(_kb.hasPropertyValue(j, p, d));
		assertTrue(_kb.hasPropertyValue(k, p, d));

	}

	@Test
	public void testRuleIndividuals() throws Exception
	{
		final ATermAppl c = term("C"), d = term("D"), i = term("i");

		_kb.addClass(c);
		_kb.addClass(d);
		_kb.addIndividual(i);
		_kb.addType(i, c);

		_kb.addRule(new Rule(Arrays.asList(new RuleAtom[] { new ClassAtom(d, new AtomIConstant(i)) }), Arrays.asList(new RuleAtom[] { new ClassAtom(c, new AtomIConstant(i)) })));

		_kb.realize();
		assertTrue(_kb.getTypes(i).contains(Collections.singleton(d)));

	}

	@Test
	public void testRuleEquality()
	{
		final ATermAppl r = term("r");
		final ATermAppl i = term("i");
		final ATermAppl j = term("j");

		final AtomIObject x = new AtomIVariable("x");
		final AtomIObject y = new AtomIVariable("y");

		_kb.addIndividual(i);
		_kb.addIndividual(j);
		_kb.addObjectProperty(r);
		_kb.addSubClass(TOP, min(r, 1, TOP));
		_kb.addSubClass(TOP, oneOf(i, j));

		_kb.addRule(new Rule(Collections.singletonList(new DifferentIndividualsAtom(x, y)), Collections.singletonList(new IndividualPropertyAtom(r, x, y))));

		_kb.realize();
		assertTrue(_kb.isConsistent());
		assertTrue(_kb.isDifferentFrom(i, j));
	}

	public void testLuigiFamilyJena() throws Exception
	{
		final OntModel ontModel = ModelFactory.createOntologyModel(PelletReasonerFactory.THE_SPEC, null);
		ontModel.read(base + "basicFamilyReference.owl");
		ontModel.read(base + "basicFamilyRules.owl");

		final Resource nella = ontModel.createResource(luigiFamily.resolve("#Nella").toString());
		final Property hasUncle = ontModel.createProperty(luigiFamily.resolve("#hasUncle").toString());
		final Resource dino = ontModel.createResource(luigiFamily.resolve("#Dino").toString());
		assertFalse(ontModel.contains(nella, hasUncle, dino));

	}

	public void testLuigiFamilyOWLApi() throws Exception
	{

		final OWLOntologyManager manager = OWL.manager;
		final OWLOntology familyRef = manager.loadOntology(IRI.create(base + "basicFamilyReference.owl"));
		final OWLOntology familyRules = manager.loadOntology(IRI.create(base + "basicFamilyRules.owl"));

		final Set<OWLAxiom> axioms = new HashSet<>();
		axioms.addAll(familyRef.getAxioms());
		axioms.addAll(familyRules.getAxioms());

		final OWLOntology mergedOntology = OWL.Ontology(axioms);

		final PelletReasoner reasoner = com.clarkparsia.pellet.owlapi.PelletReasonerFactory.getInstance().createReasoner(mergedOntology);

		final OWLIndividual nella = OWL.Individual(luigiFamily.resolve("#Nella"));
		final OWLObjectProperty hasUncle = OWL.ObjectProperty(luigiFamily.resolve("#hasUncle"));
		final OWLIndividual dino = OWL.Individual(luigiFamily.resolve("#Dino"));

		assertFalse(reasoner.isEntailed(OWL.propertyAssertion(nella, hasUncle, dino)));

	}

	public void testUncleRule()
	{
		final ATermAppl hasParent = term("hasParent"), hasSibling = term("hasSibling"), hasUncle = term("hasUncle"), male = term("Male");

		final ATermAppl c11 = term("c11"), c12 = term("c12"), p1a = term("p1a"), p2a = term("p2a");

		_kb.addClass(male);
		_kb.addObjectProperty(hasParent);
		_kb.addObjectProperty(hasSibling);
		_kb.addObjectProperty(hasUncle);

		_kb.addIndividual(c11);
		_kb.addIndividual(c12);
		_kb.addIndividual(p1a);
		_kb.addIndividual(p2a);

		_kb.addPropertyValue(hasParent, c11, p1a);
		_kb.addPropertyValue(hasSibling, c11, c12);
		_kb.addPropertyValue(hasParent, c12, p1a);
		_kb.addPropertyValue(hasSibling, c12, c11);
		_kb.addPropertyValue(hasSibling, p1a, p2a);
		_kb.addType(p2a, male);

		final AtomIVariable x = new AtomIVariable("x"), y = new AtomIVariable("y"), z = new AtomIVariable("z");

		_kb.addRule(new Rule(Arrays.asList(new RuleAtom[] { new IndividualPropertyAtom(hasUncle, y, z) }), Arrays.asList(new RuleAtom[] { new IndividualPropertyAtom(hasParent, y, x), new ClassAtom(male, z), new IndividualPropertyAtom(hasSibling, x, z), })));

		assertIteratorValues(_kb.getObjectPropertyValues(hasUncle, c11).iterator(), new Object[] { p2a, });
	}

	public void testVariableUtils1()
	{
		final AtomIVariable var1 = new AtomIVariable("var1"), var2 = new AtomIVariable("var2");
		final RuleAtom atom = new SameIndividualAtom(var1, var2);
		assertIteratorValues(VariableUtils.getVars(atom).iterator(), new Object[] { var1, var2 });
	}

	public void testVariableUtils2()
	{
		final ATermAppl p = term("p");
		final AtomIVariable var1 = new AtomIVariable("var1"), var2 = new AtomIVariable("var2");
		final RuleAtom atom = new IndividualPropertyAtom(p, var1, var2);
		assertIteratorValues(VariableUtils.getVars(atom).iterator(), new Object[] { var1, var2 });
	}

	/**
	 * This test created to verify that facts added to RETE before a clash, but not affected by the restore remain in the rete. Known to be a problem for
	 * USE_CONTINUOUS_RULES=true at r711
	 */
	@Test
	public void reteRestoreTest1()
	{
		ATermAppl a, b, c, x, y, p, q, A, B, C, G, H;

		a = term("a");
		b = term("b");
		c = term("c");
		x = term("x");
		y = term("y");

		p = term("p");
		q = term("q");

		A = term("A");
		B = term("B");
		C = term("C");
		G = term("G");
		H = term("H");

		_kb.addIndividual(a);
		_kb.addIndividual(b);
		_kb.addIndividual(c);
		_kb.addIndividual(x);
		_kb.addIndividual(y);

		_kb.addObjectProperty(p);
		_kb.addObjectProperty(q);

		_kb.addClass(A);
		_kb.addClass(B);
		_kb.addClass(C);
		_kb.addClass(G);
		_kb.addClass(H);

		_kb.addType(a, A);
		_kb.addType(b, B);
		_kb.addType(c, C);
		_kb.addType(x, or(G, all(q, not(H))));
		_kb.addType(x, max(p, 2, TOP));
		_kb.addType(y, H);
		_kb.addPropertyValue(p, x, a);
		_kb.addPropertyValue(p, x, b);
		_kb.addPropertyValue(p, x, c);

		{
			final AtomIVariable v = new AtomIVariable("v");
			final RuleAtom body = new ClassAtom(some(p, or(and(A, B), or(and(A, C), and(B, C)))), v);
			final RuleAtom head = new IndividualPropertyAtom(q, v, new AtomIConstant(y));
			final Rule rule = new Rule(Collections.singleton(head), Collections.singleton(body));
			_kb.addRule(rule);
		}

		{
			final AtomIVariable v = new AtomIVariable("v");
			final RuleAtom body = new ClassAtom(G, v);
			final RuleAtom head = new IndividualPropertyAtom(p, v, new AtomIConstant(y));
			final Rule rule = new Rule(Collections.singleton(head), Collections.singleton(body));
			_kb.addRule(rule);
		}

		assertTrue(_kb.isConsistent());
		System.err.println("***************************");
		System.err.println(_kb.getPropertyValues(p, x));
		assertIteratorValues(_kb.getPropertyValues(p, x).iterator(), new ATermAppl[] { a, b, c, y });
		assertEquals(Collections.singletonList(y), _kb.getPropertyValues(q, x));
		assertFalse(_kb.hasPropertyValue(x, q, c));
	}

	@Test
	public void testQualifiedCardinality()
	{
		// This test case is to test the inferences regarding qualified
		// cardinality restrictions in the presence of rules (see ticket 105)

		// the point of this test is test if the choose-rule is working
		// properly in the presence of rules. the rule we use in this
		// test case is completely irrelevant for the test. we are
		// adding the rule just to force the selection of XXXRuleStrategy.
		// we are using a qualified min cardinality restriction where the
		// qualification itself is also a defined concept. this setup ensures
		// that we need the application of choose rule to get the correct
		// entailments

		final ATermAppl C = term("C");
		final ATermAppl D = term("D");
		final ATermAppl E = term("E");

		final ATermAppl a = term("a");
		final ATermAppl b = term("b");
		final ATermAppl c = term("c");

		final ATermAppl p = term("p");

		_kb.addClass(C);
		_kb.addClass(D);
		_kb.addClass(E);

		_kb.addIndividual(a);
		_kb.addIndividual(b);
		_kb.addIndividual(c);

		// _kb.addType( a, D );
		_kb.addType(b, C);
		_kb.addType(c, C);

		_kb.addEquivalentClass(D, min(p, 2, E));
		_kb.addEquivalentClass(E, some(p, C));

		_kb.addObjectProperty(p);

		_kb.addPropertyValue(p, a, b);
		_kb.addPropertyValue(p, a, c);
		_kb.addPropertyValue(p, b, b);
		_kb.addPropertyValue(p, c, c);

		_kb.addDifferent(b, c);

		{
			final AtomIVariable x = new AtomIVariable("x");
			final List<RuleAtom> body = new ArrayList<>();
			body.add(new ClassAtom(C, x));
			final List<RuleAtom> head = new ArrayList<>();
			head.add(new ClassAtom(C, x));
			final Rule rule = new Rule(head, body);
			_kb.addRule(rule);
		}

		assertTrue(_kb.isConsistent());

		assertTrue(_kb.isType(a, D));
		assertTrue(_kb.isType(b, E));
		assertTrue(_kb.isType(c, E));
	}

	@Test
	public void reteRestoreTest2()
	{

		// This test case is to test if restore/backtrack in RuleBranch
		// modifies the original ABox (see ticket 302)

		final ATermAppl C = term("C");
		final ATermAppl D = term("D");

		final ATermAppl a = term("a");
		final ATermAppl b = term("b");

		_kb.addClass(C);
		_kb.addClass(D);

		_kb.addIndividual(a);
		_kb.addIndividual(b);

		_kb.addType(b, C);

		{
			final AtomIVariable x = new AtomIVariable("x");
			final List<RuleAtom> body = new ArrayList<>();
			body.add(new ClassAtom(C, x));
			final List<RuleAtom> head = new ArrayList<>();
			head.add(new ClassAtom(D, x));
			final Rule rule = new Rule(head, body);
			_kb.addRule(rule);
		}

		assertTrue(_kb.isConsistent());

		// a is not inferred to be D since it is not C. as a result of
		// the initial consistency check not(C) is added to a
		assertFalse(_kb.isType(a, D));
		// asking a != b adds a = b to a copy of the ABox and checks for
		// consistency. adding a = b causes a clash since b is a D. as a
		// result of that clash D(x) should be added to the copy. the bug
		// described in #302 causes this t be added to the original KB
		assertFalse(_kb.isDifferentFrom(a, b));
		// when we ask this query again if D(a) is added to the original
		// KB we will incorrectly conclude the instance relation holds
		assertFalse(_kb.isType(a, D));

	}

	@Test
	public void testEmptyRuleHead1()
	{

		final ATermAppl i = term("i");
		final ATermAppl C = term("C");
		final AtomIVariable x = new AtomIVariable("x");

		_kb.addClass(C);
		_kb.addIndividual(i);
		_kb.addType(i, C);

		final List<RuleAtom> body = new ArrayList<>();
		final List<RuleAtom> head = new ArrayList<>();

		body.add(new ClassAtom(C, x));

		_kb.addRule(new Rule(head, body));

		assertFalse(_kb.isConsistent());
	}

	@Test
	public void testEmptyRuleHead2()
	{

		final ATermAppl i = term("i");
		final ATermAppl C = term("C");
		final ATermAppl D = term("D");
		final AtomIVariable x = new AtomIVariable("x");

		_kb.addClass(C);
		_kb.addClass(D);
		_kb.addSubClass(C, D);
		_kb.addIndividual(i);
		_kb.addType(i, C);

		final List<RuleAtom> body = new ArrayList<>();
		final List<RuleAtom> head = new ArrayList<>();

		body.add(new ClassAtom(C, x));

		_kb.addRule(new Rule(head, body));

		assertFalse(_kb.isConsistent());
	}

	@Test
	public void testEmptyRuleHead3()
	{

		final ATermAppl i = term("i");
		final ATermAppl C = term("C");
		final ATermAppl D = term("D");
		final AtomIVariable x = new AtomIVariable("x");

		_kb.addClass(C);
		_kb.addClass(D);
		_kb.addSubClass(C, D);
		_kb.addIndividual(i);
		_kb.addType(i, D);

		final List<RuleAtom> body = new ArrayList<>();
		final List<RuleAtom> head = new ArrayList<>();

		body.add(new ClassAtom(D, x));

		_kb.addRule(new Rule(head, body));

		assertFalse(_kb.isConsistent());
	}

	@Test
	public void testEmptyRuleHead4()
	{

		final ATermAppl i = term("i");
		final ATermAppl R = term("R");
		final ATermAppl l = literal("l");
		final AtomIVariable v = new AtomIVariable("v");
		final AtomDConstant c = new AtomDConstant(l);

		_kb.addIndividual(i);
		_kb.addDatatypeProperty(R);
		_kb.addPropertyValue(R, i, l);

		final List<RuleAtom> body = new ArrayList<>();
		final List<RuleAtom> head = new ArrayList<>();

		body.add(new DatavaluedPropertyAtom(R, v, c));

		_kb.addRule(new Rule(head, body));

		assertFalse(_kb.isConsistent());
	}

	@Test
	public void testEmptyRuleHead5()
	{

		final ATermAppl i = term("i");
		final ATermAppl C = term("C");
		final ATermAppl D = term("D");
		final ATermAppl CuD = or(C, D);
		final AtomIVariable x = new AtomIVariable("x");

		_kb.addClass(C);
		_kb.addClass(D);
		_kb.addClass(CuD);
		_kb.addIndividual(i);
		_kb.addType(i, C);

		final List<RuleAtom> body = new ArrayList<>();
		final List<RuleAtom> head = new ArrayList<>();

		body.add(new ClassAtom(CuD, x));

		_kb.addRule(new Rule(head, body));

		assertFalse(_kb.isConsistent());
	}

	@Test
	public void testTransitiveProperty()
	{

		final ATermAppl a = term("a");
		final ATermAppl b = term("b");
		final ATermAppl c = term("c");
		final ATermAppl p = term("p");
		final ATermAppl q = term("q");

		final AtomIVariable x = new AtomIVariable("x");
		final AtomIVariable y = new AtomIVariable("y");

		_kb.addObjectProperty(p);
		_kb.addObjectProperty(q);
		_kb.addTransitiveProperty(p);

		_kb.addIndividual(a);
		_kb.addIndividual(b);
		_kb.addIndividual(c);
		_kb.addPropertyValue(p, a, b);
		_kb.addPropertyValue(p, b, c);

		final List<RuleAtom> body = new ArrayList<>();
		final List<RuleAtom> head = new ArrayList<>();

		body.add(new IndividualPropertyAtom(p, x, y));
		head.add(new IndividualPropertyAtom(q, x, y));

		_kb.addRule(new Rule(head, body));

		assertTrue(_kb.hasPropertyValue(a, p, b));
		assertTrue(_kb.hasPropertyValue(a, p, c));
		assertTrue(_kb.hasPropertyValue(b, p, c));

		assertTrue(_kb.hasPropertyValue(a, q, b));
		assertTrue(_kb.hasPropertyValue(a, q, c));
		assertTrue(_kb.hasPropertyValue(b, q, c));

		Map<ATermAppl, List<ATermAppl>> results = _kb.getPropertyValues(p);
		assertIteratorValues(results.get(b).iterator(), new ATermAppl[] { c });
		assertIteratorValues(results.get(a).iterator(), new ATermAppl[] { b, c });

		results = _kb.getPropertyValues(q);
		assertIteratorValues(results.get(b).iterator(), new ATermAppl[] { c });
		assertIteratorValues(results.get(a).iterator(), new ATermAppl[] { b, c });
	}

	@Test
	public void testUnsafeVariable()
	{

		// This test case is to test if restore/backtrack in RuleBranch
		// modifies the original ABox (see ticket 302)

		final ATermAppl C = term("C");
		final ATermAppl p = term("p");

		final ATermAppl a = term("a");
		final ATermAppl b = term("b");

		_kb.addClass(C);
		_kb.addObjectProperty(p);

		_kb.addIndividual(a);
		_kb.addIndividual(b);

		_kb.addEquivalentClass(C, some(p, TOP));

		final AtomIVariable x = new AtomIVariable("x");
		final AtomIVariable y = new AtomIVariable("y");
		final List<RuleAtom> body = new ArrayList<>();
		body.add(new ClassAtom(C, x));
		final List<RuleAtom> head = new ArrayList<>();
		head.add(new IndividualPropertyAtom(p, x, y));
		final Rule rule = new Rule(head, body);
		_kb.addRule(rule);

		assertEquals(singleton(rule), _kb.getRules());
		assertNull(_kb.getNormalizedRules().get(rule));

		assertTrue(_kb.isConsistent());
		assertFalse(_kb.isType(a, C));
	}

	@Test
	public void reflexiveRule()
	{

		final ATermAppl A = term("A");

		final ATermAppl p = term("p");

		final ATermAppl a = term("a");
		final ATermAppl b = term("b");
		final ATermAppl c = term("c");

		_kb.addClass(A);

		_kb.addObjectProperty(p);

		_kb.addIndividual(a);
		_kb.addIndividual(b);
		_kb.addIndividual(c);

		_kb.addPropertyValue(p, a, a);
		_kb.addPropertyValue(p, b, a);
		_kb.addPropertyValue(p, b, c);

		final AtomIVariable x = new AtomIVariable("x");
		final List<RuleAtom> body = Arrays.<RuleAtom> asList(new IndividualPropertyAtom(p, x, x));
		final List<RuleAtom> head = Arrays.<RuleAtom> asList(new ClassAtom(A, x));

		_kb.addRule(new Rule(head, body));

		assertTrue(_kb.isConsistent());

		assertTrue(_kb.isType(a, A));
		assertFalse(_kb.isType(b, A));
		assertFalse(_kb.isType(c, A));
	}

	@Test
	public void propertyAtomWithAConstant()
	{

		final ATermAppl A = term("A");

		final ATermAppl p = term("p");

		final ATermAppl a = term("a");
		final ATermAppl b = term("b");
		final ATermAppl c = term("c");

		_kb.addClass(A);

		_kb.addObjectProperty(p);

		_kb.addIndividual(a);
		_kb.addIndividual(b);
		_kb.addIndividual(c);

		_kb.addPropertyValue(p, a, b);
		_kb.addPropertyValue(p, b, c);
		_kb.addPropertyValue(p, c, c);

		final AtomIVariable x = new AtomIVariable("x");
		final List<RuleAtom> body = Arrays.<RuleAtom> asList(new IndividualPropertyAtom(p, x, new AtomIConstant(c)));
		final List<RuleAtom> head = Arrays.<RuleAtom> asList(new ClassAtom(A, x));

		_kb.addRule(new Rule(head, body));

		assertTrue(_kb.isConsistent());

		assertFalse(_kb.isType(a, A));
		assertTrue(_kb.isType(b, A));
		assertTrue(_kb.isType(c, A));
	}

	@Test
	public void complexConsequent()
	{
		final Set<SWRLAtom> bodyAtoms = new HashSet<>();
		final Set<SWRLAtom> headAtoms = new HashSet<>();

		final OWLIndividual individualA = OWL.Individual("a");
		final OWLClassExpression classC = OWL.Class("C");
		final OWLClassExpression classD = OWL.Class("D");
		final OWLClassExpression classE = OWL.Class("E");
		final OWLObjectProperty propertyP = OWL.ObjectProperty("p");
		final SWRLVariable variable = SWRL.variable("x");

		final OWLObjectSomeValuesFrom restriction = OWL.some(propertyP, classD);

		// C(?x)
		bodyAtoms.add(SWRL.classAtom(classC, variable));
		// Ep.D(?x)
		headAtoms.add(SWRL.classAtom(restriction, variable));

		final OWLAxiom[] axioms = new OWLAxiom[] {
				// C(?x) -> Ep.D(?x)
				SWRL.rule(bodyAtoms, headAtoms),
				// E = Ep.D
				OWL.equivalentClasses(classE, restriction),
				// C(a)
				OWL.classAssertion(individualA, classC),
				// E(a)
				OWL.classAssertion(individualA, classE) };

		final OWLOntology ontology = OWL.Ontology(axioms);

		final PelletReasoner reasoner = com.clarkparsia.pellet.owlapi.PelletReasonerFactory.getInstance().createNonBufferingReasoner(ontology);

		assertTrue(reasoner.isConsistent());
		assertTrue(reasoner.isEntailed(OWL.classAssertion(individualA, classE)));

		OWL.manager.removeOntology(ontology);
	}

	@Test
	public void testDifferentFromInBody() throws Exception
	{
		final OntModel ontModel = ModelFactory.createOntologyModel(PelletReasonerFactory.THE_SPEC, null);
		ontModel.read(base + "sibling-rule.n3", "TTL");

		final Resource alice = ontModel.createResource("family:alice");
		final Property sibling = ontModel.createProperty("family:sibling");
		final Resource bob = ontModel.createResource("family:bob");
		assertTrue(ontModel.contains(alice, sibling, bob));
		assertTrue(ontModel.contains(bob, sibling, alice));

		assertEquals(Collections.singletonList(bob), ontModel.listObjectsOfProperty(alice, sibling).toList());
		assertEquals(Collections.singletonList(alice), ontModel.listObjectsOfProperty(bob, sibling).toList());
	}

	@Test
	public void testRepeatedVars() throws Exception
	{
		final StringDocumentSource source = new StringDocumentSource("Prefix(owl:=<http://www.w3.org/2002/07/owl#>)\n" + "Prefix(rdf:=<http://www.w3.org/1999/02/22-rdf-syntax-ns#>)\n" + "Prefix(xml:=<http://www.w3.org/XML/1998/namespace>)\n" + "Prefix(xsd:=<http://www.w3.org/2001/XMLSchema#>)\n" + "Prefix(rdfs:=<http://www.w3.org/2000/01/rdf-schema#>)\n" + "\n" + "Ontology(<http://www.semanticweb.org/ontologies/2014/3/untitled-ontology-215>\n" + "Import(<http://www.w3.org/2006/time>)\n" + "\n" + "EquivalentClasses(<http://www.w3.org/2006/time#Instant> ObjectHasSelf(<http://www.semanticweb.org/ontologies/2014/3/untitled-ontology-215#R_Instant>))\n" + "Declaration(ObjectProperty(<http://www.semanticweb.org/ontologies/2014/3/untitled-ontology-215#R_Instant>))\n" + "Declaration(NamedIndividual(<http://www.semanticweb.org/ontologies/2014/3/untitled-ontology-215#Instant1>))\n" + "ClassAssertion(<http://www.w3.org/2006/time#Instant> <http://www.semanticweb.org/ontologies/2014/3/untitled-ontology-215#Instant1>)\n" + "DataPropertyAssertion(<http://www.w3.org/2006/time#inXSDDateTime> <http://www.semanticweb.org/ontologies/2014/3/untitled-ontology-215#Instant1> \"2000-01-01T00:00:00\"^^xsd:dateTime)\n" + "Declaration(NamedIndividual(<http://www.semanticweb.org/ontologies/2014/3/untitled-ontology-215#Instant2>))\n" + "ClassAssertion(<http://www.w3.org/2006/time#Instant> <http://www.semanticweb.org/ontologies/2014/3/untitled-ontology-215#Instant2>)\n" + "DataPropertyAssertion(<http://www.w3.org/2006/time#inXSDDateTime> <http://www.semanticweb.org/ontologies/2014/3/untitled-ontology-215#Instant2> \"2003-01-01T00:00:00\"^^xsd:dateTime)\n" + "DLSafeRule(Body(ObjectPropertyAtom(<http://www.semanticweb.org/ontologies/2014/3/untitled-ontology-215#R_Instant> Variable(<urn:swrl#x>) Variable(<urn:swrl#x>)) " + "ObjectPropertyAtom(<http://www.semanticweb.org/ontologies/2014/3/untitled-ontology-215#R_Instant> Variable(<urn:swrl#z>) Variable(<urn:swrl#z>)) " + "DataPropertyAtom(<http://www.w3.org/2006/time#inXSDDateTime> Variable(<urn:swrl#x>) Variable(<urn:swrl#y>)) " + "DataPropertyAtom(<http://www.w3.org/2006/time#inXSDDateTime> Variable(<urn:swrl#z>) Variable(<urn:swrl#w>)) " + "BuiltInAtom(<http://www.w3.org/2003/11/swrlb#lessThan> Variable(<urn:swrl#y>) Variable(<urn:swrl#w>)))" + "Head(ObjectPropertyAtom(<http://www.w3.org/2006/time#before> Variable(<urn:swrl#x>) Variable(<urn:swrl#z>))))\n" + ")");

		final OWLOntology ont = OWL.manager.loadOntologyFromOntologyDocument(source);
		final PelletReasoner reasoner = com.clarkparsia.pellet.owlapi.PelletReasonerFactory.getInstance().createReasoner(ont);
		reasoner.getKB().realize();
	}

	@Test
	public void testNoVarPropertyAtom()
	{
		classes(_A);
		dataProperties(_p, _q);
		individuals(_a, _b);

		_kb.addType(_a, _A);
		_kb.addPropertyValue(_p, _b, TermFactory.literal(true));

		final ATermAppl t = TermFactory.literal("t");
		final ATermAppl f = TermFactory.literal("f");

		final AtomIVariable x = new AtomIVariable("x");

		List<RuleAtom> body = Arrays.<RuleAtom> asList(new ClassAtom(_A, x), new DatavaluedPropertyAtom(_p, new AtomIConstant(_b), new AtomDConstant(TermFactory.literal(true))));
		List<RuleAtom> head = Arrays.<RuleAtom> asList(new DatavaluedPropertyAtom(_q, x, new AtomDConstant(t)));
		_kb.addRule(new Rule(head, body));

		body = Arrays.<RuleAtom> asList(new ClassAtom(_A, x), new DatavaluedPropertyAtom(_p, new AtomIConstant(_b), new AtomDConstant(TermFactory.literal(false))));
		head = Arrays.<RuleAtom> asList(new DatavaluedPropertyAtom(_q, x, new AtomDConstant(f)));
		_kb.addRule(new Rule(head, body));

		assertIteratorValues(_kb.getDataPropertyValues(_q, _a).iterator(), t);
		assertIteratorValues(_kb.getDataPropertyValues(_q, _b).iterator());
	}

	@Test
	public void testNoVarTypeAtom()
	{
		classes(_A, _B, _C);
		dataProperties(_q);
		individuals(_a, _b);

		_kb.addType(_a, _A);
		_kb.addType(_b, _B);

		final ATermAppl t = TermFactory.literal("t");
		final ATermAppl f = TermFactory.literal("f");

		final AtomIVariable x = new AtomIVariable("x");

		List<RuleAtom> body = Arrays.<RuleAtom> asList(new ClassAtom(_A, x), new ClassAtom(_B, new AtomIConstant(_b)));
		List<RuleAtom> head = Arrays.<RuleAtom> asList(new DatavaluedPropertyAtom(_q, x, new AtomDConstant(t)));
		_kb.addRule(new Rule(head, body));

		body = Arrays.<RuleAtom> asList(new ClassAtom(_A, x), new ClassAtom(_A, new AtomIConstant(_b)));
		head = Arrays.<RuleAtom> asList(new DatavaluedPropertyAtom(_q, x, new AtomDConstant(f)));
		_kb.addRule(new Rule(head, body));

		body = Arrays.<RuleAtom> asList(new ClassAtom(_B, x));
		head = Arrays.<RuleAtom> asList(new ClassAtom(_C, x));
		_kb.addRule(new Rule(head, body));

		assertIteratorValues(_kb.getDataPropertyValues(_q, _a).iterator(), t);
		assertIteratorValues(_kb.getDataPropertyValues(_q, _b).iterator());
		assertIteratorValues(_kb.getInstances(_C).iterator(), _b);
	}

	@Test
	public void testNoSharedVar()
	{
		classes(_A);
		dataProperties(_p, _q);
		objectProperties(_r);
		individuals(_a, _b, _c);

		_kb.addType(_a, _A);
		_kb.addPropertyValue(_p, _b, TermFactory.literal(true));
		_kb.addPropertyValue(_r, _b, _c);

		final ATermAppl t = TermFactory.literal("t");
		final ATermAppl f = TermFactory.literal("f");

		final AtomIVariable x = new AtomIVariable("x");
		final AtomDVariable y = new AtomDVariable("y");

		List<RuleAtom> body = Arrays.<RuleAtom> asList(new ClassAtom(_A, x), new DatavaluedPropertyAtom(_p, new AtomIConstant(_b), y), new BuiltInAtom(SWRLB + "equal", y, new AtomDConstant(TermFactory.literal(true))));
		List<RuleAtom> head = Arrays.<RuleAtom> asList(new DatavaluedPropertyAtom(_q, x, new AtomDConstant(t)));
		_kb.addRule(new Rule(head, body));

		body = Arrays.<RuleAtom> asList(new ClassAtom(_A, x), new DatavaluedPropertyAtom(_p, new AtomIConstant(_b), y), new BuiltInAtom(SWRLB + "equal", y, new AtomDConstant(TermFactory.literal(false))));
		head = Arrays.<RuleAtom> asList(new DatavaluedPropertyAtom(_q, x, new AtomDConstant(f)));
		_kb.addRule(new Rule(head, body));

		assertIteratorValues(_kb.getDataPropertyValues(_q, _a).iterator(), t);
		assertIteratorValues(_kb.getDataPropertyValues(_q, _b).iterator());
	}

	@Test
	public void testNoSharedVarFixedObject()
	{
		classes(_A);
		dataProperties(_p, _q);
		objectProperties(_r, _s);
		individuals(_a, _b, _c, _d);

		_kb.addType(_a, _A);
		_kb.addPropertyValue(_r, _c, _b);
		_kb.addPropertyValue(_p, _c, TermFactory.literal(true));
		_kb.addPropertyValue(_s, _d, _b);
		_kb.addPropertyValue(_p, _d, TermFactory.literal(false));

		final ATermAppl t = TermFactory.literal("t");
		final ATermAppl f = TermFactory.literal("f");

		final AtomIVariable x = new AtomIVariable("x");
		final AtomIVariable y = new AtomIVariable("y");

		List<RuleAtom> body = Arrays.<RuleAtom> asList(new ClassAtom(_A, x), new IndividualPropertyAtom(_r, y, new AtomIConstant(_b)), new DatavaluedPropertyAtom(_p, y, new AtomDConstant(TermFactory.literal(true))));
		List<RuleAtom> head = Arrays.<RuleAtom> asList(new DatavaluedPropertyAtom(_q, x, new AtomDConstant(t)));
		_kb.addRule(new Rule(head, body));

		body = Arrays.<RuleAtom> asList(new ClassAtom(_A, x), new IndividualPropertyAtom(_r, y, new AtomIConstant(_b)), new DatavaluedPropertyAtom(_p, y, new AtomDConstant(TermFactory.literal(false))));
		head = Arrays.<RuleAtom> asList(new DatavaluedPropertyAtom(_q, x, new AtomDConstant(f)));
		_kb.addRule(new Rule(head, body));

		assertIteratorValues(_kb.getDataPropertyValues(_q, _a).iterator(), t);
		assertIteratorValues(_kb.getDataPropertyValues(_q, _b).iterator());
	}

	@Test
	public void testBindingBuiltins()
	{
		classes(_A);
		dataProperties(_p, _q);
		individuals(_a, _b, _c, _d);

		_kb.addPropertyValue(_p, _a, TermFactory.literal(1));
		_kb.addPropertyValue(_p, _b, TermFactory.literal(5));
		_kb.addPropertyValue(_p, _c, TermFactory.literal(10));
		_kb.addPropertyValue(_p, _d, TermFactory.literal(15));

		final AtomIVariable x = new AtomIVariable("x");
		final AtomDVariable y = new AtomDVariable("y");
		final AtomDVariable z = new AtomDVariable("z");

		final List<RuleAtom> body = Arrays.<RuleAtom> asList(new DatavaluedPropertyAtom(_p, x, y), new BuiltInAtom(SWRLB + "pow", z, y, new AtomDConstant(TermFactory.literal(2))), new BuiltInAtom(SWRLB + "lessThan", z, new AtomDConstant(TermFactory.literal(100))));
		final List<RuleAtom> head = Arrays.<RuleAtom> asList(new ClassAtom(_A, x));
		_kb.addRule(new Rule(head, body));

		assertIteratorValues(_kb.getInstances(_A).iterator(), _a, _b);
	}

	@Test
	public void testTriangle()
	{
		objectProperties(_p, _q);
		individuals(_a, _b, _c, _d, _e);

		_kb.addPropertyValue(_p, _a, _b);
		_kb.addPropertyValue(_p, _b, _c);
		_kb.addPropertyValue(_p, _a, _c);
		_kb.addPropertyValue(_p, _b, _e);
		_kb.addPropertyValue(_p, _c, _e);

		final AtomIVariable x = new AtomIVariable("x");
		final AtomIVariable y = new AtomIVariable("y");
		final AtomIVariable z = new AtomIVariable("z");

		final List<RuleAtom> body = Arrays.<RuleAtom> asList(new IndividualPropertyAtom(_p, x, y), new IndividualPropertyAtom(_p, y, z), new IndividualPropertyAtom(_p, x, z));
		final List<RuleAtom> head = Arrays.<RuleAtom> asList(new IndividualPropertyAtom(_q, x, z));
		_kb.addRule(new Rule(head, body));

		assertIteratorValues(_kb.getObjectPropertyValues(_q, _a).iterator(), _c);
		assertIteratorValues(_kb.getObjectPropertyValues(_q, _b).iterator(), _e);
		assertIteratorValues(_kb.getObjectPropertyValues(_q, _c).iterator());
		assertIteratorValues(_kb.getObjectPropertyValues(_q, _d).iterator());
	}
}
