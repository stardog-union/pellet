// Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package com.clarkparsia.pellet.rules;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

import org.mindswap.pellet.ABox;
import org.mindswap.pellet.DependencySet;
import org.mindswap.pellet.exceptions.InternalReasonerException;
import org.mindswap.pellet.utils.ATermUtils;
import org.mindswap.pellet.utils.Comparators;
import org.mindswap.pellet.utils.Pair;

import aterm.ATermAppl;

import com.clarkparsia.pellet.rules.model.AtomDVariable;
import com.clarkparsia.pellet.rules.model.AtomIVariable;
import com.clarkparsia.pellet.rules.model.AtomVariable;
import com.clarkparsia.pellet.rules.model.BuiltInAtom;
import com.clarkparsia.pellet.rules.model.ClassAtom;
import com.clarkparsia.pellet.rules.model.DataRangeAtom;
import com.clarkparsia.pellet.rules.model.DatavaluedPropertyAtom;
import com.clarkparsia.pellet.rules.model.DefaultAtomObjectVisitor;
import com.clarkparsia.pellet.rules.model.DifferentIndividualsAtom;
import com.clarkparsia.pellet.rules.model.IndividualPropertyAtom;
import com.clarkparsia.pellet.rules.model.Rule;
import com.clarkparsia.pellet.rules.model.RuleAtom;
import com.clarkparsia.pellet.rules.model.RuleAtomVisitor;
import com.clarkparsia.pellet.rules.model.SameIndividualAtom;
import com.clarkparsia.pellet.rules.rete.Fact;
import com.clarkparsia.pellet.rules.rete.TermTuple;

/**
 * <p>
 * Title: Rules To Continuous Rete Transformer
 * </p>
 * <p>
 * Description: Transforms rules for use with continuous rete.
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
public class ContinuousReteTransformer {
	
	private class AtomFilter implements RuleAtomVisitor {
		
		private boolean result = false;
		
		/**
		 * May return true if atom is something that
		 * will be added to the ABox during completion.
		 */
		public boolean isSafe() { return result; }

		public void visit(BuiltInAtom atom) {
			result = false;
		}

		public void visit(ClassAtom atom) {
			ATermAppl c = atom.getPredicate();
			result = abox.getKB().getTBox().isPrimitive( c );
		}

		public void visit(DataRangeAtom atom) {
			result = false;
		}

		public void visit(DatavaluedPropertyAtom atom) {
			result = true;
		}

		public void visit(DifferentIndividualsAtom atom) {
			result = false;
		}

		public void visit(IndividualPropertyAtom atom) {
			result = abox.getRole( atom.getPredicate() ).isSimple();
		}

		public void visit(SameIndividualAtom atom) {
			result = false;
		}

	}
	
	private static class BindingSetter extends DefaultAtomObjectVisitor {
		private VariableBinding binding;
		private ATermAppl constant;
		
		public BindingSetter( VariableBinding binding, ATermAppl constant) {
			super();
			this.binding = binding;
			this.constant = constant;
		}
		
		@Override
		public void visit(AtomDVariable variable) {
			binding.set( variable, constant );
		}

		@Override
		public void visit(AtomIVariable variable) {
			binding.set( variable, constant );
		}
	}
	
	private static final String PREFIX = "tag:clarkparsia.info,2007:pellet:dl-safe-rules:continuous-rete:";
	private static final ATermAppl DUMMY = ATermUtils.makeTermAppl( PREFIX + "slot-filler" );
	private static final int RULEPOS = 1;
	private static final int VARSTARTPOS = 4;
	public static final ATermAppl VARBINDING = ATermUtils.makeTermAppl( PREFIX + "variable-binding" );
	private static int ruleNumber = 0;
	
	
	
	private ABox abox;
	private AtomFilter filter;
	/**
	 * A map from a Rule to an identifier
	 */
	private Map<Rule, ATermAppl> reverseRuleMap;
	/**
	 * Contains a map from the rule id to a Rule and a list of variables
	 * as they occurred in the head term of the transformed rule.
	 */
	private Map<ATermAppl, Pair<Rule, List<AtomVariable>>> ruleMap;
	private RulesToReteTranslator translator;
	
	public ContinuousReteTransformer( ABox abox ) {
		this.abox = abox;
		filter = new AtomFilter();
		ruleMap = new HashMap<ATermAppl, Pair<Rule, List<AtomVariable>>>();
		reverseRuleMap = new HashMap<Rule, ATermAppl>();
		translator = new RulesToReteTranslator( abox );
	}
	
	protected ATermAppl getRuleID( Rule rule ) {
		ATermAppl id = reverseRuleMap.get( rule );
		if ( id == null ) {
			id = ATermUtils.makeTermAppl( PREFIX + "rule-" + (ruleNumber++) );
			reverseRuleMap.put( rule, id );
		}
		return id;
	}
	
	/**
	 * <p>Transform the rule as follows:</p>
	 * <ul>
	 *  <li>The body is reduced to relations (may become empty)</li>
	 *  <li>The head is reduced to a reference to the rule, followed
	 *  by the variables left in body in the naturally sorted order.</li>
	 * </ul>
	 * 
	 */
	public com.clarkparsia.pellet.rules.rete.Rule transformRule( Rule rule ) {
		ATermAppl ruleID = getRuleID( rule );
		DependencySet ds = DependencySet.INDEPENDENT; // TODO Make DS for the rule.
		
		
		Collection<ATermAppl> vars = new TreeSet<ATermAppl>( Comparators.termComparator );
		
		List<TermTuple> body = new ArrayList<TermTuple>();
		Map<ATermAppl, AtomVariable> varToVar = new HashMap<ATermAppl, AtomVariable>();
		for ( RuleAtom atom : rule.getBody() ) {
			atom.accept( filter );
			if ( filter.isSafe() ) {
				TermTuple bodyTerm = translator.translateAtom( atom, ds );
				if ( bodyTerm != null ) {
					body.add( bodyTerm );
					for ( AtomVariable atomVar : VariableUtils.getVars( atom ) ) {
						Pair<ATermAppl, DependencySet> translatedVar = translator.translateAtomObject( atomVar );
						ATermAppl var = translatedVar.first;
						varToVar.put( var, atomVar );
						vars.add( var );
					}
				}
			}
		}
		
		List<ATermAppl> headTerms = new ArrayList<ATermAppl>();
		headTerms.add( VARBINDING );
		headTerms.add( ruleID );
		headTerms.add( DUMMY );
		headTerms.add( DUMMY ); // Space fillers so generated atoms don't collide with the triple-based rules
		headTerms.addAll( vars );
		List<TermTuple> head = Collections.singletonList( new TermTuple( DependencySet.INDEPENDENT,  headTerms ) );
		
		List<AtomVariable> ruleVars = new ArrayList<AtomVariable>( vars.size() );
		for ( ATermAppl var : vars )
			ruleVars.add( varToVar.get( var ) );
		
		ruleMap.put( ruleID, new Pair<Rule, List<AtomVariable>>( rule, ruleVars ) );
		
		return new com.clarkparsia.pellet.rules.rete.Rule( body, head );
	}
	
	/**
	 * Translate inferred rule binding into a Rule-VariableBinding pair.
	 */
	public Pair<Rule, VariableBinding> translateFact(Fact fact) {
		Pair<Rule, VariableBinding> partialApplication = null;
		if( (fact.getElements().size() >= VARSTARTPOS) && (fact.getElements().get( 0 ).equals( VARBINDING )) ) {
			Pair<Rule, List<AtomVariable>> ruleVarPair = ruleMap.get( fact.getElements().get( RULEPOS ) );
			Rule rule = ruleVarPair.first;
			List<AtomVariable> vars = ruleVarPair.second;
			List<ATermAppl> constants = fact.getElements().subList( VARSTARTPOS, fact.getElements().size() );

			if( vars.size() != constants.size() )
				throw new InternalReasonerException( "Variable list doesn't match constants!" );

			VariableBinding binding = new VariableBinding( abox );
			for( int i = 0; i < vars.size(); i++ )
				vars.get( i ).accept( new BindingSetter( binding, constants.get( i ) ) );

			partialApplication = new Pair<Rule, VariableBinding>( rule, binding );
		}
		return partialApplication;
	}
	
}
