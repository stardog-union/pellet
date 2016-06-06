// Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package org.mindswap.pellet.test.rules;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mindswap.pellet.test.PelletTestCase.assertIteratorValues;

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

public class BindingGeneratorsTest
{

	private static class BindingToMapIterator implements Iterator<Map<AtomVariable, ATermAppl>>
	{
		private final Iterator<VariableBinding> _iter;

		public BindingToMapIterator(final Iterator<VariableBinding> iter)
		{
			this._iter = iter;
		}

		@Override
		public boolean hasNext()
		{
			return _iter.hasNext();
		}

		@Override
		public Map<AtomVariable, ATermAppl> next()
		{
			final Map<AtomVariable, ATermAppl> result = new HashMap<>();
			final VariableBinding binding = _iter.next();
			for (final Map.Entry<? extends AtomVariable, ? extends Node> entry : binding.entrySet())
				result.put(entry.getKey(), entry.getValue().getTerm());
			return result;
		}

		@Override
		public void remove()
		{
			_iter.remove();
		}

	}

	KnowledgeBase _kb;

	private static final ATermAppl data1 = ATermUtils.makePlainLiteral("data1"), data2 = ATermUtils.makePlainLiteral("data2"), data3 = ATermUtils.makePlainLiteral("data3"), data4 = ATermUtils.makeTypedLiteral("4", Namespaces.XSD + "decimal"), dp1 = ATermUtils.makeTermAppl("dataProp1"), dp2 = ATermUtils.makeTermAppl("dataProp2"), mary = ATermUtils.makeTermAppl("Mary"), p = ATermUtils.makeTermAppl("p"), robert = ATermUtils.makeTermAppl("Robert"), victor = ATermUtils.makeTermAppl("Victor");

	private AtomIVariable _x, _y;
	private AtomDVariable _z;

	@Before
	public void setUp()
	{
		_kb = new KnowledgeBase();
		_x = new AtomIVariable("x");
		_y = new AtomIVariable("y");
		_z = new AtomDVariable("z");

		_kb.addDatatypeProperty(dp1);
		_kb.addDatatypeProperty(dp2);
		_kb.addSubProperty(dp1, dp2);

		_kb.addIndividual(mary);
		_kb.addIndividual(robert);
		_kb.addIndividual(victor);

		_kb.addPropertyValue(dp1, mary, data1);
		_kb.addPropertyValue(dp2, mary, data2);
		_kb.addPropertyValue(dp1, robert, data2);
		_kb.addPropertyValue(dp1, robert, data3);
		_kb.addPropertyValue(dp2, victor, data4);

	}

	@Test
	public void testCombinatorialBindingGeneration()
	{
		final BindingHelper genHelper1 = new ObjectVariableBindingHelper(_kb.getABox(), _x);
		final BindingHelper genHelper2 = new ObjectVariableBindingHelper(_kb.getABox(), _y);

		final Individual[] individualsUsed = { _kb.getABox().getIndividual(mary), _kb.getABox().getIndividual(robert), _kb.getABox().getIndividual(victor), };

		final List<BindingHelper> genSet = new ArrayList<>();

		final BindingGenerator emptyGen = new BindingGeneratorImpl(_kb.getABox(), new VariableBinding(_kb.getABox()), genSet);
		assertFalse(emptyGen.iterator().hasNext());

		genSet.add(genHelper1);
		genSet.add(genHelper2);

		final BindingGenerator gen = new BindingGeneratorImpl(_kb.getABox(), new VariableBinding(_kb.getABox()), genSet);
		final List<VariableBinding> expected = new LinkedList<>();
		for (final Individual xNode : individualsUsed)
			for (final Individual yNode : individualsUsed)
			{
				final VariableBinding bindings = new VariableBinding(_kb.getABox());
				bindings.set(_x, xNode);
				bindings.set(_y, yNode);
				expected.add(bindings);
			}
		assertIteratorValues(gen.iterator(), expected.iterator());
	}

	@Test
	public void testDataRangeBindingHelper()
	{
		final DatavaluedPropertyAtom pattern = new DatavaluedPropertyAtom(dp2, _x, _z);
		final DataRangeAtom atom = new DataRangeAtom(ATermUtils.makeTermAppl(Namespaces.XSD + "integer"), _z);

		final BindingHelper patternHelper = new DatavaluePropertyBindingHelper(_kb.getABox(), pattern);
		final BindingHelper rangeHelper = new DataRangeBindingHelper(_kb.getABox(), atom);
		final BindingGenerator gen = new BindingGeneratorImpl(_kb.getABox(), new VariableBinding(_kb.getABox()), Arrays.asList(new BindingHelper[] { patternHelper, rangeHelper, }));

		final VariableBinding expectedBinding = new VariableBinding(_kb.getABox());
		expectedBinding.set(_x, _kb.getABox().getIndividual(victor));
		expectedBinding.set(_z, _kb.getABox().getLiteral(data4));
		final List<VariableBinding> expected = new LinkedList<>();
		expected.add(expectedBinding);

		assertIteratorValues(gen.iterator(), expected.iterator());

	}

	@Test
	public void testDatavalueBindingGeneratorChained()
	{
		final DatavaluedPropertyAtom pattern1 = new DatavaluedPropertyAtom(dp2, _x, _z);
		final DatavaluedPropertyAtom pattern2 = new DatavaluedPropertyAtom(dp2, _y, _z);

		final BindingHelper genHelper1 = new DatavaluePropertyBindingHelper(_kb.getABox(), pattern1);
		final BindingHelper genHelper2 = new DatavaluePropertyBindingHelper(_kb.getABox(), pattern2);
		final BindingGenerator gen = new BindingGeneratorImpl(_kb.getABox(), new VariableBinding(_kb.getABox()), Arrays.asList(new BindingHelper[] { genHelper1, genHelper2 }));

		final List<VariableBinding> expected = new LinkedList<>();
		VariableBinding binding;
		final ATermAppl[] names = new ATermAppl[] { mary, robert, victor };
		final ATermAppl[] values = new ATermAppl[] { data1, data2, data3, data4 };
		for (final ATermAppl xName : names)
			for (final ATermAppl yName : names)
				for (final ATermAppl zValue : values)
				{
					final Individual xNode = _kb.getABox().getIndividual(xName);
					final Individual yNode = _kb.getABox().getIndividual(yName);
					final Literal zNode = _kb.getABox().addLiteral(zValue);

					if (_kb.hasPropertyValue(xName, dp2, zValue) && _kb.hasPropertyValue(yName, dp2, zValue))
					{
						binding = new VariableBinding(_kb.getABox());
						binding.set(_x, xNode);
						binding.set(_y, yNode);
						binding.set(_z, zNode);
						expected.add(binding);
					}
				}

		assertIteratorValues(gen.iterator(), expected.iterator());
	}

	@Test
	public void testDatavalueBindingGeneratorChainedSubject()
	{
		final DatavaluedPropertyAtom pattern1 = new DatavaluedPropertyAtom(dp2, _x, new AtomDConstant(data2));
		final DatavaluedPropertyAtom pattern2 = new DatavaluedPropertyAtom(dp2, _y, new AtomDConstant(data2));

		final BindingHelper genHelper1 = new DatavaluePropertyBindingHelper(_kb.getABox(), pattern1);
		final BindingHelper genHelper2 = new DatavaluePropertyBindingHelper(_kb.getABox(), pattern2);
		final BindingGenerator gen = new BindingGeneratorImpl(_kb.getABox(), new VariableBinding(_kb.getABox()), Arrays.asList(new BindingHelper[] { genHelper1, genHelper2 }));

		final List<VariableBinding> expected = new LinkedList<>();
		VariableBinding binding;
		final ATermAppl[] names = new ATermAppl[] { mary, robert };
		for (final ATermAppl xName : names)
			for (final ATermAppl yName : names)
			{
				final Individual xNode = _kb.getABox().getIndividual(xName);
				final Individual yNode = _kb.getABox().getIndividual(yName);

				binding = new VariableBinding(_kb.getABox());
				binding.set(_x, xNode);
				binding.set(_y, yNode);
				expected.add(binding);
			}

		assertIteratorValues(gen.iterator(), expected.iterator());
	}

	@Test
	public void testDatavalueBindingGeneratorObjects()
	{
		final DatavaluedPropertyAtom pattern = new DatavaluedPropertyAtom(dp2, new AtomIConstant(mary), _z);

		final BindingHelper genHelper = new DatavaluePropertyBindingHelper(_kb.getABox(), pattern);
		final BindingGenerator gen = new BindingGeneratorImpl(_kb.getABox(), new VariableBinding(_kb.getABox()), Collections.singletonList(genHelper));
		assertIteratorValues(new BindingToMapIterator(gen.iterator()), new Object[] { Collections.singletonMap(_z, data1), Collections.singletonMap(_z, data2), });

	}

	@Test
	public void testDatavalueBindingGeneratorSubjects()
	{
		final DatavaluedPropertyAtom pattern = new DatavaluedPropertyAtom(dp2, _x, new AtomDConstant(data2));

		final BindingHelper genHelper = new DatavaluePropertyBindingHelper(_kb.getABox(), pattern);
		final BindingGenerator gen = new BindingGeneratorImpl(_kb.getABox(), new VariableBinding(_kb.getABox()), Collections.singletonList(genHelper));

		assertIteratorValues(new BindingToMapIterator(gen.iterator()), new Object[] { Collections.singletonMap(_x, mary), Collections.singletonMap(_x, robert), });

	}

	@Test
	public void testIsAtomTrue()
	{
		_kb.addObjectProperty(p);
		_kb.addIndividual(mary);
		_kb.addIndividual(robert);
		_kb.addPropertyValue(p, mary, robert);

		final VariableBinding binding = new VariableBinding(_kb.getABox());
		binding.set(_x, mary);
		binding.set(_y, robert);

		final RuleAtom atom = new IndividualPropertyAtom(p, _x, _y);
		final TrivialSatisfactionHelpers tester = new TrivialSatisfactionHelpers(_kb.getABox());
		assertTrue(tester.isAtomTrue(atom, binding) != null);
	}

	@Test
	public void testObjectVariableBindingGenerator()
	{
		_kb.addIndividual(mary);
		_kb.addIndividual(robert);
		_kb.addIndividual(victor);

		final BindingHelper genHelper = new ObjectVariableBindingHelper(_kb.getABox(), _x);
		final BindingGenerator gen = new BindingGeneratorImpl(_kb.getABox(), new VariableBinding(_kb.getABox()), Collections.singletonList(genHelper));

		assertIteratorValues(new BindingToMapIterator(gen.iterator()), new Object[] { Collections.singletonMap(_x, mary), Collections.singletonMap(_x, robert), Collections.singletonMap(_x, victor), });
	}

}
