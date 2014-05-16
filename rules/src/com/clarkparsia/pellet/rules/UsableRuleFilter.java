// Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public
// License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of
// proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package com.clarkparsia.pellet.rules;

import java.util.HashSet;
import java.util.Set;

import com.clarkparsia.pellet.rules.builtins.BuiltIn;
import com.clarkparsia.pellet.rules.builtins.BuiltInRegistry;
import com.clarkparsia.pellet.rules.builtins.NoSuchBuiltIn;
import com.clarkparsia.pellet.rules.model.AtomVariable;
import com.clarkparsia.pellet.rules.model.BuiltInAtom;
import com.clarkparsia.pellet.rules.model.ClassAtom;
import com.clarkparsia.pellet.rules.model.DataRangeAtom;
import com.clarkparsia.pellet.rules.model.DatavaluedPropertyAtom;
import com.clarkparsia.pellet.rules.model.DefaultRuleAtomVisitor;
import com.clarkparsia.pellet.rules.model.DifferentIndividualsAtom;
import com.clarkparsia.pellet.rules.model.IndividualPropertyAtom;
import com.clarkparsia.pellet.rules.model.Rule;
import com.clarkparsia.pellet.rules.model.RuleAtom;
import com.clarkparsia.pellet.rules.model.RuleAtomVisitor;
import com.clarkparsia.pellet.rules.model.SameIndividualAtom;

/**
 * <p>
 * Title: Usable Rule Filter
 * </p>
 * <p>
 * Description: An iterable returning only rules that can be used by pellet,
 * discarding and warning about all others.
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
public class UsableRuleFilter {

	private static class UsableFilter extends DefaultRuleAtomVisitor {
		protected String notUsableMessage;

		public String explainNotUsable(RuleAtom atom) {
			notUsableMessage = null;
			atom.accept(this);
			return notUsableMessage;
		}
	}

	private static class BodyAtomFilter extends UsableFilter {
		public void visit(BuiltInAtom atom) {
			BuiltIn builtin = BuiltInRegistry.instance.getBuiltIn( atom.getPredicate() );
			if( builtin.equals( NoSuchBuiltIn.instance ) ) {
				notUsableMessage = "No builtin for " + atom.getPredicate();
			}
		}

		public void visit(SameIndividualAtom atom) {
			notUsableMessage = "SameIndividual atom is not supported in rule body: " + atom;
		}

	}

	private static class HeadAtomFilter extends UsableFilter {

		public void visit(BuiltInAtom atom) {
			notUsableMessage = "Builtin atoms in rule heads are not currently supported";
		}

		public void visit(DataRangeAtom atom) {
			notUsableMessage = "DataRange atoms in rule heads are not currently supported";
		}
	}

	private static BodyAtomFilter	bodyFilter	= new BodyAtomFilter();
	private static HeadAtomFilter	headFilter	= new HeadAtomFilter();

	/**
	 * Checks if a rule can be used for reasoning.
	 * 
	 * @param rule
	 *            rule to check
	 * @return <code>true</code> if rule can be used for reasoning
	 */
	public static boolean isUsable(Rule rule) {
		return explainNotUsable( rule ) == null;
	}

	/**
	 * Returns a string explaining why a rule cannot be used for reasoning, or
	 * <code>null</code> if the rule can be used for reasoning
	 * 
	 * @param rule
	 *            rule to check
	 * @return a string explaining why a rule cannot be used for reasoning, or
	 *         <code>null</code> if the rule can be used for reasoning
	 */
	public static String explainNotUsable(Rule rule) {

		Set<AtomVariable> bodyVars = new HashSet<AtomVariable>();
		for( RuleAtom atom : rule.getBody() ) {
			String notUsableExplanation = bodyFilter.explainNotUsable(atom);
			if( notUsableExplanation != null ) {
				return notUsableExplanation;
			}
			bodyVars.addAll( VariableUtils.getVars( atom ) );
		}

		for( RuleAtom atom : rule.getHead() ) {
			if( !bodyVars.containsAll( VariableUtils.getVars( atom ) ) ) {
				return "Head atom " + atom + " contains variables not found in body.";
			}
			String notUsableExplanation = headFilter.explainNotUsable(atom);
			if( notUsableExplanation != null ) {
				return notUsableExplanation;
			}
		}

		return null;
	}

}
