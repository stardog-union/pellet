// Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package org.mindswap.pellet.test.rules;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mindswap.pellet.test.PelletTestCase.assertIteratorValues;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.mindswap.pellet.Individual;
import org.mindswap.pellet.KnowledgeBase;
import org.mindswap.pellet.Literal;
import org.mindswap.pellet.Node;
import org.mindswap.pellet.utils.ATermUtils;
import org.mindswap.pellet.utils.Namespaces;

import aterm.ATermAppl;

import com.clarkparsia.pellet.rules.BindingGenerator;
import com.clarkparsia.pellet.rules.BindingGeneratorImpl;
import com.clarkparsia.pellet.rules.BindingHelper;
import com.clarkparsia.pellet.rules.DataRangeBindingHelper;
import com.clarkparsia.pellet.rules.DatavaluePropertyBindingHelper;
import com.clarkparsia.pellet.rules.ObjectVariableBindingHelper;
import com.clarkparsia.pellet.rules.TrivialSatisfactionHelpers;
import com.clarkparsia.pellet.rules.VariableBinding;
import com.clarkparsia.pellet.rules.model.AtomDConstant;
import com.clarkparsia.pellet.rules.model.AtomDVariable;
import com.clarkparsia.pellet.rules.model.AtomIConstant;
import com.clarkparsia.pellet.rules.model.AtomIVariable;
import com.clarkparsia.pellet.rules.model.AtomVariable;
import com.clarkparsia.pellet.rules.model.DataRangeAtom;
import com.clarkparsia.pellet.rules.model.DatavaluedPropertyAtom;
import com.clarkparsia.pellet.rules.model.IndividualPropertyAtom;
import com.clarkparsia.pellet.rules.model.RuleAtom;

/**
 * <p>
 * Title: Binding Generator Tests
 * </p>
 * <p>
 * Description: Tests the various binding generators
 * </p>
 * <p>
 * Copyright: Copyright (c) 2007
 * </p>
 * <p>
 * Company: Clark & Parsia, LLC. <http://www.clarkparsia.com>
 * </p>
 * 
 * @author Ron Alford
 */

public class BindingGeneratorsTest {

	private static class BindingToMapIterator implements Iterator<Map<AtomVariable, ATermAppl>> {
		private final Iterator<VariableBinding> iter;

		public BindingToMapIterator(Iterator<VariableBinding> iter) {
			this.iter = iter;
		}

		public boolean hasNext() {
			return iter.hasNext();
		}

		public Map<AtomVariable, ATermAppl> next() {
			Map<AtomVariable, ATermAppl> result = new HashMap<AtomVariable, ATermAppl>();
			VariableBinding binding = iter.next();
			for (Map.Entry<? extends AtomVariable, ? extends Node> entry : binding.entrySet()) {
				result.put(entry.getKey(), entry.getValue().getTerm());
			}
			return result;
		}

		public void remove() {
			iter.remove();
		}

	}

	KnowledgeBase kb;

	private static final ATermAppl data1 = ATermUtils.makePlainLiteral("data1"), data2 = ATermUtils
	                .makePlainLiteral("data2"), data3 = ATermUtils.makePlainLiteral("data3"), data4 = ATermUtils
	                .makeTypedLiteral("4", Namespaces.XSD + "decimal"), dp1 = ATermUtils.makeTermAppl("dataProp1"),
	                dp2 = ATermUtils.makeTermAppl("dataProp2"), mary = ATermUtils.makeTermAppl("Mary"), p = ATermUtils
	                                .makeTermAppl("p"), robert = ATermUtils.makeTermAppl("Robert"), victor = ATermUtils
	                                .makeTermAppl("Victor");

	private AtomIVariable x, y;
	private AtomDVariable z;

	@Before
	public void setUp() {
		kb = new KnowledgeBase();
		x = new AtomIVariable("x");
		y = new AtomIVariable("y");
		z = new AtomDVariable("z");

		kb.addDatatypeProperty(dp1);
		kb.addDatatypeProperty(dp2);
		kb.addSubProperty(dp1, dp2);

		kb.addIndividual(mary);
		kb.addIndividual(robert);
		kb.addIndividual(victor);

		kb.addPropertyValue(dp1, mary, data1);
		kb.addPropertyValue(dp2, mary, data2);
		kb.addPropertyValue(dp1, robert, data2);
		kb.addPropertyValue(dp1, robert, data3);
		kb.addPropertyValue(dp2, victor, data4);

	}

	@Test
	public void testCombinatorialBindingGeneration() {
		BindingHelper genHelper1 = new ObjectVariableBindingHelper(kb.getABox(), x);
		BindingHelper genHelper2 = new ObjectVariableBindingHelper(kb.getABox(), y);

		Individual[] individualsUsed = { kb.getABox().getIndividual(mary), kb.getABox().getIndividual(robert),
		                kb.getABox().getIndividual(victor), };

		List<BindingHelper> genSet = new ArrayList<BindingHelper>();

		BindingGenerator emptyGen = new BindingGeneratorImpl(kb.getABox(), new VariableBinding(kb.getABox()), genSet);
		assertFalse(emptyGen.iterator().hasNext());

		genSet.add(genHelper1);
		genSet.add(genHelper2);

		BindingGenerator gen = new BindingGeneratorImpl(kb.getABox(), new VariableBinding(kb.getABox()), genSet);
		List<VariableBinding> expected = new LinkedList<VariableBinding>();
		for (Individual xNode : individualsUsed) {
			for (Individual yNode : individualsUsed) {
				VariableBinding bindings = new VariableBinding(kb.getABox());
				bindings.set(x, xNode);
				bindings.set(y, yNode);
				expected.add(bindings);
			}
		}
		assertIteratorValues(gen.iterator(), expected.iterator());
	}

	@Test
	public void testDataRangeBindingHelper() {
		DatavaluedPropertyAtom pattern = new DatavaluedPropertyAtom(dp2, x, z);
		DataRangeAtom atom = new DataRangeAtom(ATermUtils.makeTermAppl(Namespaces.XSD + "integer"), z);

		BindingHelper patternHelper = new DatavaluePropertyBindingHelper(kb.getABox(), pattern);
		BindingHelper rangeHelper = new DataRangeBindingHelper(kb.getABox(), atom);
		BindingGenerator gen = new BindingGeneratorImpl(kb.getABox(), new VariableBinding(kb.getABox()), Arrays
		                .asList(new BindingHelper[] { patternHelper, rangeHelper, }));

		VariableBinding expectedBinding = new VariableBinding(kb.getABox());
		expectedBinding.set(x, kb.getABox().getIndividual(victor));
		expectedBinding.set(z, kb.getABox().getLiteral(data4));
		List<VariableBinding> expected = new LinkedList<VariableBinding>();
		expected.add(expectedBinding);

		assertIteratorValues(gen.iterator(), expected.iterator());

	}

	@Test
	public void testDatavalueBindingGeneratorChained() {
		DatavaluedPropertyAtom pattern1 = new DatavaluedPropertyAtom(dp2, x, z);
		DatavaluedPropertyAtom pattern2 = new DatavaluedPropertyAtom(dp2, y, z);

		BindingHelper genHelper1 = new DatavaluePropertyBindingHelper(kb.getABox(), pattern1);
		BindingHelper genHelper2 = new DatavaluePropertyBindingHelper(kb.getABox(), pattern2);
		BindingGenerator gen = new BindingGeneratorImpl(kb.getABox(), new VariableBinding(kb.getABox()), Arrays
		                .asList(new BindingHelper[] { genHelper1, genHelper2 }));

		List<VariableBinding> expected = new LinkedList<VariableBinding>();
		VariableBinding binding;
		ATermAppl[] names = new ATermAppl[] { mary, robert, victor };
		ATermAppl[] values = new ATermAppl[] { data1, data2, data3, data4 };
		for (ATermAppl xName : names) {
			for (ATermAppl yName : names) {
				for (ATermAppl zValue : values) {
					Individual xNode = kb.getABox().getIndividual(xName);
					Individual yNode = kb.getABox().getIndividual(yName);
					Literal zNode = kb.getABox().addLiteral(zValue);

					if (kb.hasPropertyValue(xName, dp2, zValue) && kb.hasPropertyValue(yName, dp2, zValue)) {
						binding = new VariableBinding(kb.getABox());
						binding.set(x, xNode);
						binding.set(y, yNode);
						binding.set(z, zNode);
						expected.add(binding);
					}
				}
			}
		}

		assertIteratorValues(gen.iterator(), expected.iterator());
	}

	@Test
	public void testDatavalueBindingGeneratorChainedSubject() {
		DatavaluedPropertyAtom pattern1 = new DatavaluedPropertyAtom(dp2, x, new AtomDConstant(data2));
		DatavaluedPropertyAtom pattern2 = new DatavaluedPropertyAtom(dp2, y, new AtomDConstant(data2));

		BindingHelper genHelper1 = new DatavaluePropertyBindingHelper(kb.getABox(), pattern1);
		BindingHelper genHelper2 = new DatavaluePropertyBindingHelper(kb.getABox(), pattern2);
		BindingGenerator gen = new BindingGeneratorImpl(kb.getABox(), new VariableBinding(kb.getABox()), Arrays
		                .asList(new BindingHelper[] { genHelper1, genHelper2 }));

		List<VariableBinding> expected = new LinkedList<VariableBinding>();
		VariableBinding binding;
		ATermAppl[] names = new ATermAppl[] { mary, robert };
		for (ATermAppl xName : names) {
			for (ATermAppl yName : names) {
				Individual xNode = kb.getABox().getIndividual(xName);
				Individual yNode = kb.getABox().getIndividual(yName);

				binding = new VariableBinding(kb.getABox());
				binding.set(x, xNode);
				binding.set(y, yNode);
				expected.add(binding);
			}
		}

		assertIteratorValues(gen.iterator(), expected.iterator());
	}

	@Test
	public void testDatavalueBindingGeneratorObjects() {
		DatavaluedPropertyAtom pattern = new DatavaluedPropertyAtom(dp2, new AtomIConstant(mary), z);

		BindingHelper genHelper = new DatavaluePropertyBindingHelper(kb.getABox(), pattern);
		BindingGenerator gen = new BindingGeneratorImpl(kb.getABox(), new VariableBinding(kb.getABox()), Collections
		                .singletonList(genHelper));
		assertIteratorValues(new BindingToMapIterator(gen.iterator()), new Object[] {
		                Collections.singletonMap(z, data1), Collections.singletonMap(z, data2), });

	}

	@Test
	public void testDatavalueBindingGeneratorSubjects() {
		DatavaluedPropertyAtom pattern = new DatavaluedPropertyAtom(dp2, x, new AtomDConstant(data2));

		BindingHelper genHelper = new DatavaluePropertyBindingHelper(kb.getABox(), pattern);
		BindingGenerator gen = new BindingGeneratorImpl(kb.getABox(), new VariableBinding(kb.getABox()), Collections
		                .singletonList(genHelper));

		assertIteratorValues(new BindingToMapIterator(gen.iterator()), new Object[] {
		                Collections.singletonMap(x, mary), Collections.singletonMap(x, robert), });

	}

	@Test
	public void testIsAtomTrue() {
		kb.addObjectProperty(p);
		kb.addIndividual(mary);
		kb.addIndividual(robert);
		kb.addPropertyValue(p, mary, robert);

		VariableBinding binding = new VariableBinding(kb.getABox());
		binding.set(x, mary);
		binding.set(y, robert);

		RuleAtom atom = new IndividualPropertyAtom(p, x, y);
		TrivialSatisfactionHelpers tester = new TrivialSatisfactionHelpers(kb.getABox());
		assertTrue(tester.isAtomTrue(atom, binding) != null);
	}

	@Test
	public void testObjectVariableBindingGenerator() {
		kb.addIndividual(mary);
		kb.addIndividual(robert);
		kb.addIndividual(victor);

		BindingHelper genHelper = new ObjectVariableBindingHelper(kb.getABox(), x);
		BindingGenerator gen = new BindingGeneratorImpl(kb.getABox(), new VariableBinding(kb.getABox()), Collections
		                .singletonList(genHelper));

		assertIteratorValues(new BindingToMapIterator(gen.iterator()), new Object[] {
		                Collections.singletonMap(x, mary), Collections.singletonMap(x, robert),
		                Collections.singletonMap(x, victor), });
	}

}
