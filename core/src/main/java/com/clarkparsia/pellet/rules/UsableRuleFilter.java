// Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public
// License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of
// proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package com.clarkparsia.pellet.rules;

import com.clarkparsia.pellet.rules.builtins.BuiltIn;
import com.clarkparsia.pellet.rules.builtins.BuiltInRegistry;
import com.clarkparsia.pellet.rules.builtins.NoSuchBuiltIn;
import com.clarkparsia.pellet.rules.model.AtomVariable;
import com.clarkparsia.pellet.rules.model.BuiltInAtom;
import com.clarkparsia.pellet.rules.model.DataRangeAtom;
import com.clarkparsia.pellet.rules.model.Rule;
import com.clarkparsia.pellet.rules.model.RuleAtom;
import com.clarkparsia.pellet.rules.model.RuleAtomVisitor;
import com.clarkparsia.pellet.rules.model.SameIndividualAtom;
import java.util.HashSet;
import java.util.Set;

/**
 * <p>
 * Title: Usable Rule Filter
 * </p>
 * <p>
 * Description: An iterable returning only rules that can be used by pellet, discarding and warning about all others.
 * </p>
 * <p>
 * Copyright: Copyright (c) 2007
 * </p>
 * <p>
 * Company: Clark & Parsia, LLC. <http://www.clarkparsia.com>
 * </p>
 *
 * @author Ron Alford
 * @author Evren Sirin
 */
public class UsableRuleFilter
{

	private static class UsableFilter implements RuleAtomVisitor
	{
		protected String notUsableMessage;

		public String explainNotUsable(final RuleAtom atom)
		{
			notUsableMessage = null;
			atom.accept(this);
			return notUsableMessage;
		}
	}

	private static class BodyAtomFilter extends UsableFilter
	{
		@Override
		public void visit(final BuiltInAtom atom)
		{
			final BuiltIn builtin = BuiltInRegistry.instance.getBuiltIn(atom.getPredicate());
			if (builtin.equals(NoSuchBuiltIn.instance))
				notUsableMessage = "No builtin for " + atom.getPredicate();
		}

		@Override
		public void visit(final SameIndividualAtom atom)
		{
			notUsableMessage = "SameIndividual atom is not supported in rule body: " + atom;
		}

	}

	private static class HeadAtomFilter extends UsableFilter
	{

		@Override
		public void visit(final BuiltInAtom atom)
		{
			notUsableMessage = "Builtin atoms in rule heads are not currently supported";
		}

		@Override
		public void visit(final DataRangeAtom atom)
		{
			notUsableMessage = "DataRange atoms in rule heads are not currently supported";
		}
	}

	private static BodyAtomFilter bodyFilter = new BodyAtomFilter();
	private static HeadAtomFilter headFilter = new HeadAtomFilter();

	/**
	 * Checks if a rule can be used for reasoning.
	 *
	 * @param rule rule to check
	 * @return <code>true</code> if rule can be used for reasoning
	 */
	public static boolean isUsable(final Rule rule)
	{
		return explainNotUsable(rule) == null;
	}

	/**
	 * Returns a string explaining why a rule cannot be used for reasoning, or <code>null</code> if the rule can be used for reasoning
	 *
	 * @param rule rule to check
	 * @return a string explaining why a rule cannot be used for reasoning, or <code>null</code> if the rule can be used for reasoning
	 */
	public static String explainNotUsable(final Rule rule)
	{

		final Set<AtomVariable> bodyVars = new HashSet<>();
		for (final RuleAtom atom : rule.getBody())
		{
			final String notUsableExplanation = bodyFilter.explainNotUsable(atom);
			if (notUsableExplanation != null)
				return notUsableExplanation;
			bodyVars.addAll(VariableUtils.getVars(atom));
		}

		for (final RuleAtom atom : rule.getHead())
		{
			if (!bodyVars.containsAll(VariableUtils.getVars(atom)))
				return "Head atom " + atom + " contains variables not found in body.";
			final String notUsableExplanation = headFilter.explainNotUsable(atom);
			if (notUsableExplanation != null)
				return notUsableExplanation;
		}

		return null;
	}

}
