// Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package org.mindswap.pellet.test.rules;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.Collections;

import org.junit.Before;
import org.junit.Test;
import org.mindswap.pellet.DependencySet;
import org.mindswap.pellet.KnowledgeBase;
import org.mindswap.pellet.utils.ATermUtils;

import aterm.ATermAppl;

import com.clarkparsia.pellet.datatypes.types.text.XSDString;
import com.clarkparsia.pellet.rules.RulesToReteTranslator;
import com.clarkparsia.pellet.rules.model.AtomDConstant;
import com.clarkparsia.pellet.rules.model.AtomDVariable;
import com.clarkparsia.pellet.rules.model.AtomIConstant;
import com.clarkparsia.pellet.rules.model.AtomIVariable;
import com.clarkparsia.pellet.rules.model.ClassAtom;
import com.clarkparsia.pellet.rules.model.DataRangeAtom;
import com.clarkparsia.pellet.rules.model.DatavaluedPropertyAtom;
import com.clarkparsia.pellet.rules.model.DifferentIndividualsAtom;
import com.clarkparsia.pellet.rules.model.IndividualPropertyAtom;
import com.clarkparsia.pellet.rules.model.Rule;
import com.clarkparsia.pellet.rules.model.RuleAtom;
import com.clarkparsia.pellet.rules.model.SameIndividualAtom;
import com.clarkparsia.pellet.rules.rete.Compiler;
import com.clarkparsia.pellet.rules.rete.TermTuple;

public class TranslatorTests {
	private final ATermAppl a = ATermUtils.makeTermAppl("a"), c = ATermUtils.makePlainLiteral("c"), pd = ATermUtils
	                .makeTermAppl("pd"), pi = ATermUtils.makeTermAppl("pi");

	private final AtomIConstant ca = new AtomIConstant(a);
	private final AtomDConstant cc = new AtomDConstant(c);

	private final ATermAppl cra = a, crc = ATermUtils.makePlainLiteral("c");

	private KnowledgeBase kb;
	private RulesToReteTranslator ruleTranslator;

	private final ATermAppl vrx = ATermUtils.makeVar("x");

	private final AtomIVariable vx = new AtomIVariable("x");
	private final AtomDVariable vz = new AtomDVariable("z");

	@Before
	public void setUp() {
		kb = new KnowledgeBase();
		ruleTranslator = new RulesToReteTranslator(kb.getABox());
	}

	@Test
	public void testClassAtom() {
		ClassAtom atom = new ClassAtom(a, vx);
		TermTuple triple = new TermTuple(DependencySet.INDEPENDENT, Compiler.TYPE, vrx, cra);
		assertEquals(triple, ruleTranslator.translateAtom(atom, DependencySet.INDEPENDENT));
	}

	@Test
	public void testDataRangeAtom() {
		DataRangeAtom atom = new DataRangeAtom(XSDString.getInstance().getName(), vz);
		assertNull(ruleTranslator.translateAtom(atom, DependencySet.INDEPENDENT));
	}

	@Test
	public void testDatavaluedPropertyAtom() {
		kb.addDatatypeProperty(pd);
		DatavaluedPropertyAtom atom = new DatavaluedPropertyAtom(pd, vx, cc);
		TermTuple triple = new TermTuple(DependencySet.INDEPENDENT, pd, vrx, crc);
		assertEquals(triple, ruleTranslator.translateAtom(atom, DependencySet.INDEPENDENT));
	}

	@Test
	public void testDifferentIndividualsAtom() {
		kb.addIndividual(a);
		DifferentIndividualsAtom atom = new DifferentIndividualsAtom(vx, ca);
		TermTuple triple = new TermTuple(DependencySet.INDEPENDENT, Compiler.DIFF_FROM, vrx, cra);
		assertEquals(triple, ruleTranslator.translateAtom(atom, DependencySet.INDEPENDENT));
	}

	@Test
	public void testIndividualPropertyAtom() {
		kb.addObjectProperty(pi);
		kb.addIndividual(a);
		IndividualPropertyAtom atom = new IndividualPropertyAtom(pi, vx, ca);
		TermTuple triple = new TermTuple(DependencySet.INDEPENDENT, pi, vrx, cra);
		assertEquals(triple, ruleTranslator.translateAtom(atom, DependencySet.INDEPENDENT));
	}

	@Test
	public void testSameIndividualAtom() {
		kb.addIndividual(a);
		SameIndividualAtom atom = new SameIndividualAtom(vx, ca);
		TermTuple triple = new TermTuple(DependencySet.INDEPENDENT, Compiler.SAME_AS, vrx, cra);
		assertEquals(triple, ruleTranslator.translateAtom(atom, DependencySet.INDEPENDENT));
	}

	@Test
	public void testTranslateRule() {
		kb.addObjectProperty(pi);
		kb.addIndividual(a);
		IndividualPropertyAtom bodyAtom = new IndividualPropertyAtom(pi, vx, ca);
		TermTuple bodyTriple = new TermTuple(DependencySet.INDEPENDENT, pi, vrx, cra);
		SameIndividualAtom headAtom = new SameIndividualAtom(vx, ca);
		TermTuple headTriple = new TermTuple(DependencySet.INDEPENDENT, Compiler.SAME_AS, vrx, cra);

		Rule rule = new Rule(Collections.singletonList((RuleAtom) headAtom), Collections
		                .singletonList((RuleAtom) bodyAtom));
		com.clarkparsia.pellet.rules.rete.Rule reteRule = new com.clarkparsia.pellet.rules.rete.Rule(Collections
		                .singletonList(bodyTriple), Collections.singletonList(headTriple));

		assertEquals(reteRule, ruleTranslator.translateRule(rule));
	}

}
