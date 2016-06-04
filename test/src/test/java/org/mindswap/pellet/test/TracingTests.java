// Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public
// License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of
// proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package org.mindswap.pellet.test;

import static com.clarkparsia.pellet.utils.TermFactory.TOP;
import static com.clarkparsia.pellet.utils.TermFactory.all;
import static com.clarkparsia.pellet.utils.TermFactory.and;
import static com.clarkparsia.pellet.utils.TermFactory.list;
import static com.clarkparsia.pellet.utils.TermFactory.literal;
import static com.clarkparsia.pellet.utils.TermFactory.max;
import static com.clarkparsia.pellet.utils.TermFactory.minInclusive;
import static com.clarkparsia.pellet.utils.TermFactory.not;
import static com.clarkparsia.pellet.utils.TermFactory.oneOf;
import static com.clarkparsia.pellet.utils.TermFactory.or;
import static com.clarkparsia.pellet.utils.TermFactory.restrict;
import static com.clarkparsia.pellet.utils.TermFactory.some;
import static com.clarkparsia.pellet.utils.TermFactory.term;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mindswap.pellet.test.PelletTestCase.assertIteratorValues;
import static org.mindswap.pellet.utils.ATermUtils.makeList;

import aterm.ATerm;
import aterm.ATermAppl;
import aterm.ATermList;
import com.clarkparsia.pellet.datatypes.Datatypes;
import com.clarkparsia.pellet.datatypes.types.real.XSDInteger;
import com.clarkparsia.pellet.rules.RulesToATermTranslator;
import com.clarkparsia.pellet.rules.model.AtomIVariable;
import com.clarkparsia.pellet.rules.model.ClassAtom;
import com.clarkparsia.pellet.rules.model.IndividualPropertyAtom;
import com.clarkparsia.pellet.rules.model.Rule;
import com.clarkparsia.pellet.rules.model.RuleAtom;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import junit.framework.JUnit4TestAdapter;
import org.apache.jena.vocabulary.XSD;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mindswap.pellet.KnowledgeBase;
import org.mindswap.pellet.PelletOptions;
import org.mindswap.pellet.utils.ATermUtils;
import org.mindswap.pellet.utils.SetUtils;

public class TracingTests extends AbstractKBTests
{

	public static junit.framework.Test suite()
	{
		return new JUnit4TestAdapter(TracingTests.class);
	}

	private final ATermAppl bob = ATermUtils.makeTermAppl("Bob"), robert = ATermUtils.makeTermAppl("Robert"), mary = ATermUtils.makeTermAppl("Mary"), victor = ATermUtils.makeTermAppl("Victor"), email = ATermUtils.makeTermAppl("MaryAndBob@example.com"), mbox = ATermUtils.makeTermAppl("mbox"), relative = ATermUtils.makeTermAppl("relative"), sibling = ATermUtils.makeTermAppl("sibling"), person = ATermUtils.makeTermAppl("person"), human = ATermUtils.makeTermAppl("human"), ssn = ATermUtils.makeTermAppl("ssn");

	private boolean old_USE_TRACING;

	@Override
	@Before
	public void initializeKB()
	{
		super.initializeKB();

		old_USE_TRACING = PelletOptions.USE_TRACING;
		PelletOptions.USE_TRACING = true;
		_kb.setDoExplanation(true);
	}

	@Override
	@After
	public void disposeKB()
	{
		super.disposeKB();

		PelletOptions.USE_TRACING = old_USE_TRACING;
	}

	public void explainInconsistency(final ATermAppl... expected)
	{
		assertFalse(_kb.isConsistent());

		final Set<ATermAppl> actual = _kb.getExplanationSet();

		assertEquals(SetUtils.create(expected), actual);
	}

	public void explainEntailment(final boolean entailment, final ATermAppl... expected)
	{
		assertTrue(entailment);

		final Set<ATermAppl> actual = _kb.getExplanationSet();

		assertEquals(SetUtils.create(expected), actual);
	}

	@Test
	public void testAsymmetric()
	{
		objectProperties(_p);
		individuals(_a, _b);

		_kb.addAsymmetricProperty(_p);

		_kb.addPropertyValue(_p, _a, _b);
		_kb.addPropertyValue(_p, _b, _a);

		explainInconsistency(ATermUtils.makeAsymmetric(_p), ATermUtils.makePropAtom(_p, _a, _b), ATermUtils.makePropAtom(_p, _b, _a));
	}

	@Test
	public void testBottomSatisfiable()
	{
		_kb.addClass(human);

		explainEntailment(!_kb.isSatisfiable(ATermUtils.makeAnd(ATermUtils.makeNot(human), ATermUtils.BOTTOM)));
	}

	@Test
	public void testCourse()
	{
		final ATermAppl Course = term("Person");
		final ATermAppl Person = term("Course");
		final ATermAppl Man = term("Man");
		final ATermAppl Woman = term("Woman");

		final ATermAppl isTaughtBy = term("isTaughtBy");

		final ATermAppl M1 = term("M1");
		final ATermAppl W1 = term("W1");
		final ATermAppl C1 = term("C1");
		final ATermAppl P1 = term("P1");
		final ATermAppl C2 = term("C2");

		_kb.addClass(Course);
		_kb.addClass(Person);
		_kb.addClass(Man);
		_kb.addClass(Woman);
		_kb.addDisjointClass(Man, Woman);

		_kb.addObjectProperty(isTaughtBy);
		_kb.addFunctionalProperty(isTaughtBy);

		_kb.addIndividual(C1);
		_kb.addIndividual(P1);
		_kb.addIndividual(M1);
		_kb.addIndividual(M1);
		_kb.addIndividual(W1);
		_kb.addIndividual(C2);

		_kb.addType(C1, Course);
		_kb.addPropertyValue(isTaughtBy, C1, M1);
		_kb.addPropertyValue(isTaughtBy, C1, P1);

		_kb.addType(C2, Course);
		_kb.addPropertyValue(isTaughtBy, C2, W1);
		_kb.addPropertyValue(isTaughtBy, C2, P1);

		_kb.addType(M1, Man);
		_kb.addType(W1, Woman);
		_kb.addType(P1, Person);

		explainInconsistency(ATermUtils.makeTypeAtom(M1, Man), ATermUtils.makePropAtom(isTaughtBy, C1, M1), ATermUtils.makePropAtom(isTaughtBy, C1, P1), ATermUtils.makeTypeAtom(W1, Woman), ATermUtils.makePropAtom(isTaughtBy, C2, W1), ATermUtils.makePropAtom(isTaughtBy, C2, P1), ATermUtils.makeFunctional(isTaughtBy), ATermUtils.makeDisjoint(Man, Woman));
	}

	/**
	 * Test explanations for bad datatypes. Not implemented, known to fail.
	 */
	@Test
	public void testDatatypeStatement()
	{
		_kb.addDatatypeProperty(ssn);
		_kb.addIndividual(robert);

		final ATermAppl ssn1 = ATermUtils.makeTypedLiteral("bob", XSD.nonNegativeInteger.toString());
		_kb.addPropertyValue(ssn, robert, ssn1);
		assertFalse(_kb.isConsistent());

		final Set<ATermAppl> explanation = _kb.getExplanationSet();

		assertTrue(explanation.contains(ATermUtils.makePropAtom(ssn, robert, ssn1)));
	}

	@Test
	public void testDisjunction()
	{
		classes(_A, _B);
		objectProperties(_p);
		individuals(_a, _b);

		_kb.addType(_a, _A);
		_kb.addPropertyValue(_p, _a, _b);
		_kb.addType(_a, or(not(_A), all(_p, _B)));

		explainEntailment(_kb.isType(_b, _B), ATermUtils.makeTypeAtom(_a, _A), ATermUtils.makePropAtom(_p, _a, _b), ATermUtils.makeTypeAtom(_a, or(not(_A), all(_p, _B))));
	}

	@Test
	public void testDomain()
	{
		classes(_A, _B);
		objectProperties(_p);
		individuals(_a, _b);

		_kb.addDomain(_p, _A);
		_kb.addType(_a, not(_A));
		_kb.addPropertyValue(_p, _a, _b);

		explainInconsistency(ATermUtils.makeDomain(_p, _A), ATermUtils.makeTypeAtom(_a, not(_A)), ATermUtils.makePropAtom(_p, _a, _b));
	}

	@Test
	public void testDomainRangeInverse()
	{
		classes(_A);
		objectProperties(_p);
		individuals(_a, _b);

		_kb.addDomain(_p, _A);
		_kb.addRange(_p, not(_A));
		_kb.addInverseProperty(_p, _p);
		_kb.addPropertyValue(_p, _a, _b);

		explainInconsistency(ATermUtils.makeDomain(_p, _A), ATermUtils.makeRange(_p, not(_A)), ATermUtils.makeInvProp(_p, _p), ATermUtils.makePropAtom(_p, _a, _b));
	}

	@Test
	public void testDomainRangeSymmetric()
	{
		classes(_A);
		objectProperties(_p);
		individuals(_a, _b);

		_kb.addDomain(_p, _A);
		_kb.addRange(_p, not(_A));
		_kb.addSymmetricProperty(_p);
		_kb.addPropertyValue(_p, _a, _b);

		explainInconsistency(ATermUtils.makeDomain(_p, _A), ATermUtils.makeRange(_p, not(_A)), ATermUtils.makeSymmetric(_p), ATermUtils.makePropAtom(_p, _a, _b));
	}

	@Test
	public void testEquivalentClass()
	{
		classes(_A, _B);

		_kb.addSubClass(_A, _B);
		_kb.addSubClass(_B, _A);

		explainEntailment(_kb.isEquivalentClass(_A, _B), ATermUtils.makeSub(_A, _B), ATermUtils.makeSub(_B, _A));
	}

	@Test
	public void testFunctionalDataProp2()
	{
		_kb.addDatatypeProperty(ssn);
		_kb.addFunctionalProperty(ssn);
		_kb.addIndividual(robert);

		final ATermAppl ssn1 = ATermUtils.makePlainLiteral("012345678");
		final ATermAppl ssn2 = ATermUtils.makePlainLiteral("123456789");

		_kb.addPropertyValue(ssn, robert, ssn1);
		_kb.addPropertyValue(ssn, robert, ssn2);

		assertFalse(_kb.isConsistent());

		final Set<ATermAppl> explanation = _kb.getExplanationSet();

		assertIteratorValues(explanation.iterator(), new Object[] { ATermUtils.makePropAtom(ssn, robert, ssn1), ATermUtils.makePropAtom(ssn, robert, ssn2), ATermUtils.makeFunctional(ssn), });
		assertTrue(explanation.size() == 3);
	}

	@Test
	public void testFunctionalDataProp1()
	{
		final ATermAppl C = term("C");
		final ATermAppl D = XSDInteger.getInstance().getName();
		final ATermAppl p = term("p");
		final ATermAppl a = term("a");
		final ATermAppl b = literal("012345678", Datatypes.INTEGER);

		_kb.addClass(C);
		_kb.addClass(D);
		_kb.addDatatypeProperty(p);
		_kb.addIndividual(a);

		_kb.addEquivalentClass(C, all(p, D));

		_kb.addFunctionalProperty(p);

		_kb.addPropertyValue(p, a, b);

		assertTrue(_kb.isConsistent());

		assertTrue(_kb.isType(a, C));

		final Set<ATermAppl> explanation = _kb.getExplanationSet();

		assertIteratorValues(explanation.iterator(), new Object[] { ATermUtils.makeEqClasses(C, all(p, D)), ATermUtils.makeFunctional(p), ATermUtils.makePropAtom(p, a, b) });
	}

	@Test
	public void testFunctionalObjectProp1()
	{
		final ATermAppl C = term("C");
		final ATermAppl D = term("D");
		final ATermAppl p = term("p");
		final ATermAppl a = term("a");
		final ATermAppl b = term("b");

		_kb.addClass(C);
		_kb.addClass(D);
		_kb.addObjectProperty(p);
		_kb.addIndividual(a);
		_kb.addIndividual(b);

		_kb.addEquivalentClass(C, all(p, D));

		_kb.addFunctionalProperty(p);

		_kb.addPropertyValue(p, a, b);
		_kb.addType(b, D);

		assertTrue(_kb.isConsistent());

		assertTrue(_kb.isType(a, C));

		final Set<ATermAppl> explanation = _kb.getExplanationSet();

		assertIteratorValues(explanation.iterator(), new Object[] { ATermUtils.makeEqClasses(C, all(p, D)), ATermUtils.makeFunctional(p), ATermUtils.makePropAtom(p, a, b), ATermUtils.makeTypeAtom(b, D) });
	}

	@Test
	public void testInverseFunctionalDataProp()
	{
		final ATermList different = ATermUtils.makeList(robert).insert(mary);
		System.out.println("Different: " + different);
		_kb.addObjectProperty(mbox);
		_kb.addInverseFunctionalProperty(mbox);
		_kb.addIndividual(robert);
		_kb.addIndividual(mary);
		_kb.addIndividual(email);
		_kb.addAllDifferent(different);

		_kb.addPropertyValue(mbox, robert, email);
		_kb.addPropertyValue(mbox, mary, email);

		assertFalse(_kb.isConsistent());

		final Set<ATermAppl> explanation = _kb.getExplanationSet();
		// System.out.println(explanation);
		assertIteratorValues(explanation.iterator(), new Object[] { ATermUtils.makePropAtom(mbox, robert, email), ATermUtils.makePropAtom(mbox, mary, email), ATermUtils.makeInverseFunctional(mbox), ATermUtils.makeAllDifferent(different), });
	}

	@Test
	public void testIrreflexive()
	{
		_kb.addObjectProperty(mbox);
		_kb.addIrreflexiveProperty(mbox);
		_kb.addIndividual(robert);
		_kb.addPropertyValue(mbox, robert, robert);

		assertFalse(_kb.isConsistent());

		final Set<ATermAppl> explanation = _kb.getExplanationSet();
		assertIteratorValues(explanation.iterator(), new Object[] { ATermUtils.makeIrreflexive(mbox), ATermUtils.makePropAtom(mbox, robert, robert), });
	}

	@Test
	public void testMaxOneDataProp()
	{
		_kb.addClass(person);
		_kb.addDatatypeProperty(ssn);
		final ATermAppl max1ssn = ATermUtils.makeMax(ssn, 1, ATermUtils.TOP_LIT);
		_kb.addSubClass(person, max1ssn);
		_kb.addSubClass(person, ATermUtils.makeMin(ssn, 1, ATermUtils.TOP_LIT));
		_kb.addIndividual(robert);
		_kb.addType(robert, person);

		final ATermAppl ssn1 = ATermUtils.makePlainLiteral("012345678");
		final ATermAppl ssn2 = ATermUtils.makePlainLiteral("123456789");

		_kb.addPropertyValue(ssn, robert, ssn1);
		_kb.addPropertyValue(ssn, robert, ssn2);

		assertFalse(_kb.isConsistent());

		final Set<ATermAppl> explanation = _kb.getExplanationSet();

		assertIteratorValues(explanation.iterator(), new Object[] { ATermUtils.makePropAtom(ssn, robert, ssn1), ATermUtils.makePropAtom(ssn, robert, ssn2), ATermUtils.makeSub(person, max1ssn), ATermUtils.makeTypeAtom(robert, person), });
	}

	@Test
	public void testRange()
	{
		final ATermAppl notPerson = ATermUtils.makeNot(person);

		_kb.addClass(person);
		_kb.addObjectProperty(sibling);
		_kb.addRange(sibling, person);
		_kb.addIndividual(robert);
		_kb.addIndividual(victor);
		_kb.addType(victor, notPerson);
		_kb.addPropertyValue(sibling, robert, victor);

		assertFalse(_kb.isConsistent());

		final Set<ATermAppl> explanation = _kb.getExplanationSet();

		assertIteratorValues(explanation.iterator(), new Object[] { ATermUtils.makeRange(sibling, person), ATermUtils.makeTypeAtom(victor, notPerson), ATermUtils.makePropAtom(sibling, robert, victor), });
	}

	@Test
	public void testReflexive()
	{
		final ATermAppl notPerson = ATermUtils.makeNot(person);
		final ATermAppl bobsType = ATermUtils.makeAllValues(relative, notPerson);
		_kb.addClass(person);
		_kb.addObjectProperty(relative);
		_kb.addReflexiveProperty(relative);
		_kb.addIndividual(robert);
		_kb.addType(robert, person);
		_kb.addType(robert, bobsType);
		_kb.addIndividual(victor);
		_kb.addType(victor, notPerson);

		// _kb.addPropertyValue(relative, robert, victor);

		assertFalse(_kb.isConsistent());
		final Set<ATermAppl> explanation = _kb.getExplanationSet();
		assertIteratorValues(explanation.iterator(), new Object[] { ATermUtils.makeReflexive(relative), ATermUtils.makeTypeAtom(robert, person), ATermUtils.makeTypeAtom(robert, bobsType),
			// ATermUtils.makePropAtom(relative, robert, victor),
		});

	}

	@Test
	public void testSameAllDifferent()
	{
		_kb.addIndividual(robert);
		_kb.addIndividual(bob);
		_kb.addIndividual(mary);

		ATermList list = ATermUtils.makeList(robert);
		list = ATermUtils.makeList(bob, list);
		list = ATermUtils.makeList(mary, list);
		_kb.addAllDifferent(list);
		_kb.addSame(robert, bob);

		assertFalse(_kb.isConsistent());

		final Set<ATermAppl> explanation = _kb.getExplanationSet();
		assertTrue(explanation.contains(ATermUtils.makeSameAs(robert, bob)));
		assertTrue(explanation.contains(ATermUtils.makeAllDifferent(list)));
		assertTrue(explanation.size() == 2);

	}

	@Test
	public void testSameDifferent()
	{
		_kb.addIndividual(robert);
		_kb.addIndividual(bob);
		_kb.addSame(robert, bob);
		_kb.addDifferent(robert, bob);
		assertFalse(_kb.isConsistent());

		final Set<ATermAppl> explanation = _kb.getExplanationSet();
		assertTrue(explanation.contains(ATermUtils.makeSameAs(robert, bob)));
		assertTrue(explanation.contains(ATermUtils.makeDifferent(robert, bob)));
		assertTrue(explanation.size() == 2);

	}

	@Test
	public void testSubProp1()
	{
		final ATermAppl noRelatives = ATermUtils.makeMax(relative, 0, ATermUtils.TOP);
		_kb.addIndividual(mary);
		_kb.addIndividual(bob);
		_kb.addObjectProperty(relative);
		_kb.addObjectProperty(sibling);
		_kb.addSubProperty(sibling, relative);
		_kb.addType(bob, noRelatives);
		_kb.addPropertyValue(sibling, bob, mary);

		assertFalse(_kb.isConsistent());

		final Set<ATermAppl> explanation = _kb.getExplanationSet();
		assertIteratorValues(explanation.iterator(), new Object[] { ATermUtils.makeSubProp(sibling, relative), ATermUtils.makePropAtom(sibling, bob, mary), ATermUtils.makeTypeAtom(bob, noRelatives), });

	}

	@Test
	public void testSubProp2()
	{
		final ATermAppl nonHumanRelatives = ATermUtils.makeAllValues(relative, ATermUtils.makeNot(person));
		_kb.addIndividual(mary);
		_kb.addIndividual(bob);
		_kb.addObjectProperty(relative);
		_kb.addObjectProperty(sibling);
		_kb.addSubProperty(sibling, relative);
		_kb.addType(bob, nonHumanRelatives);
		_kb.addType(mary, person);
		_kb.addPropertyValue(sibling, bob, mary);

		assertFalse(_kb.isConsistent());

		final Set<ATermAppl> explanation = _kb.getExplanationSet();
		assertIteratorValues(explanation.iterator(), new Object[] { ATermUtils.makeSubProp(sibling, relative), ATermUtils.makePropAtom(sibling, bob, mary), ATermUtils.makeTypeAtom(bob, nonHumanRelatives), ATermUtils.makeTypeAtom(mary, person), });

	}

	@Test
	public void testTopBottom()
	{
		_kb = new KnowledgeBase();
		_kb.addSubClass(ATermUtils.TOP, ATermUtils.BOTTOM);

		assertFalse(_kb.isConsistent());
		final Set<ATermAppl> explanation = _kb.getExplanationSet();
		assertIteratorValues(explanation.iterator(), new Object[] { ATermUtils.makeSub(ATermUtils.TOP, ATermUtils.BOTTOM), });
	}

	@Test
	public void testTransitive()
	{
		_kb.addObjectProperty(sibling);
		_kb.addTransitiveProperty(sibling);

		// ATermList different = ATermUtils.makeList(new ATermAppl[] { mary,
		// robert, victor} );
		_kb.addIndividual(robert);
		_kb.addIndividual(mary);
		_kb.addIndividual(victor);
		// _kb.addAllDifferent(different);

		// ATermAppl oneSibling = ATermUtils.makeMax(sibling, 1,
		// ATermUtils.TOP);
		final ATermAppl notVictorsSibling = ATermUtils.makeNot(ATermUtils.makeHasValue(sibling, victor));
		_kb.addType(robert, notVictorsSibling);

		_kb.addPropertyValue(sibling, robert, mary);
		_kb.addPropertyValue(sibling, mary, victor);

		assertFalse(_kb.isConsistent());
		final Set<ATermAppl> explanation = _kb.getExplanationSet();

		assertIteratorValues(explanation.iterator(), new Object[] {
			// ATermUtils.makeAllDifferent(different),
			ATermUtils.makeTypeAtom(robert, notVictorsSibling), ATermUtils.makeTransitive(sibling), ATermUtils.makePropAtom(sibling, robert, mary), ATermUtils.makePropAtom(sibling, mary, victor), });

	}

	@Test
	public void testRuleExplanation()
	{
		final KnowledgeBase kb = new KnowledgeBase();

		final ATermAppl C = ATermUtils.makeTermAppl("C");
		final ATermAppl D = ATermUtils.makeTermAppl("D");
		final ATermAppl i = ATermUtils.makeTermAppl("i");

		final List<RuleAtom> body = new ArrayList<>();
		final List<RuleAtom> head = new ArrayList<>();

		body.add(new ClassAtom(C, new AtomIVariable("x")));
		head.add(new ClassAtom(D, new AtomIVariable("x")));

		final Rule rule = new Rule(head, body);

		kb.addClass(C);
		kb.addClass(D);
		kb.addIndividual(i);
		kb.addType(i, C);
		kb.addRule(rule);

		kb.setDoExplanation(true);
		assertTrue(kb.isConsistent());
		assertTrue(kb.isType(i, D));
		final Set<ATermAppl> actual = kb.getExplanationSet();
		kb.setDoExplanation(false);

		final Set<ATermAppl> expected = new HashSet<>();
		final ATermAppl x = ATermUtils.makeVar("x");
		final ATermAppl[] b = new ATermAppl[] { ATermUtils.makeTypeAtom(x, C) };
		final ATermAppl[] h = new ATermAppl[] { ATermUtils.makeTypeAtom(x, D) };
		expected.add(ATermUtils.makeTypeAtom(i, C));
		expected.add(ATermUtils.makeRule(h, b));

		assertEquals(expected, actual);
	}

	@Test
	public void testInverseCardinality1()
	{
		final ATermAppl C = term("C");
		final ATermAppl p = term("p");
		final ATermAppl invP = term("invP");
		final ATermAppl a = term("a");
		final ATermAppl b = term("b");

		_kb.addClass(C);
		_kb.addObjectProperty(p);
		_kb.addObjectProperty(invP);
		_kb.addIndividual(a);
		_kb.addIndividual(b);

		_kb.addSubClass(C, max(invP, 0, TOP));

		_kb.addInverseProperty(p, invP);

		_kb.addPropertyValue(p, b, a);
		_kb.addType(a, C);

		assertFalse(_kb.isConsistent());

		final Set<ATermAppl> explanation = _kb.getExplanationSet();

		assertIteratorValues(explanation.iterator(), new Object[] { ATermUtils.makeSub(C, max(invP, 0, TOP)), ATermUtils.makeInvProp(p, invP), ATermUtils.makePropAtom(p, b, a), ATermUtils.makeTypeAtom(a, C) });
	}

	@Test
	public void testInverseCardinality2()
	{
		final ATermAppl C = term("C");
		final ATermAppl p = term("p");
		final ATermAppl invP = term("invP");
		final ATermAppl a = term("a");
		final ATermAppl b = term("b");
		final ATermAppl c = term("c");
		final ATermList inds = makeList(new ATerm[] { a, b, c });

		_kb.addClass(C);
		_kb.addObjectProperty(p);
		_kb.addObjectProperty(invP);
		_kb.addIndividual(a);
		_kb.addIndividual(b);
		_kb.addIndividual(c);

		_kb.addSubClass(C, max(invP, 1, TOP));

		_kb.addInverseProperty(p, invP);

		_kb.addPropertyValue(p, b, a);
		_kb.addPropertyValue(p, c, a);
		_kb.addType(a, C);
		_kb.addAllDifferent(inds);

		assertFalse(_kb.isConsistent());

		final Set<ATermAppl> explanation = _kb.getExplanationSet();

		assertIteratorValues(explanation.iterator(), new Object[] { ATermUtils.makeSub(C, max(invP, 1, TOP)), ATermUtils.makeInvProp(p, invP), ATermUtils.makeAllDifferent(inds), ATermUtils.makePropAtom(p, b, a), ATermUtils.makePropAtom(p, c, a), ATermUtils.makeTypeAtom(a, C) });
	}

	@Test
	public void testInverseCardinality3()
	{
		final ATermAppl C = term("C");
		final ATermAppl p = term("p");
		final ATermAppl invP = term("invP");
		final ATermAppl a = term("a");
		final ATermAppl b = term("b");
		final ATermAppl c = term("c");
		final ATermAppl d = term("d");
		final ATermList inds = makeList(new ATerm[] { a, b, c, d });

		_kb.addClass(C);
		_kb.addObjectProperty(p);
		_kb.addObjectProperty(invP);
		_kb.addIndividual(a);
		_kb.addIndividual(b);
		_kb.addIndividual(c);
		_kb.addIndividual(d);

		_kb.addSubClass(C, max(invP, 2, TOP));

		_kb.addInverseProperty(p, invP);

		_kb.addPropertyValue(p, b, a);
		_kb.addPropertyValue(p, c, a);
		_kb.addPropertyValue(p, d, a);
		_kb.addType(a, C);
		_kb.addAllDifferent(inds);

		assertFalse(_kb.isConsistent());

		final Set<ATermAppl> explanation = _kb.getExplanationSet();

		assertIteratorValues(explanation.iterator(), new Object[] { ATermUtils.makeSub(C, max(invP, 2, TOP)), ATermUtils.makeInvProp(p, invP), ATermUtils.makeAllDifferent(inds), ATermUtils.makePropAtom(p, b, a), ATermUtils.makePropAtom(p, c, a), ATermUtils.makePropAtom(p, d, a), ATermUtils.makeTypeAtom(a, C) });
	}

	@Test
	public void testInverseAllValues1()
	{
		final ATermAppl C = term("C");
		final ATermAppl D = term("D");
		final ATermAppl p = term("p");
		final ATermAppl invP = term("invP");
		final ATermAppl a = term("a");
		final ATermAppl b = term("b");

		_kb.addClass(C);
		_kb.addClass(D);
		_kb.addObjectProperty(p);
		_kb.addObjectProperty(invP);
		_kb.addIndividual(a);
		_kb.addIndividual(b);

		_kb.addSubClass(C, all(invP, D));

		_kb.addInverseProperty(p, invP);

		_kb.addPropertyValue(p, b, a);
		_kb.addType(a, C);

		assertTrue(_kb.isConsistent());

		assertTrue(_kb.isType(b, D));

		final Set<ATermAppl> explanation = _kb.getExplanationSet();

		assertIteratorValues(explanation.iterator(), new Object[] { ATermUtils.makeSub(C, all(invP, D)), ATermUtils.makeInvProp(p, invP), ATermUtils.makePropAtom(p, b, a), ATermUtils.makeTypeAtom(a, C) });
	}

	@Test
	public void testInverseAllValues2()
	{
		classes(_C, _D);
		objectProperties(_p, _q);
		individuals(_a, _b, _c);

		_kb.addSubClass(_C, all(_q, _D));

		_kb.addTransitiveProperty(_p);
		_kb.addInverseProperty(_p, _q);

		_kb.addPropertyValue(_p, _a, _b);
		_kb.addPropertyValue(_p, _b, _c);
		_kb.addType(_c, _C);
		_kb.addType(_a, not(_D));

		assertFalse(_kb.isConsistent());

		final Set<ATermAppl> explanation = _kb.getExplanationSet();

		assertIteratorValues(explanation.iterator(), new Object[] { ATermUtils.makeSub(_C, all(_q, _D)), ATermUtils.makeTransitive(_p), ATermUtils.makeInvProp(_p, _q), ATermUtils.makePropAtom(_p, _a, _b), ATermUtils.makePropAtom(_p, _b, _c), ATermUtils.makeTypeAtom(_a, not(_D)), ATermUtils.makeTypeAtom(_c, _C) });
	}

	@Test
	public void testRestrictedDatatypeRange()
	{
		classes(_C, _D);
		dataProperties(_p);
		individuals(_a, _b, _c);

		_kb.addRange(_p, restrict(Datatypes.INTEGER, minInclusive(literal(10))));
		_kb.addPropertyValue(_p, _a, literal(5));
		assertFalse(_kb.isConsistent());

		final Set<ATermAppl> explanation = _kb.getExplanationSet();

		assertIteratorValues(explanation.iterator(), new Object[] { ATermUtils.makeRange(_p, restrict(Datatypes.INTEGER, minInclusive(literal(10)))), ATermUtils.makePropAtom(_p, _a, literal(5)) });
	}

	@Test
	public void testDatatypeDefinitionInconsistency()
	{
		classes(_C);
		dataProperties(_p);
		individuals(_a, _b, _c);

		_kb.addRange(_p, _D);
		_kb.addDatatypeDefinition(_D, restrict(Datatypes.INTEGER, minInclusive(literal(10))));
		_kb.addPropertyValue(_p, _a, literal(5));

		explainInconsistency(ATermUtils.makeRange(_p, _D), ATermUtils.makeDatatypeDefinition(_D, restrict(Datatypes.INTEGER, minInclusive(literal(10)))), ATermUtils.makePropAtom(_p, _a, literal(5)));
	}

	@Test
	public void testDatatypeDefinition()
	{
		classes(_A);
		dataProperties(_p);
		individuals(_a);

		_kb.addDatatypeDefinition(_D, restrict(Datatypes.INTEGER, minInclusive(literal(10))));
		_kb.addPropertyValue(_p, _a, literal(15));
		_kb.addEquivalentClass(_A, some(_p, _D));

		explainEntailment(_kb.isType(_a, _A), ATermUtils.makeEqClasses(_A, some(_p, _D)), ATermUtils.makeDatatypeDefinition(_D, restrict(Datatypes.INTEGER, minInclusive(literal(10)))), ATermUtils.makePropAtom(_p, _a, literal(15)));
	}

	@Test
	public void testDatatypeEnumeration()
	{
		classes(_A);
		objectProperties(_q);
		dataProperties(_p);
		individuals(_a, _b);

		_kb.addDatatypeDefinition(_D, oneOf(literal(1), literal(2)));
		_kb.addPropertyValue(_p, _a, literal(1));
		_kb.addPropertyValue(_p, _b, literal(2));
		_kb.addPropertyValue(_p, _b, literal(3));
		_kb.addPropertyValue(_q, _a, _b);
		_kb.addEquivalentClass(_A, and(some(_p, _D), some(_q, some(_p, not(_D)))));

		explainEntailment(_kb.isType(_a, _A), ATermUtils.makeEqClasses(_A, and(some(_p, _D), some(_q, some(_p, not(_D))))), ATermUtils.makeDatatypeDefinition(_D, oneOf(literal(1), literal(2))), ATermUtils.makePropAtom(_p, _a, literal(1)), ATermUtils.makePropAtom(_p, _b, literal(3)), ATermUtils.makePropAtom(_q, _a, _b));
	}

	@Test
	public void ruleInteractionWithInverses()
	{
		// Tests #446

		classes(_A);
		objectProperties(_p, _q, _r, _f);
		dataProperties(_p);
		individuals(_a, _b, _c);

		final AtomIVariable x = new AtomIVariable("x");
		final AtomIVariable y = new AtomIVariable("y");
		final AtomIVariable z = new AtomIVariable("z");

		_kb.addSymmetricProperty(_p);
		_kb.addInverseProperty(_q, _r);

		_kb.addPropertyValue(_p, _c, _a);
		_kb.addPropertyValue(_f, _a, _b);

		final List<RuleAtom> body = Arrays.<RuleAtom> asList(new IndividualPropertyAtom(_f, x, y), new IndividualPropertyAtom(_p, x, z));
		final List<RuleAtom> head = Arrays.<RuleAtom> asList(new IndividualPropertyAtom(_r, z, y));
		final Rule rule = new Rule(head, body);
		_kb.addRule(rule);

		explainEntailment(_kb.hasPropertyValue(_b, _q, _c), ATermUtils.makePropAtom(_p, _c, _a), ATermUtils.makePropAtom(_f, _a, _b), ATermUtils.makeSymmetric(_p), ATermUtils.makeInvProp(_q, _r), new RulesToATermTranslator().translate(rule));

	}

	@Test
	public void propertyChainInstances()
	{
		// Tests #367

		objectProperties(_p, _q, _r);
		individuals(_a, _b, _c);

		_kb.addSubProperty(list(_p, _q), _r);

		_kb.addPropertyValue(_p, _a, _b);
		_kb.addPropertyValue(_q, _b, _c);

		explainEntailment(_kb.hasPropertyValue(_a, _r, _c), ATermUtils.makePropAtom(_p, _a, _b), ATermUtils.makePropAtom(_q, _b, _c), ATermUtils.makeSubProp(list(_p, _q), _r));
	}

	@Test
	public void propertyChainClasses()
	{
		// Tests #367

		classes(_A, _B, _C);
		objectProperties(_p, _q, _r);

		_kb.addSubProperty(list(_p, _q), _r);

		_kb.addSubClass(_A, some(_p, some(_q, _B)));
		_kb.addSubClass(some(_r, _B), _C);

		explainEntailment(_kb.isSubClassOf(_A, _C), ATermUtils.makeSub(_A, some(_p, some(_q, _B))), ATermUtils.makeSub(some(_r, _B), _C), ATermUtils.makeSubProp(list(_p, _q), _r));
	}

	@Ignore("Fails due to #294")
	@Test
	public void propertyChainNested()
	{
		// Tests #367, #294

		classes(_A, _B, _C);
		objectProperties(_p, _q, _r, _f);

		_kb.addSubProperty(list(_p, _q), _p);
		_kb.addSubProperty(list(_p, _r), _f);
		_kb.addSubProperty(_r, _q);

		_kb.addSubClass(_A, some(_p, some(_r, some(_r, _B))));
		_kb.addSubClass(some(_f, _B), _C);

		explainEntailment(_kb.isSubClassOf(_A, _C), ATermUtils.makeSub(_A, some(_p, some(_q, _B))), ATermUtils.makeSub(some(_r, _B), _C), ATermUtils.makeSubProp(list(_p, _q), _r), ATermUtils.makeSubProp(list(_r, _p), _f));
	}

	@Test
	public void testDomainExpression()
	{
		classes(_A, _B);
		objectProperties(_p);
		individuals(_a, _b);

		_kb.addDomain(_p, or(_A, _B));
		_kb.addType(_a, not(or(_A, _B)));
		_kb.addPropertyValue(_p, _a, _b);

		explainInconsistency(ATermUtils.makeDomain(_p, or(_A, _B)), ATermUtils.makeTypeAtom(_a, not(or(_A, _B))), ATermUtils.makePropAtom(_p, _a, _b));
	}

	@Test
	public void testRangeExpression()
	{
		classes(_A, _B);
		objectProperties(_p);
		individuals(_a, _b);

		_kb.addRange(_p, or(_A, _B));
		_kb.addType(_b, not(or(_A, _B)));
		_kb.addPropertyValue(_p, _a, _b);

		explainInconsistency(ATermUtils.makeRange(_p, or(_A, _B)), ATermUtils.makeTypeAtom(_b, not(or(_A, _B))), ATermUtils.makePropAtom(_p, _a, _b));
	}

	@Test
	public void testFunctionalSubDataProperty()
	{
		// test for ticket #551

		individuals(_a);
		dataProperties(_p, _q);

		_kb.addFunctionalProperty(_q);
		_kb.addSubProperty(_p, _q);

		_kb.addPropertyValue(_p, _a, literal(1));
		_kb.addPropertyValue(_q, _a, literal(2));

		explainInconsistency(ATermUtils.makeFunctional(_q), ATermUtils.makeSubProp(_p, _q), ATermUtils.makePropAtom(_p, _a, literal(1)), ATermUtils.makePropAtom(_q, _a, literal(2)));
	}
}
