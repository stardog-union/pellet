// Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package com.clarkparsia.pellet.rules;

import com.clarkparsia.pellet.rules.builtins.BuiltIn;
import com.clarkparsia.pellet.rules.builtins.BuiltInRegistry;
import com.clarkparsia.pellet.rules.model.AtomDVariable;
import com.clarkparsia.pellet.rules.model.AtomIVariable;
import com.clarkparsia.pellet.rules.model.AtomVariable;
import com.clarkparsia.pellet.rules.model.BuiltInAtom;
import com.clarkparsia.pellet.rules.model.DataRangeAtom;
import com.clarkparsia.pellet.rules.model.DatavaluedPropertyAtom;
import com.clarkparsia.pellet.rules.model.DefaultRuleAtomVisitor;
import com.clarkparsia.pellet.rules.model.Rule;
import com.clarkparsia.pellet.rules.model.RuleAtom;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;
import org.mindswap.pellet.ABox;

/**
 * <p>
 * Title: Binding Generator Strategy Implementation
 * </p>
 * <p>
 * Description: Implementation of a BindingGenerator construction _strategy
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

public class BindingGeneratorStrategyImpl implements BindingGeneratorStrategy
{

	private class BodyAtomsToSelectiveHelpersVisitor extends DefaultRuleAtomVisitor
	{

		private final List<BindingHelper> helpers = new ArrayList<>();

		public List<BindingHelper> getHelpers()
		{
			return helpers;
		}

		@Override
		public void visit(final BuiltInAtom atom)
		{
			final BuiltIn builtIn = BuiltInRegistry.instance.getBuiltIn(atom.getPredicate());
			helpers.add(builtIn.createHelper(atom));
		}

		@Override
		public void visit(final DataRangeAtom atom)
		{
			helpers.add(new DataRangeBindingHelper(_abox, atom));
		}

		@Override
		public void visit(final DatavaluedPropertyAtom atom)
		{
			helpers.add(new DatavaluePropertyBindingHelper(_abox, atom));
		}

	}

	private final ABox _abox;

	public BindingGeneratorStrategyImpl(final ABox abox)
	{
		this._abox = abox;
	}

	@Override
	public BindingGenerator createGenerator(final Rule rule)
	{
		return createGenerator(rule, new VariableBinding(_abox));
	}

	@Override
	public BindingGenerator createGenerator(final Rule rule, final VariableBinding initialBinding)
	{

		List<BindingHelper> helpers;

		final BodyAtomsToSelectiveHelpersVisitor selectiveVisitor = new BodyAtomsToSelectiveHelpersVisitor();
		final Set<AtomIVariable> instanceVariables = new HashSet<>();
		final Set<AtomDVariable> dataVariables = new HashSet<>();
		for (final RuleAtom pattern : rule.getBody())
		{
			pattern.accept(selectiveVisitor);
			instanceVariables.addAll(VariableUtils.getIVars(pattern));
			dataVariables.addAll(VariableUtils.getDVars(pattern));
		}
		helpers = selectiveVisitor.getHelpers();

		final Set<AtomVariable> selectiveVariables = new HashSet<>();
		for (final BindingHelper helper : helpers)
		{
			final Collection<AtomVariable> emptyCollection = Collections.emptySet();
			selectiveVariables.addAll(helper.getBindableVars(emptyCollection));
		}

		//		if ( !selectiveVariables.containsAll( dataVariables ) ) {
		//			ABox.log.warning( "IGNORING RULE "+rule+": Cannot generate bindings for all _data variables." );
		//			return new BindingGeneratorImpl();
		//		}

		for (final AtomIVariable var : instanceVariables)
			if (!selectiveVariables.contains(var))
				helpers.add(new ObjectVariableBindingHelper(_abox, var));

		helpers.addAll(new TrivialSatisfactionHelpers(_abox).getHelpers(rule));

		if (!ensureOrdering(helpers, initialBinding))
		{
			ABox.log.warning("IGNORING RULE " + rule + ": Could not generate safe ordering for body constraints.");
			return new BindingGeneratorImpl();
		}
		optimize(helpers);

		return new BindingGeneratorImpl(_abox, initialBinding, helpers);
	}

	/**
	 * Reorder list so that each binding helper's prerequisites are satisfied by the helpers before it. If no such ordering exists, return false.
	 */
	private boolean ensureOrdering(final List<BindingHelper> helpers, @SuppressWarnings("unused") final VariableBinding initialBinding)
	{
		final List<BindingHelper> unsatList = new ArrayList<>();
		final Set<AtomVariable> bound = new HashSet<>();

		for (final ListIterator<BindingHelper> listIter = helpers.listIterator(); listIter.hasNext();)
		{
			final BindingHelper helper = listIter.next();

			if (bound.containsAll(helper.getPrerequisiteVars(bound)))
			{
				bound.addAll(helper.getBindableVars(bound));

				for (final ListIterator<BindingHelper> unsatIter = unsatList.listIterator(); unsatIter.hasNext();)
				{
					final BindingHelper unsat = unsatIter.next();

					if (bound.containsAll(unsat.getPrerequisiteVars(bound)))
					{
						listIter.add(unsat);
						bound.addAll(unsat.getBindableVars(bound));
						unsatIter.remove();
					}
				}
			}
			else
			{
				unsatList.add(helper);
				listIter.remove();
			}

		}

		if (unsatList.size() == 0)
			return true;
		return false;
	}

	/**
	 * Reorder the binding helpers so that completely bound binding helpers are push as far up the list as they can be
	 */
	private void optimize(final List<BindingHelper> helpers)
	{
		final Set<AtomVariable> bound = new HashSet<>();

		for (int i = 0; i < helpers.size(); i++)
		{

			// Move any helpers which are completely bound and satisfied
			// to this point
			int j = 0;
			while (i + j < helpers.size())
			{
				final BindingHelper helper = helpers.get(i + j);
				if (bound.containsAll(helper.getBindableVars(bound)) && bound.containsAll(helper.getPrerequisiteVars(bound)))
				{
					helpers.remove(i + j);
					helpers.add(i, helper);
					i++; // Bump - since it is bound, we won't need to keep track of its bindable vars.
				}
				else
					j++; // Keep searching

			}

			// May have moved off the list.
			if (i < helpers.size())
				bound.addAll(helpers.get(i).getBindableVars(bound));

		}

	}

}
